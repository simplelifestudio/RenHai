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
 * Interestcard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "interestcard", catalog = "renhai")
public class Interestcard implements java.io.Serializable {

	// Fields

	private Integer interestCardId;
	private Long createTime;
	private Set<Interestlabelcollection> interestlabelcollections = new HashSet<Interestlabelcollection>(
			0);
	private Set<Profile> profiles = new HashSet<Profile>(0);

	// Constructors

	/** default constructor */
	public Interestcard() {
	}

	/** minimal constructor */
	public Interestcard(Long createTime) {
		this.createTime = createTime;
	}

	/** full constructor */
	public Interestcard(Long createTime,
			Set<Interestlabelcollection> interestlabelcollections,
			Set<Profile> profiles) {
		this.createTime = createTime;
		this.interestlabelcollections = interestlabelcollections;
		this.profiles = profiles;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "interestCardId", unique = true, nullable = false)
	public Integer getInterestcardId() {
		return this.interestCardId;
	}

	public void setInterestcardId(Integer interestCardId) {
		this.interestCardId = interestCardId;
	}

	@Column(name = "createTime", nullable = false)
	public Long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "interestcard")
	public Set<Interestlabelcollection> getInterestlabelcollections() {
		return this.interestlabelcollections;
	}

	public void setInterestlabelcollections(
			Set<Interestlabelcollection> interestlabelcollections) {
		this.interestlabelcollections = interestlabelcollections;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "interestcard")
	public Set<Profile> getProfiles() {
		return this.profiles;
	}

	public void setProfiles(Set<Profile> profiles) {
		this.profiles = profiles;
	}

}