/**
 * Test18AssessALoseConnection.java
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
public class Test18AssessALoseConnection extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp(demoDeviceSn);
		mockApp2 = createNewMockApp(demoDeviceSn2);
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
		super.tearDown();
	}
	
	@Test
	public void test() throws InterruptedException
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;
		IDeviceWrapper deviceWrapper1 = OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn());
		IDeviceWrapper deviceWrapper2 = OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn());
		
		mockApp1.syncDevice();
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.syncDevice();
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		
		// Step_03 调用：BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		businessPool.getBusinessScheduler().stopScheduler();
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.enterPool(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_07 Mock请求：B进入随机聊天
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.enterPool(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 调用：RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		mockApp1.waitMessage();
		
		// Step_12 调用：BusinessSession::getStatus
		//assertEquals(deviceWrapper1.getBusinessStatus(), Consts.BusinessStatus.SessionBound);
		assertTrue(deviceWrapper1.getOwnerBusinessSession() != null);
		assertTrue(deviceWrapper2.getOwnerBusinessSession() != null);
		
		logger.debug("sleep 1000");
		Thread.sleep(1000);

		logger.debug("Recovered from sleep");
		
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_13 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_14 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 调用：BusinessSession::getStatus
		
		// Step_22 Mock事件：A同意聊天
		mockApp2.clearLastReceivedCommand();
		mockApp1.chatConfirm(true);
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		
		// Step_23 Mock事件：B同意聊天
		mockApp2.clearLastReceivedCommand();
		mockApp2.chatConfirm(true);
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// B确认后，所有的设备都确认了， 此时业务会话切换状态，不再通知大家B确认了
		//mockApp1.waitMessage();
		//assertTrue(mockApp1.getLastReceivedCommand() != null);
		
		// Step_24 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_25 Mock事件：A结束通话
		Thread.sleep(1000);

		mockApp2.clearLastReceivedCommand();
		mockApp1.endChat();
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		
		// Step_26 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		Thread.sleep(1000);

		// Step_27 Mock事件：A onClose
		mockApp2.clearLastReceivedCommand();
		mockApp1.disconnect();
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		
		// Step_28 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getElementCount());
		deviceCount = onlinePool.getElementCount();
		
		// Step_29 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount, sessionPool.getElementCount());
		
		// Step_30 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_31 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_32 Mock事件：B onPing
		//mockApp2.ping();
		
		// Step_33 Mock事件：B对A评价，且之后退出业务
		mockApp2.assessAndQuit("^#Happy#^,帅哥");
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// Step_34 数据库检查：A 印象卡片信息
		// Step_35 数据库检查：B 印象卡片信息
		//fail("需要检查数据库中的印象卡片信息");
		// TODO "需要检查数据库中的印象卡片信息"
		
		// Step_36 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_37 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_38 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		// Step_39 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount, onlinePool.getElementCount());
	}
}
