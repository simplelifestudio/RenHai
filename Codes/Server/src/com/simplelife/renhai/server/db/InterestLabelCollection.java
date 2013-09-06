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
 * Interestlabelcollection entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="interestlabelcollection"
    ,catalog="renhai"
)

public class InterestLabelCollection  implements java.io.Serializable {


    // Fields    

     private Integer interestLabelMaplId;
     private InterestCard interestcard;
     private GlobalInterestLabel globalinterestlabel;
     private Integer order;
     private Integer matchCount;
     private String validFlag;


    // Constructors

    /** default constructor */
    public InterestLabelCollection() {
    }

    
    /** full constructor */
    public InterestLabelCollection(InterestCard interestcard, GlobalInterestLabel globalinterestlabel, Integer order, Integer matchCount, String validFlag) {
        this.interestcard = interestcard;
        this.globalinterestlabel = globalinterestlabel;
        this.order = order;
        this.matchCount = matchCount;
        this.validFlag = validFlag;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="interestLabelMaplId", unique=true, nullable=false)

    public Integer getInterestLabelMaplId() {
        return this.interestLabelMaplId;
    }
    
    public void setInterestLabelMaplId(Integer interestLabelMaplId) {
        this.interestLabelMaplId = interestLabelMaplId;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="interestCardId", nullable=false)

    public InterestCard getInterestcard() {
        return this.interestcard;
    }
    
    public void setInterestcard(InterestCard interestcard) {
        this.interestcard = interestcard;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="globalInterestLabelId", nullable=false)

    public GlobalInterestLabel getGlobalinterestlabel() {
        return this.globalinterestlabel;
    }
    
    public void setGlobalinterestlabel(GlobalInterestLabel globalinterestlabel) {
        this.globalinterestlabel = globalinterestlabel;
    }
    
    @Column(name="order", nullable=false)

    public Integer getOrder() {
        return this.order;
    }
    
    public void setOrder(Integer order) {
        this.order = order;
    }
    
    @Column(name="matchCount", nullable=false)

    public Integer getMatchCount() {
        return this.matchCount;
    }
    
    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }
    
    @Column(name="validFlag", nullable=false, length=7)

    public String getValidFlag() {
        return this.validFlag;
    }
    
    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }
   








}