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
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.AbstractBusinessScheduler;
import com.simplelife.renhai.server.business.pool.InputMessageCenter;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.OutputMessageCenter;
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
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.INode;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class DeviceWrapper implements IDeviceWrapper, INode, Comparable<IDeviceWrapper>
{	
	// WebScoket connection enclosed in DeviceWrapper, all JSON messages are sent/received by this connection 
    protected IBaseConnection webSocketConnection;
    
    // Time of last ping from APP
    protected Date lastPingTime;
    
    // Time of last request (including AlohaRequest and AppDataSyncRequest) from APP
    //protected Date lastActivityTime;
    // Owner business session of this device, be null if device is not in status of SessionBound
    
    protected IBusinessSession ownerBusinessSession;
    
    // Real device object enclosed in this DeviceWrapper
    protected Device device;
    
    // Service status of device, to indicate if device is out of service
    protected Consts.ServiceStatus serviceStatus;
    
    // Business session
    protected Consts.BusinessStatus businessStatus;
    
    // Business type of device, Random or Interest, effective after app selects business device pool 
    protected Consts.BusinessType businessType;

    // Online device pool, which is singleton instance in whole server
    protected OnlineDevicePool ownerOnlinePool;
    
    // Instance of slf Logger
    private Logger logger = BusinessModule.instance.getLogger();
    
    // Set service status of Device: Normal or Ban
    public void setServiceStatus(Consts.ServiceStatus serviceStatus)
    {
    	this.serviceStatus = serviceStatus;
    }
    
    /**
     * Save current time as lastPingTime 
     */
    @Override
    public void updatePingTime()
    {
    	Date now = DateUtil.getNowDate();
    	String temp = "Update last ping time: " + now.getTime();
    	
    	temp += ", connection ID: " + getConnection().getConnectionId();
    	if (device != null)
    	{
    		temp += " for device <" + device.getDeviceSn() + ">";
    	}
    	
    	LoggerFactory.getLogger("Ping").debug(temp);
    	lastPingTime = now;
    }
            
    /**
     * Constructor of DeviceWrapper 
     * @param connection: connection for sending/receiving JSON messages
     */
    public DeviceWrapper(IBaseConnection connection)
    {
    	this.webSocketConnection = connection;
    	this.businessStatus = Consts.BusinessStatus.Init;
    	connection.bind(this);
    }

    /**
     * Change business status of DeviceWrapper, and release/update relevant information if necessary 
     * @param targetStatus: target business status
     */
    public void changeBusinessStatus(Consts.BusinessStatus targetStatus, StatusChangeReason reason)
    {
    	logger.debug("[Milestone] Device <{}> changes status from " 
    			+ this.businessStatus.name() + " to " + targetStatus.name() 
    			+ ", caused by " + reason.name(), this.getDeviceSn());
    	
    	if (ownerOnlinePool == null)
    	{
    		logger.debug("But ownerOnlinePool == null, return directly");
    		return;
    	}
    	
    	AbstractBusinessDevicePool businessPool = null;
    	
    	if (this.businessStatus.getValue() >= Consts.BusinessStatus.MatchCache.getValue())
    	{
    		businessPool = ownerOnlinePool.getBusinessPool(this.businessType);
    	}
    	 
    	switch(targetStatus)
    	{
    		case Offline:
    			switch(businessStatus)
    			{
    				case Init:
    					ownerOnlinePool.deleteDevice(this, reason);
    					DAOWrapper.asyncSave(this.getDevice());
    					break;
    				case Idle:
    					ownerOnlinePool.deleteDevice(this, reason);
    					DAOWrapper.asyncSave(this.getDevice());
    					break;
    				case MatchCache:
    				case WaitMatch:
    					businessPool.onDeviceLeave(this, reason);
    					ownerOnlinePool.deleteDevice(this, reason);
    					DAOWrapper.asyncSave(this.getDevice());
    					break;
    				case SessionBound:
    					// Leave business device pool
    					unbindBusinessSession(reason);
    					businessPool.onDeviceLeave(this, reason);
    					ownerOnlinePool.deleteDevice(this, reason);
    					DAOWrapper.asyncSave(this.getDevice());
    					break;
    				default:
    					logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			this.webSocketConnection.closeConnection();
    			break;
    			
    		case Init:
    			logger.error("Abnormal business status change for device:<" + device.getDeviceSn() + ">, source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
				break;
				
    		case Idle:
    			switch(businessStatus)
    			{
    				case Idle:
    					// do nothing
    					break;
    				case Init:
    					// Init -> Idle, typical process of AppDataSyncRequest 
    					ownerOnlinePool.synchronizeDevice(this);
    					break;
    				case MatchCache:
    				case WaitMatch:
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
    			
    		case MatchCache:
    			AbstractBusinessScheduler businessScheduler = null;
    			switch(businessStatus)
    			{
    				case Idle:
    					businessPool = ownerOnlinePool.getBusinessPool(this.businessType);
    					businessStatus = targetStatus;				// To ensure that status of device is WaitMatch before enter business pool
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
    			
    		case WaitMatch:
    			switch(businessStatus)
    			{
    				case MatchCache:
    					businessPool = ownerOnlinePool.getBusinessPool(this.businessType);
    					businessStatus = targetStatus;				// To ensure that status of device is WaitMatch before enter business pool
    					businessPool.startMatch(this);
    					break;
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
        				case WaitMatch:
        					ownerBusinessSession.onDeviceEnter(this);
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
    public Consts.BusinessStatus getBusinessStatus()
    {
        return businessStatus;
    }
    
    /** */
    public Consts.ServiceStatus getServiceStatus()
    {
        return serviceStatus;
    }
    
    /** */
    public boolean checkExistenceInDb()
    {
        return false;
    }
    
    /** */
    public Date getLastPingTime()
    {
        return lastPingTime;
    }
    
     
    /** */
    public void unbindBusinessSession(StatusChangeReason reason)
    {
    	if (this.businessStatus != Consts.BusinessStatus.SessionBound)
    	{
    		return;
    	}
    	
    	logger.debug("Unbind device <{}> from business session", getDeviceSn());
    	ownerBusinessSession.onDeviceLeave(this, reason);
    	ownerBusinessSession = null;
    }
    
    /** */
    public long getLastActivityTime()
    {
    	if (device == null)
    	{
    		return 0;
    	}
        return device.getProfile().getLastActivityTime();
    }
    
    /** */
    public void setPreviousNode(INode node)
    {
    }
    
    /** */
    public void setNextNode(INode node)
    {
    }
    
    /** */
    public INode getPreviousNode()
    {
        return null;
    }
    
    /** */
    public INode getNextNode()
    {
        return null;
    }
    
    @Override
    public void onConnectionClose()
    {
    	changeBusinessStatus(Consts.BusinessStatus.Offline, Consts.StatusChangeReason.WebsocketClosedByApp);
    	/*
    	if (ownerOnlinePool != null)
    	{
    		ownerOnlinePool.deleteDevice(this, Consts.StatusChangeReason.WebsocketClosedByApp);
    	}
    	*/
    }


    @Override
    public void onJSONCommand(AppJSONMessage command)
    {
    	logger.debug("Device <{}> received " 
    		+ command.getMessageId().name() + " at status of " 
    		+ businessStatus.name() + ", messageSn: " + command.getMessageSn(), this.getDeviceSn());

    	Consts.MessageId messageId =  command.getMessageId();
    	if (messageId != Consts.MessageId.TimeoutRequest
				&& messageId != Consts.MessageId.Invalid
				&& messageId != Consts.MessageId.UnkownRequest)
    	{
    		this.updateActivityTime();
    	}
    	
    	if (this.ownerOnlinePool == null 
    			|| this.businessStatus == Consts.BusinessStatus.Init)
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
    		InputMessageCenter.instance.addMessage(command);
    	}
    	catch(Exception e)
    	{
    		ServerErrorResponse response = new ServerErrorResponse(command);
        	response.addToBody(JSONKey.ReceivedMessage, command.getJSONObject());
        	response.addToBody(JSONKey.ErrorCode, Consts.GlobalErrorCode.UnknownException_1104.getValue());
        	response.addToBody(JSONKey.ErrorDescription, "Server internal error");
        	response.addToHeader(JSONKey.MessageSn, command.getMessageSn());
        	
        	OutputMessageCenter.instance.addMessage(response);
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
    		logger.debug("ownerOnlinePool of device <{}> is null and ping is ignored, connection Id: " + connection.getConnectionId(), getDeviceSn());
    		return;
    	}
    	
    	// ��������������������������ping
        this.updatePingTime();
        connection.pong(payload);
    }


    @Override
    public void onTimeOut()
    {
    	changeBusinessStatus(Consts.BusinessStatus.Offline, Consts.StatusChangeReason.TimeoutOnSyncSending);
    	/*
    	if (ownerOnlinePool != null)
    	{
    		logger.debug("Notify online device pool about timeout of device <{}>", getDeviceSn());
    		ownerOnlinePool.deleteDevice(this, Consts.StatusChangeReason.TimeoutOnSyncSending);
    	}
    	*/
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
        	AppJSONMessage appResponse = webSocketConnection.syncSendMessage(message);
        	
        	if (appResponse != null)
        	{
        		this.onJSONCommand(appResponse);
        	}
		}
		catch (IOException e)
		{
			FileLogger.printStackTrace(e);
		}
    }
    
    /*
    @Override
    public void syncSendMessage(ServerJSONMessage message)
    {
    	SyncSendMessageTask task = new SyncSendMessageTask(this, message);
    	task.setName("SyncSendMsg" + DateUtil.getCurrentMiliseconds());
    	task.start();
    }
    */

    @Override
    public void asyncSendMessage(ServerJSONMessage message)
    {
    	Logger logger = BusinessModule.instance.getLogger();
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
	
	public String getDeviceSn()
	{
		if (this.device == null)
		{
			return "";
		}
		return this.device.getDeviceSn();
	}

	@Override
	public void updateActivityTime()
	{
		LoggerFactory.getLogger("Ping").debug("Update last activity time");
		if (device == null)
		{
			return;
		}
		device.getProfile().setLastActivityTime(System.currentTimeMillis());
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
		Devicecard deviceCard = device.getDevicecard();
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
		Interestcard interestCard = profile.getInterestcard();
		//JSONObject interestCardObj = JSONObject.(interestCard);
		
		JSONObject interestCardObj = new JSONObject();
		interestCardObj.put(JSONKey.InterestCardId, interestCard.getInterestCardId());
		
		JSONArray interestLabelListObj = new JSONArray();
		interestCardObj.put(JSONKey.InterestLabelList, interestLabelListObj);
		
		Set<Interestlabelmap> interestLabelList = interestCard.getInterestlabelmaps();
		for (Interestlabelmap map : interestLabelList)
		{
			JSONObject mapObj = new JSONObject();
			mapObj.put(JSONKey.GlobalInterestLabelId, map.getGlobalinterestlabel().getGlobalInterestLabelId());
			mapObj.put(JSONKey.InterestLabelName, map.getGlobalinterestlabel().getInterestLabelName());
			mapObj.put(JSONKey.GlobalMatchCount, map.getGlobalinterestlabel().getGlobalMatchCount());
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
		
		Set<Impresslabelmap> labelSet = impressCard.getImpresslabelmaps();
		//TreeSet<ImpresslabelmapSortable> labelList = new TreeSet<ImpresslabelmapSortable>();
		//TreeSet<ImpresslabelmapSortable> solidList = new TreeSet<ImpresslabelmapSortable>();
		
		List<Impresslabelmap> labelList = new ArrayList<Impresslabelmap>();
		List<Impresslabelmap> solidList = new ArrayList<Impresslabelmap>();
		
		for (Impresslabelmap labelMap : labelSet)
		{
			if (Consts.SolidAssessLabel.isSolidAssessLabel(labelMap.getGlobalimpresslabel().getImpressLabelName()))
			{
				//solidList.add((ImpresslabelmapSortable)labelMap);
				solidList.add(labelMap);
			}
			else
			{
				//labelList.add((ImpresslabelmapSortable)labelMap);
				labelList.add(labelMap);
			}
			labelMap.getAssessedCount();
		}
		
		for (Impresslabelmap label : solidList)
		{
			JSONObject labelObj = new JSONObject();
			labelObj.put(JSONKey.ImpressLabelName, label.getGlobalimpresslabel().getImpressLabelName());
			labelObj.put(JSONKey.GlobalImpressLabelId, label.getGlobalimpresslabel().getGlobalImpressLabelId());
			labelObj.put(JSONKey.AssessedCount, label.getAssessedCount());
			labelObj.put(JSONKey.UpdateTime, DateUtil.getDateStringByLongValue(label.getUpdateTime()));
			labelObj.put(JSONKey.AssessCount, label.getAssessCount());

			assessLabelListObj.add(labelObj);
		}
		
		int tmpCount = 0;
		for (Impresslabelmap label : labelList)
		{
			JSONObject labelObj = new JSONObject();
			labelObj.put(JSONKey.ImpressLabelName, label.getGlobalimpresslabel().getImpressLabelName());
			labelObj.put(JSONKey.GlobalImpressLabelId, label.getGlobalimpresslabel().getGlobalImpressLabelId());
			labelObj.put(JSONKey.AssessedCount, label.getAssessedCount());
			labelObj.put(JSONKey.UpdateTime, DateUtil.getDateStringByLongValue(label.getUpdateTime()));
			labelObj.put(JSONKey.AssessCount, label.getAssessCount());

			impressLabelListObj.add(labelObj);
			tmpCount ++;
			if (tmpCount >= labelCount)
			{
				break;
			}
		}

	}
	
	public JSONObject toJSONObject_ImpressCard(Profile profile)
	{
		JSONObject impressCardObj = new JSONObject();
		Impresscard impressCard = profile.getImpresscard();
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
		JSONObject wholeObj = new JSONObject();
		wholeObj.put(JSONKey.Device, toJSONObject_Device());
		return wholeObj;
	}

	@Override
	public void setBusinessType(BusinessType businessType)
	{
		this.businessType = businessType;
	}

	@Override
	public void increaseChatLoss()
	{
		Device device = this.getDevice();
		Profile profile = device.getProfile();
		Impresscard card = profile.getImpresscard();
		
		synchronized (card)
		{
			card.setChatLossCount(card.getChatLossCount() + 1);
		}
	}
	
	@Override
	public void increaseChatCount()
	{
		Device device = this.getDevice();
		Profile profile = device.getProfile();
		Impresscard card = profile.getImpresscard();
		
		synchronized (card)
		{
			card.setChatTotalCount(card.getChatTotalCount() + 1);
		}
	}

	@Override
	public void increaseChatDuration(int duration)
	{
		Device device = this.getDevice();
		Profile profile = device.getProfile();
		Impresscard card = profile.getImpresscard();
		
		synchronized (card)
		{
			card.setChatTotalDuration(card.getChatTotalDuration() + duration);
		}
	}

	@Override
	public int compareTo(IDeviceWrapper device)
	{
		return this.getDeviceSn().compareTo(device.getDeviceSn());
	}

}

