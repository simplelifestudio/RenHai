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
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 ���ã�RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		logger.debug("Original device count of random pool: {}", businessPool.getElementCount());
		
		// Step_03 ���ã�BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock����A�����������
		assertTrue(businessPool.getDevice(deviceSn1) == null);
		
		mockApp1.enterPool(Consts.BusinessType.Random);
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		assertTrue(businessPool.getDevice(deviceSn1) != null);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_11 ���ã�MockWebSocketConnection::disableConnection������A��ͨ�Ź���
		connection1.disableConnection();
		
		// Step_07 Mock����B�����������
		assertTrue(businessPool.getDevice(deviceSn2) == null);
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.enterPool(Consts.BusinessType.Random);
		assertTrue(businessPool.getDevice(deviceSn2) != null);
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_12 ���ã�RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_13 ���ã�BusinessSessionPool::getCount
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
		
		// Step_14 Mock�¼���A��ͨ�ű����õ����׳�IOException
		// Step_15 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_16 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_17 ���ã�OnlineDevicePool::getCount
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
			
			// Step_18 ���ã�BusinessSessionPool::getCount
			assertEquals(sessionCount, sessionPool.getElementCount());
			
			// Step_19 ���ã�B DeviceWrapper::getBusinessStatus
			assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		}
		else
		{
			// Step_19 ���ã�B DeviceWrapper::getBusinessStatus
			assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		}
		
	}
}
