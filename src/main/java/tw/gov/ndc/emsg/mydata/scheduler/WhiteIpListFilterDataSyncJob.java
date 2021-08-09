package tw.gov.ndc.emsg.mydata.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import tw.gov.ndc.emsg.mydata.config.IpFilter;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownloadExample;
import tw.gov.ndc.emsg.mydata.entity.WhiteIpList;
import tw.gov.ndc.emsg.mydata.entity.WhiteIpListExample;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.WhiteIpListMapper;

@Component
public class WhiteIpListFilterDataSyncJob{
	private static Logger logger = LoggerFactory.getLogger(WhiteIpListFilterDataSyncJob.class);
	@Value("${CronExp_Enable}")
	private boolean cronExpEnable;	
	@Autowired
	private WhiteIpListMapper whiteIpListMapper;
	
	/**
	 * @Scheduled(fixedRate = 300000) 5 min
	 * @Scheduled(fixedRate = 6000) 1 min
	 */	
	@Scheduled(fixedRate = 300000)
	public void execute() {
		System.out.println("======WhiteIpListFilterDataSyncJob=======:"+new Date());
		System.out.println("======cronExpEnable=======:"+cronExpEnable);
		if(cronExpEnable) {
			Map<String,Object> param = new HashMap<String,Object>();
			List<WhiteIpList> whiteIpLists = whiteIpListMapper.selectByExample(param);
			IpFilter.authorizedIpAddresses = whiteIpLists.stream().map(WhiteIpList::getIp).collect(Collectors.toList());
			System.out.println("IpFilter.authorizedIpAddresses:"+IpFilter.authorizedIpAddresses.size());
		}
	}
}
