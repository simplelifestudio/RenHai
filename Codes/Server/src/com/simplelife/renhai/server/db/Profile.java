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
 * Profile entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "profile", catalog = "renhai")
public class Profile implements java.io.Serializable {

	// Fields

	private Integer profileId;
	private Interestcard interestcard;
	private Impresscard impresscard;
	private Long lastActivityTime;
	private Long createTime;
	private Set<Deviceprofilemap> deviceprofilemaps = new HashSet<Deviceprofilemap>(
			0);
	private Set<Devicecard> devicecards = new HashSet<Devicecard>(0);
	private Set<Sessionprofilecollection> sessionprofilecollections = new HashSet<Sessionprofilecollection>(
			0);
	private Set<Profileoperationlog> profileoperationlogs = new HashSet<Profileoperationlog>(
			0);

	// Constructors

	/** default constructor */
	public Profile() {
	}

	/** minimal constructor */
	public Profile(Interestcard interestcard, Impresscard impresscard,
			Long lastActivityTime, Long createTime) {
		this.interestcard = interestcard;
		this.impresscard = impresscard;
		this.lastActivityTime = lastActivityTime;
		this.createTime = createTime;
	}

	/** full constructor */
	public Profile(Interestcard interestcard, Impresscard impresscard,
			Long lastActivityTime, Long createTime,
			Set<Deviceprofilemap> deviceprofilemaps,
			Set<Devicecard> devicecards,
			Set<Sessionprofilecollection> sessionprofilecollections,
			Set<Profileoperationlog> profileoperationlogs) {
		this.interestcard = interestcard;
		this.impresscard = impresscard;
		this.lastActivityTime = lastActivityTime;
		this.createTime = createTime;
		this.deviceprofilemaps = deviceprofilemaps;
		this.devicecards = devicecards;
		this.sessionprofilecollections = sessionprofilecollections;
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
	@JoinColumn(name = "interestCardId", nullable = false)
	public Interestcard getInterestcard() {
		return this.interestcard;
	}

	public void setInterestcard(Interestcard interestcard) {
		this.interestcard = interestcard;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "impressCardId", nullable = false)
	public Impresscard getImpresscard() {
		return this.impresscard;
	}

	public void setImpresscard(Impresscard impresscard) {
		this.impresscard = impresscard;
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
	public Set<Deviceprofilemap> getDeviceprofilemaps() {
		return this.deviceprofilemaps;
	}

	public void setDeviceprofilemaps(Set<Deviceprofilemap> deviceprofilemaps) {
		this.deviceprofilemaps = deviceprofilemaps;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
	public Set<Devicecard> getDevicecards() {
		return this.devicecards;
	}

	public void setDevicecards(Set<Devicecard> devicecards) {
		this.devicecards = devicecards;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
	public Set<Sessionprofilecollection> getSessionprofilecollections() {
		return this.sessionprofilecollections;
	}

	public void setSessionprofilecollections(
			Set<Sessionprofilecollection> sessionprofilecollections) {
		this.sessionprofilecollections = sessionprofilecollections;
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