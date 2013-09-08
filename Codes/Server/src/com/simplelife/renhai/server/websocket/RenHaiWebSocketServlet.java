package com.simplelife.renhai.server.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;

/**
 * Servlet implementation class SeedsWebSocketServlet
 */
@WebServlet("/websocket")
public class RenHaiWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
       
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
    	Logger logger = WebSocketModule.instance.getLogger();
    	logger.debug("New connection recieved.");
    	
        WebSocketConnection msgInbound = new WebSocketConnection();
        OnlineDevicePool pool = OnlineDevicePool.instance;
        pool.newDevice(msgInbound);
        return msgInbound;
    }
    
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
}
