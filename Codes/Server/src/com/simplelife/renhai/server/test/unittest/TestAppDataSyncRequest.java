/**
 * TestAppDataSyncRequest.java
 * 
 * History:
 *     2013-9-12: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.test.MockWebSocketConnection;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.JSONKey;

import junit.framework.TestCase;

/**
 * 
 */
public class TestAppDataSyncRequest extends TestCase
{
	private JSONObject wholeObj = new JSONObject();
	private JSONObject envelope = new JSONObject();
	private JSONObject header = new JSONObject();
	private JSONObject body = new JSONObject();
	
	MockWebSocketConnection conn = new MockWebSocketConnection("1");
	
	private void clear()
	{
		wholeObj.clear();
		envelope.clear();
		header.clear();
		body.clear();
		
		wholeObj.put(JSONKey.JsonEnvelope, envelope);
		envelope.put(JSONKey.Header, header);
		envelope.put(JSONKey.Body, body);
	}
	
	@Test
	public void testInvalidJSON_InvalidString()
	{
		DeviceWrapper deviceWrapper = new DeviceWrapper(conn);
		conn.onTextMessage("fjdskajsdklaj{");
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.InvalidJSONRequest_1100));
	}
	
	@Test
	public void testInvalidJSON_Empty()
	{
		wholeObj.clear();
		
		MockWebSocketConnection conn = new MockWebSocketConnection("1");
		DeviceWrapper deviceWrapper = new DeviceWrapper(conn);
		conn.onTextMessage(wholeObj.toJSONString());
	}
	
	@Test
	public void testInvalidJSON_NoEnvelope()
	{
		wholeObj.clear();
		envelope.put(JSONKey.Header, header);
		envelope.put(JSONKey.Body, body);
		
		AppJSONMessage appRequest = JSONFactory.createAppJSONMessage(wholeObj);
		DeviceWrapper deviceWrapper = new DeviceWrapper(null);
		appRequest.bindDeviceWrapper(deviceWrapper);
		appRequest.run();
	}
	
	@Test
	public void testInvalidJSON_NoHeader()
	{
	}
	
	@Test
	public void testInvalidJSON_NoBody()
	{
	}
	
	@Test
	public void testInvalidJSON_NoMessageType()
	{
	}
	
	@Test
	public void testInvalidJSON_NoMessageSn()
	{
	}
	
	@Test
	public void testInvalidJSON_NoMessageId()
	{
	}
	
	@Test
	public void testInvalidJSON_NoDeviceSn()
	{
	}
	
	@Test
	public void testInvalidJSON_EmptyMessageSn()
	{
	}

	@Test
	public void testInvalidJSON_EmptyBody()
	{
	}
	
	@Test
	public void testInvalidJSON_EmptyDataUpdate()
	{
	}
	
	@Test
	public void testInvalidJSON_FullQuery()
	{
	}
	
	@Test
	public void testInvalidJSON_FullUpdate()
	{
	}
	
	@Test
	public void testInvalidJSON_FullQueryAndUpdate()
	{
	}
}
