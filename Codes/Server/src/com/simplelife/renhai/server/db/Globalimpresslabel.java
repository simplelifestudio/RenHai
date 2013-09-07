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
	private String impressLabel;
	private Integer globalAssessCount;
	private Set<Impresslabelcollection> impresslabelcollections = new HashSet<Impresslabelcollection>(
			0);

	// Constructors

	/** default constructor */
	public Globalimpresslabel() {
	}

	/** minimal constructor */
	public Globalimpresslabel(String impressLabel, Integer globalAssessCount) {
		this.impressLabel = impressLabel;
		this.globalAssessCount = globalAssessCount;
	}

	/** full constructor */
	public Globalimpresslabel(String impressLabel, Integer globalAssessCount,
			Set<Impresslabelcollection> impresslabelcollections) {
		this.impressLabel = impressLabel;
		this.globalAssessCount = globalAssessCount;
		this.impresslabelcollections = impresslabelcollections;
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

	@Column(name = "impressLabel", nullable = false, length = 256)
	public String getImpressLabel() {
		return this.impressLabel;
	}

	public void setImpressLabel(String impressLabel) {
		this.impressLabel = impressLabel;
	}

	@Column(name = "globalAssessCount", nullable = false)
	public Integer getGlobalAssessCount() {
		return this.globalAssessCount;
	}

	public void setGlobalAssessCount(Integer globalAssessCount) {
		this.globalAssessCount = globalAssessCount;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "globalimpresslabel")
	public Set<Impresslabelcollection> getImpresslabelcollections() {
		return this.impresslabelcollections;
	}

	public void setImpresslabelcollections(
			Set<Impresslabelcollection> impresslabelcollections) {
		this.impresslabelcollections = impresslabelcollections;
	}

}