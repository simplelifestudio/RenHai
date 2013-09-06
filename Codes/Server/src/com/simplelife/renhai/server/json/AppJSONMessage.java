/**
 * AppJSONMessage.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IAppJSONMessage;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class AppJSONMessage extends AbstractJSONMessage implements Runnable, IAppJSONMessage
{
    /** */
    protected JSONObject jsonObject;
    protected JSONObject header;
    protected JSONObject body;
    
    protected String errorDescription;
    protected int errorCode;
    
    public AppJSONMessage(JSONObject jsonObject)
    {
    	this.jsonObject = jsonObject;
    }
    
    public JSONObject getHeader()
    {
    	if (header == null)
    	{
    		header = jsonObject.getJSONObject(JSONKey.FieldName.Head);
    	}
    	return header;
    }
    
    public JSONObject getBody()
    {
    	if (body == null)
    	{
    		body = jsonObject.getJSONObject(JSONKey.FieldName.Body);
    	}
    	return body;
    }
    
    public int getMessageId()
    {
    	if (getHeader() == null)
    	{
    		return Consts.MessageId.Invalid;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageType))
    	{
    		return Consts.MessageId.Invalid;
    	}
    	
    	try
    	{
    		int id = header.getIntValue(JSONKey.FieldName.MessageType);
    		return id;
    	}
    	catch(Exception e)
    	{
    		errorDescription = "";
    	}
    	return Consts.MessageId.Invalid;
    }
    
    public String getMessageType()
    {
    	if (getHeader() == null)
    	{
    		return null;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageType))
    	{
    		return null;
    	}
    	
    	return header.getString(JSONKey.FieldName.MessageType); 
    }
    
    
    protected boolean checkJsonCommand()
    {
    	if (jsonObject == null)
		{
    		errorDescription = "Empty JSON Object is attached in request";
    		errorCode = Consts.GlobalErrorCode.InvalidJSONRequest_1100;
			return false;
		}
    	
    	JSONObject header = getHeader();
    	if (header == null)
    	{
    		errorDescription = "Invalid request: " + JSONKey.FieldName.Head+ " can't be found in request";
    		errorCode = Consts.GlobalErrorCode.ParameterError_1103;
    		return false;
    	}
    	
    	if (getBody() == null)
    	{
    		errorDescription = "Invalid request: " + JSONKey.FieldName.Body + " can't be found in request";
    		errorCode = Consts.GlobalErrorCode.ParameterError_1103;
    		return false;
    	}
    
    	if (!header.containsKey(JSONKey.FieldName.MessageType))
    	{
    		errorDescription = "Invalid request: " + JSONKey.FieldName.MessageType + " can't be found in request";
    		errorCode = Consts.GlobalErrorCode.ParameterError_1103;
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageSn))
    	{
    		errorDescription = "Invalid request: " + JSONKey.FieldName.MessageSn + " can't be found in request";
    		errorCode = Consts.GlobalErrorCode.ParameterError_1103;
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.MessageId))
    	{
    		errorDescription = "Invalid request: " + JSONKey.FieldName.MessageId + " can't be found in request";
    		errorCode = Consts.GlobalErrorCode.ParameterError_1103;
    		return false;
    	}
    	
    	/*
    	if (!header.containsKey(JSONKey.FieldName.DeviceId))
    	{
    		errorMessage = "Invalid request: " + JSONKey.FieldName.DeviceId + " can't be found in request";
    		return false;
    	}
    	*/
    	
    	if (!header.containsKey(JSONKey.FieldName.DeviceSn))
    	{
    		errorDescription = "Invalid request: " + JSONKey.FieldName.DeviceSn + " can't be found in request";
    		errorCode = Consts.GlobalErrorCode.ParameterError_1103;
    		return false;
    	}
    	
    	if (!header.containsKey(JSONKey.FieldName.TimeStamp))
    	{
    		errorDescription = "Invalid request: " + JSONKey.FieldName.TimeStamp + " can't be found in request";
    		errorCode = Consts.GlobalErrorCode.ParameterError_1103;
    		return false;
    	}
    	
    	return true;
    }
    
    
    protected ServerErrorResponse createErrorResponse()
    {
    	ServerErrorResponse reponse = new ServerErrorResponse(this.deviceWrapper);
    	return reponse;
    }

    protected void responseError()
    {
    	ServerErrorResponse response = createErrorResponse();
    	response.asyncResponse();
    }

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.AbstractJSONMessage#run()
	 */
	public void run()
	{
		// TODO: 如果执行到这个方法，说明request不能识别（缺少必要字段），
    	// 接下来根据之前保存的errorInfo给App返回错误
    	responseError();
	}
}
