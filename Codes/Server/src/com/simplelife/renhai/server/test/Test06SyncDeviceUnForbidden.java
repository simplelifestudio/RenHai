/**
 * Test06SyncDeviceUnForbidden.java
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
public class Test06SyncDeviceUnForbidden extends TestCase
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
		// 前置条件 设备A已建立WebSocket连接（参考TC_01）
		// Step_01 数据库操作：将设备A的服务状态更新为禁聊，到期日期为昨天
		// Step_02 调用：OnlineDevicePool::getCount
		// Step_03 调用：DeviceWrapper::getServiceStatus
		// Step_04 Mock请求：设备同步
		// Step_05 调用：A DeviceWrapper::getServiceStatus
		// Step_06 调用：OnlineDevicePool::getCount
		// Step_06 调用：DeviceWrapper::getLastActivityTime
	}
}
