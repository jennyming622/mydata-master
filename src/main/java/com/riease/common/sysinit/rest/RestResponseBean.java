/**
 * 
 */
package com.riease.common.sysinit.rest;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.riease.common.sysinit.SysCode;

/**
 * RESTful API 請求回覆物件
 * @author wesleyzhuang
 * @param <T>
 *
 */
public class RestResponseBean {

	private static Logger logger = LoggerFactory.getLogger(RestResponseBean.class);
	
	//系統代碼
	private Integer code = SysCode.OK.value();
	//代碼說明文字
	private String text = SysCode.OK.toString();
	
	//回覆內容的資料筆數
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer total;
	
	@JsonIgnore
	private Locale useLocale = new Locale("zh", "TW");
	
	private Object data;
	
	public RestResponseBean() {
	}
			
	public RestResponseBean(HttpServletRequest request) {
		//TODO 未來可從 request 裡面取出請求取得 header ，可判斷多國語系
		//useLocale = new Locale("zh", "TW");
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
