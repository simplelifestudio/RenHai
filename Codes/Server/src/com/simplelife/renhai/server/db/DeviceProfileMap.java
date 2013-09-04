package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DeviceProfileMap entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "DeviceProfileMap", catalog = "renhai")
public class DeviceProfileMap implements java.io.Serializable {

	// Fields

	private Integer deviceProfileMapId;
	private Integer deviceId;
	private Integer profileId;

	// Constructors

	/** default constructor */
	public DeviceProfileMap() {
	}

	/** full constructor */
	public DeviceProfileMap(Integer deviceId, Integer profileId) {
		this.deviceId = deviceId;
		this.profileId = profileId;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "deviceProfileMapId", unique = true, nullable = false)
	public Integer getDeviceProfileMapId() {
		return this.deviceProfileMapId;
	}

	public void setDeviceProfileMapId(Integer deviceProfileMapId) {
		this.deviceProfileMapId = deviceProfileMapId;
	}

	@Column(name = "deviceId", nullable = false)
	public Integer getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "profileId", nullable = false)
	public Integer getProfileId() {
		return this.profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

}