/**
 * Test06SyncDeviceUnForbidden.java
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

import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.TableColumnName;
import com.simplelife.renhai.server.db.TableName;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/**
 * 
 */
public class Test06SyncDeviceUnForbidden extends AbstractTestCase
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
	
	@Test
	public void test()
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = mockApp.getDeviceWrapper();
		Devicecard deviceCard = deviceWrapper.getDevice().getDevicecard();
		
		// Step_01 数据库操作：将设备A的服务状态更新为禁聊，到期日期为昨天
		String sql = "update " + TableName.Devicecard 
				+ " set " + TableColumnName.ServiceStatus + " = 1, "
				+ TableColumnName.ForbiddenExpiredDate + " = " + DateUtil.getDateByDayBack(1)
				+ " where " + TableColumnName.DeviceSn + " = '" + deviceCard.getDeviceSn() + "'";
		DAOWrapper.executeSql(sql);
		
		// Step_02 调用：OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		
		// Step_03 调用：DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Init, deviceWrapper.getBusinessStatus());
		
		// Step_04 Mock请求：设备同步
		
		long lastActivity = deviceWrapper.getLastActivityTime().getTime();
		syncDevice(mockApp);
		
		// Step_05 调用：A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Idle, deviceWrapper.getBusinessStatus());
		
		// Step_06 调用：OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getElementCount());
		
		// Step_06 调用：DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivity);
	}
}
