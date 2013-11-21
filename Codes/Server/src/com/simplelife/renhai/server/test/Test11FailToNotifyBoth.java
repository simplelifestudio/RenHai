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
		logger.debug("Enter tearDown()");
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
		IDeviceWrapper deviceWrapper1 = onlinePool.getDevice(mockApp1.getConnectionId());
		IDeviceWrapper deviceWrapper2 = onlinePool.getDevice(mockApp2.getConnectionId());
		
		MockWebSocketConnection connection1 = getMockWebSocket(deviceWrapper1);
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
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock����A�����������
		mockApp1.enterPool(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_07 Mock����B�����������
		mockApp2.enterPool(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 ���ã�MockWebSocketConnection::disableConnection������A��ͨ�Ź���
		
		
		// Step_12 ���ã�MockWebSocketConnection::disableConnection������B��ͨ�Ź���
		// Step_13 ���ã�RandomBusinessScheduler::schedule
		mockApp1.matchStart();
		connection1.disableConnection();
		
		mockApp2.matchStart();
		connection2.disableConnection();
		
		Thread.sleep(1000);
		logger.debug("Recover from sleep");
		
		// Step_14 ���ã�BusinessSessionPool::getCount
		assertTrue(onlinePool.getDevice(demoDeviceSn) == null);
		assertTrue(onlinePool.getDevice(demoDeviceSn2) == null);
		
		assertTrue(businessPool.getDevice(demoDeviceSn) == null);
		assertTrue(businessPool.getDevice(demoDeviceSn2) == null);
		
		assertTrue(deviceWrapper1.getOwnerBusinessSession() == null);
		assertTrue(deviceWrapper2.getOwnerBusinessSession() == null);
		
		assertEquals(sessionCount, sessionPool.getElementCount());
		
		// Step_15 Mock�¼���A��ͨ�ű����õ����׳�IOException
		// Step_16 Mock�¼���B��ͨ�ű����õ����׳�IOException
		
		// Step_17 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_18 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_19 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 2, onlinePool.getElementCount());
		
		// Step_20 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount, sessionPool.getElementCount());
	}
}
