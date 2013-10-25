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

import com.simplelife.renhai.server.log.FileLogger;

/**
 * Utility class for DB query
 */
public class DBQueryUtil
{
	/**
	 * Check if given deviceSn is new device in DB
	 * @param deviceSn: SN of device
	 * @return: return true if it's new device, else return false 
	 */
	public static boolean isNewDevice(String deviceSn)
	{
		DeviceDAO dao = new DeviceDAO();
		List<Device> list = null;
		try
		{
			list = dao.findByDeviceSn(deviceSn);
		}
		catch(RuntimeException re)
		{
			// try again
			try
			{
				list = dao.findByDeviceSn(deviceSn);
			}
			catch(RuntimeException e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		
		return (list.size() == 0);
	}
	
	/**
	 * Get object of globalimpresslabel by given impress label
	 * @param label: impress label, such as ¡°Ë§¸ç¡±
	 * @return Return Object of globalimpresslabel if label existed, else return null 
	 */
	public static Globalimpresslabel getGlobalimpresslabel(String label)
	{
		GlobalimpresslabelDAO dao = new GlobalimpresslabelDAO();
		List<Globalimpresslabel> list = null;
		try
		{
			list = dao.findByImpressLabelName(label);
		}
		catch(Exception re)
		{
			// try again
			try
			{
				list = dao.findByImpressLabelName(label);
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * Get object of globainterestlabel by given interest label
	 * @param label: interest label, such as "×ãÇò"
	 * @return Return Object of globainterestlabel if label existed, else return null
	 */
	public static Globalinterestlabel getGlobalinterestlabel(String label)
	{
		GlobalinterestlabelDAO dao = new GlobalinterestlabelDAO();
		List<Globalinterestlabel> list = null;
		try
		{
			list = dao.findByInterestLabelName(label);
		}
		catch(RuntimeException re)
		{
			// try again
			try
			{
				list = dao.findByInterestLabelName(label);
			}
			catch(RuntimeException e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * Return different ID by given deviceSn
	 * @param idColumnName: name of ID column, such as deviceId 
	 * @param deviceSn: SN of device, it's unique for each device
	 * @return value of ID column
	 */
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
