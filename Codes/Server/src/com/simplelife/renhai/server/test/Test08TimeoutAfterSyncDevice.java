/**
 * Test08TimeoutAfterSyncDevice.java
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
public class Test08TimeoutAfterSyncDevice extends TestCase
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
		// ǰ������ �豸�ѽ���WebSocket���ӣ��ο�TC_01��
		// Step_01 ���ã�OnlineDevicePool::getCount
		// Step_02 Mock�����豸ͬ��
		// Step_03 ���ã�DeviceWrapper::getServiceStatus
		// Step_04 ���ã�OnlineDevicePool::getCount
		// Step_05 �ȴ�Server��Websocketͨ���쳣ʱ��
		// Step_06 Mock�¼���onPing
		// Step_07 ���ã�OnlineDevicePool::getCount
	}
}
