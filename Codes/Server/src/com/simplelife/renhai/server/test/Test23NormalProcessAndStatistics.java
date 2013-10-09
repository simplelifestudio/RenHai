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
		
		// Step_01 Mock���󣺲�ѯ����ͳ��������������豸���豸���������豸�����ޣ����ҵ���豸���豸�������ҵ���豸�����ޣ���������״̬���豸���������������״̬���豸����ҵ���豸�ص�������Ȥ��ǩ
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		// δͬ��֮ǰ�������ѯ
		assertTrue(mockApp1.lastReceivedCommandIsError());
		
		// Step_02 Mock����A�����������
		mockApp1.syncDevice();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		mockApp1.enterPool(businessType);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_03 Mock����B�����������
		mockApp2.syncDevice();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.AppDataSyncResponse, null));
		
		businessPool.getBusinessScheduler().stopScheduler();
		mockApp2.enterPool(businessType);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EnterPool));
		
		// Step_04 Mock���󣺲�ѯ����ͳ����
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.ServerDataSyncResponse, null));
		checkStat(onlinePool, businessPool);
		
		// Step_05 ���ã�RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		mockApp2.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		
		mockApp1.waitMessage();
		assertTrue(mockApp1.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBinded));
		
		mockApp2.waitMessage();
		assertTrue(mockApp2.checkLastNotification(Consts.MessageId.BusinessSessionNotification, Consts.NotificationType.SessionBinded));
		
		Thread.sleep(500);
		
		// Step_06 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_07 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		
		// Step_08 Mock�¼���Aͬ������
		mockApp1.chatConfirm(true);
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));
		
		// Step_09 Mock�¼���Bͬ������
		mockApp2.chatConfirm(true);
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AgreeChat));
		
		// Step_10 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_11 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
		
		// Step_12 Mock�¼���A����ͨ��
		mockApp1.endChat();
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EndChat));
		
		// Step_13 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_14 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
		
		// Step_15 Mock�¼���B����ͨ��
		mockApp2.endChat();
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.EndChat));
		
		// Step_16 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
		
		// Step_17 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_18 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_19 Mock�¼���A��B����
		//logger.debug("=============mockApp2 before assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		mockApp1.assessAndContinue(OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn()), "TC23_����,^#Happy#^,˧��");
		assertTrue(mockApp1.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndContinue));
		//logger.debug("=============mockApp2 after assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp2.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		
		
		// Step_20 Mock�¼���B��A����
		//logger.debug("=============mockApp1 before assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		mockApp2.assessAndQuit(OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn()), "TC24_����,^#Disgusting#^,��̬");
		assertTrue(mockApp2.checkLastResponse(Consts.MessageId.BusinessSessionResponse, Consts.OperationType.AssessAndQuit));
		//logger.debug("=============mockApp1 after assess:\n");
		//System.out.print(JSON.toJSONString(OnlineDevicePool.instance.getDevice(mockApp1.getDeviceSn()).toJSONObject(), SerializerFeature.WriteMapNullValue));
		
		
		// Step_21 ���ã�BusinessSession::getStatus
		// �ȴ�Server���������۲�����ҵ��Ự
		Thread.sleep(500);
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_22 Mock���󣺲�ѯ����ͳ����
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		checkStat(onlinePool, businessPool);
	}
}
