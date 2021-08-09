/**
 * 
 */
package com.riease.common.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 對字串進行MD5編碼
 * @author wesleyzhuang
 *
 */
public class MD5Helper {

	private MD5Helper() {
	}
	
	/**
	 * 進行MD5編碼。
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String input) {
		if(input == null) {
			return null;
		}else {
			String result = input;
			try {
		        MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
		        md.update(input.getBytes());
		        BigInteger hash = new BigInteger(1, md.digest());
		        result = hash.toString(16);
		        while(result.length() < 32) { //40 for SHA-1
		            result = "0" + result;
		        }
		    }catch(Exception ex) {
		    	ex.printStackTrace();
		    }
			return result;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ss = "1234";
		
		System.out.println(MD5Helper.md5(ss));

	}

}
