/**
 * Test21UpdateInterestcard.java
 * 
 * History:
 *     2013-9-2: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class Test21UpdateInterestcard extends AbstractTestCase
{
	private MockApp mockApp1;
	
	/**
	 * Initialize variables needed in case
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.print("==================Start of " + this.getClass().getName() + "=================\n");
		mockApp1 = createNewMockApp(demoDeviceSn);
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
	
	private void checkLabel(Interestcard card)
	{
		// TODO: 
		/*
		// InterestlabelmapDAO dao = new InterestlabelmapDAO();
		LinkedList<AbstractLabel> labelList = card.getInterestlabelmaps();
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
		*/
	}
	
	@Test
	public void test() throws InterruptedException
	{
		IDeviceWrapper deviceWrapper1 = mockApp1.getDeviceWrapper();

		// Step_01 Mock����A�豸ͬ��
		mockApp1.syncDevice();
		
		// Step_02 Mock����A������Ȥ��Ƭ��5����Ȥ��ǩ
		HashMap<String, Object> labels = new HashMap<String, Object>();
		addInterestLabels(labels);
		//mockApp1.updateInterestcard(labels);
		// TODO: 
		
		// Step_03 ���ã�DeviceWrapper::getInterestcard
		Interestcard card = deviceWrapper1.getDevice().getProfile().getInterestcard();
		checkLabel(card);
		
		// Step_04 ���ã�DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_05 ���ݿ��飺A����Ȥ��ǩ
		//fail("��Ҫ������ݿ��е���Ȥ��ǩ");
		
		// Step_06 Mock����A������Ȥ��Ƭ������1����Ȥ��ǩ
		LinkedHashMap<String, Object> labelAttr = new LinkedHashMap<String, Object>();
		labelAttr.put("order", "5");
		labelAttr.put("matchCount", "40");
		labels.put("av", labelAttr);
		//mockApp1.updateInterestcard(labels);
		
		// Step_07 ���ã�DeviceWrapper::getInterestcard
		checkLabel(card);
		
		//InterestLabel label = (InterestLabel)card.getLabelList().get(5); 
		//assertEquals(label.getOrder(), 5);
		//assertEquals(label.getName(), "av");
		
		// Step_08 ���ã�DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_09 ���ݿ��飺A����Ȥ��ǩ
		fail("��Ҫ������ݿ��е���Ȥ��ǩ");
		
		// Step_10 Mock����A������Ȥ��Ƭ��ɾ��2����Ȥ��ǩ
		labels.remove("music");
		
		// Step_11 ���ã�DeviceWrapper::getInterestcard
		//label = (InterestLabel)card.getLabelList().get(1); 
		//assertEquals(label.getOrder(), 1);
		//assertEquals(label.getName(), "game");
		
		// Step_12 ���ã�DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_13 ���ݿ��飺A����Ȥ��ǩ
		fail("��Ҫ������ݿ��е���Ȥ��ǩ");
		
		// Step_14 Mock����A������Ȥ��Ƭ���ı��ǩ˳��
		//label = (InterestLabel)card.getLabelList().get(1);
		//label.setOrder(3);
		//label = (InterestLabel)card.getLabelList().get(3);
		//label.setOrder(1);
				
		// Step_15 ���ã�DeviceWrapper::getInterestcard
		
		// Step_16 ���ã�DaoWrapper::flush
		DAOWrapper.flushToDB();
		
		// Step_17 ���ݿ��飺A����Ȥ��ǩ
		fail("��Ҫ������ݿ��е���Ȥ��ǩ");
		
		// Step_18 Mock����A��ѯ��Ȥ��Ƭ
		mockApp1.syncDevice();
		fail("��Ҫ���ظ��ı�ǩ�Ƿ���ȷ");
	}
}
