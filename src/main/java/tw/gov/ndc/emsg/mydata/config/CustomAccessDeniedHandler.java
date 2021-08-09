package tw.gov.ndc.emsg.mydata.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.rest.RestResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
 
	private final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
	
	@Value("${app.frontend.context.url}")
	private String frontendContextUrl;
	
    @Override
    public void handle(
      HttpServletRequest request,
      HttpServletResponse response, 
      AccessDeniedException exc) throws IOException, ServletException {
		String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
		String path = request.getServletPath();
		logger.debug("XMLHttpRequest >> {}", ajaxHeader);
		boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
		logger.debug("isAjax >> {}, path >> {}", isAjax, path);
		if (isAjax && StringUtils.contains(path, "rest")) {
			RestResponseBean rb = new RestResponseBean();
			rb.setCode(SysCode.SessionTimeout.value());
			rb.setText("連線逾時，系統已自動登出。");
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(new ObjectMapper().writeValueAsString(rb));
			out.flush();
		} else {
			logger.debug("== CustomAccessDeniedHandler ==");
			logger.debug("Path >> {}", request.getServletPath());
			response.sendRedirect(frontendContextUrl + "/error");
		}
    }
}
