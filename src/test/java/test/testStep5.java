package test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

public class testStep5 {

	public static void main(String[] args) throws Exception {
		String secret = "0471f681fa334dba87c0d47fccd76a47";
		String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
		String payload = "{\"filename\":\"CLI.klmZFqH5SQ.zip\",\"data\":\"application/zip;data:"+ encodeFileToBase64Binary("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ.encrypt.zip") +"\"}";
		System.out.println(header);
		System.out.println(payload);
		System.out.println("---------------------");
		System.out.println(payload);
		String unsigntoken = Base64.getUrlEncoder().encodeToString(header.getBytes("UTF-8"))+"."+Base64.getUrlEncoder().encodeToString(payload.getBytes("UTF-8"));
		System.out.println(unsigntoken);
		System.out.println("---------------------");
		String signature = HMACSHA256(unsigntoken.getBytes("UTF-8"),secret.getBytes("UTF-8"));
		System.out.println(unsigntoken+"."+signature);

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
	
	public static String encodeFileToBase64Binary(String fileName) throws Exception {
		byte[] bytes = Files.readAllBytes(Paths.get(fileName));
		String encodedString = Base64.getUrlEncoder().encodeToString(bytes);
		return encodedString;
	}
	
	public static String decodeFileToBase64Binary(String encodedString,String fileName) throws Exception {
		byte[] b = Base64.getUrlDecoder().decode(encodedString.getBytes("UTF-8"));
		FileUtils.writeByteArrayToFile(new File(fileName), b);
		return encodedString;
	}
}
