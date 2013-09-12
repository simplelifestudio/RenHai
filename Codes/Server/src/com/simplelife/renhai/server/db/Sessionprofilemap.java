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
 * Sessionprofilemap entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sessionprofilemap", catalog = "renhai")
public class Sessionprofilemap implements java.io.Serializable {

	// Fields

	private Integer sessionImpressMapId;
	private Profile profile;
	private Sessionrecord sessionrecord;

	// Constructors

	/** default constructor */
	public Sessionprofilemap() {
	}

	/** full constructor */
	public Sessionprofilemap(Profile profile, Sessionrecord sessionrecord) {
		this.profile = profile;
		this.sessionrecord = sessionrecord;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "sessionImpressMapId", unique = true, nullable = false)
	public Integer getSessionImpressMapId() {
		return this.sessionImpressMapId;
	}

	public void setSessionImpressMapId(Integer sessionImpressMapId) {
		this.sessionImpressMapId = sessionImpressMapId;
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
	@JoinColumn(name = "sessionRecordId", nullable = false)
	public Sessionrecord getSessionrecord() {
		return this.sessionrecord;
	}

	public void setSessionrecord(Sessionrecord sessionrecord) {
		this.sessionrecord = sessionrecord;
	}

}