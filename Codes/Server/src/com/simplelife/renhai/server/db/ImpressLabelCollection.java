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
 * Impresslabelcollection entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="impresslabelcollection"
    ,catalog="renhai"
)

public class ImpressLabelCollection  implements java.io.Serializable {


    // Fields    

     private Integer impressLabelMaplId;
     private GlobalImpressLabel globalimpresslabel;
     private ImpressCard impresscard;
     private Integer count;
     private Long updateTime;
     private Integer assessCount;


    // Constructors

    /** default constructor */
    public ImpressLabelCollection() {
    }

    
    /** full constructor */
    public ImpressLabelCollection(GlobalImpressLabel globalimpresslabel, ImpressCard impresscard, Integer count, Long updateTime, Integer assessCount) {
        this.globalimpresslabel = globalimpresslabel;
        this.impresscard = impresscard;
        this.count = count;
        this.updateTime = updateTime;
        this.assessCount = assessCount;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="impressLabelMaplId", unique=true, nullable=false)

    public Integer getImpressLabelMaplId() {
        return this.impressLabelMaplId;
    }
    
    public void setImpressLabelMaplId(Integer impressLabelMaplId) {
        this.impressLabelMaplId = impressLabelMaplId;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="globalImpressLabelId", nullable=false)

    public GlobalImpressLabel getGlobalimpresslabel() {
        return this.globalimpresslabel;
    }
    
    public void setGlobalimpresslabel(GlobalImpressLabel globalimpresslabel) {
        this.globalimpresslabel = globalimpresslabel;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="impressCardId", nullable=false)

    public ImpressCard getImpresscard() {
        return this.impresscard;
    }
    
    public void setImpresscard(ImpressCard impresscard) {
        this.impresscard = impresscard;
    }
    
    @Column(name="count", nullable=false)

    public Integer getCount() {
        return this.count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
    
    @Column(name="updateTime", nullable=false)

    public Long getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    
    @Column(name="assessCount", nullable=false)

    public Integer getAssessCount() {
        return this.assessCount;
    }
    
    public void setAssessCount(Integer assessCount) {
        this.assessCount = assessCount;
    }
   








}