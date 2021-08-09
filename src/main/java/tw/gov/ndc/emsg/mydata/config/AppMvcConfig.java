/**
 * 
 */
package tw.gov.ndc.emsg.mydata.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring, Java based configuration. <br>
 * 對應原本 spring-mvc-config.xml 中的設定。
 * 
 * @author wesleyzhuang
 *
 */
//@EnableWebMvc	//@EnableWebMvc 相當於xml設定檔中的 <mvc:annotation-driven />
@Configuration
public class AppMvcConfig extends WebMvcConfigurationSupport {

	private static Logger logger = LoggerFactory.getLogger(AppMvcConfig.class);
	
	//cache 靜態資源的秒數。
	@Value("${spring.handler.cache.web-static-resources.period:0}")
	private int staticResourcesPeriod;
	
	//cache JavaScript資源的秒數。
	@Value("${spring.handler.cache.web-js-resources.period:0}")
	private int jsResourcesPeriod;
	
	//cache 資料資源檔的秒數。
	@Value("${spring.handler.cache.web-data-resources.period:0}")
	private int dataResourcesPeriod;
	
	//資料資源檔根目錄
	@Value("${res.rootDir}")
	private String resourceRootDir;

//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        AntPathMatcher matcher = new AntPathMatcher();
//        matcher.setCaseSensitive(false);
//        configurer.setPathMatcher(matcher);
//    }
	
	/**
	 * 相當於xml設定檔中的 <mvc:default-servlet-handler />
	 */
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
	
	/**
	 * 相當於xml設定檔中的 <mvc:resources mapping="" location="" />
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//靜態資源
        registry.addResourceHandler("/resources/**")
        		.addResourceLocations("/resources/")
        		.setCachePeriod(staticResourcesPeriod);
		super.addResourceHandlers(registry);
    }
	
	/**
	 * 相當於xml設定檔中的 <mvc:view-controller path="" view-name=""/>
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		/*
		 * 以下註冊靜態頁網址
		 */
		//測試用
		//registry.addViewController("/test/form2js").setViewName("/test/form2js_test");

	}
	
	/**
	 * 指定上傳圖檔大小的最大限制
	 * @return
	 */
	@Bean
    public MultipartResolver multipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        // 10MBytes -> 10485760 , 5MBytes -> 5242880
        multipartResolver.setMaxUploadSize(10485760);
        return multipartResolver;
    }

	/**
	 * 指定將用於bean validation的resource bundle。 
	 * @return
	 */
	@Bean(name="validationMessageSource")
    public MessageSource validationMessageSource() { 
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:/messages/validation");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
                
        return messageSource;
    }
	
	/**
	 * 指定messageSource給LocalValidatorFactoryBean
	 * @return
	 */
	@Bean(name="validator")
	public LocalValidatorFactoryBean validator() {
	    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
	    bean.setValidationMessageSource(validationMessageSource());
	    return bean;
	}
	
	/**
	 * 替換預設的Validator為經指定messageSource的LocalValidatorFactoryBean
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#getValidator()
	 */
	@Override
	public Validator getValidator() {
		return validator();
	}	

	/*@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("https://mydata.nat.gov.tw")
			//.allowedOrigins("http://localhost:8080/mydata")
			.allowedMethods("OPTIONS","HEAD", "GET","PUT","POST","DELETE","PATCH")
			//.allowedHeaders("*")
			.allowCredentials(true).maxAge(3600);
	}*/	
}
