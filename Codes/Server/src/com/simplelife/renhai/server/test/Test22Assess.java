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

import junit.framework.TestCase;

/**
 * 
 */
public class Test22Assess extends TestCase
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
		// Step_01 Mock请求：A进入随机聊天
		// Step_02 Mock请求：B进入随机聊天
		// Step_03 Mock请求：A更新B的印象卡片
		// Step_04 调用：RandomBusinessScheduler::schedule
		// Step_05 Mock事件：A同意聊天
		// Step_06 Mock事件：B同意聊天
		// Step_07 调用：BusinessSession::getStatus
		// Step_08 Mock请求：A更新B的印象卡片
		// Step_09 Mock事件：A结束通话
		// Step_10 调用：BusinessSession::getStatus
		// Step_11 Mock事件：A对A评价
		// Step_12 Mock事件：A对B评价
		// Step_13 Mock事件：B对A评价
		// Step_14 数据库检查：A 印象卡片信息
		// Step_15 数据库检查：B 印象卡片信息
		// Step_16 调用：BusinessSession::getStatus
		// Step_17 Mock请求：A查询印象卡片，全部标签
		// Step_18 Mock请求：B查询印象卡片，前2个标签
	}
}
