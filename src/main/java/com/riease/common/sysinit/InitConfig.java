package com.riease.common.sysinit;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;


public class InitConfig { 

	protected static ResourceBundle rb = null;
	
	
	/*================ ImageMagic ====================*/
	/**
	 * ImageMggick installation directory
	 */
	public static String ImageMagicPath = getValue("ImageMagick.path");
	
	
	/*================ E-mail ====================*/
	public static String SMTP_SENDER = getValue("smtp.senderAddress");
	public static String SMTP_SENDER_NAME = getValue("smtp.senderName");
	public static String SMTP_ACCOUNT = getValue("smtp.account");
	public static String SMTP_PASSWD = getValue("smtp.password");
	public static String SMTP_HOST = getValue("smtp.host");
	public static Integer SMTP_PORT = getIntValue("smtp.port",null);
	public static boolean SMTP_SSL = getBooleanValue("smtp.ssl.enable");
	
	
	/*================ 手機推播訊息 ====================*/
	/**
	 * Google推播。GCM API key。
	 */
	public static final String GCM_HOST = getValue("gcm.host");;
	public static final String GCM_API_KEY = getValue("gcm.apiKey");
	/**
	 * Apple訊息推播。APN開發憑證。APN正式憑證。
	 */
	public static final String APN_developCertFile = getValue("apn.certFile.dev");
	public static final String APN_developCertPasswd = getValue("apn.certPassword.dev");
	public static final String APN_productCertFile = getValue("apn.certFile.production");
	public static final String APN_productCertPasswd = getValue("apn.certPassword.production");
	
	
	/*================ Google, reCAPTCHA ====================*/
	public static final String reCAPTCHA_verifyUrl = getValue("recaptcha.verifyUrl");
	public static final String reCAPTCHA_siteKey = getValue("recaptcha.site-key");
	public static final String reCAPTCHA_secretKey = getValue("recaptcha.secret-key");
	
	/**
	 * QQ Mail
	 */
	public static final String qq_mail_account = getValue("qq.mail.account");
	public static final String qq_mail_password = getValue("qq.mail.password");
	
	
	
	/**
	 * 以boolean值返回。若沒有對應的key則返回false。
	 * @param key
	 * @return
	 */
	public static boolean getBooleanValue(String key) {
		return getBooleanValue(key,false);
	}
	
	/**
	 * 以int值返回。若沒有對應的key則返回0。
	 * @param key
	 * @return
	 */
	public static int getIntValue(String key) {
		return getIntValue(key,0);
	}
	
	/**
	 * 以String值返回。若沒有對應的key則返回null。
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		return getValue(key,null);
	}
	
	/**
	 * 以String值返回。若沒有對應的key返回指定的預設值。
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getValue(String key, String defaultValue) {
		String result = "";
		try {
			if(rb == null) {
				rb = ResourceBundle.getBundle("conf.common",Locale.getDefault());
			}
			result = rb.getString(key);
			result = new String(result.getBytes("ISO-8859-1"),"UTF-8");
			if(result != null) result = result.trim();
			else result = defaultValue;
		} catch (Exception ee) {
			result = key;
		}
		return result;
	}
	
	/**
	 * 以Integer值返回。若沒有對應的key返回指定的預設值。
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Integer getIntValue(String key, Integer defaultValue) {
		String val = getValue(key);
		if(val != null && NumberUtils.isDigits(val)) {
			return Integer.valueOf(val);
		}else {
			return defaultValue;
		}
	}
	
	/**
	 * 以Boolean值返回。若沒有對應的key返回指定的預設值。
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Boolean getBooleanValue(String key, Boolean defaultValue) {
		String val = getValue(key);
		if(val != null) {
			return BooleanUtils.toBoolean(val);
		}else {
			return defaultValue;
		}
	}
	
	/*
	 * 合併路徑
	 */
	public static String joinPath(String path1, String path2) {
		if(!path1.endsWith("/")) {
			path1 += "/";
		}
		if(path2.startsWith("/")) {
			path2 = path2.substring(1);
		}
		return path1+path2;
	}
	
}
