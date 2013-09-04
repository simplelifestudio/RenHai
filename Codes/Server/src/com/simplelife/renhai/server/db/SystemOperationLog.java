package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SystemOperationLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "SystemOperationLog", catalog = "renhai")
public class SystemOperationLog implements java.io.Serializable {

	// Fields

	private Integer systemOperationLogId;
	private Integer moduleId;
	private long logTime;
	private Integer operationCode;
	private String logInfo;

	// Constructors

	/** default constructor */
	public SystemOperationLog() {
	}

	/** minimal constructor */
	public SystemOperationLog(Integer moduleId, long logTime,
			Integer operationCode) {
		this.moduleId = moduleId;
		this.logTime = logTime;
		this.operationCode = operationCode;
	}

	/** full constructor */
	public SystemOperationLog(Integer moduleId, long logTime,
			Integer operationCode, String logInfo) {
		this.moduleId = moduleId;
		this.logTime = logTime;
		this.operationCode = operationCode;
		this.logInfo = logInfo;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "systemOperationLogId", unique = true, nullable = false)
	public Integer getSystemOperationLogId() {
		return this.systemOperationLogId;
	}

	public void setSystemOperationLogId(Integer systemOperationLogId) {
		this.systemOperationLogId = systemOperationLogId;
	}

	@Column(name = "moduleId", nullable = false)
	public Integer getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	@Column(name = "logTime", nullable = false)
	public long getLogTime() {
		return this.logTime;
	}

	public void setLogTime(long logTime) {
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