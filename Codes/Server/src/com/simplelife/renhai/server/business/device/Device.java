/**
 * Device.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.util.ICardOperation;
import com.simplelife.renhai.server.util.IDbOperation;


/** */
public class Device implements IDbOperation, ICardOperation
{
    /** */
    protected Devicecard deviceCard;
    
    /** */
    protected Profile profile;
    
    /** */
    public DeviceWrapper Unnamed1;
    
    /** */
    public void updateDevicecard()
    {
    
    }
    
    /** */
    public Devicecard getDevicecard()
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
    public void setImpresscard(Impresscard card)
    {
    	profile.setImpresscard(card);
    }
    
    /** */
    public void setInterestcard(Interestcard card)
    {
    	profile.setInterestcard(card);
    }
    
    /** */
    public Interestcard getInterestcard()
    {
        return null;
    }
    
    /** */
    public Impresscard getImpresscard(int number)
    {
        return null;
    }

	@Override
	public void setDevicecard(Devicecard card)
	{
		this.deviceCard = card;
	}
	
	public void setProfile(Profile profile)
	{
		this.profile = profile;
	}
}
