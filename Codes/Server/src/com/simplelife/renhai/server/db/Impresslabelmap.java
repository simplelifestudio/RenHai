package com.simplelife.renhai.server.db;

import org.apache.ibatis.session.SqlSession;

import com.simplelife.renhai.server.util.IDbObject;

public class Impresslabelmap implements IDbObject, Comparable<Impresslabelmap> 
{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresslabelmap.impressLabelMapId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer impressLabelMapId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresslabelmap.impressCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer impressCardId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresslabelmap.globalImpressLabelId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer globalImpressLabelId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresslabelmap.assessedCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer assessedCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresslabelmap.updateTime
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Long updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresslabelmap.assessCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer assessCount;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresslabelmap.impressLabelMapId
     *
     * @return the value of impresslabelmap.impressLabelMapId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getImpressLabelMapId() {
        return impressLabelMapId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresslabelmap.impressLabelMapId
     *
     * @param impressLabelMapId the value for impresslabelmap.impressLabelMapId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setImpressLabelMapId(Integer impressLabelMapId) {
        this.impressLabelMapId = impressLabelMapId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresslabelmap.impressCardId
     *
     * @return the value of impresslabelmap.impressCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getImpressCardId() {
        return impressCardId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresslabelmap.impressCardId
     *
     * @param impressCardId the value for impresslabelmap.impressCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setImpressCardId(Integer impressCardId) {
        this.impressCardId = impressCardId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresslabelmap.globalImpressLabelId
     *
     * @return the value of impresslabelmap.globalImpressLabelId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getGlobalImpressLabelId() {
        return globalImpressLabelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresslabelmap.globalImpressLabelId
     *
     * @param globalImpressLabelName the value for impresslabelmap.globalImpressLabelId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setGlobalImpressLabelId(Integer globalImpressLabelId) {
        this.globalImpressLabelId = globalImpressLabelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresslabelmap.assessedCount
     *
     * @return the value of impresslabelmap.assessedCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getAssessedCount() {
        return assessedCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresslabelmap.assessedCount
     *
     * @param assessedCount the value for impresslabelmap.assessedCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setAssessedCount(Integer assessedCount) {
        this.assessedCount = assessedCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresslabelmap.updateTime
     *
     * @return the value of impresslabelmap.updateTime
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresslabelmap.updateTime
     *
     * @param updateTime the value for impresslabelmap.updateTime
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresslabelmap.assessCount
     *
     * @return the value of impresslabelmap.assessCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getAssessCount() {
        return assessCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresslabelmap.assessCount
     *
     * @param assessCount the value for impresslabelmap.assessCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setAssessCount(Integer assessCount) {
        this.assessCount = assessCount;
    }

    
    
	@Override
	public void save(SqlSession session)
	{
		ImpresslabelmapMapper mapper = session.getMapper(ImpresslabelmapMapper.class);
		if (this.impressLabelMapId == null)
		{
			if (this.globalImpressLabelId == null)
			{
				if (globalLabel == null)
				{
					DBModule.instance.getLogger().error("Fatal error: globalLabel == null when trying to save ImpresslabelmapMapper");
					return;
				}
				
				Integer id = globalLabel.getGlobalImpressLabelId();
				if (id == null)
				{
					// The global label shall be ahead of labelMap in queue of DAOWrapper
					DBModule.instance.getLogger().error("Fatal error: id of globalLabel is still null when trying to save ImpresslabelmapMapper");
					return;
				}
				this.globalImpressLabelId = id;
			}
			mapper.insert(this);
		}
		else
		{
			mapper.updateByPrimaryKey(this);
		}
	}
	
	public void setGlobalLabel(Globalimpresslabel label)
	{
		this.globalLabel = label;
	}
	
	public Globalimpresslabel getGlobalLabel()
	{
		/*
		if (globalLabel == null)
		{
			globalLabel = DBQueryUtil.getGlobalimpresslabel(globalImpressLabelId);
		}
		*/
		return globalLabel;
	}
	
	private Globalimpresslabel globalLabel = null;

	@Override
	public int compareTo(Impresslabelmap o)
	{
		Integer targetId = o.getImpressLabelMapId();
		if (impressLabelMapId == null && targetId == null)
		{
			// Check by global label name if all id are null 
			String labelName = globalLabel.getImpressLabelName();
			String targetLabelName = o.getGlobalLabel().getImpressLabelName(); 
			return labelName.compareTo(targetLabelName);
		}
		
		if (impressLabelMapId == null || targetId == null)
		{
			return -1;
		}
		return (impressLabelMapId - targetId);
	}
}