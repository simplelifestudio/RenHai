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
 * Hotinterestlabelstatistics entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="hotinterestlabelstatistics"
    ,catalog="renhai"
)

public class HotInterestLabelStatistics  implements java.io.Serializable {


    // Fields    

     private Integer hotInterestLabelStatisticsId;
     private GlobalInterestLabel globalinterestlabel;
     private Long saveTime;
     private Integer count;


    // Constructors

    /** default constructor */
    public HotInterestLabelStatistics() {
    }

    
    /** full constructor */
    public HotInterestLabelStatistics(GlobalInterestLabel globalinterestlabel, Long saveTime, Integer count) {
        this.globalinterestlabel = globalinterestlabel;
        this.saveTime = saveTime;
        this.count = count;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="hotInterestLabelStatisticsId", unique=true, nullable=false)

    public Integer getHotInterestLabelStatisticsId() {
        return this.hotInterestLabelStatisticsId;
    }
    
    public void setHotInterestLabelStatisticsId(Integer hotInterestLabelStatisticsId) {
        this.hotInterestLabelStatisticsId = hotInterestLabelStatisticsId;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="globalInterestLabelId", nullable=false)

    public GlobalInterestLabel getGlobalinterestlabel() {
        return this.globalinterestlabel;
    }
    
    public void setGlobalinterestlabel(GlobalInterestLabel globalinterestlabel) {
        this.globalinterestlabel = globalinterestlabel;
    }
    
    @Column(name="saveTime", nullable=false)

    public Long getSaveTime() {
        return this.saveTime;
    }
    
    public void setSaveTime(Long saveTime) {
        this.saveTime = saveTime;
    }
    
    @Column(name="count", nullable=false)

    public Integer getCount() {
        return this.count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
   








}