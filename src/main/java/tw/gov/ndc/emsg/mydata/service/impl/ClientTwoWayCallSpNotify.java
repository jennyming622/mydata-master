package tw.gov.ndc.emsg.mydata.service.impl;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.service.AbstractCallSpNotify;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

@Component("clientTwoWayCallSpNotify")
public class ClientTwoWayCallSpNotify extends AbstractCallSpNotify {

    @Value("${client.two.way.keyStore.path}")
    private String keyStorePath;

    @Value("${client.two.way.trustStore.path}")
    private String trustStorePath;

    @Value("${client.two.way.store.password}")
    private String password;

    @Override
    protected CloseableHttpClient client() throws Exception{

        File keyStoreFile = new File(keyStorePath);
        File trustStoreFile = new File(trustStorePath);

        SSLContextBuilder sslContextBuilder = SSLContexts.custom();
        sslContextBuilder.loadKeyMaterial(keyStoreFile, password.toCharArray(), password.toCharArray());
        sslContextBuilder.loadTrustMaterial(trustStoreFile, password.toCharArray());

        SSLContext sslContext = sslContextBuilder.build();

        SSLConnectionSocketFactory sslConSocFactory =
                new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("https", sslConSocFactory).build();

//		  未設的default是60秒
//        RequestConfig defaultRequestConfig = RequestConfig.custom()
//        		.setSocketTimeout(30000)  
//        		.setConnectTimeout(30000)  
//        		.setConnectionRequestTimeout(30000)
//        		.build();
        
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslConSocFactory)
                .setConnectionManager(connectionManager)
                .setRedirectStrategy(new LaxRedirectStrategy()) // 重導向
//                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

       return httpclient;
    }
}
