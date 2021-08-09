/**
 * 
 */
package com.riease.common.bean;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 檔案資訊
 * @author wesleyzhuang
 *
 */
public class FileInfo {

	private String type;
	private long size;
	private String name;
	private String base64Data;
	
	
	public String getExtension() {
		if(StringUtils.isNotEmpty(getName())) {
			return FilenameUtils.getExtension(getName());
		}else {
			return null;
		}
	}
	
	/**
	 * MIMETYPE
	 * @return
	 */
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * File size in bytes
	 * @return
	 */
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	/**
	 * File name
	 * @return
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Base64 encoded data string.
	 * @return
	 */
	public String getBase64Data() {
		return base64Data;
	}
	public void setBase64Data(String base64Data) {
		this.base64Data = base64Data;
	}
	
}
