/**
 * Test06SyncDeviceUnForbidden.java
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
public class Test06SyncDeviceUnForbidden extends TestCase
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
		// ǰ������ �豸A�ѽ���WebSocket���ӣ��ο�TC_01��
		// Step_01 ���ݿ���������豸A�ķ���״̬����Ϊ���ģ���������Ϊ����
		// Step_02 ���ã�OnlineDevicePool::getCount
		// Step_03 ���ã�DeviceWrapper::getServiceStatus
		// Step_04 Mock�����豸ͬ��
		// Step_05 ���ã�A DeviceWrapper::getServiceStatus
		// Step_06 ���ã�OnlineDevicePool::getCount
		// Step_06 ���ã�DeviceWrapper::getLastActivityTime
	}
}
