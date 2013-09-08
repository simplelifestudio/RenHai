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
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		mockApp1 = createMockApp();
		mockApp2 = createMockApp();
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
		
		OnlineDevicePool.instance.setCapacity(GlobalSetting.BusinessSetting.OnlinePoolCapacity);
	}
	
	
	@Test
	public void test()
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		IDeviceWrapper deviceWrapper2 = mockApp2.getDeviceWrapper();
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();

		// Step_02 ���ã�OnlineDevicePool::setCapacity
		pool.setCapacity(deviceCount + 1);
		
		// Step_03 ���ã�DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Init, deviceWrapper1.getBusinessStatus());
		assertEquals(Consts.DeviceBusinessStatus.Init, deviceWrapper2.getBusinessStatus());
		
		// Step_04 Mock����A�豸ͬ��
		long lastActivity = deviceWrapper1.getLastActivityTime().getTime();
		syncDevice(mockApp1);
		
		// Step_05 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		assertTrue(deviceWrapper1.getLastActivityTime().getTime() > lastActivity);
		
		// Step_06 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getElementCount());
		
		// Step_07 Mock����B�豸ͬ��
		lastActivity = deviceWrapper2.getLastActivityTime().getTime();
		syncDevice(mockApp2);
		
		// Step_08 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, pool.getElementCount());
		assertTrue(deviceWrapper2.getLastActivityTime().getTime() > lastActivity);
	}
}
