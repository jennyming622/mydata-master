package tw.gov.ndc.emsg.mydata.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;

import com.riease.common.enums.ActionEvent;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.util.RandomUtils;
import com.riease.common.util.SslUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.mapper.PortalBatchDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBoxMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.type.DownloadType;
import tw.gov.ndc.emsg.mydata.type.RequestType;
import tw.gov.ndc.emsg.mydata.type.SendType;
import tw.gov.ndc.emsg.mydata.type.VerificationType;
import tw.gov.ndc.emsg.mydata.util.DigestUtil;
import tw.gov.ndc.emsg.mydata.util.MailUtil;
import tw.gov.ndc.emsg.mydata.util.SMSUtil;
import tw.gov.ndc.emsg.mydata.util.SendLogUtil;
import tw.gov.ndc.emsg.mydata.util.TokenUtils;
import tw.gov.ndc.emsg.mydata.util.UlogUtil;
import tw.gov.ndc.emsg.mydata.util.ValidateHelper;

public class ResourceApplyThread extends Thread {
	private static Logger logger = LoggerFactory.getLogger(ResourceApplyThread.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy年MM月dd日HH時mm分");
	private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy/M/d HH:mm");
	private static final int BUFFER_SIZE = 4096;
	private static ObjectMapper om = new ObjectMapper();
	/**
	 * 檔案保存期限(小時)
	 */
	private static int fileStoreTime = 8;
    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder =  Base64.getDecoder();
    
	private String downloadPath;
	/**
	 * Tx Param
	 */
	private String txId;
	private VerificationType verificationType;
	private Integer psId;
	private Integer psdId;
	private Integer batchId;
	private Integer prId;
	private UlogUtil ulogUtil;
	private String downloadSn;
	private String transactionUid;
	private Integer code = -1;
	private Integer boxId;
	private Integer waitTime = 0;
	private PortalResourceDownload prd;
	private PortalService ps;
	private SendLogUtil sendLogUtil;
	/**
	 * session data
	 */
	private Map<String, Object> params;
	private HttpSession session;
	private SessionRecord sr;
	/**
	 * FTP Param
	 */
	private String ftpHost;
	private String ftpPort;
	private String ftpUsername;
	private String ftpPassword;
	private String ftpPath;	
	private String ftpSecretkey;
	private String ftpIv;	
	/*
	 * Mapper
	 */
	private PortalResourceMapper portalResourceMapper;
	private PortalResourceScopeMapper portalResourceScopeMapper;
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	private PortalResourceExtMapper portalResourceExtMapper;
	private PortalBoxMapper portalBoxMapper;
	private PortalBatchDownloadMapper portalBatchDownloadMapper;
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	public void run() {
		transactionUid = UUID.randomUUID().toString();
		String prefix = "";
		if(psId == null) {
			prefix = "[" + transactionUid + "]";
		} else {
			prefix = "[" + txId + "][" + transactionUid + "]";
		}

		try {

			downloadSn = UUID.randomUUID().toString();
			PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
			List<PortalResourceScope> prsList = null;
			if (portalResource != null) {
				Map<String,Object> sparam = new HashMap<String,Object>();
				sparam.put("prId", prId);
				prsList = portalResourceScopeMapper.selectByExample(sparam);
			}
			/**
			 * 資料集：
			 * 11 申請：您申請此筆資料/服務
			 * 12 同意：您同意服務條款
			 * 13 驗證：您完成身分驗證
			 * 14 傳輸：您同意 MyData 傳送資料給服務提供者
			 * 15 儲存：您下載資料
			 * 16 條碼取用：機關取用資料
			 * 17 服務應用：MyData 將資料傳送給服務提供者
			 * 18 申請完成：DP 將資料回傳給 MyData 的時間
			 * 19 申請失敗：確定 DP 無法順利將資料傳給 MyData 的時間
			 * 20 申請失敗：發生 Exception
			 *
			 * 服務：
			 * 21 申請：您申請此筆資料/服務
			 * 22 同意：您同意服務條款
			 * 23 驗證：您完成身分驗證
			 * 24 傳輸：您同意 MyData 傳送資料給服務提供者
			 * 25 儲存：您下載資料
			 * 26 條碼取用：機關取用資料
			 * 27 服務應用：MyData 將資料傳送給服務提供者
			 * 28 服務申請終止：MyData 確定無法順利將資料傳給 SP 的時間
			 * 29 服務申請終止：發生 Exception
			 *
			 *
			 * 31 預覽
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

			ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, ActionEvent.EVENT_250, scopeStr, 11, null);
			ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, null, scopeStr, 12, null);
			ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, null, scopeStr, 13, null);
			ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, null, scopeStr, 14, null);
			/**
			 * 起始正規化流程 action_type == 1 (actionType)
			 * 2020-06-08之後不判斷
			 */
			Date now = new Date();
			String randomId = SequenceHelper.createUUID();
			String filename = downloadPath + File.separator +sdf6.format(new Date())+ File.separator + randomId + File.separator + ValidatorHelper.removeSpecialCharacters(portalResource.getResourceId()) + sdf3.format(now) + randomId + ".zip";
			File tempFile = new File(filename);
			if (!tempFile.getParentFile().exists()) {
				tempFile.getParentFile().mkdirs();
			}
			String retStr = "";
			String tmpUrl = portalResource.getDataEndpoint();
			if (tmpUrl.startsWith("https")) {
				/**
				 * SSL disable
				 */
				try {
					SslUtils.ignoreSsl();
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
			Long sysTime = System.currentTimeMillis();
			logger.info("{} ===portalResource.getDataSendMethod()==: {}", prefix, portalResource.getDataSendMethod());
			if (portalResource.getDataSendMethod().equalsIgnoreCase("post")) {
				// post
				URL connectto;
				HttpURLConnection conn = null;
				try {
					connectto = new URL(portalResource.getDataEndpoint());
					conn = (HttpURLConnection) connectto.openConnection();
					conn.setRequestMethod("POST");
				} catch (IOException e1) {
					logger.error(e1.getLocalizedMessage(), e1);
				}

				/**
				 * portalResource.getDataHeader()  header參數
				 */
				byte[] contentByteArray = null;
				if(portalResource.getDataParam()==null) {
					try {
						contentByteArray = "{}".getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}else {
					try {
						contentByteArray = replaceParamStrForPost(portalResource.getDataParam().trim(),(SessionRecord) session.getAttribute(SessionRecord.SessionKey)).getBytes("UTF-8");
					} catch (UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
				if(conn!=null) {
					if (portalResource.getDataHeader() != null && portalResource.getDataHeader().trim().length() > 0) {
						try {
							JsonNode jsonNode = om.readTree(replaceParamStrForPostNotAppendEscapAppendTransactionUid(portalResource.getDataHeader().trim(),
									(SessionRecord)session.getAttribute(SessionRecord.SessionKey),transactionUid,params));
							Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();
							while (jsonNodes.hasNext()) {
								Entry<String, JsonNode> node = jsonNodes.next();
								if(node!=null&&node.getKey()!=null&&node.getValue()!=null) {
									conn.setRequestProperty(node.getKey(), node.getValue().asText());
								}
							}
							logger.info("{} contentByteArray.length= {}", prefix, contentByteArray==null?0:contentByteArray.length);
							conn.setRequestProperty("Content-Length", String.valueOf(contentByteArray==null?0:contentByteArray.length));
						} catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
							logger.info("{} {}", prefix, e);
						}
					}
					
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setInstanceFollowRedirects(false);
					conn.setDoOutput(true);
					conn.setConnectTimeout(15 * 1000);
					/**
					 * portalResource.getDataParam() body參數
					 */
					OutputStream os = null;
					try {
						os = conn.getOutputStream();
						if(os!=null&&contentByteArray!=null) {
							os.write(contentByteArray,0,contentByteArray.length);
							os.close();	
						}
					} finally {
						if(os!=null) {
							HttpClientHelper.safeClose(os);
						}
					}
					/**
					 * 連線狀態
					 */
					try {
						code = conn.getResponseCode();
					} catch (IOException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
					logger.info("{} ===post httpstatuscode===: {}", prefix, code);
					if(code == HttpStatus.OK.value()) {
						// 不需處理，直接下載 200
						InputStream inputStream = null;
						FileOutputStream outputStream = null;
						try {
							inputStream = conn.getInputStream();
							outputStream = new FileOutputStream(filename);
							int bytesRead = -1;
							byte[] buffer = new byte[BUFFER_SIZE];
							if(inputStream!=null&&outputStream!=null) {
								while ((bytesRead = inputStream.read(buffer)) != -1) {
									outputStream.write(buffer, 0, bytesRead);
								}
							}
							if(outputStream!=null) {
								outputStream.close();
							}
							if(inputStream!=null) {
								inputStream.close();
							}
						} catch (IOException e) {
							logger.error(e.getLocalizedMessage(), e);
						}finally {
							if(inputStream!=null) {
								HttpClientHelper.safeClose(inputStream);
							}
							if(outputStream!=null) {
								HttpClientHelper.safeClose(outputStream);
							}
						}
						logger.info("{} File downloaded", prefix);
					}else if(code == HttpStatus.NO_CONTENT.value()){
						/**
						 * 204 查無資料 UNDO
						 */
					}else if(code == HttpStatus.TOO_MANY_REQUESTS.value()||code == HttpStatus.UPGRADE_REQUIRED.value()){
						/**
						 * TOO_MANY_REQUESTS(429, "Too Many Requests")
						 */
						//get all headers
						logger.info("{} === 429 start === ", prefix);
						Map<String, List<String>> headersmap = conn.getHeaderFields();
						for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
							logger.info("{} Key : {}, Value : {} ", prefix, entry.getKey(), entry.getValue());
							//Retry-After
							if(entry!=null&&entry.getKey()!=null&&entry.getKey().equalsIgnoreCase("Retry-After")) {
								waitTime = Integer.valueOf(entry.getValue().get(0));
							}
						}
						
						Waiting429DownloadAndFtp newThread;
						try {
							newThread = new Waiting429DownloadAndFtp(0,tempFile,ftpHost,ftpUsername,ftpPassword,waitTime,ftpSecretkey,portalResource,portalResourceDownloadMapper,portalBoxMapper,ulogUtil, sendLogUtil,(SessionRecord)session.getAttribute(SessionRecord.SessionKey),transactionUid,params, batchId, psdId, portalBatchDownloadMapper, portalServiceDownloadMapper, scopeStr, ps, txId);
							newThread.start();
						} catch (KeyManagementException | NoSuchAlgorithmException e) {
							logger.error(e.getLocalizedMessage(), e);
						}
					}
				}
			}else {
				// get
				URL connectto;
				HttpURLConnection conn = null;
				try {
					connectto = new URL(portalResource.getDataEndpoint());
					conn = (HttpURLConnection) connectto.openConnection();
				} catch (IOException e1) {
					logger.error(e1.getLocalizedMessage(), e1);
				}
				if(conn!=null) {
					conn.setRequestMethod("GET");
					if (portalResource.getDataHeader() != null
							&& portalResource.getDataHeader().trim().length() > 0) {
						try {
							JsonNode jsonNode = om.readTree(replaceParamStrForPostNotAppendEscapAppendTransactionUid(portalResource.getDataHeader().trim(),
									(SessionRecord)session.getAttribute(SessionRecord.SessionKey),transactionUid,params));
							Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();
							while (jsonNodes.hasNext()) {
								Entry<String, JsonNode> node = jsonNodes.next();
								if(node!=null&&node.getKey()!=null&&node.getValue()!=null) {
									conn.setRequestProperty(node.getKey(), node.getValue().asText());
								}
							}
							conn.setUseCaches(false);
							conn.setAllowUserInteraction(false);
							conn.setInstanceFollowRedirects(false);
							conn.setDoOutput(true);
							code = conn.getResponseCode();
						} catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
							logger.info("{} {}", prefix, e);
						}
					}

					logger.info("{} ===get httpstatuscode===: {}", prefix, code);
					if(code == HttpStatus.OK.value()) {
						// 不需處理，直接下載 200
						InputStream inputStream = null;
						FileOutputStream outputStream = null;
						try {
							inputStream = conn.getInputStream();
							outputStream = new FileOutputStream(filename);
							int bytesRead = -1;
							byte[] buffer = new byte[BUFFER_SIZE];
							if(inputStream!=null&&outputStream!=null) {
								while ((bytesRead = inputStream.read(buffer)) != -1) {
									outputStream.write(buffer, 0, bytesRead);
								}
							}
							if(outputStream!=null) {
								outputStream.close();
							}
							if(inputStream!=null) {
								inputStream.close();
							}
						} catch (IOException e) {
							logger.error(e.getLocalizedMessage(), e);
						}finally {
							if(inputStream!=null) {
								HttpClientHelper.safeClose(inputStream);
							}
							if(outputStream!=null) {
								HttpClientHelper.safeClose(outputStream);
							}
						}
						logger.info("{} File downloaded", prefix);
					}else if(code == HttpStatus.NO_CONTENT.value()){
						/**
						 * 204 查無資料 UNDO
						 */
					}else if(code == HttpStatus.TOO_MANY_REQUESTS.value()||code == HttpStatus.UPGRADE_REQUIRED.value()){
						/**
						 * TOO_MANY_REQUESTS(429, "Too Many Requests")
						 */
						//get all headers
						logger.info("{} === 429 start ===", prefix);
						Map<String, List<String>> headersmap = conn.getHeaderFields();
						for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
							logger.info("{} Key : {}, Value : {} ", prefix, entry.getKey(), entry.getValue());
							//Retry-After
							if(entry!=null&&entry.getKey()!=null&&entry.getKey().equalsIgnoreCase("Retry-After")) {
								waitTime = Integer.valueOf(entry.getValue().get(0));
							}
						}
						
						Waiting429DownloadAndFtp newThread;
						try {
							newThread = new Waiting429DownloadAndFtp(0,tempFile,ftpHost,ftpUsername,ftpPassword,waitTime,ftpSecretkey,portalResource,portalResourceDownloadMapper,portalBoxMapper,ulogUtil, sendLogUtil, (SessionRecord)session.getAttribute(SessionRecord.SessionKey),transactionUid,params, batchId, psdId, portalBatchDownloadMapper, portalServiceDownloadMapper, scopeStr, ps, txId);
							newThread.start();
						} catch (KeyManagementException | NoSuchAlgorithmException e) {
							logger.error(e.getLocalizedMessage(), e);
						}
					}
				}
			}
			ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, ActionEvent.EVENT_280, scopeStr, null, null);
			Long endTime = System.currentTimeMillis() - sysTime;
			/**
			 * 4. FTP
			 */
		
			/**
			 * AES壓密
			 */
			if(code==HttpStatus.OK.value()) {
				System.out.println("ftp file name="+tempFile.getAbsolutePath());
				logger.info("{} ftp file name= {}", prefix, tempFile.getAbsolutePath());
				
				/**
				 * 檔案路徑重新整理
				 * API.vTL7cCtoSl 財產資料(RAW DATA)
				 * API.mBqP4awHJY 所得資料(RAW DATA)
				 * tempFile ---> new tempFile
				 */
				List<String> resourceIdSpecList = Arrays.asList("API.vTL7cCtoSl", "API.mBqP4awHJY");	
				if(!Config.AppContextUrl.equalsIgnoreCase("https://mydatadev.nat.gov.tw/mydata")&&resourceIdSpecList.contains(portalResource.getResourceId())) {
					tempFile = handleSpecPath(tempFile);
				}
				
				byte[] b;
				byte[] encryptb;
				File filename1enc = new File(tempFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(tempFile.getAbsolutePath())+"enc."+FilenameUtils.getExtension(tempFile.getAbsolutePath()));
				FileInputStream fis = null;
				try {
					
					b = Files.readAllBytes(Paths.get(tempFile.getAbsolutePath()));
					encryptb = encrypt(b,ftpSecretkey);
					FileUtils.writeByteArrayToFile(filename1enc, encryptb);
					FTPClient client = new FTPClient();
					fis = new FileInputStream(filename1enc);
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.setBufferSize(1024 * 1024 * 10);
					client.changeWorkingDirectory("/mydata");
					client.storeFile(tempFile.getName(), fis);
					client.logout();
					client.disconnect();
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				} finally {
    				if(fis!=null) {
    					HttpClientHelper.safeClose(fis);
    				}
    			}
				logger.info("{} tp file over name= {}", prefix, tempFile.getAbsolutePath());
			}else {
				/**
				 * 檔案路徑重新整理
				 * API.vTL7cCtoSl 財產資料(RAW DATA)
				 * API.mBqP4awHJY 所得資料(RAW DATA)
				 * tempFile ---> new tempFile
				 */
				List<String> resourceIdSpecList = Arrays.asList("API.vTL7cCtoSl", "API.mBqP4awHJY");	
				if(!Config.AppContextUrl.equalsIgnoreCase("https://mydatadev.nat.gov.tw/mydata")&&resourceIdSpecList.contains(portalResource.getResourceId())) {
					tempFile = changeSpecPath(tempFile);
				}
			}
			/**
			 * 5.紀錄到portal_resource_download
			 */
			prd = new PortalResourceDownload();
			prd.setDownloadSn(ValidatorHelper.removeSpecialCharacters(downloadSn));
			try {
				prd.setProviderKey(ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getAccount()));
			} catch (BadPaddingException e) {
				logger.error(e.getLocalizedMessage(), e);
			} catch (IllegalBlockSizeException e) {
				logger.error(e.getLocalizedMessage(), e);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			prd.setPrId(ValidatorHelper.limitNumber(prId));
			prd.setFiles(ValidatorHelper.removeSpecialCharacters(tempFile.getName()));
			prd.setWaitTime(ValidatorHelper.limitNumber(waitTime));
			prd.setCtime(new Date());
			// stat 0:未下載; 1:已下載
			prd.setStat(0);
			try {
				prd.setEmail(ValidatorHelper.removeSpecialCharacters(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getEmail()));
			} catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			if(code==426) {
				prd.setCode("429");
			}else {
				prd.setCode(ValidatorHelper.removeSpecialCharacters(String.valueOf(code)));
			}
			prd.setTransactionUid(ValidatorHelper.removeSpecialCharacters(transactionUid));
			prd.setVerificationType(verificationType.name());
			prd.setResponseTime(endTime.intValue());
			if(psdId != null) {
				if(txId==null) {
					prd.setDownloadType(DownloadType.vspDownload.name());
				}else {
					prd.setDownloadType(DownloadType.spDownload.name());
				}				
			}else {
				prd.setDownloadType(DownloadType.not_yet.name());
			}
			if(code==HttpStatus.OK.value()) {
				prd.setServerDownloadTime(new Date());
			}
			if(code == HttpStatus.OK.value() || code == HttpStatus.NO_CONTENT.value()
					|| code == HttpStatus.TOO_MANY_REQUESTS.value()||code == HttpStatus.UPGRADE_REQUIRED.value()) {
				prd.setRequestStatus(RequestType.success.name());

				if(code == HttpStatus.OK.value() || code == HttpStatus.NO_CONTENT.value()) {
					ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, null, scopeStr, 18, null);
				}
			} else {
				prd.setRequestStatus(RequestType.fail.name());
				ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, null, scopeStr, 19, null);
			}
			if(psdId!=null) {
				prd.setPsdId(ValidatorHelper.limitNumber(psdId));
			}
			if(batchId!=null) {
				prd.setBatchId(ValidatorHelper.limitNumber(batchId));
			}
			if(code == HttpStatus.OK.value()) {
				prd.setDigest(ValidatorHelper.removeSpecialCharacters(DigestUtil.generateFileDigest(tempFile)));
			}
			if(portalResource == null) {
				prd.setIsShow(-2);
			} else {
				prd.setIsShow(portalResource.getIsShow()==null?0:ValidatorHelper.limitNumber(portalResource.getIsShow()));
			}
			portalResourceDownloadMapper.insertSelective(prd);

			/**
			 * 清除舊portalBoxMapper
			 */
			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.setTime(new Date());
			cal1.add(Calendar.MINUTE, -20);
			Date startTime = cal1.getTime();
			List<PortalBoxExt> portalBoxExtList = null;
			try {
				portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(((SessionRecord)session.getAttribute(SessionRecord.SessionKey)).getMember()).getAccount());
			} catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			if(portalBoxExtList!=null&&portalBoxExtList.size()>0) {
				for(PortalBoxExt p:portalBoxExtList) {
					PortalBoxExt box = new PortalBoxExt();
					try {
						BeanUtils.copyProperties(box, p);
					} catch (IllegalAccessException e) {
						logger.error(e.getLocalizedMessage(), e);
					} catch (InvocationTargetException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
					/**
					 * 是否符合本下載
					 */
					if(box.getPrId().compareTo(prId)==0) {
						/**
						 * 計算剩餘時間
						 */
						if(box.getCtime().after(startTime)) {
							//本box為有效box
							portalBoxMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(p.getId()));
						}
					}
				}
			}
			/**
			 * 6. 產生portal_box
			 */
			//ulogUtil.recordFullByPr(sr, ps, txId, portalResource, transactionUid, "320", scopeStr, null, null);
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
			portalBoxMapper.insertSelective(box);
			boxId = box.getId();
			
			/**
			 * 寄信或發簡訊通知（目前僅httpcode 200且檔案準備完成才寄通知）
			 * 1. psdId == null (個人和多筆下載需通知，服務不用)
			 * 2. 觀察member聯絡方式區分MAIL或簡訊
			 */
			/**
			 * 單筆即時資料集，簡訊或通知信
			 */
			try {
				if(code==200) {
					Member member = SessionMember.getSessionMemberToMember(sr.getMember());
					if(StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
						String from = "mydata_system@ndc.gov.tw";
						String title = "【個人化資料自主運用(MyData)平臺】個人資料已完成下載（系統信件）";
						String content = "您好：<br><br>\n\n"
								+ "感謝您使用個人化資料自主運用(MyData)平臺，您申請的「" + portalResource.getName() + "」已於"+sdf8.format(new Date())+"下載完畢。<br>\n"
								+ "<br>\n";
						/**
						 * 僅線上服務不用此段文字
						 */
//						if(!(psdId!= null && txId !=null && txId.trim().length()>0)) {
//							content= content+ "請您於8小時內返回 個人化資料自主運用(MyData)平臺，並登入 <a href=\""+ Config.AppContextUrl +"/signin?toPage=verification"+ (box.getId()==null?"":box.getId()) +"\">資料條碼區</a>查看。<br>\n";
//						}
						
						content= content+ "<br>\n"
								+ "此為系統信件，請勿回信。<br>\n"
								+ "如有任何疑問或非您本人下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。<br>\n"
								+ "<br>\n"
								+ "——-<br>\n"
		                        + "<strong>我為什麼會收到這封信？</strong><br>\n"
		                        + "您會收到此封信件，是因為您於國家發展委員會個人化資料自主運用(MyData)平臺驗證身分，因此，系統會自動發此信通知您。<br>\n"
		                        + "<br>——-\n";
						List<String> tmpReveicers = new ArrayList<String>();
						tmpReveicers.add(member.getEmail());
						MailUtil.sendHtmlMail(tmpReveicers,from, title,content,Config.mailEnable);
						sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
					} else if (StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
						String smbody = "MyData通知-您申請「"+portalResource.getName()+"」資料已於"+sdf8.format(new Date())+"下載完成。如非您本人下載，請洽客服0800-009-868";
	                    SMSUtil.sendSms(member.getMobile(), smbody);
	                    sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
					} else {
						System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
					}	
				}
			}catch(Exception ex) {
				System.out.println("--寄信失敗--:\n"+ex);
			}
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage(), ex);
			boxId = -1;
		}


	}
	
	public boolean isDuplicateDownloadVerify(String tmpDownloadVerify) {
		boolean ret = false;
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		Date endTime = cal.getTime();
		// 起始時間定為8小時前(8小時前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, -fileStoreTime);
		Date startTime = cal.getTime();

		Map<String,Object> param = new HashMap<String,Object>();
		param.put("downloadVerify", tmpDownloadVerify);
		param.put("sCtime", startTime);
		List<PortalBox> conDownloadVerifyList = portalBoxMapper.selectByExample(param);
		if (conDownloadVerifyList != null && conDownloadVerifyList.size() > 0) {
			ret = true;
		}
		return ret;
	}	
	
	
	private String replaceParamStrForPost(String paramstr, SessionRecord sessionRecord) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		logger.info("in replace=\n" + ValidatorHelper.removeSpecialCharacters(paramstr));
		String patternStr = "#\\{[\\w:/\\\\.]*\\}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		while (matcher.find()) {
			matchStrList.add(matcher.group());
			logger.info(ValidatorHelper.removeSpecialCharacters(matcher.group()));
		}
		if (matchStrList != null && matchStrList.size() > 0) {
			logger.info("------------paramstr s---------------");
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
						logger.error(e.getLocalizedMessage(), e);
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
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			logger.info(ValidatorHelper.removeSpecialCharacters(paramstr));
			logger.info("-------------paramstr e--------------");
		}
		return ValidatorHelper.removeSpecialCharacters(paramstr);
	}
	
	public static String replaceParamStrForPostNotAppendEscapAppendTransactionUid(String paramstr, SessionRecord sessionRecord,
			String transactionUid,Map<String, Object> paramMap) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		logger.info("in replace=\n" + ValidatorHelper.removeSpecialCharacters(paramstr));
		String patternStr = "#\\{[\\w:/\\\\.]*\\}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		while (matcher.find()) {
			matchStrList.add(matcher.group());
			logger.info(ValidatorHelper.removeSpecialCharacters(matcher.group()));
		}
		if (matchStrList != null && matchStrList.size() > 0) {
			logger.info("------------paramstr s---------------");
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
						logger.error(e.getLocalizedMessage(), e);
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
					if(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getBirthdate() == null) {
						forReplaceStr = "2910-12-31T00:00:00.000Z";
					} else {
						String datefinalStr = sdf.format(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getBirthdate())+"T00:00:00.000Z";
						forReplaceStr = datefinalStr;
					}

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
				if (matchInnerStr.equalsIgnoreCase("accountNO")) {
					forReplaceStr = paramMap.get("accountNO").toString();
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
				// 移民署
				if (matchInnerStr.equalsIgnoreCase("personId")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid();
					if(ValidateHelper.isValidTWPID(forReplaceStr) == false) {
						forReplaceStr = "";
					}
				}

				if (matchInnerStr.equalsIgnoreCase("residentIdNo")) {
					forReplaceStr = SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getUid();
					if(ValidateHelper.isValidResidentPermit(forReplaceStr) == false) {
						forReplaceStr = "";
					}
				}

				if (matchInnerStr.equalsIgnoreCase("birthdateYYYYMMDD")) { // 日期格式為 YYYYMMDD
					String datefinalStr = sdf6.format(SessionMember.getSessionMemberToMember(sessionRecord.getMember()).getBirthdate());
					forReplaceStr = datefinalStr;
				}

				if(StringUtils.isBlank(forReplaceStr)) {
					forReplaceStr = MapUtils.getString(paramMap, matchInnerStr, "");
				}

				paramstr = paramstr.replace(s, forReplaceStr);
			}
			logger.info(ValidatorHelper.removeSpecialCharacters(paramstr));
			logger.info("-------------paramstr e--------------");
		}
		return ValidatorHelper.removeSpecialCharacters(paramstr);
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
     * 解壓縮-特殊（將檔名 META-INFO\certificate.cer --> certificate.cer）
     * @param sourceFile
     * @param targetDir
     * @throws IOException 
     */
    public static void unzipSpec(File sourceFile, File targetDir) throws IOException {
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
                    entryFilePath = ValidatorHelper.removeSpecialCharacters(targetDir.getAbsolutePath() + File.separator + entry.getName().replace("META-INFO\\", ""));
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
    
    /**
     * 處理路徑錯誤檔案，重新封裝
     * @param output
     * @param sources
     * @param metas
     * @return
     * @throws IOException
     * @throws ZipException
     */
    public static File packZipSpec(File output, ArrayList<File> sources, ArrayList<File> metas)
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
		if(sources!=null&&sources.size()>0) {
			zipFile.addFiles(sources, parameters);
		}
		parameters.setRootFolderInZip("META-INFO");
		zipFile.addFiles(metas, parameters);
		return output;
	}
	
    public static File changeSpecPath(File file) {
    	String fname = FilenameUtils.getBaseName(file.getAbsolutePath());
    	String ext = FilenameUtils.getExtension(file.getAbsolutePath());
    	File out = new File(file.getParentFile().getAbsolutePath()+File.separator+fname+"spec"+"."+ext);
    	return out;
    }
    
    public static File handleSpecPath(File file) throws IOException, ZipException {
    	String fname = FilenameUtils.getBaseName(file.getAbsolutePath());
    	String ext = FilenameUtils.getExtension(file.getAbsolutePath());
    	File out = new File(file.getParentFile().getAbsolutePath()+File.separator+fname+"spec"+"."+ext);
    	File unzipdir = new File(file.getParentFile().getAbsolutePath()+File.separator+"unzipdir");
    	unzipSpec(file,unzipdir);
		ArrayList<File> sources = new ArrayList<File>();
		List<String> sourcesexts = Arrays.asList("pdf", "json","p7b");
		ArrayList<File> metas = new ArrayList<File>();
		List<String> metasexts = Arrays.asList("cer", "sha256withrsa","xml");
		File[] allFiles = unzipdir.listFiles();
		if(allFiles!=null&&allFiles.length>0) {
			for(File f:allFiles) {
				String extension = FilenameUtils.getExtension(f.getAbsolutePath()).toLowerCase(Locale.ENGLISH);
				if(sourcesexts.contains(extension)) {
					sources.add(f);
				}
				if(metasexts.contains(extension)) {
					metas.add(f);
				}
			}
		}
		packZipSpec(out,sources,metas);
    	return out;
    }
    
	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public VerificationType getVerificationType() {
		return verificationType;
	}

	public void setVerificationType(VerificationType verificationType) {
		this.verificationType = verificationType;
	}

	public Integer getPsId() {
		return psId;
	}

	public void setPsId(Integer psId) {
		this.psId = psId;
	}

	public Integer getPsdId() {
		return psdId;
	}

	public void setPsdId(Integer psdId) {
		this.psdId = psdId;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public UlogUtil getUlogUtil() {
		return ulogUtil;
	}

	public void setUlogUtil(UlogUtil ulogUtil) {
		this.ulogUtil = ulogUtil;
	}

	public SendLogUtil getSendLogUtil() {
		return sendLogUtil;
	}

	public void setSendLogUtil(SendLogUtil sendLogUtil) {
		this.sendLogUtil = sendLogUtil;
	}

	public String getDownloadSn() {
		return downloadSn;
	}

	public void setDownloadSn(String downloadSn) {
		this.downloadSn = downloadSn;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getBoxId() {
		return boxId;
	}

	public void setBoxId(Integer boxId) {
		this.boxId = boxId;
	}

	public Integer getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(Integer waitTime) {
		this.waitTime = waitTime;
	}
	
	public PortalResourceDownload getPrd() {
		return prd;
	}

	public void setPrd(PortalResourceDownload prd) {
		this.prd = prd;
	}

	public PortalService getPs() {
		return ps;
	}

	public void setPs(PortalService ps) {
		this.ps = ps;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public SessionRecord getSr() {
		return sr;
	}

	public void setSr(SessionRecord sr) {
		this.sr = sr;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	public String getFtpSecretkey() {
		return ftpSecretkey;
	}

	public void setFtpSecretkey(String ftpSecretkey) {
		this.ftpSecretkey = ftpSecretkey;
	}

	public String getFtpIv() {
		return ftpIv;
	}

	public void setFtpIv(String ftpIv) {
		this.ftpIv = ftpIv;
	}

	public PortalResourceMapper getPortalResourceMapper() {
		return portalResourceMapper;
	}

	public void setPortalResourceMapper(PortalResourceMapper portalResourceMapper) {
		this.portalResourceMapper = portalResourceMapper;
	}

	public PortalResourceScopeMapper getPortalResourceScopeMapper() {
		return portalResourceScopeMapper;
	}

	public void setPortalResourceScopeMapper(PortalResourceScopeMapper portalResourceScopeMapper) {
		this.portalResourceScopeMapper = portalResourceScopeMapper;
	}

	public PortalResourceDownloadMapper getPortalResourceDownloadMapper() {
		return portalResourceDownloadMapper;
	}

	public void setPortalResourceDownloadMapper(PortalResourceDownloadMapper portalResourceDownloadMapper) {
		this.portalResourceDownloadMapper = portalResourceDownloadMapper;
	}

	public PortalResourceExtMapper getPortalResourceExtMapper() {
		return portalResourceExtMapper;
	}

	public void setPortalResourceExtMapper(PortalResourceExtMapper portalResourceExtMapper) {
		this.portalResourceExtMapper = portalResourceExtMapper;
	}

	public PortalBoxMapper getPortalBoxMapper() {
		return portalBoxMapper;
	}

	public void setPortalBoxMapper(PortalBoxMapper portalBoxMapper) {
		this.portalBoxMapper = portalBoxMapper;
	}

	public PortalBatchDownloadMapper getPortalBatchDownloadMapper() {
		return portalBatchDownloadMapper;
	}

	public void setPortalBatchDownloadMapper(PortalBatchDownloadMapper portalBatchDownloadMapper) {
		this.portalBatchDownloadMapper = portalBatchDownloadMapper;
	}

	public PortalServiceDownloadMapper getPortalServiceDownloadMapper() {
		return portalServiceDownloadMapper;
	}

	public void setPortalServiceDownloadMapper(PortalServiceDownloadMapper portalServiceDownloadMapper) {
		this.portalServiceDownloadMapper = portalServiceDownloadMapper;
	}
}
