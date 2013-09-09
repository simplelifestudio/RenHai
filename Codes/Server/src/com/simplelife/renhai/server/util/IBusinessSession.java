/**
 * IBusinessSession.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.util.LinkedList;

import com.simplelife.renhai.server.util.Consts.BusinessSessionStatus;


/** */
public interface IBusinessSession
{
    /** */
    public void onChatConfirm(IDeviceWrapper device);
    
    /** */
    public void onDeviceLeave(IDeviceWrapper device);
    
    /** */
    public void bind(LinkedList deviceList);
    
    /**
     * BusinessSession需要调用该方法切换状态，该方法中会检查绑定的两个设备的连接情况，如果有设备的连接已经断开，BusinessSession需要根据对应的业务逻辑决定接下来的业务流程，比如：
     * 1. 如果处于双方确认状态，直接取消匹配
     * 2. 如果处于视频通话状态，结束视频通话
     * 3. 如果处于双方评价状态，等待连接正常的那一个设备评价结束后回收
     *
     * @param    status
    **/
    public void changeStatus(Consts.BusinessSessionStatus status);
    
    /** */
    public void endChat();
    
    /** */
    public LinkedList<IDeviceWrapper> getDeviceList();
    
    /** */
    public BusinessSessionStatus getStatus();
    
    /** */
    public void impressAssess(IDeviceWrapper sourceDevice, String impressLabels);
}
