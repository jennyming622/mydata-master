/**
 * 
 */
package tw.gov.ndc.emsg.mydata.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Spring, Java based configuration. <br>
 * 對應原本 spring-core-config.xml 中的設定。
 * 
 * @author wesleyzhuang
 *
 */
@Configuration
@ComponentScan(basePackages = {"com.riease.common.sysinit",
							   "tw.gov.ndc.emsg.mydata"})
@PropertySource(
		value="classpath:/conf/common.properties",
		encoding="UTF-8"
)
@PropertySource(
		value= {"file:${app.dir.db-properties}/db.properties"},
		encoding="UTF-8"
)
public class AppConfig {
	
	/**
	 * 設定message resource bundle
	 * @return
	 */
	@Bean(name="messageSource")
    public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:/messages/syscode", 
        						   "classpath:/messages/message",
        						   "classpath:/messages/validation");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
                
        return messageSource;
    }
	
	
	
}
