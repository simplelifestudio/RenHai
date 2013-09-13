package com.simplelife.renhai.server.db;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Impresslabelmap entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "impresslabelmap", catalog = "renhai")
public class Impresslabelmap implements java.io.Serializable {

	// Fields

	private Integer impressLabelMaplId;
	private Globalimpresslabel globalimpresslabel;
	private Impresscard impresscard;
	private Integer assessedCount;
	private Long updateTime;
	private Integer assessCount;

	// Constructors

	/** default constructor */
	public Impresslabelmap() {
	}

	/** full constructor */
	public Impresslabelmap(Globalimpresslabel globalimpresslabel,
			Impresscard impresscard, Integer assessedCount, Long updateTime,
			Integer assessCount) {
		this.globalimpresslabel = globalimpresslabel;
		this.impresscard = impresscard;
		this.assessedCount = assessedCount;
		this.updateTime = updateTime;
		this.assessCount = assessCount;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "impressLabelMaplId", unique = true, nullable = false)
	public Integer getImpressLabelMaplId() {
		return this.impressLabelMaplId;
	}

	public void setImpressLabelMaplId(Integer impressLabelMaplId) {
		this.impressLabelMaplId = impressLabelMaplId;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "globalImpressLabelId", nullable = false)
	public Globalimpresslabel getGlobalimpresslabel() {
		return this.globalimpresslabel;
	}

	public void setGlobalimpresslabel(Globalimpresslabel globalimpresslabel) {
		this.globalimpresslabel = globalimpresslabel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "impressCardId", nullable = false)
	public Impresscard getImpresscard() {
		return this.impresscard;
	}

	public void setImpresscard(Impresscard impresscard) {
		this.impresscard = impresscard;
	}

	@Column(name = "assessedCount", nullable = false)
	public Integer getAssessedCount() {
		return this.assessedCount;
	}

	public void setAssessedCount(Integer assessedCount) {
		this.assessedCount = assessedCount;
	}

	@Column(name = "updateTime", nullable = false)
	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "assessCount", nullable = false)
	public Integer getAssessCount() {
		return this.assessCount;
	}

	public void setAssessCount(Integer assessCount) {
		this.assessCount = assessCount;
	}

}