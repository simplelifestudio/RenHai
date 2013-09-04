package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * InterestLabelCollection entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "InterestLabelCollection", catalog = "renhai")
public class InterestLabelCollection implements java.io.Serializable {

	// Fields

	private Integer interestLabelMaplId;
	private Integer interestCardId;
	private Integer globalInterestLabelId;
	private Integer order;
	private Integer matchCount;
	private String validFlag;

	// Constructors

	/** default constructor */
	public InterestLabelCollection() {
	}

	/** full constructor */
	public InterestLabelCollection(Integer interestCardId,
			Integer globalInterestLabelId, Integer order, Integer matchCount,
			String validFlag) {
		this.interestCardId = interestCardId;
		this.globalInterestLabelId = globalInterestLabelId;
		this.order = order;
		this.matchCount = matchCount;
		this.validFlag = validFlag;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "interestLabelMaplId", unique = true, nullable = false)
	public Integer getInterestLabelMaplId() {
		return this.interestLabelMaplId;
	}

	public void setInterestLabelMaplId(Integer interestLabelMaplId) {
		this.interestLabelMaplId = interestLabelMaplId;
	}

	@Column(name = "interestCardId", nullable = false)
	public Integer getInterestCardId() {
		return this.interestCardId;
	}

	public void setInterestCardId(Integer interestCardId) {
		this.interestCardId = interestCardId;
	}

	@Column(name = "globalInterestLabelId", nullable = false)
	public Integer getGlobalInterestLabelId() {
		return this.globalInterestLabelId;
	}

	public void setGlobalInterestLabelId(Integer globalInterestLabelId) {
		this.globalInterestLabelId = globalInterestLabelId;
	}

	@Column(name = "order", nullable = false)
	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Column(name = "matchCount", nullable = false)
	public Integer getMatchCount() {
		return this.matchCount;
	}

	public void setMatchCount(Integer matchCount) {
		this.matchCount = matchCount;
	}

	@Column(name = "validFlag", nullable = false, length = 7)
	public String getValidFlag() {
		return this.validFlag;
	}

	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}

}