package pressure_test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.util.SslUtils;
import org.apache.commons.text.StrSubstitutor;
import org.springframework.http.HttpStatus;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class pressureThread132 extends Thread {
	private static final int BUFFER_SIZE = 4096;
	private String authorization = null;
	public void run() {
		String filePath = "/Users/chenjiawei/Desktop/Work/MyData/test";
		String rUrl = "https://edesk.bli.gov.tw/na/ws/mydata/inqLIBf";
		String headerStr = "{\"Content-Type\":\"application/zip\",\"Authorization\": \"Bearer ${accessToken}\",\"transaction_uid\": \"${transactionUid}\"}";
		String randomId = SequenceHelper.createUUID();

		Map<String, Object> mapper = new HashMap<>();
		mapper.put("accessToken", authorization);
		mapper.put("transactionUid", UUID.randomUUID());
		StrSubstitutor strSub = new StrSubstitutor(mapper);
		headerStr = strSub.replace(headerStr);
		System.out.println(headerStr);
		
		String filename = filePath + "/" + authorization + "/" +randomId +".zip";
		File file = new File(filename);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		ObjectMapper om = new ObjectMapper();
		int loop = 1;
		int delay = 1;
		
		int httpstatuscode = 0;
		/**
		 * SSL disable
		 */
		try {
			SslUtils.ignoreSsl();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// post
		URL connectto = null;
		try {
			connectto = new URL(rUrl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpsURLConnection conn = null;
		try {
			conn = (HttpsURLConnection) connectto.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			conn.setRequestMethod("POST");
		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/**
		 * portalResource.getDataHeader()  header參數
		 */
		byte[] contentByteArray = null;
		try {
			contentByteArray = "{}".getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			JsonNode jsonNode = om.readTree(headerStr);
			Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();
			while (jsonNodes.hasNext()) {
				Entry<String, JsonNode> node = jsonNodes.next();
				conn.setRequestProperty(node.getKey(), node.getValue().asText());
			}
			System.out.println("contentByteArray.length="+contentByteArray.length);
			conn.setRequestProperty("Content-Length", String.valueOf(contentByteArray.length));
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		/**
		 * portalResource.getDataParam() body參數
		 */
		OutputStream os = null;
		try {
			os = conn.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.write(contentByteArray,0,contentByteArray.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 連線狀態
		 */
		try {
			httpstatuscode = conn.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("===post httpstatuscode===:"+httpstatuscode); 
		if(httpstatuscode == HttpStatus.OK.value()) {
			// 不需處理，直接下載 200 
			InputStream inputStream = null;
			try {
				inputStream = conn.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(filename);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			try {
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("File downloaded");
		}
		
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
}
