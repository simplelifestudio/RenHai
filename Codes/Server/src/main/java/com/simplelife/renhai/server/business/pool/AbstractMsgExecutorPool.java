/**
 * MessageCenter.java
 * 
 * History:
 *     2013-10-25: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ExecutorService;
import com.simplelife.renhai.server.util.IMsgExecutorPool;

/**
 * 
 */
public abstract class AbstractMsgExecutorPool implements IMsgExecutorPool
{
	protected ExecutorService executeThreadPool;
	
	public abstract void startService();
	
	public void stopService()
	{
		executeThreadPool.shutdown();
	}

	@Override
	public void execute(Runnable message)
	{
		executeThreadPool.execute(message);
	}
}
