/**
 * ImpressCard.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.util.LinkedList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.IDbOperation;
import com.simplelife.renhai.server.util.IJSONObject;


/** */
public class ImpressCard extends AbstractLabelCard implements IJSONObject, IDbOperation
{
    /** */
    protected int chatTotalCount;
    
    /** */
    protected int totalChatDuration;
    
    /** */
    protected int chatLossCount;
    
    /** */
    public Profile Unnamed1;
    
    /** */
    public int getChatLossCount()
    {
        return chatLossCount;
    }
    
    /** */
    public int getChatTotalCount()
    {
        return chatTotalCount;
    
    }
    
    /** */
    public int getTotalChatDuration()
    {
        return totalChatDuration;
    
    }
    
    /** */
    public void updateLabels(JSONArray labels)
    {
    }
    
    /** */
    public LinkedList getLabelList()
    {
        return null;
    }
    
    /** */
    public void clear()
    {
    }
    
    /** */
    public JSONObject toJSONObject()
    {
        return null;
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
}
