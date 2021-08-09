/**
 * 
 */
package com.riease.common.helper;

import java.util.Locale;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mobile Country Code Helper
 * @author wesleyzhuang
 *
 */
public class MccHelper {

	private static Logger logger = LoggerFactory.getLogger(MccHelper.class);
	
	/**
	 * 
	 * @param mobileCountryCode
	 * @return
	 */
	public static Locale locale(String mobileCountryCode) {
		String mcc = null;
		if(mobileCountryCode.startsWith("+")) {
			mcc = mobileCountryCode.substring(1);
		}else {
			mcc = mobileCountryCode;
		}
		
		Locale locale = null;
		if(NumberUtils.isDigits(mcc)) {
			int code = Integer.parseInt(mcc);
			switch(code) {
			case 1:
				locale = new Locale("en","US");
				break;
			case 86:
				locale = new Locale("zh","CN");
				break;
			case 886:
				locale = new Locale("zh","TW");
				break;
			default:
				locale = new Locale("en","US");
			}
		}else {
			logger.warn("BAD mcc, {}", mobileCountryCode);
		}
		
		if(locale == null) {
			logger.trace("找不到對應的Locale, mcc={}", mobileCountryCode);
		}else {
			logger.trace("找到對應的Locale, mcc={}", mobileCountryCode);
		}
		
		return locale;
	}
	
}
