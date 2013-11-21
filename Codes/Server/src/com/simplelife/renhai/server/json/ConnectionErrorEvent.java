/**
 * ConnectionErrorEvent.java
 * 
 * History:
 *     2013-10-3: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;

/**
 * 
 */
public class ConnectionErrorEvent extends AppJSONMessage
{
	
	public ConnectionErrorEvent(JSONObject jsonObject)
	{
		super(jsonObject);
		messageId = Consts.MessageId.ConnectionErrorEvent;
	}

	@Override
	public void doRun()
	{
		//logger.debug("Start to handle connection error of device <{}>", deviceWrapper.getDeviceSn());
		deviceWrapper.changeBusinessStatus(DeviceStatus.Disconnected, StatusChangeReason.ExceptionOnWebsocket);
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ConnectionErrorEvent;
	}

}
