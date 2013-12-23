package com.simplelife.renhai.server.business.device;

import com.simplelife.renhai.server.business.pool.TimeoutLink;


public abstract class AbstractTimeoutNode
{
	protected AbstractTimeoutNode prevNode;
	protected AbstractTimeoutNode nextNode;
	protected long lastTime;
	protected int timeoutThreshold;
	protected TimeoutLink ownerLink;

    public AbstractTimeoutNode(int timeoutThreshold, TimeoutLink ownerLink)
    {
    	this.timeoutThreshold = timeoutThreshold;
    	this.ownerLink = ownerLink;
    	ownerLink.append(this);
    }
    
	/**
	 * @return the prevNode
	 */
	public AbstractTimeoutNode getPrevNode()
	{
		return prevNode;
	}
	/**
	 * @param prevNode the prevNode to set
	 */
	public void setPrevNode(AbstractTimeoutNode prevNode)
	{
		this.prevNode = prevNode;
	}
	/**
	 * @return the nextNode
	 */
	public AbstractTimeoutNode getNextNode()
	{
		return nextNode;
	}
	/**
	 * @param nextNode the nextNode to set
	 */
	public void setNextNode(AbstractTimeoutNode nextNode)
	{
		this.nextNode = nextNode;
	}
	
	/**
     * Save current time as lastPingTime 
     */
    public void updateTime()
    {
    	long now = System.currentTimeMillis();
    	lastTime = now;
    }
    
    public long getLastTime()
    {
        return lastTime;
    }
    
    public boolean isTimeout()
    {
    	return (System.currentTimeMillis() - lastTime) > timeoutThreshold;
    }
    
    public abstract void onTimeout();
}
