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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Profile entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "profile", catalog = "renhai")
public class Profile implements java.io.Serializable {

	// Fields

	private Integer profileId;
	private Device device;
	private String serviceStatus;
	private Long unbanDate;
	private Long lastActivityTime;
	private Long createTime;
	private String active;
	private Impresscard impresscard = new Impresscard();
	private Interestcard interestcard = new Interestcard();
	private Set<Sessionprofilemap> sessionprofilemaps = new HashSet<Sessionprofilemap>(
			0);
	private Set<Profileoperationlog> profileoperationlogs = new HashSet<Profileoperationlog>(
			0);

	// Constructors

	/** default constructor */
	public Profile() {
	}

	/** minimal constructor */
	public Profile(Device device, String serviceStatus, Long lastActivityTime,
			Long createTime) {
		this.device = device;
		this.serviceStatus = serviceStatus;
		this.lastActivityTime = lastActivityTime;
		this.createTime = createTime;
	}

	/** full constructor */
	public Profile(Device device, String serviceStatus, Long unbanDate,
			Long lastActivityTime, Long createTime, String active,
			Impresscard impresscard, Interestcard interestcard,
			Set<Sessionprofilemap> sessionprofilemaps,
			Set<Profileoperationlog> profileoperationlogs) {
		this.device = device;
		this.serviceStatus = serviceStatus;
		this.unbanDate = unbanDate;
		this.lastActivityTime = lastActivityTime;
		this.createTime = createTime;
		this.active = active;
		this.impresscard = impresscard;
		this.interestcard = interestcard;
		this.sessionprofilemaps = sessionprofilemaps;
		this.profileoperationlogs = profileoperationlogs;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "profileId", unique = true, nullable = false)
	public Integer getProfileId() {
		return this.profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deviceId", nullable = false)
	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	@Column(name = "serviceStatus", nullable = false, length = 6)
	public String getServiceStatus() {
		return this.serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	@Column(name = "unbanDate")
	public Long getUnbanDate() {
		return this.unbanDate;
	}

	public void setUnbanDate(Long unbanDate) {
		this.unbanDate = unbanDate;
	}

	@Column(name = "lastActivityTime", nullable = false)
	public Long getLastActivityTime() {
		return this.lastActivityTime;
	}

	public void setLastActivityTime(Long lastActivityTime) {
		this.lastActivityTime = lastActivityTime;
	}

	@Column(name = "createTime", nullable = false)
	public Long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "active", length = 3)
	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
	public Impresscard getImpresscard() {
		return this.impresscard;
	}

	public void setImpresscard(Impresscard impresscard) {
		this.impresscard = impresscard;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
	public Interestcard getInterestcard() {
		return this.interestcard;
	}

	public void setInterestcard(Interestcard interestcard) {
		this.interestcard = interestcard;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
	public Set<Sessionprofilemap> getSessionprofilemaps() {
		return this.sessionprofilemaps;
	}

	public void setSessionprofilemaps(Set<Sessionprofilemap> sessionprofilemaps) {
		this.sessionprofilemaps = sessionprofilemaps;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
	public Set<Profileoperationlog> getProfileoperationlogs() {
		return this.profileoperationlogs;
	}

	public void setProfileoperationlogs(
			Set<Profileoperationlog> profileoperationlogs) {
		this.profileoperationlogs = profileoperationlogs;
	}

}