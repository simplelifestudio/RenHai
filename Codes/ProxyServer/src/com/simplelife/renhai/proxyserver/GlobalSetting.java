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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	private int encrypt;
	private Timer timer = new Timer();
	
	private ArrayList<IServer> serverList = new ArrayList<>();
	
	public int getEncrypt()
	{
		return encrypt;
	}
	
	public void setEncrypt(int encrypt)
	{
		this.encrypt = encrypt;
	}
	
	private GlobalSetting()
	{
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
		parseSetting(obj);
	}
	
	private synchronized void parseSetting(JSONObject obj)
	{
		int intValue;
		if (obj.containsKey(JSONKey.Encrypt))
		{
			intValue = obj.getIntValue(JSONKey.Encrypt);
			setEncrypt(intValue);
		}
		
		JSONArray servers = obj.getJSONArray(JSONKey.ServerList);
		
		serverList.clear();
		JSONObject serverObj;
		String id;
		for (int i = servers.size()-1; i>=0; i--)
		{
			serverObj = servers.getJSONObject(i);
			if (!serverObj.containsKey(JSONKey.Id))
			{
				logger.error("{} of server is missed, please check setting file", JSONKey.Id);
				continue;
			}
			
			id = serverObj.getString(JSONKey.Id);
			if (!id.equalsIgnoreCase(JSONKey.Default))
			{
				NormalServer server = new NormalServer(serverObj);
				serverList.add(server);
			}
			else
			{
				DefaultServer server = new DefaultServer(serverObj);
				serverList.add(server);
			}
		}
		
		IServer serveri;
		IServer serverj;
		int size = serverList.size();
		for (int i = 0; i < size; i++)
		{
			serveri = serverList.get(i);
			for (int j = i+1; j<size; j++)
			{
				serverj = serverList.get(j);
				
				// Order of servers: Higher version, lower version, Default Server
				if (serveri.compareTo(serverj) < 0)
				{
					serverList.set(i, serverj);
					serverList.set(j, serveri);
					serveri = serverj; 
				}
			}
		}
	}
	
	public IServer distributToServer(String version, int build)
	{
		for (IServer server : serverList)
		{
			if (server.checkVersion(version, build))
			{
				return server;
			}
		}
		
		// Abnormal case, at least DefaultServer will be returned in loop
		logger.error("Fatal error: no server is met for version {} and build " + build, version);
		return null;
	}
	
	private boolean checkJsonSetting(JSONObject obj)
	{
		logger.debug("Start to check JSON setting");
		if (!obj.containsKey(JSONKey.Encrypt))
		{
			logger.error("Field {} is missed", JSONKey.Encrypt);
			return false;
		}
		try
		{
			int intValue = obj.getIntValue(JSONKey.Encrypt);
		}
		catch(Exception e)
		{
			logger.error("Value of {} must be numric", JSONKey.Encrypt);
			return false;
		}
		
		if (!obj.containsKey(JSONKey.ServerList))
		{
			logger.error("Field {} is missed", JSONKey.ServerList);
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
