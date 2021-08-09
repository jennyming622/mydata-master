package tw.gov.ndc.emsg.mydata.util;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;

import org.apache.commons.ssl.PKCS8Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.microsoft.sqlserver.jdbc.StringUtils;
import com.riease.common.helper.HttpClientHelper;

import tw.com.chttl.ics.ICSCJApi;

@Component
public class IcscUtils {
	private static final Logger logger = LoggerFactory.getLogger(IcscUtils.class);
	
	String	szIP = "ics-moica.moi.gov.tw";
	int iPort = 443;
	String szCGIURL = "/cgi-bin/CheckSNPID/CheckSNPID";

	@Value("${icsc.client.cert}") 
	private String szClientCert;
	
	@Value("${icsc.client.key}") 
	private String szClientKey;
	
	@Value("${icsc.client.password}") 
	private String szPassword;
	
	/**
	 * 驗證憑證
	 * @param uid 身分證字號
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public boolean verifyCert(String uid, String serialNumber) throws Exception {
		if(StringUtils.isEmpty(szClientCert) || StringUtils.isEmpty(szClientKey) || StringUtils.isEmpty(szPassword)) {
			System.out.println("szClientCert or szClientKey or szPassword is empty!");
			return false;
		}
		
		ICSCJApi icscapi = new ICSCJApi();
		byte[] bSignature = null;
		int iRet = 0;
		
		//填入待驗之筆數
		icscapi.iCount = 1;

		//填入待驗之資料
		icscapi.IDSN = new String[icscapi.iCount][2];
		icscapi.IDSN[0][0] = serialNumber;		icscapi.IDSN[0][1] = uid;

		//呼叫 iMake_ToBeSignedData 擷取帶入憑證的姆指紋及先前資待驗資料產生待簽資料封包
		iRet = icscapi.iMake_ToBeSignedData(szClientCert);
		if (iRet != 0) {
			if (iRet == -1) System.out.println("讀取客戶端憑證檔失敗, 可能是找不到該檔或讀取失敗.");
			if (iRet == -2) System.out.println("無法產生憑證檔的拇指紋.");
			return false;
		}
		
		//對icscapi.ToBeSignedData進行簽章
		//Get PrivateKey
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(szClientKey);
			PrivateKey pk = null;
            if(szClientKey.indexOf(".jks") != -1){
            		//For PKCS12
			    KeyStore ks = KeyStore.getInstance("PKCS12");
				ks.load(fis, szPassword.toCharArray());
				Enumeration en = ks.aliases();

		        //抓取Alias
				String szAlias = (String) en.nextElement();

			    //取私密金鑰
			    pk = (PrivateKey) ks.getKey(szAlias, szPassword.toCharArray());
			}else{
            	//For PKCS#8 (use not-yet-commons-ssl-0.3.11.jar)
				PKCS8Key pkcs8 = new PKCS8Key(fis, szPassword.toCharArray());
				byte[] decrypted = pkcs8.getDecryptedBytes();
				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypted);
				if (pkcs8.isDSA()){
				    pk = KeyFactory.getInstance("DSA").generatePrivate(spec);
				}else if(pkcs8.isRSA()){
				    pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
				}

			    //取私密金鑰
				pk = pkcs8.getPrivateKey();
			}

			//Sign process
			Signature sigEngine = Signature.getInstance("SHA1withRSA");
			sigEngine.initSign(pk);
			sigEngine.update(icscapi.ToBeSignedData.getBytes());
			bSignature = sigEngine.sign();
		} catch(Exception ex) { 
			System.out.println("簽章發生錯誤. ("+ex.getMessage()+")"); 
			return false; 
		}finally {
			if(fis!=null) {
				HttpClientHelper.safeClose(fis);
			}
		}
		
		//發送Query並接收結果
		//iRet = icscapi.iQuery(szIP, iPort, szCGIURL, bSignature);
		boolean doFlag = true;
		IcscQueryThread thread = new IcscQueryThread(icscapi,szIP, iPort, szCGIURL, bSignature);
		thread.start();
		int count = 0;
		while(doFlag) {
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count += 1;
			if(thread.getCode()!=null) {
				iRet = thread.getiRet();
				doFlag = false;
			}
			if(count>=160) {
				iRet = -1;
				doFlag = false;
				System.out.println("API函式 iQuery 回傳值: null, connection timeout 15 sec");
			}
		}
		System.out.println("API函式 iQuery 回傳值: " + Integer.toString(iRet));
		
		if (iRet == 0) {
			int result = icscapi.iQueryResult[0];
			System.out.println("憑證序號: "+icscapi.IDSN[0][0]+"   身分證號: "+ icscapi.IDSN[0][1] + "    查詢結果 ---> "+ result);
			System.out.println("------------------------------------------------------------------");
			System.out.println("查詢結果解說\n "
					+ "0: 表示此身分證字號，有對應的已經開卡憑證(OK)\n "
					+ "1: 表示此身分證字號，沒有對應此SerialNumber的憑證 (NOT OK)\n "
					+ "2: 表示此身分證字號，只有未開卡的對應憑證 (NOT OK)\n "
					+ "999: 查詢紀錄時,資料庫端錯誤 (該筆資料必須再送Server查詢一次)");
			if(result < 0) {
				throw new Exception("System error");
			}

			if(result != 0) {
				return false;
			}else {
				return true;
			}
		} else {
			return false;
		}
	}

}
