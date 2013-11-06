/**
 * DeviceQueryUtil.java
 * 
 * History:
 *     2013-9-12: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.db;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
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
		
		try
		{
			device = DBModule.instance.deviceCache.getObject(deviceSn);
		}
		catch(RuntimeException re)
		{
			FileLogger.printStackTrace(re);
		}
		
		return (device == null);
	}
	
	public static Globalimpresslabel getGlobalimpresslabel(Integer labelId)
	{
		Globalimpresslabel label = DAOWrapper.getImpressLabelInCache(labelId);
		if (label != null)
		{
			return label;
		}
		
		SqlSession session = DAOWrapper.getSession();
		GlobalimpresslabelMapper mapper = session.getMapper(GlobalimpresslabelMapper.class);
		
		try
		{
			label = mapper.selectByPrimaryKey(labelId);
		}
		catch(Exception re)
		{
			FileLogger.printStackTrace(re);
			// try again
			try
			{
				label = mapper.selectByPrimaryKey(labelId);
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		finally
		{
			session.close();
		}
		
		return label;
	}
	
	public static Globalinterestlabel getGlobalinterestlabel(Integer labelId)
	{
		Globalinterestlabel label = DAOWrapper.getInterestLabelInCache(labelId);
		if (label != null)
		{
			return label;
		}
		
		SqlSession session = DAOWrapper.getSession();
		GlobalinterestlabelMapper mapper = session.getMapper(GlobalinterestlabelMapper.class);
		try
		{
			label = mapper.selectByPrimaryKey(labelId);
		}
		catch(RuntimeException re)
		{
			FileLogger.printStackTrace(re);
			// try again
			try
			{
				label = mapper.selectByPrimaryKey(labelId);
			}
			catch(RuntimeException e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		finally
		{
			session.close();
		}
		
		return label;
	}
}
