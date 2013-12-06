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

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Devicecard;
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
	private MockApp mockApp;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
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
		super.tearDown();
	}
	
	/**
	 * Test function
	 */
	@Test
	public void test() throws InterruptedException
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getConnectionId());
		
		// Step_01 ���ã�OnlineDevicePool::getCount
		long lastActivityTime = deviceWrapper.getLastActivityTime();
		
		// Step_02 ���ã�DeviceWrapper::getBusinessStatus
		Consts.DeviceStatus businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceStatus.Connected);
		
		// Step_03 Mock�����豸ͬ��
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_04 ���ݿ��飺�豸��Ƭ��Ϣ
		// ����ʹ���˻��棬�豸�˳������豸��֮ǰ���ᱣ�浽���ݿ�
		//SqlSession session = DAOWrapper.instance.getSession();
		//DeviceMapper mapper = session.getMapper(DeviceMapper.class);
		//Device deviceInDB = mapper.selectByDeviceSn(deviceWrapper.getDeviceSn());
		Device deviceInDB = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDevice();
		
		Devicecard deviceCardInDB = deviceInDB.getDeviceCard();
		assertEquals(mockApp.getAppVersion(), deviceCardInDB.getAppVersion());
		assertEquals(mockApp.getDeviceModel(), deviceCardInDB.getDeviceModel());
		assertEquals(OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDeviceIdentification(), deviceInDB.getDeviceSn());
		assertEquals(Consts.YesNo.No.name(), deviceCardInDB.getIsJailed());
		assertEquals(mockApp.getLocation(), deviceCardInDB.getLocation());
		assertEquals(mockApp.getOSVersion(), deviceCardInDB.getOsVersion());
		
		// Step_05 ���ݿ��飺��Ȥ��Ƭ��Ϣ
		Interestcard interCardInDB = deviceInDB.getProfile().getInterestCard();
		assertTrue(interCardInDB.getInterestLabelMapSet().size() == 3);
		
		// Step_06 ���ݿ��飺ӡ��Ƭ��Ϣ
		Impresscard impressCardInDB = deviceInDB.getProfile().getImpressCard();
		assertTrue(impressCardInDB.getChatLossCount() == 0);
		assertTrue(impressCardInDB.getChatTotalCount() == 0);
		assertTrue(impressCardInDB.getChatTotalDuration() == 0);
		assertTrue(impressCardInDB.getImpressLabelMapSet().size() == Consts.SolidImpressLabel.values().length - 1);
		
		// Step_07 ���ã�DeviceWrapper::getBusinessStatus
		businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceStatus.AppDataSynced);
		
		// Step_08 ���ã�OnlineDevicePool::getCount
		String deviceSn = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDeviceIdentification();
		assertTrue(pool.getDevice(deviceSn) != null);
		
		// Step_09 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime();
		
		// Step_10 Mock�¼���onClose
		mockApp.disconnect();
		
		// Step_11 ���ã�OnlineDevicePool::getCount
		assertTrue(OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()) == null);
		
		// Step_12 ����WebSocket����
		mockApp.connect();
		
		// Step_13 Mock�����豸ͬ��
		mockApp.chooseBusiness(businessType);
		assertTrue(mockApp.lastReceivedCommandIsError());		//�豸ͬ��֮ǰ����ʹ����������
		
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_14 ���ã�DeviceWrapper::getLastActivityTime
		deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn());
		long newActivityTime = deviceWrapper.getLastActivityTime();
		assertTrue(newActivityTime > lastActivityTime);
		
		// Step_15 Mock���󣺽���ҵ���豸��
		mockApp.chooseBusiness(businessType);
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_16 Mock�����豸ͬ��
		mockApp.clearLastReceivedCommand();
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_17 Mock�¼���onClose
		mockApp.disconnect();
	}
}
