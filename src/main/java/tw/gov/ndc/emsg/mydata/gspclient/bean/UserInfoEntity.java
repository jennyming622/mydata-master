/**
 * 
 */
package tw.gov.ndc.emsg.mydata.gspclient.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 呼叫UserInfo Endpoint回傳的資料
 * @author wesleyzhuang
 *
 */
public class UserInfoEntity {
	//對應至egov登入token1
	@JsonProperty("sub") private String sub;
	@JsonProperty("cn") private String name;
	@JsonProperty("preferred_username") private String preferredUsername;
	@JsonProperty("name") private String accountName;
	//代表身分證字號
	@JsonProperty("uid") private String uid;
	@JsonProperty("uid_verified") private Boolean isValidUid;
	@JsonProperty("birthdate") private String birthdate;
	@JsonProperty("gender") private String gender;
	@JsonProperty("email") private String email;
	@JsonProperty("email_verified") private Boolean emailVerified;
	//代表eGov帳號
	@JsonProperty("account") private String account;
	@JsonProperty("profile") private String profile;
	@JsonProperty("idp") private String idp;
	@JsonProperty("x509type") private String x509type;
	@JsonProperty("amr") private String amr;
	//綁定電話號碼？
	@JsonProperty("phone_number") private Object phoneNumber;
	private boolean boxcheck;
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
	public String getPreferredUsername() {
		return preferredUsername;
	}
	public void setPreferredUsername(String preferredUsername) {
		this.preferredUsername = preferredUsername;
	}
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * @return the isValidUid
	 */
	public Boolean getIsValidUid() {
		return isValidUid;
	}
	/**
	 * @param isValidUid the isValidUid to set
	 */
	public void setIsValidUid(Boolean isValidUid) {
		this.isValidUid = isValidUid;
	}
	/**
	 * @return the birthdate
	 */
	public String getBirthdate() {
		return birthdate;
	}
	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the emailVerified
	 */
	public Boolean getEmailVerified() {
		return emailVerified;
	}
	/**
	 * @param emailVerified the emailVerified to set
	 */
	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}
	/**
	 * @param profile the profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}
	/**
	 *  @return the idp
	 * @return
	 */
	public String getIdp() {
		return idp;
	}
	/**
	 * @param idp the idp to set
	 * @param idp
	 */
	public void setIdp(String idp) {
		this.idp = idp;
	}
	public String getX509type() {
		return x509type;
	}
	public void setX509type(String x509type) {
		this.x509type = x509type;
	}
	public String getAmr() {
		return amr;
	}
	public void setAmr(String amr) {
		this.amr = amr;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public Object getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(Object phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public boolean isBoxcheck() {
		return boxcheck;
	}
	public void setBoxcheck(boolean boxcheck) {
		this.boxcheck = boxcheck;
	}
}
