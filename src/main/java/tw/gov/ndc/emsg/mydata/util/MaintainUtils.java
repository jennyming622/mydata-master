package tw.gov.ndc.emsg.mydata.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.PortalResourceMaintain;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMaintainMapper;

@Component
public class MaintainUtils {
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	@Autowired
	private PortalResourceMaintainMapper portalResourceMaintainMapper;
	
	public boolean checkInMaintain(Integer prId) {
		boolean check = false;
		List<PortalResourceMaintain> portalResourceMaintainList = portalResourceMaintainMapper.selectByPrId(ValidatorHelper.limitNumber(prId));
		if(portalResourceMaintainList!=null&&portalResourceMaintainList.size()>0) {
			for(PortalResourceMaintain maintain:portalResourceMaintainList) {
				if(maintain!=null&&maintain.getType()!=null) {
					if(maintain.getType().equalsIgnoreCase("F")) {
						Date stime = maintain.getStime();
						Date etime = maintain.getEtime();
						if(stime!=null&&etime!=null&&stime.before(new Date()) && etime.after(new Date())) {
							check = true;
							break;
						}
					}else if(maintain.getType().equalsIgnoreCase("M")) {
						Date stime = maintain.getStime();
						Date etime = maintain.getEtime();
						if(stime!=null&&etime!=null&&stime.before(new Date()) && etime.after(new Date())) {
							//判斷某日
							Date now = new Date();
							Calendar cal = Calendar.getInstance();
							int now_day = cal.get(Calendar.DAY_OF_MONTH);
							//int now_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
							if(maintain.getDay()!=null && maintain.getDay()==now_day && maintain.getsStime()!=null && maintain.getsEtime()!=null) {
								Date now_time = null;
								try {
									now_time = sdf.parse(sdf.format(now));
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if(now_time!=null&&maintain.getsStime().before(now_time)&&maintain.getsEtime().after(now_time)) {
									check = true;
									break;
								}
							}
						}
					}else if(maintain.getType().equalsIgnoreCase("W")) {
						Date stime = maintain.getStime();
						Date etime = maintain.getEtime();
						if(stime!=null&&etime!=null&&stime.before(new Date()) && etime.after(new Date())) {
							//判斷星期
							Date now = new Date();
							Calendar cal = Calendar.getInstance();
							//int now_day = cal.get(Calendar.DAY_OF_MONTH);
							int now_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
							if(maintain.getWeekDay()!=null && maintain.getWeekDay()==now_week && maintain.getsStime()!=null && maintain.getsEtime()!=null) {
								Date now_time = null;
								try {
									now_time = sdf.parse(sdf.format(now));
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if(now_time!=null&&maintain.getsStime().before(now_time)&&maintain.getsEtime().after(now_time)) {
									check = true;
									break;
								}
							}
						}
					}
				}
			}
		}
		return check;
	}
}
