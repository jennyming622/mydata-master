package tw.gov.ndc.emsg.mydata.config;

import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.rest.RestResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{
	
	private final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);
	
	@Value("${app.oidc.authorize.uri}")
	private String gspOpenIdAuthorizeUri;
	@Value("${app.frontend.context.url}")
	private String frontendContextUrl;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
		String uri = request.getRequestURI();
		System.out.println("XMLHttpRequest >>" + ajaxHeader);
		boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
		if (isAjax && StringUtils.contains(uri, "rest")) {
			RestResponseBean rb = new RestResponseBean();
			rb.setCode(SysCode.SessionTimeout.value());
			rb.setText("連線逾時，系統已自動登出。");
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(new ObjectMapper().writeValueAsString(rb));
			out.flush();
		} else {
			System.out.println("== CustomAuthenticationEntryPoint ==");
			System.out.println(request.getServletPath());
			response.sendRedirect(frontendContextUrl);
		}
	}

}
