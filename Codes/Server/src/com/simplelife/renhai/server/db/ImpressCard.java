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
 * Impresscard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="impresscard"
    ,catalog="renhai"
)

public class ImpressCard  implements java.io.Serializable {


    // Fields    

     private Integer impressCardId;
     private Integer chatTotalCount;
     private Integer chatTotalDuration;
     private Integer chatLossCount;
     private Set<Profile> profiles = new HashSet<Profile>(0);
     private Set<ImpressLabelCollection> impresslabelcollections = new HashSet<ImpressLabelCollection>(0);


    // Constructors

    /** default constructor */
    public ImpressCard() {
    }

	/** minimal constructor */
    public ImpressCard(Integer chatTotalCount, Integer chatTotalDuration, Integer chatLossCount) {
        this.chatTotalCount = chatTotalCount;
        this.chatTotalDuration = chatTotalDuration;
        this.chatLossCount = chatLossCount;
    }
    
    /** full constructor */
    public ImpressCard(Integer chatTotalCount, Integer chatTotalDuration, Integer chatLossCount, Set<Profile> profiles, Set<ImpressLabelCollection> impresslabelcollections) {
        this.chatTotalCount = chatTotalCount;
        this.chatTotalDuration = chatTotalDuration;
        this.chatLossCount = chatLossCount;
        this.profiles = profiles;
        this.impresslabelcollections = impresslabelcollections;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="impressCardId", unique=true, nullable=false)

    public Integer getImpressCardId() {
        return this.impressCardId;
    }
    
    public void setImpressCardId(Integer impressCardId) {
        this.impressCardId = impressCardId;
    }
    
    @Column(name="chatTotalCount", nullable=false)

    public Integer getChatTotalCount() {
        return this.chatTotalCount;
    }
    
    public void setChatTotalCount(Integer chatTotalCount) {
        this.chatTotalCount = chatTotalCount;
    }
    
    @Column(name="chatTotalDuration", nullable=false)

    public Integer getChatTotalDuration() {
        return this.chatTotalDuration;
    }
    
    public void setChatTotalDuration(Integer chatTotalDuration) {
        this.chatTotalDuration = chatTotalDuration;
    }
    
    @Column(name="chatLossCount", nullable=false)

    public Integer getChatLossCount() {
        return this.chatLossCount;
    }
    
    public void setChatLossCount(Integer chatLossCount) {
        this.chatLossCount = chatLossCount;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="impresscard")

    public Set<Profile> getProfiles() {
        return this.profiles;
    }
    
    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="impresscard")

    public Set<ImpressLabelCollection> getImpresslabelcollections() {
        return this.impresslabelcollections;
    }
    
    public void setImpresslabelcollections(Set<ImpressLabelCollection> impresslabelcollections) {
        this.impresslabelcollections = impresslabelcollections;
    }
   








}