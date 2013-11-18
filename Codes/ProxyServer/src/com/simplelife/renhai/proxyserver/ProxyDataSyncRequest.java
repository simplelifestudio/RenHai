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
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.proxyserver.Consts.MessageId;
import com.simplelife.renhai.proxyserver.Consts.MessageType;
import com.simplelife.renhai.proxyserver.Consts.ServiceStatus;

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
		return null;
	}
	
	@Override
	public String getMessageSn()
	{
		return null;
	}
	
	@Override
	public MessageId getMessageId()
	{
		return null;
	}
	
	@Override
	public boolean checkJSONRequest()
	{
		return false;
	}

	@Override
	public void doRun()
	{
		ServiceStatus status = GlobalSetting.instance.getServiceStatus();
		
		ProxyDataSyncResponse response = new ProxyDataSyncResponse(this, this.out);
		
		Date begin = DateUtil.getDateByTimeZoneDateString(GlobalSetting.instance.getBeginTime());
		Date end = DateUtil.getDateByTimeZoneDateString(GlobalSetting.instance.getEndTime());
		
		if (status == ServiceStatus.Normal || System.currentTimeMillis() > end.getTime())
		{
			// Normal status, only provide serviceAddress
			response.addToBody(JSONKey.ServiceStatus, ServiceStatus.Normal.getValue());
			
			JSONObject addrObj = new JSONObject();
			addrObj.put(JSONKey.Ip, GlobalSetting.instance.getIpAddress());
			addrObj.put(JSONKey.Port, GlobalSetting.instance.getPort());
			addrObj.put(JSONKey.Path, GlobalSetting.instance.getPath());
			response.addToBody(JSONKey.ServiceAddress, addrObj);
			
			response.addToBody(JSONKey.StatusPeriod, null);
		}
		else
		{
			if (System.currentTimeMillis() < begin.getTime())
			{
				// to be maintained
				response.addToBody(JSONKey.ServiceStatus, ServiceStatus.Maintenance.getValue());
				JSONObject addrObj = new JSONObject();
				addrObj.put(JSONKey.Ip, GlobalSetting.instance.getIpAddress());
				addrObj.put(JSONKey.Port, GlobalSetting.instance.getPort());
				addrObj.put(JSONKey.Path, GlobalSetting.instance.getPath());
				response.addToBody(JSONKey.ServiceAddress, addrObj);
				
				JSONObject statusObj = new JSONObject();
				statusObj.put(JSONKey.BeginTime, GlobalSetting.instance.getBeginTime());
				statusObj.put(JSONKey.EndTime, GlobalSetting.instance.getEndTime());
				response.addToBody(JSONKey.StatusPeriod, statusObj);
			}
			else
			{
				// under maintenance
				response.addToBody(JSONKey.ServiceStatus, ServiceStatus.Maintenance.getValue());
				response.addToBody(JSONKey.ServiceAddress, null);
				
				JSONObject statusObj = new JSONObject();
				statusObj.put(JSONKey.BeginTime, GlobalSetting.instance.getBeginTime());
				statusObj.put(JSONKey.EndTime, GlobalSetting.instance.getEndTime());
				response.addToBody(JSONKey.StatusPeriod, statusObj);
			}
		}
		
		response.run();
	}
	
}
