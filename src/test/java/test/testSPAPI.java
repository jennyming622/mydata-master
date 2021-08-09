package test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceDownload;

public class testSPAPI {

	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		String permission_ticket = "54978583-1658-4686-8b3d-5a9d457bc0e8";
		String secret_key = "0471f681fa334dba87c0d47fccd76a47";
		PortalServiceDownload psdbean = new PortalServiceDownload();
		psdbean.setPsId(44);
		psdbean.setClientId("CLI.klmZFqH5SQ");
		psdbean.setPermissionTicket(permission_ticket);
		psdbean.setSecretKey(secret_key);
		psdbean.setCtime(new Date());
		psdbean.setStat(0);
		
		/**
		 * SSL disable
		 */
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		URL connectto = new URL("http://localhost:8080/sp-example/notification");
		HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);

		OutputStream os = conn.getOutputStream();
		String outStr = "{\"permission_ticket\":\"" + psdbean.getPermissionTicket() +"\",\"secret_key\":\"" +psdbean.getSecretKey() +"\"}";
		os.write(outStr.getBytes("UTF-8"));
		os.close();
		
		int respCode = conn.getResponseCode();
		System.out.println(respCode);
	}

}
