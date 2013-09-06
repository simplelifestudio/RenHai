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
 * Statisticsitemdefinition entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="statisticsitemdefinition"
    ,catalog="renhai"
)

public class StatisticsItemDefinition  implements java.io.Serializable {


    // Fields    

     private Integer statisticsItemDefinitionId;
     private Integer statisticsItem;
     private String description;
     private Set<SystemStatistics> systemstatisticses = new HashSet<SystemStatistics>(0);


    // Constructors

    /** default constructor */
    public StatisticsItemDefinition() {
    }

	/** minimal constructor */
    public StatisticsItemDefinition(Integer statisticsItem, String description) {
        this.statisticsItem = statisticsItem;
        this.description = description;
    }
    
    /** full constructor */
    public StatisticsItemDefinition(Integer statisticsItem, String description, Set<SystemStatistics> systemstatisticses) {
        this.statisticsItem = statisticsItem;
        this.description = description;
        this.systemstatisticses = systemstatisticses;
    }

   
    // Property accessors
    @Id @GeneratedValue
    
    @Column(name="statisticsItemDefinitionId", unique=true, nullable=false)

    public Integer getStatisticsItemDefinitionId() {
        return this.statisticsItemDefinitionId;
    }
    
    public void setStatisticsItemDefinitionId(Integer statisticsItemDefinitionId) {
        this.statisticsItemDefinitionId = statisticsItemDefinitionId;
    }
    
    @Column(name="statisticsItem", nullable=false)

    public Integer getStatisticsItem() {
        return this.statisticsItem;
    }
    
    public void setStatisticsItem(Integer statisticsItem) {
        this.statisticsItem = statisticsItem;
    }
    
    @Column(name="description", nullable=false, length=256)

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="statisticsitemdefinition")

    public Set<SystemStatistics> getSystemstatisticses() {
        return this.systemstatisticses;
    }
    
    public void setSystemstatisticses(Set<SystemStatistics> systemstatisticses) {
        this.systemstatisticses = systemstatisticses;
    }
   








}