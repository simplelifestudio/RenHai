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
     * BusinessSession��Ҫ���ø÷����л�״̬���÷����л���󶨵������豸�����������������豸�������Ѿ��Ͽ���BusinessSession��Ҫ���ݶ�Ӧ��ҵ���߼�������������ҵ�����̣����磺
     * 1. �������˫��ȷ��״̬��ֱ��ȡ��ƥ��
     * 2. ���������Ƶͨ��״̬��������Ƶͨ��
     * 3. �������˫������״̬���ȴ�������������һ���豸���۽��������
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
