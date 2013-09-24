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
		mockApp1 = createNewMockApp();
		mockApp2 = createNewMockApp();
		mockApp2.getDeviceWrapper().getDevice().setDeviceSn("SNOfDeviceB");
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
		
		assertTrue(deviceWrapper1.getConnection() instanceof MockWebSocketConnection);
		MockWebSocketConnection connection1 = (MockWebSocketConnection) deviceWrapper1.getConnection();
		
		mockApp1.syncDevice();
		mockApp2.syncDevice();
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 ���ã�RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		
		// Step_03 ���ã�BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock����A�����������
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_07 Mock����B�����������
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 ���ã�MockWebSocketConnection::disableConnection������A��ͨ�Ź���
		connection1.disableConnection();
		
		// Step_12 ���ã�RandomBusinessScheduler::schedule
		//businessPool.getBusinessScheduler().schedule();
		
		// Step_13 ���ã�BusinessSessionPool::getCount
		if (businessPool.getDeviceCountInChat() > 0)
		{
			// Maybe devices have not been scheduled yet
			assertEquals(sessionCount-1, sessionPool.getElementCount());
		}
		else
		{
			// Status of session was changed to init due to communication error with mockApp1
			// So the session was released to session pool
			assertEquals(sessionCount, sessionPool.getElementCount());
		}
		
		// Step_14 Mock�¼���A��ͨ�ű����õ����׳�IOException
		// Step_15 Mock�¼���A onPing
		mockApp1.ping();
		
		// Step_16 Mock�¼���B onPing
		mockApp2.ping();
		
		// Step_17 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_18 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount, sessionPool.getElementCount());
		
		// Step_19 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
	}
}
