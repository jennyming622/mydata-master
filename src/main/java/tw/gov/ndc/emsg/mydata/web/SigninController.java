/**
 * 
 */
package tw.gov.ndc.emsg.mydata.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 後台登入頁 - mvc controller
 * @author wesleyzhuang
 *
 */
@Controller
@RequestMapping("/")
public class SigninController {
	private static final Logger logger = LoggerFactory.getLogger(SigninController.class);
	
	@GetMapping("/signin")
	public String getSignin(
			@RequestParam(value="toPage", required = false) String toPage,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		String param = "";
		if(StringUtils.isNotBlank(toPage)) {
			if(toPage.startsWith("verification")) {
				String boxId = toPage.replace("verification", "");
				if(sr != null) {
					if(boxId.equals("")) {
						boxId = "1";
						return "redirect:/sp/member/"+boxId;
					}else {
						return "redirect:/sp/member/"+ValidatorHelper.removeSpecialLimitNumber(boxId);
					}
				}
				param = "verification" + ValidatorHelper.removeSpecialLimitNumber(boxId);
			} else if(toPage.startsWith("counter-agent")){
				if(sr != null) {
					return "redirect:/sp/service/counter/agent";
				}
				param = "counter-agent";
			}
		} else if(sr != null){
			return "redirect:/sp/member";
		}
		model.addAttribute("toPage", param);
		return "login-v2";
	}
	
	/**
	 * 登入方式
	 * 1. cer
	 * 2. nhi
	 * 3. tfido
	 * 4. pii
	 * 5. email
	 * 6. otp
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/signin2")
	public String getSignin2(
			@RequestParam(value="toPage", required = false) String toPage,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		String level = request.getParameter("level");
		if(level.equalsIgnoreCase("cer")) {
			level = "cer";
		}else if(level.equalsIgnoreCase("fic")) {
			level = "fic";
		}else if(level.equalsIgnoreCase("fch")) {
			level = "fch";
		}else if(level.equalsIgnoreCase("business")) {
			level = "business";
		}else if(level.equalsIgnoreCase("nhi")) {
			level = "nhi";
		}else if(level.equalsIgnoreCase("fcs")) {
			level = "fcs";
		}else if(level.equalsIgnoreCase("tfido")) {
			level = "tfido";
		}else if(level.equalsIgnoreCase("pii")) {
			level = "pii";
		}else if(level.equalsIgnoreCase("email")) {
			level = "email";
		}else if(level.equalsIgnoreCase("mobile")) {
			level = "mobile";
		}else {
			level = "cer";
		}
		model.addAttribute("level", level);
		String param = "";
		String boxId = "";
		if(toPage!=null&&toPage.startsWith("verification")) {
			boxId = toPage.replace("verification", "");
			param = "verification" + ValidatorHelper.removeSpecialLimitNumber(boxId);
		}
		model.addAttribute("toPage", param);
		return "login2";
	}
}
