package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * InterestCard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "InterestCard", catalog = "renhai")
public class InterestCard implements java.io.Serializable {

	// Fields

	private Integer interestCardId;
	private long createTime;

	// Constructors

	/** default constructor */
	public InterestCard() {
	}

	/** full constructor */
	public InterestCard(long createTime) {
		this.createTime = createTime;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "interestCardId", unique = true, nullable = false)
	public Integer getInterestCardId() {
		return this.interestCardId;
	}

	public void setInterestCardId(Integer interestCardId) {
		this.interestCardId = interestCardId;
	}

	@Column(name = "createTime", nullable = false)
	public long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}