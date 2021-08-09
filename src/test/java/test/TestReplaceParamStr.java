package test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.gov.ndc.emsg.mydata.gspclient.bean.TokenEntity;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;

public class TestReplaceParamStr {

	public static void main(String[] args) {
		String paramstr = "{\n" + 
				"  \"lprNumber\": #{lprNumber},\n" + 
				"  \"idNo\": #{uid},\n" + 
				"  \"token\":#{accessToken}\n" + 
				"}";
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("lprNumber", "AYH-1075");
		paramMap.put("uid", "H120983053");
		paramMap.put("accessToken", "KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
		String result = replaceParamStrForPostAndParam(paramstr,paramMap);
		System.out.println(result);
	}
	private static String replaceParamStrForPostAndParam(String paramstr, Map<String,String> paramMap) {
		System.out.println("in replace=\n" + paramstr);
		String patternStr = "#\\{[\\w:/\\\\.-]*\\}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		while (matcher.find()) {
			matchStrList.add(matcher.group());
			System.out.println(matcher.group());
		}
		System.out.println("------------paramMap s---------------");
		for (Map.Entry entry : paramMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
		System.out.println("------------paramMap e---------------");
		if (matchStrList != null && matchStrList.size() > 0) {
			System.out.println("------------paramstr s---------------");
			for (String s : matchStrList) {
				String forReplaceStr = "";
				String matchInnerStr = s.replaceAll("#\\{", "").replaceAll("\\}", "").trim();
				System.out.println(matchInnerStr);
				if (matchInnerStr.equalsIgnoreCase("accessToken")) {
					forReplaceStr = "\"" + paramMap.get("accessToken") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("uid")) {
					forReplaceStr = "\"" + paramMap.get("uid") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("lprNumber")) {
					System.out.println("------------paramstr s in lprNumber ---------------:"+paramMap.get("lprNumber"));
					forReplaceStr = "\"" + paramMap.get("lprNumber") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("locationHsnCd")) {
					forReplaceStr = "\"" + paramMap.get("locationHsnCd") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("carNo")) {
					forReplaceStr = "\"" + paramMap.get("carNo") + "\"";
				}
				if (matchInnerStr.equalsIgnoreCase("caseYears")) {
					forReplaceStr = "\"" + paramMap.get("caseYears") + "\"";
				}
				paramstr = paramstr.replace(s, forReplaceStr);
			}
			System.out.println(paramstr);
			System.out.println("-------------paramstr e--------------");
		}
		return paramstr;
	}	
}
