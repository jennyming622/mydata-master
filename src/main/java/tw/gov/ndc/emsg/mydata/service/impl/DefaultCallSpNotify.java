package tw.gov.ndc.emsg.mydata.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.service.AbstractCallSpNotify;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component("defaultCallSpNotify")
public class DefaultCallSpNotify extends AbstractCallSpNotify {


    @Override
    protected CloseableHttpClient client() throws Exception{

        // Ignore SSL
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustStrategy() {
                    // ignore certificate
                    public boolean isTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                        return true;
                    }
                })
                .build();

        SSLConnectionSocketFactory connectionFactory =
                new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

//		  未設的default是60秒
//        RequestConfig defaultRequestConfig = RequestConfig.custom()
//        		.setSocketTimeout(30000)  
//        		.setConnectTimeout(30000)  
//        		.setConnectionRequestTimeout(30000)
//        		.build();
        
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(connectionFactory)
                .setRedirectStrategy(new LaxRedirectStrategy())
//                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        return httpClient;
    }


}
