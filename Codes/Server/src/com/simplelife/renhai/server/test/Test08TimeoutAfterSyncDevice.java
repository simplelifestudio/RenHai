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

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test08TimeoutAfterSyncDevice extends AbstractTestCase
{
	private LocalMockApp mockApp;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp = createNewMockApp();
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp);
	}
	
	@Test
	public void test()
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = mockApp.getDeviceWrapper();
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		
		// Step_02 Mock请求：设备同步
		mockApp.syncDevice();
		
		// Step_03 调用：DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper.getBusinessStatus());
		
		// Step_04 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getElementCount());
		
		mockApp.stopTimer();
		logger.debug("Stop ping timer and wait for timeout");
		// Step_05 等待Server的Websocket通信异常时间
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 2);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		logger.debug("Recovered from sleep");
		// Step_06 Mock事件：onPing
		//mockApp.ping();
		
		// Step_07 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount-1, pool.getElementCount());
	}
}
