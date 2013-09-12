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
	private Statisticsitem statisticsitem;
	private Integer saveTime;
	private Integer count;

	// Constructors

	/** default constructor */
	public Systemstatistics() {
	}

	/** full constructor */
	public Systemstatistics(Statisticsitem statisticsitem, Integer saveTime,
			Integer count) {
		this.statisticsitem = statisticsitem;
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
	@JoinColumn(name = "statisticsItemId", nullable = false)
	public Statisticsitem getStatisticsitem() {
		return this.statisticsitem;
	}

	public void setStatisticsitem(Statisticsitem statisticsitem) {
		this.statisticsitem = statisticsitem;
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