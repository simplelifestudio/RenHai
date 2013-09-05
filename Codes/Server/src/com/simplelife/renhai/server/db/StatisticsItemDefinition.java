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

public class Statisticsitemdefinition  implements java.io.Serializable {


    // Fields    

     private Integer statisticsItemDefinitionId;
     private Integer statisticsItem;
     private String description;
     private Set<Systemstatistics> systemstatisticses = new HashSet<Systemstatistics>(0);


    // Constructors

    /** default constructor */
    public Statisticsitemdefinition() {
    }

	/** minimal constructor */
    public Statisticsitemdefinition(Integer statisticsItem, String description) {
        this.statisticsItem = statisticsItem;
        this.description = description;
    }
    
    /** full constructor */
    public Statisticsitemdefinition(Integer statisticsItem, String description, Set<Systemstatistics> systemstatisticses) {
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

    public Set<Systemstatistics> getSystemstatisticses() {
        return this.systemstatisticses;
    }
    
    public void setSystemstatisticses(Set<Systemstatistics> systemstatisticses) {
        this.systemstatisticses = systemstatisticses;
    }
   








}