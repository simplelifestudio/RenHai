/**
 * TimeoutRequest.java
 * 
 * History:
 *     2013-9-6: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;

/**
 * 
 */
public class TimeoutRequest extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public TimeoutRequest(JSONObject jsonObject)
	{
		super(jsonObject);
	}

	@Override
	public void run()
	{
		Impresscard card =  deviceWrapper.getDevice().getProfile().getImpresscard();
		card.setChatLossCount(card.getChatLossCount() + 1);
		DBModule.instance.cache(card);
		
		deviceWrapper.onTimeOut(null);
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.TimeoutRequest;
	}
	
}
