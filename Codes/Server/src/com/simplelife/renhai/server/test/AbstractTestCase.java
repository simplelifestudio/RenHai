/**
 * AbstractTestCase.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public abstract class AbstractTestCase extends TestCase
{
	protected Logger logger = LoggerFactory.getLogger(AbstractTestCase.class);
	private final String demoDeviceSn = "demoDeviceSn"; 
	
	public AbstractTestCase()
	{
		
	}
	
	/**
	 * Create new device in pool, and bind with mock app with SN: demoDeviceSn
	 * @return
	 */
	public LocalMockApp createDemoMockApp()
	{
		LocalMockApp app = createNewMockApp();
		app.getDeviceWrapper().getDevice().setDeviceSn(demoDeviceSn);
		return app;
	}
	
	/**
	 * Create new device in pool, and bind with mock App with random device SN
	 */
	public LocalMockApp createNewMockApp()
	{
		// Create new impress card
		Impresscard impressCard = new Impresscard();
		impressCard.setChatLossCount(0);
		impressCard.setChatTotalCount(0);
		impressCard.setChatTotalDuration(0);
		
		// Create new interest card
		Interestcard interestCard = new Interestcard();
		
		// Create new profile
		Profile profile = new Profile();
		long now = System.currentTimeMillis();
		profile.setLastActivityTime(now);
		profile.setCreateTime(now);
		profile.setServiceStatus(Consts.ServiceStatus.Normal.name());
		
		// Bind profile with cards
		interestCard.setProfile(profile);
		impressCard.setProfile(profile);
		
		// Create new deviceCard
		Devicecard deviceCard = new Devicecard();
		deviceCard.setRegisterTime(now);
		
		// Create Device object and bind with cards
		Device device = new Device();
		device.setDevicecard(deviceCard);
		device.setProfile(profile);
		
		// Bind DeviceWrapper with Device
		MockWebSocketConnection conn = new MockWebSocketConnection();
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = pool.newDevice(conn);
		
		if (deviceWrapper == null)
		{
			return null;
		}
		
		deviceWrapper.setDevice(device);
		LocalMockApp mockApp = new LocalMockApp(conn);
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
