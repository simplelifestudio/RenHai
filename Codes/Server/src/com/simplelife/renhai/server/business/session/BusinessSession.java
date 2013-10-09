/**
 * BusinessSession.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Sessionrecord;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessProgress;
import com.simplelife.renhai.server.util.Consts.OperationType;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.util.Consts.BusinessSessionStatus;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class BusinessSession implements IBusinessSession
{
	private AbstractBusinessDevicePool pool;
	private Logger logger = BusinessModule.instance.getLogger();
	private String sessionId;
	
	private long sessionStartTime;
	private long chatStartTime;
	private long chatEndTime;
	private long sessionEndTime;
	
	private Consts.SessionEndReason endReason = Consts.SessionEndReason.Invalid;
	
	private List<IDeviceWrapper> deviceList = new ArrayList<IDeviceWrapper>(); 
	
	// Temp list for saving devices waiting for confirmation
	//private List<IDeviceWrapper> tmpConfirmDeviceList = new ArrayList<IDeviceWrapper>();
	private HashMap<String, Consts.BusinessProgress> progressMap = new HashMap<String, Consts.BusinessProgress>();
	
	private Consts.BusinessSessionStatus status = Consts.BusinessSessionStatus.Idle;
	private Consts.BusinessSessionStatus previousStatus = Consts.BusinessSessionStatus.Idle;
	
	public BusinessSession()
	{
		this.sessionId = CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfSessionId);
	}
	
	public String getSessionId()
	{
		return sessionId;
	}
	
    
    /** */
    public void checkWebRTCToken()
    {
    
    }
    
    /**
     * Check if all devices have reached given progress
     * @param progress: 
     * @return Return true if all devices reaches/passed given progress, else return false;
     */
    private boolean checkAllDevicesReach(Consts.BusinessProgress progress)
    {
    	synchronized(progressMap)
    	{
	    	Set<Entry<String, BusinessProgress>> entrySet = progressMap.entrySet();
	    	for (Entry<String, BusinessProgress> entry : entrySet)
	    	{
	    		if (entry.getValue().compareTo(progress) < 0)
	    		{
	    			return false;
	    		}
	    	}
	    	return true;
    	}
    }
    
    private boolean checkStartSession(List<String> deviceList)
    {
    	if (deviceList == null)
    	{
    		return false;
    	}
    	
    	if (deviceList.isEmpty())
    	{
    		return false;
    	}
    	
    	IDeviceWrapper device;
    	Consts.BusinessStatus status;
    	for (String deviceSn : deviceList)
    	{
    		device = OnlineDevicePool.instance.getDevice(deviceSn);
    		if (device == null)
    		{
    			logger.error("Device <{}> is not in online device pool when trying to bind with session.", deviceSn);
    			device = pool.getDevice(deviceSn);
    			pool.onDeviceLeave(device, Consts.DeviceLeaveReason.UnknownBusinessException);
    			return false;
    		}
    		status = device.getBusinessStatus();
    		
    		if ( status != Consts.BusinessStatus.WaitMatch)
    		{
    			logger.error("Abnormal business status of device <{}>: " + status.name(), deviceSn);
    			return false;
    		}
    	}
    	return true;
    }
    
    @Override
    public void endSession()
    {
    	logger.debug("Enter endSession.");
    	sessionEndTime = System.currentTimeMillis();
    	
    	synchronized(deviceList)
    	{
	    	for (IDeviceWrapper device : deviceList)
	    	{
	    		device.changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
	    		pool.endChat(device);
	    	}
	    	deviceList.clear();
    	}
    	
    	synchronized(progressMap)
    	{
    		progressMap.clear();
    	}
    	
    	saveSessionRecord();
    	unbindBusinessDevicePool();
    	BusinessSessionPool.instance.recycleBusinessSession(this);
    }
    
    private void saveSessionRecord()
    {
    	if (endReason == Consts.SessionEndReason.CheckFailed 
    			|| endReason == Consts.SessionEndReason.Invalid)
    	{
    		return;
    	}
    	
    	Sessionrecord record = new Sessionrecord();
    	record.setBusinessType(pool.getBusinessType().name());
    	record.setSessionStartTime(this.sessionStartTime);
    	
    	int duration = (int)((chatEndTime - chatStartTime)/1000);
    	record.setChatDuration(duration);
    	
    	duration = (int)((sessionEndTime - sessionStartTime)/1000);
    	record.setSessionDuration(duration);
    	
    	record.setEndStatus(previousStatus.name());
    	record.setEndReason(endReason.name());
    	
    	DBModule.instance.cache(record);
    }
    
    @Override
    public void startSession(List<String> deviceList)
    {
    	sessionStartTime = System.currentTimeMillis();
    	
    	logger.debug("Enter startSession.");
    	if (!checkStartSession(deviceList))
    	{
    		endSession();
    		return;
    	}
    	
    	IDeviceWrapper device;
    	for (String deviceSn : deviceList)
    	{
    		progressMap.put(deviceSn, Consts.BusinessProgress.Init);
    		device = OnlineDevicePool.instance.getDevice(deviceSn);
    		if (device == null)
    		{
    			logger.error("Device <{}> is not in online device pool anymore, to be end business session", deviceSn);
    			this.endSession();
    			return;
    		}
    		this.deviceList.add(device);
    		device.bindBusinessSession(this);
    		pool.startChat(device);
    	}
    	
    	notifyDevices(null, Consts.NotificationType.SessionBinded);
    }
    
    public void notifyIncreaseChatDuration(int duration)
    {
    	List<IDeviceWrapper> tmpList;
    	synchronized(this.deviceList)
    	{
    		tmpList = new ArrayList<IDeviceWrapper>(deviceList);
    	}
    	
    	for (IDeviceWrapper device : tmpList)
    	{
			device.increaseChatDuration(duration);
    	}
    }
    
    
    public void notifyIncreaseChatCount()
    {
    	List<IDeviceWrapper> tmpList;
    	synchronized(this.deviceList)
    	{
    		tmpList = new ArrayList<IDeviceWrapper>(deviceList);
    	}
    	
    	for (IDeviceWrapper device : tmpList)
    	{
			device.increaseChatCount();
    	}
    }
    
    public void notifyDevices(IDeviceWrapper triggerDevice, Consts.NotificationType notificationType)
    {
    	notifyDevices(this.deviceList, triggerDevice, notificationType);
    }
    
    public void notifyDevices(List<IDeviceWrapper> activeDeviceList, IDeviceWrapper triggerDevice, Consts.NotificationType notificationType)
    {
    	/*
    	if (pool == null)
    	{
    		// Maybe session has been ended by other events
    		logger.warn("Session has been ended and notifying devices has been cancelled");
    		return;
    	}
    	*/
    	ServerJSONMessage notify = JSONFactory.createServerJSONMessage(null, Consts.MessageId.BusinessSessionNotification);
    	JSONObject body = notify.getBody(); 
    	body.put(JSONKey.BusinessSessionId, CommonFunctions.getJSONValue(sessionId));
    	body.put(JSONKey.OperationType, notificationType.getValue());
    	body.put(JSONKey.OperationValue, null);
    	
    	List<IDeviceWrapper> tmpList;
    	synchronized(activeDeviceList)
    	{
    		tmpList = new ArrayList<IDeviceWrapper>(activeDeviceList);
    	}
    	
    	String temp;
		for (IDeviceWrapper device : tmpList)
    	{
			if (device == triggerDevice)
			{
				continue;
			}
		
			temp = "Notify device <"+ device.getDeviceSn() +"> about " + notificationType.name();
			if (triggerDevice != null)
			{
				temp += " from device <" + triggerDevice.getDeviceSn() + ">";
			}
			logger.debug(temp);
			
			notify.getHeader().put(JSONKey.MessageSn, CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfMessageSn));
			body.put(JSONKey.BusinessType, device.getBusinessType().getValue());
			
			if (triggerDevice == null)
			{
				notify.getBody().put(JSONKey.OperationInfo, null);
			}
			else
			{
				notify.getBody().put(JSONKey.OperationInfo, triggerDevice.toJSONObject());
			}
    		notify.setDeviceWrapper(device);
    		notify.syncResponse();
    		
    		DbLogger.saveSystemLog(Consts.OperationCode.NotificationSessionBounded_1010
        			, Consts.SystemModule.business
        			, notificationType.name() + ", " + device.getDeviceSn());
    	}
	}
    
    /**
     * BusinessSession需要调用该方法切换状态，该方法中会检查绑定的两个设备的连接情况，如果有设备的连接已经断开，BusinessSession需要根据对应的业务逻辑决定接下来的业务流程，比如：
     * 1. 如果处于双方确认状态，直接取消匹配
     * 2. 如果处于视频通话状态，结束视频通话
     * 3. 如果处于双方评价状态，等待连接正常的那一个设备评价结束后回收
     *
     * @param    status
    **/
    public void changeStatus(Consts.BusinessSessionStatus targetStatus)
    {
    	logger.debug("Business session changes status from {} to " + targetStatus.name(), status.name());
    	switch(targetStatus)
    	{
    		case Idle:
    			if (pool != null)
    			{
    				endSession();
    			}
    			break;
    			
    		case ChatConfirm:
    			if (status == Consts.BusinessSessionStatus.Idle)
    			{
    				//sendChatConfirm();
    				//resetDeviceForConfirm();
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			break;
    			
    		case VideoChat:
    			if (status == Consts.BusinessSessionStatus.ChatConfirm)
    			{
    				chatStartTime = System.currentTimeMillis();
    				chatEndTime = 0;
    				notifyIncreaseChatCount();
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			
    			break;
    			
    		case Assess:
    			if (status == Consts.BusinessSessionStatus.VideoChat)
    			{
    				if (chatEndTime == 0)
    				{
    					chatEndTime = System.currentTimeMillis();
    					notifyIncreaseChatDuration((int)(chatEndTime - chatStartTime));
    				}
    				//resetDeviceForConfirm();
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			break;
    			
			default:
				break;
    	}
    	
    	previousStatus = status;
    	status = targetStatus;
    }
    
    
    /** */
    public void onBindConfirm(IDeviceWrapper device)
    {
    	Consts.BusinessProgress progress;
    	synchronized(progressMap)
    	{
    		progress = progressMap.get(device.getDeviceSn());
    	}
    	
    	if (progress == null)
    	{
    		logger.error("Fatal error that received confirmation SessionBounded from device <{}> but there is no progress record for it!");
    		return;
    	}
    	
    	if (progress.compareTo(Consts.BusinessProgress.Init) != 0)
    	{
    		logger.error("Received confirmation SessionBounded from device <{}> but it's in status of " + progress.name(), device.getDeviceSn());
    		return;
    	}
    	
    	if (this.status != Consts.BusinessSessionStatus.Idle)
    	{
    		logger.error("Bind confirm received from {} while current session status is: " + status.name(), device.getDeviceSn());
    		return;
    	}
    	
    	synchronized(progressMap)
    	{
    		progressMap.put(device.getDeviceSn(), Consts.BusinessProgress.SessionBoundConfirmed);
    		logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.SessionBoundConfirmed.name(), device.getDeviceSn());
    		
    		if (checkAllDevicesReach(Consts.BusinessProgress.SessionBoundConfirmed))
        	{
        		logger.debug("All devices responsed after Device <{}> responsed.", device.getDeviceSn());
        		changeStatus(Consts.BusinessSessionStatus.ChatConfirm);
        	}
        	else
        	{
        		logger.debug("Device <{}> responsed but not all devices responsed.", device.getDeviceSn());
        	}
    	}
    }
    
    /** */
    public void onEndChat(IDeviceWrapper device)
    {
    	Consts.BusinessProgress progress;
    	synchronized(progressMap)
    	{
    		progress = progressMap.get(device.getDeviceSn());
    	}
    	
    	if (progress == null)
    	{
    		logger.error("Fatal error that received confirmation SessionBounded from device <{}> but there is no progress record for it!");
    		return;
    	}
    	
    	if (progress.compareTo(Consts.BusinessProgress.ChatConfirmed) != 0)
    	{
    		logger.error("Received confirmation EndChat from device <{}> but it's not in status of " + progress.name(), device.getDeviceSn());
    		return;
    	}
    	
    	synchronized(progressMap)
    	{
    		progressMap.put(device.getDeviceSn(), Consts.BusinessProgress.ChatEnded);
    		logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.ChatEnded.name(), device.getDeviceSn());
    	}
    	
    	if (this.status != Consts.BusinessSessionStatus.VideoChat)
    	{
    		logger.error("EndChat received from {} but current session status is: " + status.name(), device.getDeviceSn());
    		return;
    	}
    	
    	// Change status to Assess for any of App ends chat 
    	changeStatus(Consts.BusinessSessionStatus.Assess);
    	notifyDevices(device, Consts.NotificationType.OthersideEndChat);
    }
    
    /** */
    public List<IDeviceWrapper> getDeviceList()
    {
        return deviceList;
    }
    
    /** */
    public BusinessSessionStatus getStatus()
    {
        return status;
    }
    
    @Override
    public void onAgreeChat(IDeviceWrapper device)
    {
    	Consts.BusinessProgress progress;
    	synchronized(progressMap)
    	{
    		progress = progressMap.get(device.getDeviceSn());
    	}
    	
		if (progress == null)
    	{
    		logger.error("Fatal error that received AgreeChat from device <{}> but there is no progress record for it!");
    		return;
    	}
    	
    	if (progress.compareTo(Consts.BusinessProgress.SessionBoundConfirmed) != 0)
    	{
    		logger.error("Received AgreeChat from device <{}> but it's in status of " + progress.name(), device.getDeviceSn());
    		return;
    	}

    	notifyDevices(device, Consts.NotificationType.OthersideAgreed);
    	
    	synchronized(progressMap)
    	{
    		progressMap.put(device.getDeviceSn(), Consts.BusinessProgress.ChatConfirmed);
    		logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.ChatConfirmed.name(), device.getDeviceSn());
    	}
    	
    	if (checkAllDevicesReach(Consts.BusinessProgress.ChatConfirmed))
    	{
    		logger.debug("All devices agreed chat after device <{}> agreed", device.getDeviceSn());
    		changeStatus(Consts.BusinessSessionStatus.VideoChat);
    	}
    	else
    	{
    		logger.debug("Device <{}> agreed chat but not all devices agreed.", device.getDeviceSn());
    	}
    }
    
    @Override
    public void onRejectChat(IDeviceWrapper device)
    {
    	Consts.BusinessProgress progress;
    	synchronized(progressMap)
    	{
    		progress = progressMap.get(device.getDeviceSn());
    	}
    	
    	if (progress == null)
    	{
    		logger.error("Fatal error that received confirmation RejectChat from device <{}> but there is no progress record for it!");
    		return;
    	}
    	
    	if (progress.compareTo(Consts.BusinessProgress.SessionBoundConfirmed) != 0)
    	{
    		logger.error("Received RejectChat from device <{}> but it's in status of " + progress.name(), device.getDeviceSn());
    		return;
    	}
    	
    	notifyDevices(device, Consts.NotificationType.OthersideRejected);
		
		endReason = Consts.SessionEndReason.Reject;
    	changeStatus(Consts.BusinessSessionStatus.Idle);
    }

    @Override
    public void onDeviceLeave(IDeviceWrapper device, Consts.DeviceLeaveReason reason)
    {
    	synchronized(progressMap)
    	{
    		progressMap.remove(device.getDeviceSn());
    	}
    	
    	synchronized(deviceList)
    	{
    		deviceList.remove(device);
    		logger.debug("Device <{}> was removed from business session", device.getDeviceSn());
    	}
    	
    	if (status == Consts.BusinessSessionStatus.VideoChat
    			|| status == Consts.BusinessSessionStatus.Assess)
    	{
    		if (reason == Consts.DeviceLeaveReason.TimeoutOfActivity
    				|| reason == Consts.DeviceLeaveReason.TimeoutOfPing
    				|| reason == Consts.DeviceLeaveReason.TimeoutOnSyncSending)
    		{
    			device.increaseChatLoss();
    		}
    	}
    	
    	device.unbindBusinessSession();
    	List<IDeviceWrapper> tmpList = new ArrayList<IDeviceWrapper>(deviceList);
    	switch(status)
    	{
    		case Idle:
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    			
    		case ChatConfirm:
    			logger.debug("Business session will be released due to device leave, current status: ChatConfirm");
    			endReason = Consts.SessionEndReason.ConnectionLoss;
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    			
    		case VideoChat:
    			// Do nothing if
    			//changeStatus(Consts.BusinessSessionStatus.VideoChat);
    			endReason = Consts.SessionEndReason.ConnectionLoss;
    			break;
			
    		case Assess:
    			// If not all devices finished assess
    			if (checkAllDevicesReach(Consts.BusinessProgress.AssessFinished))
    	    	{
    	    		logger.debug("All devices assessed, trying to close BusinessSession");
    	    		changeStatus(Consts.BusinessSessionStatus.Idle);
    	    	}
    			break;
    			
    		default:
				logger.error("Invalid status of BusinessSession: {}", status.name());
				break;
    	}
    	
    	notifyDevices(tmpList, device, Consts.NotificationType.OthersideLost);
    }

    @Override
    public void onAssessAndContinue(IDeviceWrapper sourceDevice)
    {
    	BusinessType type = sourceDevice.getBusinessType();
    	AbstractBusinessDevicePool pool = OnlineDevicePool.instance.getBusinessPool(type);
    	pool.endChat(sourceDevice);
    	
    	sourceDevice.changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
    	
    	synchronized(progressMap)
    	{
    		progressMap.put(sourceDevice.getDeviceSn(), Consts.BusinessProgress.AssessFinished);
    		logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.AssessFinished.name(), sourceDevice.getDeviceSn());
    	}
    	
    	if (checkAllDevicesReach(Consts.BusinessProgress.AssessFinished))
    	{
    		logger.debug("All devices finished assess after device <{}> assessed", sourceDevice.getDeviceSn());
    		endReason = Consts.SessionEndReason.NormalEnd;
			changeStatus(Consts.BusinessSessionStatus.Idle);
    	}
    	else
    	{
    		logger.debug("Device <{}> assessed but not all devices assessed.", sourceDevice.getDeviceSn());
    	}
    }
    
	@Override
	public void onAssessAndQuit(IDeviceWrapper sourceDevice)
	{
		BusinessType type = sourceDevice.getBusinessType();
    	AbstractBusinessDevicePool pool = OnlineDevicePool.instance.getBusinessPool(type);
    	pool.onDeviceLeave(sourceDevice, Consts.DeviceLeaveReason.AssessAndQuit);
    	
    	sourceDevice.changeBusinessStatus(Consts.BusinessStatus.Idle);
    	synchronized(progressMap)
    	{
    		progressMap.put(sourceDevice.getDeviceSn(), Consts.BusinessProgress.AssessFinished);
    		logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.AssessFinished.name(), sourceDevice.getDeviceSn());
    	}
    	
    	if (checkAllDevicesReach(Consts.BusinessProgress.AssessFinished))
    	{
    		logger.debug("All devices finished assess after device <{}> assessed", sourceDevice.getDeviceSn());
    		endReason = Consts.SessionEndReason.NormalEnd;
			changeStatus(Consts.BusinessSessionStatus.Idle);
    	}
    	else
    	{
    		logger.debug("Device <{}> assessed but not all devices assessed.", sourceDevice.getDeviceSn());
    	}
	}

	@Override
	public void bindBusinessDevicePool(AbstractBusinessDevicePool pool)
	{
		logger.debug("Bind business session with business device pool");
		this.pool = pool;
	}

	@Override
	public void unbindBusinessDevicePool()
	{
		logger.debug("Unbind business session from business device pool");
		this.pool = null;
	}

	@Override
	public Consts.BusinessProgress getProgressOfDevice(IDeviceWrapper device)
	{
		Consts.BusinessProgress progress = progressMap.get(device.getDeviceSn());
		if (progress == null)
		{
			return Consts.BusinessProgress.Invalid;
		}
		return progress;
	}
	@Override
	public boolean checkProgressForRequest(IDeviceWrapper device, OperationType operationType)
	{
		Consts.BusinessProgress progress = progressMap.get(device.getDeviceSn());
		if (progress == null)
		{
			return false;
		}
		
		switch(progress)
		{
			case Init:
				break;
			case SessionBoundConfirmed:
				if (operationType != Consts.OperationType.AgreeChat
						&& operationType != Consts.OperationType.RejectChat
						&& operationType != Consts.OperationType.LeavePool)
				{
					return false;
				}
			break;
			case ChatConfirmed:
				if (operationType != Consts.OperationType.EndChat
						&& operationType != Consts.OperationType.LeavePool)
				{
					return false;
				}
				break;
			case ChatEnded:
				if (operationType != Consts.OperationType.AssessAndContinue
						&& operationType != Consts.OperationType.AssessAndQuit)
				{
					return false;
				}
				break;
			case AssessFinished:
				return false;
		}
		return true;
	}
}
