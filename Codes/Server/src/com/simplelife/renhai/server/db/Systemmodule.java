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
 * Systemmodule entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "systemmodule", catalog = "renhai")
public class Systemmodule implements java.io.Serializable {

	// Fields

	private Integer moduleId;
	private Integer moduleNo;
	private String description;
	private Set<Systemoperationlog> systemoperationlogs = new HashSet<Systemoperationlog>(
			0);

	// Constructors

	/** default constructor */
	public Systemmodule() {
	}

	/** minimal constructor */
	public Systemmodule(Integer moduleNo, String description) {
		this.moduleNo = moduleNo;
		this.description = description;
	}

	/** full constructor */
	public Systemmodule(Integer moduleNo, String description,
			Set<Systemoperationlog> systemoperationlogs) {
		this.moduleNo = moduleNo;
		this.description = description;
		this.systemoperationlogs = systemoperationlogs;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "moduleId", unique = true, nullable = false)
	public Integer getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	@Column(name = "moduleNo", nullable = false)
	public Integer getModuleNo() {
		return this.moduleNo;
	}

	public void setModuleNo(Integer moduleNo) {
		this.moduleNo = moduleNo;
	}

	@Column(name = "description", nullable = false, length = 256)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "systemmodule")
	public Set<Systemoperationlog> getSystemoperationlogs() {
		return this.systemoperationlogs;
	}

	public void setSystemoperationlogs(
			Set<Systemoperationlog> systemoperationlogs) {
		this.systemoperationlogs = systemoperationlogs;
	}

}