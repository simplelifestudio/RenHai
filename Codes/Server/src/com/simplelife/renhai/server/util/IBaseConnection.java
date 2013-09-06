/**
 * IBaseConnection.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;


import java.io.IOException;
import java.nio.ByteBuffer;

import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.websocket.WsOutbound;


/** */
public interface IBaseConnection
{
	/** */
	public void onClose(int status);
	
	/** */
	public void onOpen(WsOutbound outbound);
	
	/** */
	public void onPing(ByteBuffer payload);
	
	/** */
	public void onPong(ByteBuffer payload);
	
	/** */
	public void onTextMessage(String message);
	
	/** */
	public void onTimeout();
	
    
    /** */
    public void asyncSendMessage(ServerJSONMessage message) throws IOException;
    
    /** */
    public AppJSONMessage syncSendMessage(ServerJSONMessage message) throws IOException;
	
	/** */
	public void close();
	
	/** */
	public void ping();
	
	/** */
	public void bind(IBaseConnectionOwner owner);
	
	public IBaseConnectionOwner getOwner();
}
