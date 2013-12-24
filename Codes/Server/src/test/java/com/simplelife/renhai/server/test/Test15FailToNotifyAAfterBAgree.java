/**
 * Test15FailToNotifyAAfterBAgree.java
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
import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/**
 * 
 */
public class Test15FailToNotifyAAfterBAgree extends AbstractTestCase
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
		
		onlinePool.getDeviceCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_03 调用：BusinessSessionPool::getCount
		int sessionCount = sessionPool.getDeviceCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.chooseBusiness(businessType);
		
		// Step_07 Mock请求：B进入随机聊天
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.chooseBusiness(businessType);
		
		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getDeviceCount());
		randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_11 调用：RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBound));
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBound));
		
		// Step_12 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getDeviceCount());
		sessionCount = sessionPool.getDeviceCount();
		
		// Step_13 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_14 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 调用：BusinessSession::getStatus
		
		
		// Step_16 Mock事件：A确认绑定
		//mockApp1.sendNotificationResponse(Consts.NotificationType.SessionBound, "", "1");
		
		// Step_17 Mock事件：B确认绑定
		//mockApp2.sendNotificationResponse(Consts.NotificationType.SessionBound, "", "1");
		
		// Step_18 调用：BusinessSession::getStatus
		// 等待Server处理完A和B的绑定确认
		Thread.sleep(500);
		
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		logger.debug("Status of session:{}", session.getStatus().name());
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_19 调用：A DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_20 调用：B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_21 Mock事件：A同意聊天
		mockApp1.clearLastReceivedCommand();
		mockApp2.clearLastReceivedCommand();
		mockApp1.chatConfirm(true);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));
		
		// 等待Server通知A同意聊天
		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.OthersideAgreed));
		
		// Step_23 调用：MockWebSocketConnection::disableConnection，禁用A的通信功能
		MockWebSocketConnection socket1 = getMockWebSocket(deviceWrapper1);
		socket1.disableConnection();
				
		// Step_22 Mock事件：B同意聊天
		mockApp2.clearLastReceivedCommand();
		mockApp2.chatConfirm(true);
		
		// 等待B同意聊天的响应
		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));
		
		// 等待通知来自A的OthersideLost
		mockApp2.clearLastReceivedCommand();
		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.OthersideLost));
		
		// Step_24 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_25 Mock事件：B onPing
		//mockApp2.ping();
		
		
		// Step_26 调用：OnlineDevicePool::getCount
		//assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_27 调用：BusinessSessionPool::getCount
		//assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_28 调用：B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.MatchCache, deviceWrapper2.getBusinessStatus());
		*/
	}
}
