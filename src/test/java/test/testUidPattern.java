package test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testUidPattern {

	public static void main(String[] args) {
		String paramstr = "H120983053";
		String patternStr = "[A-Z]{1}[0-9]{9}";
		Pattern pattern = Pattern.compile(patternStr);
		List<String> matchStrList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(paramstr);
		if( matcher.matches() ){
			System.out.println(paramstr +" is true!");
		}else {
			System.out.println(paramstr +" is false!");
		}

	}

}
