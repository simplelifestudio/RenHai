/**
 * IBusinessSession.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.util.Collection;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.util.Consts.BusinessSessionStatus;


/** */
public interface IBusinessSession
{
    /** */
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason);
    
    public void onDeviceEnter(IDeviceWrapper device);
    
    /** */
    public boolean startSession(List<IDeviceWrapper> deviceList, JSONObject matchCondition);
    
    public void endSession();
    
    /**
     * BusinessSession��Ҫ���ø÷����л�״̬���÷����л���󶨵������豸�����������������豸�������Ѿ��Ͽ���BusinessSession��Ҫ���ݶ�Ӧ��ҵ���߼�������������ҵ�����̣����磺
     * 1. �������˫��ȷ��״̬��ֱ��ȡ��ƥ��
     * 2. ���������Ƶͨ��״̬��������Ƶͨ��
     * 3. �������˫������״̬���ȴ�������������һ���豸���۽��������
     *
     * @param    status
    **/
    public void changeStatus(Consts.BusinessSessionStatus status);
    
    /** */
    public void onEndChat(IDeviceWrapper device);
    
    /** */
    public BusinessSessionStatus getStatus();
    
    public String getSessionId();
    
    /** */
    //public void onAssessAndContinue(IDeviceWrapper sourceDevice);
    
    //public void onAssessAndQuit(IDeviceWrapper sourceDevice);
    
    public void onBindConfirm(IDeviceWrapper device);
    
    public void onAgreeChat(IDeviceWrapper device);
    
    public void onRejectChat(IDeviceWrapper device);
    
    public void onChatMessage(IDeviceWrapper device, String chatMessage);
    
    public void bindBusinessDevicePool(AbstractBusinessDevicePool pool);
    
    public void unbindBusinessDevicePool();
    
    public void notifyDevices(IDeviceWrapper triggerDevice, Consts.NotificationType notificationType, JSONObject operationInfoObj);
    
    public boolean checkProgressForRequest(IDeviceWrapper device, Consts.OperationType operationType);
    
    public Consts.DeviceBusinessProgress getProgressOfDevice(IDeviceWrapper device);
}
