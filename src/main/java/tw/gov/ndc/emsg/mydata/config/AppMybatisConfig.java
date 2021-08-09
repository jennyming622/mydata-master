/**
 * 
 */
package tw.gov.ndc.emsg.mydata.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.riease.common.sysinit.AppDatasourceConfig;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

/**
 * Spring, Java based configuration. <br>
 * 整合mybatis的spring配置 <br>
 * 對應原本 spring-datasource-config.xml 中的設定。
 * 
 * @author wesleyzhuang
 * @see <a href="http://www.mybatis.org/mybatis-3/zh/configuration.html">
 * 			http://www.mybatis.org/mybatis-3/zh/configuration.html</a>
 */
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 10)
@Configuration
@MapperScan(basePackages = {
		"tw.gov.ndc.emsg.mydata.mapper",
	 	"tw.gov.ndc.emsg.mydata.dao"
	})
public class AppMybatisConfig extends AppDatasourceConfig {
	
	@Bean
    public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setConfigLocation(
        		applicationContext.getResource("classpath:conf/mybatis-config.xml"));
        return sessionFactoryBean;
    }
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

}
