package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * HotInterestLabelStatistics entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "HotInterestLabelStatistics", catalog = "renhai")
public class HotInterestLabelStatistics implements java.io.Serializable {

	// Fields

	private Integer hotInterestLabelStatisticsId;
	private long saveTime;
	private Integer globalInterestLabelId;
	private Integer count;

	// Constructors

	/** default constructor */
	public HotInterestLabelStatistics() {
	}

	/** full constructor */
	public HotInterestLabelStatistics(long saveTime,
			Integer globalInterestLabelId, Integer count) {
		this.saveTime = saveTime;
		this.globalInterestLabelId = globalInterestLabelId;
		this.count = count;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "hotInterestLabelStatisticsId", unique = true, nullable = false)
	public Integer getHotInterestLabelStatisticsId() {
		return this.hotInterestLabelStatisticsId;
	}

	public void setHotInterestLabelStatisticsId(
			Integer hotInterestLabelStatisticsId) {
		this.hotInterestLabelStatisticsId = hotInterestLabelStatisticsId;
	}

	@Column(name = "saveTime", nullable = false)
	public long getSaveTime() {
		return this.saveTime;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	@Column(name = "globalInterestLabelId", nullable = false)
	public Integer getGlobalInterestLabelId() {
		return this.globalInterestLabelId;
	}

	public void setGlobalInterestLabelId(Integer globalInterestLabelId) {
		this.globalInterestLabelId = globalInterestLabelId;
	}

	@Column(name = "count", nullable = false)
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}