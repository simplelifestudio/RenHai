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
 * Globalimpresslabel entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "globalimpresslabel", catalog = "renhai")
public class Globalimpresslabel implements java.io.Serializable {

	// Fields

	private Integer globalImpressLabelId;
	private String impressLabelName;
	private Integer globalAssessCount;
	private Set<Impresslabelmap> impresslabelmaps = new HashSet<Impresslabelmap>(
			0);

	// Constructors

	/** default constructor */
	public Globalimpresslabel() {
	}

	/** minimal constructor */
	public Globalimpresslabel(String impressLabelName, Integer globalAssessCount) {
		this.impressLabelName = impressLabelName;
		this.globalAssessCount = globalAssessCount;
	}

	/** full constructor */
	public Globalimpresslabel(String impressLabelName,
			Integer globalAssessCount, Set<Impresslabelmap> impresslabelmaps) {
		this.impressLabelName = impressLabelName;
		this.globalAssessCount = globalAssessCount;
		this.impresslabelmaps = impresslabelmaps;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "globalImpressLabelId", unique = true, nullable = false)
	public Integer getGlobalImpressLabelId() {
		return this.globalImpressLabelId;
	}

	public void setGlobalImpressLabelId(Integer globalImpressLabelId) {
		this.globalImpressLabelId = globalImpressLabelId;
	}

	@Column(name = "impressLabelName", nullable = false, length = 256)
	public String getImpressLabelName() {
		return this.impressLabelName;
	}

	public void setImpressLabelName(String impressLabelName) {
		this.impressLabelName = impressLabelName;
	}

	@Column(name = "globalAssessCount", nullable = false)
	public Integer getGlobalAssessCount() {
		return this.globalAssessCount;
	}

	public void setGlobalAssessCount(Integer globalAssessCount) {
		this.globalAssessCount = globalAssessCount;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "globalimpresslabel")
	public Set<Impresslabelmap> getImpresslabelmaps() {
		return this.impresslabelmaps;
	}

	public void setImpresslabelmaps(Set<Impresslabelmap> impresslabelmaps) {
		this.impresslabelmaps = impresslabelmaps;
	}

}