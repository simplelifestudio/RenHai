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
		mockApp = createMockApp();
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
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		
		// Step_02 Mock�����豸ͬ��
		syncDevice(mockApp);
		
		// Step_03 ���ã�DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper.getBusinessStatus());
		
		// Step_04 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getElementCount());
		
		// Step_05 �ȴ�Server��Websocketͨ���쳣ʱ��
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 1000 + 1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_06 Mock�¼���onPing
		mockApp.ping();
		
		// Step_07 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount-1, pool.getElementCount());
	}
}
