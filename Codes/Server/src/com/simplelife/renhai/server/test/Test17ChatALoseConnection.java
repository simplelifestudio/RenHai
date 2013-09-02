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

import junit.framework.TestCase;

/**
 * 
 */
public class Test17ChatALoseConnection extends TestCase
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
		// Step_01 调用：OnlineDevicePool::getCount
		// Step_02 调用：RandomBusinessDivicePool::getCount
		// Step_03 调用：BusinessSessionPool::getCount
		// Step_04 调用：A DeviceWrapper::getServiceStatus
		// Step_05 调用：B DeviceWrapper::getServiceStatus
		// Step_06 Mock请求：A进入随机聊天
		// Step_07 Mock请求：B进入随机聊天
		// Step_08 调用：A DeviceWrapper::getServiceStatus
		// Step_09 调用：B DeviceWrapper::getServiceStatus
		// Step_10 调用：BusinessSession::getStatus
		// Step_11 调用：RandomBusinessDivicePool::getCount
		// Step_12 调用：RandomBusinessScheduler::schedule
		// Step_13 调用：BusinessSessionPool::getCount
		// Step_14 调用：A DeviceWrapper::getServiceStatus
		// Step_15 调用：B DeviceWrapper::getServiceStatus
		// Step_16 调用：BusinessSession::getStatus
		// Step_17 Mock事件：A确认绑定
		// Step_18 Mock事件：B确认绑定
		// Step_19 调用：BusinessSession::getStatus
		// Step_20 调用：A DeviceWrapper::getServiceStatus
		// Step_21 调用：B DeviceWrapper::getServiceStatus
		// Step_22 Mock事件：A同意聊天
		// Step_23 Mock事件：B同意聊天
		// Step_24 调用：BusinessSession::getStatus
		// Step_25 Mock事件：A onClose
		// Step_26 调用：BusinessSession::getStatus
		// Step_27 调用：OnlineDevicePool::getCount
		// Step_28 调用：BusinessSessionPool::getCount
		// Step_29 调用：B DeviceWrapper::getServiceStatus
		// Step_30 Mock事件：A onPing
		// Step_31 Mock事件：B onPing
		// Step_32 Mock事件：B对A评价
		// Step_33 数据库检查：A 印象卡片信息
		// Step_34 数据库检查：B 印象卡片信息
		// Step_35 调用：BusinessSession::getStatus
		// Step_36 调用：BusinessSessionPool::getCount
		// Step_37 调用：B DeviceWrapper::getServiceStatus
	}
}
