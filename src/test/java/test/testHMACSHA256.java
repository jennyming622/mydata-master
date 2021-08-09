package test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class testHMACSHA256 {
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String secret_key = "0471f681fa334dba87c0d47fccd76a47";
		String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
		String payload = "{\"iss\":\"www.gsp.gov.tw\",\"uuid\":\"f03c5b81-9cb8-40f7-8945-c63f12580821\",\"result\":\"A0000：檢核正確!\",\"iat\":1557970965}";
		String unsigntoken = Base64.getUrlEncoder().encode(header.getBytes("UTF-8"))+"."+Base64.getUrlEncoder().encode(payload.getBytes("UTF-8"));
		String signature = HMACSHA256(unsigntoken.getBytes(),secret_key.getBytes());
		System.out.println(signature);
	}
	public static String HMACSHA256(byte[] data, byte[] key){
	      try  {
	         SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
	         Mac mac = Mac.getInstance("HmacSHA256");
	         mac.init(signingKey);
	         return byte2hex(mac.doFinal(data));
	      } catch (NoSuchAlgorithmException e) {
	         e.printStackTrace();
	      } catch (InvalidKeyException e) {
	        e.printStackTrace();
	      }
	      return null;
	}
	
	public static String byte2hex(byte[] b){
	    StringBuilder hs = new StringBuilder();
	    String stmp;
	    for (int n = 0; b!=null && n < b.length; n++) {
	        stmp = Integer.toHexString(b[n] & 0XFF);
	        if (stmp.length() == 1)
	            hs.append('0');
	        hs.append(stmp);
	    }
	    return hs.toString().toUpperCase();
	}
}
