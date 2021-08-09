package test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class testCreateAccount {
	private final static Base64.Encoder encoder = Base64.getEncoder();
	public static void main(String[] args) throws UnsupportedEncodingException {
		String uid = "H120983053";
		//String birthdate = "0590909";
		//String birthdate = "19700909";
		String birthdate = "19701009";
		System.out.println(encoder.encodeToString((uid+birthdate).getBytes("UTF-8")));
		// H120983053+19700909 = SDEyMDk4MzA1MzE5NzAwOTA5
		// H120983053+19701009 = SDEyMDk4MzA1MzE5NzAxMDA5
	}

}
