package com.simplelife.renhai.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Profileoperationlog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="profileoperationlog"
    ,catalog="renhai"
)

public class ProfileOperationLog  implements java.io.Serializable {


    // Fields    

     private Integer profileOperationLogId;
     private Profile profile;
     private OperationCodeDefinition operationcodedefinition;
     private DeviceCard devicecard;
     private Integer logTime;
     private String logInfo;


    // Constructors

    /** default constructor */
    public ProfileOperationLog() {
    }

	/** minimal constructor */
    public ProfileOperationLog(Profile profile, OperationCodeDefinition operationcodedefinition, DeviceCard devicecard, Integer logTime) {
        this.profile = profile;
        this.operationcodedefinition = operationcodedefinition;
        this.devicecard = devicecard;
        this.logTime = logTime;
    }
    
    /** full constructor */
    public ProfileOperationLog(Profile profile, OperationCodeDefinition operationcodedefinition, DeviceCard devicecard, Integer logTime, String logInfo) {
        this.profile = profile;
        this.operationcodedefinition = operationcodedefinition;
        this.devicecard = devicecard;
        this.logTime = logTime;
        this.logInfo = logInfo;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="profileOperationLogId", unique=true, nullable=false)

    public Integer getProfileOperationLogId() {
        return this.profileOperationLogId;
    }
    
    public void setProfileOperationLogId(Integer profileOperationLogId) {
        this.profileOperationLogId = profileOperationLogId;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="profileId", nullable=false)

    public Profile getProfile() {
        return this.profile;
    }
    
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="operationCode", nullable=false)

    public OperationCodeDefinition getOperationcodedefinition() {
        return this.operationcodedefinition;
    }
    
    public void setOperationcodedefinition(OperationCodeDefinition operationcodedefinition) {
        this.operationcodedefinition = operationcodedefinition;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="deviceId", nullable=false)

    public DeviceCard getDevicecard() {
        return this.devicecard;
    }
    
    public void setDevicecard(DeviceCard devicecard) {
        this.devicecard = devicecard;
    }
    
    @Column(name="logTime", nullable=false)

    public Integer getLogTime() {
        return this.logTime;
    }
    
    public void setLogTime(Integer logTime) {
        this.logTime = logTime;
    }
    
    @Column(name="logInfo", length=256)

    public String getLogInfo() {
        return this.logInfo;
    }
    
    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }
   








}