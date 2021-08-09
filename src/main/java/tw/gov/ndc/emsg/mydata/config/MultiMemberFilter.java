package tw.gov.ndc.emsg.mydata.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import tw.gov.ndc.emsg.mydata.entity.LogMemberSession;
import tw.gov.ndc.emsg.mydata.mapper.LogMemberSessionMapper;
import tw.gov.ndc.emsg.mydata.type.MemberSessionState;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class MultiMemberFilter extends OncePerRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(MultiMemberFilter.class);

    private LogMemberSessionMapper logMemberSessionMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException, IOException {


        HttpSession session = req.getSession();

        String requestUri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String sessionId = session.getId();
        if(StringUtils.equals(contextPath, "/")) contextPath = "";

        logger.debug("MultiMemberFilter {}", sessionId);

        List<LogMemberSession> list = logMemberSessionMapper.findWithSessionId(sessionId);

        String msuuidcheck = (String) session.getAttribute("msuuidcheck");

        logger.debug("MultiMemberFilter requestUri {}, contextPath {}", requestUri,
                contextPath);

        if((list != null && list.size() > 0) && StringUtils.isNotBlank(msuuidcheck) &&
                (StringUtils.indexOfAny(requestUri, "/checkOtp",
                        "/rest",
                        "/resources",
                        "RiAPI.js",
                        "/logout",
                        "/captcha.jpg") == -1)) {
            resp.sendRedirect(contextPath + "/checkOtp");
        } else {
            chain.doFilter(req, resp);
        }
    }

    public LogMemberSessionMapper getLogMemberSessionMapper() {
        return logMemberSessionMapper;
    }

    public void setLogMemberSessionMapper(LogMemberSessionMapper logMemberSessionMapper) {
        this.logMemberSessionMapper = logMemberSessionMapper;
    }
}
