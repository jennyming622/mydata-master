package test;

import java.io.BufferedReader;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.Socket;
import java.nio.charset.Charset;

import javax.net.SocketFactory;  
import javax.net.ssl.SSLSocketFactory; 

public class SSLClient {
	private static String CLIENT_KEY_STORE = "/Users/mac/Desktop/keystore/mydata.jks";

	public static void main(String[] args) throws Exception {
		// Set the key store to use for validating the server cert.
		System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);

		System.setProperty("javax.net.debug", "ssl,handshake");

		SSLClient client = new SSLClient();
		Socket s = client.clientWithoutCert();
		String msg="GET /mydata/sp/verifyCertificate HTTP/1.1\n\n";
		PrintWriter writer = new PrintWriter(s.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		byte[] msgByte = msg.getBytes(Charset.forName("UTF-8"));
		writer.println(msgByte);
		writer.flush();
		System.out.println(msgByte);
		s.close();
	}

	private Socket clientWithoutCert() throws Exception {
		SocketFactory sf = SSLSocketFactory.getDefault();
		Socket s = sf.createSocket("msg.nat.gov.tw", 8443);
		return s;
	}
}
