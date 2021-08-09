package tw.gov.ndc.emsg.mydata.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import com.riease.common.helper.HttpHelper;

import tw.gov.ndc.emsg.mydata.type.SystemOptionType;
import tw.gov.ndc.emsg.mydata.util.SystemOptionUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SystemOptionFilter extends OncePerRequestFilter {

    private static Logger logger = LoggerFactory.getLogger(SystemOptionFilter.class);
    private SystemOptionUtil systemOptionUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = HttpHelper.getRemoteIp(request);

        // 全域設定
        logger.debug("[SystemOptionFilter] >> ip {}", ip);
        Integer chatBotVer = systemOptionUtil.use(SystemOptionType.FEATURE_CHATBOT.name(), ip);
        request.setAttribute("chatbotVer", chatBotVer);
        logger.debug("[SystemOptionFilter] >> chatBotVer {}", chatBotVer);

        Integer analysisVer = systemOptionUtil.use(SystemOptionType.FEATURE_ANALYSIS.name(), ip);
        request.setAttribute("analysisVer", analysisVer);
        logger.debug("[SystemOptionFilter] >> analysisVer {}", analysisVer);

        Integer twcaVerifyVer = systemOptionUtil.use(SystemOptionType.FEATURE_TWCAVERIFY.name(), ip);
        request.setAttribute("twcaVerifyVer", twcaVerifyVer);
        logger.debug("[SystemOptionFilter] >> twcaVerifyVer {}", twcaVerifyVer);
        
        Integer counterAgentVer = systemOptionUtil.use(SystemOptionType.FEATURE_COUNTERAGENT.name(), ip);
        request.setAttribute("counterAgentVer", counterAgentVer);
        logger.debug("[SystemOptionFilter] >> counterAgentVer {}", counterAgentVer);        
        filterChain.doFilter(request, response);
    }

    public SystemOptionUtil getSystemOptionUtil() {
        return systemOptionUtil;
    }

    public void setSystemOptionUtil(SystemOptionUtil systemOptionUtil) {
        this.systemOptionUtil = systemOptionUtil;
    }
}
