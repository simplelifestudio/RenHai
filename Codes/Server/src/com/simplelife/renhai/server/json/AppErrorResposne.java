/**
 * AppErrorResposne.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;

/**
 * 
 */
public class AppErrorResposne extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public AppErrorResposne(JSONObject jsonObject)
	{
		super(jsonObject);
	}

	@Override
	public void doRun()
	{
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.AppErrorResposne;
	}
}
