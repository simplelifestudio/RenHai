/**
 * Test20MatchConfirmBReject.java
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
public class Test20MatchConfirmBReject extends AbstractTestCase
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
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = onlinePool.getElementCount();
		
		// Step_02 ���ã�RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getElementCount();
		
		// Step_03 ���ã�BusinessSessionPool::getCount
		int sessionCount = sessionPool.getElementCount();
		
		// Step_04 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper1.getBusinessStatus());
		
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper2.getBusinessStatus());
		
		// Step_06 Mock����A�����������
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_07 Mock����B�����������
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_12 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_14 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock�¼���Aȷ�ϰ�
		mockApp1.sendNotificationResponse();
		
		// Step_18 Mock�¼���Bȷ�ϰ�
		mockApp2.sendNotificationResponse();
		
		// Step_19 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_20 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_21 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_15 Mock�¼���Aͬ������
		mockApp1.chatConfirm(true);
		
		// Step_16 Mock�¼���B�ܾ�����
		mockApp2.chatConfirm(false);
		
		// Step_17 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_18 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_19 Mock�¼���A onPing
		mockApp1.ping();
		
		// Step_20 Mock�¼���B onPing
		mockApp2.ping();
		
		// Step_21 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, onlinePool.getElementCount());
		
		// Step_22 ���ã�BusinessSessionPool::getCount
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		assertEquals(sessionCount + 1, sessionPool.getElementCount());
	}
}
