/**
 * 
 */
package com.riease.common.sysinit;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring, Java based configuration. <br>
 * 對應原本 spring-datasource-config.xml 中的設定。
 * 
 * @author wesleyzhuang
 * @see <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#__propertysource">
 * 			http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#__propertysource</a>
 */
@Configuration
@EnableTransactionManagement
public class AppDatasourceConfig implements ApplicationContextAware {
	
	private static Logger logger = LoggerFactory.getLogger(AppDatasourceConfig.class);

	protected ApplicationContext applicationContext;
	
	@Value("${DB_URL}")
    private String jdbcUrl;
	
	@Value("${DB_DRIVER}")
    private String jdbcDriver;
	
	@Value("${DB_USER}")
    private String dbUser;
	
	@Value("${DB_PASSWORD}")
    private String dbPassword;
				
	@Value("${datasource.maxTotal:100}")
    private int maxTotal;
	
	@Value("${datasource.minIdle:10}")
    private int minIdle;
	
	@Value("${datasource.maxWaitMillis:10000}")
    private int maxWaitMillis;
	
	@Value("${datasource.testWhileIdle:true}")
    private boolean testWhileIdle;
	
	@Value("${datasource.testOnBorrow:true}")
    private boolean testOnBorrow;
	
	@Value("${datasource.testOnCreate:true}")
    private boolean testOnCreate;
	
	@Value("${datasource.testOnReturn:false}")
    private boolean testOnReturn;
	
	@Value("${datasource.timeBetweenEvictionRunsMillis:10000}")
    private int timeBetweenEvictionRunsMillis;
	
	@Value("${datasource.minEvictableIdleTimeMillis:30000}")
    private int minEvictableIdleTimeMillis;
	
	/**
	 * JDBC dataSource
	 * @return
	 * @see javax.sql.DataSource
	 */
	@Bean
    public DataSource dataSource() {
		
		logger.trace("jdbcDriver ...................... {}",jdbcDriver);
		logger.trace("jdbcUrl ......................... {}",jdbcUrl);
		logger.trace("maxTotal ........................ {}",maxTotal);
		logger.trace("minIdle ......................... {}",minIdle);
		logger.trace("maxWaitMillis ................... {}",maxWaitMillis);
		logger.trace("testWhileIdle ................... {}",testWhileIdle);
		logger.trace("testOnBorrow .................... {}",testOnBorrow);
		logger.trace("testOnCreate .................... {}",testOnCreate);
		logger.trace("testOnReturn .................... {}",testOnReturn);
		logger.trace("timeBetweenEvictionRunsMillis ... {}",timeBetweenEvictionRunsMillis);
		logger.trace("minEvictableIdleTimeMillis ...... {}",minEvictableIdleTimeMillis);
				
		BasicDataSource ds =  new BasicDataSource();
		ds.setDriverClassName(jdbcDriver);
		ds.setUrl(jdbcUrl);
		ds.setUsername(dbUser);
		ds.setPassword(dbPassword);
		ds.setMaxTotal(maxTotal);
		ds.setMinIdle(minIdle);
		ds.setMaxWaitMillis(maxWaitMillis);
		ds.setTestWhileIdle(testWhileIdle);
		ds.setTestOnBorrow(testOnBorrow);
		ds.setTestOnCreate(testOnCreate);
		ds.setTestOnReturn(testOnReturn);
		ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		
        return ds;
    }
	
	/**
	 * JDBC Transaction Manager
	 * @return
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
	 */
	@Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
	
	/**
	 * Spring JdbcTemplate
	 * @return
	 */
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
