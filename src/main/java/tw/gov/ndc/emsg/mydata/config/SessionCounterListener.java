package tw.gov.ndc.emsg.mydata.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


public class SessionCounterListener implements HttpSessionListener {

	private static Logger logger = LoggerFactory.getLogger(SessionCounterListener.class);

	private MySessionContext myc = MySessionContext.getInstance();
	private static int totalActiveSessions;
	public static int getTotalActiveSession(){
	        return totalActiveSessions;
	}

	/*@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		totalActiveSessions++;
		System.out.println("sessionCreated - add one session into counter");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		totalActiveSessions--;
		System.out.println("sessionDestroyed - deduct one session from counter");
	}*/
	
	@Override 
	public void sessionCreated(final HttpSessionEvent se) {
		logger.info("sessionCreated");
		totalActiveSessions++;
	    HttpSession session = se.getSession();
	    //se.getSession().setMaxInactiveInterval(20*60);
	    se.getSession().setMaxInactiveInterval(10);
	    myc.addSession(session);
	} 

	@Override 
	public void sessionDestroyed(final HttpSessionEvent se) {
		totalActiveSessions--;
	    HttpSession session = se.getSession(); 
	    myc.delSession(session);
	} 
	
}
