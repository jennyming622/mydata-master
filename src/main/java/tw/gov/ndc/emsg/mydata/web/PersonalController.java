package tw.gov.ndc.emsg.mydata.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.riease.common.enums.ActionEvent;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.riease.common.enums.AuditStatus;
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.HttpHelper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;
import tw.gov.ndc.emsg.mydata.mapper.*;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;
import tw.gov.ndc.emsg.mydata.type.DownloadType;
import tw.gov.ndc.emsg.mydata.type.SendType;
import tw.gov.ndc.emsg.mydata.util.*;

@Controller
@RequestMapping("/personal")
public class  PersonalController {
	private static final Logger logger = LoggerFactory.getLogger(PersonalController.class);
	private static DecimalFormat formatter = new DecimalFormat("#.#");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("年MM月dd日HH時mm分ss秒");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy年MM月dd日HH時mm分");
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
	/**
	 * 加密/解密算法/工作模式/填充方式
	 */
	public static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5PADDING";
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceCategoryMapper portalResourceCategoryMapper;
	@Autowired
	private UlogApiMapperExt ulogApiMapperExt;
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	@Autowired
	private PortalResourceFieldMapper portalResourceFieldMapper;
	@Autowired
	private UlogApiMapper ulogApiMapper;
	@Autowired
	private PortalBoxMapper portalBoxMapper;
	@Autowired
	private PortalBoxLogMapper portalBoxLogMapper;
	@Autowired
	private PortalBoxLockCheckMapper portalBoxLockCheckMapper;
	@Autowired
	private PortalResourceParamMapper portalResourceParamMapper;
	@Autowired
    private MemberMapper memberMapper;
	@Autowired
	private SendLogUtil sendLogUtil;
    @Autowired
    private MaintainUtils maintainUtils;
	@Autowired
    private PortalServiceDownloadMapper portalServiceDownloadMapper;
    
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
	
	@Value("${mail.enable}")
	private String mailEnable;
	@Autowired
	private UlogUtil ulogUtil;
	
	/**
	 * init for /client
	 */
	private List<PortalProvider> allPortalProviderList = null;
	private List<PortalResourceField> allPortalResourceField = null;
	private List<PortalResourceScope> allPortalResourceScopeList = null;
	private List<PortalResourceParam> allPortalResourceParamList = null;
    @PostConstruct
    public void initSpController() {
    	Map<String,Object> nullparam = new HashMap<String,Object>();
    	allPortalProviderList = portalProviderMapper.selectByExample(nullparam);
		Map<String,Object> param3 = new HashMap<String,Object>();
		param3.put("idAsc", true);
    	allPortalResourceField = portalResourceFieldMapper.selectByExample(param3);
		allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(nullparam);
		allPortalResourceParamList = portalResourceParamMapper.selectByExample(nullparam);
    }
    
    @Scheduled(fixedRate = 60000)
    public void execute() {
    	Map<String,Object> nullparam = new HashMap<String,Object>();
    	allPortalProviderList = portalProviderMapper.selectByExample(nullparam);
		Map<String,Object> param3 = new HashMap<String,Object>();
		param3.put("idAsc", true);
    	allPortalResourceField = portalResourceFieldMapper.selectByExample(param3);
		allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(nullparam);
		allPortalResourceParamList = portalResourceParamMapper.selectByExample(nullparam);
    }
    
	/**
	 * MyData分類下載 /personal/list
	 * @throws UnsupportedEncodingException 
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/cate/{cateId}/list")
	public String getPersonalCateListAll(@PathVariable("cateId") Integer cateId,
			HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IllegalAccessException, InvocationTargetException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

		model.addAttribute("testing", false);
		/**
		 * 服務類別最大為13
		 */
		if(cateId>13) {
			cateId = 13;
		}
		
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null && sr.getMember()!=null && sr.getMaskMember()== null){
			Member maskMember = new Member();
			BeanUtils.copyProperties(maskMember,SessionMember.getSessionMemberToMember(sr.getMember()));
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			logger.debug("mask member in download -> {}" , sr.getMaskMember());
			logger.debug("member in download -> {}" , SessionMember.getSessionMemberToMember(sr.getMember()));
		}
		if(sr!=null && sr.getMember()!=null) {
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
		}

		List<String> scopeList = new ArrayList<String>();
		PortalResourceCategory portalResourceCategory = portalResourceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(cateId));
		model.addAttribute("portalResourceCategory", portalResourceCategory);
		List<PortalResource> prList = portalResourceExtMapper.selectByCateIdOrderByOid(cateId);
		List<PortalResourceExt> prExtList = new ArrayList<PortalResourceExt>();
		if (prList != null && prList.size() > 0) {
			for (PortalResource pr : prList) {
				PortalResourceExt prExt = new PortalResourceExt();
				BeanUtils.copyProperties(prExt, pr);
    			if(maintainUtils.checkInMaintain(pr.getPrId())) {
    				prExt.setCheckStat(9);
    			}
	    		PortalProvider pp = null;
	    		if(allPortalProviderList!=null&&allPortalProviderList.size()>0) {
	    			for(PortalProvider tpp:allPortalProviderList) {
	    				if(tpp.getProviderId().compareTo(pr.getProviderId())==0) {
	    					pp = tpp;
	    				}
	    			}
	    		}
				if (pp != null) {
					prExt.setProviderName(pp.getName());
				}
				if(pp != null && pp.getType()!=null) {
					prExt.setType(pp.getType());
				}else {
					prExt.setType(0);
				}
				if(StringUtils.equals(pr.getResourceId(), "API.LPvQONMWj6")) {
					prExt.setUidType(1);
				} else {
					prExt.setUidType(0);
				}

//				Map<String,Object> param3 = new HashMap<String,Object>();
//				param3.put("prId", pr.getPrId());
//				param3.put("idAsc", true);
//				List<PortalResourceField> fieldList = portalResourceFieldMapper.selectByExample(param3);
				List<PortalResourceField> fieldList = new ArrayList<PortalResourceField>();
				if(allPortalResourceField!=null&&allPortalResourceField.size()>0) {
					for(PortalResourceField field:allPortalResourceField) {
						if(field.getPrId().compareTo(pr.getPrId())==0) {
							fieldList.add(field);
						}
					}
				}
				if (fieldList != null && fieldList.size() > 0) {
					prExt.setFieldList(fieldList);
				}
				/**
				 * 判斷下載要求(申請)狀態， 未申請，申請處理中，已申請等待下載 1. portal_resource_download,未超過一天下載要求 == 0
				 * ----> 未申請 > 0 進第二步 2. 該筆資料，已過下載需要實時間 currentTime >= (ctime+waitTime)----->
				 * 已申請等待下載 currentTime < (ctime+waitTime)-------> 申請處理中
				 */
				prExt.setDownloadStatus(0);
				prExt.setPercent(0.0f);
				/**
				 * 利用登入scope運算出redirectUri
				 */
//				Map<String,Object> sparam = new HashMap<String,Object>();
//				sparam.put("prId", pr.getPrId());
//				List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
				List<PortalResourceScope> portalResourceScopeList = new ArrayList<PortalResourceScope>();
				if(allPortalResourceScopeList!=null&&allPortalResourceScopeList.size()>0) {
					for(PortalResourceScope tscope:allPortalResourceScopeList) {
						if(pr.getPrId().compareTo(tscope.getPrId())==0) {
							portalResourceScopeList.add(tscope);
						}
					}
				}
				
				if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
					List<String> needScopeList = new ArrayList<String>();
					if (scopeList != null && scopeList.size() > 0) {
						for (String s : scopeList) {
							if (!needScopeList.contains(s)) {
								needScopeList.add(s);
							}
						}
					}
					List<String> newScopeList = new ArrayList<String>();
					for (PortalResourceScope prs : portalResourceScopeList) {
						if (prs.getScope() != null && prs.getScope().trim().length() > 0) {
							if (!needScopeList.contains(prs.getScope().trim())) {
								needScopeList.add(prs.getScope().trim());
							}
							if (!newScopeList.contains(prs.getScope().trim())) {
								newScopeList.add(prs.getScope().trim());
							}
						}
					}
				} else {
					List<String> needScopeList = new ArrayList<String>();
					List<String> newScopeList = new ArrayList<String>();
					if (scopeList != null && scopeList.size() > 0) {
						for (String s : scopeList) {
							if (!needScopeList.contains(s)) {
								needScopeList.add(s);
							}
							if (!newScopeList.contains(s)) {
								newScopeList.add(s);
							}
						}
					}
				}
				// prExt.setRedirectUri(redirectUri);
				
				//InputParam Handle JSON
				System.out.println("pr.getInputParam():"+pr.getInputParam());
//				Map<String, Object> prpquery = new HashMap<String, Object>(); 
//				prpquery.put("prId", pr.getPrId());
//				List<PortalResourceParam> portalResourceParamList = portalResourceParamMapper.selectByExample(prpquery);
				List<PortalResourceParam> portalResourceParamList = new ArrayList<PortalResourceParam>();
				if(allPortalResourceParamList!=null&&allPortalResourceParamList.size()>0) {
					for(PortalResourceParam prp:allPortalResourceParamList) {
						if(prp.getPrId().compareTo(pr.getPrId())==0) {
							portalResourceParamList.add(prp);
						}
					}
				}
				if(portalResourceParamList!=null&&portalResourceParamList.size()>0) {
					List<Param> paramList = new ArrayList<Param>();
					for(PortalResourceParam prp:portalResourceParamList) {
						Param p = new Param(); 
						String key = prp.getParamName();
					    String val = prp.getParamDesc();
					    p.setName(key);
					    p.setDesc(val);
					    p.setIsOption(prp.getIsOption());
					    if(prp.getParamNameDesc()!=null&&prp.getParamNameDesc().trim().length()>0) {
					    		p.setNameDesc(prp.getParamNameDesc());
					    }
					    if(key.equalsIgnoreCase("locationHsnCd")||key.equalsIgnoreCase("etd_location_hsn_cd")) {
					    		p.setType("location");
					    }else if(key.equalsIgnoreCase("etd_location_town_cd")) {
		    					p.setType("town");
					    }else if(key.equalsIgnoreCase("etd_location_vill_cd")) {
		    					p.setType("vill");
					    }else if(key.equalsIgnoreCase("etd_apply_items")) {
	    						p.setType("etd_apply_items");
					    }else if(key.equalsIgnoreCase("INQ_ID")) {
	    						p.setType("INQ_ID");
					    }else if(key.equalsIgnoreCase("PR_VAL_TP")) {
								p.setType("PR_VAL_TP");
					    }else if(key.equalsIgnoreCase("caseYears")||key.equalsIgnoreCase("etd_case_years")||key.equalsIgnoreCase("INQ_YR")) {
							p.setType("year");
							p.setYearParam(YearParamUtil.getYearParam(pr.getResourceId()));
					    }else if(key.equalsIgnoreCase("ExamYear")) {
					    	p.setType("year1");
					    }else if(key.equalsIgnoreCase("qcartype")) {
					    		p.setType("cartype");
					    }else if(key.equalsIgnoreCase("qlic")) {
				    			p.setType("lic");
					    }else if(key.endsWith("date")||key.endsWith("day")) {
					    		p.setType("date");
					    }else if(key.equalsIgnoreCase("verifycode")) {
		    					p.setType("enctext");
					    }else if(key.equalsIgnoreCase("mobile")) {
					    		p.setType("mobile");
					    }else {
					    		p.setType("text");
					    }
					    paramList.add(p);
					}
					prExt.setParamList(paramList);
					System.out.println("paramList size:"+paramList.size());					
				}
				if (sr != null) {
					Calendar cal = GregorianCalendar.getInstance();
					cal.setTime(new Date());
					Date endTime = cal.getTime();
					// 起始時間定為20分鐘前(20分鐘內下載要求有效)
					// 要加wait_time時間 ()
					int befminus = -40;
					cal.add(Calendar.MINUTE, befminus);
					Date startTime = cal.getTime();
					System.out.println("startTime=" + startTime);
					System.out.println("prId=" + pr.getPrId());
					System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("providerKey",ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount()));
					param.put("prId", ValidatorHelper.limitNumber(pr.getPrId()));
					param.put("sCtime", startTime);
					param.put("stat", 0);
					param.put("psdIdIsNull", true);
					List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
					System.out.println("downloadList size=" + downloadList.size());
					/**
					 * downloadStatus
					 * 0 檔案無下載
					 * 1 準備下載完成
					 * 2 準備中
					 */
					if (downloadList != null && downloadList.size() > 0) {
						PortalResourceDownload download = downloadList.get(0);
						List<String> files = Arrays.asList(download.getFiles().split(","));
						if (files.size() > 1) {
							prExt.setFilename("download" + SequenceHelper.createUUID() + ".zip");
						} else {
							prExt.setFilename(ValidatorHelper.removeSpecialCharacters(download.getFiles()));
						}
						prExt.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
						prExt.setWaitTime(download.getWaitTime());
						long ctime = download.getCtime().getTime();
						int waitTime = download.getWaitTime() * 1000;
						long nowTime = (new Date()).getTime();
						if (waitTime > 0) {
							float percent = ((float) (nowTime - ctime) / (float) waitTime) * 100;
							if (percent >= 100) {
								if(nowTime>(ctime+waitTime+1200000)) {
									prExt.setDownloadStatus(0);
									prExt.setRemainingTime(0);
									prExt.setPercent(0.0f);
								}else {
									prExt.setDownloadStatus(1);
									prExt.setRemainingTime(0);
									prExt.setPercent(100.0f);
								}
							} else {
								prExt.setDownloadStatus(2);
								/**
								 * 計算剩餘時間 (sec)
								 */
								int remainingTime = (int) ((nowTime - (ctime+waitTime))/1000);
								prExt.setRemainingTime(remainingTime);
								prExt.setPercent(percent);
							}
						} else {
							prExt.setDownloadStatus(1);
							prExt.setRemainingTime(0);
							prExt.setPercent(100.0f);
						}
						/**
						 * portalBoxMapper
						 */
						Map<String,Object> param1 = new HashMap<String,Object>();
						param1.put("downloadSn", ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
						param1.put("sCtime", startTime);
						param1.put("ctimeDesc", true);
						List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param1);
						if (portalBoxList != null && portalBoxList.size() > 0) {
							prExt.setDownloadVerify(portalBoxList.get(0).getDownloadVerify());
							prExt.setBoxid(portalBoxList.get(0).getId());
							Calendar cal1 = GregorianCalendar.getInstance();
							cal1.setTime(portalBoxList.get(0).getCtime());
							cal1.add(Calendar.MINUTE, -20);
							Date endDate = cal1.getTime();
							String yearStr = sdf4.format(endDate);
							int year = Integer.valueOf(yearStr) - 1911;
							String monthDayHousrMinSec = sdf5.format(endDate);
							// endTimeNote
							prExt.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
						}

					} else {
						prExt.setDownloadStatus(0);
						prExt.setPercent(0.0f);
					}
				}
				prExtList.add(prExt);
			}
		}

		model.addAttribute("prExtList", prExtList);
		return "download";
	}

	@GetMapping("/download-pre-testing/cate/{cateId}/single")
	public String downloadPreTesting(@PathVariable("cateId") Integer cateId, @RequestParam("current") Integer prId,
										 HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IllegalAccessException, InvocationTargetException, BadPaddingException, IllegalBlockSizeException, IOException {

		model.addAttribute("testing", true);

		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null && sr.getMember()!=null && sr.getMaskMember()== null){
			Member maskMember = new Member();
			BeanUtils.copyProperties(maskMember,SessionMember.getSessionMemberToMember(sr.getMember()));
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			logger.debug("mask member in download -> {}" , sr.getMaskMember());
			logger.debug("member in download -> {}" , SessionMember.getSessionMemberToMember(sr.getMember()));
		}
		if(sr!=null && sr.getMember()!=null) {
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
		}

		List<String> scopeList = new ArrayList<String>();
		PortalResourceCategory portalResourceCategory = portalResourceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(cateId));
		model.addAttribute("portalResourceCategory", portalResourceCategory);
		PortalResource pr = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
		List<PortalResourceExt> prExtList = new ArrayList<PortalResourceExt>();

		PortalResourceExt prExt = new PortalResourceExt();
		BeanUtils.copyProperties(prExt, pr);
		PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(pr.getProviderId()));
		if (pp != null) {
			prExt.setProviderName(pp.getName());
		}
		if(pp.getType()!=null) {
			prExt.setType(pp.getType());
		}else {
			prExt.setType(0);
		}
		if(StringUtils.equals(pr.getResourceId(), "API.LPvQONMWj6")) {
			prExt.setUidType(1);
		} else {
			prExt.setUidType(0);
		}

		Map<String,Object> param3 = new HashMap<String,Object>();
		param3.put("prId", ValidatorHelper.limitNumber(pr.getPrId()));
		param3.put("idAsc", true);
		List<PortalResourceField> fieldList = portalResourceFieldMapper.selectByExample(param3);
		if (fieldList != null && fieldList.size() > 0) {
			prExt.setFieldList(fieldList);
		}
		/**
		 * 判斷下載要求(申請)狀態， 未申請，申請處理中，已申請等待下載 1. portal_resource_download,未超過一天下載要求 == 0
		 * ----> 未申請 > 0 進第二步 2. 該筆資料，已過下載需要實時間 currentTime >= (ctime+waitTime)----->
		 * 已申請等待下載 currentTime < (ctime+waitTime)-------> 申請處理中
		 */
		prExt.setDownloadStatus(0);
		prExt.setPercent(0.0f);
		/**
		 * 利用登入scope運算出redirectUri
		 */
		Map<String,Object> sparam = new HashMap<String,Object>();
		sparam.put("prId", ValidatorHelper.limitNumber(pr.getPrId()));
		List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
		if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
			List<String> needScopeList = new ArrayList<String>();
			if (scopeList != null && scopeList.size() > 0) {
				for (String s : scopeList) {
					if (!needScopeList.contains(s)) {
						needScopeList.add(s);
					}
				}
			}
			List<String> newScopeList = new ArrayList<String>();
			for (PortalResourceScope prs : portalResourceScopeList) {
				if (prs.getScope() != null && prs.getScope().trim().length() > 0) {
					if (!needScopeList.contains(prs.getScope().trim())) {
						needScopeList.add(prs.getScope().trim());
					}
					if (!newScopeList.contains(prs.getScope().trim())) {
						newScopeList.add(prs.getScope().trim());
					}
				}
			}
		} else {
			List<String> needScopeList = new ArrayList<String>();
			List<String> newScopeList = new ArrayList<String>();
			if (scopeList != null && scopeList.size() > 0) {
				for (String s : scopeList) {
					if (!needScopeList.contains(s)) {
						needScopeList.add(s);
					}
					if (!newScopeList.contains(s)) {
						newScopeList.add(s);
					}
				}
			}
		}
		// prExt.setRedirectUri(redirectUri);

		//InputParam Handle JSON
		System.out.println("pr.getInputParam():"+pr.getInputParam());
		Map<String, Object> prpquery = new HashMap<String, Object>();
		prpquery.put("prId", ValidatorHelper.limitNumber(pr.getPrId()));
		List<PortalResourceParam> portalResourceParamList = portalResourceParamMapper.selectByExample(prpquery);
		if(portalResourceParamList!=null&&portalResourceParamList.size()>0) {
			List<Param> paramList = new ArrayList<Param>();
			for(PortalResourceParam prp:portalResourceParamList) {
				Param p = new Param();
				String key = prp.getParamName();
				String val = prp.getParamDesc();
				p.setName(key);
				p.setDesc(val);
				p.setIsOption(prp.getIsOption());
				if(prp.getParamNameDesc()!=null&&prp.getParamNameDesc().trim().length()>0) {
					p.setNameDesc(prp.getParamNameDesc());
				}
				if(key.equalsIgnoreCase("locationHsnCd")||key.equalsIgnoreCase("etd_location_hsn_cd")) {
					p.setType("location");
				}else if(key.equalsIgnoreCase("etd_location_town_cd")) {
					p.setType("town");
				}else if(key.equalsIgnoreCase("etd_location_vill_cd")) {
					p.setType("vill");
				}else if(key.equalsIgnoreCase("etd_apply_items")) {
					p.setType("etd_apply_items");
				}else if(key.equalsIgnoreCase("INQ_ID")) {
					p.setType("INQ_ID");
				}else if(key.equalsIgnoreCase("PR_VAL_TP")) {
					p.setType("PR_VAL_TP");
				}else if(key.equalsIgnoreCase("caseYears")||key.equalsIgnoreCase("etd_case_years")||key.equalsIgnoreCase("INQ_YR")) {
					p.setType("year");
					p.setYearParam(YearParamUtil.getYearParam(pr.getResourceId()));
			    }else if(key.equalsIgnoreCase("ExamYear")) {
	    			p.setType("year1");
				}else if(key.equalsIgnoreCase("qcartype")) {
					p.setType("cartype");
				}else if(key.equalsIgnoreCase("qlic")) {
					p.setType("lic");
				}else if(key.endsWith("date")||key.endsWith("day")) {
					p.setType("date");
				}else if(key.equalsIgnoreCase("verifycode")) {
					p.setType("enctext");
				}else if(key.equalsIgnoreCase("mobile")) {
					p.setType("mobile");
				}else {
					p.setType("text");
				}
				paramList.add(p);
			}
			prExt.setParamList(paramList);
			System.out.println("paramList size:"+paramList.size());
		}
		if (sr != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為20分鐘前(20分鐘內下載要求有效)
			// 要加wait_time時間 ()
			int befminus = -40;
			cal.add(Calendar.MINUTE, befminus);
			Date startTime = cal.getTime();
			System.out.println("startTime=" + startTime);
			System.out.println("prId=" + pr.getPrId());
			System.out.println("account=" + ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount()));
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("providerKey", ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount()));
			param.put("prId", ValidatorHelper.limitNumber(pr.getPrId()));
			param.put("sCtime", startTime);
			param.put("stat", 0);
			param.put("psdIdIsNull", true);
			List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
			System.out.println("downloadList size=" + downloadList.size());
			/**
			 * downloadStatus
			 * 0 檔案無下載
			 * 1 準備下載完成
			 * 2 準備中
			 */
			if (downloadList != null && downloadList.size() > 0) {
				PortalResourceDownload download = downloadList.get(0);
				List<String> files = Arrays.asList(download.getFiles().split(","));
				if (files.size() > 1) {
					prExt.setFilename("download" + SequenceHelper.createUUID() + ".zip");
				} else {
					prExt.setFilename(ValidatorHelper.removeSpecialCharacters(download.getFiles()));
				}
				prExt.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
				prExt.setWaitTime(download.getWaitTime());
				long ctime = download.getCtime().getTime();
				int waitTime = download.getWaitTime() * 1000;
				long nowTime = (new Date()).getTime();
				if (waitTime > 0) {
					float percent = ((float) (nowTime - ctime) / (float) waitTime) * 100;
					if (percent >= 100) {
						if(nowTime>(ctime+waitTime+1200000)) {
							prExt.setDownloadStatus(0);
							prExt.setRemainingTime(0);
							prExt.setPercent(0.0f);
						}else {
							prExt.setDownloadStatus(1);
							prExt.setRemainingTime(0);
							prExt.setPercent(100.0f);
						}
					} else {
						prExt.setDownloadStatus(2);
						/**
						 * 計算剩餘時間 (sec)
						 */
						int remainingTime = (int) ((nowTime - (ctime+waitTime))/1000);
						prExt.setRemainingTime(remainingTime);
						prExt.setPercent(percent);
					}
				} else {
					prExt.setDownloadStatus(1);
					prExt.setRemainingTime(0);
					prExt.setPercent(100.0f);
				}
				/**
				 * portalBoxMapper
				 */
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("downloadSn", ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
				param1.put("sCtime", startTime);
				param1.put("ctimeDesc", true);
				List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param1);
				if (portalBoxList != null && portalBoxList.size() > 0) {
					prExt.setDownloadVerify(portalBoxList.get(0).getDownloadVerify());
					prExt.setBoxid(portalBoxList.get(0).getId());
					//System.out.println("boxid="+portalBoxList.get(0).getId());
				}
				Calendar cal1 = GregorianCalendar.getInstance();
				cal1.setTime(portalBoxList.get(0).getCtime());
				cal1.add(Calendar.MINUTE, -20);
				Date endDate = cal1.getTime();
				String yearStr = sdf4.format(endDate);
				int year = Integer.valueOf(yearStr) - 1911;
				String monthDayHousrMinSec = sdf5.format(endDate);
				// endTimeNote
				prExt.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
			} else {
				prExt.setDownloadStatus(0);
				prExt.setPercent(0.0f);
			}
		}
		prExtList.add(prExt);

		model.addAttribute("prExtList", prExtList);
		
		if(checkIntesting(HttpHelper.getRemoteIp(request))) {
			model.addAttribute("intesting", true);
		}else {
			model.addAttribute("intesting", false);
		}
		return "download";
	}
	
	/**
	 * (非My Box式下載)
	 * @param prId
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/download/{prId}")
	public void downloadFileNotFromMyBox(@PathVariable("prId") Integer prId, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		String ip = HttpHelper.getRemoteIp(request);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
		if (sr != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為8小時前
			cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
			Date startTime = cal.getTime();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("providerKey", ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount()));
			param.put("prId", ValidatorHelper.limitNumber(prId));
			param.put("sCtime", startTime);
			param.put("psdIdIsNull", true);
			List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
			if (downloadList != null && downloadList.size() > 0) {
				PortalResourceDownload prd = downloadList.get(0);
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
				fos.close();
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
				
				/**
				 * 下載成功
				 */
				//prd.setStat(1);
				//portalResourceDownloadMapper.updateByPrimaryKeySelective(prd);
				for(PortalResourceDownload d:downloadList) {
					PortalResourceDownload d1 = new PortalResourceDownload();
					d1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(d.getDownloadSn()));
					d1.setStat(1);
					d1.setDownloadTime(new Date());
					d1.setDownloadType(DownloadType.download.name());
					portalResourceDownloadMapper.updateByPrimaryKeySelective(d1);
				}
				
				/**
				 * insert ulog_api sp 6 授權狀態 1.登入(AS) 2.授權(AS) 3.登出(AS) 4. 要求資料(SP) 5. 送資料(DP)
				 * 6.儲存 7.取消授權(AS)
				 * 
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
				String scopeStr1 = "";
				Map<String,Object> sparam = new HashMap<String,Object>();
				sparam.put("prId", prId);
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
				ulogUtil.recordFullByPr(sr, null, null, portalResource, prd.getTransactionUid(), ActionEvent.EVENT_340, scopeStr1, 15, ip);
				ulogUtil.recordFullByPr(sr, null, null, portalResource, prd.getTransactionUid(), ActionEvent.EVENT_350, scopeStr1, null, ip);
				
				
				/**
				 * 寄信通知
				 */
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
				try {
					Member member = null;
					if(prd != null) {
						Map<String,Object> param2 = new HashMap<String,Object>();
						param2.put("account", ValidatorHelper.removeSpecialCharacters(prd.getProviderKey()));
						List<Member> memberList = memberMapper.selectByExample(param2);
						if(memberList != null && memberList.size() > 0) {
							member = memberList.get(0);
						}
					}
					ulogUtil.recordFullByPr(sr, null, null, portalResource, prd.getTransactionUid(), null, scopeStr1, 35, ip);
					if(member!=null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
						String from = "mydata_system@ndc.gov.tw";
						String title = "【個人化資料自主運用(MyData)平臺】個人資料取用通知信（系統信件）";
						String content = "您好：\n\n"
								+ "感謝您使用個人化資料自主運用(MyData)平臺，您已於"+sdf8.format(new Date())+"成功完成個人資料轉存。\n"
								+ "\n" 
								+ "本次轉存之資料如下：\n"
								+ portalResource.getName()+"（資料提供單位："+ portalProvider.getName() +"）\n" 
								+ "\n"
								+ "此為系統信件，請勿回信。\n"
								+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
								+ "\n"
								+ "——-\n"
								+ "我為什麼會收到這封信？\n"
								+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺轉存或傳輸您的個人資料給上述單位，當您轉存或當服務單位收到您的個人資料，系統會自動發此信通知您。\n"
								+ "——-\n"
								+ "非本人？\n"
								+ "如非您本人同意傳輸或轉存資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
						List<String> tmpReveicers = new ArrayList<String>();
						tmpReveicers.add(member.getEmail());
						MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
						sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
					} else if (member!=null&&StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
						String smbody = "MyData通知-您已於"+sdf8.format(new Date())+"轉存「"+portalResource.getName()+"」資料";
	                    SMSUtil.sendSms(member.getMobile(), smbody);
						sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
					} else {
						System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
					}
				}catch(Exception ex) {
					System.out.println("--寄信失敗--:\n"+ex);
				}
				System.out.println("---------下載成功 1---------");

				/**
				 * 解壓
				 */
				File filename1encpwd = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"decpwd."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
				String tmpRandomId = SequenceHelper.createUUID();
				File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+tmpRandomId);
				if(!newpath.exists()) {
					newpath.mkdirs();
				}
				unzip(filename1enc,newpath);
				ArrayList<File> sources = new ArrayList<File>();
				File[] allFiles = newpath.listFiles();
				if(allFiles!=null&&allFiles.length>0) {
					for(File f:allFiles) {
						if(f.isDirectory()) {
							//UNDO
						}else {
							if(FilenameUtils.getExtension(f.getAbsolutePath()).equalsIgnoreCase("pdf")) {
								sources.add(f);
							}
						}
					}
				}
				//packZipWithPassword(filename1encpwd,sources,SessionMember.getSessionMemberToMember(sr.getMember()).getUid());
				packZip(filename1encpwd,sources);
				response.setHeader("Content-Type", "application/force-download");
				response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".zip");
				
				InputStream file_input = null;
				try {
					file_input = new FileInputStream(filename1encpwd);
					org.apache.commons.io.IOUtils.copy(file_input, response.getOutputStream());
					response.getOutputStream().flush();
					response.getOutputStream().close();
					localFile.delete();
					filename1enc.delete();
					filename1encpwd.delete();
					response.flushBuffer();					
				}finally {
					if(file_input!=null) {
						HttpClientHelper.safeClose(file_input);
					}
				}
				
				/**
				 * DELETE FTP FILE
				 */
				ulogUtil.recordFullByPr(sr, null, null, portalResource, prd.getTransactionUid(), null, scopeStr1, 34, ip);
				try {
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.changeWorkingDirectory("/mydata");
					client.deleteFile(ValidatorHelper.removeSpecialCharacters(prd.getFiles()));
					client.logout();
					client.disconnect();
					
					PortalResourceDownload tmpprd = new PortalResourceDownload();
					tmpprd.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
					tmpprd.setDtime(new Date());
					portalResourceDownloadMapper.updateByPrimaryKeySelective(tmpprd);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * (非My Box式下載)
	 * @param prId
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/download/{prId}/intesting")
	public void downloadFileFromInTesting(@PathVariable("prId") Integer prId, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		String ip = HttpHelper.getRemoteIp(request);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
		if (sr != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date());
			Date endTime = cal.getTime();
			// 起始時間定為8小時前
			cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
			Date startTime = cal.getTime();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("providerKey",SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			param.put("prId",prId);
			param.put("sCtime", startTime);
			param.put("psdIdIsNull", true);
			List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param);
			if (downloadList != null && downloadList.size() > 0) {
				PortalResourceDownload prd = downloadList.get(0);
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
				fos.close();
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
				
				/**
				 * 下載成功
				 */
				//prd.setStat(1);
				//portalResourceDownloadMapper.updateByPrimaryKeySelective(prd);
				for(PortalResourceDownload d:downloadList) {
					PortalResourceDownload d1 = new PortalResourceDownload();
					d1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(d.getDownloadSn()));
					d1.setStat(1);
					d1.setDownloadTime(new Date());
					d1.setDownloadType(DownloadType.download.name());
					portalResourceDownloadMapper.updateByPrimaryKeySelective(d1);
				}
				
				/**
				 * insert ulog_api sp 6 授權狀態 1.登入(AS) 2.授權(AS) 3.登出(AS) 4. 要求資料(SP) 5. 送資料(DP)
				 * 6.儲存 7.取消授權(AS)
				 * 
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
				String scopeStr1 = "";
				Map<String,Object> sparam = new HashMap<String,Object>();
				sparam.put("prId", prId);
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
				ulogUtil.recordFullByPr(sr, null, null, portalResource, prd.getTransactionUid(), ActionEvent.EVENT_340, scopeStr1, 15, ip);
				ulogUtil.recordFullByPr(sr, null, null, portalResource, prd.getTransactionUid(), ActionEvent.EVENT_350, scopeStr1, null, ip);
				
				/**
				 * 寄信通知
				 */
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
				try {
					Member member = null;
					if(prd != null) {
						Map<String,Object> param2 = new HashMap<String,Object>();
						param2.put("account", ValidatorHelper.removeSpecialCharacters(prd.getProviderKey()));
						List<Member> memberList = memberMapper.selectByExample(param2);
						if(memberList != null && memberList.size() > 0) {
							member = memberList.get(0);
						}
					}
					if(member != null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
						String from = "mydata_system@ndc.gov.tw";
						String title = "【個人化資料自主運用(MyData)平臺】個人資料取用通知信（系統信件）";
						String content = "您好：\n\n"
								+ "感謝您使用個人化資料自主運用(MyData)平臺，您已於"+sdf8.format(new Date())+"成功完成個人資料轉存。\n"
								+ "\n" 
								+ "本次轉存之資料如下：\n"
								+ portalResource.getName()+"（資料提供單位："+ portalProvider.getName() +"）\n" 
								+ "\n"
								+ "此為系統信件，請勿回信。\n"
								+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
								+ "\n"
								+ "——-\n"
								+ "我為什麼會收到這封信？\n"
								+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺轉存或傳輸您的個人資料給上述單位，當您轉存或當服務單位收到您的個人資料，系統會自動發此信通知您。\n"
								+ "——-\n"
								+ "非本人？\n"
								+ "如非您本人同意傳輸或轉存資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
						List<String> tmpReveicers = new ArrayList<String>();
						tmpReveicers.add(member.getEmail());
						MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
						sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
					} else if (member!=null&&StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
						String smbody = "MyData通知-您已於"+sdf8.format(new Date())+"轉存「"+portalResource.getName()+"」資料";
	                    SMSUtil.sendSms(member.getMobile(), smbody);
						sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
					} else {
						System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
					}
				}catch(Exception ex) {
					System.out.println("--寄信失敗--:\n"+ex);
				}
				System.out.println("---------下載成功 1---------");


				File filename1encpwd = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"decpwd."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
				
				if(!checkIntesting(HttpHelper.getRemoteIp(request))) {
					String tmpRandomId = SequenceHelper.createUUID();
					File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+tmpRandomId);
					if(!newpath.exists()) {
						newpath.mkdirs();
					}
					
					unzip(filename1enc,newpath);
					ArrayList<File> sources = new ArrayList<File>();
					File[] allFiles = newpath.listFiles();
					if(allFiles!=null&&allFiles.length>0) {
						for(File f:allFiles) {
							if(f.isDirectory()) {
								//UNDO
							}else {
								if(FilenameUtils.getExtension(f.getAbsolutePath()).equalsIgnoreCase("pdf")) {
									sources.add(f);
								}
							}
						}
					}
					packZip(filename1encpwd,sources);	
				}
				
				response.setHeader("Content-Type", "application/force-download");
				response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".zip");
				InputStream file_input = null;
				if(!checkIntesting(HttpHelper.getRemoteIp(request))) {
					file_input = new FileInputStream(filename1encpwd);
				}else {
					file_input = new FileInputStream(filename1enc);
				}
				try {
					org.apache.commons.io.IOUtils.copy(file_input, response.getOutputStream());
					response.getOutputStream().flush();
					response.getOutputStream().close();
					localFile.delete();
					filename1enc.delete();
					if(!checkIntesting(HttpHelper.getRemoteIp(request))) {
						filename1encpwd.delete();
					}
					response.flushBuffer();					
				}finally {
					if(file_input!=null) {
						HttpClientHelper.safeClose(file_input);
					}
				}
				
				/**
				 * DELETE FTP FILE
				 */
				try {
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.changeWorkingDirectory("/mydata");
					client.deleteFile(ValidatorHelper.removeSpecialCharacters(prd.getFiles()));
					client.logout();
					client.disconnect();
					
					PortalResourceDownload tmpprd = new PortalResourceDownload();
					tmpprd.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
					tmpprd.setDtime(new Date());
					portalResourceDownloadMapper.updateByPrimaryKeySelective(tmpprd);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	
	/**
	 * 臨櫃核驗預覽(Box式預覽)-臨櫃預覽(32, "臨櫃核驗(預覽)：您完成提供資料給臨櫃核驗機關", ULogShowType.DpShow),
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/verification/preview")
	public void postVerificationPreView(HttpServletRequest request, HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		Map<String, String> data = new HashMap<String, String>();
		String downloadVerify = null;
		String gRecaptchaResponse = null;
		if (request.getParameter("downloadVerify") != null) {
			downloadVerify = ValidatorHelper.removeSpecialCharacters(request.getParameter("downloadVerify"));
			data.put("downloadVerify", downloadVerify);
		}
		
		/**
		 * 驗證Google Recaptcha，失敗不執行
		 */
		boolean success = true;
		if (success == true) {
			/**
			 * 驗證失敗五次後鎖住，是否已超過十五分鐘
			 */
			String ip = HttpHelper.getRemoteIp(request);
			PortalBoxLockCheck portalBoxLockCheck = portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			Date now = new Date();
			Date ctime = portalBoxLockCheck==null?new Date():portalBoxLockCheck.getCtime();
			long nowTime = now.getTime();
			long ctimeTime = ctime.getTime();
			if(portalBoxLockCheck!=null&&portalBoxLockCheck.getStat()!=null&&portalBoxLockCheck.getStat()==1&&nowTime<(ctimeTime+(15*60*1000))) {
				//鎖住未過十分鐘
				// 您輸入的驗證序號有誤
				response.sendRedirect(frontendContextUrl + "/organ/error6");
			}else {
				if(downloadVerify!=null&&downloadVerify.trim().length()>0) {
					Calendar cal = GregorianCalendar.getInstance();
					cal.setTime(new Date());
					Date endTime = cal.getTime();
					// 起始時間定為20分鐘前(20分鐘前下載要求有效)
					cal.add(Calendar.MINUTE, -20);
					Date startTime = cal.getTime();
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("downloadVerify", ValidatorHelper.removeSpecialCharacters(downloadVerify));
					param.put("sCtime", startTime);
					param.put("ctimeDesc", true);
					List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param);
					System.out.println("portalBoxList size=" + portalBoxList.size());
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
						param1.put("downloadSn",ValidatorHelper.removeSpecialCharacters(portalBox.getDownloadSn()));
						param1.put("sCtime", startTime);
						param1.put("stat", 0);
						List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
						System.out.println("downloadList size=" + downloadList.size());
						if (downloadList != null && downloadList.size() > 0) {
							PortalResourceDownload download = downloadList.get(0);
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
							PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(download.getPrId()));

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
							fos.close();
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
							PortalServiceDownload psd = null;
							PortalService ps = null;
							if(download.getPsdId()!=null&&download.getPsdId()>0) {
								psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(download.getPsdId()));
								ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
							}
							if(portalBox.getAgentUid()!=null&&portalBox.getAgentBirthdate()!=null&&download.getPsdId()!=null&&download.getPsdId()>0&&psd!=null) {
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
							File pdfFile = null;
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
							if(pdfFile!=null) {
								/**
								 * 寄信通知
								 * portal_resource_download 如果psdId有值代表為臨櫃資料的預覽（需寄信或簡訊）
								 */
								/**
								 * 判斷個人資料提供和臨櫃服務提供
								 */
								if(download.getPsdId()!=null&&download.getPsdId()>0) {
									PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));
									PortalProvider tmpPortalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
									ulogUtil.recordFullByPs(psd, null, null, null, null, 35, ip);
		    						try {
		    							String from = "mydata_system@ndc.gov.tw";
		    							String title = "【個人化資料自主運用(MyData)平臺】臨櫃服務核驗個人資料通知信（系統信件）";
		    							String content = "您好：\n\n"
		    									+ "感謝您透過個人化資料自主運用(MyData)平臺申請"+"「"+ps.getName()+"」臨櫃服務，並於"+sdf8.format(new Date())+"，由「"+portalProvider.getName()+"」核驗您的個人資料，後續將由「"+portalProvider.getName()+"」為您完成服務流程。\n"
		    									+ "\n" 
		    									+ "本次核驗之資料集如下：\n";
		    							Boolean sendEmail = true;
		    							content	= content + portalResource.getName()+"（資料提供單位："+ tmpPortalProvider.getName() +"）\n";

		    							if(download != null && sendEmail == true ) {
		    								Map<String,Object> param2 = new HashMap<String,Object>();
		    								param2.put("account", ValidatorHelper.removeSpecialCharacters(download.getProviderKey()));
		    								List<Member> memberList = memberMapper.selectByExample(param2);
		    								if(memberList != null && memberList.size() > 0) {
		    									Member member = memberList.get(0);
		    									if(member != null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
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
								}else {
									/**
									 * 寄信通知
									 */
									PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
									Member member = null;
									try {
										if(download != null) {
											Map<String,Object> param2 = new HashMap<String,Object>();
											param2.put("account", ValidatorHelper.removeSpecialCharacters(download.getProviderKey()));
											List<Member> memberList = memberMapper.selectByExample(param2);
											if(memberList != null && memberList.size() > 0) {
												member = memberList.get(0);
											}
										}
										if(member != null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
											String from = "mydata_system@ndc.gov.tw";
											String title = "【個人化資料自主運用(MyData)平臺】個人資料取用通知信（系統信件）";
											String content = "您好：\n\n"
													+ "感謝您使用個人化資料自主運用(MyData)平臺，您已於"+sdf8.format(new Date())+"成功完成臨櫃核驗。\n"
													+ "\n" 
													+ "本次提供之資料如下：\n"
													+ portalResource.getName()+"（資料提供單位："+ portalProvider.getName() +"）\n" 
													+ "\n"
													+ "此為系統信件，請勿回信。\n"
													+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
													+ "\n"
													+ "——-\n"
													+ "我為什麼會收到這封信？\n"
													+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺轉存或傳輸您的個人資料給上述單位，當您轉存或當服務單位收到您的個人資料，系統會自動發此信通知您。\n"
													+ "——-\n"
													+ "非本人？\n"
													+ "如非您本人同意傳輸或轉存資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
											List<String> tmpReveicers = new ArrayList<String>();
											tmpReveicers.add(member.getEmail());
											MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
											sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
										} else if (member!=null&&StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
											String smbody = "MyData通知-您已於"+sdf8.format(new Date())+"完成「"+portalResource.getName()+"」臨櫃核驗作業";
						                    SMSUtil.sendSms(member.getMobile(), smbody);
											sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
										} else {
											System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
										}
									}catch(Exception ex) {
										System.out.println("--寄信失敗--:\n"+ex);
									}

								}
								
								response.setHeader("Content-Type", "application/pdf");
								response.setHeader("Content-Disposition", "inline; filename=" + ValidatorHelper.removeSpecialCharacters(pdfFile.getName()));
								InputStream file_input = null;
								try {
									file_input = new FileInputStream(pdfFile);
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
						} else {
							// 查無下載檔案
							response.sendRedirect(frontendContextUrl + "/organ/error3");
						}
					} else {
						// 查無驗證碼
						response.sendRedirect(frontendContextUrl + "/organ/error2");
					}
				}else {
					// 您輸入的驗證序號有誤
					response.sendRedirect(frontendContextUrl + "/organ/error2");
				}
			}
		}else {
			// 我不是機器人驗證碼錯誤
			response.sendRedirect(frontendContextUrl + "/organ/error5");
		}
	}
	
	
	/**
	 * 臨櫃核驗下載 (Box式下載)
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@GetMapping("/verification/download")
	public void postVerificationDownload(HttpServletRequest request, HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException, ParserConfigurationException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		//String accessToken = TokenUtils.getFullAccessToken(sr.getAuthToken());
		//System.out.println("accessToken......:" + accessToken);
		String downloadVerify = null;
		String gRecaptchaResponse = null;
		Map<String, String> data = new HashMap<String, String>();
		if (request.getParameter("downloadVerify") != null) {
			downloadVerify = ValidatorHelper.removeSpecialCharacters(request.getParameter("downloadVerify"));
			data.put("downloadVerify", downloadVerify);
		}

		/**
		 * 驗證Google Recaptcha，失敗不執行
		 */
		boolean success = true;
		if (success == true) {
			/**
			 * 驗證失敗五次後鎖住，是否已超過十分鐘
			 */
			String ip = HttpHelper.getRemoteIp(request);
			PortalBoxLockCheck portalBoxLockCheck = portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			Date now = new Date();
			Date ctime = portalBoxLockCheck==null?new Date():portalBoxLockCheck.getCtime();
			long nowTime = now.getTime();
			long ctimeTime = ctime.getTime();
			if(portalBoxLockCheck!=null&&portalBoxLockCheck.getStat()!=null&&portalBoxLockCheck.getStat()==1&&nowTime<(ctimeTime+(15*60*1000))) {
				//鎖住未過十分鐘
				// 您輸入的驗證序號有誤
				response.sendRedirect(frontendContextUrl + "/organ/error6");
			}else {
				if(downloadVerify!=null&&downloadVerify.trim().length()>0) {
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
					System.out.println("portalBoxList size=" + portalBoxList.size());
					
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
						/**
						 * 判斷個人資料提供和臨櫃服務提供
						 */
						if(portalBox.getPsdId()!=null) {
							PortalServiceDownload psdbean = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalBox.getPsdId()));
							PortalService portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdbean.getPsId()));
							PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getProviderId()));
							if(psdbean.getStat()!=null&&psdbean.getStat().compareTo(1)==0) {
								response.sendRedirect(frontendContextUrl + "/organ/error3");
							}
					    		List<String> downloadSnList = new ArrayList<>();
					    		if(psdbean.getDownloadSnList()!=null&&psdbean.getDownloadSnList().trim().length()>0) {
					    			String[] downloadSnArray = psdbean.getDownloadSnList().trim().split("[,]");
					    			for(String s:downloadSnArray) {
					    				downloadSnList.add(s);
					    			}
					    		}
					    		boolean totalFailCheck = true;
					    		if(downloadSnList!=null&&downloadSnList.size()>0) {
					    			for(int i=0;i<downloadSnList.size();i++) {
									PortalResourceDownload  prd = null;
									Map<String,Object> param1 = new HashMap<String,Object>();
									param1.put("downloadSn",ValidatorHelper.removeSpecialCharacters(downloadSnList.get(i)));
									List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
									if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
										prd = portalResourceDownloadList.get(0);
										if(prd.getCode()!=null&&!prd.getCode().equalsIgnoreCase("200")&&!prd.getCode().equalsIgnoreCase("201")
												&&!prd.getCode().equalsIgnoreCase("429")&&!prd.getCode().equalsIgnoreCase("204")) {
										}else {
											//只要對一個就不算
											totalFailCheck = false;
										}
									}
					    			}
					    		}
					    		if(totalFailCheck) {
								// 查無下載檔案
								response.sendRedirect(frontendContextUrl + "/organ/error3");
					    		}
					    		try {
					    			if(psdbean.getFiles()!=null) {
			    						FTPClient client = new FTPClient();
			    						FileOutputStream fos = null;
			    						client.connect(ftpHost, 21);
			    						client.login(ftpUsername, ftpPassword);
			    						client.setFileType(FTP.BINARY_FILE_TYPE);
			    						client.setBufferSize(1024 * 1024 * 10);
			    						client.changeWorkingDirectory("/mydata");
			    						String randomId = SequenceHelper.createUUID();
			    						String localFileName = ftpPath + File.separator + sdf6.format(new Date())+File.separator+ randomId + File.separator + ValidatorHelper.removeSpecialCharacters(psdbean.getFiles());
			    						if(!(new File(localFileName).getParentFile()).exists()) {
			    							(new File(localFileName).getParentFile()).mkdirs();
			    						}
			    						fos = new FileOutputStream(localFileName);
			    						client.retrieveFile(ValidatorHelper.removeSpecialCharacters(psdbean.getFiles()), fos);
			    						fos.close();
			    						client.logout();
			    						client.disconnect();
			    						
			    						/**
			    						 * localFileName 放於 FTP Server 用  AES/CBC/PKCS5PADDING 壓密過
			    						 */
			    						byte[] iv = fetchPortalServiceCbcIv(psdbean.getPsId());
			    						String deLocalFileName = localFileName.replace(".zip", "_decbc.zip");
			    						File deLocalFile = new File(deLocalFileName);
			    						try {
			    							byte[] encryptb = Files.readAllBytes(Paths.get(localFileName));
			    							byte[] decryptb_cbc = decrypt_cbc(encryptb,psdbean.getSecretKey(),new String(iv,"UTF-8"));
			    							FileUtils.writeByteArrayToFile(deLocalFile, decryptb_cbc);
			    						}catch(Exception ex) {
			    							logger.error(ex.getLocalizedMessage(), ex);
			    						}
			    						File f1 = new File(localFileName);
			    						if(f1.exists()&&f1.length()>0) {
				    						/**
				    						 * 變更服務下載狀態
				    						 */
			    							PortalServiceDownload psdbean1 = new PortalServiceDownload();
			    							psdbean1.setId(ValidatorHelper.limitNumber(psdbean.getId()));
			    							psdbean1.setStat(1);
			    							psdbean1.setDownloadTime(new Date());
				    						portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean1);
				    										    						
				    						/**
				    						 * 寄信通知
				    						 */
				    						ulogUtil.recordFullByPs(psdbean, null, null, null, null, 35, ip);
				    						try {
				    							String from = "mydata_system@ndc.gov.tw";
				    							String title = "【個人化資料自主運用(MyData)平臺】臨櫃服務取用個人資料通知信（系統信件）";
				    							String content = "您好：\n\n"
				    									+ "感謝您透過個人化資料自主運用(MyData)平臺申請"+"「"+portalService.getName()+"」臨櫃服務，並於"+sdf8.format(new Date())+"，由「"+portalProvider.getName()+"」並取得您的個人資料，後續將由「"+portalProvider.getName()+"」為您完成服務流程。\n"
				    									+ "\n" 
				    									+ "本次提供之資料集如下：\n";
				    							String downloadSnListStr = psdbean.getDownloadSnList();
				    							PortalResourceDownload tmpPortalResourceDownload = null;
				    							Boolean sendEmail = true;
				    							if(downloadSnListStr!=null&&downloadSnListStr.trim().length()>0) {
				    								String[] downloadSnArray = downloadSnListStr.trim().split("[,]"); 
				    								if(downloadSnArray!=null&&downloadSnArray.length>0) {
				    									for(String downloadSn:downloadSnArray) {
				    										if(downloadSn!=null&&downloadSn.trim().length()>0) {
				    											tmpPortalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(downloadSn));
				    											PortalResourceDownload tmpPortalResourceDownload1 = new PortalResourceDownload();
				    											tmpPortalResourceDownload1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(tmpPortalResourceDownload.getDownloadSn()));
				    											tmpPortalResourceDownload1.setStat(1);
				    											tmpPortalResourceDownload1.setDownloadTime(new Date());
				    											tmpPortalResourceDownload1.setDtime(new Date());
				    											portalResourceDownloadMapper.updateByPrimaryKeySelective(tmpPortalResourceDownload1);

				    											PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(tmpPortalResourceDownload.getPrId()));

				    											/**
				    											 * DELETE FTP FILE
				    											 */
				    											ulogUtil.recordFullByPr(sr, portalService, null, portalResource, tmpPortalResourceDownload.getTransactionUid(), null, null, 34, ip);
				    											
				    											try {
				    												client.connect(ftpHost, 21);
				    												client.login(ftpUsername, ftpPassword);
				    												client.setFileType(FTP.BINARY_FILE_TYPE);
				    												client.changeWorkingDirectory("/mydata");
				    												client.deleteFile(ValidatorHelper.removeSpecialCharacters(tmpPortalResourceDownload.getFiles()));
				    												client.logout();
				    												client.disconnect();
				    											} catch (IOException e) {
				    												e.printStackTrace();
				    											}
				    											
				    											PortalResource tmpPortalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(tmpPortalResourceDownload.getPrId()));
				    											PortalProvider tmpPortalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(tmpPortalResource.getProviderId()));
				    											/**
				    											 * 委託人
				    											 * 47 委託完成：代辦人已至臨櫃提供資料給「嘉義縣 - 敬老愛心卡」申辦完畢
				    											 * 代辦人
				    											 * 55 臨櫃核驗-轉存：您完成提供資料給「嘉義縣 - 敬老愛心卡」
				    											 */
		    								        			if(portalBox.getAgentUid()!=null&&portalBox.getAgentBirthdate()!=null&&tmpPortalResourceDownload.getPsdId()!=null&&tmpPortalResourceDownload.getPsdId()>0) {
		    								            			//委託人
		    								            			ulogUtil.recordFullByBoxAgent(portalBox, portalService, psdbean.getTxId(), tmpPortalResource, tmpPortalResourceDownload, null, null, 47, HttpHelper.getRemoteIp(request));
		    								            			//代辦人
		    								            			ulogUtil.recordFullByBoxAgent(portalBox, portalService, psdbean.getTxId(), portalResource, tmpPortalResourceDownload, null, null, 55, HttpHelper.getRemoteIp(request));
		    								        			}else {
		    								        				ulogUtil.recordFullByPs(psdbean, tmpPortalResource, tmpPortalResourceDownload.getTransactionUid(), ActionEvent.EVENT_310, null, 17, ip);
		    								        			}
				    											content	= content + tmpPortalResource.getName()+"（資料提供單位："+ tmpPortalProvider.getName() +"）\n";
				    											if(!(StringUtils.equals(tmpPortalResourceDownload.getCode(), "200") || StringUtils.equals(tmpPortalResourceDownload.getCode(), "204"))) {
																sendEmail = false;
															}
				    										}
				    									}
				    								}
				    							}
				    							Map<String,Object> param1 = new HashMap<String,Object>();
				    							param1.put("psdId", ValidatorHelper.limitNumber(psdbean.getId()));
				    							List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
				    							if(downloadList!=null&&downloadList.size()>0) {
				    								for(PortalResourceDownload p:downloadList) {
				    									PortalResourceDownload p1 = new PortalResourceDownload();
				    									p1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(p.getDownloadSn()));
				    									p1.setStat(1);
				    									p1.setDownloadTime(new Date());
				    									p1.setDtime(new Date());
				    									portalResourceDownloadMapper.updateByPrimaryKeySelective(p1);
				    								}
				    							}
				    							if(tmpPortalResourceDownload != null && sendEmail == true ) {
				    								Map<String,Object> param2 = new HashMap<String,Object>();
				    								param2.put("account", ValidatorHelper.removeSpecialCharacters(tmpPortalResourceDownload.getProviderKey()));
				    								List<Member> memberList = memberMapper.selectByExample(param2);
				    								if(memberList != null && memberList.size() > 0) {
				    									Member member = memberList.get(0);
				    									if(member != null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
				    										//如有「玉山銀行 - 線上開戶」之相關問題（例：進度查詢），
							    							//請洽玉山銀行客服中心24小時服務服務專線：(02)2182-1313、0800-30-1313。
							    							content = content + "\n如有「"+portalProvider.getName()+" - "+portalService.getName()+"」之相關問題（例：進度查詢），請洽"+portalProvider.getName()+"客服中心服務專線。\n";
							    							content = content + "\n"
							    									+ "此為系統信件，請勿回信。\n"
							    									+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
							    									+ "\n"
							    									+ "——-\n"
							    									+ "我為什麼會收到這封信？\n"
							    									+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺轉存或傳輸您的個人資料給上述單位，當您轉存或當服務單位收到您的個人資料，系統會自動發此信通知您。\n"
							    									+ "——-\n"
							    									+ "非本人？\n"
							    									+ "如非您本人同意傳輸或轉存資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
							    							List<String> tmpReveicers = new ArrayList<String>();
							    							tmpReveicers.add(member.getEmail());
							    							MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
															sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
					    								} else if (StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
					    									String smbody = "MyData 平臺通知-您已提供資料給「"+portalProvider.getName()+" - "+portalService.getName()+"」申辦臨櫃服務";
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
										ulogUtil.recordFullByPs(psdbean, null, null, null, null, 34, ip);
				    						
				    						/**
				    						 * DELETE FTP FILE
				    						 */
				    						try {
				    							client.connect(ftpHost, 21);
				    							client.login(ftpUsername, ftpPassword);
				    							client.setFileType(FTP.BINARY_FILE_TYPE);
				    							client.changeWorkingDirectory("/mydata");
				    							client.deleteFile(ValidatorHelper.removeSpecialCharacters(psdbean.getFiles()));
				    							client.logout();
				    							client.disconnect();
				    							PortalServiceDownload tmppsdbean = new PortalServiceDownload();
				    							tmppsdbean.setId(ValidatorHelper.limitNumber(psdbean.getId()));
				    							tmppsdbean.setDtime(new Date());
				    							portalServiceDownloadMapper.updateByPrimaryKeySelective(tmppsdbean);
				    						} catch (IOException e) {
				    							e.printStackTrace();
				    						}
				    						/**
				    						 * 針對deLocalFileName進行特別處理
				    						 * 1. 中文檔名
				    						 * 2. 解開資料集壓縮檔，清除JSON檔後，重新壓縮回zip檔，
				    						 * 3. 最後重新包一個新的zip檔再傳送資料給臨櫃下載動作
				    						 * 寫 filterSpFile method
				    						 */
				    						File targetFile = filterSpFile(deLocalFile);
											response.setHeader("Content-Type", "application/force-download");
											response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalService.getName()),"UTF-8")+sdf3.format(new Date())+".zip");
											InputStream file_input = null;
											try {
												file_input = new FileInputStream(targetFile);
												org.apache.commons.io.IOUtils.copy(file_input, response.getOutputStream());
												response.getOutputStream().flush();
												response.getOutputStream().close();
												f1.deleteOnExit();
												deLocalFile.deleteOnExit();
												targetFile.deleteOnExit();
												response.flushBuffer();		
											}finally {
												if(file_input!=null) {
													HttpClientHelper.safeClose(file_input);
												}
											}
			    						}
					    			}
					    		}catch(Exception ex) {
					    			response.sendRedirect(frontendContextUrl + "/organ/error3");
					    		}
						}else {
							Map<String,Object> param1 = new HashMap<String,Object>();
							param1.put("downloadSn",ValidatorHelper.removeSpecialCharacters(portalBox.getDownloadSn()));
							param1.put("sCtime", startTime);
							List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
							System.out.println("downloadList size=" + downloadList.size());
							if (downloadList != null && downloadList.size() > 0) {
								PortalResourceDownload download = downloadList.get(0);
								/**
								 * 紀錄My Box式下載Log
								 */
								PortalBoxLog portalBoxLog = new PortalBoxLog();
								portalBoxLog.setBoxId(ValidatorHelper.limitNumber(portalBox.getId()));
								portalBoxLog.setCtime(new Date());
								if(sr!=null) {
									portalBoxLog.setDownloadKey(ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount()));
								}
								portalBoxLog.setDownloadSn(ValidatorHelper.removeSpecialCharacters(portalBox.getDownloadSn()));
								portalBoxLog.setDownloadVerify(ValidatorHelper.removeSpecialCharacters(portalBox.getDownloadVerify()));
								portalBoxLog.setStat(ValidatorHelper.limitNumber(portalBox.getStat()));
								portalBoxLog.setVerifyPwd(ValidatorHelper.removeSpecialCharacters(portalBox.getVerifyPwd()));
								portalBoxLog.setProviderKey(ValidatorHelper.removeSpecialCharacters(download.getProviderKey()));
								portalBoxLog.setIp(ValidatorHelper.removeSpecialCharacters(HttpHelper.getRemoteIp(request)));
								if(download.getBatchId()!=null) {
									portalBoxLog.setBatchId(ValidatorHelper.limitNumber(download.getBatchId()));
								}
								if(download.getPsdId()!=null) {
									portalBoxLog.setPsdId(ValidatorHelper.limitNumber(download.getPsdId()));
								}
								portalBoxLogMapper.insertSelective(portalBoxLog);

								PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(download.getPrId()));
								
								if(portalResource==null) {
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
									fos.close();
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
									
									/**
									 * 下載成功
									 */
									PortalResourceDownload download1 = new PortalResourceDownload();
									download1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
									download1.setStat(1);
									download1.setDownloadTime(new Date());
									download1.setDownloadType(DownloadType.vDownload.name());
									portalResourceDownloadMapper.updateByPrimaryKeySelective(download1);
									
									/**
									 * 取用
									 * insert ulog_api sp 4 授權狀態 1.登入(AS) 2.授權(AS) 3.登出(AS) 4. 要求(SP) 5. 傳送(DP)
									 * 6.儲存(SP) 7.取消授權(AS) 8.取用
									 * 
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
									String scopeStr1 = "";
									try {
										Map<String,Object> sparam = new HashMap<String,Object>();
										sparam.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
										List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
										if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
											for (PortalResourceScope s : portalResourceScopeList) {
												scopeStr1 = scopeStr1 + s.getScope() + " ";
											}
											scopeStr1 = scopeStr1.trim();
										}
										
									} catch (Exception ex1) {
									}

									/**
									 * 寄信通知
									 */
									PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
									Member member = null;
									try {
										if(download != null) {
											Map<String,Object> param2 = new HashMap<String,Object>();
											param2.put("account", ValidatorHelper.removeSpecialCharacters(download.getProviderKey()));
											List<Member> memberList = memberMapper.selectByExample(param2);
											if(memberList != null && memberList.size() > 0) {
												member = memberList.get(0);
											}
										}
										ulogUtil.recordFullByPrUseMember(member, null, null, portalResource, download.getTransactionUid(), ActionEvent.EVENT_340, scopeStr1, 16, ip);
										ulogUtil.recordFullByPrUseMember(member, null, null, portalResource, download.getTransactionUid(), ActionEvent.EVENT_350, scopeStr1, null, ip);
										ulogUtil.recordFullByPrUseMember(member, null, null, portalResource, download.getTransactionUid(), null, scopeStr1, 35, ip);
										if(member != null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
											String from = "mydata_system@ndc.gov.tw";
											String title = "【個人化資料自主運用(MyData)平臺】個人資料取用通知信（系統信件）";
											String content = "您好：\n\n"
													+ "感謝您使用個人化資料自主運用(MyData)平臺，您已於"+sdf8.format(new Date())+"成功完成臨櫃核驗。\n"
													+ "\n" 
													+ "本次提供之資料如下：\n"
													+ portalResource.getName()+"（資料提供單位："+ portalProvider.getName() +"）\n" 
													+ "\n"
													+ "此為系統信件，請勿回信。\n"
													+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
													+ "\n"
													+ "——-\n"
													+ "我為什麼會收到這封信？\n"
													+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺轉存或傳輸您的個人資料給上述單位，當您轉存或當服務單位收到您的個人資料，系統會自動發此信通知您。\n"
													+ "——-\n"
													+ "非本人？\n"
													+ "如非您本人同意傳輸或轉存資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
											List<String> tmpReveicers = new ArrayList<String>();
											tmpReveicers.add(member.getEmail());
											MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
											sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
										} else if (member!=null&&StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
											String smbody = "MyData通知-您已於"+sdf8.format(new Date())+"完成「"+portalResource.getName()+"」臨櫃核驗作業";
						                    SMSUtil.sendSms(member.getMobile(), smbody);
											sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
										} else {
											System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
										}
									}catch(Exception ex) {
										System.out.println("--寄信失敗--:\n"+ex);
									}
									
									//如果成功，重新計算鎖住
									portalBoxLockCheckMapper.deleteByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));

									File targetFile = filterDpFile(filename1enc);
									response.setHeader("Content-Type", "application/force-download");
									response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".zip");
									InputStream file_input = null;
									try {
										file_input = new FileInputStream(targetFile);
										org.apache.commons.io.IOUtils.copy(file_input, response.getOutputStream());
										response.getOutputStream().flush();
										response.getOutputStream().close();
										localFile.delete();
										filename1enc.delete();
										targetFile.delete();
										response.flushBuffer();										
									}finally {
										if(file_input!=null) {
											HttpClientHelper.safeClose(file_input);
										}
									}
									/**
									 * DELETE FTP FILE
									 */
									ulogUtil.recordFullByPrUseMember(member, null, null, portalResource, download.getTransactionUid(), null, scopeStr1, 34, ip);
									try {
										client.connect(ftpHost, 21);
										client.login(ftpUsername, ftpPassword);
										client.setFileType(FTP.BINARY_FILE_TYPE);
										client.changeWorkingDirectory("/mydata");
										client.deleteFile(ValidatorHelper.removeSpecialCharacters(download.getFiles()));
										client.logout();
										client.disconnect();
										
										PortalResourceDownload tmpprd = new PortalResourceDownload();
										tmpprd.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
										tmpprd.setDtime(new Date());
										portalResourceDownloadMapper.updateByPrimaryKeySelective(tmpprd);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}else {
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
									fos.close();
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
									
									/**
									 * 下載成功
									 */
									PortalResourceDownload  download1 = new PortalResourceDownload();
									download1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
									download1.setStat(1);
									download1.setDownloadTime(new Date());
									download1.setDownloadType(DownloadType.vDownload.name());
									portalResourceDownloadMapper.updateByPrimaryKeySelective(download1);
									
									/**
									 * 取用
									 * insert ulog_api sp 4 授權狀態 1.登入(AS) 2.授權(AS) 3.登出(AS) 4. 要求(SP) 5. 傳送(DP)
									 * 6.儲存(SP) 7.取消授權(AS) 8.取用
									 * 
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
									 * 寄信通知
									 */
									PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
									try {
										Member member = null;
										if(download != null) {
											Map<String,Object> param2 = new HashMap<String,Object>();
											param2.put("account", ValidatorHelper.removeSpecialCharacters(download.getProviderKey()));
											List<Member> memberList = memberMapper.selectByExample(param2);
											if(memberList != null && memberList.size() > 0) {
												member = memberList.get(0);
											}
										}
										ulogUtil.recordFullByPrUseMember(member, null, null, portalResource, download.getTransactionUid(), ActionEvent.EVENT_340, scopeStr1, 16, ip);
										ulogUtil.recordFullByPrUseMember(member, null, null, portalResource, download.getTransactionUid(), ActionEvent.EVENT_350, scopeStr1, null, ip);
										ulogUtil.recordFullByPrUseMember(member, null, null, portalResource, download.getTransactionUid(), null, scopeStr1, 35, ip);
										if(member != null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
											String from = "mydata_system@ndc.gov.tw";
											String title = "【個人化資料自主運用(MyData)平臺】個人資料取用通知信（系統信件）";
											String content = "您好：\n\n"
													+ "感謝您使用個人化資料自主運用(MyData)平臺，您已於"+sdf8.format(new Date())+"成功完成臨櫃核驗。\n"
													+ "\n" 
													+ "本次提供之資料如下：\n"
													+ portalResource.getName()+"（資料提供單位："+ portalProvider.getName() +"）\n" 
													+ "\n"
													+ "此為系統信件，請勿回信。\n"
													+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
													+ "\n"
													+ "——-\n"
													+ "我為什麼會收到這封信？\n"
													+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺轉存或傳輸您的個人資料給上述單位，當您轉存或當服務單位收到您的個人資料，系統會自動發此信通知您。\n"
													+ "——-\n"
													+ "非本人？\n"
													+ "如非您本人同意傳輸或轉存資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
											List<String> tmpReveicers = new ArrayList<String>();
											tmpReveicers.add(member.getEmail());
											MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
											sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
										} else if (member!=null&&StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
											String smbody = "MyData通知-您已於"+sdf8.format(new Date())+"完成「"+portalResource.getName()+"」臨櫃核驗作業";
						                    SMSUtil.sendSms(member.getMobile(), smbody);
											sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
										} else {
											System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
										}
									}catch(Exception ex) {
										System.out.println("--寄信失敗--:\n"+ex);
									}
									
									/**
									 * 解壓
									 */
//									File filename1encpwd = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"decpwd."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
//									String tmpRandomId = SequenceHelper.createUUID();
//									File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+tmpRandomId);
//									if(!newpath.exists()) {
//										newpath.mkdirs();
//									}
//									unzip(filename1enc,newpath);
//									ArrayList<File> sources = new ArrayList<File>();
//									File[] allFiles = newpath.listFiles();
//									if(allFiles!=null&&allFiles.length>0) {
//										for(File f:allFiles) {
//											if(f.isDirectory()) {
//												//UNDO
//											}else {
//												if(FilenameUtils.getExtension(f.getAbsolutePath()).equalsIgnoreCase("pdf")) {
//													sources.add(f);
//												}
//											}
//										}
//									}
//									//packZipWithPassword(filename1encpwd,sources,SessionMember.getSessionMemberToMember(sr.getMember()).getUid());
//									packZip(filename1encpwd,sources);
									File targetFile = filterDpFile(filename1enc);
									response.setHeader("Content-Type", "application/force-download");
									response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".zip");
									InputStream file_input = null;
									try {
										file_input = new FileInputStream(targetFile);
										org.apache.commons.io.IOUtils.copy(file_input, response.getOutputStream());
										response.getOutputStream().flush();
										response.getOutputStream().close();
										localFile.delete();
										filename1enc.delete();
										targetFile.delete();
										response.flushBuffer();					
									}finally {
										if(file_input!=null) {
											HttpClientHelper.safeClose(file_input);
										}
									}
									
									/**
									 * DELETE FTP FILE
									 */
									ulogUtil.recordFullByPr(sr, null, null, portalResource, download.getTransactionUid(), null, scopeStr1, 34, ip);
									try {
										client.connect(ftpHost, 21);
										client.login(ftpUsername, ftpPassword);
										client.setFileType(FTP.BINARY_FILE_TYPE);
										client.changeWorkingDirectory("/mydata");
										client.deleteFile(ValidatorHelper.removeSpecialCharacters(download.getFiles()));
										client.logout();
										client.disconnect();
										
										PortalResourceDownload tmpprd = new PortalResourceDownload();
										tmpprd.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
										tmpprd.setDtime(new Date());
										portalResourceDownloadMapper.updateByPrimaryKeySelective(tmpprd);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								// 查無下載檔案
								response.sendRedirect(frontendContextUrl + "/organ/error3");
							}	
						}

					} else {
						// 您輸入的驗證序號有誤
						response.sendRedirect(frontendContextUrl + "/organ/error2");
					}
				}else {
					// 您輸入的驗證序號有誤
					response.sendRedirect(frontendContextUrl + "/organ/error2");
				}
			}
		}else {
			// 我不是機器人驗證碼錯誤
			response.sendRedirect(frontendContextUrl + "/organ/error5");
		}

	}
	
	
	/**
	 * 預覽(取回MyBox雲端資料)----本人
	 * 
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/box/prview/{id}")
	public void postBoxPreview(@PathVariable("id") Integer boxId, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if (sr != null) {
			System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			PortalBoxExt portalBoxExt = portalResourceExtMapper.selectMyBoxById(boxId);
			if (SessionMember.getSessionMemberToMember(sr.getMember()).getAccount().equalsIgnoreCase(portalBoxExt.getProviderKey())) {
				PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalBoxExt.getPrId()));
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
					fos.close();
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
					File pdfFile = null;
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
					if(pdfFile!=null) {
						response.setHeader("Content-Type", "application/pdf");
						response.setHeader("Content-Disposition", "inline; filename=" + ValidatorHelper.removeSpecialCharacters(pdfFile.getName()));
						InputStream file_input = null;
						try {
							file_input = new FileInputStream(pdfFile);
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
	}

	
	
	/**
	 * 預覽(取回MyBox雲端資料)----代辦人
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/box/agentPreview/{id}/{prId}/{randomVar}")
	public void postBoxAgentPreview(@PathVariable("id") Integer boxId, 
			@PathVariable("prId") Integer prId,
			@PathVariable("randomVar") String randomVar, 
			HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		boolean canRun = true;
		if(boxId==null||prId==null||randomVar==null||randomVar.trim().length()==0) {
			canRun = false;
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			String out_str = "無資料";
			InputStream stream = new ByteArrayInputStream(out_str.getBytes(StandardCharsets.UTF_8));
			org.apache.commons.io.IOUtils.copy(stream, response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
			response.flushBuffer();
		}		
		PortalBoxExt portalBoxExt = portalResourceExtMapper.selectMyBoxById(boxId);
		if(canRun && portalBoxExt==null) {
			canRun = false;
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			String out_str = "無資料";
			InputStream stream = new ByteArrayInputStream(out_str.getBytes(StandardCharsets.UTF_8));
			org.apache.commons.io.IOUtils.copy(stream, response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
			response.flushBuffer();
		}
		PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
		if(canRun && portalResource==null) {
			canRun = false;
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			String out_str = "無資料";
			InputStream stream = new ByteArrayInputStream(out_str.getBytes(StandardCharsets.UTF_8));
			org.apache.commons.io.IOUtils.copy(stream, response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
			response.flushBuffer();
		}
		String sessionRandomVar = (String)session.getAttribute("randomVar"+prId);
		if(canRun && !randomVar.equalsIgnoreCase(sessionRandomVar)) {
			canRun = false;
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			String out_str = "無資料";
			InputStream stream = new ByteArrayInputStream(out_str.getBytes(StandardCharsets.UTF_8));
			org.apache.commons.io.IOUtils.copy(stream, response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
			response.flushBuffer();
		}
		if(canRun) {
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
				fos.close();
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

				/**
				 * 代辦人
				 * 54 臨櫃核驗-預覽：您完成提供資料給臨櫃核驗機關
				 */
				
				PortalServiceDownload psd = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(download.getPsdId()));
				PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psd.getPsId()));
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("psdId", ValidatorHelper.limitNumber(download.getPsdId()));
				param.put("ctimeDesc", true);
				List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param);
				PortalBox parentBox = null;
				if(portalBoxList!=null && portalBoxList.size()>0) {
					parentBox = portalBoxList.get(0);
				}
				ulogUtil.recordFullByBoxAgent(parentBox, ps, psd.getTxId(), portalResource, download, null, null, 54, HttpHelper.getRemoteIp(request));
				
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
				File pdfFile = null;
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
				if(pdfFile!=null) {
					response.setHeader("Content-Type", "application/pdf");
					response.setHeader("Content-Disposition", "inline; filename=" + ValidatorHelper.removeSpecialCharacters(pdfFile.getName()));
					InputStream file_input = null;
					try {
						file_input = new FileInputStream(pdfFile);
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
	
	
	/**
	 * 取資料(取回MyBox雲端資料)----本人
	 * 
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/box/download/{id}")
	public void postBoxDownload(@PathVariable("id") Integer boxId, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		String accessToken = TokenUtils.getFullAccessToken(sr.getAuthToken());
		System.out.println("accessToken......:" + accessToken);

		String ip = HttpHelper.getRemoteIp(request);
		if (sr != null) {
			System.out.println(
					"account=" +SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			PortalBoxExt portalBoxExt = portalResourceExtMapper.selectMyBoxById(boxId);
			if (SessionMember.getSessionMemberToMember(sr.getMember()).getAccount().equalsIgnoreCase(portalBoxExt.getProviderKey())) {
				PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalBoxExt.getPrId()));
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("downloadSn",ValidatorHelper.removeSpecialCharacters(portalBoxExt.getDownloadSn()));
				List<PortalResourceDownload> downloadList = portalResourceDownloadMapper.selectByExample(param1);
				PortalResourceDownload download = null;
				if (downloadList != null && downloadList.size() > 0) {
					download = downloadList.get(0);
				}
				if (download != null) {
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
					fos.close();
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
					
					/**
					 * 下載成功
					 */
					PortalResourceDownload download1 = new PortalResourceDownload();
					download1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
					download1.setStat(1);
					download1.setDownloadType(DownloadType.download.name());
					download1.setDownloadTime(new Date());
					portalResourceDownloadMapper.updateByPrimaryKeySelective(download1);
					
					/**
					 * 取用
					 * insert ulog_api sp 4 授權狀態 1.登入(AS) 2.授權(AS) 3.登出(AS) 4. 要求(SP) 5. 傳送(DP)
					 * 6.儲存(SP) 7.取消授權(AS) 8.取用
					 * 
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
					//ulogUtil.recordFullByPr(sr, null, null, portalResource, download.getTransactionUid(), ActionEvent.EVENT_330, scopeStr1, null, ip);
					ulogUtil.recordFullByPr(sr, null, null, portalResource, download.getTransactionUid(), ActionEvent.EVENT_340, scopeStr1, 15, ip);
					ulogUtil.recordFullByPr(sr, null, null, portalResource, download.getTransactionUid(), ActionEvent.EVENT_350, scopeStr1, null, ip);
					
					/**
					 * 寄信通知
					 */
					PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
					try {
						Member member = null;
						if(download != null) {
							Map<String,Object> param2 = new HashMap<String,Object>();
							param2.put("account", ValidatorHelper.removeSpecialCharacters(download.getProviderKey()));
							List<Member> memberList = memberMapper.selectByExample(param2);
							if(memberList != null && memberList.size() > 0) {
								member = memberList.get(0);
							}
						}

						ulogUtil.recordFullByPr(sr, null, null, portalResource, download.getTransactionUid(), null, scopeStr1, 35, ip);
						if(member != null && StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
							String from = "mydata_system@ndc.gov.tw";
							String title = "【個人化資料自主運用(MyData)平臺】個人資料取用通知信（系統信件）";
							String content = "您好：\n\n"
									+ "感謝您使用個人化資料自主運用(MyData)平臺，您已於"+sdf8.format(new Date())+"成功完成個人資料下載。\n"
									+ "\n" 
									+ "本次轉存之資料如下：\n"
									+ portalResource.getName()+"（資料提供單位："+ portalProvider.getName() +"）\n" 
									+ "\n"
									+ "此為系統信件，請勿回信。\n"
									+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
									+ "\n"
									+ "——-\n"
									+ "我為什麼會收到這封信？\n"
									+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺下載或傳輸您的個人資料給上述單位，當您下載或當服務單位收到您的個人資料，系統會自動發此信通知您。\n"
									+ "——-\n"
									+ "非本人？\n"
									+ "如非您本人同意傳輸或下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
							List<String> tmpReveicers = new ArrayList<String>();
							tmpReveicers.add(member.getEmail());
							MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
							sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
						} else if (member!=null&&StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
							String smbody = "MyData通知-您已於"+sdf8.format(new Date())+"轉存「"+portalResource.getName()+"」資料";
		                    SMSUtil.sendSms(member.getMobile(), smbody);
							sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
						} else {
							System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
						}
					}catch(Exception ex) {
						System.out.println("--寄信失敗--:\n"+ex);
					}


					/**
					 * 解壓
					 */
					File filename1encpwd = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"decpwd."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
					String tmpRandomId = SequenceHelper.createUUID();
					File newpath = new File(ftpPath+File.separator+sdf6.format(new Date())+File.separator+tmpRandomId);
					if(!newpath.exists()) {
						newpath.mkdirs();
					}
					unzip(filename1enc,newpath);
					ArrayList<File> sources = new ArrayList<File>();
					File[] allFiles = newpath.listFiles();
					if(allFiles!=null&&allFiles.length>0) {
						for(File f:allFiles) {
//							if(f.isDirectory()) {
//								File[] nextFiles = f.listFiles();
//								if(nextFiles!=null&&nextFiles.length>0) {
//									for(File f1:nextFiles) {
//										if(f1.isDirectory()) {
//											//UNDO
//										}else {
//											/**
//											 * add to zip file
//											 */
//											sources.add(f1);
//										}
//									}
//								}
//							}else {
//								/**
//								 * add to zip file
//								 */
//								sources.add(f);
//							}
							if(f.isDirectory()) {
								//UNDO
							}else {
								if(FilenameUtils.getExtension(f.getAbsolutePath()).equalsIgnoreCase("pdf")) {
									sources.add(f);
								}
							}
						}
					}
					//packZipWithPassword(filename1encpwd,sources,SessionMember.getSessionMemberToMember(sr.getMember()).getUid());
					packZip(filename1encpwd,sources);
					response.setHeader("Content-Type", "application/force-download");
					response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".zip");
					InputStream file_input = null;
					try {
						file_input = new FileInputStream(filename1encpwd);
						org.apache.commons.io.IOUtils.copy(file_input, response.getOutputStream());
						response.getOutputStream().flush();
						response.getOutputStream().close();
						localFile.delete();
						filename1enc.delete();
						filename1encpwd.delete();
						response.flushBuffer();						
					}finally {
						if(file_input!=null) {
							HttpClientHelper.safeClose(file_input);
						}
					}
					
					/**
					 * DELETE FTP FILE
					 */
					ulogUtil.recordFullByPr(sr, null, null, portalResource, download.getTransactionUid(), null, scopeStr1, 34, ip);
					try {
						client.connect(ftpHost, 21);
						client.login(ftpUsername, ftpPassword);
						client.setFileType(FTP.BINARY_FILE_TYPE);
						client.changeWorkingDirectory("/mydata");
						client.deleteFile(ValidatorHelper.removeSpecialCharacters(download.getFiles()));
						client.logout();
						client.disconnect();
						
						PortalResourceDownload tmpprd = new PortalResourceDownload();
						tmpprd.setDownloadSn(ValidatorHelper.removeSpecialCharacters(download.getDownloadSn()));
						tmpprd.setDtime(new Date());
						portalResourceDownloadMapper.updateByPrimaryKeySelective(tmpprd);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * (非My Box式下載)
	 * 
	 * @param prId
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/preview/{prId}")
	public void previewFileNotFromMyBox(@PathVariable("prId") Integer prId, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));

		if (sr != null) {
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
				fos.close();
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
				/**
				 * 下載成功(preview不變stat)
				 */
				//PortalResourceDownload record = new PortalResourceDownload();
				//record.setStat(1);
				//portalResourceDownloadMapper.updateByExampleSelective(record, portalResourceDownloadExample);
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
					File pdfFile = null;
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

					if(pdfFile!=null) {
						response.setHeader("Content-Type", "application/pdf");
						response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
						InputStream file_input = null;
						try {
							file_input = new FileInputStream(pdfFile);
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
						File pdfFile = null;
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
						if(pdfFile!=null) {
							response.setHeader("Content-Type", "application/pdf");
							response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
							InputStream file_input = null;
							try {
								file_input = new FileInputStream(pdfFile);
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
					}else {
						response.setHeader("Content-Type", "application/pdf");
						response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
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
	}
	
	
	/**
	 * 預覽：線上預覽檔案(SP) SP service-detail.html service-detail-new.html preview
	 * @param prId
	 * @param request
	 * @param response
	 * @throws SocketException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws ZipException
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	@GetMapping("/sppreview/{prId}/{downloadSn}")
	public void previewFileNotFromSpDetail(@PathVariable("prId") Integer prId, @PathVariable("downloadSn") String downloadSn, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException, BadPaddingException, IllegalBlockSizeException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
		if (sr != null) {
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
			fos.close();
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
				File pdfFile = null;
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

				if(pdfFile!=null) {
					response.setHeader("Content-Type", "application/pdf");
					response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
					InputStream file_input = null;
					try {
						file_input = new FileInputStream(pdfFile);
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
					File pdfFile = null;
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
					if(pdfFile!=null) {
						response.setHeader("Content-Type", "application/pdf");
						response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
						InputStream file_input = null;
						try {
							file_input = new FileInputStream(pdfFile);
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
				}else {
					response.setHeader("Content-Type", "application/pdf");
					response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + URLEncoder.encode(ValidatorHelper.removeSpecialCharacters(portalResource.getName()),"UTF-8")+sdf3.format(new Date())+".pdf");
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
	
	
	/**
	 * GSP OpenID Connect的登入授權網址。
	 *            指定授權項目
	 * @param nonce
	 *            備註資訊
	 * @param state
	 *            狀態資訊
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
		InputStreamReader ins = null;
		StringBuilder sb = new StringBuilder();
		try {
			ins = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(ins);
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();

		}finally {
			if(ins!=null) {
				HttpClientHelper.safeClose(ins);
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
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		//parameters.setEncryptFiles(true);
		//parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		//parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		zipFile.addFiles(sources, parameters);
		return output;
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
		/**
		 * 只針對「資料下載」、「多筆下載」、「條碼下載區」、「臨櫃核驗」
		 * 
		 */
		if(frontendContextUrl.equalsIgnoreCase("https://mydatadev.nat.gov.tw/mydata")) {
			parameters.setPassword("A999999999");
		}else {
			parameters.setPassword(password);
		}
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
	
	private boolean checkIntesting(String ip) {
		/**
		* 聯經測試
		* 211.72.73.40
		* 60.250.133.153
		* 60.250.134.87
		* 60.250.135.18
		* 60.250.72.157
		* 60.251.132.249
		* 60.248.31.111
		* 60.248.110.163
		* 203.69.6.99
		* 123.51.216.94
		* 123.51.216.88
		* 211.72.195.139
		* 211.72.195.140
		* 127.0.0.1
		* 0:0:0:0:0:0:0:1
		*/
		System.out.println("ip="+ip);
		List<String> intestIpList = new ArrayList<String>();
		intestIpList.add("211.72.73.40");
		intestIpList.add("60.250.133.153");
		intestIpList.add("60.250.134.87");
		intestIpList.add("60.250.135.18");
		intestIpList.add("60.250.72.157");
		intestIpList.add("60.251.132.249");
		intestIpList.add("60.248.31.111");
		intestIpList.add("60.248.110.163");
		intestIpList.add("203.69.6.99");
		intestIpList.add("123.51.216.94");
		intestIpList.add("123.51.216.88");
		intestIpList.add("211.72.195.139");
		intestIpList.add("211.72.195.140");
		intestIpList.add("127.0.0.1");
		intestIpList.add("0:0:0:0:0:0:0:1");
		if(intestIpList.contains(ip)) {
			return true;
		}else {
			return false;
		}
	}
	
	private String replaceParamStrForPost(String paramstr, TokenEntity tokenEntity, UserInfoEntity userInfoEntity) {
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
					forReplaceStr = "\"" + tokenEntity.getAccessToken() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = "\"" + userInfoEntity.getUid() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = "\"" + Base64.getUrlEncoder().encodeToString(userInfoEntity.getUid().getBytes("UTF-8")) + "\"";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(userInfoEntity.getUid());
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
					try {
						date1 = sdf2.parse(userInfoEntity.getBirthdate());
						datefinalStr = sdf2.format(date1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					forReplaceStr = "\"" + datefinalStr + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = "\"" + userInfoEntity.getName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + userInfoEntity.getEmail() + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}
	
	private String replaceParamStrForPostNotAppendEscap(String paramstr, TokenEntity tokenEntity, UserInfoEntity userInfoEntity) {
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
					forReplaceStr = tokenEntity.getAccessToken();
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}
	
	private String replaceParamStrForPostAndParam(String paramstr, TokenEntity tokenEntity, UserInfoEntity userInfoEntity,Map<String,String> paramMap) {
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
					forReplaceStr = "\"" + tokenEntity.getAccessToken() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = "\"" + userInfoEntity.getUid() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = "\"" + Base64.getUrlEncoder().encodeToString(userInfoEntity.getUid().getBytes("UTF-8")) + "\"";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(userInfoEntity.getUid());
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
					try {
						date1 = sdf2.parse(userInfoEntity.getBirthdate());
						datefinalStr = sdf2.format(date1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					forReplaceStr = "\"" + datefinalStr + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = "\"" + userInfoEntity.getName() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = "\"" + userInfoEntity.getEmail() + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("lprNumber")) {
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

	private String replaceParamStrForPostAndParam1Param2Param3(String paramstr, TokenEntity tokenEntity, UserInfoEntity userInfoEntity,Map<String,String> paramMap) {
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
					forReplaceStr =  tokenEntity.getAccessToken();
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
	
	private String replaceParamStrForGet(String paramstr, TokenEntity tokenEntity, UserInfoEntity userInfoEntity) {
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
					forReplaceStr = tokenEntity.getAccessToken();
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = userInfoEntity.getUid();
				}
				if (matchInnerStr.equalsIgnoreCase("base64Uid")) {
					try {
						forReplaceStr = Base64.getUrlEncoder().encodeToString(userInfoEntity.getUid().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (matchInnerStr.equalsIgnoreCase("idtype")) {
					String idtype = "idNo";
					String patternStr1 = "[A-Z]{1}[0-9]{9}";
					Pattern pattern1 = Pattern.compile(patternStr1);
					Matcher matcher1 = pattern1.matcher(userInfoEntity.getUid());
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
					try {
						date1 = sdf2.parse(userInfoEntity.getBirthdate());
						datefinalStr = sdf2.format(date1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					forReplaceStr = datefinalStr;
				}
				if (matchInnerStr.equalsIgnoreCase("name")) {
					forReplaceStr = userInfoEntity.getName();
				}
				if (matchInnerStr.equalsIgnoreCase("email")) {
					forReplaceStr = userInfoEntity.getEmail();
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

	public boolean isDuplicateDownloadVerify(String tmpDownloadVerify) {
		boolean ret = false;
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
		Date startTime = cal.getTime();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("downloadVerify", tmpDownloadVerify);
		param.put("sCtime", startTime);
		param.put("ctimeDesc", true);
		List<PortalBox> conDownloadVerifyList = portalBoxMapper.selectByExample(param);
		if (conDownloadVerifyList != null && conDownloadVerifyList.size() > 0) {
			ret = true;
		}
		return ret;
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
	
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;
	    for (NameValuePair pair : params){
	        if (first) {
	        		first = false;
	        }else {
	        		result.append("&");
	        }
	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }
	    return result.toString();
	}
	
	/**
	 * for JSONParser orderedKeyFactory
	 */
	private ContainerFactory orderedKeyFactory = new ContainerFactory(){
	    public List creatArrayContainer() {
	      return new LinkedList();
	    }
	    public Map createObjectContainer() {
	      return new LinkedHashMap();
	    }
	};
	
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
                    FileOutputStream fos = null;
        			try {
                        if(!entryFile.isDirectory()) {
                        	fos = new FileOutputStream(entryFile);
            	            bos = new BufferedOutputStream(fos);
            	            bis = new BufferedInputStream(zip.getInputStream(entry));
            	            while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
            	                bos.write(buffer, 0, count);
            	            }
                        }
                        try {
                        	if(bos!=null) {
                	            bos.flush();
                	            bos.close();	
                        	}
                        }catch(Exception ex) {
                        	System.out.println(ex);
                        }    				
        			}finally {
        				if(fos!=null) {
        					HttpClientHelper.safeClose(fos);
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
	
    public static File [] unzip(File zipFile, File dest, String passwd) throws ZipException {  
        ZipFile zFile = new ZipFile(zipFile);  
        zFile.setFileNameCharset("UTF-8");  
        if (!zFile.isValidZipFile()) {  
            throw new ZipException("壓縮檔案不合法,可能被損壞.");  
        }  
        File destDir = dest;  
        if (destDir.isDirectory() && !destDir.exists()) {  
            destDir.mkdir();  
        }  
        if (zFile.isEncrypted()) {  
            zFile.setPassword(passwd.toCharArray());  
        }  
        zFile.extractAll(ValidatorHelper.removeSpecialCharacters(dest.getAbsolutePath()));
          
        List<FileHeader> headerList = zFile.getFileHeaders();  
        List<File> extractedFileList = new ArrayList<File>();  
        for(FileHeader fileHeader : headerList) {  
            if (!fileHeader.isDirectory()) {  
                extractedFileList.add(new File(destDir,fileHeader.getFileName()));  
            }  
        }  
        File [] extractedFiles = new File[extractedFileList.size()];  
        extractedFileList.toArray(extractedFiles);  
        return extractedFiles;  
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
	
	private void checkPortalBoxLockCheck(String ip) {
		System.out.println("==checkPortalBoxLockCheck ip==:"+ip);
		//if(ip!=null&&ip.trim().length()>0&&!ip.equalsIgnoreCase("127.0.0.1")) {
		if(ip!=null&&ip.trim().length()>0) {
			PortalBoxLockCheck portalBoxLockCheck= portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			if(portalBoxLockCheck!=null) {
				Date now = new Date();
				Date ctime = portalBoxLockCheck.getCtime();
				long nowTime = now.getTime();
				long ctimeTime = ctime.getTime();
				//如果超過10分鐘，原紀錄不理會，重新記數
				if(nowTime>(ctimeTime+(15*60*1000))) {
					PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
					portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(portalBoxLockCheck.getIp()));
					portalBoxLockCheck1.setCtime(new Date());
					portalBoxLockCheck1.setStat(0);
					portalBoxLockCheck1.setCount(1);
					portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
				}else {
					//沒超過10分鐘，次數少4次(沒加本次)
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
	
	private byte[] fetchPortalServiceCbcIv(int psId) {
        if (psId < 0) return "".getBytes();
        PortalService portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psId));
        if (portalService != null) {
            return portalService.getCbcIv().getBytes();
        }
        return "".getBytes();
    }
	
	public static byte[] decrypt_cbc(byte[] data, String key, String ivstr) throws Exception {
		// 密鑰
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
		// 向量鰎值
		IvParameterSpec iv = new IvParameterSpec(ivstr.getBytes("UTF-8"));
		// 實例化
		Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0NCQy9QS0NTNVBBRERJTkc=")));
		// 初始化，設置為解密模式
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		// 執行操作
		return cipher.doFinal(data);
	}
	
	public File filterSpFile(File orifile) throws IOException, ParserConfigurationException, SAXException, ZipException {
		File targetFile = null;
		if(orifile.exists()) {
			File f1Parent = orifile.getParentFile();
			String randomId = SequenceHelper.createUUID();
			File targetDir = new File(f1Parent.getAbsoluteFile()+File.separator+randomId);
			targetFile = new File(f1Parent.getAbsoluteFile()+File.separator+randomId+".zip");
			unzip(orifile,targetDir);
			//manifest.xml
			File mf = new File(targetDir.getAbsoluteFile()+File.separator+"META-INFO"+File.separator+"manifest.xml");
			if(mf.exists()) {
				DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		        String feature = null;
		        feature = "http://apache.org/xml/features/disallow-doctype-decl";
		        df.setFeature(feature, true);
		        feature = "http://xml.org/sax/features/external-general-entities";
		        df.setFeature(feature, false);
		        feature = "http://xml.org/sax/features/external-parameter-entities";
		        df.setFeature(feature, false);
		        feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
		        df.setFeature(feature, false);
		        df.setXIncludeAware(false);
		        df.setExpandEntityReferences(false);
				DocumentBuilder dfb = df.newDocumentBuilder();
				org.w3c.dom.Document doc = dfb.parse(mf.getAbsoluteFile());
				NodeList nList = doc.getElementsByTagName("file");
				ArrayList<File> sources = new ArrayList<>();
				ArrayList<File> manifestSource = new ArrayList<>();
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
						File tof = new File(targetDir.getAbsoluteFile()+File.separator+getTagValue("resource_id", eElement)+".zip");
						File tnf = new File(targetDir.getAbsoluteFile()+File.separator+getTagValue("resource_name", eElement)+".zip");
						File uziptnfDir = new File(tnf.getParentFile()+File.separator+getTagValue("resource_name", eElement));
						File uziptnf =  new File(tnf.getParentFile()+File.separator+getTagValue("resource_name", eElement)+".zip");
						ArrayList<File> dpsources = new ArrayList<>();
						ArrayList<File> dpmanifestSource = new ArrayList<>();
						if(tof.exists()) {
							unzip(tof,uziptnfDir);
							File[] unzipfiles = uziptnfDir.listFiles();
							if(unzipfiles!=null&&unzipfiles.length>0) {
								for(File f:unzipfiles) {
									if(f.isFile()) {
										String extension = FilenameUtils.getExtension(f.getName());
										if(!extension.equalsIgnoreCase("pdf")) {
											f.deleteOnExit();
										}else {
											dpsources.add(f);
										}
									}else if(f.isDirectory()) {
										if(f.listFiles().length>0) {
											for(File ft:f.listFiles()) {
												dpmanifestSource.add(ft);
											}
										}
									}
								}
							}
							packDpIncludeMetaInfoZip(uziptnf,dpsources,dpmanifestSource);
							sources.add(uziptnf);
						}
					}
				}
				manifestSource.add(mf);
				packDpIncludeMetaInfoZip(targetFile,sources,null);
				FileUtils.deleteDirectory(targetDir);
			}else {
				System.out.println("mf not found!");
			}
		}else {
			System.out.println("orifile not found!");
		}
		return targetFile;
	}
	
	public File filterDpFile(File orifile) throws IOException, ParserConfigurationException, ZipException {
		File targetFile = null;
		if(orifile.exists()) {
			File f1Parent = orifile.getParentFile();
			String randomId = SequenceHelper.createUUID();
			File targetDir = new File(f1Parent.getAbsoluteFile()+File.separator+randomId);
			targetFile = new File(f1Parent.getAbsoluteFile()+File.separator+randomId+".zip");
			unzip(orifile,targetDir);
			File[] unzipfiles = targetDir.listFiles();
			ArrayList<File> dpsources = new ArrayList<>();
			ArrayList<File> dpmanifestSource = new ArrayList<>();
			if(unzipfiles!=null&&unzipfiles.length>0) {
				for(File f:unzipfiles) {
					if(f.isFile()) {
						String extension = FilenameUtils.getExtension(f.getName());
						if(!extension.equalsIgnoreCase("pdf")) {
							f.deleteOnExit();
						}else {
							dpsources.add(f);
						}
					}else if(f.isDirectory()) {
						if(f.listFiles().length>0) {
							for(File ft:f.listFiles()) {
								dpmanifestSource.add(ft);
							}
						}
					}
				}
			}
			packDpIncludeMetaInfoZip(targetFile,dpsources,dpmanifestSource);
			FileUtils.deleteDirectory(targetDir);
		}else {
			System.out.println("orifile not found!");
		}
		return targetFile;
	}	
	
	private static String getTagValue(String sTag, org.w3c.dom.Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

	private static File packDpIncludeMetaInfoZip(File output, ArrayList<File> sources, ArrayList<File> manifestSource)
			throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zipFile.addFiles(sources, parameters);
		if(manifestSource!=null&&manifestSource.size()>0) {
			parameters.setRootFolderInZip("META-INFO");
			zipFile.addFiles(manifestSource, parameters);
		}
		return output;
	}	
}
