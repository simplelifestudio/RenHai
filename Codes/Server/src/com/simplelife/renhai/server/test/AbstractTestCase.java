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

import junit.framework.TestCase;

import com.simplelife.renhai.server.business.device.DeviceCard;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
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
	public final static boolean IsJailed = false;
	public final static String Location = "22.511962,113.380301";
	public final static String DeviceSN = "fjdskafjdksla;juijkl;a43243211dadfs";
	public final static String DeviceModel = "iPhone5";
	
	/**
	 * Create new device in pool, and bind with mock App
	 */
	public LocalMockApp createMockApp()
	{
		MockWebSocketConnection conn = new MockWebSocketConnection();
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		IDeviceWrapper deviceWrapper = pool.newDevice(conn);
		
		LocalMockApp mockApp = new LocalMockApp();
		mockApp.bindDeviceWrapper(deviceWrapper);
		return mockApp;
	}
	
	public void setDeviceCardDefaultValue(DeviceCard card)
	{
		card.setOsVersion(OSVersion);
		card.setAppVersion(AppVersion);
		card.setJailed(IsJailed);
		card.setLocation(Location);
		card.setDeviceSn(DeviceSN);
		card.setDeviceModel(DeviceModel);
	}
	
	/**
	 * Mock request of AppDataSyncRequest
	 */
	protected void syncDevice(LocalMockApp mockApp)
	{
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(JSONKey.FieldName.DeviceCard, new HashMap<String, Object>());
		
		HashMap<String, Object> impressCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "10");
		queryMap.put(JSONKey.FieldName.ImpressCard, impressCardMap);
		
		HashMap<String, Object> interestCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "5");
		queryMap.put(JSONKey.FieldName.InterestCard, interestCardMap);
		
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		
		HashMap<String, Object> deviceCardMap = new HashMap<String, Object>();
		DeviceCard card = mockApp.getDeviceWrapper().getDevice().getDeviceCard();
		
		deviceCardMap.put(JSONKey.FieldName.OsVersion, card.getOsVersion());
		deviceCardMap.put(JSONKey.FieldName.AppVersion, card.getAppVersion());
		deviceCardMap.put(JSONKey.FieldName.IsJailed, card.isJailed() ? "Yes" : "No");
		deviceCardMap.put(JSONKey.FieldName.Location, card.getLocation());
		deviceCardMap.put(JSONKey.FieldName.DeviceSn, card.getDeviceSn());
		deviceCardMap.put(JSONKey.FieldName.DeviceModel, card.getDeviceModel());
		updateMap.put(JSONKey.FieldName.DeviceCard, deviceCardMap);
		
		mockApp.sendAppDataSyncRequest(queryMap, updateMap);
	}
	
	protected void deleteDevice(LocalMockApp mockApp)
	{
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
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
