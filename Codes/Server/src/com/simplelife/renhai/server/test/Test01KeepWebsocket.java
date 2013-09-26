/**
 * Test01KeepWebsocket.java
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

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;


/**
 * 
 */
public class Test01KeepWebsocket extends AbstractTestCase
{
	private LocalMockApp mockApp;
	
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
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
		MockWebSocketConnection conn = new MockWebSocketConnection();
		
		// Step_02 调用：OnlineDevicePool::getCount
		OnlineDevicePool pool = OnlineDevicePool.instance;
		int deviceCount = pool.getElementCount();
		
		// Step_03 调用：OnlineDevicePool::newDevice
		mockApp = createNewMockApp();
		
		// Step_04 调用：OnlineDevicePool::getCount
		assertEquals(pool.getElementCount(), deviceCount + 1);
		
		// Step_05 Mock事件：onPing
		mockApp.ping();
		
		// Step_06 调用：DeviceWrapper::getLastPingTime()
		long lastPingTime = mockApp.getDeviceWrapper().getLastPingTime().getTime();
		
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Step_07 Mock事件：onPing
		mockApp.ping();
		
		// Step_08 调用设备的getLastPingTime()
		assertTrue(lastPingTime < mockApp.getDeviceWrapper().getLastPingTime().getTime());
	}
}
