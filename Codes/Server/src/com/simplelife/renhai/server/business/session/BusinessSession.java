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
import com.simplelife.renhai.server.util.Consts.BusinessSessionStatus;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class BusinessSession implements IBusinessSession
{
	private Logger logger = BusinessModule.instance.getLogger();
	private String sessionId;
	
	public BusinessSession()
	{
		this.sessionId = CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfSessionId);
	}
	
	public String getSessionId()
	{
		return sessionId;
	}
	
    /** */
    private Consts.BusinessSessionStatus status = Consts.BusinessSessionStatus.Idle;
    
    /** */
    private LinkedList<IDeviceWrapper> deviceList;
    
    /** */
    public void checkWebRTCToken()
    {
    
    }
    
    /** */
    public void onBindConfirm(IDeviceWrapper device)
    {
    	for (IDeviceWrapper deviceWrapper : deviceList)
    	{
    		if (!deviceWrapper.isSessionBindConfirmed())
    		{
    			return;
    		}
    	}
    	
    	changeStatus(Consts.BusinessSessionStatus.ChatConfirm);
    }
   
    /** */
    public void bind(LinkedList<IDeviceWrapper> deviceList)
    {
    	this.deviceList = deviceList; 
    	for (IDeviceWrapper device : deviceList)
    	{
    		device.bindBusinessSession(this);
    	}
    }
    
    /**
     * BusinessSession��Ҫ���ø÷����л�״̬���÷����л���󶨵������豸�����������������豸�������Ѿ��Ͽ���BusinessSession��Ҫ���ݶ�Ӧ��ҵ���߼�������������ҵ�����̣����磺
     * 1. �������˫��ȷ��״̬��ֱ��ȡ��ƥ��
     * 2. ���������Ƶͨ��״̬��������Ƶͨ��
     * 3. �������˫������״̬���ȴ�������������һ���豸���۽��������
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
    				sendChatConfirm();
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
    			}
    			else
    			{
    				logger.error("Invalid status change from " + status.name() + " to " + targetStatus.name());
    			}
    			break;
    			
			default:
				break;
    	}
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
    }
    /** */
    public void onEndChat()
    {
    	changeStatus(Consts.BusinessSessionStatus.Assess);
    }
    
    /** */
    public LinkedList getDeviceList()
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
    	for (IDeviceWrapper deviceWrapper : deviceList)
    	{
    		if (!deviceWrapper.isChatConfirmed())
    		{
    			return;
    		}
    	}
    	changeStatus(Consts.BusinessSessionStatus.VideoChat);
    }
    
    @Override
    public void onRejectChat(IDeviceWrapper device)
    {
    	changeStatus(Consts.BusinessSessionStatus.Idle);
    }

    @Override
    public void onDeviceLeave(IDeviceWrapper device)
    {
    	device.unbindBusinessSession();
    	switch(status)
    	{
    		case ChatConfirm:
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    		case Idle:
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    		case Assess:
    			for (IDeviceWrapper deviceWrapper : deviceList)
    			{
    				// If not all devices finished assess
    				if (!deviceWrapper.isAssessProvided() && !deviceWrapper.isConnectionLost())
    				{
    					return;
    				}
    			}
    			changeStatus(Consts.BusinessSessionStatus.Idle);
    			break;
    		case VideoChat:
    			changeStatus(Consts.BusinessSessionStatus.VideoChat);
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
}
