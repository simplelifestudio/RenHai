package com.simplelife.renhai.server.db;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;

import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.util.IDbObject;
import com.simplelife.renhai.server.util.IDeviceWrapper;

public class Impresscard implements IDbObject 
{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresscard.impressCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer impressCardId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresscard.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer profileId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresscard.chatTotalCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer chatTotalCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresscard.chatTotalDuration
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer chatTotalDuration;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column impresscard.chatLossCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer chatLossCount;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresscard.impressCardId
     *
     * @return the value of impresscard.impressCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getImpressCardId() {
        return impressCardId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresscard.impressCardId
     *
     * @param impressCardId the value for impresscard.impressCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setImpressCardId(Integer impressCardId) {
        this.impressCardId = impressCardId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresscard.profileId
     *
     * @return the value of impresscard.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getProfileId() {
        return profileId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresscard.profileId
     *
     * @param profileId the value for impresscard.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresscard.chatTotalCount
     *
     * @return the value of impresscard.chatTotalCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getChatTotalCount() {
        return chatTotalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresscard.chatTotalCount
     *
     * @param chatTotalCount the value for impresscard.chatTotalCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setChatTotalCount(Integer chatTotalCount) {
        this.chatTotalCount = chatTotalCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresscard.chatTotalDuration
     *
     * @return the value of impresscard.chatTotalDuration
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getChatTotalDuration() {
        return chatTotalDuration;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresscard.chatTotalDuration
     *
     * @param chatTotalDuration the value for impresscard.chatTotalDuration
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setChatTotalDuration(Integer chatTotalDuration) {
        this.chatTotalDuration = chatTotalDuration;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column impresscard.chatLossCount
     *
     * @return the value of impresscard.chatLossCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getChatLossCount() {
        return chatLossCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column impresscard.chatLossCount
     *
     * @param chatLossCount the value for impresscard.chatLossCount
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setChatLossCount(Integer chatLossCount) {
        this.chatLossCount = chatLossCount;
    }
    
    public void addImpressLabelMap(Impresslabelmap map)
    {
    	if (impressLabelMapSet.contains(map))
    	{
    		return;
    	}
    	impressLabelMapSet.add(map);
    }
    
    public void removeImpressLabelMap(Impresslabelmap map)
    {
    	if (!impressLabelMapSet.contains(map))
    	{
    		logger.warn("The Impresslabelmap <{}> which is requested to be removed is not in current impress card", map.getGlobalLabel().getImpressLabelName());
    		return;
    	}
    	impressLabelMapSet.remove(map);
    }

    public Collection<Impresslabelmap> getImpressLabelMapSet()
	{
		return impressLabelMapSet;
	}

	public void setImpressLabelMapSet(Set<Impresslabelmap> impressLabelMapSet)
	{
		this.impressLabelMapSet = impressLabelMapSet;
	}
	
	@Override
	public void save(SqlSession session)
	{
		ImpresscardMapper mapper = session.getMapper(ImpresscardMapper.class);
		if (this.impressCardId == null)
		{
			mapper.insert(this);
		}
		else
		{
			mapper.updateByPrimaryKey(this);
		}
		
		for (Impresslabelmap map : impressLabelMapSet)
		{
			map.setImpressCardId(impressCardId);
			map.save(session);
		}
	}
	
	public void updateOrAppendImpressLabel(IDeviceWrapper deviceWrapper, String labelName, boolean assessedFlag)
	{
		Collection<Impresslabelmap> impressLabels = getImpressLabelMapSet();

		for (Impresslabelmap label : impressLabels)
		{
			String tmpLabelName = label.getGlobalLabel().getImpressLabelName();
			if (tmpLabelName.equals(labelName))
			{
				synchronized(label)
				{
					if (assessedFlag)
					{
						int count = label.getAssessedCount();
						if (logger.isDebugEnabled())
						{
							logger.debug("Impress label<"+ labelName +"> of device <{}> was increased from " 
								+ count + " to " + (count + 1), deviceWrapper.getDeviceIdentification());
						}
						label.setAssessedCount(count + 1);
						label.setUpdateTime(System.currentTimeMillis());
						//label.getGlobalimpresslabel().setGlobalAssessCount(label.getGlobalimpresslabel().getGlobalAssessCount() + 1);
					}
					else
					{
						int count = label.getAssessCount();
						if (logger.isDebugEnabled())
						{
							logger.debug("Assessing count of <" + labelName + "> by device <{}> was increased from " 
								+ count + " to " + (count + 1), deviceWrapper.getDeviceIdentification());
						}
						label.setAssessCount(label.getAssessCount() + 1);
						DbLogger.increaseImpressAssessCount(tmpLabelName);
					}
				}
				return;
			}
		}
			
		// Check if it's existent global impress label
		Globalimpresslabel globalimpresslabel = DBModule.instance.impressLabelCache.getObject(labelName);
		if (globalimpresslabel == null)
		{
			logger.debug("New impress label {} from device <" + deviceWrapper.getDeviceIdentification() + ">", labelName);
			globalimpresslabel = new Globalimpresslabel();
			globalimpresslabel.setGlobalAssessCount(1);
			globalimpresslabel.setImpressLabelName(labelName);
			
			// If there is global label object with same label name in cache
			// replace global label by existent global label object  
			boolean isNewObject = DBModule.instance.impressLabelCache.putObject(labelName, globalimpresslabel);
			if (isNewObject)
			{
				logger.debug("============new object, save in DAOWrapper");
				DAOWrapper.instance.cache(globalimpresslabel);
			}
			else
			{
				logger.debug("============old object, replace globalimpresslabel with old object");
				globalimpresslabel = DBModule.instance.impressLabelCache.getObject(labelName);
			}
			//globalimpresslabel.setImpresslabelmaps(impressLabels);
		}
		
		Impresslabelmap labelMap = new Impresslabelmap();
		
		if (assessedFlag)
		{
			labelMap.setAssessCount(0);
			labelMap.setAssessedCount(1);
		}
		else
		{
			labelMap.setAssessCount(1);
			labelMap.setAssessedCount(0);
		}
		
		labelMap.setGlobalImpressLabelId(globalimpresslabel.getGlobalImpressLabelId());
		labelMap.setGlobalLabel(globalimpresslabel);
		labelMap.setUpdateTime(System.currentTimeMillis());
		labelMap.setImpressCardId(getImpressCardId());
		
		impressLabels.add(labelMap);
	}

	
	private Logger logger = DBModule.instance.getLogger();
	private Collection<Impresslabelmap> impressLabelMapSet = new ConcurrentLinkedQueue<Impresslabelmap>();
}