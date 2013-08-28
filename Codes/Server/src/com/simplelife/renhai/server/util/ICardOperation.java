/**
 * ICardOperation.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import com.simplelife.renhai.server.business.device.ImpressCard;
import com.simplelife.renhai.server.business.device.InterestCard;


/** */
public interface ICardOperation
{
    /** */
    public void setImpressCard(ImpressCard card);
    
    /** */
    public void setInterestCard(InterestCard card);
    
    /** */
    public InterestCard getInterestCard();
    
    /** */
    public ImpressCard getImpressCard(int number);
}
