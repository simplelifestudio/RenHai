/**
 * Test19MatchConfirmAReject.java
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
public class Test19MatchConfirmAReject extends AbstractTestCase
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
		
		// Step_07 Mock����B�����������
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		//businessPool.getBusinessScheduler().schedule();
		
		// Step_12 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_14 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock�¼���Aȷ�ϰ�
		mockApp1.sendNotificationResponse(Consts.NotificationType.SessionBinded, "", "1");
		
		// Step_18 Mock�¼���Bȷ�ϰ�
		mockApp2.sendNotificationResponse(Consts.NotificationType.SessionBinded, "", "1");
		
		// Step_19 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_20 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_21 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_15 Mock�¼���A�ܾ�����
		mockApp1.chatConfirm(false);
		
		// Step_16 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_17 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_18 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_19 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_20 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, onlinePool.getElementCount());
		
		// Step_21 ���ã�BusinessSessionPool::getCount
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		assertEquals(sessionCount + 1, sessionPool.getElementCount());
	}
}
