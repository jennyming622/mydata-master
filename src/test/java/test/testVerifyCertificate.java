package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.http.HttpStatus;

public class testVerifyCertificate {

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
		 
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kms, tms, new SecureRandom());
		 
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
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
			if (keyStoreStream != null) keyStoreStream.close();
			if (trustStoreStream != null) trustStoreStream.close();
		}
		 
		// Construct the connection.
		HttpsURLConnection conn =  (HttpsURLConnection) connectto.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json");
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		
		int httpstatuscode = conn.getResponseCode();
		System.out.println("==httpstatuscode==:"+httpstatuscode);
		if(httpstatuscode==HttpStatus.OK.value()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
	
			System.out.println("-----sb.toString() start---------");
			String responeStr = sb.toString();
			System.out.println(responeStr);
			System.out.println("-----sb.toString() end---------");
		}else {
			System.out.println("-----sb.toString() start---------");
			System.out.println("==httpstatuscode==:"+httpstatuscode);
			System.out.println("-----sb.toString() end---------");
		}
	}

}
