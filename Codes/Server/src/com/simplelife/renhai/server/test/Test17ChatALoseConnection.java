/**
 * Test17ChatALoseConnection.java
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

import junit.framework.TestCase;

/**
 * 
 */
public class Test17ChatALoseConnection extends TestCase
{
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void test()
	{
		// ǰ������ �豸A���豸B���ѽ���WebSocket���ӣ��ο�TC_01��
		// Step_01 ���ã�OnlineDevicePool::getCount
		// Step_02 ���ã�RandomBusinessDivicePool::getCount
		// Step_03 ���ã�BusinessSessionPool::getCount
		// Step_04 ���ã�A DeviceWrapper::getServiceStatus
		// Step_05 ���ã�B DeviceWrapper::getServiceStatus
		// Step_06 Mock����A�����������
		// Step_07 Mock����B�����������
		// Step_08 ���ã�A DeviceWrapper::getServiceStatus
		// Step_09 ���ã�B DeviceWrapper::getServiceStatus
		// Step_10 ���ã�BusinessSession::getStatus
		// Step_11 ���ã�RandomBusinessDivicePool::getCount
		// Step_12 ���ã�RandomBusinessScheduler::schedule
		// Step_13 ���ã�BusinessSessionPool::getCount
		// Step_14 ���ã�A DeviceWrapper::getServiceStatus
		// Step_15 ���ã�B DeviceWrapper::getServiceStatus
		// Step_16 ���ã�BusinessSession::getStatus
		// Step_17 Mock�¼���Aȷ�ϰ�
		// Step_18 Mock�¼���Bȷ�ϰ�
		// Step_19 ���ã�BusinessSession::getStatus
		// Step_20 ���ã�A DeviceWrapper::getServiceStatus
		// Step_21 ���ã�B DeviceWrapper::getServiceStatus
		// Step_22 Mock�¼���Aͬ������
		// Step_23 Mock�¼���Bͬ������
		// Step_24 ���ã�BusinessSession::getStatus
		// Step_25 Mock�¼���A onClose
		// Step_26 ���ã�BusinessSession::getStatus
		// Step_27 ���ã�OnlineDevicePool::getCount
		// Step_28 ���ã�BusinessSessionPool::getCount
		// Step_29 ���ã�B DeviceWrapper::getServiceStatus
		// Step_30 Mock�¼���A onPing
		// Step_31 Mock�¼���B onPing
		// Step_32 Mock�¼���B��A����
		// Step_33 ���ݿ��飺A ӡ��Ƭ��Ϣ
		// Step_34 ���ݿ��飺B ӡ��Ƭ��Ϣ
		// Step_35 ���ã�BusinessSession::getStatus
		// Step_36 ���ã�BusinessSessionPool::getCount
		// Step_37 ���ã�B DeviceWrapper::getServiceStatus
	}
}
