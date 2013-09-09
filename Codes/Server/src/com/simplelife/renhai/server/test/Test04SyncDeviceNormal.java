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
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		long lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_02 调用：DeviceWrapper::getBusinessStatus
		Consts.DeviceBusinessStatus businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceBusinessStatus.Init);
		
		// Step_03 Mock请求：设备同步
		mockApp.syncDevice();
		
		// Step_04 数据库检查：设备卡片信息
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
		
		// Step_05 数据库检查：兴趣卡片信息
		fail("数据库检查待实现");
		
		// Step_06 数据库检查：印象卡片信息
		fail("数据库检查待实现");
		
		// Step_07 调用：DeviceWrapper::getBusinessStatus
		businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.DeviceBusinessStatus.Idle);
		
		// Step_08 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount + 1, pool.getElementCount());
		
		// Step_09 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_10 Mock事件：onClose
		connectionOwner.getConnection().onClose(0);
		
		// Step_11 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getElementCount());
		
		// Step_12 建立WebSocket连接
		deviceWrapper = mockApp.getDeviceWrapper();
		assertTrue(deviceWrapper instanceof IBaseConnectionOwner);
		connectionOwner = (IBaseConnectionOwner) deviceWrapper;
		deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_13 Mock请求：设备同步
		mockApp.syncDevice();
		
		// Step_14 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		
		// Step_15 Mock请求：进入随机聊天
		mockApp.enterPool(Consts.BusinessType.Random);
		
		// Step_16 Mock请求：设备同步
		mockApp.syncDevice();
		fail("需要检查收到的消息是否正确");
		
		// Step_17 Mock事件：onClose
		connectionOwner.getConnection().onClose(0);
		
		// Step_18 建立WebSocket连接
		deviceWrapper = mockApp.getDeviceWrapper();
		assertTrue(deviceWrapper instanceof IBaseConnectionOwner);
		connectionOwner = (IBaseConnectionOwner) deviceWrapper;
		deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_19 停止数据库服务
		DBModule.instance().stopService();
		
		// Step_20 Mock请求：设备同步
		mockApp.syncDevice();
		fail("需要检查此时应该收到系统故障，操作失败");
	}
}
