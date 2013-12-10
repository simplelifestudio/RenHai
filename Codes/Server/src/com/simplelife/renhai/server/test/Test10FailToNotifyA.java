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
		IDeviceWrapper deviceWrapper1 = onlinePool.getDevice(mockApp1.getConnectionId());
		IDeviceWrapper deviceWrapper2 = onlinePool.getDevice(mockApp2.getConnectionId());
		
		String deviceSn1 = mockApp1.getDeviceSn();
		String deviceSn2 = mockApp2.getDeviceSn();
		
		assertTrue(deviceWrapper1.getConnection() instanceof MockWebSocketConnection);
		MockWebSocketConnection connection1 = (MockWebSocketConnection) deviceWrapper1.getConnection();
		
		mockApp1.syncDevice();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		mockApp2.syncDevice();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		onlinePool.getDeviceCount();
		
		// Step_02 ���ã�RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getDeviceCount();
		logger.debug("Original device count of random pool: {}", businessPool.getDeviceCount());
		
		// Step_03 ���ã�BusinessSessionPool::getCount
		int sessionCount = sessionPool.getDeviceCount();
		
		// Step_04 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock����A�����������
		assertTrue(businessPool.getDevice(deviceSn1) == null);
		
		mockApp1.chooseBusiness(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));
		deviceWrapper1 = businessPool.getDevice(deviceSn1);
		assertTrue(deviceWrapper1 != null);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		 
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_07 Mock����B�����������
		assertTrue(businessPool.getDevice(deviceSn2) == null);
		mockApp2.chooseBusiness(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));
		assertTrue(businessPool.getDevice(deviceSn2) != null);
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.MatchCache, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getDeviceCount());
		randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_12 ���ã�RandomBusinessScheduler::schedule
		//businessPool.getBusinessScheduler().schedule();
		
		// Step_11 ���ã�MockWebSocketConnection::disableConnection������A��ͨ�Ź���
		mockApp1.matchStart();
		connection1.disableConnection();
				
		// Step_13 ���ã�BusinessSessionPool::getCount
		mockApp2.matchStart();
		
		// Step_14 Mock�¼���A��ͨ�ű����õ����׳�IOException
		// Step_15 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_16 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_17 ���ã�OnlineDevicePool::getCount
		Thread.sleep(1000);
		logger.debug("recover from sleep");
		assertTrue(onlinePool.getDevice(mockApp1.getDeviceSn()) == null);
		assertTrue(onlinePool.getDevice(mockApp2.getDeviceSn()) != null);
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		mockApp2.sessionUnbind();
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		mockApp2.matchStart();
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
	}
}
