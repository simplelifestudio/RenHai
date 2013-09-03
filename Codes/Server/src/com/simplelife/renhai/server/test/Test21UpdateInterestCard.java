/**
 * Test21UpdateInterestCard.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.business.device.InterestCard;
import com.simplelife.renhai.server.business.device.InterestLabel;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test21UpdateInterestCard extends AbstractTestCase
{
	private LocalMockApp mockApp1;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		mockApp1 = createMockApp();
	}
	
	/**
	 * Clear variables created in case
	 */
	@After
	public void tearDown() throws Exception
	{
		deleteDevice(mockApp1);
	}
	
	private void addInterestLabels(HashMap<String, Object> labels)
	{
		HashMap<String, Object> labelAttr = new HashMap<String, Object>();
		labelAttr.put("order", "0");
		labelAttr.put("matchCount", "7");
		labels.put("soccer", labelAttr);
		
		labelAttr = new LinkedHashMap<String, Object>();
		labelAttr.put("order", "1");
		labelAttr.put("matchCount", "3");
		labels.put("music", labelAttr);
		
		labelAttr = new LinkedHashMap<String, Object>();
		labelAttr.put("order", "2");
		labelAttr.put("matchCount", "5");
		labels.put("game", labelAttr);
		
		labelAttr = new LinkedHashMap<String, Object>();
		labelAttr.put("order", "3");
		labelAttr.put("matchCount", "1");
		labels.put("hiking", labelAttr);
		
		labelAttr = new LinkedHashMap<String, Object>();
		labelAttr.put("order", "4");
		labelAttr.put("matchCount", "0");
		labels.put("basketball", labelAttr);
	}
	
	private void checkLabel(InterestCard card)
	{
		LinkedList<AbstractLabel> labelList = card.getLabelList();
		assertTrue(labelList.get(0) instanceof InterestLabel);
		
		InterestLabel label = (InterestLabel)labelList.get(0); 
		assertEquals(label.getOrder(), 0);
		assertEquals(label.getName(), "soccer");
		
		label = (InterestLabel)labelList.get(1); 
		assertEquals(label.getOrder(), 1);
		assertEquals(label.getName(), "music");
		
		label = (InterestLabel)labelList.get(2); 
		assertEquals(label.getOrder(), 2);
		assertEquals(label.getName(), "game");
		
		label = (InterestLabel)labelList.get(3); 
		assertEquals(label.getOrder(), 3);
		assertEquals(label.getName(), "hiking");
		
		label = (InterestLabel)labelList.get(4); 
		assertEquals(label.getOrder(), 4);
		assertEquals(label.getName(), "basketball");
	}
	
	@Test
	public void test()
	{
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();

		// Step_01 Mock请求：A设备同步
		syncDevice(mockApp1);
		
		// Step_02 Mock请求：A更新兴趣卡片，5个兴趣标签
		HashMap<String, Object> labels = new HashMap<String, Object>();
		addInterestLabels(labels);
		mockApp1.updateInterestCard(labels);
		
		// Step_03 调用：DeviceWrapper::getInterestCard
		InterestCard card = deviceWrapper1.getDevice().getInterestCard();
		checkLabel(card);
		
		// Step_04 调用：DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_05 数据库检查：A的兴趣标签
		fail("需要检查数据库中的兴趣标签");
		
		// Step_06 Mock请求：A更新兴趣卡片，增加1个兴趣标签
		LinkedHashMap<String, Object> labelAttr = new LinkedHashMap<String, Object>();
		labelAttr.put("order", "5");
		labelAttr.put("matchCount", "40");
		labels.put("av", labelAttr);
		mockApp1.updateInterestCard(labels);
		
		// Step_07 调用：DeviceWrapper::getInterestCard
		checkLabel(card);
		InterestLabel label = (InterestLabel)card.getLabelList().get(5); 
		assertEquals(label.getOrder(), 5);
		assertEquals(label.getName(), "av");
		
		// Step_08 调用：DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_09 数据库检查：A的兴趣标签
		fail("需要检查数据库中的兴趣标签");
		
		// Step_10 Mock请求：A更新兴趣卡片，删除2个兴趣标签
		labels.remove("music");
		
		// Step_11 调用：DeviceWrapper::getInterestCard
		label = (InterestLabel)card.getLabelList().get(1); 
		assertEquals(label.getOrder(), 1);
		assertEquals(label.getName(), "game");
		
		// Step_12 调用：DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_13 数据库检查：A的兴趣标签
		fail("需要检查数据库中的兴趣标签");
		
		// Step_14 Mock请求：A更新兴趣卡片，改变标签顺序
		label = (InterestLabel)card.getLabelList().get(1);
		label.setOrder(3);
		
		label = (InterestLabel)card.getLabelList().get(3);
		label.setOrder(1);
				
		// Step_15 调用：DeviceWrapper::getInterestCard
		
		// Step_16 调用：DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_17 数据库检查：A的兴趣标签
		fail("需要检查数据库中的兴趣标签");
		
		// Step_18 Mock请求：A查询兴趣卡片
		syncDevice(mockApp1);
		fail("需要检查回复的标签是否正确");
	}
}
