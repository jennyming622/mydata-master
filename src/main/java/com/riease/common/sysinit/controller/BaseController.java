package com.riease.common.sysinit.controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.riease.common.bean.PagingInfo;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.SysCode;

import tw.gov.ndc.emsg.mydata.entity.SendLog;
import tw.gov.ndc.emsg.mydata.mapper.SendLogMapper;
import tw.gov.ndc.emsg.mydata.type.SendType;

/**
 * Spring MVC Controller的基礎物件
 * @author wesleyzhuang
 *
 */
public class BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private SendLogMapper sendLogMapper;
	
	/**
	 * 從請求參數中取出分頁資訊，並設值於PagingInfo物件。
	 * @param pageIndex
	 * @param pageSize
	 * @param totalSize
	 * @param paging
	 */
	public void handlePaging(Integer pageIndex, Integer pageSize, PagingInfo paging) {
		if(pageIndex != null) 
			paging.setPageIndex(pageIndex);
		if(pageSize != null) 
			paging.setPageSize(pageSize);
	}
	
	/**
	 * 從請求參數中取出分頁資訊，並設值於PagingInfo物件。
	 * @param value		value[0]：頁次，value[1]：每頁幾筆。
	 * @param paging	控制分頁的物件
	 */
	public void handlePaging(String value, PagingInfo paging) {
		if(value != null) {
			String[] val = value.split(",");
			if(val.length == 2) {
				if(NumberUtils.isDigits(val[0]) 
						&& NumberUtils.isDigits(val[1])) {
					paging.setPageIndex(Integer.parseInt(val[0]));
					paging.setPageSize(Integer.parseInt(val[1]));
				}
			}
		}
	}
	
	/**
	 * 從請求參數中取出分頁資訊，並設值於PagingInfo物件。
	 * @param params	請求參數 (request parameter)
	 * @param paging	控制分頁的物件
	 */
	public void handlePaging(Map<String,Object> params, PagingInfo paging) {
		if(params.containsKey("paging")) {
			Object obj = params.get("paging");
			if(obj != null) {
				String[] val = String.valueOf(obj).split(",");
				if(val.length == 2) {
					if(NumberUtils.isDigits(val[0]) 
							&& NumberUtils.isDigits(val[1])) {
						paging.setPageIndex(Integer.parseInt(val[0]));
						paging.setPageSize(Integer.parseInt(val[1]));
					}
				}
			}
		}
	}
	
	/**
	 * 依照系統代碼取得文字訊息
	 * @param code				系統代碼
	 * @param args				帶入訊息的參數
	 * @param defaultMessage	預設文字
	 * @return
	 */
	public String message(SysCode code, Object[] args, String defaultMessage) {
		return message(code.stringValue(), args, defaultMessage);
	}
	
	/**
	 * 依指定的資源KEY取得文字訊息
	 * @param key
	 * @param args
	 * @param defaultMessage
	 * @return
	 */
	public String message(String key, Object[] args, String defaultMessage) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(key, args, defaultMessage, locale);
	}
	
	/**
	 * 比較Session中的變數值與請求帶入的變數值，如果相同則回傳true，反之則回傳false。
	 * @param session
	 * @param code
	 * @return
	 */
	public boolean doubleCheck(HttpSession session, String key, String code) {
		Object checkCode = session.getAttribute(key);
		if(checkCode == null) {
			return false;
		}else if(!code.contentEquals((String)checkCode)) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * 回傳 http response error code。
	 * @param response
	 * @param code
	 * @param message
	 */
	public void sendError(HttpServletResponse response, int code, String message) {
		try {
			if(StringUtils.isNotEmpty(message)) {
				response.sendError(code,message);
			}else {
				response.sendError(code);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 輸出錯誤訊息到log
	 * @param error
	 */
	public void printErrors(BindingResult error) {
		logger.debug("field error count : {} , global error count : {}", error.getFieldErrorCount(), error.getGlobalErrorCount());
		List<FieldError> fes = error.getFieldErrors();
		fes.forEach(p -> {
			logger.debug("fieldError, field:{} , code:{}", p.getField(), p.getCode());
		});
		List<ObjectError> ges = error.getGlobalErrors();
		ges.forEach(p -> {
			logger.debug("globalError, objectName:{} , code:{} , arguments:{}, defaultMessage:{}", 
						p.getObjectName(), p.getCode(), p.getArguments(), p.getDefaultMessage());
		});
	}
	
	public void printParams(Map<String,Object> params) {
		Iterator<String> iter = params.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			String val = params.get(key).toString();
			logger.debug("request parameter, key:{} , val:{}", key,val);
		}
	}
	
	/**
	 * 取得目前登入者資訊
	 * @param session
	 * @return
	 */
	public SessionRecord currentSessionRecord(HttpSession session) {
		return (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
	}
	
	/**
	 * 取得目前登入者帳號
	 * @param session
	 * @return
	 */
	public String currentUserAccount(HttpSession session) {
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		if(sr != null) {
			return ValidatorHelper.removeSpecialCharacters(sr.getMember().getAccount());
		}else {
			return null;
		}
	}
	
	/**
     * 圖形驗證碼確認
     * @param request
     * @param params
     * @return
     */
	public Boolean captchaCheck(HttpServletRequest request, String captcha, String captchaKeyVal) {
		if(StringUtils.isBlank(captcha)) {
			return false;
		}
		System.out.println("captchaCheck >> " + captcha + " : " + captchaKeyVal);
		try {
			boolean isValid = false;
			if(StringUtils.isBlank(captchaKeyVal)) {
				isValid = imageCaptchaService.validateResponseForID(request.getSession().getId(), captcha);
			} else {
				isValid = imageCaptchaService.validateResponseForID("captcha" + captchaKeyVal, captcha);
			}
			if(BooleanUtils.isFalse(isValid)) {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		return true;
    }
}
