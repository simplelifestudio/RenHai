package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ProfileOperationLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "ProfileOperationLog", catalog = "renhai")
public class ProfileOperationLog implements java.io.Serializable {

	// Fields

	private Integer profileOperationLogId;
	private Integer deviceId;
	private Integer profileId;
	private Integer logTime;
	private Integer operationCode;
	private String logInfo;

	// Constructors

	/** default constructor */
	public ProfileOperationLog() {
	}

	/** minimal constructor */
	public ProfileOperationLog(Integer deviceId, Integer profileId,
			Integer logTime, Integer operationCode) {
		this.deviceId = deviceId;
		this.profileId = profileId;
		this.logTime = logTime;
		this.operationCode = operationCode;
	}

	/** full constructor */
	public ProfileOperationLog(Integer deviceId, Integer profileId,
			Integer logTime, Integer operationCode, String logInfo) {
		this.deviceId = deviceId;
		this.profileId = profileId;
		this.logTime = logTime;
		this.operationCode = operationCode;
		this.logInfo = logInfo;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "profileOperationLogId", unique = true, nullable = false)
	public Integer getProfileOperationLogId() {
		return this.profileOperationLogId;
	}

	public void setProfileOperationLogId(Integer profileOperationLogId) {
		this.profileOperationLogId = profileOperationLogId;
	}

	@Column(name = "deviceId", nullable = false)
	public Integer getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "profileId", nullable = false)
	public Integer getProfileId() {
		return this.profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	@Column(name = "logTime", nullable = false)
	public Integer getLogTime() {
		return this.logTime;
	}

	public void setLogTime(Integer logTime) {
		this.logTime = logTime;
	}

	@Column(name = "operationCode", nullable = false)
	public Integer getOperationCode() {
		return this.operationCode;
	}

	public void setOperationCode(Integer operationCode) {
		this.operationCode = operationCode;
	}

	@Column(name = "logInfo", length = 256)
	public String getLogInfo() {
		return this.logInfo;
	}

	public void setLogInfo(String logInfo) {
		this.logInfo = logInfo;
	}

}