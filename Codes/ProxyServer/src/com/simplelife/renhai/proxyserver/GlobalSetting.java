/**
 * GlobalSetting.java
 * 
 * History:
 *     2013-11-17: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.proxyserver.Consts.ServiceStatus;

/**
 * 
 */
public class GlobalSetting
{
	public final static GlobalSetting instance = new GlobalSetting();
	public final static String EncryptKey = "20130801";
	
	private final String settingFileName = "setting.json";
	private long lastFileDate = 0;
	private Logger logger = LoggerFactory.getLogger(GlobalSetting.class);

	private ServiceStatus serviceStatus;
	private String ipAddress;
	private int port;
	private String path;
	private String protocol;
	
	private String timeZone;
	private String beginTime;
	private String endTime;
	private int encrypt;
	
	private Timer timer = new Timer();
	
	public String getProtocol()
	{
		return protocol;
	}
	
	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}
	
	public int getEncrypt()
	{
		return encrypt;
	}
	
	public void setEncrypt(int encrypt)
	{
		this.encrypt = encrypt;
	}
	
	public String getTimeZone()
	{
		return timeZone;
	}
	
	public void setTimeZone(String timeZone)
	{
		this.timeZone = timeZone;
	}
	
	/**
	 * @return the serviceStatus
	 */
	public ServiceStatus getServiceStatus()
	{
		return serviceStatus;
	}

	/**
	 * @param serviceStatus the serviceStatus to set
	 */
	public void setServiceStatus(ServiceStatus serviceStatus)
	{
		this.serviceStatus = serviceStatus;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress()
	{
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * @return the beginTime
	 */
	public String getBeginTime()
	{
		return beginTime;
	}

	/**
	 * @param beginTime the beginTime to set
	 */
	public void setBeginTime(String beginTime)
	{
		this.beginTime = beginTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime()
	{
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	
	private GlobalSetting()
	{
		serviceStatus = ServiceStatus.Normal;
		ipAddress = "192.81.135.31";
		port = 80;
		path = "/renhai/websocket";
		beginTime = "2013-01-01 00:00:00";
		endTime = "2013-01-01 00:00:00";
		timeZone = "GMT+0800";
		protocol = "ws";
		
		timer.scheduleAtFixedRate(new SettingCheckTask(), DateUtil.getNowDate(), 10000);
		logger.debug("timer of globalsetting started");
	}
	
	public void checkSettingFile()
	{
		File file = null;
		try
		{
			String fileName = CommonFunctions.getWebAppPath(settingFileName); 
			file = new File(fileName);
			long modifyDate = file.lastModified();
			//logger.debug("=============filename: "+fileName+", modifyDate: " + modifyDate + ", lastFileDate" + lastFileDate);
			if (lastFileDate != modifyDate)
			{
				logger.debug("Try to load modified setting file");
				lastFileDate = modifyDate;
				updateSetting(fileName);
			}
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
	}
	
	private void updateSetting(String fileName)
	{
		logger.debug("Start to update setting from file {}", fileName);
		String jsonStr = loadFromFile(fileName);
		
		JSONObject obj = null;
		try
		{
			obj = JSON.parseObject(jsonStr);
		}
		catch(Exception e)
		{
			logger.error("Fail to parse JSON string, check content of setting file: {}", this.settingFileName);
			FileLogger.printStackTrace(e);
			return;
		}
		
		if (obj == null)
		{
			return;
		}
		
		if (!checkJsonSetting(obj))
		{
			logger.error("Fail to parse JSON string, check content of setting file: {}", this.settingFileName);
			return;
		}
		
		int intValue = obj.getIntValue(JSONKey.ServiceStatus);
		setServiceStatus(ServiceStatus.parseValue(intValue));
		
		if (obj.containsKey(JSONKey.Encrypt))
		{
			intValue = obj.getIntValue(JSONKey.Encrypt);
			setEncrypt(intValue);
			//logger.debug("=============set encrypt as: " + intValue);
		}
		
		JSONObject tmpObj = obj.getJSONObject(JSONKey.ServiceAddress);
		String strValue = tmpObj.getString(JSONKey.Ip);
		setIpAddress(strValue);
		
		intValue = tmpObj.getIntValue(JSONKey.Port);
		setPort(intValue);
		
		strValue = tmpObj.getString(JSONKey.Path);
		setPath(strValue);
		
		strValue = tmpObj.getString(JSONKey.Protocol);
		setProtocol(strValue);
		
		tmpObj = obj.getJSONObject(JSONKey.StatusPeriod);
		strValue = tmpObj.getString(JSONKey.TimeZone);
		setTimeZone(strValue);
		
		strValue = tmpObj.getString(JSONKey.BeginTime);
		setBeginTime(strValue);
		
		strValue = tmpObj.getString(JSONKey.EndTime);
		setEndTime(strValue);
	}
	
	private boolean checkJsonSetting(JSONObject obj)
	{
		logger.debug("Start to check JSON setting");
		if (!obj.containsKey(JSONKey.ServiceStatus))
		{
			logger.error("Field {} is missed", JSONKey.ServiceStatus);
			return false;
		}
		try
		{
			int intValue = obj.getIntValue(JSONKey.ServiceStatus);
			if (ServiceStatus.parseValue(intValue) == null)
			{
				logger.error("Value of {} is invalid", JSONKey.ServiceStatus);
			}
		}
		catch(Exception e)
		{
			logger.error("Value of {} is invalid", JSONKey.ServiceStatus);
			return false;
		}
		
		
		if (!obj.containsKey(JSONKey.ServiceAddress))
		{
			logger.error("Field {} is missed", JSONKey.ServiceAddress);
			return false;
		}
		
		if (!obj.containsKey(JSONKey.StatusPeriod))
		{
			logger.error("Field {} is missed", JSONKey.StatusPeriod);
			return false;
		}
		
		JSONObject addressObj = obj.getJSONObject(JSONKey.ServiceAddress);
		if (!addressObj.containsKey(JSONKey.Protocol))
		{
			logger.error("Field {} is missed", JSONKey.Protocol);
			return false;
		}
		if (!addressObj.containsKey(JSONKey.Ip))
		{
			logger.error("Field {} is missed", JSONKey.Ip);
			return false;
		}
		if (!addressObj.containsKey(JSONKey.Port))
		{
			logger.error("Field {} is missed", JSONKey.Port);
			return false;
		}
		
		try
		{
			int intValue = obj.getIntValue(JSONKey.Port);
		}
		catch(Exception e)
		{
			logger.error("Value of {} is invalid", JSONKey.Port);
			return false;
		}
		
		if (!addressObj.containsKey(JSONKey.Path))
		{
			logger.error("Field {} is missed", JSONKey.Path);
			return false;
		}
		
		JSONObject statusObj = obj.getJSONObject(JSONKey.StatusPeriod);
		if (!statusObj.containsKey(JSONKey.TimeZone))
		{
			logger.error("Field {} is missed", JSONKey.TimeZone);
			return false;
		}
		
		if (!statusObj.containsKey(JSONKey.BeginTime))
		{
			logger.error("Field {} is missed", JSONKey.BeginTime);
			return false;
		}
		
		if (!statusObj.containsKey(JSONKey.EndTime))
		{
			logger.error("Field {} is missed", JSONKey.EndTime);
			return false;
		}
		
		return true;
	}
	
	private String loadFromFile(String fileName)
	{
		File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer strBuf = new StringBuffer();
        try 
        {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) 
            {
                strBuf.append(tempString);
            }
            reader.close();
        }
        catch (IOException e) 
        {
            FileLogger.printStackTrace(e);
        }
        finally 
        {
            if (reader != null) 
            {
                try 
                {
                    reader.close();
                }
                catch (IOException e1) 
                {
                }
            }
        }
        
        return strBuf.toString();
	}
	
	private class SettingCheckTask extends TimerTask
	{
		@Override
		public void run()
		{
			GlobalSetting.instance.checkSettingFile();
		}
		
	}
}
