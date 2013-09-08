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
		
		// Step_01 Mock请求：A进入随机聊天
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_02 Mock请求：B进入随机聊天
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_03 Mock请求：A更新B的印象卡片
		mockApp1.assess("帅哥");
		fail("检验server的回复");
		
		// Step_04 调用：RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_05 Mock事件：A同意聊天
		mockApp1.sendNotificationResponse();
		mockApp1.chatConfirm(true);
		
		// Step_06 Mock事件：B同意聊天
		mockApp2.sendNotificationResponse();
		mockApp2.chatConfirm(true);
		
		// Step_07 调用：BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_08 Mock请求：A更新B的印象卡片
		mockApp1.assess("帅哥");
		fail("检验server的回复");
		
		// Step_09 Mock事件：A结束通话
		mockApp1.endChat();
		
		// Step_10 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_11 Mock事件：A对A评价
		//mockApp1.assess("帅哥");
		
		// Step_12 Mock事件：A对B评价
		mockApp1.assess("帅哥");
		
		// Step_13 Mock事件：B对A评价
		mockApp2.assess("美女");
		
		// Step_14 数据库检查：A 印象卡片信息
		fail("检查数据库中的印象卡片");
		
		// Step_15 数据库检查：B 印象卡片信息
		fail("检查数据库中的印象卡片");
		
		// Step_16 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock请求：A查询印象卡片，全部标签
		fail("A查询印象卡片");
		
		// Step_18 Mock请求：B查询印象卡片，前2个标签
		fail("B查询印象卡片");
	}
}
