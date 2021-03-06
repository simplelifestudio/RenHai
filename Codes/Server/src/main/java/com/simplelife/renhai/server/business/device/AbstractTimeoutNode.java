package com.simplelife.renhai.server.business.device;



public abstract class AbstractTimeoutNode
{
	protected AbstractTimeoutNode prevNode;
	protected AbstractTimeoutNode nextNode;
	protected long lastTime;
	protected int timeoutThreshold;
	//protected TimeoutLink ownerLink;

    public AbstractTimeoutNode(int timeoutThreshold)
    {
    	setTimeoutThreshold(timeoutThreshold);
    	//this.ownerLink = ownerLink;
    	//ownerLink.append(this);
    }
    
    /**
	 * @return the prevNode
	 */
	public AbstractTimeoutNode getPrevNode()
	{
		return prevNode;
	}
	
	public void setTimeoutThreshold(int timeoutThreshold)
	{
		this.timeoutThreshold = timeoutThreshold;
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
