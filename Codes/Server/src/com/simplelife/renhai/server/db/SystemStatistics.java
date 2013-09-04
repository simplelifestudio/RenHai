package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SystemStatistics entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "SystemStatistics", catalog = "renhai")
public class SystemStatistics implements java.io.Serializable {

	// Fields

	private Integer systemStatisticsId;
	private Integer saveTime;
	private Integer statisticsItem;
	private Integer count;

	// Constructors

	/** default constructor */
	public SystemStatistics() {
	}

	/** full constructor */
	public SystemStatistics(Integer saveTime, Integer statisticsItem,
			Integer count) {
		this.saveTime = saveTime;
		this.statisticsItem = statisticsItem;
		this.count = count;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "systemStatisticsId", unique = true, nullable = false)
	public Integer getSystemStatisticsId() {
		return this.systemStatisticsId;
	}

	public void setSystemStatisticsId(Integer systemStatisticsId) {
		this.systemStatisticsId = systemStatisticsId;
	}

	@Column(name = "saveTime", nullable = false)
	public Integer getSaveTime() {
		return this.saveTime;
	}

	public void setSaveTime(Integer saveTime) {
		this.saveTime = saveTime;
	}

	@Column(name = "statisticsItem", nullable = false)
	public Integer getStatisticsItem() {
		return this.statisticsItem;
	}

	public void setStatisticsItem(Integer statisticsItem) {
		this.statisticsItem = statisticsItem;
	}

	@Column(name = "count", nullable = false)
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}