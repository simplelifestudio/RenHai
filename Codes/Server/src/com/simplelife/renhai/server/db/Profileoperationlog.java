package com.simplelife.renhai.server.db;

import org.apache.ibatis.session.SqlSession;

import com.simplelife.renhai.server.util.IDbObject;

public class Profileoperationlog implements IDbObject
{
	private Profile profile;
	
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column profileoperationlog.profileOperationLogId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer profileOperationLogId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column profileoperationlog.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer profileId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column profileoperationlog.logTime
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Long logTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column profileoperationlog.operationCodeId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private Integer operationCodeId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column profileoperationlog.logInfo
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    private String logInfo;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column profileoperationlog.profileOperationLogId
     *
     * @return the value of profileoperationlog.profileOperationLogId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getProfileOperationLogId() {
        return profileOperationLogId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column profileoperationlog.profileOperationLogId
     *
     * @param profileOperationLogId the value for profileoperationlog.profileOperationLogId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setProfileOperationLogId(Integer profileOperationLogId) {
        this.profileOperationLogId = profileOperationLogId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column profileoperationlog.profileId
     *
     * @return the value of profileoperationlog.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getProfileId() {
        return profileId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column profileoperationlog.profileId
     *
     * @param profileId the value for profileoperationlog.profileId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column profileoperationlog.logTime
     *
     * @return the value of profileoperationlog.logTime
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Long getLogTime() {
        return logTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column profileoperationlog.logTime
     *
     * @param logTime the value for profileoperationlog.logTime
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column profileoperationlog.operationCodeId
     *
     * @return the value of profileoperationlog.operationCodeId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public Integer getOperationCodeId() {
        return operationCodeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column profileoperationlog.operationCodeId
     *
     * @param operationCodeId the value for profileoperationlog.operationCodeId
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setOperationCodeId(Integer operationCodeId) {
        this.operationCodeId = operationCodeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column profileoperationlog.logInfo
     *
     * @return the value of profileoperationlog.logInfo
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public String getLogInfo() {
        return logInfo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column profileoperationlog.logInfo
     *
     * @param logInfo the value for profileoperationlog.logInfo
     *
     * @mbggenerated Fri Nov 01 15:18:11 CST 2013
     */
    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo == null ? null : logInfo.trim();
    }

    public void setProfile(Profile profile)
    {
    	this.profile = profile;
    }
    
	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IDbObject#save(org.apache.ibatis.session.SqlSession)
	 */
	@Override
	public void save(SqlSession session)
	{
		if (profileId == null)
		{
			profileId = profile.getProfileId();
		}
		ProfileoperationlogMapper mapper = session.getMapper(ProfileoperationlogMapper.class);
		mapper.insert(this);
	}
}