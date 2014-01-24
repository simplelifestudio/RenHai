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


/**
 * 
 */
public class Test06SyncDeviceUnForbidden extends AbstractTestCase
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
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn());
		
		Device device = OnlineDevicePool.instance.getDevice(mockApp.getDeviceSn()).getDevice();
		String.valueOf(device.getDeviceId()); 
		
		// Step_01 ���ݿ���������豸A�ķ���״̬����Ϊ���ģ���������Ϊ����
		SqlSession session = DAOWrapper.instance.getSession();
		ProfileMapper mapper = session.getMapper(ProfileMapper.class);
		Profile profile = deviceWrapper.getDevice().getProfile(); 
		profile.setServiceStatus(Consts.ServiceStatus.Banned.name());
		profile.setUnbanDate(DateUtil.getDateByDayBack(1).getTime());
		mapper.insert(profile);
		
		// Step_02 ���ã�OnlineDevicePool::getCount
		int deviceCount = pool.getDeviceCount();
		
		// Step_03 ���ã�DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.Connected, deviceWrapper.getBusinessStatus());
		
		// Step_04 Mock�����豸ͬ��
		
		long lastActivity = deviceWrapper.getLastActivityTime();
		mockApp.syncDevice();
		assertTrue(mockApp.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		// Step_05 ���ã�A DeviceWrapper::getBusinessStatus
		assertEquals(Consts.DeviceStatus.AppDataSynced, deviceWrapper.getBusinessStatus());
		
		// Step_06 ���ã�OnlineDevicePool::getCount
		assertEquals(deviceCount, pool.getDeviceCount());
		
		// Step_06 ���ã�DeviceWrapper::getLastActivityTime
		assertTrue(deviceWrapper.getLastActivityTime() > lastActivity);
		*/
	}
}
