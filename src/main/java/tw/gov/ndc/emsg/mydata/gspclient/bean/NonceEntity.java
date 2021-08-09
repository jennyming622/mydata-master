/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient.bean;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * OpenID中的可選參數。client自行設定的字符串，可讓client方便用來關聯client session與token。
 * Nonce物件將nonce字串中的資訊以屬性方式呈現。
 * @author wesleyzhuang
 *
 */
public class NonceEntity {

	private Date timestamp;
	private String sessionIdOrToken;
	
	/**
	 * @return the date
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * @param date the date to set
	 */
	public void setTimestamp(Date date) {
		this.timestamp = date;
	}
	/**
	 * @return the sessionIdOrToken
	 */
	public String getSessionIdOrToken() {
		return sessionIdOrToken;
	}
	/**
	 * @param sessionIdOrToken the sessionIdOrToken to set
	 */
	public void setSessionIdOrToken(String sessionIdOrToken) {
		this.sessionIdOrToken = sessionIdOrToken;
	}
	
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(sessionIdOrToken)
//			.append("::")
//			.append(DateFormatUtils.format(timestamp, "yyyyMMddHHmmss"));
//		return sb.toString();
//	}
	
}
