/**
 * Test00Common.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import com.simplelife.renhai.server.business.device.DeviceCard;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test00Common
{
	public final static String OSVersion = "iOS 6.1.2";
	public final static String AppVersion = "0.1";
	public final static boolean IsJailed = false;
	public final static String Location = "22.511962,113.380301";
	public final static String DeviceSN = "fjdskafjdksla;juijkl;a43243211dadfs";
	public final static String DeviceModel = "iPhone5";
	
	public static IDeviceWrapper createDeviceWrapper()
	{
		MockWebSocketConnection conn = new MockWebSocketConnection();
		OnlineDevicePool pool = OnlineDevicePool.getInstance();
		IDeviceWrapper deviceWrapper = pool.newDevice(conn);
		return deviceWrapper;
	}
	
	public static void setDefaultDeviceCard(DeviceCard card)
	{
		card.setOsVersion(OSVersion);
		card.setAppVersion(AppVersion);
		card.setJailed(IsJailed);
		card.setLocation(Location);
		card.setDeviceSn(DeviceSN);
		card.setDeviceModel(DeviceModel);
	}
}
