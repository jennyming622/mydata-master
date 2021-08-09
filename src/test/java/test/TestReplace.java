package test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestReplace {

	public static void main(String[] args) {
		 String patternStr = "#\\{[\\w:/\\\\.]*\\}";
		 Pattern pattern = Pattern.compile(patternStr);
		 String content = 
				"{\n" + 
		 		"	\"token\":#{access_token},\n" + 
		 		"	\"ID\":#{uid},\n" + 
		 		"	\"Birthday\":#{birthdate}\n" + 
		 		"}";
		 List<String> matchStrList = new ArrayList<String>();
		 Matcher matcher = pattern.matcher(content);
		 while(matcher.find()){
			 matchStrList.add(matcher.group());
			 System.out.println(matcher.group());
		 }
		 if(matchStrList!=null&&matchStrList.size()>0){
			 System.out.println("---------------------------");
			 for(String s:matchStrList){
				 //String forReplaceStr = "";
				 //String matchInnerStr = s.replaceAll("#{", "").replaceAll("}", "").trim();
				 System.out.println(s);
			 }
			 System.out.println("---------------------------");
		 }
	}

}
