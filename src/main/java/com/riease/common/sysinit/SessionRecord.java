package com.riease.common.sysinit;

import java.io.Serializable;

import tw.gov.ndc.emsg.mydata.entity.AuthToken;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.web.SessionMember;

public class SessionRecord implements Serializable {

	public static final String SessionKey = "SignedInUser";
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -7783022199317917448L;
	
	private String userID;				//帳號的流水號
    //private String userAccount = "";	// 帳號
    private String userName = "";		//帳號名稱
    private long loginTime = System.currentTimeMillis();
    //private HttpSession session = null;
    //private Object userBean;			// 帳號物件
    private String remoteIp;
    private Integer roleId;
    private String roleType;			// 帳號角色類別
    private String roleName = "";
    private String multifactorType = "";
//    private Locale currentLocale;			//目前所選的locale
    private AuthToken authToken;
    private Member member;
	private Member maskMember;
    private boolean boxcheck;
    private String uuid;
//    public String getSessionId() {
//    	if(this.getSession() == null) {
//    		return null;
//    	}
//    	return this.getSession().getId();
//    }
    
    
    
    public String getRoleType() {
		return roleType;
	}
	public SessionRecord setRoleType(String roleType) {
		this.roleType = roleType;
		return this;
	}
	
	public Integer getRoleId() {
		return roleId;
	}
	public SessionRecord setRoleId(Integer roleId) {
		this.roleId = roleId;
		return this;
	}
	
	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	/**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }
    /**
     * @param userID the userID to set
     */
    public SessionRecord setUserID(String userID) {
        this.userID = userID;
        return this;
    }
    /**
     * @return the loginTime
     */
    public long getLoginTime() {
        return loginTime;
    }
    /**
     * @param loginTime the loginTime to set
     */
    public SessionRecord setLoginTime(long loginTime) {
        this.loginTime = loginTime;
        return this;
    }
//    /**
//     * @return the session
//     */
//    public HttpSession getSession() {
//        return session;
//    }
//    /**
//     * @param session the session to set
//     */
//    public SessionRecord setSession(HttpSession session) {
//        this.session = session;
//        return this;
//    }
    
//	public Object getUserBean() {
//		return userBean;
//	}
//	public SessionRecord setUserBean(Object userBean) {
//		this.userBean = userBean;
//		return this;
//	}
	public String getUserName() {
		return userName;
	}
	public SessionRecord setUserName(String userName) {
		this.userName = userName;
		return this;
	}
	public String getRemoteIp() {
		return remoteIp;
	}
	public SessionRecord setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
		return this;
	}
	
//	public String getUserAccount() {
//		return userAccount;
//	}
//	public void setUserAccount(String userAccount) {
//		this.userAccount = userAccount;
//	}

//	public Locale getCurrentLocale() {
//		return currentLocale;
//	}
//
//	public void setCurrentLocale(Locale currentLocale) {
//		this.currentLocale = currentLocale;
//	}
	
	public AuthToken getAuthToken() {
		return authToken;
	}
	public void setAuthToken(AuthToken authToken) {
		this.authToken = authToken;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public boolean isBoxcheck() {
		return boxcheck;
	}
	public void setBoxcheck(boolean boxcheck) {
		this.boxcheck = boxcheck;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Member getMaskMember() {
		return maskMember;
	}

	public void setMaskMember(Member maskMember) {
		this.maskMember = maskMember;
	}

	public String getMultifactorType() {
		return multifactorType;
	}

	public void setMultifactorType(String multifactorType) {
		this.multifactorType = multifactorType;
	}
}
