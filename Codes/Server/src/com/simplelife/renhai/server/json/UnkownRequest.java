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
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AppJSONMessage#run()
	 */
	@Override
	public void run()
	{
    	responseError(Consts.MessageId.UnkownRequest.name());
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AppJSONMessage#getMessageId()
	 */
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.UnkownRequest;
	}
	
	

}
