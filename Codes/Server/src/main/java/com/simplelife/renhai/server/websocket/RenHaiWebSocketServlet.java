package com.simplelife.renhai.server.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.DateUtil;

/**
 * Servlet implementation class SeedsWebSocketServlet
 */
@WebServlet("/websocket")
public class RenHaiWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = WebSocketModule.instance.getLogger();
       
    /**
     * @see WebSocketServlet#WebSocketServlet()
     */
    public RenHaiWebSocketServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    super.doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    super.doGet(request, response);
	}

    /* (non-Javadoc)
     * @see org.apache.catalina.websocket.WebSocketServlet#createWebSocketInbound(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1)
    {
    	Thread.currentThread().setName("WebSocket" + DateUtil.getCurrentMiliseconds());
    	
    	String id = arg1.getRemoteAddr() + "_" + arg1.getRemotePort();
    	logger.debug("New connection received from {}", id);
    	
    	WebSocketConnection conn = new WebSocketConnection(id);
        OnlineDevicePool pool = OnlineDevicePool.instance;
        if (pool.newDevice(conn) == null)
        {
        	logger.error("pool.newDevice(conn) == null");
        	return null;
        }
        return conn;
    }
    
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
}
