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

import com.alibaba.fastjson.JSONObject;
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
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		mockApp1 = createNewMockApp();
		mockApp2 = createNewMockApp();
		mockApp2.getDeviceWrapper().getDevice().setDeviceSn("SNOfDeviceB");
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
	public void test()
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random);
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		IDeviceWrapper deviceWrapper2 = mockApp2.getDeviceWrapper();
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
		
		businessPool.getBusinessScheduler().stopScheduler();
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_07 Mock请求：B进入随机聊天
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 调用：RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_12 调用：BusinessSession::getStatus
		//assertEquals(deviceWrapper1.getBusinessStatus(), Consts.BusinessStatus.SessionBound);
		int count = 0;
		while (deviceWrapper1.getOwnerBusinessSession() == null && count < 5)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		assertTrue(deviceWrapper1.getOwnerBusinessSession() != null);
		
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		//assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_13 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_14 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		assertTrue(deviceWrapper1.getOwnerBusinessSession() != null);
		
		// Step_15 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		assertTrue(deviceWrapper2.getOwnerBusinessSession() != null);
		
		// Step_16 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock事件：A确认绑定
		mockApp1.sendNotificationResponse(Consts.NotificationType.SessionBinded, "", "1");
		
		// Step_18 Mock事件：B确认绑定
		mockApp2.sendNotificationResponse(Consts.NotificationType.SessionBinded, "", "1");
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_19 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_20 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_21 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_22 Mock事件：A同意聊天
		mockApp1.chatConfirm(true);
		mockApp2.sendNotificationResponse(Consts.NotificationType.OthersideAgreed, "", "1");
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_23 Mock事件：B同意聊天
		mockApp2.chatConfirm(true);
		
		// Step_24 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_25 Mock事件：A结束通话
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		mockApp1.endChat();
		
		// Step_26 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_27 Mock事件：A onClose
		mockApp1.close();
		
		// Step_28 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getElementCount());
		deviceCount = onlinePool.getElementCount();
		
		// Step_29 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount, sessionPool.getElementCount());
		
		// Step_30 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_31 Mock事件：A onPing
		mockApp1.ping();
		
		// Step_32 Mock事件：B onPing
		mockApp2.ping();
		
		// Step_33 Mock事件：B对A评价，且之后退出业务
		JSONObject obj = mockApp1.getDeviceWrapper().toJSONObject();
		mockApp2.assessAndQuit(obj.toJSONString());
		
		// Step_34 数据库检查：A 印象卡片信息
		// Step_35 数据库检查：B 印象卡片信息
		//fail("需要检查数据库中的印象卡片信息");
		// TODO "需要检查数据库中的印象卡片信息"
		
		// Step_36 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_37 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_38 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_39 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount, onlinePool.getElementCount());
	}
}
