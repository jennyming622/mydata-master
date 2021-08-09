package tw.gov.ndc.emsg.mydata.web;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.bean.PagingInfo;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;

import tw.gov.ndc.emsg.mydata.entity.ClickLog;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;
import tw.gov.ndc.emsg.mydata.mapper.ClickLogMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;

@Controller
@RequestMapping("/rest/log")
public class LogRestController extends BaseRestController {
	
	private static Logger logger = LoggerFactory.getLogger(LogRestController.class);
	
	@Autowired
	private ClickLogMapper clickLogMapper;
	
	@PostMapping("/click")
	public ResponseEntity<RestResponseBean> changeStatus(
			@RequestBody Map<String,Object> params,
			BindingResult result,
			HttpServletRequest request,
			PagingInfo paging) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String uid = auth.getName();
		ClickLog clog = new ClickLog();
		clog.setAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
		if(params.get("cntType")!=null) {
			clog.setCntType(params.get("cntType").toString());
		}
		if(params.get("cntId")!=null) {
			clog.setCntId(Integer.valueOf(params.get("cntId").toString()));
		}
		if(uid!=null) {
			clog.setUid(uid);
		}
		clog.setCtime(new Date());
		clickLogMapper.insertSelective(clog);
		return responseOK();
	}
}
