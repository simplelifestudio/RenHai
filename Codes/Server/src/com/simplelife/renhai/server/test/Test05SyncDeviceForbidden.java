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

import com.simplelife.renhai.server.business.device.DeviceCard;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.TableColumnName;
import com.simplelife.renhai.server.db.TableName;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * TC_05 �����豸ͬ��_�����豸
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
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		IDeviceWrapper deviceWrapper = mockApp.getDeviceWrapper();
		DeviceCard deviceCard = deviceWrapper.getDevice().getDeviceCard();
		
		// Step_01 ���ݿ���������豸A�ķ���״̬����Ϊ���ģ���������Ϊ����
		String sql = "update " + TableName.DeviceCard 
				+ " set " + TableColumnName.ServiceStatus + " = 1, "
				+ TableColumnName.ForbiddenExpiredDate + " = " + DateUtil.getDateByDayBack(-1)
				+ " where " + TableColumnName.DeviceSn + " = '" + deviceCard.getDeviceSn() + "'";
		DAOWrapper.executeSql(sql);
		
		// Step_02 ���ã�OnlineDevicePool::getCount
		int deviceCount = pool.getElementCount();
		
		// Step_03 ���ã�DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceBusinessStatus.Init, deviceWrapper.getBusinessStatus());
		
		// Step_04 Mock�����豸ͬ��
		long lastActivity = deviceWrapper.getLastActivityTime().getTime();
		syncDevice(mockApp);
		
		// Step_05 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount - 1, pool.getElementCount());
		
		// Step_06 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime().getTime() > lastActivity);
		assertEquals(Consts.DeviceBusinessStatus.Init, deviceWrapper.getBusinessStatus());
	}
}
