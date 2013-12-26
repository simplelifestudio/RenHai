package com.simplelife.renhai.server.db;

import org.apache.ibatis.session.SqlSession;

import com.simplelife.renhai.server.util.IDbObject;

public class Device implements IDbObject 
{
	/**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column device.deviceId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer deviceId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column device.deviceSn
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private String deviceSn;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column device.deviceId
     *
     * @return the value of device.deviceId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column device.deviceId
     *
     * @param deviceId the value for device.deviceId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column device.deviceSn
     *
     * @return the value of device.deviceSn
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public String getDeviceSn() {
        return deviceSn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column device.deviceSn
     *
     * @param deviceSn the value for device.deviceSn
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn == null ? null : deviceSn.trim();
    }
    
    public Profile getProfile()
	{
		return profile;
	}

	public void setProfile(Profile profile)
	{
		this.profile = profile;
	}

	public Devicecard getDeviceCard()
	{
		return deviceCard;
	}

	public void setDeviceCard(Devicecard deviceCard)
	{
		this.deviceCard = deviceCard;
	}

	private Devicecard deviceCard;
	private Profile profile;

	public void saveToDB()
	{
		// to get deviceId
		SqlSession session = DAOWrapper.instance.getSession();
		if (deviceId == null)
		{
			DeviceMapper deviceMapper = session.getMapper(DeviceMapper.class);
			deviceMapper.insert(this);
			session.commit();
		}
		session.close();
	}
	
	@Override
	public void save(SqlSession session)
	{
		if (deviceId == null)
		{
			DeviceMapper deviceMapper = session.getMapper(DeviceMapper.class);
			deviceMapper.insert(this);
		}
		else
		{
			// The only field: deviceSn is not possible to be updated
		}
		
		deviceCard.setDeviceId(deviceId);
		deviceCard.save(session);
		
		profile.setDeviceId(deviceId);
		profile.save(session);
	}
}