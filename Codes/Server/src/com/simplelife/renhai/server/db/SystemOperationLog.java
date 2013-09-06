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
 * Systemoperationlog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="systemoperationlog"
    ,catalog="renhai"
)

public class SystemOperationLog  implements java.io.Serializable {


    // Fields    

     private Integer systemOperationLogId;
     private ModuleDefinition moduledefinition;
     private OperationCodeDefinition operationcodedefinition;
     private Long logTime;
     private String logInfo;


    // Constructors

    /** default constructor */
    public SystemOperationLog() {
    }

	/** minimal constructor */
    public SystemOperationLog(ModuleDefinition moduledefinition, OperationCodeDefinition operationcodedefinition, Long logTime) {
        this.moduledefinition = moduledefinition;
        this.operationcodedefinition = operationcodedefinition;
        this.logTime = logTime;
    }
    
    /** full constructor */
    public SystemOperationLog(ModuleDefinition moduledefinition, OperationCodeDefinition operationcodedefinition, Long logTime, String logInfo) {
        this.moduledefinition = moduledefinition;
        this.operationcodedefinition = operationcodedefinition;
        this.logTime = logTime;
        this.logInfo = logInfo;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="systemOperationLogId", unique=true, nullable=false)

    public Integer getSystemOperationLogId() {
        return this.systemOperationLogId;
    }
    
    public void setSystemOperationLogId(Integer systemOperationLogId) {
        this.systemOperationLogId = systemOperationLogId;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="moduleId", nullable=false)

    public ModuleDefinition getModuledefinition() {
        return this.moduledefinition;
    }
    
    public void setModuledefinition(ModuleDefinition moduledefinition) {
        this.moduledefinition = moduledefinition;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="operationCode", nullable=false)

    public OperationCodeDefinition getOperationcodedefinition() {
        return this.operationcodedefinition;
    }
    
    public void setOperationcodedefinition(OperationCodeDefinition operationcodedefinition) {
        this.operationcodedefinition = operationcodedefinition;
    }
    
    @Column(name="logTime", nullable=false)

    public Long getLogTime() {
        return this.logTime;
    }
    
    public void setLogTime(Long logTime) {
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