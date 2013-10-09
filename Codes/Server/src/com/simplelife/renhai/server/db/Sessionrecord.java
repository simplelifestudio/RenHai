package com.simplelife.renhai.server.db;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Sessionrecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sessionrecord", catalog = "renhai")
public class Sessionrecord implements java.io.Serializable {

	// Fields

	private Integer sessionRecordId;
	private String businessType;
	private Long sessionStartTime;
	private Integer sessionDuration;
	private Long chatStartTime;
	private Integer chatDuration;
	private String endStatus;
	private String endReason;
	private Set<Sessionprofilemap> sessionprofilemaps = new HashSet<Sessionprofilemap>(
			0);

	// Constructors

	/** default constructor */
	public Sessionrecord() {
	}

	/** minimal constructor */
	public Sessionrecord(String businessType, Long sessionStartTime,
			Integer sessionDuration, String endStatus, String endReason) {
		this.businessType = businessType;
		this.sessionStartTime = sessionStartTime;
		this.sessionDuration = sessionDuration;
		this.endStatus = endStatus;
		this.endReason = endReason;
	}

	/** full constructor */
	public Sessionrecord(String businessType, Long sessionStartTime,
			Integer sessionDuration, Long chatStartTime, Integer chatDuration,
			String endStatus, String endReason,
			Set<Sessionprofilemap> sessionprofilemaps) {
		this.businessType = businessType;
		this.sessionStartTime = sessionStartTime;
		this.sessionDuration = sessionDuration;
		this.chatStartTime = chatStartTime;
		this.chatDuration = chatDuration;
		this.endStatus = endStatus;
		this.endReason = endReason;
		this.sessionprofilemaps = sessionprofilemaps;
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

	@Column(name = "sessionStartTime", nullable = false)
	public Long getSessionStartTime() {
		return this.sessionStartTime;
	}

	public void setSessionStartTime(Long sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}

	@Column(name = "sessionDuration", nullable = false)
	public Integer getSessionDuration() {
		return this.sessionDuration;
	}

	public void setSessionDuration(Integer sessionDuration) {
		this.sessionDuration = sessionDuration;
	}

	@Column(name = "chatStartTime")
	public Long getChatStartTime() {
		return this.chatStartTime;
	}

	public void setChatStartTime(Long chatStartTime) {
		this.chatStartTime = chatStartTime;
	}

	@Column(name = "chatDuration")
	public Integer getChatDuration() {
		return this.chatDuration;
	}

	public void setChatDuration(Integer chatDuration) {
		this.chatDuration = chatDuration;
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sessionrecord")
	public Set<Sessionprofilemap> getSessionprofilemaps() {
		return this.sessionprofilemaps;
	}

	public void setSessionprofilemaps(Set<Sessionprofilemap> sessionprofilemaps) {
		this.sessionprofilemaps = sessionprofilemaps;
	}

}