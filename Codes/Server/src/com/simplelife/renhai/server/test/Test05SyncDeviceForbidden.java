/**
 * Test05SyncDeviceForbidden.java
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
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.TableColumnName;
import com.simplelife.renhai.server.db.TableName;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * TC_05 已有设备同步_禁聊设备
 */
public class Test05SyncDeviceForbidden extends AbstractTestCase
{
	private LocalMockApp mockApp;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp = createDemoMockApp();
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp);
	}

	
	@Test
	public void test()
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = mockApp.getDeviceWrapper();
		Devicecard deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		DeviceDAO dao = new DeviceDAO();
		Device device = dao.findByDeviceSn(deviceWrapper.getDeviceSn()).get(0);
		String deviceId = String.valueOf(device.getDeviceId()); 
		
		// Step_01 数据库操作：将设备A的服务状态更新为禁聊，到期日期为明年
		String sql = "update " + TableName.Profile 
				+ " set " + TableColumnName.ServiceStatus + " = '" + Consts.ServiceStatus.Banned.name() + "', "
				+ TableColumnName.UnbanDate + " = " + DateUtil.getDateByDayBack(-365).getTime()
				+ " where " + TableColumnName.DeviceId + " = " + deviceId;
		DAOWrapper.executeSql(sql);
		
		// Step_02 调用：OnlineDevicePool::getCount
		//int deviceCount = pool.getElementCount();
		
		// Step_03 调用：DeviceWrapper::getBusinessStatus
		assertEquals(Consts.BusinessStatus.Init, deviceWrapper.getBusinessStatus());
		
		// Step_04 Mock请求：设备同步
		long lastActivity = deviceWrapper.getLastActivityTime().getTime();
		mockApp.syncDevice();
		
		// Step_05 调用：OnlineDevicePool::getCount
		//assertEquals(deviceCount - 1, pool.getElementCount());
		assertTrue(pool.getDevice(mockApp.getDeviceWrapper().getDeviceSn()) == null);
		
		// Step_06 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivity);
		assertEquals(Consts.BusinessStatus.Init, deviceWrapper.getBusinessStatus());
	}
}
