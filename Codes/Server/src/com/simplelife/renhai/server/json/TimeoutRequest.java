/**
 * TimeoutRequest.java
 * 
 * History:
 *     2013-9-6: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
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
		logger.debug("Start to handle timeout of device <{}>", deviceWrapper.getDeviceSn());
		deviceWrapper.onTimeOut(null);
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.TimeoutRequest;
	}
	
}
