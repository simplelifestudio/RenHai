package com.simplelife.renhai.server.db;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Devicecard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "devicecard", catalog = "renhai")
public class Devicecard implements java.io.Serializable {

	// Fields

	private Integer deviceId;
	private Profile profile;
	private String deviceSn;
	private Long registerTime;
	private String serviceStatus;
	private Long forbiddenExpiredDate;
	private String deviceModel;
	private String osVersion;
	private String appVersion;
	private String location;
	private String isJailed;
	private Set<Profileoperationlog> profileoperationlogs = new HashSet<Profileoperationlog>(
			0);
	private Set<Deviceprofilemap> deviceprofilemaps = new HashSet<Deviceprofilemap>(
			0);

	// Constructors

	/** default constructor */
	public Devicecard() {
	}

	/** minimal constructor */
	public Devicecard(Profile profile, String deviceSn, Long registerTime,
			String serviceStatus, String deviceModel, String osVersion,
			String appVersion) {
		this.profile = profile;
		this.deviceSn = deviceSn;
		this.registerTime = registerTime;
		this.serviceStatus = serviceStatus;
		this.deviceModel = deviceModel;
		this.osVersion = osVersion;
		this.appVersion = appVersion;
	}

	/** full constructor */
	public Devicecard(Profile profile, String deviceSn, Long registerTime,
			String serviceStatus, Long forbiddenExpiredDate,
			String deviceModel, String osVersion, String appVersion,
			String location, String isJailed,
			Set<Profileoperationlog> profileoperationlogs,
			Set<Deviceprofilemap> deviceprofilemaps) {
		this.profile = profile;
		this.deviceSn = deviceSn;
		this.registerTime = registerTime;
		this.serviceStatus = serviceStatus;
		this.forbiddenExpiredDate = forbiddenExpiredDate;
		this.deviceModel = deviceModel;
		this.osVersion = osVersion;
		this.appVersion = appVersion;
		this.location = location;
		this.isJailed = isJailed;
		this.profileoperationlogs = profileoperationlogs;
		this.deviceprofilemaps = deviceprofilemaps;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profileId", nullable = false)
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Column(name = "deviceSn", nullable = false, length = 256)
	public String getDeviceSn() {
		return this.deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	@Column(name = "registerTime", nullable = false)
	public Long getRegisterTime() {
		return this.registerTime;
	}

	public void setRegisterTime(Long registerTime) {
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
	public Long getForbiddenExpiredDate() {
		return this.forbiddenExpiredDate;
	}

	public void setForbiddenExpiredDate(Long forbiddenExpiredDate) {
		this.forbiddenExpiredDate = forbiddenExpiredDate;
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "devicecard")
	public Set<Profileoperationlog> getProfileoperationlogs() {
		return this.profileoperationlogs;
	}

	public void setProfileoperationlogs(
			Set<Profileoperationlog> profileoperationlogs) {
		this.profileoperationlogs = profileoperationlogs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "devicecard")
	public Set<Deviceprofilemap> getDeviceprofilemaps() {
		return this.deviceprofilemaps;
	}

	public void setDeviceprofilemaps(Set<Deviceprofilemap> deviceprofilemaps) {
		this.deviceprofilemaps = deviceprofilemaps;
	}

}