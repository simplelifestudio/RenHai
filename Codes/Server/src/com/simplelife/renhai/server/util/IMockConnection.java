/**
 * IMockConnection.java
 * 
 * History:
 *     2013-10-8: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

import java.nio.ByteBuffer;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 */
public interface IMockConnection
{
	public void bindMockApp(IMockApp mockApp);
	public JSONObject getLastSentMessage();
	public void disableConnection();
	public void enableConnection();
	public void onTextMessage(String message);
	public JSONObject syncSendToServer(JSONObject jsonObject);
	public void asyncSendToServer(JSONObject jsonObject);
	public void onTimeout();
	public void closeConnection();
	public void ping();
	public String getConnectionId();
	public void onPing(ByteBuffer pingData);
	
	public boolean isOpen();
}
