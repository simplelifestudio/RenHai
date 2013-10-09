/**
 * Test23NormalProcessAndStatistics.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class Test23NormalProcessAndStatistics extends AbstractTestCase
{
	private MockApp mockApp1;
	private MockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp(demoDeviceSn);
		mockApp2 = createNewMockApp(demoDeviceSn2);
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
	}
	
	private void checkStat(OnlineDevicePool onlinePool, AbstractBusinessDevicePool businessPool)
	{
		JSONObject lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		JSONObject body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		JSONObject deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		JSONObject deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		JSONObject interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		// Invalid request before AppDataSyncRequest
		JSONObject header = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
		assertEquals(header.getIntValue(JSONKey.MessageId), Consts.MessageId.ServerDataSyncResponse.getValue());
		
		lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		assertEquals(onlinePool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Online));
		
		if (businessType == Consts.BusinessType.Random)
		{
			assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Random));
			assertEquals(0, deviceCountObj.getIntValue(JSONKey.Interest));
			assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.RandomChat));
			assertEquals(0, deviceCountObj.getIntValue(JSONKey.InterestChat));
		}
		else
		{
			assertEquals(0, deviceCountObj.getIntValue(JSONKey.Random));
			assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Interest));
			assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.InterestChat));
			assertEquals(0, deviceCountObj.getIntValue(JSONKey.RandomChat));
		}
		
		assertEquals(onlinePool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.Chat));
		assertEquals(GlobalSetting.BusinessSetting.OnlinePoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Online));
		assertEquals(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Random));
		assertEquals(GlobalSetting.BusinessSetting.InterestBusinessPoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Interest));
	}
	
	@Test
	public void test() throws InterruptedException
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(businessType);
		IDeviceWrapper deviceWrapper1 = OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn());
		
		// Step_01 Mock请求：查询所有统计项，包括：在线设备池设备数，在线设备池上限，随机业务设备池设备数，随机业务设备池上限，处于聊天状态的设备数，处于随机聊天状态的设备数，业务设备池的热门兴趣标签
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		// 未同步之前不允许查询
		assertTrue(mockApp1.lastReceivedCommandIsError());
		
		// Step_02 Mock请求：A进入随机聊天
		mockApp1.syncDevice();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		mockApp1.enterPool(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_03 Mock请求：B进入随机聊天
		mockApp2.syncDevice();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.enterPool(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_04 Mock请求：查询所有统计项
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.ServerDataSyncResponse, null));
		checkStat(onlinePool, businessPool);
		
		// Step_05 调用：RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		mockApp2.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBinded));
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBinded));
		
		Thread.sleep(500);
		
		// Step_06 调用：BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_07 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		
		// Step_08 Mock事件：A同意聊天
		mockApp1.chatConfirm(true);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));
		
		// Step_09 Mock事件：B同意聊天
		mockApp2.chatConfirm(true);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));
		
		// Step_10 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_11 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
		
		// Step_12 Mock事件：A结束通话
		mockApp1.endChat();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EndChat));
		
		// Step_13 调用：BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_14 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
		
		// Step_15 Mock事件：B结束通话
		mockApp2.endChat();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EndChat));
		
		// Step_16 Mock请求：查询所有统计项
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
		
		// Step_17 Mock事件：A onPing
		//mockApp1.ping();
		
		// Step_18 Mock事件：B onPing
		//mockApp2.ping();
		
		// Step_19 Mock事件：A对B评价
		//logger.debug("=============mockApp2 before assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		mockApp1.assessAndContinue(OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn()), "TC23_评价,^#Happy#^,帅哥");
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndContinue));
		//logger.debug("=============mockApp2 after assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		
		
		// Step_20 Mock事件：B对A评价
		//logger.debug("=============mockApp1 before assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		mockApp2.assessAndQuit(OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn()), "TC24_评价,^#Disgusting#^,变态");
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndQuit));
		//logger.debug("=============mockApp1 after assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		
		
		// Step_21 调用：BusinessSession::getStatus
		// 等待Server处理完评论并结束业务会话
		Thread.sleep(500);
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_22 Mock请求：查询所有统计项
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
	}
}
