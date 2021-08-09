package tw.gov.ndc.emsg.mydata.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import tw.gov.ndc.emsg.mydata.entity.PortalBatchDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownloadExample;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceDownload;
import tw.gov.ndc.emsg.mydata.mapper.PortalBatchDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceDownloadMapper;

public class WaitingBatchDownloadAndFtp extends Thread {
	private File tempFile;
	private String ftpHost;
	private String ftpUsername;
	private String ftpPassword;
	private PortalBatchDownload pbdbean;
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	private PortalResourceMapper portalResourceMapper;
	private PortalBatchDownloadMapper portalBatchDownloadMapper;
	private String ftpSecretkey;
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
	
	public WaitingBatchDownloadAndFtp(File tempFile,String ftpHost,String ftpUsername,String ftpPassword,PortalBatchDownload pbdbean,PortalResourceDownloadMapper portalResourceDownloadMapper,PortalResourceMapper portalResourceMapper,PortalBatchDownloadMapper portalBatchDownloadMapper,String ftpSecretkey) throws NoSuchAlgorithmException, KeyManagementException {
		this.tempFile = tempFile;
		this.ftpHost = ftpHost;
		this.ftpUsername = ftpUsername;
		this.ftpPassword = ftpPassword;
		this.pbdbean = pbdbean;
		this.portalResourceDownloadMapper = portalResourceDownloadMapper;
		this.portalResourceMapper = portalResourceMapper;
		this.portalBatchDownloadMapper = portalBatchDownloadMapper;
		this.ftpSecretkey = ftpSecretkey;
	}	
	
	public void run() {


		
		List<String> downloadSnList = new ArrayList<>();
		if(pbdbean.getDownloadSnList()!=null&&pbdbean.getDownloadSnList().trim().length()>0) {
			String[] downloadSnArray = pbdbean.getDownloadSnList().trim().split("[,]");
			for(String s:downloadSnArray) {
				downloadSnList.add(s);
			}
		}
		if(downloadSnList!=null&&downloadSnList.size()>0) {
			boolean totalCheck = false;
			boolean[] checkArray = new boolean[downloadSnList.size()]; 
			for(int i=0;i<downloadSnList.size();i++) {
				checkArray[i] = false;
			}
			ArrayList<File> localFiles = new ArrayList<File>();
			try {
				if(pbdbean.getWaitTime()==null||pbdbean.getWaitTime()==0) {
				}else {
					Thread.sleep(pbdbean.getWaitTime() *1000);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while(totalCheck==false) {
				for(int i=0;i<downloadSnList.size();i++) {
					PortalResourceDownload  portalResourceDownload = null;
					PortalResource portalResource = null;
					Map<String,Object> param1 = new HashMap<String,Object>();
					param1.put("downloadSn",downloadSnList.get(i));
					List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
					if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
						portalResourceDownload = portalResourceDownloadList.get(0);
						portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceDownload.getPrId()));
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
							cal.add(Calendar.SECOND, waitTime);
						}else {
							//cal.add(Calendar.SECOND, 60);
						}
						if(waitTime!=0) {
							cal.add(Calendar.SECOND, (int)(waitTime/2));
						}else {
							cal.add(Calendar.SECOND, 180);
						}
						Date breakTime = cal.getTime();
						/**
						 * 估計時間小於現在，進行下載
						 */
						boolean downloadCheck = false;
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
							String localFileName = tempFile.getAbsolutePath() + File.separator + (portalResource==null?"":ValidatorHelper.removeSpecialCharacters(portalResource.getResourceId()))+"."+extStr;
							System.out.println("localFileName="+localFileName);
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
							File filename1enc = new File(localFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(localFile.getAbsolutePath())+"dec."+FilenameUtils.getExtension(localFile.getAbsolutePath()));
							byte[] b = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));
							try {
								byte[] decryptb = decrypt(b,ftpSecretkey);
								FileUtils.writeByteArrayToFile(filename1enc, decryptb);
							} catch (Exception e) {
								e.printStackTrace();
							}
							localFiles.add(filename1enc);
							downloadCheck= true;
						} catch (IOException e) {
							try {
								if(fos!=null) {
									fos.close();
								}
								client.logout();
								client.disconnect();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							e.printStackTrace();
						}
						//下載成功	
					}
				}
				/**
				 * 全域檢查
				 * 預設成功當全部成功為true,否則false
				 */
				totalCheck=true;
				System.out.println("==多筆下載全域檢查==:"+totalCheck);
				if(totalCheck==false) {
					try {
						Thread.sleep(10 *1000);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}
			}
			try {
				/**
				 * 將 tempFile 壓成一個檔案
				 * ftp /mydata
				 */
				File clientIdZip = packZip(new File(tempFile.getAbsolutePath() + File.separator + pbdbean.getFiles()), localFiles);
				
				/**
				 * FTPClient 
				 * ftp.retrieveFile 下載檔案
				 * ftp.storeFile 上傳檔案
				 * ftp.deleteFile()刪除遠端FTP Server的檔案
				 */
				byte[] b = Files.readAllBytes(Paths.get(clientIdZip.getAbsolutePath()));
				byte[] encryptb = encrypt(b,ftpSecretkey);
				File filename1enc = new File(tempFile.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(tempFile.getAbsolutePath())+"enc."+FilenameUtils.getExtension(tempFile.getAbsolutePath()));
				FileUtils.writeByteArrayToFile(filename1enc, encryptb);
				
				FTPClient client = new FTPClient();
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(filename1enc);
					client.connect(ftpHost, 21);
					client.login(ftpUsername, ftpPassword);
					client.setFileType(FTP.BINARY_FILE_TYPE);
					client.setBufferSize(1024 * 1024 * 10);
					client.changeWorkingDirectory("/mydata");
					client.storeFile(clientIdZip.getName(), fis);
					client.logout();
					client.disconnect();
            	}finally {
    				if(fis!=null) {
    					HttpClientHelper.safeClose(fis);
    				}
    			}	
			} catch (Exception e) {
				/**
				 * 9,異常中斷
				 */
				pbdbean.setStat(9);
				portalBatchDownloadMapper.updateByPrimaryKeySelective(pbdbean);
				e.printStackTrace();
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
