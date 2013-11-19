/**
 * GlobalSetting.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.log.FileLogger;


/** */
public class GlobalSetting
{
	private static String settingFileName = "setting.json";
	private static long lastFileDate;
	private static Logger logger = BusinessModule.instance.getLogger();
	private static Timer timer = new Timer();
	
	private GlobalSetting()
	{
		
	}
	
	public static class TimeOut
	{
		public static int JSONMessageEcho = 9;
		public static int ChatConfirm = 15 * 1000;
		public static int Assess = 60;
		public static int CheckExpiredToken = 3600;
		
		public static int FlushCacheToDB = 30 * 1000;
		public static int DeviceInIdle = 300 * 1000;
		
		public static int OnlineDeviceConnection = 300 * 1000;
		public static int PingInterval = 5 * 1000;
		
		public static int SaveStatistics = 600 * 1000;
	}
	
	public static class DBSetting
	{
		public static int MaxRecordCountForFlush = 30;
		public static int MaxRecordCountForDiscard = 1000;
		
		public static int GlobalImpressLabelCacheCount = 1000;
		public static int GlobalInterestLabelCacheCount = 1000;
		public static int DeviceCacheCount = 10000;
	}
	
	public static class BusinessSetting
	{
		public static int OnlinePoolCapacity = 10000;
		public static int RandomBusinessPoolCapacity = 5000;
		public static int InterestBusinessPoolCapacity = 5000;
		public static int DefaultImpressLabelCount = 10;
		public static int HotInterestLabelCount = 10;
		
		public static int Encrypt = 1;
		public static String EncryptKey = "20130801";
		public static int LengthOfSessionId = 16;
		public static int LengthOfMessageSn = 16;
		
		public static int MaxImpressLabelCount = 32;
		public static int InputMessageHandleThreads = 200;
		public static int OutputMessageSendThreads = 500;
		public static int MessageQueueTime = 3 * 1000;
		
		public static int DelayOfSessionBound = 1000;
		
		public static int OpenTokKey = 34556802;
		public static String OpenTokSecret = "7a94109e525016628a92a1dcc392e5bdc0f27e7e";
		public static long OpenTokTokenExpiration = 2505600;			// Opentok has changed the expiration time to second instead of millisecond!
		public static long OpenTokTokenDuration = 7200000;				// Longest duration for using token (video chat)
	}

	public static void checkSettingFile()
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
	
	private static void updateSetting(String fileName)
	{
		String jsonStr = loadFromFile(fileName);
		
		JSONObject obj = null;
		try
		{
			obj = JSON.parseObject(jsonStr);
		}
		catch(Exception e)
		{
			logger.error("Fail to parse JSON string, check content of setting file: {}", settingFileName);
			FileLogger.printStackTrace(e);
			return;
		}
		
		if (obj == null)
		{
			return;
		}
		
		if (!checkJsonSetting(obj))
		{
			logger.error("Fail to parse JSON string, check content of setting file: {}", settingFileName);
			return;
		}
		
		logger.debug("Start to update setting from file {}", fileName);
		JSONObject tmpObj = obj.getJSONObject(SettingFieldName.TimeOut);
		TimeOut.JSONMessageEcho			= getIntValue(tmpObj, SettingFieldName.JSONMessageEcho);
		TimeOut.ChatConfirm				= getIntValue(tmpObj, SettingFieldName.ChatConfirm);
		TimeOut.Assess					= getIntValue(tmpObj, SettingFieldName.Assess);
		TimeOut.CheckExpiredToken		= getIntValue(tmpObj, SettingFieldName.CheckExpiredToken);
		TimeOut.FlushCacheToDB			= getIntValue(tmpObj, SettingFieldName.FlushCacheToDB);
		TimeOut.DeviceInIdle			= getIntValue(tmpObj, SettingFieldName.DeviceInIdle);
		TimeOut.OnlineDeviceConnection	= getIntValue(tmpObj, SettingFieldName.OnlineDeviceConnection);
		TimeOut.PingInterval			= getIntValue(tmpObj, SettingFieldName.PingInterval);
		TimeOut.SaveStatistics			= getIntValue(tmpObj, SettingFieldName.SaveStatistics);
		
		tmpObj = obj.getJSONObject(SettingFieldName.DBSetting);
		DBSetting.MaxRecordCountForFlush			= getIntValue(tmpObj, SettingFieldName.MaxRecordCountForFlush);
		DBSetting.MaxRecordCountForDiscard			= getIntValue(tmpObj, SettingFieldName.MaxRecordCountForDiscard);
		DBSetting.GlobalImpressLabelCacheCount		= getIntValue(tmpObj, SettingFieldName.GlobalImpressLabelCacheCount);
		DBSetting.GlobalInterestLabelCacheCount		= getIntValue(tmpObj, SettingFieldName.GlobalInterestLabelCacheCount);
		DBSetting.DeviceCacheCount					= getIntValue(tmpObj, SettingFieldName.DeviceCacheCount);
		
		tmpObj = obj.getJSONObject(SettingFieldName.BusinessSetting);
		BusinessSetting.OnlinePoolCapacity				= getIntValue(tmpObj, SettingFieldName.OnlinePoolCapacity);
		BusinessSetting.RandomBusinessPoolCapacity		= getIntValue(tmpObj, SettingFieldName.RandomBusinessPoolCapacity);
		BusinessSetting.InterestBusinessPoolCapacity	= getIntValue(tmpObj, SettingFieldName.InterestBusinessPoolCapacity);
		BusinessSetting.DefaultImpressLabelCount		= getIntValue(tmpObj, SettingFieldName.DefaultImpressLabelCount);
		BusinessSetting.HotInterestLabelCount			= getIntValue(tmpObj, SettingFieldName.HotInterestLabelCount);
		BusinessSetting.Encrypt							= getIntValue(tmpObj, SettingFieldName.Encrypt);
		BusinessSetting.EncryptKey						= getStringValue(tmpObj, SettingFieldName.EncryptKey);
		BusinessSetting.LengthOfSessionId				= getIntValue(tmpObj, SettingFieldName.LengthOfSessionId);
		BusinessSetting.LengthOfMessageSn				= getIntValue(tmpObj, SettingFieldName.LengthOfMessageSn);
		BusinessSetting.MaxImpressLabelCount			= getIntValue(tmpObj, SettingFieldName.MaxImpressLabelCount);
		BusinessSetting.InputMessageHandleThreads		= getIntValue(tmpObj, SettingFieldName.InputMessageHandleThreads);
		BusinessSetting.OutputMessageSendThreads		= getIntValue(tmpObj, SettingFieldName.OutputMessageSendThreads);
		BusinessSetting.MessageQueueTime				= getIntValue(tmpObj, SettingFieldName.MessageQueueTime);
		BusinessSetting.DelayOfSessionBound				= getIntValue(tmpObj, SettingFieldName.DelayOfSessionBound);
		BusinessSetting.OpenTokKey						= getIntValue(tmpObj, SettingFieldName.OpenTokKey);
		BusinessSetting.OpenTokSecret					= getStringValue(tmpObj, SettingFieldName.OpenTokSecret);
		BusinessSetting.OpenTokTokenExpiration			= getIntValue(tmpObj, SettingFieldName.OpenTokTokenExpiration);
		BusinessSetting.OpenTokTokenDuration			= getIntValue(tmpObj, SettingFieldName.OpenTokTokenDuration);
	}
	
	private static String getStringValue(JSONObject obj, String fieldName)
	{
		String value = null;
		try
		{
			value = obj.getString(fieldName);
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
		return value;
	}
	
	private static int getIntValue(JSONObject obj, String fieldName)
	{
		int value = -1;
		try
		{
			value = obj.getIntValue(fieldName);
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
		return value;
	}
	private static boolean checkJsonSetting(JSONObject obj)
	{
		logger.debug("Start to check JSON setting");
		if(!checkField(obj, SettingFieldName.TimeOut))
		{
			return false;
		}
		
		if(!checkField(obj, SettingFieldName.DBSetting))
		{
			return false;
		}
		
		if(!checkField(obj, SettingFieldName.BusinessSetting))
		{
			return false;
		}
		
		JSONObject tmpObj = obj.getJSONObject(SettingFieldName.TimeOut);
		if(!checkField(tmpObj, SettingFieldName.JSONMessageEcho))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.ChatConfirm))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.Assess))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.CheckExpiredToken))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.FlushCacheToDB))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.DeviceInIdle))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.OnlineDeviceConnection))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.PingInterval))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.SaveStatistics))
		{
			return false;
		}
		
		tmpObj = obj.getJSONObject(SettingFieldName.DBSetting);
		if(!checkField(tmpObj, SettingFieldName.MaxRecordCountForFlush))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.MaxRecordCountForDiscard))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.GlobalImpressLabelCacheCount))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.GlobalInterestLabelCacheCount))
		{
			return false;
		}

		if(!checkField(tmpObj, SettingFieldName.DeviceCacheCount))
		{
			return false;
		}

		tmpObj = obj.getJSONObject(SettingFieldName.BusinessSetting);
		if(!checkField(tmpObj, SettingFieldName.OnlinePoolCapacity))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.RandomBusinessPoolCapacity))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.InterestBusinessPoolCapacity))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.DefaultImpressLabelCount))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.HotInterestLabelCount))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.Encrypt))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.EncryptKey))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.LengthOfSessionId))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.LengthOfMessageSn))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.MaxImpressLabelCount))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.InputMessageHandleThreads))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.OutputMessageSendThreads))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.MessageQueueTime))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.DelayOfSessionBound))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.OpenTokKey))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.OpenTokSecret))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.OpenTokTokenExpiration))
		{
			return false;
		}
		
		if(!checkField(tmpObj, SettingFieldName.OpenTokTokenDuration))
		{
			return false;
		}
		
		return true;
	}
	
	private static boolean checkField(JSONObject obj, String fieldName)
	{
		if (!obj.containsKey(fieldName))
		{
			logger.error("Invalid setting file as {} is missed", fieldName);
			return false;
		}
		return true;
	}
	
	private static String loadFromFile(String fileName)
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
	
	private static class SettingCheckTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				GlobalSetting.checkSettingFile();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	
	public static void startService()
	{
		timer.scheduleAtFixedRate(new SettingCheckTask(), 10000, 10000);
		logger.debug("Timer of GlobalSetting started");
	}
	
	public static void stopService()
	{
		timer.cancel();
	}
}
