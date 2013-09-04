package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ImpressCard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "ImpressCard", catalog = "renhai")
public class ImpressCard implements java.io.Serializable {

	// Fields

	private Integer impressCardId;
	private Integer chatTotalCount;
	private Integer chatTotalDuration;
	private Integer chatLossCount;

	// Constructors

	/** default constructor */
	public ImpressCard() {
	}

	/** full constructor */
	public ImpressCard(Integer chatTotalCount, Integer chatTotalDuration,
			Integer chatLossCount) {
		this.chatTotalCount = chatTotalCount;
		this.chatTotalDuration = chatTotalDuration;
		this.chatLossCount = chatLossCount;
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

}