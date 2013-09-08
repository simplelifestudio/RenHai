/**
 * Test23NormalProcessAndStatistics.java
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
public class Test23NormalProcessAndStatistics extends AbstractTestCase
{
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		mockApp1 = createMockApp();
		mockApp2 = createMockApp();
		mockApp2.getDeviceWrapper().getDevice().getDevicecard().setDeviceSn("SNOfDeviceB");
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
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		
		// Step_01 Mock请求：查询所有统计项，包括：在线设备池设备数，在线设备池上限，随机业务设备池设备数，随机业务设备池上限，处于聊天状态的设备数，处于随机聊天状态的设备数，业务设备池的热门兴趣标签
		mockApp1.sendServerDataSyncRequest();
		fail("需要解释并核实各个统计项");
		
		// Step_02 Mock请求：A进入随机聊天
		syncDevice(mockApp1);
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_03 Mock请求：B进入随机聊天
		syncDevice(mockApp2);
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_04 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		fail("需要解释并核实各个统计项");
		
		// Step_05 调用：RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_06 调用：BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_07 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		fail("需要解释并核实各个统计项");
		
		// Step_08 Mock事件：A同意聊天
		mockApp1.chatConfirm(true);
		
		// Step_09 Mock事件：B同意聊天
		mockApp2.chatConfirm(true);
		
		// Step_10 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_11 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		fail("需要解释并核实各个统计项");
		
		// Step_12 Mock事件：A结束通话
		mockApp1.endChat();
		
		// Step_13 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_14 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		fail("需要解释并核实各个统计项");
		
		// Step_15 Mock事件：B结束通话
		mockApp2.endChat();
		
		// Step_16 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		fail("需要解释并核实各个统计项");
		
		// Step_17 Mock事件：A onPing
		mockApp1.ping();
		
		// Step_18 Mock事件：B onPing
		mockApp2.ping();
		
		// Step_19 Mock事件：A对B评价
		mockApp1.assess("TC23_评价");
		
		// Step_20 Mock事件：B对A评价
		mockApp2.assess("TC24_评价");
		
		// Step_21 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_22 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		fail("需要解释并核实各个统计项");
	}
}
