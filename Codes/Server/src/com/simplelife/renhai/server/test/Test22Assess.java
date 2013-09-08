/**
 * Test22Assess.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test22Assess extends AbstractTestCase
{
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	@Before
	public void setUp() throws Exception
	{
		mockApp1 = createMockApp();
		mockApp2 = createMockApp();
	}
	
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
	}
	
	@Test
	public void test()
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random);
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		
		syncDevice(mockApp1);
		syncDevice(mockApp2);
		
		// Step_01 Mock����A�����������
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_02 Mock����B�����������
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_03 Mock����A����B��ӡ��Ƭ
		mockApp1.assess("˧��");
		fail("����server�Ļظ�");
		
		// Step_04 ���ã�RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_05 Mock�¼���Aͬ������
		mockApp1.sendNotificationResponse();
		mockApp1.chatConfirm(true);
		
		// Step_06 Mock�¼���Bͬ������
		mockApp2.sendNotificationResponse();
		mockApp2.chatConfirm(true);
		
		// Step_07 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_08 Mock����A����B��ӡ��Ƭ
		mockApp1.assess("˧��");
		fail("����server�Ļظ�");
		
		// Step_09 Mock�¼���A����ͨ��
		mockApp1.endChat();
		
		// Step_10 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_11 Mock�¼���A��A����
		//mockApp1.assess("˧��");
		
		// Step_12 Mock�¼���A��B����
		mockApp1.assess("˧��");
		
		// Step_13 Mock�¼���B��A����
		mockApp2.assess("��Ů");
		
		// Step_14 ���ݿ��飺A ӡ��Ƭ��Ϣ
		fail("������ݿ��е�ӡ��Ƭ");
		
		// Step_15 ���ݿ��飺B ӡ��Ƭ��Ϣ
		fail("������ݿ��е�ӡ��Ƭ");
		
		// Step_16 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock����A��ѯӡ��Ƭ��ȫ����ǩ
		fail("A��ѯӡ��Ƭ");
		
		// Step_18 Mock����B��ѯӡ��Ƭ��ǰ2����ǩ
		fail("B��ѯӡ��Ƭ");
	}
}
