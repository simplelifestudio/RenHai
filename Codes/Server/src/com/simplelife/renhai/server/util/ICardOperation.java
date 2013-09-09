/**
 * ICardOperation.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;


/** */
public interface ICardOperation
{
    /** */
    public void setImpresscard(Impresscard card);
    
    /** */
    public void setInterestcard(Interestcard card);
    
    /** */
    public Interestcard getInterestcard();
    
    /** */
    public Impresscard getImpresscard(int number);
    
    public void setDevicecard(Devicecard card);
    
    public Devicecard getDevicecard();
}
