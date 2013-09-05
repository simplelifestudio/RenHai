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
 * Globalinterestlabel entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="globalinterestlabel"
    ,catalog="renhai"
)

public class Globalinterestlabel  implements java.io.Serializable {


    // Fields    

     private Integer globalInterestLabelId;
     private String interestLabel;
     private Integer globalMatchCount;
     private Set<Hotinterestlabelstatistics> hotinterestlabelstatisticses = new HashSet<Hotinterestlabelstatistics>(0);
     private Set<Interestlabelcollection> interestlabelcollections = new HashSet<Interestlabelcollection>(0);


    // Constructors

    /** default constructor */
    public Globalinterestlabel() {
    }

	/** minimal constructor */
    public Globalinterestlabel(String interestLabel, Integer globalMatchCount) {
        this.interestLabel = interestLabel;
        this.globalMatchCount = globalMatchCount;
    }
    
    /** full constructor */
    public Globalinterestlabel(String interestLabel, Integer globalMatchCount, Set<Hotinterestlabelstatistics> hotinterestlabelstatisticses, Set<Interestlabelcollection> interestlabelcollections) {
        this.interestLabel = interestLabel;
        this.globalMatchCount = globalMatchCount;
        this.hotinterestlabelstatisticses = hotinterestlabelstatisticses;
        this.interestlabelcollections = interestlabelcollections;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="globalInterestLabelId", unique=true, nullable=false)

    public Integer getGlobalInterestLabelId() {
        return this.globalInterestLabelId;
    }
    
    public void setGlobalInterestLabelId(Integer globalInterestLabelId) {
        this.globalInterestLabelId = globalInterestLabelId;
    }
    
    @Column(name="interestLabel", nullable=false, length=256)

    public String getInterestLabel() {
        return this.interestLabel;
    }
    
    public void setInterestLabel(String interestLabel) {
        this.interestLabel = interestLabel;
    }
    
    @Column(name="globalMatchCount", nullable=false)

    public Integer getGlobalMatchCount() {
        return this.globalMatchCount;
    }
    
    public void setGlobalMatchCount(Integer globalMatchCount) {
        this.globalMatchCount = globalMatchCount;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="globalinterestlabel")

    public Set<Hotinterestlabelstatistics> getHotinterestlabelstatisticses() {
        return this.hotinterestlabelstatisticses;
    }
    
    public void setHotinterestlabelstatisticses(Set<Hotinterestlabelstatistics> hotinterestlabelstatisticses) {
        this.hotinterestlabelstatisticses = hotinterestlabelstatisticses;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="globalinterestlabel")

    public Set<Interestlabelcollection> getInterestlabelcollections() {
        return this.interestlabelcollections;
    }
    
    public void setInterestlabelcollections(Set<Interestlabelcollection> interestlabelcollections) {
        this.interestlabelcollections = interestlabelcollections;
    }
   








}