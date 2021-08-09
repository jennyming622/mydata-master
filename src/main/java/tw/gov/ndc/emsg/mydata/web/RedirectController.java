package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;

@Controller
@RequestMapping("/redirect")
public class RedirectController {
	private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);
	
	@GetMapping("/service")
	public void getSpSiteMap(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
		//returnUrl
		String returnUrl = request.getParameter("returnUrl")==null?"":request.getParameter("returnUrl").toString();
		System.out.println("==returnUrl==");
		System.out.println(returnUrl);
		String returnUrlBase = "";
		String returnUrlQuery = "";
		System.out.println("returnUrl="+returnUrl);
		if(returnUrl!=null&&returnUrl.trim().length()>0) {
			//returnUrl 剖析
			String[] urlArray = returnUrl.split("[?]");
			if(urlArray.length > 0){
				returnUrlBase = urlArray[0];
				if(urlArray.length > 1){
					returnUrlQuery =  urlArray[1];
				}
			}
		}
		System.out.println("==returnUrlBase==");
		System.out.println(returnUrlBase);
		System.out.println("==returnUrlQuery==");
		System.out.println(returnUrlQuery);
		/**
		 * returnUrlQuery處理
		 */
		String handleReturnUrlQuery = "";
		String permission_ticket = "";
		String[] parameterArray = returnUrlQuery.split("[&]");
		if(parameterArray!=null&&parameterArray.length>0) {
			for(String s:parameterArray) {
				if(s.startsWith("permission_ticket")) {
					permission_ticket = s.replace("permission_ticket=", "");
				}else {
					if(handleReturnUrlQuery.length()==0) {
						handleReturnUrlQuery = s;
					}else {
						handleReturnUrlQuery = handleReturnUrlQuery +"&"+s;
					}
				}
			}
		}
		System.out.println("==handleReturnUrlQuery==");
		System.out.println(handleReturnUrlQuery);
		System.out.println("==permission_ticket==");
		System.out.println(permission_ticket);
		//response.setHeader(name, value);
		String location = ValidatorHelper.removeSpecialCharacters(returnUrlBase);
		if(handleReturnUrlQuery.length()>0) {
			location = location +"?"+ValidatorHelper.removeSpecialCharacters(handleReturnUrlQuery);
		}
		response.setHeader("permission_ticket", ValidatorHelper.removeSpecialCharacters(permission_ticket));
		response.setStatus(HttpServletResponse.SC_FOUND);
		response.sendRedirect(location);
		
	}
	
	public String conbindTicketReturnUrl(String returnUrlBase,String returnUrlQuery,String permission_ticket) {
		String ret = "";
		if(returnUrlBase!=null&&returnUrlBase.trim().length()>0) {
			if(returnUrlQuery!=null&&returnUrlQuery.trim().length()>0) {
				ret = returnUrlBase +"?permission_ticket="+permission_ticket+"&"+returnUrlQuery;
			}else {
				ret = returnUrlBase +"?permission_ticket="+permission_ticket;
			}
		}
		return ret;
	}
}
