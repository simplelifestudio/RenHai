/**
 * Test04SyncDeviceNormal.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;


import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
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
		mockApp = createMockApp();
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
		assertTrue(deviceWrapper instanceof IBaseConnectionOwner);
		IBaseConnectionOwner connectionOwner = (IBaseConnectionOwner) deviceWrapper;
		
		Devicecard deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		long lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_02 ���ã�DeviceWrapper::getBusinessStatus
		Consts.DeviceBusinessStatus businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceBusinessStatus.Init);
		
		// Step_03 Mock�����豸ͬ��
		mockApp.syncDevice();
		
		// Step_04 ���ݿ��飺�豸��Ƭ��Ϣ
		String sql = "select * from Devicecard where deviceSn = '" + deviceCard.getDeviceSn() + "";
		List<Devicecard> deviceList = DAOWrapper.query(sql, Devicecard.class);
		assertEquals(1, deviceList.size());
		Devicecard cardInDB = deviceList.get(0);
		
		assertEquals(AppVersion, cardInDB.getAppVersion());
		assertEquals(DeviceModel, cardInDB.getDeviceModel());
		assertEquals(DeviceSN, cardInDB.getDeviceSn());
		assertEquals(IsJailed, cardInDB.getIsJailed());
		assertEquals(Location, cardInDB.getLocation());
		assertEquals(OSVersion, cardInDB.getOsVersion());
		
		// Step_05 ���ݿ��飺��Ȥ��Ƭ��Ϣ
		fail("���ݿ����ʵ��");
		
		// Step_06 ���ݿ��飺ӡ��Ƭ��Ϣ
		fail("���ݿ����ʵ��");
		
		// Step_07 ���ã�DeviceWrapper::getBusinessStatus
		businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceBusinessStatus.Idle);
		
		// Step_08 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount + 1, pool.getElementCount());
		
		// Step_09 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_10 Mock�¼���onClose
		connectionOwner.getConnection().onClose(0);
		
		// Step_11 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getElementCount());
		
		// Step_12 ����WebSocket����
		deviceWrapper = mockApp.getDeviceWrapper();
		assertTrue(deviceWrapper instanceof IBaseConnectionOwner);
		connectionOwner = (IBaseConnectionOwner) deviceWrapper;
		deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_13 Mock�����豸ͬ��
		mockApp.syncDevice();
		
		// Step_14 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		
		// Step_15 Mock���󣺽����������
		mockApp.enterPool(Consts.BusinessType.Random);
		
		// Step_16 Mock�����豸ͬ��
		mockApp.syncDevice();
		fail("��Ҫ����յ�����Ϣ�Ƿ���ȷ");
		
		// Step_17 Mock�¼���onClose
		connectionOwner.getConnection().onClose(0);
		
		// Step_18 ����WebSocket����
		deviceWrapper = mockApp.getDeviceWrapper();
		assertTrue(deviceWrapper instanceof IBaseConnectionOwner);
		connectionOwner = (IBaseConnectionOwner) deviceWrapper;
		deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_19 ֹͣ���ݿ����
		DBModule.instance().stopService();
		
		// Step_20 Mock�����豸ͬ��
		mockApp.syncDevice();
		fail("��Ҫ����ʱӦ���յ�ϵͳ���ϣ�����ʧ��");
	}
}
