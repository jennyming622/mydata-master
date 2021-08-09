package tw.gov.ndc.emsg.mydata.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class InputMaskController {
    @GetMapping("/inputMask")
    public String inputMask(HttpServletRequest request, ModelMap model) {

        String contextPath = request.getContextPath();

        HttpSession session = request.getSession();

        return "inputMask";
    }
}
