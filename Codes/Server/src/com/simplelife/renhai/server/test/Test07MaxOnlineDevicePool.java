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

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test07MaxOnlineDevicePool extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
		OnlineDevicePool.instance.setCapacity(GlobalSetting.BusinessSetting.OnlinePoolCapacity);
	}
	
	
	@Test
	public void test() throws InterruptedException
	{
		
		OnlineDevicePool pool = OnlineDevicePool.instance;
		
		// Step_01 调用：OnlineDevicePool::getCount
		
		// Step_02 调用：OnlineDevicePool::setCapacity
		mockApp1 = createNewMockApp(demoDeviceSn);
		int deviceCount;
		synchronized(pool)
		{
			deviceCount = pool.getElementCount();
			pool.setCapacity(deviceCount);
		}
		
		mockApp2 = createNewMockApp(demoDeviceSn2);
		
		assertTrue(mockApp1 != null);
		assertTrue(mockApp2 == null);
		
		assertEquals(deviceCount, pool.getElementCount());
		
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		
		// Step_03 调用：DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Init, deviceWrapper1.getBusinessStatus());
		
		// Step_04 Mock请求：A设备同步
		long lastActivity = deviceWrapper1.getLastActivityTime().getTime();
		
		Thread.sleep(200);
		
		mockApp1.syncDevice();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_05 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		assertTrue(deviceWrapper1.getLastActivityTime().getTime() > lastActivity);
		
		// Step_06 调用：OnlineDevicePool::getCount
		assertTrue(pool.getDevice(deviceWrapper1.getDeviceSn()) != null);
		
		// Step_07 Mock请求：B设备同步
		
		
		// Step_08 调用：OnlineDevicePool::getCount

	}
}
