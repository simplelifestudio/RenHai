/**
 * RandomBusinessScheduler.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;


/** */
public class RandomBusinessScheduler extends AbstractBusinessScheduler
{
    /** */
    public void startScheduler()
    {
    }
    
    
    /** */
    public void stopScheduler()
    {
    }
    
    /** */
    public void schedule()
    {
    }


	@Override
	public void run()
	{
		// TODO
		if (deviceMap.size() <=2)
		{
			try
			{
				this.sleep(5);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
