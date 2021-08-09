package com.riease.common.sysinit.security;

import org.hibernate.validator.constraints.NotEmpty;

import com.riease.common.validator.ValidateFirst;

/**
 * 使用Kaptcha做form submit時，裝載應驗證字串。
 * @author wesleyzhuang
 *
 */
public class KaptchaForm {
	
	@NotEmpty(message="{NotEmpty.captcha}", groups={ValidateFirst.class})
	private String captcha;

	/**
	 * @return the captcha
	 */
	public String getCaptcha() {
		return captcha;
	}

	/**
	 * @param captcha the captcha to set
	 */
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	
}
