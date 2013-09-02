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
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;

import junit.framework.TestCase;

/**
 * 
 */
public class Test03TimeoutBeforeSyncDevice extends TestCase
{
	private IDeviceWrapper mockDevice;
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@After
	public void tearDown() throws Exception
	{
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		pool.releaseDevice(mockDevice);
	}
	
	@Test
	public void test()
	{
		// Step_01 ����MockWebSocketConnection����
		MockWebSocketConnection conn = new MockWebSocketConnection();
		// Step_02 ���ã�OnlineDevicePool::newDevice
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		mockDevice = pool.newDevice(conn);
		
		// Step_02 �ȴ�Server��Websocketͨ���쳣ʱ��
		try
		{
			Thread.sleep(GlobalSetting.TimeOut.OnlineDeviceConnection * 1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_03 Mock�¼���onPing
		conn.onPing();
		Date lastPingTime = mockDevice.getLastPingTime();
		
		conn.onPing();
		assertEquals(lastPingTime, mockDevice.getLastPingTime());
		
		// Step_04 ����Websocket����
		
	}
}
