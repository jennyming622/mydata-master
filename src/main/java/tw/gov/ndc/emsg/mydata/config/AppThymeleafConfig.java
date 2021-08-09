/**
 * 
 */
package tw.gov.ndc.emsg.mydata.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;

/**
 * 在spring上配置Thymeleaf
 * @author wesleyzhuang
 *
 */
@Configuration
public class AppThymeleafConfig implements ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(AppThymeleafConfig.class);
	
	protected ApplicationContext applicationContext;
	
	@Value("${thymeleaf.resolver.cache.enable}")
	private boolean resolverCacheable;
	
	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
	    resolver.setTemplateEngine(templateEngine());
	    resolver.setCharacterEncoding("UTF-8");
	    resolver.setContentType("text/html; charset=UTF-8");
	    return resolver;
	}

	@Bean
	public TemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
	    engine.setEnableSpringELCompiler(true);
	    
	    //email template resolver
//	    engine.addTemplateResolver(htmlEmailTemplateResolver());
	    //view template resolver
	    engine.addTemplateResolver(viewTemplateResolver());
	    	    
	    //for message-source
	    MessageSource messageSource = (MessageSource)applicationContext.getBean("messageSource");
	    engine.setMessageSource(messageSource);
	    //for spring-security
	    engine.addDialect(new SpringSecurityDialect());
	    //for bootstraps data-attributes
	    engine.addDialect(new DataAttributeDialect());
	    // add custom dialect here ...
	    // engine.addDialect(new MyCustomDialect());
	    return engine;
	}
	
	private ITemplateResolver viewTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
	    resolver.setApplicationContext(applicationContext);
	    resolver.setOrder(2);
	    resolver.setPrefix("/WEB-INF/views/");
	    resolver.setSuffix(".html");
	    resolver.setTemplateMode(TemplateMode.HTML);
	    resolver.setCharacterEncoding("UTF-8");
	    resolver.setCacheable(resolverCacheable);
	    return resolver;
	}
	
//	private ITemplateResolver htmlEmailTemplateResolver() {
//        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//        templateResolver.setOrder(1);
//        templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
//        templateResolver.setPrefix("/mail/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//        templateResolver.setCharacterEncoding("UTF-8");
//        templateResolver.setCacheable(false);
//        return templateResolver;
//    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
