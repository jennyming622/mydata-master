package tw.gov.ndc.emsg.mydata.web;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.riease.common.helper.HttpClientHelper;
import com.riease.common.util.SslUtils;

public class SpApiErrorHandleThread  extends Thread {
	private String sendUrl;
	private String outStr;
	private long waittime;
	public SpApiErrorHandleThread(String sendUrl,String outStr,long waittime) {
		this.sendUrl = sendUrl;
		this.outStr = outStr;
		this.waittime = waittime;
	}
	
	public void run() {
		try {
			Thread.sleep(waittime);
			if (sendUrl.startsWith("https")) {
				/**
				 * SSL disable
				 */
				SslUtils.ignoreSsl();
				
				URL connectto = new URL(sendUrl);
				HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
				int respCode = 0;
				if(conn!=null) {
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					conn.setRequestProperty("Accept", "application/json");
					
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setInstanceFollowRedirects(false);
					conn.setDoOutput(true);
					System.out.println("SP-API 2 "+waittime+":"+new Date()+":"+outStr);
					OutputStream os = null;
					try {
						os = conn.getOutputStream();
						os.write(outStr.getBytes("UTF-8"));
						os.close();
					}finally {
						if(os!=null) {
							HttpClientHelper.safeClose(os);
						}
					}
					respCode = conn.getResponseCode();
				}
				if(respCode==200||respCode==201) {
					//傳送success
					System.out.println("== mydata-sp notification success! 3==:"+respCode+" - "+new Date());
					System.out.println(sendUrl+" success!" + waittime + " ms!" + outStr);
				}else {
					if(waittime<=60000l) {
						SpApiErrorHandleThread t = new SpApiErrorHandleThread(sendUrl,outStr,300000l);
						t.run();
					}else if(waittime<=300000l){
						System.out.println("== mydata-sp notification error! 300000l==:"+respCode+" - "+new Date());
						SpApiErrorHandleThread t = new SpApiErrorHandleThread(sendUrl,outStr,900000l);
						t.run();
					}else {
						System.out.println("== mydata-sp notification error! 900000l==:"+respCode+" - "+new Date());
						System.out.println(sendUrl+" Error!" + waittime + " ms!" + outStr);
					}
				}
			} else {					
				URL connectto = new URL(sendUrl);
				HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
				int respCode = 0;
				if(conn!=null) {
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					conn.setRequestProperty("Accept", "application/json");
					
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setInstanceFollowRedirects(false);
					conn.setDoOutput(true);
					System.out.println("SP-API 2 "+waittime+":"+new Date()+":"+outStr);
					OutputStream os = null;
					try {
						os = conn.getOutputStream();
						os.write(outStr.getBytes("UTF-8"));
						os.close();
					}finally {
						if(os!=null) {
							HttpClientHelper.safeClose(os);
						}
					}
					respCode = conn.getResponseCode();
				}
				if(respCode==200||respCode==201) {
					//傳送success
					System.out.println("== mydata-sp notification success! 3==:"+respCode+" - "+new Date());
					System.out.println(sendUrl+" success!" + waittime + " ms!" + outStr);
				}else {
					if(waittime<=60000l) {
						SpApiErrorHandleThread t = new SpApiErrorHandleThread(sendUrl,outStr,300000l);
						t.run();
					}else if(waittime<=300000l){
						System.out.println("== mydata-sp notification error! 300000l==:"+respCode+" - "+new Date());
						SpApiErrorHandleThread t = new SpApiErrorHandleThread(sendUrl,outStr,900000l);
						t.run();
					}else {
						System.out.println("== mydata-sp notification error! 900000l==:"+respCode+" - "+new Date());
						System.out.println(sendUrl+" Error!" + waittime + " ms!" + outStr);
					}
				}
			}
		} catch(Exception ex) {
			System.out.println(sendUrl+" Error!" + waittime + " ms!" +" - "+new Date() + " - " + outStr);
		}
	}
}
