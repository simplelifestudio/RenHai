/**
 * ConnectionErrorEvent.java
 * 
 * History:
 *     2013-10-3: Tomas Chen, initial version
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
public class ConnectionErrorEvent extends AppJSONMessage
{
	
	public ConnectionErrorEvent(JSONObject jsonObject)
	{
		super(jsonObject);
	}

	@Override
	public void run()
	{
		logger.debug("Start to handle connection error of device <{}>", deviceWrapper.getDeviceSn());
		deviceWrapper.onConnectionClose();
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		
		Impresscard card =  deviceWrapper.getDevice().getProfile().getImpresscard();
		card.setChatLossCount(card.getChatLossCount() + 1);
		//DBModule.instance.cache(card);
		t.commit();
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.TimeoutRequest;
	}
	
}
