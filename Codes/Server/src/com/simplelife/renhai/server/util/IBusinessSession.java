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

import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.util.Consts.BusinessSessionStatus;


/** */
public interface IBusinessSession
{
    /** */
    public void onDeviceLeave(IDeviceWrapper device);
    
    /** */
    //public void startSession(LinkedList<IDeviceWrapper> deviceList);
    
    public void startSession(List<String> deviceList);
    
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
    public List<IDeviceWrapper> getDeviceList();
    
    /** */
    public BusinessSessionStatus getStatus();
    
    public String getSessionId();
    
    /** */
    public void onAssessAndContinue(IDeviceWrapper sourceDevice, IDeviceWrapper targetDevice);
    
    public void onAssessAndQuit(IDeviceWrapper sourceDevice, IDeviceWrapper targetDevice);
    
    public void onBindConfirm(IDeviceWrapper device);
    
    public void onAgreeChat(IDeviceWrapper device);
    
    public void onRejectChat(IDeviceWrapper device);
    
    public void bindBusinessDevicePool(AbstractBusinessDevicePool pool);
    
    public void unbindBusinessDevicePool();
    
}
