package tw.gov.ndc.emsg.mydata.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.util.SslUtils;

import tw.gov.ndc.emsg.mydata.entity.MIDClause;
import tw.gov.ndc.emsg.mydata.gspclient.OidcGrantType;
import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenEntity;

@Component
public class TWCAUtils {
	@Value("${twca.atmLoginUrl}")
	private String atmLoginUrl;
	
	@Value("${twca.queryVerifyResultUrl}")
	private String queryVerifyResultUrl;
	
	@Value("${twca.atmVerifyInvokeUrl}")
	private String atmVerifyInvokeUrl;
	
	@Value("${twca.serverSideQueryUrl}")
	private String serverSideQueryUrl;
	
	@Value("${twca.MIDClauseUrl}")
	private String midClauseUrl;
	/**
	 * TWCA系統參數
	 */
	@Value("${twca.BusinessNo}")
	public String BusinessNo;

	@Value("${twca.ApiVersion}")
	public String ApiVersion;

	@Value("${twca.HashKeyNo}")
	public String HashKeyNo;
	
	@Value("${twca.HashKey}")
	public String HashKey;
	
	@Value("${twcaProxyURL}")
	public String twcaProxyURL;
	
	/**
	 * TWCA第三方認證中心API
	 * signatureVerifyAPIUrlTwid
	 */
	@Value("${signatureVerifyAPIUrlTwid}")
	public String signatureVerifyAPIUrlTwid;

	/**
	 * TWCA第三方認證中心API
	 * signatureVerifyAPIUrlCht
	 */
	@Value("${signatureVerifyAPIUrlCht}")
	public String signatureVerifyAPIUrlCht;
	
	private String download_client_id = "CLI.wwSSxxEEdd";
	private static String twid_secret = "KvloHv1DS3lMe53nKvloHv1DS3lMe53n";
	private static String twid_iv = "cFJ0feEe4icTRx9P";
	
	private static String cht_secret = "a6c84309914945cebcbd96a772f9b7d5";
	private static String cht_iv = "9d2e8310c4e04b2f";
	
	
//	public String BusinessNo = "03723402";
//	public String ApiVersion="1.0";
//	public String HashKeyNo="10";
//	public String HashKey="trgfdyh6fuk6";
//	public String serverSideQueryUrl = "https://twiddemo.twca.com.tw/MyDataIDPortal/ServerSideQuery";
	/**
	 * ATM Login TWCA晶片金融卡登入
	 * @param MemberNo
	 * @param VerifyNo
	 * @return
	 * @throws Exception
	 */
	public String postAtmLogin(String MemberNo,String VerifyNo) throws Exception {
		String token = null;
		List<NameValuePair> pairList = new ArrayList<>();
		//System.out.println("VerifyNo="+VerifyNo);
		String ReturnParams = "";
		
		//JSON STRING
		String InputParams = "";
		String Action = "SIGN,CERT";
		String Plaintext = MemberNo;
		String CAType = "6";
		//String AssignCertPassword ="";
		String ErrCodeType = "1";
		InputParams = "{\"MemberNo\":\""+ MemberNo+"\",\"Action\":\""+Action+"\",\"Plaintext\":\""+Plaintext+"\",\"CAType\":\""+CAType+"\",\"AssignCertPassword\":\"\",\"ErrCodeType\":"+ ErrCodeType +"}";
		//System.out.println("InputParams=\n"+InputParams);
		pairList.add(new BasicNameValuePair("BusinessNo", BusinessNo));
		pairList.add(new BasicNameValuePair("ApiVersion", ApiVersion));
		pairList.add(new BasicNameValuePair("HashKeyNo",HashKeyNo));
		pairList.add(new BasicNameValuePair("Hashkey", HashKey));
		pairList.add(new BasicNameValuePair("VerifyNo", VerifyNo));
		pairList.add(new BasicNameValuePair("ReturnURL", "MyDataReturn.jsp"));
		pairList.add(new BasicNameValuePair("ReturnParams", ReturnParams));
		List<String> params = new ArrayList<String>();
		params.add(BusinessNo);
		params.add(ApiVersion);
		params.add(HashKeyNo);
		params.add(VerifyNo);
		params.add(ReturnParams);
		params.add(InputParams);
		params.add(HashKey);
		pairList.add(new BasicNameValuePair("IdentifyNo", Identify(params)));
		pairList.add(new BasicNameValuePair("InputParams", InputParams));
		CloseableHttpClient httpClient = createSSLClientDefault();
		
		HttpPost post = new HttpPost(atmLoginUrl);
		try {
			post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			//System.out.println(responseString);
			JSONObject json = new JSONObject(responseString);
			//System.out.println("JSON OutputParams: "+json.get("OutputParams"));
			JSONObject OutputParamsObj = new JSONObject(json.get("OutputParams").toString());
			//System.out.println("JSON Token: "+OutputParamsObj.get("Token"));
			token = OutputParamsObj.get("Token").toString();
		}else {
			System.out.print("以 postAtmLogin 請求 token 失敗！");
		}
		return token;
	}
	
	/**
	 * TWCA ATM金融卡流程，初始步驟需先進行登入，參數IdentifyNo運算（送與TWCA JS mydataTWCALib插件）
	 * @param VerifyNo
	 * @param Token
	 * @return
	 */
	public String countIdentifyNoForATMInvoke(String VerifyNo,String Token) {
		String IdentifyNo = "";
		List<String> params = new ArrayList<String>();
		params.add(BusinessNo);
		params.add(ApiVersion);
		params.add(HashKeyNo);
		params.add(VerifyNo);
		params.add(Token);
		params.add(HashKey);
		try {
			IdentifyNo = Identify(params);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return IdentifyNo;
	}
	
	/**
	 * ATM QueryVerifyResult，TWCA ATM金融卡流程，驗證帶回Token，確認Token是否有效
	 * @throws Exception 
	 */
	public boolean postQueryVerifyResult(String MemberNo,String VerifyNo,String Token) throws Exception {
		boolean ret = false;
		List<NameValuePair> pairList = new ArrayList<>();
		//System.out.println("VerifyNo="+VerifyNo);
		pairList.add(new BasicNameValuePair("BusinessNo", BusinessNo));
		pairList.add(new BasicNameValuePair("ApiVersion", ApiVersion));
		pairList.add(new BasicNameValuePair("HashKeyNo",HashKeyNo));
		pairList.add(new BasicNameValuePair("VerifyNo", VerifyNo));
		pairList.add(new BasicNameValuePair("MemberNo", MemberNo));
		pairList.add(new BasicNameValuePair("Token", Token));
		List<String> params = new ArrayList<String>();
		params.add(BusinessNo);
		params.add(ApiVersion);
		params.add(HashKeyNo);
		params.add(VerifyNo);
		params.add(MemberNo);
		params.add(Token);
		params.add(HashKey);
		pairList.add(new BasicNameValuePair("IdentifyNo", Identify(params)));
		CloseableHttpClient httpClient = createSSLClientDefault();
		
		HttpPost post = new HttpPost(queryVerifyResultUrl);
		try {
			post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			//System.out.print(responseString);
			JSONObject json = new JSONObject(responseString);
			if(json.get("ReturnCode").toString().equalsIgnoreCase("0")) {
				ret =true;
			}
		}else {
			System.out.print("以 postAtmLogin 請求 token 失敗！");
		}
		return ret;
	}
	
	/**
	 * TWCA第三方認證中心
	 * @param clientId (空值時使用 client_id= "CLI.wwSSxxEEdd" twid_iv = "cFJ0feEe4icTRx9P")
	 * @param txId
	 * @param pid
	 * @param holder
	 * @param asOptional
	 * @param verificationType
	 * @param pkcs7Str
	 * @return
	 * @throws Exception
	 */
	public boolean postSignatureVerifyAPIUrlTwid(String clientId,String txId,String pid,String holder,String asOptional,String verificationType,String pkcs7Str) throws Exception {
		boolean ret = false;
		/**
		 * 第三方身分驗證中心之驗證API 進行檢查，驗證失敗丟401
		 * AES/CBC/PADDING5 
		 * download 特定 client_id = CLI.wwSSxxEEdd
		 * header.encrypted_key.initialization_vector.ciphertext.authentication_tag
		 * ciphertext
		 * {
		 * 	"tx_id": ${tx_id},
		 * 	"data": {
		 * 	"pid": "${身分證字號}",
		 * 	"holder": "${姓名}",
		 * 	"as_optional":"${晶片卡押碼流水號}",
		 *  "verification_type":"${verification_type}"
		 * 	},
		 * 	"pkcs7": ${base64_encoded_pkcs7file-data}
		 * }
		 */
		if(clientId==null) {
			clientId = download_client_id;
		}
		if(txId==null) {
			txId = UUID.randomUUID().toString();
		}
		if(holder==null) {
			holder = "";
		}
		if(asOptional==null) {
			asOptional = "";
		}
        String twidsignatureVerifyUri = signatureVerifyAPIUrlTwid + clientId;
        int respCode = 0;
        System.out.println("== twidsignatureVerifyUri ==:"+twidsignatureVerifyUri);
		String dataStr = "{\"pid\":\""+pid+"\",\"holder\":\""+holder+"\",\"as_optional\":\""+asOptional+"\",\"verification_type\":\""+verificationType+"\"}";

		String payload = "{\"tx_id\":\""+ txId +"\",\"data\":"+ dataStr +",\"pkcs7\":\""+pkcs7Str+"\"}";
		System.out.println("== payload ==:"+payload);
		// jwe
		String jweStr = JWEUtil.encrypt(payload, twid_secret, twid_iv.getBytes());
        //send twid api to check
		SslUtils.ignoreSsl();
		URL connectto = new URL(twidsignatureVerifyUri);
		HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
		if(conn!=null) {
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(false);
			conn.setDoOutput(true);
			conn.setConnectTimeout(15000);
			OutputStream os = null;
			try {
				os = conn.getOutputStream();
				os.write(jweStr.getBytes("UTF-8"));
				os.close();				
			}finally {
				if(os!=null) {
					HttpClientHelper.safeClose(os);
				}
			}
			respCode = conn.getResponseCode();
			System.out.println("== twidsignatureVerifyUri respCode ==:"+respCode);
			if(respCode==200) {
				System.out.println("== respCode is 200 ==:");
				StringBuilder sb = new StringBuilder();
				InputStream inputStream = null; 
				BufferedReader br = null;
				try {
					inputStream = conn.getInputStream();
					br = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();	
					inputStream.close();
				}finally {
					if(inputStream!=null) {
						HttpClientHelper.safeClose(inputStream);
					}
					if(br!=null) {
						HttpClientHelper.safeClose(br);
					}
				}
				System.out.println("== twidsignatureVerifyUri sb ==:"+sb.toString());
		        try {
					System.out.println("== twidsignatureVerifyUri resp sb.toString() ==:"+sb.toString());
					ObjectMapper om = new ObjectMapper();
					JsonNode respData = om.readTree(sb.toString());
					String verified = respData.get("verified")==null?"false":respData.get("verified").asText();
					if(!verified.equalsIgnoreCase("true")) {
						ret = false;
					}else {
						ret = true;
					}
		        } catch (Exception ex) {
		        	ret = false;
			    }
				
			}else {
				System.out.println("== respCode != 200 ==:");
				ret = false;
			}			
		}
		return ret;
	}
	
	/**
	 * CHT第三方認證中心
	 * @param clientId (空值時使用 client_id= "CLI.wwSSxxEEdd" twid_iv = "cFJ0feEe4icTRx9P")
	 * @param txId
	 * @param pid
	 * @param holder
	 * @param asOptional
	 * @param verificationType
	 * @param pkcs7Str
	 * @return
	 * @throws Exception
	 */
	public boolean postSignatureVerifyAPIUrlCht(String clientId,String txId,String pid,String holder,String asOptional,String verificationType,String pkcs7Str) throws Exception {
		boolean ret = false;
		/**
		 * 第三方身分驗證中心之驗證API 進行檢查，驗證失敗丟401
		 * AES/CBC/PADDING5 
		 * download 特定 client_id = CLI.wwSSxxEEdd
		 * header.encrypted_key.initialization_vector.ciphertext.authentication_tag
		 * ciphertext
		 * {
		 * 	"tx_id": ${tx_id},
		 * 	"data": {
		 * 	"pid": "${身分證字號}",
		 * 	"holder": "${姓名}",
		 * 	"as_optional":"${晶片卡押碼流水號}",
		 *  "verification_type":"${verification_type}"
		 * 	},
		 * 	"pkcs7": ${base64_encoded_pkcs7file-data}
		 * }
		 */
		if(clientId==null) {
			clientId = download_client_id;
		}
		if(txId==null) {
			txId = UUID.randomUUID().toString();
		}
		if(holder==null) {
			holder = "";
		}
		if(asOptional==null) {
			asOptional = "";
		}
        String chtSignatureVerifyUri = signatureVerifyAPIUrlCht + clientId;
        int respCode = 0;
        System.out.println("== signatureVerifyUri ==:"+chtSignatureVerifyUri);
		String dataStr = "{\"pid\":\""+pid+"\",\"holder\":\""+holder+"\",\"as_optional\":\""+asOptional+"\",\"verification_type\":\""+verificationType+"\"}";

		String payload = "{\"tx_id\":\""+ txId +"\",\"data\":"+ dataStr +",\"pkcs7\":\""+pkcs7Str+"\"}";
		System.out.println("== payload ==:"+payload);
		// jwe
		String jweStr = JWEUtil.encrypt(payload, cht_secret, cht_iv.getBytes());
        //send twid api to check
		SslUtils.ignoreSsl();
		URL connectto = new URL(chtSignatureVerifyUri);
		HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
		if(conn!=null) {
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(false);
			conn.setDoOutput(true);
			conn.setConnectTimeout(15000);
			OutputStream os = null;
			try {
				os = conn.getOutputStream();
				os.write(jweStr.getBytes("UTF-8"));
				os.close();		
			}finally {
				if(os!=null) {
					HttpClientHelper.safeClose(os);
				}
			}
			respCode = conn.getResponseCode();
			System.out.println("== chtSignatureVerifyUri respCode ==:"+respCode);
			if(respCode==200) {
				System.out.println("== respCode is 200 ==:");
				StringBuilder sb = new StringBuilder();
				InputStream inputStream = null; 
				BufferedReader br = null;
				try {
					inputStream = conn.getInputStream();
					br = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();	
					inputStream.close();
				}finally {
					if(inputStream!=null) {
						HttpClientHelper.safeClose(inputStream);
					}
					if(br!=null) {
						HttpClientHelper.safeClose(br);
					}
				}
				System.out.println("== chtSignatureVerifyUri sb ==:"+sb.toString());
		        try {
					System.out.println("== chtSignatureVerifyUri resp sb.toString() ==:"+sb.toString());
					ObjectMapper om = new ObjectMapper();
					JsonNode respData = om.readTree(sb.toString());
					String verified = respData.get("verified")==null?"false":respData.get("verified").asText();
					if(!verified.equalsIgnoreCase("true")) {
						ret = false;
					}else {
						ret = true;
					}
		        } catch (Exception ex) {
		        	ret = false;
			    }
				
			}else {
				System.out.println("== respCode != 200 ==:");
				ret = false;
			}
		}
		
		return ret;
	}	
	
	public MIDClause getMidClauseUrl() throws Exception {
		MIDClause ret = new MIDClause();
		//midClauseUrl = "https://twiddemo.twca.com.tw/MyDataIDPortal/MIDClause";
		int respCode = 0;
		SslUtils.ignoreSsl();
		URL connectto = new URL(midClauseUrl);
		HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
		MIDClause bean = new MIDClause();
		if(conn!=null) {
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(false);
			conn.setDoOutput(true);
			conn.setConnectTimeout(15000);
			respCode = conn.getResponseCode();
			if(respCode==200) {
				System.out.println("== respCode is 200 ==:");
				StringBuilder sb = new StringBuilder();
				InputStream inputStream = null; 
				BufferedReader br = null;
				try {
					inputStream = conn.getInputStream();
					br = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();	
					inputStream.close();
				}finally {
					if(inputStream!=null) {
						HttpClientHelper.safeClose(inputStream);
					}
					if(br!=null) {
						HttpClientHelper.safeClose(br);
					}
				}
				System.out.println("== midClauseUrl sb ==:"+sb.toString());
		        try {
					System.out.println("== chtSignatureVerifyUri resp sb.toString() ==:"+sb.toString());
					ObjectMapper om = new ObjectMapper();
					JsonNode respData = om.readTree(sb.toString());
					Class<?> clazz = bean.getClass();
					for (Field field : clazz.getDeclaredFields()) {
						if(field.getName().equalsIgnoreCase("html")) {
							field.set(bean, respData.get(field.getName()).toString().replaceAll("\\\"", "").trim().replaceAll("\\\\r\\\\n", "").replaceAll("\\\\t", ""));
						}else {
							field.set(bean, respData.get(field.getName()).toString().replaceAll("\\\"", "").trim());
						}
					}
		        } catch (Exception ex) {
		        	System.out.println("== midClauseUrl Exception ==:"+ex);
			    }
			}
		}
		return bean;
	}
	
	/**
	 * IdentifyNo 運算程式
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String Identify(List<String> params) throws UnsupportedEncodingException {
		String identityStr ="";
		if(params!=null&&params.size()>0) {
			for(String s:params) {
				identityStr = identityStr + s;
			}
		}
		byte[] _inputByte = identityStr.getBytes("UTF-16LE");
		HashCode _hashByte = Hashing.sha256().hashBytes(_inputByte);
		String IdentifyNo = _hashByte.toString().toLowerCase(Locale.ENGLISH);
		return IdentifyNo;
	}
	
	/**
	 * 確認身分字號和手機是否本人
	 * @param MemberNo 身分證字號
	 * @param VerifyNo 驗證交易流水號
	 * @param Msisdn 手機門號
	 * @param Birthday 生日
	 * @param ClauseVer 同意的條款版本
	 * @param ClauseTime 同意條款時間（2021-02-26T10:00:00Z）
	 * @return
	 * @throws Exception
	 */
	public String postServerSideQuery(String MemberNo,String VerifyNo,String Msisdn,String Birthday,String ClauseVer,String ClauseTime) throws Exception {
		String code = null;
		List<NameValuePair> pairList = new ArrayList<>();
		//JSON STRING
		String InputParams = "";
		String Action = "ValidateMSISDNAdvance";
		String MIDInputParams = "{\"Msisdn\":\""+Msisdn+"\",\"Birthday\":\""+ Birthday +"\",\"ClauseVer\":\""+ ClauseVer +"\",\"ClauseTime\":\""+ClauseTime+"\"}";
		InputParams = "{\"MemberNo\":\""+ MemberNo +"\",\"Action\":\""+Action+"\",\"MIDInputParams\":"+MIDInputParams+"}";
		System.out.println("InputParams=\n"+InputParams);
		pairList.add(new BasicNameValuePair("BusinessNo", BusinessNo));
		pairList.add(new BasicNameValuePair("ApiVersion", ApiVersion));
		pairList.add(new BasicNameValuePair("HashKeyNo",HashKeyNo));
		pairList.add(new BasicNameValuePair("VerifyNo",VerifyNo));
		List<String> params = new ArrayList<String>();
		params.add(BusinessNo);
		params.add(ApiVersion);
		params.add(HashKeyNo);
		params.add(VerifyNo);
		params.add(InputParams);
		params.add(HashKey);
		pairList.add(new BasicNameValuePair("IdentifyNo", Identify(params)));
		pairList.add(new BasicNameValuePair("InputParams", InputParams));
		CloseableHttpClient httpClient = createSSLClientDefault();
		System.out.println("twca.serverSideQueryUrl="+serverSideQueryUrl);
		HttpPost post = new HttpPost(serverSideQueryUrl);
		try {
			post.setEntity(new StringEntity(URLEncodedUtils.format(pairList, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		HttpResponse response = httpClient.execute(post);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println(responseString);
			JSONObject json = new JSONObject(responseString);
			code = json.get("ResultCode").toString();
			System.out.println(code);
		}else {
			System.out.print("身分字號和手機驗證失敗！");
		}
		return code;
	}	
	
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有證書
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslFactory).build();
        } catch (Exception e) {
        	System.out.print("https證書異常\n" + e);
        }
        return HttpClients.createDefault();
    }	
	
	public static void main(String[] args) throws Exception {
		//String MemberNo = "H120983053";
		//String VerifyNo = UUID.randomUUID().toString().replaceAll("-", "");
		//TWCAUtils uitl = new TWCAUtils();
		//String token = uitl.postAtmLogin(MemberNo,VerifyNo);
		//uitl.postAtmVerifyInvoke();
		//uitl.postQueryVerifyResult();
		//String payload = "{\"tx_id\":\"c9522174-2d48-4361-a63e-aecf8b25ba23\",\"data\":{\"pid\":\"H120983053\",\"holder\":\"\",\"as_optional\":\"0000003\",\"verification_type\":\"FIC\"},\"pkcs7\":\"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\"}";
		//String jweStr = JWEUtil.encrypt(payload, twid_secret, twid_iv.getBytes());
		//System.out.println(jweStr);
		
//		String MemberNo = "H120983053";
//		String Msisdn = "0919310751";
//		String Birthday = "19700909";
//		String ClauseVer = "1090602";
//		String ClauseTime = "2021-03-12T02:06:15Z";
//		String VerifyNo = UUID.randomUUID().toString().replaceAll("-", "");
//		TWCAUtils uitl = new TWCAUtils();
//		String token = uitl.postServerSideQuery(MemberNo,VerifyNo,Msisdn,Birthday,ClauseVer,ClauseTime);
//		System.out.println(token);
		TWCAUtils uitl = new TWCAUtils();
		MIDClause clause = uitl.getMidClauseUrl();
		System.out.println("==html==\n"+clause.getHtml());
		System.out.println("==html==\n"+clause.getClausever());
		System.out.println("==html==\n"+clause.getRspTime());
	}
}
