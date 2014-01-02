/**
 * BusinessSession.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.InputMsgExecutorPool;
import com.simplelife.renhai.server.business.pool.MessageHandler;
import com.simplelife.renhai.server.business.pool.OutputMsgExecutorPool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Sessionrecord;
import com.simplelife.renhai.server.db.Webrtcsession;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessSessionEventType;
import com.simplelife.renhai.server.util.Consts.BusinessSessionStatus;
import com.simplelife.renhai.server.util.Consts.DeviceBusinessProgress;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.NotificationType;
import com.simplelife.renhai.server.util.Consts.OperationType;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IRunnableMessage;
import com.simplelife.renhai.server.util.JSONKey;


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
	private Webrtcsession webRTCSession;
	private AtomicBoolean webRTCSessionRecycleFlag = new AtomicBoolean(false);
	private JSONObject matchCondition;
	
	private Consts.SessionEndReason endReason = Consts.SessionEndReason.Invalid;
	
	private List<IDeviceWrapper> deviceList; 
	
	// Temp list for saving devices waiting for confirmation
	//private List<IDeviceWrapper> tmpConfirmDeviceList = new ArrayList<IDeviceWrapper>();
	private ConcurrentHashMap<String, Consts.DeviceBusinessProgress> progressMap = new ConcurrentHashMap<String, Consts.DeviceBusinessProgress>();
	
	private Consts.BusinessSessionStatus status = Consts.BusinessSessionStatus.Idle;
	
	private MessageHandler eventHandler = new MessageHandler(sessionId, InputMsgExecutorPool.instance);
	
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
    private boolean checkAllDevicesReach(Consts.DeviceBusinessProgress progress)
    {
    	Set<Entry<String, DeviceBusinessProgress>> entrySet = progressMap.entrySet();
    	for (Entry<String, DeviceBusinessProgress> entry : entrySet)
    	{
    		if (entry.getValue().compareTo(progress) < 0)
    		{
    			return false;
    		}
    	}
    	return true;
    }
    
    private boolean checkStartSession(List<IDeviceWrapper> deviceList)
    {
    	if (deviceList == null)
    	{
    		return false;
    	}
    	
    	if (deviceList.isEmpty())
    	{
    		return false;
    	}
    	
    	webRTCSession = WebRTCSessionPool.instance.getWebRTCSession();
    	if (webRTCSession == null)
    	{
    		logger.error("Fatal error: no available WebRTC session for starting business session");
    		return false;
    	}
    	
    	webRTCSessionRecycleFlag.set(false);
    	
    	Consts.DeviceStatus status;
    	int size = deviceList.size();
    	
    	for (IDeviceWrapper deviceWrapper : deviceList)
    	{
    		if (deviceWrapper == null)
    		{
    			logger.error("Fatal error: DeviceWrapper is null when trying to bind with session.");
    			pool.onDeviceLeave(deviceWrapper, Consts.StatusChangeReason.UnknownBusinessException);
    			return false;
    		}
    		
    		status = deviceWrapper.getBusinessStatus();
    		
    		if ( status != Consts.DeviceStatus.MatchStarted)
    		{
    			logger.error("Abnormal business status of device <{}> when trying to start session: " + status.name(), deviceWrapper.getDeviceIdentification());
    			return false;
    		}
    	}
    	return true;
    }
    
    @Override
    public synchronized void endSession()
    {
    	if (pool == null)
    	{
    		logger.debug("Enter endSession, but session has been ended.");
    		return;
    	}
    	
    	logger.debug("Enter endSession with id {}.", sessionId);
    	sessionEndTime = System.currentTimeMillis();

    	//logger.debug("===============2000===============clear deviceList of session: {}", this.getSessionId());
    	deviceList.clear();
    	deviceList = null;
    	
   		progressMap.clear();
   		
   		matchCondition = null;
   		recycleWebRTCSession();
    	
    	saveSessionRecord();
    	unbindBusinessDevicePool();
    	BusinessSessionPool.instance.recycleBusinessSession(this);
    }
    
    private void recycleWebRTCSession()
    {
    	if (webRTCSessionRecycleFlag.compareAndSet(false, true))
		{
			WebRTCSessionPool.instance.recycleWetRTCSession(webRTCSession);
			webRTCSession = null;
		}
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
    	record.setChatStartTime(this.chatStartTime);
    	
    	int duration = (int)((chatEndTime - chatStartTime)/1000);
    	record.setChatDuration(duration);
    	
    	duration = (int)((sessionEndTime - sessionStartTime)/1000);
    	record.setSessionDuration(duration);
    	
    	record.setEndStatus(status.name());
    	record.setEndReason(endReason.name());
    	
    	DAOWrapper.instance.cache(record);
    }
    
    @Override
    public boolean startSession(List<IDeviceWrapper> deviceList, JSONObject matchCondition)
    {
    	sessionStartTime = System.currentTimeMillis();
    	chatStartTime = 0;
    	chatEndTime = 0;
    	sessionEndTime = 0;
    	this.matchCondition = matchCondition;
    	this.deviceList = deviceList;
    	
    	if (logger.isDebugEnabled())
    	{
    		String tmpStr = "Enter startSession with id " + sessionId + ", deviceList:";  
	    	for (IDeviceWrapper device : deviceList)
	    	{
	    		tmpStr += "<" + device.getDeviceIdentification() + "> ";
	    	}
    		logger.debug(tmpStr);
    	}
    	
    	if (!checkStartSession(deviceList))
    	{
    		endSession();
    		return false;
    	}
    	
    	try
    	{
    		int size = deviceList.size();
    		IDeviceWrapper device;
    		for (int i = 0; i < size; i++)
	    	{
    			device = deviceList.get(i);
    			if (device == null)
    			{
    				logger.error("Fatal Error: device object enclosed in Device <{}> is null!", device.getDeviceIdentification());
    				continue;
    			}
    			
	    		updateBusinessProgress(device.getDeviceIdentification(), Consts.DeviceBusinessProgress.Init);
	    		device.bindBusinessSession(this);
	    		device.changeBusinessStatus(DeviceStatus.SessionBound, Consts.StatusChangeReason.BusinessSessionStarted);
	    	}
    	}
    	catch(Exception e)
    	{
    		FileLogger.printStackTrace(e);
    	}
    	
    	notifyDevices(null, Consts.NotificationType.SessionBound, null);
    	//newEvent(BusinessSessionEventType.NotifySessionBound, null, null);
    	return true;
    }
    
    public void notifyIncreaseChatDuration(int duration)
    {
    	for (IDeviceWrapper device : deviceList)
    	{
			device.increaseChatDuration(duration);
    	}
    }
    
    
    public void notifyIncreaseChatCount()
    {
    	for (IDeviceWrapper device : deviceList)
    	{
			device.increaseChatCount();
    	}
    }
    
    private void defaultGoodAssess(Collection<IDeviceWrapper> activeDeviceList, IDeviceWrapper triggerDevice)
    {
    	logger.debug("Device <{}> provides default good assess due to its connection loss", triggerDevice.getDeviceIdentification());
    	if (activeDeviceList == null)
    	{
    		logger.debug("Device <{}>, return directly due to activeDeviceList is null", triggerDevice.getDeviceIdentification());
    		return;
    	}
    	
    	for (IDeviceWrapper device : activeDeviceList)
    	{
    		Impresscard card = device
					.getDevice()
					.getProfile()
					.getImpressCard();
    		if (device == triggerDevice)
			{
				card.updateOrAppendImpressLabel(device, Consts.SolidImpressLabel.Good.getValue(), false);
				device.increaseChatCount();
			}
    		else
    		{
				card.updateOrAppendImpressLabel(device, Consts.SolidImpressLabel.Good.getValue(), true);
    		}
    	}
    }
   
    public void notifyDevices(IDeviceWrapper triggerDevice, 
    		Consts.NotificationType notificationType,
    		JSONObject operationInfoObj)
    {
    	/*
    	String tempStr;
    	if (triggerDevice == null)
    	{
    		tempStr = "SessionBound";
    	}
    	else
    	{
    		tempStr = triggerDevice.getDeviceIdentification();
    	}
    	*/
    	
    	if (deviceList == null)
    	{
    		if (logger.isDebugEnabled())
    		{
    			String temp = "Cancel notification of " + notificationType.name() + " from device <{" + triggerDevice.getDeviceIdentification() + "}> due to deviceList is null";
    			logger.debug(temp);
    		}
    		return;
    	}
    	
    	String temp;
    	for (IDeviceWrapper device : deviceList)
    	{
    		if (device == triggerDevice)
			{
				continue;
			}

			// Check if status of device is allowed for notification
			if (!checkProgressForNotification(device, notificationType, operationInfoObj))
			{
				continue;
			}
			
			// create notification for each device, to avoid conflict of multi-thread on devices
			if (logger.isDebugEnabled())
			{
				temp = "Notify device <"+ device.getDeviceIdentification() +"> about " + notificationType.name();
				if (triggerDevice != null)
				{
					temp += " from device <" + triggerDevice.getDeviceIdentification() + ">";
				}
				logger.debug(temp);
			}
			
			if (operationInfoObj != null)
			{
				createNotifyEvent(device, notificationType, operationInfoObj);
			}
			else
			{
				JSONObject tmpObj;
				if(triggerDevice == null ||  notificationType == NotificationType.SessionBound)
				{
					tmpObj = new JSONObject();
					tmpObj.put(JSONKey.Device, getOperationInfoOfOtherDevices(device));
				    
				    JSONObject rtcObj = new JSONObject();
				    rtcObj.put(JSONKey.ApiKey, GlobalSetting.BusinessSetting.OpenTokKey);
				    rtcObj.put(JSONKey.SessionId, this.webRTCSession.getWebrtcsession());
				    rtcObj.put(JSONKey.Token, this.webRTCSession.getToken());
				    tmpObj.put(JSONKey.Webrtc, rtcObj);
				    tmpObj.put(JSONKey.MatchedCondition, this.matchCondition);
				  
				    updateBusinessProgress(device.getDeviceIdentification(), DeviceBusinessProgress.SessionBoundNotified);
				}
				else
				{
					tmpObj = new JSONObject();
					JSONObject deviceObj = new JSONObject();
					deviceObj.put(JSONKey.DeviceSn, device.getDeviceIdentification());
					tmpObj.put(JSONKey.Device, deviceObj);
				}
				
				createNotifyEvent(device, notificationType, tmpObj);
			}
    	}
	}
    
    private void createNotifyEvent(IDeviceWrapper device, 
    		Consts.NotificationType notificationType,
    		JSONObject operationInfoObj)
    {
    	switch(notificationType)
		{
			case OthersideAgreed:
				newEvent(BusinessSessionEventType.NotifyOthersideAgreed, device, operationInfoObj);
				break;
			case OthersideChatMessage:
				newEvent(BusinessSessionEventType.NotifyOthersideChatMessage, device, operationInfoObj);
				break;
			case OthersideEndChat:
				newEvent(BusinessSessionEventType.NotifyOthersideEndChat, device, operationInfoObj);
				break;
			case OthersideLost:
				newEvent(BusinessSessionEventType.NotifyOthersideLost, device, operationInfoObj);
				break;
			case OthersideRejected:
				newEvent(BusinessSessionEventType.NotifyOthersideRejected, device, operationInfoObj);
				break;
			case SessionBound:
				newEvent(BusinessSessionEventType.NotifySessionBound, device, operationInfoObj);
				break;
			case Invalid:
				logger.error("Invalid notification type found for device <{}>", device.getDeviceIdentification());
				break;
		}
    }
    
    @Override
    public void notifyDevice(IDeviceWrapper device, 
    		NotificationType notificationType, 
    		JSONObject operationInfoObj)
    {
    	if (device == null)
    	{
    		logger.warn("Null DeviceWrapper found when trying to notify {}", notificationType.name());
    		return;
    	}
    	else if (device.getDevice() == null)
    	{
    		logger.warn("Null device found when trying to notify {} about " + notificationType.name(), device.getDeviceIdentification());
    		return;
    	}
    	
    	ServerJSONMessage notify = JSONFactory.createServerJSONMessage(null, Consts.MessageId.BusinessSessionNotification);
    	JSONObject header = notify.getHeader(); 
		header.put(JSONKey.MessageSn, CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfMessageSn));
		header.put(JSONKey.DeviceId, device.getDevice().getDeviceId());
		header.put(JSONKey.DeviceSn, device.getDeviceIdentification());
		
		JSONObject body = notify.getBody(); 
    	body.put(JSONKey.BusinessSessionId, CommonFunctions.getJSONValue(sessionId));
    	body.put(JSONKey.OperationType, notificationType.getValue());
    	body.put(JSONKey.OperationValue, null);
    	body.put(JSONKey.BusinessType, device.getBusinessType().getValue());
    	body.put(JSONKey.OperationInfo, operationInfoObj);
    	
    	if (notificationType == NotificationType.SessionBound)
		{
			notify.setDelayOfHandle(GlobalSetting.BusinessSetting.DelayOfSessionBound);
		}
    	
    	notify.setDeviceWrapper(device);
		device.prepareResponse(notify);
    }
    
    private JSONObject getOperationInfoOfOtherDevices(IDeviceWrapper deviceToBeExcluded)
    {
    	if (deviceList == null)
    	{
    		return null;
    	}
    	
    	for (IDeviceWrapper device : deviceList)
    	{
    		if (device != deviceToBeExcluded)
    		{
    			return device.toJSONObject();
    		}
    	}
    	return null;
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
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("[Milestone] Status of " + sessionId + " will be changed from {} to " + targetStatus.name(), status.name());
    	}
    	
    	if (status == targetStatus)
    	{
    		if (targetStatus == Consts.BusinessSessionStatus.Idle && pool != null)
    		{
				endSession();
    		}
    		return;
    	}
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
    				//notifyIncreaseChatCount();
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			
    			break;
    			
    		case Assess:
    			if (chatEndTime == 0)
				{
					chatEndTime = System.currentTimeMillis();
					notifyIncreaseChatDuration((int)(chatEndTime - chatStartTime));
				}
    			
    			if ((status != Consts.BusinessSessionStatus.VideoChat) && (status != Consts.BusinessSessionStatus.Assess))
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			break;
    			
			default:
				break;
    	}
    	status = targetStatus;
    	
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("[Milestone] Status of " + sessionId + " changed to {}", status.name());
    	}
    }
    
    
    /** */
    public synchronized void onBindConfirm(IDeviceWrapper device)
    {
    	Consts.DeviceBusinessProgress progress;
   		progress = progressMap.get(device.getDeviceIdentification());
    	
    	if (progress == null)
    	{
    		logger.error("Fatal error that received confirmation SessionBound from device <{}> but there is no progress record for it!");
    		return;
    	}
    	
    	/*
    	if (progress.getValue() >= Consts.BusinessProgress.ChatConfirmed.getValue())
    	{
    		// TODO: it's not good here, normal business progress is broken
    		// Purpose of the code is handling abnormal situation of confirm of SessionBounded is handled after AgreeChat
    		logger.debug("Business progress of " + device.getDeviceSn() + " is " + progress.name() + " and response of SessionBound is ignored");
    		return;
    	}
    	*/
    	
    	if (progress != Consts.DeviceBusinessProgress.SessionBoundNotified)
    	{
    		logger.error("Received confirmation SessionBound from device <{}> but its business progress is " + progress.name(), device.getDeviceIdentification());
    		return;
    	}
    	
    	if (this.status != Consts.BusinessSessionStatus.Idle)
    	{
    		logger.error("Bind confirm received from {} while current session status is: " + status.name(), device.getDeviceIdentification());
    		return;
    	}
    	
    	logger.debug("Device <{}> confirmed SessionBound", device.getDeviceIdentification());
    	updateBusinessProgress(device.getDeviceIdentification(), Consts.DeviceBusinessProgress.SessionBoundAcked);
    	//progressMap.put(device.getDeviceSn(), Consts.BusinessProgress.SessionBoundConfirmed);
		//logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.SessionBoundConfirmed.name(), device.getDeviceSn());
		
		if (checkAllDevicesReach(Consts.DeviceBusinessProgress.SessionBoundAcked))
    	{
    		logger.debug("All devices responded after Device <{}> responded.", device.getDeviceIdentification());
    		changeStatus(Consts.BusinessSessionStatus.ChatConfirm);
    	}
    	else
    	{
    		logger.debug("Device <{}> responded but not all devices responded.", device.getDeviceIdentification());
    	}
		
		device.enterChatConfirm();
    }
    
    /** */
    public void onEndChat(IDeviceWrapper device)
    {
    	Consts.DeviceBusinessProgress progress;
   		progress = progressMap.get(device.getDeviceIdentification());
    	
    	if (progress == null)
    	{
    		logger.error("Fatal error that received confirmation SessionBound from device <{}> but there is no progress record for it!");
    		return;
    	}
    	
    	if (progress != Consts.DeviceBusinessProgress.ChatAgreed)
    	{
    		logger.error("Received EndChat from device <{}> but it's not in status of " + progress.name(), device.getDeviceIdentification());
    		return;
    	}
    	
    	updateBusinessProgress(device.getDeviceIdentification(), Consts.DeviceBusinessProgress.ChatEnded);
		//progressMap.put(device.getDeviceSn(), Consts.BusinessProgress.ChatEnded);
		//logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.ChatEnded.name(), device.getDeviceSn());
    	
    	if (this.status != Consts.BusinessSessionStatus.VideoChat
    			&& this.status != Consts.BusinessSessionStatus.Assess)
    	{
    		logger.error("EndChat received from <{}> but status of session "+ sessionId +" is: " + status.name(), device.getDeviceIdentification());
    		return;
    	}
    	
    	// Change status to Assess for any of App ends chat 
    	changeStatus(Consts.BusinessSessionStatus.Assess);
    	notifyDevices(device, Consts.NotificationType.OthersideEndChat, null);
    }
    
    /** */
    public BusinessSessionStatus getStatus()
    {
        return status;
    }
    
    private void updateBusinessProgress(String deviceSn, DeviceBusinessProgress progress)
    {
    	if (progress != DeviceBusinessProgress.Init)
    	{
    		String sTemp = "[Milestone] Business progress of Device <" + deviceSn + "> is changed to " + progress.name();
    		
    		DeviceBusinessProgress tmpProgress = progressMap.get(deviceSn);
    		if (tmpProgress == null)
    		{
    			sTemp += ", but its progress is not saved in BusinessSesion currently";
    		}
    		logger.debug(sTemp);
    	}
    	progressMap.put(deviceSn, progress);
    }
    
    public void newEvent(BusinessSessionEventType eventType, IDeviceWrapper device, Object operationInfo)
    {
    	BusinessSessionEvent event = new BusinessSessionEvent(eventType, device, operationInfo, this);
    	eventHandler.addMessage(event);
    }
    
    @Override
    public void onAgreeChat(IDeviceWrapper device)
    {
    	Consts.DeviceBusinessProgress progress;
   		progress = progressMap.get(device.getDeviceIdentification());
    	
		if (progress == null)
    	{
    		logger.error("Fatal error that received AgreeChat from device <{}> but there is no progress record for it!");
    		return;
    	}
    	
    	if (progress != Consts.DeviceBusinessProgress.SessionBoundAcked)
    	{
    		logger.error("Received AgreeChat from device <{}> but its business progress is " + progress.name(), device.getDeviceIdentification());
    		return;
    	}

    	notifyDevices(device, Consts.NotificationType.OthersideAgreed, null);
    	
    	updateBusinessProgress(device.getDeviceIdentification(), Consts.DeviceBusinessProgress.ChatAgreed);
   		//progressMap.put(device.getDeviceSn(), Consts.BusinessProgress.ChatConfirmed);
   		//logger.debug("Business progress of device <{}> was updated to " + Consts.BusinessProgress.ChatConfirmed.name(), device.getDeviceSn());
    	
    	if (checkAllDevicesReach(Consts.DeviceBusinessProgress.ChatAgreed))
    	{
    		logger.debug("All devices agreed chat after device <{}> agreed", device.getDeviceIdentification());
    		changeStatus(Consts.BusinessSessionStatus.VideoChat);
    		
    		for (IDeviceWrapper tmpDevice : deviceList)
    		{
    			tmpDevice.leaveChatConfirm();
    		}
    	}
    	else
    	{
    		logger.debug("Device <{}> agreed chat but not all devices agreed so far.", device.getDeviceIdentification());
    	}
    }
    
    @Override
    public void onChatMessage(IDeviceWrapper device, String chatMessage)
    {
    	JSONObject obj = new JSONObject();
    	obj.put(JSONKey.ChatMessage, chatMessage);
    	notifyDevices(device, Consts.NotificationType.OthersideChatMessage, obj);
    }
    
    @Override
    public void onRejectChat(IDeviceWrapper device)
    {
    	Consts.DeviceBusinessProgress progress;
   		progress = progressMap.get(device.getDeviceIdentification());
    	
    	if (progress == null)
    	{
    		logger.error("Fatal error that received confirmation RejectChat from device <{}> but there is no progress record for it!", device.getDeviceIdentification());
    		return;
    	}
    	
    	if (progress.compareTo(Consts.DeviceBusinessProgress.SessionBoundAcked) != 0)
    	{
    		logger.error("Received RejectChat from device <{}> but its business progress is " + progress.name(), device.getDeviceIdentification());
    		return;
    	}
    	
    	notifyDevices(device, Consts.NotificationType.OthersideRejected, null);
		
		endReason = Consts.SessionEndReason.Reject;
    	//changeStatus(Consts.BusinessSessionStatus.Idle);
    }

    @Override
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason)
    {
    	if (deviceList == null)
    	{
    		return;
    	}
    	
    	//deviceList.remove(device);
   		//progressMap.remove(device.getDeviceIdentification());
   		
   		//device.unbindBusinessSession();
		logger.debug("Device <{}> was removed from business session due to " + reason.name(), device.getDeviceIdentification());
    	
		if (reason == Consts.StatusChangeReason.TimeoutOfActivity
				|| reason == Consts.StatusChangeReason.TimeoutOfPing
				|| reason == Consts.StatusChangeReason.TimeoutOnSyncSending
				|| reason == Consts.StatusChangeReason.WebSocketReconnect
				|| reason == Consts.StatusChangeReason.WebsocketClosedByApp)
		{
			if (this.getStatus() == BusinessSessionStatus.VideoChat)
			{
				device.increaseChatLoss();
			}
			if (chatStartTime > 0)
			{
				int duration = (int) (System.currentTimeMillis() - chatStartTime);
				device.increaseChatDuration(duration);
			}
			
			defaultGoodAssess(deviceList, device);
			notifyDevices(device, NotificationType.OthersideLost, null);
    	}
		else if (reason == Consts.StatusChangeReason.AppLeaveBusiness)
		{
			notifyDevices(device, NotificationType.OthersideRejected, null);
		}
		
		updateBusinessProgress(device.getDeviceIdentification(), DeviceBusinessProgress.Leaved);
   		
		// Recycle WebRTC session after any of device leaved 
		recycleWebRTCSession();
		
		if (checkAllDevicesReach(DeviceBusinessProgress.Leaved))
    	//if (deviceList == null || deviceList.isEmpty())
    	{
    		if (reason == StatusChangeReason.AssessAndContinue
    				|| reason == StatusChangeReason.AssessAndQuit)
    		{
    			endReason = Consts.SessionEndReason.NormalEnd;
    		}
    		else
    		{
    			endReason = Consts.SessionEndReason.AllDeviceRemoved;
    		}
    		changeStatus(Consts.BusinessSessionStatus.Idle);
    		return;
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
	public Consts.DeviceBusinessProgress getProgressOfDevice(IDeviceWrapper device)
	{
		Consts.DeviceBusinessProgress progress = progressMap.get(device.getDeviceIdentification());
		if (progress == null)
		{
			return Consts.DeviceBusinessProgress.Invalid;
		}
		return progress;
	}
	
	@Override
	public boolean checkProgressForNotification(IDeviceWrapper device, NotificationType notificationType, JSONObject operationInfoObj)
	{
		DeviceBusinessProgress progress = this.getProgressOfDevice(device);
		if (progress == DeviceBusinessProgress.Invalid)
		{
			logger.error("It's trying to check notification for Device <{}> but its progress is invalid", device.getDeviceIdentification());
			return false;
		}
		
		if (progress.getValue() >= Consts.DeviceBusinessProgress.ChatEnded.getValue())
		{
			// Cancel notification if device has ended chat or leaved session
			return false;
		}
		
		if (notificationType != NotificationType.SessionBound)
		{
			// It's not allowed to send Othersidexxx before notifying SessionBound
			if (progress.ordinal() < DeviceBusinessProgress.SessionBoundNotified.ordinal())
			{
				if (!eventHandler.isEmpty())
				{
					// Currently, it's designed to cover case of OthersideLost occurred before notification of SessionBound
					// Create new notification event to notify after notification of SessionBound 
					this.createNotifyEvent(device, notificationType, operationInfoObj);
				}
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean checkProgressForRequest(IDeviceWrapper device, OperationType operationType)
	{
		Consts.DeviceBusinessProgress progress = progressMap.get(device.getDeviceIdentification());
		if (progress == null)
		{
			return false;
		}
		
		switch(progress)
		{
			case Init:
				break;
			case SessionBoundAcked:
				if (operationType != Consts.OperationType.AgreeChat
						&& operationType != Consts.OperationType.RejectChat
						&& operationType != Consts.OperationType.SessionUnbind)
				{
					return false;
				}
			break;
			case ChatAgreed:
				if (operationType != Consts.OperationType.EndChat
						&& operationType != Consts.OperationType.SessionUnbind
						&& operationType != Consts.OperationType.ChatMessage)
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
			/*
			case AssessFinished:
				return false;
			*/
			default:
				logger.error("Invalid business progress:{}", progress.name());
				return false;
		}
		return true;
	}
}
