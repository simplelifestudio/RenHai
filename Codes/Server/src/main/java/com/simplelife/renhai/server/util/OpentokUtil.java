/**
 * OpentokUtil.java
 * 
 * History:
 *     2013-11-12: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.opentok.api.API_Config;
import com.opentok.api.OpenTokSDK;
import com.opentok.api.constants.RoleConstants;
import com.opentok.api.constants.SessionProperties;
import com.opentok.exception.OpenTokException;
import com.opentok.util.TokBoxXML;
import com.simplelife.renhai.server.log.FileLogger;

/**
 * 
 */
public class OpentokUtil
{
	public static String requestNewSession(OpenTokSDK sdk)
	{
		SessionProperties sp = new SessionProperties();
        sp.p2p_preference = "enabled";
        try
		{
			String sessionId = sdk.create_session(null, sp).getSessionId();
			return sessionId;
		}
		catch (OpenTokException e)
		{
			FileLogger.printStackTrace(e);
		}
        return null;
	}
	
	public static String updateToken(String sessionId, OpenTokSDK sdk)
	{
		try
		{
			long dueDate = System.currentTimeMillis() / 1000 + GlobalSetting.BusinessSetting.OpenTokTokenExpiration;
			String t = sdk.generate_token(sessionId, RoleConstants.PUBLISHER, dueDate);
			//TokBoxXML xml = get_token_info(t);
	        //String token = xml.getElementValue("connection_data", "token").trim();
	        return t;
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
		return null;
	}
	
	private static TokBoxXML get_token_info(String token) throws OpenTokException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-TB-TOKEN-AUTH",token);
		TokBoxXML xml;
		String response = request(API_Config.API_URL + "/token/validate", new HashMap<String, String>(), headers);
	    xml = new TokBoxXML(response);
	    return xml;
    }
	
	private static String request(String reqString, Map<String, String> paramList, Map<String, String> headers)
	{
		StringBuilder returnString = new StringBuilder();
		
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		OutputStreamWriter wr = null;
		BufferedWriter bufWriter = null;
		
		try
		{
			StringBuilder dataString = new StringBuilder();
			
			for (Iterator<String> i = paramList.keySet().iterator(); i.hasNext();)
			{
				String key = i.next();
				String value = paramList.get(key);
				
				if (null != value)
				{
					value = URLEncoder.encode(paramList.get(key), "UTF-8").replaceAll("\\+", "%20");
					dataString.append(URLEncoder.encode(key, "UTF-8")).append("=").append(value).append("&");
				}
			}
			url = new URL(reqString);
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setRequestProperty("Content-Length", Integer.toString(dataString.toString().length()));
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("Accept", "text/html, application/xhtml+xml,application/xml");
			
			for (Iterator<String> i = headers.keySet().iterator(); i.hasNext();)
			{
				String key = i.next();
				String value = headers.get(key);
				conn.setRequestProperty(key, value);
			}
			
			wr = new OutputStreamWriter(conn.getOutputStream(), "UTF8");
			bufWriter = new BufferedWriter(wr);
			bufWriter.write(dataString.toString());
			bufWriter.flush();
			
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
			
			String str;
			while (null != ((str = br.readLine())))
			{
				returnString.append(str);
				returnString.append("\n");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != conn)
				{
					conn.disconnect();
				}
				
				if (null != wr)
				{
					wr.close();
				}
				
				if (null != bufWriter)
				{
					bufWriter.close();
				}
				
				if (null != br)
				{
					br.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return returnString.toString();
	}
}
