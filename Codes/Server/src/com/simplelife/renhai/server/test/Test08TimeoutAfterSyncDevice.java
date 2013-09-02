/**
 * Test08TimeoutAfterSyncDevice.java
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
public class Test08TimeoutAfterSyncDevice extends TestCase
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
		// 前置条件 设备已建立WebSocket连接（参考TC_01）
		// Step_01 调用：OnlineDevicePool::getCount
		// Step_02 Mock请求：设备同步
		// Step_03 调用：DeviceWrapper::getServiceStatus
		// Step_04 调用：OnlineDevicePool::getCount
		// Step_05 等待Server的Websocket通信异常时间
		// Step_06 Mock事件：onPing
		// Step_07 调用：OnlineDevicePool::getCount
	}
}
