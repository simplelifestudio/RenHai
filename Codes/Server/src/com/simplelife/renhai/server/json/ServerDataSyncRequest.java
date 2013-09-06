/**
 * ServerDataSyncRequest.java
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
public class ServerDataSyncRequest extends AppJSONMessage
{
	
	/**
	 * @param messageObject
	 */
	public ServerDataSyncRequest(JSONObject messageObject)
	{
		super(messageObject);
	}
	
	protected boolean checkJsonCommand()
    {
		if (!super.checkJsonCommand())
		{
			return false;
		}
		
		if (body.isEmpty())
		{
			setErrorCode(Consts.GlobalErrorCode.ParameterError_1103);
			setErrorDescription("No content in body of request.");
			return false;
		}
		return true;
    }
	
	@Override
	public void run()
	{
		if (!checkJsonCommand())
		{
			responseError(Consts.MessageId.ServerDataSyncRequest.name());
			return;
		}
		
	}
}
