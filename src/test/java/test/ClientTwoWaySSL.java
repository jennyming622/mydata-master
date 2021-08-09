package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

public class ClientTwoWaySSL {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
//		// TODO Auto-generated method stub
//
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		//System.out.println("KeyStore.getDefaultType()="+KeyStore.getDefaultType());
//		//System.out.println("KeyStore.getDefaultType()="+KeyStore.getDefaultType());
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        KeyStore trustStore = KeyStore.getInstance("PKCS12");
//
//        //FileInputStream keyStoreIn = new FileInputStream(new File("/Users/mac/Desktop/keystore/mydata.jks"));
//        //FileInputStream trustStoreIn = new FileInputStream(new File("/Users/mac/Desktop/keystore/mydata.jks"));
//
//        FileInputStream keyStoreIn = new FileInputStream(new File("/Users/mac/Desktop/keystore/mydatadevssl.jks"));
//        FileInputStream trustStoreIn = new FileInputStream(new File("/Users/mac/Desktop/keystore/mydatadevssl.jks"));
//
//        try {
//            keyStore.load(keyStoreIn, "mydata1234".toCharArray());
//            trustStore.load(trustStoreIn, "mydata1234".toCharArray());
//        } finally {
//            //keyStoreIn.close();
//            trustStoreIn.close();
//        }
//
//        SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, "mydata1234", trustStore);
//        Scheme sch = new Scheme("https", socketFactory, 8443);
//
//        httpclient.getConnectionManager().getSchemeRegistry().register(sch);
//
//        //HttpGet httpget = new HttpGet("https://mydata.nat.gov.tw:8443/sp/about");
//        HttpGet httpget = new HttpGet("https://mydatadev.nat.gov.tw:8443/mydata/sp/about");
//
//        System.out.println("Request:" + httpget.getRequestLine());
//
//        HttpResponse response = httpclient.execute(httpget);
//        HttpEntity entity = response.getEntity();
//
//        System.out.println("----------------------------------------");
//        System.out.println(response.getStatusLine());
//        if (entity != null) {
//            System.out.println("Response content length: " + entity.getContentLength());
//        }
//        if (entity != null) {
//            entity.consumeContent();
//        }
//        httpclient.getConnectionManager().shutdown();

        File keyStoreFile = new File("/Users/chenjiawei/Desktop/Work/MyData/twoWay/keystore/mydatadevssl.jks");
        File trustStoreFile = new File("/Users/chenjiawei/Desktop/Work/MyData/twoWay/keystore/mydatadevssl.jks");

        SSLContextBuilder sslContextBuilder = SSLContexts.custom();
        sslContextBuilder.loadKeyMaterial(keyStoreFile, "mydata1234".toCharArray(), "mydata1234".toCharArray());
        sslContextBuilder.loadTrustMaterial(trustStoreFile, "mydata1234".toCharArray());

        SSLContext sslContext = sslContextBuilder.build();

        SSLConnectionSocketFactory sslConSocFactory =
            new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> registry =
            RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslConSocFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        CloseableHttpClient httpclient = HttpClients.custom()
            .setSSLSocketFactory(sslConSocFactory)
            .setConnectionManager(connectionManager)
            .setRedirectStrategy(new LaxRedirectStrategy())
            .build();

        HttpPost httpPost = new HttpPost("https://175.184.247.112:8056/MyData/notifydownload");
            httpPost.setEntity(new StringEntity("{ \"code\": \"OK\" }"));
        HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        if (entity != null) {
            System.out.println("Response content length: " + entity.getContentLength());
        }
        if (entity != null) {
            entity.consumeContent();
        }
        httpclient.getConnectionManager().shutdown();
	}

}
