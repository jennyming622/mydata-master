package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.Authcode;
import tw.gov.ndc.emsg.mydata.mapper.AuthcodeMapper;


@Controller
@RequestMapping("/login")
public class LoginController {
	private static Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	
	@Autowired
	private AuthcodeMapper authcodeMapper;
	
	/**
	 * https://login.cp.gov.tw/v1/connect/authorize
	 */
	@Value("${app.oidc.authorize.uri}")
	private String authorizeUri;
	
	//桃園署立醫院，孕婦健康手冊
	@Value("${gsp.oidc.client.id}")
	private String clientId;	
	@Value("${app.oidc.redirect.uri}")
	private String redirectUri;
	
	@Value("${app.oidc.response.type}")
	private String responseType;
	@Value("${app.oidc.response.mode}")
	private String responseMode;	
	
	@GetMapping
	public String getLogin(HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) {		
		/**
		 * pid = 身分證字號，
		 * client_id = 
		 * tygh.sme.prenatal-care(SP 桃園署立醫院，孕婦健康手冊), 
		 * tygh.sme.children-care(SP 桃園署立醫院，孕婦健康手冊),
		 * mydata.portal(SP 國發會MyData入口網)
		 */
		List<Authcode> authcodes = null;
		String pid = request.getParameter("pid");
		if(pid!=null&&pid.trim().length()>0&&pid.trim().equalsIgnoreCase("null")) {
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("pid", ValidatorHelper.removeSpecialCharacters(pid));
			param.put("clientId", clientId);
			authcodes = authcodeMapper.selectByExample(param);
		}
		if(authcodes==null||authcodes.size()==0) {
			return "redirect:/sp/tyghSP_prenatal";
		}else{
			String uid = authcodes.get(0).getPid();
			if(uid != null && uid.trim().length()>0) {
				System.out.println("--------------------doAutoLogin before--------------------------------");
				System.out.println(uid);
				doAutoLogin(uid,uid,request);
				HttpSession session = request.getSession();
				session.setAttribute("accessToken", authcodes.get(0).getAccessToken());
				System.out.println("--------------------doAutoLogin after--------------------------------");
				return "redirect:/sp/prenatal";
			} else {
				return "redirect:/sp/tyghSP_prenatal";
			}
		}
	}
	
	
	/**
	 * GSP OpenID Connect的登入授權網址。
	 * @param scope	指定授權項目
	 * @param nonce	備註資訊
	 * @param state	狀態資訊
	 * @return
	 */
	public String GetAuthorizationUrl(String clientId,String redirectUri,String responseMode,String responseType,List<String> scopeList, String nonce, String state) {		
		StringBuilder sb = new StringBuilder();
		sb.append(authorizeUri)
			.append("?client_id=").append(clientId)
			.append("&redirect_uri=").append(redirectUri)
			.append("&response_mode=").append(responseMode)
			.append("&response_type=").append(responseType);
		final StringJoiner sj = new StringJoiner(" ");
		scopeList.forEach(p -> {
			sj.add(p);
		});
		if(StringUtils.isNotEmpty(sj.toString())) {
			sb.append("&scope=").append(sj.toString());
		}
		if(StringUtils.isNotEmpty(nonce)) {
			sb.append("&nonce=").append(nonce); 
		}
		if(StringUtils.isNotEmpty(state)) {
			sb.append("&state=").append(state);
		}
		return sb.toString();
	}
	
	private void doAutoLogin(String username, String password, HttpServletRequest request) {
	    try {
	        // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
	    		//request.getSession().setMaxInactiveInterval(20*60);
	    		request.getSession().setMaxInactiveInterval(10);
	        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password,Collections.emptyList());
	        token.setDetails(new WebAuthenticationDetails(request));
	        /*CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
	        Authentication authentication = customAuthenticationProvider.authenticate(token);
	        logger.debug("Logging in with [{}]", authentication.getPrincipal());*/
	        SecurityContextHolder.getContext().setAuthentication(token);
	    } catch (Exception e) {
	        //SecurityContextHolder.getContext().setAuthentication(null);
	        try {
				request.logout();
			} catch (ServletException ex) {
				ex.printStackTrace();
			}
	        logger.error("Failure in autoLogin", e);
	    }

	}	
	
}
