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
     * BusinessSession��Ҫ���ø÷����л�״̬���÷����л���󶨵������豸�����������������豸�������Ѿ��Ͽ���BusinessSession��Ҫ���ݶ�Ӧ��ҵ���߼�������������ҵ�����̣����磺
     * 1. �������˫��ȷ��״̬��ֱ��ȡ��ƥ��
     * 2. ���������Ƶͨ��״̬��������Ƶͨ��
     * 3. �������˫������״̬���ȴ�������������һ���豸���۽��������
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
