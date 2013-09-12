package com.simplelife.renhai.server.db;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Device entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "device", catalog = "renhai")
public class Device implements java.io.Serializable {

	// Fields

	private Integer deviceId;
	private String deviceSn;
	private Devicecard devicecard = new Devicecard();
	private Profile profile = new Profile();

	// Constructors

	/** default constructor */
	public Device() {
	}

	/** full constructor */
	public Device(String deviceSn, Devicecard devicecard,
			Profile profile) {
		this.deviceSn = deviceSn;
		this.devicecard = devicecard;
		this.profile = profile;
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

	@Column(name = "deviceSn", length = 256)
	public String getDeviceSn() {
		return this.deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "device")
	public Devicecard getDevicecard() {
		return this.devicecard;
	}

	public void setDevicecard(Devicecard devicecard) {
		this.devicecard = devicecard;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "device")
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

}