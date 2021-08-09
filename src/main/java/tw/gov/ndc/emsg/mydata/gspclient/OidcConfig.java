/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 符合OpenID Connect規範的configuration參數。 
 * @author wesleyzhuang
 *
 */
public class OidcConfig {
	@JsonProperty("issuer") private String issuer;
	@JsonProperty("jwks_uri") private String jwksUri;
	@JsonProperty("check_session_iframe") private String checkSessionIframe;
	
	@JsonProperty("authorization_endpoint") private String authorizationEndpoint; 
	@JsonProperty("token_endpoint") private String tokenEndpoint;
	@JsonProperty("userinfo_endpoint") private String userinfoEndpoint;
	@JsonProperty("end_session_endpoint") private String endSessionEndpoint;
	@JsonProperty("revocation_endpoint") private String revocationEndpoint;
	@JsonProperty("introspection_endpoint") private String introspectionEndpoint;
	
	@JsonProperty("frontchannel_logout_supported") private Boolean frontchannelLogoutSupported;
	@JsonProperty("frontchannel_logout_session_supported") private Boolean frontchannelLogoutSessionSupported;
	
	@JsonProperty("scope_supported") private List<String> scopesSupported; 
	@JsonProperty("claims_supported") private List<String> claimsSupported;
	@JsonProperty("grant_types_supported") private List<String> grantTypesSupported;
	@JsonProperty("response_types_supported") private List<String> responseTypesSupported;
	@JsonProperty("response_modes_supported") private List<String> responseModesSupported;
	@JsonProperty("token_endpoint_auth_methods_supported") private List<String> tokenEndpointAuthMethodsSupported;
	@JsonProperty("subject_types_supported") private List<String> subjectTypesSupported;
	@JsonProperty("id_token_signing_alg_values_supported") private List<String> idTokenSigningAlgValuesSupported;
	@JsonProperty("code_challenge_methods_supported") private List<String> codeChallengeMethodsSupported;

	@JsonProperty("apiamr_endpoint") private String apiamrEndpoint; 
	/**
	 * @return the issuer
	 */
	public String getIssuer() {
		return issuer;
	}

	/**
	 * @param issuer the issuer to set
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	/**
	 * @return the jwksUri
	 */
	public String getJwksUri() {
		return jwksUri;
	}

	/**
	 * @param jwksUri the jwksUri to set
	 */
	public void setJwksUri(String jwksUri) {
		this.jwksUri = jwksUri;
	}

	/**
	 * @return the checkSessionIframe
	 */
	public String getCheckSessionIframe() {
		return checkSessionIframe;
	}

	/**
	 * @param checkSessionIframe the checkSessionIframe to set
	 */
	public void setCheckSessionIframe(String checkSessionIframe) {
		this.checkSessionIframe = checkSessionIframe;
	}

	/**
	 * @return the authorizationEndpoint
	 */
	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}

	/**
	 * @param authorizationEndpoint the authorizationEndpoint to set
	 */
	public void setAuthorizationEndpoint(String authorizationEndpoint) {
		this.authorizationEndpoint = authorizationEndpoint;
	}

	/**
	 * @return the tokenEndpoint
	 */
	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	/**
	 * @param tokenEndpoint the tokenEndpoint to set
	 */
	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}

	/**
	 * @return the userinfoEndpoint
	 */
	public String getUserinfoEndpoint() {
		return userinfoEndpoint;
	}

	/**
	 * @param userinfoEndpoint the userinfoEndpoint to set
	 */
	public void setUserinfoEndpoint(String userinfoEndpoint) {
		this.userinfoEndpoint = userinfoEndpoint;
	}

	/**
	 * @return the endSessionEndpoint
	 */
	public String getEndSessionEndpoint() {
		return endSessionEndpoint;
	}

	/**
	 * @param endSessionEndpoint the endSessionEndpoint to set
	 */
	public void setEndSessionEndpoint(String endSessionEndpoint) {
		this.endSessionEndpoint = endSessionEndpoint;
	}

	/**
	 * @return the revocationEndpoint
	 */
	public String getRevocationEndpoint() {
		return revocationEndpoint;
	}

	/**
	 * @param revocationEndpoint the revocationEndpoint to set
	 */
	public void setRevocationEndpoint(String revocationEndpoint) {
		this.revocationEndpoint = revocationEndpoint;
	}

	/**
	 * @return the introspectionEndpoint
	 */
	public String getIntrospectionEndpoint() {
		return introspectionEndpoint;
	}

	/**
	 * @param introspectionEndpoint the introspectionEndpoint to set
	 */
	public void setIntrospectionEndpoint(String introspectionEndpoint) {
		this.introspectionEndpoint = introspectionEndpoint;
	}

	/**
	 * @return the frontchannelLogoutSupported
	 */
	public Boolean getFrontchannelLogoutSupported() {
		return frontchannelLogoutSupported;
	}

	/**
	 * @param frontchannelLogoutSupported the frontchannelLogoutSupported to set
	 */
	public void setFrontchannelLogoutSupported(Boolean frontchannelLogoutSupported) {
		this.frontchannelLogoutSupported = frontchannelLogoutSupported;
	}

	/**
	 * @return the frontchannelLogoutSessionSupported
	 */
	public Boolean getFrontchannelLogoutSessionSupported() {
		return frontchannelLogoutSessionSupported;
	}

	/**
	 * @param frontchannelLogoutSessionSupported the frontchannelLogoutSessionSupported to set
	 */
	public void setFrontchannelLogoutSessionSupported(Boolean frontchannelLogoutSessionSupported) {
		this.frontchannelLogoutSessionSupported = frontchannelLogoutSessionSupported;
	}

	/**
	 * @return the scopesSupported
	 */
	public List<String> getScopesSupported() {
		return scopesSupported;
	}

	/**
	 * @param scopesSupported the scopesSupported to set
	 */
	public void setScopesSupported(List<String> scopesSupported) {
		this.scopesSupported = scopesSupported;
	}

	/**
	 * @return the claimsSupported
	 */
	public List<String> getClaimsSupported() {
		return claimsSupported;
	}

	/**
	 * @param claimsSupported the claimsSupported to set
	 */
	public void setClaimsSupported(List<String> claimsSupported) {
		this.claimsSupported = claimsSupported;
	}

	/**
	 * @return the grantTypesSupported
	 */
	public List<String> getGrantTypesSupported() {
		return grantTypesSupported;
	}

	/**
	 * @param grantTypesSupported the grantTypesSupported to set
	 */
	public void setGrantTypesSupported(List<String> grantTypesSupported) {
		this.grantTypesSupported = grantTypesSupported;
	}

	/**
	 * @return the responseTypesSupported
	 */
	public List<String> getResponseTypesSupported() {
		return responseTypesSupported;
	}

	/**
	 * @param responseTypesSupported the responseTypesSupported to set
	 */
	public void setResponseTypesSupported(List<String> responseTypesSupported) {
		this.responseTypesSupported = responseTypesSupported;
	}

	/**
	 * @return the responseModesSupported
	 */
	public List<String> getResponseModesSupported() {
		return responseModesSupported;
	}

	/**
	 * @param responseModesSupported the responseModesSupported to set
	 */
	public void setResponseModesSupported(List<String> responseModesSupported) {
		this.responseModesSupported = responseModesSupported;
	}

	/**
	 * @return the tokenEndpointAuthMethodsSupported
	 */
	public List<String> getTokenEndpointAuthMethodsSupported() {
		return tokenEndpointAuthMethodsSupported;
	}

	/**
	 * @param tokenEndpointAuthMethodsSupported the tokenEndpointAuthMethodsSupported to set
	 */
	public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
		this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
	}

	/**
	 * @return the subjectTypesSupported
	 */
	public List<String> getSubjectTypesSupported() {
		return subjectTypesSupported;
	}

	/**
	 * @param subjectTypesSupported the subjectTypesSupported to set
	 */
	public void setSubjectTypesSupported(List<String> subjectTypesSupported) {
		this.subjectTypesSupported = subjectTypesSupported;
	}

	/**
	 * @return the idTokenSigningAlgValuesSupported
	 */
	public List<String> getIdTokenSigningAlgValuesSupported() {
		return idTokenSigningAlgValuesSupported;
	}

	/**
	 * @param idTokenSigningAlgValuesSupported the idTokenSigningAlgValuesSupported to set
	 */
	public void setIdTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
		this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
	}

	/**
	 * @return the codeChallengeMethodsSupported
	 */
	public List<String> getCodeChallengeMethodsSupported() {
		return codeChallengeMethodsSupported;
	}

	/**
	 * @param codeChallengeMethodsSupported the codeChallengeMethodsSupported to set
	 */
	public void setCodeChallengeMethodsSupported(List<String> codeChallengeMethodsSupported) {
		this.codeChallengeMethodsSupported = codeChallengeMethodsSupported;
	}

	public String getApiamrEndpoint() {
		return apiamrEndpoint;
	}

	public void setApiamrEndpoint(String apiamrEndpoint) {
		this.apiamrEndpoint = apiamrEndpoint;
	}
}
