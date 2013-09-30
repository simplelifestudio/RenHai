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
 * Interestlabelmap entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "interestlabelmap", catalog = "renhai")
public class Interestlabelmap implements java.io.Serializable {

	// Fields

	private Integer interestLabelMaplId;
	private Interestcard interestcard;
	private Globalinterestlabel globalinterestlabel;
	private Integer labelOrder;
	private Integer matchCount;
	private String validFlag;

	// Constructors

	/** default constructor */
	public Interestlabelmap() {
	}

	/** full constructor */
	public Interestlabelmap(Interestcard interestcard,
			Globalinterestlabel globalinterestlabel, Integer labelOrder,
			Integer matchCount, String validFlag) {
		this.interestcard = interestcard;
		this.globalinterestlabel = globalinterestlabel;
		this.labelOrder = labelOrder;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interestCardId", nullable = false)
	public Interestcard getInterestcard() {
		return this.interestcard;
	}

	public void setInterestcard(Interestcard interestcard) {
		this.interestcard = interestcard;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "globalInterestLabelId", nullable = false)
	public Globalinterestlabel getGlobalinterestlabel() {
		return this.globalinterestlabel;
	}

	public void setGlobalinterestlabel(Globalinterestlabel globalinterestlabel) {
		this.globalinterestlabel = globalinterestlabel;
	}

	@Column(name = "labelOrder", nullable = false)
	public Integer getLabelOrder() {
		return this.labelOrder;
	}

	public void setLabelOrder(Integer labelOrder) {
		this.labelOrder = labelOrder;
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