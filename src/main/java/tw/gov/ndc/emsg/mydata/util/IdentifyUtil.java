package tw.gov.ndc.emsg.mydata.util;

import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import tw.gov.ndc.emsg.mydata.model.WebServiceJobId;

@Component("identifyUtil")
public class IdentifyUtil implements ValidUtil {
	
	private static Logger log = LoggerFactory.getLogger(IdentifyUtil.class);
	
	@Value("${moi.url}")
	private String url;
	@Value("${moi.privateKeyFilePath}")
	private String privateKeyFilePath;
	@Value("${moi.orgId}")
	private String orgId;
	@Value("${moi.userId}")
	private String userId;
	@Value("${moi.srisConsumerAdminId}")
	private String srisConsumerAdminId;
	
	public String call(Map<String, Object> parameters, WebServiceJobId jobId) throws Exception{
		log.debug("Use IdentifyUtil");

		Map<String, Object> respMap = new HashMap<>(10);
		respMap.put("result", false);
		
		log.warn("request url {}", this.url);
		ObjectMapper objectMapper = new ObjectMapper();
		log.warn("conditionMap values >>> {}", objectMapper.writeValueAsString(parameters));
		
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
		
		claims.setClaim("orgId", orgId); 
		claims.setClaim("apId", "NDC"); 
		claims.setClaim("userId", userId); 
		claims.setClaim("opType", "RW");
		claims.setClaim("jobId", jobId.getStringValue());
		claims.setClaim("conditionMap", objectMapper.writeValueAsString(parameters)); 
		
		log.warn("claims values >>> {}", objectMapper.writeValueAsString(claims));
		
		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		
	    PrivateKey privateKey = PemUtil.readPrivateKeyFromFile(this.privateKeyFilePath, "RSA");
	    jws.setKey(privateKey);
	    jws.setHeader("typ", "JWT");
	    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
	    
	    String token = jws.getCompactSerialization();
	    log.warn("create token >>> [{}]", token);
	    
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

		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(15 * 1000).build();


	    HttpGet httpGet = new HttpGet(this.url);
	    httpGet.setHeader("Authorization", "Bearer " + token);
	    httpGet.setHeader("Cache-Control", "no-cache");
	    httpGet.setHeader("Content-Type", "application/json");
	    httpGet.setHeader("accept", "application/json");
	    httpGet.setHeader("sris-consumerAdminId", srisConsumerAdminId);
	    httpGet.setConfig(requestConfig);
	    
	    try {
    		CloseableHttpResponse httpResp = httpClient.execute(httpGet);
    		String content = EntityUtils.toString(httpResp.getEntity());
			System.out.println("content values >>> " + content);
    		log.warn("content values >>> {}", content);
    		return content;
	    }catch(Exception ex) {
	    	log.error(ex.getLocalizedMessage(), ex);
	    }
		return RETURN_FAIL;
	}

	/**
	 * 利用內政部WebService驗證身份
	 * 回傳1表示通過
	 * @param response
	 * @return
	 */
	public Boolean isIdentifyValid(String response){
		if(StringUtils.equals(response, RETURN_FAIL)) {
			return false;
		}

		System.out.println("isIdentifyValid response >> " + response);
		// 1 -> ok , 0 -> fail
		String identifyValid = "1";
		JSONObject dataObj = new JSONObject(response).getJSONObject("responseData");
		if(!dataObj.has("checkAll")){
			return false;
		}
		String checkAll = dataObj.getString("checkAll");
		return StringUtils.equals(checkAll,identifyValid);
	}

	public static void main(String[] args) {
		String json = "{\"httpCode\":\"200\",\"httpMessage\":\"OK\",\"rdCode\":\"RS7009\",\"rdMessage\":\"查詢作業完成\",\"responseData\":{\"checkAll\":\"1\",\"checkAreaCode\":\"N/A\",\"checkBirthDate\":\"1\",\"checkBirthDateRange\":\"N/A\",\"checkCardNo\":\"N/A\",\"checkChgidDateRange\":\"N/A\",\"checkChgnameDateRange\":\"N/A\",\"checkDeathDateRange\":\"N/A\",\"checkFilmNo\":\"N/A\",\"checkHouseholdId\":\"N/A\",\"checkIdMarkAndMarkDate\":\"N/A\",\"checkIncidentYyymmddRange\":\"N/A\",\"checkLivingStyleCode\":\"N/A\",\"checkPluralMoveDateRange\":\"N/A\",\"checkSameHousehold\":\"N/A\",\"checkSingleMoveDateRange\":\"N/A\",\"checkSpecialInciCode\":\"N/A\",\"checkSpecialMark\":\"N/A\",\"checkSpouse\":\"N/A\"}}";
		//String json = RETURN_FAIL;
		try {
			System.out.println(new IdentifyUtil().isIdentifyValid(json));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}
