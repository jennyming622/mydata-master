package tw.gov.ndc.emsg.mydata.web;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.service.image.ImageCaptchaService;

import com.riease.common.enums.ActionEvent;
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.HttpHelper;
import com.riease.common.helper.ValidatorHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.bean.PagingInfo;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;
import com.riease.common.sysinit.security.jcaptcha.SampleGimpyFactory;
import com.riease.common.util.RandomUtils;

import org.tempuri.WsAuthGSPSoapProxy;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs.SignerInfo;
import sun.security.timestamp.TimestampToken;
import sun.security.util.DerInputStream;
import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.entity.ext.NhiResultEntity;
import tw.gov.ndc.emsg.mydata.mapper.*;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;
import tw.gov.ndc.emsg.mydata.model.WebServiceJobId;
import tw.gov.ndc.emsg.mydata.type.*;
import tw.gov.ndc.emsg.mydata.util.*;

@Controller
@RequestMapping("/rest/user")
public class UserInfoRestController extends BaseRestController {
    private static Logger logger = LoggerFactory.getLogger(UserInfoRestController.class);
    
    public static String keystorePublicCrypto = Config.keystorePublicCrypto;
    public final static String ALGORITHM = "RSA";
    public final static String SIGNATURE_ALGORITHM = "MD5withRSA";
    private final static Base64.Encoder encoder = Base64.getEncoder();
    private final static Base64.Decoder base64Decoder = Base64.getDecoder();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("年MM月dd日HH時mm分ss秒");
    private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy年MM月dd日HH時mm分");
    private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy/M/d HH:mm");
    private static Long VERIFY_TIME = 2 * 60 * 1000L; // 有效時間兩分鐘

    @Value("${gsp.oidc.client.id}")
    private String clientId;
    @Value("${app.oidc.redirect.uri}")
    private String redirectUri;
    @Value("${gsp.oidc.client.secret}")
    private String clientSecret;
    @Value("${app.oidc.logout.redirect.uri}")
    private String logoutRedirectUri;
    @Value("${app.frontend.context.url}")
    private String frontendContextUrl;
    @Value("${gsp.oidc.nhicard.verify}")
    private String nhicardverifyurl;
    @Value("${gsp.oidc.cert.verify}")
    private String certverifyurl;
    @Value("${gsp.oidc.moeca.verify}")
    private String moecaverifyurl;
    @Value("${gsp.oidc.pii.verify}")
    private String piiverifyurl;
    
    @Value("${tfido.syscode}")
    private String tfidoSyscode;
    @Value("${tfido.apikey}")
    private String tfidoApikey;

    @Autowired
    private VerifyMapper verifyMapper;
	@Autowired
	private PortalResourceMapper portalResourceMapper;
    @Autowired
    private PortalResourceExtMapper portalResourceExtMapper;
    @Autowired
    private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberPrivacyMapper memberPrivacyMapper;
    @Autowired
    private TfidoRecordMapper tfidoRecordMapper;
    @Autowired
    private VerificationLogMapper verificationLogMapper;
	@Autowired
	private CertDuplicateCheckMapper certDuplicateCheckMapper;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired 
	private PortalBoxMapper portalBoxMapper;
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private UlogApiMapperExt ulogApiMapperExt;
    @Autowired
    private LogMemberSessionMapper logMemberSessionMapper;
    @Autowired
    private MemberChangeUtil memberChangeUtil;
    @Autowired
    private MobileIdLogMapper mobileIdLogMapper;
	
	@Autowired
	SampleGimpyFactory captchaFactory;
	
	@Autowired
	private UlogUtil ulogUtil;
    @Autowired
    private LoginUtil loginUtil;
    @Autowired
    private IcscUtils icscUtils;
    @Autowired
    private NhiUtils nhiUtils;
    @Autowired
    private SendLogUtil sendLogUtil;
    @Autowired
    private ValidFactory validFactory;
    @Autowired
    private TWCAUtils twcaUtils;
    @Autowired
    private LogMemberSessionUtil logMemberSessionUtil;
    
    @Value("${mail.enable}")
    private String mailEnable;
    @Value("${moi_Enable}")
    private boolean moiEnable ;
    @Value("${app.frontend.context.url}")
    private String frontendUrl;
	/**
	 * 檔案保存期限(小時)
	 */
	private static int fileStoreTime = 8;
	
    /**
     * 使用者資料，可判斷使用者已登入或未登入
     *
     * @param params
     * @param result
     * @param request
     * @param paging
     * @return ResponseEntity<RestResponseBean> Map<String,Object> data
     * code
     * text
     * data
     * data.userInfo
     * data.signOutUrl
     * data.loginStatus
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/info")
    public ResponseEntity<RestResponseBean> getUserInfo(
            @RequestBody Map<String, Object> params,
            BindingResult result,
            HttpServletRequest request,
            PagingInfo paging) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {    	
        HttpSession session = request.getSession();
        String userAgent = request.getHeader("User-Agent");
        DeviceType deviceType = DeviceUtil.parseDevice(userAgent);
        if(deviceType == DeviceType.Android || deviceType == DeviceType.IPhone) {
            if(session.getMaxInactiveInterval() == 10) {
                session.setMaxInactiveInterval(120);
            }
        } else {
        	session.setMaxInactiveInterval(10);
        }
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        boolean pidCheck = true;
        Map<String, Object> data = new HashMap<String, Object>();
        if (session != null && sr != null) {
        		try {
	            data.put("signOutUrl", session.getAttribute("signOutUrl"));
	            data.put("loginStatus", session.getAttribute("loginStatus"));
	            if (session.getAttribute("pid") != null && ((String) session.getAttribute("pid")).length() > 0 && sr != null) {
	                if (((String) session.getAttribute("pid")).equalsIgnoreCase(SessionMember.getSessionMemberToMember(sr.getMember()).getUid())) {
	                    pidCheck = true;
	                } else {
	                    pidCheck = false;
	                }
	            }
	            if (session.getAttribute("sp_return_url_409") != null && session.getAttribute("sp_return_url_409").toString().trim().length() > 0) {
	                data.put("sp_return_url_409", session.getAttribute("sp_return_url_409").toString().trim());
	                //data.put("sp_logout_url", session.getAttribute("signOutUrl").toString().trim());
	            }
	            logger.debug("==pidCheck==:{}" , pidCheck);
	            logger.debug("==   個人身分證字號  ==:{}" , session.getAttribute("pid"));
	            data.put("pidCheck", pidCheck);
	            if (session.getAttribute("X509type") != null && session.getAttribute("X509type").toString().trim().length() > 0) {
	                data.put("X509type", session.getAttribute("X509type").toString());
	            } else {
	                data.put("X509type", "None");
	            }
	            if (session != null && session.getAttribute("cancelBeforeLogin") != null && (boolean) session.getAttribute("cancelBeforeLogin") == true) {
	                session.setAttribute("cancelBeforeLogin", false);
	                data.put("cancelBeforeLogin", true);
	            } else {
	                data.put("cancelBeforeLogin", false);
	            }
	            System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
	            List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
	            if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
	                boolean boxcheck = false;
	                for (PortalBoxExt pbext : portalBoxExtList) {
	                    if (pbext != null && pbext.getCode().equalsIgnoreCase("200")) {
	                        boxcheck = true;
	                    }
	                }
	                data.put("boxcheck", boxcheck);
	                sr.setBoxcheck(true);
	            } else {
	                data.put("boxcheck", false);
	                sr.setBoxcheck(false);
	            }
	            Member maskMember = new Member();
	            BeanUtils.copyProperties(SessionMember.getSessionMemberToMember(sr.getMember()),maskMember);
	            sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
	            session.setAttribute(SessionRecord.SessionKey, sr);
	            sr.setMember(SessionMember.getSessionMemberToMember(sr.getMember()));
	            data.put("userInfo", sr);
        		}catch(Exception ex) {
        			System.out.println("== getUserInfo ==:"+ex);
        		}
        }
        return responseOK(data);
    }

    /**
     * 健保卡取得驗證網址 (OLD)
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/nhicardVerifyUrl")
    public ResponseEntity<RestResponseBean> getnhicardVerifyUrl(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        if (sr != null) {
            data.put("userInfo", sr);
            String nhicardverifyurltemp = nhicardverifyurl;
            /**
             * 取得 [身分字號];[UUID] NDC.cer加密
             */
            String uuid = UUID.randomUUID().toString();
            String publicKeyStr = getPublicKeyStr();
            System.out.println("公鑰：" + publicKeyStr);
            String content = SessionMember.getSessionMemberToMember(sr.getMember()).getUid() + ";" + uuid;
            String encryptedData = encryptByPublicKey(content);
            System.out.println("加密後：" + encryptedData);
            System.out.println("加密後URLEncode：" + URLEncoder.encode(encryptedData, "UTF-8"));
            nhicardverifyurltemp = nhicardverifyurltemp + URLEncoder.encode(encryptedData, "UTF-8");
            System.out.println("== nhicardverifyurltemp ==:" + nhicardverifyurltemp);
            session.setAttribute("uuidcheck", uuid);
            data.put("uuidcheck", uuid);
            data.put("verifyUrl", nhicardverifyurltemp);
        }
        return responseOK(data);
    }

    /**
     * 健保卡取得驗證網址 (NEW)
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/nhicardVerifyUrl")
    public ResponseEntity<RestResponseBean> postnhicardVerifyUrl(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        String uid = "";
        String birthdate = "";
        String scope = "";
        List<String> scopeList = new ArrayList<String>();
        if (params.get("uid") != null) {
            uid = ValidatorHelper.removeSpecialCharacters(params.get("uid").toString());
        }
        if (params.get("birthdate") != null) {
            birthdate = ValidatorHelper.removeSpecialCharacters(params.get("birthdate").toString());
        }
        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(params.get("scope").toString()) + " ";
        }

        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        if (scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        System.out.println("uid=" + uid);
        System.out.println("birthdate=" + birthdate);

        Map<String, Object> data = new HashMap<String, Object>();
        if (uid != null && birthdate != null) {
            /**
             * 取得 [身分字號];[UUID] NDC.cer加密
             */
            String uuid = UUID.randomUUID().toString();
            session.setAttribute("uuidcheck", uuid);
            session.setAttribute("checkLevel", "nhi");
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("nhi");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            Member member = loginUtil.doNewAutoLogin(record, request, null);
            data.put("member", member);
        }
        return responseOK(data);
    }

    /**
     * 健保卡檢查是否驗證
     *
     * @param uuid
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/nhicardVerifyUrl/check/{uuid}")
    public ResponseEntity<RestResponseBean> postNhicardVerifyUrlcheck(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        if (sr != null) {
            //select verify
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            Date endTime = cal.getTime();
            // 起始時間定為1小時前
            cal.add(Calendar.HOUR_OF_DAY, -1);
            Date startTime = cal.getTime();
            if (session.getAttribute("uuidcheck").toString().equalsIgnoreCase(uuid)) {
				/*VerifyExample verifyExample = new VerifyExample();
				verifyExample.createCriteria().andCtimeGreaterThanOrEqualTo(startTime).andKeyEqualTo(uuid);*/
                Map<String, Object> verifyparam = new HashMap<String, Object>();
                verifyparam.put("sCtime", startTime);
                verifyparam.put("key", uuid);
                List<Verify> verifyList = verifyMapper.selectByExample(verifyparam);
                if (verifyList != null && verifyList.size() > 0) {
                    if (uuid.equalsIgnoreCase(sr.getUuid())) {
                        List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
                        if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
                            boolean boxcheck = false;
                            for (PortalBoxExt pbext : portalBoxExtList) {
                                if (pbext != null && pbext.getCode().equalsIgnoreCase("200")) {
                                    boxcheck = true;
                                }
                            }
                            sr.setBoxcheck(true);
                        } else {
                            sr.setBoxcheck(false);
                        }
                        session.setAttribute(SessionRecord.SessionKey, sr);
                        data.put("userInfo", sr);
                        return responseOK(data);
                    } else {
                        return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
                    }
                } else {
                    return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
                }
            } else {
                return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
            }
        }
        return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
    }

    /**
     * 新版自行讀取健保卡資料驗證
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyNHICard")
    public ResponseEntity<RestResponseBean> VerifyNHICard(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
    	
        HttpSession session = request.getSession();
        List<String> scopeList = new ArrayList<String>();
        String captcha = MapUtils.getString(params, "captcha");
		String captchaKeyVal = MapUtils.getString(params, "captchaKeyVal", "");
		
		Boolean isValid = captchaCheck(request, captcha, captchaKeyVal);
		if(BooleanUtils.isFalse(isValid)) {
			return responseError(SysCode.InvalidCaptcha, "captcha", "圖型驗證碼不正確");
		}

        String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
        String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthdate")));
        String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));

        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setVerificationType(VerificationType.nhi.name());
        vlog.setCtime(new Date());
        if(isDoubleVerify) {
            vlog.setIsDoubleVerify(1);
        }

        String scope = "";
        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }
        
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || params.get("health")==null) {
            System.out.println("== VerifyNHICard 缺少必要參數 ==");
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
	    }
        logger.info("讀取健保卡資料驗證，uid = {}，birthdate = {}", uid, birthdate);
        System.out.println("== 讀取健保卡資料驗證 ==");
        Map<String, Object> health = (Map<String, Object>) params.get("health");
        JSONObject obj = new JSONObject(health);
        String p_Orgid = objToStr(obj.get("p_Orgid"));
        String p_OrgPass = objToStr(obj.get("p_OrgPass"));
        String p_GetBasic = objToStr(obj.get("p_GetBasic"));
        String p_PassCode = objToStr(obj.get("p_PassCode"));
        String p_Random = objToStr(obj.get("p_Random"));
        String p_Token = objToStr(obj.get("p_Token"));
        String p_idno = objToStr(obj.get("p_idno"));
        String p_unit = objToStr(obj.get("p_unit"));
        System.out.println("== 讀取健保卡資料驗證2 ==");
        if (!p_idno.contentEquals(uid)) {
            System.out.println("== 身分驗證錯誤! ==");
            return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
        }

        String result = null;
        try {
            WsAuthGSPSoapProxy proxy = new WsAuthGSPSoapProxy();
            result = proxy.userSign(p_Orgid, p_OrgPass, p_GetBasic, p_PassCode, p_Random, p_Token, p_idno, p_unit);
            System.out.println("== result =="+result);
        } catch (Exception e1) {
        		System.out.println("== 健保卡驗證功能暫時無法使用，請使用其他驗證方式，謝謝。 ==\n"+e1);
            logger.error("[{}], {}", uid, e1);
            logger.error("error msg -> {}", e1.getMessage());
            logger.error("error cause ->  {}", e1.getCause());
            vlog.setStatus(0);
            verificationLogMapper.insertSelective(vlog);
            return responseError(SysCode.CustomError, "id", "健保卡驗證功能暫時無法使用，請使用其他驗證方式，謝謝。");
        } catch(Throwable t) {
            System.out.println("== 健保卡驗證功能暫時無法使用，請使用其他驗證方式，謝謝。 ==\n"+t);
            logger.error("[{}], {}", uid, t);
            vlog.setStatus(0);
            verificationLogMapper.insertSelective(vlog);
            return responseError(SysCode.CustomError, "id", "健保卡驗證功能暫時無法使用，請使用其他驗證方式，謝謝。");
        }
        logger.info("[{}] WsAuthGSPSoap Response = {}", uid, result);
        System.out.println("== WsAuthGSPSoap Response =="+result);

        if(result.contains("發生錯誤")) { //
            vlog.setStatus(0);
        } else {
            vlog.setStatus(1);
        }
        verificationLogMapper.insertSelective(vlog);

        // A0000:檢核正確，A8002:註冊密碼輸入錯誤
        if (!result.contains("A0000")) {
            System.out.println("== 健保卡驗證失敗，請重新操作。 ==");
            String msg = "健保卡驗證失敗，請重新操作。";
            if(result.contains("A8002")) {
                msg = "健保卡註冊密碼錯誤。";
            }
            RestResponseBean rb = new RestResponseBean();
            rb.setCode(SysCode.AuthenticateFail.value());
            rb.setText(msg);

            logger.info("[{}], {}", uid, msg);


            return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
        }

        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        String uuid = UUID.randomUUID().toString();
        //insert into VerifyMapper
        Verify record = new Verify();
        record.setKey(uuid);
        record.setUid(uid);
        record.setBirthdate(sdf6.parse(birthdate));
        record.setCheckLevel("nhi");
        record.setScope(scope);
        record.setCtime(new Date());
        verifyMapper.insertSelective(record);

        Member member = null;
        if(!isDoubleVerify) {
        	member = loginUtil.doNewAutoLogin(record, request, null);
        } else {
            Map<String,Object> meParam = new HashMap<>();
            meParam.put("account", account);
            List<Member> meList = memberMapper.selectByExample(meParam);
            if(meList==null||meList.size()==0) {
            		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
            }else {
            	member = meList.get(0);
            }
            member.setUidVerified(true);
            member.setIsDoubleVerify(true);
            member.setIsDoubleVerifyTime(new Date());
            //memberMapper.updateByPrimaryKeySelective(member);
			Member member1 = new Member();
			member1.setId(ValidatorHelper.limitNumber(member.getId()));
			member1.setUidVerified(true);
			member1.setIsDoubleVerify(true);
			member1.setIsDoubleVerifyTime(new Date());
			memberMapper.updateByPrimaryKeySelective(member1);
        	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        	if (sr != null) {
        		sr.setMember(new SessionMember(member));
        		session.setAttribute(SessionRecord.SessionKey, sr);
        	}            	
        }
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("member", member);

        logger.info("[{}], 健保卡驗證完成", uid);
        
        return responseOK(data);
    }

    /**
     * 新版自行讀取健保卡資料驗證
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/captchainfo")
    public ResponseEntity<RestResponseBean> captchainfo(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
    	HttpSession session = request.getSession();
    	String currentCaptcha = "";
    	String captchaKeyVal = MapUtils.getString(params, "captchaKeyVal", "");
    	System.out.println("2.captchaKeyVal="+captchaKeyVal);
    	try {
    		currentCaptcha = (String) session.getAttribute("currentCaptcha"+captchaKeyVal);
    		System.out.println("2.currentCaptcha="+currentCaptcha);
    	}catch(Exception ex) {
    		logger.info("[{}], captchainfo驗證失敗", ex);
    	}
    	return responseOK(currentCaptcha.replace("", " ").trim());
    }

    /**
     * 新版自行讀取自然人憑證資料驗證 (MyData驗證)
     * 此版有檢查是否是「內政部憑證管理中心」發出憑證
     * @param request
     * @param response
     * @param params
     * isDoubleVerify 二次驗證驗證流程
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyCert")
    public ResponseEntity<RestResponseBean> verifyCert(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
    	
        HttpSession session = request.getSession();
        String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
        String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthdate")));
        String scope = "";
        String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));

        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setVerificationType(VerificationType.cer.name());
        vlog.setCtime(new Date());
        if(isDoubleVerify) {
            vlog.setIsDoubleVerify(1);
        }

        /**
         * 前台scope參數
         * scope == null --> download
         * scope != null
         * path 含有 member 會員專區驗證
         * path 含有 login 會員登入
         */
        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }
        String pkcs7String = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("pkcs7")));
        
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || StringUtils.isEmpty(pkcs7String)) {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
        logger.info("讀取自然人憑證資料驗證，uid = {}，birthdate = {}", uid, birthdate);

        List<String> scopeList = new ArrayList<String>();
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        
        /**
         * scope 驗證紀錄，授權範圍
         * scopeList 紀錄於 session 影響，單筆或多筆下載時，有無超出範圍
         */
        scope = scope.trim();
        logger.info("scope >> {}", scope);
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        if (StringUtils.isNotEmpty(uid) && StringUtils.isNotEmpty(birthdate) && StringUtils.isNotEmpty(pkcs7String)) {
            PKCS7 pkcs7 = new PKCS7(new DerInputStream(base64Decoder.decode(pkcs7String)));
            // 從 PKCS7 檔案中取出憑證。
            X509Certificate[] certs = pkcs7.getCertificates();
            //System.out.println("==certs.length==:"+certs.length);
            //System.out.println(new String(pkcs7.getContentInfo().getData()));
            if(pkcs7!=null) {
	        		try {
	    	            String sn = new String(pkcs7.getContentInfo().getData());
	    	            CertDuplicateCheck certDuplicateCheck = certDuplicateCheckMapper.selectByPrimaryKey(sn);
	    	            if(certDuplicateCheck==null) {
	    	            		CertDuplicateCheck certDuplicateCheckTmp = new CertDuplicateCheck();
	    	            		certDuplicateCheckTmp.setSn(sn);
	    	            		certDuplicateCheckTmp.setCtime(new Date());
	    	            		certDuplicateCheckMapper.insertSelective(certDuplicateCheckTmp);
	    	            }else {
	    	            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
	    	            }
            		}catch(Exception ex) {
            			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            		}
            }else {
            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }

            // 驗證憑證有效性。
            X509Certificate cert = certs[0];
            // 驗證憑證是否已過期。
            cert.checkValidity();
            // 確認是否為自然人憑證
            String issuerDN = cert.getIssuerDN().toString();
            if(!issuerDN.contains("內政部憑證管理中心")) {
            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }
            // 取serialNumber
            String dn = cert.getSubjectDN().toString();
            String serialNumber = (dn.split(",")[0]).split("=")[1];
            //驗證自然人憑證及身分證字號是否為同一人
            Integer status = 1;
            try {
                boolean verify = icscUtils.verifyCert(uid, serialNumber);
                if (!verify) {
                    return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
                }
            } catch (Exception ex) {
                status = 0;
                logger.error(ex.getLocalizedMessage(), ex);
                return responseError(SysCode.CustomError, "id", "自然人憑證驗證功能暫時無法使用，請使用其他驗證方式，謝謝。");
            } finally {
            		vlog.setSignature(pkcs7String);
                vlog.setStatus(status);
                verificationLogMapper.insertSelective(vlog);
            }
            
            String uuid = UUID.randomUUID().toString();
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("cer");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            Member member = null;
            if(!isDoubleVerify) {
            	member = loginUtil.doNewAutoLogin(record, request, null);
            } else {
                Map<String,Object> meParam = new HashMap<>();
                meParam.put("account", account);
                List<Member> meList = memberMapper.selectByExample(meParam);
                if(meList==null||meList.size()==0) {
                		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
                }else {
                	member = meList.get(0);
                }
                member.setUidVerified(true);
                member.setIsDoubleVerify(true);
                member.setIsDoubleVerifyTime(new Date());
                //memberMapper.updateByPrimaryKeySelective(member);
    			Member member1 = new Member();
    			member1.setId(ValidatorHelper.limitNumber(member.getId()));
    			member1.setUidVerified(true);
    			member1.setIsDoubleVerify(true);
    			member1.setIsDoubleVerifyTime(new Date());
    			memberMapper.updateByPrimaryKeySelective(member1);
            	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
            	if (sr != null) {
            		sr.setMember(new SessionMember(member));
            		session.setAttribute(SessionRecord.SessionKey, sr);
            	}            	
            }
            
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("member", member);
            
            return responseOK(data);
        } else {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
    }

    
    /**
     * 金融卡驗證 ATM
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyFic")
    public ResponseEntity<RestResponseBean> verifyFic(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
    	
        HttpSession session = request.getSession();
        String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
        String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthdate")));
        String scope = "";
        String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));
        String token = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("token")));
        String verifyNo = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("verifyNo")));
        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setVerificationType(VerificationType.fic.name());
        vlog.setCtime(new Date());
        if(isDoubleVerify) {
            vlog.setIsDoubleVerify(1);
        }

        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }
        
        String signCerString = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("signCer")));
        
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || StringUtils.isEmpty(signCerString)) {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
        logger.info("讀取金融卡憑證資料驗證，uid = {}，birthdate = {}", uid, birthdate);
        
        boolean checkQueryVerify = twcaUtils.postQueryVerifyResult(uid, verifyNo, token);
        if(!checkQueryVerify) {
        	return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
        }
        
        List<String> scopeList = new ArrayList<String>();
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        logger.info("scope >> {}", scope);
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }
        
        if (StringUtils.isNotEmpty(uid) && StringUtils.isNotEmpty(birthdate) && StringUtils.isNotEmpty(signCerString)) {
        		org.json.JSONObject json = new org.json.JSONObject(signCerString);
            PKCS7 pkcs7 = new PKCS7(new DerInputStream(base64Decoder.decode(json.get("signature").toString())));
            // 從 PKCS7 檔案中取出憑證。
            X509Certificate[] certs = pkcs7.getCertificates();
            String sn = "";
            if(pkcs7!=null) {
	        		try {
	    	            sn = new String(pkcs7.getContentInfo().getData());
	    	            CertDuplicateCheck certDuplicateCheck = certDuplicateCheckMapper.selectByPrimaryKey(sn);
	    	            if(certDuplicateCheck==null) {
	    	            		CertDuplicateCheck certDuplicateCheckTmp = new CertDuplicateCheck();
	    	            		certDuplicateCheckTmp.setSn(sn);
	    	            		certDuplicateCheckTmp.setCtime(new Date());
	    	            		certDuplicateCheckMapper.insertSelective(certDuplicateCheckTmp);
	    	            }else {
	    	            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
	    	            }
            		}catch(Exception ex) {
            			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            		}
            }else {
            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }
            // 驗證憑證有效性。
            X509Certificate cert = certs[0];
            // 驗證憑證是否已過期。
            cert.checkValidity();
            // 確認是否為自然人憑證
            String issuerDN = cert.getIssuerDN().toString();
            String dn = cert.getSubjectDN().toString();
            String serialNumber = (dn.split(",")[0]).split("=")[1];
            String cn = (dn.split(",")[1]).split("=")[1];
            System.out.println("issuerDN="+issuerDN);
            System.out.println("dn="+dn);
            System.out.println("serialNumber="+serialNumber);
            System.out.println("cn="+cn);
            //驗證憑證及身分證字號是否為同一人
    		if(serialNumber!=null&&serialNumber.trim().length()>0) {
    			if(!serialNumber.trim().startsWith(uid)) {
    				return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    			}
    		}else {
    			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    		}
    		
            
            logger.info("第三方驗證中心TWCA驗證，uid = {}，birthdate = {}", uid, birthdate);
            boolean checkPostSignatureVerifyAPIUrlTwid = twcaUtils.postSignatureVerifyAPIUrlTwid(null, null, uid, null, sn.replace(verifyNo, ""), "FIC", json.get("signature").toString());
            logger.info("checkPostSignatureVerifyAPIUrlTwid = {}", checkPostSignatureVerifyAPIUrlTwid);
            if(!checkPostSignatureVerifyAPIUrlTwid) {
            	return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }
            
            Integer status = 1;
            vlog.setSignature(signCerString);
            vlog.setStatus(status);
            verificationLogMapper.insertSelective(vlog);
            
            String uuid = UUID.randomUUID().toString();
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("fic");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            Member member = null;
            if(!isDoubleVerify) {
            	member = loginUtil.doNewAutoLogin(record, request, null);
            } else {
                Map<String,Object> meParam = new HashMap<>();
                meParam.put("account", account);
                List<Member> meList = memberMapper.selectByExample(meParam);
                if(meList==null||meList.size()==0) {
                		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
                }else {
                	member = meList.get(0);
                }
                member.setUidVerified(true);
                member.setIsDoubleVerify(true);
                member.setIsDoubleVerifyTime(new Date());
                //memberMapper.updateByPrimaryKeySelective(member);
    			Member member1 = new Member();
    			member1.setId(ValidatorHelper.limitNumber(member.getId()));
    			member1.setUidVerified(true);
    			member1.setIsDoubleVerify(true);
    			member1.setIsDoubleVerifyTime(new Date());
    			memberMapper.updateByPrimaryKeySelective(member1);
            	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
            	if (sr != null) {
            		sr.setMember(new SessionMember(member));
            		session.setAttribute(SessionRecord.SessionKey, sr);
            	}            	
            }
            
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("member", member);
            return responseOK(data);
        } else {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
    }

    /**
     * 硬體金融憑證驗證
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyFch")
    public ResponseEntity<RestResponseBean> verifyFch(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
        HttpSession session = request.getSession();
        String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
        String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthdate")));
        String scope = "";
        String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));

        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setVerificationType(VerificationType.fic.name());
        vlog.setCtime(new Date());
        if(isDoubleVerify) {
            vlog.setIsDoubleVerify(1);
        }

        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }
        
        String signCerString = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("signCer")));
        
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || StringUtils.isEmpty(signCerString)) {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
        logger.info("讀取硬體金融憑證資料驗證，uid = {}，birthdate = {}", uid, birthdate);

        List<String> scopeList = new ArrayList<String>();
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        logger.info("scope >> {}", scope);
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }
        
        if (StringUtils.isNotEmpty(uid) && StringUtils.isNotEmpty(birthdate) && StringUtils.isNotEmpty(signCerString)) {
        		org.json.JSONObject json = new org.json.JSONObject(signCerString);
            PKCS7 pkcs7 = new PKCS7(new DerInputStream(base64Decoder.decode(json.get("signature").toString())));
            // 從 PKCS7 檔案中取出憑證。
            X509Certificate[] certs = pkcs7.getCertificates();
            if(pkcs7!=null) {
	        		try {
	        			String sn = ValidatorHelper.removeSpecialCharacters(new String(pkcs7.getContentInfo().getData()));
	    	            CertDuplicateCheck certDuplicateCheck = certDuplicateCheckMapper.selectByPrimaryKey(sn);
	    	            if(certDuplicateCheck==null) {
	    	            		CertDuplicateCheck certDuplicateCheckTmp = new CertDuplicateCheck();
	    	            		certDuplicateCheckTmp.setSn(sn);
	    	            		certDuplicateCheckTmp.setCtime(new Date());
	    	            		certDuplicateCheckMapper.insertSelective(certDuplicateCheckTmp);
	    	            }else {
	    	            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
	    	            }
            		}catch(Exception ex) {
            			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            		}
            }else {
            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }
            // 驗證憑證有效性。
            X509Certificate cert = certs[0];
            // 驗證憑證是否已過期。
            cert.checkValidity();
            // 確認是否為自然人憑證
            String issuerDN = cert.getIssuerDN().toString();
            String dn = cert.getSubjectDN().toString();
            String serialNumber = (dn.split(",")[0]).split("=")[1];
            String cn = (dn.split(",")[1]).split("=")[1];
            System.out.println("issuerDN="+issuerDN);
            System.out.println("dn="+dn);
            System.out.println("serialNumber="+serialNumber);
            System.out.println("cn="+cn);
            //驗證憑證及身分證字號是否為同一人
    		if(serialNumber!=null&&serialNumber.trim().length()>0) {
    			if(!serialNumber.trim().startsWith(uid)) {
    				return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    			}
    		}else {
    			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    		}
	    		
            logger.info("第三方驗證中心TWCA驗證，uid = {}，birthdate = {}", uid, birthdate);
            boolean checkPostSignatureVerifyAPIUrlTwid = twcaUtils.postSignatureVerifyAPIUrlTwid(null, null, uid, null, null, "FCH", json.get("signature").toString());
            logger.info("checkPostSignatureVerifyAPIUrlTwid = {}", checkPostSignatureVerifyAPIUrlTwid);
            if(!checkPostSignatureVerifyAPIUrlTwid) {
            	return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }
            
            Integer status = 1;
            vlog.setSignature(signCerString);
            vlog.setStatus(status);
            verificationLogMapper.insertSelective(vlog);
            
            String uuid = UUID.randomUUID().toString();
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("fch");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            Member member = null;
            if(!isDoubleVerify) {
            	member = loginUtil.doNewAutoLogin(record, request, null);
            } else {
                Map<String,Object> meParam = new HashMap<>();
                meParam.put("account", account);
                List<Member> meList = memberMapper.selectByExample(meParam);
                if(meList==null||meList.size()==0) {
                		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
                }else {
                	member = meList.get(0);
                }
                member.setUidVerified(true);
                member.setIsDoubleVerify(true);
                member.setIsDoubleVerifyTime(new Date());
                //memberMapper.updateByPrimaryKeySelective(member);
    			Member member1 = new Member();
    			member1.setId(ValidatorHelper.limitNumber(member.getId()));
    			member1.setUidVerified(true);
    			member1.setIsDoubleVerify(true);
    			member1.setIsDoubleVerifyTime(new Date());
    			memberMapper.updateByPrimaryKeySelective(member1);
            	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
            	if (sr != null) {
            		sr.setMember(new SessionMember(member));
            		session.setAttribute(SessionRecord.SessionKey, sr);
            	}            	
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("member", member);
            return responseOK(data);
        } else {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
    }
    
    /**
     * 軟體金融憑證
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyFcs")
    public ResponseEntity<RestResponseBean> verifyFcs(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
		System.out.println("==== verifyFcs ======");
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
        HttpSession session = request.getSession();
        String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
        String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthdate")));
        String scope = "";
        String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));

        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setVerificationType(VerificationType.fic.name());
        vlog.setCtime(new Date());
        if(isDoubleVerify) {
            vlog.setIsDoubleVerify(1);
        }

        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }
        
        String signCerString = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("signCer")));
        System.out.println("==== signCerString ======\n"+signCerString);
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || StringUtils.isEmpty(signCerString)) {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
        logger.info("讀取軟體金融憑證資料驗證，uid = {}，birthdate = {}", uid, birthdate);

        List<String> scopeList = new ArrayList<String>();
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        logger.info("scope >> {}", scope);
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }
        if (StringUtils.isNotEmpty(uid) && StringUtils.isNotEmpty(birthdate) && StringUtils.isNotEmpty(signCerString)) {
        		org.json.JSONObject json = new org.json.JSONObject(signCerString);
        		System.out.println("==== signature ======\n"+json.get("signature"));
            PKCS7 pkcs7 = new PKCS7(new DerInputStream(base64Decoder.decode(json.get("signature").toString())));
            // 從 PKCS7 檔案中取出憑證。
            X509Certificate[] certs = pkcs7.getCertificates();
            if(pkcs7!=null) {
        		try {
    	            String sn = new String(pkcs7.getContentInfo().getData());
    	            CertDuplicateCheck certDuplicateCheck = certDuplicateCheckMapper.selectByPrimaryKey(sn);
    	            if(certDuplicateCheck==null) {
    	            		CertDuplicateCheck certDuplicateCheckTmp = new CertDuplicateCheck();
    	            		certDuplicateCheckTmp.setSn(sn);
    	            		certDuplicateCheckTmp.setCtime(new Date());
    	            		certDuplicateCheckMapper.insertSelective(certDuplicateCheckTmp);
    	            }else {
    	            		return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    	            }
        		}catch(Exception ex) {
        			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
        		}
            }else {
            	return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }
            // 驗證憑證有效性。
            X509Certificate cert = certs[0];
            // 驗證憑證是否已過期。
            System.out.println("==== cert.checkValidity init ======");
            cert.checkValidity();
            System.out.println("==== cert.checkValidity end ======");
            // 確認是否為自然人憑證
            String issuerDN = cert.getIssuerDN().toString();
            String dn = cert.getSubjectDN().toString();
            String serialNumber = (dn.split(",")[0]).split("=")[1];
            String cn = (dn.split(",")[1]).split("=")[1];
            System.out.println("issuerDN="+issuerDN);
            System.out.println("dn="+dn);
            System.out.println("serialNumber="+serialNumber);
            System.out.println("cn="+cn);
            //驗證憑證及身分證字號是否為同一人
    		if(cn!=null&&cn.trim().length()>0) {
    			if(!cn.trim().startsWith(uid)) {
    				return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    			}
    		}else {
    			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    		}
    		
            logger.info("第三方驗證中心TWCA驗證，uid = {}，birthdate = {}", uid, birthdate);
            boolean checkPostSignatureVerifyAPIUrlTwid = twcaUtils.postSignatureVerifyAPIUrlTwid(null, null, uid, null, null, "FCS", json.get("signature").toString());
            logger.info("checkPostSignatureVerifyAPIUrlTwid = {}", checkPostSignatureVerifyAPIUrlTwid);
            if(!checkPostSignatureVerifyAPIUrlTwid) {
            	return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
            }
            Integer status = 1;
            vlog.setSignature(signCerString);
            vlog.setStatus(status);
            verificationLogMapper.insertSelective(vlog);
            
            String uuid = UUID.randomUUID().toString();
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("fcs");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            Member member = null;
            if(!isDoubleVerify) {
            	member = loginUtil.doNewAutoLogin(record, request, null);
            } else {
                Map<String,Object> meParam = new HashMap<>();
                meParam.put("account", account);
                List<Member> meList = memberMapper.selectByExample(meParam);
                if(meList==null||meList.size()==0) {
                		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
                }else {
                	member = meList.get(0);
                }
                member.setUidVerified(true);
                member.setIsDoubleVerify(true);
                member.setIsDoubleVerifyTime(new Date());
                //memberMapper.updateByPrimaryKeySelective(member
                Member member1 = new Member();
    			member1.setId(ValidatorHelper.limitNumber(member.getId()));
    			member1.setUidVerified(true);
    			member1.setIsDoubleVerify(true);
    			member1.setIsDoubleVerifyTime(new Date());
    			memberMapper.updateByPrimaryKeySelective(member1);
            	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
            	if (sr != null) {
            		sr.setMember(new SessionMember(member));
            		session.setAttribute(SessionRecord.SessionKey, sr);
            	}            	
            }
            
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("member", member);
            return responseOK(data);
        } else {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
    }
    
    /**
     * Mobile-ID 驗證
     * @param request
     * @param response
     * @param params
     * uid
     * birthdate
     * scope
     * prId
     * msisdn 手機號碼
     * clauseVer 版號
     * clauseVerTime 確認時間 
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyMobileId")
    public ResponseEntity<RestResponseBean> verifyMobileId(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
    	request.getSession().setMaxInactiveInterval(120);
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
    	
        HttpSession session = request.getSession();
        String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
        String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthdate")));
        String msisdn = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("msisdn")));
        String clauseVer = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("clauseVer")));
        String clauseVerTime = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("clauseVerTime")));
        
        String scope = "";
        String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));

        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setVerificationType(VerificationType.mobileId.name());
        vlog.setCtime(new Date());
        if(isDoubleVerify) {
            vlog.setIsDoubleVerify(1);
        }

        /**
         * 前台scope參數
         * scope == null --> download
         * scope != null
         * path 含有 member 會員專區驗證
         * path 含有 login 會員登入
         */
        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }

        
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || StringUtils.isEmpty(msisdn) || StringUtils.isEmpty(clauseVer) || StringUtils.isEmpty(clauseVerTime)) {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
        logger.info("讀取Mobile ID資料驗證，uid = {}，birthdate = {}", uid, birthdate);

        List<String> scopeList = new ArrayList<String>();
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        
        /**
         * scope 驗證紀錄，授權範圍
         * scopeList 紀錄於 session 影響，單筆或多筆下載時，有無超出範圍
         */
        scope = scope.trim();
        logger.info("scope >> {}", scope);
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        if (StringUtils.isNotEmpty(uid) && StringUtils.isNotEmpty(birthdate) && StringUtils.isNotEmpty(msisdn) && StringUtils.isNotEmpty(clauseVer) && StringUtils.isNotEmpty(clauseVerTime)) {
            Integer status = 1;
            String VerifyNo = UUID.randomUUID().toString().replaceAll("-", "");
            try {
            	String code = twcaUtils.postServerSideQuery(uid,VerifyNo,msisdn,birthdate,clauseVer,clauseVerTime);
                if (code==null||!code.equalsIgnoreCase("S")) {
                    return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
                }
            } catch (Exception ex) {
                status = 0;
                logger.error(ex.getLocalizedMessage(), ex);
                return responseError(SysCode.CustomError, "id", "Mobile ID驗證功能暫時無法使用，請使用其他驗證方式，謝謝。");
            } finally {
                vlog.setStatus(status);
                verificationLogMapper.insertSelective(vlog);

                try {
                    MobileIdLog mobileIdLog = new MobileIdLog();
                    mobileIdLog.setAccount(account);
                    mobileIdLog.setUid(uid);
                    mobileIdLog.setVerifyNo(VerifyNo);
                    mobileIdLog.setMobile(msisdn);
                    mobileIdLog.setBirthdate(sdf6.parse(birthdate));
                    mobileIdLog.setVersion(clauseVer);
                    mobileIdLog.setCtime(new Date());
                    mobileIdLogMapper.insertSelective(mobileIdLog);
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage(), e);
                }

            }
            
            String uuid = UUID.randomUUID().toString();
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("mobileId");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            Member member = null;
            if(!isDoubleVerify) {
            	member = loginUtil.doNewAutoLogin(record, request, null);
            } else {
                Map<String,Object> meParam = new HashMap<>();
                meParam.put("account", account);
                List<Member> meList = memberMapper.selectByExample(meParam);
                if(meList==null||meList.size()==0) {
                		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
                }else {
                	member = meList.get(0);
                }
                member.setUidVerified(true);
                member.setIsDoubleVerify(true);
                member.setIsDoubleVerifyTime(new Date());
                member.setMobile(msisdn);
                //memberMapper.updateByPrimaryKeySelective(member);
                Member member1 = new Member();
    			member1.setId(ValidatorHelper.limitNumber(member.getId()));
    			member1.setUidVerified(true);
    			member1.setIsDoubleVerify(true);
    			member1.setIsDoubleVerifyTime(new Date());
    			memberMapper.updateByPrimaryKeySelective(member1);
            	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
            	if (sr != null) {
            		sr.setMember(new SessionMember(member));
            		session.setAttribute(SessionRecord.SessionKey, sr);
            	}            	
            }
            
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("member", member);
            
            return responseOK(data);
        } else {
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }
    }
    
    
    /**
     * 工商憑證取得驗證網址
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/MOECAVerifyUrl")
    public ResponseEntity<RestResponseBean> getMOECAVerifyUrl(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        if (sr != null) {
            data.put("userInfo", sr);
            String certverifyurltemp = moecaverifyurl;
            /**
             * 取得 [身分字號];[UUID] NDC.cer加密
             */
            String uuid = UUID.randomUUID().toString();
            String publicKeyStr = getPublicKeyStr();
            System.out.println("公鑰：" + publicKeyStr);
            //String content = "AAAAAAAA"+";"+uuid;
            String content = SessionMember.getSessionMemberToMember(sr.getMember()).getUid() + ";" + uuid;
            String encryptedData = encryptByPublicKey(content);
            System.out.println("加密後：" + encryptedData);
            System.out.println("加密後URLEncode：" + URLEncoder.encode(encryptedData, "UTF-8"));
            certverifyurltemp = certverifyurltemp + URLEncoder.encode(encryptedData, "UTF-8");
            System.out.println("== nhicardverifyurltemp ==:" + certverifyurltemp);
            session.setAttribute("uuidcheck", uuid);
            session.setAttribute("checkLevel", "moeca");
            data.put("uuidcheck", uuid);
            data.put("verifyUrl", certverifyurltemp);
        }
        return responseOK(data);
    }

    @PostMapping("/MOECAVerifyUrl")
    public ResponseEntity<RestResponseBean> postMOECAVerifyUrl(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        String uid = "";
        String birthdate = "";
        String scope = "";
        List<String> scopeList = new ArrayList<String>();
        if (params.get("uid") != null) {
            uid = params.get("uid").toString();
        }
        if (params.get("birthdate") != null) {
            birthdate = params.get("birthdate").toString();
        }
        if (params.get("scope") != null) {
            scope = params.get("scope").toString() + " ";
        }

        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        if (scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        System.out.println("uid=" + uid);
        System.out.println("birthdate=" + birthdate);

        Map<String, Object> data = new HashMap<String, Object>();
        if (uid != null && birthdate != null) {
            String certverifyurltemp = certverifyurl;
            /**
             * 取得 [身分字號];[UUID] NDC.cer加密
             */
            String uuid = UUID.randomUUID().toString();
            String publicKeyStr = getPublicKeyStr();
            System.out.println("公鑰：" + publicKeyStr);
            String content = uid.toUpperCase() + ";" + uuid;
            String encryptedData = encryptByPublicKey(content);
            System.out.println("加密後：" + encryptedData);
            System.out.println("加密後URLEncode：" + URLEncoder.encode(encryptedData, "UTF-8"));
            certverifyurltemp = certverifyurltemp + URLEncoder.encode(encryptedData, "UTF-8");
            System.out.println("== nhicardverifyurltemp ==:" + certverifyurltemp);
            session.setAttribute("uuidcheck", uuid);
            session.setAttribute("checkLevel", "cer");
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("cer");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            data.put("uuidcheck", uuid);
            data.put("verifyUrl", certverifyurltemp);
        }
        return responseOK(data);
    }
    
    /**
     * 新版自行讀取工商憑證資料驗證
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/verifyMOECA")
    public ResponseEntity<RestResponseBean> verifyMOECA(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        String scope = "";

        VerificationLog vlog = new VerificationLog();
        vlog.setVerificationType(VerificationType.moeaca.name());
        vlog.setCtime(new Date());

        if (params.get("scope") != null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }

        String pkcs7String = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("pkcs7")));
        
        if (StringUtils.isEmpty(pkcs7String)) {
			return responseError(SysCode.MissingRequiredParameter,"pkcs7","缺少必要參數!");
		}
        
        List<String> scopeList = new ArrayList<String>();
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        if (scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        PKCS7 pkcs7 = new PKCS7(new DerInputStream(base64Decoder.decode(pkcs7String)));
        // 從 PKCS7 檔案中取出憑證。
        X509Certificate[] certs = pkcs7.getCertificates();
        // 從 PKCS7 檔案中取出簽章UUID
        if(pkcs7!=null) {
    		try {
	            String sn = new String(pkcs7.getContentInfo().getData());
	            CertDuplicateCheck certDuplicateCheck = certDuplicateCheckMapper.selectByPrimaryKey(sn);
	            if(certDuplicateCheck==null) {
	        		CertDuplicateCheck certDuplicateCheckTmp = new CertDuplicateCheck();
	        		certDuplicateCheckTmp.setSn(sn);
	        		certDuplicateCheckTmp.setCtime(new Date());
	        		certDuplicateCheckMapper.insertSelective(certDuplicateCheckTmp);
	            }else {
	            	return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
	            }
    		}catch(Exception ex) {
    			return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
    		}
        }else {
        	return responseError(SysCode.AuthenticateFail, "uid", "身份驗證錯誤!");
        }
        // 驗證憑證有效性。
        X509Certificate cert = certs[0];
        // 驗證憑證是否已過期。
        cert.checkValidity();
        // 確認是否為工商憑證
        String issuerDN = cert.getIssuerDN().toString();
        if(!issuerDN.contains("工商憑證管理中心")) {
        		vlog.setSignature(pkcs7String);
            vlog.setStatus(1);
            verificationLogMapper.insertSelective(vlog);
            return responseError(SysCode.AuthenticateFail, "pkcs7", "身份驗證錯誤!");
        }
        // 取統編
        String dn = cert.getSubjectDN().toString();
        String serialNumber = (dn.split(",")[0]).split("=")[1];

        String uuid = UUID.randomUUID().toString();
        //insert into VerifyMapper
        Verify record = new Verify();
        record.setKey(uuid);
        record.setUid(serialNumber);
        record.setCheckLevel("moeaca");
        record.setScope(scope);
        record.setCtime(new Date());
        verifyMapper.insertSelective(record);

        Member member = loginUtil.doNewAutoLogin(record, request, null);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("member", member);

        String account = encoder.encodeToString((serialNumber.toUpperCase()).getBytes("UTF-8"));
        vlog.setSignature(pkcs7String);
        vlog.setAccount(account);
        vlog.setStatus(1);
        verificationLogMapper.insertSelective(vlog);

        return responseOK(data);
    }


    /**
     * 自然人憑證檢查是否驗證
     *
     * @param uuid
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/MOECAVerifyUrl/check/{uuid}")
    public ResponseEntity<RestResponseBean> postMOECAVerifyUrlcheck(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        if (sr != null) {
            //select verify
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            Date endTime = cal.getTime();
            // 起始時間定為1小時前
            cal.add(Calendar.HOUR_OF_DAY, -1);
            Date startTime = cal.getTime();
            if (session.getAttribute("uuidcheck").toString().equalsIgnoreCase(uuid)) {
				/*VerifyExample verifyExample = new VerifyExample();
				verifyExample.createCriteria().andCtimeGreaterThanOrEqualTo(startTime).andKeyEqualTo(uuid);*/
                Map<String, Object> verifyparam = new HashMap<String, Object>();
                verifyparam.put("sCtime", startTime);
                verifyparam.put("key", uuid);
                List<Verify> verifyList = verifyMapper.selectByExample(verifyparam);
                if (verifyList != null && verifyList.size() > 0) {
                    if (uuid.equalsIgnoreCase(sr.getUuid())) {
                        List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
                        if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
                            boolean boxcheck = false;
                            for (PortalBoxExt pbext : portalBoxExtList) {
                                if (pbext != null && pbext.getCode().equalsIgnoreCase("200")) {
                                    boxcheck = true;
                                }
                            }
                            sr.setBoxcheck(true);
                        } else {
                            sr.setBoxcheck(false);
                        }
                        session.setAttribute(SessionRecord.SessionKey, sr);
                        data.put("userInfo", sr);
                        return responseOK(data);
                    } else {
                        return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
                    }
                } else {
                    return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
                }
            } else {
                return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
            }
        }
        return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
    }

    /**
     * 取得雙證件驗證網址
     *
     * @param checkFields
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/piiVerifyUrl/{checkFields}")
    public ResponseEntity<RestResponseBean> getPIIVerifyUrl(
            @PathVariable("checkFields") String checkFields,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        if (sr != null) {
            data.put("userInfo", sr);
            String piiverifyurltemp = piiverifyurl;
            piiverifyurltemp = piiverifyurltemp + checkFields;
            System.out.println("== piiverifyurltemp ==:" + piiverifyurltemp);
            session.setAttribute("piiverify", "false");
            data.put("verifyUrl", piiverifyurltemp);
            String uuid = UUID.randomUUID().toString();
            session.setAttribute("uuidcheck", uuid);
            session.setAttribute("checkLevel", "pii");
            data.put("uuidcheck", uuid);
        }
        return responseOK(data);
    }

    /**
     * 取得雙證件驗證網址
     * @param checkFields
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/piiVerifyUrl/{checkFields}")
    public ResponseEntity<RestResponseBean> postPIIVerifyUrl(
            @PathVariable("checkFields") String checkFields,
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        String uid = "";
        String birthdate = "";
        String scope = "";
        List<String> scopeList = new ArrayList<String>();
        if (params.get("uid") != null) {
            uid = params.get("uid").toString();
        }
        if (params.get("birthdate") != null) {
            birthdate = params.get("birthdate").toString();
        }
        if (params.get("scope") != null) {
            scope = params.get("scope").toString() + " ";
        }

        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        if (scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        System.out.println("uid=" + uid);
        System.out.println("birthdate=" + birthdate);

        Map<String, Object> data = new HashMap<String, Object>();
        if (uid != null && birthdate != null) {
            String piiverifyurltemp = piiverifyurl;
            piiverifyurltemp = piiverifyurltemp.replace("checkFieldsTmpValue", checkFields);
            /**
             * 取得 [身分字號];[UUID] NDC.cer加密
             */
            String uuid = UUID.randomUUID().toString();
            String publicKeyStr = getPublicKeyStr();
            System.out.println("公鑰：" + publicKeyStr);
            //String content = "AAAAAAAA"+";"+uuid;
            String content = uid + ";" + uuid;
            String encryptedData = encryptByPublicKey(content);
            System.out.println("加密後：" + encryptedData);
            System.out.println("加密後URLEncode：" + URLEncoder.encode(encryptedData, "UTF-8"));
            piiverifyurltemp = piiverifyurltemp + URLEncoder.encode(encryptedData, "UTF-8");
            System.out.println("== piiverifyurltemp ==:" + piiverifyurltemp);
            session.setAttribute("uuidcheck", uuid);
            session.setAttribute("checkLevel", "pii");
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("pii");
            record.setScope(scope);
            record.setCtime(new Date());
            verifyMapper.insertSelective(record);

            data.put("uuidcheck", uuid);
            data.put("verifyUrl", piiverifyurltemp);

        }
        return responseOK(data);
    }


    /**
     * 新版驗證雙證件取資料
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/newPiiVerifyUrl/check")
    public ResponseEntity<RestResponseBean> postPIIVerifyCheckWithoutUrl( @RequestBody Map<String, Object> params,
    		HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
    	
    	HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        String uid = "";
        String birthdate = "";
        String scope = "";
        String multifactorType = MapUtils.getString(params, "multifactorType");
        if(sr != null){
            sr.setMultifactorType(multifactorType);
        }

        boolean isSingle = true;
        List<String> scopeList = new ArrayList<String>();
        if (params.get("uid") != null) {
            uid = params.get("uid").toString();
        }
        if (params.get("birthdate") != null) {
            birthdate = params.get("birthdate").toString();
        }
        if (params.get("scope") != null) {
            scope = params.get("scope").toString() + " ";
        }
        if (params.get("isSingle") != null ){
            isSingle = StringUtils.equals(params.get("isSingle").toString().trim(),"true");
        }

        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {

            String[] prIdArray;

            if(isSingle){
                // 單筆下載
                prIdArray = params.get("prId").toString().trim().split("[,]");
            }else {
                // 多筆下載
                prIdArray = (new String(Base64.getUrlDecoder().decode(params.get("prId").toString().getBytes("UTF-8")),"UTF-8")).split("[,]");
            }

            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        System.out.println("uid=" + uid);
        System.out.println("birthdate=" + birthdate);

        Map<String, Object> data = new HashMap<String, Object>();
        if (uid != null && birthdate != null) {
            /**
             * 取得 [身分字號];[UUID] NDC.cer加密
             */
            String uuid = UUID.randomUUID().toString();
            session.setAttribute("uuidcheck", uuid);
            session.setAttribute("checkLevel", "pii");
            //insert into VerifyMapper
            Verify record = new Verify();
            record.setKey(uuid);
            record.setUid(uid);
            record.setBirthdate(sdf6.parse(birthdate));
            record.setCheckLevel("pii");
            record.setScope(scope);
            record.setCtime(new Date());
            record.setMultifactorType(multifactorType);
            verifyMapper.insertSelective(record);

            Member member = null;
            if(!isDoubleVerify) {
            	member = loginUtil.doNewAutoLogin(record, request, null);
            } else {
            	String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));
                Map<String,Object> meParam = new HashMap<>();
                meParam.put("account", account);
                List<Member> meList = memberMapper.selectByExample(meParam);
                if(meList==null||meList.size()==0) {
                		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
                }else {
                	member = meList.get(0);
                }
                member.setUidVerified(true);
                member.setIsDoubleVerify(true);
                member.setIsDoubleVerifyTime(new Date());
                //memberMapper.updateByPrimaryKeySelective(member);
                Member member1 = new Member();
    			member1.setId(ValidatorHelper.limitNumber(member.getId()));
    			member1.setUidVerified(true);
    			member1.setIsDoubleVerify(true);
    			member1.setIsDoubleVerifyTime(new Date());
    			memberMapper.updateByPrimaryKeySelective(member1);
            	if (sr != null) {
            		try {
						sr.setMember(new SessionMember(member));
					} catch (BadPaddingException e) {
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						e.printStackTrace();
					}
            		session.setAttribute(SessionRecord.SessionKey, sr);
            	}            	
            }
            
            logger.debug("member -> {}" , member);
            data.put("uuidcheck", uuid);
            data.put("member",member);
        }
        return responseOK(data);
    }

    /**
     * 雙證件驗證檢查是否驗證
     *
     * @param uuid
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/piiVerifyUrl/check/{uuid}")
    public ResponseEntity<RestResponseBean> postPIIVerifyUrlcheck(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        if (sr != null) {
            //select verify
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            Date endTime = cal.getTime();
            // 起始時間定為1小時前
            cal.add(Calendar.HOUR_OF_DAY, -1);
            Date startTime = cal.getTime();
            Map<String, Object> verifyparam = new HashMap<String, Object>();
            verifyparam.put("sCtime", startTime);
            verifyparam.put("key", uuid);
            List<Verify> verifyList = verifyMapper.selectByExample(verifyparam);

            if (verifyList != null && verifyList.size() > 0) {
                if (uuid.equalsIgnoreCase(sr.getUuid())) {
                    List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
                    if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
                        boolean boxcheck = false;
                        for (PortalBoxExt pbext : portalBoxExtList) {
                            if (pbext != null && pbext.getCode().equalsIgnoreCase("200")) {
                                boxcheck = true;
                            }
                        }
                        sr.setBoxcheck(true);
                    } else {
                        sr.setBoxcheck(false);
                    }
                    session.setAttribute(SessionRecord.SessionKey, sr);
                    sr.setMember(SessionMember.getSessionMemberToMember(sr.getMember()));
                    data.put("userInfo", sr);
                    return responseOK(data);
                } else {
                    return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
                }
            } else {
                return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
            }
        }
        return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
    }


    @PostMapping("/webservice/nhi")
    public ResponseEntity<RestResponseBean> verifyNhi(HttpServletRequest request,
                                                      HttpServletResponse response) throws KeyManagementException, NoSuchAlgorithmException {
        logger.debug("into webservice nhi..");
        Map<String, Object> data = new HashMap<>();
        String rs = nhiUtils.call();
        data.put("result",rs);

        return responseOK(data);
    }


    /**
     * 每一個驗證方式都要先用WebService Api驗證身分證字號與生日是否相符
     * @param request
     * @param response
     * @param params
     * @return
     */
    @PostMapping("/webservice/birthdate")
    public ResponseEntity<RestResponseBean> verifyUidAndBirth(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		@RequestBody Map<String, Object> params) throws Exception {
        String verificationlevel = MapUtils.getString(params, "verificationlevel");
        if(StringUtils.equals(verificationlevel, "0.5")) {
            return responseOK();
        }

        String uid = "", birthDate = "";
        // 身分證字號
        uid = params.get("uid").toString();
        // 生日
        birthDate = params.get("birthdate").toString();

        Map<String,Object> mParam = new HashMap<>();
        mParam.put("uid", uid.toUpperCase());
        mParam.put("statIsNullorZero", true);
        if(moiEnable) {
            List<Member> mutimList = memberMapper.selectByExample(mParam);
            if(mutimList!=null&&mutimList.size()>1) {
            	//同一身分證號檢查
            	ValidUtil validUtil = validFactory.get(uid);
            	for(Member m:mutimList) {
            		//不同於現有生日做判斷
            		if(m.getBirthdate().compareTo(CDateUtil.ROCDateToAD(birthDate))!=0) {
                    	/**
                    	 * 啟動Thread, 判斷
                    	 */
            			SameUidThread   newThread = new SameUidThread(m,validUtil,memberMapper);
            			newThread.start();
            		}
            	}
            }
        }

        if(StringUtils.isNotEmpty(birthDate)) {
            mParam.put("birthdate", CDateUtil.ROCDateToAD(birthDate));
        }
        List<Member> mList = memberMapper.selectByExample(mParam);
        logger.info("[{},{}] mList is null >> {}", uid, birthDate, mList == null);
        if(mList!=null&&mList.size()>0) {
            logger.info("[{},{}] mList size >> {}", uid, birthDate, mList.size());
            return responseOK();
        }
        logger.info("[{},{}] member is new ", uid, birthDate);

        /**
         * 驗證戶政 Web Service參數
         * 1. 當可驗證時同時補確認多生日會員
         */
        if(moiEnable) {
            Map<String,Object> identifyMap = new HashMap<>();
            identifyMap.put("ck_personId",uid);
            identifyMap.put("ck_birthDate", birthDate);

            ulogUtil.recordFullByPr(null, null, null, null, null, ActionEvent.EVENT_170, null, null, HttpHelper.getRemoteIp(request));
            ValidUtil validUtil = validFactory.get(uid);
            String birthResult = validUtil.call(identifyMap, WebServiceJobId.BirthDate);
            logger.info("birthResult -> {}" , birthResult);

            if(StringUtils.equals(birthResult, ValidUtil.RETURN_FAIL)) {
                return responseError(SysCode.CustomError,"birthdate","驗證系統異常");
            }

            if(!validUtil.isIdentifyValid(birthResult)) {
                return responseError(SysCode.AccountNotSynBirthDateDisabled,"birthdate","身分驗證失敗，請重新輸入。");
            }
            ulogUtil.recordFullByPr(null, null, null, null, null, ActionEvent.EVENT_171, null, null, HttpHelper.getRemoteIp(request));
        }

        return responseOK();
    }


    /**
     * @param checkFields 12 -> 身分證發證日期 + 健保卡卡號 , 10 -> 戶口名簿號碼 + 健保卡卡號
     */
    @PostMapping("/piiVerifyUrl/mydata/{checkFields}")
    public ResponseEntity<RestResponseBean> verifyPiiByMyData(
    		HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable("checkFields") String checkFields,
        @RequestBody Map<String, Object> params) throws Exception {

        String uid = "", idMark = "", idMarkDate = "", nhiCardNumber = "",
                birthDate = "", houseHoldId = "";
        // 驗證戶政 Web Service參數
        Map<String,Object> identifyMap = new HashMap<>();

        // 身分證字號
        uid = params.get("uid").toString();
        // 健保卡卡號
        nhiCardNumber = params.get("nhiCardNumber").toString();
        // 生日
        birthDate = params.get("birthdate").toString();
        identifyMap.put("ck_personId",uid);
        identifyMap.put("ck_birthDate", birthDate);

        String account = encoder.encodeToString((uid.toUpperCase()+CDateUtil.ROCDateToADStr(birthDate)).getBytes("UTF-8"));

        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setCtime(new Date());
        vlog.setType(VerificationPath.download.name());

        logger.debug("check fields -> {}", checkFields);

        if(StringUtils.equals("12", checkFields)){
            // 身分證換發方式
            idMark = params.get("idMark").toString();
            // 身分證換/補證日期
            idMarkDate = params.get("idMarkDate").toString();
            identifyMap.put("ck_idMark",idMark);
            identifyMap.put("ck_idMarkDate",idMarkDate);
            vlog.setVerificationType(VerificationType.pii_1.name());
        }else if(StringUtils.equals("10", checkFields)){
            // 戶口名簿戶號
            houseHoldId = params.get("houseHoldId").toString();
            identifyMap.put("ck_householdId",houseHoldId);
            vlog.setVerificationType(VerificationType.pii_2.name());
        }
        if(moiEnable) {
            ValidUtil validUtil = validFactory.getIdentifyUtil();
            String idMarkResult = validUtil.call(identifyMap,WebServiceJobId.IdMarkAndHouseHold);
            logger.info("idMarkResult -> {}" , idMarkResult);
            boolean idMarkValid = validUtil.isIdentifyValid(idMarkResult);
            logger.info("idMarkValid -> {}" , idMarkValid);

            if(StringUtils.equals(idMarkResult, "Fail")) {
                vlog.setStatus(0);
            } else {
                vlog.setStatus(1);
            }
            verificationLogMapper.insertSelective(vlog);

            if(!idMarkValid){
                if(StringUtils.equals("12",checkFields)){
                    return responseError(SysCode.AuthenticateFail,"身分證發證日期或發證日期種類(領/補/換)錯誤");
                }else if(StringUtils.equals("10",checkFields)){
                    return responseError(SysCode.AuthenticateFail,"戶口名簿戶號錯誤");
                }
            }
        }

        NhiResultEntity entity = nhiUtils.isValid(uid,nhiCardNumber);
        boolean nhiValid = entity.isValid();
        logger.debug("nhiValid -> {}" , nhiValid);
        if(!nhiValid){
            return responseError(SysCode.AuthenticateFail,"健保卡卡號錯誤");
        }

        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        String uuid = UUID.randomUUID().toString();
        if (sr != null) {
            data.put("userInfo", sr);
            session.setAttribute("piiverify", "false");
            session.setAttribute("uuidcheck", uuid);
            session.setAttribute("checkLevel", "pii");

        }
        data.put("uuidcheck", uuid);
        return responseOK(data);
    }
    
    /**
     * midClause
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/midClause")
    public ResponseEntity<RestResponseBean> getMidClause(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        if (sr != null) {
    		MIDClause clause = twcaUtils.getMidClauseUrl();
    		data.put("clause", clause);
        }
        return responseOK(data);
    }    
    
    /**
     * 臨櫃核驗頁面驗證手機號碼、電子信箱 
     */
    @PostMapping("/verifyMobileOrEmailForVerificatoin")
    public ResponseEntity<RestResponseBean> verifyMobileOrEmailForVerificatoin(HttpServletRequest request, HttpServletResponse response,
                                                                @RequestBody Map<String, Object> params) {
        HttpSession session = request.getSession();
        String inform = params.get("inform_method").toString();
        Integer memberId = (Integer) params.get("memberId");
        Member member = memberMapper.selectByPrimaryKey(memberId);
        String ip = HttpHelper.getRemoteIp(request);
        
        // 取得會員資料後傳送驗證碼
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        String uuid = UUID.randomUUID().toString();
        String checkCode = RandomUtils.numericString(8);
        session.setAttribute("uuidcheck", uuid);
        session.setAttribute("checkLevel", "email");
        session.setAttribute("checkCode", checkCode);
        session.setAttribute("uuidcheckTime", new Date().getTime());
        //insert into VerifyMapper
        Verify record = new Verify();
        record.setKey(uuid);
        record.setUid(ValidatorHelper.removeSpecialCharacters(member.getUid()));
        record.setBirthdate(ValidatorHelper.limitDate(member.getBirthdate()));
        record.setCheckLevel("email");
        record.setScope("basic");
        record.setCtime(new Date());
        verifyMapper.insertSelective(record);
        data.put("uuidcheck", uuid);
        data.put("inform_method", member.getInformMethod());

        ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_320, null, null, ip);
        if (StringUtils.equals("email", inform)) {
            //傳送EMAIL
            System.out.println("checkCode=" + checkCode);

            ulogUtil.recordFullByPr(sr, null, null, null, null, null, null, 36, ip);
            /**
             * 寄通知信
             */
            try {
                String from = "mydata_system@ndc.gov.tw";
                String title = "【個人化資料自主運用(MyData)平臺】臨櫃核驗驗證密碼（系統信件）";
                String content = "您好：\n\n"
                        + "感謝您使用個人化資料自主運用(MyData)平臺，您本次的驗證密碼如下，請您於 2 分鐘內提供給臨櫃人員(逾期無效)，以完成臨櫃核驗流程\n"
                        + "\n"
                        + "<div style=\""
                        + "    display: block;"
                        + "    margin: 20px 0;"
                        + "    padding: 10px 20px;"
                        + "    font-size: 1rem;\n"
                        + "    background-color: #efefef;"
                        + "    border: none;\">"
                        + checkCode
                        + "</div>"
                        + "\n"
                        + "此為系統信件，請勿回信。\n"
                        + "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
                        + "<br><br>\n"
                        + "——-\n"
                        + "<strong>我為什麼會收到這封信？</strong><br>\n"
                        + "您會收到此封信件，是因為您於國家發展委員會個人化資料自主運用(MyData)平臺驗證身分，因此，系統會自動發此信通知您。\n"
                        + "<br><br>——-\n"
                        + "<strong>非本人？</strong><br>\n"
                        + "如非您本人同意傳輸或下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
                List<String> tmpReveicers = new ArrayList<String>();
                tmpReveicers.add(member.getEmail());
                /**
                 * 強制寄信 mailEnable == "true"
                 */
                //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_220, null, null, ip);
                MailUtil.sendHtmlMail(tmpReveicers, from, title, content, mailEnable);
                sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
            } catch (Exception ex) {
                System.out.println("--寄信失敗--:\n" + ex);
            }
        } else {
            //傳送簡訊checkCode
            System.out.println("checkCode=" + checkCode);
            String smbody = "MyData平臺-您的驗證碼為" + checkCode + "，請您於2分鐘內提供給臨櫃進行驗證(逾期無效)";
            try {
                //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_200, null, null, ip);
                SMSUtil.sendSms(member.getMobile(), smbody);
                sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        data.put("member", member);
        return responseOK(data);
    }

    /**
     * 臨櫃核驗頁面驗證手機號碼、電子信箱 
     */
    @PostMapping("/agentStep1Confirm")
    public ResponseEntity<RestResponseBean> postAgentStep1Confirm(
    		HttpServletRequest request, HttpServletResponse response,
        @RequestBody Map<String, Object> params) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        HttpSession session = request.getSession();

        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        String ip = HttpHelper.getRemoteIp(request);
        
        String agentVerify = MapUtils.getString(params, "agentVerify");
        if(agentVerify==null||agentVerify.trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());

        Date endTime = cal.getTime();
        // 起始時間定為24小時前(一天前未執行下載要求有效)
        cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
        Date startTime = cal.getTime();

		Map<String,Object> param = new HashMap<String,Object>();
		param.put("agentVerify", agentVerify);
		param.put("sCtime", startTime);
		param.put("idDesc", true);

		List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(params);
		PortalBox box = null;
		if(portalBoxList!=null&&portalBoxList.size()>0) {
			box = portalBoxList.get(0);
		}
        if(box==null||box.getDownloadSn()==null||box.getDownloadSn().trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }



        Member agentMember = null;
        Map<String,Object> mParam = new HashMap<>();
        mParam.put("uid", ValidatorHelper.removeSpecialCharacters(box.getAgentUid().toUpperCase()));
        mParam.put("birthdate", ValidatorHelper.limitDate(box.getAgentBirthdate()));
        mParam.put("statIsNullorZero", true);
        List<Member> mList = memberMapper.selectByExample(mParam);
        if(mList==null||mList.size()==0) {
        		return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
        }else {
        		agentMember = mList.get(0);
        }

        // 代辦人必須等於登入者帳號
        Member member = SessionMember.getSessionMemberToMember(sr.getMember());
        if(!StringUtils.equals(member.getAccount(), agentMember.getAccount())) {
            return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
        }
        
		PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
		PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
		PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));

        Map<String,Object> prdParam = new HashMap<>();
        prdParam.put("psdId", ValidatorHelper.limitNumber(psd.getId()));
        List<PortalResourceDownload> prdList = portalResourceDownloadMapper.selectByExample(prdParam);
        String account = "";
        if(prdList!=null&&prdList.size()>0) {
            account = prdList.get(0).getProviderKey();
        }

        Member organMember = null;
        Map<String,Object> meParam = new HashMap<>();
        meParam.put("account", ValidatorHelper.removeSpecialCharacters(account));
        List<Member> meList = memberMapper.selectByExample(meParam);
        if(meList==null||meList.size()==0) {
            return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
        }else {
            organMember = meList.get(0);
        }
        
        Map<String, Object> data = new HashMap<String, Object>();
		data.put("boxId", box.getId());
		data.put("applyName", MaskUtil.maskNameByO(organMember.getName()));
		data.put("providerName", pp.getName());
		data.put("serviceName", ps.getName());
        return responseOK(data);
    } 
    
    /**
     * 臨櫃核驗頁面驗證手機號碼、電子信箱 
     */
    @PostMapping("/agentStep2Confirm")
    public ResponseEntity<RestResponseBean> postAgentStep2Confirm(
    		HttpServletRequest request, HttpServletResponse response,
        @RequestBody Map<String, Object> params) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        HttpSession session = request.getSession();
        
        String ip = HttpHelper.getRemoteIp(request);
        
        String agentVerify = params.get("agentVerify").toString();
        if(agentVerify==null||agentVerify.trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("agentVerify", agentVerify);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
		Date startTime = cal.getTime();
		param.put("sCtime", startTime);
		param.put("idDesc", true);
		List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(params);
		PortalBox box = null;
		if(portalBoxList!=null&&portalBoxList.size()>0) {
			box = portalBoxList.get(0);
		}
        if(box==null||box.getDownloadSn()==null||box.getDownloadSn().trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }

        Member agentMember = null;
        Map<String,Object> mParam = new HashMap<>();
        mParam.put("uid", ValidatorHelper.removeSpecialCharacters(box.getAgentUid().toUpperCase()));
        mParam.put("birthdate", ValidatorHelper.limitDate(box.getAgentBirthdate()));
        mParam.put("statIsNullorZero", true);
        List<Member> mList = memberMapper.selectByExample(mParam);
        if(mList==null||mList.size()==0) {
        		return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
        }else {
        		agentMember = mList.get(0);
        }
        
        // 取得會員資料後傳送驗證碼
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);

		PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
		PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
		PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));           

        Member member = SessionMember.getSessionMemberToMember(sr.getMember());

        Map<String,Object> prdParam = new HashMap<>();
        prdParam.put("psdId", ValidatorHelper.limitNumber(psd.getId()));
        List<PortalResourceDownload> prdList = portalResourceDownloadMapper.selectByExample(prdParam);
        String account = "";
        if(prdList!=null&&prdList.size()>0) {
            account = prdList.get(0).getProviderKey();
        }

        Member organMember = null;
        Map<String,Object> meParam = new HashMap<>();
        meParam.put("account", ValidatorHelper.removeSpecialCharacters(account));
        List<Member> meList = memberMapper.selectByExample(meParam);
        if(meList==null||meList.size()==0) {
            return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
        }else {
            organMember = meList.get(0);
        }

        
        if (StringUtils.equals("email", organMember.getInformMethod())) {
            /**
             * 代辦人同意，EMAIL (給，委託人)
             */
            try {
    			String from = "mydata_system@ndc.gov.tw";
    			String title = "【個人化資料自主運用(MyData)平臺】代辦人同意代辦通知信（系統信件，請勿回覆）";
    			String content = "您好：\n\n"
    					+ "您申請" + MaskUtil.maskNameByO(agentMember.getName()) + "代辦「"+pp.getName()+" - "+ps.getName()+"」服務，代辦人已於"+sdf8.format(new Date())+"同意。\n"
    					+ "\n"
    					+ "此為系統信件，請勿回信。\n"
    					+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
    					+ "\n"
    					+ "——-\n"
    					+ "我為什麼會收到這封信？\n"
    					+ "您會收到此封信件，是因為國家發展委員會個人化資料自主運用(MyData)平臺服務的代辦服務，當代辦人同意時，系統會自動發此信通知您。";
    			
    			List<String> tmpReveicers = new ArrayList<String>();
    			tmpReveicers.add(organMember.getEmail());
    			MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
    			sendLogUtil.writeSendLog(SendType.email, organMember.getAccount(), organMember.getEmail(), title, content);
            }catch (Exception ex) {
                System.out.println("--寄信失敗--:\n" + ex);
            }
        }else {
            /**
            * 代辦人同意，SMS (給，委託人)
            */
            try {
    			String smbody = "MyData通知-您申請的「"+ ps.getName()+"」服務，代辦人已於"+sdf8.format(new Date())+"同意代辦。";
                try {
    				SMSUtil.sendSms(organMember.getMobile(), smbody);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			sendLogUtil.writeSendLog(SendType.mobile, organMember.getAccount(), organMember.getMobile(), smbody);
            } catch (Exception e) {
            	e.printStackTrace();
            }	
        }
        box.setAgreeAgent(1);
        //portalBoxMapper.updateByPrimaryKeySelective(box);
		PortalBox box1 = new PortalBox();
		box1.setId(ValidatorHelper.limitNumber(box.getId()));
		box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(box.getAgentUid()));
		box1.setAgentBirthdate(ValidatorHelper.limitDate(box.getAgentBirthdate()));
		box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(box.getAgentVerify()));
		box1.setAgreeAgent(1);
		portalBoxMapper.updateByPrimaryKeySelective(box1);
		
        Map<String, Object> data = new HashMap<>();
		data.put("boxId", box.getId());
		data.put("applyName", MaskUtil.maskNameByO(organMember.getName()));
		data.put("providerName", pp.getName());
		data.put("serviceName", ps.getName());
		data.put("member", agentMember);
        return responseOK(data);
    }    
    
    /**
     * 臨櫃核驗頁面，代辦人不同意代辦
     * 1. 情除 portal_box agent_uid,agent_birthdate,agent_verify
     */
    @PostMapping("/agentStep2Reject")
    public ResponseEntity<RestResponseBean> postAgentStep2Reject(
    		HttpServletRequest request, HttpServletResponse response,
        @RequestBody Map<String, Object> params) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        HttpSession session = request.getSession();
        
        String ip = HttpHelper.getRemoteIp(request);
        
        String agentVerify = params.get("agentVerify").toString();
        if(agentVerify==null||agentVerify.trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("agentVerify", agentVerify);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
		Date startTime = cal.getTime();
		param.put("sCtime", startTime);
		param.put("idDesc", true);
		List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(params);
		PortalBox box = null;
		if(portalBoxList!=null&&portalBoxList.size()>0) {
			box = portalBoxList.get(0);
		}
        if(box==null||box.getDownloadSn()==null||box.getDownloadSn().trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }
        
        Member agentMember = null;
        Map<String,Object> mParam = new HashMap<>();
        mParam.put("uid", ValidatorHelper.removeSpecialCharacters(box.getAgentUid().toUpperCase()));
        mParam.put("birthdate", ValidatorHelper.limitDate(box.getAgentBirthdate()));
        mParam.put("statIsNullorZero", true);
        List<Member> mList = memberMapper.selectByExample(mParam);
        if(mList==null||mList.size()==0) {
        		return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
        }else {
        		agentMember = mList.get(0);
        }
        
        
		PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
		PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
		PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));

        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Member member = SessionMember.getSessionMemberToMember(sr.getMember());

        Map<String,Object> prdParam = new HashMap<>();
        prdParam.put("psdId", ValidatorHelper.limitNumber(psd.getId()));
        List<PortalResourceDownload> prdList = portalResourceDownloadMapper.selectByExample(prdParam);
        String account = "";
        if(prdList!=null&&prdList.size()>0) {
            account = prdList.get(0).getProviderKey();
        }

        Member organMember = null;
        Map<String,Object> meParam = new HashMap<>();
        meParam.put("account", ValidatorHelper.removeSpecialCharacters(account));
        List<Member> meList = memberMapper.selectByExample(meParam);
        if(meList==null||meList.size()==0) {
            return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
        }else {
            organMember = meList.get(0);
        }
		
		Map<String,Object> param1 = new HashMap<String,Object>();
		param1.put("psdId", ValidatorHelper.limitNumber(psd.getId()));
		List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
    	if(portalResourceDownloadList!=null) {
    		for(PortalResourceDownload prd:portalResourceDownloadList) {
    			if(prd.getPrId()!=null&&prd.getPrId()>0) {
        			PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
        			//委託人
        			ulogUtil.recordFullByBoxAgent(box, ps, psd.getTxId(), portalResource, prd, null, null, 48, null);
        			//代辦人
        			ulogUtil.recordFullByBoxAgent(box, ps, psd.getTxId(), portalResource, prd, null, null, 56, null);
    			}
    		}
    	}
		box.setAgentVerify(null);
		box.setAgentUid(null);
		box.setAgentBirthdate(null);
        box.setAgreeAgent(null);
		//portalBoxMapper.updateByPrimaryKeySelective(box);
		PortalBox box1 = new PortalBox();
		box1.setId(ValidatorHelper.limitNumber(box.getId()));
		box1.setAgentVerify(null);
		box1.setAgentUid(null);
		box1.setAgentBirthdate(null);
		box1.setAgreeAgent(null);
		portalBoxMapper.updateByPrimaryKeySelective(box1);
		
        Map<String, Object> data = new HashMap<String, Object>();
		/**
		 * 寄送代辦通知簡訊或EMAIL
		 */
		if(StringUtils.equals(organMember.getInformMethod(), "email") && BooleanUtils.isTrue(organMember.getEmailVerified())) {
			String from = "mydata_system@ndc.gov.tw";
			String title = "【個人化資料自主運用(MyData)平臺】代辦人不同意代辦通知信（系統信件，請勿回覆）";
			String content = "您好：\n\n"
					+ "您申請" + MaskUtil.maskNameByO(agentMember.getName()) + "代辦，「"+pp.getName()+" - "+ps.getName()+"」服務，代辦人已於"+sdf8.format(new Date())+"不同意此項代辦作業，請您重新指定代辦人。\n"
					+ "\n"
					+ "此為系統信件，請勿回信。\n"
					+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
					+ "\n"
					+ "——-\n"
					+ "我為什麼會收到這封信？\n"
					+ "您會收到此封信件，是因為國家發展委員會個人化資料自主運用(MyData)平臺服務的代辦服務，當代辦人不同意時，系統會自動發此信通知您。";
			
			List<String> tmpReveicers = new ArrayList<String>();
			tmpReveicers.add(organMember.getEmail());
			MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
			sendLogUtil.writeSendLog(SendType.email, organMember.getAccount(), organMember.getEmail(), title, content);
		} else if (StringUtils.equals(organMember.getInformMethod(), "mobile") && BooleanUtils.isTrue(organMember.getMobileVerified())) {
			String smbody = "MyData通知-您申請「"+ps.getName()+"」服務代辦，代辦人已於"+sdf8.format(new Date())+"終止此項代辦，請重新指定代辦人。";
            try {
				SMSUtil.sendSms(organMember.getMobile(), smbody);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendLogUtil.writeSendLog(SendType.mobile, organMember.getAccount(), organMember.getMobile(), smbody);
		} else {
			System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
		}
		data.put("boxId", box.getId());
		data.put("applyName", MaskUtil.maskNameByO(organMember.getName()));
		data.put("providerName", pp.getName());
		data.put("serviceName", ps.getName());
        return responseOK(data);
    }      
    
    /**
     * 臨櫃核驗頁面驗證手機號碼、電子信箱 
     */
    @PostMapping("/agentStep4Confirm")
    public ResponseEntity<RestResponseBean> postAgentStep3Confirm(
    		HttpServletRequest request, HttpServletResponse response,
        @RequestBody Map<String, Object> params) {
        HttpSession session = request.getSession();
        
        String ip = HttpHelper.getRemoteIp(request);
        
        String agentVerify = params.get("agentVerify").toString();
        if(agentVerify==null||agentVerify.trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("agentVerify", agentVerify);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
		Date sCtime = cal.getTime();
		param.put("sCtime", sCtime);
		param.put("idDesc", true);

		List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(params);
		PortalBox p = null;
		if(portalBoxList!=null&&portalBoxList.size()>0) {
			p = portalBoxList.get(0);
		}
        if(p==null||p.getDownloadSn()==null||p.getDownloadSn().trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }

		/**
		 * 算多少秒開始
		 */
		PortalBoxExt box = new PortalBoxExt();
		BeanUtils.copyProperties(p, box);
		PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
		PortalService portalService = null;
		if (portalServiceDownload != null) {
			box.setCode(portalServiceDownload.getCode());
			box.setApplyTime(portalServiceDownload.getCtime());
			portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getPsId()));
			if (portalService != null) {
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getProviderId()));
				if (portalProvider != null) {
					box.setPrName(portalService.getName());
					box.setPsId(portalService.getPsId());
					box.setCateId(portalService.getCateId());
					box.setProviderName(portalProvider.getName());
				}
			}
		}
		/**
		 * 計算剩餘時間
		 */
		Calendar cal1 = GregorianCalendar.getInstance();
		cal1.setTime(box.getCtime());
		cal1.add(Calendar.MINUTE, 20);
		Date endDate = cal1.getTime();
		String yearStr = sdf4.format(endDate);
		int year = Integer.valueOf(yearStr) - 1911;
		String monthDayHousrMinSec = sdf5.format(endDate);
		// endTimeNote
		box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
		Date now = new Date();
		/**
		 * box.stat 0 正常 1 逾期
		 */
		if (now.before(endDate)) {
			if(box.getStat()>0) {
				box.setStat(1);
			}else {
				box.setStat(0);
			}
		} else {
			box.setStat(1);
		}
		box.setCanPreView(0);

		/**
		 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
		 * (ctime+waitTime)-------> 申請處理中
		 */
		long ctime = box.getCtime().getTime();
		long nowTime = (new Date()).getTime();

		/**
		 * 算多少秒過期
		 */
		long endtimelong = cal1.getTimeInMillis();
		Calendar cal2 = GregorianCalendar.getInstance();
		cal2.setTime(new Date());
		long nowtimelong = cal2.getTimeInMillis();
		if (nowtimelong >= endtimelong) {
			box.setDataTime(0);
			box.setMin(0);
		} else {
			int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
			int waitmin = waittimesec / 60;
			box.setDataTime(waittimesec);
			box.setMin(waitmin);
		}

		/**
		 * 算多少秒開始
		 */
		if (nowTime >= ctime) {
			box.setStartTime(-1);
		} else {
			int startTime = (int) ((ctime - nowTime) / 1000);
			box.setStartTime(startTime);
		}

		/**
		 * 確認是否有代辦人
		 */
		if(box.getAgentUid()!=null&&box.getAgentBirthdate()!=null) {
            Map<String,Object> mmParam = new HashMap<>();
            mmParam.put("uid", box.getAgentUid().toUpperCase());
            mmParam.put("birthdate", box.getAgentBirthdate());
            mmParam.put("statIsNullorZero", true);
            List<Member> mmList = memberMapper.selectByExample(mmParam);
            if(mmList!=null&&mmList.size()>0) {
            	box.setAgentName(MaskUtil.maskNameByO(mmList.get(0).getName()));
            }
		}

		/**
		 * 相關box
		 */
		List<PortalBoxExt> tmpPortalBoxExtList = portalResourceExtMapper.selectMyBoxForCounterByPsdId(box.getPsdId());
		List<PortalBoxExt> tmpfinalPortalBoxExtList = new ArrayList<PortalBoxExt>();
		List<Integer> tmpforFinalPrIdCheck = new ArrayList<Integer>();
		if(tmpPortalBoxExtList!=null&&tmpPortalBoxExtList.size()>0) {
			for(PortalBoxExt tp:tmpPortalBoxExtList) {
				PortalBoxExt tmpbox = new PortalBoxExt();
				BeanUtils.copyProperties(tp, tmpbox);
				PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(tmpbox.getDownloadSn());
				PortalResource portalResource = null;
				if (portalResourceDownload != null) {
					tmpbox.setCode(portalResourceDownload.getCode());
					portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceDownload.getPrId()));
					if (portalResource != null) {
						PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
						if (portalProvider != null) {
							tmpbox.setPrName(portalResource.getName());
							tmpbox.setPrId(portalResource.getPrId());
							tmpbox.setCateId(portalResource.getCateId());
							tmpbox.setProviderName(portalProvider.getName());
							tmpbox.setRandomVar(UUID.randomUUID().toString());
						}
					}
				}
				/**
				 * 計算剩餘時間
				 */
				Calendar cal3 = GregorianCalendar.getInstance();
				cal3.setTime(tmpbox.getCtime());
				cal3.add(Calendar.MINUTE, 20);
				Date tmpendDate = cal3.getTime();
				String tmpyearStr = sdf4.format(tmpendDate);
				int tmpyear = Integer.valueOf(tmpyearStr) - 1911;
				String tmpmonthDayHousrMinSec = sdf5.format(tmpendDate);
				// endTimeNote
				tmpbox.setEndTimeNote("此序號" + tmpyear + tmpmonthDayHousrMinSec + "前有效");
				Date tmpnow = new Date();
				if (tmpnow.before(tmpendDate)) {
					tmpbox.setStat(0);
				} else {
					tmpbox.setStat(1);
				}

				if (tmpbox.getFiles() != null && portalResource != null
						&& !"API.vTL7cCtoSl".equalsIgnoreCase(portalResource.getResourceId()) 
						&& !"API.mBqP4awHJY".equalsIgnoreCase(portalResource.getResourceId())) {
					tmpbox.setCanPreView(1);
				} else {
					tmpbox.setCanPreView(0);
				}
				/**
				 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
				 * (ctime+waitTime)-------> 申請處理中
				 */
				long tmpctime = tmpbox.getCtime().getTime();
				int tmpwaitTime = tmpbox.getWaitTime() * 1000;
				long tmpnowTime = (new Date()).getTime();

				/**
				 * 算多少秒過期
				 */
				long tmpendtimelong = cal3.getTimeInMillis();
				Calendar cal4 = GregorianCalendar.getInstance();
				cal4.setTime(new Date());
				long tmpnowtimelong = cal4.getTimeInMillis();
				if (tmpnowtimelong >= tmpendtimelong) {
					tmpbox.setDataTime(0);
					tmpbox.setMin(0);
				} else {
					int waittimesec = (int) ((tmpendtimelong - tmpnowtimelong) / 1000);
					int waitmin = waittimesec / 60;
					tmpbox.setDataTime(waittimesec);
					tmpbox.setMin(waitmin);
				}

				/**
				 * 算多少秒開始
				 */
				if (tmpnowTime >= tmpctime) {
					tmpbox.setStartTime(-1);
				} else {
					int startTime = (int) ((tmpctime - tmpnowTime) / 1000);
					tmpbox.setStartTime(startTime);
				}

				if ((tmpnowTime + tmpwaitTime) >= tmpctime) {
					if (!tmpforFinalPrIdCheck.contains(tmpbox.getPrId())) {
						PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(tmpbox.getDownloadSn());
						if (download.getStat() != 9) {
							System.out.println("download.getBatchId()===:" + download.getBatchId());
							if (tmpbox.getPrName() != null && portalResource != null && tmpbox.getPrName().trim().length() > 0) {
								tmpfinalPortalBoxExtList.add(tmpbox);
								session.setAttribute("randomVar"+(portalResource.getPrId()==null?"":portalResource.getPrId()), tmpbox.getRandomVar());
								tmpforFinalPrIdCheck.add(tmpbox.getPrId());
							}
						}
					}
				}
			}

			box.setBoxList(tmpfinalPortalBoxExtList);
		}

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("box", box);
        data.put("downloadVerify", box.getDownloadVerify());
        return responseOK(data);
    }
    
    
    @PostMapping("/createRandomVerify")
    public ResponseEntity<RestResponseBean> postCreateRandomVerify(
    		HttpServletRequest request, HttpServletResponse response,
        @RequestBody Map<String, Object> params) {
        String ip = HttpHelper.getRemoteIp(request);
        
        String downloadVerify = params.get("downloadVerify").toString();
        if(downloadVerify==null||downloadVerify.trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("downloadVerify", downloadVerify);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
		Date sCtime = cal.getTime();
		param.put("sCtime", sCtime);
		param.put("idDesc", true);
		List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(params);
		PortalBox p = null;
		if(portalBoxList!=null&&portalBoxList.size()>0) {
			p = portalBoxList.get(0);
		}
        if(p==null||p.getDownloadSn()==null||p.getDownloadSn().trim().length()==0) {
        	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
        }
		RandomUtils ru = new RandomUtils();
		String tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
		while (isDuplicateDownloadVerify(tmpDownloadVerify)) {
			tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
		}
		//延後時間
		//p.setCtime(new Date());
		//portalBoxMapper.updateByPrimaryKey(p);
		if(p.getPsdId()!=null) {
			List<PortalBoxExt> tmpPortalBoxExtList = portalResourceExtMapper.selectMyBoxForCounterByPsdId(ValidatorHelper.limitNumber(p.getPsdId()));
			if(tmpPortalBoxExtList!=null&&tmpPortalBoxExtList.size()>0) {
				for(PortalBox tbox:tmpPortalBoxExtList) {
					tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
					while (isDuplicateDownloadVerify(tmpDownloadVerify)) {
						tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
					}
					tbox.setStat(0);
					tbox.setCtime(new Date());
					tbox.setDownloadVerify(tmpDownloadVerify);
					//portalBoxMapper.updateByPrimaryKey(tbox);
					PortalBox box1 = new PortalBox();
					box1.setId(ValidatorHelper.limitNumber(tbox.getId()));
					tbox.setStat(0);
					tbox.setCtime(new Date());
					tbox.setDownloadVerify(tmpDownloadVerify);
					//必須判斷欄位
					box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(tbox.getAgentUid()));
					box1.setAgentBirthdate(ValidatorHelper.limitDate(tbox.getAgentBirthdate()));
					box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(tbox.getAgentVerify()));
					box1.setAgreeAgent(ValidatorHelper.limitNumber(tbox.getAgreeAgent()));
					portalBoxMapper.updateByPrimaryKeySelective(box1);
				}
			}
		}
		
		/**
		 * box child 先改，再改parent
		 */
		p.setStat(0);
		p.setDownloadVerify(tmpDownloadVerify);
		p.setCtime(new Date());
		//portalBoxMapper.updateByPrimaryKey(p);
		PortalBox p1 = new PortalBox();
		p1.setId(ValidatorHelper.limitNumber(p.getId()));
		p.setStat(0);
		p.setDownloadVerify(tmpDownloadVerify);
		p.setCtime(new Date());
		//必須判斷欄位
		p1.setAgentUid(ValidatorHelper.removeSpecialCharacters(p.getAgentUid()));
		p1.setAgentBirthdate(ValidatorHelper.limitDate(p.getAgentBirthdate()));
		p1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(p.getAgentVerify()));
		p1.setAgreeAgent(ValidatorHelper.limitNumber(p.getAgreeAgent()));
		portalBoxMapper.updateByPrimaryKeySelective(p1);
		
        Member agentMember = null;
        Map<String,Object> mParam = new HashMap<>();
        mParam.put("uid", ValidatorHelper.removeSpecialCharacters(p.getAgentUid().toUpperCase()));
        mParam.put("birthdate", ValidatorHelper.limitDate(p.getAgentBirthdate()));
        mParam.put("statIsNullorZero", true);
        List<Member> mList = memberMapper.selectByExample(mParam);
        if(mList==null||mList.size()==0) {
        		return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
        }else {
        		agentMember = mList.get(0);
        }
		/**
		 * 算多少秒開始
		 */
		PortalBoxExt box = new PortalBoxExt();
		BeanUtils.copyProperties(p, box);
		PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
		PortalService portalService = null;
		if (portalServiceDownload != null) {
			box.setCode(portalServiceDownload.getCode());
			box.setApplyTime(portalServiceDownload.getCtime());
			portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getPsId()));
			if (portalService != null) {
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getProviderId()));
				if (portalProvider != null) {
					box.setPrName(portalService.getName());
					box.setPsId(portalService.getPsId());
					box.setCateId(portalService.getCateId());
					box.setProviderName(portalProvider.getName());
				}
			}
		}
		/**
		 * 計算剩餘時間
		 */
		Calendar cal1 = GregorianCalendar.getInstance();
		cal1.setTime(box.getCtime());
		cal1.add(Calendar.MINUTE, 20);
		Date endDate = cal1.getTime();
		String yearStr = sdf4.format(endDate);
		int year = Integer.valueOf(yearStr) - 1911;
		String monthDayHousrMinSec = sdf5.format(endDate);
		// endTimeNote
		box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
		Date now = new Date();
		/**
		 * box.stat 0 正常 1 逾期
		 */
		if (now.before(endDate)) {
			if(box.getStat()>0) {
				box.setStat(1);
			}else {
				box.setStat(0);
			}
		} else {
			box.setStat(1);
		}
		box.setCanPreView(0);
		/**
		 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
		 * (ctime+waitTime)-------> 申請處理中
		 */
		long ctime = box.getCtime().getTime();
		long nowTime = (new Date()).getTime();
		
		/**
		 * 算多少秒過期
		 */
		long endtimelong = cal1.getTimeInMillis();
		Calendar cal2 = GregorianCalendar.getInstance();
		cal2.setTime(new Date());
		long nowtimelong = cal2.getTimeInMillis();
		if (nowtimelong >= endtimelong) {
			box.setDataTime(0);
			box.setMin(0);
		} else {
			int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
			int waitmin = waittimesec / 60;
			box.setDataTime(waittimesec);
			box.setMin(waitmin);
		}

		/**
		 * 算多少秒開始
		 */
		if (nowTime >= ctime) {
			box.setStartTime(-1);
		} else {
			int startTime = (int) ((ctime - nowTime) / 1000);
			box.setStartTime(startTime);
		}

		/**
		 * 確認是否有代辦人
		 */
		if(box.getAgentUid()!=null&&box.getAgentBirthdate()!=null) {
            Map<String,Object> mmParam = new HashMap<>();
            mmParam.put("uid", box.getAgentUid().toUpperCase());
            mmParam.put("birthdate", box.getAgentBirthdate());
            mmParam.put("statIsNullorZero", true);
            List<Member> mmList = memberMapper.selectByExample(mmParam);
            if(mmList!=null&&mmList.size()>0) {
            	box.setAgentName(MaskUtil.maskNameByO(mmList.get(0).getName()));
            }
		}
		
		/**
		 * 相關box
		 */
		List<PortalBoxExt> tmpPortalBoxExtList = portalResourceExtMapper.selectMyBoxForCounterByPsdId(box.getPsdId());
		List<PortalBoxExt> tmpfinalPortalBoxExtList = new ArrayList<PortalBoxExt>();
		List<Integer> tmpforFinalPrIdCheck = new ArrayList<Integer>();
		if(tmpPortalBoxExtList!=null&&tmpPortalBoxExtList.size()>0) {
			for(PortalBoxExt tp:tmpPortalBoxExtList) {
				PortalBoxExt tmpbox = new PortalBoxExt();
				BeanUtils.copyProperties(tp, tmpbox);
				PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(tmpbox.getDownloadSn());
				PortalResource portalResource = null;
				if (portalResourceDownload != null) {
					tmpbox.setCode(portalResourceDownload.getCode());
					portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceDownload.getPrId()));
					if (portalResource != null) {
						PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
						if (portalProvider != null) {
							tmpbox.setPrName(portalResource.getName());
							tmpbox.setPrId(portalResource.getPrId());
							tmpbox.setCateId(portalResource.getCateId());
							tmpbox.setProviderName(portalProvider.getName());
						}
					}
				}
				/**
				 * 計算剩餘時間
				 */
				Calendar cal3 = GregorianCalendar.getInstance();
				cal3.setTime(tmpbox.getCtime());
				cal3.add(Calendar.MINUTE, 20);
				Date tmpendDate = cal3.getTime();
				String tmpyearStr = sdf4.format(tmpendDate);
				int tmpyear = Integer.valueOf(tmpyearStr) - 1911;
				String tmpmonthDayHousrMinSec = sdf5.format(tmpendDate);
				// endTimeNote
				tmpbox.setEndTimeNote("此序號" + tmpyear + tmpmonthDayHousrMinSec + "前有效");
				Date tmpnow = new Date();
				if (tmpnow.before(tmpendDate)) {
					tmpbox.setStat(0);
				} else {
					tmpbox.setStat(1);
				}

				if (tmpbox.getFiles() != null && portalResource != null && portalResource.getResourceId() !=null
						&& !portalResource.getResourceId().equalsIgnoreCase("API.vTL7cCtoSl") 
						&& !portalResource.getResourceId().equalsIgnoreCase("API.mBqP4awHJY")) {
					tmpbox.setCanPreView(1);
				} else {
					tmpbox.setCanPreView(0);
				}

				/**
				 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
				 * (ctime+waitTime)-------> 申請處理中
				 */
				long tmpctime = tmpbox.getCtime().getTime();
				int tmpwaitTime = tmpbox.getWaitTime() * 1000;
				long tmpnowTime = (new Date()).getTime();

				/**
				 * 算多少秒過期
				 */
				long tmpendtimelong = cal3.getTimeInMillis();
				Calendar cal4 = GregorianCalendar.getInstance();
				cal4.setTime(new Date());
				long tmpnowtimelong = cal4.getTimeInMillis();
				if (tmpnowtimelong >= tmpendtimelong) {
					tmpbox.setDataTime(0);
					tmpbox.setMin(0);
				} else {
					int waittimesec = (int) ((tmpendtimelong - tmpnowtimelong) / 1000);
					int waitmin = waittimesec / 60;
					tmpbox.setDataTime(waittimesec);
					tmpbox.setMin(waitmin);
				}

				/**
				 * 算多少秒開始
				 */
				if (tmpnowTime >= tmpctime) {
					tmpbox.setStartTime(-1);
				} else {
					int startTime = (int) ((tmpctime - tmpnowTime) / 1000);
					tmpbox.setStartTime(startTime);
				}

				if ((tmpnowTime + tmpwaitTime) >= tmpctime) {
					if (!tmpforFinalPrIdCheck.contains(tmpbox.getPrId())) {
						PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(tmpbox.getDownloadSn());
						if (download.getStat() != 9) {
							System.out.println("download.getBatchId()===:" + download.getBatchId());
							if (tmpbox.getPrName() != null && tmpbox.getPrName().trim().length() > 0) {
								tmpfinalPortalBoxExtList.add(tmpbox);
								tmpforFinalPrIdCheck.add(tmpbox.getPrId());
							}
						}
					}
				}
			}
			
			box.setBoxList(tmpfinalPortalBoxExtList);
		}
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("box", box);
        data.put("downloadVerify", box.getDownloadVerify()); 	
    	return responseOK(data);
    }
    /**
     * member頁面驗證手機號碼、電子信箱
     */
    @PostMapping("/verifyMobileOrEmail")
    public ResponseEntity<RestResponseBean> verifyMobileOrEmail(HttpServletRequest request, HttpServletResponse response,
                                                                @RequestBody Map<String, Object> params) {
        HttpSession session = request.getSession();
        String inform = params.get("inform_method").toString();
        Integer memberId = (Integer) params.get("memberId");
        Member member = memberMapper.selectByPrimaryKey(memberId);
        // 取得會員資料後傳送驗證碼
        Map<String, Object> data = sendCheckCodeByInformMethod(request, session, member, inform);
        return responseOK(data);
    }
    
    /**
     * 使用者操作歷史記錄，以prId和使用者帳號查詢
     * @param params
     * @param result
     * @param request
     * @param paging
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/log")
    public ResponseEntity<RestResponseBean> getNewUserLog(
            @RequestBody Map<String, Object> params,
            BindingResult result,
            HttpServletRequest request,
            PagingInfo paging) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
    	
    	HttpSession session = request.getSession();
    	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
    	
        PortalResource portalResource = null;
        PortalProvider portalProvider = null;
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
        	portalResource = portalResourceMapper.selectByPrimaryKey(Integer.valueOf(params.get("prId").toString().trim()));
			// providerName
			portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
        }
        
		List<UlogApi> ulogList = null;
		if (sr != null && sr.getMember().getAccount() != null && portalResource!=null) {
			ulogList = ulogApiMapperExt.getLogByAccountAndResourceIdAndAuditEventForRow(
					SessionMember.getSessionMemberToMember(sr.getMember()).getAccount(),
					ValidatorHelper.removeSpecialCharacters(portalResource.getResourceId()));
		}
		/**
		 * dp download log
		 */
		List<UlogApiExt> ulogExtList = new ArrayList<UlogApiExt>();
		if (ulogList != null && ulogList.size() > 0) {
			for (UlogApi ulog : ulogList) {
				List<UlogApiExt> ulogExtMiddleList = new ArrayList<UlogApiExt>();
				List<UlogApi> ulog1List = new ArrayList<UlogApi>();
				if (sr != null && sr.getMember().getAccount() != null && portalResource!=null) {
					ulog1List = ulogApiMapperExt.getLogByAccountAndResourceIdAndAuditEventAndTransactionUidForResource(
						SessionMember.getSessionMemberToMember(sr.getMember()).getAccount(),
						ValidatorHelper.removeSpecialCharacters(portalResource.getResourceId()),ValidatorHelper.removeSpecialCharacters(ulog.getTransactionUid()));
				
				}
				
				for(UlogApi ulog1:ulog1List) {
					UlogApiExt titleExt = new UlogApiExt();
					UlogApiExt ulogExt = new UlogApiExt();
					try {
						org.apache.commons.beanutils.BeanUtils.copyProperties(ulogExt, ulog1);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
					/**
					 * 資料集： 11 申請：您申請此筆資料/服務 12 同意：您同意服務條款 13 驗證：您完成身分驗證 14 傳輸：您同意 MyData 傳送資料給服務提供者
					 * 15 儲存：您下載資料 16 條碼取用：機關取用資料 17 服務應用：MyData 將資料傳送給服務提供者
					 */
					if (ulogExt.getClientId() == null || ulogExt.getClientId().equalsIgnoreCase("CLI.mydata.portal")) {
						if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 11) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("申請");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 12) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("同意");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 13) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("驗證");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 14) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("傳輸");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 15) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("自行儲存");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 16) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("臨櫃核驗-轉存");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 17) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("線上服務");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 18) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("申請完成");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 19) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("申請失敗");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 20) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("申請失敗");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 28) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("服務申請終止");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 29) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("服務申請終止");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 31) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("資料下載-預覽");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 32) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("臨櫃核驗-預覽");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 41) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("委託申請");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 42) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("同意");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 43) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("授權成功");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 44) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("授權失敗");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 45) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("委託終止");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 46) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("委託逾時");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 47) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("委託完成");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 48) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("不同意");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 51) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("同意代辦");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 52) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("委託終止");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 53) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("委託逾時");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 54) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("臨櫃核驗-預覽");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 55) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("臨櫃核驗-轉存");
						} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 56) {
							List<String> scopeItemList = new ArrayList<String>();
							ulogExt.setProviderName(portalProvider==null?"":portalProvider.getName());
							ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("不同意");
						}
						
					} else if (ulogExt.getClientId() != null) {
						Map<String, Object> psparam = new HashMap<String, Object>();
						psparam.put("clientId", ulogExt.getClientId());
						List<PortalService> tmpPortalServiceList = portalServiceMapper.selectByExample(psparam);
						if (tmpPortalServiceList != null && tmpPortalServiceList.size() > 0) {
							PortalService tmpPortalService = tmpPortalServiceList.get(0);
							PortalProvider psPortalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(tmpPortalService.getProviderId()));
							/**
							 * 資料集： 11 申請：您申請此筆資料/服務 12 同意：您同意服務條款 13 驗證：您完成身分驗證 14 傳輸：您同意 MyData 傳送資料給服務提供者
							 * 15 儲存：您下載資料 16 條碼取用：機關取用資料 17 服務應用：MyData 將資料傳送給服務提供者
							 */
							if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 11) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("申請");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 12) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("同意");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 13) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("驗證");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 14) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("傳輸");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 15) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("自行儲存");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 16) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("臨櫃核驗-轉存");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 17) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								if(StringUtils.isEmpty(ulogExt.getTxId())) {
									ulogExt.setActionStr("臨櫃核驗-轉存");
								}else {
									ulogExt.setActionStr("服務應用");
								}
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 18) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("申請完成");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 19) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("申請失敗");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 20) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("申請失敗");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 28) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("服務申請終止");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 29) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("服務申請終止");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 31) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("資料下載-預覽");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 32) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("臨櫃核驗-預覽");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 41) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("委託申請");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 42) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("同意");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 43) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("授權成功");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 44) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("授權失敗");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 45) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("委託終止");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 46) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("委託逾時");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 47) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("委託完成");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 48) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("不同意");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 51) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("同意代辦");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 52) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("委託終止");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 53) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("委託逾時");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 54) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("臨櫃核驗-預覽");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 55) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("臨櫃核驗-轉存");
							} else if (ulog1.getAuditEvent() != null && ulog1.getAuditEvent() == 56) {
								List<String> scopeItemList = new ArrayList<String>();
								ulogExt.setProviderName(psPortalProvider==null?"":psPortalProvider.getName());
								ulogExt.setResourceName(portalResource==null?"":portalResource.getName());
								ulogExt.setServiceName(tmpPortalService.getName());
								ulogExt.setScopeItemList(scopeItemList);
								ulogExt.setActionStr("不同意");
							}
						}
					}
					if (ulog1.getCtime() != null) {
						ulogExt.setCtimeStr(DateFormatUtils.format(ulog1.getCtime(), "yyyy年MM月dd日"));
						ulogExt.setCtimeStr1(DateFormatUtils.format(ulog1.getCtime(), "HH時mm分"));
					}

					if (ulogExt.getAuditEvent() == 11 || ulogExt.getAuditEvent() == 51) {
						ulogExtMiddleList.add(ulogExt);
						try {
							UlogApiExt tmpUlog = ulogExtMiddleList.get(0);
							org.apache.commons.beanutils.BeanUtils.copyProperties(titleExt, tmpUlog);
							ulogExtMiddleList.remove(0);
							titleExt.setUlogApiExtList(ulogExtMiddleList);
							ulogExtList.add(titleExt);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}

					} else {
						ulogExtMiddleList.add(ulogExt);
					}
				}
				
			}
		}
        
        return responseOK(ulogExtList);
    }
    
    /**
     * 判斷系統是否session timeout(5 secs/1 time )
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/alive")
    public ResponseEntity<RestResponseBean> getAlive(HttpServletRequest request, HttpServletResponse response) {
    		//System.out.println(request.getSession()==null?("==alive session null=="+new Date()):("==alive session ==:"+request.getSession().getId()+new Date()));
        HttpSession session = request.getSession();
        String userAgent = request.getHeader("User-Agent");
        DeviceType deviceType = DeviceUtil.parseDevice(userAgent);

        if(deviceType == DeviceType.Android || deviceType == DeviceType.IPhone) {
            if(session.getMaxInactiveInterval() == 10) {
                session.setMaxInactiveInterval(120);
            }
        }

        return responseOK();
    }

    /**
     * 發送根據informMethod發送驗證碼
     * @param session
     * @param member
     * @param informMethod : email or mobile
     * @return
     */
    private Map<String, Object> sendCheckCodeByInformMethod(HttpServletRequest request, HttpSession session, Member member, String informMethod) {
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        Map<String, Object> data = new HashMap<String, Object>();
        String uuid = UUID.randomUUID().toString();
        String checkCode = RandomUtils.numericString(8);
        session.setAttribute("uuidcheck", uuid);
        session.setAttribute("checkLevel", "email");
        session.setAttribute("checkCode", checkCode);
        session.setAttribute("uuidcheckTime", new Date().getTime());
        //insert into VerifyMapper
        Verify record = new Verify();
        record.setKey(uuid);
        record.setUid(ValidatorHelper.removeSpecialCharacters(member.getUid()));
        record.setBirthdate(ValidatorHelper.limitDate(member.getBirthdate()));
        record.setCheckLevel("email");
        record.setScope("basic");
        record.setCtime(new Date());
        verifyMapper.insertSelective(record);
        data.put("uuidcheck", uuid);
        data.put("inform_method", member.getInformMethod());

        String ip = HttpHelper.getRemoteIp(request);

        if (StringUtils.equals("email", informMethod)) {
            //傳送EMAIL
            System.out.println("checkCode=" + checkCode);
            /**
             * 寄通知信
             */
            try {
                String from = "mydata_system@ndc.gov.tw";
                String title = "【個人化資料自主運用(MyData)平臺】Email 動態密碼（系統信件）";
                String content = "您好：\n\n"
                        + "感謝您使用個人化資料自主運用(MyData)平臺，您本次的動態密碼如下，請您於 2 分鐘內於 MyData 平臺輸入此驗證碼，以完成驗證流程。\n"
                        + "\n"
                        + "<div style=\""
                        + "    display: block;"
                        + "    margin: 20px 0;"
                        + "    padding: 10px 20px;"
                        + "    font-size: 1rem;\n"
                        + "    background-color: #efefef;"
                        + "    border: none;\">"
                        + checkCode
                        + "</div>"
                        + "\n"
                        + "此為系統信件，請勿回信。\n"
                        + "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
                        + "<br><br>\n"
                        + "——-\n"
                        + "<p style=\"margin-bottom:2px\">"
                        + "<strong>我為什麼會收到這封信？</strong><br>\n"
                        + "</p>"
                        + "您會收到此封信件，是因為您於國家發展委員會個人化資料自主運用(MyData)平臺驗證身分，因此，系統會自動發此信通知您。\n"
                        + "<br><br>——-\n"
                        + "<p style=\"margin-bottom:2px\">"
                        + "<strong>非本人？</strong><br>\n"
                        + "</p>"
                        + "如非您本人同意傳輸或下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
                List<String> tmpReveicers = new ArrayList<String>();
                tmpReveicers.add(member.getEmail());
                /**
                 * 強制寄信 mailEnable == "true"
                 */
                //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_220, null, null, ip);
                MailUtil.sendHtmlMail(tmpReveicers, from, title, content, mailEnable);
                sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
            } catch (Exception ex) {
                System.out.println("--寄信失敗--:\n" + ex);
            }
        } else {
            //傳送簡訊checkCode
            System.out.println("checkCode=" + checkCode);
            String smbody = "MyData 動態密碼訊息限當次有效：密碼 " + checkCode + "。請於網頁上輸入密碼";
            try {
                //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_200, null, null, ip);
                SMSUtil.sendSms(member.getMobile(), smbody);
                sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        data.put("member", member);
        return data;
    }

    @PostMapping("/emailOrMobileLoginStep1")
    public ResponseEntity<RestResponseBean> postForEmailOrMobileLoginFirstStep(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        String roleType = "";        
        if(session!=null&&sr!=null) {
        		roleType = ((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getRoleType();
        }

        String ip = HttpHelper.getRemoteIp(request);

        String uid = "";
        String birthdate = "";
        String scope = "";
        String inform = "";
        List<String> scopeList = new ArrayList<String>();
        if (params.get("uid") != null) {
            uid = params.get("uid").toString();
        }
        if (params.get("birthdate") != null) {
            birthdate = params.get("birthdate").toString();
        }
        if (params.get("scope") != null) {
            scope = params.get("scope").toString() + " ";
        }
        if (params.get("inform_method") != null) {
            inform = params.get("inform_method").toString();

        }
        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        if (scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }

        System.out.println("uid=" + uid);
        System.out.println("birthdate=" + birthdate);
        Map<String, Object> data = new HashMap<String, Object>();
        if (uid != null && birthdate != null) {
            /**
             * 確認Member內有該會員資料 (要有) inform_method
             * true: 根據 inform_method 寄出 checkCode，並等待 checkCode 步驟後進行登入
             * false: 回補填資料Flow，並重新進行此步驟
             * 可用回應有無uuidcheck判斷是否有寄出「驗證碼」
             */
            /**
             * 處理Member Table
             */
            String account = encoder.encodeToString((uid.toUpperCase() + birthdate).getBytes("UTF-8"));
            Member member = null;
            Map<String, Object> mParam = new HashMap<String, Object>();
            mParam.put("uid", uid.toUpperCase());
            if(!roleType.equals("0.5")) {
            	mParam.put("birthdate", birthdate.contains("-") ? sdf.parse(birthdate) : sdf6.parse(birthdate));
            }
            mParam.put("statIsNullorZero", true);
            List<Member> mList = memberMapper.selectByExample(mParam);

            if (mList != null && mList.size() > 0) {
                member = mList.get(0);
            } else {
                return responseError(SysCode.DataNotExist, "member", "您目前非 MyData 會員。請使用其他身分驗證方式進入個人專區，謝謝。");
            }
            if (StringUtils.isNotEmpty(inform)) {
                // 為了判斷是否有輸入inform
                member.setInformMethod(inform);
            }
            if (member.getInformMethod() != null && member.getInformMethod().length() > 0) {
                /**
                 * 根據 inform_method 寄出 checkCode，並等待 checkCode 步驟後進行登入
                 * EMAIL or Mobile
                 */
                String uuid = UUID.randomUUID().toString();
                String checkCode = RandomUtils.numericString(8);
                session.setAttribute("uuidcheck", uuid);
                session.setAttribute("checkLevel", "email");
                session.setAttribute("checkCode", checkCode);
                session.setAttribute("uuidcheckTime", new Date().getTime());
                //insert into VerifyMapper
                Verify record = new Verify();
                record.setKey(uuid);
                record.setUid(uid);
                if(!roleType.equals("0.5")) {
                	record.setBirthdate(sdf6.parse(birthdate));
                }
                record.setCheckLevel("email");
                record.setScope(scope);
                record.setCtime(new Date());
                verifyMapper.insertSelective(record);
                data.put("uuidcheck", uuid);
                data.put("inform_method", member.getInformMethod());

                if (member.getInformMethod().equalsIgnoreCase("email")) {
                    //傳送EMAIL
                    System.out.println("checkCode=" + checkCode);
                    /**
                     * 寄通知信
                     */
                    try {
                        String from = "mydata_system@ndc.gov.tw";
                        String title = "【個人化資料自主運用(MyData)平臺】Email 動態密碼（系統信件）";
                        String content = "您好：\n\n"
                                + "感謝您使用個人化資料自主運用(MyData)平臺，您本次的動態密碼如下，請您於 2 分鐘內於 MyData 平臺輸入此驗證碼，以完成驗證流程。\n"
                                + "\n"
                                + "<div style=\""
                                + "    display: block;"
                                + "    margin: 20px 0;"
                                + "    padding: 10px 20px;"
                                + "    font-size: 1rem;\n"
                                + "    background-color: #efefef;"
                                + "    border: none;\">"
                                + checkCode
                                + "</div>"
                                + "\n"
                                + "此為系統信件，請勿回信。\n"
                                + "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
                                + "<br><br>\n"
                                + "——-\n"
                                + "<p style=\"margin-bottom:2px\">"
                                + "<strong>我為什麼會收到這封信？</strong><br>\n"
                                + "</p>"
                                + "您會收到此封信件，是因為您於國家發展委員會個人化資料自主運用(MyData)平臺驗證身分，因此，系統會自動發此信通知您。\n"
                                + "<br><br>——-\n"
                                + "<p style=\"margin-bottom:2px\">"
                                + "<strong>非本人？</strong><br>\n"
                                + "</p>"
                                + "如非您本人同意傳輸或下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
                        List<String> tmpReveicers = new ArrayList<String>();
                        tmpReveicers.add(member.getEmail());
                        /**
                         * 強制寄信 mailEnable == "true"
                         */
                        //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_220, scope, null, ip);
                        MailUtil.sendHtmlMail(tmpReveicers, from, title, content, mailEnable);
                        sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
                    } catch (Exception ex) {
                        System.out.println("--寄信失敗--:\n" + ex);
                    }

                } else if (member.getInformMethod().equalsIgnoreCase("mobile")) {
                    logger.debug("準備發送簡訊");
                    //傳送簡訊checkCode
                    //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_200, scope, null, ip);
                    System.out.println("checkCode=" + checkCode);
                    String smbody = "MyData 動態密碼訊息限當次有效：密碼 " + checkCode + "。請於網頁上輸入密碼";
                    SMSUtil.sendSms(member.getMobile(), smbody);
                    sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
                }
            } else {
                /**
                 * 回應基本資料並進行補填要求
                 */
//				data.put("member", member);
            }

            // 遮罩
            data.put("member", member);
        }

        return responseOK(data);
    }

    /**
     * 補登會員資料
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/emailOrMobileLoginStep2")
    public ResponseEntity<RestResponseBean> postForEmailOrMobileLoginFirstStep2(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        String uid = null;
        String birthdate = null;
        String name = null;
        String email = null;
        String mobile = null;
        String informMethod = null;
        if (params.get("uid") != null) {
            uid = params.get("uid").toString();
        }
        if (params.get("birthdate") != null) {
            birthdate = params.get("birthdate").toString();
        }
        if (params.get("name") != null) {
            name = params.get("name").toString();
        }
        if (params.get("email") != null) {
            email = params.get("email").toString();
        }
        if (params.get("mobile") != null) {
            mobile = params.get("mobile").toString();
        }
        if (params.get("informMethod") != null) {
            informMethod = params.get("informMethod").toString();
        }
        Member member = null;
        Member member1 = new Member();
        Map<String, Object> mParam = new HashMap<String, Object>();
        mParam.put("uid", uid==null?"":uid.toUpperCase());
        if(birthdate!=null&&StringUtils.isNotEmpty(birthdate)) {
        	mParam.put("birthdate", sdf6.parse(birthdate));
        }
        mParam.put("statIsNullorZero", true);
        System.out.println(mobile);
        List<Member> mList = memberMapper.selectByExample(mParam);
        if (mList != null && mList.size() > 0) {
            member = mList.get(0);
            member1.setId(ValidatorHelper.limitNumber(member.getId()));
        }
        Map<String, Object> data = new HashMap<String, Object>();
        if (member != null) {
            if (name != null) {
                member.setName(ValidatorHelper.removeSpecialCharacters(name));
                member1.setName(ValidatorHelper.removeSpecialCharacters(name));
            }
            if (email != null) {
                member.setEmail(ValidatorHelper.removeSpecialCharacters(email));
                member1.setEmail(ValidatorHelper.removeSpecialCharacters(email));
            }
            if (mobile != null) {
                member.setMobile(ValidatorHelper.removeSpecialCharacters(mobile));
                member1.setMobile(ValidatorHelper.removeSpecialCharacters(mobile));
            }
            if (informMethod != null) {
                member.setInformMethod(ValidatorHelper.removeSpecialCharacters(informMethod));
                member1.setInformMethod(ValidatorHelper.removeSpecialCharacters(informMethod));
                if (informMethod.equalsIgnoreCase("email")) {
                    member.setEmailVerified(true);
                    member1.setEmailVerified(true);
                } else if (informMethod.equalsIgnoreCase("mobile")) {
                    member.setMobileVerified(true);
                    member1.setMobileVerified(true);
                }
            }
            
            memberMapper.updateByPrimaryKeySelective(member1);
            if(session!=null&&sr!=null) {
            	sr.setMember(new SessionMember(member));
            	session.setAttribute(SessionRecord.SessionKey, sr);
            }
            data.put("member", member);       	
        }
        return responseOK(data);
    }


    @PostMapping("/emailOrMobileLoginStep/check")
    public ResponseEntity<RestResponseBean> postForEmailOrMobileLoginFirstStep1Check(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        String uuidcheck = null;
        String checkCode = null;
        String informMethod = null;
        if (params.get("uuidcheck") != null) {
            uuidcheck = params.get("uuidcheck").toString();
        }
        if (params.get("checkCode") != null) {
            checkCode = params.get("checkCode").toString();
        }
        // member頁面的informMethod才會有值,login2則為空值
        if (params.get("informMethod") != null) {
            informMethod = params.get("informMethod").toString();
        }
        //是否執行登入判斷，Y 表示為臨櫃核驗動作，跟會員登入無關
        String isVerification = MapUtils.getString(params, "isVerification");
        
        Long uuidcheckTime = (Long) session.getAttribute("uuidcheckTime");
        if(uuidcheckTime == null || uuidcheckTime + VERIFY_TIME < new Date().getTime()) {
        	return responseError(SysCode.InvalidVerifyCode, "id", "身分驗證錯誤!");
        }

        VerificationLog vlog = new VerificationLog();
        String verificationPath = MapUtils.getString(params, "verificationPath");
        if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
            vlog.setType(VerificationPath.member.name());
        } else {
            vlog.setType(VerificationPath.login.name());
        }
        vlog.setVerificationType(VerificationType.emailOrMobile.name());
        vlog.setCtime(new Date());
        String session_uuidcheck = null;
        String session_checkCode = null;
        if(session.getAttribute("uuidcheck") != null) {
        	session_uuidcheck = session.getAttribute("uuidcheck").toString();
        }
        if(session.getAttribute("checkCode") != null) {
        	session_checkCode = session.getAttribute("checkCode").toString();
        }
        if (uuidcheck != null && checkCode != null && session_uuidcheck != null && session_checkCode != null
        		&& uuidcheck.equalsIgnoreCase(session_uuidcheck)
        		&& checkCode.equalsIgnoreCase(session_checkCode)) {

            /**
             * UUID驗證參數，暫定為一小時有效
             */
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            Date endTime = cal.getTime();
            // 起始時間定為1小時前
            cal.add(Calendar.HOUR_OF_DAY, -1);
            Date startTime = cal.getTime();
            Map<String, Object> verifyparam = new HashMap<String, Object>();
            verifyparam.put("sCtime", startTime);
            verifyparam.put("key", uuidcheck);
            List<Verify> verifyList = verifyMapper.selectByExample(verifyparam);
            if (verifyList != null && verifyList.size() > 0) {
                //session = changeSessionId(request);
                Verify verify = verifyList.get(0);
                String birthdate = verify.getBirthdate() == null?"":sdf6.format(verify.getBirthdate());
                String account = encoder.encodeToString((verify.getUid().toUpperCase()+birthdate).getBytes("UTF-8"));
                vlog.setAccount(account);
                vlog.setStatus(1);
                verificationLogMapper.insertSelective(vlog);
                /**
                 * Login
                 */

                Map<String, Object> data = new HashMap<String, Object>();
                if(StringUtils.isBlank(isVerification)) { //臨櫃核驗不再做登入動作
                    if(informMethod==null||StringUtils.isEmpty(informMethod)){
                        // login2頁面才需要登入
                        //ulogUtil.recordFullByPr(sr, null, null, null, null, "230", verify.getScope(), null, null);
                        Member member =loginUtil.doNewAutoLogin(verify, request, uuidcheck);
                        data.put("member", member);
                    }else {
                        // member頁面不需要再次登入
                        if (sr != null) {
                            verifySuccessResult(SessionMember.getSessionMemberToMember(SessionMember.getSessionMemberToMember(sr.getMember())), informMethod, sr);
                        }
                        if (StringUtils.equals(informMethod, "email")) {
                            ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_230, verify.getScope(), null, HttpHelper.getRemoteIp(request));
                        } else {
                            ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_210, verify.getScope(), null, HttpHelper.getRemoteIp(request));
                        }
                        return responseOK(SessionMember.getSessionMemberToMember(SessionMember.getSessionMemberToMember(sr.getMember())));
                    }
                }
                return responseOK(data);
            } else {
                vlog.setStatus(1);
                verificationLogMapper.insertSelective(vlog);
                return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
            }
        } else {
            /**
             * -1105, 身分驗證錯誤
             */
            vlog.setStatus(1);
            verificationLogMapper.insertSelective(vlog);
            return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
        }
    }


    /**
     * 驗證成功後更新member table和session儲存的member
     * 
     * @param member
     * @param informMethod
     */
    private void verifySuccessResult(Member member, String informMethod, SessionRecord sr) {
    	 Member member1 = new Member();
        if (StringUtils.isNotEmpty(informMethod)) {
            if (StringUtils.equals(informMethod, "email")) {
                member.setEmailVerified(true);
                member1.setEmailVerified(true);
            } else {
                member.setMobileVerified(true);
                member1.setMobileVerified(true);
            }
            member.setInformMethod(informMethod);
            Member maskMember = new Member();
            BeanUtils.copyProperties(member,maskMember);
            sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
        }
        member1.setId(ValidatorHelper.limitNumber(member.getId()));
        member1.setInformMethod(ValidatorHelper.removeSpecialCharacters(informMethod));
        int rc = memberMapper.updateByPrimaryKeySelective(member1);
         // 將member更新存入session
        if(rc > 0){
//            sr.setsMember(new SessionMember(member));
        }

    }

    /**
     * 傳送針對 new email or mobile 驗證碼，
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/emailOrMobileMessage/send")
    public ResponseEntity<RestResponseBean> postForEmailOrMobileMesage(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
    	request.getSession().setMaxInactiveInterval(120);
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
    	String email = null;
    	String mobile = null;
    	String ip = HttpHelper.getRemoteIp(request);

        if (params.get("email") != null) {
        		email = params.get("email").toString();
        }
        if (params.get("mobile") != null) {
        		mobile = params.get("mobile").toString();
        }
        String prId = MapUtils.getString(params, "prId", ""); // 如果是 download prId 必須有值
        if(sr!=null) {
        	 	Map<String, Object> data = new HashMap<String, Object>();
            String uuid = UUID.randomUUID().toString();
            String checkCode = RandomUtils.numericString(8);
            session.setAttribute("uuidcheckforCheckCode" + prId, uuid);
            data.put("uuidcheckforCheckCode", uuid);
            session.setAttribute("checkforCheckCode" + prId, checkCode);
            session.setAttribute("uuidcheckTimeforCheckCode" + prId, new Date().getTime());
            if(email!=null) {
                //傳送EMAIL
                System.out.println("checkCode=" + checkCode);
                session.setAttribute("checkforCheckLevel" + prId, "email");
                session.setAttribute("checkEmail" + prId, email);
                data.put("checkLevel", "email");
                /**
                 * 寄通知信
                 */
                try {
                    String from = "mydata_system@ndc.gov.tw";
                    String title = "【個人化資料自主運用(MyData)平臺】Email 驗證碼（系統信件）";
                    String content = "您好：\n\n"
                            + "感謝您使用個人化資料自主運用(MyData)平臺，您本次的驗證碼如下，請於2分鐘內返回 MyData平臺進行驗證(逾期無效)，以完成驗證流程。\n"
                            + "\n"
                            + "<div style=\""
                            + "    display: block;"
                            + "    margin: 20px 0;"
                            + "    padding: 10px 20px;"
                            + "    font-size: 1rem;\n"
                            + "    background-color: #efefef;"
                            + "    border: none;\">"
                            + checkCode
                            + "</div>"
                            + "\n"
                            + "此為系統信件，請勿回信。\n"
                            + "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
                            + "<br><br>\n"
                            + "——-\n"
                            + "<p style=\"margin-bottom:2px\">"
                            + "<strong>我為什麼會收到這封信？</strong><br>\n"
                            + "</p>"
                            + "您會收到此封信件，是因為您於國家發展委員會個人化資料自主運用(MyData)平臺驗證身分，因此，系統會自動發此信通知您。\n"
                            + "<br><br>——-\n"
                            + "<p style=\"margin-bottom:2px\">"
                            + "<strong>非本人？</strong><br>\n"
                            + "</p>"
                            + "如非您本人同意傳輸或下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
                    List<String> tmpReveicers = new ArrayList<String>();
                    tmpReveicers.add(email);
                    /**
                     * 強制寄信 mailEnable == "true"
                     */
                    ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_220, "basic", null, ip);
                    MailUtil.sendHtmlMail(tmpReveicers, from, title, content, mailEnable);
                } catch (Exception ex) {
                    System.out.println("--寄信失敗--:\n" + ex);
                }
            }else if(mobile!=null) {
                //傳送簡訊checkCode
            		session.setAttribute("checkforCheckLevel" + prId, "mobile");
            		session.setAttribute("checkMobile" + prId, mobile);
            		data.put("checkLevel", "mobile");
                ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_200, "basic", null, ip);
                System.out.println("checkCode=" + checkCode);
                String smbody = "MyData 手機驗證訊息限當次有效：驗證碼 " + checkCode + "。請於網頁上輸入手機驗證碼";
                SMSUtil.sendSms(mobile, smbody);
            }else {
            		return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
            }
            return responseOK(data);
        }else{
        		return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
        }
    }
    
    /**
     * 檢查 uuidcheck checkCode
     */
    @PostMapping("/emailOrMobileMessage/check")
    public ResponseEntity<RestResponseBean> postForEmailOrMobileMesageCheck(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		String checkCode = null;
		String uuidcheckforCheckCode = null;
		String prId = MapUtils.getString(params, "prId", ""); // 如果是 download prId 必須有值
        if (params.get("checkforCheckCode") != null) {
        		checkCode = params.get("checkforCheckCode").toString();
        }
        if (params.get("uuidcheckforCheckCode") != null) {
        		uuidcheckforCheckCode = params.get("uuidcheckforCheckCode").toString();
	    }

        String informMethod = MapUtils.getString(params, "informMethod");
        
        Long uuidcheckTime = (Long) session.getAttribute("uuidcheckTimeforCheckCode" + prId);
        if(uuidcheckTime == null || uuidcheckTime + VERIFY_TIME < new Date().getTime()) {
        	return responseError(SysCode.InvalidVerifyCode, "id", "身分驗證錯誤!");
        }
        
        if(sr!=null&&sr.getMember()!=null) {
            String session_uuidcheckforCheckCode = session.getAttribute("uuidcheckforCheckCode" + prId) == null ? null : session.getAttribute("uuidcheckforCheckCode"  + prId).toString();
            String session_checkCode = session.getAttribute("checkforCheckCode" + prId) == null ? null : session.getAttribute("checkforCheckCode"  + prId).toString();
            String session_checkLevel = session.getAttribute("checkforCheckLevel" + prId) == null ? null : session.getAttribute("checkforCheckLevel" + prId).toString();
            if (uuidcheckforCheckCode != null && checkCode != null && session_uuidcheckforCheckCode != null && session_checkCode != null &&
            			uuidcheckforCheckCode.equalsIgnoreCase(session_uuidcheckforCheckCode) &&
                    checkCode.equalsIgnoreCase(session_checkCode)) {
            	Map<String, Object> data = new HashMap<>();
            	data.put("session_checkLevel", session_checkLevel);

                if (StringUtils.equals(informMethod, "email")) {
                    ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_230, null, null, HttpHelper.getRemoteIp(request));
                } else {
                    ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_210, null, null, HttpHelper.getRemoteIp(request));
                }
            	return responseOK(data);
            }else{
            	return responseError(SysCode.InvalidVerifyCode, "id", "無效的驗證碼!");
            }
        }else {
        	return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
        }
    }
    
    /**
     *	更改會員資料 
     *  TODO WEDER 會員資料
     */
    @PostMapping("/emailOrMobileMessage/update")
    public ResponseEntity<RestResponseBean> postForEmailOrMobileMesageUpdate(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);

        String verificationType = MapUtils.getString(params, "verificationType");
       
        if(sr!=null&&sr.getMember()!=null) {
            String session_checkLevel = session.getAttribute("checkforCheckLevel") == null ? null : session.getAttribute("checkforCheckLevel").toString();
            String session_checkEmail = session.getAttribute("checkEmail") == null ? null : session.getAttribute("checkEmail").toString();
            String session_checkMobile = session.getAttribute("checkMobile") == null ? null : session.getAttribute("checkMobile").toString();
            Member maskMember = new Member();
            Map<String, Object> data = new HashMap<String, Object>();
            System.out.println(session_checkLevel);
            if (session_checkLevel !=null) {
                Member member = null;
                Map<String, Object> mParam = new HashMap<String, Object>();
                mParam.put("uid", SessionMember.getSessionMemberToMember(sr.getMember()).getUid());
                mParam.put("birthdate", SessionMember.getSessionMemberToMember(sr.getMember()).getBirthdate());
                mParam.put("statIsNullorZero", true);
                List<Member> mList = memberMapper.selectByExample(mParam);
                if (mList != null && mList.size() > 0) {
                    member = mList.get(0);
                }

                if(member!=null && session_checkLevel.equalsIgnoreCase("email")) {
                    String beforeData = member.getEmail();
                    String afterData = session_checkEmail;
                    member.setEmail(session_checkEmail);
                    member.setEmailVerified(true);
                    member.setUidVerified(true);
                    Member member1 = new Member();
                    member1.setId(ValidatorHelper.limitNumber(member.getId()));
                    member1.setEmail(ValidatorHelper.removeSpecialCharacters(session_checkEmail));
                    member1.setEmailVerified(true);
                    member1.setUidVerified(true);
                    memberMapper.updateByPrimaryKeySelective(member1);
                    memberChangeUtil.saveLog(member, beforeData, afterData, MemberChangeType.Email, verificationType);
                }else if(member!=null && session_checkLevel.equalsIgnoreCase("mobile")) {
                    String beforeData = member.getMobile();
                    String afterData = session_checkMobile;
                    member.setMobile(session_checkMobile);
                    member.setMobileVerified(true);
                    member.setUidVerified(true);
                    Member member1 = new Member();
                    member1.setId(ValidatorHelper.limitNumber(member.getId()));
                    member1.setMobile(ValidatorHelper.removeSpecialCharacters(session_checkMobile));
                    member1.setMobileVerified(true);
                    member1.setUidVerified(true);
                    memberMapper.updateByPrimaryKeySelective(member1);
                    memberChangeUtil.saveLog(member, beforeData, afterData, MemberChangeType.Mobile, verificationType);
                }else {
                    return responseError(SysCode.MissingRequiredParameter, "checkLevel", "缺少必要參數");
                }
                BeanUtils.copyProperties(member,maskMember);
            		sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
            		sr.setMember(new SessionMember(member));
            		session.setAttribute(SessionRecord.SessionKey, sr);
            		data.put("userInfo", sr);
            		data.put("session_checkLevel", session_checkLevel);
            		data.put("member", SessionMember.getSessionMemberToMember(sr.getMember()));
            }else{
            		return responseError(SysCode.InvalidVerifyCode, "id", "更新失敗!");
            }
        		return responseOK(data);
        }else {
        		return responseError(SysCode.AuthenticateFail, "id", "更新失敗!");
        }
    }
    
    /**
     * 檢查目前登入類型
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/preCheckLoginType")
    public ResponseEntity<RestResponseBean> getPreCheckLoginType(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("checkLevel", session.getAttribute("checkLevel") == null ? null : session.getAttribute("checkLevel").toString());
        return responseOK(data);
    }

    //cancelBeforeLogin
    @GetMapping("/cancelBeforeLogin/check")
    public ResponseEntity<RestResponseBean> checkCancelBeforeLogin(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        if ((boolean) session.getAttribute("cancelBeforeLogin") == true) {
            session.setAttribute("cancelBeforeLogin", false);
            return responseError(SysCode.AccountDisabled, "id", "您已成功登入，為確保帳戶使用安全，我們將同步登出於其他瀏覽器，謝謝您！");
        } else {
            return responseOK();
        }
    }

    /**
     * 更新會員資訊
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     */
    @PostMapping("/updateUserInfo")
    public ResponseEntity<RestResponseBean> updateUserInfo(
            HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) throws BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		int rc = 0;
		Integer id = ValidatorHelper.limitNumber((Integer) params.get("id"));
        String name = params.get("name")==null?null:ValidatorHelper.removeSpecialCharacters(params.get("name").toString());
        String email = params.get("email")==null?null:ValidatorHelper.removeSpecialCharacters(params.get("email").toString());
        String mobile = params.get("mobile")==null?null:ValidatorHelper.removeSpecialCharacters(params.get("mobile").toString());
        String inform = "";
        // 更新會員資訊時有可能inform_method為空值(手機號碼和電子郵件皆未認證時）
        if(params.get("inform_method") != null){
            inform = ValidatorHelper.removeSpecialCharacters(params.get("inform_method").toString());
        }
        Member member = memberMapper.selectByPrimaryKey(id);
        Member member1 = new Member();
        if(member!=null) {
        	member1.setId(ValidatorHelper.limitNumber(member.getId()));
            String oldInform = member.getInformMethod();
            String newInform = inform;

            if(mobile!=null&&!StringUtils.equals(mobile,member.getMobile())){
                member.setMobileVerified(false);
                member1.setMobileVerified(false);
            }
            if(email!=null&&!StringUtils.equals(email,member.getEmail())){
                member.setEmailVerified(false);
                member1.setEmailVerified(false);
            }

            member.setName(name);
            member.setEmail(email);
            member.setMobile(mobile);
            member.setInformMethod(inform);
            
            member1.setName(name);
            member1.setEmail(email);
            member1.setMobile(mobile);
            member1.setInformMethod(inform);

            rc = memberMapper.updateByPrimaryKeySelective(member1);
            // 將member更新存入session
            if (rc > 0 && session != null && sr !=null) {
                sr.setMember(new SessionMember(member));
                Member maskMember = new Member();
                BeanUtils.copyProperties(member,maskMember);
            	sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
                sr.setMaskMember(maskMember);
                session.setAttribute(SessionRecord.SessionKey, sr);
                if(!StringUtils.equals(oldInform, newInform)) {
                    memberChangeUtil.informMethodChangeSend(member);
                }
            }        	
        }
        return rc > 0 ? responseOK(member) : responseError(SysCode.DatabaseAccessFailed, "id", "更新失敗！");
    }
	
    /**
     * tfido驗證 step1
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
	@PostMapping("/notifyTFido")
	public ResponseEntity<RestResponseBean> notifyTFido(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
		String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
		if(StringUtils.isEmpty(uid)) {
			return responseError(SysCode.MissingRequiredParameter,"uid","缺少必要參數[身分證字號]");
		}
        
        String responeStr = TWFidoUtil.exPushAuth(uid, tfidoSyscode, tfidoApikey);
        if(StringUtils.isEmpty(responeStr)) {
        	return responseError(SysCode.CustomError, "id", "TW FidO驗證功能暫時無法使用，請使用其他驗證方式，謝謝。");
        }
        
		logger.debug("notifyTFido resp = "+responeStr);
		org.json.JSONObject rsObj = new org.json.JSONObject(responeStr);
		String rcode = rsObj.getString("rcode");
		String msg = rsObj.getString("msg");
		if(!rcode.contentEquals("0000")) {
			RestResponseBean rb = new RestResponseBean();
			rb.setCode(SysCode.AuthenticateFail.value());
			rb.setText("TW FidO "+msg+"。");
			return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
		}
		String qrid = rsObj.getString("qrid");

		TfidoRecord record = new TfidoRecord();
		record.setUid(uid);
		record.setQrid(qrid);
		tfidoRecordMapper.insertSelective(record);
		
		Map<String, Object> data = new HashMap<>();
		data.put("TFidoResp", responeStr);
		data.put("qrid", qrid);
		
		return responseOK(data);
	}
    
	/**
	 * tfido驗證 step2
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
    @PostMapping("/confirmTFido")
	public ResponseEntity<RestResponseBean> confirmTFido(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
    	boolean isDoubleVerify = false;
    	if(params.get("isDoubleVerify")!=null && objToStr(params.get("isDoubleVerify")).equalsIgnoreCase("true")) {
    		isDoubleVerify = true;
    	}
    	
    	HttpSession session = request.getSession();
        List<String> scopeList = new ArrayList<String>();
        String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
        String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthdate")));
        String account = encoder.encodeToString((uid.toUpperCase()+birthdate).getBytes("UTF-8"));
        VerificationLog vlog = new VerificationLog();
        vlog.setAccount(account);
        vlog.setVerificationType(VerificationType.tfido.name());
        vlog.setCtime(new Date());
        if(isDoubleVerify) {
            vlog.setIsDoubleVerify(1);
        }

        String scope = "";
        if(params.get("scope")!=null) {
            scope = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("scope"))) + " ";
            String verificationPath = MapUtils.getString(params, "verificationPath");
            if(StringUtils.equals(verificationPath, VerificationPath.member.name())) {
                vlog.setType(VerificationPath.member.name());
            } else {
                vlog.setType(VerificationPath.login.name());
            }
        } else {
            vlog.setType(VerificationPath.download.name());
        }
        
        String qrid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("qrid")));

        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || StringUtils.isEmpty(qrid)) {
			return responseError(SysCode.MissingRequiredParameter,"qrid","缺少必要參數!");
		}
        logger.info("tfido驗證，uid = {}，birthdate = {}", uid, birthdate);
        
        Map<String, Object> tfidoParam = new HashMap<String, Object>();
        tfidoParam.put("uid", uid);
        tfidoParam.put("qrid", qrid);
        List<TfidoRecord> tfidos = tfidoRecordMapper.selectByExample(tfidoParam);
        if(CollectionUtils.isEmpty(tfidos)) {
            return responseError(SysCode.AuthenticateFail,"uid","身分驗證錯誤!");
        }

		String responeStr = TWFidoUtil.authConfirm(qrid, tfidoSyscode, tfidoApikey);
        if(StringUtils.isEmpty(responeStr)) {
            vlog.setStatus(0);
            verificationLogMapper.insertSelective(vlog);
        	return responseError(SysCode.CustomError, "id", "TW FidO驗證功能暫時無法使用，請使用其他驗證方式，謝謝。");
        }
		logger.info("confirmTFido resp = "+responeStr);
		org.json.JSONObject rsObj = new org.json.JSONObject(responeStr);
		String rcode = rsObj.getString("rcode");

        if(rcode.contentEquals("9005")) {
            vlog.setStatus(0);
            verificationLogMapper.insertSelective(vlog);
        } else if (rcode.contentEquals("0000")) {
            vlog.setStatus(1);
            verificationLogMapper.insertSelective(vlog);
        }


		if(!rcode.contentEquals("0000")) {
			return responseError(SysCode.AuthenticateFail,"uid","身分驗證錯誤!");
		}

        if (params.get("prId") != null && params.get("prId").toString().trim().length() > 0) {
            String[] prIdArray = params.get("prId").toString().trim().split("[,]");
            if (prIdArray != null && prIdArray.length > 0) {
                for (String prIdStr : prIdArray) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("prId", Integer.valueOf(prIdStr));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        for (PortalResourceScope s : portalResourceScopeList) {
                            if (s.getScope() != null && s.getScope().trim().length() > 0) {
                                scope = scope + ValidatorHelper.removeSpecialCharacters(s.getScope()) + " ";
                                scopeList.add(ValidatorHelper.removeSpecialCharacters(s.getScope()));
                            }
                        }
                    }
                }
            }
        }
        scope = scope.trim();
        
        if ( !isDoubleVerify && scopeList != null && scopeList.size() > 0) {
            session.setAttribute("scopeList", scopeList);
        }
        
        String uuid = UUID.randomUUID().toString();
        //insert into VerifyMapper
        Verify record = new Verify();
        record.setKey(uuid);
        record.setUid(uid);
        record.setBirthdate(sdf6.parse(birthdate));
        record.setCheckLevel("tfido");
        record.setScope(scope);
        record.setCtime(new Date());
        verifyMapper.insertSelective(record);

        Member member = null;
        if(!isDoubleVerify) {
        	member = loginUtil.doNewAutoLogin(record, request, null);
        } else {
            Map<String,Object> meParam = new HashMap<>();
            meParam.put("account", ValidatorHelper.removeSpecialCharacters(account));
            List<Member> meList = memberMapper.selectByExample(meParam);
            if(meList==null||meList.size()==0) {
            		return responseError(SysCode.AccountNotExist, "id", "帳號不存在。");
            }else {
            	member = meList.get(0);
            }
            member.setUidVerified(true);
            member.setIsDoubleVerify(true);
            member.setIsDoubleVerifyTime(new Date());
            //memberMapper.updateByPrimaryKeySelective(member);
            Member member1 = new Member();
			member1.setId(ValidatorHelper.limitNumber(member.getId()));
			member1.setUidVerified(true);
			member1.setIsDoubleVerify(true);
			member1.setIsDoubleVerifyTime(new Date());
			memberMapper.updateByPrimaryKeySelective(member1);
        	SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        	if (sr != null) {
        		sr.setMember(new SessionMember(member));
        		session.setAttribute(SessionRecord.SessionKey, sr);
        	}            	
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("member", member);

        return responseOK(data);
	}
    
    @PostMapping("/verifyGRecaptcha")
	public ResponseEntity<RestResponseBean> verifyGRecaptcha(
			@RequestBody Map<String, Object> params) throws Exception {
    	
        String gRecaptchaResponse = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("g-recaptcha-response")));
        CommonApplyUtils applyUtils = new CommonApplyUtils();
		boolean success = applyUtils.googleRecaptcha(gRecaptchaResponse);
		if (success == true) {
	        return responseOK();
		}else {
			return responseError(SysCode.InvalidVerifyCode, "captcha", "驗證失敗!");
		}
	}

    @PostMapping("/checkBoxAgentData")
	public ResponseEntity<RestResponseBean> checkBoxAgentData(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null&&sr.getMember()!=null) {
			/**
			 * 會員檢查
			 */
			String boxId = "";
	        String agent_uid = "";
	        String agent_birthdate = "";
	        if (params.get("boxId") != null) {
	        	boxId = params.get("boxId").toString();
	        } 
	        if (params.get("agent_uid") != null) {
	        	agent_uid = params.get("agent_uid").toString();
	        }
	        if (params.get("agent_birthdate") != null) {
	        	agent_birthdate = ValidatorHelper.removeSpecialLimitNumber(params.get("agent_birthdate").toString());
	        }
	        if(boxId==null||boxId.trim().length()==0) {
	        	return responseError(SysCode.MissingRequiredParameter, "id", "缺少必要參數。");
	        }
	        if(agent_uid==null||agent_uid.trim().length()==0||agent_birthdate==null||agent_birthdate.trim().length()==0) {
	        	return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
	        }
            if(agent_uid.equalsIgnoreCase(SessionMember.getSessionMemberToMember(sr.getMember()).getUid())) {
            	return responseError(SysCode.AgentIsNotSameUser, "id", "代辦人不可為同一使用者。");
            }

            PortalBox box = portalBoxMapper.selectByPrimaryKey(Integer.valueOf(boxId));
            if(box.getDownloadSn()==null||box.getDownloadSn().trim().length()==0) {
            	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
            }
            String provider_key = "";
            for(String s:box.getDownloadSn().split("[,]")) {
            	PortalResourceDownload prd = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(s));
            	provider_key = prd.getProviderKey();
            }
            if(!SessionMember.getSessionMemberToMember(sr.getMember()).getAccount().equalsIgnoreCase(provider_key)) {
            	return responseError(SysCode.NoPermission, "id", "無該條碼存取權限。");
            }
            Member agentMember = null;
            Map<String,Object> mParam = new HashMap<>();
            mParam.put("uid", agent_uid.toUpperCase());
            mParam.put("birthdate", CDateUtil.ROCDateToAD(agent_birthdate));
            mParam.put("statIsNullorZero", true);
            List<Member> mList = memberMapper.selectByExample(mParam);
            if(mList==null||mList.size()==0) {
            	return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
            }else {
            	agentMember = mList.get(0);
            	if(agentMember.getName()==null||agentMember.getName().trim().length()==0||agentMember.getInformMethod()==null||agentMember.getInformMethod().trim().length()==0) {
            		return responseError(SysCode.AgentMemberNotFill, "id", "代辦人聯絡資料不全。");
            	}
            }
			PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
			PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
			PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));
			
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("boxId", box.getId());
			data.put("agentName", MaskUtil.maskNameByO(agentMember.getName()));
			data.put("agent_uid", agent_uid);
			data.put("agent_birthdate", agent_birthdate);
			data.put("providerName", pp.getName());
			data.put("serviceName", ps.getName());
			return responseOK(data);
		}else {
			return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤。");
		}
    }

    /**
     * 異動代辦人資訊（未代辦條碼指定代辦人資料）
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/changeBoxAgentData")
	public ResponseEntity<RestResponseBean> changeBoxAgentData(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null&&sr.getMember()!=null) {
			/**
			 * 會員檢查
			 */
			String boxId = "";
	        String agent_uid = "";
	        String agent_birthdate = "";
	        if (params.get("boxId") != null) {
	        	boxId = params.get("boxId").toString();
	        } 
	        if (params.get("agent_uid") != null) {
	        	agent_uid = params.get("agent_uid").toString();
	        }
	        if (params.get("agent_birthdate") != null) {
	        	agent_birthdate = params.get("agent_birthdate").toString();
	        }
	        if(boxId==null||boxId.trim().length()==0||agent_uid==null||agent_uid.trim().length()==0||agent_birthdate==null||agent_birthdate.trim().length()==0) {
	        	return responseError(SysCode.MissingRequiredParameter, "id", "缺少必要參數。");
	        }
	        
            if(agent_uid.equalsIgnoreCase(SessionMember.getSessionMemberToMember(sr.getMember()).getUid())) {
            	return responseError(SysCode.AgentIsNotSameUser, "id", "代辦人不可為同一使用者。");
            }

            PortalBox box = portalBoxMapper.selectByPrimaryKey(Integer.valueOf(boxId));
            if(box.getDownloadSn()==null||box.getDownloadSn().trim().length()==0) {
            	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
            }
            String provider_key = "";
            for(String s:box.getDownloadSn().split("[,]")) {
            	PortalResourceDownload prd = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(s));
            	provider_key = prd.getProviderKey();
            }
            if(!SessionMember.getSessionMemberToMember(sr.getMember()).getAccount().equalsIgnoreCase(provider_key)) {
            	return responseError(SysCode.NoPermission, "id", "無該條碼存取權限。");
            }
			RandomUtils ru = new RandomUtils();
			String tmpAgentVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
			while (isDuplicateAgentVerify(tmpAgentVerify)) {
				tmpAgentVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
			}
			try {
				box.setAgentVerify(tmpAgentVerify);
				box.setAgentUid(agent_uid);
				box.setAgentBirthdate(CDateUtil.ROCDateToAD(agent_birthdate));
				//portalBoxMapper.updateByPrimaryKeySelective(box);
				PortalBox box1 = new PortalBox();
				box1.setId(ValidatorHelper.limitNumber(box.getId()));
				box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(tmpAgentVerify));
				box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(agent_uid));
				box1.setAgentBirthdate(ValidatorHelper.limitDate(CDateUtil.ROCDateToAD(agent_birthdate)));
				box1.setAgreeAgent(ValidatorHelper.limitNumber(box.getAgreeAgent()));
				portalBoxMapper.updateByPrimaryKeySelective(box1);
			} catch (Exception ex) {
				return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
			} 
			
			PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
			PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
			PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));
			
            Member agentMember = null;
            Map<String,Object> mParam = new HashMap<>();
            mParam.put("uid", agent_uid.toUpperCase());
            mParam.put("birthdate", CDateUtil.ROCDateToAD(agent_birthdate));
            mParam.put("statIsNullorZero", true);
            List<Member> mList = memberMapper.selectByExample(mParam);
            if(mList==null||mList.size()==0) {
            	/**
            	 * 44 授權失敗：您輸入的訊息，非 MyData 會員
            	 */
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("psdId", ValidatorHelper.limitNumber(psd.getId()));
				List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
            	if(portalResourceDownloadList!=null) {
            		for(PortalResourceDownload prd:portalResourceDownloadList) {
            			if(prd.getPrId()!=null&&prd.getPrId()>0) {
                			PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
                			ulogUtil.recordFullByPr(sr, ps, psd.getTxId(), portalResource, prd.getTransactionUid(), null, null, 44, HttpHelper.getRemoteIp(request));            				
            			}
            		}
            	}
            	return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
            }else {
            	agentMember = mList.get(0);
            	if(agentMember.getName()==null||agentMember.getName().trim().length()==0||agentMember.getInformMethod()==null||agentMember.getInformMethod().trim().length()==0) {
            		return responseError(SysCode.AgentMemberNotFill, "id", "代辦人聯絡資料不全。");
            	}
            }
        	/**
        	 * 委託人
			 * 41 委託申請：您選擇此項臨櫃服務委託代辦
			 * 42 同意：您同意委託代辦項目
			 * 43 授權成功：已提供驗證碼給代辦人
			 * 
			 * 代辦人
			 * 51 同意代辦：您同意項臨櫃服務代辦
        	 */
			Map<String,Object> param1 = new HashMap<String,Object>();
			param1.put("psdId", ValidatorHelper.limitNumber(psd.getId()));
			List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
        	if(portalResourceDownloadList!=null) {
        		for(PortalResourceDownload prd:portalResourceDownloadList) {
        			if(prd.getPrId()!=null&&prd.getPrId()>0) {
            			PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
            			//委託人
            			ulogUtil.recordFullByPr(sr, ps, psd.getTxId(), portalResource, prd.getTransactionUid(), null, null, 41, HttpHelper.getRemoteIp(request));
            			ulogUtil.recordFullByPr(sr, ps, psd.getTxId(), portalResource, prd.getTransactionUid(), null, null, 42, HttpHelper.getRemoteIp(request));
            			ulogUtil.recordFullByPr(sr, ps, psd.getTxId(), portalResource, prd.getTransactionUid(), null, null, 43, HttpHelper.getRemoteIp(request));
            			//代辦人
            			ulogUtil.recordFullByBoxAgent(box, ps, psd.getTxId(), portalResource, prd, null, null, 51, HttpHelper.getRemoteIp(request));
        			}
        		}
        	}
            
			/**
			 * 寄送代辦通知簡訊或EMAIL
			 */
			if(StringUtils.equals(agentMember.getInformMethod(), "email") && BooleanUtils.isTrue(agentMember.getEmailVerified())) {
				String from = "mydata_system@ndc.gov.tw";
				String title = "【個人化資料自主運用(MyData)平臺】服務代辦通知信（系統信件，請勿回覆）";
				String content = "您好：<br><br>"
						+MaskUtil.maskNameByO(SessionMember.getSessionMemberToMember(sr.getMember()).getName())+"於"+sdf8.format(new Date())+"請您代辦「"
						+ pp.getName()+" - "
						+ ps.getName()
						+ "」服務，當次有效代辦序號："
						+ tmpAgentVerify
						+ "，詳情請到<a href='" + frontendUrl + "/sp/service/counter/agent' target='_blank' title='MyData平臺代辦專區'>MyData平臺代辦專區</a>確認。<br>"
						+ "<br>"
						+ "若您未同意代辦，請忽略此信件。<br>"
						+ "<br>"
						+ "此為系統信件，請勿回信。<br>"
						+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。<br>"
						+ "<br>"
						+ "——-<br>"
						+ "我為什麼會收到這封信？<br>"
						+ "您會收到此封信件，是因為國家發展委員會個人化資料自主運用(MyData)平臺服務的代辦服務，當您被設定為代辦人時，系統會自動發此信通知您。";
				
				List<String> tmpReveicers = new ArrayList<String>();
				tmpReveicers.add(agentMember.getEmail());
				MailUtil.sendHtmlMail(tmpReveicers,from, title,content,mailEnable);
				sendLogUtil.writeSendLog(SendType.email, agentMember.getAccount(), agentMember.getEmail(), title, content);
			} else if (StringUtils.equals(agentMember.getInformMethod(), "mobile") && BooleanUtils.isTrue(agentMember.getMobileVerified())) {
				String smbody = "MyData通知-您有受託代辦「"
						+ ps.getName()
						+ "」服務，請至本平臺服務代辦專區(" + frontendUrl + "/sp/service/counter/agent)輸入代辦序號："
						+ tmpAgentVerify;
                SMSUtil.sendSms(agentMember.getMobile(), smbody);
				sendLogUtil.writeSendLog(SendType.mobile, agentMember.getAccount(), agentMember.getMobile(), smbody);
			} else {
				System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
			}
			
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("boxId", box.getId());
			data.put("agentName", MaskUtil.maskNameByO(agentMember.getName()));
			return responseOK(data);
		}else {
			return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤。");
		}
	}
    
    /**
     * 異動代辦人資訊（未代辦條碼指定代辦人資料）
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/cancelBoxAgentData")
	public ResponseEntity<RestResponseBean> cancelBoxAgentData(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null&&sr.getMember()!=null) {
			/**
			 * 會員檢查
			 */
			String boxId = "";
	        if (params.get("boxId") != null) {
	        		boxId = params.get("boxId").toString();
	        } 
	        if(boxId==null||boxId.trim().length()==0) {
	        		return responseError(SysCode.MissingRequiredParameter, "id", "缺少必要參數。");
	        }
            PortalBox box = portalBoxMapper.selectByPrimaryKey(Integer.valueOf(boxId));
            if(box.getDownloadSn()==null||box.getDownloadSn().trim().length()==0) {
            	return responseError(SysCode.DataNotExist, "id", "資料不存在。");
            }
            
            Member agentMember = null;
            Map<String,Object> mParam = new HashMap<>();
            mParam.put("uid", ValidatorHelper.removeSpecialCharacters(box.getAgentUid().toUpperCase()));
            mParam.put("birthdate", ValidatorHelper.limitDate(box.getAgentBirthdate()));
            mParam.put("statIsNullorZero", true);
            List<Member> mList = memberMapper.selectByExample(mParam);
            if(mList==null||mList.size()==0) {
            	return responseError(SysCode.AgentIsExit, "id", "代辦人不存在。");
            }else {
            	agentMember = mList.get(0);
            }
            String provider_key = "";
            for(String s:box.getDownloadSn().split("[,]")) {
            	PortalResourceDownload prd = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(s));
            	provider_key = prd.getProviderKey();
            }
            if(!SessionMember.getSessionMemberToMember(sr.getMember()).getAccount().equalsIgnoreCase(provider_key)) {
            	return responseError(SysCode.NoPermission, "id", "無該條碼存取權限。");
            }
            
			PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
			PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
			PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));            
			/**
			 * 委託人
			 * 45 委託終止：您終止此項委託代辦項目
			 * 
			 * 代辦人
			 * 52 委託終止：委託人已取消此代辦申請
			 */
			Map<String,Object> param1 = new HashMap<String,Object>();
			param1.put("psdId", ValidatorHelper.limitNumber(psd.getId()));
			List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
        	if(portalResourceDownloadList!=null) {
        		for(PortalResourceDownload prd:portalResourceDownloadList) {
        			if(prd.getPrId()!=null&&prd.getPrId()>0) {
            			PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
            			//委託人
            			ulogUtil.recordFullByBoxAgent(box, ps, psd.getTxId(), portalResource, prd, null, null, 45, null);
            			//代辦人
            			ulogUtil.recordFullByBoxAgent(box, ps, psd.getTxId(), portalResource, prd, null, null, 52, null);
        			}
        		}
        	}
			box.setAgentVerify(null);
			box.setAgentUid(null);
			box.setAgentBirthdate(null);
			//portalBoxMapper.updateByPrimaryKeySelective(box);
			PortalBox box1 = new PortalBox();
			box1.setId(ValidatorHelper.limitNumber(box.getId()));
			box.setAgentVerify(null);
			box.setAgentUid(null);
			box.setAgentBirthdate(null);
			box1.setAgreeAgent(ValidatorHelper.limitNumber(box.getAgreeAgent()));
			portalBoxMapper.updateByPrimaryKeySelective(box1);

			
			/**
			 * 寄送代辦通知簡訊或EMAIL
			 */
			if(StringUtils.equals(agentMember.getInformMethod(), "email") && BooleanUtils.isTrue(agentMember.getEmailVerified())) {
				String from = "mydata_system@ndc.gov.tw";
				String title = "【個人化資料自主運用(MyData)平臺】取消服務代辦通知信（系統信件，請勿回覆）";
				String content = "您好：\n\n"
						+MaskUtil.maskNameByO(SessionMember.getSessionMemberToMember(sr.getMember()).getName())+"於"+sdf8.format(new Date())+" 取消請您代辦「"
						+ pp.getName()+" - "
						+ ps.getName()
						+ "」服務。\n"
						+ "\n"
						+ "——-\n"
						+ "我為什麼會收到這封信？\n"
						+ "您會收到此封信件，是因為國家發展委員會個人化資料自主運用(MyData)平臺服務的代辦服務，當事人人取消代辦申請時，系統會自動發此信通知您。";
				
				List<String> tmpReveicers = new ArrayList<String>();
				tmpReveicers.add(agentMember.getEmail());
				MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
				sendLogUtil.writeSendLog(SendType.email, agentMember.getAccount(), agentMember.getEmail(), title, content);
			} else if (StringUtils.equals(agentMember.getInformMethod(), "mobile") && BooleanUtils.isTrue(agentMember.getMobileVerified())) {
				String smbody = "MyData通知-當事人已於"+sdf8.format(new Date())+"取消「"
						+ ps.getName()
						+ "」服務之代辦申請";
                SMSUtil.sendSms(agentMember.getMobile(), smbody);
				sendLogUtil.writeSendLog(SendType.mobile, agentMember.getAccount(), agentMember.getMobile(), smbody);
			} else {
				System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
			}
			
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("boxId", box.getId());
			data.put("agentName", MaskUtil.maskNameByO(agentMember.getName()));
			return responseOK(data);
		}else {
			return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤。");
		}
	}

    /**
     * TWCA ATM金融卡流程，初始步驟需先進行登入
     * @param request
     * @param response
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/ficAtmLogin")
	public ResponseEntity<RestResponseBean> postFicATMLogin(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
        String uid = "";
        if (params.get("uid") != null) {
            uid = params.get("uid").toString();
        }
        String VerifyNo = UUID.randomUUID().toString().replaceAll("-", "");
    		String token = twcaUtils.postAtmLogin(uid, VerifyNo);
    		Map<String,Object> data = new HashMap<String,Object>();
    		if(token!=null&&token.trim().length()>0) {
    			data.put("MemberNo", uid);
    			data.put("VerifyNo", VerifyNo);
    			data.put("Token", token);
    			data.put("BusinessNo", twcaUtils.BusinessNo);
    			data.put("ApiVersion", twcaUtils.ApiVersion);
    			data.put("HashKeyNo", twcaUtils.HashKeyNo);
    			data.put("HashKey", twcaUtils.HashKey);
    			data.put("IdentifyNo", twcaUtils.countIdentifyNoForATMInvoke(VerifyNo, token));
    			data.put("proxyURL", twcaUtils.twcaProxyURL+"/DO");
    			return responseOK(data);
    		}else {
    			return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤。");
    		}
    }
    
    @PostMapping("/fchInitParam")
	public ResponseEntity<RestResponseBean> postfchInitParam(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
        String uid = "";
        if (params.get("uid") != null) {
            uid = params.get("uid").toString();
        }
        String VerifyNo = UUID.randomUUID().toString().replaceAll("-", "");
    		Map<String,Object> data = new HashMap<String,Object>();
    		data.put("MemberNo", uid);
    		data.put("VerifyNo", VerifyNo);
    		data.put("proxyURL", twcaUtils.twcaProxyURL+"/CpntAuth");
    		return responseOK(data);
    }
    
    @PostMapping("/fcsInitParam")
	public ResponseEntity<RestResponseBean> postfcsInitParam(
			HttpServletRequest request,HttpServletResponse response,
			@RequestBody Map<String, Object> params) throws Exception {
        String uid = "";
        if (params.get("uid") != null) {
            uid = ValidatorHelper.removeSpecialCharactersForXss(params.get("uid").toString());
        }
        String VerifyNo = UUID.randomUUID().toString().replaceAll("-", "");
    		Map<String,Object> data = new HashMap<String,Object>();
    		data.put("MemberNo", ValidatorHelper.removeSpecialCharactersForXss(uid));
    		data.put("VerifyNo", ValidatorHelper.removeSpecialCharactersForXss(VerifyNo));
    		return responseOK(data);
    }
    
    /**
     * 由 keystoreCrypto 讀取公鑰
     *
     * @return
     * @throws Exception
     */
    private static Key getPublicKey() throws Exception {
        String keyStr = "";
        InputStream file_input = null;
        try {
        	file_input = new FileInputStream(keystorePublicCrypto);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(file_input);
            keyStr = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
			if(file_input!=null) {
				HttpClientHelper.safeClose(file_input);
			}
		}
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key k = keyFactory.generatePublic(keySpec);
        return k;
    }

    public static String getPublicKeyStr() throws Exception {
        String keyStr = "";
        InputStream file_input = null;
        try {
        	file_input = new FileInputStream(keystorePublicCrypto);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(file_input);
            keyStr = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
			if(file_input!=null) {
				HttpClientHelper.safeClose(file_input);
			}
		}
        return keyStr;
    }

    /**
     * 使用公鑰進行加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data) throws Exception {

        Key k = getPublicKey();

        Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("UlNB")));
        cipher.init(Cipher.ENCRYPT_MODE, k);

        byte[] bytes = cipher.doFinal(data.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(bytes);
    }

    public String maskName(String userName) {
        String ret = "";
        if (userName != null && userName.length() >= 3) {
            ret = userName.substring(0, 1);
            for (int i = 1; i < userName.length() - 1; i++) {
                ret = ret + "*";
            }
            ret = ret + userName.substring(userName.length() - 1);
        } else if (userName != null && userName.length() == 2) {
            ret = userName.substring(1, 2);
        } else if (userName != null && userName.length() == 1) {
            ret = "*";
        }
        return ret;
    }

    private HttpSession changeSessionId(HttpServletRequest request) {
        HttpSession oldSession = request.getSession();

        Map<String, Object> attrs = new HashMap<>();
        for (String name : Collections.list(oldSession.getAttributeNames())) {
            attrs.put(name, oldSession.getAttribute(name));
            oldSession.removeAttribute(name);
        }
        //oldSession.invalidate(); // 令目前 Session 失效

        HttpSession newSession = request.getSession();
        for (String name : attrs.keySet()) {
            newSession.setAttribute(name, attrs.get(name));
        }
        return newSession;
    }

    @PostMapping("/test/immigration")
    public ResponseEntity<RestResponseBean> testImmigration(@RequestBody Map<String, Object> params) throws Exception {
        ValidUtil validUtil = validFactory.getImmigrationUtil();
        String result = validUtil.call(params, WebServiceJobId.BirthDate);
        return responseOK(result);
    }

    @PostMapping("/test/validUtil")
    public ResponseEntity<RestResponseBean> testValidUtil(@RequestBody Map<String, Object> params) throws Exception {
        String uid = MapUtils.getString(params, "ck_personId");
        String birthDate = MapUtils.getString(params, "ck_birthDate");
        System.out.println("==uid==:"+uid);
        System.out.println("==birthDate==:"+birthDate);
        ValidUtil validUtil = validFactory.get(uid);
        String result = validUtil.call(params, WebServiceJobId.BirthDate);
        System.out.println("==result==:"+result);
        return responseOK(result);
    }
    
	public boolean isDuplicateDownloadVerify(String tmpDownloadVerify) {
		boolean ret = false;
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
		Date startTime = cal.getTime();
		
		/*PortalBoxExample portalBoxExample = new PortalBoxExample();
		portalBoxExample.createCriteria().andDownloadVerifyEqualTo(tmpDownloadVerify).andCtimeGreaterThanOrEqualTo(startTime);*/
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("downloadVerify", tmpDownloadVerify);
		param.put("sCtime", startTime);
		List<PortalBox> conDownloadVerifyList = portalBoxMapper.selectByExample(param);
		if (conDownloadVerifyList != null && conDownloadVerifyList.size() > 0) {
			ret = true;
		}
		return ret;
	}
	
	public boolean isDuplicateAgentVerify(String tmpAgentVerify) {
		boolean ret = false;
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -8);
		Date startTime = cal.getTime();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("agentVerify", tmpAgentVerify);
		param.put("sCtime", startTime);
		List<PortalBox> conDownloadVerifyList = portalBoxMapper.selectByExample(param);
		if (conDownloadVerifyList != null && conDownloadVerifyList.size() > 0) {
			ret = true;
		}
		return ret;
	}

    /**
     * 檢查 uuidcheck checkCode
     */
    @PostMapping("/opt/check")
    public ResponseEntity<RestResponseBean> postOtpCheck(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        String msuuidcheck = MapUtils.getString(params, "msuuidcheck");
        String mscheckCode = MapUtils.getString(params, "mscheckCode");

        Long msuuidcheckTime = (Long) session.getAttribute("msuuidcheckTime");
        if(msuuidcheckTime == null || msuuidcheckTime + VERIFY_TIME < new Date().getTime()) {
            return responseError(SysCode.InvalidVerifyCode, "id", "身分驗證錯誤!");
        }

        if(sr!=null&&sr.getMember()!=null) {
            Member member = sr.getMember();
            String session_msuuidcheck = session.getAttribute("msuuidcheck") == null ? null : session.getAttribute("msuuidcheck").toString();
            String session_mscheckCode = session.getAttribute("mscheckCode") == null ? null : session.getAttribute("mscheckCode").toString();
            if (StringUtils.isNotBlank(msuuidcheck) && StringUtils.isNotBlank(mscheckCode)  &&
            		session_msuuidcheck !=null && session_mscheckCode != null && 
            		StringUtils.isNotBlank(session_msuuidcheck) && StringUtils.isNotBlank(session_mscheckCode) &&
                    StringUtils.equals(msuuidcheck, session_msuuidcheck) &&
                    StringUtils.equals(mscheckCode, session_mscheckCode)) {

                List<LogMemberSession> list = logMemberSessionMapper.findWithSessionId(session.getId());

                if(list != null && list.size() > 0) {
                    LogMemberSession logMemberSession = list.get(0);
                    LogMemberSession logMemberSession1 = new  LogMemberSession();
                    logMemberSession1.setId(ValidatorHelper.limitNumber(logMemberSession.getId()));
                    logMemberSession1.setState(MemberSessionState.Login.name());
                    logMemberSession1.setUpdateAt(new Date());
                    logMemberSessionMapper.updateByPrimaryKeySelective(logMemberSession1);

                    // clear session
                    logMemberSessionMapper.deleteOtherMember(ValidatorHelper.limitNumber(member.getId()), ValidatorHelper.removeSpecialCharacters(session.getId()));
                }

                session.removeAttribute("msuuidcheckTime");
                session.removeAttribute("msuuidcheck");
                session.removeAttribute("mscheckCode");

                return responseOK();
            }else{
                return responseError(SysCode.InvalidVerifyCode, "id", "無效的驗證碼!");
            }
        }else {
            return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
        }
    }

    /**
     * 檢查 uuidcheck checkCode
     */
    @PostMapping("/opt/resend")
    public ResponseEntity<RestResponseBean> postOtpResend(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) throws Exception {

        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);

        if(sr!=null&&sr.getMember()!=null) {
            Member member = SessionMember.getSessionMemberToMember(sr.getMember());

            List<LogMemberSession> list = logMemberSessionMapper.findWithSessionId(session.getId());

            if(list != null && list.size() > 0) {
                LogMemberSession logMemberSession = list.get(0);
                logMemberSessionUtil.sendOTP(member, session, logMemberSession);
                member.setLogMemberSession(logMemberSession);
                return responseOK(member);
            } else {
                return responseError(SysCode.SystemError, "id", "系統錯誤!");
            }
        }
        return responseError(SysCode.AuthenticateFail, "id", "身分驗證錯誤!");
    }

    @PostMapping("/check/isMember")
    public ResponseEntity<RestResponseBean> checkIsMember(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) {

        HttpSession session = request.getSession();
        String id = MapUtils.getString(params, "id");
        String uid = MapUtils.getString(params, "uid");
        String birthdate = MapUtils.getString(params, "birthdate");


        if(StringUtils.isBlank(uid) || StringUtils.isBlank(birthdate)) {
            System.out.println("== check is member 缺少必要參數 ==");
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }

        Map<String, Object> data = new HashMap<>();

        try {
            String account = encoder.encodeToString((uid.toUpperCase() + birthdate).getBytes("UTF-8"));

            Map<String, Object> mParam = new HashMap<>();
            mParam.put("account", account);
            List<Member> mList = memberMapper.selectByExample(mParam);

            if (mList.size() > 0) {
                Member member = mList.get(0);
                if(member.getStat()!=null&&member.getStat()!=0) {
                	return responseError(SysCode.AccountNotSynBirthDateDisabled, "uid", "身分驗證失敗，請重新輸入。");
                }
                // 必須有填寫姓名及聯絡方式才可驗證為正式會員
                if(!StringUtils.isEmpty(member.getName())&&!StringUtils.isEmpty(member.getInformMethod())) {
                    Map<String, Object> mpParam = new HashMap<>();
                    mpParam.put("memberId", ValidatorHelper.limitNumber(member.getId()));
                    List<MemberPrivacy> mpList = memberPrivacyMapper.selectByExample(mpParam);

                    if(mpList.size() > 0) {
                    	Member tMember = new Member();
                    	tMember.setInformMethod(member.getInformMethod());
                    	tMember.setIsDoubleVerify(member.getIsDoubleVerify());
                    	tMember.setIsOld(member.getIsOld());
                        data.put("agree", true);
                        data.put("member", tMember);

                        session.removeAttribute("agreePrivacyDate");
                        session.removeAttribute("privacyVersion");
                        return responseOK(data);
                    }                	
                }else {
                	memberMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(member.getId()));
                	//memberPrivacyMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(member.getId()));
                }
            }

            String uuid = UUID.randomUUID().toString();

            session.setAttribute("checkPrivacyUUID" + id, uuid);
            data.put("agree", false);
            data.put("checkPrivacyUUID", uuid);

            return responseOK(data);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return responseError(SysCode.SystemError, "uid", "系統錯誤");
        }
    }

    @PostMapping("/check/isMemberWithMoeca")
    public ResponseEntity<RestResponseBean> checkIsMemberWithMoeca(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) {

        String id = MapUtils.getString(params, "id", "");
        String uid = MapUtils.getString(params, "uid");

        if(StringUtils.isBlank(uid)) {
            System.out.println("== check is member 缺少必要參數 ==");
            return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
        }

        Map<String, Object> data = new HashMap<>();

        try {
            String account = encoder.encodeToString((uid.toUpperCase()).getBytes("UTF-8"));

            Map<String, Object> mParam = new HashMap<>();
            mParam.put("account", account);
            List<Member> mList = memberMapper.selectByExample(mParam);

            if (mList.size() > 0) {
                Member member = mList.get(0);
                // 必須有填寫姓名及聯絡方式才可驗證為正式會員
                if(!StringUtils.isEmpty(member.getName())&&!StringUtils.isEmpty(member.getInformMethod())) {
                    Map<String, Object> mpParam = new HashMap<>();
                    mpParam.put("memberId", ValidatorHelper.limitNumber(member.getId()));
                    List<MemberPrivacy> mpList = memberPrivacyMapper.selectByExample(mpParam);

                    if(mpList.size() > 0) {
                        data.put("agree", true);
                        data.put("member", member);
                        return responseOK(data);
                    }
                }else {
                	memberMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(member.getId()));
                	//memberPrivacyMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(member.getId()));
                }
            }


            String uuid = UUID.randomUUID().toString();
            HttpSession session = request.getSession();
            session.setAttribute("checkPrivacyUUID"+id, uuid);
            data.put("agree", false);
            data.put("checkPrivacyUUID", uuid);

            return responseOK(data);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return responseError(SysCode.SystemError, "uid", "系統錯誤");
        }
    }

    @PostMapping("/agree/privacy")
    public ResponseEntity<RestResponseBean> agreePrivacy(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody Map<String, Object> params) {

        String id = MapUtils.getString(params, "id", "");
        String checkPrivacyUUID = MapUtils.getString(params, "checkPrivacyUUID");
        String version = MapUtils.getString(params, "version");
        String key = "checkPrivacyUUID" + id;

        HttpSession session = request.getSession();
        String sysCheckPrivacyUUID = (String) session.getAttribute(key);

        if(StringUtils.equals(checkPrivacyUUID, sysCheckPrivacyUUID)) {
            session.setAttribute("agreePrivacyDate", new Date());
            session.setAttribute("privacyVersion", version);
            session.removeAttribute(key);
            return responseOK();
        }

        return responseError(SysCode.InvalidParameter, "uid", "無效的參數");
    }
}
