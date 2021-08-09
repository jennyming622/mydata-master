/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 反查 access_token 的結果
 * @author wesleyzhuang
 *
 */
public class IntrospectEntity {
	//OPTIONAL. Integer timestamp, measured in the number of seconds since January 1 1970 UTC, indicating when this token is not to be used before, as defined in JWT [RFC7519]
	@JsonProperty("nbf") private Long nbf;
	//OPTIONAL.  Integer timestamp, measured in the number of seconds since January 1 1970 UTC, indicating when this token will expire, as defined in JWT [RFC7519].
	@JsonProperty("exp") private Long exp;
	//OPTIONAL.  String representing the issuer of this token, as defined in JWT [RFC7519].
	@JsonProperty("iss") private String iss;
	//OPTIONAL.  Service-specific string identifier or list of string identifiers representing the intended audience for this token, as defined in JWT [RFC7519].
	@JsonProperty("aud") private List<String> aud;
	//OPTIONAL.  Client identifier for the OAuth 2.0 client that requested this token.
	@JsonProperty("client_id") private String clientId;
	//OPTIONAL.  Subject of the token, as defined in JWT [RFC7519]. Usually a machine-readable identifier of the resource owner who authorized this token.
	@JsonProperty("sub") private String sub;
	@JsonProperty("auth_time") private Long authTime;
	@JsonProperty("idp") private String idp;
	//OPTIONAL.  Human-readable identifier for the resource owner who authorized this token.
	@JsonProperty("name") private String name;
	@JsonProperty("amr") private String amr;
	//OPTIONAL.  A JSON string containing a space-separated list of scopes associated with this token, in the format described in Section 3.3 of OAuth 2.0 [RFC6749].
	@JsonProperty("scope") private String scope;
	//REQUIRED.  Boolean indicator of whether or not the presented token is currently active.
	@JsonProperty("active") private Boolean active;
	@JsonProperty("verification") private String verification;
	
	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
	/**
	 * @return the sub
	 */
	public String getSub() {
		return sub;
	}
	/**
	 * @param sub the sub to set
	 */
	public void setSub(String sub) {
		this.sub = sub;
	}
	/**
	 * @return the nbf
	 */
	public Long getNbf() {
		return nbf;
	}
	/**
	 * @param nbf the nbf to set
	 */
	public void setNbf(Long nbf) {
		this.nbf = nbf;
	}
	/**
	 * @return the exp
	 */
	public Long getExp() {
		return exp;
	}
	/**
	 * @param exp the exp to set
	 */
	public void setExp(Long exp) {
		this.exp = exp;
	}
	/**
	 * @return the iss
	 */
	public String getIss() {
		return iss;
	}
	/**
	 * @param iss the iss to set
	 */
	public void setIss(String iss) {
		this.iss = iss;
	}
	/**
	 * @return the aud
	 */
	public List<String> getAud() {
		return aud;
	}
	/**
	 * @param aud the aud to set
	 */
	public void setAud(List<String> aud) {
		this.aud = aud;
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
	 * @return the authTime
	 */
	public Long getAuthTime() {
		return authTime;
	}
	/**
	 * @param authTime the authTime to set
	 */
	public void setAuthTime(Long authTime) {
		this.authTime = authTime;
	}
	/**
	 * @return the idp
	 */
	public String getIdp() {
		return idp;
	}
	/**
	 * @param idp the idp to set
	 */
	public void setIdp(String idp) {
		this.idp = idp;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the amr
	 */
	public String getAmr() {
		return amr;
	}
	/**
	 * @param amr the amr to set
	 */
	public void setAmr(String amr) {
		this.amr = amr;
	}
	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}
	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getVerification() {
		return verification;
	}
	public void setVerification(String verification) {
		this.verification = verification;
	}
}
