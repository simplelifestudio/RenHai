/**
 * Test12TimeoutNotifyA.java
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
public class Test12TimeoutNotifyA extends TestCase
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
		// Step_10 ���ã�RandomBusinessDivicePool::getCount
		// Step_11 ���ã�RandomBusinessScheduler::schedule
		// Step_12 ���ã�BusinessSessionPool::getCount
		// Step_13 ���ã�A DeviceWrapper::getServiceStatus
		// Step_14 ���ã�B DeviceWrapper::getServiceStatus
		// Step_15 Mock�¼���A timeOut
		// Step_16 Mock�¼���A onPing
		// Step_17 Mock�¼���B onPing
		// Step_18 ���ã�OnlineDevicePool::getCount
		// Step_19 ���ã�BusinessSessionPool::getCount
		// Step_20 ���ã�B DeviceWrapper::getServiceStatus
	}
}
