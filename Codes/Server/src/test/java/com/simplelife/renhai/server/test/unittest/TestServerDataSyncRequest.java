/**
 * TestSververDataSyncRequest.java
 * 
 * History:
 *     2013-9-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.test.AbstractTestCase;
import com.simplelife.renhai.server.test.MockApp;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class TestServerDataSyncRequest extends AbstractTestCase 
{
	/*
	private JSONObject envelope = new JSONObject();
	private JSONObject header = new JSONObject();
	private JSONObject body = new JSONObject();
	
	String deviceSn = "demoDeviceSn";

	private void clear()
	{
		envelope.clear();
		header.clear();
		body.clear();

		envelope.put(JSONKey.Header, header);
		envelope.put(JSONKey.Body, body);
	}

	@Test
	public void testWholeSync()
	{
		clear();
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageId, Consts.MessageId.ServerDataSyncRequest.getValue());
		header.put(JSONKey.MessageSn, CommonFunctions.getRandomString(16));
		header.put(JSONKey.DeviceId, 0);
		
		JSONObject capacityObj = new JSONObject();
		body.put(JSONKey.DeviceCapacity, capacityObj);
		capacityObj.put(JSONKey.Online, null);
		capacityObj.put(JSONKey.Interest, null);
		capacityObj.put(JSONKey.Random, null);
		
		JSONObject countObj = new JSONObject();
		body.put(JSONKey.DeviceCount, countObj);
		countObj.put(JSONKey.Chat, null);
		countObj.put(JSONKey.Online, null);
		countObj.put(JSONKey.Interest, null);
		countObj.put(JSONKey.Random, null);
		countObj.put(JSONKey.InterestChat, null);
		countObj.put(JSONKey.RandomChat, null);
		
		JSONObject interestObj = new JSONObject();
		body.put(JSONKey.InterestLabelList, interestObj);
		interestObj.put(JSONKey.Current, null);
		
		JSONObject historyObj = new JSONObject();
		interestObj.put(JSONKey.History, historyObj);
		historyObj.put(JSONKey.StartTime, null);
		historyObj.put(JSONKey.EndTime, null);
		
		MockApp app = this.createNewMockApp(deviceSn);
		app.syncDevice();
		assertTrue(!app.lastReceivedCommandIsError());
		
		app.chooseBusiness(businessType);
		
		app.sendRawJSONMessage(envelope, true);
		assertTrue(!app.lastReceivedCommandIsError());
	}
	
	@Test
	public void testPartialDeviceCount()
	{
		clear();
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageId, Consts.MessageId.ServerDataSyncRequest.getValue());
		header.put(JSONKey.MessageSn, CommonFunctions.getRandomString(16));
		header.put(JSONKey.DeviceId, 0);
		
		JSONObject capacityObj = new JSONObject();
		body.put(JSONKey.DeviceCapacity, capacityObj);
		capacityObj.put(JSONKey.Online, null);
		capacityObj.put(JSONKey.Interest, null);
		capacityObj.put(JSONKey.Random, null);
		
		JSONObject countObj = new JSONObject();
		body.put(JSONKey.DeviceCount, countObj);
		countObj.put(JSONKey.Chat, null);
		countObj.put(JSONKey.Online, null);
		countObj.put(JSONKey.Random, null);
		countObj.put(JSONKey.RandomChat, null);
		
		MockApp app = this.createNewMockApp(deviceSn);
		//app.syncDevice();
		//assertTrue(!app.lastReceivedCommandIsError());
		
		app.sendRawJSONMessage(envelope, true);
		assertTrue(app.lastReceivedCommandIsError());
	}
	
	@Test
	public void testNullDeviceCount()
	{
		clear();
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageId, Consts.MessageId.ServerDataSyncRequest.getValue());
		header.put(JSONKey.MessageSn, CommonFunctions.getRandomString(16));
		header.put(JSONKey.DeviceId, 0);
		
		JSONObject capacityObj = new JSONObject();
		body.put(JSONKey.DeviceCapacity, capacityObj);
		capacityObj.put(JSONKey.Online, null);
		capacityObj.put(JSONKey.Interest, null);
		capacityObj.put(JSONKey.Random, null);
		
		body.put(JSONKey.DeviceCount, null);
		
		JSONObject interestObj = new JSONObject();
		body.put(JSONKey.InterestLabelList, interestObj);
		interestObj.put(JSONKey.Current, null);
		
		JSONObject historyObj = new JSONObject();
		interestObj.put(JSONKey.History, historyObj);
		historyObj.put(JSONKey.StartTime, null);
		historyObj.put(JSONKey.EndTime, null);
		
		
		MockApp app = this.createNewMockApp(deviceSn);
		app.syncDevice();
		assertTrue(!app.lastReceivedCommandIsError());
		
		app.sendRawJSONMessage(envelope, true);
		assertTrue(!app.lastReceivedCommandIsError());
	}
	*/
	public void testNullDeviceCount()
	{
	}
}
