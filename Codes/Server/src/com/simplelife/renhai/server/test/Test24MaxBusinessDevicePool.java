/**
 * Test24MaxBusinessDevicePool.java
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

import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.GlobalSetting;

/**
 * 
 */
public class Test24MaxBusinessDevicePool extends AbstractTestCase
{
	private LocalMockApp mockApp1;
	private LocalMockApp mockApp2;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		mockApp1 = createMockApp();
		mockApp2 = createMockApp();
		mockApp2.getDeviceWrapper().getDevice().setDeviceSn("SNOfDeviceB");
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
		deleteDevice(mockApp2);
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random);
		businessPool.setCapacity(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity);
	}
	
	@Test
	public void test()
	{
		OnlineDevicePool onlinePool = OnlineDevicePool.instance;
		AbstractBusinessDevicePool businessPool = onlinePool.getBusinessPool(Consts.BusinessType.Random);
		
		mockApp1.syncDevice();
		mockApp2.syncDevice();
		
		// Step_05 ���ã�RandomBusinessDevicePool::setCapacity(1)
		businessPool.setCapacity(businessPool.getElementCount() + 1);
		
		// Step_06 Mock����A�����������
		mockApp1.enterPool(BusinessType.Random);
		
		// Step_07 Mock����B�����������
		mockApp2.enterPool(BusinessType.Random);
		fail("���Server����Ӧ����ҵ���豸�شﵽ����");
	}
}
