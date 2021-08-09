/**
 * 
 */
package com.riease.common.helper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * MIMETYPE協助物件
 * @author wesleyzhuang
 *
 */
public class MimeTypeHelper {
	
	//以extension為鍵值的表
	private static Map<String,String> extmap = new HashMap<String,String>();
	private static Map<String,String> mimemap = new HashMap<String,String>();
	static {
		extmap.put("txt", "text/plain");
		extmap.put("txt", "application/xml");
		extmap.put("pdf", "application/pdf");
		extmap.put("jpg", "image/jpeg");
		extmap.put("jpeg", "image/jpeg");
		extmap.put("png", "image/png");
		extmap.put("gif", "image/gif");
		extmap.put("bmp", "image/bmp");
		extmap.put("tif", "image/tiff");
		extmap.put("tiff", "image/tiff");
		extmap.put("doc", "application/msword");
		extmap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		extmap.put("xls", "application/vnd.ms-excel");
		extmap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		extmap.put("ppt", "application/vnd.ms-powerpoint");
		extmap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		
		mimemap.put("text/plain", "txt");
		mimemap.put("application/xml", "xml");
		mimemap.put("application/pdf", "pdf");
		mimemap.put("image/jpeg", "jpg");
		mimemap.put("image/png", "png");
		mimemap.put("image/gif", "gif");
		mimemap.put("image/bmp", "bmp");
		mimemap.put("image/tiff", "tif");
		mimemap.put("application/msword", "doc");
		mimemap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
		mimemap.put("application/vnd.ms-excel", "xls");
		mimemap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
		mimemap.put("application/vnd.ms-powerpoint", "ppt");
		mimemap.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
	}
	
	/**
	 * 以附檔名取mimetype
	 * @param extension
	 * @return
	 */
	public static String getMimeType(String extension) {
		return extmap.get(extension.toLowerCase(Locale.ENGLISH));
	}
	
	/**
	 * 以mimetype取附檔名
	 * @param mimetype
	 * @return
	 */
	public static String getExtension(String mimetype) {
		return mimemap.get(mimetype.toLowerCase(Locale.ENGLISH));
	}
	
}
