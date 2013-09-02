/**
 * Test04SyncDeviceNormal.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.business.device.Device;
import com.simplelife.renhai.server.business.device.DeviceCard;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.json.JSONKey;
import com.simplelife.renhai.server.util.DeviceServiceStatus;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IDeviceWrapper;

import junit.framework.TestCase;

/**
 * 
 */
public class Test04SyncDeviceNormal extends TestCase
{
	private IDeviceWrapper deviceWrapper;
	private IBaseConnectionOwner connectionOwner;
	private LocalMockApp mockApp;
	private DeviceCard deviceCard;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		newDevice();
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		pool.releaseDevice(deviceWrapper);
	}
	
	/**
	 * Create new device in pool, and bind with mock App
	 */
	private void newDevice()
	{
		deviceWrapper = Test00Common.createDeviceWrapper();
		assertTrue(deviceWrapper instanceof IBaseConnectionOwner);
		connectionOwner = (IBaseConnectionOwner) deviceWrapper;
		
		Device device = deviceWrapper.getDevice();
		deviceCard = device.getDeviceCard();
		Test00Common.setDefaultDeviceCard(deviceCard);
		
		mockApp = new LocalMockApp();
		mockApp.bindDevice(deviceWrapper);
	}
	
	/**
	 * Mock request of AppDataSyncRequest
	 */
	private void syncDevice()
	{
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(JSONKey.FieldName.DeviceCard, new HashMap<String, Object> ());
		
		HashMap<String, Object> impressCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "10");
		queryMap.put(JSONKey.FieldName.ImpressCard, impressCardMap);
		
		HashMap<String, Object> interestCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "5");
		queryMap.put(JSONKey.FieldName.InterestCard, interestCardMap);
		
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		
		HashMap<String, Object> deviceCardMap = new HashMap<String, Object>();
		DeviceCard card = this.deviceWrapper.getDevice().getDeviceCard();
		
		deviceCardMap.put(JSONKey.FieldName.OsVersion, card.getOsVersion());
		deviceCardMap.put(JSONKey.FieldName.AppVersion, card.getAppVersion());
		deviceCardMap.put(JSONKey.FieldName.IsJailed, card.isJailed()? "Yes" : "No");
		deviceCardMap.put(JSONKey.FieldName.Location, card.getLocation());
		deviceCardMap.put(JSONKey.FieldName.DeviceSn, card.getDeviceSn());
		deviceCardMap.put(JSONKey.FieldName.DeviceModel, card.getDeviceModel());
		updateMap.put(JSONKey.FieldName.DeviceCard, deviceCardMap);
		
		mockApp.sendAppDataSyncRequest(queryMap, updateMap);
		
		// Step_04 数据库检查：设备卡片信息
		String sql = "select * from DeviceCard where deviceSn = '" + deviceCard.getDeviceSn() + "";
		List<DeviceCard> deviceList = DAOWrapper.query(sql, DeviceCard.class);
		assertEquals(1, deviceList.size());
		DeviceCard cardInDB = deviceList.get(0);
		
		assertEquals(Test00Common.AppVersion, cardInDB.getAppVersion());
		assertEquals(Test00Common.DeviceModel, cardInDB.getDeviceModel());
		assertEquals(Test00Common.DeviceSN, cardInDB.getDeviceSn());
		assertEquals(Test00Common.IsJailed, cardInDB.isJailed());
		assertEquals(Test00Common.Location, cardInDB.getLocation());
		assertEquals(Test00Common.OSVersion, cardInDB.getOsVersion());
	}
	
	/**
	 * Test function
	 */
	@Test
	public void test()
	{
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		
		// Step_01 调用：OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		long lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_02 调用：DeviceWrapper::getServiceStatus
		DeviceServiceStatus serviceStatus = deviceWrapper.getServiceStatus();
		assertEquals(serviceStatus, DeviceServiceStatus.Init);
		
		// Step_03 Mock请求：设备同步
		syncDevice();
		
		// Step_05 数据库检查：兴趣卡片信息
		fail("数据库检查待实现");
		
		// Step_06 数据库检查：印象卡片信息
		fail("数据库检查待实现");
		
		// Step_07 调用：DeviceWrapper::getServiceStatus
		serviceStatus = deviceWrapper.getServiceStatus();
		assertEquals(serviceStatus, DeviceServiceStatus.Idle);
		
		// Step_08 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount + 1, pool.getElementCount());
		
		// Step_09 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
		
		// Step_10 Mock事件：onClose
		connectionOwner.getConnection().onClose();
		
		// Step_11 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getElementCount());
		
		// Step_12 建立WebSocket连接
		newDevice();
		
		// Step_13 Mock请求：设备同步
		syncDevice();
		
		// Step_14 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivityTime);
		
		// Step_15 Mock请求：进入随机聊天
		mockApp.enterPool(JSONKey.BusinessType.Random);
		
		// Step_16 Mock请求：设备同步
		syncDevice();
		fail("需要检查收到的消息是否正确");
		
		// Step_17 Mock事件：onClose
		connectionOwner.getConnection().onClose();
		
		// Step_18 建立WebSocket连接
		newDevice();
		
		// Step_19 停止数据库服务
		DBModule.instance().stopService();
		
		// Step_20 Mock请求：设备同步
		syncDevice();
		fail("需要检查此时应该收到系统故障，操作失败");
	}
}
