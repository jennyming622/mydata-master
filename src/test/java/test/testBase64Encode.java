package test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class testBase64Encode {

	public static void main(String[] args) throws UnsupportedEncodingException {
		//String param1 = "API.idPhotoRev:API.Kr1C3b1ijJ:API.UDauDOLyZg";
		String param1 = "API.Kr1C3b1ijJ:API.UDauDOLyZg";
		String encodeStr = Base64.getUrlEncoder().encodeToString(param1.getBytes("UTF-8"));
		System.out.println(encodeStr);
		
	}

}
