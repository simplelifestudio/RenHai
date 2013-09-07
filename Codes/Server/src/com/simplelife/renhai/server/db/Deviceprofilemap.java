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
 * Deviceprofilemap entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "deviceprofilemap", catalog = "renhai")
public class Deviceprofilemap implements java.io.Serializable {

	// Fields

	private Integer deviceProfileMapId;
	private Profile profile;
	private Devicecard devicecard;

	// Constructors

	/** default constructor */
	public Deviceprofilemap() {
	}

	/** full constructor */
	public Deviceprofilemap(Profile profile, Devicecard devicecard) {
		this.profile = profile;
		this.devicecard = devicecard;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profileId", nullable = false)
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deviceId", nullable = false)
	public Devicecard getDevicecard() {
		return this.devicecard;
	}

	public void setDevicecard(Devicecard devicecard) {
		this.devicecard = devicecard;
	}

}