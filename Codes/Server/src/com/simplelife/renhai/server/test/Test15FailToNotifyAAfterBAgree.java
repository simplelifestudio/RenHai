/**
 * Test15FailToNotifyAAfterBAgree.java
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
public class Test15FailToNotifyAAfterBAgree extends AbstractTestCase
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
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.enterPool(businessType);
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getElementCount());
		randomDeviceCount = businessPool.getElementCount();
		
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		mockApp1.waitMessage();
		assertFalse(mockApp1.lastReceivedCommandIsError());
		
		mockApp2.waitMessage();
		assertFalse(mockApp2.lastReceivedCommandIsError());
		
		// Step_12 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getElementCount());
		sessionCount = sessionPool.getElementCount();
		
		// Step_13 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_14 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 ���ã�BusinessSession::getStatus
		
		
		// Step_16 Mock�¼���Aȷ�ϰ�
		//mockApp1.sendNotificationResponse(Consts.NotificationType.SessionBinded, "", "1");
		
		// Step_17 Mock�¼���Bȷ�ϰ�
		//mockApp2.sendNotificationResponse(Consts.NotificationType.SessionBinded, "", "1");
		
		// Step_18 ���ã�BusinessSession::getStatus
		try
		{
			// �ȴ�Server������A��B�İ�ȷ��
			Thread.sleep(1000);
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		logger.debug("Status of session:{}", session.getStatus().name());
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_19 ���ã�A DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_20 ���ã�B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_21 Mock�¼���Aͬ������
		mockApp1.clearLastReceivedCommand();
		mockApp2.clearLastReceivedCommand();
		mockApp1.chatConfirm(true);
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		// �ȴ�Server֪ͨAͬ������
		mockApp2.waitMessage();
		assertFalse(mockApp2.lastReceivedCommandIsError());
		
		// Step_23 ���ã�MockWebSocketConnection::disableConnection������A��ͨ�Ź���
		MockWebSocketConnection socket1 = getMockWebSocket(deviceWrapper1);
		socket1.disableConnection();
				
		// Step_22 Mock�¼���Bͬ������
		mockApp2.clearLastReceivedCommand();
		mockApp2.chatConfirm(true);
		
		// �ȴ�Bͬ���������Ӧ
		mockApp2.waitMessage();
		assertFalse(mockApp2.lastReceivedCommandIsError());
		
		// �ȴ�֪ͨ����A��OthersideLost
		mockApp2.waitMessage();
		assertFalse(mockApp2.lastReceivedCommandIsError());
		
		// Step_24 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_25 Mock�¼���B onPing
		//mockApp2.ping();
		
		
		// Step_26 ���ã�OnlineDevicePool::getCount
		//assertEquals(deviceCount - 1, onlinePool.getElementCount());
		
		// Step_27 ���ã�BusinessSessionPool::getCount
		//assertEquals(sessionCount + 1, sessionPool.getElementCount());
		
		// Step_28 ���ã�B DeviceWrapper::getBusinessStatus
		//assertEquals(Consts.BusinessStatus.WaitMatch, deviceWrapper2.getBusinessStatus());
	}
}
