/**
 * IAbstractLabelCard.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.util.LinkedList;

import com.alibaba.fastjson.JSONArray;


/** */
public interface IAbstractLabelCard
{
    /** */
    public void updateLabels(JSONArray labels);
    
    /** */
    public LinkedList getLabelList();
    
    /** */
    public void clear();
}
