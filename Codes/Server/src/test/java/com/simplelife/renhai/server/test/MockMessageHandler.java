/**
 * MessageHandler.java
 * 
 * History:
 *     2013-11-8: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractMsgExecutorPool;
import com.simplelife.renhai.server.log.FileLogger;

/**
 * 
 */
public class MockMessageHandler implements Runnable
{
	private MockApp app; 
	private AbstractMsgExecutorPool executor;
	
	private boolean executeFlag = false;
	private Lock executeLock = new ReentrantLock();
	
	private ConcurrentLinkedQueue<JSONObject> queue = new ConcurrentLinkedQueue<>();
	private Lock queueLock = new ReentrantLock();
	
	
	public MockMessageHandler(MockApp app, AbstractMsgExecutorPool executor)
	{
		this.app = app;
		this.executor = executor;
	}
	
	public void clearMessage()
	{
		if (queue.isEmpty())
		{
			return;
		}
		
		queueLock.lock();
		queue.clear();
		queueLock.unlock();
	}
	
	public void addMessage(JSONObject obj)
	{
		if (obj == null)
		{
			return;
		}
		
		queueLock.lock();
		queue.add(obj);
		
		executeLock.lock();
		if (!executeFlag)
		{
			executeFlag = true;
			executor.execute(this);
		}
		executeLock.unlock();
		queueLock.unlock();
	}

	@Override
	public void run()
	{
		queueLock.lock();
		
		JSONObject obj;
		while(!queue.isEmpty())
		{
			obj = queue.remove();
			queueLock.unlock();
			try
			{
				executeMessage(obj);
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
			queueLock.lock();
		}
		
		executeLock.lock();
		executeFlag = false;
		executeLock.unlock();
		queueLock.unlock();
	}
	
	private void executeMessage(JSONObject obj)
	{
		app.execute(obj);
	}
}
