/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 呼叫 token_endpoint 後回傳的資料物件
 * @author wesleyzhuang
 *
 */
public class TokenEntity {

	@JsonProperty("id_token") private String idToken;
	@JsonProperty("access_token") private String accessToken;
	@JsonProperty("expires_in") private Integer expiresIn;
	@JsonProperty("token_type") private String tokenType;
	@JsonProperty("refresh_token") private String refreshToken;
	
	/**
	 * @return the idToken
	 */
	public String getIdToken() {
		return idToken;
	}
	/**
	 * @param idToken the idToken to set
	 */
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	/**
	 * @return the expiresIn
	 */
	public Integer getExpiresIn() {
		return expiresIn;
	}
	/**
	 * @param expiresIn the expiresIn to set
	 */
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}
	/**
	 * @return the tokenType
	 */
	public String getTokenType() {
		return tokenType;
	}
	/**
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
}
