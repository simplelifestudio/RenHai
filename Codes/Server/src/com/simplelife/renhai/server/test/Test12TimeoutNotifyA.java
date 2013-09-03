/**
 * Test12TimeoutNotifyA.java
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
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class Test12TimeoutNotifyA extends AbstractTestCase
{
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		mockApp1 = createMockApp();
		mockApp2 = createMockApp();
		mockApp2.getDeviceWrapper().getDevice().getDeviceCard().setDeviceSn("SNOfDeviceB");
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
		OnlineDevicePool onlinePool = OnlineDevicePool.getInstance();
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random); 
		BusinessSessionPool sessionPool = BusinessSessionPool.getInstance();  
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		IDeviceWrapper deviceWrapper2 = mockApp2.getDeviceWrapper();
		syncDevice(mockApp1);
		syncDevice(mockApp2);
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		
		// Step_03 调用：BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper2.getBusinessStatus());

		// Step_06 Mock请求：A进入随机聊天
		mockApp1.enterPool(JSONKey.BusinessType.Random);
		
		// Step_07 Mock请求：B进入随机聊天
		mockApp2.enterPool(JSONKey.BusinessType.Random);

		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();

		// Step_11 调用：RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_12 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_13 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_14 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_15 Mock事件：A timeOut
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.ChatConfirm * 1000 + 1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_16 Mock事件：A onPing
		mockApp1.ping();
		
		// Step_17 Mock事件：B onPing
		mockApp2.ping();
		
		// Step_18 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_19 调用：BusinessSessionPool::getCount
		assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_20 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
	}
}
