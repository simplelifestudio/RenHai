/**
 * Test04SyncDeviceNormal.java
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

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/**
 * TC_04 ���豸�״�ͬ��/��������״̬�豸ͬ��/�ѽ���ҵ���ͬ��/���ݿ��쳣
 */
public class Test04SyncDeviceNormal extends AbstractTestCase
{
	private LocalMockApp mockApp;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp = createNewMockApp(CommonFunctions.getRandomString(10));
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp);
	}
	
	/**
	 * Test function
	 */
	@Test
	public void test()
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = mockApp.getDeviceWrapper();
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		long lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_02 ���ã�DeviceWrapper::getBusinessStatus
		Consts.BusinessStatus businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.BusinessStatus.Init);
		
		// Step_03 Mock�����豸ͬ��
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_04 ���ݿ��飺�豸��Ƭ��Ϣ
		DeviceDAO deviceDAO = new DeviceDAO();
		Device deviceInDB = deviceDAO.findByDeviceSn(deviceWrapper.getDeviceSn()).get(0);
		Devicecard deviceCardInDB = deviceInDB.getDevicecard();
		assertEquals(mockApp.getAppVersion(), deviceCardInDB.getAppVersion());
		assertEquals(mockApp.getDeviceModel(), deviceCardInDB.getDeviceModel());
		assertEquals(mockApp.getDeviceWrapper().getDeviceSn(), deviceCardInDB.getDevice().getDeviceSn());
		assertEquals(Consts.YesNo.No.name(), deviceCardInDB.getIsJailed());
		assertEquals(mockApp.getLocation(), deviceCardInDB.getLocation());
		assertEquals(mockApp.getOSVersion(), deviceCardInDB.getOsVersion());
		
		// Step_05 ���ݿ��飺��Ȥ��Ƭ��Ϣ
		Interestcard interCardInDB = deviceInDB.getProfile().getInterestcard();
		assertTrue(interCardInDB.getInterestlabelmaps().isEmpty());
		
		// Step_06 ���ݿ��飺ӡ��Ƭ��Ϣ
		Impresscard impressCardInDB = deviceInDB.getProfile().getImpresscard();
		assertTrue(impressCardInDB.getChatLossCount() == 0);
		assertTrue(impressCardInDB.getChatTotalCount() == 0);
		assertTrue(impressCardInDB.getChatTotalDuration() == 0);
		assertTrue(impressCardInDB.getImpresslabelmaps().size() == Consts.SolidAssessLabel.values().length - 1);
		
		// Step_07 ���ã�DeviceWrapper::getBusinessStatus
		businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.BusinessStatus.Idle);
		
		// Step_08 ���ã�OnlineDevicePool::getCount
		String deviceSn = mockApp.getDeviceWrapper().getDeviceSn();
		assertTrue(pool.getDevice(deviceSn) != null);
		
		// Step_09 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_10 Mock�¼���onClose
		mockApp.close();
		
		// Step_11 ���ã�OnlineDevicePool::getCount
		assertTrue(pool.getDevice(mockApp.getDeviceWrapper().getDeviceSn()) == null);
		
		// Step_12 ����WebSocket����
		deviceWrapper = mockApp.getDeviceWrapper();
		
		// Step_13 Mock�����豸ͬ��
		mockApp.enterPool(businessType);
		assertTrue(mockApp.lastReceivedCommandIsError());		//�豸ͬ��֮ǰ����ʹ����������
		
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_14 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		
		// Step_15 Mock���󣺽����������
		mockApp.enterPool(businessType);
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_16 Mock�����豸ͬ��
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_17 Mock�¼���onClose
		mockApp.close();
		
		// Step_18 ����WebSocket����
		deviceWrapper = mockApp.getDeviceWrapper();
		
		// Step_19 ֹͣ���ݿ����
		DBModule.instance.stopService();
		
		// Step_20 Mock�����豸ͬ��
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		// fail("��Ҫ����ʱӦ���յ�ϵͳ���ϣ�����ʧ��");
	}
}
