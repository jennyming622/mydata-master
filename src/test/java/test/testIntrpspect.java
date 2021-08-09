package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.gov.ndc.emsg.mydata.gspclient.bean.IntrospectEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenRecord;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;

public class testIntrpspect {
	private final Base64.Encoder encoder = Base64.getEncoder();
	private final Base64.Decoder decoder = Base64.getDecoder();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testIntrpspect h = new testIntrpspect();
		try {
			h.introspect("c41c70f12ff1bc62cbe9f77508de71c835ccc3df045a5b3c91a14c476ff10daf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String introspect(final String token) throws IOException {
		final List<NameValuePair> pairList = new ArrayList<>();
		pairList.add(new BasicNameValuePair("token", token));
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		final HttpPost post = new HttpPost("https://login.cp.gov.tw/v1/connect/introspect");
		post.addHeader("Accept", "application/json");//"application/json"
		post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");// "application/x-www-form-urlencoded;charset=UTF-8"
		post.addHeader("Authorization", "Basic " + basicAuthenticationSchema("API.ea0klQl5Wp", "eyB2rKE9FOw0sUKy"));//"Basic QVBJLmVhMGtsUWw1V3A6ZXlCMnJLRTlGT3cwc1VLeQ=="
		post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		
		System.out.println("introspect={}"+ post);
		
		final HttpResponse response = httpClient.execute(post);//HttpClientBuilder.create().build()
		
		final int httpStatusCode = response.getStatusLine().getStatusCode();
		if (httpStatusCode != HttpStatus.SC_OK) {
			System.out.println("introspect failure, HttpStatus={}"+ httpStatusCode);
			throw new RuntimeException("introspect failure, HttpStatus=" + httpStatusCode);
		}
		
		final String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
		System.out.println("introspect success, responseStr={}"+ responseStr);
		
		return responseStr;
	}
	
	private String basicAuthenticationSchema(String clientId, String clientSecret) {
		StringBuilder sb = new StringBuilder();
		sb.append(clientId).append(":").append(clientSecret);
		try {
			//return encoder.encodeToString(sb.toString().getBytes("UTF-8"));
			return encoder.encodeToString(sb.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
