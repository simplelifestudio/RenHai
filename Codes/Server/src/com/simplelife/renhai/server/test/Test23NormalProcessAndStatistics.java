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

import junit.framework.TestCase;

/**
 * 
 */
public class Test23NormalProcessAndStatistics extends AbstractTestCase
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
		// Step_01 Mock���󣺲�ѯ����ͳ��������������豸���豸���������豸�����ޣ����ҵ���豸���豸�������ҵ���豸�����ޣ���������״̬���豸���������������״̬���豸����ҵ���豸�ص�������Ȥ��ǩ
		// Step_02 Mock����A�����������
		// Step_03 Mock����B�����������
		// Step_04 Mock���󣺲�ѯ����ͳ����
		// Step_05 ���ã�RandomBusinessScheduler::schedule
		// Step_06 ���ã�BusinessSession::getStatus
		// Step_07 Mock���󣺲�ѯ����ͳ����
		// Step_08 Mock�¼���Aͬ������
		// Step_09 Mock�¼���Bͬ������
		// Step_10 ���ã�BusinessSession::getStatus
		// Step_11 Mock���󣺲�ѯ����ͳ����
		// Step_12 Mock�¼���A����ͨ��
		// Step_13 ���ã�BusinessSession::getStatus
		// Step_14 Mock���󣺲�ѯ����ͳ����
		// Step_15 Mock�¼���B����ͨ��
		// Step_16 Mock���󣺲�ѯ����ͳ����
		// Step_17 Mock�¼���A onPing
		// Step_18 Mock�¼���B onPing
		// Step_19 Mock�¼���A��B����
		// Step_20 Mock�¼���B��A����
		// Step_21 ���ã�BusinessSession::getStatus
		// Step_22 Mock���󣺲�ѯ����ͳ����
	}
}
