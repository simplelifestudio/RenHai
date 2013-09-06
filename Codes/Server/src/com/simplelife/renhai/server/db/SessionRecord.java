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
 * Sessionrecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="sessionrecord"
    ,catalog="renhai"
)

public class SessionRecord  implements java.io.Serializable {


    // Fields    

     private Integer sessionRecordId;
     private String businessType;
     private Long startTime;
     private Integer duration;
     private String endStatus;
     private String endReason;
     private Set<SessionProfileCollection> sessionprofilecollections = new HashSet<SessionProfileCollection>(0);


    // Constructors

    /** default constructor */
    public SessionRecord() {
    }

	/** minimal constructor */
    public SessionRecord(String businessType, Long startTime, Integer duration, String endStatus, String endReason) {
        this.businessType = businessType;
        this.startTime = startTime;
        this.duration = duration;
        this.endStatus = endStatus;
        this.endReason = endReason;
    }
    
    /** full constructor */
    public SessionRecord(String businessType, Long startTime, Integer duration, String endStatus, String endReason, Set<SessionProfileCollection> sessionprofilecollections) {
        this.businessType = businessType;
        this.startTime = startTime;
        this.duration = duration;
        this.endStatus = endStatus;
        this.endReason = endReason;
        this.sessionprofilecollections = sessionprofilecollections;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="sessionRecordId", unique=true, nullable=false)

    public Integer getSessionRecordId() {
        return this.sessionRecordId;
    }
    
    public void setSessionRecordId(Integer sessionRecordId) {
        this.sessionRecordId = sessionRecordId;
    }
    
    @Column(name="businessType", nullable=false, length=8)

    public String getBusinessType() {
        return this.businessType;
    }
    
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    @Column(name="startTime", nullable=false)

    public Long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
    
    @Column(name="duration", nullable=false)

    public Integer getDuration() {
        return this.duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    @Column(name="endStatus", nullable=false, length=12)

    public String getEndStatus() {
        return this.endStatus;
    }
    
    public void setEndStatus(String endStatus) {
        this.endStatus = endStatus;
    }
    
    @Column(name="endReason", nullable=false, length=14)

    public String getEndReason() {
        return this.endReason;
    }
    
    public void setEndReason(String endReason) {
        this.endReason = endReason;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="sessionrecord")

    public Set<SessionProfileCollection> getSessionprofilecollections() {
        return this.sessionprofilecollections;
    }
    
    public void setSessionprofilecollections(Set<SessionProfileCollection> sessionprofilecollections) {
        this.sessionprofilecollections = sessionprofilecollections;
    }
   








}