package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OperationCodeDefinition entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "OperationCodeDefinition", catalog = "renhai")
public class OperationCodeDefinition implements java.io.Serializable {

	// Fields

	private Integer operationCodeDefinitionId;
	private Integer operationCode;
	private String operationType;
	private String description;

	// Constructors

	/** default constructor */
	public OperationCodeDefinition() {
	}

	/** full constructor */
	public OperationCodeDefinition(Integer operationCode, String operationType,
			String description) {
		this.operationCode = operationCode;
		this.operationType = operationType;
		this.description = description;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "operationCodeDefinitionId", unique = true, nullable = false)
	public Integer getOperationCodeDefinitionId() {
		return this.operationCodeDefinitionId;
	}

	public void setOperationCodeDefinitionId(Integer operationCodeDefinitionId) {
		this.operationCodeDefinitionId = operationCodeDefinitionId;
	}

	@Column(name = "operationCode", nullable = false)
	public Integer getOperationCode() {
		return this.operationCode;
	}

	public void setOperationCode(Integer operationCode) {
		this.operationCode = operationCode;
	}

	@Column(name = "operationType", nullable = false, length = 6)
	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	@Column(name = "description", nullable = false, length = 256)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}