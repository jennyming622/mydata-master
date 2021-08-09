package tw.gov.ndc.emsg.mydata.util;

import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LogoutUtil {
	private static final Logger logger = LoggerFactory.getLogger(LogoutUtil.class);

	
	public void doLogout(HttpServletRequest request) {
		System.out.println("==doLogout start==");
		HttpSession session = request.getSession();
        for(String name : Collections.list(session.getAttributeNames())) {
            session.removeAttribute(name);
        }
        //SecurityContextHolder.getContext().setAuthentication(null);
        try {
			request.logout();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
}
