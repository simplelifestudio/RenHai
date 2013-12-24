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
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	@Before
	public void setUp() throws Exception
	{
		/*
		super.setUp();
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp(demoDeviceSn);
		mockApp2 = createNewMockApp(demoDeviceSn2);
		*/
	}
	
	@After
	public void tearDown() throws Exception
	{
		/*
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
		super.tearDown();
		*/
	}
	
	@Test
	public void test() throws InterruptedException
	{
		/*
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		IDeviceWrapper deviceWrapper1 = OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn());
		
		mockApp1.syncDevice();
		mockApp2.syncDevice();
		
		// Step_01 Mock����A�����������
		mockApp1.chooseBusiness(businessType);
		
		// Step_02 Mock����B�����������
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.chooseBusiness(businessType);
		
		// Step_03 Mock����A����B��ӡ��Ƭ
		mockApp1.assessAndContinue("˧��");
		assertTrue(mockApp1.lastReceivedCommandIsError());
		//fail("����server�Ļظ�");
		
		// Step_04 ���ã�RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		mockApp1.waitMessage();
		mockApp2.waitMessage();
		
		Thread.sleep(500);
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_05 Mock�¼���Aͬ������
		mockApp1.chatConfirm(true);
		mockApp1.waitMessage();
		
		// Step_06 Mock�¼���Bͬ������
		mockApp2.chatConfirm(true);
		mockApp2.waitMessage();
		// Step_07 ���ã�BusinessSession::getStatus
		
		
		// Step_08 Mock����A����B��ӡ��Ƭ
		mockApp1.assessAndContinue("^#SoSo#^,˧��");
		assertTrue(mockApp1.lastReceivedCommandIsError());
		
		// Step_09 Mock�¼���A����ͨ��
		mockApp1.endChat();
		
		// Step_10 ���ã�BusinessSession::getStatus
		Thread.sleep(500);
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_11 Mock�¼���A��A����
		//mockApp1.assess("˧��");
		
		// Step_12 Mock�¼���A��B����
		mockApp1.assessAndContinue("^#Disgusting#^,TC22_����1");
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndContinue));
		
		// Step_13 Mock�¼���B��A����
		mockApp2.assessAndContinue("^#Happy#^,��Ů,����,TC22_����2");
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndContinue));
		
		// Step_14 ���ݿ��飺A ӡ��Ƭ��Ϣ
		//fail("������ݿ��е�ӡ��Ƭ");
		
		// Step_15 ���ݿ��飺B ӡ��Ƭ��Ϣ
		//fail("������ݿ��е�ӡ��Ƭ");
		
		// Step_16 ���ã�BusinessSession::getStatus
		//Thread.sleep(500);
		//assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock����A��ѯӡ��Ƭ��ȫ����ǩ
		//fail("A��ѯӡ��Ƭ");
		
		// Step_18 Mock����B��ѯӡ��Ƭ��ǰ2����ǩ
		//fail("B��ѯӡ��Ƭ");
		*/
	}
}
