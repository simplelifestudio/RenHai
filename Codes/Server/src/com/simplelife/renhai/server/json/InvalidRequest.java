/**
 * InvalidRequest.java
 * 
 * History:
 *     2013-9-8: Tomas Chen, initial version
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
public class InvalidRequest extends AppJSONMessage
{
	private String receivedMessage;
	public InvalidRequest(JSONObject jsonObject)
	{
		super(jsonObject);
		messageId = Consts.MessageId.Invalid;
	}
	
	public void setReceivedMessage(String message)
	{
		receivedMessage = message;
	}
	
	public String getReceivedMessage()
	{
		return receivedMessage;
	}

	/**
	 * Override responseError in super class as received Message may not be valid JSON Object
	 */
	@Override
	protected void responseError()
    {
    	ServerErrorResponse response = createErrorResponse();
    	if (this.jsonObject != null)
    	{
    		response.addToBody(JSONKey.ReceivedMessage, this.jsonObject);
    	}
    	else
    	{
    		response.addToBody(JSONKey.ReceivedMessage, receivedMessage);
    	}
    	
    	if (this.getErrorCode() != null)
    	{
    		response.addToBody(JSONKey.ErrorCode, this.getErrorCode());
    	}
    	else
    	{
    		response.addToBody(JSONKey.ErrorCode, Consts.GlobalErrorCode.InvalidJSONRequest_1100);
    	}
    	
    	if (this.getErrorDescription() != null)
    	{
    		response.addToBody(JSONKey.ErrorDescription, this.getErrorDescription());
    	}
    	else
    	{
    		response.addToBody(JSONKey.ErrorDescription, "Invalid JSON request");
    	}
    	response.addToHeader(JSONKey.MessageSn, this.getMessageSn());
    	
    	response.asyncResponse();
    }
	
	@Override
	public void doRun()
	{
		responseError();
	}
	
	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
	{
		return false;
	}
    
	@Override
    public Consts.MessageId getMessageId()
    {
    	return Consts.MessageId.Invalid;
    }
}
