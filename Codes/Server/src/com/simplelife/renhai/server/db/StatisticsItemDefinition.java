package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * StatisticsItemDefinition entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "StatisticsItemDefinition", catalog = "renhai")
public class StatisticsItemDefinition implements java.io.Serializable {

	// Fields

	private Integer statisticsItemDefinitionId;
	private Integer statisticsItem;
	private String description;

	// Constructors

	/** default constructor */
	public StatisticsItemDefinition() {
	}

	/** full constructor */
	public StatisticsItemDefinition(Integer statisticsItem, String description) {
		this.statisticsItem = statisticsItem;
		this.description = description;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "statisticsItemDefinitionId", unique = true, nullable = false)
	public Integer getStatisticsItemDefinitionId() {
		return this.statisticsItemDefinitionId;
	}

	public void setStatisticsItemDefinitionId(Integer statisticsItemDefinitionId) {
		this.statisticsItemDefinitionId = statisticsItemDefinitionId;
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

}