/**
 * Test13FailToNotifyB.java
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
public class Test13FailToNotifyB extends AbstractTestCase
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
		
		MockWebSocketConnection connection2 = getMockWebSocket(deviceWrapper2);
		
		mockApp1.syncDevice();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		mockApp2.syncDevice();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getDeviceCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getDeviceCount();
		
		sessionPool.getDeviceCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.clearLastReceivedCommand();
		mockApp1.chooseBusiness(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));
		
		businessPool.getBusinessScheduler().stopScheduler();
		// Step_07 Mock请求：B进入随机聊天
		mockApp2.clearLastReceivedCommand();
		mockApp2.chooseBusiness(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));
		
		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getDeviceCount());
		randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_12 调用：RandomBusinessScheduler::schedule
		connection2.disableConnection();
		mockApp1.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		
		// Step_13 Mock事件：B的通信被禁用掉后，抛出IOException
		// Step_14 调用：BusinessSessionPool::getCount
		
		// 接收SessionBound
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBound));
		//assertEquals(sessionCount-1, sessionPool.getElementCount());
		
		// 接收OthersideLost
		mockApp1.clearLastReceivedCommand();
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.OthersideLost));
		
		//assertEquals(sessionCount, sessionPool.getElementCount());
		//sessionCount = sessionPool.getElementCount();
		
		// Step_15 调用：A DeviceWrapper::getBusinessStatus
		assertTrue(OnlineDevicePool.instance.getDevice(demoDeviceSn) != null);
		assertTrue(businessPool.getDevice(demoDeviceSn) != null);
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		assertTrue(deviceWrapper1.getOwnerBusinessSession() == null);
		
		// Step_16 调用：B DeviceWrapper::getBusinessStatus
		assertTrue(OnlineDevicePool.instance.getDevice(demoDeviceSn2) == null);
		assertTrue(businessPool.getDevice(demoDeviceSn2) == null);
		
		// Step_17 调用：BusinessSession::getStatus
		
				
		// Step_20 调用：A DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_21 调用：B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_22 Mock事件：A同意聊天
		//mockApp1.chatConfirm(true);
		
		// Step_23 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_24 Mock事件：B onPing
		//mockApp2.ping();
		
		// Step_25 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getDeviceCount());
		
		// Step_26 调用：BusinessSessionPool::getCount
		
		// Step_27 调用：A DeviceWrapper::getBusinessStatus
		*/
	}
}
