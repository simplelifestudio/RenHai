package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Profile entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "Profile", catalog = "renhai")
public class Profile implements java.io.Serializable {

	// Fields

	private Integer profileId;
	private Integer interestCardId;
	private Integer impressCardId;
	private long lastActivityTime;
	private long createTime;

	// Constructors

	/** default constructor */
	public Profile() {
	}

	/** full constructor */
	public Profile(Integer interestCardId, Integer impressCardId,
			long lastActivityTime, long createTime) {
		this.interestCardId = interestCardId;
		this.impressCardId = impressCardId;
		this.lastActivityTime = lastActivityTime;
		this.createTime = createTime;
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

	@Column(name = "interestCardId", nullable = false)
	public Integer getInterestCardId() {
		return this.interestCardId;
	}

	public void setInterestCardId(Integer interestCardId) {
		this.interestCardId = interestCardId;
	}

	@Column(name = "impressCardId", nullable = false)
	public Integer getImpressCardId() {
		return this.impressCardId;
	}

	public void setImpressCardId(Integer impressCardId) {
		this.impressCardId = impressCardId;
	}

	@Column(name = "lastActivityTime", nullable = false)
	public long getLastActivityTime() {
		return this.lastActivityTime;
	}

	public void setLastActivityTime(long lastActivityTime) {
		this.lastActivityTime = lastActivityTime;
	}

	@Column(name = "createTime", nullable = false)
	public long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}