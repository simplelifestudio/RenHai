/**
 * Test07MaxOnlineDevicePool.java
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
public class Test07MaxOnlineDevicePool extends TestCase
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
		// 前置条件 设备A、B已建立WebSocket连接（参考TC_01）
		// Step_01 调用：OnlineDevicePool::getCount
		// Step_02 调用：OnlineDevicePool::setCapacity
		// Step_03 调用：DeviceWrapper::getServiceStatus
		// Step_04 Mock请求：A设备同步
		// Step_05 调用：A DeviceWrapper::getServiceStatus
		// Step_06 调用：OnlineDevicePool::getCount
		// Step_07 Mock请求：B设备同步
		// Step_08 调用：OnlineDevicePool::getCount

	}
}
