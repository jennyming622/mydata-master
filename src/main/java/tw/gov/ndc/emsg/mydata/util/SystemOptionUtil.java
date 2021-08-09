package tw.gov.ndc.emsg.mydata.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.entity.SystemOption;
import tw.gov.ndc.emsg.mydata.mapper.SystemOptionMapper;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class SystemOptionUtil {
    Logger logger = LoggerFactory.getLogger(SystemOptionUtil.class);

    @Autowired
    private SystemOptionMapper systemOptionMapper;

    private static final Integer ENABLE = new Integer(1);
    private static final Integer DISABLE = new Integer(0);

    private static List<SystemOption> optionList = new ArrayList<>();

    /**
     * 載入 Options
     */
    @PostConstruct
    public void initOption() {
        optionList = systemOptionMapper.selectByExample();
        logger.info("init options => {}", optionList.size());
    }

    /**
     * 找到相對應 option
     * @param option
     * @return
     */
    public SystemOption getOption(String option) {
        return optionList.stream().filter(tmp -> StringUtils.equals(tmp.getOption(), option))
                .findFirst().orElse(null);
    }

    /**
     * 回傳顯示版本
     * @param option
     * @param ip
     * @return
     */
    public Integer use(String option, String ip) {
        SystemOption systemOption = getOption(option);
        return use(systemOption, ip);
    }

    /**
     * 回傳顯示版本
     * Notion 新功能過濾器
     * @param systemOption
     * @param ip
     * @return
     */
    public Integer use(SystemOption systemOption, String ip) {
        // 停用不顯示
        if(systemOption.getEnable().equals(new Integer(DISABLE))) {
            return new Integer(0);
        }

        if(StringUtils.isBlank(systemOption.getWhiteIp())) {
            return systemOption.getVersion();
        }

        for(String ipAddress : systemOption.getIpList()) {
            String[] ipAddressSplitList = ipAddress.split("[.]");
            if (ipAddressSplitList != null && ipAddressSplitList.length == 3) {
                if (ip.startsWith(ipAddress)) {
                    return systemOption.getTestVersion();
                }
            } else {
                if (ip.equals(ipAddress)) {
                    return systemOption.getTestVersion();
                }
            }
        }

        return systemOption.getVersion();
    }

}
