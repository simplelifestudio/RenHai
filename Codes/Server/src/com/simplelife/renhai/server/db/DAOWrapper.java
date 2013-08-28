/**
 * DAOWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import java.util.LinkedList;
import java.util.Timer;

import com.simplelife.renhai.server.util.IDbOperation;


/** */
public class DAOWrapper
{
    /** */
    protected LinkedList<IDbOperation> linkToBeSaved;
    
    /** */
    protected Timer timer;
    
    /** */
    public void toBeSaved(IDbOperation object)
    {
    
    }
    
    /** */
    public boolean executeSql(String sql)
    {
        return false;
    
    }
    
    /** */
    public boolean exists(String sql)
    {
        return false;
    
    }
    
    /** */
    public void query(String sql, Class objClass)
    {
    
    }
    
    /** */
    public void delete(Object obj)
    {
    
    }
    
    /** */
    public void save()
    {
    
    }
    
    /** */
    public void flushToDB()
    {
    
    }
}
