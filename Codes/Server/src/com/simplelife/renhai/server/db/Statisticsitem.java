package com.simplelife.renhai.server.db;

public class Statisticsitem {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statisticsitem.statisticsitemId
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    private Integer statisticsitemId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statisticsitem.statisticsItem
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    private Integer statisticsItem;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statisticsitem.description
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    private String description;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statisticsitem.statisticsitemId
     *
     * @return the value of statisticsitem.statisticsitemId
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public Integer getStatisticsitemId() {
        return statisticsitemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statisticsitem.statisticsitemId
     *
     * @param statisticsitemId the value for statisticsitem.statisticsitemId
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public void setStatisticsitemId(Integer statisticsitemId) {
        this.statisticsitemId = statisticsitemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statisticsitem.statisticsItem
     *
     * @return the value of statisticsitem.statisticsItem
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public Integer getStatisticsItem() {
        return statisticsItem;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statisticsitem.statisticsItem
     *
     * @param statisticsItem the value for statisticsitem.statisticsItem
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public void setStatisticsItem(Integer statisticsItem) {
        this.statisticsItem = statisticsItem;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statisticsitem.description
     *
     * @return the value of statisticsitem.description
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statisticsitem.description
     *
     * @param description the value for statisticsitem.description
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}