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
 * Impresscard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "impresscard", catalog = "renhai")
public class Impresscard implements java.io.Serializable {

	// Fields

	private Integer impressCardId;
	private Profile profile;
	private Integer chatTotalCount;
	private Integer chatTotalDuration;
	private Integer chatLossCount;
	private Set<Impresslabelmap> impresslabelmaps = new HashSet<Impresslabelmap>(
			0);

	// Constructors

	/** default constructor */
	public Impresscard() {
	}

	/** minimal constructor */
	public Impresscard(Integer chatTotalCount, Integer chatTotalDuration,
			Integer chatLossCount) {
		this.chatTotalCount = chatTotalCount;
		this.chatTotalDuration = chatTotalDuration;
		this.chatLossCount = chatLossCount;
	}

	/** full constructor */
	public Impresscard(Profile profile, Integer chatTotalCount,
			Integer chatTotalDuration, Integer chatLossCount,
			Set<Impresslabelmap> impresslabelmaps) {
		this.profile = profile;
		this.chatTotalCount = chatTotalCount;
		this.chatTotalDuration = chatTotalDuration;
		this.chatLossCount = chatLossCount;
		this.impresslabelmaps = impresslabelmaps;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "impressCardId", unique = true, nullable = false)
	public Integer getImpressCardId() {
		return this.impressCardId;
	}

	public void setImpressCardId(Integer impressCardId) {
		this.impressCardId = impressCardId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profileId")
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Column(name = "chatTotalCount", nullable = false)
	public Integer getChatTotalCount() {
		return this.chatTotalCount;
	}

	public void setChatTotalCount(Integer chatTotalCount) {
		this.chatTotalCount = chatTotalCount;
	}

	@Column(name = "chatTotalDuration", nullable = false)
	public Integer getChatTotalDuration() {
		return this.chatTotalDuration;
	}

	public void setChatTotalDuration(Integer chatTotalDuration) {
		this.chatTotalDuration = chatTotalDuration;
	}

	@Column(name = "chatLossCount", nullable = false)
	public Integer getChatLossCount() {
		return this.chatLossCount;
	}

	public void setChatLossCount(Integer chatLossCount) {
		this.chatLossCount = chatLossCount;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "impresscard")
	public Set<Impresslabelmap> getImpresslabelmaps() {
		return this.impresslabelmaps;
	}

	public void setImpresslabelmaps(Set<Impresslabelmap> impresslabelmaps) {
		this.impresslabelmaps = impresslabelmaps;
	}

}