package tw.gov.ndc.emsg.mydata.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.mapper.LogMemberSessionMapper;

import java.util.Date;

@Component
public class MultiMemberDeleteJob {
    private static Logger logger = LoggerFactory.getLogger(tw.gov.ndc.emsg.mydata.scheduler.MultiMemberDeleteJob.class);

    @Autowired
    private LogMemberSessionMapper logMemberSessionMapper;

    @Value("${multi.member.login.cron.enable}")
    private Boolean multiMemberLoginCronEnable;

    /**
     * @Scheduled(fixedRate = 60000) 1 min
     */
    @Scheduled(fixedRate = 60000)
    public void execute() {
        logger.info("[MultiMemberDeleteJob] date: {}, enable: {}", new Date(), multiMemberLoginCronEnable);
        if(multiMemberLoginCronEnable == false) {
            return;
        }

        logMemberSessionMapper.deleteExpiredSession();
    }
}
