/**
 * 
 */
package com.riease.common.sysinit.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.riease.common.bean.PagingInfo;
import com.riease.common.sysinit.SysCode;

/**
 * 
 * @author wesleyzhuang
 *
 */
public class BaseModelService {

	@Autowired
	private MessageSource messageSource;
	
	//@Autowired
	//private Sessioncon
	
	
	/**
	 * 轉換為String，來源可包含String,Number,Boolean，defaultValue為null。
	 * @param params
	 * @param mapKey
	 * @return
	 */
	public String string(Map<String,Object> params, String mapKey) {
		return string(params,mapKey,null);
	}
	
	/**
	 * 轉換為String，來源可包含String,Number,Boolean，其它則回傳defaultValue。
	 * @param params
	 * @param mapKey
	 * @param defaultValue
	 * @return
	 */
	public String string(Map<String,Object> params, String mapKey, String defaultValue) {
		return stringValue(params.get(mapKey),defaultValue);
	}
	
	/**
	 * 轉換為String，來源可包含String,Number,Boolean，其它則回傳defaultValue。
	 * @param object
	 * @param defaultValue
	 * @return
	 */
	public String stringValue(Object object, String defaultValue) {
		if(object instanceof String) {
			return (String)object;
		}else if(object instanceof Number) {
			return String.valueOf((Number)object);
		}else if(object instanceof Boolean) {
			return String.valueOf((Boolean)object);
		}else {
			return defaultValue;
		}
	}
	
	/**
	 * 轉換為Integer，來源可包含String,Number,Boolean，defaultValue為null。
	 * @param params
	 * @param mapKey
	 * @return
	 */
	public Integer integer(Map<String,Object> params, String mapKey) {
		return integer(params,mapKey,null);
	}
	
	/**
	 * 轉換為Integer，來源可包含String,Number,Boolean，其它則回傳defaultValue。
	 * @param params
	 * @param mapKey
	 * @param defaultValue
	 * @return
	 */
	public Integer integer(Map<String,Object> params, String mapKey, Integer defaultValue) {
		return integerValue(params.get(mapKey),defaultValue);
	}
	
	/**
	 * 轉換為Integer，來源可包含String,Number,Boolean，其它則回傳defaultValue。
	 * @param object
	 * @param defaultValue
	 * @return
	 */
	public Integer integerValue(Object object, Integer defaultValue) {
		if(object instanceof Number) {
			return (Integer)object;
		}else if(object instanceof String) {
			String val = (String)object;
			if(NumberUtils.isNumber(val)) {
				return Integer.parseInt(val);
			}else {
				return defaultValue;
			}
		}else if(object instanceof Boolean) {
			Boolean b = (Boolean) object;
			return b?1:0;
		}else {
			return defaultValue;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> listValue(Object object, Class<T> clazz, List<T> defaultValue) {
		if(object instanceof List) {
			return (List<T>)object;
		}else {
			return defaultValue;
		}
	}
		
	/**
	 * 使用於like查詢，被%符號包住的字串。
	 * @param object
	 * @return
	 */
	public String likeString(Object object) {
		String str = stringValue(object,"");
		return String.format("%%s%", str);
	}
	
	/**
	 * myBatis的分頁物件。
	 * @param paging
	 * @return
	 */
	public RowBounds rowBounds(PagingInfo paging) {
		return new RowBounds(paging.getPageIndex()*paging.getPageSize(), paging.getPageSize());
	}
	
	/**
	 * 依照系統代碼取得文字訊息
	 * @param code				系統代碼
	 * @param args				帶入訊息的參數
	 * @param defaultMessage	預設文字
	 * @return
	 */
	public String message(SysCode code, Object[] args, String defaultMessage) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(code.stringValue(), args, defaultMessage, locale);
	}
}
