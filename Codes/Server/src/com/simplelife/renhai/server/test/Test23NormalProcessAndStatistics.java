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

import com.alibaba.fastjson.JSONObject;
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
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp();
		mockApp2 = createNewMockApp();
		mockApp2.getDeviceWrapper().getDevice().setDeviceSn("SNOfDeviceB");
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
	
	@Test
	public void test()
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random);
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();
		
		// Step_01 Mock���󣺲�ѯ����ͳ��������������豸���豸���������豸�����ޣ����ҵ���豸���豸�������ҵ���豸�����ޣ���������״̬���豸���������������״̬���豸����ҵ���豸�ص�������Ȥ��ǩ
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		
		JSONObject lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		JSONObject body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		JSONObject deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		JSONObject deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		JSONObject interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		// Invalid request before AppDataSyncRequest
		JSONObject header = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
		assertEquals(header.getIntValue(JSONKey.MessageId), Consts.MessageId.ServerErrorResponse.getValue());
		
		/*
		assertEquals(onlinePool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Online));
		assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Random));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.Interest));
		assertEquals(onlinePool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.Chat));
		assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.RandomChat));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.InterestChat));
		
		assertEquals(GlobalSetting.BusinessSetting.OnlinePoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Online));
		assertEquals(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Random));
		assertEquals(GlobalSetting.BusinessSetting.InterestBusinessPoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Interest));
		*/
		
		// Step_02 Mock����A�����������
		mockApp1.syncDevice();
		mockApp1.enterPool(Consts.BusinessType.Random);
		
		// Step_03 Mock����B�����������
		mockApp2.syncDevice();
		mockApp2.enterPool(Consts.BusinessType.Random);
		
		// Step_04 Mock���󣺲�ѯ����ͳ����
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		
		lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		assertEquals(onlinePool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Online));
		assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Random));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.Interest));
		assertEquals(onlinePool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.Chat));
		assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.RandomChat));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.InterestChat));
		
		assertEquals(GlobalSetting.BusinessSetting.OnlinePoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Online));
		assertEquals(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Random));
		assertEquals(GlobalSetting.BusinessSetting.InterestBusinessPoolCapacity, deviceCapacityObject.getIntValue(JSONKey.Interest));

		
		// Step_05 ���ã�RandomBusinessScheduler::schedule
		mockApp1.clearLastReceivedCommand();
		mockApp2.clearLastReceivedCommand();
		businessPool.getBusinessScheduler().schedule();
		
		mockApp1.waitMessage();
		mockApp2.waitMessage();
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Step_06 ���ã�BusinessSession::getStatus
		IBusinessSession session = deviceWrapper1.getOwnerBusinessSession();
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.ChatConfirm);
		
		// Step_07 Mock���󣺲�ѯ����ͳ����
		mockApp1.sendServerDataSyncRequest();
		
		// Step_08 Mock�¼���Aͬ������
		mockApp1.chatConfirm(true);
		
		// Step_09 Mock�¼���Bͬ������
		mockApp2.chatConfirm(true);
		
		// Step_10 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.VideoChat);
		
		// Step_11 Mock���󣺲�ѯ����ͳ����
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		mockApp1.waitMessage();
		
		lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		assertEquals(onlinePool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Online));
		assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Random));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.Interest));
		assertEquals(onlinePool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.Chat));
		assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.RandomChat));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.InterestChat));
		
		
		// Step_12 Mock�¼���A����ͨ��
		mockApp1.endChat();
		
		// Step_13 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Assess);
		
		// Step_14 Mock���󣺲�ѯ����ͳ����
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		mockApp1.waitMessage();
		
		lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		assertEquals(onlinePool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Online));
		assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Random));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.Interest));
		assertEquals(onlinePool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.Chat));
		assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.RandomChat));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.InterestChat));
		
		// Step_15 Mock�¼���B����ͨ��
		mockApp2.endChat();
		
		// Step_16 Mock���󣺲�ѯ����ͳ����
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		mockApp1.waitMessage();
		
		lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		assertEquals(onlinePool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Online));
		assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Random));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.Interest));
		assertEquals(onlinePool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.Chat));
		assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.RandomChat));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.InterestChat));
		
		
		// Step_17 Mock�¼���A onPing
		//mockApp1.ping();
		
		// Step_18 Mock�¼���B onPing
		//mockApp2.ping();
		
		// Step_19 Mock�¼���A��B����
		mockApp1.assessAndContinue(mockApp2.getDeviceWrapper(), "TC23_����,ϲ��,˧��");
		
		// Step_20 Mock�¼���B��A����
		mockApp2.assessAndQuit(mockApp1.getDeviceWrapper(), "TC24_����,����,��̬");
		
		// Step_21 ���ã�BusinessSession::getStatus
		assertEquals(session.getStatus(), Consts.BusinessSessionStatus.Idle);
		
		// Step_22 Mock���󣺲�ѯ����ͳ����
		mockApp1.clearLastReceivedCommand();
		mockApp1.sendServerDataSyncRequest();
		
		lastCmd = mockApp1.getLastReceivedCommand();
		assertTrue(lastCmd != null);
		assertTrue(lastCmd.containsKey(JSONKey.JsonEnvelope));
		body = lastCmd.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		
		deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		deviceCapacityObject = body.getJSONObject(JSONKey.DeviceCapacity);
		interestObj = body.getJSONObject(JSONKey.InterestLabelList);
		
		assertEquals(onlinePool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Online));
		assertEquals(businessPool.getElementCount(), deviceCountObj.getIntValue(JSONKey.Random));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.Interest));
		assertEquals(onlinePool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.Chat));
		assertEquals(businessPool.getDeviceCountInChat(), deviceCountObj.getIntValue(JSONKey.RandomChat));
		assertEquals(0, deviceCountObj.getIntValue(JSONKey.InterestChat));
		
	}
}
