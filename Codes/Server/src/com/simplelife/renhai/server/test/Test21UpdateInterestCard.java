/**
 * Test21UpdateInterestCard.java
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
public class Test21UpdateInterestCard extends AbstractTestCase
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
		// Step_01 Mock����A�豸ͬ��
		// Step_02 Mock����A������Ȥ��Ƭ��5����Ȥ��ǩ
		// Step_03 ���ã�DeviceWrapper::getInterestCard
		// Step_04 ���ã�DaoWrapper::flush
		// Step_05 ���ݿ��飺A����Ȥ��ǩ
		// Step_06 Mock����A������Ȥ��Ƭ������1����Ȥ��ǩ
		// Step_07 ���ã�DeviceWrapper::getInterestCard
		// Step_08 ���ã�DaoWrapper::flush
		// Step_09 ���ݿ��飺A����Ȥ��ǩ
		// Step_10 Mock����A������Ȥ��Ƭ��ɾ��2����Ȥ��ǩ
		// Step_11 ���ã�DeviceWrapper::getInterestCard
		// Step_12 ���ã�DaoWrapper::flush
		// Step_13 ���ݿ��飺A����Ȥ��ǩ
		// Step_14 Mock����A������Ȥ��Ƭ���ı��ǩ˳��
		// Step_15 ���ã�DeviceWrapper::getInterestCard
		// Step_16 ���ã�DaoWrapper::flush
		// Step_17 ���ݿ��飺A����Ȥ��ǩ
		// Step_18 Mock����A��ѯ��Ȥ��Ƭ
	}
}
