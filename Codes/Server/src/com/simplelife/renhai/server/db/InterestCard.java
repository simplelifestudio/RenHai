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
 * Interestcard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="interestcard"
    ,catalog="renhai"
)

public class InterestCard  implements java.io.Serializable {


    // Fields    

     private Integer interestCardId;
     private Long createTime;
     private Set<InterestLabelCollection> interestlabelcollections = new HashSet<InterestLabelCollection>(0);
     private Set<Profile> profiles = new HashSet<Profile>(0);


    // Constructors

    /** default constructor */
    public InterestCard() {
    }

	/** minimal constructor */
    public InterestCard(Long createTime) {
        this.createTime = createTime;
    }
    
    /** full constructor */
    public InterestCard(Long createTime, Set<InterestLabelCollection> interestlabelcollections, Set<Profile> profiles) {
        this.createTime = createTime;
        this.interestlabelcollections = interestlabelcollections;
        this.profiles = profiles;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="interestCardId", unique=true, nullable=false)

    public Integer getInterestCardId() {
        return this.interestCardId;
    }
    
    public void setInterestCardId(Integer interestCardId) {
        this.interestCardId = interestCardId;
    }
    
    @Column(name="createTime", nullable=false)

    public Long getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="interestcard")

    public Set<InterestLabelCollection> getInterestlabelcollections() {
        return this.interestlabelcollections;
    }
    
    public void setInterestlabelcollections(Set<InterestLabelCollection> interestlabelcollections) {
        this.interestlabelcollections = interestlabelcollections;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="interestcard")

    public Set<Profile> getProfiles() {
        return this.profiles;
    }
    
    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }
   








}