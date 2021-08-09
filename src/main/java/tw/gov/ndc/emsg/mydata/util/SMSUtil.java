package tw.gov.ndc.emsg.mydata.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.util.SslUtils;

public class SMSUtil {
	private static final Logger logger = LoggerFactory.getLogger(SMSUtil.class);

	private static String smsapiuri ="https://smsapi.mitake.com.tw/api/mtk/SmSend?username=80688543A&password=2020!MyData";
	private static final int BUFFER_SIZE = 4096;
	public static void sendSms(String dstaddr, String smbody) throws Exception {
		dstaddr = ValidatorHelper.removeSpecialCharacters(dstaddr);
		smbody = ValidatorHelper.removeSpecialCharacters(smbody);
		SslUtils.ignoreSsl();
		int httpstatuscode = 0;
		String clientid = UUID.randomUUID().toString();
		System.out.print(smbody);
		String smbodyBig5UrlEncodeStr = URLEncoder.encode(smbody, "BIG5");
		System.out.print(smbodyBig5UrlEncodeStr);
		String sendurl = smsapiuri+"&dstaddr="+dstaddr+"&smbody="+smbodyBig5UrlEncodeStr+"&clientid="+clientid;
		logger.info("[{}] ===get sendurl===: {}", dstaddr, sendurl);
		URL connectto = new URL(sendurl);
		HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
		if(conn!=null) {
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setDoOutput(true);
			httpstatuscode = conn.getResponseCode();
			
			//logger.info("[{}] ===get httpstatuscode===: {}", dstaddr, httpstatuscode);
			if(httpstatuscode == HttpStatus.OK.value()) {
				//成功 200
				InputStream inputStream = null;
				BufferedReader reader = null;
				try {
					inputStream = conn.getInputStream();
					
					if(inputStream!=null) {
						reader = new BufferedReader(new InputStreamReader(inputStream));
						StringBuilder result = new StringBuilder();
						String line;
						if(reader!=null) {
							while((line = reader.readLine()) != null) {
							    result.append(line);
							}	
						}
						logger.info("[{}] result success= {}", dstaddr, ValidatorHelper.removeSpecialCharacters(result.toString()));
					}	
				}finally {
					if(inputStream!=null) {
						HttpClientHelper.safeClose(inputStream);
					}
					if(reader!=null) {
						HttpClientHelper.safeClose(reader);
					}
				}
			}else {
				logger.info("[{}] result failed", dstaddr);
			}	
		}
	}
	public static void main(String[] args) throws Exception {
		SMSUtil.sendSms("0978962977","程式中文測試");
	}

}
