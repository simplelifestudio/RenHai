/**
 * Test14TimeoutNotifyB.java
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
public class Test14TimeoutNotifyB extends AbstractTestCase
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
		// Step_04 ���ã�A DeviceWrapper::getBusinessStatus
		// Step_05 ���ã�B DeviceWrapper::getBusinessStatus
		// Step_06 Mock����A�����������
		// Step_07 Mock����B�����������
		// Step_08 ���ã�A DeviceWrapper::getBusinessStatus
		// Step_09 ���ã�B DeviceWrapper::getBusinessStatus
		// Step_10 ���ã�RandomBusinessDivicePool::getCount
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		// Step_12 ���ã�BusinessSessionPool::getCount
		// Step_13 ���ã�A DeviceWrapper::getBusinessStatus
		// Step_14 ���ã�B DeviceWrapper::getBusinessStatus
		// Step_15 ���ã�BusinessSession::getStatus
		// Step_16 Mock�¼���Aȷ�ϰ�
		// Step_17 Mock�¼���Bȷ�ϰ�
		// Step_18 ���ã�BusinessSession::getStatus
		// Step_19 ���ã�A DeviceWrapper::getBusinessStatus
		// Step_20 ���ã�B DeviceWrapper::getBusinessStatus
		// Step_21 Mock�¼���Aͬ������
		// Step_22 Mock�¼���B timeOut
		// Step_23 Mock�¼���A onPing
		// Step_24 Mock�¼���B onPing
		// Step_25 ���ã�OnlineDevicePool::getCount
		// Step_26 ���ã�BusinessSessionPool::getCount
		// Step_27 ���ã�A���ã�A DeviceWrapper::getBusinessStatus
	}
}
