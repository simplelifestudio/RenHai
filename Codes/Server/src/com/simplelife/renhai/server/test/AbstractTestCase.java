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
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IMockConnection;

/**
 * 
 */
public abstract class AbstractTestCase extends TestCase
{
	protected Logger logger = LoggerFactory.getLogger(AbstractTestCase.class);
	protected final String demoDeviceSn = "demoDeviceSn"; 
	protected final String demoDeviceSn2 = "demoDeviceSn2";
	protected Consts.BusinessType businessType = Consts.BusinessType.Interest;
	
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

	public Device createNewDevice(String deviceSn)
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
		deviceCard.setOsVersion(MockApp.OSVersion);
		deviceCard.setAppVersion(MockApp.AppVersion);
		deviceCard.setIsJailed(Consts.YesNo.No.toString());
		deviceCard.setLocation(MockApp.Location);
		deviceCard.setDeviceModel(MockApp.DeviceModel);
		
		// Create Device object and bind with cards
		Device device = new Device();
		
		device.setDevicecard(deviceCard);
		deviceCard.setDevice(device);
		
		device.setProfile(profile);
		profile.setDevice(device);
		
		device.setDeviceSn(deviceSn);
		
		//DBModule.instance.cache(device);
		return device;
	}
	
	/**
	 * Create new device in pool, and bind with mock App with random device SN
	 */
	public MockApp createNewMockApp(String deviceSn)
	{
		if (deviceSn == null || deviceSn.length() == 0)
		{
			deviceSn = CommonFunctions.getRandomString(24);
		}
		
		Device device = loadDevice(deviceSn);
		if (device == null)
		{
			device =  createNewDevice(deviceSn);
		}
		
		MockWebSocketConnection conn = new MockWebSocketConnection();
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = pool.newDevice(conn);
		if (deviceWrapper == null)
		{
			return null;
		}
		deviceWrapper.setDevice(device);
		
		MockApp mockApp = new MockApp(conn);
		mockApp.bindDeviceWrapper(deviceWrapper);
		return mockApp;
	}
	
	protected void deleteDevice(MockApp mockApp)
	{
		mockApp.pingTimer.cancel();
		OnlineDevicePool pool = OnlineDevicePool.instance;
		mockApp.close();
	}
	
	protected MockWebSocketConnection getMockWebSocket(IDeviceWrapper deviceWrapper)
	{
		IDeviceWrapper connectionOwner1 = (IDeviceWrapper) deviceWrapper;
		
		assertTrue(connectionOwner1.getConnection() instanceof MockWebSocketConnection);
		MockWebSocketConnection connection = (MockWebSocketConnection) connectionOwner1.getConnection();
		return connection;
	}

}
