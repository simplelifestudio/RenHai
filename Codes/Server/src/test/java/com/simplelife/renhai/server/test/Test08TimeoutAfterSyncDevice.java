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

/**
 * 
 */
public class Test08TimeoutAfterSyncDevice extends AbstractTestCase
{
	private MockApp mockApp;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		/*
		super.setUp();
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp = createNewMockApp(demoDeviceSn);
		*/
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		/*
		deleteDevice(mockApp);
		super.tearDown();
		*/
	}
	
	@Test
	public void test() throws InterruptedException
	{
		/*
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = pool.getDevice(mockApp.getConnectionId());
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = pool.getDeviceCount();
		
		// Step_02 Mock�����豸ͬ��
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_03 ���ã�DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper.getBusinessStatus());
		
		// Step_04 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getDeviceCount());
		
		mockApp.stopTimer();
		logger.debug("Stop ping timer and wait for timeout");
		// Step_05 �ȴ�Server��Websocketͨ���쳣ʱ��
		Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 2);
		
		logger.debug("Recovered from sleep");
		// Step_06 Mock�¼���onPing
		//mockApp.ping();
		
		assertTrue(pool.getDevice(mockApp.getDeviceSn()) == null );
		// Step_07 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount-1, pool.getDeviceCount());
		*/
	}
}
