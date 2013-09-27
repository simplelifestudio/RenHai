/**
 * Test09TimeoutWaitForMatch.java
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

import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;

/**
 * 
 */
public class Test09TimeoutWaitForMatch extends AbstractTestCase
{
	private LocalMockApp mockApp;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp = createNewMockApp(demoDeviceSn);
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
		
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		String deviceSn = mockApp.getDeviceWrapper().getDeviceSn();
		AbstractBusinessDevicePool randomPool = pool.getBusinessPool(Consts.BusinessType.Random);
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		
		// Step_02 ���ã�RandomBusinessDevicePool::getCount
		//int randomDeviceCount = pool.getBusinessPool(Consts.BusinessType.Random).getElementCount();
		
		// Step_03 Mock���󣺽����������
		assertTrue(randomPool.getDevice(deviceSn) == null);
		mockApp.enterPool(Consts.BusinessType.Random);
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_04 ���ã�RandomBusinessDevicePool::getCount
		//assertEquals(randomDeviceCount + 1.getElementCount());
		assertTrue(randomPool.getDevice(deviceSn) != null);
		
		// Step_05 �ȴ�Server��Websocketͨ���쳣ʱ��
		mockApp.stopTimer();
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 2);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_06 Mock�¼���onPing
		//mockApp.ping();
		
		// Step_07 ���ã�OnlineDevicePool::getCount
		assertTrue(mockApp.getDeviceWrapper().getOwnerOnlineDevicePool() == null);
		assertTrue(pool.getDevice(deviceSn) == null);
		assertTrue(randomPool.getDevice(deviceSn) == null);
		
		// Step_08 ���ã�RandomBusinessDevicePool::getCount
		assertTrue(randomPool.getDevice(deviceSn) == null);
	}
}
