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
		// 前置条件 设备A已建立WebSocket连接（参考TC_01）
		// Step_01 Mock请求：A设备同步
		// Step_02 Mock请求：A更新兴趣卡片，5个兴趣标签
		// Step_03 调用：DeviceWrapper::getInterestCard
		// Step_04 调用：DaoWrapper::flush
		// Step_05 数据库检查：A的兴趣标签
		// Step_06 Mock请求：A更新兴趣卡片，增加1个兴趣标签
		// Step_07 调用：DeviceWrapper::getInterestCard
		// Step_08 调用：DaoWrapper::flush
		// Step_09 数据库检查：A的兴趣标签
		// Step_10 Mock请求：A更新兴趣卡片，删除2个兴趣标签
		// Step_11 调用：DeviceWrapper::getInterestCard
		// Step_12 调用：DaoWrapper::flush
		// Step_13 数据库检查：A的兴趣标签
		// Step_14 Mock请求：A更新兴趣卡片，改变标签顺序
		// Step_15 调用：DeviceWrapper::getInterestCard
		// Step_16 调用：DaoWrapper::flush
		// Step_17 数据库检查：A的兴趣标签
		// Step_18 Mock请求：A查询兴趣卡片
	}
}
