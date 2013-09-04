package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ImpressLabelCollection entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "ImpressLabelCollection", catalog = "renhai")
public class ImpressLabelCollection implements java.io.Serializable {

	// Fields

	private Integer impressLabelMaplId;
	private Integer impressCardId;
	private Integer globalImpressLabelId;
	private Integer count;
	private long updateTime;
	private Integer assessCount;

	// Constructors

	/** default constructor */
	public ImpressLabelCollection() {
	}

	/** full constructor */
	public ImpressLabelCollection(Integer impressCardId,
			Integer globalImpressLabelId, Integer count, long updateTime,
			Integer assessCount) {
		this.impressCardId = impressCardId;
		this.globalImpressLabelId = globalImpressLabelId;
		this.count = count;
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

	@Column(name = "impressCardId", nullable = false)
	public Integer getImpressCardId() {
		return this.impressCardId;
	}

	public void setImpressCardId(Integer impressCardId) {
		this.impressCardId = impressCardId;
	}

	@Column(name = "globalImpressLabelId", nullable = false)
	public Integer getGlobalImpressLabelId() {
		return this.globalImpressLabelId;
	}

	public void setGlobalImpressLabelId(Integer globalImpressLabelId) {
		this.globalImpressLabelId = globalImpressLabelId;
	}

	@Column(name = "count", nullable = false)
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Column(name = "updateTime", nullable = false)
	public long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(long updateTime) {
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