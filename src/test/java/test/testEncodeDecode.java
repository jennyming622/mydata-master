package test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class testEncodeDecode {
	public static void main(String[] args) throws UnsupportedEncodingException, Exception {
		String uid = "H120983053";
		System.out.println(uid);
		String encryptUidStr = Base64.getUrlEncoder().encodeToString(uid.getBytes());
		System.out.println(encryptUidStr);
		System.out.println(new String(Base64.getUrlDecoder().decode(encryptUidStr),"UTF-8"));
	}
}
