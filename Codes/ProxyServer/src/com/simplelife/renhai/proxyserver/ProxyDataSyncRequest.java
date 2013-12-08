/**
 * ProxyRequest.java
 * 
 * History:
 *     2013-11-17: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import java.io.PrintWriter;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.proxyserver.Consts.GlobalErrorCode;
import com.simplelife.renhai.proxyserver.Consts.MessageId;
import com.simplelife.renhai.proxyserver.Consts.MessageType;

/**
 * 
 */
public class ProxyDataSyncRequest extends AppJSONMessage
{
	public ProxyDataSyncRequest(JSONObject jsonObject, PrintWriter out)
	{
		super(jsonObject, out);
		messageId = Consts.MessageId.ProxyDataSyncRequest;
	}
	
	@Override
	public MessageType getMessageType()
	{
		return MessageType.AppRequest;
	}
	
	@Override
	public MessageId getMessageId()
	{
		return MessageId.ProxyDataSyncRequest;
	}
	
	@Override
	public boolean checkJSONRequest()
	{
		if (!body.containsKey(JSONKey.AppVersion))
		{
			this.setErrorCode(GlobalErrorCode.InvalidJSONRequest_1100);
			this.setErrorDescription(JSONKey.AppVersion + " is missed");
			return false;
		}
		
		JSONObject obj = body.getJSONObject(JSONKey.AppVersion);
		if (!obj.containsKey(JSONKey.Version))
		{
			this.setErrorCode(GlobalErrorCode.InvalidJSONRequest_1100);
			this.setErrorDescription(JSONKey.Version + " under " + JSONKey.AppVersion + " is missed");
			return false;
		}
		
		if (!obj.containsKey(JSONKey.Version))
		{
			this.setErrorCode(GlobalErrorCode.InvalidJSONRequest_1100);
			this.setErrorDescription(JSONKey.Build + " under " + JSONKey.AppVersion + " is missed");
			return false;
		}
		
		return true;
	}

	@Override
	public void doRun()
	{
		logger.debug("Start to execute ProxyDataSyncRequest");
		
		try
		{
			if (!checkJSONRequest())
			{
				this.responseError();
				return;
			}
			
			JSONObject appVerObj = body.getJSONObject(JSONKey.AppVersion);
			String version = appVerObj.getString(JSONKey.Version);
			int build = appVerObj.getIntValue(JSONKey.Build);
			
			IServer server = GlobalSetting.instance.distributToServer(version, build);
			ProxyDataSyncResponse response = new ProxyDataSyncResponse(this, this.out);
			response.addToBody(JSONKey.Server, server.getResponse());
			response.run();
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			return;
		}
	}
	
}
