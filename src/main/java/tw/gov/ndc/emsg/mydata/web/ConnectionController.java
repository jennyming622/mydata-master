package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riease.common.enums.ActionEvent;
import com.riease.common.helper.HttpHelper;
import com.riease.common.helper.ValidatorHelper;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.sysinit.controller.BaseRestController;

import tw.gov.ndc.emsg.mydata.entity.AuthToken;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.OrganWhiteIpList;
import tw.gov.ndc.emsg.mydata.entity.OrganWhiteIpListExample;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceScope;
import tw.gov.ndc.emsg.mydata.gspclient.OidcGrantType;
import tw.gov.ndc.emsg.mydata.gspclient.bean.IntrospectEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;
import tw.gov.ndc.emsg.mydata.mapper.AuthTokenMapper;
import tw.gov.ndc.emsg.mydata.mapper.MemberMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.util.TokenUtils;
import tw.gov.ndc.emsg.mydata.util.UlogUtil;

@Controller
@RequestMapping("/connect")
public class ConnectionController extends BaseRestController{
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	@Autowired PortalResourceMapper portalResourceMapper;
	@Autowired PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired AuthTokenMapper authTokenMapper;
	@Autowired MemberMapper memberMapper;
	
	@Autowired TokenUtils tokenUtils;
	
	@Value("${app.frontend.context.url}") 
	private String frontendContextUrl;
	@Value("${gsp.oidc.token.asid}")
	private String asId;	
	@Autowired
	private UlogUtil ulogUtil;
	
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@PostMapping("/introspect")
	public ResponseEntity<Map<String,Object>> introspect(
			HttpServletRequest request,
			@RequestHeader("Authorization") String authorization,
			@RequestParam(name="token",required=true) String token) throws UnsupportedEncodingException {
		String ip = HttpHelper.getRemoteIp(request);
		/**
		 * 10.20.13.251 GSP 舊管道查詢
		 */
		List<String> gspIpList = new ArrayList<String>();
		//OIDC
		gspIpList.add("10.20.13.251");
		gspIpList.add("10.20.89.3");
		gspIpList.add("10.20.38.27");
		//localhost
//		gspIpList.add("0:0:0:0:0:0:0:1");
//		//IF2
//		gspIpList.add("10.20.13.16");
//		//IF1
//		gspIpList.add("10.20.13.17");
//		//IF3
//		gspIpList.add("10.20.13.23");
//		//DEV01-01
//		gspIpList.add("10.20.13.24");
		
		List<String> localhostIpList = new ArrayList<String>();
		localhostIpList.add("0:0:0:0:0:0:0:1");
		localhostIpList.add("127.0.0.1");
		
		Map<String,Object> data = new HashMap<String,Object>();
		
		/*if(frontendContextUrl.contentEquals("https://mydatadev.nat.gov.tw/mydata")) {
			if(!token.startsWith("mydatadev::")) {
				data.put("active", false);
				return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
			}
		}else if(frontendContextUrl.contentEquals("https://mydata.nat.gov.tw")) {
			if(!token.startsWith("mydata::")) {
				data.put("active", false);
				return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
			}
		}*/
		
		// 解析Authorization
		String credential = authorization.split(" ")[1];
		String resource = new String(new Base64().decode(credential), "UTF-8");
		String[] array = resource.split(":");
		String resource_id = array[0];
		String resource_secret = array[1];		
		
		if(asId.equalsIgnoreCase("mydata")) {
			if(token.startsWith("mydata::")) {
				// 取得scope
				Map<String,Object> prParam = new HashMap<>();
				prParam.put("resourceId", resource_id);
				prParam.put("resourceSecret", resource_secret);
				List<PortalResource> prList = portalResourceMapper.selectByExample(prParam);
				if(CollectionUtils.isEmpty(prList)) {
					data.put("active", false);
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}
				PortalResource pr = prList.get(0);
				
				Map<String,Object> prsParam = new HashMap<>();
				prsParam.put("prId", ValidatorHelper.limitNumber(pr.getPrId()));
				List<PortalResourceScope> prsList = portalResourceScopeMapper.selectByExample(prsParam);
				if(CollectionUtils.isEmpty(prsList)) {
					data.put("active", false);
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}
				List<String> scope = prsList.stream()
					    .map(PortalResourceScope::getScope)
					    .collect(Collectors.toList());
				
				String authToken = token.split("::")[1];
				Map<String,Object> aParam = new HashMap<>();
				aParam.put("token", authToken);
				aParam.put("intime", true);
				List<AuthToken> authList = authTokenMapper.selectByExample(aParam);
				if(CollectionUtils.isEmpty(authList)) {
					data.put("active", false);
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}
				AuthToken auth = authList.get(0);
				
				boolean active = false;
				for(String s : scope) {
					if(auth.getScope().contains(s)) {
						active = true;
						break;
					}
				}
				data.put("active", active);
				if(active) {
					if(!gspIpList.contains(ip)) {
						data.put("verification", auth.getVerification());
					}
					ulogUtil.recordFullByConn(resource_id, auth, ActionEvent.EVENT_260, HttpHelper.getRemoteIp(request));
				}
				return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
			}else if(token.startsWith("mydatadev::")){
				// 1. mydata
				IntrospectEntity entity = null;
				try {
					entity =  introspectAccessToken("https://mydatadev.nat.gov.tw/mydata/connect/introspect", token, resource_id, resource_secret);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 2. mydata-stage
				if(entity==null||entity.getActive()==false) {
					try {
						entity =  introspectAccessToken("https://mydatadev.nat.gov.tw/mydata-stage/connect/introspect", token, resource_id, resource_secret);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(entity==null||entity.getActive()==false) {
					data.put("active", false);
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}else{
					data.put("active", true);
					if(!gspIpList.contains(ip)) {
						data.put("verification", entity.getVerification());
					}
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}
			}else {
				data.put("active", false);
				return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
			}
		}else if(asId.equalsIgnoreCase("mydatadev")){
			if(token.startsWith("mydatadev::")) {
				// 1. mydata
				// 取得scope
				boolean checkFlag = true;
				Map<String,Object> prParam = new HashMap<>();
				prParam.put("resourceId", resource_id);
				prParam.put("resourceSecret", resource_secret);
				List<PortalResource> prList = portalResourceMapper.selectByExample(prParam);
				if(CollectionUtils.isEmpty(prList)) {
					data.put("active", false);
					checkFlag = false;
					//return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}
				List<PortalResourceScope> prsList = null;
				if(checkFlag) {
					PortalResource pr = prList.get(0);
					Map<String,Object> prsParam = new HashMap<>();
					prsParam.put("prId", ValidatorHelper.limitNumber(pr.getPrId()));
					prsList = portalResourceScopeMapper.selectByExample(prsParam);
					if(CollectionUtils.isEmpty(prsList)) {
						data.put("active", false);
						checkFlag = false;
						//return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
					}
				}
				boolean active = false;
				AuthToken auth = null;
				if(checkFlag) {
					List<String> scope = prsList.stream()
						    .map(PortalResourceScope::getScope)
						    .collect(Collectors.toList());
					
					String authToken = token.split("::")[1];
					Map<String,Object> aParam = new HashMap<>();
					aParam.put("token", authToken);
					aParam.put("intime", true);
					List<AuthToken> authList = authTokenMapper.selectByExample(aParam);
					if(CollectionUtils.isEmpty(authList)) {
						data.put("active", false);
						checkFlag = false;
						//return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
					}
					if(checkFlag) {
						auth = authList.get(0);
						for(String s : scope) {
							if(auth.getScope().contains(s)) {
								active = true;
								break;
							}
						}
					}
				}
				//return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				// 2. mydata-stage
				
				if(active) {
					data.put("active", active);
					if(active) {
						if(!gspIpList.contains(ip)) {
							data.put("verification", auth.getVerification());
						}
						ulogUtil.recordFullByConn(resource_id, auth, ActionEvent.EVENT_260, HttpHelper.getRemoteIp(request));
						return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
					}
				}else {
					if(!localhostIpList.contains(ip)) {
						if(frontendContextUrl.contentEquals("https://mydatadev.nat.gov.tw/mydata-stage")) {
							IntrospectEntity entity = null;
							if(entity==null||entity.getActive()==false) {
								try {
									entity =  introspectAccessToken("https://mydatadev.nat.gov.tw/mydata/connect/introspect", token, resource_id, resource_secret);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							if(entity==null||entity.getActive()==false) {
								data.put("active", false);
								return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
							}else{
								data.put("active", true);
								if(!gspIpList.contains(ip)) {
									data.put("verification", entity.getVerification());
								}
								return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
							}
						}else{
							IntrospectEntity entity = null;
							if(entity==null||entity.getActive()==false) {
								try {
									entity =  introspectAccessToken("https://mydatadev.nat.gov.tw/mydata-stage/connect/introspect", token, resource_id, resource_secret);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							if(entity==null||entity.getActive()==false) {
								data.put("active", false);
								return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
							}else{
								data.put("active", true);
								if(!gspIpList.contains(ip)) {
									data.put("verification", entity.getVerification());
								}
								return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
							}
						}
					}else {
						data.put("active", false);
						return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
					}
				}
			}else if(token.startsWith("mydata::")){
				// 1. ROOT
				IntrospectEntity entity = null;
				if(entity==null||entity.getActive()==false) {
					try {
						entity =  introspectAccessToken("https://mydata.nat.gov.tw/mydata-stage/connect/introspect", token, resource_id, resource_secret);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(entity==null||entity.getActive()==false) {
					data.put("active", false);
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}else{
					data.put("active", true);
					if(!gspIpList.contains(ip)) {
						data.put("verification", entity.getVerification());
					}
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}
			}else {
				data.put("active", false);
				return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
			}
		}else {
			data.put("active", false);
			return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
		}
		data.put("active", false);
		return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
	}
	
	@GetMapping("/userinfo")
	public ResponseEntity<Map<String,Object>> userinfo(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestHeader("Authorization") String authorization) {
		
		String ip = HttpHelper.getRemoteIp(request);
		List<String> localhostIpList = new ArrayList<String>();
		localhostIpList.add("0:0:0:0:0:0:0:1");
		localhostIpList.add("127.0.0.1");
		
		String token = authorization.split(" ")[1];
		/*if(frontendContextUrl.contentEquals("https://mydatadev.nat.gov.tw/mydata")) {
			if(!token.startsWith("mydatadev::")) {
				return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
			}
		}else if(frontendContextUrl.contentEquals("https://mydata.nat.gov.tw")) {
			if(!token.startsWith("mydata::")) {
				return new ResponseEntity<Map<String,Object>>(HttpStatus.BAD_REQUEST);
			}
		}*/
		String authToken = token.split("::")[1];
		if(asId.equalsIgnoreCase("mydata")) {
			if(token.startsWith("mydata::")) {
				Map<String,Object> aParam = new HashMap<>();
				aParam.put("token", authToken);
				aParam.put("intime", true);
				List<AuthToken> authList = authTokenMapper.selectByExample(aParam);
				if(CollectionUtils.isEmpty(authList)) {
					return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
				}
				AuthToken auth = authList.get(0);
				
				Map<String,Object> mParam = new HashMap<>();
				mParam.put("account", ValidatorHelper.removeSpecialCharacters(auth.getAccount()));
				List<Member> mList = memberMapper.selectByExample(mParam);
				if(CollectionUtils.isEmpty(mList)) {
					return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
				}
				Member m = mList.get(0);
				String uidVerified = "True";
				/*if(m.getUidVerified()!= null && m.getUidVerified()==true) {
					uidVerified = "True";
				}*/
				
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("sub", m.getId().toString());
				data.put("cn", m.getName());
				data.put("uid", m.getUid());
				data.put("uid_verified", uidVerified);
				data.put("birthdate", m.getBirthdate()==null?"":sdf.format(m.getBirthdate()));
				data.put("gender", m.getGender());
				data.put("email", m.getEmail());
				data.put("account", m.getAccount());
				if(m!=null&&m.getAccount()!=null) {
					ulogUtil.recordFullByConn(null, auth, ActionEvent.EVENT_270, HttpHelper.getRemoteIp(request));
					ulogUtil.recordFullByConn(null, auth, ActionEvent.EVENT_275, HttpHelper.getRemoteIp(request));
				}
				return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
			}else if(token.startsWith("mydatadev::")){
				//1.mydata
				UserInfoEntity infoEntity = null;
				try {
					infoEntity= requestUserInfo("https://mydatadev.nat.gov.tw/mydata/connect/userinfo",token);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//2.mydata-stage
				if(infoEntity==null||infoEntity.getAccount()==null) {
					try {
						infoEntity= requestUserInfo("https://mydatadev.nat.gov.tw/mydata-stage/connect/userinfo",token);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(infoEntity!=null&&infoEntity.getAccount()!=null) {
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("sub", infoEntity.getSub());
					data.put("cn", infoEntity.getName());
					data.put("uid", infoEntity.getUid());
					data.put("uid_verified", infoEntity.getIsValidUid());
					data.put("birthdate", infoEntity.getBirthdate()==null?"":infoEntity.getBirthdate());
					data.put("gender", infoEntity.getGender());
					data.put("email", infoEntity.getEmail());
					data.put("account", infoEntity.getAccount());
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}else {
					return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
				}
			}else {
				return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
			}
		}else if(asId.equalsIgnoreCase("mydatadev")){
			if(token.startsWith("mydatadev::")) {
				boolean checkFlag = true;
				Map<String,Object> aParam = new HashMap<>();
				aParam.put("token", authToken);
				aParam.put("intime", true);
				List<AuthToken> authList = authTokenMapper.selectByExample(aParam);
				if(CollectionUtils.isEmpty(authList)) {
					checkFlag = false;
					//return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
				}
				if(checkFlag) {
					AuthToken auth = authList.get(0);
					Map<String,Object> mParam = new HashMap<>();
					mParam.put("account", ValidatorHelper.removeSpecialCharacters(auth.getAccount()));
					List<Member> mList = memberMapper.selectByExample(mParam);
					if(CollectionUtils.isEmpty(mList)) {
						return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
					}
					Member m = mList.get(0);
					String uidVerified = "True";
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("sub", m.getId().toString());
					data.put("cn", m.getName());
					data.put("uid", m.getUid());
					data.put("uid_verified", uidVerified);
					data.put("birthdate", m.getBirthdate()==null?"":sdf.format(m.getBirthdate()));
					data.put("gender", m.getGender());
					data.put("email", m.getEmail());
					data.put("account", m.getAccount());
					if(m!=null&&m.getAccount()!=null) {
						ulogUtil.recordFullByConn(null, auth, ActionEvent.EVENT_270, HttpHelper.getRemoteIp(request));
						ulogUtil.recordFullByConn(null, auth, ActionEvent.EVENT_275, HttpHelper.getRemoteIp(request)); //dp完成身份驗證
						return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
					}else {
						return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
					}					
				}else {
					if(!localhostIpList.contains(ip)) {
						if(frontendContextUrl.contentEquals("https://mydatadev.nat.gov.tw/mydata-stage")) {
							//1.mydata-stage
							UserInfoEntity infoEntity = null;
							try {
								infoEntity= requestUserInfo("https://mydatadev.nat.gov.tw/mydata/connect/userinfo",token);
							} catch (IOException e) {
								e.printStackTrace();
							}
							if(infoEntity!=null&&infoEntity.getAccount()!=null) {
								Map<String,Object> data1 = new HashMap<String,Object>();
								data1.put("sub", infoEntity.getSub());
								data1.put("cn", infoEntity.getName());
								data1.put("uid", infoEntity.getUid());
								data1.put("uid_verified", infoEntity.getIsValidUid());
								data1.put("birthdate", infoEntity.getBirthdate()==null?"":infoEntity.getBirthdate());
								data1.put("gender", infoEntity.getGender());
								data1.put("email", infoEntity.getEmail());
								data1.put("account", infoEntity.getAccount());
								return new ResponseEntity<Map<String,Object>>(data1, HttpStatus.OK);
							}else {
								return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
							}
						}else {
							//1.mydata
							UserInfoEntity infoEntity = null;
							try {
								infoEntity= requestUserInfo("https://mydatadev.nat.gov.tw/mydata-stage/connect/userinfo",token);
							} catch (IOException e) {
								e.printStackTrace();
							}
							if(infoEntity!=null&&infoEntity.getAccount()!=null) {
								Map<String,Object> data1 = new HashMap<String,Object>();
								data1.put("sub", infoEntity.getSub());
								data1.put("cn", infoEntity.getName());
								data1.put("uid", infoEntity.getUid());
								data1.put("uid_verified", infoEntity.getIsValidUid());
								data1.put("birthdate", infoEntity.getBirthdate()==null?"":infoEntity.getBirthdate());
								data1.put("gender", infoEntity.getGender());
								data1.put("email", infoEntity.getEmail());
								data1.put("account", infoEntity.getAccount());
								return new ResponseEntity<Map<String,Object>>(data1, HttpStatus.OK);
							}else {
								return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
							}
						}						
					}else {
						return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
					}
				}
			}else if(token.startsWith("mydata::")) {
				//1.mydata
				UserInfoEntity infoEntity = null;
				try {
					infoEntity= requestUserInfo("https://mydata.nat.gov.tw/connect/userinfo",token);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(infoEntity!=null&&infoEntity.getAccount()!=null) {
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("sub", infoEntity.getSub());
					data.put("cn", infoEntity.getName());
					data.put("uid", infoEntity.getUid());
					data.put("uid_verified", infoEntity.getIsValidUid());
					data.put("birthdate", infoEntity.getBirthdate()==null?"":infoEntity.getBirthdate());
					data.put("gender", infoEntity.getGender());
					data.put("email", infoEntity.getEmail());
					data.put("account", infoEntity.getAccount());
					return new ResponseEntity<Map<String,Object>>(data, HttpStatus.OK);
				}else {
					return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
				}
			}else {
				return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
			}
		}else {
			return new ResponseEntity<Map<String,Object>>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	public IntrospectEntity introspectAccessToken(String endpoint, String accessToken, String clientId, String clientSecret) throws ClientProtocolException, IOException {
		
		List<NameValuePair> pairList = new ArrayList<>();
//		pairList.add(new BasicNameValuePair("client_id", getClientId()));
//		pairList.add(new BasicNameValuePair("client_secret", getClientSecret()));
		pairList.add(new BasicNameValuePair("token", accessToken));
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpPost post = new HttpPost(endpoint);
		post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		post.addHeader("Accept", "application/json");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		post.addHeader("Authorization", "Basic "+basicAuthenticationSchema(clientId,clientSecret));
		
		IntrospectEntity introspectEntity = null;
		
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
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
	 * 請求用戶資訊
	 * @param accessToken
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public UserInfoEntity requestUserInfo(String endpoint,String accessToken) throws ClientProtocolException, IOException {
		if(StringUtils.isEmpty(accessToken)) {
			logger.warn("access_token is NULL or empty");
			return null;
		}
		if(StringUtils.isEmpty(endpoint)) {
			logger.warn("user_info endpoint is NULL or empt");
			return null;
		}
		
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		
		HttpGet get = new HttpGet(endpoint);
		get.addHeader("Content-Type", "application/json");
		get.addHeader("Authorization", "Bearer "+accessToken);
		
		HttpResponse response = httpClient.execute(get);
		UserInfoEntity userInfo = null;
		
		if(response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
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
	
	/*
	 * 符合 HTTP Basic authentication schema 將 clientId:clientSecret 字串以Base64編碼。
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	private String basicAuthenticationSchema(String clientId, String clientSecret) {
		StringBuilder sb = new StringBuilder();
		sb.append(clientId).append(":").append(clientSecret);
		try {
			//return encoder.encodeToString(sb.toString().getBytes("UTF-8"));
			return java.util.Base64.getEncoder().encodeToString(sb.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}	
	
	public static void main(String args[]) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, EncoderException {
//		String text = "0900000000";
//		String encrypt = DbEncryptUtils.encryptAES(text);
//		String decrypt = DbEncryptUtils.decryptAES(encrypt);
//		System.out.println("encrypt = "+encrypt);
//		System.out.println("decrypt = "+decrypt);
		
//		String test = "API.7QovE2Gev6:DkPpy1ekM670zBtI";
//		final byte[] textByte = test.getBytes("UTF-8");
//		//編碼
//		final String encodedText = new Base64().encodeToString(textByte);
//		System.out.println(encodedText);
		
//		String token = "mydatadev::testToken";
//		System.out.println(token.split("::")[1]);
		
	}
}
