package tw.gov.ndc.emsg.mydata.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.riease.common.enums.ActionEvent;
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.util.SslUtils;

import net.lingala.zip4j.exception.ZipException;
import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.mapper.PortalBatchDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBoxMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceDownloadMapper;
import tw.gov.ndc.emsg.mydata.type.RequestType;
import tw.gov.ndc.emsg.mydata.type.SendType;
import tw.gov.ndc.emsg.mydata.util.DigestUtil;
import tw.gov.ndc.emsg.mydata.util.MailUtil;
import tw.gov.ndc.emsg.mydata.util.SMSUtil;
import tw.gov.ndc.emsg.mydata.util.SendLogUtil;
import tw.gov.ndc.emsg.mydata.util.UlogUtil;

public class Waiting429DownloadAndFtp extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(Waiting429DownloadAndFtp.class);
	private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy年MM月dd日HH時mm分");
	private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy/M/d HH:mm");
	private static final int BUFFER_SIZE = 4096;
	private int next = 0;
	private File tempFile;
	private String ftpHost;
	private String ftpUsername;
	private String ftpPassword;
	private String ftpSecretkey;
	private int wait_time;
	private PortalResource portalResource;
	private PortalService portalService;
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	private PortalBoxMapper portalBoxMapper;
	private PortalBatchDownloadMapper portalBatchDownloadMapper;
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	private UlogUtil ulogUtil;
	private SendLogUtil sendLogUtil;
	private SessionRecord sr;
	private String transactionUid;
	private Map<String, Object> paramMap;
	private Integer batchId;
	private Integer psdId;
    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder =  Base64.getDecoder();
	private String scopeStr;
	private String tmpBoxId;
	private String txId;

	public Waiting429DownloadAndFtp(int next,File tempFile,String ftpHost,String ftpUsername,String ftpPassword,int wait_time,String ftpSecretkey,
									PortalResource portalResource,PortalResourceDownloadMapper portalResourceDownloadMapper,PortalBoxMapper portalBoxMapper,
									UlogUtil ulogUtil,SendLogUtil sendLogUtil,SessionRecord sr,String transactionUid,Map<String, Object> paramMap,Integer batchId,Integer psdId,
									PortalBatchDownloadMapper portalBatchDownloadMapper, PortalServiceDownloadMapper portalServiceDownloadMapper,
									String scopeStr, PortalService portalService, String txId) throws NoSuchAlgorithmException, KeyManagementException {
		this.next = (next+1);
		this.tempFile = tempFile;
		this.ftpHost = ftpHost;
		this.ftpUsername = ftpUsername;
		this.ftpPassword = ftpPassword;
		this.wait_time = wait_time;
		this.ftpSecretkey = ftpSecretkey;
		this.portalResource = portalResource;
		this.portalResourceDownloadMapper = portalResourceDownloadMapper;
		this.portalBoxMapper = portalBoxMapper;
		this.ulogUtil = ulogUtil;
		this.sendLogUtil = sendLogUtil;
		this.sr = sr;
		this.transactionUid = transactionUid;
		this.paramMap = paramMap;
		this.batchId = batchId;
		this.psdId = psdId;
		this.portalBatchDownloadMapper = portalBatchDownloadMapper;
		this.portalServiceDownloadMapper = portalServiceDownloadMapper;
		this.scopeStr = scopeStr;
		this.portalService = portalService;
		this.txId = txId;
	}
	/**
	 * 一定是ActionType=1,標準流程
	 * @throws Exception 
	 * 
	 * 429流程控制
	 * /rest/personal/apply/{prId}
	 * /rest/mutipledownload/apply/{prIdEncode}
	 * /rest/service/apply/{psId}
	 * 當第二次延長時間時 （均須延長），但需考慮有否多Thread未完成情形
	 * portalResourceDownloadMapper（無）
	 * portalBatchDownloadMapper（有）----> 先不考慮，因目前沒有線上回問流程
	 * portalServiceDownloadMapper（有）
	 * portalBoxMapper（無）
	 */
	public void run() {
		logger.info("[{}] ===Waiting429DownloadAndFtp start=== ", transactionUid);
		int httpstatuscode = 0;
		Integer retryAfter = 0;
		ObjectMapper om = new ObjectMapper();
		String tmpUrl = portalResource.getDataEndpoint();
		if(wait_time>0) {
			try {
				Thread.sleep(wait_time*1000);
				logger.info("[{}] -- sleep {} sec --", transactionUid, wait_time);
			} catch (InterruptedException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}

		String account = null;
		try {
			logger.info("[{}] member >> {}",transactionUid, new ObjectMapper().writeValueAsString(sr.getMember()));
			account = SessionMember.getSessionMemberToMember(sr.getMember()).getAccount();
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		if(account==null||StringUtils.isBlank(account)) {
			logger.error("[{}] Account not found", transactionUid);
			return;
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("prId", ValidatorHelper.limitNumber(portalResource.getPrId()));
		param.put("providerKey", account);
		param.put("transactionUid", transactionUid);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, -8);
		Date startTime = cal.getTime();
		param.put("sCtime", startTime);
		//param.put("stat", 0);
		param.put("code", "429");
		List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param);

		try {
			if (tmpUrl.startsWith("https")) {
				/**
				 * SSL disable
				 */
				SslUtils.ignoreSsl();
				if (portalResource.getDataSendMethod().equalsIgnoreCase("post")) {
					String scopeStr1 = "";
					ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, ActionEvent.EVENT_250, scopeStr1, null, null);
					// post
					Long systime = System.currentTimeMillis();
					URL connectto;
					connectto = new URL(portalResource.getDataEndpoint());
					HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
					conn.setRequestMethod("POST");
					/**
					 * portalResource.getDataHeader()  header參數
					 */
					logger.info("[{}] ===portalResource.getDataParam()==: {}", transactionUid, portalResource.getDataParam());
					byte[] contentByteArray = null;
					if(portalResource.getDataParam()==null) {
						contentByteArray = "{}".getBytes("UTF-8");
					}else {
						contentByteArray = PersonalRestController.replaceParamStrForPost(portalResource.getDataParam().trim(),sr).getBytes("UTF-8");
					}
					if (portalResource.getDataHeader() != null && portalResource.getDataHeader().trim().length() > 0) {
						try {
							JsonNode jsonNode = om.readTree(PersonalRestController.replaceParamStrForPostNotAppendEscapAppendTransactionUid(portalResource.getDataHeader().trim(),
									sr,transactionUid,paramMap));
							Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();
							while (jsonNodes.hasNext()) {
								Entry<String, JsonNode> node = jsonNodes.next();
								conn.setRequestProperty(node.getKey(), node.getValue().asText());
							}
							logger.info("[{}] contentByteArray.length= {}", transactionUid, contentByteArray.length);
							conn.setRequestProperty("Content-Length", String.valueOf(contentByteArray.length));
						} catch (IOException e) {
							logger.error("[{}] {}", transactionUid, e);
						}
					}
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setInstanceFollowRedirects(false);
					conn.setDoOutput(true);
					/**
					 * portalResource.getDataParam() body參數
					 */
					OutputStream os = null;
					try {
						os = conn.getOutputStream();
						os.write(contentByteArray,0,contentByteArray.length);
						os.close();
					}finally {
						if(os!=null) {
							HttpClientHelper.safeClose(os);
						}
					}
					/**
					 * 連線狀態
					 */
					httpstatuscode = conn.getResponseCode();
					Long endTime = System.currentTimeMillis() - systime;
					logger.info("[{}] ===post httpstatuscode===: {}", transactionUid, httpstatuscode);
					if(httpstatuscode == HttpStatus.OK.value()) {
						// 不需處理，直接下載 200 
						
						InputStream inputStream = null;
						FileOutputStream outputStream = null;
						try {
							inputStream = conn.getInputStream();
							outputStream = new FileOutputStream(tempFile);
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
						
						logger.info("[{}] File downloaded", transactionUid);
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("200");
								prd1.setServerDownloadTime(new Date());
								prd1.setResponseTime(endTime.intValue());
								prd1.setDigest(DigestUtil.generateFileDigest(tempFile));
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}
						}
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, ActionEvent.EVENT_280, null, null, null);
					}else if(httpstatuscode == HttpStatus.TOO_MANY_REQUESTS.value()||httpstatuscode == HttpStatus.UPGRADE_REQUIRED.value()){
						//get all headers
						Map<String, List<String>> headersmap = conn.getHeaderFields();
						logger.info("[{}] === 429 start ===:2:", transactionUid);
						for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
							logger.info("[{}] Key : {}, Value : {}", transactionUid, entry.getKey(), entry.getValue());
							//Retry-After
							if(entry!=null&&entry.getKey()!=null&&entry.getKey().equalsIgnoreCase("Retry-After")) {
								retryAfter = ValidatorHelper.limitNumber(Integer.valueOf(entry.getValue().get(0)));
							}
						}
						// Waiting429DownloadAndFtp
						if(next<=16) {
							Integer nextWaittime = 0;
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								PortalResourceDownload prd = portalResourceDownloadList.get(0);
								nextWaittime = ValidatorHelper.limitNumber(prd.getWaitTime())+retryAfter;
								/**
								 * 小於90 min(5400 sec)
								 */
								if(nextWaittime<=5400) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setWaitTime(nextWaittime);
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									if(batchId!=null) {
										PortalBatchDownload portalBatchDownload = portalBatchDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(batchId));
										if(portalBatchDownload!=null&&portalBatchDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalBatchDownload portalBatchDownload1 = new PortalBatchDownload();
											portalBatchDownload1.setId(ValidatorHelper.limitNumber(portalBatchDownload.getId()));
											portalBatchDownload1.setWaitTime(nextWaittime);
											portalBatchDownloadMapper.updateByPrimaryKeySelective(portalBatchDownload1);
										}
									}
									if(psdId!=null) {
										PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdId));
										if(portalServiceDownload!=null&&portalServiceDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalServiceDownload portalServiceDownload1 = new PortalServiceDownload();
											portalServiceDownload1.setId(ValidatorHelper.limitNumber(portalServiceDownload.getId()));
											portalServiceDownload1.setWaitTime(nextWaittime);
											portalServiceDownloadMapper.updateByPrimaryKeySelective(portalServiceDownload1);
										}
									}
								}
							}
							/**
							 * box時間延長
							 */
							if(nextWaittime<=5400) {
								Map<String, Object> boxparam = new HashMap<String, Object>();
								boxparam.put("downloadSn", ValidatorHelper.removeSpecialCharacters(portalResourceDownloadList.get(0).getDownloadSn()));
								boxparam.put("ctimeDesc", true);
								List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(boxparam);
								if(portalBoxList!=null&&portalBoxList.size()>0) {
									PortalBox box = portalBoxList.get(0);
									Calendar cal2 = GregorianCalendar.getInstance();
									cal2.setTime(new Date());
									cal2.add(Calendar.SECOND, retryAfter);
									Date boxStartTime = cal2.getTime();
									PortalBox box1 = new PortalBox();
									box1.setId(ValidatorHelper.limitNumber(box.getId()));
									box1.setCtime(boxStartTime);
									//必須判斷欄位
									box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(box.getAgentUid()));
									box1.setAgentBirthdate(ValidatorHelper.limitDate(box.getAgentBirthdate()));
									box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(box.getAgentVerify()));
									box1.setAgreeAgent(ValidatorHelper.limitNumber(box.getAgreeAgent()));
									portalBoxMapper.updateByPrimaryKeySelective(box1);
								}
								Waiting429DownloadAndFtp newThread = new Waiting429DownloadAndFtp(next, tempFile, ftpHost, ftpUsername, ftpPassword, retryAfter, ftpSecretkey, portalResource, portalResourceDownloadMapper,portalBoxMapper, ulogUtil, sendLogUtil, sr, transactionUid, paramMap, batchId, psdId, portalBatchDownloadMapper, portalServiceDownloadMapper, scopeStr, portalService, txId);
								newThread.start();								
							}else {
								if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
									for(PortalResourceDownload prd:portalResourceDownloadList) {
										PortalResourceDownload prd1 = new PortalResourceDownload();
										prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
										prd1.setCode("508");
										prd1.setRequestStatus(RequestType.fail.name());
										prd1.setResponseTime(endTime.intValue());
										portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									}

									ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
								}
							}
						}else {
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								for(PortalResourceDownload prd:portalResourceDownloadList) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setCode("508");
									prd1.setRequestStatus(RequestType.fail.name());
									prd1.setResponseTime(endTime.intValue());
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
								}

								ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
							}
						}
					}else if(httpstatuscode == HttpStatus.NO_CONTENT.value()) {
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("204");
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						}
					} else {
						if(httpstatuscode == HttpStatus.FOUND.value()) {
							try {
								Map<String, List<String>> headersmap = conn.getHeaderFields();
								logger.info("[{}] === 302 start ===:", transactionUid);
								for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
									logger.info("[{}] Key : {}, Value : {}", transactionUid, entry.getKey(), entry.getValue());
								}
							}catch(Exception e) {
								logger.error("[{}] {} 302 inner error!", transactionUid, e);
							}
						}
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode(ValidatorHelper.removeSpecialCharacters(String.valueOf(httpstatuscode)));
								prd1.setRequestStatus(RequestType.fail.name());
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
						}
					}
				}else {
					// get
					Long systime = System.currentTimeMillis();
					URL connectto = new URL(portalResource.getDataEndpoint());
					HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
					conn.setRequestMethod("GET");
					if (portalResource.getDataHeader() != null
							&& portalResource.getDataHeader().trim().length() > 0) {
						try {
							JsonNode jsonNode = om.readTree(PersonalRestController.replaceParamStrForPostNotAppendEscapAppendTransactionUid(portalResource.getDataHeader().trim(),
									sr,transactionUid,paramMap));
							Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();
							while (jsonNodes.hasNext()) {
								Entry<String, JsonNode> node = jsonNodes.next();
								conn.setRequestProperty(node.getKey(), node.getValue().asText());
							}
	
						} catch (IOException e) {
							logger.error("[{}] {}", transactionUid, e);
						}
					}
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setInstanceFollowRedirects(false);
					conn.setDoOutput(true);
					/**
					 * portalResource.getDataParam() body參數
					 */
					
					/**
					 * 連線狀態
					 */
					httpstatuscode = conn.getResponseCode();
					Long endTime = System.currentTimeMillis() - systime;
					logger.info("[{}] ===get httpstatuscode===: {}", transactionUid, httpstatuscode);
					if(httpstatuscode == HttpStatus.OK.value()) {
						// 不需處理，直接下載 200
						InputStream inputStream = null;
						FileOutputStream outputStream = null;
						try {
							inputStream = conn.getInputStream();
							outputStream = new FileOutputStream(tempFile);
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

						logger.info("[{}] File downloaded ", transactionUid);

						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("200");
								prd1.setServerDownloadTime(new Date());
								prd1.setResponseTime(endTime.intValue());
								prd1.setDigest(DigestUtil.generateFileDigest(tempFile));
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}
						}
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, ActionEvent.EVENT_280, null, null, null);
					}else if(httpstatuscode == HttpStatus.TOO_MANY_REQUESTS.value()||httpstatuscode == HttpStatus.UPGRADE_REQUIRED.value()){
						//429
						//get all headers
						logger.info("[{}] === 429 start ===:2: ", transactionUid);
						Map<String, List<String>> headersmap = conn.getHeaderFields();
						for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
							logger.info("[{}] Key : {}, Value : {}", transactionUid, entry.getKey(), entry.getValue());
							//Retry-After
							if(entry!=null&&entry.getKey()!=null&&entry.getKey().equalsIgnoreCase("Retry-After")) {
								retryAfter = ValidatorHelper.limitNumber(Integer.valueOf(entry.getValue().get(0)));
							}
						}
						// Waiting429DownloadAndFtp
						if(next<=16) {
							Integer nextWaittime = 0;
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								PortalResourceDownload prd = portalResourceDownloadList.get(0);
								nextWaittime = ValidatorHelper.limitNumber(prd.getWaitTime())+retryAfter;
								/**
								 * 小於90 min(5400 sec)
								 */
								if(nextWaittime<=5400) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setWaitTime(nextWaittime);
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									if(batchId!=null) {
										PortalBatchDownload portalBatchDownload = portalBatchDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(batchId));
										if(portalBatchDownload!=null&&portalBatchDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalBatchDownload portalBatchDownload1 = new PortalBatchDownload();
											portalBatchDownload1.setId(ValidatorHelper.limitNumber(portalBatchDownload.getId()));
											portalBatchDownload1.setWaitTime(nextWaittime);
											portalBatchDownloadMapper.updateByPrimaryKeySelective(portalBatchDownload1);
										}
									}
									if(psdId!=null) {
										PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdId));
										if(portalServiceDownload!=null&&portalServiceDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalServiceDownload portalServiceDownload1= new PortalServiceDownload();
											portalServiceDownload1.setId(ValidatorHelper.limitNumber(portalServiceDownload.getId()));
											portalServiceDownload1.setWaitTime(nextWaittime);
											portalServiceDownloadMapper.updateByPrimaryKeySelective(portalServiceDownload1);
										}
									}
								}
							}
							/**
							 * box時間延長
							 */
							if(nextWaittime<=5400) {
								Map<String, Object> boxparam = new HashMap<String, Object>();
								boxparam.put("downloadSn", ValidatorHelper.removeSpecialCharacters(portalResourceDownloadList.get(0).getDownloadSn()));
								boxparam.put("ctimeDesc", true);
								List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(boxparam);
								if(portalBoxList!=null&&portalBoxList.size()>0) {
									PortalBox box = portalBoxList.get(0);
									Calendar cal2 = GregorianCalendar.getInstance();
									cal2.setTime(new Date());
									cal2.add(Calendar.SECOND, retryAfter);
									Date boxStartTime = cal2.getTime();
									PortalBox box1 = new PortalBox();
									box1.setId(ValidatorHelper.limitNumber(box.getId()));
									box1.setCtime(boxStartTime);
									//必須判斷欄位
									box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(box.getAgentUid()));
									box1.setAgentBirthdate(ValidatorHelper.limitDate(box.getAgentBirthdate()));
									box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(box.getAgentVerify()));
									box1.setAgreeAgent(ValidatorHelper.limitNumber(box.getAgreeAgent()));
									portalBoxMapper.updateByPrimaryKeySelective(box1);
								}
								Waiting429DownloadAndFtp newThread = new Waiting429DownloadAndFtp(next, tempFile, ftpHost, ftpUsername, ftpPassword, retryAfter, ftpSecretkey, portalResource, portalResourceDownloadMapper,portalBoxMapper, ulogUtil, sendLogUtil, sr, transactionUid, paramMap, batchId, psdId, portalBatchDownloadMapper, portalServiceDownloadMapper, scopeStr, portalService, txId);
								newThread.start();								
							}else {
								if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
									for(PortalResourceDownload prd:portalResourceDownloadList) {
										PortalResourceDownload prd1 = new PortalResourceDownload();
										prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
										prd1.setCode("508");
										prd1.setRequestStatus(RequestType.fail.name());
										prd1.setResponseTime(endTime.intValue());
										portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									}

									ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
								}
							}
						}else {
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								for(PortalResourceDownload prd:portalResourceDownloadList) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setCode("508");
									prd1.setRequestStatus(RequestType.fail.name());
									prd1.setResponseTime(endTime.intValue());
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
								}

								ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
							}
						}
					}else if(httpstatuscode == HttpStatus.NO_CONTENT.value()) {
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("204");
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						}
					} else {
						if(httpstatuscode == HttpStatus.FOUND.value()) {
							try {
								Map<String, List<String>> headersmap = conn.getHeaderFields();
								logger.info("[{}] === 302 start ===:", transactionUid);
								for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
									logger.info("[{}] Key : {}, Value : {}", transactionUid, entry.getKey(), entry.getValue());
								}
							}catch(Exception e) {
								logger.error("[{}] {} 302 inner error!", transactionUid, e);
							}
						}
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode(ValidatorHelper.removeSpecialCharacters(String.valueOf(httpstatuscode)));
								prd1.setRequestStatus(RequestType.fail.name());
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
						}
					}
				}
			}else {
				if (portalResource.getDataSendMethod().equalsIgnoreCase("post")) {
					// post
					Long systime = System.currentTimeMillis();
					URL connectto = new URL(portalResource.getDataEndpoint());
					HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
					conn.setRequestMethod("POST");
					/**
					 * portalResource.getDataHeader()  header參數
					 */
					logger.info("[{}] ===portalResource.getDataParam()==: {}", transactionUid, portalResource.getDataParam());
					byte[] contentByteArray = null;
					if(portalResource.getDataParam()==null) {
						contentByteArray = "{}".getBytes("UTF-8");
					}else {
						contentByteArray = PersonalRestController.replaceParamStrForPost(portalResource.getDataParam().trim(),sr).getBytes("UTF-8");
					}
					if (portalResource.getDataHeader() != null && portalResource.getDataHeader().trim().length() > 0) {
						try {
							JsonNode jsonNode = om.readTree(PersonalRestController.replaceParamStrForPostNotAppendEscapAppendTransactionUid(portalResource.getDataHeader().trim(),
									sr,transactionUid,paramMap));
							Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();
							while (jsonNodes.hasNext()) {
								Entry<String, JsonNode> node = jsonNodes.next();
								conn.setRequestProperty(node.getKey(), node.getValue().asText());
							}
							logger.info("[{}] contentByteArray.length= {}", transactionUid, contentByteArray.length);
							conn.setRequestProperty("Content-Length", String.valueOf(contentByteArray.length));
						} catch (IOException e) {
							logger.error("[{}] {}", transactionUid, e);
						}
					}
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setInstanceFollowRedirects(false);
					conn.setDoOutput(true);
					/**
					 * portalResource.getDataParam() body參數
					 */					
					OutputStream os = null;
					try {
						os = conn.getOutputStream();
						os.write(contentByteArray,0,contentByteArray.length);
						os.close();
					}finally {
						if(os!=null) {
							HttpClientHelper.safeClose(os);
						}
					}
					/**
					 * 連線狀態
					 */
					httpstatuscode = conn.getResponseCode();
					Long endTime = System.currentTimeMillis() - systime;
					logger.info("[{}] ===post httpstatuscode===: {}", transactionUid, httpstatuscode);
					if(httpstatuscode == HttpStatus.OK.value()) {
						// 不需處理，直接下載 200 
						InputStream inputStream = null;
						FileOutputStream outputStream = null;
						try {
							inputStream = conn.getInputStream();
							outputStream = new FileOutputStream(tempFile);
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
						
						logger.info("[{}] File downloaded ", transactionUid);

						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("200");
								prd1.setServerDownloadTime(new Date());
								prd1.setResponseTime(endTime.intValue());
								prd1.setDigest(DigestUtil.generateFileDigest(tempFile));
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}
						}
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, ActionEvent.EVENT_280, null, null, null);
					}else if(httpstatuscode == HttpStatus.TOO_MANY_REQUESTS.value()||httpstatuscode == HttpStatus.UPGRADE_REQUIRED.value()){
						//get all headers
						logger.info("[{}] === 429 start ===:2: ", transactionUid);
						Map<String, List<String>> headersmap = conn.getHeaderFields();
						for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
							logger.info("[{}] Key : {} , Value : {} ", transactionUid, entry.getKey(), entry.getValue());
							//Retry-After
							if(entry!=null&&entry.getKey()!=null&&entry.getKey().equalsIgnoreCase("Retry-After")) {
								retryAfter = ValidatorHelper.limitNumber(Integer.valueOf(entry.getValue().get(0)));
							}
						}
						// Waiting429DownloadAndFtp
						if(next<=16) {
							Integer nextWaittime = 0;
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								PortalResourceDownload prd = portalResourceDownloadList.get(0);
								nextWaittime = ValidatorHelper.limitNumber(prd.getWaitTime())+retryAfter;
								/**
								 * 小於90 min(5400 sec)
								 */
								if(nextWaittime<=5400) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setWaitTime(nextWaittime);
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									if(batchId!=null) {
										PortalBatchDownload portalBatchDownload = portalBatchDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(batchId));
										if(portalBatchDownload!=null&&portalBatchDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalBatchDownload portalBatchDownload1 = new PortalBatchDownload();
											portalBatchDownload1.setId(ValidatorHelper.limitNumber(portalBatchDownload.getId()));
											portalBatchDownload1.setWaitTime(nextWaittime);
											portalBatchDownloadMapper.updateByPrimaryKeySelective(portalBatchDownload1);
										}
									}
									if(psdId!=null) {
										PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdId));
										if(portalServiceDownload!=null&&portalServiceDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalServiceDownload portalServiceDownload1= new PortalServiceDownload();
											portalServiceDownload1.setId(ValidatorHelper.limitNumber(portalServiceDownload.getId()));
											portalServiceDownload1.setWaitTime(nextWaittime);
											portalServiceDownloadMapper.updateByPrimaryKeySelective(portalServiceDownload1);
										}
									}
								}
							}
							/**
							 * box時間延長
							 */
							if(nextWaittime<=5400) {
								Map<String, Object> boxparam = new HashMap<String, Object>();
								boxparam.put("downloadSn", ValidatorHelper.removeSpecialCharacters(portalResourceDownloadList.get(0).getDownloadSn()));
								boxparam.put("ctimeDesc", true);
								List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(boxparam);
								if(portalBoxList!=null&&portalBoxList.size()>0) {
									PortalBox box = portalBoxList.get(0);
									Calendar cal2 = GregorianCalendar.getInstance();
									cal2.setTime(new Date());
									cal2.add(Calendar.SECOND, retryAfter);
									Date boxStartTime = cal2.getTime();
									PortalBox box1 = new PortalBox();
									box1.setId(ValidatorHelper.limitNumber(box.getId()));
									box1.setCtime(boxStartTime);
									//必須判斷欄位
									box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(box.getAgentUid()));
									box1.setAgentBirthdate(ValidatorHelper.limitDate(box.getAgentBirthdate()));
									box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(box.getAgentVerify()));
									box1.setAgreeAgent(ValidatorHelper.limitNumber(box.getAgreeAgent()));
									portalBoxMapper.updateByPrimaryKeySelective(box1);
								}
								Waiting429DownloadAndFtp newThread = new Waiting429DownloadAndFtp(next, tempFile, ftpHost, ftpUsername, ftpPassword, retryAfter, ftpSecretkey, portalResource, portalResourceDownloadMapper,portalBoxMapper, ulogUtil, sendLogUtil, sr, transactionUid, paramMap, batchId, psdId, portalBatchDownloadMapper, portalServiceDownloadMapper, scopeStr, portalService, txId);
								newThread.start();								
							}else {
								if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
									for(PortalResourceDownload prd:portalResourceDownloadList) {
										PortalResourceDownload prd1 = new PortalResourceDownload();
										prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
										prd1.setCode("508");
										prd1.setRequestStatus(RequestType.fail.name());
										prd1.setResponseTime(endTime.intValue());
										portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									}

									ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
								}
							}
						}else {
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								for(PortalResourceDownload prd:portalResourceDownloadList) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setCode("508");
									prd1.setRequestStatus(RequestType.fail.name());
									prd1.setResponseTime(endTime.intValue());
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
								}

								ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
							}
						}
					}else if(httpstatuscode == HttpStatus.NO_CONTENT.value()) {
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("204");
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						}
					}else {
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode(ValidatorHelper.removeSpecialCharacters(String.valueOf(httpstatuscode)));
								prd1.setRequestStatus(RequestType.fail.name());
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
						}
					}
				}else {
					Long systime = System.currentTimeMillis();
					// get
					URL connectto = new URL(portalResource.getDataEndpoint());
					HttpURLConnection conn = (HttpURLConnection) connectto.openConnection();
					conn.setRequestMethod("GET");
					if (portalResource.getDataHeader() != null
							&& portalResource.getDataHeader().trim().length() > 0) {
						try {
							JsonNode jsonNode = om.readTree(PersonalRestController.replaceParamStrForPostNotAppendEscapAppendTransactionUid(portalResource.getDataHeader().trim(),
									sr,transactionUid,paramMap));
							Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();
							while (jsonNodes.hasNext()) {
								Entry<String, JsonNode> node = jsonNodes.next();
								conn.setRequestProperty(node.getKey(), node.getValue().asText());
							}
	
						} catch (IOException e) {
							logger.error("[{}] {}", transactionUid, e);
						}
					}
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setInstanceFollowRedirects(false);
					conn.setDoOutput(true);
					/**
					 * portalResource.getDataParam() body參數
					 */
					/**
					 * 連線狀態
					 */
					httpstatuscode = conn.getResponseCode();
					Long endTime = System.currentTimeMillis() - systime;
					logger.info("[{}] ===get httpstatuscode===: {} ", transactionUid, httpstatuscode);
					if(httpstatuscode == HttpStatus.OK.value()) {
						// 不需處理，直接下載 200
						InputStream inputStream = null;
						FileOutputStream outputStream = null;
						try {
							inputStream = conn.getInputStream();
							outputStream = new FileOutputStream(tempFile);
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
						
						logger.info("[{}] File downloaded", transactionUid);

						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("200");
								prd1.setServerDownloadTime(new Date());
								prd1.setResponseTime(endTime.intValue());
								prd1.setDigest(DigestUtil.generateFileDigest(tempFile));
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}
						}
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, ActionEvent.EVENT_280, null, null, null);
					}else if(httpstatuscode == HttpStatus.TOO_MANY_REQUESTS.value()||httpstatuscode == HttpStatus.UPGRADE_REQUIRED.value()){
						//429
						//get all headers
						Map<String, List<String>> headersmap = conn.getHeaderFields();
						logger.info("[{}] === 429 start ===:2:", transactionUid);
						for (Map.Entry<String, List<String>> entry : headersmap.entrySet()) {
							System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
							//Retry-After
							if(entry!=null&&entry.getKey()!=null&&entry.getKey().equalsIgnoreCase("Retry-After")) {
								retryAfter = ValidatorHelper.limitNumber(Integer.valueOf(entry.getValue().get(0)));
							}
						}
						// Waiting429DownloadAndFtp
						if(next<=16) {
							Integer nextWaittime = 0;
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								PortalResourceDownload prd = portalResourceDownloadList.get(0);
								nextWaittime = ValidatorHelper.limitNumber(prd.getWaitTime())+retryAfter;
								/**
								 * 小於90 min(5400 sec)
								 */
								if(nextWaittime<=5400) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setWaitTime(nextWaittime);
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									if(batchId!=null) {
										PortalBatchDownload portalBatchDownload = portalBatchDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(batchId));
										if(portalBatchDownload!=null&&portalBatchDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalBatchDownload portalBatchDownload1 = new PortalBatchDownload();
											portalBatchDownload1.setId(ValidatorHelper.limitNumber(portalBatchDownload.getId()));
											portalBatchDownload1.setWaitTime(nextWaittime);
											portalBatchDownloadMapper.updateByPrimaryKeySelective(portalBatchDownload1);
										}
									}
									if(psdId!=null) {
										PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdId));
										if(portalServiceDownload!=null&&portalServiceDownload.getWaitTime().compareTo(nextWaittime)<0) {
											PortalServiceDownload portalServiceDownload1= new PortalServiceDownload();
											portalServiceDownload1.setId(ValidatorHelper.limitNumber(portalServiceDownload.getId()));
											portalServiceDownload1.setWaitTime(nextWaittime);
											portalServiceDownloadMapper.updateByPrimaryKeySelective(portalServiceDownload1);
										}
									}
								}
							}
							/**
							 * box時間延長
							 */
							if(nextWaittime<=5400) {
								Map<String, Object> boxparam = new HashMap<String, Object>();
								boxparam.put("downloadSn", ValidatorHelper.removeSpecialCharacters(portalResourceDownloadList.get(0).getDownloadSn()));
								boxparam.put("ctimeDesc", true);
								List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(boxparam);
								if(portalBoxList!=null&&portalBoxList.size()>0) {
									PortalBox box = portalBoxList.get(0);
									Calendar cal2 = GregorianCalendar.getInstance();
									cal2.setTime(new Date());
									cal2.add(Calendar.SECOND, retryAfter);
									Date boxStartTime = cal2.getTime();
									PortalBox box1 = new PortalBox();
									box1.setId(ValidatorHelper.limitNumber(box.getId()));
									box1.setCtime(boxStartTime);
									//必須判斷欄位
									box1.setAgentUid(ValidatorHelper.removeSpecialCharacters(box.getAgentUid()));
									box1.setAgentBirthdate(ValidatorHelper.limitDate(box.getAgentBirthdate()));
									box1.setAgentVerify(ValidatorHelper.removeSpecialCharacters(box.getAgentVerify()));
									box1.setAgreeAgent(ValidatorHelper.limitNumber(box.getAgreeAgent()));
									portalBoxMapper.updateByPrimaryKeySelective(box1);
								}
								Waiting429DownloadAndFtp newThread = new Waiting429DownloadAndFtp(next, tempFile, ftpHost, ftpUsername, ftpPassword, retryAfter, ftpSecretkey, portalResource, portalResourceDownloadMapper,portalBoxMapper, ulogUtil, sendLogUtil, sr, transactionUid, paramMap, batchId, psdId, portalBatchDownloadMapper, portalServiceDownloadMapper, scopeStr, portalService, txId);
								newThread.start();					
							}else {
								if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
									for(PortalResourceDownload prd:portalResourceDownloadList) {
										PortalResourceDownload prd1 = new PortalResourceDownload();
										prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
										prd1.setCode("508");
										prd1.setRequestStatus(RequestType.fail.name());
										prd1.setResponseTime(endTime.intValue());
										portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
									}

									ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
								}
							}
						}else {
							if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
								for(PortalResourceDownload prd:portalResourceDownloadList) {
									PortalResourceDownload prd1 = new PortalResourceDownload();
									prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
									prd1.setCode("508");
									prd1.setRequestStatus(RequestType.fail.name());
									prd1.setResponseTime(endTime.intValue());
									portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
								}

								ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
							}
						}
					}else if(httpstatuscode == HttpStatus.NO_CONTENT.value()) {
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode("204");
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 18, null);
						}
					}else {
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							for(PortalResourceDownload prd:portalResourceDownloadList) {
								PortalResourceDownload prd1 = new PortalResourceDownload();
								prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
								prd1.setCode(ValidatorHelper.removeSpecialCharacters(String.valueOf(httpstatuscode)));
								prd1.setRequestStatus(RequestType.fail.name());
								prd1.setResponseTime(endTime.intValue());
								portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
							}

							ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 19, null);
						}
					}
				}
			}
		} catch (Exception e1) {
			for(PortalResourceDownload prd:portalResourceDownloadList) {
				PortalResourceDownload prd1 = new PortalResourceDownload();
				prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
				prd1.setCode("403");
				prd1.setRequestStatus(RequestType.fail.name());
				portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
			}

			ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 20, null);
			logger.error("[{}] {}", transactionUid, e1.getLocalizedMessage());
			logger.error(e1.getLocalizedMessage(), e1);
			e1.printStackTrace();
		}
		/**
		 * 4. FTP
		 */
		if(httpstatuscode==HttpStatus.OK.value()) {
			try {
				logger.info("[{}] ==tempFile.getAbsolutePath()==: {}", transactionUid, tempFile.getAbsolutePath());
				
				/**
				 * 檔案路徑重新整理
				 * API.vTL7cCtoSl 財產資料(RAW DATA)
				 * API.mBqP4awHJY 所得資料(RAW DATA)
				 * tempFile ---> new tempFile
				 */
				List<String> resourceIdSpecList = Arrays.asList("API.vTL7cCtoSl", "API.mBqP4awHJY");
				if(!Config.AppContextUrl.equalsIgnoreCase("https://mydatadev.nat.gov.tw/mydata")&&resourceIdSpecList.contains(portalResource.getResourceId())) {
					tempFile = ResourceApplyThread.handleSpecPath(tempFile);
				}
				
				byte[] b = Files.readAllBytes(Paths.get(tempFile.getAbsolutePath()));
				byte[] encryptb = PersonalRestController.encrypt(b,ftpSecretkey);
				File filename1enc = new File(tempFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(tempFile.getAbsolutePath())+"enc."+FilenameUtils.getExtension(tempFile.getAbsolutePath()));
				logger.info("[{}] ==filename1enc.getAbsolutePath()==: {}", transactionUid, filename1enc.getAbsolutePath());
				//System.out.println("==filename1enc.getAbsolutePath()==:"+filename1enc.getAbsolutePath());
				FileUtils.writeByteArrayToFile(filename1enc, encryptb);
				logger.info("[{}] ==filename1enc.getAbsolutePath() ok!==", transactionUid);
				//System.out.println("==filename1enc.getAbsolutePath() ok!==");
				FTPClient client = new FTPClient();
				FileOutputStream fos = null;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(filename1enc);
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.setBufferSize(1024 * 1024 * 10);
					client.changeWorkingDirectory("/mydata");
					client.storeFile(tempFile.getName(), fis);
					client.logout();
					client.disconnect();			
				}finally {
					if(fis!=null) {
						HttpClientHelper.safeClose(fis);
					}
				}
				/**
				 * 寄信或發簡訊通知（目前僅httpcode 200且檔案準備完成才寄通知）
				 * 1. psdId == null (個人和多筆下載需通知，服務不用)
				 * 2. 觀察member聯絡方式區分MAIL或簡訊
				 */
				/**
				 * 單筆非即時資料集，簡訊或通知信
				 */
				try {
					Member member = SessionMember.getSessionMemberToMember(sr.getMember());
					if(StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
						String from = "mydata_system@ndc.gov.tw";
						String title = "【個人化資料自主運用(MyData)平臺】個人資料已完成下載（系統信件）";
						Map<String, Object> boxparam = new HashMap<String, Object>();
						boxparam.put("downloadSn", ValidatorHelper.removeSpecialCharacters(portalResourceDownloadList.get(0).getDownloadSn()));
						boxparam.put("ctimeDesc", true);
						List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(boxparam);
						if(portalBoxList!=null&&portalBoxList.size()>0) {
							tmpBoxId = portalBoxList.get(0).getId().toString();
						}else {
							tmpBoxId = "";
						}
						String content = "您好：<br><br>\n\n"
								+ "感謝您使用個人化資料自主運用(MyData)平臺，您申請的「" + portalResource.getName() + "」已於"+sdf8.format(new Date())+"下載完畢。" +
								"請您於8小時內返回個人化資料自主運用(ＭyData)平臺，並登入<a href=\""+ Config.AppContextUrl +"/signin?toPage=verification"+ tmpBoxId +"\">資料條碼區</a>查看。<br>\n"
								+ "<br>\n";
						/**
						 * 僅線上服務不用此段文字
						 */
//						if(!(psdId!= null && txId !=null && txId.trim().length()>0)) {
//							content= content+ "請您於8小時內返回 個人化資料自主運用(MyData)平臺，並登入 <a href=\""+ Config.AppContextUrl +"/signin?toPage=verification"+ tmpBoxId +"\">資料條碼區</a>查看。<br>\n";
//						}
						
						content= content+ "<br>\n"
								+ "此為系統信件，請勿回信。<br>\n"
								+ "如有任何疑問或非您本人下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。<br>\n"
								+ "<br>\n"
								+ "——-<br>\n"
								+ "我為什麼會收到這封信？<br>\n"
								+ "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺轉存或傳輸您的個人資料給上述單位，當您轉存或當服務單位收到您的個人資料，系統會自動發此信通知您。<br>\n";
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
				}catch(Exception ex) {
					System.out.println("--寄信失敗--:\n"+ex);
				}
				
			} catch (Exception e1) {
				for(PortalResourceDownload prd:portalResourceDownloadList) {
					PortalResourceDownload prd1 = new PortalResourceDownload();
					prd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(prd.getDownloadSn()));
					prd1.setCode("403");
					prd1.setRequestStatus(RequestType.fail.name());
					portalResourceDownloadMapper.updateByPrimaryKeySelective(prd1);
				}

				ulogUtil.recordFullByPr(sr, portalService, txId, portalResource, transactionUid, null, scopeStr, 20, null);
				logger.error("{} >> error ", transactionUid);
				logger.error(e1.getLocalizedMessage(), e1);
			}
		} else if (httpstatuscode==HttpStatus.NO_CONTENT.value()){
			try {
				if(psdId==null) {
					Member member = SessionMember.getSessionMemberToMember(sr.getMember());
					if(StringUtils.equals(member.getInformMethod(), "email") && BooleanUtils.isTrue(member.getEmailVerified())) {
						String from = "mydata_system@ndc.gov.tw";
						String title = "【個人化資料自主運用(MyData)平臺】個人資料查詢完成（系統信件）";
						String content = "您好：\n\n"
								+ "感謝您使用 個人化資料自主運用(MyData)平臺，您申請的「" + portalResource.getName() + "」已於"+sdf8.format(new Date())+"確認查無相關(證明)資料。\n"
								+ "\n"
								+ "此為系統信件，請勿回信。\n"
								+ "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
						List<String> tmpReveicers = new ArrayList<String>();
						tmpReveicers.add(member.getEmail());
						MailUtil.sendMail(tmpReveicers,from, title,content,Config.mailEnable);
						logger.debug("send Email for 204");
					} else if (StringUtils.equals(member.getInformMethod(), "mobile") && BooleanUtils.isTrue(member.getMobileVerified())) {
						String smbody = "MyData 平臺通知-個人資料查詢完成，您申請的「"+portalResource.getName()+"」查無相關(證明)資料";
						SMSUtil.sendSms(member.getMobile(), smbody);
						logger.debug("send mobile for 204");
					} else {
						System.out.println("--寄信失敗--:\n無綁定任何聯絡方式");
					}
				}
			}catch(Exception ex) {
				System.out.println("--寄信失敗--:\n"+ex);
			}
		}
	}
}
