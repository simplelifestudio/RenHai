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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.test.AbstractTestCase;
import com.simplelife.renhai.server.test.LocalMockApp;
import com.simplelife.renhai.server.test.MockWebSocketConnection;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class TestAppDataSyncRequest extends AbstractTestCase
{
	//private JSONObject wholeObj = new JSONObject();
	private JSONObject envelope = new JSONObject();
	private JSONObject header = new JSONObject();
	private JSONObject body = new JSONObject();

	private String deviceSn = "demoDeviceSn";
	private String deviceModel = "iPhone100";
	private String osVersion = "iOS100";
	private String appVersion = "1.01";
	private String location = "x, y";
	private String isJailed = "0";
	
	
	private void clear()
	{
		envelope.clear();
		header.clear();
		body.clear();
		
		//wholeObj.put(JSONKey.JsonEnvelope, envelope);
		envelope.put(JSONKey.Header, header);
		envelope.put(JSONKey.Body, body);
		
		deviceSn = deviceSn;
	}
	
	@Test
	public void testInvalidJSON_Empty()
	{
		//wholeObj.clear();
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_NoEnvelope()
	{
		envelope.clear();
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_NoHeader()
	{
		envelope.clear();
		//envelope.put(JSONKey.Header, header);
		envelope.put(JSONKey.Body, body);
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_NoBody()
	{
		envelope.clear();
		envelope.put(JSONKey.Header, header);
		//envelope.put(JSONKey.Body, body);
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_NoMessageSn()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		//header.put(JSONKey.MessageSn, "MessageSnfjdskajfdsklaj;");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_NoMessageId()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "MessageSnfjdskajfdsklaj;");
		//header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_NoDeviceSn()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "MessageSnfjdskajfdsklaj;");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		//header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_EmptyMessageSn()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, null);
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}

	@Test
	public void testInvalidJSON_EmptyBody()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	@Test
	public void testInvalidJSON_EmptyDataUpdate()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		JSONObject queryObj = new JSONObject();
		body.put(JSONKey.DataQuery, queryObj);
		
		JSONObject updateObj = new JSONObject();
		body.put(JSONKey.DataUpdate, updateObj);
		
		queryObj.put(JSONKey.Device, null);
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());

	}
	
	@Test
	public void testInvalidJSON_FullQueryNewDevice()
	{
		clear();
		
		deviceSn = DateUtil.getNow();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		
		body.put(JSONKey.DataQuery, newQueryObject());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		int errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getIntValue(JSONKey.ErrorCode);
		assertTrue(errorCode == Consts.GlobalErrorCode.ParameterError_1103.getValue());
	}
	
	private JSONObject newQueryObject()
	{
		JSONObject queryObj = new JSONObject();
		JSONObject deviceObj = new JSONObject();
		JSONObject deviceCardObj = new JSONObject();
		JSONObject profileObj = new JSONObject();
		JSONObject interestCardObj = new JSONObject();
		JSONObject impressCardObj = new JSONObject();
		
		queryObj.put(JSONKey.Device, deviceObj);
		
		deviceObj.put(JSONKey.DeviceId, null);
		deviceObj.put(JSONKey.DeviceSn, null);
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		
		deviceCardObj.put(JSONKey.DeviceCardId, null);
		deviceCardObj.put(JSONKey.DeviceId, null);
		deviceCardObj.put(JSONKey.RegisterTime, null);
		deviceCardObj.put(JSONKey.DeviceModel, null);
		deviceCardObj.put(JSONKey.OsVersion, null);
		deviceCardObj.put(JSONKey.AppVersion, null);
		deviceCardObj.put(JSONKey.Location, null);
		deviceCardObj.put(JSONKey.IsJailed, null);
		
		queryObj.put(JSONKey.Profile, profileObj);
		profileObj.put(JSONKey.ProfileId, null);
		profileObj.put(JSONKey.ServiceStatus, null);
		profileObj.put(JSONKey.UnbanDate, null);
		profileObj.put(JSONKey.LastActivityTime, null);
		profileObj.put(JSONKey.CreateTime, null);
		profileObj.put(JSONKey.Active, null);
		
		profileObj.put(JSONKey.InterestCard, interestCardObj);
		interestCardObj.put(JSONKey.InterestCardId, null);
		
		profileObj.put(JSONKey.InterestCard, impressCardObj);
		impressCardObj.put(JSONKey.ImpressCardId, null);
		impressCardObj.put(JSONKey.ChatTotalCount, null);
		impressCardObj.put(JSONKey.ChatTotalDuration, null);
		impressCardObj.put(JSONKey.ChatLossCount, null);
		impressCardObj.put(JSONKey.ImpressLabelList, "5");
		
		return queryObj;
	}
	
	@Test
	public void testInvalidJSON_FullUpdate()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		body.put(JSONKey.DataUpdate, newUpdateObject());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.getDevice(deviceSn);
		Device device = deviceWrapper.getDevice();
		Devicecard deviceCard = device.getDevicecard();
		
		assertTrue(deviceWrapper != null);
		assertTrue(deviceCard.getOsVersion().equals(this.osVersion));
		assertTrue(deviceCard.getAppVersion().equals(this.appVersion));
		//assertTrue(deviceCard.getIsJailed().equals(this.isJailed));
		assertTrue(deviceCard.getLocation().equals(this.location));
		assertTrue(deviceCard.getDeviceModel().equals(this.deviceModel));
	}
	
	
	@Test
	public void test_FullQuery()
	{
		clear();
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, CommonFunctions.getRandomString(16));
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		JSONObject queryObj = new JSONObject();
		body.put(JSONKey.DataQuery, queryObj);
		
		JSONObject deviceObj = new JSONObject();
		queryObj.put(JSONKey.Device, deviceObj);
		
		deviceObj.put(JSONKey.DeviceCard, null);
		deviceObj.put(JSONKey.Profile, null);
		deviceObj.put(JSONKey.DeviceSn, null);
		deviceObj.put(JSONKey.DeviceId, null);
		
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		assertTrue(!app.lastReceivedCommandIsError());
	}
	
	private JSONObject newUpdateObject()
	{
		JSONObject updateObj = new JSONObject();
		JSONObject deviceObj = new JSONObject();
		JSONObject deviceCardObj = new JSONObject();
		JSONObject profileObj = new JSONObject();
		JSONObject interestCardObj = new JSONObject();
		JSONObject impressCardObj = new JSONObject();
		
		updateObj.put(JSONKey.Device, deviceObj);
		
		//deviceObj.put(JSONKey.DeviceId, null);
		deviceObj.put(JSONKey.DeviceSn, deviceSn);
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		
		//deviceCardObj.put(JSONKey.DeviceCardId, null);
		//deviceCardObj.put(JSONKey.DeviceId, null);
		//deviceCardObj.put(JSONKey.RegisterTime, null);
		deviceCardObj.put(JSONKey.DeviceModel, deviceModel);
		deviceCardObj.put(JSONKey.OsVersion, osVersion);
		deviceCardObj.put(JSONKey.AppVersion, appVersion);
		deviceCardObj.put(JSONKey.Location, location);
		deviceCardObj.put(JSONKey.IsJailed, isJailed);
		
		deviceObj.put(JSONKey.Profile, profileObj);
		//profileObj.put(JSONKey.ProfileId, null);
		//profileObj.put(JSONKey.ServiceStatus, null);
		//profileObj.put(JSONKey.UnbanDate, null);
		//profileObj.put(JSONKey.LastActivityTime, null);
		//profileObj.put(JSONKey.CreateTime, null);
		//profileObj.put(JSONKey.Active, null);
		
		profileObj.put(JSONKey.InterestCard, interestCardObj);
		//interestCardObj.put(JSONKey.InterestCardId, null);
		JSONArray labelArray = new JSONArray();
		for (int i = 0; i < 5; i++)
		{
			JSONObject label = new JSONObject();
			label.put(JSONKey.InterestLabelName, "InterestLabel" + i);
			label.put(JSONKey.LabelOrder, i);
			labelArray.add(label);
		}
		interestCardObj.put(JSONKey.InterestLabelList, labelArray);
		
		profileObj.put(JSONKey.ImpressCard, impressCardObj);
		//impressCardObj.put(JSONKey.ImpressCardId, null);
		//impressCardObj.put(JSONKey.ChatTotalCount, null);
		//impressCardObj.put(JSONKey.ChatTotalDuration, null);
		//impressCardObj.put(JSONKey.ChatLossCount, null);
		//impressCardObj.put(JSONKey.ImpressLabelList, "5");
		
		return updateObj;
	}
	
	@Test
	public void testInvalidJSON_FullQueryAndUpdate()
	{
		clear();
		
		deviceSn = "demoDeviceSn2";
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		body.put(JSONKey.DataQuery, newQueryObject());
		body.put(JSONKey.DataUpdate, newUpdateObject());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.getDevice(deviceSn);
		Device device = deviceWrapper.getDevice();
		Devicecard deviceCard = device.getDevicecard();
		
		assertTrue(deviceWrapper != null);
		assertTrue(deviceCard.getOsVersion().equals(this.osVersion));
		assertTrue(deviceCard.getAppVersion().equals(this.appVersion));
		//assertTrue(deviceCard.getIsJailed().equals(this.isJailed));
		assertTrue(deviceCard.getLocation().equals(this.location));
		assertTrue(deviceCard.getDeviceModel().equals(this.deviceModel));
	}
	
	@Test
	public void testInvalidJSON_FullQueryExistentDevice()
	{
		clear();
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		body.put(JSONKey.DataQuery, newQueryObject());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		JSONObject obj = app.getLastReceivedCommand();
		System.out.print(obj.toJSONString());
	}
	
	@Test
	public void testInvalidJSON_PartialUpdate()
	{
		clear();
		String messageSn = CommonFunctions.getRandomString(16);
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageSn, messageSn);
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.DeviceId, null);
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		osVersion = "iOS7";
		appVersion = DateUtil.getNow();
		body.put(JSONKey.DataUpdate, newUpdateObject_Partial());
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.clearLastReceivedCommand();
		app.sendRawJSONMessage(envelope, true);
		app.waitMessage();
		
		JSONObject obj = app.getLastReceivedCommand(); 
		assertTrue(obj != null);
		assertTrue(messageSn.equals(obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header).getString(JSONKey.MessageSn)));
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.getDevice(deviceSn);
		Device device = deviceWrapper.getDevice();
		Devicecard deviceCard = device.getDevicecard();
		
		assertTrue(deviceCard.getAppVersion().equals(appVersion));
		assertTrue(deviceCard.getOsVersion().equals(osVersion));
		
		
	}
	
	@Test
	public void testImpressCardSync()
	{
		clear();
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		header.put(JSONKey.MessageSn, "HS2OJYQ2QI8300YN");
		header.put(JSONKey.DeviceId, "0");
		
		JSONObject queryObj = new JSONObject();
		body.put(JSONKey.DataQuery, queryObj);
		
		JSONObject deviceObj = new JSONObject();
		queryObj.put(JSONKey.Device, deviceObj);
		
		JSONObject profileObj = new JSONObject();
		deviceObj.put(JSONKey.Device, profileObj);
		
		profileObj.put(JSONKey.ImpressCard, null);
		
		LocalMockApp app = this.createNewMockApp(deviceSn);
		app.sendRawJSONMessage(envelope, true);
		
		assertTrue(!app.lastReceivedCommandIsError());
	}
	
	private JSONObject newUpdateObject_Partial()
	{
		JSONObject updateObj = new JSONObject();
		JSONObject deviceObj = new JSONObject();
		JSONObject deviceCardObj = new JSONObject();
		JSONObject profileObj = new JSONObject();
		JSONObject interestCardObj = new JSONObject();
		JSONObject impressCardObj = new JSONObject();
		
		updateObj.put(JSONKey.Device, deviceObj);
		
		deviceObj.put(JSONKey.DeviceSn, deviceSn);
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		
		deviceCardObj.put(JSONKey.DeviceModel, deviceModel);
		deviceCardObj.put(JSONKey.OsVersion, osVersion);
		deviceCardObj.put(JSONKey.AppVersion, appVersion);
		deviceCardObj.put(JSONKey.Location, location);
		deviceCardObj.put(JSONKey.IsJailed, isJailed);

		return updateObj;
	}
	
}
