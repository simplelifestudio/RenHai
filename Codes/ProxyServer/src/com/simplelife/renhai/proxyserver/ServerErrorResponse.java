/**
 * ServerErrorResponse.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import java.io.PrintWriter;

import com.simplelife.renhai.proxyserver.Consts.MessageId;

/**
 * 
 */
public class ServerErrorResponse extends ServerJSONMessage
{
	public ServerErrorResponse(AppJSONMessage request, PrintWriter out)
	{
		super(request, out);
		setMessageType(Consts.MessageType.ServerResponse);
		setMessageId(Consts.MessageId.ServerErrorResponse);
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ServerErrorResponse;
	}
}
