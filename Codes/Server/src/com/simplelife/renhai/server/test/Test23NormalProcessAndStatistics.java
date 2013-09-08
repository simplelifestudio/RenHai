/**
 * Test23NormalProcessAndStatistics.java
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
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test23NormalProcessAndStatistics extends AbstractTestCase
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
		mockApp2.getDeviceWrapper().getDevice().getDevicecard().setDeviceSn("SNOfDeviceB");
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
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		
		// Step_01 Mock���󣺲�ѯ����ͳ��������������豸���豸���������豸�����ޣ����ҵ���豸���豸�������ҵ���豸�����ޣ���������״̬���豸���������������״̬���豸����ҵ���豸�ص�������Ȥ��ǩ
		mockApp1.sendServerDataSyncRequest();
		fail("��Ҫ���Ͳ���ʵ����ͳ����");
		
		// Step_02 Mock����A�����������
		syncDevice(mockApp1);
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_03 Mock����B�����������
		syncDevice(mockApp2);
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_04 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		fail("��Ҫ���Ͳ���ʵ����ͳ����");
		
		// Step_05 ���ã�RandomBusinessScheduler::schedule
		businessPool.getBusinessScheduler().schedule();
		
		// Step_06 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_07 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		fail("��Ҫ���Ͳ���ʵ����ͳ����");
		
		// Step_08 Mock�¼���Aͬ������
		mockApp1.chatConfirm(true);
		
		// Step_09 Mock�¼���Bͬ������
		mockApp2.chatConfirm(true);
		
		// Step_10 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_11 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		fail("��Ҫ���Ͳ���ʵ����ͳ����");
		
		// Step_12 Mock�¼���A����ͨ��
		mockApp1.endChat();
		
		// Step_13 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_14 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		fail("��Ҫ���Ͳ���ʵ����ͳ����");
		
		// Step_15 Mock�¼���B����ͨ��
		mockApp2.endChat();
		
		// Step_16 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		fail("��Ҫ���Ͳ���ʵ����ͳ����");
		
		// Step_17 Mock�¼���A onPing
		mockApp1.ping();
		
		// Step_18 Mock�¼���B onPing
		mockApp2.ping();
		
		// Step_19 Mock�¼���A��B����
		mockApp1.assess("TC23_����");
		
		// Step_20 Mock�¼���B��A����
		mockApp2.assess("TC24_����");
		
		// Step_21 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_22 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		fail("��Ҫ���Ͳ���ʵ����ͳ����");
	}
}
