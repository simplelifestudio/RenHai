package com.simplelife.renhai.server.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.pool.HotLabel;

public class KeywordFilter
{
	private static HashSet<String> keywordSet;
	
	public static void init(String keywordString)
	{
		if (keywordSet == null)
		{
			keywordSet = new HashSet<>();
		}
		
		keywordSet.clear();
		String[] tmpArray = keywordString.split(",");
		
		if (tmpArray.length == 0)
		{
			return;
		}
		
		for (int i = tmpArray.length-1; i >= 0; i--)
		{
			keywordSet.add(tmpArray[i]);
		}
		BusinessModule.instance.getLogger().debug("Update filter keys as: {}", keywordString);
	}
	
	public static boolean isFilteredKeyword(String keyword)
	{
		if (keywordSet.contains(keyword))
		{
			return true;
		}
		return false;
	}
	
	public static void filterHotLabel(Collection<HotLabel> keywordList)
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
		
		ArrayList<HotLabel> tmpCol = new ArrayList<>();
		tmpCol.addAll(keywordList);
		HotLabel tmpLabel;
		for (int i = tmpCol.size()-1; i>=0; i--)
		{
			tmpLabel = tmpCol.get(i);
			if (isFilteredKeyword(tmpLabel.getLabelName()))
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
