package tw.gov.ndc.emsg.mydata.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.util.RandomUtils;

import tw.gov.ndc.emsg.mydata.entity.AuthToken;
import tw.gov.ndc.emsg.mydata.mapper.AuthTokenMapper;

@Component
public class TokenUtils {
	
	@Autowired AuthTokenMapper authTokenMapper;
 	 
	@Value("${app.frontend.context.url}") 
	private String frontendContextUrl;
	
	@Value("${gsp.oidc.token.asid}")
	private String asId;
	
	public AuthToken generateToken(String account, String scope,String checkLevel) {
		RandomUtils ru = new RandomUtils();
		String token = ru.alphaNumericString(64).toLowerCase(Locale.ENGLISH);
		
		/*String asId = "mydata";
		if(frontendContextUrl.contentEquals("https://mydatadev.nat.gov.tw/mydata")||frontendContextUrl.contentEquals("https://mydatadev.nat.gov.tw/mydata-v2")) {
			asId = "mydatadev";
		}else if(frontendContextUrl.contentEquals("https://mydata.nat.gov.tw")||frontendContextUrl.contentEquals("https://mydata.nat.gov.tw/mydata-v2")) {
			asId = "mydata";
		}*/
		
		/**
		 * level
		 * 0 自然人憑證   ROLE_ID 0 ROLE_TYPE 0 cer
		 * 0.5 工商憑證   ROLE_ID 0 ROLE_TYPE 0.5 moeaca
		 * 1 tfido       ROLE_ID 1 ROLE_TYPE 1 tfido
		 * 2 健保卡       ROLE_ID 2 ROLE_TYPE 2 nhi
		 * 3 雙證件驗證       ROLE_ID 3 ROLE_TYPE 3 pii
		 * 4 行動電話         ROLE_ID 4 ROLE_TYPE 4 mobile
		 * 5 EMAIL       ROLE_ID 5 ROLE_TYPE 5  email
		 */		
		
		AuthToken authToken = new AuthToken();
		authToken.setAccount(ValidatorHelper.removeSpecialCharacters(account));
		authToken.setAsId(ValidatorHelper.removeSpecialCharacters(asId));
		Date now = new Date();
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(now);
		// 起始時間定為24小時前(一天前未執行下載要求有效)
		cal.add(Calendar.HOUR_OF_DAY, 2);
		Date overTime = cal.getTime();		
		authToken.setCtime(now);
		authToken.setEtime(overTime);
		authToken.setScope(ValidatorHelper.removeSpecialCharacters(scope));
		authToken.setToken(ValidatorHelper.removeSpecialCharacters(token));
		if(checkLevel.equalsIgnoreCase("cer")) {
			authToken.setVerification("CER");
		}else if(checkLevel.equalsIgnoreCase("fic")) {
			authToken.setVerification("FIC");
		}else if(checkLevel.equalsIgnoreCase("fch")) {
			authToken.setVerification("FCH");
		}else if(checkLevel.equalsIgnoreCase("fcs")) {
			authToken.setVerification("FCS");
		}else if(checkLevel.equalsIgnoreCase("moeaca")) {
			authToken.setVerification("MOE");
		}else if(checkLevel.equalsIgnoreCase("tfido")) {
			authToken.setVerification("TFD");
		}else if(checkLevel.equalsIgnoreCase("nhi")) {
			authToken.setVerification("NHI");
		}else if(checkLevel.equalsIgnoreCase("pii")) {
			authToken.setVerification("PII");
		}else if(checkLevel.equalsIgnoreCase("email")) {
			authToken.setVerification("OTP");
		}
		authTokenMapper.insertSelective(authToken);
		return authToken;
	}

	public static String getFullAccessToken(AuthToken authToken) {
		String ret = authToken.getAsId()+"::"+authToken.getToken();
		return ret;
	}
}
