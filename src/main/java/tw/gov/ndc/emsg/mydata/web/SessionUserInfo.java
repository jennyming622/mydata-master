package tw.gov.ndc.emsg.mydata.web;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.fasterxml.jackson.annotation.JsonProperty;

import tw.gov.ndc.emsg.dbencrypt.DbEncryptUtilsV2;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;

public class SessionUserInfo {
	private String sub;
	private String name;
	private String preferredUsername;
	private String accountName;
	//代表身分證字號
	private String uid;
	private Boolean isValidUid;
	private String birthdate;
	private String gender;
	private String email;
	private Boolean emailVerified;
	//代表eGov帳號
	private String account;
	private String profile;
	private String idp;
	private String x509type;
	private String amr;
	//綁定電話號碼？
	private Object phoneNumber;
	private boolean boxcheck;
	
	public SessionUserInfo(UserInfoEntity info) throws BadPaddingException, IllegalBlockSizeException{
		sub = DbEncryptUtilsV2.encryptAES(info.getSub());
		name = DbEncryptUtilsV2.encryptAES(info.getName());
		preferredUsername = DbEncryptUtilsV2.encryptAES(info.getPreferredUsername());
		accountName = DbEncryptUtilsV2.encryptAES(info.getAccountName());
		uid = DbEncryptUtilsV2.encryptAES(info.getUid());
		isValidUid = info.getIsValidUid();
		birthdate = DbEncryptUtilsV2.encryptAES(info.getBirthdate());
		gender = DbEncryptUtilsV2.encryptAES(info.getGender());
		email = DbEncryptUtilsV2.encryptAES(info.getEmail());
		emailVerified = info.getEmailVerified();
		
		account = DbEncryptUtilsV2.encryptAES(info.getAccount());
		profile = DbEncryptUtilsV2.encryptAES(info.getProfile());
		idp = DbEncryptUtilsV2.encryptAES(info.getIdp());
		x509type = DbEncryptUtilsV2.encryptAES(info.getX509type());
		amr = DbEncryptUtilsV2.encryptAES(info.getAmr());
		phoneNumber = info.getPhoneNumber();
		boxcheck = info.isBoxcheck();
	}
	
	public static UserInfoEntity getSessionUserInfoToUserInfo(SessionUserInfo sInfo) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		UserInfoEntity info = new UserInfoEntity();
		info.setSub(DbEncryptUtilsV2.decryptAES(sInfo.getSub()));
		info.setName(DbEncryptUtilsV2.decryptAES(sInfo.getName()));
		info.setPreferredUsername(DbEncryptUtilsV2.decryptAES(sInfo.getPreferredUsername()));
		info.setAccountName(DbEncryptUtilsV2.decryptAES(sInfo.getAccountName()));
		info.setUid(DbEncryptUtilsV2.decryptAES(sInfo.getUid()));
		info.setIsValidUid(sInfo.getIsValidUid());
		info.setBirthdate(DbEncryptUtilsV2.decryptAES(sInfo.getBirthdate()));
		info.setGender(DbEncryptUtilsV2.decryptAES(sInfo.getGender()));
		info.setEmail(DbEncryptUtilsV2.decryptAES(sInfo.getEmail()));
		info.setEmailVerified(sInfo.getEmailVerified());
		info.setAccount(DbEncryptUtilsV2.decryptAES(sInfo.getAccount()));
		info.setProfile(DbEncryptUtilsV2.decryptAES(sInfo.getProfile()));
		info.setIdp(DbEncryptUtilsV2.decryptAES(sInfo.getIdp()));
		info.setX509type(DbEncryptUtilsV2.decryptAES(sInfo.getX509type()));
		info.setAmr(DbEncryptUtilsV2.decryptAES(sInfo.getAmr()));
		info.setPhoneNumber(sInfo.getPhoneNumber());
		info.setBoxcheck(sInfo.isBoxcheck());
		return info;
	}
	
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPreferredUsername() {
		return preferredUsername;
	}
	public void setPreferredUsername(String preferredUsername) {
		this.preferredUsername = preferredUsername;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Boolean getIsValidUid() {
		return isValidUid;
	}
	public void setIsValidUid(Boolean isValidUid) {
		this.isValidUid = isValidUid;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean getEmailVerified() {
		return emailVerified;
	}
	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getIdp() {
		return idp;
	}
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
