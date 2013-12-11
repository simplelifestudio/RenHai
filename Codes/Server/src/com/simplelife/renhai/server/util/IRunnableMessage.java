/**
 * IRunnableCommand.java
 * 
 * History:
 *     2013-12-11: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

/**
 * 
 */
public interface IRunnableMessage extends Runnable
{
	int getQueueDuration();
	void setQueueTime(long now);
	String getMessageName();
	String getMessageSn();
	String getMsgOwnerInfo();
	int getDelayOfHandle();
}
