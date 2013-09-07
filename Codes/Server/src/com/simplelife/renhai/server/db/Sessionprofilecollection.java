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
 * Sessionprofilecollection entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sessionprofilecollection", catalog = "renhai")
public class Sessionprofilecollection implements java.io.Serializable {

	// Fields

	private Integer sessionImpressMapId;
	private Profile profile;
	private Sessionrecord sessionrecord;
	private Integer count;

	// Constructors

	/** default constructor */
	public Sessionprofilecollection() {
	}

	/** full constructor */
	public Sessionprofilecollection(Profile profile,
			Sessionrecord sessionrecord, Integer count) {
		this.profile = profile;
		this.sessionrecord = sessionrecord;
		this.count = count;
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

	@Column(name = "count", nullable = false)
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}