package com.simplelife.renhai.server.db;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private Profile profile;
	private Set<Interestlabelmap> interestlabelmaps = new HashSet<Interestlabelmap>(
			0);

	// Constructors

	/** default constructor */
	public Interestcard() {
	}

	/** minimal constructor */
	public Interestcard(Profile profile) {
		this.profile = profile;
	}

	/** full constructor */
	public Interestcard(Profile profile, Set<Interestlabelmap> interestlabelmaps) {
		this.profile = profile;
		this.interestlabelmaps = interestlabelmaps;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profileId", nullable = false)
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "interestcard")
	public Set<Interestlabelmap> getInterestlabelmaps() {
		return this.interestlabelmaps;
	}

	public void setInterestlabelmaps(Set<Interestlabelmap> interestlabelmaps) {
		this.interestlabelmaps = interestlabelmaps;
	}

}