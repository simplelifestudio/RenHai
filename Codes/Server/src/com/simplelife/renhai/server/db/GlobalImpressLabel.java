package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * GlobalImpressLabel entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "GlobalImpressLabel", catalog = "renhai")
public class GlobalImpressLabel implements java.io.Serializable {

	// Fields

	private Integer globalImpressLabelId;
	private String impressLabel;
	private Integer globalAssessCount;

	// Constructors

	/** default constructor */
	public GlobalImpressLabel() {
	}

	/** full constructor */
	public GlobalImpressLabel(String impressLabel, Integer globalAssessCount) {
		this.impressLabel = impressLabel;
		this.globalAssessCount = globalAssessCount;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "globalImpressLabelId", unique = true, nullable = false)
	public Integer getGlobalImpressLabelId() {
		return this.globalImpressLabelId;
	}

	public void setGlobalImpressLabelId(Integer globalImpressLabelId) {
		this.globalImpressLabelId = globalImpressLabelId;
	}

	@Column(name = "impressLabel", nullable = false, length = 256)
	public String getImpressLabel() {
		return this.impressLabel;
	}

	public void setImpressLabel(String impressLabel) {
		this.impressLabel = impressLabel;
	}

	@Column(name = "globalAssessCount", nullable = false)
	public Integer getGlobalAssessCount() {
		return this.globalAssessCount;
	}

	public void setGlobalAssessCount(Integer globalAssessCount) {
		this.globalAssessCount = globalAssessCount;
	}

}