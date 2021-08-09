/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.gov.ndc.emsg.mydata.gspclient.bean.NonceEntity;

/**
 * nonce字串helper物件
 * 格式：
 * @author wesleyzhuang
 *
 */
@Component
public class NonceHelper {

	private final Base64.Encoder encoder = Base64.getEncoder();
	private final Base64.Decoder decoder = Base64.getDecoder();
	
	private String sessionIdOrToken;
	private Date timestamp;
	
	/**
	 * Set sessionId or token
	 * @param sessionIdOrToken
	 * @return
	 */
	public NonceHelper sessionIdOrToken(String sessionIdOrToken) {
		this.sessionIdOrToken = sessionIdOrToken;
		return this;
	}
	/**
	 * Set timestamp
	 * @param timestamp
	 * @return
	 */
	public NonceHelper timestamp(Date timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	/**
	 * 產生Nonce物件
	 * @return
	 */
	public NonceEntity build() {
		NonceEntity nonce = new NonceEntity();
		nonce.setTimestamp(timestamp);
		nonce.setSessionIdOrToken(sessionIdOrToken);
		return nonce;
	}
	
	/**
	 * 產生nonce字串。
	 * OpenID中的可選參數。client自行設定的字符串，可讓client方便用來關聯client session與token。
	 * 
	 * @param nonce
	 * @return
	 * @see tw.gov.ndc.emsg.mydata.gspclient.bean.NonceEntity
	 */
	public String encodeNonce(NonceEntity nonce) {
		if(nonce == null) return null;
		String result = "";
		ObjectMapper om = new ObjectMapper();
		try {
			result = om.writeValueAsString(nonce);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		try {
			result = encoder.encodeToString(result.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 將編碼後的nonce字串解譯為Nonce物件。
	 * @param encodedString
	 * @return
	 */
	public NonceEntity decodeNonce(String encodedString) {
		if(StringUtils.isEmpty(encodedString)) {
			return null;
		}
		NonceEntity nonce = null;;
		try {
			String json = new String(
					decoder.decode(encodedString.getBytes("UTF-8")),
					"UTF-8");
			ObjectMapper om = new ObjectMapper();
			nonce = om.readValue(json, NonceEntity.class);
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nonce;
	}
	
}
