/**
 * 
 */
package tw.gov.ndc.emsg.mydata.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlets.DoSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.openid.OpenIDAuthenticationProvider;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.google.common.collect.ImmutableList;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.security.jcaptcha.JcaptchaVerifyFilter;
import tw.gov.ndc.emsg.mydata.mapper.LogMemberSessionMapper;
import tw.gov.ndc.emsg.mydata.mapper.WhiteIpListMapper;
import tw.gov.ndc.emsg.mydata.util.SystemOptionUtil;

/**
 * 設置多個SecurityConfig
 * @author wesleyzhuang
 *
 */
@Configuration
@EnableWebSecurity
public class AppHttpSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(AppHttpSecurityConfig.class);
	
	/*@Autowired
	private JcaptchaVerifyFilter captchaVerifyFilter;*/
	/*@Autowired
	private IpFilter ipFilter;*/	
	
	@Value("${app.oidc.authorize.uri}")
	private String gspOpenIdAuthorizeUri;
	
	@Autowired
  	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private WhiteIpListMapper whiteIpListMapper;
	@Autowired
	private SystemOptionUtil systemOptionUtil;
	@Autowired
    private LogMemberSessionMapper logMemberSessionMapper;
	
	@Value("${app.frontend.context.url}")
	private String origin;
	
	private static WebSecurityCorsFilter webSecurityCorsFilter;
	private static DoSFilter doSFilter;

	@Value("${CronExp_Enable}")
	private boolean cronExpEnable;
	@Value("${SSL_CronExp_Enable}")
	private boolean sslCronExpEnable;
	
	/*@Bean
	CorsConfigurationSource corsConfigurationSource() {
        final org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
		return source;
	}*/

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		//web.httpFirewall(loggingHttpFirewall);
		//web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
		return;
	}	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*captchaVerifyFilter.setFailureUrl(String.format("/signin?error=%d", SysCode.InvalidCaptcha.value()));
		captchaVerifyFilter.setTargetUrl("/signin.do");
		http.addFilterBefore(captchaVerifyFilter, UsernamePasswordAuthenticationFilter.class);*/

		http.addFilterBefore(doWebSecurityCorsFilter(), ChannelProcessingFilter.class);
		/**
		 * DDOS org.eclipse.jetty.servlets.DoSFilter
		 */
		http.addFilterBefore(doSFilter(), FilterSecurityInterceptor.class);
		/**
		 * 加上ip Filter
		 */
		http.addFilterBefore(ipFilter(), FilterSecurityInterceptor.class);

		http.addFilterBefore(multiMemberFilter(), FilterSecurityInterceptor.class);

		/**
		 * 加上 System option Filter
		 */
		http.addFilterBefore(systemOptionFilter(), FilterSecurityInterceptor.class);

		//http.addFilterBefore(new RequestRejectedExceptionFilter(),ChannelProcessingFilter.class);
		/**
		 * require ssl, cronExpEnable 目前僅正式環境為 false, so !false = true, 正式環境開SSL
		 */
		if(sslCronExpEnable) {
			http.requiresChannel().anyRequest().requiresSecure();
		}

		http
		.cors()
		.and()
		.headers().contentSecurityPolicy("script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; frame-src 'none'; object-src 'none'; img-src 'self' data: http: https:; connect-src 'self' wss: http: https:; font-src 'self'; frame-ancestors 'none'; media-src 'none'; manifest-src 'none'; prefetch-src 'self'; base-uri 'none';")
		.and()
		.cacheControl()
		.disable()
	    //.addHeaderWriter(new DefaultCacheControlHeadersWriter("no-store","no-cache", "max-age=0", "must-revalidate", "private"))
		.addHeaderWriter(new StaticHeadersWriter(HttpHeaders.CACHE_CONTROL, "private, no-cache, no-store, max-age=0, must-revalidate"))
		.addHeaderWriter(new StaticHeadersWriter(HttpHeaders.PRAGMA, "no-cache"))
		.frameOptions().sameOrigin()
		.and()
		.csrf()
		.csrfTokenRepository(new CookieCsrfTokenRepository())
		.ignoringAntMatchers("/connect/*")
		.ignoringAntMatchers("/service/*/*/*")
		.ignoringAntMatchers("/service/test/*/*/*")
		.ignoringAntMatchers("/service/data")
		.ignoringAntMatchers("/service/test/data")
		.ignoringAntMatchers("/service/uid_valid")
		.ignoringAntMatchers("/service/type_valid")
		.ignoringAntMatchers("/service/txid_valid")
		.ignoringAntMatchers("/service/txid_status")
		.ignoringAntMatchers("/service/otheridentify/apply")
		.ignoringAntMatchers("/service/spsignature/*")
		.ignoringAntMatchers("/service/spsignature/*/*/*")
		.ignoringAntMatchers("/resources/plugins/font-awesome/css/*")
		.ignoringAntMatchers("/resources/plugins/font-awesome/fonts/*")
		.ignoringAntMatchers("/resources/plugins/font-awesome/less/*")
		.ignoringAntMatchers("/resources/plugins/font-awesome/scss/*")
		.ignoringAntMatchers("/resources/dist/pdf/*.pdf")
		.ignoringAntMatchers("/resources/dist/pdf/*.odt")
		.ignoringAntMatchers("/resources/dist/twca/AgentSetup.exe")
		.ignoringAntMatchers("/resources/dist/twca/AgentSetup.pkg")
		.ignoringAntMatchers("/rest/user/test/immigration")
		.ignoringAntMatchers("/rest/user/test/validUtil")
		.ignoringAntMatchers("/mydatalog/v01/log")
		.ignoringAntMatchers("/log/sp")
		.ignoringAntMatchers("/log/dp")
		.ignoringAntMatchers("/error")
		.ignoringAntMatchers("/login-again")
		.ignoringAntMatchers("/find_all_resources")
		.ignoringAntMatchers("/rest/user/alive")
		.ignoringAntMatchers("/rest/chatbot/webhook")
		.and()
		
		/**
		 * sessionManagement
		 */
		//.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and()
		//.sessionManagement().sessionFixation().newSession().and()
		//.sessionManagement().sessionFixation().migrateSession().and()
		//.sessionManagement().sessionFixation().none().and()
        
		.exceptionHandling()
        .authenticationEntryPoint(customAuthenticationEntryPoint)
        .accessDeniedHandler(customAccessDeniedHandler)
        .and()
		.authorizeRequests()
		//.antMatchers("/resources/**").permitAll()
		.antMatchers("/resources/dist/css/*.css").permitAll()
		.antMatchers("/resources/dist/css/bootstrap.css.map").permitAll()
		.antMatchers("/resources/dist/fonts/*.eot").permitAll()
		.antMatchers("/resources/dist/fonts/*.svg").permitAll()
		.antMatchers("/resources/dist/fonts/*.ttf").permitAll()
		.antMatchers("/resources/dist/fonts/*.woff").permitAll()
		.antMatchers("/resources/dist/fonts/*.woff2").permitAll()
		.antMatchers("/resources/dist/fonts/*.otf").permitAll()
		.antMatchers("/resources/dist/image/*.png").permitAll()
		.antMatchers("/resources/dist/image/*.jpg").permitAll()
		.antMatchers("/resources/dist/image/*.svg").permitAll()
		.antMatchers("/resources/dist/js/*.js").permitAll()
		.antMatchers("/resources/dist/pdf/*.pdf").permitAll()
		.antMatchers("/resources/dist/pdf/*.odt").permitAll()
		.antMatchers("/resources/dist/twca/AgentSetup.exe").permitAll()
		.antMatchers("/resources/dist/twca/AgentSetup.pkg").permitAll()
		.antMatchers("/resources/plugins/font-awesome/css/*.css").permitAll()
		.antMatchers("/resources/plugins/font-awesome/css/*.map").permitAll()
		.antMatchers("/resources/plugins/font-awesome/fonts/*.eot").permitAll()
		.antMatchers("/resources/plugins/font-awesome/fonts/*.svg").permitAll()
		.antMatchers("/resources/plugins/font-awesome/fonts/*.ttf").permitAll()
		.antMatchers("/resources/plugins/font-awesome/fonts/*.woff").permitAll()
		.antMatchers("/resources/plugins/font-awesome/fonts/*.woff2").permitAll()
		.antMatchers("/resources/plugins/font-awesome/fonts/*.otf").permitAll()
		.antMatchers("/resources/plugins/font-awesome/less/*.less").permitAll()
		.antMatchers("/resources/plugins/font-awesome/scss/*.scss").permitAll()
		.antMatchers("/RiAPI.js").permitAll()
		.antMatchers("/signin").permitAll()
		.antMatchers("/signin2").permitAll()
		.antMatchers("/captcha.jpg").permitAll()
		.antMatchers("/login").permitAll()
		//.antMatchers("/login_success").permitAll()
		.antMatchers("/login_fail").permitAll()
		.antMatchers("/logout_success").permitAll()
		.antMatchers("/signin.do").permitAll()
		.antMatchers("/sp/service").permitAll()
		.antMatchers("/sp/service/counter").permitAll()
		.antMatchers("/sp/service/counter/{cate}/list").permitAll()
		.antMatchers("/sp/service/counter/agent").permitAll()
		.antMatchers("/sp/sitemap").permitAll()
		.antMatchers("/sp/contactus").permitAll()
		.antMatchers("/sp/faq").permitAll()
		.antMatchers("/sp/about").permitAll()
		.antMatchers("/sp/news").permitAll()
		.antMatchers("/sp/news/notify").permitAll()
		.antMatchers("/sp/news/op").permitAll()
		.antMatchers("/sp/news/manual").permitAll()
		.antMatchers("/sp/news/download").permitAll()
		.antMatchers("/sp/news/ready").permitAll()
		.antMatchers("/sp/news/lazy").permitAll()
		.antMatchers("/sp/news/apply").permitAll()
		.antMatchers("/sp/news/slist1").permitAll()
		.antMatchers("/sp/news/slist2").permitAll()
		.antMatchers("/sp/news/slist3").permitAll()
		.antMatchers("/sp/news/slist4").permitAll()
		.antMatchers("/sp/servicepolicy").permitAll()
		.antMatchers("/sp/downloadpolicy").permitAll()
		.antMatchers("/sp/privacypolicy").permitAll()
//		.antMatchers("/sp/verification/list").permitAll()
//		.antMatchers("/sp/verification/list/*").permitAll()
		.antMatchers("/dp/testdp").permitAll()
		.antMatchers("/rest/dp/unzip").permitAll()
		.antMatchers("/rest/dp/unzip2").permitAll()
		.antMatchers("/personal/cate/1/list").permitAll()
		.antMatchers("/personal/cate/2/list").permitAll()
		.antMatchers("/personal/cate/3/list").permitAll()
		.antMatchers("/personal/cate/4/list").permitAll()
		.antMatchers("/personal/cate/5/list").permitAll()
		.antMatchers("/personal/cate/6/list").permitAll()
		.antMatchers("/personal/cate/7/list").permitAll()
		.antMatchers("/personal/cate/8/list").permitAll()
		.antMatchers("/personal/cate/9/list").permitAll()
		.antMatchers("/personal/cate/10/list").permitAll()
		.antMatchers("/personal/cate/11/list").permitAll()
		.antMatchers("/personal/cate/12/list").permitAll()
		.antMatchers("/personal/cate/13/list").permitAll()
		.antMatchers("/personal/verification/download").permitAll()
		.antMatchers("/personal/verification/preview").permitAll()
		.antMatchers("/personal/download-pre-testing/cate/*/single").permitAll()
		.antMatchers("/personal/box/agentPreview/*/*/*").permitAll()
		.antMatchers("/mydatalog/v01/log").permitAll()
		.antMatchers("/log/sp").permitAll()
		.antMatchers("/log/dp").permitAll()
		.antMatchers("/organ").permitAll()
		.antMatchers("/organ/error1").permitAll()
		.antMatchers("/organ/error2").permitAll()
		.antMatchers("/organ/error3").permitAll()
		.antMatchers("/organ/error4").permitAll()
		.antMatchers("/organ/error5").permitAll()
		.antMatchers("/organ/error6").permitAll()
		.antMatchers("/authorization").permitAll()
		.antMatchers("/rest/personal/contactus").permitAll()
		.antMatchers("/rest/personal/countpercent/*").permitAll()
		.antMatchers("/rest/personal/countpercent/batch/*").permitAll()
		.antMatchers("/rest/personal/countpercent/batch/*/*").permitAll()
		.antMatchers("/rest/user/info").permitAll()
		.antMatchers("/rest/user/certVerifyUrl").permitAll()
		.antMatchers("/rest/user/nhicardVerifyUrl").permitAll()
		.antMatchers("/rest/user/verifyCert").permitAll()
		.antMatchers("/rest/user/verifyFic").permitAll()
		.antMatchers("/rest/user/verifyFch").permitAll()
		.antMatchers("/rest/user/verifyFcs").permitAll()
		.antMatchers("/rest/user/verifyNHICard").permitAll()
		.antMatchers("/rest/user/verifyMobileId").permitAll()
		.antMatchers("/rest/user/notifyTFido").permitAll()
		.antMatchers("/rest/user/confirmTFido").permitAll()
		.antMatchers("/rest/user/verifyMOECA").permitAll()
		.antMatchers("/rest/user/verifyGRecaptcha").permitAll()
		.antMatchers("/rest/user/MOECAVerifyUrl").permitAll()
		.antMatchers("/rest/user/piiVerifyUrl/*").permitAll()
		.antMatchers("/rest/user/piiVerifyUrl/mydata/*").permitAll()
		.antMatchers("/rest/user/newPiiVerifyUrl/check").permitAll()
		.antMatchers("/rest/user/webservice/birthdate").permitAll()
		.antMatchers("/rest/user/webservice/nhi").permitAll()
		.antMatchers("/rest/user/emailOrMobileLoginStep1").permitAll()
		.antMatchers("/rest/user/emailOrMobileLoginStep2").permitAll()
		.antMatchers("/rest/user/emailOrMobileLoginStep/check").permitAll()
		.antMatchers("/rest/user/verifyMobileOrEmail").permitAll()
		.antMatchers("/rest/user/verifyMobileOrEmailForVerificatoin").permitAll()
		//.antMatchers("/rest/user/agentStep1Confirm").permitAll()
		//.antMatchers("/rest/user/agentStep2Confirm").permitAll()
		//.antMatchers("/rest/user/agentStep2Reject").permitAll()
		//.antMatchers("/rest/user/agentStep4Confirm").permitAll()
		.antMatchers("/rest/user/test/immigration").permitAll()
		.antMatchers("/rest/user/test/validUtil").permitAll()
		.antMatchers("/rest/user/captchainfo").permitAll()
		.antMatchers("/rest/user/ficAtmLogin").permitAll()
		.antMatchers("/rest/user/fchInitParam").permitAll()
		.antMatchers("/rest/user/fcsInitParam").permitAll()
		.antMatchers("/rest/user/createRandomVerify").permitAll()
		.antMatchers("/rest/user/check/isMember").permitAll()
		.antMatchers("/rest/user/check/isMemberWithMoeca").permitAll()
		.antMatchers("/rest/user/agree/privacy").permitAll()
		.antMatchers("/rest/personal/verification/check").permitAll()
		.antMatchers("/rest/personal/verification/lock").permitAll()
		.antMatchers("/rest/personal/verification/preview").permitAll()
		.antMatchers("/rest/option/town").permitAll()
		.antMatchers("/rest/option/vill").permitAll()
		.antMatchers("/rest/service/loginBySp").permitAll()
		.antMatchers("/rest/search/check").permitAll()
		.antMatchers("/rest/sp/service").permitAll()
		.antMatchers("/rest/sp/service/counter").permitAll()
		.antMatchers("/rest/auth/logout").permitAll()
		.antMatchers("/rest/analysis/record").permitAll()
		.antMatchers("/sp/mTeams").permitAll()
		.antMatchers("/sp/pPlicy").permitAll()
		.antMatchers("/sp/verifyCertificate").permitAll()
		.antMatchers("/").permitAll()
		.antMatchers("/logout").permitAll()
		.antMatchers("/signin.do").permitAll()
		.antMatchers("/connect/*").permitAll()
		.antMatchers("/error").permitAll()
		.antMatchers("/login-again").permitAll()
		.antMatchers("/service/data").permitAll()
		.antMatchers("/service/test/data").permitAll()
		.antMatchers("/service/uid_valid").permitAll()
		.antMatchers("/service/type_valid").permitAll()
		.antMatchers("/service/txid_valid").permitAll()
		.antMatchers("/service/txid_status").permitAll()
		.antMatchers("/service/list/*").permitAll()
		.antMatchers("/service/*/*/*").permitAll()
		.antMatchers("/service/counter/*").permitAll()
		.antMatchers("/service/otheridentify/apply").permitAll()
		.antMatchers("/service/spsignature/*").permitAll()
		.antMatchers("/service/spsignature/*/*/*").permitAll()
		.antMatchers("/search/*").permitAll()
		.antMatchers("/mutipledownload/list").permitAll()
		.antMatchers("/mutipledownload/download/*").permitAll()
		.antMatchers("/mutipledownload/batch/download/*").permitAll()
		.antMatchers("/find_all_resources").permitAll()
		.antMatchers("/hello").permitAll()
		.antMatchers("/hellomail").permitAll()
		.antMatchers("/testthread").permitAll()
		.antMatchers("/news/list").permitAll()
		.antMatchers("/news/detail/*").permitAll()	
		.antMatchers("/rest/user/alive").permitAll()
		.antMatchers("/rest/chatbot/webhook").permitAll()
		.antMatchers("/rest/chatbot/send").permitAll()
		.antMatchers("/rest/system/option/init").permitAll()
		.antMatchers("/rest/system/option/test/sendEmail").permitAll()
		.antMatchers("/rest/system/option/test/TWFido/push").permitAll()
		.antMatchers("/rest/system/option/test/TWFido/confirm").permitAll()
		.antMatchers("/checkOtp").permitAll()
		.antMatchers("/inputMask").permitAll()
		.anyRequest().authenticated()
		.and()
		.formLogin()
        .loginPage("/")
		.and()
		.logout()
		//.logoutUrl("/signout.do")
		.logoutRequestMatcher(new AntPathRequestMatcher("/signout.do"))
		//.logoutSuccessUrl("/logout_success")
		.logoutSuccessUrl("/")
		.invalidateHttpSession(true);
	}
	
	private static class DefaultCacheControlHeadersWriter implements HeaderWriter {
	    private final HeaderWriter delegate;

	    private DefaultCacheControlHeadersWriter(String... cacheControlValues) {
	      this.delegate = new StaticHeadersWriter(HttpHeaders.CACHE_CONTROL, cacheControlValues);
	    }

		@Override
		public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		      if (!response.containsHeader(HttpHeaders.CACHE_CONTROL)) {
			        delegate.writeHeaders(request, response);
			  }
		}
	}
    
	private Filter doSFilter() {
		if(doSFilter==null) {
			doSFilter = new DoSFilter();
			doSFilter.setMaxRequestsPerSec(1000);
			doSFilter.setMaxRequestMs(600000l);
			doSFilter.setDelayMs(2000l);
		}
		return doSFilter;
	}
	
	private Filter doWebSecurityCorsFilter() {
		if(webSecurityCorsFilter==null) {
			webSecurityCorsFilter = new WebSecurityCorsFilter();
		}
		return webSecurityCorsFilter;
	}
	
	private Filter ipFilter() {
        IpFilter ipFilter = new IpFilter();
        ipFilter.setCronExpEnable(cronExpEnable);
        ipFilter.setWhiteListMapper(whiteIpListMapper);
        return ipFilter;
    }

    private Filter systemOptionFilter() {
		SystemOptionFilter optionFilter = new SystemOptionFilter();
		optionFilter.setSystemOptionUtil(systemOptionUtil);
		return optionFilter;
	}

    private Filter multiMemberFilter() {
        MultiMemberFilter multiMemberFilter = new MultiMemberFilter();
        multiMemberFilter.setLogMemberSessionMapper(logMemberSessionMapper);
        return multiMemberFilter;
    }
}
