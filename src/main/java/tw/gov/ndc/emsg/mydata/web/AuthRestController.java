package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;

import tw.gov.ndc.emsg.mydata.util.LogoutUtil;

/**
 * 登入登出控制
 * @author Weder
 */
@Controller
@RequestMapping("/rest/auth")
public class AuthRestController extends BaseRestController {
	@Autowired
	private LogoutUtil logoutUtil;
	
	@GetMapping("/logout")
	public ResponseEntity<RestResponseBean> getLogout(HttpServletRequest request,
			HttpServletResponse response) throws SocketException, IOException, IllegalAccessException, InvocationTargetException {
		logoutUtil.doLogout(request);
		return responseOK();
	}
}
