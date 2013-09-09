/**
 * Test03TimeoutBeforeSyncDevice.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test03TimeoutBeforeSyncDevice extends AbstractTestCase
{
	private LocalMockApp mockApp;
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp);
	}
	
	@Test
	public void test()
	{
		// Step_01 创建MockWebSocketConnection对象
		MockWebSocketConnection conn = new MockWebSocketConnection("1111");
		
		// Step_02 调用：OnlineDevicePool::newDevice
		mockApp = createMockApp();
		
		// Step_02 等待Server的Websocket通信异常时间
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 1000 + 1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_03 Mock事件：onPing
		mockApp.ping();
		Date lastPingTime = mockApp.getDeviceWrapper().getLastPingTime();
		
		mockApp.ping();
		assertTrue(lastPingTime.getTime() < mockApp.getDeviceWrapper().getLastPingTime().getTime());
		
		// Step_04 建立Websocket连接
		
	}
}
