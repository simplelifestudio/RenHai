package com.simplelife.renhai.proxyserver;

import java.io.PrintWriter;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.proxyserver.Consts.MessageId;
import com.simplelife.renhai.proxyserver.Consts.MessageType;

public class ProxyDataSyncResponse extends ServerJSONMessage
{
	public ProxyDataSyncResponse(AppJSONMessage request, PrintWriter out)
	{
		super(request, out);
		setMessageType(MessageType.ServerResponse);
		setMessageId(getMessageId());
	}

	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.ProxyDataSyncResponse;
	}
}
