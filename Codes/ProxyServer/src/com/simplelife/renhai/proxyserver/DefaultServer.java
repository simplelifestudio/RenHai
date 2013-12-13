package com.simplelife.renhai.proxyserver;

import com.alibaba.fastjson.JSONObject;

public class DefaultServer implements IServer
{
	private JSONObject response = new JSONObject();
	
	public DefaultServer(JSONObject obj)
	{
		response.put(JSONKey.Id, getId());
		
		if (obj.containsKey(JSONKey.Broadcast))
		{
			response.put(JSONKey.Broadcast, obj.getString(JSONKey.Broadcast));
		}
		else
		{
			response.put(JSONKey.Broadcast, null);
		}
	}
	
	@Override
	public int compareTo(IServer o)
	{
		// To ensure DefaultServer is the last one
		return -1;
	}
	
	@Override
	public String getId()
	{
		return "Default";
	}

	@Override
	public boolean checkVersion(String version, int build)
	{
		return true;
	}

	@Override
	public JSONObject getResponse()
	{
		return response;
	}

	@Override
	public void init(JSONObject obj)
	{
		return;
	}
}
