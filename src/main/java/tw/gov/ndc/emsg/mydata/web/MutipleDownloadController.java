package tw.gov.ndc.emsg.mydata.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;

import net.lingala.zip4j.exception.ZipException;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.mapper.PortalBatchDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBoxMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalProviderMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceFieldMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceParamMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.mapper.UlogMapper;
import tw.gov.ndc.emsg.mydata.mapper.VerifyMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.util.MaintainUtils;
import tw.gov.ndc.emsg.mydata.util.MaskUtil;
import tw.gov.ndc.emsg.mydata.util.YearParamUtil;

@Controller
@RequestMapping("/mutipledownload")
public class MutipleDownloadController {
	private static final Logger logger = LoggerFactory.getLogger(MutipleDownloadController.class);
	private final Base64.Encoder encoder = Base64.getEncoder();
	private final Base64.Decoder decoder = Base64.getDecoder();
	private static DecimalFormat formatter = new DecimalFormat("#.#");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("年MM月dd日HH時mm分ss秒");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private static final int BUFFER_SIZE = 4096;
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
	
	@Value("${app.download.path.temp}")
	private String downloadPath;	
	
	@Autowired
	private PortalResourceMapper portalResourceMapper;	
	@Autowired
	private UlogMapper ulogMapper;
	@Autowired
	private VerifyMapper verifyMapper;
	@Autowired 
	private PortalBoxMapper portalBoxMapper;
	@Autowired
	private PortalResourceDownloadMapper portalResourceDownloadMapper;	
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceFieldMapper portalResourceFieldMapper;
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;
	//@Autowired
	//private NonceHelper nonceHelper;
	@Autowired
	private PortalBatchDownloadMapper portalBatchDownloadMapper;
	@Autowired
	private PortalResourceParamMapper portalResourceParamMapper;
    @Autowired
    private MaintainUtils maintainUtils;
	
	@GetMapping("/list")
	public String mutipleDownloadList(ModelMap model,HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
        if (session != null && sr != null) {
        		List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
            if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
                sr.setBoxcheck(true);
            } else {
                sr.setBoxcheck(false);
            }
            session.setAttribute(SessionRecord.SessionKey, sr);  
        }
		List<String> scopeList = new ArrayList<String>();

		List<PortalResourceExt> prList = portalResourceExtMapper.selectPortalResourceWithCateName();
		List<PortalResourceExt> prExtList = new ArrayList<PortalResourceExt>();
		if (prList != null && prList.size() > 0) {
			for (PortalResource pr : prList) {
				PortalResourceExt prExt = new PortalResourceExt();
				BeanUtils.copyProperties(prExt, pr);
	    			if(maintainUtils.checkInMaintain(pr.getPrId())) {
	    				prExt.setCheckStat(9);
	    			}
				PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(pr.getProviderId()));
				if (pp != null) {
					prExt.setProviderName(pp.getName());
				}
				if(pp.getType()!=null) {
					prExt.setType(pp.getType());
				}else {
					prExt.setType(0);
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
					logger.debug("paramList size {}",paramList.size());
				}
				prExtList.add(prExt);
			}
		}
		model.addAttribute("prExtList", prExtList);
		
		return "multiple-downloads";
	}
	
	@GetMapping("/download/{prIdEncode}")
	public String mutipleDownloadReadyDown(@PathVariable("prIdEncode") String prIdEncode,ModelMap model,HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
		String[] prIdList = (new String(Base64.getUrlDecoder().decode(prIdEncode.getBytes("UTF-8")),"UTF-8")).split("[,]"); 
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr!=null && sr.getMember()!=null){
			Member maskMember = new Member();
			BeanUtils.copyProperties(maskMember,SessionMember.getSessionMemberToMember(sr.getMember()));
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			logger.debug("mask member in multi-download -> {}" ,maskMember);
		}
		if(sr!=null && sr.getMember()!=null) {
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
		}

		List<String> scopeList = new ArrayList<String>();
		PortalResourceExt prExt = new PortalResourceExt();
		List<PortalResourceExt> finalPrExtList = new ArrayList<PortalResourceExt>();
		if(prIdList!=null&&prIdList.length>0) {
			List<String> needScopeList = new ArrayList<String>();
			List<String> newScopeList = new ArrayList<String>();
			List<Param> paramList = new ArrayList<Param>();
			boolean checkMoeca = true;
			boolean checkStat = true;
			for(String s:prIdList) {
				PortalResourceExt ext = new PortalResourceExt();
				List<Param> extParamList = new ArrayList<Param>();
				PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(Integer.valueOf(s)));
				if(portalResource!=null) {
					BeanUtils.copyProperties(ext, portalResource);
					PortalProvider pp = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
					if (pp != null) {
						ext.setProviderName(pp.getName());
					}
					if(pp.getType()!=null) {
						prExt.setType(pp.getType());
					}else {
						prExt.setType(0);
					}
					if(prExt.getLevel()==null||prExt.getLevel()>portalResource.getLevel()) {
						prExt.setLevel(portalResource.getLevel());
					}
					if(portalResource.getMoecaCheck()==null||portalResource.getMoecaCheck()==0) {
						checkMoeca = false;
					}
					if(portalResource.getCheckStat()==null||portalResource.getCheckStat()==1) {
						checkStat = false;
					}
					/*PortalResourceScopeExample portalResourceScopeExample = new PortalResourceScopeExample();
					portalResourceScopeExample.createCriteria().andPrIdEqualTo(portalResource.getPrId());*/
					Map<String,Object> sparam = new HashMap<String,Object>();
					sparam.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
					List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);				
					if (portalResourceScopeList != null && portalResourceScopeList.size() > 0) {
						if (scopeList != null && scopeList.size() > 0) {
							for (String sc : scopeList) {
								if (!needScopeList.contains(sc)) {
									needScopeList.add(sc);
								}
							}
						}
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
						if (scopeList != null && scopeList.size() > 0) {
							for (String sc : scopeList) {
								if (!needScopeList.contains(sc)) {
									needScopeList.add(sc);
								}
								if (!newScopeList.contains(sc)) {
									newScopeList.add(sc);
								}
							}
						}
					}
					
		            Map<String, Object> param3 = new HashMap<String, Object>();
		            param3.put("prId", ValidatorHelper.limitNumber(ext.getPrId()));
		            param3.put("idAsc", true);
		            List<PortalResourceField> portalResourceFieldList = portalResourceFieldMapper.selectByExample(param3);					
					if(portalResourceFieldList!=null&&portalResourceFieldList.size()>0) {
						ext.setFieldList(portalResourceFieldList);
					}
					//finalPrExtList.add(ext);
					Map<String, Object> prpquery = new HashMap<String, Object>(); 
					prpquery.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
					List<PortalResourceParam> portalResourceParamList = portalResourceParamMapper.selectByExample(prpquery);
					if(portalResourceParamList!=null&&portalResourceParamList.size()>0) {
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
									p.setYearParam(YearParamUtil.getYearParam(portalResource.getResourceId()));
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
						    extParamList.add(p);
						}
						ext.setParamList(extParamList);				
					}
					finalPrExtList.add(ext);
				}
			}
			prExt.setParamList(paramList);
			if(checkMoeca) {
				prExt.setMoecaCheck(1);
			}else {
				prExt.setMoecaCheck(0);
			}
			if(checkStat) {
				//正常
				prExt.setCheckStat(0);
			}else {
				//異常
				prExt.setCheckStat(1);
			}
		}		
		model.addAttribute("pr", prExt);
		model.addAttribute("prList", finalPrExtList);
		//prIdEncode
		model.addAttribute("prIdEncode", prIdEncode);
		return "multiple-downloads-ready";
	}
	
	///batch/download
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
	 */
	@GetMapping("/batch/download/{batchId}")
	public void postBoxDownload(@PathVariable("batchId") Integer batchId, HttpServletRequest request,
			HttpServletResponse response)
			throws SocketException, IOException, NoSuchAlgorithmException, KeyManagementException, ZipException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if (sr != null) {
			PortalBatchDownload download = portalBatchDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(batchId));
			// type 1
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
				logger.info("File exist > {}", localFile.getAbsolutePath());
			} else {
				logger.info("File not exist > {}", localFile.getAbsolutePath());
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
			PortalBatchDownload download1 = new PortalBatchDownload();
			download1.setId(ValidatorHelper.limitNumber(download.getId()));
			download1.setStat(1);
			portalBatchDownloadMapper.updateByPrimaryKeySelective(download1);

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
