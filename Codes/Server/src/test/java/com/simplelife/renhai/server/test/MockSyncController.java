/**
 * MockSyncController.java
 * 
 * History:
 *     2013-10-13: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 */
public class MockSyncController
{
	public final Lock lock = new ReentrantLock();
	public final Condition condition = lock.newCondition(); 
	public JSONObject message;
}