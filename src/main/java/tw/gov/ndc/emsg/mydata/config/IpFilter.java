package tw.gov.ndc.emsg.mydata.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.riease.common.helper.HttpHelper;
import com.riease.common.sysinit.security.jcaptcha.JcaptchaVerifyFilter;
import tw.gov.ndc.emsg.mydata.entity.WhiteIpList;
import tw.gov.ndc.emsg.mydata.entity.WhiteIpListExample;
import tw.gov.ndc.emsg.mydata.mapper.WhiteIpListMapper;
import tw.gov.ndc.emsg.mydata.web.ServiceRestController;

public class IpFilter extends OncePerRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(ServiceRestController.class);
    private WhiteIpListMapper whiteIpListMapper;
    public static List<String> authorizedIpAddresses = null;
    private boolean cronExpEnable;
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException, IOException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(cronExpEnable) {
            /**
             * /connect/introspect
             * /connect/userinfo
             * /mydatalog/v01/log
             */
        		//logger.debug("使用者IP:{}",HttpHelper.getRemoteIp(req));
            List<String> allowRequestUriList = new ArrayList<String>();
            allowRequestUriList.add("/connect/introspect");
            allowRequestUriList.add("/connect/userinfo");
            allowRequestUriList.add("/mydatalog/v01/log");
            allowRequestUriList.add("/rest/chatbot/webhook");
            String requestUri = req.getRequestURI();
            boolean allowUriCheck = false;
            if(allowRequestUriList!=null&&allowRequestUriList.size()>0) {
            		for(String aUri:allowRequestUriList) {
            			if(requestUri.contains(aUri)) {
            			    logger.info(aUri);
            				allowUriCheck = true;
            				break;
            			}
            		}
            }
            if(!allowUriCheck) {
            		if(authorizedIpAddresses==null) {
            			Map<String,Object> param = new HashMap<String,Object>();
            			List<WhiteIpList> whiteIpLists = whiteIpListMapper.selectByExample(param);
                    authorizedIpAddresses = whiteIpLists.stream().map(WhiteIpList::getIp).collect(Collectors.toList());
            		}
                if (authentication != null) {
                    if (!authorizedIpAddresses.isEmpty()) {
                        boolean authorized = false;
                        for (String ipAddress : authorizedIpAddresses) {
                            String[] ipAddressSplitList = ipAddress.split("[.]");
                            if (ipAddressSplitList != null && ipAddressSplitList.length == 3) {
                                if (HttpHelper.getRemoteIp(req).startsWith(ipAddress)) {
                                    authorized = true;
                                    break;
                                }
                            } else {
                                if (HttpHelper.getRemoteIp(req).equals(ipAddress)) {
                                    authorized = true;
                                    break;
                                }
                            }
                        }
                        if (!authorized) {
                        		logger.debug("非白名單IP:{}",HttpHelper.getRemoteIp(req));
                            throw new AccessDeniedException("Access has been denied for you IP address:" + HttpHelper.getRemoteIp(req));
                        }
                    }
                } else {
                		logger.debug("白名單列表為空值！");
                }        	
            }else {
            		logger.debug("=== 允許例外路徑 requestUri=={}",requestUri);
            }        	
        }
        chain.doFilter(req, resp);
    }
    
    public List<String> getAuthorizedIpAddresses() {
        return authorizedIpAddresses;
    }

    public void setAuthorizedIpAddresses(List<String> authorizedIpAddresses) {
        this.authorizedIpAddresses = authorizedIpAddresses;
    }

    public void setWhiteListMapper(WhiteIpListMapper whiteListMapper) {
        this.whiteIpListMapper = whiteListMapper;
    }

	public boolean isCronExpEnable() {
		return cronExpEnable;
	}
	
	public void setCronExpEnable(boolean cronExpEnable) {
		this.cronExpEnable = cronExpEnable;
	}
    
}
