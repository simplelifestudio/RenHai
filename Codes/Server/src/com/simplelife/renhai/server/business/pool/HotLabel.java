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
public class HotLabel
{
	private int labelCount;
	private String labelName;

	public int getLabelCount()
	{
		return labelCount;
	}
	
	public void setLabelCount(int labelOrder)
	{
		this.labelCount = labelOrder;
	}
	
	public String getLabelName()
	{
		return labelName;
	}
	
	public void setLabelName(String labelName)
	{
		this.labelName = labelName;
	}
	
	public int compareTo(HotLabel label)
	{
		return (labelCount - label.getLabelCount());
	}
}
