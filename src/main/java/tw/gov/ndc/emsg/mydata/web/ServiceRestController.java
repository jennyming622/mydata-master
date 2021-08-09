package tw.gov.ndc.emsg.mydata.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.riease.common.enums.ActionEvent;
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.HttpHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.riease.common.enums.AuditStatus;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;
import com.riease.common.util.RandomUtils;
import com.riease.common.util.SslUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import sun.security.pkcs.PKCS7;
import sun.security.util.DerInputStream;
import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.factory.CallSpNotifyFactory;
import tw.gov.ndc.emsg.mydata.mapper.*;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;
import tw.gov.ndc.emsg.mydata.service.AbstractCallSpNotify;
import tw.gov.ndc.emsg.mydata.type.DownloadType;
import tw.gov.ndc.emsg.mydata.type.RequestType;
import tw.gov.ndc.emsg.mydata.type.SystemOptionType;
import tw.gov.ndc.emsg.mydata.type.VerificationType;
import tw.gov.ndc.emsg.mydata.util.*;

@Controller
@RequestMapping("/rest/service")
public class ServiceRestController extends BaseRestController {
	private static Logger logger = LoggerFactory.getLogger(ServiceRestController.class);
	private static DecimalFormat formatter = new DecimalFormat("#.#");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("年MM月dd日HH時mm分ss秒");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private static final int BUFFER_SIZE = 4096;
	/**
	 * 檔案保存期限(小時)
	 */
	private static int fileStoreTime = 8;
    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder =  Base64.getDecoder();
	
	@Value("${ftp.host}")
	private String ftpHost;
	@Value("${ftp.port}")
	private String ftpPort;
	@Value("${ftp.username}")
	private String ftpUsername;
	@Value("${ftp.password}")
	private String ftpPassword;
	@Value("${ftp.download.path.temp}")
	private String ftpPath;	
	@Value("${ftp.secretkey}")
	private String ftpSecretkey;
	@Value("${ftp.iv}")
	private String ftpIv;
	
	@Value("${app.frontend.context.url}")
	private String frontendContextUrl;
	@Value("${app.download.path.temp}")
	private String downloadPath;

	// Mydata
	@Value("${gsp.oidc.client.id}")
	private String clientId;
	@Value("${app.oidc.redirect.uri}")
	private String redirectUri;

	@Value("${app.oidc.response.type}")
	private String responseType;
	@Value("${app.oidc.response.mode}")
	private String responseMode;

	@Value("${app.oidc.authorize.uri}")
	private String authorizeUri;

	@Value("${pdf.path.temp}")
	private String pdfPath;
	
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired
	private UlogApiMapperExt ulogApiMapperExt;
	@Autowired
	private UlogApiMapper ulogApiMapper;
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalBoxMapper portalBoxMapper;
	@Autowired
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	@Autowired
	private PortalServiceDownloadSubMapper portalServiceDownloadSubMapper;
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private MemberTempMapper memberTempMapper;
	@Autowired
	private PortalServiceScopeMapper portalServiceScopeMapper;
	@Autowired 
	private VerifyMapper verifyMapper;
	@Autowired
	private UlogUtil ulogUtil;
	@Autowired
	private SendLogUtil sendLogUtil;
	@Autowired 
	private LoginUtil loginUtil;
    @Autowired
    private IcscUtils icscUtils;

    @Autowired
	private CallSpNotifyFactory callSpNotifyFactory;
    @Autowired
	private SystemOptionUtil systemOptionUtil;
	
	private CBCUtil CBCUtil;
	
	/**
	 * 服務申請
	 * @param psId
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 */
	@PostMapping("/apply/{psId}")
	public ResponseEntity<RestResponseBean> getPersonalDownloadApply(@PathVariable("psId") Integer psId,
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestBody Map<String, Object> params) {
		request.getSession().setMaxInactiveInterval(120);
		logger.info("[{}] params {}", psId, params.toString());
		Integer verificationlevel = MapUtils.getInteger(params, "verificationlevel");
		String txId = MapUtils.getString(params, "txId");
		PortalService portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psId));
		String model = ValidatorHelper.removeSpecialCharacters(MapUtils.getString(params, "model"));
		VerificationType verificationType = null;
		String ip = HttpHelper.getRemoteIp(request);
		try {
			HttpSession session = request.getSession();
			SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
			logger.info("sr {}", new ObjectMapper().writeValueAsString(sr));
			ObjectMapper om = new ObjectMapper();
			int httpstatuscode = 0;			
			logger.info("[{}] ==prIdList s==", psId);
			ArrayList<String> prIdList = (ArrayList<String>) params.get("prIdList");
			Integer tmpLevel = 4;
			PortalResource portalResource = null;
			if(prIdList!=null&&prIdList.size()>0) {
				for(String s:prIdList) {
					logger.info("[{}] prId = {}", psId, s);
					Integer prId = Integer.valueOf(s);
					portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
					if(portalResource.getLevel().compareTo(tmpLevel)<0) {
						tmpLevel = portalResource.getLevel();
					}
				}
			}
			/**
			 * 檢查授權權限是否符合
			 */
			logger.info("[{}] ==prIdList e==", psId);
			if(!LoginUtil.checkInLevel(session, tmpLevel)) {
				return responseError(SysCode.NoPermission, "apply", "授權等級或權限不足!");
			}			
			//returnUrl
			String returnUrl = params.get("returnUrl")==null?"":params.get("returnUrl").toString();
			String returnUrlBase = "";
			String returnUrlQuery = "";
			//System.out.println("returnUrl="+returnUrl);
			logger.info("[{}] returnUrl= {}", psId, returnUrl);
			if(returnUrl!=null&&returnUrl.trim().length()>0) {
				//returnUrl 剖析
				String[] urlArray = returnUrl.split("[?]");
				if(urlArray.length > 0){
					returnUrlBase = urlArray[0];
					if(urlArray.length > 1){
						returnUrlQuery =  urlArray[1];
					}
				}
			}

			/**
			 * 0. init parameter permission_ticket,secret_key
			 * 1. 下載資料集 prIdList
			 * 2. 使用參數 params
			 * 3. 起Thread進行處理
			 */
			List<String> downloadSnList = new ArrayList<String>();
			List<String> successDownloadSnList = new ArrayList<String>();
			List<String> failDownloadSnList = new ArrayList<String>();
			List<String> failResourceIdList = new ArrayList<String>();
			List<PortalResourceDownload> retPrdList = new ArrayList<PortalResourceDownload>();
			String permission_ticket = UUID.randomUUID().toString();
			String secret_key = UUID.randomUUID().toString().replace("-", "");
			int sp_wait_time = 0;
			/**
			 * 須將上面申請參數於處理完成後，紀錄於「服務申請下載表」
			 * 最後起一個Thread處理，檔案下載後打包事宜，並將打包資料檔紀錄於「服務申請下載表」
			 * 1. permission_ticket
			 * 2. secret_key
			 * 3. wait_time 
			 * 4. ctime
			 * 5. downloadSnList -----
			 * 6. etime （估計時間）每個 步驟完， 用 ctime+waittime = etime, 比較哪個最後，再壓到table的data ----
			 * 7. pack zip file path ----> AES壓完
			 * 8. 狀態
			 * 9. dtime -----
			 */

			verificationType = VerificationType.convertToType(sr.getRoleType(), sr.getMultifactorType());
			PortalServiceDownload psdbean = new PortalServiceDownload();
			psdbean.setPsId(ValidatorHelper.limitNumber(psId));
			psdbean.setClientId(ValidatorHelper.removeSpecialCharacters(portalService.getClientId()));
			psdbean.setPermissionTicket(ValidatorHelper.removeSpecialCharacters(permission_ticket));
			psdbean.setSecretKey(ValidatorHelper.removeSpecialCharacters(secret_key));
			psdbean.setCtime(new Date());
			psdbean.setStat(0);
			psdbean.setTxId(ValidatorHelper.removeSpecialCharacters(txId));
			psdbean.setIsShow(portalService.getIsShow()==null?0:ValidatorHelper.limitNumber(portalService.getIsShow()));
			int psdbeanRet = portalServiceDownloadMapper.insertSelective(psdbean);

			Integer pcsId = MapUtils.getInteger(params, "pcsId");
			if(pcsId != null) {
				PortalServiceDownloadSub downloadSub = new PortalServiceDownloadSub();
				downloadSub.setPsdId(psdbean.getId());
				downloadSub.setPcsId(pcsId);
				portalServiceDownloadSubMapper.insert(downloadSub);
			}
			
			/**
			 * 申請回傳資料是否失敗 504
			 */
			boolean downloadSnHttpCode = true;
			List<ResourceApplyThread> finalApplyThreadList = new ArrayList<ResourceApplyThread>();
			if(prIdList!=null&&prIdList.size()>0) {
				/**
				 * 多個資料集申請
				 */
				/**
				 * SP 申請
				 * 要求 insert ulog_api sp 4 授權狀態 1.登入(AS) 2.授權(AS) 3.登出(AS) 4. 要求(SP) 5. 傳送(DP)
				 * 6.儲存(SP) 7.取消授權(AS) 8.取用, 申請(11), 申請完成(12), 收到(13)
				 */
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 21, ip);
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 22, ip);
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 23, ip);
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 24, ip);
				
				/**
				 * 個別資料集申請
				 */
				for(String prIdstr:prIdList) {
					logger.info("[{}][{}] service apply prId= {}", psId, txId, prIdstr);
					if(prIdstr!=null&&prIdstr.trim().length()>0) {
						ResourceApplyThread thread = new ResourceApplyThread();
						thread.setDownloadPath(downloadPath);
						thread.setTxId(txId);
						thread.setVerificationType(verificationType);
						thread.setPsId(psId);
						thread.setPsdId(psdbean.getId());
						//thread.setBatchId(batchId);
						thread.setPs(portalService);
						thread.setPrId(Integer.valueOf(prIdstr));
						thread.setUlogUtil(ulogUtil);
						thread.setSendLogUtil(sendLogUtil);
						thread.setParams(params);
						thread.setSession(session);
						thread.setSr(sr);
						thread.setFtpHost(ftpHost);
						thread.setFtpPort(ftpPort);
						thread.setFtpUsername(ftpUsername);
						thread.setFtpPassword(ftpPassword);
						thread.setFtpPath(ftpPath);
						thread.setFtpSecretkey(ftpSecretkey);
						thread.setFtpIv(ftpIv);
						thread.setPortalResourceMapper(portalResourceMapper);
						thread.setPortalResourceScopeMapper(portalResourceScopeMapper);
						thread.setPortalResourceDownloadMapper(portalResourceDownloadMapper);
						thread.setPortalResourceExtMapper(portalResourceExtMapper);
						thread.setPortalBoxMapper(portalBoxMapper);
						thread.setPortalServiceDownloadMapper(portalServiceDownloadMapper);
						thread.start();
						finalApplyThreadList.add(thread);
					}
				}
			}
			boolean checkApplyThreadStat = true;
			while(checkApplyThreadStat) {
				boolean tmpCheck = true;
				try {
					Thread.sleep(500l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(ResourceApplyThread p:finalApplyThreadList) {
					if(p.getBoxId()==null) {
						tmpCheck = false;
					}
				}
				if(tmpCheck==true) {
					checkApplyThreadStat = false;
				}
			}
			String downloadSnListStr = "";
			if(finalApplyThreadList!=null&&finalApplyThreadList.size()>0) {
				for(ResourceApplyThread p:finalApplyThreadList) {
					if(p!=null&&p.getDownloadSn().trim().length()>0) {
						if(downloadSnListStr!=null&&downloadSnListStr.trim().length()>0) {
							downloadSnListStr = downloadSnListStr +","+p.getDownloadSn().trim();
						}else {
							downloadSnListStr = p.getDownloadSn().trim();
						}
					}
					if(p.getWaitTime()>sp_wait_time) {
						sp_wait_time = p.getWaitTime();
					}
					retPrdList.add(p.getPrd());
				}
			}
			psdbean.setDownloadSnList(ValidatorHelper.removeSpecialCharacters(downloadSnListStr));
			psdbean.setWaitTime(ValidatorHelper.limitNumber(sp_wait_time));
			psdbean.setUid(ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getUid()));
			psdbean.setVerificationLevel(verificationlevel==null?0:ValidatorHelper.limitNumber(verificationlevel));
			
			if(StringUtils.equals(model, "MyData")) {
				if(verificationType==null) {
					psdbean.setVerificationType(VerificationType.cer.name());
				}else {
					psdbean.setVerificationType(verificationType.name());
				}
				psdbean.setModel(model);
			} else if(StringUtils.equals(model, "SP")){
				if(verificationType==null) {
					psdbean.setVerificationType(VerificationType.cer.name());
				}else {
					psdbean.setVerificationType(verificationType.name());
				}
				Map<String,Object> mparam = new HashMap<String,Object>();
				mparam.put("txId", txId);
				List<MemberTemp> memberTempList = memberTempMapper.selectByExample(mparam);
				if(memberTempList!=null&&memberTempList.size()>0) {
					MemberTemp memberTemp = memberTempList.get(0);
					/**
					 * SP-twid
					 * SP-cht
					 */
					if(memberTemp.getAsId()!=null) {
						psdbean.setModel("SP2-"+ValidatorHelper.removeSpecialCharacters(memberTemp.getAsId()));
					}else {
						psdbean.setModel(model);
					}
				}else {
					psdbean.setModel(model);
				}
			}else {
				if(verificationType==null) {
					psdbean.setVerificationType(VerificationType.cer.name());
				}else {
					psdbean.setVerificationType(verificationType.name());
				}
				psdbean.setModel(model);
			}
			
			portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean);
			logger.info("[{}][{}] ==============psdbeanRet====================: {}", psId, txId, psdbeanRet);
			logger.info("[{}][{}] ==============psdbean id====================: {}", psId, txId, psdbean.getId());			
			
			/**
			 * 起Thread
			 * 1. 監控檔案 (資料集下載)
			 * 2. 封裝檔案
			 * 3. 上傳檔案
			 * 4. 更新portalServiceDownload files url
			 * 
			 * downloadPath = /root/mydata/temp
			 */
			String randomId = SequenceHelper.createUUID();
			String pstempFileName = downloadPath + File.separator +sdf6.format(new Date())+File.separator+ randomId;
			File pstempFile = new File(pstempFileName);
			Thread psThread = new WaitingServiceDownloadAndFtp(pstempFile, ftpHost, ftpUsername, ftpPassword,psdbean,portalServiceMapper,portalResourceDownloadMapper,portalResourceMapper,portalServiceDownloadMapper, ftpSecretkey);
			psThread.start();

			/**
			 * 使用tx_id有無判斷是線上服務或臨櫃服務 1.txId not null:線上服務 2.txId null:臨櫃服務
			 * 臨櫃服務才需要條碼(txId == null)
			 */
			Integer tmpBoxId = null;
			if(txId==null) {
				/**
				 * batch 5.紀錄到portal_resource_download
				 */
				PortalResourceDownload prd = new PortalResourceDownload();
				prd.setDownloadSn(UUID.randomUUID().toString());
				prd.setProviderKey(ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getAccount()));
				prd.setPrId(0);
				prd.setWaitTime(ValidatorHelper.limitNumber(sp_wait_time));
				prd.setCtime(new Date());
				// stat 0:未下載; 1:已下載
				prd.setStat(0);
				prd.setPsdId(ValidatorHelper.limitNumber(psdbean.getId()));
				prd.setDownloadType(DownloadType.vspDownload.name());
				prd.setEmail(ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getEmail()));
				prd.setIsShow(-1);
				portalResourceDownloadMapper.insertSelective(prd);
				
				/**
				 * 產生portal_box
				 */
				RandomUtils ru = new RandomUtils();
				String tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
				while (isDuplicateDownloadVerify(tmpDownloadVerify)) {
					tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
				}
				PortalBox box = new PortalBox();
				Calendar cal2 = GregorianCalendar.getInstance();
				cal2.setTime(new Date());
				cal2.add(Calendar.SECOND, sp_wait_time);
				Date boxStartTime = cal2.getTime();
				box.setCtime(boxStartTime);
				box.setDownloadSn(psdbean.getDownloadSnList());
				box.setStat(0);
				box.setVerifyPwd(tmpDownloadVerify);
				box.setDownloadVerify(tmpDownloadVerify);
				box.setPsdId(psdbean.getId());
				portalBoxMapper.insertSelective(box);
				tmpBoxId = box.getId();
			}
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("psdId", psdbean.getId()==null?"0":psdbean.getId().toString());
			data.put("prdList", retPrdList);
			data.put("boxId", tmpBoxId);
			/**
			 * downloadSnHttpCode 表示申請下載過程中是否有錯誤
			 */
			//data.put("downloadSnHttpCode", downloadSnHttpCode);
			return responseOK(data);
		}catch(Exception ex) {
			logger.error(ex.getLocalizedMessage(), ex);
			PortalServiceDownload psdbean = new PortalServiceDownload();
			psdbean.setPsId(ValidatorHelper.limitNumber(psId));
			psdbean.setClientId(ValidatorHelper.removeSpecialCharacters(portalService.getClientId()));
			psdbean.setCtime(new Date());
			psdbean.setTxId(ValidatorHelper.removeSpecialCharacters(txId));
			psdbean.setRequestStatus(RequestType.fail.name());
			if(verificationType==null) {
				psdbean.setVerificationType(VerificationType.cer.name());
			}else {
				psdbean.setVerificationType(verificationType.name());
			}
			psdbean.setModel(model);
			psdbean.setIsShow(portalService.getIsShow()==null?0:ValidatorHelper.limitNumber(portalService.getIsShow()));
			portalServiceDownloadMapper.insertSelective(psdbean);
			logger.error("[{}] {}", psId, ex);
			return responseError(SysCode.InvalidAction,psId.toString(),"無效的動作!");
		}
	}
	
	/**
	 * 停用/apply/{psId}，的SP-API，由此發動
	 * service-detail.html, service-detail-new.html
	 * $('#step4').show();
	 * ckeckedApplyDirect(obj);
	 * @param psdId
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/spNotify/{psdId}")
	public ResponseEntity<RestResponseBean> postApplyAfterSpAPINotify(@PathVariable("psdId") Integer psdId,
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestBody Map<String, Object> params) throws Exception {
		request.getSession().setMaxInactiveInterval(120);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);		
		PortalServiceDownload psdbean = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdId));
		if(psdbean==null) {
			return responseError(SysCode.AccessDenied,psdId.toString(),"存取被拒絕!");
		}
		if(!psdbean.getUid().equalsIgnoreCase(SessionMember.getSessionMemberToMember(sr.getMember()).getUid())) {
			return responseError(SysCode.NoPermission,psdId.toString(),"無存取權限!");
		}

		String ip = HttpHelper.getRemoteIp(request);
		
		/**
		 * 0. init parameter permission_ticket,secret_key
		 * 1. 下載資料集 prIdList
		 * 2. 使用參數 params
		 * 3. 起Thread進行處理
		 */
		List<String> downloadSnList = new ArrayList<String>();
		List<String> successDownloadSnList = new ArrayList<String>();
		List<String> failDownloadSnList = new ArrayList<String>();
		List<String> failResourceIdList = new ArrayList<String>();
		/**
		 * 申請回傳資料是否失敗 504
		 */
		boolean downloadSnHttpCode = true;
		/**
		 * 只要一個對就不是
		 */
		boolean spApiErrorCode = true;
		
		Integer psId = ValidatorHelper.limitNumber(psdbean.getPsId());
		String txId = ValidatorHelper.removeSpecialCharacters(psdbean.getTxId());
		PortalService portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdbean.getPsId()));
		PortalResource portalResource = null;
		Map<String, Object> prdparam = new HashMap<String, Object>();
		prdparam.put("psdId", ValidatorHelper.limitNumber(psdbean.getId()));
		List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(prdparam);
		if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
			for(PortalResourceDownload prd:portalResourceDownloadList) {
				portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
				/**
				 * 判斷 portal_resource_download code 正不正常
				 * 200
				 */
				if(prd.getCode()!=null&&!prd.getCode().equalsIgnoreCase("200")&&!prd.getCode().equalsIgnoreCase("201")
						&&!prd.getCode().equalsIgnoreCase("429")&&!prd.getCode().equalsIgnoreCase("204")) {
					failDownloadSnList.add(prd.getDownloadSn());
					failResourceIdList.add(portalResource.getResourceId());
					downloadSnHttpCode = false;
				}else {
					successDownloadSnList.add(prd.getDownloadSn());
				}
			}
		}else {
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 28, ip);
			return responseError(SysCode.DataNotExist,psdId.toString(),"資料不存在!");
		}


		ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_240, null, null, ip);
		/**
		 * 參數檢查 returnUrl
		 */
		String returnUrl = params.get("returnUrl").toString();
		String returnUrlBase = "";
		String returnUrlQuery = "";
		if(returnUrl==null||returnUrl.trim().length()==0) {
			return responseError(SysCode.DataNotExist,psdId.toString(),"資料不存在!");
		}else {
			//returnUrl 剖析
			String[] urlArray = returnUrl.split("[?]",1000);
			if(urlArray.length > 0){
				returnUrlBase = urlArray[0];
				if(urlArray.length > 1){
					returnUrlQuery =  urlArray[1];
				}
			}
		}		
		
		/**
		 * 通知SP-API，成功
		 * 此處為檔案成功後通知
		 * 通知，portalService.getLogoutRedirectUri()
		 */
		String clientSecret = portalService.getClientSecret()+portalService.getClientSecret();
		ulogUtil.recordFullByPr(sr, portalService, psdbean.getTxId(), null, null, ActionEvent.EVENT_290, null, null, ip);
		Map<String,String> data = new HashMap<String,String>();
		logger.info("[{}][{}] === SP-API START ===", psdbean.getPsId(), psdbean.getTxId());
		data.put("psdId", psdbean.getId()==null?"0":psdbean.getId().toString());
		try {
			if(portalService!=null&&portalService.getLogoutRedirectUri()!=null) {
				Integer clientTwoWayVer = systemOptionUtil.use(SystemOptionType.FEATURE_CLIENT_TWO_WAY.name(), ip);
				int respCode = 0;
				String outStr = "";
				logger.info("[{}][{}] {}", psId, txId, portalService.getLogoutRedirectUri());
				logger.info("[{}][{}] clientTwoWayVer >> {}", psId, txId, clientTwoWayVer);
				if(clientTwoWayVer.equals(new Integer(1))) {
					//組 response 依是否有完成下載
					SpNotify spNotify = new SpNotify();
					spNotify.setTxId(txId);
					spNotify.setPermissionTicket(psdbean.getPermissionTicket());
					if(portalService.getResponseMode()!=null&&portalService.getResponseMode().trim().length()>0&&!portalService.getResponseMode().trim().equalsIgnoreCase("form_post")) {
						spNotify.setApiKey(portalService.getResponseMode().trim());
					}
					if(downloadSnHttpCode) {
						String secretKey = new String(base64Encoder.encode(CBCUtil.encrypt_cbc(psdbean.getSecretKey().getBytes(), clientSecret, portalService.getCbcIv())));
						spNotify.setSecretKey(secretKey);
					} else {
						List<String> resourceIdList = new ArrayList<>();
						if(failResourceIdList!=null&&failResourceIdList.size()>0) {
							for(String s:failResourceIdList) {
								resourceIdList.add(s);
							}
						}
						spNotify.setUnableToDeliver(resourceIdList);
					}

					// call SpNotify API 依是否有使用雙向憑證決定使用哪個 Service
					AbstractCallSpNotify callSpNotify = callSpNotifyFactory.get(portalService.getCertEdate() != null);
					outStr = callSpNotify.responseData(spNotify);
					System.out.println(String.format("[%d][%s] data - 0 >> %s", psId, txId, outStr));
					respCode = callSpNotify.call(portalService.getLogoutRedirectUri(), outStr);
					System.out.println(String.format("[%d][%s][%d]data - 0.1 >> %s", psId, txId, respCode, outStr));
				} else {
					// 新流程測試 OK 後可拿掉
					if (portalService.getLogoutRedirectUri().startsWith("https")) {
						SslUtils.ignoreSsl();
						URL connectto = new URL(portalService.getLogoutRedirectUri());
						HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
						if(conn!=null) {
							conn.setRequestMethod("POST");
							conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
							conn.setRequestProperty("Accept", "application/json");
							conn.setUseCaches(false);
							conn.setAllowUserInteraction(false);
							conn.setInstanceFollowRedirects(false);
							conn.setDoOutput(true);
							conn.setConnectTimeout(15000);

							String secretKey = new String(base64Encoder.encode(CBCUtil.encrypt_cbc(psdbean.getSecretKey().getBytes(), clientSecret, portalService.getCbcIv())));
							OutputStream os = null;
							try {
								os = conn.getOutputStream();
								if(downloadSnHttpCode) {
									outStr = "{\"tx_id\":\""+txId+"\",";
									if(portalService.getResponseMode()!=null&&portalService.getResponseMode().trim().length()>0&&!portalService.getResponseMode().trim().equalsIgnoreCase("form_post")) {
										outStr = outStr + "\"apiKey\":\"" + portalService.getResponseMode().trim() +"\",";
									}
									outStr = outStr + "\"permission_ticket\":\"" + psdbean.getPermissionTicket() +"\",";
									outStr = outStr +"\"secret_key\":\"" +secretKey +"\"}";
								}else {
									String resourceIdStr = "";
									if(failResourceIdList!=null&&failResourceIdList.size()>0) {
										for(String s:failResourceIdList) {
											if(resourceIdStr==null||resourceIdStr.trim().length()==0) {
												resourceIdStr = "\""+s+"\"";
											}else {
												resourceIdStr = resourceIdStr + ",\""+s+"\"";
											}
										}
									}
									outStr = "{\"tx_id\":\""+txId+"\",";
									if(portalService.getResponseMode()!=null&&portalService.getResponseMode().trim().length()>0&&!portalService.getResponseMode().trim().equalsIgnoreCase("form_post")) {
										outStr = outStr + "\"apiKey\":\"" + portalService.getResponseMode().trim() +"\",";
									}
									outStr = outStr + "\"permission_ticket\":\"" + psdbean.getPermissionTicket() +"\",";
									outStr = outStr + "\"unable_to_deliver\":[" +resourceIdStr +"]}";
								}
								logger.info("[{}][{}] === SP-API 1 ===: {} : {}", psId, txId, new Date(), outStr);
								os.write(outStr.getBytes("UTF-8"));
								os.close();	
							}finally {
								if(os!=null) {
									HttpClientHelper.safeClose(os);
								}
							}
							respCode = conn.getResponseCode();	
						}
					}else {
						URL connectto = new URL(portalService.getLogoutRedirectUri());
						HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
						if(conn!=null) {
							conn.setRequestMethod("POST");
							conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
							conn.setRequestProperty("Accept", "application/json");
							conn.setUseCaches(false);
							conn.setAllowUserInteraction(false);
							conn.setInstanceFollowRedirects(false);
							conn.setDoOutput(true);
							conn.setConnectTimeout(15000);

							String secretKey = new String(base64Encoder.encode(CBCUtil.encrypt_cbc(psdbean.getSecretKey().getBytes(), clientSecret, portalService.getCbcIv())));
							OutputStream os = null;
							try {
								os = conn.getOutputStream();
								if(downloadSnHttpCode) {
									outStr = "{\"tx_id\":\""+txId+"\",";
									if(portalService.getResponseMode()!=null&&portalService.getResponseMode().trim().length()>0&&!portalService.getResponseMode().trim().equalsIgnoreCase("form_post")) {
										outStr = outStr + "\"apiKey\":\"" + portalService.getResponseMode().trim() +"\",";
									}
									outStr = outStr + "\"permission_ticket\":\"" + psdbean.getPermissionTicket() +"\",";
									outStr = outStr +"\"secret_key\":\"" +secretKey +"\"}";
								}else {
									String resourceIdStr = "";
									if(failResourceIdList!=null&&failResourceIdList.size()>0) {
										for(String s:failResourceIdList) {
											if(resourceIdStr==null||resourceIdStr.trim().length()==0) {
												resourceIdStr = "\""+s+"\"";
											}else {
												resourceIdStr = resourceIdStr + ",\""+s+"\"";
											}
										}
									}
									outStr = "{\"tx_id\":\""+txId+"\",";
									if(portalService.getResponseMode()!=null&&portalService.getResponseMode().trim().length()>0&&!portalService.getResponseMode().trim().equalsIgnoreCase("form_post")) {
										outStr = outStr + "\"apiKey\":\"" + portalService.getResponseMode().trim() +"\",";
									}
									outStr = outStr + "\"permission_ticket\":\"" + psdbean.getPermissionTicket() +"\",";
									outStr = outStr + "\"unable_to_deliver\":[" +resourceIdStr +"]}";
								}
								logger.info("[{}][{}] === SP-API 1 ===: {} : {}", psId, txId, new Date(), outStr);
								os.write(outStr.getBytes("UTF-8"));
								os.close();
							}finally {
								if(os!=null) {
									HttpClientHelper.safeClose(os);
								}
							}
							respCode = conn.getResponseCode();
						}
					}
				}

				ulogUtil.recordFullByPr(sr, portalService, psdbean.getTxId(), null, null, ActionEvent.EVENT_291, null, null, ip);
				if(respCode==200||respCode==201) {
					ulogUtil.recordFullByPr(sr, portalService, psdbean.getTxId(), null, null, ActionEvent.EVENT_292, null, null, ip);
					//傳送success
					logger.info("[{}][{}] == mydata-sp notification success! 1==: {} - {}", psId, txId, respCode, new Date());
					logger.info("[{}][{}] {} Success!{}", psId, txId, portalService.getLogoutRedirectUri(), outStr);
					System.out.println(String.format("[%d][%s] %d, data >> %s", psId, txId, respCode, outStr));

					ulogUtil.recordFullByPr(sr, portalService, psdbean.getTxId(), null, null, ActionEvent.EVENT_300, null, null, ip);
					PortalServiceDownload psdbean1 = new PortalServiceDownload();
					psdbean1.setId(ValidatorHelper.limitNumber(psdbean.getId()));
					psdbean1.setCode(ValidatorHelper.removeSpecialCharacters(String.valueOf(respCode)));
					psdbean1.setRequestStatus(RequestType.success.name());
					portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean1);
				}else {
					logger.info("[{}][{}] == mydata-sp notification error! ==: {}", psId, txId, respCode);
					logger.info("[{}][{}] {} error!{}", psId, txId, portalService.getLogoutRedirectUri(), outStr);
					System.out.println(String.format("[%d][%s] %d, data >> %s", psId, txId, respCode, outStr));
					/**
					 * 應該起Thread去等，並傳送訊息，不應在原線程sleep，這會造成服務停頓
					 */
					spApiErrorCode = false;
					PortalServiceDownload psdbean1 = new PortalServiceDownload();
					psdbean1.setId(ValidatorHelper.limitNumber(psdbean.getId()));
					psdbean1.setCode(ValidatorHelper.removeSpecialCharacters(String.valueOf(respCode)));
					psdbean1.setRequestStatus(RequestType.fail.name());
					portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean1);
					for(PortalResourceDownload prd:portalResourceDownloadList) {
						portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, prd.getTransactionUid(), null, null, 28, ip);
					}

				}
			}
			txId = new String(base64Encoder.encode(CBCUtil.encrypt_cbc(txId.getBytes(), clientSecret, portalService.getCbcIv())));
			String sp_return_url = conbindReturnUrl(returnUrlBase,returnUrlQuery,"200",txId);
			if(!downloadSnHttpCode) {
				sp_return_url = conbindReturnUrl(returnUrlBase,returnUrlQuery,"504",txId);
			}
			if(!spApiErrorCode) {
				sp_return_url = conbindReturnUrl(returnUrlBase,returnUrlQuery,"410",txId);
			}
			data.put("sp_return_url", sp_return_url);
			data.put("sp_return_url_205", conbindReturnUrl(returnUrlBase,returnUrlQuery,"205",txId));
			// 刪除memberTemp中的資料
			Integer tempId = (Integer) params.get("tempId");
			memberTempMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(tempId));
		}catch(Exception ex) {
			logger.info("[{}][{}] {}", psId, txId, ex);
			String tmpTxId = new String(base64Encoder.encode(CBCUtil.encrypt_cbc(txId.getBytes(), clientSecret, portalService.getCbcIv())));
			String sp_return_url = conbindReturnUrl(returnUrlBase,returnUrlQuery,"410",tmpTxId);
			data.put("sp_return_url", sp_return_url);
			data.put("sp_return_url_205", conbindReturnUrl(returnUrlBase,returnUrlQuery,"205",tmpTxId));
			// 刪除memberTemp中的資料
			Integer tempId = (Integer) params.get("tempId");
			memberTempMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(tempId));
			PortalServiceDownload psdbean1 = new PortalServiceDownload();
			psdbean1.setId(ValidatorHelper.limitNumber(psdbean.getId()));
			psdbean1.setCode("403");
			psdbean1.setRequestStatus(RequestType.fail.name());
			portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean1);
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 29, ip);
		}
		return responseOK(data);
	}

	@PostMapping("/refuse/{psdId}")
	public ResponseEntity<RestResponseBean> refuseSpAPINotify(@PathVariable("psdId") Integer psdId,
																	  HttpServletRequest request,
																	  HttpServletResponse response,
																	  @RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		PortalServiceDownload psdbean = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdId));
		if (psdbean == null) {
			return responseError(SysCode.AccessDenied, psdId.toString(), "存取被拒絕!");
		}
		if (!psdbean.getUid().equalsIgnoreCase(SessionMember.getSessionMemberToMember(sr.getMember()).getUid())) {
			return responseError(SysCode.NoPermission, psdId.toString(), "無存取權限!");
		}

		String txId = psdbean.getTxId();
		PortalService portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdbean.getPsId()));

		//ulogUtil.recordFullByPs(psdbean, null, null, "241", null, null, HttpHelper.getRemoteIp(request));
		ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_241, null, null, HttpHelper.getRemoteIp(request));
		PortalResource portalResource = null;
		Map<String, Object> prdparam = new HashMap<String, Object>();
		prdparam.put("psdId", ValidatorHelper.limitNumber(psdbean.getId()));
		List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(prdparam);
		if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
			for (PortalResourceDownload prd : portalResourceDownloadList) {
				portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
				ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, prd.getTransactionUid(), null, null, 28, HttpHelper.getRemoteIp(request));
			}
		}
		return responseOK();
	}
	
	
	
	@PostMapping("/loginBySp")
	public ResponseEntity<RestResponseBean> loginBySp(
			@RequestBody Map<String,Object> params,
			BindingResult result,
			HttpServletRequest request) throws Exception {
		
		System.out.println("params = "+params.toString());
		
		String uid = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("uid")));
		String birthdate = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("birthday")));
		String psId = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("psId")));
		String verificationType = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("verificationType")));
		String asId = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("asId")));

		String version = MapUtils.getString(params, "version");
		Boolean agree = MapUtils.getBoolean(params, "agree");

		if(agree == false) {
			HttpSession session = request.getSession();
			session.setAttribute("agreePrivacyDate", new Date());
			session.setAttribute("privacyVersion", version);
		}
		
		if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(birthdate) || StringUtils.isEmpty(psId)) {
			return responseError(SysCode.MissingRequiredParameter, "uid", "缺少必要參數");
		}
		
		Map<String,Object> pParam = new HashMap<>();
		pParam.put("psId", Integer.parseInt(psId));
		List<PortalServiceScope> scopeList = portalServiceScopeMapper.selectByExample(pParam);
		String scope = String.join(" ", scopeList.stream()
				.map(PortalServiceScope::getScope)
				.collect(Collectors.toList()));
		
		Verify verify = new Verify();
		verify.setKey(UUID.randomUUID().toString());
		verify.setUid(uid);
		verify.setBirthdate(sdf6.parse(birthdate));
		if(verificationType==null) {
			verify.setCheckLevel("cer");
		}else {
			verify.setCheckLevel(verificationType.toLowerCase(Locale.ENGLISH));
		}
		verify.setScope(ValidatorHelper.removeSpecialCharacters(scope));
		verify.setCtime(new Date());
		verifyMapper.insertSelective(verify);
		
		Member member = loginUtil.doNewAutoLogin(verify, request, null);
		
		RestResponseBean rb = new RestResponseBean();
		rb.setCode(SysCode.OK.value());
		rb.setText(SysCode.OK.name());
		rb.setData(member);
		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
	}
	
	@PostMapping("/saveMember")
	public ResponseEntity<RestResponseBean> saveMember(
			@RequestBody Map<String,Object> params,
			BindingResult result,
			HttpServletRequest request) {
		
		String id = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("id")));
		String name = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("name")));
		String method = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("method")));
		
		if(StringUtils.isEmpty(id) || StringUtils.isEmpty(name) || StringUtils.isEmpty(method)) {
			return responseError(SysCode.MissingRequiredParameter, "id", "缺少必要參數!");
		}
		
		Member member1 = new Member();
		member1.setId(Integer.valueOf(id));
		if(name!=null&&name.trim().length()>0) {
			member1.setName(name);
		}
		if(method!=null&&method.trim().length()>0) {
			member1.setInformMethod(method);
		}
		if(params.get("mobile") != null) member1.setMobile(ValidatorHelper.removeSpecialCharacters(params.get("mobile").toString()));
		if(params.get("email") != null) member1.setEmail(ValidatorHelper.removeSpecialCharacters(params.get("email").toString()));
		if (method != null) {
            if (method.equalsIgnoreCase("email")) {
            	member1.setEmailVerified(true);
            } else if (method.equalsIgnoreCase("mobile")) {
            	member1.setMobileVerified(true);
            }
        }
		memberMapper.updateByPrimaryKeySelective(member1);
		
		Member member = memberMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(Integer.parseInt(id)));
		return responseOK(member);
	}
	
	@PostMapping("/updateMember")
	public ResponseEntity<RestResponseBean> updateMember(
			@RequestBody Map<String,Object> params,
			BindingResult result,
			HttpServletRequest request) {
		
		String id = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("id")));
		Integer tempId = ValidatorHelper.limitNumber((Integer) params.get("tempId"));
		String changeMail = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("changeMail")));
		String changeMobile = ValidatorHelper.removeSpecialCharacters(objToStr(params.get("changeMobile")));
		
		MemberTemp temp = memberTempMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(tempId));
		
		Member member1 = new Member();
		if(changeMail.contentEquals("1")) {
			member1.setId(Integer.parseInt(id));
			member1.setEmail(ValidatorHelper.removeSpecialCharacters(temp.getEmail()));
		}
		if(changeMobile.contentEquals("1")) {
			member1.setId(Integer.parseInt(id));
			member1.setMobile(ValidatorHelper.removeSpecialCharacters(temp.getMobile()));
		}
		if(member1.getId()!=null) {
			memberMapper.updateByPrimaryKeySelective(member1);
		}
		
		Member member = memberMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(Integer.parseInt(id)));
		return responseOK(member);
	}
	
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}
		return result.toString();
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
	
	private List<UlogApiExt> getUlog(String userAccount) throws IllegalAccessException, InvocationTargetException {
		List<UlogApiExt> ulogExtList = new ArrayList<>();
		List<UlogApi> ulogList = ulogApiMapperExt.getLogByAccount(userAccount);
		if (ulogList != null && ulogList.size() > 0) {
			for (UlogApi ulog : ulogList) {
				UlogApiExt ulogExt = new UlogApiExt();
				BeanUtils.copyProperties(ulogExt, ulog);
				if (ulog.getAuditEvent() != null) {
					ulogExt.setActionStr(AuditStatus.getStatus(ulog.getAuditEvent()).toString());
				}
				if (ulog.getClientId() != null) {
					Map<String,Object> psparam = new HashMap<String,Object>();
					psparam.put("clientId", ValidatorHelper.removeSpecialCharacters(ulog.getClientId()));
					List<PortalService> psList = portalServiceMapper.selectByExample(psparam);
					if (psList.size() > 0)
						ulogExt.setServiceName(psList.get(0).getName());
				}
				if (ulog.getAuditEvent() != null && ulog.getAuditEvent() == 1) {
					ulogExt.setServiceItem("您使用eGov帳號/憑證進行MyData平台登入作業!");
					ulogExt.setServiceItem("個人化資料自主運用(MyData)平臺");
				} else {
					if (ulog.getScope() != null) {
						StringBuilder sb = new StringBuilder();
						String[] scope = ulog.getScope().split(" ");
						for (String s : scope) {
							Map<String,Object> sparam = new HashMap<String,Object>();
							sparam.put("scope", ValidatorHelper.removeSpecialCharacters(s));
							List<PortalResourceScope> prsList = portalResourceScopeMapper.selectByExample(sparam);
							PortalResourceScope prs = null;
							if (prsList != null && prsList.size() > 0) {
								prs = prsList.get(0);
							}
							if (prs != null)
								sb.append(prs.getDescription()).append(" ");
						}
						ulogExt.setServiceItem(sb.toString());
					}
				}
				if (ulog.getCtime() != null) {
					ulogExt.setCtimeStr(sdf1.format(ulog.getCtime()));
				}
				ulogExtList.add(ulogExt);
			}
		}

		return ulogExtList;
	}

	private String connect(String url, JSONObject body) throws IOException {
		URL connectto = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
		StringBuilder sb = new StringBuilder();
		if(conn!=null) {
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(false);
			conn.setDoOutput(true);
			OutputStream os = null;
			try {
				os = conn.getOutputStream();
				os.write(body.toString().getBytes("UTF-8"));
				os.close();	
			}finally {
				if(os!=null) {
					HttpClientHelper.safeClose(os);
				}
			}
			InputStreamReader inputStream = null; 
			BufferedReader br = null;
			try {
				inputStream = new InputStreamReader(conn.getInputStream());
				br = new BufferedReader(inputStream);
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();			
			}finally {
				if(inputStream!=null) {
					HttpClientHelper.safeClose(inputStream);
				}
				if(br!=null) {
					HttpClientHelper.safeClose(br);
				}
			}	
		}
		return sb.toString();
	}

	private String[] toStringArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		String[] arr = new String[array.length()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = array.optString(i);
		}
		return arr;
	}

	private Date getDownloadTime(Date date, int waitTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, waitTime);
		return calendar.getTime();
	}
	
	private File packZipWithPassword(File output, ArrayList<File> sources, String password) throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		/**
		 * COMP_STORE = 0;（僅打包，不壓縮） （對應好壓的儲存） 
		 * COMP_DEFLATE = 8;（預設） （對應好壓的標準） 
		 * COMP_AES_ENC = 99;
		 */
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		parameters.setPassword(password);
		zipFile.addFiles(sources, parameters);
		return output;
	}

	private String replaceParamStrForPost(String paramstr, SessionRecord sessionRecord) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		System.out.println("in replace=\n" + paramstr);
		String patternStr = "#\\{[\\w:/\\\\.]*\\}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		while (matcher.find()) {
			matchStrList.add(matcher.group());
			System.out.println(matcher.group());
		}
		if (matchStrList != null && matchStrList.size() > 0) {
			System.out.println("------------paramstr s---------------");
			for (String s : matchStrList) {
				String forReplaceStr = "";
				String matchInnerStr = s.replaceAll("#\\{", "").replaceAll("\\}", "").trim();
				System.out.println(matchInnerStr);
				if (matchInnerStr.equalsIgnoreCase("accessToken")) {
					forReplaceStr = "\"" + TokenUtils.getFullAccessToken(sessionRecord.getAuthToken()) + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = "\"" + Base64.getUrlEncoder().encodeToString(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid().getBytes("UTF-8")) + "\"";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid());
					if( matcher1.matches() ){
						idtype = "idNo";
						forReplaceStr = "\"" + idtype + "\"";
					}else {
						idtype = "banNo";
						forReplaceStr = "\"" + idtype + "\"";
					}
				}
				if (matchInnerStr.equalsIgnoreCase("birthdate")) {
					String datefinalStr = sdf2.format(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getBirthdate());
					forReplaceStr = "\"" + datefinalStr + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = "\"" + sessionRecord.getUserName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail() + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}

	
	/**
	 * PDF檔案格式
	 * 
	 * @author mac
	 *
	 */
	public class TableHeader extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte cb = writer.getDirectContent();
				cb.saveState();
				cb.beginText();

				BaseFont bf = null;
				try {
					bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Header
				float x = document.right(20);
				float y = document.top(-20);

				// 中
				cb.setFontAndSize(bf, 25);
				String title = "下載資料";
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, (document.right() + document.left()) / 2, y, 0);

				// 右
				y = document.top(-10);
				cb.setFontAndSize(bf, 8);
				String textstr = "報表產製時間：" + sdf1.format(new Date());
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, textstr, x, y, 0);
				cb.endText();
				cb.restoreState();
			} catch (Exception de) {
				throw new ExceptionConverter(de);
			}
		}
	}

	public class TableHeaderForFreeway extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte cb = writer.getDirectContent();
				// 左
				Image imgSoc = Image.getInstance(Config.logoPath+"/freeway-logo.png");
				imgSoc.scaleToFit(50, 50);
				imgSoc.setAbsolutePosition(100, 780);
				cb.addImage(imgSoc);

				cb.saveState();
				cb.beginText();

				BaseFont bf = null;
				try {
					bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Header
				float x = document.right(20);
				float y = document.top(-20);

				// 中
				cb.setFontAndSize(bf, 25);
				String title = "ETC欠費總額查詢";
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, (document.right() + document.left()) / 2, y, 0);

				// 右
				y = document.top(-10);
				cb.setFontAndSize(bf, 8);
				String textstr = "報表產製時間：" + sdf1.format(new Date());
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, textstr, x, y, 0);
				cb.endText();
				cb.restoreState();
			} catch (Exception de) {
				throw new ExceptionConverter(de);
			}
		}
	}

	public class TableHeaderForTpde extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte cb = writer.getDirectContent();
				// 左
				Image imgSoc = Image.getInstance(Config.logoPath+"/tpde.png");
				imgSoc.scaleToFit(50, 50);
				imgSoc.setAbsolutePosition(100, 780);
				cb.addImage(imgSoc);

				cb.saveState();
				cb.beginText();

				BaseFont bf = null;
				try {
					bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Header
				float x = document.right(20);
				float y = document.top(-20);

				// 中
				cb.setFontAndSize(bf, 25);
				String title = "高級中等學校學籍紀錄";
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, (document.right() + document.left()) / 2, y, 0);

				// 右
				y = document.top(-10);
				cb.setFontAndSize(bf, 8);
				String textstr = "報表產製時間：" + sdf1.format(new Date());
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, textstr, x, y, 0);
				cb.endText();
				cb.restoreState();
			} catch (Exception de) {
				throw new ExceptionConverter(de);
			}
		}
	}

	public static class TableHeaderForRisReviewOne extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte cb = writer.getDirectContent();
				// 左
				Image imgSoc = Image.getInstance(Config.logoPath+"/ris-logo.png");
				imgSoc.scaleToFit(100, 300);
				imgSoc.setAbsolutePosition(100, 780);
				cb.addImage(imgSoc);

				cb.saveState();
				cb.beginText();

				BaseFont bf = null;
				try {
					bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Header
				float x = document.right(20);
				float y = document.top(-20);

				// 中
				cb.setFontAndSize(bf, 25);
				String title = "個人戶籍資料";
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, (document.right() + document.left()) / 2, y, 0);

				// 右
				y = document.top(-10);
				cb.setFontAndSize(bf, 8);
				String textstr = "報表產製時間：" + sdf1.format(new Date());
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, textstr, x, y, 0);
				cb.endText();
				cb.restoreState();
			} catch (Exception de) {
				throw new ExceptionConverter(de);
			}
		}
	}

	public static class TableHeaderForRisReviewAll extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte cb = writer.getDirectContent();
				// 左
				Image imgSoc = Image.getInstance(Config.logoPath+"/ris-logo.png");
				imgSoc.scaleToFit(100, 300);
				imgSoc.setAbsolutePosition(100, 780);
				cb.addImage(imgSoc);

				cb.saveState();
				cb.beginText();

				BaseFont bf = null;
				try {
					bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Header
				float x = document.right(20);
				float y = document.top(-20);

				// 中
				cb.setFontAndSize(bf, 25);
				String title = "現戶全戶戶籍資料";
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, (document.right() + document.left()) / 2, y, 0);

				// 右
				y = document.top(-10);
				cb.setFontAndSize(bf, 8);
				String textstr = "報表產製時間：" + sdf1.format(new Date());
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, textstr, x, y, 0);
				cb.endText();
				cb.restoreState();
			} catch (Exception de) {
				throw new ExceptionConverter(de);
			}
		}
	}

	public static class TableHeaderForRisReviewCog extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte cb = writer.getDirectContent();
				// 左
				Image imgSoc = Image.getInstance(Config.logoPath+"/ris-logo.png");
				imgSoc.scaleToFit(100, 300);
				imgSoc.setAbsolutePosition(100, 780);
				cb.addImage(imgSoc);

				cb.saveState();
				cb.beginText();

				BaseFont bf = null;
				try {
					bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Header
				float x = document.right(20);
				float y = document.top(-20);

				// 中
				cb.setFontAndSize(bf, 25);
				String title = "親屬關係資料";
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, (document.right() + document.left()) / 2, y, 0);

				// 右
				y = document.top(-10);
				cb.setFontAndSize(bf, 8);
				String textstr = "報表產製時間：" + sdf1.format(new Date());
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, textstr, x, y, 0);
				cb.endText();
				cb.restoreState();
			} catch (Exception de) {
				throw new ExceptionConverter(de);
			}
		}
	}

	public static class TableHeaderForK12ea extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte cb = writer.getDirectContent();
				// 左
				Image imgSoc = Image.getInstance(Config.logoPath+"/k12ea-logo.png");
				imgSoc.scaleToFit(100, 300);
				imgSoc.setAbsolutePosition(100, 780);
				cb.addImage(imgSoc);

				cb.saveState();
				cb.beginText();

				BaseFont bf = null;
				try {
					bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Header
				float x = document.right(20);
				float y = document.top(-20);

				// 中
				cb.setFontAndSize(bf, 25);
				String title = "高級中等學校學籍";
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, (document.right() + document.left()) / 2, y, 0);

				// 右
				y = document.top(-10);
				cb.setFontAndSize(bf, 8);
				String textstr = "報表產製時間：" + sdf1.format(new Date());
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, textstr, x, y, 0);
				cb.endText();
				cb.restoreState();
			} catch (Exception de) {
				throw new ExceptionConverter(de);
			}
		}
	}	
	
	public static String replaceParamStrForPostNotAppendEscapAppendTransactionUid(String paramstr, SessionRecord sessionRecord,
			String transactionUid,Map<String, Object> paramMap) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		System.out.println("in replace=\n" + paramstr);
		String patternStr = "#\\{[\\w:/\\\\.]*\\}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		while (matcher.find()) {
			matchStrList.add(matcher.group());
			System.out.println(matcher.group());
		}
		if (matchStrList != null && matchStrList.size() > 0) {
			System.out.println("------------paramstr s---------------");
			for (String s : matchStrList) {
				String forReplaceStr = "";
				String qbirthdayStr = "";
				String matchInnerStr = s.replaceAll("#\\{", "").replaceAll("\\}", "").trim();
				System.out.println(matchInnerStr);
				if (matchInnerStr.equalsIgnoreCase("accessToken")) {
					forReplaceStr = TokenUtils.getFullAccessToken(sessionRecord.getAuthToken());
				}
				if (matchInnerStr.equalsIgnoreCase("transactionUid")) {
					forReplaceStr = transactionUid;
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid();
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = Base64.getUrlEncoder().encodeToString(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid());
					if( matcher1.matches() ){
						idtype = "idNo";
						forReplaceStr = idtype;
					}else {
						idtype = "banNo";
						forReplaceStr = idtype;
					}
				}
				if (matchInnerStr.equalsIgnoreCase("birthdate")) {
					String datefinalStr = sdf2.format(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getBirthdate());
					forReplaceStr = datefinalStr;
				}
				if (matchInnerStr.equalsIgnoreCase("qbirthday")) {
					String datefinalStr = sdf.format(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getBirthdate())+"T00:00:00.000Z";
					forReplaceStr = datefinalStr;
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = sessionRecord.getUserName();
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail();
				}
				if (matchInnerStr.equalsIgnoreCase("lprNumber")) {
					forReplaceStr = paramMap.get("lprNumber").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("locationHsnCd")) {
					forReplaceStr = paramMap.get("locationHsnCd").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_hsn_cd")) {
					forReplaceStr = paramMap.get("etd_location_hsn_cd").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_town_cd")) {
					forReplaceStr = paramMap.get("etd_location_town_cd").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_vill_cd")) {
					forReplaceStr = paramMap.get("etd_location_vill_cd").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("etd_tax_no")) {
					forReplaceStr = paramMap.get("etd_tax_no").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("etd_apply_items")) {
					forReplaceStr = paramMap.get("etd_apply_items").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("INQ_ID")) {
					forReplaceStr = paramMap.get("INQ_ID").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("PR_VAL_TP")) {
					forReplaceStr = paramMap.get("PR_VAL_TP").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("carNo")) {
					forReplaceStr = paramMap.get("carNo").toString().toUpperCase();
				}
				if (matchInnerStr.equalsIgnoreCase("qplateno")) {
					forReplaceStr = paramMap.get("qplateno").toString().toUpperCase();
				}
				if (matchInnerStr.equalsIgnoreCase("etd_car_no")) {
					forReplaceStr = paramMap.get("etd_car_no").toString().toUpperCase();
				}
				if (matchInnerStr.equalsIgnoreCase("caseYears")) {
					forReplaceStr = paramMap.get("caseYears").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("etd_case_years")) {
					forReplaceStr = paramMap.get("etd_case_years").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("INQ_YR")) {
					forReplaceStr = paramMap.get("INQ_YR").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("ExamYear")) {
					forReplaceStr = paramMap.get("ExamYear").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("qcartype")) {
					forReplaceStr = paramMap.get("qcartype").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("qlic")) {
					forReplaceStr = paramMap.get("qlic").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("mobile")) {
					forReplaceStr = paramMap.get("mobile").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("verifycode")) {
					forReplaceStr = paramMap.get("verifycode").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("startdate")) {
					forReplaceStr = paramMap.get("startdate").toString().replaceAll("-", "/");
				}
				if (matchInnerStr.equalsIgnoreCase("enddate")) {
					forReplaceStr = paramMap.get("enddate").toString().replaceAll("-", "/");
				}
				if (matchInnerStr.equalsIgnoreCase("invnum")) {
					forReplaceStr = paramMap.get("invnum").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("invdate")) {
					forReplaceStr = paramMap.get("invdate").toString().replaceAll("-", "/");
				}
				if (matchInnerStr.equalsIgnoreCase("WaterNo")) {
					forReplaceStr = paramMap.get("WaterNo").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("ApplicantName")) {
					forReplaceStr = paramMap.get("ApplicantName").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("FilingCaseNo")) {
					forReplaceStr = paramMap.get("FilingCaseNo").toString();
				}
				if (matchInnerStr.equalsIgnoreCase("EquipRegNo")) {
					forReplaceStr = paramMap.get("EquipRegNo").toString();
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}		
	
	public String conbindTicketReturnUrl(String returnUrlBase,String returnUrlQuery,String tx_id) {
		String ret = "";
		if(returnUrlBase!=null&&returnUrlBase.trim().length()>0) {
			if(returnUrlQuery!=null&&returnUrlQuery.trim().length()>0) {
				ret = returnUrlBase +"?tx_id="+tx_id+"&"+returnUrlQuery;
			}else {
				ret = returnUrlBase +"?tx_id="+tx_id;
			}
		}
		return ret;
	}
	
	public String conbindReturnUrl(String returnUrlBase,String returnUrlQuery,String code,String txId) {
		String ret = "";
		if(returnUrlBase!=null&&returnUrlBase.trim().length()>0) {
			if(returnUrlQuery!=null&&returnUrlQuery.trim().length()>0) {
				ret = returnUrlBase +"?code="+code+"&tx_id="+txId+"&"+returnUrlQuery;
			}else {
				ret = returnUrlBase +"?code="+code+"&tx_id="+txId;
			}
		}
		return ret;
	}	
	
	
	private File packZip(File output, ArrayList<File> sources)
			throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		/**
		 * COMP_STORE = 0;（僅打包，不壓縮） （對應好壓的儲存） 
		 * COMP_DEFLATE = 8;（預設） （對應好壓的標準） 
		 * COMP_AES_ENC = 99;
		 */
		parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zipFile.addFiles(sources, parameters);
		return output;
	}	
	
	
	/**
	* 轉換密鑰
	* @param key 二進制密鑰
	* @return Key 密鑰
	*/
	public static Key toKey(String key) throws Exception {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(key);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return originalKey;
	}	
	
	/**
	* 加密數據
	* @param data 待加密數據
	* @param key 密鑰
	* @return byte[] 加密後的數據
	*/
	public static byte[] encrypt(byte[] data, String key) throws Exception {
		// 還原密鑰
		Key k = toKey(key);
		// 實例化
		Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0VDQi9QS0NTNVBhZGRpbmc=")));
		// 初始化，設置為加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);
		// 執行操作
		return cipher.doFinal(data);
	}
	
	/**
	* 解密數據
	* @param data 待解密數據
	* @param key 密鑰
	* @return byte[] 解密後的數據
	*/
	public static byte[] decrypt(byte[] data, String key) throws Exception {
		// 歡迎密鑰
		Key k = toKey(key);
		// 實例化
		Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0VDQi9QS0NTNVBhZGRpbmc=")));
		// 初始化，設置為解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);
		// 執行操作
		return cipher.doFinal(data);
	}
	
}
