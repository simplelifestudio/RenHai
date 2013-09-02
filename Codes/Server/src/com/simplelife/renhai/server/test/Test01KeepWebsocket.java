/**
 * Test01KeepWebsocket.java
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

import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.IDeviceWrapper;

import junit.framework.TestCase;


/**
 * 
 */
public class Test01KeepWebsocket extends TestCase
{
	private IDeviceWrapper mockDevice;
	
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@After
	public void tearDown() throws Exception
	{
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		pool.releaseDevice(mockDevice);
	}
	
	@Test
	public void test()
	{
		// Step_01 创建MockWebSocketConnection对象
		MockWebSocketConnection conn = new MockWebSocketConnection();
		
		// Step_02 调用：OnlineDevicePool::getCount
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		int deviceCount = pool.getElementCount();
		
		// Step_03 调用：OnlineDevicePool::newDevice
		mockDevice = pool.newDevice(conn);
		
		// Step_04 调用：OnlineDevicePool::getCount
		assertEquals(pool.getElementCount(), deviceCount + 1);
		
		// Step_05 Mock事件：onPing
		conn.onPing();
		
		// Step_06 调用：DeviceWrapper::getLastPingTime()
		Date lastPingTime = mockDevice.getLastPingTime();
		
		// Step_07 Mock事件：onPing
		conn.onPing();
		
		// Step_08 调用设备的getLastPingTime()
		assertTrue(lastPingTime.getTime() < mockDevice.getLastPingTime().getTime());
	}
}
