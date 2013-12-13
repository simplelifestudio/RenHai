package com.simplelife.renhai.proxyserver;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.proxyserver.Consts.ServiceStatus;

public class NormalServer implements IServer
{
	private JSONObject response = new JSONObject();
	private Logger logger = LoggerFactory.getLogger(NormalServer.class);
	private String id;
	private AppBaseVersion appBaseVersion = new AppBaseVersion();
	private Status status = new Status();
	private Address address = new Address();
	private String broadcast;
	
	public NormalServer(JSONObject obj)
	{
		init(obj);
	}
	
	public AppBaseVersion getAppBaseVersion()
	{
		return appBaseVersion;
	}
	
	@Override
	public int compareTo(IServer o)
	{
		if (o instanceof DefaultServer)
		{
			return 1;
		}
		return appBaseVersion.compareTo(((NormalServer)o).getAppBaseVersion());
	}
	
	@Override
	public String getId()
	{
		return id;
	}
	
	@Override
	public boolean checkVersion(String version, int build)
	{
		return appBaseVersion.checkVersion(version, build);
	}

	@Override
	public JSONObject getResponse()
	{
		response.put(JSONKey.Id, id);
		response.put(JSONKey.Broadcast, broadcast);
		
		response.put(JSONKey.Status, status.getResponse());
		
		ServiceStatus tmpStatus = status.getServiceStatus();
		if (tmpStatus != ServiceStatus.Maintenance)
		{
			response.put(JSONKey.Address, address.getResponse());
		}
		/*
		switch(tmpStatus)
		{
			case Maintenance:
				response.put(JSONKey.Status, status.getResponse());
				break;
			case Normal:
				response.put(JSONKey.Address, address.getResponse());
				break;
			case ToBeMaintained:
				response.put(JSONKey.Address, address.getResponse());
				response.put(JSONKey.Status, status.getResponse());
				break;
			case Invalid:
				logger.error("Fatal error: abnormal service status");
				break;
		}
		*/
		
		return response;
	}
	
	@Override
	public void init(JSONObject obj)
	{
		if (!checkSettingFile(obj))
		{
			logger.error("Fatal error: invalid setting file, please fix the specific error above and retry");
			return;
		}
		id = obj.getString(JSONKey.Id);
		broadcast = obj.getString(JSONKey.Broadcast);
		
		JSONObject tmpObj = obj.getJSONObject(JSONKey.AppVersion);
		appBaseVersion.init(tmpObj);
		
		tmpObj = obj.getJSONObject(JSONKey.Status);
		status.init(tmpObj);
		
		tmpObj = obj.getJSONObject(JSONKey.Address);
		address.init(tmpObj);
	}
	
	private boolean checkSettingFile(JSONObject obj)
	{
		if (isFieldMissed(obj, JSONKey.Id))
		{
			return false;
		}
		
		if (isFieldMissed(obj, JSONKey.Status))
		{
			return false;
		}
		
		if (isFieldMissed(obj, JSONKey.Address))
		{
			return false;
		}
		
		if (isFieldMissed(obj, JSONKey.Broadcast))
		{
			return false;
		}
		
		JSONObject tmpObj = obj.getJSONObject(JSONKey.AppVersion);
		if (!appBaseVersion.checkSettingFile(tmpObj))
		{
			return false;
		}
		
		tmpObj = obj.getJSONObject(JSONKey.Status);
		if (!status.checkSettingFile(tmpObj))
		{
			return false;
		}
		
		tmpObj = obj.getJSONObject(JSONKey.Address);
		if (!address.checkSettingFile(tmpObj))
		{
			return false;
		}
		return true;
	}
	
	private boolean isFieldMissed(JSONObject obj, String fieldName)
	{
		if (obj == null)
		{
			logger.error("No definition contains {}.", fieldName);
			return true;
		}
		
		if (!obj.containsKey(fieldName))
		{
			logger.error("{} is missed.", fieldName);
			return true;
		}
		return false;
	}
	
	private class AppBaseVersion
	{
		private String version;
		private int build;
		private JSONObject response = new JSONObject();
		
		public void init(JSONObject versionObj)
		{
			version = versionObj.getString(JSONKey.Version);
			build = versionObj.getIntValue(JSONKey.Build);
			
			response.put(JSONKey.Version, version);
			response.put(JSONKey.BeginTime, build);
		}
		
		public String getVersion()
		{
			return version;
		}

		public void setVersion(String version)
		{
			this.version = version;
		}

		public int getBuild()
		{
			return build;
		}

		public void setBuild(int build)
		{
			this.build = build;
		}
		
		public boolean checkVersion(String version, int build)
		{
			int i = compareVersion(this.version, version);
			if (i < 0)
			{
				// Base version of this server is lower than version of App
				return true;
			}
			else if (i > 0)
			{
				// Base version of this server is higher than version of App
				return false;
			}
			
			return build >= this.build;
		}
		
		private int compareVersion(String version1, String version2)
		{
			String[] verStrArray1 = version1.split("\\.");
			String[] verStrArray2 = version2.split("\\.");
			
			int minLength = verStrArray1.length > verStrArray2.length ? verStrArray2.length : verStrArray1.length;
			for (int i = 0; i < minLength; i++)
			{
				Integer intVer1 = Integer.parseInt(verStrArray1[i]);
				Integer intVer2 = Integer.parseInt(verStrArray2[i]);
				
				if (intVer1 != intVer2)
				{
					return intVer1 - intVer2; 
				}
			}
			
			// All compared version numbers are same, check the length
			return verStrArray1.length - verStrArray2.length;
		}
		
		public int compareTo(AppBaseVersion o)
		{
			int i = compareVersion(this.version, o.getVersion());
			if (i != 0)
			{
				return i;
			}
			return this.build - o.getBuild();
		}
		
		public boolean checkSettingFile(JSONObject versionObj)
		{
			if (isFieldMissed(versionObj, JSONKey.Version))
			{
				return false;
			}
			
			if (isFieldMissed(versionObj, JSONKey.Build))
			{
				return false;
			}
			return true;
		}
		
		public JSONObject getResponse()
		{
			return response;
		}
	}
	
	private class Status
	{
		private JSONObject response = new JSONObject();
		private ServiceStatus serviceStatus = ServiceStatus.Normal;
		private String beginTime = "2013-01-01 00:00:00";
		private String endTime = "2013-01-01 00:00:00";
		private String timeZone = "GMT+0800";
		
		public ServiceStatus getServiceStatus()
		{
			if (serviceStatus == ServiceStatus.Normal)
			{
				return ServiceStatus.Normal;
			}
			
			Date begin = DateUtil.getDateByTimeZoneDateString(beginTime, timeZone);
			Date end = DateUtil.getDateByTimeZoneDateString(endTime, timeZone);
			long now = System.currentTimeMillis(); 
			if (now > end.getTime())
			{
				return ServiceStatus.Normal;
			}
			else if (now < begin.getTime())
			{
				return ServiceStatus.ToBeMaintained;
			}
			return ServiceStatus.Maintenance;
		}
		
		public boolean checkSettingFile(JSONObject statusObj)
		{
			if (isFieldMissed(statusObj, JSONKey.ServiceStatus))
			{
				return false;
			}
			
			int status = statusObj.getIntValue(JSONKey.ServiceStatus);
			if (ServiceStatus.parseValue(status) == ServiceStatus.Invalid)
			{
				logger.error("Value of {} is invalid.", JSONKey.ServiceStatus);
				return false;
			}
			
			if (isFieldMissed(statusObj, JSONKey.StatusPeriod))
			{
				return false;
			}

			JSONObject obj = statusObj.getJSONObject(JSONKey.StatusPeriod);
			if (isFieldMissed(obj, JSONKey.TimeZone))
			{
				return false;
			}
			
			if (isFieldMissed(obj, JSONKey.BeginTime))
			{
				return false;
			}
			
			if (isFieldMissed(obj, JSONKey.EndTime))
			{
				return false;
			}
			return true;
		}
		
		public void init(JSONObject statusObj)
		{
			serviceStatus = ServiceStatus.parseValue(statusObj.getIntValue(JSONKey.ServiceStatus));
			JSONObject obj = statusObj.getJSONObject(JSONKey.StatusPeriod);
			timeZone = obj.getString(JSONKey.TimeZone);
			beginTime = obj.getString(JSONKey.BeginTime);
			endTime = obj.getString(JSONKey.EndTime);
		}
		
		public JSONObject getResponse()
		{
			response.clear();
			if (this.getServiceStatus() == ServiceStatus.Normal)
			{
				response.put(JSONKey.ServiceStatus, ServiceStatus.Normal.getValue());
			}
			else
			{
				response.put(JSONKey.ServiceStatus, ServiceStatus.Maintenance.getValue());
				JSONObject obj = new JSONObject();
				obj.put(JSONKey.TimeZone, timeZone);
				obj.put(JSONKey.BeginTime, beginTime);
				obj.put(JSONKey.EndTime, endTime);
				response.put(JSONKey.StatusPeriod, obj);
			}
			return response;
		}
	}
	
	private class Address
	{
		private JSONObject response = new JSONObject();
		private String ipAddress;
		private int port;
		private String path;
		private String protocol;
		
		public boolean checkSettingFile(JSONObject statusObj)
		{
			if (isFieldMissed(statusObj, JSONKey.Protocol))
			{
				return false;
			}
			
			if (isFieldMissed(statusObj, JSONKey.Ip))
			{
				return false;
			}
			
			if (isFieldMissed(statusObj, JSONKey.Port))
			{
				return false;
			}
			
			try
			{
				int intValue = statusObj.getIntValue(JSONKey.Port);
			}
			catch(Exception e)
			{
				logger.error("Value of {} is invalid", JSONKey.Port);
				return false;
			}
			
			
			if (isFieldMissed(statusObj, JSONKey.Path))
			{
				return false;
			}
			return true;
		}
		
		public void init(JSONObject addressObj)
		{
			protocol = addressObj.getString(JSONKey.Protocol);
			ipAddress = addressObj.getString(JSONKey.Ip);
			port = addressObj.getIntValue(JSONKey.Port);
			path = addressObj.getString(JSONKey.Path);
			
			response.put(JSONKey.Protocol, protocol);
			response.put(JSONKey.Ip, ipAddress);
			response.put(JSONKey.Port, port);
			response.put(JSONKey.Path, path);
		}
		
		public JSONObject getResponse()
		{
			return response;
		}
	}
}
