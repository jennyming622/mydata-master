package tw.gov.ndc.emsg.mydata.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.LogMemberSession;
import tw.gov.ndc.emsg.mydata.mapper.LogMemberSessionMapper;
import tw.gov.ndc.emsg.mydata.type.MemberSessionState;
import tw.gov.ndc.emsg.mydata.util.LogoutUtil;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/logout")
public class LogoutController {
	private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

	@Autowired
	private LogMemberSessionMapper logMemberSessionMapper;

	@GetMapping
	public String getLogout(HttpServletRequest request,HttpServletResponse response,ModelMap model) {
		HttpSession session = request.getSession();

		List<LogMemberSession> list = logMemberSessionMapper.findWithSessionId(ValidatorHelper.removeSpecialCharacters(session.getId()));
		logger.info("LogoutUtil list > ", list.size());
		if(list != null && list.size() > 0) {
			LogMemberSession logMemberSession = list.get(0);
			LogMemberSession logMemberSession1 = new LogMemberSession();
			logMemberSession1.setId(ValidatorHelper.limitNumber(logMemberSession.getId()));
			logMemberSession1.setState(MemberSessionState.Destroy.name());
			logMemberSession1.setUpdateAt(new Date());
			logMemberSessionMapper.updateByPrimaryKeySelective(logMemberSession1);
		}
		return "redirect:/signout.do";
	}
}
