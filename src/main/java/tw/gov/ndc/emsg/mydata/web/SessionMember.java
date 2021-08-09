package tw.gov.ndc.emsg.mydata.web;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.lang3.StringUtils;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.dbencrypt.DbEncryptUtilsV2;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;

public class SessionMember extends Member{
	private Integer id;
	private String account;
	private String name;
	private Date birthdate;
	private String gender;
	private String uid;
	private Boolean uidVerified;
	private String email;
	private Boolean emailVerified;
	private String mobile;
	private Boolean mobileVerified;
	private String informMethod;	
	private String birthdateForRE;
	private Date loginTime;
	private Date preLoginTime;

	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
	public SessionMember(Member member) throws BadPaddingException, IllegalBlockSizeException{
		id = member.getId();
		account = DbEncryptUtilsV2.encryptAES(member.getAccount());
		name = DbEncryptUtilsV2.encryptAES(member.getName());
		birthdate = member.getBirthdate();
		gender = DbEncryptUtilsV2.encryptAES(member.getGender());
		uid = DbEncryptUtilsV2.encryptAES(member.getUid());
		uidVerified = member.getUidVerified();
		email = DbEncryptUtilsV2.encryptAES(member.getEmail());
		emailVerified = member.getEmailVerified();
		mobile = DbEncryptUtilsV2.encryptAES(member.getMobile());
		mobileVerified = member.getMobileVerified();
		informMethod = DbEncryptUtilsV2.encryptAES(member.getInformMethod());
		birthdateForRE = DbEncryptUtilsV2.encryptAES(member.getBirthdateForRE());
		loginTime = member.getLoginTime();
		preLoginTime = member.getPreLoginTime();
	}
	
	public static Member getSessionMemberToMember(Member sMember) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		Member member = new Member();
		member.setId(ValidatorHelper.limitNumber(sMember.getId()));
		member.setAccount(ValidatorHelper.removeSpecialCharacters(DbEncryptUtilsV2.decryptAES(sMember.getAccount())));
		member.setName(ValidatorHelper.removeSpecialCharacters(DbEncryptUtilsV2.decryptAES(sMember.getName())));
		member.setBirthdate(ValidatorHelper.limitDate(sMember.getBirthdate()));
		member.setGender(ValidatorHelper.removeSpecialCharacters(DbEncryptUtilsV2.decryptAES(sMember.getGender())));
		member.setUid(ValidatorHelper.removeSpecialCharacters(DbEncryptUtilsV2.decryptAES(sMember.getUid())));
		member.setUidVerified(sMember.getUidVerified());
		member.setEmail(ValidatorHelper.removeSpecialCharacters(DbEncryptUtilsV2.decryptAES(sMember.getEmail())));
		member.setEmailVerified(sMember.getEmailVerified());
		member.setMobile(ValidatorHelper.removeSpecialCharacters(DbEncryptUtilsV2.decryptAES(sMember.getMobile())));
		member.setMobileVerified(sMember.getMobileVerified());
		member.setInformMethod(ValidatorHelper.removeSpecialCharacters(DbEncryptUtilsV2.decryptAES(sMember.getInformMethod())));
		member.setLoginTime(sMember.getLoginTime());
		member.setPreLoginTime(sMember.getPreLoginTime());

		String birthdateForRE = DbEncryptUtilsV2.decryptAES(sMember.getBirthdateForRE());
		if(StringUtils.isBlank(birthdateForRE) && sMember.getBirthdate() != null) {
			String yearStr = String.valueOf(Integer.valueOf(sdf1.format(sMember.getBirthdate())) -1911);
			String monthStr = sdf2.format(sMember.getBirthdate());
			String dateStr = sdf3.format(sMember.getBirthdate());
			if(yearStr.length()==1) {
				yearStr = "00" + yearStr;
			}else if(yearStr.length()==2) {
				yearStr = "0" + yearStr;
			}
			member.setBirthdateForRE(yearStr+monthStr+dateStr);
		} else {
			member.setBirthdateForRE(birthdateForRE);
		}


		return member;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Boolean getUidVerified() {
		return uidVerified;
	}

	public void setUidVerified(Boolean uidVerified) {
		this.uidVerified = uidVerified;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean getMobileVerified() {
		return mobileVerified;
	}

	public void setMobileVerified(Boolean mobileVerified) {
		this.mobileVerified = mobileVerified;
	}

	public String getInformMethod() {
		return informMethod;
	}

	public void setInformMethod(String informMethod) {
		this.informMethod = informMethod;
	}

	public String getBirthdateForRE() {
		return birthdateForRE;
	}

	public void setBirthdateForRE(String birthdateForRE) {
		this.birthdateForRE = birthdateForRE;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getPreLoginTime() {
		return preLoginTime;
	}

	public void setPreLoginTime(Date preLoginTime) {
		this.preLoginTime = preLoginTime;
	}
}
