package com.simplelife.renhai.server.db;

import org.apache.ibatis.session.SqlSession;

import com.simplelife.renhai.server.util.IDbObject;

public class Globalinterestlabel implements IDbObject 
{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column globalinterestlabel.globalInterestLabelId
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    private Integer globalInterestLabelId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column globalinterestlabel.interestLabelName
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    private String interestLabelName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column globalinterestlabel.globalMatchCount
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    private Integer globalMatchCount;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column globalinterestlabel.globalInterestLabelId
     *
     * @return the value of globalinterestlabel.globalInterestLabelId
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public Integer getGlobalInterestLabelId() {
        return globalInterestLabelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column globalinterestlabel.globalInterestLabelId
     *
     * @param globalInterestLabelId the value for globalinterestlabel.globalInterestLabelId
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public void setGlobalInterestLabelId(Integer globalInterestLabelId) {
        this.globalInterestLabelId = globalInterestLabelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column globalinterestlabel.interestLabelName
     *
     * @return the value of globalinterestlabel.interestLabelName
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public String getInterestLabelName() {
        return interestLabelName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column globalinterestlabel.interestLabelName
     *
     * @param interestLabelName the value for globalinterestlabel.interestLabelName
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public void setInterestLabelName(String interestLabelName) {
        this.interestLabelName = interestLabelName == null ? null : interestLabelName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column globalinterestlabel.globalMatchCount
     *
     * @return the value of globalinterestlabel.globalMatchCount
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public Integer getGlobalMatchCount() {
        return globalMatchCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column globalinterestlabel.globalMatchCount
     *
     * @param globalMatchCount the value for globalinterestlabel.globalMatchCount
     *
     * @mbggenerated Sat Nov 02 05:09:06 CST 2013
     */
    public void setGlobalMatchCount(Integer globalMatchCount) {
        this.globalMatchCount = globalMatchCount;
    }

	@Override
	public void save(SqlSession session)
	{
		GlobalinterestlabelMapper mapper = session.getMapper(GlobalinterestlabelMapper.class);
		if (this.globalInterestLabelId != null)
		{
			mapper.updateByPrimaryKey(this);
		}
		else
		{
			Globalinterestlabel label = (Globalinterestlabel) mapper.selectByStringKey(interestLabelName);
			if (label == null)
			{
				mapper.insert(this);
			}
			else
			{
				mapper.updateByPrimaryKey(this);
				this.globalInterestLabelId = label.getGlobalInterestLabelId();
			}
		}
	}
	
	@Override
	public Globalinterestlabel clone()
	{
		Globalinterestlabel label = new Globalinterestlabel();
		label.setGlobalInterestLabelId(globalInterestLabelId);
		label.setGlobalMatchCount(globalMatchCount);
		label.setInterestLabelName(interestLabelName);
		return label;
	}
}