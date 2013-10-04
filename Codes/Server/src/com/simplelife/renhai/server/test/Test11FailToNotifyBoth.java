/**
 * Test11FailToNotifyBoth.java
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
import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test11FailToNotifyBoth extends AbstractTestCase
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
		logger.debug("Enter tearDown()");
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
	}
	

	@Test
	public void test()
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random); 
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;  
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		IDeviceWrapper deviceWrapper2 = mockApp2.getDeviceWrapper();
		
		MockWebSocketConnection connection1 = getMockWebSocket(deviceWrapper1);
		MockWebSocketConnection connection2 = getMockWebSocket(deviceWrapper2);
		mockApp1.syncDevice();
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.syncDevice();
		assertTrue(!mockApp2.lastReceivedCommandIsError());

		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		
		// Step_03 调用：BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock请求：A进入随机聊天
		mockApp1.enterPool(businessType);
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		businessPool.getBusinessScheduler().stopScheduler();
		
		// Step_07 Mock请求：B进入随机聊天
		mockApp2.enterPool(businessType);
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 调用：MockWebSocketConnection::disableConnection，禁用A的通信功能
		connection1.disableConnection();
		
		// Step_12 调用：MockWebSocketConnection::disableConnection，禁用B的通信功能
		connection2.disableConnection();
		
		// Step_13 调用：RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		logger.debug("Recover from sleep");
		
		// Step_14 调用：BusinessSessionPool::getCount
		assertTrue(onlinePool.getDevice(demoDeviceSn) == null);
		assertTrue(onlinePool.getDevice(demoDeviceSn2) == null);
		
		assertTrue(businessPool.getDevice(demoDeviceSn) == null);
		assertTrue(businessPool.getDevice(demoDeviceSn2) == null);
		
		assertTrue(deviceWrapper1.getOwnerBusinessSession() == null);
		assertTrue(deviceWrapper1.getOwnerBusinessSession() == null);
		
		assertEquals(sessionCount, sessionPool.getElementCount());
		
		// Step_15 Mock事件：A的通信被禁用掉后，抛出IOException
		// Step_16 Mock事件：B的通信被禁用掉后，抛出IOException
		
		// Step_17 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_18 Mock事件：B onPing
		//mockApp2.ping();
		
		// Step_19 调用：OnlineDevicePool::getCount
		//assertEquals(deviceCount - 2, onlinePool.getElementCount());
		
		// Step_20 调用：BusinessSessionPool::getCount
		//assertEquals(sessionCount, sessionPool.getElementCount());
	}
}
