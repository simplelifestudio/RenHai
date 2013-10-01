/**
 * Test24MaxBusinessDevicePool.java
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
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.GlobalSetting;

/**
 * 
 */
public class Test24MaxBusinessDevicePool extends AbstractTestCase
{
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp(demoDeviceSn);
		mockApp2 = createNewMockApp(demoDeviceSn2);
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random);
		businessPool.setCapacity(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity);
	}
	
	@Test
	public void test()
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random);
		
		mockApp1.syncDevice();
		mockApp2.syncDevice();
		
		// Step_05 调用：RandomBusinessDevicePool::setCapacity(1)
		businessPool.setCapacity(businessPool.getElementCount() + 1);
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.enterPool(BusinessType.Random);
		
		// Step_07 Mock请求：B进入随机聊天
		mockApp2.clearLastReceivedCommand();
		mockApp2.enterPool(BusinessType.Random);
		assertTrue(mockApp2.lastReceivedCommandIsError());
	}
}
