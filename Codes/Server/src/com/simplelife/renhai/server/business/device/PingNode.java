package com.simplelife.renhai.server.business.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;

public class PingNode
{
	private IDeviceWrapper deviceWrapper;
	private PingNode prevNode;
	private PingNode nextNode;
    private long lastPingTime;
    private int pingActiveTime;

    public PingNode(IDeviceWrapper deviceWrapper)
    {
    	this.deviceWrapper = deviceWrapper;
    	pingActiveTime = GlobalSetting.TimeOut.PingInterval
    			+ GlobalSetting.TimeOut.PingInterval 
    			+ GlobalSetting.TimeOut.CheckPingInterval;
    }
	
    /**
	 * @return the curNode
	 */
	public IDeviceWrapper getDeviceWrapper()
	{
		return deviceWrapper;
	}
	
	/**
	 * @return the prevNode
	 */
	public PingNode getPrevNode()
	{
		return prevNode;
	}
	/**
	 * @param prevNode the prevNode to set
	 */
	public void setPrevNode(PingNode prevNode)
	{
		this.prevNode = prevNode;
	}
	/**
	 * @return the nextNode
	 */
	public PingNode getNextNode()
	{
		return nextNode;
	}
	/**
	 * @param nextNode the nextNode to set
	 */
	public void setNextNode(PingNode nextNode)
	{
		this.nextNode = nextNode;
	}
	
	/**
     * Save current time as lastPingTime 
     */
    public void updatePingTime()
    {
    	long now = System.currentTimeMillis();
    	lastPingTime = now;
    	Logger logger = LoggerFactory.getLogger("Ping");
    	if (logger.isDebugEnabled())
    	{
	    	String temp = "Update last ping time: " + now;
	    	temp += ", connection ID: " + deviceWrapper.getConnection().getConnectionId();
	    	Device device = deviceWrapper.getDevice();
	    	if (device != null)
	    	{
	    		temp += " for Node <" + device.getDeviceSn() + ">";
	    	}
	    	logger.debug(temp);
    	}
    }
    
    public long getLastPingTime()
    {
        return lastPingTime;
    }
    
    public boolean isPingTimeout()
    {
    	return (System.currentTimeMillis() - lastPingTime) > pingActiveTime;
    }
}
