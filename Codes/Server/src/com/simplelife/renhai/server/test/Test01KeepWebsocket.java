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
		// Step_01 ����MockWebSocketConnection����
		MockWebSocketConnection conn = new MockWebSocketConnection();
		
		// Step_02 ���ã�OnlineDevicePool::getCount
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		int deviceCount = pool.getElementCount();
		
		// Step_03 ���ã�OnlineDevicePool::newDevice
		mockDevice = pool.newDevice(conn);
		
		// Step_04 ���ã�OnlineDevicePool::getCount
		assertEquals(pool.getElementCount(), deviceCount + 1);
		
		// Step_05 Mock�¼���onPing
		conn.onPing();
		
		// Step_06 ���ã�DeviceWrapper::getLastPingTime()
		Date lastPingTime = mockDevice.getLastPingTime();
		
		// Step_07 Mock�¼���onPing
		conn.onPing();
		
		// Step_08 �����豸��getLastPingTime()
		assertTrue(lastPingTime.getTime() < mockDevice.getLastPingTime().getTime());
	}
}
