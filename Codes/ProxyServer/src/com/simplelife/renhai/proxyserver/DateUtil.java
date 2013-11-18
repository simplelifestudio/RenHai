/**
 * Datejava
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.proxyserver;

import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

/** */
public class DateUtil
{
	private final static int INVALID_VALUE = 0xffff;
	private static SimpleDateFormat defaultDateFormatter;
	
	public DateUtil()
	{
		
	}
	
	
	public static int getCurrentMiliseconds()
	{
		return (int) (System.currentTimeMillis() % 1000);
	}
	
	public static Date getDateByTimeZoneDateString(String dateString)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date date = null;
		try
		{
			date = sdf.parse(dateString);
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
		return date;
	}
	
	public static String getDateStringByLongValue(long dateInMilliSeconds)
	{
		Date date = new Date(dateInMilliSeconds);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sdf.setTimeZone(getDefaultTimeZone());
		return sdf.format(date);
	}
	
	/**
	 * Get simple date formatter with default time zone of GMT+8
	 * @return simple date formatter
	 */
	public static SimpleDateFormat getDefaultDateFormatter()
	{
		if (defaultDateFormatter == null)
		{
			defaultDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			defaultDateFormatter.setTimeZone(getDefaultTimeZone());
		}
		return defaultDateFormatter; 
	}
	
	/**
	 * Get default time zone of China
	 * @return default time zone
	 */
	public static TimeZone getDefaultTimeZone()
	{
		return TimeZone.getTimeZone("GMT+8");
	}
	
	/**
	 * Get calendar instance with default time zone of GMT+8 
	 * @return Calendar instance
	 */
	public static Calendar getCalendar()
	{
		TimeZone.setDefault(getDefaultTimeZone());
		return Calendar.getInstance();
	}
	
	/**
	 * Get day difference between two dates
	 * @param d1:date1
	 * @param d2:date2
	 * @return day difference between d1 and d2, and difference will be negative if d1 > d2 
	 */
	public static long getDaysBetween(String d1, String d2)
	{
		Calendar cal_start;
		Calendar cal_end;
		
		try
		{
			Date date_start = getDefaultDateFormatter().parse(d1);
			Date date_end = getDefaultDateFormatter().parse(d2);
			cal_start = Calendar.getInstance();
			cal_end = Calendar.getInstance();
			cal_start.setTime(date_start);
			cal_end.setTime(date_end);
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			return INVALID_VALUE;
		}
		return getDaysBetween(cal_start, cal_end);
	}
	
	/**
	 * Get day difference between two dates
	 * @param d1:date1
	 * @param d2:date2
	 * @return day difference between d1 and d2, and difference will be negative if d1 > d2
	 */
	public static long getDaysBetween(Calendar d1, Calendar d2)
	{
		try
		{
			long sec1 = d1.getTimeInMillis() - d2.getTimeInMillis();
			long days = sec1/1000/3600/24;
			return days;
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			return INVALID_VALUE;
		}
	}
	
	/**
	 * Get date as format of "yyyy-MM-dd"
	 * @param date: string of date
	 * @return String of date with format of "yyyy-MM-dd" 
	 */
	public static String getFormatedDate(String date)
	{
		return getFormatedDate(date, "yyyy-MM-dd");
	}
	
	/**
	 * Get date as given date format
	 * @param date: string of date
	 * @param format: given date format, such as "yyyy-MM-dd"
	 * @return String of date with given format
	 */
	public static String getFormatedDate(String date, String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try
		{
			Date dateTmp = sdf.parse(date);
			return sdf.format(dateTmp);
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			return null;
		}
	}
	
	/**
	 * Get day difference between given date and today
	 * @param date: given date
	 * @return day difference
	 */
	public static long getDaysFromToday(Date date)
	{
	    Calendar cal_start;
        try
        {
            cal_start = getCalendar();
            cal_start.setTime(date);
            
            return getDaysBetween(cal_start, getCalendar());
        }
        catch(Exception e)
        {
            FileLogger.printStackTrace(e);
            return INVALID_VALUE;
        }
	}
	
	public static String increaseDate(String startDate, int days)
	{
		Calendar cal_start;
		try
		{
			Date date_start = getDefaultDateFormatter().parse(startDate);
			cal_start = getCalendar();
			cal_start.setTime(date_start);
			cal_start.add(Calendar.DAY_OF_YEAR, days);
			return getDefaultDateFormatter().format(cal_start.getTime());
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			return null;
		}
	}
	
	/**
	 * Get day difference between given date and today
	 * @param date: given date
	 * @return day difference
	 */
	public static long getDaysFromToday(String date)
	{
		Calendar cal_start;
		try
		{
			Date date_start = getDefaultDateFormatter().parse(date);
			cal_start = getCalendar();
			cal_start.setTime(date_start);
			
			return getDaysBetween(cal_start, getCalendar());
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			return INVALID_VALUE;
		}
	}
	
	/**
	 * Get date object of current
	 * @return
	 */
	public static Date getNowDate()
	{
		return getCalendar().getTime();
	}
	
	
	/**
	 * Get date string as format of "yyyy-MM-dd HH:mm:ss.SSS"
	 * @return
	 */
	public static String getNow()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sdf.setTimeZone(getDefaultTimeZone());
		return sdf.format(getCalendar().getTime());
	}
	
	/**
	 * Get date string as format of "yyyy-MM-dd"
	 * @return
	 */
	public static String getToday()
	{
		return getDefaultDateFormatter().format(getCalendar().getTime());
	}
	
	/**
	 * Get date string of backward from today by given days, in format of "yyyy-MM-dd"
	 * @param dayBack: backward days from today
	 * @return Date string in format of "yyyy-MM-dd"
	 */
	public static Date getDateByDayBack(int dayBack)
	{
		Calendar cal = getCalendar();
		cal.add(Calendar.DAY_OF_YEAR, -dayBack);
		return cal.getTime();
	}
	
	/**
	 * Get date string of backward from today by given days, in format of "yyyy-MM-dd"
	 * @param dayBack: backward days from today
	 * @return Date string in format of "yyyy-MM-dd"
	 */
	public static String getDateStringByDayBack(int dayBack)
	{
		String now;
		Calendar cal = getCalendar();
		cal.add(Calendar.DAY_OF_YEAR, -dayBack);
		now = getDefaultDateFormatter().format(cal.getTime());
		return now;
	}
	
	/**
	 * Get date of next time for triggering task
	 * @param taskTriggerHour: hour of triggering task
	 * @return Date of next trigger
	 */
	public static Date getTaskTrigger(int taskTriggerHour, boolean tomorrow)
	{
		return getTaskTrigger(taskTriggerHour, 0, tomorrow);
	}
	
	/**
	 * Check if currently is time to trigger task
	 * @param taskTriggerHour: hour of triggering task
	 * @return Return true if time is up, else return false
	 */
	public static boolean isTimeToTrigerTask(int taskTriggerHour)
	{
		return isTimeToTrigerTask(taskTriggerHour, 0);
	}
	
	/**
	 * Check if currently is time to trigger task
	 * @param taskTriggerHour: hour of triggering task
	 * @param taskTriggerMinute: minutes of triggering task
	 * @return Return true if time is up, else return false
	 */
	public static boolean isTimeToTrigerTask(int taskTriggerHour, int taskTriggerMinute)
	{
		Calendar cal = getCalendar();
		int curHour = cal.get(Calendar.HOUR_OF_DAY);
		int curMinute = cal.get(Calendar.MINUTE);
		
		if ((curHour > taskTriggerHour) || 
				((curHour == taskTriggerHour) && (curMinute > taskTriggerMinute)))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get date of next time for triggering task
	 * @param taskTriggerHour: hour of triggering task
	 * @param taskTriggerMinute: minutes of triggering task
	 * @return Date of next trigger
	 */
	public static Date getTaskTrigger(int taskTriggerHour, int taskTriggerMinute, boolean tomorrow)
	{
		// change to start from given time
		Calendar cal = getCalendar();
		cal.set(Calendar.HOUR_OF_DAY, taskTriggerHour);
		cal.set(Calendar.MINUTE, taskTriggerMinute);

		
		if (tomorrow)
	    {
	        cal.add(Calendar.DAY_OF_YEAR, 1);
	    }
		else
		{
			/*
			int curHour = cal.get(Calendar.HOUR_OF_DAY);
			int curMinute = cal.get(Calendar.MINUTE);
			if ((curHour > taskTriggerHour) || 
				((curHour == taskTriggerHour) && (curMinute > taskTriggerMinute)))
			{
				cal.add(Calendar.MINUTE, 2);
			}
			*/
			
		}
		return cal.getTime();
	}
	
	
	/**
	 * return 2013-06-01 by "[6-01]最新BT合集"
	 * @param title: title in web page
	 * @return Formatted date string
	 */
	public static String getDateByTitle(String title)
	{
		String outDate = null;
		int start = title.indexOf('[');
		boolean flag = true;
		if (start < 0)
		{
			flag = false;
		}
		
		int end = title.indexOf(']'); 
		if (end < 0)
		{
			flag = false;
		}
		
		String dateString = title.substring(start + 1, end);
		int mid = dateString.indexOf('-'); 
		if (mid < 0)
		{
			flag = false;
		}
		
		if (!flag)
		{
			FileLogger.warning("Invalid date format found: " + title);
			return null;
		}
		outDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		outDate += "-";
		outDate += dateString;
		
		return DateUtil.getFormatedDate(outDate);
	}
}
