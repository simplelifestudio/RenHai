/**
 * Test18AssessALoseConnection.java
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


/**
 * 
 */
public class Test18AssessALoseConnection extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		/*
		super.setUp();
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp(demoDeviceSn);
		mockApp2 = createNewMockApp(demoDeviceSn2);
		*/
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		/*
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
		super.tearDown();
		*/
	}
	
	@Test
	public void test() throws InterruptedException
	{
		/*
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		BusinessSessionPool sessionPool = BusinessSessionPool.instance;
		IDeviceWrapper deviceWrapper1 = OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn());
		IDeviceWrapper deviceWrapper2 = OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn());
		
		mockApp1.syncDevice();
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.syncDevice();
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = onlinePool.getDeviceCount();
		
		// Step_02 ���ã�RandomBusinessDevicePool::getCount
		int randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_03 ���ã�BusinessSessionPool::getCount
		int sessionCount = sessionPool.getDeviceCount();
		
		// Step_04 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper1.getBusinessStatus());
		
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		businessPool.getBusinessScheduler().stopScheduler();
		
		// Step_06 Mock����A�����������
		mockApp1.chooseBusiness(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));
		
		// Step_07 Mock����B�����������
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.chooseBusiness(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.ChooseBusiness));
		
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper1.getBusinessStatus());
		
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.BusinessChoosed, deviceWrapper2.getBusinessStatus());
		
		// Step_10 ���ã�RandomBusinessDevicePool::getCount
		assertEquals(randomDeviceCount + 2, businessPool.getDeviceCount());
		randomDeviceCount = businessPool.getDeviceCount();
		
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		mockApp1.waitMessage();
		
		// Step_12 ���ã�BusinessSession::getStatus
		//assertEquals(deviceWrapper1.getBusinessStatus(), Consts.BusinessStatus.SessionBound);
		assertTrue(deviceWrapper1.getOwnerBusinessSession() != null);
		assertTrue(deviceWrapper2.getOwnerBusinessSession() != null);
		
		logger.debug("sleep 1000");
		Thread.sleep(1000);

		logger.debug("Recovered from sleep");
		
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_13 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount - 1, sessionPool.getDeviceCount());
		sessionCount = sessionPool.getDeviceCount();
		
		// Step_14 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper1.getBusinessStatus());
		
		// Step_15 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_16 ���ã�BusinessSession::getStatus
		
		// Step_22 Mock�¼���Aͬ������
		mockApp2.clearLastReceivedCommand();
		mockApp1.chatConfirm(true);
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		
		// Step_23 Mock�¼���Bͬ������
		mockApp2.clearLastReceivedCommand();
		mockApp2.chatConfirm(true);
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// Bȷ�Ϻ����е��豸��ȷ���ˣ� ��ʱҵ��Ự�л�״̬������֪ͨ���Bȷ����
		//mockApp1.waitMessage();
		//assertTrue(mockApp1.getLastReceivedCommand() != null);
		
		// Step_24 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_25 Mock�¼���A����ͨ��
		Thread.sleep(1000);

		mockApp2.clearLastReceivedCommand();
		mockApp1.endChat();
		assertTrue(!mockApp1.lastReceivedCommandIsError());
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		
		// Step_26 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		Thread.sleep(1000);

		// Step_27 Mock�¼���A onClose
		mockApp2.clearLastReceivedCommand();
		mockApp1.disconnect();
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		
		// Step_28 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, onlinePool.getDeviceCount());
		deviceCount = onlinePool.getDeviceCount();
		
		// Step_29 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount, sessionPool.getDeviceCount());
		
		// Step_30 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.SessionBound, deviceWrapper2.getBusinessStatus());
		
		// Step_31 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_32 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_33 Mock�¼���B��A���ۣ���֮���˳�ҵ��
		mockApp2.assessAndQuit("^#Happy#^,˧��");
		assertTrue(mockApp2.getLastReceivedCommand() != null);
		assertTrue(!mockApp2.lastReceivedCommandIsError());
		
		// Step_34 ���ݿ��飺A ӡ��Ƭ��Ϣ
		// Step_35 ���ݿ��飺B ӡ��Ƭ��Ϣ
		//fail("��Ҫ������ݿ��е�ӡ��Ƭ��Ϣ");
		// TODO "��Ҫ������ݿ��е�ӡ��Ƭ��Ϣ"
		
		// Step_36 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_37 ���ã�BusinessSessionPool::getCount
		assertEquals(sessionCount + 1, sessionPool.getDeviceCount());
		
		// Step_38 ���ã�B DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper2.getBusinessStatus());
		
		// Step_39 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, onlinePool.getDeviceCount());
		*/
	}
}
