/**
 * 
 */
package tw.gov.ndc.emsg.mydata.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * WebApplication初始動作，整合spring-security。
 * @author wesleyzhuang
 *
 */
public class MyWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    
	public MyWebApplicationInitializer() {
		//宣告初始設定檔
		super(AppConfig.class);
	}
	
	/**
	 * 實作初始spring-security filter chain之前的動作。
	 * @see org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer#beforeSpringSecurityFilterChain(javax.servlet.ServletContext)
	 */
	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		super.beforeSpringSecurityFilterChain(servletContext);
		System.out.println("-------MyWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer --------- beforeSpringSecurityFilterChain ---------字元編碼轉換before----");
		//字元編碼轉換
		final CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        servletContext.addFilter("characterEncodingFilter", encodingFilter)
        				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
		
        //瀏覽器form表單只支持GET與POST請求，而DELETE、PUT等method並不支持，spring3.0添加了一個過濾器，可以將這些請求轉換為嫑準的HTTP方法，使得支持GET、POST、PUT與DELETE請求，該過濾器為HiddenHttpMethodFilter。
		final HiddenHttpMethodFilter methodFilter = new HiddenHttpMethodFilter();
        servletContext.addFilter("hiddenHttpMethodFilter", methodFilter)
						.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/rest/*");
		
		
	}

	/**
	 * 實作初始spring-security filter chain之後的動作。
	 * @see org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer#afterSpringSecurityFilterChain(javax.servlet.ServletContext)
	 */
	@Override
	protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
		super.afterSpringSecurityFilterChain(servletContext);
		/*
		 * SpringMVC DispatchServlet 
		 */
		AnnotationConfigWebApplicationContext dispatcherContext =  new AnnotationConfigWebApplicationContext();
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("springmvc-dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		
	    /*
	     * 此處的url-pattern必需使用格式如 / 或 /xxx/* 。
	     * 若使用後者，請求的完整網址必需是此處宣告的url-pattern + RequestMapping中定義的path，才會成功對應到。
	     */
	    dispatcher.addMapping("/");
	}
	
}
