/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient.bean;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 授權記錄
 * @author wesleyzhuang
 *
 */
public class TokenRecord implements Serializable{
	private static final long serialVersionUID = 1L;
	@JsonProperty("key") private String key;
	@JsonProperty("clientId") private String clientId;
	@JsonProperty("creationTime") private Date creationTime;
	@JsonProperty("subjectId") private String subjectId;
	@JsonProperty("type") private String type;
	@JsonProperty("expiration") private Date expiration;
	@JsonProperty("data") private String data;
	//@JsonProperty("data") private TokenData data;
	
	private TokenData tokenData;
	
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * @return the creationTime
	 */
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss", timezone="GMT")
	public Date getCreationTime() {
		return creationTime;
	}
	/**
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	/**
	 * @return the subjectId
	 */
	public String getSubjectId() {
		return subjectId;
	}
	/**
	 * @param subjectId the subjectId to set
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the expiration
	 */
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss", timezone="GMT")
	public Date getExpiration() {
		return expiration;
	}
	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	public TokenData getTokenData() {
		if(tokenData == null && StringUtils.isNotEmpty(getData())) {
			tokenData = new TokenData(getData());
		}
		return tokenData;
	}
	
}
