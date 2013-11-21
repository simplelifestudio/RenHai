/**
 * HotLabel.java
 * 
 * History:
 *     2013-10-4: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

/**
 * 
 */
public class HotLabel implements Comparable<HotLabel>
{
	private int profileCount;
	private String labelName;

	public int getProfileCount()
	{
		return profileCount;
	}
	
	public void setProfileCount(int labelOrder)
	{
		this.profileCount = labelOrder;
	}
	
	public String getLabelName()
	{
		return labelName;
	}
	
	public void setLabelName(String labelName)
	{
		this.labelName = labelName;
	}
	
	@Override
	public int compareTo(HotLabel label)
	{
		return (label.getProfileCount() - profileCount);
	}
}
