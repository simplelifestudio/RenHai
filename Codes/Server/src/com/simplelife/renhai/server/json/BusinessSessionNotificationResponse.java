/**
 * BusinessSessionNotificationResponse.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class BusinessSessionNotificationResponse extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public BusinessSessionNotificationResponse(JSONObject jsonObject)
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
		
		if (!body.containsKey(JSONKey.FieldName.OperationType));
		{
			errorCode = 1103;
			errorDescription = JSONKey.FieldName.OperationType + " can't be found.";
		}
		return true;
    }
	
	@Override
	public void run()
	{
		if (!checkJsonCommand())
		{
			responseError();
			return;
		}
		
	}
}
