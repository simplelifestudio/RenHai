package com.simplelife.renhai.server.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.simplelife.renhai.server.util.ComparableResult;

public class KeywordFilter
{
	private static HashSet<String> keywordSet;
	
	public static void init(JSONArray array)
	{
		if (keywordSet == null)
		{
			keywordSet = new HashSet<>();
		}
		
		keywordSet.clear();
		
		int size = array.size(); 
		if (size == 0)
		{
			return;
		}
		
		for (int i = size-1; i >= 0; i--)
		{
			keywordSet.add(array.getString(i));
		}
		BusinessModule.instance.getLogger().debug("Updated {} filter keys", size);
	}
	
	public static boolean isFilteredKeyword(String keyword)
	{
		if (keywordSet.contains(keyword))
		{
			return true;
		}
		return false;
	}
	
	public static void filterHotLabel(Collection<ComparableResult> keywordList)
	{
		Logger logger = BusinessModule.instance.getLogger();
		if (keywordList == null)
		{
			return;
		}
		
		if (keywordList.isEmpty())
		{
			return;
		}
		
		ArrayList<ComparableResult> tmpCol = new ArrayList<>();
		tmpCol.addAll(keywordList);
		ComparableResult tmpLabel;
		for (int i = tmpCol.size()-1; i>=0; i--)
		{
			tmpLabel = tmpCol.get(i);
			if (isFilteredKeyword(tmpLabel.toString()))
			{
				tmpCol.remove(i);
			}
		}
		
		keywordList.clear();
		keywordList.addAll(tmpCol);
	}
	
	public static void removeFilteredKeyword(Collection<String> keywordList)
	{
		Collection<String> tmpCol = new ArrayList<>();
		tmpCol.addAll(keywordList);
		tmpCol.retainAll(keywordSet);
		keywordList.removeAll(tmpCol);
	}
}
