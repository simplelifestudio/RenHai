package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ModuleDefinition entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "ModuleDefinition", catalog = "renhai")
public class ModuleDefinition implements java.io.Serializable {

	// Fields

	private Integer moduleDefinitionId;
	private Integer moduleId;
	private String description;

	// Constructors

	/** default constructor */
	public ModuleDefinition() {
	}

	/** full constructor */
	public ModuleDefinition(Integer moduleId, String description) {
		this.moduleId = moduleId;
		this.description = description;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "moduleDefinitionId", unique = true, nullable = false)
	public Integer getModuleDefinitionId() {
		return this.moduleDefinitionId;
	}

	public void setModuleDefinitionId(Integer moduleDefinitionId) {
		this.moduleDefinitionId = moduleDefinitionId;
	}

	@Column(name = "moduleId", nullable = false)
	public Integer getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	@Column(name = "description", nullable = false, length = 256)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}