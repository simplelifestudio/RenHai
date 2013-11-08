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
import com.simplelife.renhai.server.business.pool.OutputMessageCenter;
import com.simplelife.renhai.server.log.DbLogger;
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
		messageId = Consts.MessageId.AlohaRequest;
	}

	/**
	 * Check if JSON request is valid
	 */
	@Override
	protected boolean checkJSONRequest()
    {
		return true;
    }
	
    @Override
    public void doRun()
    {
    	Logger logger = JSONModule.instance.getLogger();
    	logger.debug("Start run of AlohaRequest");
    	if (!checkJSONRequest())
    	{
    		logger.debug("checkJSONRequest failed");
    		ServerErrorResponse response = new ServerErrorResponse(this);
    		response.addToBody(JSONKey.ReceivedMessage, Consts.MessageId.AlohaResponse);
    		response.addToBody(JSONKey.ErrorCode, this.getErrorCode());
    		response.addToBody(JSONKey.ErrorDescription, this.getErrorDescription());
    		OutputMessageCenter.instance.addMessage(response);
    	}
    	
    	ServerJSONMessage response = JSONFactory.createServerJSONMessage(this, Consts.MessageId.AlohaResponse);
    	if (response == null)
    	{
    		logger.error("createServerJSONMessage returns null");
    		return;
    	}

    	DbLogger.saveSystemLog(Consts.OperationCode.AlohaRequest_1002
    			, Consts.SystemModule.business
    			, header.getString(JSONKey.DeviceSn));
    	
    	response.addToBody(JSONKey.Content, helloApp);
    	response.response();
    	DbLogger.saveSystemLog(Consts.OperationCode.AlohaResponse_1003
    			, Consts.SystemModule.business
    			, header.getString(JSONKey.DeviceSn));
    }

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.AlohaRequest;
	}
}
