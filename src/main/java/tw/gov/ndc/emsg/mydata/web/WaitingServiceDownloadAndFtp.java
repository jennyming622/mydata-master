package tw.gov.ndc.emsg.mydata.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExt;
import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceDownload;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;

public class WaitingServiceDownloadAndFtp extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(WaitingServiceDownloadAndFtp.class);
	
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
	 * 加密/解密算法/工作模式/填充方式 AES/CBC/PKCS5PADDING
	 */
	public static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5PADDING";
	
	private File tempFile;
	private String ftpHost;
	private String ftpUsername;
	private String ftpPassword;
	private PortalServiceDownload psdbean;
	private PortalServiceMapper portalServiceMapper;
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	private PortalResourceMapper portalResourceMapper;
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	private String ftpSecretkey;
	
	public WaitingServiceDownloadAndFtp(File tempFile,String ftpHost,String ftpUsername,String ftpPassword,PortalServiceDownload psdbean,PortalServiceMapper portalServiceMapper,PortalResourceDownloadMapper portalResourceDownloadMapper,PortalResourceMapper portalResourceMapper,PortalServiceDownloadMapper portalServiceDownloadMapper, String ftpSecretkey) throws NoSuchAlgorithmException, KeyManagementException {
		this.tempFile = tempFile;
		this.ftpHost = ftpHost;
		this.ftpUsername = ftpUsername;
		this.ftpPassword = ftpPassword;
		this.psdbean = psdbean;
		this.portalServiceMapper = portalServiceMapper;
		this.portalResourceDownloadMapper = portalResourceDownloadMapper;
		this.portalResourceMapper = portalResourceMapper;
		this.portalServiceDownloadMapper = portalServiceDownloadMapper;
		this.ftpSecretkey = ftpSecretkey;
	}
	
	public void run() {
		List<String> downloadSnList = new ArrayList<>();
		if(psdbean.getDownloadSnList()!=null&&psdbean.getDownloadSnList().trim().length()>0) {
			String[] downloadSnArray = psdbean.getDownloadSnList().trim().split("[,]");
			for(String s:downloadSnArray) {
				downloadSnList.add(s);
			}
		}
		if(downloadSnList!=null&&downloadSnList.size()>0) {
			boolean totalCheck = false;
			boolean[] checkArray = new boolean[downloadSnList.size()]; 
			List<PortalResourceDownload> finalPortalResourceDownloadList = new ArrayList<PortalResourceDownload>();
			/**
			 * 所有回應碼
			 */
			List<PortalResourceExt> finalPortalResourceList = new ArrayList<PortalResourceExt>();
			/**
			 * 非 200, 204之外的其他回應碼
			 */
			List<PortalResourceExt> failPortalResourceList = new ArrayList<PortalResourceExt>();
			for(int i=0;i<downloadSnList.size();i++) {
				checkArray[i] = false;
			}
			ArrayList<File> localFiles = new ArrayList<File>();
			try {
				if(psdbean.getWaitTime()==null||psdbean.getWaitTime()==0) {
				}else {
					Thread.sleep(psdbean.getWaitTime() *1000 + 60 * 1000); // 延遲60秒
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while(totalCheck==false) {
				for(int i=0;i<downloadSnList.size();i++) {
					try {
						boolean notDoDownload = false;
						PortalResourceDownload  portalResourceDownload = null;
						PortalResource portalResource = null;
						Map<String,Object> param1 = new HashMap<String,Object>();
						param1.put("downloadSn",downloadSnList.get(i));
						List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
						if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
							portalResourceDownload = portalResourceDownloadList.get(0);
							portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceDownload.getPrId()));
							logger.info("[{}][{}] =downloadSn= ",psdbean.getTxId(), ValidatorHelper.removeSpecialCharacters(portalResourceDownload.getDownloadSn()));
						}
						if(portalResourceDownload!=null&&portalResource!=null) {
							/**
							 * 時間檢查
							 */
							Date ctime = portalResourceDownload.getCtime();
							Integer waitTime = portalResourceDownload.getWaitTime();
							Calendar cal = GregorianCalendar.getInstance();
							cal.setTime(ctime);
							if(waitTime!=0) {
								cal.add(Calendar.SECOND, 5400);
							}else {
								cal.add(Calendar.SECOND, 180);
							}
							Date breakTime = cal.getTime();
							if(portalResourceDownload.getCode().equalsIgnoreCase("426")||portalResourceDownload.getCode().equalsIgnoreCase("429")) {
								notDoDownload = true;
							}
							while(notDoDownload) {
								try {
									Thread.sleep(60 *1000);
								} catch (InterruptedException e2) {
									e2.printStackTrace();
								}
								portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
								if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
									portalResourceDownload = portalResourceDownloadList.get(0);
								}
								if(portalResourceDownload.getCode().equalsIgnoreCase("426")||portalResourceDownload.getCode().equalsIgnoreCase("429")) {
									if((new Date()).compareTo(breakTime)>=0) {
										notDoDownload = false;
									}else {
										notDoDownload = true;
									}
								}else {
									notDoDownload = false;
								}
							}

							/**
							 * 估計時間小於現在，進行下載
							 */
							boolean downloadCheck = false;
							if(portalResourceDownload.getCode().equalsIgnoreCase("200")) {
								FTPClient client = new FTPClient();
								FileOutputStream fos = null;
								//InetAddress ftpServer;
								try {
									//ftpServer = InetAddress.getByName(ftpHost);
									client.connect(ftpHost, 21);
									client.login(ftpUsername, ftpPassword);
									client.setFileType(FTP.BINARY_FILE_TYPE);
									client.setBufferSize(1024 * 1024 * 10);
									client.changeWorkingDirectory("/mydata");
									//判斷附檔名
									//portalResourceDownload.getFiles().split("\\.");
									String[] filesStrList = ValidatorHelper.removeSpecialCharacters(portalResourceDownload.getFiles()).split("[.]");
									String extStr = "";
									if(filesStrList.length==0) {
									}else {
										extStr = filesStrList[filesStrList.length-1];
									}
									String localFileName = tempFile.getAbsolutePath() + File.separator + (portalResource==null?"":ValidatorHelper.removeSpecialCharacters(portalResource.getResourceId()))+"encrypt."+extStr;
									logger.info("[{}][{}] localFileName {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), localFileName);
									//System.out.println("localFileName="+localFileName);
									File localFile = new File(localFileName);
									if(!localFile.getParentFile().exists()) {
										localFile.getParentFile().mkdirs();
									}
									fos = new FileOutputStream(localFileName);
									client.retrieveFile(ValidatorHelper.removeSpecialCharacters(portalResourceDownload.getFiles()), fos);
									if(fos!=null) {
										fos.close();
									}
									client.logout();
									client.disconnect();
									/**
									 * AES解密
									 */
									File filename1enc = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath()).replace("encrypt", "")+"."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
									byte[] b = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));
									try {
										byte[] decryptb = decrypt(b,ftpSecretkey);
										FileUtils.writeByteArrayToFile(filename1enc, decryptb);
									} catch (Exception e) {
										e.printStackTrace();
									}
									localFiles.add(filename1enc);
									downloadCheck= true;
								} catch (Exception e) {
									try {
										if(fos!=null) {
											fos.close();
										}
										client.logout();
										client.disconnect();
									} catch (IOException e1) {
										logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e1);
									}
									logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e);
								}
								//下載成功
								if(downloadCheck) {
									PortalResourceExt portalResourceExt = new PortalResourceExt();
									try {
										BeanUtils.copyProperties(portalResourceExt, portalResource);
									} catch (IllegalAccessException e) {
										logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e);
									} catch (InvocationTargetException e) {
										logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e);
									}
									portalResourceExt.setCode("200");
									finalPortalResourceList.add(portalResourceExt);
								}
							}else if(portalResourceDownload.getCode().equalsIgnoreCase("204")) {
								PortalResourceExt portalResourceExt = new PortalResourceExt();
								try {
									BeanUtils.copyProperties(portalResourceExt, portalResource);
								} catch (IllegalAccessException e) {
									logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e);
								} catch (InvocationTargetException e) {
									logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e);
								}
								portalResourceExt.setCode(portalResourceDownload.getCode());
								finalPortalResourceList.add(portalResourceExt);
							}else {
								PortalResourceExt portalResourceExt = new PortalResourceExt();
								try {
									BeanUtils.copyProperties(portalResourceExt, portalResource);
								} catch (IllegalAccessException e) {
									logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e);
								} catch (InvocationTargetException e) {
									logger.error("[{}][{}] {}", psdbean.getTxId(), portalResourceDownload.getDownloadSn(), e);
								}
								portalResourceExt.setCode(portalResourceDownload.getCode());
								finalPortalResourceList.add(portalResourceExt);
								failPortalResourceList.add(portalResourceExt);
							}
						}
					} catch (Exception e) {
						logger.error("[{}] throws Exception", psdbean.getTxId());
						logger.error(e.getLocalizedMessage(), e);
					}
				}
				/**
				 * 全域檢查
				 * 預設成功當全部成功為true,否則false
				 */
				totalCheck=true;
				logger.info("[{}] ==全域檢查==: {}", psdbean.getTxId(), totalCheck);
				if(totalCheck==false) {
					try {
						Thread.sleep(10 *1000);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}
			}
			/**
			 * 將 tempFile 壓成一個檔案
			 * ftp /mydata
			 */
			try {
				/**
				 * 產生/META-INFO/manifest.xml
				 */
				File xmlFile = new File(tempFile.getAbsolutePath() + File.separator +"META-INFO"+File.separator+"manifest.xml");
				String xmlFileStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
				xmlFileStr += "<files>";
				//List<PortalResource> finalPortalResourceList
				if(finalPortalResourceList!=null&&finalPortalResourceList.size()>0) {
					for(PortalResourceExt pr:finalPortalResourceList) {
						xmlFileStr +=  "<file>";
						//filename
						xmlFileStr += "<filename>"+pr.getResourceId()+".zip</filename>";
						//resource_id
						xmlFileStr += "<resource_id>"+pr.getResourceId()+"</resource_id>";
						//resource_name
						xmlFileStr += "<resource_name>"+pr.getName()+"</resource_name>";
						if(pr.getCode().equalsIgnoreCase("429")||pr.getCode().equalsIgnoreCase("426")) {
							xmlFileStr += "<code>403</code>";
						}else {
							xmlFileStr += "<code>"+pr.getCode()+"</code>";
						}
						xmlFileStr +=  "</file>";
					}
				}
				xmlFileStr += "</files>";
				FileUtils.writeStringToFile(xmlFile, xmlFileStr, "UTF-8");
				
				/**
				 * 如果有錯誤資料集 failPortalResourceList !=null && failPortalResourceList.size() > 0
				 */
				if(failPortalResourceList!=null&&failPortalResourceList.size()>0) {
					localFiles = new ArrayList<File>();
					psdbean.setFileFinishTime(null);
				}else {
					psdbean.setFileFinishTime(new Date());
				}
				File clientIdZip = packZip(new File(tempFile.getAbsolutePath() + File.separator + ValidatorHelper.removeSpecialCharacters(psdbean.getClientId()) +"." +SequenceHelper.createUUID() + ".zip"), localFiles, xmlFile);
				 
				 /**
				  * AES壓密---> 廢除
				  */
				byte[] b = null;
				PortalService portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(psdbean.getPsId()));
				String sequenceUUID = SequenceHelper.createUUID();
				File clientIdZipEncrypt = new File(tempFile.getAbsolutePath() + File.separator + ValidatorHelper.removeSpecialCharacters(psdbean.getClientId()) +"." +sequenceUUID + ".zip");

				
				/**
				 * 紀錄資料庫
				 */
				logger.info("[{}] ===portalServiceDownloadMapper.updateByPrimaryKey 1==", psdbean.getTxId());
				psdbean.setFiles(clientIdZipEncrypt.getName());
				portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean);
				logger.info("[{}] ===portalServiceDownloadMapper.updateByPrimaryKey 1== {}", psdbean.getTxId(), psdbean.getFiles());
				/**
				 * 使用tx_id有無判斷是線上服務或臨櫃服務 1.txId not null:線上服務 2.txId null:臨櫃服務
				 * 臨櫃服務才需要條碼(txId == null)
				 */
				if(psdbean.getTxId()==null) {
					Map<String, Object> tmpparam = new HashMap<String, Object>();
					tmpparam.put("prId", 0);
					tmpparam.put("psdId", psdbean.getId());
					List<PortalResourceDownload> tmpPortalResourceDownloadList = portalResourceDownloadMapper.selectByExample(tmpparam);
					if(tmpPortalResourceDownloadList!=null&&tmpPortalResourceDownloadList.size()>0) {
						PortalResourceDownload tmpprd = tmpPortalResourceDownloadList.get(0);
						PortalResourceDownload tmpprd1 = new PortalResourceDownload();
						tmpprd1.setDownloadSn(ValidatorHelper.removeSpecialCharacters(tmpprd.getDownloadSn()));
						tmpprd1.setCode("200");
						tmpprd1.setFiles(ValidatorHelper.removeSpecialCharacters(clientIdZipEncrypt.getName()));
						portalResourceDownloadMapper.updateByPrimaryKeySelective(tmpprd1);
					}
				}
				FileInputStream fis_cbc = null;
				try {
					b = Files.readAllBytes(Paths.get(clientIdZip.getAbsolutePath()));
					byte[] encryptb_cbc = encrypt_cbc(b,psdbean.getSecretKey(),portalService.getCbcIv());
					FileUtils.writeByteArrayToFile(clientIdZipEncrypt, encryptb_cbc);
					if(clientIdZipEncrypt.exists()) {
						logger.info("[{}] File clientIdZipEncrypt_cbc exists!", psdbean.getTxId());
					}
					/**
					 * cbc檔案上傳
					 */
					FTPClient client_cbc = new FTPClient();
					fis_cbc = new FileInputStream(clientIdZipEncrypt);
					//InetAddress ftpServer_cbc = InetAddress.getByName(ftpHost);
					client_cbc.connect(ftpHost, 21);
					client_cbc.login(ftpUsername, ftpPassword);
					client_cbc.setFileType(FTP.BINARY_FILE_TYPE);
					client_cbc.setBufferSize(1024 * 1024 * 10);
					client_cbc.changeWorkingDirectory("/mydata");
					client_cbc.storeFile(clientIdZipEncrypt.getName(), fis_cbc);
					client_cbc.logout();
					client_cbc.disconnect();
					logger.info("[{}] encryptb_cbc ftp success! ---->", psdbean.getTxId());
					//System.out.println("encryptb_cbc ftp success! ---->");
				}catch(Exception ex) {
					logger.error("[{}] encryptb_cbc error ----> {}", psdbean.getTxId(), ex);
					//System.out.println("encryptb_cbc error ---->"+ ex);
            	}finally {
    				if(fis_cbc!=null) {
    					HttpClientHelper.safeClose(fis_cbc);
    				}
    			}
			} catch (Exception e) {
				/**
				 * 9,異常中斷
				 */
				psdbean.setStat(9);
				psdbean.setFiles(null);
				psdbean.setFileFinishTime(null);
				portalServiceDownloadMapper.updateByPrimaryKeySelective(psdbean);
				//e.printStackTrace();
				logger.error("[{}] {}", psdbean.getTxId(), e);
			} 
		}
	}
	
	private File packZip(File output, ArrayList<File> sources, File xmlFile)
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
		if(sources!=null&&sources.size()>0) {
			zipFile.addFiles(sources, parameters);
		}
		parameters.setRootFolderInZip("META-INFO");
		zipFile.addFile(xmlFile, parameters);
		return output;
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
	
	public static byte[] encrypt_cbc(byte[] data, String key, String ivstr) throws Exception {
		// 還原密鑰
		//Key k = toKey(key);
		// 密鑰
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
		// 向量鰎值
		IvParameterSpec iv = new IvParameterSpec(ivstr.getBytes("UTF-8"));
		// 實例化
		Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0NCQy9QS0NTNVBBRERJTkc=")));
		// 初始化，設置為加密模式
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		// 執行操作
		return cipher.doFinal(data);
	}
	
	public static byte[] decrypt_cbc(byte[] data, String key, String ivstr) throws Exception {
		// 歡迎密鑰
		//Key k = toKey(key);
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
	
}
