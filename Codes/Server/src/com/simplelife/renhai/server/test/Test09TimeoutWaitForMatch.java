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
	private MockApp mockApp;
	
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
	public void test() throws InterruptedException
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		String deviceSn = mockApp.getDeviceWrapper().getDeviceSn();
		AbstractBusinessDevicePool businessPool = pool.getBusinessPool(businessType);
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		//int randomDeviceCount = pool.getBusinessPool(Consts.BusinessType.Random).getElementCount();
		
		// Step_03 Mock请求：进入随机聊天
		assertTrue(businessPool.getDevice(deviceSn) == null);
		mockApp.enterPool(businessType);
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_04 调用：RandomBusinessDevicePool::getCount
		//assertEquals(randomDeviceCount + 1.getElementCount());
		assertTrue(businessPool.getDevice(deviceSn) != null);
		
		// Step_05 等待Server的Websocket通信异常时间
		mockApp.stopTimer();
		Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 2);
		
		// Step_06 Mock事件：onPing
		//mockApp.ping();
		
		// Step_07 调用：OnlineDevicePool::getCount
		assertTrue(mockApp.getDeviceWrapper().getOwnerOnlineDevicePool() == null);
		assertTrue(pool.getDevice(deviceSn) == null);
		assertTrue(businessPool.getDevice(deviceSn) == null);
		
		// Step_08 调用：RandomBusinessDevicePool::getCount
		assertTrue(businessPool.getDevice(deviceSn) == null);
	}
}
