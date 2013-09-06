/**
 * AlohaRequest.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
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
    	if (!checkJsonCommand())
    	{
    		ServerErrorResponse response = new ServerErrorResponse(deviceWrapper);
    		response.addToBody(JSONKey.FieldName.ReceivedMessage, Consts.MessageId.AlohaResponse);
    		response.addToBody(JSONKey.FieldName.ErrorCode, this.errorCode);
    		response.addToBody(JSONKey.FieldName.ErrorDescription, this.errorDescription);
    		response.asyncResponse();
    	}
    	
    	ServerJSONMessage response = JSONFactory.createServerJSONMessage(Consts.MessageId.AlohaResponse, deviceWrapper);
    	if (response == null)
    	{
    		// TODO: log error
    	}
    	
    	response.addToBody(JSONKey.FieldName.Content, helloApp);
    	response.asyncResponse();
    }
}
