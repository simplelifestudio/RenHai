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
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/**
 * TC_04 新设备首次同步/已有正常状态设备同步/已进入业务后同步/数据库异常
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
		mockApp = createNewMockApp();
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
		
		Devicecard deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		long lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_02 调用：DeviceWrapper::getBusinessStatus
		Consts.BusinessStatus businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.BusinessStatus.Init);
		
		// Step_03 Mock请求：设备同步
		mockApp.clearLastReceivedCommand();
		mockApp.syncDevice();
		JSONObject lastMsg = mockApp.getLastReceivedCommand();
		
		// Step_04 数据库检查：设备卡片信息
		DeviceDAO deviceDAO = new DeviceDAO();
		Device deviceInDB = deviceDAO.findByDeviceSn(deviceWrapper.getDeviceSn()).get(0);
		Devicecard deviceCardInDB = deviceInDB.getDevicecard();
		assertEquals(mockApp.getAppVersion(), deviceCardInDB.getAppVersion());
		assertEquals(mockApp.getDeviceModel(), deviceCardInDB.getDeviceModel());
		assertEquals(mockApp.getDeviceWrapper().getDeviceSn(), deviceCardInDB.getDevice().getDeviceSn());
		assertEquals(Consts.YesNo.No.name(), deviceCardInDB.getIsJailed());
		assertEquals(mockApp.getLocation(), deviceCardInDB.getLocation());
		assertEquals(mockApp.getOSVersion(), deviceCardInDB.getOsVersion());
		
		// Step_05 数据库检查：兴趣卡片信息
		Interestcard interCardInDB = deviceInDB.getProfile().getInterestcard();
		assertTrue(interCardInDB.getInterestlabelmaps().isEmpty());
		
		// Step_06 数据库检查：印象卡片信息
		Impresscard impressCardInDB = deviceInDB.getProfile().getImpresscard();
		assertTrue(impressCardInDB.getChatLossCount() == 0);
		assertTrue(impressCardInDB.getChatTotalCount() == 0);
		assertTrue(impressCardInDB.getChatTotalDuration() == 0);
		assertTrue(impressCardInDB.getImpresslabelmaps().isEmpty());
		
		// Step_07 调用：DeviceWrapper::getBusinessStatus
		businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.BusinessStatus.Idle);
		
		// Step_08 调用：OnlineDevicePool::getCount
		String deviceSn = mockApp.getDeviceWrapper().getDeviceSn();
		assertTrue(pool.getDevice(deviceSn) != null);
		
		// Step_09 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_10 Mock事件：onClose
		mockApp.close();
		
		// Step_11 调用：OnlineDevicePool::getCount
		assertTrue(pool.getDevice(mockApp.getDeviceWrapper().getDeviceSn()) == null);
		
		// Step_12 建立WebSocket连接
		deviceWrapper = mockApp.getDeviceWrapper();
		deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_13 Mock请求：设备同步
		mockApp.syncDevice();
		
		// Step_14 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		
		// Step_15 Mock请求：进入随机聊天
		mockApp.enterPool(Consts.BusinessType.Random);
		
		// Step_16 Mock请求：设备同步
		mockApp.clearLastReceivedCommand();
		mockApp.syncDevice();
		lastMsg = mockApp.getLastReceivedCommand();
		
		// Step_17 Mock事件：onClose
		mockApp.close();
		
		// Step_18 建立WebSocket连接
		deviceWrapper = mockApp.getDeviceWrapper();
		deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_19 停止数据库服务
		DBModule.instance.stopService();
		
		// Step_20 Mock请求：设备同步
		mockApp.syncDevice();
		// fail("需要检查此时应该收到系统故障，操作失败");
	}
}
