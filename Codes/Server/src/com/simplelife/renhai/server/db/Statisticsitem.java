package com.simplelife.renhai.server.db;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Statisticsitem entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "statisticsitem", catalog = "renhai")
public class Statisticsitem implements java.io.Serializable {

	// Fields

	private Integer statisticsitemId;
	private Integer statisticsItem;
	private String description;
	private Set<Systemstatistics> systemstatisticses = new HashSet<Systemstatistics>(
			0);

	// Constructors

	/** default constructor */
	public Statisticsitem() {
	}

	/** minimal constructor */
	public Statisticsitem(Integer statisticsItem, String description) {
		this.statisticsItem = statisticsItem;
		this.description = description;
	}

	/** full constructor */
	public Statisticsitem(Integer statisticsItem, String description,
			Set<Systemstatistics> systemstatisticses) {
		this.statisticsItem = statisticsItem;
		this.description = description;
		this.systemstatisticses = systemstatisticses;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "statisticsitemId", unique = true, nullable = false)
	public Integer getStatisticsitemId() {
		return this.statisticsitemId;
	}

	public void setStatisticsitemId(Integer statisticsitemId) {
		this.statisticsitemId = statisticsitemId;
	}

	@Column(name = "statisticsItem", nullable = false)
	public Integer getStatisticsItem() {
		return this.statisticsItem;
	}

	public void setStatisticsItem(Integer statisticsItem) {
		this.statisticsItem = statisticsItem;
	}

	@Column(name = "description", nullable = false, length = 256)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "statisticsitem")
	public Set<Systemstatistics> getSystemstatisticses() {
		return this.systemstatisticses;
	}

	public void setSystemstatisticses(Set<Systemstatistics> systemstatisticses) {
		this.systemstatisticses = systemstatisticses;
	}

}