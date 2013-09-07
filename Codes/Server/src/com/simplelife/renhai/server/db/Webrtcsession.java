package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Webrtcsession entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "webrtcsession", catalog = "renhai")
public class Webrtcsession implements java.io.Serializable {

	// Fields

	private Integer webRtcSessionId;
	private String webrtcsession;
	private Integer requestDate;
	private String token;
	private Integer tokenUpdateDate;
	private String expirationDate;

	// Constructors

	/** default constructor */
	public Webrtcsession() {
	}

	/** full constructor */
	public Webrtcsession(String webrtcsession, Integer requestDate,
			String token, Integer tokenUpdateDate, String expirationDate) {
		this.webrtcsession = webrtcsession;
		this.requestDate = requestDate;
		this.token = token;
		this.tokenUpdateDate = tokenUpdateDate;
		this.expirationDate = expirationDate;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "webRtcSessionId", unique = true, nullable = false)
	public Integer getWebRtcSessionId() {
		return this.webRtcSessionId;
	}

	public void setWebRtcSessionId(Integer webRtcSessionId) {
		this.webRtcSessionId = webRtcSessionId;
	}

	@Column(name = "webrtcsession", nullable = false, length = 256)
	public String getWebrtcsession() {
		return this.webrtcsession;
	}

	public void setWebrtcsession(String webrtcsession) {
		this.webrtcsession = webrtcsession;
	}

	@Column(name = "requestDate", nullable = false)
	public Integer getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(Integer requestDate) {
		this.requestDate = requestDate;
	}

	@Column(name = "token", nullable = false, length = 256)
	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column(name = "tokenUpdateDate", nullable = false)
	public Integer getTokenUpdateDate() {
		return this.tokenUpdateDate;
	}

	public void setTokenUpdateDate(Integer tokenUpdateDate) {
		this.tokenUpdateDate = tokenUpdateDate;
	}

	@Column(name = "expirationDate", nullable = false, length = 256)
	public String getExpirationDate() {
		return this.expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

}