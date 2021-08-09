package tw.gov.ndc.emsg.mydata.util;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.riease.common.enums.ActionEvent;
import com.riease.common.util.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.riease.common.helper.HttpHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;

import tw.gov.ndc.emsg.mydata.config.MySessionContext;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.mapper.LogMemberSessionMapper;
import tw.gov.ndc.emsg.mydata.mapper.MemberMapper;
import tw.gov.ndc.emsg.mydata.mapper.MemberPrivacyMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalLoginMapper;
import tw.gov.ndc.emsg.mydata.type.DeviceType;
import tw.gov.ndc.emsg.mydata.type.MemberSessionState;
import tw.gov.ndc.emsg.mydata.type.SendType;
import tw.gov.ndc.emsg.mydata.web.SessionMember;

@Component
public class LoginUtil {
	private static final Logger logger = LoggerFactory.getLogger(LoginUtil.class);
	private final Base64.Encoder encoder = Base64.getEncoder();
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private PortalLoginMapper portalLoginMapper;
	@Autowired 
	private MemberMapper memberMapper;
	@Autowired
	private MemberPrivacyMapper memberPrivacyMapper;
	@Autowired
	private LogMemberSessionMapper logMemberSessionMapper;
	@Autowired 
	private TokenUtils tokenUtils;
	@Autowired
	private UlogUtil ulogUtil;
	@Autowired
	private LogMemberSessionUtil logMemberSessionUtil;

	
	/**
	 * 關於重新登入就關閉舊的session [ session.invalidate();], 會造成多視窗操作的問題
	 * 2020-05-03 MyData滲透測試報告, 僅註銷重登入換session , 不註銷同一IP不同session 
	 * 
	 */
	public Member doNewAutoLogin(Verify verify, HttpServletRequest request,String uuid) {
		String uid = ValidatorHelper.removeSpecialCharacters(verify.getUid());
		String birthdate = "";
		if(verify.getBirthdate()!=null) {
			birthdate = ValidatorHelper.removeSpecialCharacters(sdf6.format(verify.getBirthdate()));
		}
		String checkLevel = verify.getCheckLevel();
		String scope = verify.getScope();
	    try {
			String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));
			System.out.println("account="+account);
			HttpSession session = request.getSession();
			/**
			 * 根據安全測試，要求登入變更session_id
			 */
			session = changeSessionId(request);
			String roleType = "";
			SessionRecord recordTmp = ((SessionRecord)session.getAttribute(SessionRecord.SessionKey));
			if(recordTmp != null) {
				roleType = recordTmp.getRoleType();
			}
			System.out.println(roleType);

		/**
		 * 處理Member Table
		 */
			Member member = null;
			Map<String,Object> mParam = new HashMap<>();
			mParam.put("uid", uid.toUpperCase());
			if(StringUtils.isNotEmpty(birthdate)) {
				mParam.put("birthdate", sdf6.parse(birthdate));
			}
			mParam.put("statIsNullorZero", true);
			List<Member> mList = memberMapper.selectByExample(mParam);
			Boolean isNewMember = false;
			if(mList!=null&&mList.size()>0) {
				member = mList.get(0);
			}else {
				member = new Member();
				member.setAccount(account);
				member.setUid(uid.toUpperCase());
				member.setCtime(new Date());
				if(StringUtils.isNotEmpty(birthdate)) {
					member.setBirthdate(sdf6.parse(birthdate));
				}
				memberMapper.insertSelective(member);
				isNewMember = true;
			}
			
			Integer memberId = ValidatorHelper.limitNumber(member.getId());
//			Date agreePrivacyDate = ValidatorHelper.limitDate((Date) session.getAttribute("agreePrivacyDate"));
//			String version = ValidatorHelper.removeSpecialCharacters((String) session.getAttribute("privacyVersion"));
//			if(agreePrivacyDate != null && StringUtils.isNotBlank(version)) {
//				try {
//					MemberPrivacy memberPrivacy = new MemberPrivacy();
//					memberPrivacy.setMemberId(memberId);
//					memberPrivacy.setAgreeDate(agreePrivacyDate);
//					memberPrivacy.setVersion(version);
//					memberPrivacyMapper.insertSelective(memberPrivacy);
//				} catch (Exception e){
//					logger.error(e.getLocalizedMessage(), e);
//				}
//			}
			Map<String, Object> mpParam = new HashMap<>();
			mpParam.put("memberId", memberId);
			List<MemberPrivacy> mpList = memberPrivacyMapper.selectByExample(mpParam);
			if(mpList.size() == 0) {
				MemberPrivacy memberPrivacy = new MemberPrivacy();
				memberPrivacy.setMemberId(memberId);
				memberPrivacy.setAgreeDate(new Date());
				memberPrivacy.setVersion("v1.0");
				memberPrivacyMapper.insertSelective(memberPrivacy);
			}

			/**
			 * 處理 AuthToken
			 */
			AuthToken authToken = tokenUtils.generateToken(account, verify.getScope(), checkLevel);

			/**
			 * level
			 * 0 自然人憑證   ROLE_ID 0 ROLE_TYPE 0 cer
			 * 0.1 FIC：晶片金融卡
			 * 0.2 FCH：硬體金融憑證
			 * 0.3 FCS：軟體金融憑證
			 * 0.5 工商憑證   ROLE_ID 0 ROLE_TYPE 0.5 moeaca
			 * 1 tfido       ROLE_ID 1 ROLE_TYPE 1 tfido
			 * 2 健保卡       ROLE_ID 2 ROLE_TYPE 2 nhi
			 * 3 雙證件驗證       ROLE_ID 3 ROLE_TYPE 3 pii
			 * 4 行動電話 or EMAIL       ROLE_ID 4 ROLE_TYPE 4 mobile or email
			 * 5 mobile id ROLE_ID 5 ROLE_TYPE 5  mobile id
			 */

			if(checkLevel.equalsIgnoreCase("cer")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(0);
				sessionRecord.setRoleName("自然人憑證");
				sessionRecord.setRoleType("0");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("fic")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(0);
				sessionRecord.setRoleName("晶片金融卡");
				sessionRecord.setRoleType("0.1");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("fch")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(0);
				sessionRecord.setRoleName("硬體金融憑證");
				sessionRecord.setRoleType("0.2");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("fcs")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				//sessionRecord.setRoleId(0);
				sessionRecord.setRoleId(2);
				sessionRecord.setRoleName("軟體金融憑證");
				sessionRecord.setRoleType("2.1");
				//sessionRecord.setRoleType("0.3");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("moeaca")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(0);
				sessionRecord.setRoleName("工商憑證");
				sessionRecord.setRoleType("0.5");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("tfido")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(1);
				sessionRecord.setRoleName("tfido");
				sessionRecord.setRoleType("1");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("nhi")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(2);
				sessionRecord.setRoleName("健保卡");
				sessionRecord.setRoleType("2");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("pii")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(3);
				sessionRecord.setRoleName("雙證件驗證");
				sessionRecord.setRoleType("3");
				sessionRecord.setMultifactorType(verify.getMultifactorType());
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("email")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(4);
				sessionRecord.setRoleName("email");
				sessionRecord.setRoleType("4");
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
				if(uuid!=null&&uuid.trim().length()>0) {
					sessionRecord.setUuid(uuid);
				}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}else if(checkLevel.equalsIgnoreCase("mobileId")) {
				SessionRecord sessionRecord = new SessionRecord();
				sessionRecord.setLoginTime(System.currentTimeMillis());
				sessionRecord.setRemoteIp(HttpHelper.getRemoteIp(request));
				sessionRecord.setUserName(member.getName()==null?"匿名":maskName(member.getName()));
				sessionRecord.setRoleId(5);
				sessionRecord.setRoleName("mobileId");
				if(roleType.equalsIgnoreCase("5")) {
					sessionRecord.setRoleType("5");
				} else {
					sessionRecord.setRoleType("5");
				}
				sessionRecord.setMember(new SessionMember(member));
				Member maskMember = new Member();
				BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(member),maskMember);
				sessionRecord.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
				sessionRecord.setAuthToken(authToken);
					if(uuid!=null&&uuid.trim().length()>0) {
						sessionRecord.setUuid(uuid);
					}
				session.setAttribute(SessionRecord.SessionKey, sessionRecord);
				recordTmp = sessionRecord;
			}

			ulogUtil.recordFullByPr(recordTmp, null, null, null, null, ActionEvent.EVENT_180, null, null, HttpHelper.getRemoteIp(request));

			if(isNewMember == true) {
				ulogUtil.recordFullByPr(recordTmp, null, null, null, null, ActionEvent.EVENT_190, null, null, HttpHelper.getRemoteIp(request));
			}

			//request.getSession().setMaxInactiveInterval(20*60);
			request.getSession().setMaxInactiveInterval(10);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(member.getName()==null?account:member.getName(), account,Collections.emptyList());
			token.setDetails(new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(token);
			String sessionId = ValidatorHelper.removeSpecialCharacters(request.getRequestedSessionId());
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("egovAcc", account);
			List<PortalLogin> portalLoginList = portalLoginMapper.selectByExample(param);
			session.setAttribute("cancelBeforeLogin", false);
			System.out.println("==== cancelBeforeLogin is false ====");
			if(portalLoginList!=null&&portalLoginList.size()>0) {
					PortalLogin record = portalLoginList.get(0);
					System.out.println("==sessionId==:"+sessionId);
					if(sessionId.equalsIgnoreCase(record.getSessionId())) {
						//UNDO
					}else {
						/**
						 * 清出舊session by record.getSessionId()
						 */
						MySessionContext myc = MySessionContext.getInstance();
						HttpSession sess = myc.getSession(record.getSessionId());
						if(sess!=null) {
							try {
								sess.invalidate();
								session.setAttribute("cancelBeforeLogin", true);
								System.out.println("==== cancelBeforeLogin is true ====");
								PortalLogin record1 = new PortalLogin();
								record1.setId(ValidatorHelper.limitNumber(record.getId()));
								record1.setEgovAcc(ValidatorHelper.removeSpecialCharacters(account));
								record1.setSessionId(ValidatorHelper.removeSpecialCharacters(sessionId));
								record1.setCtime(new Date());
								portalLoginMapper.updateByPrimaryKey(record1);
							}catch(Exception ex) {
								System.out.println(ex);
							}
						}else {
							System.out.println("==== before my session is null ====");
						}
					}
			}else {
					//addLoginflag
					PortalLogin record = new PortalLogin();
					record.setEgovAcc(account);
					record.setSessionId(sessionId);
					record.setCtime(new Date());
					portalLoginMapper.insertSelective(record);
			}

			Date preLoginTime = ValidatorHelper.limitDate(member.getLoginTime());

			member.setLoginTime(new Date());
			member.setPreLoginTime(preLoginTime);
			//memberMapper.updateByPrimaryKeySelective(member); 
			/**
			 * 更新登入時間及前次登入時間
			 */
			Member member1 = new Member();
			member1.setId(ValidatorHelper.limitNumber(member.getId()));
			member1.setLoginTime(new Date());
			member1.setPreLoginTime(preLoginTime);
			memberMapper.updateByPrimaryKeySelective(member1);
			
			List<LogMemberSession> logMemberSessionList =
					logMemberSessionMapper.findWithSessionId(ValidatorHelper.removeSpecialCharacters(session.getId()));

			// LogMemberSession 查無紀錄才需新增
			if( !(logMemberSessionList != null && logMemberSessionList.size() > 0)) {
				createLogMemberSession(member, session);
			} else {
				LogMemberSession logMemberSession = logMemberSessionList.get(0);
				member.setLogMemberSession(logMemberSession);
			}

			return member;
	    } catch (Exception e) {
	        try {
				request.logout();
			} catch (ServletException ex) {
				ex.printStackTrace();
			}
	        logger.error("Failure in autoLogin", e);
	        return null;
	    }
	}
		
	public static boolean checkInLevel(HttpSession session,Integer level) {
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr==null) {
			return false;
		}else {
			if(level.compareTo(sr.getRoleId())>=0) {
				return true;
			}else {
				return false;
			}
		}
	}
	
    private HttpSession changeSessionId(HttpServletRequest request) {
        HttpSession oldSession = request.getSession();
        String oldSessionId = oldSession.getId();
        
        Map<String, Object> attrs = new HashMap<>();
        for(String name : Collections.list(oldSession.getAttributeNames())) {
            attrs.put(name, oldSession.getAttribute(name));
            oldSession.removeAttribute(name);
        }
        oldSession.invalidate(); // 令目前 Session 失效

        HttpSession newSession = request.getSession();
		String newSessionId = newSession.getId();
        for(String name : attrs.keySet()) {
            newSession.setAttribute(name, attrs.get(name));
        }

		String userAgent = request.getHeader("User-Agent");
		DeviceType deviceType = DeviceUtil.parseDevice(userAgent);

		if(deviceType == DeviceType.Android || deviceType == DeviceType.IPhone) {
			newSession.setMaxInactiveInterval(120);
		}

		List<LogMemberSession> logMemberSessionList =
				logMemberSessionMapper.findWithSessionId(oldSessionId);

		if(logMemberSessionList != null && logMemberSessionList.size() > 0) {
			LogMemberSession logMemberSession = logMemberSessionList.get(0);
			LogMemberSession logMemberSession1 = new LogMemberSession();
			logMemberSession1.setId(ValidatorHelper.limitNumber(logMemberSession.getId()));
			logMemberSession1.setMemberId(ValidatorHelper.limitNumber(logMemberSession.getMemberId()));
			logMemberSession1.setSessionId(ValidatorHelper.removeSpecialCharacters(newSessionId));
			logMemberSession1.setState(ValidatorHelper.removeSpecialCharacters(logMemberSession.getState()));
			logMemberSession1.setCreatedAt(ValidatorHelper.limitDate(logMemberSession.getCreatedAt()));
			logMemberSession1.setUpdateAt(new Date());
			logMemberSession1.setMsuuidcheck(ValidatorHelper.removeSpecialCharacters(logMemberSession.getMsuuidcheck()));
			logMemberSession1.setMsuuidcheckTime(ValidatorHelper.limitNumber(logMemberSession.getMsuuidcheckTime()));
			logMemberSessionMapper.updateByPrimaryKeySelective(logMemberSession1);
		}
        
        return newSession;
    }	
	
	public String maskName(String userName) {
		String ret = "";
		if(userName!=null&&userName.length()>=3) {
			ret = userName.substring(0, 1);
			for(int i=1;i<userName.length()-1;i++) {
				ret = ret +"*";
			}
			ret = ret + userName.substring(userName.length()-1);
		}else if(userName!=null&&userName.length()==2) {
			ret = userName.substring(1, 2);
		}else if(userName!=null&&userName.length()==1) {
			ret = "*";
		}
		return ret;
	}

	private void createLogMemberSession(Member member, HttpSession session) {
		Long t1 = System.currentTimeMillis();
		List<LogMemberSession> logMemberSessionList =
				logMemberSessionMapper.findWithMemberId(ValidatorHelper.limitNumber(member.getId()), MemberSessionState.Login.name(), t1);

		LogMemberSession logMemberSession = new LogMemberSession();
		logMemberSession.setMemberId(ValidatorHelper.limitNumber(member.getId()));
		logMemberSession.setSessionId(ValidatorHelper.removeSpecialCharacters(session.getId()));
		logMemberSession.setCreatedAt(new Date());
		if(logMemberSessionList != null && logMemberSessionList.size() > 0) {
			logMemberSession.setState(MemberSessionState.Ready.name());
		} else {
			logMemberSession.setState(MemberSessionState.Login.name());
		}
		logMemberSessionMapper.insertSelective(logMemberSession);

		if(StringUtils.equals(logMemberSession.getState(), MemberSessionState.Ready.name())) {
			logMemberSessionUtil.sendOTP(member, session, logMemberSession);
		}

		member.setLogMemberSession(logMemberSession);
	}
}
