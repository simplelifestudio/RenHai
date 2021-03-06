/**
 * DeviceWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.InputMsgExecutorPool;
import com.simplelife.renhai.server.business.pool.MessageHandler;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.InvalidRequest;
import com.simplelife.renhai.server.json.ServerErrorResponse;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessSessionEventType;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IRunnableMessage;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class DeviceWrapper implements IDeviceWrapper, Comparable<IDeviceWrapper>
{	
	// WebScoket connection enclosed in DeviceWrapper, all JSON messages are sent/received by this connection 
    protected IBaseConnection webSocketConnection;
    
    // Time of last request (including AlohaRequest and AppDataSyncRequest) from APP
    //protected Date lastActivityTime;
    // Owner business session of this device, be null if device is not in status of SessionBound
    
    protected IBusinessSession ownerBusinessSession;
    
    // Real device object enclosed in this DeviceWrapper
    protected Device device;
    
    // Business session
    protected Consts.DeviceStatus businessStatus;
    
    // Business type of device, Random or Interest, effective after app selects business device pool 
    protected Consts.BusinessType businessType;

    // Online device pool, which is singleton instance in whole server
    protected OnlineDevicePool ownerOnlinePool;
    
    // Instance of slf Logger
    private Logger logger = BusinessModule.instance.getLogger();
    
    private Long lastActivityTime;
    
    private MessageHandler inputMessageHandler;
    private MessageHandler outputMessageHandler;
    
    private AbstractTimeoutNode pingTimeoutNode;
    private AbstractTimeoutNode chatConfirmTimeoutNode;
    
    private MessageHandler changeStatusHandler;
    
    private CopyOnWriteArrayList<SyncSendTimeoutNode> syncSendingTimeoutNodeList = new CopyOnWriteArrayList<>();
    
    @Override
    public void updatePingTime()
    {
    	pingTimeoutNode.updateTime();
    	if (pingTimeoutNode.getNextNode() == null && pingTimeoutNode.getPrevNode() == null)
    	{
    		// which means pingNode has been removed from PingLink
    		return;
    	}
    	
    	OnlineDevicePool.pingLink.moveToTail(pingTimeoutNode);
    	
    	if (this.chatConfirmTimeoutNode != null)
    	{
    		OnlineDevicePool.chatConfirmTimeoutLink.moveToTail(chatConfirmTimeoutNode);
    	}
    }
    
    public void enterChatConfirm()
    {
    	/*
    	chatConfirmTimeoutNode = new PingTimeoutNode(GlobalSetting.TimeOut.ChatConfirmTimeout, this);
    	OnlineDevicePool.chatConfirmTimeoutLink.append(chatConfirmTimeoutNode);
    	logger.debug("Device <{}> entered phase of ChatConfirm", this.getDeviceIdentification());
    	*/
    }
    
    public void leaveChatConfirm()
    {
    	/*
    	OnlineDevicePool.chatConfirmTimeoutLink.removeNode(chatConfirmTimeoutNode);
    	chatConfirmTimeoutNode = null;
    	logger.debug("Device <{}> leaved phase of ChatConfirm", this.getDeviceIdentification());
    	*/
    }
    
    public AbstractTimeoutNode getPingNode()
    {
    	return pingTimeoutNode;
    }
    
    /**
     * Constructor of DeviceWrapper 
     * @param connection: connection for sending/receiving JSON messages
     */
    public DeviceWrapper(IBaseConnection connection)
    {
    	this.webSocketConnection = connection;
    	this.businessStatus = DeviceStatus.Connected;
    	pingTimeoutNode = new PingTimeoutNode(GlobalSetting.TimeOut.PingTimeout, this);
    	OnlineDevicePool.pingLink.append(pingTimeoutNode);
    	
    	connection.bind(this);
    	inputMessageHandler = new MessageHandler(connection.getConnectionId(), InputMsgExecutorPool.instance);
        outputMessageHandler = new MessageHandler(connection.getConnectionId(), InputMsgExecutorPool.instance);
        changeStatusHandler = new  MessageHandler(connection.getConnectionId(), InputMsgExecutorPool.instance);
    }

    /**
     * Change business status of DeviceWrapper, and release/update relevant information if necessary 
     * @param targetStatus: target business status
     */
    public void changeBusinessStatus(Consts.DeviceStatus targetStatus, StatusChangeReason reason)
    {
    	changeStatusHandler.addMessage(new DeviceStatusChangeTask(targetStatus, reason));
    }
    
    private void serialChangeBusinessStatus(Consts.DeviceStatus targetStatus, StatusChangeReason reason)
    {
    	if (logger.isDebugEnabled())
    	{
	    	logger.debug("[Milestone] Device <{}> will change status from " 
	    			+ this.businessStatus.name() + " to " + targetStatus.name() 
	    			+ ", caused by " + reason.name(), this.getDeviceIdentification());
    	}
    	
		if (ownerOnlinePool == null)
    	{
			if (businessStatus != DeviceStatus.Disconnected)
			{
				logger.error("ownerOnlinePool of Device <{}> is null in status of " + businessStatus.name(), getDeviceIdentification());
				return;
			}
    	}
    	
    	AbstractBusinessDevicePool businessPool = null;
    	
    	if (this.businessStatus.getValue() >= Consts.DeviceStatus.BusinessChoosed.getValue())
    	{
    		businessPool = ownerOnlinePool.getBusinessPool(this.businessType);
    	}
    	 
    	switch(targetStatus)
    	{
    		case Disconnected:
    			inputMessageHandler.clearMessage();
    			outputMessageHandler.clearMessage();
    			switch(businessStatus)
    			{
    				case Connected:
    					ownerOnlinePool.deleteDevice(this, reason);
    					unbindOnlineDevicePool();				// No request is accepted from now on
    					DAOWrapper.instance.cache(this.getDevice());
    					break;
    				case AppDataSynced:
    					ownerOnlinePool.deleteDevice(this, reason);
    					unbindOnlineDevicePool();				// No request is accepted from now on
    					device.getProfile().setLastActivityTime(lastActivityTime);
    					DAOWrapper.instance.cache(this.getDevice());
    					break;
    				case BusinessChoosed:
    				case MatchStarted:
    					businessPool.onDeviceLeave(this, reason);
    					ownerOnlinePool.deleteDevice(this, reason);
    					unbindOnlineDevicePool();				// No request is accepted from now on
    					device.getProfile().setLastActivityTime(lastActivityTime);
    					DAOWrapper.instance.cache(this.getDevice());
    					break;
    				case SessionBound:
    					try
    					{
    						unbindBusinessSession(reason);
    					}
    					catch(Exception e)
    					{
    						FileLogger.printStackTrace(e);
    					}
    					try
    					{
    						businessPool.onDeviceLeave(this, reason);
    					}
    					catch(Exception e)
    					{
    						FileLogger.printStackTrace(e);
    					}
    					try
    					{
    						ownerOnlinePool.deleteDevice(this, reason);
    					}
    					catch(Exception e)
    					{
    						FileLogger.printStackTrace(e);
    					}
    					try
    					{
    						unbindOnlineDevicePool();				// No request is accepted from now on
    					}
    					catch(Exception e)
    					{
    						FileLogger.printStackTrace(e);
    					}
    					try
    					{
    						device.getProfile().setLastActivityTime(lastActivityTime);
    					}
    					catch(Exception e)
    					{
    						FileLogger.printStackTrace(e);
    					}
    					try
    					{
    						DAOWrapper.instance.cache(this.getDevice());
    					}
    					catch(Exception e)
    					{
    						FileLogger.printStackTrace(e);
    					}
    					break;
    					
    				case Disconnected:
    					break;
    					
    				default:
    					logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			
    			if (reason != StatusChangeReason.TimeoutOfPing)
    			{
    				OnlineDevicePool.pingLink.removeNode(pingTimeoutNode);
    				//PingActionQueue.instance.newAction(TimeoutActionType.RemoveNode, this.pingTimeoutNode);
    			}
    			this.webSocketConnection.closeConnection();			// Close socket at last step
    			break;
    			
    		case Connected:
    			if (reason == StatusChangeReason.NewConnection)
    			{
    				OnlineDevicePool.pingLink.append(this.getPingNode());
    			}
    			else
    			{
    				logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    			}
				break;
				
    		case AppDataSynced:
    			switch(businessStatus)
    			{
    				case AppDataSynced:
    					// do nothing
    					break;
    				case Connected:
    					// Init -> Idle, typical process of AppDataSyncRequest
    					businessStatus = targetStatus;			// To ensure that deviceSn is available from now on
    					ownerOnlinePool.synchronizeDevice(this);
    					
    					inputMessageHandler.setMsgOwnerInfo(getDeviceSn());
    					outputMessageHandler.setMsgOwnerInfo(getDeviceSn());
    					changeStatusHandler.setMsgOwnerInfo(getDeviceSn());
    					break;
    				case BusinessChoosed:
    				case MatchStarted:
    					// Leave business device pool
    					businessPool.onDeviceLeave(this, reason);
    					break;
    				case SessionBound:
    					// Leave business device pool
    					unbindBusinessSession(reason);
    					businessPool.onDeviceLeave(this, reason);
    					break;
    				default:
    					logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			break;
    			
    		case BusinessChoosed:
    			switch(businessStatus)
    			{
    				case AppDataSynced:
    					businessPool = ownerOnlinePool.getBusinessPool(this.businessType);
    					businessPool.onDeviceEnter(this);
    					break;
    				case SessionBound:
    					this.unbindBusinessSession(reason);
    					businessPool.endChat(this);
    					break;
    				default:
    					logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			break;
    			
    		case MatchStarted:
    			switch(businessStatus)
    			{
    				case BusinessChoosed:
    					businessPool = ownerOnlinePool.getBusinessPool(this.businessType);
    					businessStatus = targetStatus;				// To ensure that status of device is WaitMatch before enter business pool
    					logger.debug("Device <{}> changed status to " + targetStatus.name(), getDeviceIdentification());
    					businessPool.startMatch(this);
    					
    					// Important, return directly here to avoid status is overwritten to WaitMatch again 
    					// if response of SessionBound is fast enough
    					return;					  
    				default:
    					logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			break;
    			
    		case SessionBound:
    			if (this.ownerBusinessSession == null)
    			{
    				logger.error("Fatal error when trying to change status of device <{}> to SessionBound but its session is still null.", device.getDeviceSn());
    			}
    			else
    			{
    				switch(businessStatus)
        			{
        				case MatchStarted:
        					//ownerBusinessSession.onDeviceEnter(this);
        					businessPool.startChat(this);
        					break;
        				default:
        					logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
        					break;
        			}
    			}
    			break;
    		default:
    			logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
				break;
    	}
    	
    	businessStatus = targetStatus;
    	logger.debug("Device <{}> changed status to " + targetStatus.name(), getDeviceIdentification());
    }
    
    /**
     * Return BusinessSession of Device, it's null if Device is not in status of SessionBound  
     */
    public IBusinessSession getOwnerBusinessSession()
    {
        return ownerBusinessSession;
    }
    
    
    /** */
    public Device getDevice()
    {
        return device;
    }
    
    /** */
    public Consts.DeviceStatus getBusinessStatus()
    {
        return businessStatus;
    }
    
    /** */
    public boolean checkExistenceInDb()
    {
        return false;
    }
    
    /** */
    public long getLastPingTime()
    {
        return pingTimeoutNode.getLastTime();
    }
    
    public void updatePingThreshold(int pingThreshold)
    {
    	pingTimeoutNode.setTimeoutThreshold(pingThreshold);
    }
     
    /** */
    public void unbindBusinessSession(StatusChangeReason reason)
    {
    	if (this.businessStatus != Consts.DeviceStatus.SessionBound)
    	{
    		logger.debug("Device <{}> is not in status of SessionBound, return directly");
    		return;
    	}
    	
    	if (ownerBusinessSession == null)
    	{
    		logger.debug("ownerBusinessSession of Device <{}> is null, return directly");
    		return;
    	}
    	
    	logger.debug("Unbind device <{}> from business session", getDeviceIdentification());
    	//ownerBusinessSession.onDeviceLeave(this, reason);
    	ownerBusinessSession.newEvent(BusinessSessionEventType.DeviceLeave, this, reason);
    	ownerBusinessSession = null;
    }
    
    /** */
    public long getLastActivityTime()
    {
        return this.lastActivityTime;
    }
    
    @Override
    public void onConnectionClose()
    {
    	changeBusinessStatus(Consts.DeviceStatus.Disconnected, Consts.StatusChangeReason.WebsocketClosedByApp);
    	/*
    	if (ownerOnlinePool != null)
    	{
    		ownerOnlinePool.deleteDevice(this, Consts.StatusChangeReason.WebsocketClosedByApp);
    	}
    	*/
    }


    private void removeTimeoutNode(String messageSn)
    {
    	if (syncSendingTimeoutNodeList.isEmpty())
    	{
    		return;
    	}
    	
    	for (SyncSendTimeoutNode node : syncSendingTimeoutNodeList)
    	{
    		if (node.isWaiting(messageSn))
    		{
    			OnlineDevicePool.syncSendingTimeoutLink.removeNode(node);
    			syncSendingTimeoutNodeList.remove(node);
    			return;
    		}
    	}
    }
    @Override
    public void onJSONCommand(AppJSONMessage command)
    {
    	if (logger.isDebugEnabled())
    	{
	    	logger.debug("Device <{}> received " 
	    		+ command.getMessageId().name() + " at status of " 
	    		+ businessStatus.name() + ", messageSn: " + command.getMessageSn(), this.getDeviceIdentification());
    	}
    	
    	if (this.businessStatus == Consts.DeviceStatus.Disconnected)
    	{
    		logger.warn("Device <"+ getDeviceIdentification() +"> was disconnected, handle of command {} is given up", command.getMessageId().name());
    		return;
    	}
    	
    	Consts.MessageId messageId =  command.getMessageId();
    	if (messageId != Consts.MessageId.TimeoutRequest
				&& messageId != Consts.MessageId.Invalid
				&& messageId != Consts.MessageId.UnkownRequest)
    	{
    		this.updateActivityTime();
    		
    		if (!syncSendingTimeoutNodeList.isEmpty())
        	{
    			removeTimeoutNode(command.getMessageSn());
        	}
    	}
    	
    	if (this.ownerOnlinePool == null 
    			|| this.businessStatus == Consts.DeviceStatus.Connected)
    	{
    		if (messageId != Consts.MessageId.AlohaRequest 
    				&& messageId != Consts.MessageId.AppDataSyncRequest
    				&& messageId != Consts.MessageId.TimeoutRequest
    				&& messageId != Consts.MessageId.Invalid
    				&& messageId != Consts.MessageId.UnkownRequest
    				&& messageId != Consts.MessageId.BusinessSessionNotificationResponse)
    		{
    			command = new InvalidRequest(command.getJSONObject());
    			command.setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
    			
    			if (ownerOnlinePool == null)
    			{
    				command.setErrorDescription("No request is allowed if connection has been released, please synchronize device again.");
    			}
    			else
    			{
    				command.setErrorDescription("Command is not supported before device is synchonized.");
    			}
    		}
    	}

    	try
    	{
    		command.bindDeviceWrapper(this);
    		//Thread cmdThread = new Thread(command);
    		//cmdThread.setName(command.getMessageId().name() + DateUtil.getCurrentMiliseconds());
    		//cmdThread.start();
    		inputMessageHandler.addMessage(command);
    	}
    	catch(Exception e)
    	{
    		ServerErrorResponse response = new ServerErrorResponse(command);
        	response.addToBody(JSONKey.ReceivedMessage, command.getJSONObject());
        	response.addToBody(JSONKey.ErrorCode, Consts.GlobalErrorCode.UnknownException_1104.getValue());
        	response.addToBody(JSONKey.ErrorDescription, "Server internal error");
        	response.addToHeader(JSONKey.MessageSn, command.getMessageSn());
        	
        	outputMessageHandler.addMessage(response);
    		FileLogger.printStackTrace(e);
    	}
    }

    @Override
    public void bindBusinessSession(IBusinessSession session)
    {
        ownerBusinessSession = session;
    }

    @Override
    public void onPing(IBaseConnection connection, ByteBuffer payload)
    {
    	if (this.ownerOnlinePool == null)
    	{
    		logger.debug("ownerOnlinePool of device <{}> is null and ping is ignored, connection Id: " + connection.getConnectionId(), getDeviceIdentification());
    		return;
    	}
    	
    	this.updatePingTime();
        connection.pong(payload);
    }


    @Override
    public void onTimeOut()
    {
    	changeBusinessStatus(Consts.DeviceStatus.Disconnected, Consts.StatusChangeReason.TimeoutOnSyncSending);
    }

    @Override
    public void syncSendMessage(ServerJSONMessage message)
    {
        try
		{
        	if (webSocketConnection == null)
        	{
        		logger.error("webSocketConnection == null");
        		return;
        	}
        	
        	SyncSendTimeoutNode node = new SyncSendTimeoutNode(GlobalSetting.TimeOut.JSONMessageEcho * 1000, this, message.getMessageSn());
        	OnlineDevicePool.syncSendingTimeoutLink.append(node);
        	
        	this.syncSendingTimeoutNodeList.add(node);
        	webSocketConnection.asyncSendMessage(message);
        	/*
        	AppJSONMessage appResponse = webSocketConnection.syncSendMessage(message);
        	
        	if (appResponse != null)
        	{
        		this.onJSONCommand(appResponse);
        	}
        	*/
		}
		catch (IOException e)
		{
			FileLogger.printStackTrace(e);
		}
    }
    
    @Override
    public void asyncSendMessage(ServerJSONMessage message)
    {
    	try
		{
        	if (webSocketConnection == null)
        	{
        		logger.error("webSocketConnection == null");
        		return;
        	}
			webSocketConnection.asyncSendMessage(message);
		}
		catch (IOException e)
		{
			FileLogger.printStackTrace(e);
		}
    }

	@Override
	public IBaseConnection getConnection()
	{
		return webSocketConnection;
	}
	
	public void setDevice(Device device)
	{
		this.device = device;
	}
	
	private String getDeviceSn()
	{
		if (this.device == null)
		{
			return "";
		}
		return this.device.getDeviceSn();
	}
	
	public String getDeviceIdentification()
	{
		if (this.businessStatus == DeviceStatus.Connected || this.businessStatus == DeviceStatus.Invalid)
		{
			return this.getConnection().getConnectionId();
		}
		return this.getDeviceSn();
	}

	@Override
	public void updateActivityTime()
	{
		LoggerFactory.getLogger("Ping").debug("Update last activity time");
		lastActivityTime = System.currentTimeMillis();
	}
	
	@Override
	public BusinessType getBusinessType()
	{
		return businessType;
	}

	/** */
    public void bindOnlineDevicePool(OnlineDevicePool pool)
    {
    	this.ownerOnlinePool = pool;
    }
    
    
	@Override
	public void unbindOnlineDevicePool()
	{
		this.ownerOnlinePool = null;
		//this.webSocketConnection.closeConnection();
	}

	@Override
	public OnlineDevicePool getOwnerOnlineDevicePool()
	{
		return ownerOnlinePool;
	}

	public JSONObject toJSONObject_DeviceCard()
	{
		JSONObject deviceCardObj = new JSONObject();
		Devicecard deviceCard = device.getDeviceCard();
		deviceCardObj.put(JSONKey.DeviceCardId, deviceCard.getDeviceCardId());
		deviceCardObj.put(JSONKey.RegisterTime, DateUtil.getDateStringByLongValue(deviceCard.getRegisterTime()));
		deviceCardObj.put(JSONKey.DeviceModel, deviceCard.getDeviceModel());
		deviceCardObj.put(JSONKey.OsVersion, deviceCard.getOsVersion());
		deviceCardObj.put(JSONKey.AppVersion, deviceCard.getAppVersion());
		deviceCardObj.put(JSONKey.Location, deviceCard.getLocation());
		deviceCardObj.put(JSONKey.IsJailed, Consts.YesNo.parseValue(deviceCard.getIsJailed()).getValue());
		
		return deviceCardObj;
	}
	
	public JSONObject toJSONObject_Device()
	{
		JSONObject deviceObj = new JSONObject();
		deviceObj.put(JSONKey.DeviceId, device.getDeviceId());
		deviceObj.put(JSONKey.DeviceSn, device.getDeviceSn());
		deviceObj.put(JSONKey.DeviceCard, toJSONObject_DeviceCard());
		deviceObj.put(JSONKey.Profile, toJSONObject_Profile());
		return deviceObj;
	}
	
	public JSONObject toJSONObject_Profile()
	{
		JSONObject profileObj = new JSONObject();
		Profile profile = device.getProfile();
		profileObj.put(JSONKey.ProfileId, profile.getProfileId());
		profileObj.put(JSONKey.ServiceStatus, Consts.ServiceStatus.parseFromStringValue(profile.getServiceStatus()).getValue());
		
		profileObj.put(JSONKey.InterestCard, toJSONObject_InterestCard(profile));
		profileObj.put(JSONKey.ImpressCard, toJSONObject_ImpressCard(profile));
		
		if (profile.getUnbanDate() != null)
		{
			profileObj.put(JSONKey.UnbanDate, DateUtil.getDateStringByLongValue(profile.getUnbanDate()));
		}
		else
		{
			profileObj.put(JSONKey.UnbanDate, null);
		}
		profileObj.put(JSONKey.LastActivityTime, DateUtil.getDateStringByLongValue(profile.getLastActivityTime()));
		profileObj.put(JSONKey.CreateTime, DateUtil.getDateStringByLongValue(profile.getCreateTime()));
		profileObj.put(JSONKey.Active, Consts.YesNo.parseValue(profile.getActive()).getValue());

		return profileObj;
	}
	
	public JSONObject toJSONObject_InterestCard(Profile profile)
	{
		Interestcard interestCard = profile.getInterestCard();
		//JSONObject interestCardObj = JSONObject.(interestCard);
		
		JSONObject interestCardObj = new JSONObject();
		interestCardObj.put(JSONKey.InterestCardId, interestCard.getInterestCardId());
		
		JSONArray interestLabelListObj = new JSONArray();
		interestCardObj.put(JSONKey.InterestLabelList, interestLabelListObj);
		
		Collection<Interestlabelmap> interestLabelList = interestCard.getInterestLabelMapSet();
		for (Interestlabelmap map : interestLabelList)
		{
			JSONObject mapObj = new JSONObject();
			mapObj.put(JSONKey.GlobalInterestLabelId, map.getGlobalInterestLabelId());
			mapObj.put(JSONKey.InterestLabelName, map.getGlobalLabel().getInterestLabelName());
			mapObj.put(JSONKey.GlobalMatchCount, map.getGlobalLabel().getGlobalMatchCount());
			mapObj.put(JSONKey.LabelOrder, map.getLabelOrder());
			mapObj.put(JSONKey.MatchCount, map.getMatchCount());
			mapObj.put(JSONKey.ValidFlag, Consts.ValidInvalid.Valid.getValue());
			
			interestLabelListObj.add(mapObj);
		}

		return interestCardObj;
	}
	
	@Override
	public void toJSONObject_ImpressLabels(Impresscard impressCard, JSONObject impressCardObj, int labelCount)
	{
		JSONArray assessLabelListObj = new JSONArray();
		JSONArray impressLabelListObj = new JSONArray();
		
		impressCardObj.put(JSONKey.AssessLabelList, assessLabelListObj);
		impressCardObj.put(JSONKey.ImpressLabelList, impressLabelListObj);
		
		Collection<Impresslabelmap> labelSet = impressCard.getImpressLabelMapSet();
		//TreeSet<ImpresslabelmapSortable> labelList = new TreeSet<ImpresslabelmapSortable>();
		//TreeSet<ImpresslabelmapSortable> solidList = new TreeSet<ImpresslabelmapSortable>();
		
		List<Impresslabelmap> labelList = new ArrayList<Impresslabelmap>();
		List<Impresslabelmap> solidList = new ArrayList<Impresslabelmap>();
		
		for (Impresslabelmap labelMap : labelSet)
		{
			if (Consts.SolidImpressLabel.isSolidImpressLabel(labelMap.getGlobalLabel().getImpressLabelName()))
			{
				solidList.add(labelMap);
			}
			else
			{
				if (labelMap.getAssessedCount() > 0)
				{
					labelList.add(labelMap);
				}
			}
			labelMap.getAssessedCount();
		}
		
		for (Impresslabelmap label : solidList)
		{
			JSONObject labelObj = new JSONObject();
			putLabelDetails(labelObj, label);
			assessLabelListObj.add(labelObj);
		}
		
		int tmpCount = 0;
		for (Impresslabelmap label : labelList)
		{
			JSONObject labelObj = new JSONObject();
			putLabelDetails(labelObj, label);
			impressLabelListObj.add(labelObj);
			tmpCount ++;
			if (tmpCount >= labelCount)
			{
				break;
			}
		}
	}
	
	private void putLabelDetails(JSONObject labelObj, Impresslabelmap label)
	{
		labelObj.put(JSONKey.ImpressLabelName, label.getGlobalLabel().getImpressLabelName());
		labelObj.put(JSONKey.GlobalImpressLabelId, label.getGlobalLabel().getGlobalImpressLabelId());
		labelObj.put(JSONKey.AssessedCount, label.getAssessedCount());
		labelObj.put(JSONKey.UpdateTime, DateUtil.getDateStringByLongValue(label.getUpdateTime()));
		labelObj.put(JSONKey.AssessCount, label.getAssessCount());
	}
	
	public JSONObject toJSONObject_ImpressCard(Profile profile)
	{
		JSONObject impressCardObj = new JSONObject();
		Impresscard impressCard = profile.getImpressCard();
		impressCardObj.put(JSONKey.ImpressCardId, impressCard.getImpressCardId());
		impressCardObj.put(JSONKey.ChatTotalCount, impressCard.getChatTotalCount());
		impressCardObj.put(JSONKey.ChatTotalDuration, impressCard.getChatTotalDuration());
		impressCardObj.put(JSONKey.ChatLossCount, impressCard.getChatLossCount());
		
		toJSONObject_ImpressLabels(impressCard, impressCardObj, GlobalSetting.BusinessSetting.MaxImpressLabelCount);
		return impressCardObj;
	}
	
	@Override
	public JSONObject toJSONObject()
	{
		//JSONObject wholeObj = new JSONObject();
		//wholeObj.put(JSONKey.Device, toJSONObject_Device());
		return toJSONObject_Device();
	}

	@Override
	public void setBusinessType(BusinessType businessType)
	{
		this.businessType = businessType;
	}

	@Override
	public void increaseMatchCount(String interestLabel)
	{
		Device device = this.getDevice();
		Profile profile = device.getProfile();
		Interestcard card = profile.getInterestCard();
		Collection<Interestlabelmap> maps = card.getInterestLabelMapSet();
		
		for (Interestlabelmap map : maps)
		{
			if (interestLabel.equals(map.getGlobalLabel().getInterestLabelName()))
			{
				synchronized (map)
				{
					int count = map.getMatchCount();
					logger.debug("Match count of device <{}> by label " + interestLabel + " is increased from " + count + " to " + (count+1), getDeviceIdentification());
					map.setMatchCount(count + 1);
				}
				return;
			}
		}
	}
	
	@Override
	public void increaseChatLoss()
	{
		Device device = this.getDevice();
		Profile profile = device.getProfile();
		Impresscard card = profile.getImpressCard();
		
		synchronized (card)
		{
			int count = card.getChatLossCount();
			logger.debug("Chat loss count of device <{}> was increased from " + count + " to " + (count+1), getDeviceIdentification());
			card.setChatLossCount(count + 1);
			//count = card.getChatTotalCount();
			//logger.debug("Chat total count of device <{}> was increased from " + count + " to " + (count+1), getDeviceSn());
			//card.setChatTotalCount(count + 1);
		}
	}
	
	@Override
	public void increaseChatCount()
	{
		Device device = this.getDevice();
		Profile profile = device.getProfile();
		Impresscard card = profile.getImpressCard();
		
		synchronized (card)
		{
			int count = card.getChatTotalCount();
			logger.debug("Chat total count of device <{}> was increased from " + count + " to " + (count+1), getDeviceIdentification());
			card.setChatTotalCount(count + 1);
		}
	}

	@Override
	public void increaseChatDuration(int duration)
	{
		Device device = this.getDevice();
		Profile profile = device.getProfile();
		Impresscard card = profile.getImpressCard();
		
		synchronized (card)
		{
			int count = card.getChatTotalDuration() + duration;
			logger.debug("Chat duration of device <{}> was increased to " + count, getDeviceIdentification());
			card.setChatTotalDuration(count);
		}
	}

	@Override
	public int compareTo(IDeviceWrapper device)
	{
		return this.getDeviceIdentification().compareTo(device.getDeviceIdentification());
	}
	
	@Override
	public void prepareResponse(ServerJSONMessage response)
	{
		outputMessageHandler.addMessage(response);
		return;
	}
	
	@Override
	public String toString()
	{
		return getDeviceIdentification();
	}
	
	private class DeviceStatusChangeTask implements IRunnableMessage
	{
		private DeviceStatus targetStatus;
		private StatusChangeReason reason;
		private long queueTime;
		public DeviceStatusChangeTask(DeviceStatus targetStatus, StatusChangeReason reason)
		{
			this.targetStatus = targetStatus;
			this.reason = reason;
		}
		@Override
		public void run()
		{
			try
			{
				serialChangeBusinessStatus(targetStatus, reason);
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}

		@Override
		public int getQueueDuration()
		{
			return (int)(System.currentTimeMillis() - queueTime);
		}

		@Override
		public void setQueueTime(long now)
		{
			queueTime = now;
		}

		@Override
		public String getMessageName()
		{
			return "DeviceStatusChangeTask";
		}

		@Override
		public String getMessageSn()
		{
			return "";
		}

		@Override
		public String getMsgOwnerInfo()
		{
			return getDeviceSn();
		}

		@Override
		public int getDelayOfHandle()
		{
			return 0;
		}
		
		@Override
		public boolean isSyncMessage()
		{
			return false;
		}
	}
}

