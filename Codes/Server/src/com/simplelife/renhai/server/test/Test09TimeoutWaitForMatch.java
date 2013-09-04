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

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.JSONKey;

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
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		syncDevice(mockApp);
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = pool.getBusinessPool(Consts.BusinessType.Random).getElementCount();
		
		// Step_03 Mock请求：进入随机聊天
		mockApp.enterPool(JSONKey.BusinessType.Random);
		
		// Step_04 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 1, pool.getBusinessPool(Consts.BusinessType.Random).getElementCount());
		
		// Step_05 等待Server的Websocket通信异常时间
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 1000 + 1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_06 Mock事件：onPing
		mockApp.ping();
		
		// Step_07 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount-1, pool.getElementCount());
		
		// Step_08 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount, pool.getBusinessPool(Consts.BusinessType.Random).getElementCount());
	}
}
