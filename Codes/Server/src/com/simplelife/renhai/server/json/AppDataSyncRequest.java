/**
 * AppDataSyncRequest.java
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
public class AppDataSyncRequest extends AppJSONMessage
{
	
	/**
	 * @param jsonObject
	 */
	public AppDataSyncRequest(JSONObject jsonObject)
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
		
		boolean query = body.containsKey(JSONKey.FieldName.DataQuery);
		boolean update = body.containsKey(JSONKey.FieldName.DataUpdate);
		if (!(query || update))
		{
			errorCode = 1103;
			errorDescription = "Neither " + JSONKey.FieldName.DataQuery + " nor " + JSONKey.FieldName.DataUpdate + " is included in request";
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
