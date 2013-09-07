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
 * Systemstatistics entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "systemstatistics", catalog = "renhai")
public class Systemstatistics implements java.io.Serializable {

	// Fields

	private Integer systemStatisticsId;
	private Statisticsitemdefinition statisticsitemdefinition;
	private Integer saveTime;
	private Integer count;

	// Constructors

	/** default constructor */
	public Systemstatistics() {
	}

	/** full constructor */
	public Systemstatistics(Statisticsitemdefinition statisticsitemdefinition,
			Integer saveTime, Integer count) {
		this.statisticsitemdefinition = statisticsitemdefinition;
		this.saveTime = saveTime;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "statisticsItem", nullable = false)
	public Statisticsitemdefinition getStatisticsitemdefinition() {
		return this.statisticsitemdefinition;
	}

	public void setStatisticsitemdefinition(
			Statisticsitemdefinition statisticsitemdefinition) {
		this.statisticsitemdefinition = statisticsitemdefinition;
	}

	@Column(name = "saveTime", nullable = false)
	public Integer getSaveTime() {
		return this.saveTime;
	}

	public void setSaveTime(Integer saveTime) {
		this.saveTime = saveTime;
	}

	@Column(name = "count", nullable = false)
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}