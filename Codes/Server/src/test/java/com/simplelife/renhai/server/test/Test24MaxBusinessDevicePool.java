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

/**
 * 
 */
public class Test24MaxBusinessDevicePool extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		/*
		super.setUp();
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp(demoDeviceSn);
		mockApp2 = createNewMockApp(demoDeviceSn2);
		*/
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		/*
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		businessPool.setCapacity(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity);
		super.tearDown();
		*/
	}
	
	@Test
	public void test() throws InterruptedException
	{
		/*
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		
		mockApp1.syncDevice();
		mockApp2.syncDevice();
		
		// Step_05 调用：RandomBusinessDevicePool::setCapacity(1)
		businessPool.setCapacity(businessPool.getDeviceCount() + 1);
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.chooseBusiness(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));

		// Step_07 Mock请求：B进入随机聊天
		mockApp2.clearLastReceivedCommand();
		mockApp2.chooseBusiness(businessType);
		assertTrue(mockApp2.lastReceivedCommandIsError());
		*/
	}
}
