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
 * Globalinterestlabel entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "globalinterestlabel", catalog = "renhai")
public class Globalinterestlabel implements java.io.Serializable {

	// Fields

	private Integer globalInterestLabelId;
	private String interestLabelName;
	private Integer globalMatchCount;
	private Set<Hotinterestlabelstatistics> hotinterestlabelstatisticses = new HashSet<Hotinterestlabelstatistics>(
			0);
	private Set<Interestlabelmap> interestlabelmaps = new HashSet<Interestlabelmap>(
			0);

	// Constructors

	/** default constructor */
	public Globalinterestlabel() {
	}

	/** minimal constructor */
	public Globalinterestlabel(String interestLabelName,
			Integer globalMatchCount) {
		this.interestLabelName = interestLabelName;
		this.globalMatchCount = globalMatchCount;
	}

	/** full constructor */
	public Globalinterestlabel(String interestLabelName,
			Integer globalMatchCount,
			Set<Hotinterestlabelstatistics> hotinterestlabelstatisticses,
			Set<Interestlabelmap> interestlabelmaps) {
		this.interestLabelName = interestLabelName;
		this.globalMatchCount = globalMatchCount;
		this.hotinterestlabelstatisticses = hotinterestlabelstatisticses;
		this.interestlabelmaps = interestlabelmaps;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "globalInterestLabelId", unique = true, nullable = false)
	public Integer getGlobalInterestLabelId() {
		return this.globalInterestLabelId;
	}

	public void setGlobalInterestLabelId(Integer globalInterestLabelId) {
		this.globalInterestLabelId = globalInterestLabelId;
	}

	@Column(name = "interestLabelName", nullable = false, length = 256)
	public String getInterestLabelName() {
		return this.interestLabelName;
	}

	public void setInterestLabelName(String interestLabelName) {
		this.interestLabelName = interestLabelName;
	}

	@Column(name = "globalMatchCount", nullable = false)
	public Integer getGlobalMatchCount() {
		return this.globalMatchCount;
	}

	public void setGlobalMatchCount(Integer globalMatchCount) {
		this.globalMatchCount = globalMatchCount;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "globalinterestlabel")
	public Set<Hotinterestlabelstatistics> getHotinterestlabelstatisticses() {
		return this.hotinterestlabelstatisticses;
	}

	public void setHotinterestlabelstatisticses(
			Set<Hotinterestlabelstatistics> hotinterestlabelstatisticses) {
		this.hotinterestlabelstatisticses = hotinterestlabelstatisticses;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "globalinterestlabel")
	public Set<Interestlabelmap> getInterestlabelmaps() {
		return this.interestlabelmaps;
	}

	public void setInterestlabelmaps(Set<Interestlabelmap> interestlabelmaps) {
		this.interestlabelmaps = interestlabelmaps;
	}

}