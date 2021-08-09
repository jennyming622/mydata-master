package tw.gov.ndc.emsg.mydata.util;


import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CDateUtil {

    private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdfMonthdate = new SimpleDateFormat("MMdd");
	
    public static Date ROCDateToAD(String dateStr) throws Exception{
        if(StringUtils.isBlank(dateStr)) {
            return null;
        }
        dateStr = dateStr.replaceFirst("^0*", "");
        Integer tmpYear = null;
        String date = "";
        if(dateStr.length() == 7) {
            tmpYear = Integer.parseInt(StringUtils.substring(dateStr, 0, 3)) + 1911;
            date = StringUtils.substring(dateStr, 3);
        } else {
            tmpYear = Integer.parseInt(StringUtils.substring(dateStr, 0, 2)) + 1911;
            date = StringUtils.substring(dateStr, 2);
        }

        String year = String.valueOf(tmpYear);

        return sdf6.parse(year + date);
    }
    
    public static String ROCDateToADStr(String dateStr) throws Exception{
        if(StringUtils.isBlank(dateStr)) {
            return null;
        }
        dateStr = dateStr.replaceFirst("^0*", "");
        Integer tmpYear = null;
        String date = "";
        if(dateStr.length() == 7) {
            tmpYear = Integer.parseInt(StringUtils.substring(dateStr, 0, 3)) + 1911;
            date = StringUtils.substring(dateStr, 3);
        } else {
            tmpYear = Integer.parseInt(StringUtils.substring(dateStr, 0, 2)) + 1911;
            date = StringUtils.substring(dateStr, 2);
        }

        String year = String.valueOf(tmpYear);

        return (year + date);
    }
    
    public static String ADDateToROCStr(Date date) throws Exception{
    	String dateStr="";
    	String adYearStr = String.valueOf(Integer.valueOf(sdfYear.format(date))- 1911) ;
    	if(adYearStr.length()==2) {
    		adYearStr = "0"+adYearStr;
    	}else if(adYearStr.length()==1) {
    		adYearStr = "00"+adYearStr;
    	}
    	dateStr = dateStr+sdfMonthdate.format(date);
    	return dateStr;
    }

    public static Date getAssignDateZero(Integer month, Integer day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DATE, day);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c.getTime();
    }

    public static void main(String[] args) {
        try {
            Date date428 = getAssignDateZero(4, 28);
            Date date701 = getAssignDateZero(7, 1);
            System.out.println(date428.before(new Date()));
            System.out.println(date701.before(new Date()));
        } catch(Exception e) {

        }
    }
}
