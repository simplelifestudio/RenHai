/**
 * Test03TimeoutBeforeSyncDevice.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test03TimeoutBeforeSyncDevice extends AbstractTestCase
{
	private LocalMockApp mockApp;
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp);
	}
	
	@Test
	public void test()
	{
		// Step_01 ����MockWebSocketConnection����
		
		// Step_02 ���ã�OnlineDevicePool::newDevice
		mockApp = createMockApp();
		String connectionId = mockApp.getConnectionId();
		
		//String deviceSn = mockApp.getDeviceWrapper().getDeviceSn();
		IDeviceWrapper device = OnlineDevicePool.instance.getDevice(connectionId); 
		assertTrue(device != null);
		assertTrue(device.getOwnerOnlineDevicePool() == OnlineDevicePool.instance);
		
		// Step_02 �ȴ�Server��Websocketͨ���쳣ʱ��
		try
		{
			logger.debug("Wait for timeout, be patient...\n");
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection + 1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_03 Mock�¼���onPing
		logger.debug("Recover from sleep, start to check connection again.");
		device = OnlineDevicePool.instance.getDevice(connectionId); 
		assertTrue(device == null);
		//assertTrue(device.getOwnerOnlineDevicePool() == null);
		
		//assertTrue(OnlineDevicePool.instance.getDevice(deviceSn) == null);
		// Step_04 ����Websocket����
		
	}
}
