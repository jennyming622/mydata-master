package test;

import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import tw.gov.ndc.emsg.mydata.util.IdentifyUtil;
import tw.gov.ndc.emsg.mydata.util.PemUtil;

public class IdentifyTest {
	
	public static void main(String args[]) {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put("ck_personId", "F128205045");
			param.put("ck_birthDate", "0800109");
			
			Map<String, Object> respMap = new HashMap<>(10);
			respMap.put("result", false);
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			JwtClaims claims = new JwtClaims();
			claims.setIssuer("6NwuqHzRJNOQ7WCPpioXWluoZO6FXzCK");
			claims.setSubject("iisi restful api"); 
			claims.setAudience("iisi rd");
			
			// 因戶政司的系統時間慢分 所以將 iat 時間調慢 3 分鐘
			NumericDate exp = NumericDate.now();
			exp.addSeconds(-60 * 10);
			claims.setIssuedAt(exp);
			claims.setExpirationTimeMinutesInTheFuture(10); 
			claims.setClaim("jti", UUID.randomUUID().toString());
			
			claims.setClaim("orgId", "A41000000G"); 
			claims.setClaim("apId", "NDC"); 
			claims.setClaim("userId", "A41000000G"); 
			claims.setClaim("opType", "RW"); 
			claims.setClaim("jobId", "V1C101");
			claims.setClaim("conditionMap", objectMapper.writeValueAsString(param)); 
			
			JsonWebSignature jws = new JsonWebSignature();
			jws.setPayload(claims.toJson());
			
		    PrivateKey privateKey = PemUtil.readPrivateKeyFromFile("/Users/chenjiawei/Desktop/Work/MyData/keystore/mydataAP-test.pem", "RSA");
		    jws.setKey(privateKey);
		    jws.setHeader("typ", "JWT");
		    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
		    
		    String token = jws.getCompactSerialization();
		    System.out.println("create token >>> [" + token + "]");
		    
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

	        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
	        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
			
			CloseableHttpClient httpClient = HttpClients.custom()
	                .setSSLSocketFactory(connectionFactory)
	                .build();
		    HttpGet httpGet = new HttpGet("https://210.241.14.33:1443/integration/rwv1c1");
		    httpGet.setHeader("Authorization", "Bearer " + token);
		    httpGet.setHeader("Cache-Control", "no-cache");
		    httpGet.setHeader("Content-Type", "application/json");
		    httpGet.setHeader("accept", "application/json");
		    httpGet.setHeader("sris-consumerAdminId", "00000000");
		    
		    try {
	    		CloseableHttpResponse httpResp = httpClient.execute(httpGet);
	    		String content = EntityUtils.toString(httpResp.getEntity());
	    		
	    		System.out.println(content);
	    		
		    }catch(Exception ex) {
		    	System.out.println(ex.getLocalizedMessage());
		    }
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			System.out.println(ex);
		}
	}

}
