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

import junit.framework.TestCase;

/**
 * 
 */
public class Test23NormalProcessAndStatistics extends AbstractTestCase
{
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void test()
	{
		// 前置条件 设备A和设备B均已建立WebSocket连接（参考TC_01）
		// Step_01 Mock请求：查询所有统计项，包括：在线设备池设备数，在线设备池上限，随机业务设备池设备数，随机业务设备池上限，处于聊天状态的设备数，处于随机聊天状态的设备数，业务设备池的热门兴趣标签
		// Step_02 Mock请求：A进入随机聊天
		// Step_03 Mock请求：B进入随机聊天
		// Step_04 Mock请求：查询所有统计项
		// Step_05 调用：RandomBusinessScheduler::schedule
		// Step_06 调用：BusinessSession::getStatus
		// Step_07 Mock请求：查询所有统计项
		// Step_08 Mock事件：A同意聊天
		// Step_09 Mock事件：B同意聊天
		// Step_10 调用：BusinessSession::getStatus
		// Step_11 Mock请求：查询所有统计项
		// Step_12 Mock事件：A结束通话
		// Step_13 调用：BusinessSession::getStatus
		// Step_14 Mock请求：查询所有统计项
		// Step_15 Mock事件：B结束通话
		// Step_16 Mock请求：查询所有统计项
		// Step_17 Mock事件：A onPing
		// Step_18 Mock事件：B onPing
		// Step_19 Mock事件：A对B评价
		// Step_20 Mock事件：B对A评价
		// Step_21 调用：BusinessSession::getStatus
		// Step_22 Mock请求：查询所有统计项
	}
}
