/**
 * Test13FailToNotifyB.java
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
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/**
 * 
 */
public class Test13FailToNotifyB extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
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
	public void test() throws InterruptedException
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;
		IDeviceWrapper deviceWrapper1 = OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn());
		IDeviceWrapper deviceWrapper2 = OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn());
		
		MockWebSocketConnection connection2 = getMockWebSocket(deviceWrapper2);
		
		mockApp1.syncDevice();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		mockApp2.syncDevice();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
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
		mockApp1.clearLastReceivedCommand();
		mockApp1.enterPool(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		businessPool.getBusinessScheduler().stopScheduler();
		// Step_07 Mock����B�����������
		mockApp2.clearLastReceivedCommand();
		mockApp2.enterPool(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_12 ���ã�RandomBusinessScheduler::schedule
		connection2.disableConnection();
		mockApp1.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		
		// Step_13 Mock�¼���B��ͨ�ű����õ����׳�IOException
		// Step_14 ���ã�BusinessSessionPool::getCount
		
		// ����SessionBounded
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBinded));
		//assertEquals(sessionCount-1, sessionPool.getElementCount());
		
		// ����OthersideLost
		mockApp1.clearLastReceivedCommand();
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.OthersideLost));
		
		//assertEquals(sessionCount, sessionPool.getElementCount());
		//sessionCount = sessionPool.getElementCount();
		
		// Step_15 ���ã�A DeviceWrapper::getBusinessStatus
		assertTrue(OnlineDevicePool.instance.getDevice(demoDeviceSn) != null);
		assertTrue(businessPool.getDevice(demoDeviceSn) != null);
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		assertTrue(deviceWrapper1.getOwnerBusinessSession() == null);
		
		// Step_16 ���ã�B DeviceWrapper::getBusinessStatus
		assertTrue(OnlineDevicePool.instance.getDevice(demoDeviceSn2) == null);
		assertTrue(businessPool.getDevice(demoDeviceSn2) == null);
		
		// Step_17 ���ã�BusinessSession::getStatus
		
				
		// Step_20 ���ã�A DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_21 ���ã�B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_22 Mock�¼���Aͬ������
		//mockApp1.chatConfirm(true);
		
		// Step_23 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_24 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_25 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_26 ���ã�BusinessSessionPool::getCount
		
		// Step_27 ���ã�A DeviceWrapper::getBusinessStatus
	}
}
