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


/**
 * 
 */
public class Test01KeepWebsocket extends AbstractTestCase
{
	private MockApp mockApp;
	
	@Before
	public void setUp() throws Exception
	{
		//super.setUp();
		//System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
	}
	
	@After
	public void tearDown() throws Exception
	{
		//deleteDevice(mockApp);
		//super.tearDown();
	}
	
	@Test
	public void test() throws InterruptedException
	{
		/*
		//new MockWebSocketConnection();
		
		// Step_02 ���ã�OnlineDevicePool::getCount
		OnlineDevicePool pool = OnlineDevicePool.instance;
		int deviceCount = pool.getDeviceCount();
		
		// Step_03 ���ã�OnlineDevicePool::newDevice
		mockApp = createNewMockApp(demoDeviceSn);
		
		// Step_04 ���ã�OnlineDevicePool::getCount
		int newCount = pool.getDeviceCount(); 
		assertEquals(newCount, deviceCount + 1);
		
		// Step_05 Mock�¼���onPing
		mockApp.ping();
		
		// Step_06 ���ã�DeviceWrapper::getLastPingTime()
		IDeviceWrapper device = OnlineDevicePool.instance.getDevice(mockApp.getConnectionId());
		long lastPingTime = device.getLastPingTime();
		
		Thread.sleep(100);
		
		// Step_07 Mock�¼���onPing
		mockApp.ping();
		
		// Step_08 �����豸��getLastPingTime()
		assertTrue(lastPingTime < device.getLastPingTime());
		*/
	}
}
