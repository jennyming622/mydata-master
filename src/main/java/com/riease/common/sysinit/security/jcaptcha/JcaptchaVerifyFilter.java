/**
 * 
 */
package com.riease.common.sysinit.security.jcaptcha;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.riease.common.helper.ValidatorHelper;

/**
 * 擷取請求中與jcaptcha有關的欄位時，並檢查驗證碼是否正確。
 * @author wesleyzhuang
 *
 */
@Component
public class JcaptchaVerifyFilter extends OncePerRequestFilter {

	private static Logger logger = LoggerFactory.getLogger(JcaptchaVerifyFilter.class);
	
	//reCAPTCHA用來放置驗證字串的參數名。
	public static final String JCAPTCHA_RESPONSE_FIELD = "captcha";
	
	private String challenge;
	private String failureUrl;
	private String targetUrl;
	private SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
	
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws ServletException, IOException {

		//jcapcha
		challenge = ValidatorHelper.removeSpecialCharacters(req.getParameter(JCAPTCHA_RESPONSE_FIELD));
		
		if(challenge != null && isTargetUri(req)) {
			
			boolean isValid = imageCaptchaService.validateResponseForID(req.getSession().getId(), challenge);
			logger.trace("challenge ...... {} , isValid ... {}", challenge, isValid);
			
			if(isValid) {
				challenge = null;
				isValid = false;
			}else {
				failureHandler.setDefaultFailureUrl(failureUrl);
				failureHandler.onAuthenticationFailure(req, resp, 
						new BadCredentialsException("JCAPTCHA invalid!"));
				return;
			}
		}
		
		//logger.trace("request ......... {}", req.getRequestURI());
		chain.doFilter(req, resp);
	}

	private boolean isTargetUri(HttpServletRequest req) {
		if(getTargetUrl() != null) {
			return req.getRequestURI().endsWith(getTargetUrl());
		}else {
			return false;
		}
	}

	/**
	 * @return the failureUrl
	 */
	public String getFailureUrl() {
		return failureUrl;
	}


	/**
	 * @param failureUrl the failureUrl to set
	 */
	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}

	/**
	 * @return the targetUrl
	 */
	public String getTargetUrl() {
		return targetUrl;
	}

	/**
	 * @param targetUrl the targetUrl to set
	 */
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
		
}
