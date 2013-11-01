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
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.log.FileLogger;

/**
 * Utility class for DB query
 */
public class DBQueryUtil
{
	private static Logger logger = BusinessModule.instance.getLogger();
	/**
	 * Check if given deviceSn is new device in DB
	 * @param deviceSn: SN of device
	 * @return: return true if it's new device, else return false 
	 */
	public static boolean isNewDevice(String deviceSn)
	{
		Device device = DAOWrapper.getDeviceInCache(deviceSn, false);
		if (device != null)
		{
			logger.debug("Device is in DB cache");
			return false;
		}
		
		if (null != OnlineDevicePool.instance.getDevice(deviceSn))
		{
			logger.debug("Device is in OnlineDevicePool");
			return false;
		}
		
		DeviceDAO dao = new DeviceDAO();
		List<Device> list = null;
		try
		{
			list = dao.findByDeviceSn(deviceSn);
		}
		catch(RuntimeException re)
		{
			FileLogger.printStackTrace(re);
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
		
		return (list.isEmpty());
	}
	
	/**
	 * Get object of globalimpresslabel by given impress label
	 * @param label: impress label, such as ��˧�硱
	 * @return Return Object of globalimpresslabel if label existed, else return null 
	 */
	public static Globalimpresslabel getGlobalimpresslabel(String labelName)
	{
		Globalimpresslabel label = DAOWrapper.getImpressLabelInCache(labelName);
		if (label != null)
		{
			return label;
		}
		
		GlobalimpresslabelDAO dao = new GlobalimpresslabelDAO();
		List<Globalimpresslabel> list = null;
		try
		{
			list = dao.findByImpressLabelName(labelName);
		}
		catch(Exception re)
		{
			// try again
			try
			{
				list = dao.findByImpressLabelName(labelName);
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
	 * @param label: interest label, such as "����"
	 * @return Return Object of globainterestlabel if label existed, else return null
	 */
	public static Globalinterestlabel getGlobalinterestlabel(String labelName)
	{
		Globalinterestlabel label = DAOWrapper.getInterestLabelInCache(labelName);
		if (label != null)
		{
			return label;
		}
		
		GlobalinterestlabelDAO dao = new GlobalinterestlabelDAO();
		List<Globalinterestlabel> list = null;
		try
		{
			list = dao.findByInterestLabelName(labelName);
		}
		catch(RuntimeException re)
		{
			// try again
			try
			{
				list = dao.findByInterestLabelName(labelName);
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
