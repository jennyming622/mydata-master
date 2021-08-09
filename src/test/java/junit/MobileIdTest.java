package junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.gov.ndc.emsg.mydata.config.AppConfig;
import tw.gov.ndc.emsg.mydata.entity.MobileIdLog;
import tw.gov.ndc.emsg.mydata.mapper.MobileIdLogMapper;

import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = AppConfig.class)
public class MobileIdTest {

    private Logger logger = LoggerFactory.getLogger(MobileIdTest.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private final Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    private MobileIdLogMapper mobileIdLogMapper;

    @Test
    public void test() throws Exception{
        logger.info("test");

        try {
            String account = encoder.encodeToString(("F200000008".toUpperCase()+"20210102").getBytes("UTF-8"));
            MobileIdLog mobileIdLog = new MobileIdLog();
            mobileIdLog.setAccount(account);
            mobileIdLog.setUid("F200000008");
            mobileIdLog.setVerifyNo(UUID.randomUUID().toString().replaceAll("-", ""));
            mobileIdLog.setMobile("0911223344");
            mobileIdLog.setBirthdate(sdf.parse("20210102"));
            mobileIdLog.setVersion("1.0");
            mobileIdLog.setCtime(new Date());
            mobileIdLogMapper.insertSelective(mobileIdLog);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}
