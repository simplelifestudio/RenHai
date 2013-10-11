/**
 * IMockConnection.java
 * 
 * History:
 *     2013-10-8: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.json.ServerJSONMessage;

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
	public void sendMessage(ServerJSONMessage message) throws IOException;
	public void sendMessage(String message) throws IOException;
	public void onTimeout();
	public void close();
	public String getConnectionId();
	public void onPing(ByteBuffer pingData);
	
	public boolean isOpen();
}
