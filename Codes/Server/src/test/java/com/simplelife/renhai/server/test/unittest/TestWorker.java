/**
 * TestWorker.java
 * 
 * History:
 *     2013-11-24: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.TimeoutLink;
import com.simplelife.renhai.server.test.MockWebSocketConnection;
import com.simplelife.renhai.server.util.GlobalSetting;

/**
 * 
 */
public class TestWorker
{
	/*
	@Test
	public void testPingActionQueue() throws InterruptedException
	{
		PingActionQueue.instance.startService();
		
		for (int i = 0; i < 10; i++)
		{
			PingActionQueue.instance.newAction(PingActionType.CheckInactivity, null);
			Thread.sleep(500);
		}
	}
	
	@Test
	public void testPingLink()
	{
		TimeoutLink link = OnlineDevicePool.pingLink;
		assertTrue(link.size() == 0);
		assertTrue(link.getHead() == null);
		assertTrue(link.getTail() == null);
		
		MockWebSocketConnection conn = new MockWebSocketConnection();
		DeviceWrapper device1 = new DeviceWrapper(conn);
		DeviceWrapper device2 = new DeviceWrapper(conn);
		
		assertTrue(link.size() == 2);
		assertTrue(link.getHead() == device1.getPingNode());
		assertTrue(link.getTail() == device2.getPingNode());
		
		link.removeNode(device2.getPingNode());
		assertTrue(link.size() == 1);
		assertTrue(link.getHead() == device1.getPingNode());
		assertTrue(link.getTail() == device1.getPingNode());
		
		link.removeNode(device1.getPingNode());
		assertTrue(link.size() == 0);
		assertTrue(link.getHead() == null);
		assertTrue(link.getTail() == null);
		
		link.append(device1.getPingNode());
		link.append(device2.getPingNode());
		assertTrue(link.size() == 2);
		assertTrue(link.getHead() == device1.getPingNode());
		assertTrue(link.getTail() == device2.getPingNode());
	}
	
	@Test
	public void testPing() throws InterruptedException
	{
		PingActionQueue.instance.startService();
		MockWebSocketConnection conn = new MockWebSocketConnection();
		List<DeviceWrapper> devices = new ArrayList<>(); 
		for(int i = 0; i < 10; i++)
		{
			DeviceWrapper device = new DeviceWrapper(conn);
			devices.add(device);
			device.bindOnlineDevicePool(OnlineDevicePool.instance);
		}
		GlobalSetting.TimeOut.PingInterval = 2000;
		Random random = new Random();
		DeviceWrapper device;
		for (int i = 0; i < 20; i++)
		{
			device = devices.get(random.nextInt(10));
			device.onPing(conn, null);
			
			if (i % 5 == 0)
			{
				PingActionQueue.instance.newAction(PingActionType.CheckInactivity, null);
			}
			Thread.sleep(500);
		}
		System.out.println("finished");
	}
	*/
	public void testPing() throws InterruptedException
	{
	}
}
