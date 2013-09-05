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
 * Moduledefinition entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="moduledefinition"
    ,catalog="renhai"
)

public class Moduledefinition  implements java.io.Serializable {


    // Fields    

     private Integer moduleDefinitionId;
     private Integer moduleId;
     private String description;
     private Set<Systemoperationlog> systemoperationlogs = new HashSet<Systemoperationlog>(0);


    // Constructors

    /** default constructor */
    public Moduledefinition() {
    }

	/** minimal constructor */
    public Moduledefinition(Integer moduleId, String description) {
        this.moduleId = moduleId;
        this.description = description;
    }
    
    /** full constructor */
    public Moduledefinition(Integer moduleId, String description, Set<Systemoperationlog> systemoperationlogs) {
        this.moduleId = moduleId;
        this.description = description;
        this.systemoperationlogs = systemoperationlogs;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="moduleDefinitionId", unique=true, nullable=false)

    public Integer getModuleDefinitionId() {
        return this.moduleDefinitionId;
    }
    
    public void setModuleDefinitionId(Integer moduleDefinitionId) {
        this.moduleDefinitionId = moduleDefinitionId;
    }
    
    @Column(name="moduleId", nullable=false)

    public Integer getModuleId() {
        return this.moduleId;
    }
    
    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }
    
    @Column(name="description", nullable=false, length=256)

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="moduledefinition")

    public Set<Systemoperationlog> getSystemoperationlogs() {
        return this.systemoperationlogs;
    }
    
    public void setSystemoperationlogs(Set<Systemoperationlog> systemoperationlogs) {
        this.systemoperationlogs = systemoperationlogs;
    }
   








}