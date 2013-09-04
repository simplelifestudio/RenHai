package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SessionProfileCollection entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "SessionProfileCollection", catalog = "renhai")
public class SessionProfileCollection implements java.io.Serializable {

	// Fields

	private Integer sessionImpressMapId;
	private Integer sessionRecordId;
	private Integer profileId;
	private Integer count;

	// Constructors

	/** default constructor */
	public SessionProfileCollection() {
	}

	/** full constructor */
	public SessionProfileCollection(Integer sessionRecordId, Integer profileId,
			Integer count) {
		this.sessionRecordId = sessionRecordId;
		this.profileId = profileId;
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

	@Column(name = "sessionRecordId", nullable = false)
	public Integer getSessionRecordId() {
		return this.sessionRecordId;
	}

	public void setSessionRecordId(Integer sessionRecordId) {
		this.sessionRecordId = sessionRecordId;
	}

	@Column(name = "profileId", nullable = false)
	public Integer getProfileId() {
		return this.profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	@Column(name = "count", nullable = false)
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}