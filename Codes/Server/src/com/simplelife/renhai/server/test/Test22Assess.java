/**
 * Test22Assess.java
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
public class Test22Assess extends TestCase
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
		// Step_01 Mock����A�����������
		// Step_02 Mock����B�����������
		// Step_03 Mock����A����B��ӡ��Ƭ
		// Step_04 ���ã�RandomBusinessScheduler::schedule
		// Step_05 Mock�¼���Aͬ������
		// Step_06 Mock�¼���Bͬ������
		// Step_07 ���ã�BusinessSession::getStatus
		// Step_08 Mock����A����B��ӡ��Ƭ
		// Step_09 Mock�¼���A����ͨ��
		// Step_10 ���ã�BusinessSession::getStatus
		// Step_11 Mock�¼���A��A����
		// Step_12 Mock�¼���A��B����
		// Step_13 Mock�¼���B��A����
		// Step_14 ���ݿ��飺A ӡ��Ƭ��Ϣ
		// Step_15 ���ݿ��飺B ӡ��Ƭ��Ϣ
		// Step_16 ���ã�BusinessSession::getStatus
		// Step_17 Mock����A��ѯӡ��Ƭ��ȫ����ǩ
		// Step_18 Mock����B��ѯӡ��Ƭ��ǰ2����ǩ
	}
}
