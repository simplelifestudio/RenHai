/**
 * AlohaRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class AlohaRequest extends AppJSONMessage
{
	private final String helloApp = "Hello App!";
	
	/**
	 * @param jsonObject
	 */
	public AlohaRequest(JSONObject jsonObject)
	{
		super(jsonObject);
	}


	@Override
	protected boolean checkJsonCommand()
    {
		if (!super.checkJsonCommand())
		{
			return false;
		}
		
		return true;
    }
	
	
    @Override
    public void run()
    {
    	Logger logger = JSONModule.instance.getLogger();
    	logger.debug("Start run of AlohaRequest");
    	if (!checkJsonCommand())
    	{
    		logger.debug("checkJsonCommand failed");
    		ServerErrorResponse response = new ServerErrorResponse(this);
    		response.addToBody(JSONKey.FieldName.ReceivedMessage, Consts.MessageId.AlohaResponse);
    		response.addToBody(JSONKey.FieldName.ErrorCode, this.getErrorCode());
    		response.addToBody(JSONKey.FieldName.ErrorDescription, this.getErrorDescription());
    		response.asyncResponse();
    	}
    	
    	ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.AlohaResponse);
    	if (response == null)
    	{
    		logger.error("createServerJSONMessage returns null");
    		return;
    	}
    	
    	response.addToBody(JSONKey.FieldName.Content, helloApp);
    	response.asyncResponse();
    }


	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AppJSONMessage#getMessageId()
	 */
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.AlohaRequest;
	}
}
