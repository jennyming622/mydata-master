package tw.gov.ndc.emsg.mydata.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tw.gov.ndc.emsg.mydata.entity.Member;

public class MaskUtil {

    static Logger logger = LoggerFactory.getLogger(MaskUtil.class);
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
    public static Member maskSensitiveInformation(Member m){
	    	if(StringUtils.isNotEmpty(m.getAccount())){
	    		m.setAccount(maskUid(m.getAccount()));
	    	}
        if(StringUtils.isNotEmpty(m.getName())){
            m.setName(maskName(m.getName()));
        }
        if(StringUtils.isNotEmpty(m.getEmail())){
            m.setEmail(maskEmail(m.getEmail()));
        }
        if(StringUtils.isNotEmpty(m.getUid())){
            m.setUid(maskUid(m.getUid()));
        }
        if(StringUtils.isNotEmpty(m.getMobile())){
            m.setMobile(maskMobile(m.getMobile()));
        }
        if(m.getBirthdate()!=null) {
        		String yearStr = String.valueOf(Integer.valueOf(sdf1.format(m.getBirthdate())) -1911);
        		String monthStr = sdf2.format(m.getBirthdate());
        		String dateStr = sdf3.format(m.getBirthdate());
        		if(yearStr.length()==1) {
        			yearStr = "00" + yearStr;
        		}else if(yearStr.length()==2) {
        			yearStr = "0" + yearStr;
        		}
        		m.setBirthdateForRE(maskBirthdate(yearStr+monthStr+dateStr));
        }
        return m;
    }

    private static String maskMobileOrUid(String value){
        return value.substring(0,value.length()-4) + "****";
    }

    public static String maskUid(String value){
        return value.substring(0,value.length()-4) + "****";
    }
    
    private static String maskMobile(String value){
    		String ret = value.substring(0,3) + "****" + value.substring(7,10);
        return ret;
    }    

    private static String maskName(String name){
        // 若不足3個字則補齊
        if(name.length() == 2){
            name += '#';
        }
        // 取代中間的char
        name = replaceAt(name,name.length()/2,"*");
        // slice補充的字元
        return name.endsWith("#") ? name.substring(0,name.length()-2) : name;
    }

    public static String maskNameByO(String name){
        // 若不足3個字則補齊
        if(name.length() == 2){
            name += '#';
        }
        // 取代中間的char
        name = replaceAt(name,name.length()/2,"○");
        // slice補充的字元
        return name.endsWith("#") ? name.substring(0,name.length()-2) : name;
    }
    
    public static String maskNameByEtc(String name){
    	if(name.length() > 3){
    		name = name.substring(0, 3) + "…";
    	}
        return name;
    }    
    
    private static String maskEmail(String email){
        String[] cs = email.split("@");
        String preStr = cs[0];
        if(preStr.length() < 5){
            for(int i = 1; i < preStr.length() ;i++){
                preStr = replaceAt(preStr,i,"*");
            }
        }else {
            preStr = preStr.substring(0, preStr.length()-4) + "****";
        }
        if(cs.length>=2) {
        		return preStr + '@' + cs[1];
        }else {
        		return preStr;
        }
    }


    private static String replaceAt(String str, int idx ,String replace){
        return str.substring(0,idx) + replace + str.substring(idx+1);
    }

    private static String maskBirthdate(String birthdate){
        String tmp1 = replaceAt(birthdate, 1, "*");
        String tmp2 = replaceAt(tmp1, 3, "*");
        return replaceAt(tmp2, 5, "*");
    }

}


