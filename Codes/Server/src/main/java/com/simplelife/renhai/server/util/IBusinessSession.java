/**
 * IBusinessSession.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.util.Consts.BusinessSessionEventType;
import com.simplelife.renhai.server.util.Consts.BusinessSessionStatus;
import com.simplelife.renhai.server.util.Consts.NotificationType;


/** */
public interface IBusinessSession
{
    /** */
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason);
    
    /** */
    public boolean startSession(List<IDeviceWrapper> deviceList, JSONObject matchCondition);
    
    public void endSession();
    
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
    public void onEndChat(IDeviceWrapper device);
    
    /** */
    public BusinessSessionStatus getStatus();
    
    public String getSessionId();
    
    public void newEvent(BusinessSessionEventType eventType, IDeviceWrapper device, Object operationInfo);
    
    /** */
    //public void onAssessAndContinue(IDeviceWrapper sourceDevice);
    
    //public void onAssessAndQuit(IDeviceWrapper sourceDevice);
    
    public void onBindConfirm(IDeviceWrapper device);
    
    public void onAgreeChat(IDeviceWrapper device);
    
    public void onRejectChat(IDeviceWrapper device);
    
    public void onChatMessage(IDeviceWrapper device, String chatMessage);
    
    public void bindBusinessDevicePool(AbstractBusinessDevicePool pool);
    
    public void unbindBusinessDevicePool();
    
    public void notifyDevices(IDeviceWrapper triggerDevice, NotificationType notificationType, JSONObject operationInfoObj);
    
    public void notifyDevice(IDeviceWrapper device, NotificationType notificationType, JSONObject operationInfoObj);
    
    public boolean checkProgressForRequest(IDeviceWrapper device, Consts.OperationType operationType);
    
    public boolean checkProgressForNotification(IDeviceWrapper device, NotificationType notificationType, JSONObject operationInfoObj);
    
    public Consts.DeviceBusinessProgress getProgressOfDevice(IDeviceWrapper device);
}
