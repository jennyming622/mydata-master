package test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;

public class TestReplaceParam {

	public static void main(String[] args) {
		String paramstr = "{\n" + 
				"	\"token\":#{accessToken},\n" + 
				"	\"ID\":#{uid},\n" + 
				"	\"Birthday\":#{birthdate}\n" + 
				"}";

		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setAccessToken("xxxxxxxxxxxxxx");
		UserInfoEntity userInfoEntity = new UserInfoEntity();
		userInfoEntity.setUid("H120983053");
		userInfoEntity.setBirthdate("1970/09/09");
		System.out.println(paramstr);
		System.out.println(replaceParamStr(paramstr,tokenEntity,userInfoEntity));

	}
	private static String replaceParamStr(String paramstr,TokenEntity  tokenEntity,UserInfoEntity userInfoEntity) {
		String patternStr = "#\\{[\\w:/\\\\.]*\\}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		while(matcher.find()){
			matchStrList.add(matcher.group());
			System.out.println(matcher.group());
		}
		if(matchStrList!=null&&matchStrList.size()>0){
			 System.out.println("---------------------------");
			 for(String s:matchStrList){
				 String forReplaceStr = "";
				 String matchInnerStr = s.replaceAll("#\\{", "").replaceAll("\\}", "").trim();
				 System.out.println(matchInnerStr);
				 if(matchInnerStr.equalsIgnoreCase("accessToken")) {
					 forReplaceStr = "\""+tokenEntity.getAccessToken()+"\"";
				 }
				 if(matchInnerStr.equalsIgnoreCase("uid")) {
					 forReplaceStr = "\""+userInfoEntity.getUid()+"\"";
				 }
				 if(matchInnerStr.equalsIgnoreCase("birthdate")) {
					 forReplaceStr = "\""+userInfoEntity.getBirthdate()+"\"";
				 }
				 if(matchInnerStr.equalsIgnoreCase("name")) {
					 forReplaceStr = "\""+userInfoEntity.getName()+"\"";
				 }
				 if(matchInnerStr.equalsIgnoreCase("email")) {
					 forReplaceStr = "\""+userInfoEntity.getEmail()+"\"";
				 }
				 paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println("---------------------------");
		}
		return paramstr;
	}
}
