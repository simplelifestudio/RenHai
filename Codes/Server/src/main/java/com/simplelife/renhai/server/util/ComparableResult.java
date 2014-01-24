/**
 * HotLabel.java
 * 
 * History:
 *     2013-10-4: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

/**
 * 
 */
public class ComparableResult implements Comparable<ComparableResult>
{
	private int count;
	private String field;

	public int getCount()
	{
		return count;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public String getField()
	{
		return field;
	}
	
	public void setField(String field)
	{
		this.field = field;
	}
	
	@Override
	public String toString()
	{
		return field.toString();
	}
	
	@Override
	public int compareTo(ComparableResult targetObject)
	{
		return (targetObject.getCount() - count);
	}
}
