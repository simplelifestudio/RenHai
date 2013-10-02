/**
 * Test25TimeoutNotifyANoEcho.java
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
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/**
 * 
 */
public class Test25TimeoutNotifyANoEcho extends AbstractTestCase
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
		mockApp1.enterPool(businessType);
		
		// Step_07 Mock����B�����������
		mockApp2.enterPool(businessType);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_12 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_13 ������Mock�¼���A onPing
		
		
		// Step_14 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_17 Mock�¼���B ȷ�ϰ�
		mockApp2.sendNotificationResponse(Consts.NotificationType.SessionBinded, "", "1");
		
		// Step_18 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_19 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_20 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_21 �ȴ���ȷ�ϳ�ʱʱ��
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.ChatConfirm * 2);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_22 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_23 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_24 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_25 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_26 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
	}
}
