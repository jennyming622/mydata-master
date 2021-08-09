/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient;

/**
 * OpenID Connect Grant Type
 * @author wesleyzhuang
 *
 */
public enum OidcGrantType {
	/**
	 * 指定要求access_token
	 */
	authorization_code,
	/**
	 * 指定更新access_token
	 */
	refresh_token
	;
}
