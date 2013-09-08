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
 * Systemoperationlog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "systemoperationlog", catalog = "renhai")
public class Systemoperationlog implements java.io.Serializable {

	// Fields

	private Integer systemOperationLogId;
	private Systemmodule systemmodule;
	private Operationcode operationcode;
	private Long logTime;
	private String logInfo;

	// Constructors

	/** default constructor */
	public Systemoperationlog() {
	}

	/** minimal constructor */
	public Systemoperationlog(Systemmodule systemmodule,
			Operationcode operationcode, Long logTime) {
		this.systemmodule = systemmodule;
		this.operationcode = operationcode;
		this.logTime = logTime;
	}

	/** full constructor */
	public Systemoperationlog(Systemmodule systemmodule,
			Operationcode operationcode, Long logTime, String logInfo) {
		this.systemmodule = systemmodule;
		this.operationcode = operationcode;
		this.logTime = logTime;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moduleId", nullable = false)
	public Systemmodule getSystemmodule() {
		return this.systemmodule;
	}

	public void setSystemmodule(Systemmodule systemmodule) {
		this.systemmodule = systemmodule;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operationCodeId", nullable = false)
	public Operationcode getOperationcode() {
		return this.operationcode;
	}

	public void setOperationcode(Operationcode operationcode) {
		this.operationcode = operationcode;
	}

	@Column(name = "logTime", nullable = false)
	public Long getLogTime() {
		return this.logTime;
	}

	public void setLogTime(Long logTime) {
		this.logTime = logTime;
	}

	@Column(name = "logInfo", length = 256)
	public String getLogInfo() {
		return this.logInfo;
	}

	public void setLogInfo(String logInfo) {
		this.logInfo = logInfo;
	}

}