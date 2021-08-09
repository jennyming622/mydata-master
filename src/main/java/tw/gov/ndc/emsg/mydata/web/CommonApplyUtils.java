package tw.gov.ndc.emsg.mydata.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceScope;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceScopeExample;
import tw.gov.ndc.emsg.mydata.entity.UlogApi;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.mapper.UlogApiMapper;

public class CommonApplyUtils {
	
	// Mydata
	@Value("${gsp.oidc.client.id}")
	private String clientId;	
	
	@Autowired
	private PortalResourceMapper portalResourceMapper;	
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;	
	@Autowired
	private UlogApiMapper ulogApiMapper;
	
	/**
	 * 紀錄資料庫
	 * @param userInfoEntity 使用者資料
	 * @param portalResource 資料集
	 * @param auditEvent 授權活動類型
	 */
	private void recordUlogApi(UserInfoEntity userInfoEntity,PortalResource portalResource,Integer auditEvent) {
		/**
		 * 要求 insert ulog_api sp 授權狀態 
		 * 1.登入(AS) 
		 * 2.授權(AS) 
		 * 3.登出(AS) 
		 * 4. 要求(MyData) 
		 * 5. 傳送(DP) 
		 * 6.儲存(MyData) 
		 * 7.取消授權(AS) 
		 * 8.取用, 
		 * 11.申請(SP), 
		 * 12.申請完成(SP), 
		 * 13.收到(SP)
		 * 
		 * 資料集：
		 * 11 同意申請：您申請此筆資料/服務 
		 * 12 同意：您同意服務條款
		 * 13 驗證：您完成身分驗證
		 * 14 傳輸：您同意 MyData 傳送資料給服務提供者
		 * 15 自行儲存：您下載資料
		 * 16 臨櫃核驗-轉存：機關取用資料
		 * 17 線上服務：MyData 將資料傳送給服務提供者 or 
		 *    臨櫃核驗-轉存: 您完成提供資料給「嘉義縣 - 敬老愛心卡」
		 * 18 申請完成
		 * 19 申請失敗
		 * 20 申請失敗
		 * 
		 * 服務：
		 * 21 申請：您申請此筆資料/服務 
		 * 22 同意：您同意服務條款
		 * 23 驗證：您完成身分驗證
		 * 24 傳輸：您同意 MyData 傳送資料給服務提供者
		 * 25 儲存：您下載資料
		 * 26 條碼取用：機關取用資料
		 * 27 服務應用：MyData 將資料傳送給服務提供者
		 * 28 服務申請終止
		 * 29 服務申請終止
		 * 
		 * 31 臨櫃核驗-預覽 「臨櫃人原」使用「預覽」
		 * 32 臨櫃核驗-預覽 「臨櫃人原」使用「預覽」
		 * 
		 * 34 檔案刪除 
		 * 
		 * 委託人LOG
		 * 41 委託申請：您選擇此項臨櫃服務委託代辦
		 * 42 同意：您同意委託代辦項目
		 * 43 授權成功：已提供驗證碼給代辦人
		 * 44 授權失敗：您輸入的訊息，非 MyData 會員
		 * 45 委託終止：您終止此項委託代辦項目
		 * 46 委託逾時：已超過資料可取用時間
		 * 47 委託完成：代辦人已至臨櫃提供資料給「嘉義縣 - 敬老愛心卡」申辦完畢
		 * 
		 * 代辦人LOG
		 * 51 同意代辦：您同意項臨櫃服務代辦
		 * 52 委託終止：委託人已取消此代辦申請
		 * 53 委託逾時：已超過資料可取用時間
		 * 54 臨櫃核驗-預覽：您完成提供資料給臨櫃核驗機關
		 * 55 臨櫃核驗-轉存：您完成提供資料給「嘉義縣 - 敬老愛心卡」
		 */
		UlogApi record = new UlogApi();
		record.setProviderKey(ValidatorHelper.removeSpecialCharacters(userInfoEntity.getAccount()));
		record.setUserName(ValidatorHelper.removeSpecialCharacters(userInfoEntity.getName()));
		record.setUid(ValidatorHelper.removeSpecialCharacters(userInfoEntity.getUid()));
		record.setClientId(clientId);
		record.setAuditEvent(ValidatorHelper.limitNumber(auditEvent));
		record.setResourceId(ValidatorHelper.removeSpecialCharacters(portalResource.getResourceId()));
		String scopeStr = "";
		try {
			/*PortalResourceScopeExample portalResourceScopeExample = new PortalResourceScopeExample();
			portalResourceScopeExample.createCriteria().andPrIdEqualTo(portalResource.getPrId());*/
			Map<String,Object> sparam = new HashMap<String,Object>();
			sparam.put("prId", portalResource.getPrId());
			List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
			if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
				for (PortalResourceScope s : portalResourceScopeList) {
					if(s.getScope()!=null&&s.getScope().trim().length()>0) {
						scopeStr = scopeStr + s.getScope() + " ";
					}
				}
				scopeStr = scopeStr.trim();
			}
		} catch (Exception ex) {
		}
		record.setScope(ValidatorHelper.removeSpecialCharacters(scopeStr));
		record.setCtime(new Date());
		record.setIp("117.56.91.59");
		ulogApiMapper.insertSelective(record);
	}
	
	/**
	 * Google 我不是機器人驗證
	 * @param gRecaptchaResponse
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @throws IOException
	 * 
	 * Google 我不是機器人驗證，執行步驟說明
	 * POST https://www.google.com/recaptcha/api/siteverify 驗證 recaptcha 
	 * 1. ehousekeeper1@gmail.com 「login https://www.google.com/recaptcha/admin」 
	 * 2. site key: 6LcpMXEUAAAAAPPW-wxHaaVxz9pBWDydWDd8YGO3 Secret key: 6LcpMXEUAAAAAKO4FtywDt5QZYC7NQ3SuljfpOvp 
	 * 3. <script src='https://www.google.com/recaptcha/api.js'></script> 
	 * 4. <div class="g-recaptcha" data-sitekey="6LcpMXEUAAAAAPPW-wxHaaVxz9pBWDydWDd8YGO3"></div> 
	 * 5. this manager 驗證recaptcha
	 */
	public boolean googleRecaptcha(String gRecaptchaResponse) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		boolean success = false;
		System.out.println("g-recaptcha-response=" + gRecaptchaResponse);
		if(gRecaptchaResponse!=null&&gRecaptchaResponse.trim().length()>0) {
			String retStr = "";
			String tmpUrl = "https://www.google.com/recaptcha/api/siteverify";

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					X509Certificate[] certs = null;
					return certs;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}};

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

			// URL connectto = new URL(tmpUrl);
			URL connectto = new URL(null, tmpUrl, new sun.net.www.protocol.https.Handler());
			HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
			if(conn!=null) {
				conn.setRequestMethod("POST");
				/*
				 * conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				 * conn.setRequestProperty("Accept", "application/json");
				 */
				conn.setUseCaches(false);
				conn.setAllowUserInteraction(false);
				conn.setInstanceFollowRedirects(false);
				conn.setDoOutput(true);

				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("secret", "6Lc-G-IUAAAAAFXIwOe7W5IMpMdfWpe4PLkFpHPu"));
				params1.add(new BasicNameValuePair("response", gRecaptchaResponse));
				OutputStream os = null;
				BufferedWriter writer = null;
				try {
					os = conn.getOutputStream();
					writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					writer.write(getQuery(params1));
					writer.flush();
					writer.close();
					os.close();	
				}finally {
					if(writer!=null) {
						HttpClientHelper.safeClose(writer);
					}
					if(os!=null) {
						HttpClientHelper.safeClose(os);
					}
				}
				
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
				retStr = sb.toString();
				System.out.println("retStr=\n" + retStr);
				JSONObject json = new JSONObject(retStr);
				success = json.getBoolean("success");	
			}
		}
		return success;
	}
	
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}
		return result.toString();
	}	
}
