package com.simplelife.renhai.server.websocket;


public class StringUtils 
{
    public static String parseByte2HexStr(byte buf[])
    {  
    	StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < buf.length; i++) 
        {  
        	String hex = Integer.toHexString(buf[i] & 0xFF);  
            if (hex.length() == 1) 
            {  
            	hex = '0' + hex;  
            }  
            sb.append(hex.toUpperCase());  
        }  
        
        return sb.toString();  
    }	
}
