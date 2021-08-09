package com.riease.common.sysinit.security.jcaptcha;

import org.hibernate.validator.constraints.NotEmpty;

import com.riease.common.validator.ValidateFirst;

/**
 * 使用JCAPTCHA做form submit時，裝載應驗證字串。
 * @author wesleyzhuang
 *
 */
public abstract class JcaptchaForm {
	
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
