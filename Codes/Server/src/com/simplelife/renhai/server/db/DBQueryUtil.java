/**
 * DeviceQueryUtil.java
 * 
 * History:
 *     2013-9-12: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.db;

import java.util.List;

import org.slf4j.Logger;

import com.simplelife.renhai.server.util.Consts.DBExistResult;

/**
 * 
 */
public class DBQueryUtil
{
	public static boolean isNewDevice(String deviceSn)
	{
		String sql = "select * from " + TableName.Device 
				+ " where " + TableColumnName.DeviceSn + " = '" + deviceSn + "'";
		
		if (DAOWrapper.exists(sql) == DBExistResult.Existent)
		{
			return false;
		}
		return true;
	}
	
	public static Globalimpresslabel getGlobalimpresslabel(String label)
	{
		String sql = "select * from " + TableName.GlobalImpressLabel
				+ " where " + TableColumnName.ImpressLabelName + " = '" + label + "'";
		
		List<Globalimpresslabel> list = DAOWrapper.query(sql, Globalimpresslabel.class);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}
	
	public static Globalinterestlabel getGlobalinterestlabel(String label)
	{
		String sql = "select * from " + TableName.GlobalInterestLabel
				+ " where " + TableColumnName.InterestLabelName + " = '" + label + "'";
		
		List<Globalinterestlabel> list = DAOWrapper.query(sql, Globalinterestlabel.class);
		if (list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}
	
	public static String getIDByDeviceSn(String idColumnName, String deviceSn)
	{
		Logger logger = DBModule.instance.getLogger();
		
		DeviceDAO deviceDAO = new DeviceDAO();
		List<Device> deviceList = deviceDAO.findByDeviceSn(deviceSn);
		if (deviceList.size() == 0)
		{
			logger.warn("Device SN can't be found in DB: {}", deviceSn);
			return null;
		}

		Device device = deviceList.get(0);
		if (idColumnName.equals(TableColumnName.DeviceId))
		{
			return device.getDeviceId().toString();
		}
		
		if (idColumnName.equals(TableColumnName.DeviceCardId))
		{
			return device.getDevicecard().getDeviceCardId().toString();
		}
		
		if (idColumnName.equals(TableColumnName.ProfileId))
		{
			return device.getProfile().getProfileId().toString();
		}
		
		if (idColumnName.equals(TableColumnName.InterestCardId))
		{
			return device.getProfile().getInterestcard().getInterestCardId().toString();
		}
		
		if (idColumnName.equals(TableColumnName.ImpressCardId))
		{
			return device.getProfile().getImpresscard().getImpressCardId().toString();
		}
		
		return null;
	}
}
