/**
 * SeedServiceServlet.java 
 * 
 * History:
 *     2013-06-09: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.proxyserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * Servlet implementation class SeedServiceServlet
 */
@WebServlet("/request")
public class ServiceServlet extends HttpServlet {
	private final String jsonMIME = "application/json";
	private Logger logger = LoggerFactory.getLogger(ServiceServlet.class);

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceServlet() {
		super();
		GlobalSetting.instance.checkSettingFile();
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		super.service(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void reportError(String errMsg, PrintWriter out)
	{
		logger.error(errMsg);
		ServerErrorResponse errRes = new ServerErrorResponse(null, out);
		errRes.addToBody(JSONKey.ErrorCode, 1001);
		errRes.addToBody(JSONKey.ErrorDescription, errMsg);
		errRes.run();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String command = null;
		PrintWriter out = response.getWriter();
		try
		{
			logger.debug("Beginning of doPost, proceed request from client");
			response.setCharacterEncoding("UTF-8");
			response.setContentType(jsonMIME);
	
			/*
			String clientContentType = request.getContentType(); 
			if (clientContentType == null || !clientContentType.contains(jsonMIME)) 
			{
				logger.warn("Invalid MIME found from client: " + clientContentType);
			}
			*/
	
			command = readCommand(request);
	
			logger.debug("JSON command received: " + command);
			
			JSONObject jsonObj = null;
			jsonObj = JSON.parseObject(command);
			
			if (jsonObj == null)
			{
				String temp = "Invalid command received: \n" + command; 
				reportError(temp, out);
				return;
			}
	
			if (!jsonObj.containsKey(JSONKey.JsonEnvelope))
			{
				String temp = "Invalid command, JsonEnvelope can't be found: \n" + command; 
				reportError(temp, out);
				return;
			}
			
			JSONObject envObj = null;
			
			if (GlobalSetting.instance.getEncrypt() == 0)
			{
				envObj = jsonObj.getJSONObject(JSONKey.JsonEnvelope);
			}
			else
	    	{
				logger.debug("Try to decrypt message");
				String message = jsonObj.getString(JSONKey.JsonEnvelope);
	    		try
				{
					message = SecurityUtils.decryptByDESAndDecodeByBase64(message, GlobalSetting.EncryptKey);
					envObj = JSON.parseObject(message);
				}
				catch (Exception e)
				{
					String temp = "Failed to parse JSON String after decryption:\n" + message;
					reportError(temp, out);
					FileLogger.printStackTrace(e);
				}
	    	}
			
			
			AbstractJSONMessage appRequest = JSONFactory.createAppJSONMessage(envObj, out);
			if (appRequest == null)
			{
				return;
			}
			appRequest.run();
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
			String temp = "Invalid command received: \n" + command; 
			reportError(temp, out);
			return;
		}
	}


	/**
	 * Read JSON command from client
	 * 
	 * @param request
	 *            : http request from client
	 * @return: complete string of JSON command from client
	 */
	private String readCommand(HttpServletRequest request) {
		StringBuffer strBuf = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				strBuf.append(line);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			FileLogger.printStackTrace(e);
		}
		return strBuf.toString();
		
	}
}
