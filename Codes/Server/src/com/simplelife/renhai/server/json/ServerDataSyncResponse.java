/**
 * ServerDataSyncResponse.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.Consts.MessageId;

/**
 * 
 */
public class ServerDataSyncResponse extends ServerJSONMessage
{
	public ServerDataSyncResponse(AppJSONMessage request)
	{
		super(request);
		setMessageType(Consts.MessageType.ServerResponse);
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ServerDataSyncResponse;
	}
}
