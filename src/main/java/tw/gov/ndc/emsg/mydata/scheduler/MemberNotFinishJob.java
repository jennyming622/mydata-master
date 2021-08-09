package tw.gov.ndc.emsg.mydata.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.mapper.MemberMapper;
import tw.gov.ndc.emsg.mydata.mapper.MemberPrivacyMapper;

@Component
public class MemberNotFinishJob {
	private static Logger logger = LoggerFactory.getLogger(tw.gov.ndc.emsg.mydata.scheduler.MemberNotFinishJob.class);
	
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private MemberPrivacyMapper memberPrivacyMapper;
	
    @Value("${member.finish.cron.enable}")
    private Boolean memberFinishCronEnable;
    
    /**
     * 60 * 1000 = 60 sec (1 minute)
     * 3600 *1000 = 3600 sec (1hr)
     */
    @Scheduled(fixedRate = 1800000)
    public void execute() {
        if(memberFinishCronEnable == false) {
            return;
        }
    	List<Member> memberList = memberMapper.selectNotFinishMember();
    	if(memberList!=null&&memberList.size()>0) {
    		Date now = new Date();
    		Date before = new Date();
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(now);
    		calendar.add(Calendar.MINUTE, -60);  //30分鐘前
    		before = calendar.getTime();  
    		for(Member m:memberList) {
    			if(m.getCtime()!=null&&m.getCtime().before(before)) {
    				memberMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(m.getId()));
    				//memberPrivacyMapper.deleteByPrimaryKey(ValidatorHelper.limitNumber(m.getId()));
    			}
    		}
    	}
    }
}
