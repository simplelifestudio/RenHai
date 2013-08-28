/**
 * Profile.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.IAbstractLabelCard;
import com.simplelife.renhai.server.util.ICardOperation;
import com.simplelife.renhai.server.util.IDbOperation;
import com.simplelife.renhai.server.util.IJSONObject;


/** */
public class Profile implements IDbOperation, IJSONObject, ICardOperation
{
    /** */
    protected ImpressCard impressCard;
    
    /** */
    protected InterestCard interestCard;
    
    /** */
    protected Date createTime;
    
    /** */
    public void setImpressCard(IAbstractLabelCard card)
    {
    
    }
    
    /** */
    public void setInterestCard(IAbstractLabelCard card)
    {
    
    }
    
    /** */
    public InterestCard getInterestCard()
    {
        return interestCard;
    
    }
    
    /** */
    public boolean saveToDb()
    {
        return false;
    }
    
    /** */
    public boolean loadFromDb()
    {
        return false;
    }
    
    /** */
    public JSONObject toJSONObject()
    {
        return null;
    }
    
    /** */
    public void setImpressCard(ImpressCard card)
    {
    }
    
    /** */
    public void setInterestCard(InterestCard card)
    {
    }

    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.ICardOperation#getImpressCard(int)
     */
    @Override
    public ImpressCard getImpressCard(int number)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
