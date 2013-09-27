/**
 * AbstractTestCase.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.DeviceDAO;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public abstract class AbstractTestCase extends TestCase
{
	protected Logger logger = LoggerFactory.getLogger(AbstractTestCase.class);
	protected final String demoDeviceSn = "demoDeviceSn"; 
	protected final String demoDeviceSn2 = "demoDeviceSn2";
	
	public AbstractTestCase()
	{
		
	}
	
	private Device loadDevice(String deviceSn)
	{
		DeviceDAO deviceDAO = new DeviceDAO();
		List<Device> cardList = deviceDAO.findByDeviceSn(deviceSn);
		if (cardList.size() == 0 )
		{
			return null;
		}

		Device device = cardList.get(0);
		return device;
	}

	
	/**
	 * Create new device in pool, and bind with mock App with random device SN
	 */
	public LocalMockApp createNewMockApp(String deviceSn)
	{
		if (deviceSn == null || deviceSn.length() == 0)
		{
			deviceSn = CommonFunctions.getRandomString(24);
		}
		
		MockWebSocketConnection conn = new MockWebSocketConnection();
		LocalMockApp mockApp = new LocalMockApp(conn);
		
		Device device = loadDevice(deviceSn);
		if (device == null)
		{
			mockApp.stopTimer();
			device =  mockApp.createNewDevice(deviceSn);	
		}
		
		// Bind DeviceWrapper with Device
		
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = pool.newDevice(conn);
		conn.bind(deviceWrapper);
		
		if (deviceWrapper == null)
		{
			return null;
		}
		
		deviceWrapper.setDevice(device);
		mockApp.bindDeviceWrapper(deviceWrapper);
		return mockApp;
	}
	
	protected void deleteDevice(LocalMockApp mockApp)
	{
		mockApp.pingTimer.cancel();
		OnlineDevicePool pool = OnlineDevicePool.instance;
		pool.deleteDevice(mockApp.getDeviceWrapper());
	}
	
	protected MockWebSocketConnection getMockWebSocket(IDeviceWrapper deviceWrapper)
	{
		IDeviceWrapper connectionOwner1 = (IDeviceWrapper) deviceWrapper;
		
		assertTrue(connectionOwner1.getConnection() instanceof MockWebSocketConnection);
		MockWebSocketConnection connection = (MockWebSocketConnection) connectionOwner1.getConnection();
		return connection;
	}

}
