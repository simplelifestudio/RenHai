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
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test12TimeoutNotifyA extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
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
		super.tearDown();
	}
	
	@Test
	public void test() throws InterruptedException
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType); 
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;  
		IDeviceWrapper deviceWrapper1 = OnlineDevicePool.instance.getDevice(mockApp1.getConnectionId());
		IDeviceWrapper deviceWrapper2 = OnlineDevicePool.instance.getDevice(mockApp2.getConnectionId());
		
		mockApp1.syncDevice();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		mockApp2.syncDevice();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = onlinePool.getDeviceCount();
		
		// Step_02 调用：RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_03 调用：BusinessSessionPool::getCount
		int sessionCount = sessionPool.getDeviceCount();
		
		// Step_04 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());

		// Step_06 Mock请求：A进入随机聊天
		mockApp1.clearLastReceivedCommand();
		mockApp1.chooseBusiness(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));
		
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp1.stopAutoReply();
		
		// Step_07 Mock请求：B进入随机聊天
		mockApp2.clearLastReceivedCommand();
		mockApp2.chooseBusiness(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));

		// Step_08 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_09 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_10 调用：RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getDeviceCount());
		randomDeviceCount = businessPool.getDeviceCount();

		mockApp1.matchStart();
		mockApp2.matchStart();
		
		// Step_11 调用：RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		mockApp2.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		mockApp1.waitMessage();
		mockApp2.waitMessage();
		
		// Step_12 调用：BusinessSessionPool::getCount
		//由于A没有回应session会话绑定，等待超时后session会被释放，调度没有成功
		//assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBound));
		//assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBound));
		//mockApp2.clearLastReceivedCommand();
		
		//assertEquals(sessionCount - 1, sessionPool.getElementCount());
		//sessionCount = sessionPool.getElementCount();
		
		// Step_13 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_14 调用：B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_15 Mock事件：A timeOut
		
		// Server等待A的响应超时，此时B应该收到A ConnectionLost的通知
		mockApp2.waitMessage();
		//assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.OthersideLost));
		
		// Step_16 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_17 Mock事件：B onPing
		//mockApp2.ping();
		
		// Step_18 调用：OnlineDevicePool::getCount
		//assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_19 调用：BusinessSessionPool::getCount
		//assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_20 调用：B DeviceWrapper::getBusinessStatus
		mockApp2.sessionUnbind();
		
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
	}
}
