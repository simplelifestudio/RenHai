package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DeviceCard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "DeviceCard", catalog = "renhai")
public class DeviceCard implements java.io.Serializable {

	// Fields

	private Integer deviceId;
	private String deviceSn;
	private long registerTime;
	private String serviceStatus;
	private long forbiddenExpiredDate;
	private Integer profileId;
	private String deviceModel;
	private String osVersion;
	private String appVersion;
	private String location;
	private String isJailed;

	// Constructors

	/** default constructor */
	public DeviceCard() {
	}

	/** minimal constructor */
	public DeviceCard(String deviceSn, long registerTime, String serviceStatus,
			Integer profileId, String deviceModel, String osVersion,
			String appVersion) {
		this.deviceSn = deviceSn;
		this.registerTime = registerTime;
		this.serviceStatus = serviceStatus;
		this.profileId = profileId;
		this.deviceModel = deviceModel;
		this.osVersion = osVersion;
		this.appVersion = appVersion;
	}

	/** full constructor */
	public DeviceCard(String deviceSn, long registerTime, String serviceStatus,
			long forbiddenExpiredDate, Integer profileId, String deviceModel,
			String osVersion, String appVersion, String location,
			String isJailed) {
		this.deviceSn = deviceSn;
		this.registerTime = registerTime;
		this.serviceStatus = serviceStatus;
		this.forbiddenExpiredDate = forbiddenExpiredDate;
		this.profileId = profileId;
		this.deviceModel = deviceModel;
		this.osVersion = osVersion;
		this.appVersion = appVersion;
		this.location = location;
		this.isJailed = isJailed;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "deviceId", unique = true, nullable = false)
	public Integer getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "deviceSn", nullable = false, length = 256)
	public String getDeviceSn() {
		return this.deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	@Column(name = "registerTime", nullable = false)
	public long getRegisterTime() {
		return this.registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	@Column(name = "serviceStatus", nullable = false, length = 9)
	public String getServiceStatus() {
		return this.serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	@Column(name = "forbiddenExpiredDate")
	public long getForbiddenExpiredDate() {
		return this.forbiddenExpiredDate;
	}

	public void setForbiddenExpiredDate(long forbiddenExpiredDate) {
		this.forbiddenExpiredDate = forbiddenExpiredDate;
	}

	@Column(name = "profileId", nullable = false)
	public Integer getProfileId() {
		return this.profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	@Column(name = "deviceModel", nullable = false, length = 256)
	public String getDeviceModel() {
		return this.deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	@Column(name = "osVersion", nullable = false, length = 128)
	public String getOsVersion() {
		return this.osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	@Column(name = "appVersion", nullable = false, length = 128)
	public String getAppVersion() {
		return this.appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@Column(name = "location", length = 256)
	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(name = "isJailed", length = 5)
	public String getIsJailed() {
		return this.isJailed;
	}

	public void setIsJailed(String isJailed) {
		this.isJailed = isJailed;
	}

}