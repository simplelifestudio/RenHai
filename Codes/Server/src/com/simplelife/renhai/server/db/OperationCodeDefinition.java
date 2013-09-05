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
 * Operationcodedefinition entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="operationcodedefinition"
    ,catalog="renhai"
)

public class Operationcodedefinition  implements java.io.Serializable {


    // Fields    

     private Integer operationCodeDefinitionId;
     private Integer operationCode;
     private String operationType;
     private String description;
     private Set<Profileoperationlog> profileoperationlogs = new HashSet<Profileoperationlog>(0);
     private Set<Systemoperationlog> systemoperationlogs = new HashSet<Systemoperationlog>(0);


    // Constructors

    /** default constructor */
    public Operationcodedefinition() {
    }

	/** minimal constructor */
    public Operationcodedefinition(Integer operationCode, String operationType, String description) {
        this.operationCode = operationCode;
        this.operationType = operationType;
        this.description = description;
    }
    
    /** full constructor */
    public Operationcodedefinition(Integer operationCode, String operationType, String description, Set<Profileoperationlog> profileoperationlogs, Set<Systemoperationlog> systemoperationlogs) {
        this.operationCode = operationCode;
        this.operationType = operationType;
        this.description = description;
        this.profileoperationlogs = profileoperationlogs;
        this.systemoperationlogs = systemoperationlogs;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="operationCodeDefinitionId", unique=true, nullable=false)

    public Integer getOperationCodeDefinitionId() {
        return this.operationCodeDefinitionId;
    }
    
    public void setOperationCodeDefinitionId(Integer operationCodeDefinitionId) {
        this.operationCodeDefinitionId = operationCodeDefinitionId;
    }
    
    @Column(name="operationCode", nullable=false)

    public Integer getOperationCode() {
        return this.operationCode;
    }
    
    public void setOperationCode(Integer operationCode) {
        this.operationCode = operationCode;
    }
    
    @Column(name="operationType", nullable=false, length=6)

    public String getOperationType() {
        return this.operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    @Column(name="description", nullable=false, length=256)

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="operationcodedefinition")

    public Set<Profileoperationlog> getProfileoperationlogs() {
        return this.profileoperationlogs;
    }
    
    public void setProfileoperationlogs(Set<Profileoperationlog> profileoperationlogs) {
        this.profileoperationlogs = profileoperationlogs;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="operationcodedefinition")

    public Set<Systemoperationlog> getSystemoperationlogs() {
        return this.systemoperationlogs;
    }
    
    public void setSystemoperationlogs(Set<Systemoperationlog> systemoperationlogs) {
        this.systemoperationlogs = systemoperationlogs;
    }
   








}