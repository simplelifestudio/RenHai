/**
 * Test04SyncDeviceNormal.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;


import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceMapper;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;


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
	public void test() throws InterruptedException
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn());
		
		// Step_01 调用：OnlineDevicePool::getCount
		long lastActivityTime = deviceWrapper.getLastActivityTime();
		
		// Step_02 调用：DeviceWrapper::getBusinessStatus
		Consts.BusinessStatus businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.BusinessStatus.Init);
		
		// Step_03 Mock请求：设备同步
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_04 数据库检查：设备卡片信息
		SqlSession session = DAOWrapper.getSession();
		DeviceMapper mapper = session.getMapper(DeviceMapper.class);
		Device deviceInDB = mapper.selectByDeviceSn(deviceWrapper.getDeviceSn());
		
		Devicecard deviceCardInDB = deviceInDB.getDeviceCard();
		assertEquals(mockApp.getAppVersion(), deviceCardInDB.getAppVersion());
		assertEquals(mockApp.getDeviceModel(), deviceCardInDB.getDeviceModel());
		assertEquals(OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDeviceSn(), deviceInDB.getDeviceSn());
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
		assertTrue(impressCardInDB.getImpressLabelMapSet().size() == Consts.SolidAssessLabel.values().length - 1);
		
		// Step_07 调用：DeviceWrapper::getBusinessStatus
		businessStatus = deviceWrapper.getBusinessStatus();
		assertEquals(businessStatus, Consts.BusinessStatus.Idle);
		
		// Step_08 调用：OnlineDevicePool::getCount
		String deviceSn = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDeviceSn();
		assertTrue(pool.getDevice(deviceSn) != null);
		
		// Step_09 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime();
		
		// Step_10 Mock事件：onClose
		mockApp.disconnect();
		
		// Step_11 调用：OnlineDevicePool::getCount
		assertTrue(pool.getDevice(OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDeviceSn()) == null);
		
		// Step_12 建立WebSocket连接
		deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn());
		
		// Step_13 Mock请求：设备同步
		mockApp.enterPool(businessType);
		assertTrue(mockApp.lastReceivedCommandIsError());		//设备同步之前不能使用其他命令
		
		mockApp.syncDevice();
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_14 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime() > lastActivityTime);
		
		// Step_15 Mock请求：进入随机聊天
		mockApp.enterPool(businessType);
		assertTrue(!mockApp.lastReceivedCommandIsError());
		
		// Step_16 Mock请求：设备同步
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_17 Mock事件：onClose
		mockApp.disconnect();
		
		// Step_18 建立WebSocket连接
		deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn());
		
		// Step_19 停止数据库服务
		DBModule.instance.stopService();
		
		// Step_20 Mock请求：设备同步
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		// fail("需要检查此时应该收到系统故障，操作失败");
	}
}
