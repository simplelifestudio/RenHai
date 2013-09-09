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

import com.simplelife.renhai.server.db.Devicecard;
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
	public final static String IsJailed = "No";
	public final static String Location = "22.511962,113.380301";
	public final static String DeviceSN = "fjdskafjdksla;juijkl;a43243211dadfs";
	public final static String DeviceModel = "iPhone5";
	
	/**
	 * Create new device in pool, and bind with mock App
	 */
	public LocalMockApp createMockApp()
	{
		MockWebSocketConnection conn = new MockWebSocketConnection("1111");
		OnlineDevicePool pool = OnlineDevicePool.instance;
		IDeviceWrapper deviceWrapper = pool.newDevice(conn);
		
		LocalMockApp mockApp = new LocalMockApp();
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
	
	/**
	 * Mock request of AppDataSyncRequest
	 */
	protected void syncDevice(LocalMockApp mockApp)
	{
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(JSONKey.FieldName.Devicecard, new HashMap<String, Object>());
		
		HashMap<String, Object> impressCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "10");
		queryMap.put(JSONKey.FieldName.Impresscard, impressCardMap);
		
		HashMap<String, Object> interestCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "5");
		queryMap.put(JSONKey.FieldName.Interestcard, interestCardMap);
		
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		
		HashMap<String, Object> deviceCardMap = new HashMap<String, Object>();
		Devicecard card = mockApp.getDeviceWrapper().getDevice().getDevicecard();
		
		deviceCardMap.put(JSONKey.FieldName.OsVersion, card.getOsVersion());
		deviceCardMap.put(JSONKey.FieldName.AppVersion, card.getAppVersion());
		deviceCardMap.put(JSONKey.FieldName.IsJailed, card.getIsJailed());
		deviceCardMap.put(JSONKey.FieldName.Location, card.getLocation());
		deviceCardMap.put(JSONKey.FieldName.DeviceSn, card.getDeviceSn());
		deviceCardMap.put(JSONKey.FieldName.DeviceModel, card.getDeviceModel());
		updateMap.put(JSONKey.FieldName.Devicecard, deviceCardMap);
		
		mockApp.sendAppDataSyncRequest(queryMap, updateMap);
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
