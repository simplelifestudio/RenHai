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

/**
 * 
 */
public class InvalidRequest extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public InvalidRequest(JSONObject jsonObject)
	{
		super(jsonObject);
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AppJSONMessage#run()
	 */
	@Override
	public void run()
	{
		responseError(Consts.MessageId.InvalidRequest.name());
	}
	
	@Override
    public JSONObject getHeader()
    {
    	return null;
    }
    
	@Override
    public JSONObject getBody()
    {
    	return null;
    }
	
	@Override
	protected boolean checkJsonCommand()
	{
		setErrorDescription("Invalid JSON string");
		setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
		return false;
	}
    
	@Override
    public Consts.MessageId getMessageId()
    {
    	return Consts.MessageId.InvalidRequest;
    }
	
	@Override
    public String getMessageSn()
    {
    	return "InvalidMessageSn";
    }
}
