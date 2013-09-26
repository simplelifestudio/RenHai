/**
 * Test02CreateRealConnection.java
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

/**
 * 
 */
public class Test02CreateRealConnection extends AbstractTestCase
{
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void test()
	{
		// 前置条件 Server已经部署
		// Step_01 通过echo.html和server建立WebSocket连接
		// Step_02 通过页面发送设备同步的Json命令
	}
}
