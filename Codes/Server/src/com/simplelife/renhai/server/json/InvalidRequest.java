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
	public InvalidRequest(JSONObject jsonObject)
	{
		super(jsonObject);
	}

	@Override
	public void run()
	{
		responseError(Consts.MessageId.Invalid);
	}
	
	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
	{
		setErrorDescription("Invalid JSON string");
		setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
		return false;
	}
    
	@Override
    public Consts.MessageId getMessageId()
    {
    	return Consts.MessageId.Invalid;
    }
}
