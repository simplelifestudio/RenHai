/**
 * Test19MatchConfirmAReject.java
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
public class Test19MatchConfirmAReject extends AbstractTestCase
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
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getDeviceCount();
		
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
		
		Thread.sleep(500);
		// Step_12 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getDeviceCount());
		sessionCount = sessionPool.getDeviceCount();
		
		// Step_14 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 调用：BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_17 Mock事件：A确认绑定
		mockApp2.clearLastReceivedCommand();
		mockApp1.chatConfirm(true);
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		mockApp2.waitMessage();
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_18 Mock事件：B确认绑定
		mockApp2.clearLastReceivedCommand();
		mockApp2.chatConfirm(false);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.RejectChat));
		
		Thread.sleep(500);
		
		// Step_16 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_17 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_18 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_19 Mock事件：B onPing
		//mockApp2.ping();
		
		// Step_20 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount, onlinePool.getDeviceCount());
		
		// Step_21 调用：BusinessSessionPool::getCount
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		assertEquals(sessionCount + 1, sessionPool.getDeviceCount());
		*/
	}
}
