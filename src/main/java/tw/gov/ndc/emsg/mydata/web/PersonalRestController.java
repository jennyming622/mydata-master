package tw.gov.ndc.emsg.mydata.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
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
import java.util.zip.ZipEntry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.riease.common.enums.ActionEvent;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
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
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.HttpHelper;
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
import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.mapper.*;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;
import tw.gov.ndc.emsg.mydata.type.DownloadType;
import tw.gov.ndc.emsg.mydata.type.RequestType;
import tw.gov.ndc.emsg.mydata.type.SendType;
import tw.gov.ndc.emsg.mydata.type.VerificationType;
import tw.gov.ndc.emsg.mydata.util.CBCUtil;
import tw.gov.ndc.emsg.mydata.util.LoginUtil;
import tw.gov.ndc.emsg.mydata.util.MailUtil;
import tw.gov.ndc.emsg.mydata.util.SMSUtil;
import tw.gov.ndc.emsg.mydata.util.SendLogUtil;
import tw.gov.ndc.emsg.mydata.util.TokenUtils;
import tw.gov.ndc.emsg.mydata.util.UlogUtil;

@Controller
@RequestMapping("/rest/personal")
public class PersonalRestController extends BaseRestController {
	private static Logger logger = LoggerFactory.getLogger(PersonalRestController.class);
	private static DecimalFormat formatter = new DecimalFormat("#.#");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("年MM月dd日HH時mm分ss秒");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
	private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy/M/d HH:mm");
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

	@Value("${mail.enable}")
	private String mailEnable;

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
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	@Autowired
	private ContactusMapper contactusMapper;
	@Autowired
	private PortalBoxMapper portalBoxMapper;
	@Autowired
	private UlogApiMapperExt ulogApiMapperExt;
	@Autowired
	private UlogApiMapper ulogApiMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalBoxLockCheckMapper portalBoxLockCheckMapper;
	@Autowired
	private PortalServiceDownloadSubMapper portalServiceDownloadSubMapper;
	@Autowired
	private PortalCounterSubMapperExt portalCounterSubMapperExt;
	@Autowired
	private UlogUtil ulogUtil;
	@Autowired
	private SendLogUtil sendLogUtil;
	@Autowired
	private MemberMapper memberMapper;

	private CBCUtil CBCUtil;
	
	@PostMapping("/contactus")
	@ResponseBody
	public ResponseEntity<RestResponseBean> postContactUs(HttpServletRequest request,
														  @RequestBody Map<String, Object> params)
			throws NoSuchAlgorithmException, KeyManagementException, IOException {
		HttpSession session = request.getSession();
		//String gRecaptchaResponse = "";
		String name = "";
		String email = "";
		String title = "";
		String content = "";

		String captcha = MapUtils.getString(params, "captcha");
		String prId = MapUtils.getString(params, "prId", "");
		Boolean isValid = captchaCheck(request, captcha, prId);
		if(BooleanUtils.isFalse(isValid)) {
			return responseError(SysCode.InvalidCaptcha, "captcha", "圖型驗證碼不正確");
		}

		Map<String, String> data = new HashMap<String, String>();
		if (params.get("captcha") != null) {
			captcha = params.get("captcha").toString();
		}
		//if (params.get("g-recaptcha-response") != null) {
		//	gRecaptchaResponse = params.get("g-recaptcha-response").toString();
		//}
		if (params.get("name") != null) {
			name = params.get("name").toString();
			name = removeHtmlTag(name);
		}
		if (params.get("email") != null) {
			email = params.get("email").toString();
			email = removeHtmlTag(email);
		}
		if (params.get("title") != null) {
			title = params.get("title").toString();
			title = removeHtmlTag(title);
		}
		if (params.get("content") != null) {
			content = params.get("content").toString();
			content = removeHtmlTag(content);
		}

		if (!name.isEmpty() && !email.isEmpty() && !title.isEmpty() && !content.isEmpty()) {
			Contactus record = new Contactus();
			record.setName(name);
			record.setEmail(email);
			record.setTitle(title);
			record.setContent(content);
			record.setCtime(new Date());
			record.setStat(0);
			contactusMapper.insertSelective(record);

			List<String> reveicers = new ArrayList<String>();
			reveicers.add("mydata@ndc.gov.tw");
			String from = email;
			title = "我想要更多-"+title;
			MailUtil.sendMail(reveicers, from,title,content,mailEnable);
			
			return responseOK(data);
		} else {
			return responseError(SysCode.MissingRequiredParameter, "name", "缺少必要參數!");
		}
		/*} else {
			return responseError(SysCode.InvalidVerifyCode, "captcha", "驗證失敗!");
		}*/
	}

	/**
	 * 產生新的portalBox
	 *
	 * @param request
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@PostMapping("/verification")
	@ResponseBody
	public ResponseEntity<RestResponseBean> postVerification(HttpServletRequest request,
		@RequestBody Map<String, Object> params) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		String accessToken = TokenUtils.getFullAccessToken(sr.getAuthToken());
		System.out.println("accessToken......:" + accessToken);
		String downloadVerify = "";
		Map<String, String> data = new HashMap<String, String>();
		if (params.get("downloadVerify") != null) {
			downloadVerify = params.get("downloadVerify").toString();
		}
		Boolean retCheck = false;
		if (sr != null) {
			System.out.println("account=" +SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為24小時前(一天前未執行下載要求有效)
			cal.add(Calendar.MINUTE, -20);
			Date startTime = cal.getTime();
			/**
			 * 根據驗證碼確定，要異動的portal_box
			 */
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("downloadVerify", downloadVerify);
			param.put("ctimeDesc", true);
			List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param);
			if (portalBoxList != null && portalBoxList.size() > 0) {
				PortalBox box = portalBoxList.get(0);
				/**
				 * portalResourceDownloadMapper
				 */
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("providerKey", ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount()));
				param1.put("downloadSn", ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
				param1.put("sCtime", startTime);
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
				PortalResourceDownload portalResourceDownload = null;
				if (downloadList != null && downloadList.size() > 0) {
					portalResourceDownload = downloadList.get(0);
				}
				RandomUtils ru = new RandomUtils();
				//延後時間
				if(box.getPsdId()!=null) {
					List<PortalBoxExt> tmpPortalBoxExtList = portalResourceExtMapper.selectMyBoxForCounterByPsdId(ValidatorHelper.limitNumber(box.getPsdId()));
					if(tmpPortalBoxExtList!=null&&tmpPortalBoxExtList.size()>0) {
						for(PortalBox tbox:tmpPortalBoxExtList) {
							PortalBox tbox1 = new PortalBox();
							String tmpDownloadVerify1 = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
							while (isDuplicateDownloadVerify(tmpDownloadVerify1)) {
								tmpDownloadVerify1 = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
							}
							tbox1.setId(ValidatorHelper.limitNumber(tbox.getId()));
							tbox1.setStat(0);
							tbox1.setCtime(new Date());
							tbox1.setDownloadVerify(tmpDownloadVerify1);
							//必須判斷欄位
							tbox1.setAgentUid(ValidatorHelper.removeSpecialCharacters(tbox.getAgentUid()));
							tbox1.setAgentBirthdate(ValidatorHelper.limitDate(tbox.getAgentBirthdate()));
							tbox1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(tbox.getAgentVerify()));
							tbox1.setAgreeAgent(ValidatorHelper.limitNumber(tbox.getAgreeAgent()));
							portalBoxMapper.updateByPrimaryKeySelective(tbox1);
						}
					}
				}
				
				/**
				 * box child 先改，再改parent
				 */
				String tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
				while (isDuplicateDownloadVerify(tmpDownloadVerify)) {
					tmpDownloadVerify = ru.alphaNumericString(8).toLowerCase(Locale.ENGLISH);
				}
				box.setStat(0);
				box.setDownloadVerify(tmpDownloadVerify);
				box.setCtime(new Date());
				//portalBoxMapper.updateByPrimaryKeySelective(box);
				/**
				 * 更新裝態及DownloadVerify, 需判斷 is null
				 */
				PortalBox box1 = new PortalBox();
				box1.setId(ValidatorHelper.limitNumber(box.getId()));
				box1.setStat(0);
				box1.setDownloadVerify(tmpDownloadVerify);
				box1.setCtime(new Date());
				//必須判斷欄位
				box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(box.getAgentUid()));
				box1.setAgentBirthdate(ValidatorHelper.limitDate(box.getAgentBirthdate()));
				box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(box.getAgentVerify()));
				box1.setAgreeAgent(ValidatorHelper.limitNumber(box.getAgreeAgent()));
				portalBoxMapper.updateByPrimaryKeySelective(box1);
				
				PortalResourceExt bean = new PortalResourceExt();
				try {
					BeanUtils.copyProperties(bean, box);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				Calendar cal1 = GregorianCalendar.getInstance();
				cal1.setTime(box.getCtime());
				cal1.add(Calendar.MINUTE, 20);
				Date endDate = cal1.getTime();
				if (portalResourceDownload != null && portalResourceDownload.getCtime() != null) {
					Calendar cal2 = GregorianCalendar.getInstance();
					cal2.setTime(portalResourceDownload.getCtime());
					cal2.add(Calendar.HOUR_OF_DAY, fileStoreTime);
					Date downloadFinalDate = cal2.getTime();
					if (endDate.after(downloadFinalDate)) {
						endDate = downloadFinalDate;
					}
				}
				String yearStr = sdf4.format(endDate);
				int year = Integer.valueOf(yearStr) - 1911;
				String monthDayHousrMinSec = sdf5.format(endDate);
				// endTimeNote
				bean.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
				return responseOK(bean);
			} else {
				return responseError(SysCode.DataNotExist, "downloadVerify", "驗證資料不存在!");
			}
		} else {
			return responseError(SysCode.NotAuthenticated, "downloadVerify", "尚未完成身分驗證!");
		}
	}

	@PostMapping("/verification/download")
	@ResponseBody
	public void postVerificationDownload(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> params)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, BadPaddingException, IllegalBlockSizeException, ZipException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		String accessToken = TokenUtils.getFullAccessToken(sr.getAuthToken());
		System.out.println("accessToken......:" + accessToken);
		String verifypwdbox = null;
		String downloadVerify = null;
		if (params.get("verifyPwd") != null) {
			verifypwdbox = ValidatorHelper.removeSpecialCharacters(params.get("verifyPwd").toString());
		}
		if (params.get("downloadVerify") != null) {
			downloadVerify = ValidatorHelper.removeSpecialCharacters(params.get("downloadVerify").toString());
		}
		if (sr != null && verifypwdbox!=null && downloadVerify!=null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為24小時前(一天前未執行下載要求有效)
			cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
			Date startTime = cal.getTime();

			Map<String,Object> param = new HashMap<String,Object>();
			param.put("verifyPwd", verifypwdbox);
			param.put("downloadVerify", downloadVerify);
			param.put("sCtime", startTime);
			param.put("stat", 0);
			param.put("ctimeDesc", true);
			List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param);
			if (portalBoxList != null && portalBoxList.size() > 0) {
				PortalBox portalBox = portalBoxList.get(0);
				for (PortalBox pbox : portalBoxList) {
					PortalBox pbox1 = new PortalBox();
					pbox1.setId(ValidatorHelper.limitNumber(pbox.getId()));
					pbox1.setStat(1);
					//必須判斷欄位
					pbox1.setAgentUid(ValidatorHelper.removeSpecialCharacters(pbox.getAgentUid()));
					pbox1.setAgentBirthdate(ValidatorHelper.limitDate(pbox.getAgentBirthdate()));
					pbox1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(pbox.getAgentVerify()));
					pbox1.setAgreeAgent(ValidatorHelper.limitNumber(pbox.getAgreeAgent()));
					portalBoxMapper.updateByPrimaryKeySelective(pbox1);
				}
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("providerKey", ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount()));
				param1.put("downloadSn", ValidatorHelper.removeSpecialCharacters(portalBox.getDownloadSn()));
				param1.put("sCtime", startTime);
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
				System.out.println("downloadList size=" + downloadList.size());
				if (downloadList != null && downloadList.size() > 0) {
					PortalResourceDownload download = downloadList.get(0);
					FTPClient client = new FTPClient();
					FileOutputStream fos = null;
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.setBufferSize(1024 * 1024 * 10);
					client.changeWorkingDirectory("/mydata");
					String randomId = SequenceHelper.createUUID();
					String localFileName = ftpPath + File.separator + randomId + File.separator + ValidatorHelper.removeSpecialCharacters(download.getFiles());
					if(!(new File(localFileName).getParentFile()).exists()) {
						(new File(localFileName).getParentFile()).mkdirs();
					}
					fos = new FileOutputStream(localFileName);
					client.retrieveFile(ValidatorHelper.removeSpecialCharacters(download.getFiles()), fos);
					if(fos!=null) {
						fos.close();
					}
					client.logout();
					client.disconnect();
					File localFile = new File(localFileName);
					if (localFile.exists()) {
						System.out.println("File exist=" + localFile.getAbsolutePath());
					} else {
						System.out.println("File not exist=" + localFile.getAbsolutePath());
					}
					/**
					 * AES解密
					 */
					File tmpFile = new File(localFileName);
					File filename1enc = new File(localFile.getParentFile().getAbsoluteFile() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"dec."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
					byte[] b = Files.readAllBytes(Paths.get(localFileName));
					try {
						byte[] decryptb = decrypt(b,ftpSecretkey);
						FileUtils.writeByteArrayToFile(filename1enc, decryptb);
					} catch (Exception e) {
						e.printStackTrace();
					}
					/**
					 * 下載成功
					 */
					download.setStat(1);
					param1.put("stat", 1);
					portalResourceDownloadMapper.updateStatByExample(param1);

					response.setHeader("Content-Type", "application/force-download");
					response.setHeader("Content-Disposition", "attachment; filename=" + ValidatorHelper.removeSpecialCharacters(localFile.getName()));
					InputStream file_input = null;
					try {
						file_input = new FileInputStream(filename1enc);
						org.apache.commons.io.IOUtils.copy(file_input, response.getOutputStream());
						response.getOutputStream().flush();
						response.getOutputStream().close();
						localFile.delete();
						filename1enc.delete();
						response.flushBuffer();						
					}finally {
						if(file_input!=null) {
							HttpClientHelper.safeClose(file_input);
						}
					}
				}
			}
		}
	}

	@GetMapping("/detail/download/{sn}")
	public void detailDownload(@PathVariable("sn") String sn, HttpServletRequest request, HttpServletResponse response)
			throws SocketException, IOException, ZipException {
		
		/*PortalResourceDownloadExample ex = new PortalResourceDownloadExample();
		ex.or().andDownloadSnEqualTo(sn);*/
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("downloadSn",ValidatorHelper.removeSpecialCharacters(sn));
		List<PortalResourceDownload> prdList = portalResourceDownloadMapper.selectByExample(param);
		if (prdList != null && prdList.size() > 0) {
			PortalResourceDownload prd = prdList.get(0);
			PortalResource pr = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));

			// connect ftp
			FTPClient client = new FTPClient();
			FileOutputStream fos = null;
			client.connect(ftpHost, 21);
			client.login(ftpUsername, ftpPassword);
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.setBufferSize(1024 * 1024 * 10);
			client.changeWorkingDirectory("/" + pr.getFtpAccount());

			ArrayList<File> localFiles = new ArrayList<File>();
			List<String> files = Arrays.asList(prd.getFiles().split(","));
			for (String file : files) {
				if (!new File(ftpPath).exists()) {
					new File(ftpPath).mkdir();
				}
				String localFileName = ftpPath +File.separator+ ValidatorHelper.removeSpecialCharacters(file);
				File localFile = new File(localFileName);
				fos = new FileOutputStream(localFileName);
				client.retrieveFile(ValidatorHelper.removeSpecialCharacters(file), fos);
				if(fos!=null) {
					fos.close();
				}
				/**
				 * AES解密
				 */
				File filename1enc = new File(localFile.getParentFile().getAbsoluteFile() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"dec."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
				localFiles.add(filename1enc);
				byte[] b = Files.readAllBytes(Paths.get(localFileName));
				try {
					byte[] decryptb = decrypt(b,ftpSecretkey);
					FileUtils.writeByteArrayToFile(filename1enc, decryptb);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			client.logout();
			client.disconnect();

			File tobeDownload = null;
			if (localFiles.size() > 1) {
				tobeDownload = packZip(new File(ftpPath +File.separator+ sn + ".zip"), localFiles);
				for (File f : localFiles) {
					f.delete();
				}
			} else {
				tobeDownload = localFiles.get(0);
			}

			response.setHeader("Content-Type", "application/force-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + ValidatorHelper.removeSpecialCharacters(tobeDownload.getName()));
			ServletOutputStream outputStream = null;
			InputStream file_input = null;
			try {
				outputStream = response.getOutputStream();
				file_input = new FileInputStream(tobeDownload);
				org.apache.commons.io.IOUtils.copy(file_input, outputStream);
				outputStream.flush();
				outputStream.close();	
        	}finally {
				if(file_input!=null) {
					HttpClientHelper.safeClose(file_input);
				}
				if(outputStream!=null) {
					HttpClientHelper.safeClose(outputStream);
				}
			}
			
			tobeDownload.delete();
			// response.sendRedirect(request.getContextPath()+"/personal/detail");
			response.flushBuffer();

			prd.setStat(1);
			param.put("stat", 1);
			portalResourceDownloadMapper.updateStatByExample(param);
		}
	}


	@GetMapping("/box/status/{id}")
	public ResponseEntity<RestResponseBean> checkBoxstatus(@PathVariable("id") Integer boxId, HttpServletRequest request, HttpServletResponse response)
			throws SocketException, IOException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		PortalBoxExt portalBoxExt = new PortalBoxExt();
		if (sr != null) {
			PortalBoxExt tmpbox = portalResourceExtMapper.selectMyBoxById(boxId);
			if(tmpbox!=null&&tmpbox.getProviderKey().equalsIgnoreCase(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount())) {
				portalBoxExt = tmpbox;

				PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(tmpbox.getDownloadSn()));
				if(download != null) {
					portalBoxExt.setCode(download.getCode());
				}
			}
		}
		return responseOK(portalBoxExt);
	}
	
	/*private File packZip(File output, List<File> sources) throws IOException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
		zos.setLevel(Deflater.DEFAULT_COMPRESSION);
		for (File source : sources) {
			if (!source.canRead()) {
				throw new IOException("Cannot read file " + source.getCanonicalPath() + ", check read permission");
			}
			String fileName = source.getName();
			zos.putNextEntry(new ZipEntry(fileName));
			FileInputStream fis = new FileInputStream(source);
			byte[] buffer = new byte[4092];
			int byteCount = 0;
			while ((byteCount = fis.read(buffer)) != -1) {
				zos.write(buffer, 0, byteCount);
			}
			fis.close();
			zos.closeEntry();
		}
		zos.flush();
		zos.close();
		return output;
	}*/


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

	@GetMapping("/countpercent/{prId}")
	public ResponseEntity<RestResponseBean> countPercent(@PathVariable("prId") Integer prId, HttpServletRequest request,
														 HttpServletResponse response)
			throws SocketException, IOException, IllegalAccessException, InvocationTargetException, BadPaddingException, IllegalBlockSizeException {
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
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			param.put("prId", prId);
			param.put("sCtime", startTime);
			param.put("stat", 0);
			param.put("psdIdIsNull", true);
			List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
			if (downloadList != null && downloadList.size() > 0) {
				PortalResourceDownload download = downloadList.get(0);
				/**
				 * portalBoxMapper
				 */
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("downloadSn", ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
				param1.put("sCtime", startTime);
				param1.put("ctimeDesc", true);
				List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param1);
				if (portalBoxList != null && portalBoxList.size() > 0) {
					data.put("boxid", portalBoxList.get(0).getId());
				}
				long ctime = download.getCtime().getTime();
				long nowTime = (new Date()).getTime();
				if(download.getWaitTime()!=null&&download.getWaitTime().compareTo(Integer.valueOf(0))>0) {
					PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
					if(download.getCode()!=null) {
						if(download.getCode().contentEquals("429")||download.getCode().contentEquals("426")) {
							PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
							data.put("ppName", pp.getName());
							int waitTime = (download.getWaitTime()) * 1000;
							/**
							 * 使用portalResource.getBatchTime()時間
							 */
							if(portalResource.getBatchTime()!=null) {
								if((portalResource.getBatchTime()*60*1000)>waitTime) {
									waitTime = portalResource.getBatchTime()*60*1000;
								}
							}
							int percent = (int) (((float) (nowTime - ctime) / (float) waitTime) * 100);
							if (percent >= 100) {
//								if(download.getServerDownloadTime() != null) {
//									data.put("percent", 100);
//								} else {
									data.put("percent", 99);
								//}
							} else {
								data.put("percent", percent);
							}
						}else {
							data.put("percent", 100);
						}
					}else {
						int waitTime = (download.getWaitTime()) * 1000;
						/**
						 * 使用portalResource.getBatchTime()時間
						 */
						if(portalResource.getBatchTime()!=null) {
							if((portalResource.getBatchTime()*60*1000)>waitTime) {
								waitTime = portalResource.getBatchTime()*60*1000;
							}
						}
						int percent = (int) (((float) (nowTime - ctime) / (float) waitTime) * 100);
						if (percent >= 100) {
							data.put("percent", 100);
						} else {
							data.put("percent", percent);
						}
					}
				}else {
					data.put("percent", 100);
				}

				// ctime後8小時
				long h24Time = ctime + fileStoreTime * 60 * 60 * 1000;
				// 剩餘
				if (nowTime >= h24Time) {
					data.put("timedesc", "此資料已超過保管時間，請重新申請即可使用。");
				} else {
					long diff = h24Time - nowTime;
					long diffh = diff / (60 * 60 * 1000);
					long diffm = (diff - diffh * (60 * 60 * 1000)) / (60 * 1000);
					long diffs = (diff - diffh * (60 * 60 * 1000) - diffm * (60 * 1000)) / 1000;
					data.put("timedesc", "此資料有效期間尚餘" + diffh + "小時" + diffm + "分鐘" + diffs + "秒。<small class=\"link\">若超過保管時間，請重新申請即可使用。</small>");
				}
				data.put("httpcode", download.getCode());
			}else {
				data.put("percent", -1);
			}
		} else {
			data.put("percent", -1);
			// data.put("box", null);
			data.put("timedesc", "尚未登入。");
		}
		return responseOK(data);
	}

	@GetMapping("/service/counter/countpercent/{boxId}")
	public ResponseEntity<RestResponseBean> serviceCounterBoxPercent(@PathVariable("boxId") Integer boxId, HttpServletRequest request,HttpServletResponse response)
			throws SocketException, IOException, IllegalAccessException, InvocationTargetException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		Map<String, Object> data = new HashMap<String, Object>();
		if (sr != null) {
			PortalBox box = portalBoxMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(boxId));
			if(box!=null) {
				PortalServiceDownload psdBean = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
				if(psdBean.getFiles()!=null) {
					data.put("percent", 100);
				}else {
					data.put("percent", 0);
				}
			}else {
				data.put("percent", -1);
			}
		}else {
			data.put("percent", -1);
			data.put("timedesc", "尚未登入。");
		}
		return responseOK(data);
	}
	@GetMapping("/countpercent/batch/{batchId}/{prId}")
	public ResponseEntity<RestResponseBean> batchCountPercent(@PathVariable("batchId") Integer batchId,@PathVariable("prId") Integer prId, HttpServletRequest request,
															  HttpServletResponse response)
			throws SocketException, IOException, IllegalAccessException, InvocationTargetException, BadPaddingException, IllegalBlockSizeException {
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
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			param.put("prId", prId);
			param.put("sCtime", startTime);
			param.put("stat", 0);
			param.put("batchId", batchId);
			List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
			if (downloadList != null && downloadList.size() > 0) {
				PortalResourceDownload download = downloadList.get(0);
				/**
				 * portalBoxMapper
				 */
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("downloadSn", ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
				param1.put("sCtime", startTime);
				param1.put("ctimeDesc", true);
				List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param1);
				if (portalBoxList != null && portalBoxList.size() > 0) {
					data.put("boxid", portalBoxList.get(0).getId());
					//System.out.println("boxid="+portalBoxList.get(0).getId());
				}
				long ctime = download.getCtime().getTime();
				long nowTime = (new Date()).getTime();
				if(download.getWaitTime()!=null&&download.getWaitTime().compareTo(Integer.valueOf(0))>0) {
					PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
					if(download.getCode()!=null) {
						if(download.getCode().contentEquals("429")||download.getCode().contentEquals("426")) {
							PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
							data.put("ppName", pp.getName());
							int waitTime = (download.getWaitTime()) * 1000;
							/**
							 * 使用portalResource.getBatchTime()時間
							 */
							if(portalResource.getBatchTime()!=null) {
								if((portalResource.getBatchTime()*60*1000)>waitTime) {
									waitTime = portalResource.getBatchTime()*60*1000;
								}
							}
							int percent = (int) (((float) (nowTime - ctime) / (float) waitTime) * 100);
							if (percent >= 100) {
								data.put("percent", 100);
							} else {
								data.put("percent", percent);
							}
						}else {
							data.put("percent", 100);
						}
					}else {
						int waitTime = (download.getWaitTime()) * 1000;
						/**
						 * 使用portalResource.getBatchTime()時間
						 */
						if(portalResource.getBatchTime()!=null) {
							if((portalResource.getBatchTime()*60*1000)>waitTime) {
								waitTime = portalResource.getBatchTime()*60*1000;
							}
						}
						int percent = (int) (((float) (nowTime - ctime) / (float) waitTime) * 100);
						if (percent >= 100) {
							data.put("percent", 100);
						} else {
							data.put("percent", percent);
						}
					}
				}else {
					data.put("percent", 100);
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
				data.put("httpcode", download.getCode());
			}else {
				data.put("percent", -1);
			}
		} else {
			data.put("percent", -1);
			// data.put("box", null);
			data.put("timedesc", "尚未登入。");
		}
		return responseOK(data);
	}


	/**
	 * OTP檢查
	 * @param request
	 * @param params
	 * @return
	 */
	@PostMapping("/userInfo/phoneNumber/check")
	@ResponseBody
	public ResponseEntity<RestResponseBean> postOtpCodeCheck(HttpServletRequest request,
															 @RequestBody Map<String, Object> params) {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if (params.get("otpcode") != null) {
			if (sr != null) {
				if (session.getAttribute("otpcode") != null
						&& session.getAttribute("otpcode").toString().trim().length() > 0) {
					if (session.getAttribute("otpcode").toString().trim()
							.equalsIgnoreCase(params.get("otpcode").toString().trim())) {
						// 清除舊otpcode
						session.removeAttribute("otpcode");
						return responseOK();
					} else {
						return responseError(SysCode.DataNotExist, "otpcode", "資料不存在!");
					}
				} else {
					return responseError(SysCode.DataNotExist, "otpcode", "資料不存在!");
				}
			} else {
				return responseError(SysCode.AuthenticateFail, "otpcode", "尚未完成身分驗證!");
			}
		} else {
			return responseError(SysCode.MissingRequiredParameter, "otpcode", "尚未完成身分驗證!");
		}
	}

	@GetMapping("/apply/check/{prId}")
	public ResponseEntity<RestResponseBean> getPersonalDownloadApply(@PathVariable("prId") Integer prId,
																	 HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為24小時前(一天前未執行下載要求有效)
			cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
			Date startTime = cal.getTime();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			param.put("prId", prId);
			param.put("sCtime", startTime);
			param.put("psdIdIsNull", true);
			long downloadCount = portalResourceDownloadMapper.countByExample(param);
			if(downloadCount>0) {
				return responseError(SysCode.DataExist, "apply", "申請失敗");
			}else {
				return responseOK();
			}
		} catch (Exception ex) {
			System.out.println(ex);
			System.out.println("--------------/apply/check responseError--------------:"+prId);
			return responseError(SysCode.SystemError, "apply", "申請失敗");
		}
	}
	/**
	 * 申請個人資料下載 /rest/personal/apply/{prId}
	 *
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/apply/{prId}")
	public ResponseEntity<RestResponseBean> getPersonalDownloadApply(@PathVariable("prId") Integer prId,
		HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
		request.getSession().setMaxInactiveInterval(120);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		VerificationType verificationType = VerificationType.convertToType(sr.getRoleType(), sr.getMultifactorType());
		String transactionUid = UUID.randomUUID().toString();
		PortalResource portalResource = null;
		try {
			portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
			ObjectMapper om = new ObjectMapper();
			int httpstatuscode = 0;
			// 判斷prId,有沒有含有在session scopeList
			String scopeStr = "";
			if (sr != null && session.getAttribute("scopeList") != null) {
				/**
				 * 原則上授權範圍不會不足
				 */
				boolean checkScope = true;
				List<String> scopeList = (List<String>) session.getAttribute("scopeList");
				List<String> tmpScopeList = new ArrayList<>();
				for (String scope : scopeList) {
					tmpScopeList.add(scope);
				}
				if (portalResource != null) {
					Map<String,Object> sparam = new HashMap<String,Object>();
					sparam.put("prId", prId);
					List<PortalResourceScope> prsList = portalResourceScopeMapper.selectByExample(sparam);
					if (prsList != null && prsList.size() > 0) {
						for (PortalResourceScope prs : prsList) {
							String tscope =  ValidatorHelper.removeSpecialCharacters(prs.getScope());
							if (!tmpScopeList.contains(tscope)) {
								tmpScopeList.add(tscope);
								checkScope = false;
							}
						}
					}
				}
				if(!LoginUtil.checkInLevel(session, portalResource.getLevel())) {
					return responseError(SysCode.NoPermission, "apply", "授權權限不足!");
				}
				if (!checkScope) {
					// 無存取權限
					return responseError(SysCode.NoPermission, "apply", "無存取權限!");
				} else {
					// 原授權範圍足夠
					List<ResourceApplyThread> finalApplyThreadList = new ArrayList<ResourceApplyThread>();
					if (portalResource != null) {
						ResourceApplyThread thread = new ResourceApplyThread();
						thread.setDownloadPath(downloadPath);
						//thread.setTxId(txId);
						thread.setVerificationType(verificationType);
						//thread.setPsId(psId);
						//thread.setPsdId(psdbean.getId());
						//thread.setBatchId(batchId);
						thread.setPrId(prId);
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
						thread.start();
						finalApplyThreadList.add(thread);
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
				}
				return responseOK();
			}else {
				return responseError(SysCode.RequestRefused, "apply", "拒絕存取");
			}
		} catch (Exception ex) {
			try {
				PortalResourceDownload prd = new PortalResourceDownload();
				prd.setDownloadSn(UUID.randomUUID().toString());
				prd.setProviderKey(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
				prd.setPrId(prId);
				prd.setCtime(new Date());
				prd.setTransactionUid(transactionUid);
				prd.setEmail(SessionMember.getSessionMemberToMember(sr.getMember()).getEmail());
				prd.setVerificationType(verificationType.name());
				prd.setRequestStatus(RequestType.fail.name());
				if(portalResource == null) {
					prd.setIsShow(-2); // portalResource is null
				} else {
					prd.setIsShow(portalResource.getIsShow()==null?0:portalResource.getIsShow());
				}
				portalResourceDownloadMapper.insertSelective(prd);
			} catch (Exception e) {
				logger.error("[{}] 申請失敗 responseError--1", transactionUid);
				logger.error(ex.getLocalizedMessage(), ex);
			}
			logger.error("[{}] 申請失敗 responseError--2", transactionUid);
			logger.error(ex.getLocalizedMessage(), ex);
			return responseError(SysCode.SystemError, "資料暫時無法下載，請稍後再試");
//			return responseError(SysCode.SystemError, "apply", "申請失敗");
		}
	}

	/**
	 * 刪除舊有下載資料
	 * @param request
	 * @param params
	 * @return
	 */
	@GetMapping("/download/del/{prId}")
	public ResponseEntity<RestResponseBean> postDelDownloadFileByPrId(@PathVariable("prId") Integer prId,
																	  HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if (sr != null) {
			try {
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.HOUR, -fileStoreTime);
				Date startTime = cal.getTime();
				/*PortalResourceDownloadExample portalResourceDownloadExample = new PortalResourceDownloadExample();
				portalResourceDownloadExample.createCriteria()
				.andProviderKeyEqualTo(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount())
				.andPrIdEqualTo(prId)
				.andCtimeGreaterThan(startTime)
				.andStatEqualTo(0);
				portalResourceDownloadExample.setOrderByClause("ctime desc");*/
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
				param.put("prId", prId);
				param.put("sCtime", startTime);
				param.put("stat", 0);
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
				if(downloadList!=null&&downloadList.size()>0) {
					for(PortalResourceDownload d:downloadList) {
						PortalResourceDownload d1 = new PortalResourceDownload();
						d1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(d.getDownloadSn()));
						d1.setStat(9);
						portalResourceDownloadMapper.updateByPrimaryKeySelective(d1);
					}
				}
				return responseOK();
			}catch(Exception ex) {
				return responseError(SysCode.SystemError, "delete", "刪除失敗");
			}
		}else {
			return responseError(SysCode.AuthenticateFail, "delete", "尚未完成身分驗證!");
		}
	}

	/**
	 *
	 * @param request
	 * @param params
	 * @return
	 */
	@GetMapping("/download/changestatone/{prId}")
	public ResponseEntity<RestResponseBean> updateDownloadFileToStatOneByPrId(@PathVariable("prId") Integer prId,
																			  HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if (sr != null) {
			try {
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.HOUR, -fileStoreTime);
				Date startTime = cal.getTime();
				/*PortalResourceDownloadExample portalResourceDownloadExample = new PortalResourceDownloadExample();
				portalResourceDownloadExample.createCriteria()
				.andProviderKeyEqualTo(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount())
				.andPrIdEqualTo(prId)
				.andCtimeGreaterThan(startTime)
				.andStatEqualTo(0);
				portalResourceDownloadExample.setOrderByClause("ctime desc");*/
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
				param.put("prId", prId);
				param.put("sCtime", startTime);
				param.put("stat", 0);
				param.put("psdIdIsNull", true);
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
				if(downloadList!=null&&downloadList.size()>0) {
					for(PortalResourceDownload d:downloadList) {
						PortalResourceDownload d1 = new PortalResourceDownload();
						d1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(d.getDownloadSn()));
						d1.setStat(1);
						portalResourceDownloadMapper.updateByPrimaryKeySelective(d1);
					}
				}
				return responseOK();
			}catch(Exception ex) {
				return responseError(SysCode.SystemError, "change", "異動失敗");
			}
		}else {
			return responseError(SysCode.AuthenticateFail, "change", "尚未登入!");
		}
	}

	
	/**
	 *
	 * @param request
	 * @param params
	 * @return
	 */
	@GetMapping("/download/changestatonebyspdetail/{prId}")
	public ResponseEntity<RestResponseBean> updateDownloadFileToStatOneByPrIdForServiceDetail(@PathVariable("prId") Integer prId,
																			  HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if (sr != null) {
			try {
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.HOUR, -fileStoreTime);
				Date startTime = cal.getTime();
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
				param.put("prId", prId);
				param.put("sCtime", startTime);
				param.put("stat", 0);
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
				if(downloadList!=null&&downloadList.size()>0) {
					for(PortalResourceDownload d:downloadList) {
						PortalResourceDownload d1 = new PortalResourceDownload();
						d1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(d.getDownloadSn()));
						d1.setStat(1);
						portalResourceDownloadMapper.updateByPrimaryKeySelective(d1);
					}
				}
				return responseOK();
			}catch(Exception ex) {
				return responseError(SysCode.SystemError, "change", "異動失敗");
			}
		}else {
			return responseError(SysCode.AuthenticateFail, "change", "尚未登入!");
		}
	}	
	
	@PostMapping("/verification/check")
	@ResponseBody
	public ResponseEntity<RestResponseBean> verificationCheck(
		HttpServletRequest request,
		@RequestBody Map<String, Object> params) {
		//String captcha = MapUtils.getString(params, "captcha");
		String prId = MapUtils.getString(params, "prId", "");
		/*Boolean isValid = captchaCheck(request, captcha, prId);
		if(BooleanUtils.isFalse(isValid)) {
			String ip = HttpHelper.getRemoteIp(request);
			checkPortalBoxLockCheck(ip);
			return responseError(SysCode.InvalidCaptcha, "captcha", "圖型驗證碼不正確");
		}*/

		String downloadVerify = null;
		if (params.get("downloadVerify") != null) {
			downloadVerify = params.get("downloadVerify").toString();
		}
		if(downloadVerify==null||downloadVerify.trim().length()==0) {
			// 當驗證條碼輸入錯誤的時候 lock
			String ip = HttpHelper.getRemoteIp(request);
			checkPortalBoxLockCheck(ip);
			return responseError(SysCode.SystemError, "downloadVerify", "您輸入的驗證序號有誤");
		}else {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為20分鐘前(20分鐘前下載要求有效)
			cal.add(Calendar.MINUTE, -20);
			Date startTime = cal.getTime();
			/*PortalBoxExample portalBoxExample = new PortalBoxExample();
			portalBoxExample.createCriteria().andDownloadVerifyEqualTo(downloadVerify).andCtimeGreaterThan(startTime);
			portalBoxExample.setOrderByClause("ctime desc");*/
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("downloadVerify", downloadVerify);
			param.put("sCtime", startTime);
			param.put("ctimeDesc", true);
			List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param);
			if (portalBoxList != null && portalBoxList.size() > 0) {
				Map<String, Object> data = new HashMap<String, Object>();
				// 回傳 Ｍember 供發驗證碼使用
				PortalBox portalBox = portalBoxList.get(0);
				if(portalBox.getPsdId()!=null) {
					PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalBox.getPsdId()));
					PortalServiceDownloadSub downloadSub = portalServiceDownloadSubMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getId()));
					if(portalServiceDownload == null) {
						return responseError(SysCode.DataNotExist, "downloadVerify", "您輸入的驗證序號有誤");
					}
					if(portalServiceDownload.getStat() == 1) {
						return responseError(SysCode.DataNotExist, "downloadVerify", "您輸入的驗證序號有誤");
					}
					PortalBoxExt box = new PortalBoxExt();
					try {
						BeanUtils.copyProperties(box, portalBox);
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}

					PortalService portalService = null;
					if (portalServiceDownload != null) {
						box.setCode(portalServiceDownload.getCode());
						portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getPsId()));
						if (portalService != null) {
							PortalCounterSub counterSub = portalCounterSubMapperExt.selectByPrimaryKey(ValidatorHelper.limitNumber(downloadSub.getPcsId()));
							data.put("counterType", counterSub.getType());

							PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getProviderId()));
							if (portalProvider != null) {
								box.setPsName(portalService.getName());
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
						int tmpstartTime = (int) ((ctime - nowTime) / 1000);
						box.setStartTime(tmpstartTime);
					}
					
					List<PortalBoxExt> tmpPortalBoxExtList = portalResourceExtMapper.selectMyBoxForCounterByPsdId(box.getPsdId());
					List<PortalBoxExt> tmpfinalPortalBoxExtList = new ArrayList<PortalBoxExt>();
					List<Integer> tmpforFinalPrIdCheck = new ArrayList<Integer>();
					if(tmpPortalBoxExtList!=null&&tmpPortalBoxExtList.size()>0) {
						for(PortalBoxExt tp:tmpPortalBoxExtList) {
							PortalBoxExt tmpbox = new PortalBoxExt();
							try {
								BeanUtils.copyProperties(tmpbox, tp);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(tmpbox.getDownloadSn()));
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

							/**
							 * API.vTL7cCtoSl	財產資料(RAW DATA)
							 * API.mBqP4awHJY	所得資料(RAW DATA)
							 */
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
								int tmpstartTime = (int) ((tmpctime - tmpnowTime) / 1000);
								tmpbox.setStartTime(tmpstartTime);
							}

							if ((tmpnowTime + tmpwaitTime) >= tmpctime) {
								if (!tmpforFinalPrIdCheck.contains(tmpbox.getPrId())) {
									PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(tmpbox.getDownloadSn()));
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
					
					ulogUtil.recordFullByPr(null, portalService, null, null, null, ActionEvent.EVENT_330, null, null, HttpHelper.getRemoteIp(request));
					List<Member> memberList = null;
					
					if(box.getAgentUid()!=null&&box.getAgentBirthdate()!=null) {
						System.out.println("===通知代辦人===");
			            Map<String,Object> mmParam = new HashMap<>();
			            mmParam.put("uid", box.getAgentUid().toUpperCase());
			            mmParam.put("birthdate", box.getAgentBirthdate());
			            mmParam.put("statIsNullorZero", true);
						memberList = memberMapper.selectByExample(mmParam);
					}else {
						System.out.println("===通知原申請人===");
						Map<String,Object> param2 = new HashMap<String,Object>();
						param2.put("account", ValidatorHelper.removeSpecialCharacters(tmpPortalBoxExtList.get(0).getProviderKey()));
						memberList = memberMapper.selectByExample(param2);
					}
					if(memberList != null && memberList.size() > 0) {
						String ip = HttpHelper.getRemoteIp(request);
						resetPortalBoxLockCheck(ip); //條碼正確，lock ip 重置
						Member member = memberList.get(0);
						data.put("member", member);
						data.put("box", box);
						return responseOK(data);
					} else {
						return responseError(SysCode.AccountNotExist, "downloadVerify", "您輸入的驗證序號有誤");
					}
				}else {
					PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(portalBox.getDownloadSn()));
					if(portalResourceDownload == null) {
						return responseError(SysCode.DataNotExist, "downloadVerify", "您輸入的驗證序號有誤");
					}

					if(portalResourceDownload.getStat() == 1) { // 已下載
						return responseError(SysCode.DataNotExist, "downloadVerify", "您輸入的驗證序號有誤");
					}

					PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceDownload.getPrId()));

					ulogUtil.recordFullByPr(null, null, null, portalResource, portalResourceDownload.getTransactionUid(), ActionEvent.EVENT_330, null, null, HttpHelper.getRemoteIp(request));
					
					Map<String,Object> param2 = new HashMap<String,Object>();
					param2.put("account", ValidatorHelper.removeSpecialCharacters(portalResourceDownload.getProviderKey()));
					List<Member> memberList = memberMapper.selectByExample(param2);
					if(memberList != null && memberList.size() > 0) {
						String ip = HttpHelper.getRemoteIp(request);
						resetPortalBoxLockCheck(ip); //條碼正確，lock ip 重置
						Member member = memberList.get(0);
						data.put("member", member);
						return responseOK(data);
					} else {
						return responseError(SysCode.AccountNotExist, "downloadVerify", "您輸入的驗證序號有誤");
					}
				}

			}else {
				// 當驗證條碼輸入錯誤的時候 lock
				String ip = HttpHelper.getRemoteIp(request);
				checkPortalBoxLockCheck(ip);
				return responseError(SysCode.SystemError, "downloadVerify", "您輸入的驗證序號有誤");
			}
		}
	}

	@GetMapping("/verification/lock")
	@ResponseBody
	public ResponseEntity<RestResponseBean> verificationLock(HttpServletRequest request) {
		String ip = HttpHelper.getRemoteIp(request);
		PortalBoxLockCheck portalBoxLockCheck= portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
		Map<String, Object> data = new HashMap<>();
		data.put("lock", portalBoxLockCheck);
		return responseOK(data);
	}

	/**
	 * 資料下載，多筆下載的預覽功能（瀏覽器產生公私金鑰，上傳公鑰加密Server端產生AES/CBC key及加密後資料，由client端解密）
	 * @param prId
	 * @param request
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/preview/{prId}")
	public ResponseEntity<RestResponseBean> previewFileNotFromMyBox(@PathVariable("prId") Integer prId,
		HttpServletRequest request, @RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
		File pdfFile = null;
		if (sr != null && params.get("key")!=null &&params.get("key").toString().length()>0) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為8小時前
			cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
			Date startTime = cal.getTime();
			Map<String,Object> param1 = new HashMap<String,Object>();
			param1.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			param1.put("prId", prId);
			param1.put("sCtime", startTime);
			param1.put("psdIdIsNull", true);
			List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
			if (downloadList != null && downloadList.size() > 0) {
				PortalResourceDownload prd = downloadList.get(0);
				Integer previewCount;
				if(prd.getPreviewCount() == null) {
					previewCount = new Integer(1);
				} else {
					previewCount = ValidatorHelper.limitNumber(prd.getPreviewCount()) + 1;
				}
				PortalResourceDownload prd1 = new PortalResourceDownload();
				prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
				prd1.setPreviewCount(previewCount);
				portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);

				PortalResource pr = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
				// connect ftp
				FTPClient client = new FTPClient();
				FileOutputStream fos = null;
				client.connect(ftpHost, 21);
				client.login(ftpUsername, ftpPassword);
				client.setFileType(FTP.BINARY_FILE_TYPE);
				client.setBufferSize(1024 * 1024 * 10);
				client.changeWorkingDirectory("/mydata");
				String randomId = SequenceHelper.createUUID();
				String localFileName = ftpPath + File.separator +sdf6.format(new Date())+File.separator+ randomId + File.separator + ValidatorHelper.removeSpecialCharacters(prd.getFiles());
				if(!(new File(localFileName).getParentFile()).exists()) {
					(new File(localFileName).getParentFile()).mkdirs();
				}
				fos = new FileOutputStream(localFileName);
				client.retrieveFile(ValidatorHelper.removeSpecialCharacters(prd.getFiles()), fos);
				if(fos!=null) {
					fos.close();
				}
				client.logout();
				client.disconnect();
				File localFile = new File(localFileName);
				/**
				 * AES解密
				 */
				File filename1enc = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"dec."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
				byte[] b = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));
				try {
					byte[] decryptb = decrypt(b,ftpSecretkey);
					FileUtils.writeByteArrayToFile(filename1enc, decryptb);
				} catch (Exception e) {
					e.printStackTrace();
				}

				//附檔名
				String[] filesStrList = localFile.getName().split("[.]");
				String extStr = "";
				if(filesStrList.length==0) {
				}else {
					extStr = filesStrList[filesStrList.length-1];
				}

				String scopeStr1 = "";
				Map<String,Object> sparam = new HashMap<String,Object>();
				sparam.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
				List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
				try {
					if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
						for (PortalResourceScope s : portalResourceScopeList) {
							scopeStr1 = scopeStr1 + s.getScope() + " ";
						}
						scopeStr1 = scopeStr1.trim();
					}
				} catch (Exception ex1) {
				}
				// 31 預覽
				ulogUtil.recordFullByPr(sr, null, null, portalResource, prd.getTransactionUid(), null, scopeStr1, 31, HttpHelper.getRemoteIp(request));

				/**
				 * 解壓取新的pdf
				 */
				File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+localFile.getName().replaceFirst("."+extStr, ""));
				if(!newpath.exists()) {
					newpath.mkdirs();
				}
				unzip(filename1enc,newpath);
				File[] allFiles = newpath.listFiles();
				
				if(allFiles!=null&&allFiles.length>0) {
					for(File f:allFiles) {
						System.out.println(f.getAbsolutePath());
						String[] allFileStrList = f.getName().split("[.]");
						String ext1Str = "";
						if(allFileStrList.length==0) {
						}else {
							ext1Str = allFileStrList[allFileStrList.length-1];
						}
						System.out.println(ext1Str);
						if(ext1Str.equalsIgnoreCase("pdf")) {
							pdfFile = f;
						}
					}
				}

				if(pdfFile!=null&&localFile!=null&&filename1enc!=null) {
					localFile.delete();
					filename1enc.delete();
				}
			}
		}
		/**
		 * pdf file 轉 base64 string
		 */
		Map<String, Object> data = new HashMap<>();
		if(pdfFile!=null) {
			data.put("filename", URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64Decoder.decode(params.get("key").toString()));
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        Key k = keyFactory.generatePublic(keySpec);
	        Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("UlNBL0VDQi9PQUVQV2l0aE1ENUFuZE1HRjFQYWRkaW5n")));
	        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
	        cipher.init(Cipher.ENCRYPT_MODE, k, oaepParams);
	        // Data must not be longer than 245 bytes
	        String base64FileStr = base64Encoder.encodeToString(FileUtils.readFileToByteArray(pdfFile));
	        String keyStr = SequenceHelper.createUUID();
			String ivStr = RandomUtils.alphaNumericString(16);//SequenceHelper.createUUID();
			String key_encode_str = base64Encoder.encodeToString(cipher.doFinal(keyStr.getBytes("UTF-8")));
			data.put("key", key_encode_str);
			data.put("iv", ivStr);
			data.put("data", base64Encoder.encodeToString(CBCUtil.encrypt_cbc(base64FileStr.getBytes("UTF-8"),keyStr,ivStr)));
			return responseOK(data);
		} else {
			return responseError(SysCode.DataNotExist, "prId", "資料不存在");
		}
	}
	
	/**
	 * 個人專區，資料條碼檔案預覽功能（瀏覽器產生公私金鑰，上傳公鑰加密Server端產生AES/CBC key及加密後資料，由client端解密）
	 * @param boxId
	 * @param request
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/box/prview/{id}")
	public ResponseEntity<RestResponseBean> postBoxPreview(@PathVariable("id") Integer boxId,
		HttpServletRequest request, @RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		File pdfFile = null;
		PortalResource portalResource = null;
		if (sr != null && params.get("key")!=null &&params.get("key").toString().length()>0) {
			System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			PortalBoxExt portalBoxExt = portalResourceExtMapper.selectMyBoxById(boxId);
			if (SessionMember.getSessionMemberToMember(sr.getMember()).getAccount().equalsIgnoreCase(portalBoxExt.getProviderKey())) {
				portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalBoxExt.getPrId()));
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("downloadSn",ValidatorHelper.removeSpecialCharacters(portalBoxExt.getDownloadSn()));				
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
				PortalResourceDownload download = null;
				if (downloadList != null && downloadList.size() > 0) {
					download = downloadList.get(0);
				}
				if (download != null) {
					Integer previewCount;
					if(download.getPreviewCount() == null) {
						previewCount = new Integer(1);
					} else {
						previewCount = ValidatorHelper.limitNumber(download.getPreviewCount()) + 1;
					}
					PortalResourceDownload download1 = new PortalResourceDownload();
					download1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
					download1.setPreviewCount(previewCount);
					portalResourceDownloadMapper.updateByPrimaryKeySelective(download1);
					
					FTPClient client = new FTPClient();
					FileOutputStream fos = null;
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.setBufferSize(1024 * 1024 * 10);
					client.changeWorkingDirectory("/mydata");
					String randomId = SequenceHelper.createUUID();
					String localFileName = ftpPath + File.separator +sdf6.format(new Date())+File.separator+ randomId + File.separator + ValidatorHelper.removeSpecialCharacters(download.getFiles());
					if(!(new File(localFileName).getParentFile()).exists()) {
						(new File(localFileName).getParentFile()).mkdirs();
					}
					fos = new FileOutputStream(localFileName);
					client.retrieveFile(ValidatorHelper.removeSpecialCharacters(download.getFiles()), fos);
					if(fos!=null) {
						fos.close();
					}
					client.logout();
					client.disconnect();
					File localFile = new File(localFileName);
					if (localFile.exists()) {
						System.out.println("File exist=" + localFile.getAbsolutePath());
					} else {
						System.out.println("File not exist=" + localFile.getAbsolutePath());
					}
					/**
					 * AES解密
					 */
					File filename1enc = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"dec."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
					byte[] b = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));
					try {
						byte[] decryptb = decrypt(b,ftpSecretkey);
						FileUtils.writeByteArrayToFile(filename1enc, decryptb);
					} catch (Exception e) {
						e.printStackTrace();
					}

					String scopeStr1 = "";
					Map<String,Object> sparam = new HashMap<String,Object>();
					sparam.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
					List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
					try {
						if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
							for (PortalResourceScope s : portalResourceScopeList) {
								scopeStr1 = scopeStr1 + s.getScope() + " ";
							}
							scopeStr1 = scopeStr1.trim();
						}
					} catch (Exception ex1) {
					}
					// 31 預覽
					ulogUtil.recordFullByPr(sr, null, null, portalResource, download.getTransactionUid(), null, scopeStr1, 31, HttpHelper.getRemoteIp(request));

					//附檔名
					String[] filesStrList = filename1enc.getName().split("[.]");
					String extStr = "";
					if(filesStrList.length==0) {
					}else {
						extStr = filesStrList[filesStrList.length-1];
					}

					/**
					 * 解壓取新的pdf
					 */
					//解壓
					File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+filename1enc.getName().replaceFirst("."+extStr, ""));
					if(!newpath.exists()) {
						newpath.mkdirs();
					}
					unzip(filename1enc,newpath);
					File[] allFiles = newpath.listFiles();
					if(allFiles!=null&&allFiles.length>0) {
						for(File f:allFiles) {
							System.out.println(f.getAbsolutePath());
							String[] allFileStrList = f.getName().split("[.]");
							String ext1Str = "";
							if(allFileStrList.length==0) {
							}else {
								ext1Str = allFileStrList[allFileStrList.length-1];
							}
							System.out.println(ext1Str);
							if(ext1Str.equalsIgnoreCase("pdf")) {
								pdfFile = f;
							}
						}
					}
				}
			}
		}
		/**
		 * pdf file 轉 base64 string
		 */
		Map<String, Object> data = new HashMap<>();
		if(pdfFile!=null&&portalResource!=null) {
			data.put("filename", URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64Decoder.decode(params.get("key").toString()));
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        Key k = keyFactory.generatePublic(keySpec);
	        Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("UlNBL0VDQi9PQUVQV2l0aE1ENUFuZE1HRjFQYWRkaW5n")));
	        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
	        cipher.init(Cipher.ENCRYPT_MODE, k, oaepParams);
	        // Data must not be longer than 245 bytes
	        String base64FileStr = base64Encoder.encodeToString(FileUtils.readFileToByteArray(pdfFile));
	        String keyStr = SequenceHelper.createUUID();
			String ivStr = RandomUtils.alphaNumericString(16);//SequenceHelper.createUUID();
			String key_encode_str = base64Encoder.encodeToString(cipher.doFinal(keyStr.getBytes("UTF-8")));
			data.put("key", key_encode_str);
			data.put("iv", ivStr);
			data.put("data", base64Encoder.encodeToString(CBCUtil.encrypt_cbc(base64FileStr.getBytes("UTF-8"),keyStr,ivStr)));
			return responseOK(data);
		} else {
			return responseError(SysCode.DataNotExist, "prId", "資料不存在");
		}
	}
	
	/**
	 * 線上及臨櫃服務預覽功能（瀏覽器產生公私金鑰，上傳公鑰加密Server端產生AES/CBC key及加密後資料，由client端解密）
	 * @param prId
	 * @param downloadSn
	 * @param request
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/sppreview/{prId}/{downloadSn}")
	public ResponseEntity<RestResponseBean> previewFileNotFromSpDetail(@PathVariable("prId") Integer prId, @PathVariable("downloadSn") String downloadSn,
		HttpServletRequest request, @RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
		File pdfFile = null;
		if (sr != null && params.get("key")!=null &&params.get("key").toString().length()>0) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為8小時前
			cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
			Date startTime = cal.getTime();
			
			PortalResourceDownload prd = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(downloadSn));
			Integer previewCount;
			if(prd.getPreviewCount() == null) {
				previewCount = new Integer(1);
			} else {
				previewCount = ValidatorHelper.limitNumber(prd.getPreviewCount()) + 1;
			}
			PortalResourceDownload prd1 = new PortalResourceDownload();
			prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
			prd1.setPreviewCount(previewCount);
			portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);

			PortalResource pr = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPrId()));
			// connect ftp
			FTPClient client = new FTPClient();
			FileOutputStream fos = null;
			client.connect(ftpHost, 21);
			client.login(ftpUsername, ftpPassword);
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.setBufferSize(1024 * 1024 * 10);
			client.changeWorkingDirectory("/mydata");
			String randomId = SequenceHelper.createUUID();
			String localFileName = ftpPath + File.separator +sdf6.format(new Date())+File.separator+ randomId + File.separator + ValidatorHelper.removeSpecialCharacters(prd.getFiles());
			if(!(new File(localFileName).getParentFile()).exists()) {
				(new File(localFileName).getParentFile()).mkdirs();
			}
			fos = new FileOutputStream(localFileName);
			client.retrieveFile(ValidatorHelper.removeSpecialCharacters(prd.getFiles()), fos);
			if(fos!=null) {
				fos.close();
			}
			client.logout();
			client.disconnect();
			File localFile = new File(localFileName);
			/**
			 * AES解密
			 */
			File filename1enc = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"dec."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
			byte[] b = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));
			try {
				byte[] decryptb = decrypt(b,ftpSecretkey);
				FileUtils.writeByteArrayToFile(filename1enc, decryptb);
			} catch (Exception e) {
				e.printStackTrace();
			}

			String scopeStr1 = "";
			Map<String,Object> sparam = new HashMap<String,Object>();
			sparam.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
			List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
			try {
				if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
					for (PortalResourceScope s : portalResourceScopeList) {
						scopeStr1 = scopeStr1 + s.getScope() + " ";
					}
					scopeStr1 = scopeStr1.trim();
				}
			} catch (Exception ex1) {
			}

			PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPsdId()));
			PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));

			// 31 預覽
			ulogUtil.recordFullByPr(sr, ps, psd.getTxId(), portalResource, prd.getTransactionUid(), null, scopeStr1, 31, HttpHelper.getRemoteIp(request));

			//附檔名
			String[] filesStrList = localFile.getName().split("[.]");
			String extStr = "";
			if(filesStrList.length==0) {
			}else {
				extStr = filesStrList[filesStrList.length-1];
			}
			/**
			 * actionType==1,標準zip回傳規格
			 */
			if(portalResource.getActionType()!=null&&portalResource.getActionType()==1) {
				/**
				 * 解壓取新的pdf
				 */
				File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+localFile.getName().replaceFirst("."+extStr, ""));
				if(!newpath.exists()) {
					newpath.mkdirs();
				}
				unzip(filename1enc,newpath);
				File[] allFiles = newpath.listFiles();
				if(allFiles!=null&&allFiles.length>0) {
					for(File f:allFiles) {
						System.out.println(f.getAbsolutePath());
						String[] allFileStrList = f.getName().split("[.]");
						String ext1Str = "";
						if(allFileStrList.length==0) {
						}else {
							ext1Str = allFileStrList[allFileStrList.length-1];
						}
						System.out.println(ext1Str);
						if(ext1Str.equalsIgnoreCase("pdf")) {
							pdfFile = f;
						}
					}
				}
			}else {
				if(extStr.equalsIgnoreCase("zip")) {
					/**
					 * 解壓取新的pdf
					 */
					File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+localFile.getName().replaceFirst("."+extStr, ""));
					if(!newpath.exists()) {
						newpath.mkdirs();
					}
					unzip(filename1enc,newpath);							
					File[] allFiles = newpath.listFiles();
					if(allFiles!=null&&allFiles.length>0) {
						for(File f:allFiles) {
							System.out.println(f.getAbsolutePath());
							String[] allFileStrList = f.getName().split("[.]");
							String ext1Str = "";
							if(allFileStrList.length==0) {
							}else {
								ext1Str = allFileStrList[allFileStrList.length-1];
							}
							System.out.println(ext1Str);
							if(ext1Str.equalsIgnoreCase("pdf")) {
								pdfFile = f;
							}
						}
					}
				}
			}
		}
		/**
		 * pdf file 轉 base64 string
		 */
		Map<String, Object> data = new HashMap<>();
		if(pdfFile!=null&&portalResource!=null) {
			data.put("filename", URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64Decoder.decode(params.get("key").toString()));
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        Key k = keyFactory.generatePublic(keySpec);
	        Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("UlNBL0VDQi9PQUVQV2l0aE1ENUFuZE1HRjFQYWRkaW5n")));
	        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
	        cipher.init(Cipher.ENCRYPT_MODE, k, oaepParams);
	        // Data must not be longer than 245 bytes
	        String base64FileStr = base64Encoder.encodeToString(FileUtils.readFileToByteArray(pdfFile));
	        String keyStr = SequenceHelper.createUUID();
			String ivStr = RandomUtils.alphaNumericString(16);//SequenceHelper.createUUID();
			String key_encode_str = base64Encoder.encodeToString(cipher.doFinal(keyStr.getBytes("UTF-8")));
			data.put("key", key_encode_str);
			data.put("iv", ivStr);
			data.put("data", base64Encoder.encodeToString(CBCUtil.encrypt_cbc(base64FileStr.getBytes("UTF-8"),keyStr,ivStr)));
			return responseOK(data);
		} else {
			return responseError(SysCode.DataNotExist, "prId", "資料不存在");
		}
	}
	
	/**
	 * 臨櫃核驗預覽功能（瀏覽器產生公私金鑰，上傳公鑰加密Server端產生AES/CBC key及加密後資料，由client端解密）
	 * @param request
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/verification/preview")
	public ResponseEntity<RestResponseBean> postVerificationPreView(
		HttpServletRequest request, @RequestBody Map<String, Object> params) throws Exception {
		HttpSession session = request.getSession();
		String ip = HttpHelper.getRemoteIp(request);
		File pdfFile = null;
		PortalResource portalResource = null;
		PortalResourceDownload download = null;
		PortalServiceDownload psd = null;
		PortalService ps = null;
		if (params.get("key")!=null &&params.get("key").toString().length()>0 && params.get("downloadVerify")!=null &&params.get("downloadVerify").toString().length()>0) {
			String downloadVerify = params.get("downloadVerify").toString().trim();
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為20分鐘前(20分鐘前下載要求有效)
			cal.add(Calendar.MINUTE, -20);
			Date startTime = cal.getTime();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("downloadVerify", downloadVerify);
			param.put("sCtime", startTime);
			param.put("ctimeDesc", true);
			List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param);
			if (portalBoxList != null && portalBoxList.size() > 0) {
				PortalBox portalBox = portalBoxList.get(0);
				for (PortalBox pbox : portalBoxList) {
					PortalBox pbox1 = new PortalBox();
					pbox1.setId(ValidatorHelper.limitNumber(pbox.getId()));
					pbox1.setStat((pbox.getStat() == null ? 0 : ValidatorHelper.limitNumber(pbox.getStat())) + 1);
					//必須判斷欄位
					pbox1.setAgentUid(ValidatorHelper.removeSpecialCharacters(pbox.getAgentUid()));
					pbox1.setAgentBirthdate(ValidatorHelper.limitDate(pbox.getAgentBirthdate()));
					pbox1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(pbox.getAgentVerify()));
					pbox1.setAgreeAgent(ValidatorHelper.limitNumber(pbox.getAgreeAgent()));
					portalBoxMapper.updateByPrimaryKeySelective(pbox1);
				}
				cal.add(Calendar.HOUR, -fileStoreTime);
				startTime = cal.getTime();
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("downloadSn", ValidatorHelper.removeSpecialCharacters(portalBox.getDownloadSn()));
				param1.put("sCtime", startTime);
				param1.put("stat", 0);
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
				System.out.println("downloadList size=" + downloadList.size());
				if (downloadList != null && downloadList.size() > 0) {
					download = downloadList.get(0);
					Integer previewCount;
					if(download.getPreviewCount() == null) {
						previewCount = new Integer(1);
					} else {
						previewCount = ValidatorHelper.limitNumber(download.getPreviewCount()) + 1;
					}
					PortalResourceDownload download1 = new PortalResourceDownload();
					download1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
					download1.setVerificationPreviewCount(previewCount);
					portalResourceDownloadMapper.updateByPrimaryKeySelective(download1);
					
					/**
					 * 紀錄My Box式下載Log
					 */	
					portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(download.getPrId()));

					FTPClient client = new FTPClient();
					FileOutputStream fos = null;
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.setBufferSize(1024 * 1024 * 10);
					client.changeWorkingDirectory("/mydata");
					String randomId = SequenceHelper.createUUID();
					String localFileName = ftpPath + File.separator +sdf6.format(new Date())+File.separator+ randomId + File.separator + ValidatorHelper.removeSpecialCharacters(download.getFiles());
					if(!(new File(localFileName).getParentFile()).exists()) {
						(new File(localFileName).getParentFile()).mkdirs();
					}
					fos = new FileOutputStream(localFileName);
					client.retrieveFile(ValidatorHelper.removeSpecialCharacters(download.getFiles()), fos);
					if(fos!=null) {
						fos.close();
					}
					client.logout();
					client.disconnect();
					File localFile = new File(localFileName);
					if (localFile.exists()) {
						System.out.println("File exist=" + localFile.getAbsolutePath());
					} else {
						System.out.println("File not exist=" + localFile.getAbsolutePath());
					}
					/**
					 * AES解密
					 */
					File filename1enc = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"dec."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
					byte[] b = Files.readAllBytes(Paths.get(localFileName));
					try {
						byte[] decryptb = decrypt(b,ftpSecretkey);
						FileUtils.writeByteArrayToFile(filename1enc, decryptb);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					String scopeStr1 = "";
					Map<String,Object> sparam = new HashMap<String,Object>();
					sparam.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
					List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
					try {
						if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
							for (PortalResourceScope s : portalResourceScopeList) {
								scopeStr1 = scopeStr1 + s.getScope() + " ";
							}
							scopeStr1 = scopeStr1.trim();
						}
					} catch (Exception ex1) {
					}
					/**
					 * 臨櫃核驗-預覽
					 * 32 預覽
					 * 
					 * 代辦人LOG
					 * 54 臨櫃核驗-預覽：您完成提供資料給臨櫃核驗機關
					 * 
					 */
					System.out.println("===========download.getPsdId()========:"+download.getPsdId());
					if(download.getPsdId()!=null&&download.getPsdId()>0) {
						psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(download.getPsdId()));
						ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
					}
					if(portalBox.getAgentUid()!=null&&portalBox.getAgentBirthdate()!=null&&download.getPsdId()!=null&&download.getPsdId()>0 && psd != null) {
						ulogUtil.recordFullByBoxAgent(portalBox, ps, psd.getTxId(), portalResource, download, null, null, 54, HttpHelper.getRemoteIp(request));
					}else {
						ulogUtil.recordFullByPrDownload(download, portalResource, null, scopeStr1, 32, HttpHelper.getRemoteIp(request));
					}
					
					/**
					 * 下載成功
					 */
					//download.setStat(1);
					//portalResourceDownloadMapper.updateByPrimaryKeySelective(download);
					//如果成功，重新計算鎖住
					portalBoxLockCheckMapper.deleteByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
					
					//附檔名
					String[] filesStrList = localFile.getName().split("[.]");
					String extStr = "";
					if(filesStrList.length==0) {
					}else {
						extStr = filesStrList[filesStrList.length-1];
					}
					/**
					 * 解壓取新的pdf
					 */
					File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+filename1enc.getName().replaceFirst("."+extStr, ""));
					if(!newpath.exists()) {
						newpath.mkdirs();
					}
					unzip(filename1enc,newpath);
					File[] allFiles = newpath.listFiles();
					if(allFiles!=null&&allFiles.length>0) {
						for(File f:allFiles) {
							System.out.println(f.getAbsolutePath());
							String[] allFileStrList = f.getName().split("[.]");
							String ext1Str = "";
							if(allFileStrList.length==0) {
							}else {
								ext1Str = allFileStrList[allFileStrList.length-1];
							}
							System.out.println(ext1Str);
							if(ext1Str.equalsIgnoreCase("pdf")) {
								pdfFile = f;
							}
						}
					}
				}
			}
		}
		/**
		 * pdf file 轉 base64 string
		 */
		Map<String, Object> data = new HashMap<>();
		if(pdfFile!=null&&portalResource!=null&&download!=null&&ps!=null) {
			/**
			 * 寄信通知
			 * portal_resource_download 如果psdId有值代表為臨櫃資料的預覽（需寄信或簡訊）
			 */
			if(download.getPsdId()!=null&&download.getPsdId()>0&&portalResource.getProviderId()!=null&&ps.getProviderId()!=null) {
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));
				PortalProvider tmpPortalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
				ulogUtil.recordFullByPs(psd, null, null, null, null, 35, ip);
				try {
					String from = "mydata_system@ndc.gov.tw";
					String title = "【個人化資料自主運用(MyData)平臺】臨櫃服務核驗個人資料通知信（系統信件）";
					String content = "您好：\n\n"
							+ "感謝您透過個人化資料自主運用(MyData)平臺申請「"+ps.getName()+"」臨櫃服務，並於"+sdf8.format(new Date())+"，由「"+portalProvider.getName()+"」核驗您的個人資料，後續將由「"+portalProvider.getName()+"」為您完成服務流程。\n"
							+ "\n" 
							+ "本次核驗之資料集如下：\n";
					Boolean sendEmail = true;
					content	= content + portalResource.getName()+"（資料提供單位："+ tmpPortalProvider.getName() +"）\n";

					if(sendEmail == true) {
						Map<String,Object> param2 = new HashMap<String,Object>();
						param2.put("account", ValidatorHelper.removeSpecialCharacters(download.getProviderKey()));
						List<Member> memberList = memberMapper.selectByExample(param2);
						if(memberList != null && memberList.size() > 0) {
							Member member = memberList.get(0);
							if(StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
								//如有「玉山銀行 - 線上開戶」之相關問題（例：進度查詢），
    							//請洽玉山銀行客服中心24小時服務服務專線：(02)2182-1313、0800-30-1313。
    							content = content + "\n如有「"+portalProvider.getName()+" - "+ps.getName()+"」之相關問題（例：進度查詢），請洽"+portalProvider.getName()+"客服中心服務專線。\n";
    							content = content + "\n"
    									+ "此為系統信件，請勿回信。\n"
    									+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
    									+ "\n"
    									+ "——-\n"
    									+ "我為什麼會收到這封信？\n"
    									+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺提供您的個人資料給上述單位臨櫃核驗，當您服務單位完成您的個人資料核驗，系統會自動發此信通知您。\n"
    									+ "——-\n"
    									+ "非本人？\n"
    									+ "如非您本人同意傳輸或轉存資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
    							List<String> tmpReveicers = new ArrayList<String>();
    							tmpReveicers.add(member.getEmail());
    							MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
								sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
							} else if (StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
								String smbody = "MyData 平臺通知-您已提供資料給「"+portalProvider.getName()+" - "+ps.getName()+"」進行臨櫃核驗";
								SMSUtil.sendSms(member.getMobile(), smbody);
								sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
							} else {
								logger.info("[{}] --寄信失敗--: 無綁定任何聯絡方式");
							}
						} else {
						logger.info("[{}] --寄信失敗--: 無綁定任何聯絡方式");
						}
					}
				}catch(Exception ex) {
					logger.info("[{}] --寄信失敗--");
					logger.error(ex.getLocalizedMessage(), ex);
				}
			}
			
			data.put("filename", URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64Decoder.decode(params.get("key").toString()));
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        Key k = keyFactory.generatePublic(keySpec);
	        Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("UlNBL0VDQi9PQUVQV2l0aE1ENUFuZE1HRjFQYWRkaW5n")));
	        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
	        cipher.init(Cipher.ENCRYPT_MODE, k, oaepParams);
	        // Data must not be longer than 245 bytes
	        String base64FileStr = base64Encoder.encodeToString(FileUtils.readFileToByteArray(pdfFile));
	        String keyStr = SequenceHelper.createUUID();
			String ivStr = RandomUtils.alphaNumericString(16);//SequenceHelper.createUUID();
			String key_encode_str = base64Encoder.encodeToString(cipher.doFinal(keyStr.getBytes("UTF-8")));
			data.put("key", key_encode_str);
			data.put("iv", ivStr);
			data.put("data", base64Encoder.encodeToString(CBCUtil.encrypt_cbc(base64FileStr.getBytes("UTF-8"),keyStr,ivStr)));
			return responseOK(data);
		} else {
			return responseError(SysCode.DataNotExist, "prId", "資料不存在");
		}
	}
	
	public static byte[] cipherDoFinal(Cipher cipher, byte[] srcBytes)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return cipher.doFinal(srcBytes);
	}
	
	/**
	 * 分段加密
	 * @param cipher
	 * @param srcBytes
	 * @param segmentSize
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 */
    public static byte[] cipherDoFinal(Cipher cipher, byte[] srcBytes, int segmentSize)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (segmentSize <= 0)
            throw new RuntimeException("分段大小必須大於0");
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = srcBytes.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 對數據分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > segmentSize) {
                cache = cipher.doFinal(srcBytes, offSet, segmentSize);
            } else {
                cache = cipher.doFinal(srcBytes, offSet, inputLen - offSet);
            }
        	if(i==0) {
        		System.out.println("orig 0 = " + bytesToHex(Arrays.copyOfRange(srcBytes,0,segmentSize)));
        	}
            out.write(cache, 0, cache.length);
            System.out.println(i + " = " + bytesToHex(cache));
            i++;
            offSet = i * segmentSize;
        }
        byte[] data = out.toByteArray();
        out.close();
        return data;
    }
    
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }    
    
	private void checkPortalBoxLockCheck(String ip) {
		System.out.println("==checkPortalBoxLockCheck ip==:"+ip);
		if(ip!=null&&ip.trim().length()>0) {
			PortalBoxLockCheck portalBoxLockCheck= portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			if(portalBoxLockCheck!=null) {
				Date now = new Date();
				Date ctime = portalBoxLockCheck.getCtime();
				long nowTime = now.getTime();
				long ctimeTime = ctime.getTime();
				//如果超過15分鐘，原紀錄不理會，重新記數
				if(nowTime>(ctimeTime+(15*60*1000))) {
					PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
					portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(portalBoxLockCheck.getIp()));
					portalBoxLockCheck1.setCtime(new Date());
					portalBoxLockCheck1.setStat(0);
					portalBoxLockCheck1.setCount(1);
					portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
				}else {
					//沒超過15分鐘，次數少4次(沒加本次)
					if(portalBoxLockCheck.getCount()<4) {
						PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
						portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(portalBoxLockCheck.getIp()));
						portalBoxLockCheck1.setCtime(new Date());
						portalBoxLockCheck1.setCount(ValidatorHelper.limitNumber(portalBoxLockCheck.getCount())+1);
						portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
					}else {
						//超過5次(含本次)
						PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
						portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(portalBoxLockCheck.getIp()));
						portalBoxLockCheck1.setStat(1);
						portalBoxLockCheck1.setCount(ValidatorHelper.limitNumber(portalBoxLockCheck.getCount())+1);
						portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
					}
				}
			}else {
				PortalBoxLockCheck record = new PortalBoxLockCheck();
				record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
				record.setCount(1);
				record.setCtime(new Date());
				record.setStat(0);
				portalBoxLockCheckMapper.insertSelective(record);
			}
		}
	}
	
	private void resetPortalBoxLockCheck(String ip) {
		System.out.println("==reset PortalBoxLockCheck ip==:"+ip);
		if(ip!=null&&ip.trim().length()>0) {
			PortalBoxLockCheck portalBoxLockCheck= portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			if(portalBoxLockCheck!=null) {
				PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
				portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(ip));
				portalBoxLockCheck1.setCtime(new Date());
				portalBoxLockCheck1.setStat(0);
				portalBoxLockCheck1.setCount(0);
				portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
			}
		}
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

	/**
	 * GSP OpenID Connect的登入授權網址。
	 *
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

    /**
     * 解壓縮
     * @param sourceFile
     * @param targetDir
     * @throws IOException 
     */
    public void unzip(File sourceFile, File targetDir) throws IOException {
        if (!targetDir.exists() || !targetDir.isDirectory()) {
        		targetDir.mkdirs();
        }
        ZipEntry entry = null;
        String entryFilePath = null, entryDirPath = null;
        File entryFile = null, entryDir = null;
        int index = 0, count = 0, bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        java.util.zip.ZipFile zip = null;
        if(sourceFile!=null&&sourceFile.getAbsoluteFile()!=null) {
            try {
                zip = new java.util.zip.ZipFile(sourceFile.getAbsoluteFile());
                Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
                while (entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    entryFilePath = ValidatorHelper.removeSpecialCharacters(targetDir.getAbsolutePath() + File.separator + entry.getName());
                    index = entryFilePath.lastIndexOf(File.separator);
                    if (index != -1) {
                        entryDirPath = entryFilePath.substring(0, index);
                    } else {
                        entryDirPath = "";
                    }
                    entryDir = new File(entryDirPath);
                    if (!entryDir.exists() || !entryDir.isDirectory()) {
                        entryDir.mkdirs();
                    }
                    entryFile = new File(entryFilePath);
                    if(!entryFile.isDirectory()) {
                    	FileOutputStream fos = null;
                    	try {
                    		fos = new FileOutputStream(entryFile);
            	            bos = new BufferedOutputStream(fos);
            	            bis = new BufferedInputStream(zip.getInputStream(entry));
            	            if(bos!=null&&bis!=null) {
            		            while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
            		                bos.write(buffer, 0, count);
            		            }
            	            }
        	            	if(bos!=null) {
        			            bos.flush();
        			            bos.close();
        	            	}
            	            if(fos!=null) {
            	            	fos.close();
            	            }
        	            	if(bis!=null) {
        	            		bis.close();
        	            	}
                    	}finally {
                            if(fos!=null) {
                            	HttpClientHelper.safeClose(fos);
                            }
            				if(bos!=null) {
            					HttpClientHelper.safeClose(bos);
            				}
            				if(bis!=null) {
            					HttpClientHelper.safeClose(bis);
            				}
            			}
                    }
                }
                if(zip!=null) {
                	HttpClientHelper.safeClose(zip);
                }
            } catch(Exception ex) {
                if(zip!=null) {
                	HttpClientHelper.safeClose(zip);
                }
            } finally {
                if(zip!=null) {
                	HttpClientHelper.safeClose(zip);
                }
            }        	
        }
    }

	private File packZipWithPassword(File output, ArrayList<File> sources, String password)
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
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		parameters.setPassword(password);
		//zipFile.addFiles(sources, parameters);
		if(sources!=null&&sources.size()>0) {
			for(File f:sources) {
				if(f.getAbsolutePath().indexOf("META-INFO")>-1) {
					parameters.setRootFolderInZip("META-INFO");
					zipFile.addFile(f, parameters);
				}else {
					parameters.setRootFolderInZip("");
					zipFile.addFile(f, parameters);
				}
			}
		}
		return output;
	}

	public static String replaceParamStrForPost(String paramstr, SessionRecord sessionRecord) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
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
					forReplaceStr = "\"" + sessionRecord.getAuthToken().getToken() + "\"";
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

	public static String replaceParamStrForPostAppendTransactionUid(String paramstr, SessionRecord sr,String transactionUid) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
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
					forReplaceStr = "\"" + TokenUtils.getFullAccessToken(sr.getAuthToken()) + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sr.getMember()).getUid() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = "\"" + Base64.getUrlEncoder().encodeToString(SessionMember.getSessionMemberToMember(sr.getMember()).getUid().getBytes("UTF-8")) + "\"";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(SessionMember.getSessionMemberToMember(sr.getMember()).getUid());
					if( matcher1.matches() ){
						idtype = "idNo";
						forReplaceStr = "\"" + idtype + "\"";
					}else {
						idtype = "banNo";
						forReplaceStr = "\"" + idtype + "\"";
					}
				}
				if (matchInnerStr.equalsIgnoreCase("birthdate")) {
					Date date1 = null;
					String datefinalStr = "";
					date1 = SessionMember.getSessionMemberToMember(sr.getMember()).getBirthdate();
					datefinalStr = sdf2.format(date1);
					forReplaceStr = "\"" + datefinalStr + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sr.getMember()).getName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sr.getMember()).getEmail() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("transactionUid")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sr.getMember()).getEmail() + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}

	public static String replaceParamStrForPostNotAppendEscap(String paramstr, SessionRecord sr) {
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
	 * @param tokenEntity
	 * @param userInfoEntity
	 * @param transactionUid
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String replaceParamStrForPostNotAppendEscapAppendTransactionUid(String paramstr, SessionRecord sr,String transactionUid,Map<String, Object> paramMap) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
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
					forReplaceStr = TokenUtils.getFullAccessToken(sr.getAuthToken());
				}
				if (matchInnerStr.equalsIgnoreCase("transactionUid")) {
					forReplaceStr = transactionUid;
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sr.getMember()).getUid();
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = Base64.getUrlEncoder().encodeToString(SessionMember.getSessionMemberToMember(sr.getMember()).getUid().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(SessionMember.getSessionMemberToMember(sr.getMember()).getUid());
					if( matcher1.matches() ){
						idtype = "idNo";
						forReplaceStr = idtype;
					}else {
						idtype = "banNo";
						forReplaceStr = idtype;
					}
				}
				if (matchInnerStr.equalsIgnoreCase("birthdate")) {
					Date date1 = null;
					String datefinalStr = "";
					date1 = SessionMember.getSessionMemberToMember(sr.getMember()).getBirthdate();
					datefinalStr = sdf2.format(date1);
					forReplaceStr = datefinalStr;
				}
				if (matchInnerStr.equalsIgnoreCase("qbirthday")) {
					Date date1 = null;
					String datefinalStr = "";
					date1 = SessionMember.getSessionMemberToMember(sr.getMember()).getBirthdate();
					datefinalStr = sdf.format(date1)+"T00:00:00.000Z";
					forReplaceStr = datefinalStr;
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sr.getMember()).getName();
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sr.getMember()).getEmail();
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

	public static String replaceParamStrForPostAndParam(String paramstr, SessionRecord sr, Map<String, Object> paramMap) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
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
					forReplaceStr = "\"" + TokenUtils.getFullAccessToken(sr.getAuthToken()) + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sr.getMember()).getUid() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = "\"" + Base64.getUrlEncoder().encodeToString(SessionMember.getSessionMemberToMember(sr.getMember()).getUid().getBytes("UTF-8")) + "\"";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(sr.getMember().getUid());
					if( matcher1.matches() ){
						idtype = "idNo";
						forReplaceStr = "\"" + idtype + "\"";
					}else {
						idtype = "banNo";
						forReplaceStr = "\"" + idtype + "\"";
					}
				}
				if (matchInnerStr.equalsIgnoreCase("birthdate")) {
					Date date1 = null;
					String datefinalStr = "";
					date1 = SessionMember.getSessionMemberToMember(sr.getMember()).getBirthdate();
					datefinalStr = sdf2.format(date1);
					forReplaceStr = "\"" + datefinalStr + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sr.getMember()).getName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + SessionMember.getSessionMemberToMember(sr.getMember()).getEmail() + "\"";
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

	public static String replaceParamStrForPostAndParam1Param2Param3(String paramstr, SessionRecord sr, Map<String, String> paramMap) {
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
					forReplaceStr = TokenUtils.getFullAccessToken(sr.getAuthToken());
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

	public static String replaceParamStrForGet(String paramstr, SessionRecord sr) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
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
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sr.getMember()).getUid();
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = Base64.getUrlEncoder().encodeToString(SessionMember.getSessionMemberToMember(sr.getMember()).getUid().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(SessionMember.getSessionMemberToMember(sr.getMember()).getUid());
					if( matcher1.matches() ){
						idtype = "idNo";
						forReplaceStr = idtype;
					}else {
						idtype = "banNo";
						forReplaceStr = idtype;
					}
				}
				if (matchInnerStr.equalsIgnoreCase("birthdate")) {
					Date date1 = null;
					String datefinalStr = "";
					date1 = SessionMember.getSessionMemberToMember(sr.getMember()).getBirthdate();
					datefinalStr = sdf2.format(date1);
					forReplaceStr = datefinalStr;
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sr.getMember()).getName();
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sr.getMember()).getEmail();
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
	
	/**
	 * 去除Html tag
	 * @param str String
	 * @return String
	 */
	private static String removeHtmlTag(String str) {
		String tagPattern = "<{1}[^>]{1,}>{1}";
		//String tagPattern = "<[^>]+>";
		return str.replaceAll(tagPattern, "");
	}

	public static String stripTagsCharArray(String source) {
		char[] array = new char[source.length()];
		int arrayIndex = 0;
		boolean inside = false;
		for (int i = 0; i < source.length(); i++) {
			char let = source.charAt(i);
			if (let == '<') {
				inside = true;
				continue;
			}
			if (let == '>') {
				inside = false;
				continue;
			}
			if (!inside) {
				array[arrayIndex] = let;
				arrayIndex++;
			}
		}
		return new String(array, 0, arrayIndex);
	}
}
