/**
 * AbstractLabelCard.java
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
import com.simplelife.renhai.server.util.IAbstractLabelCard;
import com.simplelife.renhai.server.util.IJSONObject;


/** */
public abstract class AbstractLabelCard implements IAbstractLabelCard, IJSONObject
{
    /** */
    public LinkedList<AbstractLabel> labelList;
    
    /** */
    public AbstractLabel Unnamed1;
    
    /** */
    public void updateLabels(JSONArray labels)
    {
    }
    
    /** */
    public LinkedList<AbstractLabel> getLabelList()
    {
        return labelList;
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
