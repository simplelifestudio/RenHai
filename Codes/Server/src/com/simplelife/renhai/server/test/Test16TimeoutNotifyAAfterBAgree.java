/**
 * Test16TimeoutNotifyAAfterBAgree.java
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
public class Test16TimeoutNotifyAAfterBAgree extends AbstractTestCase
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
		// Step_16 Mock�¼���Bȷ�ϰ�
		// Step_17 ���ã�BusinessSession::getStatus
		// Step_18 ���ã�A DeviceWrapper::getBusinessStatus
		// Step_19 ���ã�B DeviceWrapper::getBusinessStatus
		// Step_20 Mock�¼���Bͬ������
		// Step_21 Mock�¼���A timeOut
		// Step_22 Mock�¼���A onPing
		// Step_23 Mock�¼���B onPing
		// Step_24 ���ã�OnlineDevicePool::getCount
		// Step_25 ���ã�BusinessSessionPool::getCount
		// Step_26 ���ã�B DeviceWrapper::getBusinessStatus
	}
}
