package com.simplelife.renhai.server.business.device;


public abstract class AbstractTimeoutNode
{
	private AbstractTimeoutNode prevNode;
	private AbstractTimeoutNode nextNode;
    private long lastTime;
    private int timeoutThreshold;

    public AbstractTimeoutNode(int timeoutThreshold)
    {
    	this.timeoutThreshold = timeoutThreshold;
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
