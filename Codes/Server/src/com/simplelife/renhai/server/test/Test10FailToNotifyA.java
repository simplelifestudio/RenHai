/**
 * Test10FailToNotifyA.java
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
public class Test10FailToNotifyA extends AbstractTestCase
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
	}
	
	@Test
	public void test()
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random); 
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;  
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		IDeviceWrapper deviceWrapper2 = mockApp2.getDeviceWrapper();
		
		String deviceSn1 = deviceWrapper1.getDeviceSn();
		String deviceSn2 = deviceWrapper2.getDeviceSn();
		
		assertTrue(deviceWrapper1.getConnection() instanceof MockWebSocketConnection);
		MockWebSocketConnection connection1 = (MockWebSocketConnection) deviceWrapper1.getConnection();
		
		mockApp1.syncDevice();
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.syncDevice();
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		logger.debug("Original device count of random pool: {}", businessPool.getElementCount());
		
		// Step_03 调用：BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock请求：A进入随机聊天
		assertTrue(businessPool.getDevice(deviceSn1) == null);
		
		mockApp1.enterPool(Consts.BusinessType.Random);
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		assertTrue(businessPool.getDevice(deviceSn1) != null);
		
		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_11 调用：MockWebSocketConnection::disableConnection，禁用A的通信功能
		connection1.disableConnection();
		
		// Step_07 Mock请求：B进入随机聊天
		assertTrue(businessPool.getDevice(deviceSn2) == null);
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.enterPool(Consts.BusinessType.Random);
		assertTrue(businessPool.getDevice(deviceSn2) != null);
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_12 调用：RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_13 调用：BusinessSessionPool::getCount
		if (businessPool.getDeviceCountInChat() > 0)
		{
			// Maybe devices have not been scheduled yet
			assertEquals(sessionCount-1, sessionPool.getElementCount());
		}
		else
		{
			// Status of session was changed to idle and returned to pool
			// Due to communication error with mockApp1
			assertEquals(sessionCount, sessionPool.getElementCount());
		}
		
		// Step_14 Mock事件：A的通信被禁用掉后，抛出IOException
		// Step_15 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_16 Mock事件：B onPing
		//mockApp2.ping();
		
		// Step_17 调用：OnlineDevicePool::getCount
		try
		{
			Thread.sleep(1000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (deviceWrapper1.getOwnerOnlineDevicePool() == null)
		{
			assertTrue(onlinePool.getDevice(deviceSn1) == null);
			
			// Step_18 调用：BusinessSessionPool::getCount
			assertEquals(sessionCount, sessionPool.getElementCount());
			
			// Step_19 调用：B DeviceWrapper::getBusinessStatus
			assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		}
		else
		{
			// Step_19 调用：B DeviceWrapper::getBusinessStatus
			assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		}
		
	}
}
