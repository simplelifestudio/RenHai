/**
 * ServerErrorResponse.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class ServerErrorResponse extends ServerJSONMessage
{
	public ServerErrorResponse(IDeviceWrapper deviceWrapper)
	{
		super(deviceWrapper);
		setMessageType(Consts.MessageType.ServerResponse);
		setMessageId(JSONKey.MessageId.ServerErrorResponse);
	}
}
