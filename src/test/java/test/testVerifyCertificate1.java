package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class testVerifyCertificate1 {

	public static void main(String[] args) throws IOException {
		System.out.println("---testVerifyCertificate start---");
		URL connectto = new URL("https://msg.nat.gov.tw:8443/mydata/sp/verifyCertificate");
		FileInputStream keyStoreStream = null;
		FileInputStream trustStoreStream = null;
		try {
			keyStoreStream = new FileInputStream(new File("/Users/mac/Desktop/keystore/mydata.jks"));
			KeyStore clientStore = KeyStore.getInstance("JKS");
			clientStore.load(keyStoreStream, "mydata1234".toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, "mydata1234".toCharArray());
			KeyManager[] kms = kmf.getKeyManagers();

			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStoreStream = new FileInputStream(new File("/Users/mac/Desktop/keystore/mydata.jks"));
			trustStore.load(trustStoreStream, "mydata1234".toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustStore);
			TrustManager[] tms = tmf.getTrustManagers();

			SSLContext sslContext = SSLContext.getInstance("TLSv1");
			sslContext.init(kms, tms, new SecureRandom());

			// HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			Socket socket = socketFactory.createSocket("msg.nat.gov.tw", 8443);

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			send("hello", out);
			send("exit", out);
			receive(in);
			socket.close();

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (keyStoreStream != null)
				keyStoreStream.close();
			if (trustStoreStream != null)
				trustStoreStream.close();
		}
	}

	public static void send(String s, PrintWriter out) throws IOException {
		System.out.println("Sending: " + s);
		out.println(s);
	}

	public static void receive(BufferedReader in) throws IOException {
		String s;
		while ((s = in.readLine()) != null) {
			System.out.println("Reveived: " + s);
		}
	}
}
