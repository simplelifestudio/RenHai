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
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
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
	
	private List<IDeviceWrapper> deviceList = new ArrayList<IDeviceWrapper>(); 
	
	// Temp list for saving devices waiting for confirmation
	private List<IDeviceWrapper> tmpConfirmDeviceList = new ArrayList<IDeviceWrapper>();
	
	private Consts.BusinessSessionStatus status = Consts.BusinessSessionStatus.Idle;
	
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
    
    /** */
    public void onBindConfirm(IDeviceWrapper device)
    {
    	if (!tmpConfirmDeviceList.contains(device))
    	{
    		logger.error("Received confirmation from device <{}> but it's not in status of waiting confirmation", device.getDeviceSn());
    		return;
    	}
    	
    	if (this.status != Consts.BusinessSessionStatus.Idle)
    	{
    		logger.error("Bind confirm received from {} while curret session status is: " + status.name(), device.getDeviceSn());
    		return;
    	}
    	
    	synchronized (tmpConfirmDeviceList)
		{
    		tmpConfirmDeviceList.remove(device);
		}
    	
    	synchronized(deviceList)
    	{
    		deviceList.add(device);
    	}
    	
    	// If not all devices confirmed
		if (tmpConfirmDeviceList.size() > 0)
		{
			return;
		}
    	
    	changeStatus(Consts.BusinessSessionStatus.ChatConfirm);
    }
   
    private boolean checkBind(List<String> deviceList)
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
    public void startSession(List<String> deviceList)
    {
    	if (!checkBind(deviceList))
    	{
    		return;
    	}
    	
    	IDeviceWrapper device;
    	for (String deviceSn : deviceList)
    	{
    		device = OnlineDevicePool.instance.getDevice(deviceSn);
    		pool.startChat(device);
    		device.bindBusinessSession(this);
    		
    		tmpConfirmDeviceList.add(device);
    	}
    	
    	notifyDevices(null, Consts.NotificationType.SessionBinded);
    }
    
    private void notifyDevices(IDeviceWrapper triggerDevice, Consts.NotificationType notificationType)
    {
    	if (tmpConfirmDeviceList.size() == 0)
    	{
    		return;
    	}
    	
    	ServerJSONMessage notify = null;
    	switch(this.status)
    	{
    		case Assess:
    			break;
    			
    		case ChatConfirm:
    			notify = JSONFactory.createServerJSONMessage(null, Consts.MessageId.BusinessSessionNotification);
    			notify.getBody().put(JSONKey.BusinessSessionId, this.sessionId);
    			notify.getBody().put(JSONKey.BusinessType, this.pool.getBusinessType().toString());
    			notify.getBody().put(JSONKey.OperationType, notificationType.toString());
    			notify.getBody().put(JSONKey.OperationValue, "");
    			break;
    			
    		case Idle:
    			notify = JSONFactory.createServerJSONMessage(null, Consts.MessageId.BusinessSessionNotification);
    			notify.getBody().put(JSONKey.BusinessSessionId, this.sessionId);
    			notify.getBody().put(JSONKey.BusinessType, this.pool.getBusinessType().toString());
    			notify.getBody().put(JSONKey.OperationType, Consts.NotificationType.SessionBinded.toString());
    			notify.getBody().put(JSONKey.OperationValue, "");
    			break;
    			
    		case VideoChat:
    			break;
			default:
				break;
    	}
    	
		for (IDeviceWrapper device : tmpConfirmDeviceList)
    	{
			if (device == triggerDevice)
			{
				continue;
			}
			
			notify.getHeader().put(JSONKey.MessageSn, CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfMessageSn));
			notify.getBody().put(JSONKey.OperationInfo, triggerDevice.toJSONObject());
    		notify.setDeviceWrapper(device);
    		notify.syncResponse();
    	}
	}
    
    /*
    public void startSession(LinkedList<IDeviceWrapper> deviceList)
    {
    	this.deviceList = deviceList; 
    	for (IDeviceWrapper device : deviceList)
    	{
    		pool.startChat(device);
    		device.bindBusinessSession(this);
    	}
    }
    */
    
    private void resetDeviceForConfirm()
    {
    	synchronized (tmpConfirmDeviceList)
		{
			tmpConfirmDeviceList.addAll(deviceList);
		}
		synchronized (deviceList)
		{
			deviceList.clear();
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
    	switch(targetStatus)
    	{
    		case Idle:
    			unbindDevices();
    			break;
    			
    		case ChatConfirm:
    			if (status == Consts.BusinessSessionStatus.Idle)
    			{
    				status = targetStatus;
    				//sendChatConfirm();
    				resetDeviceForConfirm();
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			break;
    			
    		case VideoChat:
    			if (status == Consts.BusinessSessionStatus.ChatConfirm)
    			{
    				status = targetStatus;
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			break;
    			
    		case Assess:
    			if (status == Consts.BusinessSessionStatus.VideoChat)
    			{
    				status = targetStatus;
    				resetDeviceForConfirm();
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			break;
    			
			default:
				break;
    	}
    	
    	status = targetStatus;
    }
    
    private void sendChatConfirm()
    {
    	ServerJSONMessage notification = JSONFactory.createServerJSONMessage(null, Consts.MessageId.BusinessSessionNotification);
    	
    	for (IDeviceWrapper device : deviceList)
    	{
    		notification.setDeviceWrapper(device);
    		notification.asyncResponse();
    	}
    }
    
    private void unbindDevices()
    {
    	for (IDeviceWrapper device : deviceList)
    	{
    		device.unbindBusinessSession();
    	}
    	
    	tmpConfirmDeviceList.clear();
    	deviceList.clear();
    }
    
    /** */
    public void onEndChat(IDeviceWrapper device)
    {
    	// Change status to Assess for any of App ends chat 
    	changeStatus(Consts.BusinessSessionStatus.Assess);
    	
    	if (!tmpConfirmDeviceList.contains(device))
    	{
    		logger.error("Received confirmation from device <{}> but it's not in status of waiting confirmation", device.getDeviceSn());
    		return;
    	}
    	
    	synchronized (tmpConfirmDeviceList)
		{
    		tmpConfirmDeviceList.remove(device);
		}
    	
    	synchronized(deviceList)
    	{
    		deviceList.add(device);
    	}
    	
    	// If not all devices assess
		if (tmpConfirmDeviceList.size() > 0)
		{
			return;
		}
    	
    	changeStatus(Consts.BusinessSessionStatus.Idle);
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
    	if (!tmpConfirmDeviceList.contains(device))
    	{
    		logger.error("Received confirmation from device <{}> but it's not in status of waiting confirmation", device.getDeviceSn());
    		return;
    	}
    	
    	synchronized (tmpConfirmDeviceList)
		{
    		tmpConfirmDeviceList.remove(device);
		}
    	
    	synchronized(deviceList)
    	{
    		deviceList.add(device);
    	}
    	
    	// If not all devices confirmed
		if (tmpConfirmDeviceList.size() > 0)
		{
			return;
		}
		
		notifyDevices(device, Consts.NotificationType.OthersideAgreed);
    	changeStatus(Consts.BusinessSessionStatus.VideoChat);
    }
    
    @Override
    public void onRejectChat(IDeviceWrapper device)
    {
    	if (!tmpConfirmDeviceList.contains(device))
    	{
    		logger.error("Received confirmation from device <{}> but it's not in status of waiting confirmation", device.getDeviceSn());
    		return;
    	}
    	
    	synchronized (tmpConfirmDeviceList)
		{
    		tmpConfirmDeviceList.remove(device);
		}
    	
    	synchronized(deviceList)
    	{
    		deviceList.add(device);
    	}
    	
    	// If not all devices confirmed
		if (tmpConfirmDeviceList.size() > 0)
		{
			return;
		}
		
		notifyDevices(device, Consts.NotificationType.OthersideRejected);
    	changeStatus(Consts.BusinessSessionStatus.Idle);
    }

    @Override
    public void onDeviceLeave(IDeviceWrapper device)
    {
    	tmpConfirmDeviceList.remove(device);
    	deviceList.remove(device);
    	
    	device.unbindBusinessSession();
    	switch(status)
    	{
    		case Idle:
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    			
    		case ChatConfirm:
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    			
    		case VideoChat:
    			// Do nothing if
    			//changeStatus(Consts.BusinessSessionStatus.VideoChat);
    			break;
			
    		case Assess:
    			// If not all devices finished assess
				if (tmpConfirmDeviceList.size() > 0)
				{
					return;
				}
    			
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    			
    		default:
				logger.error("Invalid status of BusinessSession: {}", status.name());
				break;
    	}
    }

    @Override
    public void onAssessAndContinue(IDeviceWrapper sourceDevice, IDeviceWrapper targetDevice)
    {
    	if (targetDevice.getDeviceSn().equals(sourceDevice.getDeviceSn()))
    	{
    		return;
    	}
    	
    	BusinessType type = sourceDevice.getBusinessType();
    	AbstractBusinessDevicePool pool = OnlineDevicePool.instance.getBusinessPool(type);
    	pool.endChat(sourceDevice);
    }
    
	@Override
	public void onAssessAndQuit(IDeviceWrapper sourceDevice, IDeviceWrapper targetDevice)
	{
    	if (targetDevice.getDeviceSn().equals(sourceDevice.getDeviceSn()))
    	{
    		return;
    	}
    	
    	BusinessType type = sourceDevice.getBusinessType();
    	AbstractBusinessDevicePool pool = OnlineDevicePool.instance.getBusinessPool(type);
    	pool.deviceLeave(sourceDevice);
	}

	@Override
	public void bind(AbstractBusinessDevicePool pool)
	{
		this.pool = pool;
	}
}
