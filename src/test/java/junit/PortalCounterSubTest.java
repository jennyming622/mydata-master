package junit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.gov.ndc.emsg.mydata.config.AppConfig;
import tw.gov.ndc.emsg.mydata.entity.ext.PortalCounterSubExt;
import tw.gov.ndc.emsg.mydata.mapper.PortalCounterSubMapperExt;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = AppConfig.class)
public class PortalCounterSubTest {
    private Logger logger = LoggerFactory.getLogger(PortalCounterSubTest.class);

    @Autowired
    private PortalCounterSubMapperExt portalCounterSubMapper;

    @Test
    public void test() throws Exception{
        Map<String, Object> param = new HashMap<>();
        List<PortalCounterSubExt> subExtList = portalCounterSubMapper.selectExt(param);

        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(subExtList));
    }
}
