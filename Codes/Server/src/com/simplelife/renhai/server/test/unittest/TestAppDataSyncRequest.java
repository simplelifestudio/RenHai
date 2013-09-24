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
import com.simplelife.renhai.server.test.MockWebSocketConnection;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;
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

	private String deviceSn = "demoDeviceSn";
	private String deviceModel = "iPhone100";
	private String osVersion = "iOS100";
	private String appVersion = "1.01";
	private String location = "x, y";
	private String isJailed = "0";
	
	MockWebSocketConnection conn = new MockWebSocketConnection();
	
	private void clear()
	{
		wholeObj.clear();
		envelope.clear();
		header.clear();
		body.clear();
		conn.clear();
		
		wholeObj.put(JSONKey.JsonEnvelope, envelope);
		envelope.put(JSONKey.Header, header);
		envelope.put(JSONKey.Body, body);
		
		deviceSn = "demoDeviceSn";
	}
	
	@Test
	public void testInvalidJSON_InvalidString()
	{
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage("fjdskajsdklaj{");
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.InvalidJSONRequest_1100.toString()));
	}
	
	@Test
	public void testInvalidJSON_Empty()
	{
		wholeObj.clear();
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.InvalidJSONRequest_1100.toString()));
	}
	
	@Test
	public void testInvalidJSON_NoEnvelope()
	{
		wholeObj.clear();
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.InvalidJSONRequest_1100.toString()));
	}
	
	@Test
	public void testInvalidJSON_NoHeader()
	{
		wholeObj.clear();
		wholeObj.put(JSONKey.JsonEnvelope, envelope);
		//envelope.put(JSONKey.Header, header);
		envelope.put(JSONKey.Body, body);
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));
	}
	
	@Test
	public void testInvalidJSON_NoBody()
	{
		wholeObj.clear();
		wholeObj.put(JSONKey.JsonEnvelope, envelope);
		envelope.put(JSONKey.Header, header);
		//envelope.put(JSONKey.Body, body);
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));
	}
	
	@Test
	public void testInvalidJSON_NoMessageSn()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		//header.put(JSONKey.MessageSn, "MessageSnfjdskajfdsklaj;");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));
	}
	
	@Test
	public void testInvalidJSON_NoMessageId()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "MessageSnfjdskajfdsklaj;");
		//header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));
	}
	
	@Test
	public void testInvalidJSON_NoDeviceSn()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "MessageSnfjdskajfdsklaj;");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		//header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));
	}
	
	@Test
	public void testInvalidJSON_EmptyMessageSn()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));
	}

	@Test
	public void testInvalidJSON_EmptyBody()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));
	}
	
	@Test
	public void testInvalidJSON_EmptyDataUpdate()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, "DeviceSnjfkdlajujfkdla;jl");
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		JSONObject queryObj = new JSONObject();
		body.put(JSONKey.DataQuery, queryObj);
		
		JSONObject updateObj = new JSONObject();
		body.put(JSONKey.DataUpdate, updateObj);
		
		queryObj.put(JSONKey.Device, "");
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));

	}
	
	@Test
	public void testInvalidJSON_FullQueryNewDevice()
	{
		clear();
		
		deviceSn = DateUtil.getNow();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		
		body.put(JSONKey.DataQuery, newQueryObject());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		String errorCode = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body).getString(JSONKey.ErrorCode);
		assertTrue(errorCode.equals(Consts.GlobalErrorCode.ParameterError_1103.toString()));

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
		
		deviceObj.put(JSONKey.DeviceId, "");
		deviceObj.put(JSONKey.DeviceSn, "");
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		
		deviceCardObj.put(JSONKey.DeviceCardId, "");
		deviceCardObj.put(JSONKey.DeviceId, "");
		deviceCardObj.put(JSONKey.RegisterTime, "");
		deviceCardObj.put(JSONKey.DeviceModel, "");
		deviceCardObj.put(JSONKey.OsVersion, "");
		deviceCardObj.put(JSONKey.AppVersion, "");
		deviceCardObj.put(JSONKey.Location, "");
		deviceCardObj.put(JSONKey.IsJailed, "");
		
		queryObj.put(JSONKey.Profile, profileObj);
		profileObj.put(JSONKey.ProfileId, "");
		profileObj.put(JSONKey.ServiceStatus, "");
		profileObj.put(JSONKey.UnbanDate, "");
		profileObj.put(JSONKey.LastActivityTime, "");
		profileObj.put(JSONKey.CreateTime, "");
		profileObj.put(JSONKey.Active, "");
		
		profileObj.put(JSONKey.InterestCard, interestCardObj);
		interestCardObj.put(JSONKey.InterestCardId, "");
		
		profileObj.put(JSONKey.InterestCard, impressCardObj);
		impressCardObj.put(JSONKey.ImpressCardId, "");
		impressCardObj.put(JSONKey.ChatTotalCount, "");
		impressCardObj.put(JSONKey.ChatTotalDuration, "");
		impressCardObj.put(JSONKey.ChatLossCount, "");
		impressCardObj.put(JSONKey.ImpressLabelList, "5");
		
		return queryObj;
	}
	
	@Test
	public void testInvalidJSON_FullUpdate()
	{
		clear();
		
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		body.put(JSONKey.DataUpdate, newUpdateObject());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		deviceWrapper = OnlineDevicePool.instance.getDevice(deviceSn);
		Device device = deviceWrapper.getDevice();
		Devicecard deviceCard = device.getDevicecard();
		
		assertTrue(deviceWrapper != null);
		assertTrue(deviceCard.getOsVersion().equals(this.osVersion));
		assertTrue(deviceCard.getAppVersion().equals(this.appVersion));
		//assertTrue(deviceCard.getIsJailed().equals(this.isJailed));
		assertTrue(deviceCard.getLocation().equals(this.location));
		assertTrue(deviceCard.getDeviceModel().equals(this.deviceModel));
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
		
		//deviceObj.put(JSONKey.DeviceId, "");
		deviceObj.put(JSONKey.DeviceSn, deviceSn);
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		
		//deviceCardObj.put(JSONKey.DeviceCardId, "");
		//deviceCardObj.put(JSONKey.DeviceId, "");
		//deviceCardObj.put(JSONKey.RegisterTime, "");
		deviceCardObj.put(JSONKey.DeviceModel, deviceModel);
		deviceCardObj.put(JSONKey.OsVersion, osVersion);
		deviceCardObj.put(JSONKey.AppVersion, appVersion);
		deviceCardObj.put(JSONKey.Location, location);
		deviceCardObj.put(JSONKey.IsJailed, isJailed);
		
		deviceObj.put(JSONKey.Profile, profileObj);
		//profileObj.put(JSONKey.ProfileId, "");
		//profileObj.put(JSONKey.ServiceStatus, "");
		//profileObj.put(JSONKey.UnbanDate, "");
		//profileObj.put(JSONKey.LastActivityTime, "");
		//profileObj.put(JSONKey.CreateTime, "");
		//profileObj.put(JSONKey.Active, "");
		
		profileObj.put(JSONKey.InterestCard, interestCardObj);
		//interestCardObj.put(JSONKey.InterestCardId, "");
		JSONArray labelArray = new JSONArray();
		for (int i = 0; i < 5; i++)
		{
			JSONObject label = new JSONObject();
			label.put(JSONKey.InterestLabel, "InterestLabel" + i);
			label.put(JSONKey.LabelOrder, i);
			labelArray.add(label);
		}
		interestCardObj.put(JSONKey.InterestLabelList, labelArray);
		
		profileObj.put(JSONKey.ImpressCard, impressCardObj);
		//impressCardObj.put(JSONKey.ImpressCardId, "");
		//impressCardObj.put(JSONKey.ChatTotalCount, "");
		//impressCardObj.put(JSONKey.ChatTotalDuration, "");
		//impressCardObj.put(JSONKey.ChatLossCount, "");
		//impressCardObj.put(JSONKey.ImpressLabelList, "5");
		
		return updateObj;
	}
	
	@Test
	public void testInvalidJSON_FullQueryAndUpdate()
	{
		clear();
		
		deviceSn = "demoDeviceSn2";
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		body.put(JSONKey.DataQuery, newQueryObject());
		body.put(JSONKey.DataUpdate, newUpdateObject());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		deviceWrapper = OnlineDevicePool.instance.getDevice(deviceSn);
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
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		body.put(JSONKey.DataQuery, newQueryObject());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		JSONObject obj = conn.getLastSentMessage();
		System.out.print(obj.toJSONString());
	}
	
	@Test
	public void testInvalidJSON_PartialUpdate()
	{
		clear();
		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
		header.put(JSONKey.MessageSn, "fdsafdsareafds");
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		header.put(JSONKey.DeviceId, "");
		header.put(JSONKey.DeviceSn, deviceSn);
		header.put(JSONKey.TimeStamp, DateUtil.getNow());
		
		osVersion = "iOS7";
		appVersion = DateUtil.getNow();
		body.put(JSONKey.DataUpdate, newUpdateObject_Partial());
		
		IDeviceWrapper deviceWrapper = OnlineDevicePool.instance.newDevice(conn);
		conn.onTextMessage(wholeObj.toJSONString());
		
		deviceWrapper = OnlineDevicePool.instance.getDevice(deviceSn);
		Device device = deviceWrapper.getDevice();
		Devicecard deviceCard = device.getDevicecard();
		
		assertTrue(deviceCard.getAppVersion().equals(appVersion));
		assertTrue(deviceCard.getOsVersion().equals(osVersion));
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
