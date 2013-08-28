/**
 * BusinessSession.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.LinkedList;

import com.simplelife.renhai.server.util.BusinessSessionStatus;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class BusinessSession implements IBusinessSession
{
    /** */
    private BusinessSessionStatus status;
    
    /** */
    private LinkedList deviceList;
    
    /** */
    public void onImpressAssess(IDeviceWrapper sourceDevice, IDeviceWrapper targetDevice, String impressLabels)
    {
    
    }
    
    /** */
    public void checkWebRTCToken()
    {
    
    }
    
    /** */
    public void onBindConfirm()
    {
    
    }
    
   
   
    /** */
    public void bind(LinkedList deviceList)
    {
    }
    
    /**
     * BusinessSession需要调用该方法切换状态，该方法中会检查绑定的两个设备的连接情况，如果有设备的连接已经断开，BusinessSession需要根据对应的业务逻辑决定接下来的业务流程，比如：
     * 1. 如果处于双方确认状态，直接取消匹配
     * 2. 如果处于视频通话状态，结束视频通话
     * 3. 如果处于双方评价状态，等待连接正常的那一个设备评价结束后回收
     *
     * @param    status
    **/
    public void changeStatus(BusinessSessionStatus status)
    {
    }
    
    /** */
    public void endChat()
    {
    }
    
    /** */
    public LinkedList getDeviceList()
    {
        return null;
    }
    
    /** */
    public BusinessSessionStatus getStatus()
    {
        return null;
    }
    
  
    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IBusinessSession#onChatConfirm(com.simplelife.renhai.server.util.IDeviceWrapper)
     */
    @Override
    public void onChatConfirm(IDeviceWrapper device)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IBusinessSession#onDeviceLeave(com.simplelife.renhai.server.util.IDeviceWrapper)
     */
    @Override
    public void onDeviceLeave(IDeviceWrapper device)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IBusinessSession#impressAssess(com.simplelife.renhai.server.util.IDeviceWrapper, java.lang.String)
     */
    @Override
    public void impressAssess(IDeviceWrapper sourceDevice, String impressLabels)
    {
        // TODO Auto-generated method stub
        
    }
}
