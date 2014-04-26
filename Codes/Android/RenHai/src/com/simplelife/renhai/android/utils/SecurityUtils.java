package com.simplelife.renhai.android.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils 
{
	private static byte[] iv = {1, 9, 8, 9, 0, 6, 0, 4};

	public static byte[] encryptDES(String encryptString, String encryptKey) throws Exception 
	{
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
	    SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
	    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
	    byte[] encryptedData = cipher.doFinal(encryptString.getBytes("UTF-8"));

	    return encryptedData;
	}

	public static String encryptByDESAndEncodeByBase64(String encryptString, String encryptKey) throws Exception
	{
		return Base64Utils.encode(encryptDES(encryptString, encryptKey));
	}
	
	public static String decryptDES(byte[] encryptedData, String decryptKey) throws Exception 
	{
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes("UTF-8"), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(encryptedData);
	    String decryptedString = new String(decryptedData, "UTF-8");

	    return decryptedString;
	}
	
	public static String decryptByDESAndDecodeByBase64(String encryptedString, String decryptKey) throws Exception
	{
		byte[] encryptedData = Base64Utils.decode(encryptedString);
		return decryptDES(encryptedData, decryptKey);
	}  	   
	
	public static void testEncrypt(String[] args)
	{
		try
		{
		    String plainText = "{\"id\":\"AlohaRequest\",\"body\":{\"content\":\"我勒个去~\"}}";
		    String keyText ="20120801";

		    String encryptedString = SecurityUtils.encryptByDESAndEncodeByBase64(plainText, keyText);
		    String decryptedString = SecurityUtils.decryptByDESAndDecodeByBase64(encryptedString, keyText);
		    
		    System.out.println("密钥：" + keyText);
		    System.out.println("原文：" + plainText);
		    System.out.println("密文：" + encryptedString);		    
		    System.out.println("解密：" + decryptedString);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
