/**
 * AbstractTestCase.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.HashMap;

import org.hibernate.Session;

import junit.framework.TestCase;

import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.DevicecardDAO;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.ImpresscardDAO;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.InterestcardDAO;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.ProfileDAO;
import com.simplelife.renhai.server.business.device.Device;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;

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
	
	/**
	 * Create new device in pool, and bind with mock App
	 */
	public LocalMockApp createMockApp()
	{
		// Create new impress card
		Impresscard impressCard = new Impresscard();
		ImpresscardDAO impressCardDAO = new ImpresscardDAO();
		impressCard.setChatLossCount(0);
		impressCard.setChatTotalCount(0);
		impressCard.setChatTotalDuration(0);
		
		// Create new interest card
		Interestcard interestCard = new Interestcard();
		InterestcardDAO interestCardDAO = new InterestcardDAO();
		interestCard.setCreateTime(DateUtil.getNowDate().getTime());
		
		// Create new profile
		Profile profile = new Profile();
		profile.setImpresscard(impressCard);
		profile.setInterestcard(interestCard);
		long now = DateUtil.getNowDate().getTime();
		profile.setLastActivityTime(now);
		profile.setCreateTime(now);
		
		// Bind profile with cards
		profile.setInterestcard(interestCard);
		profile.setImpresscard(impressCard);
		
		// Save profile
		ProfileDAO profileDAO = new ProfileDAO();
		
		// Create new deviceCard and update to data from APP
		Devicecard deviceCard = new Devicecard();
		setDevicecardDefaultValue(deviceCard);
		
		// Save device card
		DevicecardDAO deviceCardDAO = new DevicecardDAO();
		deviceCard.setProfile(profile);
		deviceCard.setRegisterTime(now);
		deviceCard.setServiceStatus(Consts.DeviceServiceStatus.Normal.name());
		
		// Create Device object and bind with cards
		Device device = new Device();
		device.setDevicecard(deviceCard);
		device.setProfile(profile);
		
		// Bind DeviceWrapper with Device
		MockWebSocketConnection conn = new MockWebSocketConnection("1111");
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
		card.setDeviceSn(DeviceSN);
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
