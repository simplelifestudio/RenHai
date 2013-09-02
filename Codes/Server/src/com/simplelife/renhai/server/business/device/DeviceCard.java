/**
 * DeviceCard.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.IDbOperation;
import com.simplelife.renhai.server.util.IJSONObject;


/**
 * 这是device类的描述
**/
public class DeviceCard implements IJSONObject, IDbOperation
{
    /**
	 * @return the deviceSn
	 */
	public String getDeviceSn()
	{
		return deviceSn;
	}

	/**
	 * @param deviceSn the deviceSn to set
	 */
	public void setDeviceSn(String deviceSn)
	{
		this.deviceSn = deviceSn;
	}

	/**
	 * @return the registerTime
	 */
	public Date getRegisterTime()
	{
		return registerTime;
	}

	/**
	 * @param registerTime the registerTime to set
	 */
	public void setRegisterTime(Date registerTime)
	{
		this.registerTime = registerTime;
	}

	/**
	 * @return the serviceStatus
	 */
	public int getServiceStatus()
	{
		return serviceStatus;
	}

	/**
	 * @param serviceStatus the serviceStatus to set
	 */
	public void setServiceStatus(int serviceStatus)
	{
		this.serviceStatus = serviceStatus;
	}

	/**
	 * @return the forbiddenExpiredDate
	 */
	public Date getForbiddenExpiredDate()
	{
		return forbiddenExpiredDate;
	}

	/**
	 * @param forbiddenExpiredDate the forbiddenExpiredDate to set
	 */
	public void setForbiddenExpiredDate(Date forbiddenExpiredDate)
	{
		this.forbiddenExpiredDate = forbiddenExpiredDate;
	}

	/**
	 * @return the profileId
	 */
	public int getProfileId()
	{
		return profileId;
	}

	/**
	 * @param profileId the profileId to set
	 */
	public void setProfileId(int profileId)
	{
		this.profileId = profileId;
	}

	/**
	 * @return the deviceModel
	 */
	public String getDeviceModel()
	{
		return deviceModel;
	}

	/**
	 * @param deviceModel the deviceModel to set
	 */
	public void setDeviceModel(String deviceModel)
	{
		this.deviceModel = deviceModel;
	}

	/**
	 * @return the osVersion
	 */
	public String getOsVersion()
	{
		return osVersion;
	}

	/**
	 * @param osVersion the osVersion to set
	 */
	public void setOsVersion(String osVersion)
	{
		this.osVersion = osVersion;
	}

	/**
	 * @return the appVersion
	 */
	public String getAppVersion()
	{
		return appVersion;
	}

	/**
	 * @param appVersion the appVersion to set
	 */
	public void setAppVersion(String appVersion)
	{
		this.appVersion = appVersion;
	}

	/**
	 * @return the location
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

	/**
	 * @return the isJailed
	 */
	public boolean isJailed()
	{
		return isJailed;
	}

	/**
	 * @param isJailed the isJailed to set
	 */
	public void setJailed(boolean isJailed)
	{
		this.isJailed = isJailed;
	}

	
	/**
	 * @return the deviceId
	 */
	public int getDeviceId()
	{
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(int deviceId)
	{
		this.deviceId = deviceId;
	}

	/**
	 * Increment ID of device
	 */
	private int deviceId;
	
	
	/**
	 * SN of device, it can be used to distinguish devices definitely 
	 */
    private String deviceSn;
    
    /**
     * Register time of device, which is also means the time of first synchronize device data 
     */
    private Date registerTime;
    
    /**
     * Service status of device, it can be: Normal, Forbidden
     */
    private int serviceStatus;
    
    /**
     * Date of un-forbidden if device is forbidden
     */
    private Date forbiddenExpiredDate;
    
    /**
     * ID of profile
     */
    private int profileId;
    
    /**
     * Model of device, such as: iPhone, iPad
     */
    private String deviceModel;
    
    /**
     * Version of OS, such as iOS 5.1
     */
    private String osVersion;
    
    /**
     * Version of APP
     */
    private String appVersion;
    
    /**
     * Location of device
     */
    private String location;
    
    /**
     * If device is jailed
     */
    private boolean isJailed;
    
    /** */
    public JSONObject toJSONObject()
    {
        return null;
    }
    
    /** */
    public boolean saveToDb()
    {
        return false;
    }
    
    /** */
    public boolean loadFromDb()
    {
        return false;
    }
}
