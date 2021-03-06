/**
 * Test03TimeoutBeforeSyncDevice.java
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
public class Test03TimeoutBeforeSyncDevice extends AbstractTestCase
{
	private MockApp mockApp;
	@Before
	public void setUp() throws Exception
	{
		/*
		super.setUp();
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		GlobalSetting.TimeOut.OnlineDeviceConnection = 5 * 1000;
		*/
	}
	
	@After
	public void tearDown() throws Exception
	{
		/*
		GlobalSetting.TimeOut.OnlineDeviceConnection = 30 * 1000;
		deleteDevice(mockApp);
		super.tearDown();
		*/
	}
	
	@Test
	public void test() throws InterruptedException
	{
		/*
		// Step_01 创建MockWebSocketConnection对象
		
		// Step_02 调用：OnlineDevicePool::newDevice
		mockApp = createNewMockApp(demoDeviceSn);
		String connectionId = mockApp.getConnectionId();
		
		//String deviceSn = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDeviceSn();
		IDeviceWrapper device = OnlineDevicePool.instance.getDevice(connectionId); 
		assertTrue(device != null);
		assertTrue(device.getOwnerOnlineDevicePool() == OnlineDevicePool.instance);
		
		// Step_02 等待Server的Websocket通信异常时间
		mockApp.stopTimer();
		logger.debug("Wait for timeout, be patient...\n");
		Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 2);
		assertTrue(OnlineDevicePool.instance.getDevice(connectionId) == null);
		*/
	}
}
