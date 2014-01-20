/**
 * TestKeywordFilter.java
 * 
 * History:
 *     2014-1-19: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

import com.simplelife.renhai.server.business.KeywordFilter;
import com.simplelife.renhai.server.business.pool.HotLabel;
import com.simplelife.renhai.server.test.MockApp;
import com.simplelife.renhai.server.test.MockAppConsts;
import com.simplelife.renhai.server.util.Consts.BusinessType;

/**
 * 
 */
public class TestKeywordFilter
{
	@Test
	public void testKeyFilter()
	{
		String filterString = "一夜情,约炮,政治";
		KeywordFilter.init(filterString);
		ArrayList<String> tmpKey = new ArrayList<>();
		tmpKey.add("一夜情");
		tmpKey.add("读书");
		tmpKey.add("约炮");
		tmpKey.add("习近平");
		tmpKey.add("政治");
		tmpKey.add("爬山");
		
		System.out.println("filterString:" + filterString);
		System.out.println("Before filter: " + tmpKey.toString());
		KeywordFilter.removeFilteredKeyword(tmpKey);
		System.out.println("After filter: " + tmpKey.toString());
	}
	
	@Test
	public void testKeyFilter2()
	{
		String filterString = "一夜情,约炮,政治,毛泽东";
		KeywordFilter.init(filterString);
		
		LinkedList<HotLabel> list = new LinkedList<>();
		
		HotLabel tmpLabel = new HotLabel();
		tmpLabel.setLabelName("一夜情");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("读书");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("约炮");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("习近平");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("政治");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		tmpLabel = new HotLabel();
		tmpLabel.setLabelName("爬山");
		tmpLabel.setProfileCount(20);
		list.add(tmpLabel);
		
		
		System.out.println("filterString:" + filterString);
		System.out.print("Before filter: ");
		for (int i = list.size() -1; i >=0; i--)
		{
			System.out.print(list.get(i).getLabelName() + ",");
		}
		System.out.print("\r\n");
		
		KeywordFilter.filterHotLabel(list);
		System.out.print("After filter: ");
		for (int i = list.size() -1; i >=0; i--)
		{
			System.out.print(list.get(i).getLabelName() + ",");
		}
		System.out.print("\r\n");
	}
	
	@Test
	public void testHotLabel()
	{
		MockApp.mockAppExecutePool.startService();
		MockApp app1 = new MockApp(
				"MockApp1", 
				MockAppConsts.MockAppBehaviorMode.Manual.name(), 
				"ws://106.186.16.174/renhai/websocket", 
				"一夜情,读书,爬山,约炮,模拟");
		app1.syncDevice();
		app1.chooseBusiness(BusinessType.Interest);
		
		MockApp app2 = new MockApp(
				"MockApp2", 
				MockAppConsts.MockAppBehaviorMode.Manual.name(), 
				"ws://106.186.16.174/renhai/websocket", 
				"体育,读书,爬山,约炮,模拟");
		app2.syncDevice();
		app2.chooseBusiness(BusinessType.Interest);
		
		app1.sendServerDataSyncRequest();
	}
}
