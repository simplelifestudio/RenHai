/**
 * StatisticsServlet.java
 * 
 * History:
 *     2014-1-17: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.StatisticsMapper;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.JSONKey;

@WebServlet("/stat")
public class StatisticsServlet extends HttpServlet
{
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        super.service(request, response);
    }

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        SqlSession session = DAOWrapper.instance.getSession();
		StatisticsMapper mapper = session.getMapper(StatisticsMapper.class);
        int registerDeviceCount = mapper.selectRegisterDeviceCount();
        
        out.write("【注册用户：" + registerDeviceCount + "    ");
        out.write("当前在线：" + OnlineDevicePool.instance.getDeviceCount() + "】");
    }
}
