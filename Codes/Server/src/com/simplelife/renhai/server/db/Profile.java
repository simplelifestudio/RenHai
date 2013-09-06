package com.simplelife.renhai.server.db;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * Profile entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="profile"
    ,catalog="renhai"
)

public class Profile  implements java.io.Serializable {


    // Fields    

     private Integer profileId;
     private InterestCard interestcard;
     private ImpressCard impresscard;
     private Long lastActivityTime;
     private Long createTime;
     private Set<DeviceProfileMap> deviceprofilemaps = new HashSet<DeviceProfileMap>(0);
     private Set<DeviceCard> devicecards = new HashSet<DeviceCard>(0);
     private Set<SessionProfileCollection> sessionprofilecollections = new HashSet<SessionProfileCollection>(0);
     private Set<ProfileOperationLog> profileoperationlogs = new HashSet<ProfileOperationLog>(0);


    // Constructors

    /** default constructor */
    public Profile() {
    }

	/** minimal constructor */
    public Profile(InterestCard interestcard, ImpressCard impresscard, Long lastActivityTime, Long createTime) {
        this.interestcard = interestcard;
        this.impresscard = impresscard;
        this.lastActivityTime = lastActivityTime;
        this.createTime = createTime;
    }
    
    /** full constructor */
    public Profile(InterestCard interestcard, ImpressCard impresscard, Long lastActivityTime, Long createTime, Set<DeviceProfileMap> deviceprofilemaps, Set<DeviceCard> devicecards, Set<SessionProfileCollection> sessionprofilecollections, Set<ProfileOperationLog> profileoperationlogs) {
        this.interestcard = interestcard;
        this.impresscard = impresscard;
        this.lastActivityTime = lastActivityTime;
        this.createTime = createTime;
        this.deviceprofilemaps = deviceprofilemaps;
        this.devicecards = devicecards;
        this.sessionprofilecollections = sessionprofilecollections;
        this.profileoperationlogs = profileoperationlogs;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="profileId", unique=true, nullable=false)

    public Integer getProfileId() {
        return this.profileId;
    }
    
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
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
        @JoinColumn(name="impressCardId", nullable=false)

    public ImpressCard getImpresscard() {
        return this.impresscard;
    }
    
    public void setImpresscard(ImpressCard impresscard) {
        this.impresscard = impresscard;
    }
    
    @Column(name="lastActivityTime", nullable=false)

    public Long getLastActivityTime() {
        return this.lastActivityTime;
    }
    
    public void setLastActivityTime(Long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }
    
    @Column(name="createTime", nullable=false)

    public Long getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="profile")

    public Set<DeviceProfileMap> getDeviceprofilemaps() {
        return this.deviceprofilemaps;
    }
    
    public void setDeviceprofilemaps(Set<DeviceProfileMap> deviceprofilemaps) {
        this.deviceprofilemaps = deviceprofilemaps;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="profile")

    public Set<DeviceCard> getDevicecards() {
        return this.devicecards;
    }
    
    public void setDevicecards(Set<DeviceCard> devicecards) {
        this.devicecards = devicecards;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="profile")

    public Set<SessionProfileCollection> getSessionprofilecollections() {
        return this.sessionprofilecollections;
    }
    
    public void setSessionprofilecollections(Set<SessionProfileCollection> sessionprofilecollections) {
        this.sessionprofilecollections = sessionprofilecollections;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="profile")

    public Set<ProfileOperationLog> getProfileoperationlogs() {
        return this.profileoperationlogs;
    }
    
    public void setProfileoperationlogs(Set<ProfileOperationLog> profileoperationlogs) {
        this.profileoperationlogs = profileoperationlogs;
    }
   








}