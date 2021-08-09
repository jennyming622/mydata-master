package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.riease.common.helper.HttpHelper;
import com.riease.common.sysinit.SessionRecord;

import tw.gov.ndc.emsg.mydata.config.MySessionContext;
import tw.gov.ndc.emsg.mydata.entity.AuthToken;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxExt;
import tw.gov.ndc.emsg.mydata.entity.PortalLogin;
import tw.gov.ndc.emsg.mydata.entity.PortalLoginExample;
import tw.gov.ndc.emsg.mydata.entity.Verify;
import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;
import tw.gov.ndc.emsg.mydata.mapper.MemberMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBoxMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalLoginMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.UlogMapper;
import tw.gov.ndc.emsg.mydata.mapper.VerifyMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.util.LoginUtil;
import tw.gov.ndc.emsg.mydata.util.TokenUtils;

@Controller
@RequestMapping("/authcode")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	 
	@Value("${gsp.oidc.client.id}")
	private String clientId;	
	@Value("${app.oidc.redirect.uri}")
	private String redirectUri;
	@Value("${gsp.oidc.client.secret}")
	private String clientSecret;
	@Value("${app.oidc.logout.redirect.uri}")
	private String logoutRedirectUri;
	@Value("${app.frontend.context.url}")
	private String frontendContextUrl;

	@Autowired
	private VerifyMapper verifyMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired 
	private LoginUtil loginUtil;
	
	/**
	 * 登入管理用
	 */
    private static HashMap loginmap = new HashMap();
	
	/**
	 * 健保卡驗證回傳
	 * @param result
	 * @param request
	 * @return
	 * @throws java.text.ParseException 
	 * @throws UnsupportedEncodingException 
	 */
	@GetMapping("/NHICardVerify")
	public String getNHICard(
			@RequestParam(name="result",required=false) String result,
			HttpServletRequest request) throws UnsupportedEncodingException, java.text.ParseException {
		System.out.println("-------- NHICardVerify start----------------");
		System.out.println("result:"+result);
		HttpSession session = request.getSession();
		if(result!=null&&result.length()>0) {
			String[] resultList = result.split("\\.");
			if(resultList!=null&&resultList.length==3) {
				String payloadStr = resultList[1];
				System.out.println("payloadStr:"+result);
				JSONObject payloadObj = null;
				try {
					payloadObj = (JSONObject) JSONValue.parseWithException(new String(Base64.getUrlDecoder().decode(payloadStr),"UTF-8"));
				} catch (ParseException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(payloadObj!=null) {
					String uuid = payloadObj.get("uuid").toString();
					String statusStr = payloadObj.get("result").toString();
					System.out.println("uuid:"+uuid);
					System.out.println("statusStr:"+statusStr);
					if(statusStr.startsWith("A0000")) {
						System.out.println("---- 健保卡驗證回傳：Success !------");
						/**
						 * UUID驗證參數，暫定為一小時有效
						 */
						Calendar cal = GregorianCalendar.getInstance();
						cal.setTime(new Date());
						Date endTime = cal.getTime();
						// 起始時間定為1小時前
						cal.add(Calendar.HOUR_OF_DAY, -1);
						Date startTime = cal.getTime();
						Map<String,Object> verifyparam = new HashMap<String,Object>();
						verifyparam.put("sCtime", startTime);
						verifyparam.put("key", uuid);
						List<Verify> verifyList= verifyMapper.selectByExample(verifyparam);
						if(verifyList!=null&&verifyList.size()>0) {
							//session = changeSessionId(request);
							session.setAttribute("loginmessage","驗證成功！");
							Verify verify = verifyList.get(0);
							/**
							 * Login
							 */
							loginUtil.doNewAutoLogin(verify,request,uuid);
							return "redirect:/login_success";
						}else {
							session.setAttribute("loginmessage","驗證失敗！");
							return "redirect:/login_fail";
						}
					}else {
						session.setAttribute("loginmessage","驗證失敗！");
						return "redirect:/login_fail";
					}
				}
			}else {
				session.setAttribute("loginmessage","驗證失敗！");
				return "redirect:/login_fail";
			}
		}else {
			session.setAttribute("loginmessage","驗證失敗！");
			return "redirect:/login_fail";
		}
		session.setAttribute("loginmessage","驗證失敗！");
		return "redirect:/login_fail";
	}
	
	/**
	 * 自然人憑證驗證回傳
	 * @param result
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws java.text.ParseException 
	 */
	@GetMapping("/CertVerify")
	public String getCert(
			@RequestParam(name="result",required=false) String result,
			HttpServletRequest request) throws UnsupportedEncodingException, java.text.ParseException {
		System.out.println("-------- CertVerify start----------------");
		System.out.println("result:"+result);
		HttpSession session = request.getSession();
		if(result!=null&&result.length()>0) {
			String[] resultList = result.split("\\.");
			if(resultList!=null&&resultList.length==3) {
				String payloadStr = resultList[1];
				System.out.println("payloadStr:"+payloadStr);
				JSONObject payloadObj = null;
				try {
					payloadObj = (JSONObject) JSONValue.parseWithException(new String(Base64.getUrlDecoder().decode(payloadStr),"UTF-8"));
				} catch (ParseException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(payloadObj!=null) {
					String uuid = payloadObj.get("uuid").toString();
					String statusStr = payloadObj.get("result").toString();
					System.out.println("uuid:"+uuid);
					System.out.println("statusStr:"+statusStr);
					if(statusStr.startsWith("A0000")) {
						System.out.println("---- 自然人憑證驗證回傳：Success !------");
						/**
						 * UUID驗證參數，暫定為一小時有效
						 */
						Calendar cal = GregorianCalendar.getInstance();
						cal.setTime(new Date());
						Date endTime = cal.getTime();
						// 起始時間定為1小時前
						cal.add(Calendar.HOUR_OF_DAY, -1);
						Date startTime = cal.getTime();
						Map<String,Object> verifyparam = new HashMap<String,Object>();
						verifyparam.put("sCtime", startTime);
						verifyparam.put("key", uuid);
						List<Verify> verifyList= verifyMapper.selectByExample(verifyparam);
						if(verifyList!=null&&verifyList.size()>0) {
							//session = changeSessionId(request);
							session.setAttribute("loginmessage","驗證成功！");
							Verify verify = verifyList.get(0);
							/**
							 * Login
							 */
							loginUtil.doNewAutoLogin(verify,request, uuid);
							
							return "redirect:/login_success";
						}else {
							session.setAttribute("loginmessage","驗證失敗！");
							return "redirect:/login_fail";
						}
					}else {
						session.setAttribute("loginmessage","驗證失敗！");
						return "redirect:/login_fail";
					}
				}
			}else {
				session.setAttribute("loginmessage","驗證失敗！");
				return "redirect:/login_fail";
			}
		}else {
			session.setAttribute("loginmessage","驗證失敗！");
			return "redirect:/login_fail";
		}
		session.setAttribute("loginmessage","驗證失敗！");
		return "redirect:/login_fail";
	}	
	
	/**
	 * 工商憑證驗證回傳
	 * @param result
	 * @param request
	 * @return
	 * @throws java.text.ParseException 
	 * @throws UnsupportedEncodingException 
	 */
	@GetMapping("/MOECAVerify")
	public String getMOECA(
			@RequestParam(name="result",required=false) String result,
			HttpServletRequest request) throws UnsupportedEncodingException, java.text.ParseException {
		System.out.println("-------- CertVerify start----------------");
		System.out.println("result:"+result);
		HttpSession session = request.getSession();
		if(result!=null&&result.length()>0) {
			String[] resultList = result.split("\\.");
			if(resultList!=null&&resultList.length==3) {
				String payloadStr = resultList[1];
				System.out.println("payloadStr:"+payloadStr);
				JSONObject payloadObj = null;
				try {
					payloadObj = (JSONObject) JSONValue.parseWithException(new String(Base64.getUrlDecoder().decode(payloadStr),"UTF-8"));
				} catch (ParseException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(payloadObj!=null) {
					String uuid = payloadObj.get("uuid").toString();
					String statusStr = payloadObj.get("result").toString();
					System.out.println("uuid:"+uuid);
					System.out.println("statusStr:"+statusStr);
					if(statusStr.startsWith("A0000")) {
						System.out.println("---- 工商憑證驗證回傳：Success !------");
						/**
						 * UUID驗證參數，暫定為一小時有效
						 */
						Calendar cal = GregorianCalendar.getInstance();
						cal.setTime(new Date());
						Date endTime = cal.getTime();
						// 起始時間定為1小時前
						cal.add(Calendar.HOUR_OF_DAY, -1);
						Date startTime = cal.getTime();
						Map<String,Object> verifyparam = new HashMap<String,Object>();
						verifyparam.put("sCtime", startTime);
						verifyparam.put("key", uuid);
						List<Verify> verifyList= verifyMapper.selectByExample(verifyparam);
						if(verifyList!=null&&verifyList.size()>0) {
							//session = changeSessionId(request);
							session.setAttribute("loginmessage","驗證成功！");
							Verify verify = verifyList.get(0);
							/**
							 * Login
							 */
							loginUtil.doNewAutoLogin(verify,request, uuid);
							return "redirect:/login_success";
						}else {
							session.setAttribute("loginmessage","驗證失敗！");
							return "redirect:/login_fail";
						}
					}else {
						session.setAttribute("loginmessage","驗證失敗！");
						return "redirect:/login_fail";
					}
				}
			}else {
				session.setAttribute("loginmessage","驗證失敗！");
				return "redirect:/login_fail";
			}
		}else {
			session.setAttribute("loginmessage","驗證失敗！");
			return "redirect:/login_fail";
		}
		session.setAttribute("loginmessage","驗證失敗！");
		return "redirect:/login_fail";
	}		
	
	
	/**
	 * 雙證件驗證回傳
	 * @param result
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 * @throws java.text.ParseException 
	 */
	@GetMapping("/PIIVerify")
	public String getPIIVerify(
			@RequestParam(name="result",required=false) String result,
			HttpServletRequest request) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, java.text.ParseException {
		System.out.println("-------- PIIVerify start----------------");
		System.out.println("result:"+result);
		HttpSession session = request.getSession();
		if(result!=null&&result.length()>0) {
			String[] resultList = result.split("\\.");
			if(resultList!=null&&resultList.length==3) {
				String payloadStr = resultList[1];
				System.out.println("payloadStr:"+result);
				JSONObject payloadObj = null;
				try {
					payloadObj = (JSONObject) JSONValue.parseWithException(new String(Base64.getUrlDecoder().decode(payloadStr),"UTF-8"));
				} catch (ParseException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(session.getAttribute("uuidcheck")!=null && payloadObj!=null) {
					String statusStr = payloadObj.get("result").toString();
					System.out.println("statusStr:"+statusStr);
					if(statusStr.startsWith("success")) {
						/*System.out.println("==session.getAttribute(uuidcheck)==:"+session.getAttribute("uuidcheck").toString());
						Verify record = new Verify();
						record.setKey(session.getAttribute("uuidcheck").toString());
						record.setCtime(new Date());
						verifyMapper.insertSelective(record);
						session.setAttribute("loginmessage","驗證成功！");*/
						System.out.println("---- 雙證件驗證回傳：Success !------");
						/**
						 * UUID驗證參數，暫定為一小時有效
						 */
						Calendar cal = GregorianCalendar.getInstance();
						cal.setTime(new Date());
						Date endTime = cal.getTime();
						// 起始時間定為1小時前
						cal.add(Calendar.HOUR_OF_DAY, -1);
						Date startTime = cal.getTime();
						Map<String,Object> verifyparam = new HashMap<String,Object>();
						verifyparam.put("sCtime", startTime);
						verifyparam.put("key", session.getAttribute("uuidcheck").toString());
						List<Verify> verifyList= verifyMapper.selectByExample(verifyparam);
						if(verifyList!=null&&verifyList.size()>0) {
							//session = changeSessionId(request);
							session.setAttribute("loginmessage","驗證成功！");
							Verify verify = verifyList.get(0);
							/**
							 * Login
							 */
							loginUtil.doNewAutoLogin(verify,request, session.getAttribute("uuidcheck").toString());
							return "redirect:/login_success";
						}else {
							session.setAttribute("loginmessage","驗證失敗！");
							return "redirect:/login_fail";
						}
					}else {
						session.setAttribute("loginmessage","驗證失敗！");
						return "redirect:/login_fail";
					}
				}
			}else {
				session.setAttribute("loginmessage","驗證失敗！");
				return "redirect:/login_fail";
			}
		}else {
			session.setAttribute("loginmessage","驗證失敗！");
			return "redirect:/login_fail";
		}
		session.setAttribute("loginmessage","驗證失敗！");
		return "redirect:/login_fail";
	}
	
	public static synchronized void delLoginflag(String userid) {
		HttpSession session = (HttpSession) loginmap.get(userid);
		System.out.print(session);
		System.out.print(session.getId());
		if (session != null) {
            //釋放session對象，把該鍵值對從map中移除
			System.out.println("==釋放session對象==");
			try {
				session.invalidate();
			}catch(Exception ex) {
				System.out.println("=delLoginflag="+ex);
			}
			loginmap.remove(userid);
		}
	}

	public static synchronized HttpSession getLoginflag(String userid) {
		if (userid == null) {
			return null;
		}
		return (HttpSession) loginmap.get(userid);
	}	
	
	public String maskName(String userName) {
		String ret = "";
		if(userName!=null&&userName.length()>=3) {
			ret = userName.substring(0, 1);
			for(int i=1;i<userName.length()-1;i++) {
				ret = ret +"*";
			}
			ret = ret + userName.substring(userName.length()-1);
		}else if(userName!=null&&userName.length()==2) {
			ret = userName.substring(1, 2);
		}else if(userName!=null&&userName.length()==1) {
			ret = "*";
		}
		return ret;
	}
}
