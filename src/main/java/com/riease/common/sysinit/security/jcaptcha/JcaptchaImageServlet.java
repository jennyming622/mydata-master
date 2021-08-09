package com.riease.common.sysinit.security.jcaptcha;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.riease.common.helper.ValidatorHelper;

/**
 * 產生Jcaptcha圖型驗證碼
 * Servlet implementation class JcaptchaImageServlet
 */
@WebServlet("/captcha.jpg")
public class JcaptchaImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    	
	private static Logger logger = LoggerFactory.getLogger(JcaptchaImageServlet.class);
	
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	SampleGimpyFactory captchaFactory;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JcaptchaImageServlet() {
        super();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext (this);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		response.setDateHeader("Expires", 0L);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");

		BufferedImage bi = null;
		String prId = request.getParameter("prId");
		
		if(StringUtils.isBlank(prId)) {
			System.out.println(request.getSession(true).getId());
			bi = imageCaptchaService.getImageChallengeForID(request.getSession(true).getId());
			request.getSession().setAttribute("currentCaptcha", captchaFactory.getWordBridge().getGeneratedWord());
		} else {
			bi = imageCaptchaService.getImageChallengeForID("captcha" + ValidatorHelper.removeSpecialLimitNumberAndAlphabet(prId));
			request.getSession().setAttribute("currentCaptcha"+ValidatorHelper.removeSpecialLimitNumberAndAlphabet(prId), captchaFactory.getWordBridge().getGeneratedWord());
			request.getSession().setAttribute("currentCaptcha", captchaFactory.getWordBridge().getGeneratedWord());
		}
	    
	    ServletOutputStream out = response.getOutputStream();
	    ImageIO.write(bi, "jpg", out);
	    try {
	      out.flush();
	    }finally {
	      out.close();
	    }
	    
	}
	
}
