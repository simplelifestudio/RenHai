package com.simplelife.renhai.server.db;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;

import com.simplelife.renhai.server.util.IDbObject;

public class Interestcard implements IDbObject 
{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column interestcard.interestCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer interestCardId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column interestcard.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer profileId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column interestcard.interestCardId
     *
     * @return the value of interestcard.interestCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getInterestCardId() {
        return interestCardId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column interestcard.interestCardId
     *
     * @param interestCardId the value for interestcard.interestCardId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setInterestCardId(Integer interestCardId) {
        this.interestCardId = interestCardId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column interestcard.profileId
     *
     * @return the value of interestcard.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getProfileId() {
        return profileId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column interestcard.profileId
     *
     * @param profileId the value for interestcard.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
    
    public void addInterestLabelMap(Interestlabelmap map)
    {
    	if (interestLabelMapSet.contains(map))
    	{
    		return;
    	}
    	interestLabelMapSet.add(map);
    }
    
    public void removeInterestLabelMap(Interestlabelmap map)
    {
    	if (interestLabelMapSet.contains(map))
    	{
    		interestLabelMapSet.remove(map);
    	}
    	
    	if (!removedInterestLabelMapSet.contains(map))
    	{
    		removedInterestLabelMapSet.add(map);
    	}
    }
    
	public Set<Interestlabelmap> getInterestLabelMapSet()
	{
		return interestLabelMapSet;
	}

	@Override
	public void save(SqlSession session)
	{
		InterestcardMapper mapper = session.getMapper(InterestcardMapper.class);
		if (this.interestCardId == null)
		{
			mapper.insert(this);
		}
		else
		{
			// It's not possible to update profileId
		}
		
		for (Interestlabelmap map : interestLabelMapSet)
		{
			map.setInterestCardId(interestCardId);
			map.save(session);
		}
		
		for (Interestlabelmap map : removedInterestLabelMapSet)
		{
			map.delete(session);
		}
	}
	
	private Set<Interestlabelmap> interestLabelMapSet = new ConcurrentSkipListSet<Interestlabelmap>();
	private Set<Interestlabelmap> removedInterestLabelMapSet = new ConcurrentSkipListSet<Interestlabelmap>();
	private Logger logger = DBModule.instance.getLogger();
}