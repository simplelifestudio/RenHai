/**
 * Test17ChatALoseConnection.java
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
public class Test17ChatALoseConnection extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
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
		mockApp2.syncDevice();
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		
		// Step_03 调用：BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.enterPool(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));

		// Step_07 Mock请求：B进入随机聊天
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.enterPool(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));

		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_11 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_12 调用：RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBinded));

		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBinded));
		
		// Step_13 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_14 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 调用：BusinessSession::getStatus
		// 等待Server处理完A和B的绑定确认
		Thread.sleep(500);
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
				
		// Step_17 Mock事件：A确认绑定
		mockApp2.clearLastReceivedCommand();
		mockApp1.chatConfirm(true);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));

		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.OthersideAgreed));
		
		// Step_18 Mock事件：B确认绑定
		mockApp1.clearLastReceivedCommand();
		mockApp2.chatConfirm(true);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_25 Mock事件：A onClose
		Thread.sleep(1000);
		mockApp1.disconnect();
		
		// Step_26 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_27 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_28 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount, sessionPool.getElementCount());
		
		// Step_29 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_30 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_31 Mock事件：B onPing
		//mockApp2.ping();
		
		mockApp2.endChat();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EndChat));
		
		// Step_32 Mock事件：B对A评价
		mockApp2.assessAndContinue("TC17_评价,^#Happy#^,美女");
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndContinue));
		
		// Step_33 数据库检查：A 印象卡片信息
		//fail("需要检查数据库中的印象卡片信息");
		
		// Step_34 数据库检查：B 印象卡片信息
		//fail("需要检查数据库中的印象卡片信息");
		
		// Step_35 调用：BusinessSession::getStatus
		// 等待server释放会话
		Thread.sleep(500);
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_36 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_37 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
	}
}
