/**
 * AbstractTestCase.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import org.junit.internal.runners.TestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public abstract class AbstractTestCase extends TestCase
{
	public final static String OSVersion = "iOS 6.1.2";
	public final static String AppVersion = "0.1";
	public final static String IsJailed = "No";
	public final static String Location = "22.511962,113.380301";
	public final static String DeviceSN = "fjdskafjdksla;juijkl;a43243211dadfs";
	public final static String DeviceModel = "iPhone5";
	
	protected Logger logger = LoggerFactory.getLogger(AbstractTestCase.class);
	
	public AbstractTestCase()
	{
		
	}
	
	/**
	 * Create new device in pool, and bind with mock App
	 */
	public LocalMockApp createMockApp()
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
		setDevicecardDefaultValue(deviceCard);
		deviceCard.setRegisterTime(now);
		
		// Create Device object and bind with cards
		Device device = new Device();
		device.setDevicecard(deviceCard);
		device.setProfile(profile);
		
		// Bind DeviceWrapper with Device
		MockWebSocketConnection conn = new MockWebSocketConnection();
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = pool.newDevice(conn);
		deviceWrapper.setDevice(device);
		
		LocalMockApp mockApp = new LocalMockApp(conn);
		mockApp.bindDeviceWrapper(deviceWrapper);
		return mockApp;
	}
	
	public void setDevicecardDefaultValue(Devicecard card)
	{
		card.setOsVersion(OSVersion);
		card.setAppVersion(AppVersion);
		card.setIsJailed(IsJailed);
		card.setLocation(Location);
		card.setDeviceModel(DeviceModel);
	}
	
	protected void deleteDevice(LocalMockApp mockApp)
	{
		OnlineDevicePool pool = OnlineDevicePool.instance;
		pool.releaseDevice(mockApp.getDeviceWrapper());
	}
	
	protected MockWebSocketConnection getMockWebSocket(IDeviceWrapper deviceWrapper)
	{
		assertTrue(deviceWrapper instanceof IBaseConnectionOwner);
		IBaseConnectionOwner connectionOwner1 = (IBaseConnectionOwner) deviceWrapper;
		
		assertTrue(connectionOwner1.getConnection() instanceof MockWebSocketConnection);
		MockWebSocketConnection connection = (MockWebSocketConnection) connectionOwner1.getConnection();
		return connection;
	}

}
