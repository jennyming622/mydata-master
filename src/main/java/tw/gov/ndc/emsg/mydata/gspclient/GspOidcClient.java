/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient;

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

/**
 * 取得 GSP 提供的 OpenID Configuration
 * @author wesleyzhuang
 *
 */
//@Component
public class GspOidcClient {

	private static Logger logger = LoggerFactory.getLogger(GspOidcClient.class);
	
	private final static Base64.Encoder encoder = Base64.getEncoder();
	private final static Base64.Decoder decoder = Base64.getDecoder();
	
	@Value("${gsp.oidc.config.uri}")
	private String gspOpenIdConfigUri;
	@Value("${gsp.oidc.client.id}")
	private String clientId;		
	@Value("${gsp.oidc.client.secret}")
	private String clientSecret;
	@Value("${app.oidc.redirect.uri}")
	private String redirectUri;
	@Value("${app.oidc.response.type}")
	private String responseType;
	@Value("${app.oidc.response.mode}")
	private String responseMode;
	@Value("${app.oidc.logout.redirect.uri}")
	private String logoutRedirectUri;
	@Value("${gsp.oidc.token.query.uri}")
	private String tokenQueryUri;
	@Value("${gsp.oidc.token.cancel.uri}")
	private String tokenCancelUri;
	@Value("${gsp.oidc.token.revoke.uri}")
	private String tokenRevokeUri;
	@Value("${gsp.oidc.token.checkuc.uri}")
	private String checkucUri;
	
	private OidcConfig config;
	
	/**
	 * 取得授權主機提供的OpenID Configuration相關參數。
	 * 建構此物件時已自動呼叫一次，除非要重新從GSP取得，否則不需重新呼叫。
	 */
	@PostConstruct
	public boolean discoveryConfig() {
		logger.debug("gsp.oidc.config.uri ............ {}", gspOpenIdConfigUri);
		logger.debug("gsp.oidc.client.id ............. {}", clientId);
		logger.debug("gsp.oidc.client.secret ......... {}", clientSecret);
		logger.debug("app.oidc.redirect.uri .......... {}", redirectUri);
		logger.debug("app.oidc.logout.redirect.uri ... {}", logoutRedirectUri);
		logger.debug("app.oidc.response.type ......... {}", responseType);
		logger.debug("app.oidc.response.mode ......... {}", responseMode);
		try {
			RestTemplate restTemplate = new RestTemplate();
			config = restTemplate.getForObject(gspOpenIdConfigUri, OidcConfig.class);
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 從GSP取得的OpenID Connect環境參數。
	 * @return
	 */
	public OidcConfig getConfig() {
		if(config == null) {
			discoveryConfig();
		}
		return config;
	}
	
	/**
	 * GSP OpenID Connect的登入授權網址。
	 * @param scope	指定授權項目
	 * @return
	 */
	public String authorizationUrl(String scope) {
		return authorizationUrl(scope,null,null,null);
	}
	/**
	 * GSP OpenID Connect的登入授權網址。
	 * @param scopeList	指定授權項目
	 * @return
	 */
	public String authorizationUrl(List<String> scopeList) {
		final StringJoiner sj = new StringJoiner(" ");
		scopeList.forEach(p -> {
			sj.add(p);
		});
		return authorizationUrl(sj.toString(),null,null,null);
	}
	/**
	 * GSP OpenID Connect的登入授權網址。
	 * @param scopeList	指定授權項目
	 * @param nonce		備註資訊
	 * @param state		狀態資訊
	 * @return
	 */
	public String authorizationUrl(List<String> scopeList, String nonce, String state,List<String> newScopeList) {
		final StringJoiner sj = new StringJoiner(" ");
		final StringJoiner nsj = new StringJoiner(" ");
		scopeList.forEach(p -> {
			sj.add(p);
		});
		if(newScopeList!=null&&newScopeList.size()>0) {
			newScopeList.forEach(p -> {
				nsj.add(p);
			});
		}
		return authorizationUrl(sj.toString(),nonce,state,nsj.toString());
	}
	
	/**
	 * GSP OpenID Connect的登入授權網址。
	 * @param scope	指定授權項目
	 * @param nonce	備註資訊
	 * @param state	狀態資訊
	 * @return
	 */
	public String authorizationUrl(String scope, String nonce, String state,String newScope) {
		System.out.println("------ authorizationUrl s1------------");
		if(config == null) {
			logger.warn("OpenIDConfig is NULL");
			System.out.println("OpenIDConfig is NULL");
			return null;
		}
		if(StringUtils.isEmpty(config.getAuthorizationEndpoint())) {
			logger.warn("authorization endpoint is NULL or empt");
			System.out.println("authorization endpoint is NULL or empt");
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(config.getAuthorizationEndpoint())
			.append("?client_id=").append(clientId)
			.append("&redirect_uri=").append(redirectUri)
			.append("&response_mode=").append(responseMode)
			.append("&response_type=").append(responseType);
		if(StringUtils.isNotEmpty(scope)) {
			sb.append("&scope=").append(scope);
		}
		if(StringUtils.isNotEmpty(newScope)) {
			sb.append("&newscope=").append(newScope);
		}
		if(StringUtils.isNotEmpty(nonce)) {
			sb.append("&nonce=").append(nonce); 
		}
		if(StringUtils.isNotEmpty(state)) {
			sb.append("&state=").append(state);
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 登出授權主機的網址
	 * @param idToken
	 * @return
	 */
	public String signOutUrl(String idToken) {
		return signOutUrl(idToken,null);
	}
	
	/**
	 * 登出授權主機的網址
	 * @param idToken	登入驗證成功後取得的id_token
	 * @param logoutRedirectUri	登出成功後要重導向的網址
	 * @param state	用來方便client標示狀態，授權主機會原值返回。
	 * @return
	 */
	public String signOutUrl(String idToken, String state) {
		if(config == null) {
			logger.warn("OpenIDConfig is NULL");
			return null;
		}
		if(StringUtils.isEmpty(config.getEndSessionEndpoint())) {
			logger.warn("end_session endpoint is NULL or empt");
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(config.getEndSessionEndpoint())
			.append("?id_token_hint=").append(idToken)
			.append("&post_logout_redirect_uri=").append(logoutRedirectUri);
		if(StringUtils.isNotEmpty(state)) {
			sb.append("&state=").append(state);
		}
		return sb.toString();
	}
	
	/**
	 * 向授權主機請求access_token。
	 * @param code			授權主機核發的authorization_code。
	 * @param grantType		指定授權方式。authorization_code or refresh_token
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public TokenEntity requestAccessToken(String code) throws ClientProtocolException, IOException {
		return requestAccessToken(code,null);
	}
	
	/**
	 * 向授權主機請求access_token。
	 * @param code			授權主機核發的authorization_code。
	 * @param grantType		指定授權方式。authorization_code or refresh_token
	 * @param scope			指定授權範圍。
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public TokenEntity requestAccessToken(String code, String scope) throws ClientProtocolException, IOException {
		if(config == null) {
			logger.warn("OpenIDConfig is NULL");
			return null;
		}
		if(StringUtils.isEmpty(config.getTokenEndpoint())) {
			logger.warn("token endpoint is NULL or empt");
			return null;
		}
		
		List<NameValuePair> pairList = new ArrayList<>();
		pairList.add(new BasicNameValuePair("client_id", getClientId()));
		pairList.add(new BasicNameValuePair("client_secret", getClientSecret()));
		pairList.add(new BasicNameValuePair("grant_type", OidcGrantType.authorization_code.toString()));
		pairList.add(new BasicNameValuePair("code", code));
		pairList.add(new BasicNameValuePair("redirect_uri", getRedirectUri()));
		if(StringUtils.isNotEmpty(scope)) {
			pairList.add(new BasicNameValuePair("scope", scope));
		}
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpPost post = new HttpPost(config.getTokenEndpoint());
		post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		TokenEntity tokenEntity = null;
		
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			logger.debug("以 authorization_code 請求 access_token 成功！ -> {}", responseString);
			if(StringUtils.isNotEmpty(responseString)) {
				ObjectMapper om = new ObjectMapper();
				tokenEntity = om.readValue(responseString, TokenEntity.class);
			}
		}else {
			logger.warn("以 authorization_code 請求 access_token 失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
		}
		
		return tokenEntity;
	}
	
	/**
	 * 向授權主機請求更新access_token。
	 * @param refreshToken	授權主機核發的access_token。
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public TokenEntity refreshAccessToken(String refreshToken) throws ClientProtocolException, IOException {
		return refreshAccessToken(refreshToken,null);
	}
	
	/**
	 * 向授權主機請求更新access_token。
	 * @param refreshToken	授權主機核發的access_token。
	 * @param scope			指定授權範圍。
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public TokenEntity refreshAccessToken(String refreshToken, String scope) throws ClientProtocolException, IOException {
		if(config == null) {
			logger.warn("OpenIDConfig is NULL");
			return null;
		}
		if(StringUtils.isEmpty(config.getTokenEndpoint())) {
			logger.warn("token endpoint is NULL or empt");
			return null;
		}
		
		List<NameValuePair> pairList = new ArrayList<>();
		pairList.add(new BasicNameValuePair("client_id", getClientId()));
		pairList.add(new BasicNameValuePair("client_secret", getClientSecret()));
		pairList.add(new BasicNameValuePair("grant_type", OidcGrantType.refresh_token.toString()));
		pairList.add(new BasicNameValuePair("refresh_token", refreshToken));
		if(StringUtils.isNotEmpty(scope)) {
			pairList.add(new BasicNameValuePair("scope", scope));
		}
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpPost post = new HttpPost(config.getTokenEndpoint());
		post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		TokenEntity tokenEntity = null;
		
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			logger.debug("請求 refresh_token 成功！ -> {}", responseString);
			if(StringUtils.isNotEmpty(responseString)) {
				ObjectMapper om = new ObjectMapper();
				tokenEntity = om.readValue(responseString, TokenEntity.class);
			}
		}else {
			logger.debug("請求 refresh_token 失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
		}
		
		return tokenEntity;
	}
	
	public IntrospectEntity introspectAccessToken(String accessToken) throws ClientProtocolException, IOException {
		return introspectAccessToken(accessToken, getClientId(), getClientSecret());
	}
	
	/**
	 * 反查 access_token
	 * @param accessToken
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public IntrospectEntity introspectAccessToken(String accessToken, String clientId, String clientSecret) throws ClientProtocolException, IOException {
		if(config == null) {
			logger.warn("OpenIDConfig is NULL");
			return null;
		}
		if(StringUtils.isEmpty(config.getIntrospectionEndpoint())) {
			logger.warn("introspection endpoint is NULL or empt");
			return null;
		}
		
		List<NameValuePair> pairList = new ArrayList<>();
//		pairList.add(new BasicNameValuePair("client_id", getClientId()));
//		pairList.add(new BasicNameValuePair("client_secret", getClientSecret()));
		pairList.add(new BasicNameValuePair("token", accessToken));
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpPost post = new HttpPost(config.getIntrospectionEndpoint());
		post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		post.addHeader("Accept", "application/json");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		post.addHeader("Authorization", "Basic "+basicAuthenticationSchema(clientId,clientSecret));
		
		IntrospectEntity introspectEntity = null;
		
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			logger.debug("反查 access_token 成功！ -> {}", responseString);
			if(StringUtils.isNotEmpty(responseString)) {
				ObjectMapper om = new ObjectMapper();
				introspectEntity = om.readValue(responseString, IntrospectEntity.class);
			}
		}else {
			logger.debug("反查 access_token 失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
		}
		
		return introspectEntity;
	}
	
	/**
	 * 廢止 access_token
	 * @param accessToken
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void revocationAccessToken(String accessToken) throws ClientProtocolException, IOException {
		revocationToken(accessToken,OidcTokenType.access_token);
	}
	/**
	 * 廢止 refresh_token
	 * @param refreshToken
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void revocationRefreshToken(String refreshToken) throws ClientProtocolException, IOException {
		revocationToken(refreshToken,OidcTokenType.refresh_token);
	}
	/**
	 * 廢止 access_token or refresh_token
	 * @param accessOrRefreshToken
	 * @param tokenType
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void revocationToken(String accessOrRefreshToken, OidcTokenType tokenType) throws ClientProtocolException, IOException {
		if(config == null) {
			logger.warn("OpenIDConfig is NULL");
			return;
		}
		if(StringUtils.isEmpty(config.getRevocationEndpoint())) {
			logger.warn("revocation endpoint is NULL or empt");
			return;
		}
		
		List<NameValuePair> pairList = new ArrayList<>();
		pairList.add(new BasicNameValuePair("token", accessOrRefreshToken));
		pairList.add(new BasicNameValuePair("token_type_hint", tokenType.toString()));
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpPost post = new HttpPost(config.getRevocationEndpoint());
		post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		post.addHeader("Authorization", "Basic "+basicAuthenticationSchema(clientId,clientSecret));
		
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			logger.debug("廢止 access_token 成功！");
		}else {
			logger.debug("廢止 access_token 失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
		}
	}
	
	/**
	 * 請求用戶資訊
	 * @param accessToken
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public UserInfoEntity requestUserInfo(String accessToken) throws ClientProtocolException, IOException {
		if(StringUtils.isEmpty(accessToken)) {
			logger.warn("access_token is NULL or empty");
			return null;
		}
		if(StringUtils.isEmpty(config.getUserinfoEndpoint())) {
			logger.warn("user_info endpoint is NULL or empt");
			return null;
		}
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpGet get = new HttpGet(config.getUserinfoEndpoint());
		get.addHeader("Content-Type", "application/json");
		get.addHeader("Authorization", "Bearer "+accessToken);
		
		HttpResponse response = httpClient.execute(get);
		UserInfoEntity userInfo = null;
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			logger.debug("以 access_token 請求 user_info 成功！ -> {}", responseString);
			System.out.println("responseString=\n"+responseString);
			if(StringUtils.isNotEmpty(responseString)) {
				ObjectMapper om = new ObjectMapper();
				userInfo = om.readValue(responseString, UserInfoEntity.class);
			}
		}else {
			logger.warn("以 access_token 請求 user_info 失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
		}
		
		return userInfo;
	}
	
	public boolean requestCheckUc(String accessToken) throws ClientProtocolException, IOException {
		boolean ckeck = false;
		if(StringUtils.isEmpty(accessToken)) {
			logger.warn("access_token is NULL or empty");
			return ckeck;
		}
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpGet get = new HttpGet(checkucUri+accessToken);
		HttpResponse response = httpClient.execute(get);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			ckeck = true;
			return ckeck;
		}else {
			return ckeck;
		}
	}
	
	
	/**
	 * 請求登入狀態
	 * 
	 */
	
	/**
	 * 查詢授權記錄
	 * @return
	 */
	public List<TokenRecord> requestTokenRecords(String accessToken) throws ClientProtocolException, IOException {
		if(StringUtils.isEmpty(accessToken)) {
			logger.warn("access_token is NULL or empty");
			return null;
		}
		if(StringUtils.isEmpty(tokenQueryUri)) {
			logger.warn("token query uri is NULL or empty");
			return null;
		}
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpGet get = new HttpGet(tokenQueryUri);
		get.addHeader("Content-Type", "application/json");
		get.addHeader("Authorization", "Bearer "+accessToken);
		
		HttpResponse response = httpClient.execute(get);
		List<TokenRecord> result = null;
		System.out.println("----response.getStatusLine().getStatusCode()----:"+response.getStatusLine().getStatusCode());
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println("----responseString----:\n"+responseString);
			logger.debug("以 access_token 請求授權記錄成功！ -> {}", responseString);
			if(StringUtils.isNotEmpty(responseString)) {
				ObjectMapper om = new ObjectMapper();
				result = om.readValue(responseString, 
							om.getTypeFactory().constructCollectionType(List.class, TokenRecord.class));
			}
		}else {
			result = new ArrayList<>();
			logger.warn("以 access_token 請求授權記錄失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
		}
		
		return result;
	}
	
	/**
	 * 取消授權
	 * @param accessToken
	 * @param key
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Boolean cancelTokenRecord(String accessToken, String key) throws ClientProtocolException, IOException {
		if(StringUtils.isEmpty(accessToken)) {
			logger.warn("access_token is NULL or empty");
			return null;
		}
		if(StringUtils.isEmpty(tokenCancelUri)) {
			logger.warn("token query uri is NULL or empty");
			return null;
		}
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		//String uri = tokenCancelUri.replace("{key}", URLEncoder.encode(key, "UTF-8"));
		String uri = tokenCancelUri.replace("{key}",key);
		System.out.println("uri.......:"+uri);
		logger.debug("取消授權記錄uri .................... {}", uri);
		HttpPut put = new HttpPut(uri);
		put.addHeader("Content-Type", "application/json");
		put.addHeader("Authorization", "Bearer "+accessToken);
		
		HttpResponse response = httpClient.execute(put);
		Boolean result = null;
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
				|| response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
			logger.debug("以 access_token 取消授權記錄成功！");
			System.out.println("HttpStatus.......:"+response.getStatusLine().getStatusCode());
			result = true;
		}else {
			logger.warn("以 access_token 取消授權記錄失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
			System.out.println("HttpStatus.......:"+response.getStatusLine().getStatusCode());
			result = false;
		}
		
		return result;
	}
	
	/**
	 * 取消授權
	 * @param accessToken
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	public Boolean revokeTokenRecord(String accessToken, String clientId, String clientSecret) throws ClientProtocolException, IOException {
		if(StringUtils.isEmpty(accessToken)) {
			logger.warn("access_token is NULL or empty");
			return null;
		}
		if(StringUtils.isEmpty(tokenRevokeUri)) {
			logger.warn("token revoke uri is NULL or empty");
			return null;
		}
		
		List<NameValuePair> pairList = new ArrayList<>();
		pairList.add(new BasicNameValuePair("token", accessToken));
		pairList.add(new BasicNameValuePair("token_type_hint", "access_token"));		
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		//String uri = tokenCancelUri.replace("{key}", URLEncoder.encode(key, "UTF-8"));
		String uri = tokenRevokeUri.replace("{clientId}",clientId);
		System.out.println("uri.......:"+uri);
		logger.debug("取消授權記錄uri .................... {}", uri);
		HttpPost post = new HttpPost(uri);
		post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		post.addHeader("Authorization", "Basic "+basicAuthenticationSchema(clientId,clientSecret));
		
		HttpResponse response = httpClient.execute(post);
		Boolean result = null;
		
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
				|| response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
			logger.debug("以 access_token 取消授權記錄成功！");
			System.out.println("HttpStatus.......:"+response.getStatusLine().getStatusCode());
			result = true;
		}else {
			logger.warn("以 access_token 取消授權記錄失敗！  HttpStatus -> {}", response.getStatusLine().getStatusCode());
			System.out.println("HttpStatus.......:"+response.getStatusLine().getStatusCode());
			result = false;
		}
		
		return result;
	}
	
	/*
	 * 符合 HTTP Basic authentication schema 將 clientId:clientSecret 字串以Base64編碼。
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	private static String basicAuthenticationSchema(String clientId, String clientSecret) {
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
	
	
	/**
	 * @return the gspOpenIdConfigUri
	 */
	public String getGspOpenIdConfigUri() {
		return gspOpenIdConfigUri;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @return the redirectUri
	 */
	public String getRedirectUri() {
		return redirectUri;
	}

	/**
	 * @return the responseType
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * @return the responseMode
	 */
	public String getResponseMode() {
		return responseMode;
	}

	/**
	 * @return the logoutRedirectUri
	 */
	public String getLogoutRedirectUri() {
		return logoutRedirectUri;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		//GspOidcClient client = new GspOidcClient();
		//System.out.println(client.basicAuthenticationSchema("tygh.resource.prenatal","tygh.resource.prenatal::xKNWfTX8JZ"));
		//地政 地籍及實價資料
		System.out.println(basicAuthenticationSchema("API.KvvyRZSc5K","T7AeDNQOjRmRNbu6"));
		//地政 所有權人名下地籍資料
		System.out.println(basicAuthenticationSchema("API.WE8hJHljiN","scUVyoYaV7KOMhKA"));
		//System.out.println(client.basicAuthenticationSchema("moi.mydata.household.q201","RlLh9Ool2CU9zIoZ"));
		//System.out.println(client.basicAuthenticationSchema("moi.mydata.household.q201","tygh.resource.prenatal::xKNWfTX8JZ"));
		//For GSP login log
		//System.out.println(client.basicAuthenticationSchema("login.cp.gov.tw","login.cp.gov.tw::pib7E6SFN1"));
		//System.out.println(client.basicAuthenticationSchema("API.moi.mydata.household.q201","tygh.resource.prenatal::xKNWfTX8JZ"));
		//System.out.println(client.basicAuthenticationSchema("tygh.resource.prenatal-help","tygh.resource.prenatal-help::t9cTzmpcLg"));
		//ETC欠費總額查詢
		System.out.println(basicAuthenticationSchema("API.hAechdGCiP","vaJPsmo7sMmeWWz5"));
		//健保署 個人投退保資料
		System.out.println("-------健保署 個人投退保資料------");
		System.out.println(basicAuthenticationSchema("API.zH584wn59r","2zYZEPAwmvoz3188"));
		//健保署 保險費繳納紀錄
		System.out.println("-------健保署 保險費繳納紀錄------");
		System.out.println(basicAuthenticationSchema("API.1qIr0nM0BT","PL3DSwVE9tlnJvBq"));
		//戶政
		//個人戶籍資料查詢 ris_review_one
		System.out.println("-------個人戶籍資料查詢 ris_review_one------");
		System.out.println(basicAuthenticationSchema("API.7QovE2Gev6","DkPpy1ekM670zBtI"));
		//個人資料查驗 ris_check
		System.out.println(basicAuthenticationSchema("API.fS9bsX4web","ukYfRtHJeD5gT2Xk"));
		//現戶全戶戶籍資料 ris_review_all
		System.out.println(basicAuthenticationSchema("API.UDauDOLyZg","fsb3pF0bm6PbRf08"));
		System.out.println(basicAuthenticationSchema("API.Kr1C3b1ijJ","n0ckEd67HGqp6LsD"));
		System.out.println(basicAuthenticationSchema("API.NZwfiKZVdj","j5lrGd1h2FiaaNHb"));
		//mydata portal
		System.out.println(basicAuthenticationSchema("CLI.mydata.portal","mydata.portal::hh3H2UG4W1"));
		//內政部地政司
		System.out.println(basicAuthenticationSchema("API.WE8hJHljiN","scUVyoYaV7KOMhKA"));
		//經濟部商業司 公司股東董監事經理人
		System.out.println("-------經濟部商業司 公司股東董監事經理人 ris_review_one------");
		System.out.println(basicAuthenticationSchema("API.ea0klQl5Wp","eyB2rKE9FOw0sUKy"));
		//財政部財政資訊中心
		//財產資料
		System.out.println("-------經濟部商業司 財產資料------");
		System.out.println(basicAuthenticationSchema("API.Mo23SDWhsn","Ig2TxOdzfox5jxXS"));
		//核發使用牌照稅繳納證明
		System.out.println("-------經濟部商業司 核發使用牌照稅繳納證明------");
		System.out.println(basicAuthenticationSchema("API.wH2r0nBb3O","J7k9RVLaVZ2vvw9S"));
		//查詢車籍資料
		System.out.println("-------查詢車籍資料------");
		System.out.println(basicAuthenticationSchema("API.60GGfgGX1A","TJapW3eDC3VdySiO"));
		//查詢駕籍資料
		System.out.println("-------查詢駕籍資料------");
		System.out.println(basicAuthenticationSchema("API.n0c9qVZBQI","x5vmHQB1GemuA9JJ"));
		//違規資料查詢
		System.out.println("-------違規資料查詢------");
		System.out.println(basicAuthenticationSchema("API.e98PaKiAFn","L9Z1OtNDNftrXEnF"));
		//勞保
		System.out.println(basicAuthenticationSchema("API.UZQkKbsOpz","Kfuv8BmG2AObXBNk"));
		//低收入戶及中低收入戶證明(衛生福利部社會救助及社工司) API.AbAYGIjoYw jsR93sCv4WC3InIU
		System.out.println(basicAuthenticationSchema("API.AbAYGIjoYw","jsR93sCv4WC3InIU"));
		//戶政司 身分證影像
		System.out.println(basicAuthenticationSchema("API.idPhotoRev","idPhotoRev99zBtI"));
		//高級中等學校學生畢業資料
		System.out.println("-------高級中等學校學生畢業資料------");
		System.out.println(basicAuthenticationSchema("API.UbwT0ojLZ1","kq1cUxIgdjceHY0v"));
		System.out.println("-------個人居家檢疫/隔離資料查詢------");
		System.out.println(basicAuthenticationSchema("API.Qg2td6qw1a","rv3SeuMzrvjkpRMw"));
		System.out.println("-------存簿儲金交易明細(3個月內)------");
		System.out.println(basicAuthenticationSchema("API.1giQpSHPlI","sZ07sAULQpLBaB01"));
		System.out.println("-------天然氣家庭用戶用氣資料(縣竹苗地區)------");
		System.out.println(basicAuthenticationSchema("API.oGreGhX7UQ","hrp7eWPCDDCZEK1T"));
		System.out.println("-------再生能源發電設備同意備案資料------");
		System.out.println(basicAuthenticationSchema("API.zoJwabdfV0","NKOHqFbqg4SKSkrd"));
		System.out.println("-------再生能源發電設備登記資料------");
		System.out.println(basicAuthenticationSchema("API.gyO0pan9IQ","Nw3UJAHtQIOBwWED"));
		System.out.println("-------廢棄物清理專業技術人員證書資料------");
		System.out.println(basicAuthenticationSchema("API.yqPqkeBhjs","ht0L33ml3AHilSRe"));
		System.out.println("-------中低收入老人生活津貼資料------");
		System.out.println(basicAuthenticationSchema("API.ccuZXSsnGv","cjtfWvgVm9DbdJD0"));
		System.out.println("-------衛生福利部社會救助及社工司 社會工作師資格------");
		System.out.println(basicAuthenticationSchema("API.3QaRElWzZ0","rxhfRMkrywA3DySI"));
		System.out.println("-------衛生福利部社會救助及社工司 身心障礙資格------");
		System.out.println(basicAuthenticationSchema("API.nyhnLM1fve","q5sX4RNzBG0fLsgw"));
	}
}
