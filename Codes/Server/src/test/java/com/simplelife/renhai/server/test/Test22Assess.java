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
		
		// Step_01 Mock请求：A进入随机聊天
		mockApp1.chooseBusiness(businessType);
		
		// Step_02 Mock请求：B进入随机聊天
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.chooseBusiness(businessType);
		
		// Step_03 Mock请求：A更新B的印象卡片
		mockApp1.assessAndContinue("帅哥");
		assertTrue(mockApp1.lastReceivedCommandIsError());
		//fail("检验server的回复");
		
		// Step_04 调用：RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		mockApp1.waitMessage();
		mockApp2.waitMessage();
		
		Thread.sleep(500);
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_05 Mock事件：A同意聊天
		mockApp1.chatConfirm(true);
		mockApp1.waitMessage();
		
		// Step_06 Mock事件：B同意聊天
		mockApp2.chatConfirm(true);
		mockApp2.waitMessage();
		// Step_07 调用：BusinessSession::getStatus
		
		
		// Step_08 Mock请求：A更新B的印象卡片
		mockApp1.assessAndContinue("^#SoSo#^,帅哥");
		assertTrue(mockApp1.lastReceivedCommandIsError());
		
		// Step_09 Mock事件：A结束通话
		mockApp1.endChat();
		
		// Step_10 调用：BusinessSession::getStatus
		Thread.sleep(500);
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_11 Mock事件：A对A评价
		//mockApp1.assess("帅哥");
		
		// Step_12 Mock事件：A对B评价
		mockApp1.assessAndContinue("^#Disgusting#^,TC22_评价1");
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndContinue));
		
		// Step_13 Mock事件：B对A评价
		mockApp2.assessAndContinue("^#Happy#^,美女,气质,TC22_评价2");
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndContinue));
		
		// Step_14 数据库检查：A 印象卡片信息
		//fail("检查数据库中的印象卡片");
		
		// Step_15 数据库检查：B 印象卡片信息
		//fail("检查数据库中的印象卡片");
		
		// Step_16 调用：BusinessSession::getStatus
		//Thread.sleep(500);
		//assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock请求：A查询印象卡片，全部标签
		//fail("A查询印象卡片");
		
		// Step_18 Mock请求：B查询印象卡片，前2个标签
		//fail("B查询印象卡片");
		*/
	}
}
