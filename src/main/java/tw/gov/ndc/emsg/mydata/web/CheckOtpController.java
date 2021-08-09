package tw.gov.ndc.emsg.mydata.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class CheckOtpController {

    @GetMapping("/checkOtp")
    public String checkOtp(HttpServletRequest request, ModelMap model) {

        String contextPath = request.getContextPath();

        HttpSession session = request.getSession();
        String msuuidcheck = (String) session.getAttribute("msuuidcheck");
        Long msuuidcheckTime = (Long) session.getAttribute("msuuidcheckTime");

        if(StringUtils.isBlank(msuuidcheck)) {
            return "redirect:/";
        }

        model.addAttribute("msuuidcheck", msuuidcheck);
        model.addAttribute("msuuidcheckTime", msuuidcheckTime);
        return "otpCheck";
    }
}
