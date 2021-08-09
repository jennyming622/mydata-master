package tw.gov.ndc.emsg.mydata.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;

import tw.gov.ndc.emsg.mydata.config.Breadcrumb;
import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.mapper.AuthTokenMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalLoginMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.model.WebServiceJobId;
import tw.gov.ndc.emsg.mydata.util.IdentifyUtil;
import tw.gov.ndc.emsg.mydata.util.MailUtil;

@Controller
@RequestMapping("/")
public class HomeController {
	private static Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private PortalLoginMapper portalLoginMapper;
	@Autowired
	private AuthTokenMapper tokenMapper;
	@Autowired
	private PortalResourceScopeMapper scopeMapper;
	
	//Mydata
	@Value("${gsp.oidc.client.id}")
	private String clientId;	
	@Value("${app.oidc.redirect.uri}")
	private String redirectUri;	
		
	@Value("${app.oidc.response.type}")
	private String responseType;
	@Value("${app.oidc.response.mode}")
	private String responseMode;	
	
	//資料資源檔根目錄
	@Value("${res.rootDir}")
	private String resourceRootDir;

	
	private String userHome = System.getProperty("user.home");
	private SimpleDateFormat df1 = new SimpleDateFormat("yyyy");
	private SimpleDateFormat df2 = new SimpleDateFormat("MM");
	
	@Value("${app.frontend.context.url}")
	private String frontendContextUrl;

	@Autowired
	private IdentifyUtil identifyUtil;
	
	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();	
	
	/**
	 * 進入首頁
	 * @param model
	 * @return
	 */
	@GetMapping
	public String homePage(
			ModelMap model,HttpServletRequest request) {
		logger.debug("進入首頁");

		return "index";
	}


	private void testMoiIdentify(){

		Map<String, Object> param = new HashMap<>();
		param.put("ck_personId", "H124074153");
//		param.put("ck_birthDate", "0781127");
//		param.put("ck_areaCode",new String[]{"68000"});
//		param.put("ck_idMark","3");
//		param.put("ck_idMarkDate","0990322");
		param.put("ck_householdId","Y2967863");
		try {
			String rs = identifyUtil.call(param, WebServiceJobId.IdMarkAndHouseHold);
			logger.debug("test Moi Identity result -> {}", rs );

			JSONObject response = new JSONObject(rs);
			JSONObject data =  response.getJSONObject("responseData");
			logger.debug("response str -> {}" ,response.toString());

//			Integer checkAll = Integer.valueOf(data.getString("checkAll"));
//			logger.debug("checkAll -> {}" ,checkAll);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根據條件陣列 取得scope
	 * 1. by name
	 * 2. by provider id
	 */
	private List<PortalResourceScope> selectScopesByExample(Object[] obs){
		Map<String,Object> map = new HashMap<>();
		StringBuilder sb = new StringBuilder();

		List<PortalResourceScope> rs = new ArrayList<>();
		boolean isByName = (obs instanceof String[]);
		if(isByName){
			sb.append("%(");
			for(int i = 0 ; i < obs.length ; i++){
				sb.append(obs[i]);
				// 最後一項不加'|'
				if(i < obs.length - 1){
					if(obs[i] instanceof String){
						// by name
						sb.append("|");
					}

				}
			}
			sb.append(")%");
			map.put("scopes",sb.toString());
			rs = scopeMapper.selectByExampleLike(map);
		}else {
			for (Object ob : obs) {
				map.put("prId", ob);
				rs.addAll(scopeMapper.selectByProviderId(map));
				map.clear();
			}
		}
		return rs;
	}




	/**
	 * 產生所有scope
	 */
	private void creatAllScopeToken(){
		logger.debug("creat all scope token");

		// 取得scope
		Map<String,Object> map = new HashMap<>();
		List<PortalResourceScope> scopes = scopeMapper.selectByExample(map);
		StringBuilder sb = new StringBuilder();
		for (PortalResourceScope s : scopes){
			sb.append(s.getScope()).append(" ");
		}
		logger.debug("all scope str -> {}" ,sb.toString());

		// 取得token
		map.clear();
		map.put("token","rsdoqt1awt77d53x1h11qqdr3l9ghezlrh6fp9orvvmqb5dx1aeacics90cnbszj");
		AuthToken token = tokenMapper.selectByExample(map).stream().findFirst().orElse(null);
		if(token == null) {
			logger.debug("token null");
			return;
		}
		logger.debug("token scope -> " + token.getScope());


	}
	
	@GetMapping("/login_success")
	public String loginSuccess(
			ModelMap model,HttpServletRequest request) {
		logger.debug("登入成功");
		HttpSession session = request.getSession();
		if(session!=null&&session.getAttribute("loginmessage")!=null&&session.getAttribute("loginmessage").toString().length()>0) {
			model.addAttribute("loginmessage", session.getAttribute("loginmessage").toString());
			session.removeAttribute("loginmessage");
		}else {
			model.addAttribute("loginmessage", "登入成功！");
		}
		return "login_success";
	}
	
	@GetMapping("/login_fail")
	public String loginFail(
			ModelMap model,HttpServletRequest request) {
		logger.debug("登入失敗");
		HttpSession session = request.getSession();
		if(session!=null&&session.getAttribute("loginmessage")!=null&&session.getAttribute("loginmessage").toString().length()>0) {
			model.addAttribute("loginmessage", session.getAttribute("loginmessage").toString());
			session.removeAttribute("loginmessage");
		}else {
			model.addAttribute("loginmessage", "登入失敗！");
		}
		return "login_fail";
	}	
	
	@GetMapping("/logout_success")
	public String logoutSuccess(
			ModelMap model,HttpServletRequest request) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		System.out.println("------ /logout_success ------");
        HttpSession session = request.getSession();
        SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
        if(sr!=null) {
    	        Map<String,Object> param = new HashMap<String,Object>();
    	        param.put("egovAcc", SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
            List<PortalLogin> portalLoginList = portalLoginMapper.selectByExample(param);	
            if(portalLoginList!=null&&portalLoginList.size()>0) {
            		PortalLogin record = portalLoginList.get(0);
            		portalLoginMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(record.getId()));
            }
        }
        session.invalidate();
		model.addAttribute("loginmessage", "登出成功！");
		return "login_success";
	}	
	
	@GetMapping("/error")
	public String errorPageHandle(
			ModelMap model,HttpServletRequest request) {
		HttpSession session = request.getSession();
		session = changeSessionId(request);
		return "error";
	}
	
	@GetMapping("/login-again")
	public String errorLoginPageHandle(
			ModelMap model,HttpServletRequest request) {
		HttpSession session = request.getSession();
		session = changeSessionId(request);
		return "login-again";
	}	
		
    private HttpSession changeSessionId(HttpServletRequest request) {
        HttpSession oldSession = request.getSession();
        
        Map<String, Object> attrs = new HashMap<>();
        for(String name : Collections.list(oldSession.getAttributeNames())) {
            attrs.put(name, oldSession.getAttribute(name));
            oldSession.removeAttribute(name);
        }
        oldSession.invalidate(); // 令目前 Session 失效

        HttpSession newSession = request.getSession(); 
        for(String name : attrs.keySet()) {
            newSession.setAttribute(name, attrs.get(name));
        }
        
        return newSession;
    }		
}
