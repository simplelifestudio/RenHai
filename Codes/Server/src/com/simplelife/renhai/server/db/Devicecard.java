package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Devicecard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "devicecard", catalog = "renhai")
public class Devicecard implements java.io.Serializable {

	// Fields

	private Integer deviceCardId;
	private Device device;
	private Long registerTime;
	private String deviceModel;
	private String osVersion;
	private String appVersion;
	private String location;
	private String isJailed;

	// Constructors

	/** default constructor */
	public Devicecard() {
	}

	/** minimal constructor */
	public Devicecard(Device device, Long registerTime, String deviceModel,
			String osVersion, String appVersion, String isJailed) {
		this.device = device;
		this.registerTime = registerTime;
		this.deviceModel = deviceModel;
		this.osVersion = osVersion;
		this.appVersion = appVersion;
		this.isJailed = isJailed;
	}

	/** full constructor */
	public Devicecard(Device device, Long registerTime, String deviceModel,
			String osVersion, String appVersion, String location,
			String isJailed) {
		this.device = device;
		this.registerTime = registerTime;
		this.deviceModel = deviceModel;
		this.osVersion = osVersion;
		this.appVersion = appVersion;
		this.location = location;
		this.isJailed = isJailed;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "deviceCardId", unique = true, nullable = false)
	public Integer getDeviceCardId() {
		return this.deviceCardId;
	}

	public void setDeviceCardId(Integer deviceCardId) {
		this.deviceCardId = deviceCardId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deviceId", nullable = false)
	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	@Column(name = "registerTime", nullable = false)
	public Long getRegisterTime() {
		return this.registerTime;
	}

	public void setRegisterTime(Long registerTime) {
		this.registerTime = registerTime;
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

	@Column(name = "isJailed", nullable = false, length = 4)
	public String getIsJailed() {
		return this.isJailed;
	}

	public void setIsJailed(String isJailed) {
		this.isJailed = isJailed;
	}

}