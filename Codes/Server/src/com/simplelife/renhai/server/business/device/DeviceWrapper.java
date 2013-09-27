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
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Impresslabelmap;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.InvalidRequest;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.INode;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class DeviceWrapper implements IDeviceWrapper, INode
{
	private class SyncSendMessageTask extends Thread
	{
		private DeviceWrapper device;
		private ServerJSONMessage message;
		
		public SyncSendMessageTask(DeviceWrapper device, ServerJSONMessage message)
		{
			this.device = device;
			this.message = message;
		}
		
		@Override
		public void run()
		{
			device.syncSendMessageByThread(message);
		}
		
	}
	/** */
    protected IBaseConnection webSocketConnection;
    
    // Time of last ping from APP
    protected Date lastPingTime;
    
    // Time of last request (including AlohaRequest and AppDataSyncRequest) from APP
    protected Date lastActivityTime;
    
    // Owner business session of this device, be null if device is not in status of SessionBound
    protected IBusinessSession ownerBusinessSession;
    
    // Real device object enclosed in this DeviceWrapper
    protected Device device;
    
    // Service status of device, to indicate if device is out of service
    protected Consts.ServiceStatus serviceStatus;
    
    // Business session
    protected Consts.BusinessStatus businessStatus;
    
    protected Consts.BusinessType businessType;
    
    protected OnlineDevicePool ownerOnlinePool;
    
    private Logger logger = BusinessModule.instance.getLogger();
    
    public void setServiceStatus(Consts.ServiceStatus serviceStatus)
    {
    	this.serviceStatus = serviceStatus;
    }
    
    /** */
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
    	
    	logger.debug(temp);
    	lastPingTime = now;
    }
            
    /** */
    public void onChatConfirm()
    {
    
    }
    
    /** */
    public DeviceWrapper(IBaseConnection connection)
    {
    	this.webSocketConnection = connection;
    	this.businessStatus = Consts.BusinessStatus.Init;
    	connection.bind(this);
    }
    
    /** */
    public void changeBusinessStatus(Consts.BusinessStatus targetStatus)
    {
    	Logger logger = BusinessModule.instance.getLogger();
    	switch(targetStatus)
    	{
    		case Init:
    			break;
    		case Idle:
    			switch(businessStatus)
    			{
    				case Idle:
    					break;
    				case Init:
    					ownerOnlinePool.synchronizeDevice(this);
    					break;
    				case WaitMatch:
    					break;
    				case SessionBound:
    					this.unbindBusinessSession();
    					break;
    				default:
    					logger.error("Abnormal business status change for device:" + device.getDeviceSn() + ", source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			break;
    		case SessionBound:
    			break;
    		case WaitMatch:
    			switch(businessStatus)
    			{
    				case Idle:
    					break;
    				case SessionBound:
    					this.unbindBusinessSession();
    					break;
    				default:
    					logger.error("Abnormal business status change for device:" + device.getDeviceSn() + ", source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			break;
    		default:
    			logger.error("Abnormal target business status for device:" + device.getDeviceSn());
    			break;
    	}
    	
    	businessStatus = targetStatus;
    }
    
    /** */
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
    public void unbindBusinessSession()
    {
    	ownerBusinessSession = null;
    }
    
    /** */
    public Date getLastActivityTime()
    {
        return lastActivityTime;
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
    public void onClose(IBaseConnection connection)
    {
    	if (ownerOnlinePool != null)
    	{
    		ownerOnlinePool.deleteDevice(this);
    	}
    }


    @Override
    public void onJSONCommand(AppJSONMessage command)
    {
    	this.updateActivityTime();
    	if (this.ownerOnlinePool == null 
    			|| this.businessStatus == Consts.BusinessStatus.Init)
    	{
    		Consts.MessageId messageId =  command.getMessageId();
    		if (messageId != Consts.MessageId.AlohaRequest 
    				&& messageId != Consts.MessageId.AppDataSyncRequest
    				&& messageId != Consts.MessageId.TimeoutRequest
    				&& messageId != Consts.MessageId.Invalid)
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

    	command.bindDeviceWrapper(this);
        (new Thread(command)).run();
    }

    @Override
    public void bindBusinessSession(IBusinessSession session)
    {
        this.ownerBusinessSession = session;
        this.changeBusinessStatus(Consts.BusinessStatus.SessionBound);
    }

    @Override
    public void onPing(IBaseConnection connection, ByteBuffer payload)
    {
    	if (this.ownerOnlinePool == null)
    	{
    		logger.debug("ownerOnlinePool of device <{}> is null and ping is ignored", getDeviceSn());
    		return;
    	}
    	
    	// 只有没有被释放的连接才回应ping
        this.updatePingTime();
        connection.pong(payload);
    }


    @Override
    public void onTimeOut(IBaseConnection conection)
    {
    	if (ownerOnlinePool != null)
    	{
    		ownerOnlinePool.deleteDevice(this);
    	}
    }

    public void syncSendMessageByThread(ServerJSONMessage message)
    {
        try
		{
        	if (webSocketConnection == null)
        	{
        		logger.error("webSocketConnection == null");
        		return;
        	}
			AppJSONMessage appResponse = webSocketConnection.syncSendMessage(message);
			this.onJSONCommand(appResponse);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
    @Override
    public void syncSendMessage(ServerJSONMessage message)
    {
    	(new SyncSendMessageTask(this, message)).start();
    }

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
			e.printStackTrace();
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
		return this.device.getDeviceSn();
	}

	@Override
	public void updateActivityTime()
	{
		logger.debug("Update last activity time");
		this.lastActivityTime = DateUtil.getNowDate();
	}
	
	@Override
	public void enterPool(BusinessType businessType)
	{
		if (ownerOnlinePool.getBusinessPool(businessType).onDeviceEnter(this))
		{
			changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
			this.businessType = businessType;
		}
		else
		{
			logger.warn("Device <{}> failed to enter business pool", getDeviceSn());
		}
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
		this.webSocketConnection.close();
	}

	@Override
	public OnlineDevicePool getOwnerOnlineDevicePool()
	{
		return ownerOnlinePool;
	}

	@Override
	public JSONObject toJSONObject()
	{
		JSONObject wholeObj = new JSONObject();
		JSONObject deviceObj = new JSONObject();
		JSONObject deviceCardObj = new JSONObject();
		JSONObject profileObj = new JSONObject();
		JSONObject interestCardObj = new JSONObject();
		JSONObject impressCardObj = new JSONObject();
		
		wholeObj.put(JSONKey.Device, deviceObj);
		deviceObj.put(JSONKey.DeviceId, device.getDeviceId().toString());
		deviceObj.put(JSONKey.DeviceSn, device.getDeviceSn());
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		deviceObj.put(JSONKey.Profile, profileObj);
		profileObj.put(JSONKey.InterestCard, interestCardObj);
		profileObj.put(JSONKey.ImpressCard, impressCardObj);
		
		Devicecard deviceCard = device.getDevicecard();
		deviceCardObj.put(JSONKey.DeviceCardId, deviceCard.getDeviceCardId().toString());
		deviceCardObj.put(JSONKey.RegisterTime, DateUtil.getDateStringByLongValue(deviceCard.getRegisterTime()));
		deviceCardObj.put(JSONKey.DeviceModel, deviceCard.getDeviceModel());
		deviceCardObj.put(JSONKey.OsVersion, deviceCard.getOsVersion());
		deviceCardObj.put(JSONKey.AppVersion, deviceCard.getAppVersion());
		deviceCardObj.put(JSONKey.Location, deviceCard.getLocation());
		deviceCardObj.put(JSONKey.IsJailed, deviceCard.getIsJailed());
		
		Profile profile = device.getProfile();
		profileObj.put(JSONKey.ProfileId, profile.getProfileId());
		profileObj.put(JSONKey.ServiceStatus, Consts.ServiceStatus.parseFromStringValue(profile.getServiceStatus()).toString());
		
		if (profile.getUnbanDate() != null)
		{
			profileObj.put(JSONKey.UnbanDate, DateUtil.getDateStringByLongValue(profile.getUnbanDate()));
		}
		else
		{
			profileObj.put(JSONKey.UnbanDate, "");
		}
		profileObj.put(JSONKey.LastActivityTime, DateUtil.getDateStringByLongValue(profile.getLastActivityTime()));
		profileObj.put(JSONKey.CreateTime, DateUtil.getDateStringByLongValue(profile.getCreateTime()));
		profileObj.put(JSONKey.Active, Consts.YesNo.parseValue(profile.getActive()).toString());

		Interestcard interestCard = profile.getInterestcard();
		interestCardObj.put(JSONKey.InterestCardId, interestCard.getInterestCardId().toString());
		
		JSONArray interestLabelListObj = new JSONArray();
		interestCardObj.put(JSONKey.InterestLabelList, interestLabelListObj);
		
		Set<Interestlabelmap> interestLabelList = interestCard.getInterestlabelmaps();
		for (Interestlabelmap map : interestLabelList)
		{
			JSONObject mapObj = new JSONObject();
			mapObj.put(JSONKey.GlobalInterestLabelId, map.getGlobalinterestlabel().getGlobalInterestLabelId().toString());
			mapObj.put(JSONKey.InterestLabelName, map.getGlobalinterestlabel().getInterestLabel());
			mapObj.put(JSONKey.GlobalMatchCount, map.getGlobalinterestlabel().getGlobalMatchCount().toString());
			mapObj.put(JSONKey.LabelOrder, map.getGlobalinterestlabel().getGlobalMatchCount().toString());
			mapObj.put(JSONKey.MatchCount, map.getMatchCount().toString());
			mapObj.put(JSONKey.ValidFlag, map.getValidFlag());
			
			interestLabelListObj.add(mapObj);
		}
		
		Impresscard impressCard = profile.getImpresscard();
		impressCardObj.put(JSONKey.ImpressCardId, impressCard.getImpressCardId().toString());
		impressCardObj.put(JSONKey.ChatTotalCount, impressCard.getChatTotalCount().toString());
		impressCardObj.put(JSONKey.ChatTotalDuration, impressCard.getChatTotalDuration().toString());
		impressCardObj.put(JSONKey.ChatLossCount, impressCard.getChatLossCount().toString());
		
		JSONArray assessLabelListObj = new JSONArray();
		JSONArray impressLabelListObj = new JSONArray();
		
		impressCardObj.put(JSONKey.AssessLabelList, assessLabelListObj);
		impressCardObj.put(JSONKey.ImpressLabelList, impressLabelListObj);
		
		Set<Impresslabelmap> impressLabelList = impressCard.getImpresslabelmaps();
		String label;
		for (Impresslabelmap map : impressLabelList)
		{
			JSONObject mapObj = new JSONObject();
			label = map.getGlobalimpresslabel().getImpressLabel();
			mapObj.put(JSONKey.ImpressLabelName, label);
			mapObj.put(JSONKey.GlobalImpressLabelId, map.getGlobalimpresslabel().getGlobalImpressLabelId().toString());
			mapObj.put(JSONKey.AssessedCount, map.getAssessedCount().toString());
			mapObj.put(JSONKey.UpdateTime, DateUtil.getDateStringByLongValue(map.getUpdateTime()));
			mapObj.put(JSONKey.AssessCount, map.getAssessCount().toString());
			
			if (Consts.SolidAssessLabel.isSolidAssessLabel(label))
			{
				assessLabelListObj.add(mapObj);
			}
			else
			{
				impressLabelListObj.add(mapObj);
			}
		}
		return wholeObj;
	}

}

