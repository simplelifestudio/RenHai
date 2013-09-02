/**
 * Device.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import com.simplelife.renhai.server.util.ICardOperation;
import com.simplelife.renhai.server.util.IDbOperation;


/** */
public class Device implements IDbOperation, ICardOperation
{
    /** */
    protected DeviceCard deviceCard;
    
    /** */
    protected Profile profile;
    
    /** */
    public DeviceWrapper Unnamed1;
    
    /** */
    public void updateDeviceCard()
    {
    
    }
    
    /** */
    public DeviceCard getDeviceCard()
    {
		return deviceCard;
    }
    
    /** */
    public Profile getProfile()
    {
        return profile;
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
    public void setImpressCard(ImpressCard card)
    {
    }
    
    /** */
    public void setInterestCard(InterestCard card)
    {
    }
    
    /** */
    public InterestCard getInterestCard()
    {
        return null;
    }
    
    /** */
    public ImpressCard getImpressCard(int number)
    {
        return null;
    }
}
