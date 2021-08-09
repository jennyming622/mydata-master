/**
 * 
 */
package com.riease.common.bean;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * 
 * @author wesleyzhuang
 *
 */
public class Base64File {
	
	private String fileName;
	private String fileBase64String;
	private String mimeType;
	
	public Base64File() {
	}
	
	/**
	 * map key : 
	 * 	fname	：檔案名稱
	 *  data	：base64Encoded string
	 * @param map
	 * @throws Exception 
	 */
	public Base64File(Map<String,Object> map) throws Exception {
		if(map.containsKey("fname")) {
			setFileName((String)map.get("fname"));
		}else {
			throw new Exception("Miss map-key 'fname'");
		}
		if(map.containsKey("data")) {
			String dstr = (String)map.get("data");
			if(dstr.startsWith("data:")) {
				setMimeType(dstr.substring("data:".length(), dstr.indexOf(";base64")));
				setFileBase64String(dstr.substring(dstr.indexOf(";base64,")+";base64,".length()));
			}else {
				setFileBase64String(dstr);
			}
		}else {
			throw new Exception("Miss map-key 'data'");
		}
	}
	/**
	 * file name
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * set file name
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * get base64encoded string
	 * @return
	 */
	public String getFileBase64String() {
		return fileBase64String;
	}
	/**
	 * set base64encoded string
	 * @param fileBase64String
	 */
	public void setFileBase64String(String fileBase64String) {
		this.fileBase64String = fileBase64String;
	}
	/**
	 * 
	 * @param fileBase64String
	 */
	public void setFileBase64StringAndMine(String fileBase64String) {
		String dstr = fileBase64String;
		if(dstr.startsWith("data:")) {
			setMimeType(dstr.substring("data:".length(), dstr.indexOf(";base64")));
			setFileBase64String(dstr.substring(dstr.indexOf(";base64,")+";base64,".length()));
		}else {
			setFileBase64String(dstr);
		}
	}	
	/**
	 * 將base64encoded string轉換為binary。
	 * @return
	 */
	public byte[] toBinary() {
		if(this.fileBase64String == null || this.fileBase64String.isEmpty()) {
			return null;
		}else {
			return Base64.decodeBase64(this.fileBase64String);
		}
	}
	/**
	 * 將binary寫入指定目錄，並使用預設檔名。
	 * @param targetDir
	 * @return
	 * @throws IOException
	 */
	public File writeFile(File targetDir) throws IOException {
		return writeFile(targetDir, getFileName());
	}
	/**
	 * 將binary寫入指定目錄，並使用指定檔名。
	 * @param targetFile
	 * @throws IOException 
	 */
	public File writeFile(File targetDir, String fileName) throws IOException {
		byte[] bs = toBinary();
		if(bs == null) {
			return null;
		}
		if(!targetDir.exists()) {
			targetDir.mkdirs();
		}
		File f = new File(targetDir,fileName);
		if(f.exists()) {
			f.delete();
		}
		FileUtils.writeByteArrayToFile(f, toBinary());
		return f;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getFilenameExtension() {
		if(fileName != null && !fileName.isEmpty()) {
			return FilenameUtils.getExtension(fileName).toLowerCase(Locale.ENGLISH);
		}
		else if(mimeType != null && !mimeType.isEmpty()) {
			return mimeType.replace("image/", "").toLowerCase(Locale.ENGLISH);
		}
		else {
			return "";
		}
	}
	
}
