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
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.util.IAbstractLabelCard;
import com.simplelife.renhai.server.util.ICardOperation;
import com.simplelife.renhai.server.util.IDbOperation;
import com.simplelife.renhai.server.util.IJSONObject;


/** */
public class Profile implements IDbOperation, IJSONObject, ICardOperation
{
    /** */
    protected Impresscard impressCard;
    
    /** */
    protected Interestcard interestCard;
    
    /** */
    protected Date createTime;
    
    /** */
    public void setImpresscard(IAbstractLabelCard card)
    {
    
    }
    
    /** */
    public void setInterestcard(IAbstractLabelCard card)
    {
    
    }
    
    /** */
    public Interestcard getInterestcard()
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
    public void setImpresscard(Impresscard card)
    {
    }
    
    /** */
    public void setInterestcard(Interestcard card)
    {
    }

    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.ICardOperation#getImpresscard(int)
     */
    @Override
    public Impresscard getImpresscard(int number)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
