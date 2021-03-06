/**
 * InvalidRequesst.java
 * 
 * History:
 *     2013-9-6: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.util.Consts.MessageId;

/**
 * 
 */
public class UnkownRequest extends AppJSONMessage
{
    
	/**
	 * @param jsonObject
	 */
	public UnkownRequest(JSONObject jsonObject)
	{
		super(jsonObject);
		messageId = Consts.MessageId.UnkownRequest;
	}

	@Override
	public void doRun()
	{
		Consts.MessageId.UnkownRequest.name();  
		if (header != null)
		{
			if (header.containsKey(JSONKey.MessageId))
			{
				header.getString(JSONKey.MessageId);
			}
		}
		responseError();
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.UnkownRequest;
	}
}
