/**
 * Test25TimeoutNotifyANoEcho.java
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


/**
 * 
 */
public class Test25TimeoutNotifyANoEcho extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
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
	
	/**
	 * Clear variables created in case
	 */
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
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;
		IDeviceWrapper deviceWrapper1 = OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn());
		IDeviceWrapper deviceWrapper2 = OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn());
		mockApp1.syncDevice();
		mockApp2.syncDevice();
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = onlinePool.getDeviceCount();
		
		// Step_02 ���ã�RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_03 ���ã�BusinessSessionPool::getCount
		int sessionCount = sessionPool.getDeviceCount();
		
		// Step_04 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock����A�����������
		mockApp1.chooseBusiness(businessType);
		businessPool.getBusinessScheduler().stopScheduler();
		
		// Step_07 Mock����B�����������
		mockApp2.chooseBusiness(businessType);
		
		Thread.sleep(500);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getDeviceCount());
		randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		mockApp2.clearLastReceivedCommand();
		mockApp1.stopAutoReply();
		businessPool.getBusinessScheduler().schedule();
		
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBound));
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBound));

		// Step_12 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getDeviceCount());
		sessionCount = sessionPool.getDeviceCount();
		
		// Step_13 ������Mock�¼���A onPing
		
		// Step_14 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock�¼���B ȷ�ϰ�
		mockApp2.chatConfirm(true);
		
		Thread.sleep(500);
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_21 �ȴ���ȷ�ϳ�ʱʱ��
		Thread.sleep(GlobalSetting.TimeOut.ChatConfirm * 2);
		
		// Step_22 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_23 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_24 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getDeviceCount());
		
		// Step_25 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount + 1, sessionPool.getDeviceCount());
		
		// Step_26 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		*/
	}
}
