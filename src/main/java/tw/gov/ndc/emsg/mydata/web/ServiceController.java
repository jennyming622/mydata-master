package tw.gov.ndc.emsg.mydata.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
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
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.riease.common.enums.ActionEvent;
import com.riease.common.helper.HttpHelper;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.SysCode;
import com.riease.common.util.SslUtils;

import sun.security.pkcs.PKCS7;
import sun.security.util.DerInputStream;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.entity.ext.MemberTempExt;
import tw.gov.ndc.emsg.mydata.entity.ext.PortalCounterSubExt;
import tw.gov.ndc.emsg.mydata.entity.ext.PortalCounterSubScopeExt;
import tw.gov.ndc.emsg.mydata.mapper.*;
import tw.gov.ndc.emsg.mydata.type.RequestType;
import tw.gov.ndc.emsg.mydata.type.SendType;
import tw.gov.ndc.emsg.mydata.type.SystemOptionType;
import tw.gov.ndc.emsg.mydata.util.*;

@Controller
@RequestMapping("/service")
public class ServiceController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
    private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy???MM???dd???HH???mm???");
	private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy/M/d HH:mm");
    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder =  Base64.getDecoder();
	/**
	 * ????????????
	 */
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * ??????/????????????/????????????/???????????? AES/ECB/PKCS5PADDING
	 */
	//public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";
	/**
	 * ??????/????????????/????????????/????????????
	 */
	public static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5PADDING";
	/**
	 * ??????????????????(??????)
	 */
	private static int fileStoreTime = 8;
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
    @Value("${signatureVerifyAPIUrlTwid}")
    private String signatureVerifyAPIUrlTwid;
	@Value("${mail.enable}")
	private String mailEnable;

	@Autowired
	private IcscUtils icscUtils;
    @Autowired
    private TWCAUtils twcaUtils;
    
	private CBCUtil CBCUtil;
	
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
	private PortalServiceScopeMapper portalServiceScopeMapper;
	@Autowired
	private PortalServiceScopeCounterMapper portalServiceScopeCounterMapper;
	@Autowired
	private PortalServiceCategoryMapper portalServiceCategoryMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired
	private PortalResourceFieldMapper portalResourceFieldMapper;
	@Autowired
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	@Autowired
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	@Autowired
	private UlogApiMapper ulogApiMapper;
	@Autowired
	private PortalResourceParamMapper portalResourceParamMapper;
	@Autowired
	private MemberTempMapper memberTempMapper;
	@Autowired 
	private PortalServiceSaltMapper portalServiceSaltMapper;
	@Autowired 
	private PortalServiceAllowIpMapper portalServiceAllowIpMapper;
	@Autowired 
	private PortalServiceStageIpMapper portalServiceStageIpMapper;
	@Autowired
	private UlogUtil ulogUtil;
	@Autowired
    private MemberMapper memberMapper;
	@Autowired
	private MemberPrivacyMapper memberPrivacyMapper;
	@Autowired
	private SendLogUtil sendLogUtil;
    @Autowired
    private MaintainUtils maintainUtils;
    @Autowired
	private PortalCounterSubMapperExt portalCounterSubMapperExt;
    @Autowired
	private SystemOptionUtil systemOptionUtil;
	/**
	 * init for /client
	 */
	private List<PortalResource> allPortalResourceList = null;
	private List<PortalResourceScope> allPortalResourceScopeList = null;
	private List<PortalServiceScope> allPortalServiceScopeList = null;
	private List<PortalServiceScopeCounter> allPortalServiceScopeCounterList = null;
	
    @PostConstruct
    public void initSpController() {
        Map<String, Object> rparam = new HashMap<String, Object>();
        allPortalResourceList = portalResourceMapper.selectByExample(rparam);
        Map<String, Object> sparam = new HashMap<String, Object>();
        allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
        Map<String, Object> pssparam = new HashMap<String, Object>();
        allPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
    	Map<String, Object> psscparam = new HashMap<String, Object>();
    	allPortalServiceScopeCounterList = portalServiceScopeCounterMapper.selectByExample(psscparam);
    }
    
    @Scheduled(fixedRate = 60000)
    public void execute() {
        Map<String, Object> rparam = new HashMap<String, Object>();
        allPortalResourceList = portalResourceMapper.selectByExample(rparam);
        Map<String, Object> sparam = new HashMap<String, Object>();
        allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
        Map<String, Object> pssparam = new HashMap<String, Object>();
        allPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
    	Map<String, Object> psscparam = new HashMap<String, Object>();
    	allPortalServiceScopeCounterList = portalServiceScopeCounterMapper.selectByExample(psscparam);
    }	
	
	@GetMapping("/data")
	public void getServiceDataDownload(HttpServletRequest request, 
			HttpServletResponse response, ModelMap model) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
	    Map<String, String> map = new HashMap<String, String>();
	    Enumeration headerNames = request.getHeaderNames();
	    while (headerNames.hasMoreElements()) {
	        String key = (String) headerNames.nextElement();
	        String value = request.getHeader(key);
	        map.put(key, value);
	    }
	    /**
	     * ????????????IP
	     */
	    boolean checkIpIn = false;
	    String ip = HttpHelper.getRemoteIp(request);
	    Map<String,Object> psparam = new HashMap<String,Object>();
	    List<PortalServiceAllowIp> portalServiceAllowIpList = portalServiceAllowIpMapper.selectByExample(psparam);
	    if(portalServiceAllowIpList!=null&&portalServiceAllowIpList.size()>0) {
	    		for(PortalServiceAllowIp p:portalServiceAllowIpList) {
	    			if(p!=null&&p.getIp()!=null&&p.getIp().trim().equalsIgnoreCase(ip.trim())) {
	    				checkIpIn = true;
	    			}
	    		}
	    }
		String tx_id = ValidatorHelper.removeSpecialCharacters(map.get("tx_id"));
	    if(!checkIpIn) {
	    	logger.info("[{}] 401 Unauthorized and ip is [{}]", tx_id, ip);
	    		/**
	    		 * 401 Unauthorized
	    		 */
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return;
	    }
	    
		/**
		 * ??????permission_ticket
		 */
	    PortalServiceDownload psdbean = null;
		String permission_ticket = ValidatorHelper.removeSpecialCharacters(map.get("permission_ticket"));
	    //System.out.println("=== /service/data permission_ticket ==:"+permission_ticket);
	    //System.out.println("=== ip ==:"+HttpHelper.getRemoteIp(request));
	    if(permission_ticket!=null&&permission_ticket.trim().length()>0) {
			Map<String,Object> psdparam = new HashMap<String,Object>();
			psdparam.put("permissionTicket", permission_ticket);
			if(tx_id!=null&&tx_id.trim().length()>0) {
				psdparam.put("txId", tx_id);
			}
			psdparam.put("ctimeDesc", true);
		    List<PortalServiceDownload> portalServiceDownloadList = portalServiceDownloadMapper.selectByExample(psdparam);
		    //System.out.println("portalServiceDownloadList.size =:"+portalServiceDownloadList.size());
		    if(portalServiceDownloadList!=null&&portalServiceDownloadList.size()>0) {
		    		psdbean = portalServiceDownloadList.get(0);
		    		/**
		    		 * ???????????????????????? stat=1
		    		 * 
		    		 */
		    		if(psdbean.getStat()!=null&&psdbean.getStat().compareTo(1)==0) {
						logger.info("[{}] 201 ????????????????????????", tx_id);
			    		/**
			    		 * 201 ????????????????????????
			    		 */
			    		response.setStatus(HttpServletResponse.SC_CREATED);
			    		return;
		    		}

		    		if(!StringUtils.equals(psdbean.getCode(), "200")) {
						logger.info("[{}] 403 ????????????", tx_id);
						/**
						 * 403 ??? SP-API ??????????????? 200 ??????????????????
						 */
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						return;
					}

					logger.info("[{}] start get data", tx_id);

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
								//????????????????????????
								totalFailCheck = false;
							}
						}
		    			}
		    		}
		    		if(totalFailCheck) {
						logger.info("[{}] 504 ?????????????????????", tx_id);
			    		/**
			    		 * 504 ?????????????????????
			    		 */
			    		response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
			    		return;
		    		}
		    		
		    		Calendar cal = GregorianCalendar.getInstance();
		    		cal.setTime(psdbean.getCtime());
				// ??????????????????24?????????(????????????????????????????????????)
				cal.add(Calendar.HOUR_OF_DAY, fileStoreTime);
				Date endTime = cal.getTime();
		    		if(endTime.after(new Date())) {
		    			long ctime = psdbean.getCtime().getTime();
		    			int waitTime = 0;
		    			/**
		    			 * 1. ???psdbean.getWaitTime()???????????????????????????
		    			 * 2. ???psdbean.getFiles()??????????????????????????????????????????????????????????????????????????????
		    			 */
		    			if(psdbean.getWaitTime().compareTo(Integer.valueOf(0))>0) {
		    				waitTime = psdbean.getWaitTime() * 1000 + 65 * 1000; //??????65???
		    			}else {
		    				waitTime = 0; //??????0???(??????)
		    			}
		    			long nowTime = (new Date()).getTime();
		    			if(nowTime>=(ctime+waitTime)) { 
		    				/**
		    				 * FTPClient 
		    				 * ftp.retrieveFile ????????????
		    				 * ftp.storeFile ????????????
		    				 * ftp.deleteFile()????????????FTP Server?????????
		    				 */
		    		    		try {
		    		    			if(psdbean.getFiles()!=null) {
		    						FTPClient client = new FTPClient();
		    						FileOutputStream fos = null;
		    						//InetAddress ftpServer = InetAddress.getByName(ftpHost);
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
		    						 * localFileName ?????? FTP Server ???  AES/CBC/PKCS5PADDING ?????????
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
			    						String secret = psdbean.getSecretKey();
			    						String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
			    						String payload = "{\"filename\":\""+ psdbean.getClientId()+".zip" +"\",\"data\":\"application/zip;data:"+ encodeFileToBase64Binary(deLocalFileName) +"\"}";
	                                // jwe
	                                String jweStr = JWEUtil.encrypt(payload, secret, iv);

			    						
		    						/**
		    						 * ????????????????????????
		    						 */
	                                PortalServiceDownload psdbean1 = new PortalServiceDownload();
	                                psdbean1.setId(ValidatorHelper.limitNumber(psdbean.getId()));
	                                psdbean1.setStat(1);
	                                psdbean1.setDownloadTime(new Date());
		    						portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean1);
		    						
		    						response.setStatus(HttpServletResponse.SC_OK);
		    						response.setCharacterEncoding("UTF-8");
		    						response.setContentType("application/jwe; charset=UTF-8");
		    						PrintWriter out = response.getWriter();
		    						out.print(jweStr);
		    						out.close();	
			    						
	                                ulogUtil.recordFullByPs(psdbean, null, null, ActionEvent.EVENT_300, null, 27, ip);
	                                ulogUtil.recordFullByPs(psdbean, null, null, ActionEvent.EVENT_340, null, null, ip);
	                                ulogUtil.recordFullByPs(psdbean, null, null, ActionEvent.EVENT_350, null, null, ip);
	                                
			    						/**
			    						 * ????????????
			    						 */
			    						PortalService portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdbean.getPsId()));
			    						PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getProviderId()));

										ulogUtil.recordFullByPs(psdbean, null, null, null, null, 35, ip);
			    						try {
			    							
			    							String from = "mydata_system@ndc.gov.tw";
			    							String title = "??????????????????????????????(MyData)??????????????????????????????????????????????????????";
			    							String content = "?????????\n\n"
			    									+ "??????????????????????????????????????????(MyData)????????????????????????????????????????????????????????????????????????????????????????????????????????????"+sdf8.format(new Date())+"????????????"+portalProvider.getName()+"??-??"+portalService.getName()+"???????????????????????????????????????"+portalProvider.getName()+"???????????????"+portalProvider.getName()+"???????????????????????????\n"
			    									+ "\n" 
			    									+ "??????????????????????????????\n";
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
															ulogUtil.recordFullByPr(sr, portalService, tx_id, portalResource, tmpPortalResourceDownload.getTransactionUid(), null, null, 34, ip);
			    											/**
			    											 * DELETE FTP FILE
			    											 */
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
			    											ulogUtil.recordFullByPs(psdbean, tmpPortalResource, tmpPortalResourceDownload.getTransactionUid(), ActionEvent.EVENT_310, null, 17, ip);
			    											content	= content + tmpPortalResource.getName()+"????????????????????????"+ tmpPortalProvider.getName() +"???\n";
			    											if(!(StringUtils.equals(tmpPortalResourceDownload.getCode(), "200") || StringUtils.equals(tmpPortalResourceDownload.getCode(), "204"))) {
															sendEmail = false;
														}
			    										}
			    									}
			    								}
			    							}
			    							
			    							
			    							if(tmpPortalResourceDownload != null && sendEmail == true ) {
			    								Map<String,Object> param2 = new HashMap<String,Object>();
			    								param2.put("account", ValidatorHelper.removeSpecialCharacters(tmpPortalResourceDownload.getProviderKey()));
			    								List<Member> memberList = memberMapper.selectByExample(param2);
			    								if(memberList != null && memberList.size() > 0) {
			    									Member member = memberList.get(0);
			    									if(StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
			    										//???????????????????????-???????????????????????????????????????????????????????????
						    							//??????????????????????????????24???????????????????????????(02)2182-1313???0800-30-1313???
						    							content = content + "\n?????????"+portalProvider.getName()+"??-??"+portalService.getName()+"???????????????????????????????????????????????????"+portalProvider.getName()+"???????????????????????????\n";
						    							content = content + "\n"
						    									+ "????????????????????????????????????\n"
						    									+ "??????????????????????????????????????????0800-009-868??????????????????????????????mydata@ndc.gov.tw???\n"
						    									+ "\n"
						    									+ "??????-\n"
						    									+ "?????????????????????????????????\n"
						    									+ "?????????????????????????????????????????????????????????????????????????????????????????????????????????(MyData)??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n"
						    									+ "??????-\n"
						    									+ "????????????\n"
						    									+ "??????????????????????????????????????????????????????????????????0800-009-868??????????????????????????????mydata@ndc.gov.tw???\n";
						    							List<String> tmpReveicers = new ArrayList<String>();
						    							tmpReveicers.add(member.getEmail());
						    							MailUtil.sendMail(tmpReveicers,from, title,content,mailEnable);
														sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
				    								} else if (StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
				    									String smbody = "MyData??????-????????????"+portalService.getName()+"?????????????????????" + sdf8.format(new Date()) + "????????????" + portalProvider.getName() + "???";
				    									SMSUtil.sendSms(member.getMobile(), smbody);
														sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
				    								} else {
			    										logger.info("[{}] --????????????--: ???????????????????????????", tx_id);
				    								}
			    								} else {
												logger.info("[{}] --????????????--: ???????????????????????????", tx_id);
			    								}
			    							}
			    						}catch(Exception ex) {
										logger.info("[{}] --????????????--", tx_id);
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
		    						}else {
		    							logger.info("[{}] 429 Too Many Requests-1 retry-after > 10", tx_id);
		    			    			/**
		    			    			 * 429 Too Many Requests
		    			    			 */
		    			    			response.setStatus(429);
		    			    			response.setCharacterEncoding("UTF-8");
			    						response.setContentType("application/json; charset=UTF-8");	  
			    						response.setHeader("Retry-After", String.valueOf(10));
		    						}
		    		    			}else {
		    		    				logger.info("[{}] 429 Too Many Requests-2 retry-after > 10", tx_id);
		    			    			/**
		    			    			 * 429 Too Many Requests
		    			    			 */
		    			    			response.setStatus(429);
		    			    			response.setCharacterEncoding("UTF-8");
		    			    			response.setContentType("application/json; charset=UTF-8");	  
		    			    			response.setHeader("Retry-After", String.valueOf(10));
		    		    			}	    					
		    		    		}catch (Exception ex) {
		    		    			logger.info("[{}] 429 Too Many Requests-3 retry-after > 10", tx_id);
		    		    			logger.error(ex.getLocalizedMessage(), ex);
		    		    			/**
		    		    			 * 429 Too Many Requests
		    		    			 */
		    		    			response.setStatus(429);
		    		    			response.setCharacterEncoding("UTF-8");
		    		    			response.setContentType("application/json; charset=UTF-8");	  
		    		    			response.setHeader("Retry-After", String.valueOf(10));
		    		    		}
		    			}else {
		    				long interval = ((ctime+waitTime+10*1000)-nowTime)/1000; //??????30 sec
		    				if(interval<10) {
		    					interval = 10;
		    				}
							logger.info("[{}] 429 Too Many Requests-4 retry-after > {}", tx_id, ValidatorHelper.limitNumber(interval));
	    	    			/**
	    	    			 * 429 Too Many Requests
	    	    			 */
		    	    		response.setStatus(429);
		    	    		response.setCharacterEncoding("UTF-8");
		    				response.setContentType("application/json; charset=UTF-8");	  
		    				response.setHeader("Retry-After", String.valueOf(ValidatorHelper.limitNumber(interval)));
		    			}
		    		}else {
						logger.info("[{}] 403 Forbidden", tx_id);
			    		/**
			    		 * 403 Forbidden
			    		 */
			    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		    		}
		    }else {
				logger.info("[{}] 403 permission_ticket ?????????????????????????????????", tx_id);
		    		/**
		    		 * ?????? 403 permission_ticket ??????????????????
		    		 */
		    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		    }
	    }else {
			logger.info("[{}] 400 permission_ticket ?????????", tx_id);
	    		/**
	    		 *?????? ->?????? 400 permission_ticket ?????????
	    		 */
	    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    }
	}

    /**
     * ??????uid,permission_ticket?????????????????????
     *
     * @param request
     * @param response
     * @param model
     */
    @GetMapping("/uid_valid")
    public void postUidValid(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        /**
         * ????????????IP
         */
	    boolean checkIpIn = false;
	    String ip = HttpHelper.getRemoteIp(request);
	    Map<String,Object> psparam = new HashMap<String,Object>();
	    List<PortalServiceAllowIp> portalServiceAllowIpList = portalServiceAllowIpMapper.selectByExample(psparam);
	    if(portalServiceAllowIpList!=null&&portalServiceAllowIpList.size()>0) {
	    		for(PortalServiceAllowIp p:portalServiceAllowIpList) {
	    			if(p!=null&&p.getIp()!=null&&p.getIp().trim().equalsIgnoreCase(ip.trim())) {
	    				checkIpIn = true;
	    			}
	    		}
	    }
		String permission_ticket = ValidatorHelper.removeSpecialCharacters(map.get("permission_ticket"));
	    if(!checkIpIn) {
			logger.info("[{}] 401 Unauthorized", permission_ticket);
	    		/**
	         * 401 Unauthorized
	         */
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return;
	    }

        String uid = ValidatorHelper.removeSpecialCharacters(map.get("uid"));
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        // ??????????????????24?????????
        cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
        Date startTime = cal.getTime();
        if (permission_ticket != null && permission_ticket.trim().length() > 0 && uid != null && uid.trim().length() > 0) {
            Map<String, Object> psdparam = new HashMap<String, Object>();
            psdparam.put("permissionTicket", permission_ticket);
            psdparam.put("sCtime", startTime);
            List<PortalServiceDownload> portalServiceDownloadList = portalServiceDownloadMapper.selectByExample(psdparam);
            PrintWriter out;
            if (portalServiceDownloadList != null && portalServiceDownloadList.size() > 0) {
                try {
                    PortalServiceDownload portalServiceDownload = portalServiceDownloadList.get(0);
                    String secretKey = ValidatorHelper.removeSpecialCharacters(portalServiceDownload.getSecretKey());
                    byte[] encryptb = Base64.getUrlDecoder().decode(uid);
                    byte[] decryptb = decrypt(encryptb, secretKey);
                    if (Arrays.equals(decryptb, (portalServiceDownload.getUid() == null ? null : portalServiceDownload.getUid().getBytes("UTF-8")))) {
                        String outStr = "{\"code\":true}";
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=UTF-8");
                        out = response.getWriter();
                        out.print(outStr);
                        out.close();
                    } else {
                        String outStr = "{\"code\":false}";
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=UTF-8");
                        out = response.getWriter();
                        out.print(outStr);
                        out.close();
                    }
                } catch (Exception e) {
					logger.info("[{}] 403 Forbidden -1", permission_ticket);
                    /**
                     * 403 Forbidden
                     */
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            } else {
                try {
                    String outStr = "{\"code\":false}";
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=UTF-8");
                    out = response.getWriter();
                    out.print(outStr);
                    out.close();
                } catch (IOException e) {
					logger.info("[{}] 403 Forbidden -2", permission_ticket);
                    /**
                     * 403 Forbidden
                     */
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            }
        } else {
			logger.info("[{}] 403 Forbidden -3", permission_ticket);
			/**
            /**
             * 403 Forbidden
             */
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @GetMapping("/type_valid")
    public void postTypeValid(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        /**
         * ????????????IP
         */
	    boolean checkIpIn = false;
	    String ip = HttpHelper.getRemoteIp(request);
	    Map<String,Object> psparam = new HashMap<String,Object>();
	    List<PortalServiceAllowIp> portalServiceAllowIpList = portalServiceAllowIpMapper.selectByExample(psparam);
	    if(portalServiceAllowIpList!=null&&portalServiceAllowIpList.size()>0) {
	    		for(PortalServiceAllowIp p:portalServiceAllowIpList) {
	    			if(p!=null&&p.getIp()!=null&&p.getIp().trim().equalsIgnoreCase(ip.trim())) {
	    				checkIpIn = true;
	    			}
	    		}
	    }
		String txId = ValidatorHelper.removeSpecialCharacters(map.get("tx_id"));
	    if(!checkIpIn) {
			logger.info("[{}] 401 Unauthorized", txId);
			/**
	    		/**
	         * 401 Unauthorized
	         */
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return;
	    }
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        // ??????????????????24?????????
        cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
        Date startTime = cal.getTime();
        if (txId != null && txId.trim().length() > 0) {
            Map<String, Object> psdparam = new HashMap<String, Object>();
            psdparam.put("txId", txId);
            psdparam.put("ctimeDesc", true);
            List<PortalServiceDownload> portalServiceDownloadList = portalServiceDownloadMapper.selectByExample(psdparam);
            if (portalServiceDownloadList != null && portalServiceDownloadList.size() > 0) {
                PortalServiceDownload portalServiceDownload = portalServiceDownloadList.get(0);
                if (portalServiceDownload != null && portalServiceDownload.getVerificationLevel() != null) {
                    try {
                        /**
                         * 0 : ??????????????? CER
                         * 0.5 : ???????????? MOE
                         * 1 : TFD
                         * 2 : ????????? NHI
                         * 3 : ??????????????? PII
                         * 4 : E???????????? GOV
                         */
                        String verification = "GOV";
                        if (portalServiceDownload.getVerificationLevel() == 0) {
                            verification = "CER";
                        }
                        if (portalServiceDownload.getVerificationLevel() == 0.5) {
                            verification = "MOE";
                        }
                        if (portalServiceDownload.getVerificationLevel() == 1) {
                            verification = "TFD";
                        }
                        if (portalServiceDownload.getVerificationLevel() == 2) {
                            verification = "NHI";
                        }
                        if (portalServiceDownload.getVerificationLevel() == 3) {
                            verification = "PII";
                        }
                        String outStr = "{\"verification\":\"" + verification + "\"}";
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=UTF-8");
                        PrintWriter out;
                        out = response.getWriter();
                        out.print(outStr);
                        out.close();
                    } catch (IOException e) {
						logger.info("[{}] 403 Forbidden -1", txId);
                        /**
                         * 403 Forbidden
                         */
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    }
                } else {
					logger.info("[{}] 403 Forbidden -2", txId);
                    /**
                     * 403 Forbidden
                     */
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            } else {
				logger.info("[{}] 403 Forbidden -3", txId);
                /**
                 * 403 Forbidden
                 */
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
			logger.info("[{}] 403 Forbidden -4", txId);
            /**
             * 403 Forbidden
             */
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @GetMapping("/txid_status")
    public void getTxidValid(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        /**
         * ????????????IP
         */
	    boolean checkIpIn = false;
	    String ip = HttpHelper.getRemoteIp(request);
	    Map<String,Object> psparam = new HashMap<String,Object>();
	    List<PortalServiceAllowIp> portalServiceAllowIpList = portalServiceAllowIpMapper.selectByExample(psparam);
	    if(portalServiceAllowIpList!=null&&portalServiceAllowIpList.size()>0) {
	    		for(PortalServiceAllowIp p:portalServiceAllowIpList) {
	    			if(p!=null&&p.getIp()!=null&&p.getIp().trim().equalsIgnoreCase(ip.trim())) {
	    				checkIpIn = true;
	    			}
	    		}
	    }
		String txId = ValidatorHelper.removeSpecialCharacters(map.get("tx_id"));
	    if(!checkIpIn) {
			logger.info("[{}] 401 Unauthorized", txId);
	    		/**
	         * 401 Unauthorized
	         */
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return;
	    }
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        // ??????????????????24?????????
        cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
        Date startTime = cal.getTime();
        if (txId != null && txId.trim().length() > 0) {
            Map<String, Object> psdparam = new HashMap<String, Object>();
            psdparam.put("txId", txId);
            psdparam.put("ctimeDesc", true);
            List<PortalServiceDownload> portalServiceDownloadList = portalServiceDownloadMapper.selectByExample(psdparam);
            if (portalServiceDownloadList != null && portalServiceDownloadList.size() > 0) {
                PortalServiceDownload portalServiceDownload = portalServiceDownloadList.get(0);
                if (portalServiceDownload != null) {
                    try {
                        String textStr = "";
                        String codeStr = "";
                        /**
                         * ??????????????????
                         */
                        String downloadSnListStr = portalServiceDownload.getDownloadSnList();
                        if (downloadSnListStr != null && downloadSnListStr.trim().length() > 0) {
							codeStr = portalServiceDownload.getCode();

							/**
							 * codeStr is null ????????? SP-API ??????
							 */
							if(StringUtils.isNotBlank(codeStr)) {
								if(StringUtils.equals(codeStr, "200") && portalServiceDownload.getFileFinishTime() != null) {
									/**
									 * SP-API ???????????? 200
									 * ??????????????????
									 * 1.?????????????????? -> ?????????????????????
									 * 2.?????????????????? -> SP???????????????
									 */
									if(portalServiceDownload.getDownloadTime() == null) {
										codeStr = "200";
										textStr = "?????????????????????";
									} else {
										codeStr = "201";
										textStr = "SP???????????????";
									}
								} else if(StringUtils.equals(codeStr, "200") && portalServiceDownload.getFileFinishTime() == null) {
									codeStr = "429";
									textStr = "MyData ???????????????";
								} else {
									codeStr = "410";
									textStr = "SP-API ????????????";
								}
							} else {
								String[] downloadSnList = downloadSnListStr.split(",");
								Boolean has400 = false;
								Boolean has403 = false;
								Boolean has205 = false;
								Boolean has429 = false;
								for(String downloadSn : downloadSnList) {
									PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(downloadSn));
									if(download == null) {
										continue;
									}

									if(ulogUtil.hasUserReject(portalServiceDownload.getTxId(), download.getTransactionUid())) {
										has205 = true;
										break;
									}

									if(StringUtils.equals(download.getCode(), "400")) {
										has400 = true;
									}

									if(StringUtils.equals(download.getCode(), "403")) {
										has403 = true;
									}

									if(StringUtils.equals(download.getCode(), "429")) {
										has429 = true;
									}
								}

								if(has205 == true) {
									codeStr = "205";
									textStr = "User????????????????????????SP";
								} else if(has400 == true) {
									codeStr = "400";
									textStr = "????????????";
								} else if(has403 == true) {
									codeStr = "403";
									textStr = "?????????????????????";
								} else if(has429 == true) {
									codeStr = "429";
									textStr = "MyData ???????????????";
								} else {
									codeStr = "504";
									textStr = "SP?????????DP??????????????????????????????????????? DP ?????????";
								}

							}

                            String outStr = "{\"code\":\"" + codeStr + "\",\"text\":\"" + textStr + "\"}";
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json; charset=UTF-8");
                            PrintWriter out;
                            out = response.getWriter();
                            out.print(outStr);
                            out.close();
                        } else {
                            codeStr = "429";
                            textStr = "MyData ???????????????";
                            String outStr = "{\"code\":\"" + codeStr + "\",\"text\":\"" + textStr + "\"}";
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json; charset=UTF-8");
                            PrintWriter out;
                            out = response.getWriter();
                            out.print(outStr);
                            out.close();
                        }
                    } catch (IOException e) {
						logger.info("[{}] 409 Conflict", txId);
                        /**
                         * 409 Conflict
                         */
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                    }
                } else {
					logger.info("[{}] 204 No Content", txId);
                    /**
                     * 204 No Content
                     */
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            } else {
				logger.info("[{}] 403 Forbidden -1", txId);
                /**
                 * 403 Forbidden
                 */
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
			logger.info("[{}] 403 Forbidden -2", txId);
            /**
             * 403 Forbidden
             */
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }


    /**
     * MyData????????????????????????
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("/list/{cate_id}")
    public String getServicelist(@PathVariable("cate_id") Integer cateId,
                                 HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IllegalAccessException, InvocationTargetException {
        /**
         * ?????????????????????4
         */
        if (cateId > 4) {
            cateId = 4;
        }
		
		Map<String,Object> psparam = new HashMap<String,Object>();
		psparam.put("cateId", cateId);
		psparam.put("isShow", 1);
	    List<PortalService> portalServiceList =  portalServiceMapper.selectByExample(psparam);
	    List<PortalServiceExt> portalServiceExtList = new ArrayList<PortalServiceExt>();
	    if(portalServiceList!=null&&portalServiceList.size()>0) {
	    		for(PortalService p:portalServiceList) {
	    			PortalServiceExt ext = new PortalServiceExt();
	    			BeanUtils.copyProperties(ext, p);
	    			PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
	    			if(portalProvider!=null) {
	    				ext.setProviderName(portalProvider.getName());
	    			}
	    			portalServiceExtList.add(ext);
	    		}
	    }  
	    model.addAttribute("psExtList", portalServiceExtList);
	    return "service-list";
	}
	
	/**
	 * MyData??????????????????-->?????????????????????
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/{client_id:.+}/{parameters}/{tx_id}")
	public String getServiceIdPage(@PathVariable("client_id") String clientId,
			@PathVariable("parameters") String parameters,
			@PathVariable("tx_id") String txId,
			HttpServletRequest request, 
			HttpServletResponse response, 
			ModelMap model) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null && sr.getMember()!=null){
			Member maskMember = new Member();
			BeanUtils.copyProperties(maskMember,SessionMember.getSessionMemberToMember(sr.getMember()));
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
		}

		List<String> tmpscopeList = new ArrayList<String>();		
		int level = 4;
		/**
		 * ???????????? returnUrl
		 */
		String returnUrl = ValidatorHelper.removeSpecialCharacters(request.getParameter("returnUrl"));
		String returnUrlBase = "";
		String returnUrlQuery = "";
		//System.out.println("returnUrl="+returnUrl);
		if(returnUrl==null||returnUrl.trim().length()==0) {
			logger.info("[{}] http status code 404", txId);
			//???returnUrl???????????? http status code 404
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}else {
			//returnUrl ??????
			String[] urlArray = returnUrl.split("[?]",1000);
			if(urlArray.length > 0){
				returnUrlBase = urlArray[0];
				if(urlArray.length > 1){
					returnUrlQuery = urlArray[1];
				}
			}
		}
		/**
		 * ???????????? client_id
		 */
		PortalService portalService = null;
		Map<String,Object> psparam = new HashMap<String,Object>();
		psparam.put("clientId", clientId);
		List<PortalService> portalServiceList = portalServiceMapper.selectByExample(psparam);
		String cbcTxId = "";
		if(portalServiceList!=null&&portalServiceList.size()>0) {
			portalService = portalServiceList.get(0);
			cbcTxId = base64Encoder.encodeToString(CBCUtil.encrypt_cbc(txId.getBytes("UTF-8"),(portalService.getClientSecret()+portalService.getClientSecret()),portalService.getCbcIv()));
		}else {
			logger.info("[{}] 401???????????? client_id ??? resource_id???", txId);
			//401???????????? client_id ??? resource_id???
			response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",txId));
			return null;
		}
		String sp_return_url_401 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId);
		model.addAttribute("sp_return_url_401", sp_return_url_401);
		
		String sp_return_url_409 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"409",cbcTxId);
		session.setAttribute("sp_return_url_409", sp_return_url_409);
		
		String sp_return_url_205 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"205",cbcTxId);
		model.addAttribute("sp_return_url_205", sp_return_url_205);

		String sp_return_url_408 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"408",cbcTxId);
		model.addAttribute("sp_return_url_408", sp_return_url_408);

		String sp_return_url_423 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"423",cbcTxId);
		model.addAttribute("sp_return_url_423", sp_return_url_423);
		Boolean isDeactivate = portalService.getCheckStat() != null && portalService.getCheckStat().equals(new Integer(8));
		model.addAttribute("isDeactivate", isDeactivate);
		
		/**
		 * ?????????????????????????????????IP
		 */
		String ip = HttpHelper.getRemoteIp(request);
		Integer stageModeVer = systemOptionUtil.use(SystemOptionType.FEATURE_STAGE_MODE.name(), ip);
		if(portalService!=null&&stageModeVer==1) {
			if(portalService.getIsShow()!=null&&portalService.getIsShow()==2) {
			    boolean checkIpIn = false;
			    Map<String,Object> pssparam = new HashMap<String,Object>();
			    List<PortalServiceStageIp> portalServiceStageIpList = portalServiceStageIpMapper.selectByExample(pssparam);
			    if(portalServiceStageIpList!=null&&portalServiceStageIpList.size()>0) {
		    		for(PortalServiceStageIp p:portalServiceStageIpList) {
		    			if(p!=null&&p.getIp()!=null&&p.getIp().trim().equalsIgnoreCase(ip.trim())) {
		    				checkIpIn = true;
		    			}
		    		}
			    }
			    if(!checkIpIn) {
			    	logger.info("[{}] 401 Unauthorized and ip is [{}]", txId, ip);
		    		/**
		    		 * 401 Unauthorized
		    		 */
				    PortalServiceCategory portalServiceCategory =  portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getCateId()));
				    model.addAttribute("portalServiceCategory", portalServiceCategory);	
		    		model.addAttribute("portalService", portalService);
		    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    		return "service-detail-error";
			    }
			}else if(portalService.getIsShow()==null||portalService.getIsShow()==0) {
				/**
				 * ?????????
				 */
			    PortalServiceCategory portalServiceCategory =  portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getCateId()));
			    model.addAttribute("portalServiceCategory", portalServiceCategory);	
	    		model.addAttribute("portalService", portalService);
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return "service-detail-error";
			}			
		}
		
		/**
		 * ???????????? pid
		 */
		String pidAesEnCode = null;
		String secretKey = portalService.getClientSecret()+portalService.getClientSecret();
		byte[] decryptpsid=null;
		String tmpforpid = null;
		try {
			if(request.getParameter("pid")!=null&&request.getParameter("pid").trim().length()>0) {
				pidAesEnCode = ValidatorHelper.removeSpecialCharacters(request.getParameter("pid").replaceAll(" ", "+"));
				try {
					decryptpsid = CBCUtil.decrypt_cbc(Base64.getDecoder().decode(pidAesEnCode.getBytes("UTF-8")),secretKey,portalService.getCbcIv());
					//logger.debug("is decryptUid AES/CBC/PANDDING5 = "+ new String(decryptpsid,"UTF-8"));
				}catch(Exception ex1) {
					logger.debug("not aes/cbc");
				}
				//tmpforpid = (decryptpsid==null?null:new String(decryptpsid,"UTF-8"));
				Charset charset = Charset.forName("UTF-8");
				CharsetDecoder decoder = charset.newDecoder();
				ByteBuffer srcBuffer = ByteBuffer.wrap(decryptpsid);
				CharBuffer resBuffer = decoder.decode(srcBuffer);
				StringBuilder tmpStringBuilder = new StringBuilder(resBuffer);
				if(tmpStringBuilder!=null&&tmpStringBuilder.length()>0) {
					session.setAttribute("pid", tmpStringBuilder.toString());
					model.addAttribute("pid", tmpStringBuilder.toString());
					model.addAttribute("maskPid", MaskUtil.maskUid(tmpStringBuilder.toString()));
					logger.debug("session ??????????????? {}"+ tmpStringBuilder.toString());
				}else {
					logger.info("[{}] 400???????????? client_id ??? resource_id -1", txId);
		            //400???????????? client_id ??? resource_id???
		            response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "400", cbcTxId));
		            return null;
				}
			}else {
				logger.info("[{}] 400???????????? client_id ??? resource_id -2", txId);
	            //400???????????? client_id ??? resource_id???
	            response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "400", cbcTxId));
	            return null;
			}
		} catch (Exception e2) {
			logger.error(e2.getLocalizedMessage(), e2);
		}
        /**
         * ???????????? tx_id
         */
        if (txId == null || txId.trim().length() == 0) {
			logger.info("[{}] 401???tx_id is null", txId);
            //401???????????? client_id ??? resource_id???
            response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "401", cbcTxId));
            return null;
        }
        
        model.addAttribute("txId", txId);

        PortalServiceCategory portalServiceCategory = portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getCateId()));
        model.addAttribute("portalServiceCategory", portalServiceCategory);
        /**
         * ??????portalService
         */
        //System.out.println("sp_return_url="+returnUrlBase.trim());
        //System.out.println("sp_db_url    ="+portalService.getRedirectUri().trim());
        if (!returnUrlBase.trim().equalsIgnoreCase(portalService.getRedirectUri().trim())) {
			logger.info("[{}] 404???sp_return_url ?????????MyData????????????????????????????????????", txId);
            response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "404", cbcTxId));
            return null;
        }

        /**
         * ??????portal_service_scope???????????????resourceId
         */
		/*Map<String,Object> sparam = new HashMap<String,Object>();
		List<PortalResourceScope> allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);*/
        if (allPortalResourceScopeList == null) {
            Map<String, Object> sparam = new HashMap<String, Object>();
            allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
        }
        List<Integer> allowPrIdList = new ArrayList<Integer>();
        //Map<String,Object> pssparam = new HashMap<String,Object>();
        //pssparam.put("psId", portalService.getPsId());
        //List<PortalServiceScope> portalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
        if (allPortalServiceScopeList == null) {
            Map<String, Object> pssparam = new HashMap<String, Object>();
            allPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
        }
        List<PortalServiceScope> portalServiceScopeList = new ArrayList<PortalServiceScope>();
        if (allPortalServiceScopeList != null && allPortalServiceScopeList.size() > 0) {
            for (PortalServiceScope tmppsc : allPortalServiceScopeList) {
                if (tmppsc.getPsId().compareTo(portalService.getPsId()) == 0) {
                    portalServiceScopeList.add(tmppsc);
                }
            }
        }
        if (portalServiceScopeList != null && portalServiceScopeList.size() > 0) {
            for (PortalServiceScope psc : portalServiceScopeList) {
                //Map<String,Object> sparam = new HashMap<String,Object>();
                //sparam.put("scope", psc.getScope());
                //List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                /**
                 * tmp scope portalResourceScopeList
                 */
                List<PortalResourceScope> tmpScopePortalResourceScopeList = new ArrayList<PortalResourceScope>();
                if (allPortalResourceScopeList != null && allPortalResourceScopeList.size() > 0) {
                    for (PortalResourceScope tmpPrc : allPortalResourceScopeList) {
                        if (tmpPrc.getScope().equalsIgnoreCase(psc.getScope())) {
                            tmpScopePortalResourceScopeList.add(tmpPrc);
                        }
                    }
                }
                if (tmpScopePortalResourceScopeList != null && tmpScopePortalResourceScopeList.size() > 0) {
                    if (!allowPrIdList.contains(tmpScopePortalResourceScopeList.get(0).getPrId())) {
                        allowPrIdList.add(tmpScopePortalResourceScopeList.get(0).getPrId());
                    }
                }
            }
        } else {
			logger.info("[{}] 403???portal_service_scope?????????", txId);
            //403???portal_service_scope?????????
            response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "403", cbcTxId));
            return null;
        }

        /**
         * ???????????? parameters ?????? resourceId
         */
        List<Integer> prIdList = new ArrayList<Integer>();
        List<PortalResourceExt> portalResourceExtList = new ArrayList<PortalResourceExt>();
        if (parameters != null && parameters.trim().length() > 0) {
            try {
                byte[] parameterBytes = decoder.decode(parameters);
                String[] pss;
                pss = (new String(parameterBytes, "UTF-8")).split("\\:");
                //Map<String,Object> rparam = new HashMap<String,Object>();
                //List<PortalResource> allPortalResourceList = portalResourceMapper.selectByExample(rparam);
                if (allPortalResourceList == null) {
                    Map<String, Object> rparam = new HashMap<String, Object>();
                    allPortalResourceList = portalResourceMapper.selectByExample(rparam);
                }

                if (pss != null && pss.length > 0) {
                	boolean checkResourceOk = true;
                    boolean checkResourceIdExit = true;
                    int moecaCount = 0;

                    if(checkParamDuplication(pss) == true) {
						logger.info("[{}] 400???parameter Duplication", txId);
						//400?????????????????????SP????????? path parameter???
						response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "400", cbcTxId));
						return null;
					}

                    for (String s : pss) {
                        //System.out.println("==parameter==:"+s);
                        if (s != null && s.trim().length() > 0) {
                            //Map<String,Object> rparam = new HashMap<String,Object>();
                            //rparam.put("resourceId", s.trim());
                            //List<PortalResource> portalResourceList = portalResourceMapper.selectByExample(rparam);
                            List<PortalResource> portalResourceList = new ArrayList<PortalResource>();
                            if (allPortalResourceList != null && allPortalResourceList.size() > 0) {
                                for (PortalResource tpr : allPortalResourceList) {
                                    if (s.trim().equalsIgnoreCase(tpr.getResourceId())) {
										portalResourceList.add(tpr);
                                        if(tpr.getMoecaCheck()!=null && tpr.getMoecaCheck() == 1) {
                                        		moecaCount++;
                                        }
                                        
                                        if(tpr.getStatus() != 1 || (tpr.getCheckStat() != null && tpr.getCheckStat() != 0)) {
                                        		checkResourceOk = false;
                                        }else {
                                        		if(maintainUtils.checkInMaintain(tpr.getPrId())) {
                                        			checkResourceOk = false;
                                        		}
                                        }
                                    }
                                }
                            }
                            if (portalResourceList != null && portalResourceList.size() > 0) {
                                prIdList.add(portalResourceList.get(0).getPrId());
                                try {
                                    PortalResourceExt bean = new PortalResourceExt();
                                    BeanUtils.copyProperties(bean, portalResourceList.get(0));
                                    portalResourceExtList.add(bean);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }

                                if (portalResourceList.get(0).getLevel() < level) {
                                    level = portalResourceList.get(0).getLevel();
                                }
                                if (!allowPrIdList.contains(portalResourceList.get(0).getPrId())) {
                                    checkResourceIdExit = false;
                                }
                            } else {
                                checkResourceIdExit = false;
                            }
                        }
                    }
                    if (!checkResourceIdExit) {
						logger.info("[{}] 401???MyData??????SP???????????? resoruce_id ?????????SP???????????????DP?????????????????????", txId);
                        //401???MyData??????SP???????????? resoruce_id ?????????SP???????????????DP?????????????????????
                        response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "401", cbcTxId));
                        return null;
                    }

                    if(moecaCount == pss.length) {
                    		model.addAttribute("moecaCheck", true);
                    }else {
                    		model.addAttribute("moecaCheck", false);
                    }
                    
                    model.addAttribute("checkResourceOk", checkResourceOk);
                    model.addAttribute("sp_return_url_504", conbindReturnUrl(returnUrlBase, returnUrlQuery, "504", cbcTxId));
                } else {
					logger.info("[{}] 401???????????? client_id ??? resource_id - 5", txId);
                    //401???????????? client_id ??? resource_id???
                    response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "401", cbcTxId));
                    return null;
                }
            } catch (UnsupportedEncodingException e) {
				logger.info("[{}] 400?????????????????????SP????????? path parameter???", txId);
                //400?????????????????????SP????????? path parameter???
                response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "400", cbcTxId));
                return null;
            }
        } else {
			logger.info("[{}] 401???????????? client_id ??? resource_id - 6", txId);
            //401???????????? client_id ??? resource_id???
            response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "401", cbcTxId));
            return null;
        }

        /**
         *
         */
        List<String> needScopeList = new ArrayList<String>();
        List<String> newScopeList = new ArrayList<String>();
        List<String> scopeList = Arrays.asList("openid", "gsp.profile", "email", "uid", "phone_number", "API.amr", "API0000001-read");
        for (String s : scopeList) {
            needScopeList.add(s);
        }
        if (prIdList != null && prIdList.size() > 0) {
            for (Integer prId : prIdList) {
                /**
                 * tmp scope portalResourceScopeList
                 */
                List<PortalResourceScope> tmpScopePortalResourceScopeList = new ArrayList<PortalResourceScope>();
                if (allPortalResourceScopeList != null && allPortalResourceScopeList.size() > 0) {
                    for (PortalResourceScope tmpPrc : allPortalResourceScopeList) {
                        if (tmpPrc.getPrId().compareTo(prId) == 0) {
                            tmpScopePortalResourceScopeList.add(tmpPrc);
                        }
                    }
                }
                if (tmpScopePortalResourceScopeList != null && tmpScopePortalResourceScopeList.size() > 0) {
                    for (PortalResourceScope prs : tmpScopePortalResourceScopeList) {
                        if (prs != null && prs.getScope() != null && prs.getScope().trim().length() > 0) {
                            needScopeList.add(prs.getScope().trim());
                            newScopeList.add(prs.getScope().trim());
                        }
                    }
                }
            }
        }
        //level
        model.addAttribute("level", level);
        //portalService
        PortalServiceExt portalServiceExt = new PortalServiceExt();
        try {
            BeanUtils.copyProperties(portalServiceExt, portalService);
            PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceExt.getProviderId()));
            portalServiceExt.setProviderName(portalProvider.getName());
			if(portalProvider.getType()!=null) {
				portalServiceExt.setType(portalProvider.getType());
			}else {
				portalServiceExt.setType(0);
			}
        } catch (IllegalAccessException e1) {
            logger.error(e1.getLocalizedMessage(), e1);
        } catch (InvocationTargetException e1) {
			logger.error(e1.getLocalizedMessage(), e1);
        }
        model.addAttribute("portalService", portalServiceExt);
        //????????????????????????
        model.addAttribute("returnUrl", returnUrl);
        /**
         * ???????????????
         */
        //????????????
        List<Param> paramList = new ArrayList<Param>();
        List<PortalResourceExt> finalPortalResourceExtList = new ArrayList<PortalResourceExt>();
        if (portalResourceExtList != null && portalResourceExtList.size() > 0) {
            for (PortalResourceExt ext : portalResourceExtList) {
                Map<String, Object> param3 = new HashMap<String, Object>();
                param3.put("prId", ext.getPrId());
                param3.put("idAsc", true);
                List<PortalResourceField> portalResourceFieldList = portalResourceFieldMapper.selectByExample(param3);
                try {
                    PortalResourceExt bean = new PortalResourceExt();
                    BeanUtils.copyProperties(bean, ext);
                    bean.setFieldList(portalResourceFieldList);
                    PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(bean.getProviderId()));
					if (pp != null) {
						bean.setProviderName(pp.getName());
					}
					if(pp.getType()!=null) {
						bean.setType(pp.getType());
					}else {
						bean.setType(0);
					}
					Map<String, Object> prpquery = new HashMap<String, Object>(); 
					prpquery.put("prId", ext.getPrId());
					List<PortalResourceParam> portalResourceParamList = portalResourceParamMapper.selectByExample(prpquery);
					if(portalResourceParamList!=null&&portalResourceParamList.size()>0) {
						List<Param> paramList1 = new ArrayList<Param>();
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
									p.setYearParam(YearParamUtil.getYearParam(bean.getResourceId()));
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
						    paramList1.add(p);
						    paramList.add(p);


						}
						bean.setParamList(paramList1);
						System.out.println("paramList size:"+paramList.size());					
					}
					finalPortalResourceExtList.add(bean);
				} catch (IllegalAccessException e) {
					logger.error(e.getLocalizedMessage(), e);
				} catch (InvocationTargetException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
		}
		model.addAttribute("portalResourceExtList", finalPortalResourceExtList);
		model.addAttribute("paramList", paramList);
		if(sr != null) {
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_140, null, null, HttpHelper.getRemoteIp(request));
		}
		return "service-detail";
	}

	private Boolean checkParamDuplication(String[] params) {
		List<String> tmpList = new ArrayList<>();
		Boolean isDuplication = false;
		for(String ss : params) {
			if(tmpList.contains(ss) == true) {
				isDuplication = true;
				break;
			} else {
				tmpList.add(ss);
			}
		}
		return isDuplication;
	}
	
	
	@GetMapping("/spsignature/{client_id:.+}/{parameters}/{tx_id}")
	public String getSpsignatureServiceIdPage(@PathVariable("client_id") String clientId,
			@PathVariable("parameters") String parameters,
			@PathVariable("tx_id") String txId,
			HttpServletRequest request, 
			HttpServletResponse response, 
			ModelMap model) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null && sr.getMember()!=null){
			Member maskMember = new Member();
			BeanUtils.copyProperties(maskMember,SessionMember.getSessionMemberToMember(sr.getMember()));
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
		}
		/**
		 * ?????? txId ???????????????????????????
		 * 1. ?????????txId????????????portal_service_salt????????????????????????10?????????408??????????????????SP???????????????????????????????????????MyData??????600??????
		 * 2. 20?????????????????????????????????????????????408???????????????????????????returnUrl???
		 * 3. ????????????access_token
		 */
		
		
		int level = 4;
		/**
		 * ???????????? returnUrl
		 */
		String returnUrl = ValidatorHelper.removeSpecialCharacters(request.getParameter("returnUrl"));
		String returnUrlBase = "";
		String returnUrlQuery = "";
		//System.out.println("returnUrl="+returnUrl);
		if(returnUrl==null||returnUrl.trim().length()==0) {
			logger.info("[{}] ???returnUrl???????????? http status code 404", txId);
			//???returnUrl???????????? http status code 404
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}else {
			//returnUrl ??????
			String[] urlArray = returnUrl.split("[?]",1000);
			if(urlArray.length > 0){
				returnUrlBase = urlArray[0];
				if(urlArray.length > 1){
					returnUrlQuery =  urlArray[1];
				}
			}
		}

		/**
		 * ???????????? client_id
		 */
		PortalService portalService = null;
		Map<String,Object> psparam = new HashMap<String,Object>();
		psparam.put("clientId", clientId);
		List<PortalService> portalServiceList = portalServiceMapper.selectByExample(psparam);
		String cbcTxId = "";
		if(portalServiceList!=null&&portalServiceList.size()>0) {
			portalService = portalServiceList.get(0);
			cbcTxId = base64Encoder.encodeToString(CBCUtil.encrypt_cbc(txId.getBytes("UTF-8"),(portalService.getClientSecret()+portalService.getClientSecret()),portalService.getCbcIv()));
		}else {
			logger.info("[{}] 401???????????? client_id ??? resource_id???", txId);
			//401???????????? client_id ??? resource_id???
			response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId));
			return null;
		}
		String sp_return_url_401 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId);
		model.addAttribute("sp_return_url_401", sp_return_url_401);
		
		String sp_return_url_409 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"409",cbcTxId);
		session.setAttribute("sp_return_url_409", sp_return_url_409);
		
		String sp_return_url_205 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"205",cbcTxId);
		model.addAttribute("sp_return_url_205", sp_return_url_205);

		String sp_return_url_408 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"408",cbcTxId);
		model.addAttribute("sp_return_url_408", sp_return_url_408);

		String sp_return_url_423 = conbindReturnUrl(returnUrlBase,returnUrlQuery,"423",cbcTxId);
		model.addAttribute("sp_return_url_423", sp_return_url_423);
		Boolean isDeactivate = portalService.getCheckStat() != null && portalService.getCheckStat().equals(new Integer(8));
		model.addAttribute("isDeactivate", isDeactivate);

		/**
		 * ?????????????????????????????????IP
		 */
		String ip = HttpHelper.getRemoteIp(request);
		Integer stageModeVer = systemOptionUtil.use(SystemOptionType.FEATURE_STAGE_MODE.name(), ip);
		if(portalService!=null&&stageModeVer==1) {
			if(portalService.getIsShow()!=null&&portalService.getIsShow()==2) {
			    boolean checkIpIn = false;
			    Map<String,Object> pssparam = new HashMap<String,Object>();
			    List<PortalServiceStageIp> portalServiceStageIpList = portalServiceStageIpMapper.selectByExample(pssparam);
			    if(portalServiceStageIpList!=null&&portalServiceStageIpList.size()>0) {
		    		for(PortalServiceStageIp p:portalServiceStageIpList) {
		    			if(p!=null&&p.getIp()!=null&&p.getIp().trim().equalsIgnoreCase(ip.trim())) {
		    				checkIpIn = true;
		    			}
		    		}
			    }
			    if(!checkIpIn) {
			    	logger.info("[{}] 401 Unauthorized and ip is [{}]", txId, ip);
		    		/**
		    		 * 401 Unauthorized
		    		 */
				    PortalServiceCategory portalServiceCategory =  portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getCateId()));
				    model.addAttribute("portalServiceCategory", portalServiceCategory);	
		    		model.addAttribute("portalService", portalService);
		    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    		return "service-detail-error";
			    }
			}else if(portalService.getIsShow()==null||portalService.getIsShow()==0) {
				/**
				 * ?????????
				 */
			    PortalServiceCategory portalServiceCategory =  portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getCateId()));
			    model.addAttribute("portalServiceCategory", portalServiceCategory);	
	    		model.addAttribute("portalService", portalService);
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return "service-detail-error";
			}			
		}
		
		/**
		 * ???????????? tx_id
		 */
		if(txId==null||txId.trim().length()==0) {
			logger.info("[{}] 401???tx_id is null", txId);
			//401???????????? client_id ??? resource_id???
			response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId));
			return null;
		}


		model.addAttribute("txId", txId);
	    PortalServiceCategory portalServiceCategory =  portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getCateId()));
	    model.addAttribute("portalServiceCategory", portalServiceCategory);		
		/**
		 * ??????portalService
		 */
		if(!returnUrlBase.trim().equalsIgnoreCase(portalService.getRedirectUri().trim())) {
			logger.info("[{}] 404???sp_return_url ?????????MyData????????????????????????????????????", txId);
			//404???sp_return_url ?????????MyData????????????????????????????????????
			response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"404",cbcTxId));
			return null;
		}
		
		/**
		 * ??????portal_service_scope???????????????resourceId
		 */
		if(allPortalResourceScopeList==null) {
			Map<String,Object> sparam = new HashMap<String,Object>();
			allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
		}
		List<Integer> allowPrIdList = new ArrayList<Integer>();
		if(allPortalServiceScopeList==null) {
			Map<String,Object> pssparam = new HashMap<String,Object>();
			allPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
		}
		List<PortalServiceScope> portalServiceScopeList = new ArrayList<PortalServiceScope>();
		if(allPortalServiceScopeList!=null&&allPortalServiceScopeList.size()>0) {
			for(PortalServiceScope tmppsc:allPortalServiceScopeList) {
				if(tmppsc.getPsId().compareTo(portalService.getPsId())==0) {
					portalServiceScopeList.add(tmppsc);
				}
			}
		}
		if(portalServiceScopeList!=null&&portalServiceScopeList.size()>0) {
			for(PortalServiceScope psc:portalServiceScopeList) {
				/**
				 * tmp scope portalResourceScopeList
				 */
				List<PortalResourceScope> tmpScopePortalResourceScopeList = new ArrayList<PortalResourceScope>();
				if(allPortalResourceScopeList!=null&&allPortalResourceScopeList.size()>0) {
					for(PortalResourceScope tmpPrc:allPortalResourceScopeList) {
						if(tmpPrc.getScope().equalsIgnoreCase(psc.getScope())) {
							tmpScopePortalResourceScopeList.add(tmpPrc);
						}
					}					
				}
				if(tmpScopePortalResourceScopeList!=null&&tmpScopePortalResourceScopeList.size()>0) {
					if(!allowPrIdList.contains(tmpScopePortalResourceScopeList.get(0).getPrId())) {
						allowPrIdList.add(tmpScopePortalResourceScopeList.get(0).getPrId());
					}
				}
			}
		}else {
			logger.info("[{}] 403???portal_service_scope?????????", txId);
			//403???portal_service_scope?????????
			response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"403",cbcTxId));
			return null;
		}
		
		/**
		 * ???????????? parameters ?????? resourceId
		 */
		List<Integer> prIdList = new ArrayList<Integer>();
		List<PortalResourceExt> portalResourceExtList  = new ArrayList<PortalResourceExt>();
		if(parameters!=null&&parameters.trim().length()>0) {
			try {
				byte[] parameterBytes = decoder.decode(parameters);
				String[] pss;
				pss = (new String(parameterBytes,"UTF-8")).split("\\:");

				if(allPortalResourceList==null) {
					Map<String,Object> rparam = new HashMap<String,Object>();
					allPortalResourceList = portalResourceMapper.selectByExample(rparam);
				}
				if(pss!=null&&pss.length>0) {
					boolean checkResourceOk = true;
					boolean checkResourceIdExit = true;

					if(checkParamDuplication(pss) == true) {
						logger.info("[{}] 400???parameter Duplication", txId);
						//400?????????????????????SP????????? path parameter???
						response.sendRedirect(conbindReturnUrl(returnUrlBase, returnUrlQuery, "400", cbcTxId));
						return null;
					}

					for(String s:pss) {
						if(s!=null&&s.trim().length()>0) {
							List<PortalResource> portalResourceList = new ArrayList<PortalResource>();
							if(allPortalResourceList!=null&&allPortalResourceList.size()>0) {
								for(PortalResource tpr:allPortalResourceList) {
									if(s.trim().equalsIgnoreCase(tpr.getResourceId())) {
										portalResourceList.add(tpr);
										
										if(tpr.getStatus() != 1 || (tpr.getCheckStat() != null && tpr.getCheckStat() != 0)) {
                                        		checkResourceOk = false;
                                        }else {
	                                    		if(maintainUtils.checkInMaintain(tpr.getPrId())) {
	                                    			checkResourceOk = false;
	                                    		}
	                                    }
									}
								}
							}
							if(portalResourceList!=null&&portalResourceList.size()>0) {
								prIdList.add(portalResourceList.get(0).getPrId());
								try {
									PortalResourceExt bean = new PortalResourceExt();
									BeanUtils.copyProperties(bean, portalResourceList.get(0));
									portalResourceExtList.add(bean);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								
								if(portalResourceList.get(0).getLevel()<level) {
									level = portalResourceList.get(0).getLevel();
								}
								if(!allowPrIdList.contains(portalResourceList.get(0).getPrId())) {
									checkResourceIdExit = false;
								}
							}else {
								checkResourceIdExit = false;
							}
						}
					}
					if(!checkResourceIdExit) {
						logger.info("[{}] 401???MyData??????SP???????????? resoruce_id ?????????SP???????????????DP?????????????????????", txId);
						//401???MyData??????SP???????????? resoruce_id ?????????SP???????????????DP?????????????????????
						response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId));
						return null;
					}
					
					model.addAttribute("checkResourceOk", checkResourceOk);
                    model.addAttribute("sp_return_url_504", conbindReturnUrl(returnUrlBase, returnUrlQuery, "504", cbcTxId));
				}else {
					logger.info("[{}] 401???????????? client_id ??? resource_id??? -3", txId);
					//401???????????? client_id ??? resource_id???
					response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId));
					return null;
				}
			} catch (UnsupportedEncodingException e) {
				logger.info("[{}] 400?????????????????????SP????????? path parameter???", txId);
				//400?????????????????????SP????????? path parameter???
				response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"400",cbcTxId));
				return null;
			}
		}else {
			logger.info("[{}] 4401???????????? client_id ??? resource_id??? -4", txId);
			//401???????????? client_id ??? resource_id???
			response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId));
			return null;
		}
		
		List<String> needScopeList = new ArrayList<String>();
		List<String> newScopeList = new ArrayList<String>();
		List<String> scopeList = Arrays.asList("openid", "gsp.profile", "email", "uid","phone_number","API.amr", "API0000001-read");
		for(String s:scopeList) {
			needScopeList.add(s);
		}

		if(prIdList!=null&&prIdList.size()>0) {
			for(Integer prId:prIdList) {
				/**
				 * tmp scope portalResourceScopeList
				 */
				List<PortalResourceScope> tmpScopePortalResourceScopeList = new ArrayList<PortalResourceScope>();
				if(allPortalResourceScopeList!=null&&allPortalResourceScopeList.size()>0) {
					for(PortalResourceScope tmpPrc:allPortalResourceScopeList) {
						if(tmpPrc.getPrId().compareTo(prId)==0) {
							tmpScopePortalResourceScopeList.add(tmpPrc);
						}
					}					
				}
				if(tmpScopePortalResourceScopeList!=null&&tmpScopePortalResourceScopeList.size()>0) {
					for(PortalResourceScope prs:tmpScopePortalResourceScopeList) {
						if(prs!=null&&prs.getScope()!=null&&prs.getScope().trim().length()>0) {
							needScopeList.add(prs.getScope().trim());
							newScopeList.add(prs.getScope().trim());
						}
					}
				}
			}
		}

		//level
		model.addAttribute("level", level);
		//portalService
		PortalServiceExt portalServiceExt = new PortalServiceExt();
		try {
			BeanUtils.copyProperties(portalServiceExt, portalService);
			PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceExt.getProviderId()));
			portalServiceExt.setProviderName(portalProvider.getName());
			if(portalProvider.getType()!=null) {
				portalServiceExt.setType(portalProvider.getType());
			}else {
				portalServiceExt.setType(0);
			}
		} catch (IllegalAccessException e1) {
			logger.error(e1.getLocalizedMessage(), e1);
		} catch (InvocationTargetException e1) {
			logger.error(e1.getLocalizedMessage(), e1);
		}
		model.addAttribute("portalService", portalServiceExt);
		//????????????????????????
		model.addAttribute("returnUrl", returnUrl);
		/**
		 * ???????????????
		 */
		//????????????
		List<Param> paramList = new ArrayList<Param>();
		List<PortalResourceExt> finalPortalResourceExtList = new ArrayList<PortalResourceExt>();
		if(portalResourceExtList!=null&&portalResourceExtList.size()>0) {
			for(PortalResourceExt ext:portalResourceExtList) {
				Map<String,Object> param3 = new HashMap<String,Object>();
				param3.put("prId", ext.getPrId());
				param3.put("idAsc", true);
				List<PortalResourceField> portalResourceFieldList = portalResourceFieldMapper.selectByExample(param3);
				try {
					PortalResourceExt bean = new PortalResourceExt();
					BeanUtils.copyProperties(bean, ext);
					bean.setFieldList(portalResourceFieldList);
                    PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(bean.getProviderId()));
					if (pp != null) {
						bean.setProviderName(pp.getName());
					}
					if(pp.getType()!=null) {
						bean.setType(pp.getType());
					}else {
						bean.setType(0);
					}
					Map<String, Object> prpquery = new HashMap<String, Object>(); 
					prpquery.put("prId", ext.getPrId());
					List<PortalResourceParam> portalResourceParamList = portalResourceParamMapper.selectByExample(prpquery);
					if(portalResourceParamList!=null&&portalResourceParamList.size()>0) {
						List<Param> paramList1 = new ArrayList<Param>();
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
									p.setYearParam(YearParamUtil.getYearParam(bean.getResourceId()));
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
						    paramList1.add(p);
						    paramList.add(p);
						}
						bean.setParamList(paramList1);
						logger.info("[{}] paramList size: {}", txId, paramList.size());
					}
					finalPortalResourceExtList.add(bean);
				} catch (IllegalAccessException e) {
					logger.error(e.getLocalizedMessage(), e);
				} catch (InvocationTargetException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
		}
		model.addAttribute("portalResourceExtList", finalPortalResourceExtList);
		model.addAttribute("paramList", paramList);
		Map<String,Object> mparam = new HashMap<String,Object>();
		mparam.put("txId", txId);
		List<MemberTemp> memberTempList = memberTempMapper.selectByExample(mparam);
		if(CollectionUtils.isEmpty(memberTempList)) {
			logger.info("[{}] memberTemp not found", txId);
			response.sendRedirect(conbindReturnUrl(returnUrlBase,returnUrlQuery,"401",cbcTxId));
			return null;
		}
		MemberTempExt tempExt = new MemberTempExt();
		BeanUtils.copyProperties(tempExt, memberTempList.get(0));
		if(tempExt.getBirthdate()!=null) {
	    		String yearStr = String.valueOf(Integer.valueOf(sdf1.format(tempExt.getBirthdate())) -1911);
	    		String monthStr = sdf2.format(tempExt.getBirthdate());
	    		String dateStr = sdf3.format(tempExt.getBirthdate());
	    		if(yearStr.length()==1) {
	    			yearStr = "00" + yearStr;
	    		}else if(yearStr.length()==2) {
	    			yearStr = "0" + yearStr;
	    		}
			tempExt.setBirthdateStr(yearStr+monthStr+dateStr);
		}
		model.addAttribute("memberTemp", tempExt);
		Member maskMember = new Member();
		BeanUtils.copyProperties(maskMember,tempExt);
		model.addAttribute("maskMember",MaskUtil.maskSensitiveInformation(maskMember));

		if(sr != null) {
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_140, null, null, HttpHelper.getRemoteIp(request));
		}

		String birthdate = sdf6.format(tempExt.getBirthdate());
		String account = encoder.encodeToString((tempExt.getUid().toUpperCase() + birthdate).getBytes("UTF-8"));

		Map<String, Object> mParam = new HashMap<>();
		mParam.put("account", account);
		List<Member> mList = memberMapper.selectByExample(mParam);


		if (mList.size() > 0) {
			Member member = mList.get(0);
			Map<String, Object> mpParam = new HashMap<>();
			mpParam.put("memberId", ValidatorHelper.limitNumber(member.getId()));
			List<MemberPrivacy> mpList = memberPrivacyMapper.selectByExample(mpParam);
			if(mpList.size() > 0) {
				model.addAttribute("agree", true);
			} else {
				model.addAttribute("agree", false);
			}
		} else {
			model.addAttribute("agree", false);
		}
		return "service-detail-new";
	}	
	
	
	
	/**
	 * Third Apply API ?????????????????????SP????????????
	 * @param request
	 * @param response
	 * @param model
	 * @throws IOException
	 */
	@PostMapping("/otheridentify/apply")
	public void postOtheridentifyApply(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		System.out.println("---@PostMapping /service/otheridentify/apply start---");
		Map<String,Object> params = new HashMap<String,Object>(); //??????????????????
		String secret_key = "5389b4c94a3a4017af299250688206ba"; //??????secret_key
		String jwtjsonstr = getBody(request);
		System.out.println(jwtjsonstr);
		String[] jwtjsonstrList = jwtjsonstr.split("[.]");
		/**
		 * ??????JWT????????????
		 */
		System.out.println("==jwtjsonstrList.length==:"+jwtjsonstrList.length);
		String unsigntoken = jwtjsonstrList[0]+"."+jwtjsonstrList[1];
		String payload = jwtjsonstrList[1];
		String jwtsignature = jwtjsonstrList[2];
		/**
		 * ?????? HMACSHA256 ({header}.{payload}, secret_key)
		 */
		String signature = HMACSHA256(unsigntoken.getBytes(),secret_key.getBytes());
		System.out.println("==jwtsignature==:"+jwtsignature);
		System.out.println("==signature   ==:"+signature);
		/**
		 * JWT????????????
		 */
		if(signature.equalsIgnoreCase(jwtsignature)) {
			System.out.println("==verify jwt==:"+true);
			JSONObject payloadObj = null;
			try {
				String payloadStrDecode = new String(Base64.getUrlDecoder().decode(payload),"UTF-8");
				System.out.println("==payload decode==\n"+payloadStrDecode);
				payloadObj = (JSONObject) JSONValue.parseWithException(payloadStrDecode);
			} catch (ParseException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(payloadObj!=null) {
				for (Object o : payloadObj.entrySet()) {
					@SuppressWarnings("rawtypes")
					Map.Entry e = (Map.Entry) o;
					String key = (String) e.getKey();
					String value = (String) e.getValue();
					params.put(key, value);
					System.out.println("key:"+key+", value="+value);
				}
			}
		}else {
			System.out.println("==verify jwt==:"+false);
	    		/**
	    		 * 401 Unauthorized JWT????????????
	    		 */
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return;
		}
				
		/**
		 * ????????????
		 */
		PortalService portalService = null;
		if(params!=null&&params.size()>0) {
			String txId = params.get("tx_id")==null?null:params.get("tx_id").toString();
			/**
			 * ???????????? tx_id
			 */
			if(txId==null||txId.trim().length()==0) {
				System.out.println("??????tx_id");
		    		/**
		    		 * 401 Unauthorized client_id?????????
		    		 */
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    			return;
			}
			/**
			 * client_id ??????
			 */
			String clientId = params.get("client_id")==null?null:params.get("client_id").toString();
			if(clientId==null) {
				System.out.println("client_id?????????");
		    		/**
		    		 * 401 Unauthorized client_id?????????
		    		 */
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    			return;
			}else {
                Map<String, Object> psparam = new HashMap<String, Object>();
                psparam.put("clientId", ValidatorHelper.removeSpecialCharacters(clientId));
                List<PortalService> portalServiceList = portalServiceMapper.selectByExample(psparam);
                if (portalServiceList == null || portalServiceList.size() == 0) {
                    System.out.println("????????????client_id??????");
                    /**
                     * 401 Unauthorized client_id?????????
                     */
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                } else {
                    portalService = portalServiceList.get(0);
                }
            }


            /**
             * ??????portal_service_scope???????????????resourceId
             */
            List<Integer> allowPrIdList = new ArrayList<Integer>();
            Map<String, Object> pssparam = new HashMap<String, Object>();
            pssparam.put("psId", ValidatorHelper.limitNumber(portalService.getPsId()));
            List<PortalServiceScope> portalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
            if (portalServiceScopeList != null && portalServiceScopeList.size() > 0) {
                for (PortalServiceScope psc : portalServiceScopeList) {
                    Map<String, Object> sparam = new HashMap<String, Object>();
                    sparam.put("scope", ValidatorHelper.removeSpecialCharacters(psc.getScope()));
                    List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
                    if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
                        if (!allowPrIdList.contains(portalResourceScopeList.get(0).getPrId())) {
                            allowPrIdList.add(portalResourceScopeList.get(0).getPrId());
                        }
                    }
                }
            } else {
                //403???portal_service_scope?????????
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            /**
             * ???????????? resource_id parameters ?????? resourceId
             */
            int level = 4;
            String parameters = params.get("resource_id") == null ? null : params.get("resource_id").toString();
            List<Integer> prIdList = new ArrayList<Integer>();
            List<PortalResourceExt> portalResourceExtList = new ArrayList<PortalResourceExt>();
            if (parameters != null && parameters.trim().length() > 0) {
                try {
                    byte[] parameterBytes = decoder.decode(parameters);
                    String[] pss;
                    pss = (new String(parameterBytes, "UTF-8")).split("\\:");
                    if (pss != null && pss.length > 0) {
                        boolean checkResourceIdExit = true;
                        for (String s : pss) {
                            System.out.println("==parameter==:" + s);
                            if (s != null && s.trim().length() > 0) {
								/*PortalResourceExample portalResourceExample = new PortalResourceExample();
								portalResourceExample.createCriteria().andResourceIdEqualTo(s.trim());*/
								Map<String,Object> rparam = new HashMap<String,Object>();
								rparam.put("resourceId", ValidatorHelper.removeSpecialCharacters(s.trim()));
								List<PortalResource> portalResourceList = portalResourceMapper.selectByExample(rparam);
								if(portalResourceList!=null&&portalResourceList.size()>0) {
									prIdList.add(portalResourceList.get(0).getPrId());
									try {
										PortalResourceExt bean = new PortalResourceExt();
										BeanUtils.copyProperties(bean, portalResourceList.get(0));
										portalResourceExtList.add(bean);
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									} catch (InvocationTargetException e) {
										e.printStackTrace();
									}
									
									if(portalResourceList.get(0).getLevel()<level) {
										level = portalResourceList.get(0).getLevel();
									}
									if(!allowPrIdList.contains(portalResourceList.get(0).getPrId())) {
										checkResourceIdExit = false;
									}
								}else {
									checkResourceIdExit = false;
								}
							}
						}
						if(!checkResourceIdExit) {
					    		/**
					    		 * 401 Unauthorized ???????????????
					    		 */
				    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				    			return;
						}
					}else {
				    		/**
				    		 * 401 Unauthorized ???????????????
				    		 */
			    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			    			return;
					}
				} catch (UnsupportedEncodingException e) {
			    		/**
			    		 * 401 Unauthorized ???????????????
			    		 */
		    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    			return;
				}
			}else {
		    		/**
		    		 * 401 Unauthorized ???????????????
		    		 */
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    			return;
			}
			
			/**
			 * as_id?????? (twid) 
			 */
			String asId = params.get("as_id")==null?null:params.get("as_id").toString();
			if(asId==null||asId.trim().length()==0) {
				System.out.println("??????as_id");
		    		/**
		    		 * 401 Unauthorized as_id?????????
		    		 */
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    			return;
			}
			
			/**
			 * token??????
			 */
			String token = params.get("token")==null?null:params.get("token").toString();
			if(token==null||token.trim().length()==0) {
				System.out.println("??????token");
		    		/**
		    		 * 401 Unauthorized token?????????
		    		 */
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    			return;
			}
			
			/**
			 * verification??????
			 */
			String verification = params.get("verification")==null?null:params.get("verification").toString();
			if(verification==null||verification.trim().length()==0) {
				System.out.println("??????verification");
		    		/**
		    		 * 401 Unauthorized verification?????????
		    		 */
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    			return;
			}
			
			/**
			 * verification_time
			 */
			String verificationTimeStr = params.get("verification_time")==null?null:params.get("verification_time").toString();
			if(verificationTimeStr==null||verificationTimeStr.trim().length()==0) {
				System.out.println("??????verification_time");
		    		/**
		    		 * 401 Unauthorized verification_time?????????
		    		 */
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    			return;
			}
			/**
			 * ??????????????????????????????
			 */
			
			/**
			 * 0. init parameter permission_ticket,secret_key
			 * 1. ??????????????? prIdList
			 * 2. ???????????? params
			 * 3. ???Thread????????????
			 */
			List<String> downloadSnList = new ArrayList<String>();
			String permission_ticket = UUID.randomUUID().toString();
			String apply_secret_key = UUID.randomUUID().toString().replace("-", "");
			int sp_wait_time = 0;
			/**
			 * ?????????????????????????????????????????????????????????????????????????????????
			 * ???????????????Thread????????????????????????????????????????????????????????????????????????????????????????????????
			 * 1. permission_ticket
			 * 2. secret_key
			 * 3. wait_time 
			 * 4. ctime
			 * 5. downloadSnList -----
			 * 6. etime ???????????????????????? ???????????? ??? ctime+waittime = etime, ??????????????????????????????table???data ----
			 * 7. pack zip file path ----> AES??????
			 * 8. ??????
			 * 9. dtime -----
			 */
			PortalServiceDownload psdbean = new PortalServiceDownload();
			psdbean.setPsId(portalService.getPsId());
			psdbean.setClientId(portalService.getClientId());
			psdbean.setPermissionTicket(permission_ticket);
			psdbean.setSecretKey(secret_key);
			psdbean.setCtime(new Date());
			psdbean.setStat(0);
			PortalResource portalResource = null;

			/**
			 * SP ??????
			 * ?????? insert ulog_api sp 4 ???????????? 1.??????(AS) 2.??????(AS) 3.??????(AS) 4. ??????(SP) 5. ??????(DP)
			 * 6.??????(SP) 7.????????????(AS) 8.??????, ??????(11), ????????????(12), ??????(13)
			 * 
			 * ????????????
			 * 11 ??????????????????????????????/?????? 
			 * 12 ??????????????????????????????
			 * 13 ??????????????????????????????
			 * 14 ?????????????????? MyData ??????????????????????????????
			 * 15 ????????????????????????
			 * 16 ?????????????????????????????????
			 * 17 ???????????????MyData ?????????????????????????????????
			 * 
			 * ?????????
			 * 21 ??????????????????????????????/?????? 
			 * 22 ??????????????????????????????
			 * 23 ??????????????????????????????
			 * 24 ?????????????????? MyData ??????????????????????????????
			 * 25 ????????????????????????
			 * 26 ?????????????????????????????????
			 * 27 ???????????????MyData ?????????????????????????????????
			 */
			/*UlogApi record0 = new UlogApi();
			record0.setProviderKey(userInfoEntity.getAccount());
			record0.setUserName(userInfoEntity.getName());
			record0.setUid(userInfoEntity.getUid());
			record0.setClientId(portalService.getClientId());
			record0.setAuditEvent(21);
			record0.setCtime(new Date());
			record0.setIp("117.56.91.59");
			ulogApiMapper.insertSelective(record0);
			record0.setCtime(new Date());
			record0.setAuditEvent(22);
			ulogApiMapper.insertSelective(record0);
			record0.setCtime(new Date());
			record0.setAuditEvent(23);
			ulogApiMapper.insertSelective(record0);
			record0.setAuditEvent(24);
			ulogApiMapper.insertSelective(record0);*/
			String ip = HttpHelper.getRemoteIp(request);
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 21, ip);
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 22, ip);
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 23, ip);
			ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 24, ip);
			if(prIdList!=null&&prIdList.size()>0) {
				for(Integer prId:prIdList) {
					System.out.println("service apply prId="+prId);
					portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
					List<PortalResourceScope> prsList = null;
					if (portalResource != null) {
						Map<String,Object> sparam = new HashMap<String,Object>();
						sparam.put("prId", ValidatorHelper.limitNumber(prId));
						prsList = portalResourceScopeMapper.selectByExample(sparam);
					}
					/**
					 * ????????????
					 * 11 ??????????????????????????????/?????? 
					 * 12 ??????????????????????????????
					 * 13 ??????????????????????????????
					 * 14 ?????????????????? MyData ??????????????????????????????
					 * 15 ????????????????????????
					 * 16 ?????????????????????????????????
					 * 17 ???????????????MyData ?????????????????????????????????
					 * 
					 * ?????????
					 * 21 ??????????????????????????????/?????? 
					 * 22 ??????????????????????????????
					 * 23 ??????????????????????????????
					 * 24 ?????????????????? MyData ??????????????????????????????
					 * 25 ????????????????????????
					 * 26 ?????????????????????????????????
					 * 27 ???????????????MyData ?????????????????????????????????
					 */
					String scopeStr = "";
					try {
						if (prsList != null && prsList.size() > 0) {
							for (PortalResourceScope prs : prsList) {
								scopeStr = scopeStr + prs.getScope().trim() + " ";
							}
							scopeStr = scopeStr.trim();
						}
					} catch (Exception ex) {
					}
					/*UlogApi record = new UlogApi();
					record.setProviderKey(userInfoEntity.getAccount());
					record.setUserName(userInfoEntity.getName());
					record.setUid(userInfoEntity.getUid());
					record.setClientId("CLI.mydata.portal");
					record.setAuditEvent(11);
					record.setResourceId(portalResource.getResourceId());
					record.setScope(scopeStr);
					record.setCtime(new Date());
					record.setIp("117.56.91.59");
					ulogApiMapper.insertSelective(record);
					record.setCtime(new Date());
					record.setAuditEvent(12);
					ulogApiMapper.insertSelective(record);
					record.setCtime(new Date());
					record.setAuditEvent(13);
					ulogApiMapper.insertSelective(record);
					record.setAuditEvent(14);
					ulogApiMapper.insertSelective(record);*/
					ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_250, null, 11, ip);
					ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 12, ip);
					ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 13, ip);
					ulogUtil.recordFullByPr(sr, portalService, txId, null, null, null, null, 14, ip);
					/**
					 * ??????????????? action_type = 1,?????????????????? action_type = 0 or null
					 */
					if (portalResource.getActionType() == null || portalResource.getActionType() == 0) {
						/**
						 * ???????????????????????? action_type == null || action_type == 0 
						 */
						
					}
				}
			}
		}else {
			System.out.println("???????????????");
	    		/**
	    		 * 401 Unauthorized ???????????????
	    		 */
    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    			return;
		}
		
		/**
		 * ???????????????
		 */
		
		
		/*response.setContentType("text/plain;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();*/
		
	}
	
	/**
	 * ??????3 ???????????????????????????
	 * request:
	 * client_id
	 * tx_id
	 * 
	 * respone:
	 * tx_id
	 * salt
	 * 
	 * ??????3.1 ??????????????????
	 * client_id
	 * data
	 * pkcs7
	 * verification_type (?????????api??????)
	 * as_id (?????????api??????)
	 * 
	 * success:
	 * redirect to ???????????????
	 * fail:
	 * error code
	 * 
	 * @param clientId
	 * @param request
	 * @param response
	 * @param model
	 * @throws IOException
	 */
	@PostMapping("/spsignature/{client_id:.+}")
	public void postSpsignature(@PathVariable("client_id") String clientId,HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		System.out.println("clientId:"+clientId);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		Map<String,Object> params = new HashMap<String,Object>(); //??????????????????
		String requestBodyStr = getBody(request);
		JSONObject payloadObj = null;
		String txId = null;
		String salt = null;
		String verificationType = "CER";
		String asId = null;
		try {
			payloadObj = (JSONObject) JSONValue.parseWithException(requestBodyStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(payloadObj!=null) {
			for (Object o : payloadObj.entrySet()) {
				@SuppressWarnings("rawtypes")
				Map.Entry e = (Map.Entry) o;
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				params.put(key, value);
				System.out.println("key:"+key+", value="+value);
			}
		}
		/**
		 * ???????????? client_id
		 */
		PortalService portalService = null;
		Map<String,Object> psparam = new HashMap<String,Object>();
		psparam.put("clientId", clientId);
		List<PortalService> portalServiceList = portalServiceMapper.selectByExample(psparam);
		if(portalServiceList!=null&&portalServiceList.size()>0) {
			portalService = portalServiceList.get(0);
		}else {
			logger.info("[{}] client_id ??????????????? httpstatus 403", clientId);
			/**
			 * client_id ??????????????? httpstatus 403
			 */
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=UTF-8");
			return;
		}

		String ip = HttpHelper.getRemoteIp(request);
		if(params.get("tx_id")!=null) {
			txId = params.get("tx_id").toString();
			if(params.get("data")!=null&&params.get("pkcs7")!=null) {
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_130, null, null, ip);
				Base64.Decoder base64Decoder = Base64.getDecoder();
				// ??????????????????????????????
				byte[] encryptedData = params.get("data").toString().getBytes();
		        
		        // decode & decrypt data
				/**
				 * ????????????????????????SP??????????????????portal_service client_secret & cbc_iv ?????? verification_type???????????? CER(???????????????) ???
				 * ??????????????????????????? as_id ??? verification_type?????????????????????????????? secret_key & cbc_iv ??????????????? verification_type ??? as_id
				 * 1. as_id = twid
				 * 
				 */
		        String secretKey = portalService.getClientSecret()+portalService.getClientSecret();
		        byte[] dataJson;
		        try {
		        		dataJson = CBCUtil.decrypt_cbc(base64Decoder.decode(encryptedData),secretKey,portalService.getCbcIv());
			    } catch (Exception ex) {
					logger.info("[{}] 401 dataJson ????????????", clientId);
			    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					return;
			    }
		        // parse data to Map
		        Map<String, String> data;
		        try {
		        	JSONParser parser = new JSONParser();
    				JSONObject object = (JSONObject) parser.parse(new String(dataJson));
    				ObjectMapper om = new ObjectMapper();
    		        data = om.convertValue(object, Map.class);
		        } catch (Exception ex) {
					logger.info("[{}] 400 dataJson ???????????????", clientId);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					return;
			    }

		        if(params.get("as_id")!=null) {
		        	asId = params.get("as_id").toString();
		        }
		        if(params.get("verification_type")!=null) {
		        	verificationType = params.get("verification_type").toString();
		        }
		        //???????????????CER
		        if(verificationType.equalsIgnoreCase("CER")&&asId!=null) {
					logger.info("[{}] 400 dataJson ???????????????", clientId);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					return;
		        }
		        
		        String pid = data.get("pid");
		        String holder = data.get("holder");
		        String birthday = data.get("birthday");
		        String email = data.get("email");
		        String mobile = data.get("mobile");
		        String asOptional = data.get("as_optional");
		        salt = data.get("salt");
    		        
		        // data ????????????????????????????????????????????????????????????
		        if(StringUtils.isEmpty(salt) || StringUtils.isEmpty(pid) || StringUtils.isEmpty(birthday)) {
				logger.info("[{}] 400 data ?????????????????????????????????????????????????????????", clientId);
		        		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					return;
		        }

				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_150, null, null, ip);
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_160, null, null, ip);
				// ?????? PKCS7 ?????????
				PKCS7 pkcs7 = new PKCS7(new DerInputStream(base64Decoder.decode(params.get("pkcs7").toString())));
				 // ??? PKCS7 ????????????????????????
				X509Certificate[] certs = pkcs7.getCertificates();
				// ????????????????????????
		        X509Certificate prevCert = null;
		        for(int i=0; i<certs.length; i++) {
		            X509Certificate cert = certs[i];

		            try {
			            	 // ??????????????????????????????
			            	cert.checkValidity();
			            	
			            	// ??????????????????????????????????????????
				        if(prevCert != null) {
				            prevCert.verify(cert.getPublicKey());
				        }
		            } catch (Exception ex) {
						logger.info("[{}] 403 ???????????????", clientId);
			    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/json; charset=UTF-8");
						return;
				    }
		            if(asId!=null&&(asId.equalsIgnoreCase("twid")||asId.equalsIgnoreCase("cht"))) {
		            		//UNDO
		            }else {
			            // ???CA??????????????????????????? OCSP or CRL???
			            String dn = cert.getSubjectDN().toString();
			            String serialNumber = (dn.split(",")[0]).split("=")[1];
			            boolean verify = icscUtils.verifyCert(pid, serialNumber);
			            if(!verify) {
							logger.info("[{}] 409 ???????????????", clientId);
				            	response.setStatus(HttpServletResponse.SC_CONFLICT);
				    			response.setCharacterEncoding("UTF-8");
				    			response.setContentType("application/json; charset=UTF-8");
				    			return;
			            }
		            }

		            prevCert = cert;
		        }
		        ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_161, null, null, ip);
		        // ?????????
		        if(pkcs7.verify(encryptedData) == null) {
					logger.info("[{}] 401 ???????????????", clientId);
		        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					return;
		        }
		        
		        Map<String,Object> saltParam = new HashMap<String,Object>();
		        saltParam.put("txId", ValidatorHelper.removeSpecialCharacters(txId));
		        saltParam.put("salt", ValidatorHelper.removeSpecialCharacters(salt));
		        List<PortalServiceSalt> saltList = portalServiceSaltMapper.selectByExample(saltParam);
		        // tx_id???salt????????????
		        if(CollectionUtils.isEmpty(saltList)) {
					logger.info("[{}] 403 salt ?????????", clientId);
		        		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					return;
		        }
		        PortalServiceSalt saltEntity = saltList.get(0);
		        // ???????????????salt??????????????????15??????
		        // ??????1hrs = 3600 sec
		        if(addSecond(saltEntity.getCtime(), 3600).before(new Date())) {
					logger.info("[{}] 408 ??????", clientId);
		        		response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					return;
		        }
		        
		        if(asId!=null&&asId.equalsIgnoreCase("twid")) {
					/**
					 * ????????????????????????????????????API ??????????????????????????????401
					 * AES/CBC/PADDING5 
					 * download ?????? client_id = CLI.wwSSxxEEdd
					 * header.encrypted_key.initialization_vector.ciphertext.authentication_tag
					 * ciphertext
					 * {
					 * 	"tx_id": ${tx_id},
					 * 	"data": {
					 * 	"pid": "${???????????????}",
					 * 	"holder": "${??????}",
					 * 	"as_optional":"${????????????????????????}",
					 *  "verification_type":"${verification_type}"
					 * 	},
					 * 	"pkcs7": ${base64_encoded_pkcs7file-data}
					 * }
					 */
		        	
		        	boolean checkPostSignatureVerifyAPIUrlTwid = twcaUtils.postSignatureVerifyAPIUrlTwid(clientId, txId, pid, holder, asOptional, verificationType, params.get("pkcs7").toString());
		        	if(!checkPostSignatureVerifyAPIUrlTwid) {
						logger.info("[{}] 401 ???????????????", clientId);
			        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/json; charset=UTF-8");
						return;
		        	}
		        }else if(asId!=null&&asId.equalsIgnoreCase("cht")) {
		        	//???????????????????????????
		        	boolean checkPostSignatureVerifyAPIUrlCht = twcaUtils.postSignatureVerifyAPIUrlCht(clientId, txId, pid, holder, asOptional, verificationType, params.get("pkcs7").toString());
		        	if(!checkPostSignatureVerifyAPIUrlCht) {
						logger.info("[{}] 401 ???????????????", clientId);
			        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/json; charset=UTF-8");
						return;
		        	}
		        }

				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_151, null, null, ip);

				MemberTemp memberTemp = new MemberTemp();
				memberTemp.setUid(ValidatorHelper.removeSpecialCharacters(pid));
				if(StringUtils.isNotEmpty(birthday)) {
					Date birthdate = new SimpleDateFormat("yyyy/MM/dd").parse(birthday);
					memberTemp.setBirthdate(ValidatorHelper.limitDate(birthdate));
				}
				if(StringUtils.isNotEmpty(holder)) memberTemp.setName(ValidatorHelper.removeSpecialCharacters(holder));
				if(StringUtils.isNotEmpty(email)) memberTemp.setEmail(ValidatorHelper.removeSpecialCharacters(email));
				if(StringUtils.isNotEmpty(mobile)) memberTemp.setMobile(ValidatorHelper.removeSpecialCharacters(mobile));
				memberTemp.setTxId(ValidatorHelper.removeSpecialCharacters(txId));
				if(verificationType!=null&&StringUtils.isNotEmpty(verificationType)) memberTemp.setVerificationType(ValidatorHelper.removeSpecialCharacters(verificationType));
				if(asId!=null&&StringUtils.isNotEmpty(asId)) memberTemp.setAsId(ValidatorHelper.removeSpecialCharacters(asId));
				memberTempMapper.insertSelective(memberTemp);
	        		
		        response.setStatus(HttpServletResponse.SC_OK);
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json; charset=UTF-8");
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_131, null, null, ip);
			}else {
				/**
				* tx_id check create salt
				*/
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_120, null, null, ip);
				
				salt = SequenceHelper.createUUID();
				
				PortalServiceSalt saltEntity = new PortalServiceSalt();
				saltEntity.setPsId(ValidatorHelper.limitNumber(portalService.getPsId()));
				saltEntity.setTxId(ValidatorHelper.removeSpecialCharacters(txId));
				saltEntity.setSalt(ValidatorHelper.removeSpecialCharacters(salt));
				saltEntity.setCtime(new Date());
				portalServiceSaltMapper.insertSelective(saltEntity);
				
				String outStr = "{\"tx_id\":\""+ txId +"\",\"salt\":\""+salt+"\"}";
				response.setStatus(HttpServletResponse.SC_OK);
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
		        out.print(outStr);
				out.close();
				ulogUtil.recordFullByPr(sr, portalService, txId, null, null, ActionEvent.EVENT_121, null, null, ip);
			}
		}else {
			logger.info("[{}] 403 tx_id is null", clientId);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=UTF-8");
			return;
		}
	}
	
	
	/**
	 * MyData??????????????????-->?????????????????????
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/counter/{client_id:.+}")
	public String getServiceIdPageByCounter(@PathVariable("client_id") String clientId,
			HttpServletRequest request, 
			HttpServletResponse response, 
			ModelMap model) throws Exception {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null && sr.getMember()!=null){
			Member maskMember = new Member();
			BeanUtils.copyProperties(maskMember,SessionMember.getSessionMemberToMember(sr.getMember()));
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
		}

		List<String> tmpscopeList = new ArrayList<String>();		
		int level = 4;
		/**
		 * ???????????? client_id
		 */
		PortalService portalService = null;
		Map<String,Object> psparam = new HashMap<String,Object>();
		psparam.put("clientId", clientId);
		List<PortalService> portalServiceList = portalServiceMapper.selectByExample(psparam);
		String cbcTxId = "";
		if(portalServiceList!=null&&portalServiceList.size()>0) {
			portalService = portalServiceList.get(0);
		}else {
			//401???????????? client_id???
			return null;
		}
        PortalServiceCategory portalServiceCategory = portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getCateId()));
        model.addAttribute("portalServiceCategory", portalServiceCategory);

        Map<String, Object> pcsParam = new HashMap<>();
        pcsParam.put("psId", ValidatorHelper.limitNumber(portalService.getPsId()));
		List<PortalCounterSubExt> counterSubExtList = portalCounterSubMapperExt.selectExt(pcsParam);

        /**
         * ??????portal_service_scope???????????????resourceId
         */
		Boolean isDPMaintain = false;
        Boolean moecaCheck = true;
        for(PortalCounterSubExt counterSubExt : counterSubExtList) {
        	for(PortalCounterSubScopeExt subScopeExt : counterSubExt.getSubScopeExtList()) {
        		for(PortalResourceExt portalResourceExt : subScopeExt.getDpList()) {
					if( !(portalResourceExt.getMoecaCheck()!=null && portalResourceExt.getMoecaCheck() == 1)) {
						moecaCheck = false;
					}
					if((portalResourceExt.getCheckStat() != null && portalResourceExt.getCheckStat() != 0) || maintainUtils.checkInMaintain(portalResourceExt.getPrId())) {
						isDPMaintain = true;
					}

					Map<String, Object> param3 = new HashMap<String, Object>();
					param3.put("prId", ValidatorHelper.limitNumber(portalResourceExt.getPrId()));
					param3.put("idAsc", true);
					List<PortalResourceField> portalResourceFieldList = portalResourceFieldMapper.selectByExample(param3);
					try {
						portalResourceExt.setFieldList(portalResourceFieldList);
						PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceExt.getProviderId()));
						if (pp != null) {
							portalResourceExt.setProviderName(pp.getName());
						}
						if (pp.getType() != null) {
							portalResourceExt.setType(pp.getType());
						} else {
							portalResourceExt.setType(0);
						}
						Map<String, Object> prpquery = new HashMap<String, Object>();
						prpquery.put("prId", ValidatorHelper.limitNumber(portalResourceExt.getPrId()));
						List<PortalResourceParam> portalResourceParamList = portalResourceParamMapper.selectByExample(prpquery);
						if (portalResourceParamList != null && portalResourceParamList.size() > 0) {
							List<Param> paramList1 = new ArrayList<Param>();
							for (PortalResourceParam prp : portalResourceParamList) {
								Param p = new Param();
								String key = prp.getParamName();
								String val = prp.getParamDesc();
								p.setName(key);
								p.setDesc(val);
								p.setIsOption(prp.getIsOption());
								if (prp.getParamNameDesc() != null && prp.getParamNameDesc().trim().length() > 0) {
									p.setNameDesc(prp.getParamNameDesc());
								}
								if (key.equalsIgnoreCase("locationHsnCd") || key.equalsIgnoreCase("etd_location_hsn_cd")) {
									p.setType("location");
								} else if (key.equalsIgnoreCase("etd_location_town_cd")) {
									p.setType("town");
								} else if (key.equalsIgnoreCase("etd_location_vill_cd")) {
									p.setType("vill");
								} else if (key.equalsIgnoreCase("etd_apply_items")) {
									p.setType("etd_apply_items");
								} else if (key.equalsIgnoreCase("INQ_ID")) {
									p.setType("INQ_ID");
								} else if (key.equalsIgnoreCase("PR_VAL_TP")) {
									p.setType("PR_VAL_TP");
								} else if (key.equalsIgnoreCase("caseYears") || key.equalsIgnoreCase("etd_case_years") || key.equalsIgnoreCase("INQ_YR")) {
									p.setType("year");
									p.setYearParam(YearParamUtil.getYearParam(portalResourceExt.getResourceId()));
								} else if (key.equalsIgnoreCase("ExamYear")) {
									p.setType("year1");
								} else if (key.equalsIgnoreCase("qcartype")) {
									p.setType("cartype");
								} else if (key.equalsIgnoreCase("qlic")) {
									p.setType("lic");
								} else if (key.endsWith("date") || key.endsWith("day")) {
									p.setType("date");
								} else if (key.equalsIgnoreCase("verifycode")) {
									p.setType("enctext");
								} else if (key.equalsIgnoreCase("mobile")) {
									p.setType("mobile");
								} else {
									p.setType("text");
								}
								paramList1.add(p);
							}
							portalResourceExt.setParamList(paramList1);
						}
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			}
		}
        model.addAttribute("counterSubExtList", counterSubExtList);
		model.addAttribute("maintain", isDPMaintain);

        /**
         * ???????????? parameters ?????? resourceId
         */
        //level
        model.addAttribute("level", level);
		model.addAttribute("moecaCheck", moecaCheck);
        //portalService
        PortalServiceExt portalServiceExt = new PortalServiceExt();
        try {
            BeanUtils.copyProperties(portalServiceExt, portalService);
            PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceExt.getProviderId()));
            portalServiceExt.setProviderName(portalProvider.getName());
			if(portalProvider.getType()!=null) {
				portalServiceExt.setType(portalProvider.getType());
			}else {
				portalServiceExt.setType(0);
			}
        } catch (IllegalAccessException e1) {
            logger.error(e1.getLocalizedMessage(), e1);
        } catch (InvocationTargetException e1) {
			logger.error(e1.getLocalizedMessage(), e1);
        }
        model.addAttribute("portalService", portalServiceExt);
        /**
         * ???????????????
         */
        //????????????
		//??????????????????????????????504???????????????????????????
		model.addAttribute("sp_return_url_504", frontendContextUrl+"/sp/service/counter");
		
		return "v2/counter-detail";
		

		//return "service-detail-counter";
	}	
	
	public static String getBody(HttpServletRequest request) throws IOException {
	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;
	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
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
	
	public static String HMACSHA256(byte[] data, byte[] key){
	      try  {
	         SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
	         Mac mac = Mac.getInstance("HmacSHA256");
	         mac.init(signingKey);
	         return byte2hex(mac.doFinal(data));
	      } catch (NoSuchAlgorithmException e) {
	         e.printStackTrace();
	      } catch (InvalidKeyException e) {
	        e.printStackTrace();
	      }
	      return null;
	}
	
	public static String byte2hex(byte[] b){
	    StringBuilder hs = new StringBuilder();
	    String stmp;
	    for (int n = 0; b!=null && n < b.length; n++) {
	        stmp = Integer.toHexString(b[n] & 0XFF);
	        if (stmp.length() == 1)
	            hs.append('0');
	        hs.append(stmp);
	    }
	    return hs.toString().toUpperCase();
	}	
	
	public static String encodeFileToBase64Binary(String fileName) throws Exception {
		byte[] bytes = Files.readAllBytes(Paths.get(fileName));
		String encodedString = Base64.getUrlEncoder().encodeToString(bytes);
		return encodedString;
	}
	
	public static String readFileAsString(String fileName) throws Exception {
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
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
	* ????????????
	* @param key ???????????????
	* @return Key ??????
	*/
	public static Key toKey(String key) throws Exception {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(key);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, KEY_ALGORITHM);
		return originalKey;
	}	
	
	/**
	* ????????????
	* @param data ???????????????
	* @param key ??????
	* @return byte[] ??????????????????
	*/
	public static byte[] encrypt(byte[] data, String key) throws Exception {
		// ????????????
		Key k = toKey(key);
		// ?????????
		Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0VDQi9QS0NTNVBhZGRpbmc=")));
		// ?????????????????????????????????
		cipher.init(Cipher.ENCRYPT_MODE, k);
		// ????????????
		return cipher.doFinal(data);
	}
	
	/**
	* ????????????
	* @param data ???????????????
	* @param key ??????
	* @return byte[] ??????????????????
	*/
	public static byte[] decrypt(byte[] data, String key) throws Exception {
		// ????????????
		Key k = toKey(key);
		// ?????????
		Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0VDQi9QS0NTNVBhZGRpbmc=")));
		// ?????????????????????????????????
		cipher.init(Cipher.DECRYPT_MODE, k);
		// ????????????
		return cipher.doFinal(data);
	}
	
	private Date addSecond(Date dt, int second) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		c.add(Calendar.SECOND, second);
		dt = c.getTime();
		return dt;
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
		// ??????
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
		// ????????????
		IvParameterSpec iv = new IvParameterSpec(ivstr.getBytes("UTF-8"));
		// ?????????
		Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0NCQy9QS0NTNVBBRERJTkc=")));
		// ?????????????????????????????????
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		// ????????????
		return cipher.doFinal(data);
	}
}
