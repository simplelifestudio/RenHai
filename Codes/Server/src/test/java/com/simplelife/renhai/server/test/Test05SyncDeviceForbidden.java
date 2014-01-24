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

/**
 * TC_05 �����豸ͬ��_�����豸
 */
public class Test05SyncDeviceForbidden extends AbstractTestCase
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
		mockApp = createNewMockApp(demoDeviceSn);
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

	
	@Test
	public void test() throws InterruptedException
	{
		/*
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = pool.getDevice(mockApp.getConnectionId());
		
		Device device = pool.getDevice(mockApp.getDeviceSn()).getDevice();
		String.valueOf(device.getDeviceId()); 
		
		// Step_01 ���ݿ���������豸A�ķ���״̬����Ϊ���ģ���������Ϊ����
		SqlSession session = DAOWrapper.instance.getSession();
		ProfileMapper mapper = session.getMapper(ProfileMapper.class);
		Profile profile = deviceWrapper.getDevice().getProfile(); 
		profile.setServiceStatus(Consts.ServiceStatus.Banned.name());
		profile.setUnbanDate(DateUtil.getDateByDayBack(-365).getTime());
		//session.update(arg0);
		profile.save(session);
		
		// Step_02 ���ã�OnlineDevicePool::getCount
		//int deviceCount = pool.getElementCount();
		
		// Step_03 ���ã�DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.Connected, deviceWrapper.getBusinessStatus());
		
		// Step_04 Mock�����豸ͬ��
		long lastActivity = deviceWrapper.getLastActivityTime();
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.ServerErrorResponse, null));
		
		// Step_05 ���ã�OnlineDevicePool::getCount
		//assertEquals(deviceCount - 1, pool.getElementCount());
		assertTrue(pool.getDevice(mockApp.getDeviceSn()) == null);
		
		// Step_06 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime() > lastActivity);
		assertEquals(Consts.DeviceStatus.Connected, deviceWrapper.getBusinessStatus());
		
		profile.setServiceStatus(Consts.ServiceStatus.Normal.name());
		profile.setUnbanDate(null);
		mapper.insert(profile);
		*/
	}
}
