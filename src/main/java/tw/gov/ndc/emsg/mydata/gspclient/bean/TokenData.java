/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient.bean;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 授權記錄data欄位
 * @author wesleyzhuang
 *
 */
public class TokenData {

	private String subjectId;
	private String clientId;
	private List<String> scopes;
	private Date creationTime;
	private Date expiration;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	public TokenData(String data) {
		
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); //GMT+0
		
		ObjectMapper om = new ObjectMapper();
		try {
			System.out.println("----------------------");
			System.out.println(data);
			System.out.println("----------------------");
			JsonNode node = om.readTree(data);
			this.subjectId = node.findValue("SubjectId").asText();
			this.clientId = node.findValue("ClientId").asText();
			String ct = node.findValue("CreationTime").asText();
			//2017-10-26 09:14:26
			if(StringUtils.isNotEmpty(ct) && !StringUtils.equals(ct, "null")) {
				if(ct.lastIndexOf(".")>0) {
					ct = ct.substring(0, ct.lastIndexOf("."));
				}
				this.creationTime = sdf.parse(ct);
			}
			String et = node.findValue("Expiration").asText();
			if(StringUtils.isNotEmpty(et) && !StringUtils.equals(et, "null")) {
				et = et.substring(0, et.lastIndexOf("."));
				this.expiration = sdf.parse(et);
			}
			List<String> scopes = new ArrayList<>();
			System.out.println("node.findValue(\"Scopes\")="+node.findValue("Scopes"));
			node.findValue("Scopes").forEach(p -> {
				scopes.add(p.asText());
			});
			this.scopes = scopes;
		} catch (IOException e) {
			System.out.println("TokenData 1 ex:\n"+e);
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("TokenData 2 ex:\n"+e);
			e.printStackTrace();
		}
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
	 * @return the scopes
	 */
	public List<String> getScopes() {
		return scopes;
	}
	/**
	 * @param scopes the scopes to set
	 */
	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}
	/**
	 * @return the creationTime
	 */
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
	 * @return the expiration
	 */
	public Date getExpiration() {
		return expiration;
	}
	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
}
