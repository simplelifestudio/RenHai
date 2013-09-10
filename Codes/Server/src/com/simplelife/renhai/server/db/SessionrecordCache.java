
/**
 * 
 * 
 * History:
 *     2013-9-10: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */
package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.util.IDbCache;
import org.slf4j.Logger;
public class SessionrecordCache extends Sessionrecord implements IDbCache
{
	@Override
	public boolean saveToDb()
	{
		Logger logger = DBModule.instance.getLogger();
		try
		{
			SessionrecordDAO dao = new SessionrecordDAO();
			dao.save(this);
			return true;
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			return false;
		}
	}
    
    @Override
	public void cache()
	{
		DAOWrapper.cache(this);
	}
}
