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


/**
 * TC_04 新设备首次同步/已有正常状态设备同步/已进入业务后同步/数据库异常
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
		/*
		super.setUp();
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp = createNewMockApp(CommonFunctions.getRandomString(10));
		*/
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		/*
		deleteDevice(mockApp);
		super.tearDown();
		*/
	}
	
	/**
	 * Test function
	 */
	@Test
	public void test() throws InterruptedException
	{
		/*
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getConnectionId());
		
		// Step_01 调用：OnlineDevicePool::getCount
		long lastActivityTime = deviceWrapper.getLastActivityTime();
		
		// Step_02 调用：DeviceWrapper::getBusinessStatus
		Consts.DeviceStatus businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceStatus.Connected);
		
		// Step_03 Mock请求：设备同步
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_04 数据库检查：设备卡片信息
		// 由于使用了缓存，设备退出在线设备池之前不会保存到数据库
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
		
		// Step_05 数据库检查：兴趣卡片信息
		Interestcard interCardInDB = deviceInDB.getProfile().getInterestCard();
		assertTrue(interCardInDB.getInterestLabelMapSet().size() == 3);
		
		// Step_06 数据库检查：印象卡片信息
		Impresscard impressCardInDB = deviceInDB.getProfile().getImpressCard();
		assertTrue(impressCardInDB.getChatLossCount() == 0);
		assertTrue(impressCardInDB.getChatTotalCount() == 0);
		assertTrue(impressCardInDB.getChatTotalDuration() == 0);
		assertTrue(impressCardInDB.getImpressLabelMapSet().size() == Consts.SolidImpressLabel.values().length - 1);
		
		// Step_07 调用：DeviceWrapper::getBusinessStatus
		businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceStatus.AppDataSynced);
		
		// Step_08 调用：OnlineDevicePool::getCount
		String deviceSn = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDeviceIdentification();
		assertTrue(pool.getDevice(deviceSn) != null);
		
		// Step_09 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime();
		
		// Step_10 Mock事件：onClose
		mockApp.disconnect();
		
		// Step_11 调用：OnlineDevicePool::getCount
		assertTrue(OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()) == null);
		
		// Step_12 建立WebSocket连接
		mockApp.connect();
		
		// Step_13 Mock请求：设备同步
		mockApp.chooseBusiness(businessType);
		assertTrue(mockApp.lastReceivedCommandIsError());		//设备同步之前不能使用其他命令
		
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_14 调用：DeviceWrapper::getLastActivityTime
		deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn());
		long newActivityTime = deviceWrapper.getLastActivityTime();
		assertTrue(newActivityTime > lastActivityTime);
		
		// Step_15 Mock请求：进入业务设备池
		mockApp.chooseBusiness(businessType);
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_16 Mock请求：设备同步
		mockApp.clearLastReceivedCommand();
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_17 Mock事件：onClose
		mockApp.disconnect();
		*/
	}
}
