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
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		GlobalSetting.TimeOut.OnlineDeviceConnection = 5 * 1000;
	}
	
	@After
	public void tearDown() throws Exception
	{
		GlobalSetting.TimeOut.OnlineDeviceConnection = 30 * 1000;
		deleteDevice(mockApp);
	}
	
	@Test
	public void test()
	{
		// Step_01 创建MockWebSocketConnection对象
		
		// Step_02 调用：OnlineDevicePool::newDevice
		mockApp = createNewMockApp();
		String connectionId = mockApp.getConnectionId();
		
		//String deviceSn = mockApp.getDeviceWrapper().getDeviceSn();
		IDeviceWrapper device = OnlineDevicePool.instance.getDevice(connectionId); 
		assertTrue(device != null);
		assertTrue(device.getOwnerOnlineDevicePool() == OnlineDevicePool.instance);
		
		// Step_02 等待Server的Websocket通信异常时间
		mockApp.stopTimer();
		try
		{
			logger.debug("Wait for timeout, be patient...\n");
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 2);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_03 Mock事件：onPing
		device = OnlineDevicePool.instance.getDevice(connectionId); 
		assertTrue(device == null);
		//assertTrue(device.getOwnerOnlineDevicePool() == null);
		
		//assertTrue(OnlineDevicePool.instance.getDevice(deviceSn) == null);
		// Step_04 建立Websocket连接
		
	}
}
