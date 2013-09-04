package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SessionRecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "SessionRecord", catalog = "renhai")
public class SessionRecord implements java.io.Serializable {

	// Fields

	private Integer sessionRecordId;
	private String businessType;
	private long startTime;
	private Integer duration;
	private String endStatus;
	private String endReason;

	// Constructors

	/** default constructor */
	public SessionRecord() {
	}

	/** full constructor */
	public SessionRecord(String businessType, long startTime, Integer duration,
			String endStatus, String endReason) {
		this.businessType = businessType;
		this.startTime = startTime;
		this.duration = duration;
		this.endStatus = endStatus;
		this.endReason = endReason;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "sessionRecordId", unique = true, nullable = false)
	public Integer getSessionRecordId() {
		return this.sessionRecordId;
	}

	public void setSessionRecordId(Integer sessionRecordId) {
		this.sessionRecordId = sessionRecordId;
	}

	@Column(name = "businessType", nullable = false, length = 8)
	public String getBusinessType() {
		return this.businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@Column(name = "startTime", nullable = false)
	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Column(name = "duration", nullable = false)
	public Integer getDuration() {
		return this.duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@Column(name = "endStatus", nullable = false, length = 12)
	public String getEndStatus() {
		return this.endStatus;
	}

	public void setEndStatus(String endStatus) {
		this.endStatus = endStatus;
	}

	@Column(name = "endReason", nullable = false, length = 14)
	public String getEndReason() {
		return this.endReason;
	}

	public void setEndReason(String endReason) {
		this.endReason = endReason;
	}

}