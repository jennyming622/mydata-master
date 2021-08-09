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
import java.net.SocketException;
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
import java.util.StringJoiner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.commons.beanutils.BeanUtils;
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
import org.springframework.web.bind.annotation.GetMapping;
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
import com.octo.captcha.service.image.ImageCaptchaService;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;
import com.riease.common.util.RandomUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.PortalBatchDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalBox;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxExt;
import tw.gov.ndc.emsg.mydata.entity.PortalProvider;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownload;
import tw.gov.ndc.emsg.mydata.mapper.ContactusMapper;
import tw.gov.ndc.emsg.mydata.mapper.MemberMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBatchDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBoxMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalProviderMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;
import tw.gov.ndc.emsg.mydata.mapper.UlogApiMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;
import tw.gov.ndc.emsg.mydata.type.DownloadType;
import tw.gov.ndc.emsg.mydata.type.RequestType;
import tw.gov.ndc.emsg.mydata.type.VerificationType;
import tw.gov.ndc.emsg.mydata.util.LoginUtil;
import tw.gov.ndc.emsg.mydata.util.SendLogUtil;
import tw.gov.ndc.emsg.mydata.util.TokenUtils;
import tw.gov.ndc.emsg.mydata.util.UlogUtil;

@Controller
@RequestMapping("/rest/mutipledownload")
public class MutipleDownloadRestController extends BaseRestController {
	private static final Logger logger = LoggerFactory.getLogger(MutipleDownloadRestController.class);
	private static DecimalFormat formatter = new DecimalFormat("#.#");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("年MM月dd日HH時mm分ss秒");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
	private static final int BUFFER_SIZE = 4096;
	/**
	 * 檔案保存期限(小時)
	 */
	private static int fileStoreTime = 8;
    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder =  Base64.getDecoder();
	/**
	 * 密鑰算法
	 */
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * 加密/解密算法/工作模式/填充方式 AES/ECB/PKCS5PADDING
	 */
	//public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";	
	
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
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
	private ContactusMapper contactusMapper;
	@Autowired
	private PortalBoxMapper portalBoxMapper;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private UlogApiMapperExt ulogApiMapperExt;
	@Autowired
	private UlogApiMapper ulogApiMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalBatchDownloadMapper portalBatchDownloadMapper;
	@Autowired 
	private MemberMapper memberMapper;
	@Autowired
	private UlogUtil ulogUtil;
	@Autowired
	private SendLogUtil sendLogUtil;
	
	@GetMapping("/countpercent/{batchId}")
	public ResponseEntity<RestResponseBean> countPercent(@PathVariable("batchId") Integer batchId, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, IllegalAccessException, InvocationTargetException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		Map<String, Object> data = new HashMap<String, Object>();
		if (sr != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為24小時前(一天前未執行下載要求有效)
			cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
			Date startTime = cal.getTime();
			
			PortalBatchDownload portalBatchDownload = portalBatchDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(batchId));
			if(portalBatchDownload!=null) {
				long ctime = portalBatchDownload.getCtime().getTime();
				int waitTime = portalBatchDownload.getWaitTime() * 1000;
				long nowTime = (new Date()).getTime();
				
				int percent = (int) (((float) (nowTime - ctime) / (float) waitTime) * 100);
				if (percent >= 100) {
					data.put("percent", 100);
				} else {
					data.put("percent", percent);
				}
				// ctime後24小時
				long h24Time = ctime + fileStoreTime * 60 * 60 * 1000;
				// 剩餘
				if (nowTime >= h24Time) {
					data.put("timedesc", "此資料已超過保管時間，請重新申請即可使用。");
				} else {
					long diff = h24Time - nowTime;
					long diffh = diff / (60 * 60 * 1000);
					long diffm = (diff - diffh * (60 * 60 * 1000)) / (60 * 1000);
					long diffs = (diff - diffh * (60 * 60 * 1000) - diffm * (60 * 1000)) / 1000;
					data.put("timedesc", "此資料有效期間尚餘" + diffh + "小時" + diffm + "分鐘" + diffs
							+ "秒。<small class=\"link\">若超過保管時間，請重新申請即可使用。</small>");
				}
			}else {
				data.put("percent", -1);
			}
		} else {
			data.put("percent", -1);
			data.put("timedesc", "尚未登入。");
		}
		return responseOK(data);
	}
	
	/**
	 * 申請個人資料下載 /rest/personal/apply/{prId}
	 * 
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/apply/{prIdEncode}")
	public ResponseEntity<RestResponseBean> getPersonalDownloadApply(@PathVariable("prIdEncode") String prIdEncode,
			HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
		request.getSession().setMaxInactiveInterval(120);
		prIdEncode = ValidatorHelper.removeSpecialCharacters(prIdEncode);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		VerificationType verificationType = VerificationType.convertToType(sr.getRoleType(), sr.getMultifactorType());
		Integer batchId = null;
		try {
	    		Member member = null;
	    		Map<String,Object> mParam = new HashMap<>();
	    		mParam.put("account", SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
	    		List<Member> mList = memberMapper.selectByExample(mParam);
	    		if(mList!=null&&mList.size()>0) {
	    			member = mList.get(0);
	    		}
			//ObjectMapper om = new ObjectMapper();
			PortalResource portalResource = null;
			PortalBatchDownload portalBatchDownload = new PortalBatchDownload();
			List<String> downloadSnList = new ArrayList<String>();
			List<Integer> successList = new ArrayList<Integer>();
			List<Integer> failList = new ArrayList<Integer>();
			List<Integer> fail404List = new ArrayList<Integer>();
			Map<Integer,Integer> boxMap = new HashMap<Integer,Integer>();
			int batch_wait_time = 0;
			int httpstatuscode = 0;
			int waitTime = 0;
			// 判斷prId,有沒有含有在session scopeList
			String scopeStr = "";
			List<ResourceApplyThread> finalApplyThreadList = new ArrayList<ResourceApplyThread>();
			if (sr != null && session.getAttribute("scopeList") != null) {
				/**
				 * 原則上授權範圍不會不足
				 */
				boolean checkScope = true;
				List<String> scopeList = (List<String>) session.getAttribute("scopeList");
				String[] prIdList = (new String(Base64.getUrlDecoder().decode(prIdEncode.getBytes("UTF-8")),"UTF-8")).split("[,]"); 
				Integer tmpLevel = 4;
				if(prIdList!=null&&prIdList.length>0) {
					for(String prIdStr:prIdList) {
						Integer prId = Integer.valueOf(prIdStr);
						portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
						if(portalResource.getLevel().compareTo(tmpLevel)<0) {
							tmpLevel = portalResource.getLevel();
						}
					}
				}
				if(!LoginUtil.checkInLevel(session, tmpLevel)) {
					return responseError(SysCode.NoPermission, "apply", "授權等級或權限不足!");
				}
				/**
				 * init 先寫入portalBatchDownload 得到 batchId
				 */
				String randomIdforBatch = SequenceHelper.createUUID();
				Date nowforBatch = new Date();
				String uidmask = member==null?"":ValidatorHelper.removeSpecialCharacters(member.getUid());
				if (uidmask != null && uidmask.length() > 0) {
					try {
						uidmask = maskString(uidmask, 3, 7, 'X');
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String filenameforBatch = downloadPath + File.separator + randomIdforBatch + File.separator + uidmask + sdf3.format(nowforBatch) + randomIdforBatch + ".zip";
				File tempFileforBatch = new File(filenameforBatch);
				if (!tempFileforBatch.getParentFile().exists()) {
					tempFileforBatch.getParentFile().mkdirs();
				}
				portalBatchDownload.setCtime(new Date());
				portalBatchDownload.setFiles(tempFileforBatch.getName());
				portalBatchDownload.setPrIdList(new String(Base64.getUrlDecoder().decode(prIdEncode.getBytes("UTF-8")),"UTF-8"));
				portalBatchDownload.setStat(0);
				portalBatchDownload.setWaitTime(ValidatorHelper.limitNumber(batch_wait_time));
				portalBatchDownloadMapper.insertSelective(portalBatchDownload);
				batchId = portalBatchDownload.getId();
				logger.error("[{}] ==========batchId=========", batchId);
				if(prIdList!=null&&prIdList.length>0) {
					for(String prIdStr:prIdList) {
						ResourceApplyThread thread = new ResourceApplyThread();
						thread.setDownloadPath(downloadPath);
						//thread.setTxId(txId);
						thread.setVerificationType(verificationType);
						//thread.setPsId(psId);
						//thread.setPsdId(psdbean.getId());
						thread.setBatchId(batchId);
						thread.setPrId(Integer.valueOf(prIdStr));
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
						thread.setPortalBatchDownloadMapper(portalBatchDownloadMapper);
						thread.start();
						finalApplyThreadList.add(thread);
					}
				}

				boolean checkApplyThreadStat = true;
				while(checkApplyThreadStat) {
					boolean tmpCheck = true;
					try {
						Thread.sleep(1000l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(ResourceApplyThread p:finalApplyThreadList) {
						//System.out.println(p.getAuthorization()+","+p.getCode());
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
						if(p.getWaitTime()>batch_wait_time) {
							batch_wait_time = p.getWaitTime();
						}
						if(p.getBoxId()!=null) {
							boxMap.put(p.getPrId(), p.getBoxId());
						}
						if(p.getCode()==HttpStatus.OK.value()||p.getCode()==HttpStatus.NO_CONTENT.value()||
								p.getCode() == HttpStatus.TOO_MANY_REQUESTS.value()||p.getCode() == HttpStatus.UPGRADE_REQUIRED.value()) {
							successList.add(p.getPrId());
						}else {
							if(p.getCode() == HttpStatus.BAD_REQUEST.value()) {
								fail404List.add(p.getPrId());
							}
							failList.add(p.getPrId());
						}
					}
				}
				portalBatchDownload.setDownloadSnList(ValidatorHelper.removeSpecialCharacters(downloadSnListStr));
				portalBatchDownload.setWaitTime(ValidatorHelper.limitNumber(batch_wait_time));
				portalBatchDownloadMapper.updateByPrimaryKeySelective(portalBatchDownload);
				logger.info("[{}] ====END batch_wait_time===: {}", ValidatorHelper.limitNumber(batchId), ValidatorHelper.limitNumber(batch_wait_time));
				/**
				 * 起Thread
				 * 1. 監控檔案 (資料集下載)
				 * 2. 封裝檔案
				 * 3. 上傳檔案
				 * 4. 更新portalServiceDownload files url
				 * 
				 * downloadPath = /root/mydata/temp
				 */
				/*String pstempFileName = downloadPath + File.separator + randomIdforBatch;
				File pstempFile = new File(pstempFileName);
				Thread psThread = new WaitingBatchDownloadAndFtp(pstempFile, ftpHost, ftpUsername, ftpPassword,portalBatchDownload,portalResourceDownloadMapper,portalResourceMapper,portalBatchDownloadMapper, ftpSecretkey);
				psThread.start();*/
				/**
				 * 資料集：
				 * 11 申請：您申請此筆資料/服務 
				 * 12 同意：您同意服務條款
				 * 13 驗證：您完成身分驗證
				 * 14 傳輸：您同意 MyData 傳送資料給服務提供者
				 * 15 儲存：您下載資料
				 * 16 條碼取用：機關取用資料
				 * 17 服務應用：MyData 將資料傳送給服務提供者
				 * 
				 * 服務：
				 * 21 申請：您申請此筆資料/服務 
				 * 22 同意：您同意服務條款
				 * 23 驗證：您完成身分驗證
				 * 24 傳輸：您同意 MyData 傳送資料給服務提供者
				 * 25 儲存：您下載資料
				 * 26 條碼取用：機關取用資料
				 * 27 服務應用：MyData 將資料傳送給服務提供者
				 */
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("batchId", portalBatchDownload.getId());
				param.put("idDesc", true);
				List<PortalBox> finalPortalBoxList = portalBoxMapper.selectByExample(param);
				PortalBox finalPortalBox = null;
				if(finalPortalBoxList!=null&&finalPortalBoxList.size()>0) {
					finalPortalBox = finalPortalBoxList.get(0);
				}
				/**
				 * batch 5.紀錄到portal_resource_download
				 */
				PortalResourceDownload prd = new PortalResourceDownload();
				prd.setDownloadSn(UUID.randomUUID().toString());
				prd.setProviderKey(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getAccount());
				prd.setPrId(0);
				prd.setFiles(ValidatorHelper.removeSpecialCharacters(portalBatchDownload.getFiles()));
				prd.setWaitTime(waitTime);
				prd.setCtime(new Date());
				// stat 0:未下載; 1:已下載
				prd.setStat(0);
				prd.setBatchId(portalBatchDownload.getId());
				prd.setEmail(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getEmail());
				prd.setCode("200");
				prd.setIsShow(-1);
				portalResourceDownloadMapper.insertSelective(prd);
				//ulogUtil.recordFullByPr(sr, null, null, portalResource, transactionUid, "320", scopeStr, null, null);
				/**
				 * batch 6. 產生portal_box
				 */
				RandomUtils ru = new RandomUtils();
				String tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
				while (isDuplicateDownloadVerify(tmpDownloadVerify)) {
					tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
				}
				PortalBox box = new PortalBox();
				Calendar cal2 = GregorianCalendar.getInstance();
				cal2.setTime(new Date());
				cal2.add(Calendar.SECOND, waitTime);
				Date boxStartTime = cal2.getTime();
				box.setCtime(boxStartTime);
				box.setDownloadSn(prd.getDownloadSn());
				box.setStat(0);
				box.setVerifyPwd(tmpDownloadVerify);
				box.setDownloadVerify(tmpDownloadVerify);
				box.setBatchId(portalBatchDownload.getId());
				portalBoxMapper.insertSelective(box);

				Map<String,Object> data = new HashMap<String,Object>();
				data.put("portalBatchDownload", portalBatchDownload);
				if(box!=null) {
					data.put("finalPortalBox", box);
				}
				data.put("success", successList);
				data.put("fail", failList);
				data.put("fail404", fail404List);
				data.put("box", boxMap);
				logger.info("[{}] --------------responseOK--------------", batchId);
				return responseOK(data);
			}else {
				return responseError(SysCode.RequestRefused, "apply", "拒絕存取");
			}
		} catch (Exception ex) {
			try {
				PortalResourceDownload prd = new PortalResourceDownload();
				prd.setDownloadSn(UUID.randomUUID().toString());
				prd.setProviderKey(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getAccount());
				prd.setPrId(0);
				prd.setCtime(new Date());
				prd.setEmail(SessionMember.getSessionMemberToMember(sr.getMember()).getEmail());
				prd.setVerificationType(verificationType.name());
				prd.setRequestStatus(RequestType.fail.name());
				prd.setBatchId(batchId);
				prd.setIsShow(-1);
				portalResourceDownloadMapper.insertSelective(prd);
			} catch (Exception e) {
				logger.error("[{}] 申請失敗 responseError--1", batchId);
				logger.error(ex.getLocalizedMessage(), ex);
			}
			logger.error("[{}] 申請失敗 responseError--1", batchId);
			logger.error(ex.getLocalizedMessage(), ex);
//			return responseError(SysCode.SystemError, "apply", "申請失敗");
			return responseError(SysCode.SystemError, "資料暫時無法下載，請稍後再試");
		}
	}
	
	/**
	 * GSP OpenID Connect的登入授權網址。
	 * @param scope 指定授權項目
	 * @param nonce 備註資訊
	 * @param state 狀態資訊
	 * @return
	 */
	public String GetAuthorizationUrl(String clientId, String redirectUri, String responseMode, String responseType,
			List<String> scopeList, String nonce, String state) {
		StringBuilder sb = new StringBuilder();
		sb.append(authorizeUri).append("?client_id=").append(clientId).append("&redirect_uri=").append(redirectUri)
				.append("&response_mode=").append(responseMode).append("&response_type=").append(responseType);
		final StringJoiner sj = new StringJoiner(" ");
		scopeList.forEach(p -> {
			sj.add(p);
		});
		if (StringUtils.isNotEmpty(sj.toString())) {
			sb.append("&scope=").append(sj.toString());
		}
		if (StringUtils.isNotEmpty(nonce)) {
			sb.append("&nonce=").append(nonce);
		}
		if (StringUtils.isNotEmpty(state)) {
			sb.append("&state=").append(state);
		}
		return sb.toString();
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
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("transactionUid")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail() + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr.trim();
	}

	private String replaceParamStrForPostAppendTransactionUid(String paramstr, SessionRecord sessionRecord ,String transactionUid) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
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
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("transactionUid")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail() + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}	
	
	private String replaceParamStrForPostNotAppendEscap(String paramstr, SessionRecord sr) {
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
					forReplaceStr = TokenUtils.getFullAccessToken(sr.getAuthToken());
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}

	/**
	 * action_type=1時
	 * @param paramstr
	 * @param sessionRecord
	 * @param transactionUid
	 * @param paramMap
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
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
					forReplaceStr = SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getName();
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
	
	private String replaceParamStrForPostAndParam(String paramstr, SessionRecord sessionRecord, Map<String, Object> paramMap) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		System.out.println("in replace=\n" + paramstr);
		String patternStr = "#\\{[\\w:/\\\\.-]*\\}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		while (matcher.find()) {
			matchStrList.add(matcher.group());
			System.out.println(matcher.group());
		}
		System.out.println("------------paramMap s---------------");
		for (Map.Entry entry : paramMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
		System.out.println("------------paramMap e---------------");
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
						forReplaceStr = "\"" + Base64.getUrlEncoder().encodeToString(sessionRecord.getMember().getUid().getBytes("UTF-8")) + "\"";
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
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("lprNumber")) {
					System.out.println(
							"------------paramstr s in lprNumber ---------------:" + paramMap.get("lprNumber"));
					forReplaceStr = "\"" + paramMap.get("lprNumber") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("locationHsnCd")) {
					forReplaceStr = "\"" + paramMap.get("locationHsnCd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_hsn_cd")) {
					forReplaceStr = "\"" + paramMap.get("etd_location_hsn_cd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_town_cd")) {
					forReplaceStr = "\"" + paramMap.get("etd_location_town_cd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_vill_cd")) {
					forReplaceStr = "\"" + paramMap.get("etd_location_vill_cd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_tax_no")) {
					forReplaceStr = "\"" + paramMap.get("etd_tax_no") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_apply_items")) {
					forReplaceStr = "\"" + paramMap.get("etd_apply_items") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("INQ_ID")) {
					forReplaceStr = "\"" + paramMap.get("INQ_ID") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("PR_VAL_TP")) {
					forReplaceStr = "\"" + paramMap.get("PR_VAL_TP") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("carNo")) {
					forReplaceStr = "\"" + paramMap.get("carNo").toString().toUpperCase() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("qplateno")) {
					forReplaceStr = "\"" + paramMap.get("qplateno").toString().toUpperCase() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_car_no")) {
					forReplaceStr = "\"" + paramMap.get("etd_car_no").toString().toUpperCase() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("caseYears")) {
					forReplaceStr = "\"" + paramMap.get("caseYears") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_case_years")) {
					forReplaceStr = "\"" + paramMap.get("etd_case_years") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("INQ_YR")) {
					forReplaceStr = "\"" + paramMap.get("INQ_YR") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("ExamYear")) {
					forReplaceStr = "\"" + paramMap.get("ExamYear") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("qcartype")) {
					forReplaceStr = "\"" + paramMap.get("qcartype") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("WaterNo")) {
					forReplaceStr = "\"" + paramMap.get("WaterNo") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("ApplicantName")) {
					forReplaceStr = "\"" + paramMap.get("ApplicantName") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("FilingCaseNo")) {
					forReplaceStr = "\"" + paramMap.get("FilingCaseNo") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("EquipRegNo")) {
					forReplaceStr = "\"" + paramMap.get("EquipRegNo") + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}

	public static String replaceParamStrForPostAndParam1Param2Param3(String paramstr, SessionRecord sessionRecord, Map<String, String> paramMap) {
		System.out.println("in replace=\n" + paramstr);
		String patternStr = "#\\{[\\w:/\\\\.-]*\\}";
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
					forReplaceStr = TokenUtils.getFullAccessToken(sessionRecord.getAuthToken());
				}
				if (matchInnerStr.equalsIgnoreCase("locationHsnCd")) {
					forReplaceStr = "\"" + paramMap.get("locationHsnCd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_hsn_cd")) {
					forReplaceStr = "\"" + paramMap.get("etd_location_hsn_cd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_town_cd")) {
					forReplaceStr = "\"" + paramMap.get("etd_location_town_cd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_location_vill_cd")) {
					forReplaceStr = "\"" + paramMap.get("etd_location_vill_cd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_tax_no")) {
					forReplaceStr = "\"" + paramMap.get("etd_tax_no") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_apply_items")) {
					forReplaceStr = "\"" + paramMap.get("etd_apply_items") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("INQ_ID")) {
					forReplaceStr = "\"" + paramMap.get("INQ_ID") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("PR_VAL_TP")) {
					forReplaceStr = "\"" + paramMap.get("PR_VAL_TP") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("carNo")) {
					forReplaceStr = "\"" + paramMap.get("carNo").toUpperCase() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("qplateno")) {
					forReplaceStr = "\"" + paramMap.get("qplateno").toUpperCase() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_car_no")) {
					forReplaceStr = "\"" + paramMap.get("etd_car_no").toUpperCase() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("caseYears")) {
					forReplaceStr = "\"" + paramMap.get("caseYears") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("etd_case_years")) {
					forReplaceStr = "\"" + paramMap.get("etd_case_years") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("INQ_YR")) {
					forReplaceStr = "\"" + paramMap.get("INQ_YR") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("ExamYear")) {
					forReplaceStr = "\"" + paramMap.get("ExamYear") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("qcartype")) {
					forReplaceStr = "\"" + paramMap.get("qcartype") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("WaterNo")) {
					forReplaceStr = "\"" + paramMap.get("WaterNo") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("ApplicantName")) {
					forReplaceStr = "\"" + paramMap.get("ApplicantName") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("FilingCaseNo")) {
					forReplaceStr = "\"" + paramMap.get("FilingCaseNo") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("EquipRegNo")) {
					forReplaceStr = "\"" + paramMap.get("EquipRegNo") + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}

	private String replaceParamStrForGet(String paramstr, SessionRecord sessionRecord) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
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
					forReplaceStr = TokenUtils.getFullAccessToken(sessionRecord.getAuthToken());
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
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getName();
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getEmail();
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}

	private static void addWatermark(PdfStamper pdfStamper, String waterMarkName, String passwd) {
		PdfContentByte content = null;
		BaseFont base = null;
		Rectangle pageRect = null;
		PdfGState gs = new PdfGState();
		try {
			// 設置字體
			base = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (base == null || pdfStamper == null) {
				return;
			}
			// passwd
			pdfStamper.setEncryption(passwd.getBytes(), passwd.getBytes(), PdfWriter.ALLOW_PRINTING,
					PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);

			// 設置透明度為0.4
			gs.setFillOpacity(0.4f);
			gs.setStrokeOpacity(0.4f);
			int toPage = pdfStamper.getReader().getNumberOfPages();
			for (int i = 1; i <= toPage; i++) {
				pageRect = pdfStamper.getReader().getPageSizeWithRotation(i);
				// 计算水印X,Y座標
				float x = pageRect.getWidth() / 2;
				float y = pageRect.getHeight() / 2;
				// 获得PDF最顶层
				content = pdfStamper.getOverContent(i);
				content.saveState();
				// set Transparency
				content.setGState(gs);
				content.beginText();
				content.setColorFill(BaseColor.GRAY);
				content.setFontAndSize(base, 60);
				// 水印文字成45度角倾斜
				content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, x, y, 45);
				content.endText();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			content = null;
			base = null;
			pageRect = null;
		}
	}

	private String maskString(String strText, int start, int end, char maskChar) throws Exception {
		if (strText == null || strText.equals(""))
			return "";

		if (start < 0)
			start = 0;

		if (end > strText.length())
			end = strText.length();

		if (start > end)
			throw new Exception("End index cannot be greater than start index");

		int maskLength = end - start;

		if (maskLength == 0)
			return strText;

		StringBuilder sbMaskString = new StringBuilder(maskLength);

		for (int i = 0; i < maskLength; i++) {
			sbMaskString.append(maskChar);
		}

		return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskLength);
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
