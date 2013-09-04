package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * GlobalInterestLabel entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "GlobalInterestLabel", catalog = "renhai")
public class GlobalInterestLabel implements java.io.Serializable {

	// Fields

	private Integer globalInterestLabelId;
	private String interestLabel;
	private Integer globalMatchCount;

	// Constructors

	/** default constructor */
	public GlobalInterestLabel() {
	}

	/** full constructor */
	public GlobalInterestLabel(String interestLabel, Integer globalMatchCount) {
		this.interestLabel = interestLabel;
		this.globalMatchCount = globalMatchCount;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "globalInterestLabelId", unique = true, nullable = false)
	public Integer getGlobalInterestLabelId() {
		return this.globalInterestLabelId;
	}

	public void setGlobalInterestLabelId(Integer globalInterestLabelId) {
		this.globalInterestLabelId = globalInterestLabelId;
	}

	@Column(name = "interestLabel", nullable = false, length = 256)
	public String getInterestLabel() {
		return this.interestLabel;
	}

	public void setInterestLabel(String interestLabel) {
		this.interestLabel = interestLabel;
	}

	@Column(name = "globalMatchCount", nullable = false)
	public Integer getGlobalMatchCount() {
		return this.globalMatchCount;
	}

	public void setGlobalMatchCount(Integer globalMatchCount) {
		this.globalMatchCount = globalMatchCount;
	}

}