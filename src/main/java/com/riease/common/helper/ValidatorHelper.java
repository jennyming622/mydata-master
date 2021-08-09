package com.riease.common.helper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.gov.ndc.emsg.mydata.Config;

public class ValidatorHelper {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static Map<String,String> map = getClearStringCompareMap();
	public static String removeSpecialCharacters(String str) {
//		return str;
		String resultStr = "";
		if (str == null) {
			return null;
		}
		for (char c : str.toCharArray()) {
			String s = String.valueOf(c);
			if (map.get(s) != null) {
				resultStr += map.get(s);
			}
		}
		return resultStr;
	}
	
	public static String removeSpecialCharactersForXss(String str) {
//		return str;
		String resultStr = "";
		Map<String,String> map = getClearStringCompareMapForXss();
		if (str == null) {
			return null;
		}
		for (char c : str.toCharArray()) {
			String s = String.valueOf(c);
			if (map.get(s) != null) {
				resultStr += map.get(s);
			}
		}
		return resultStr;
	}	
	
	public static String removeSpecialLimitNumber(String str) {
//		return str;
		String resultStr = "";
		Map<String,String> map = getClearStringNumber();
		if (str == null) {
			return null;
		}
		for (char c : str.toCharArray()) {
			String s = String.valueOf(c);
			if (map.get(s) != null) {
				resultStr += map.get(s);
			}
		}
		return resultStr;
	}
	
	public static Integer limitNumber(Integer input) {
//		return input;
		Integer result = 0;
		String resultStr = "";
		Map<String,String> map = getClearStringNumber();
		if (input == null) {
			return null;
		}
		for (char c : input.toString().toCharArray()) {
			String s = String.valueOf(c);
			if (map.get(s) != null) {
				resultStr += map.get(s);
			}
		}
		if(resultStr.trim().length()>0) {
			result = Integer.valueOf(resultStr);
		}
		return result;
	}
	
	public static Long limitNumber(Long input) {
//		return input;
		long result = 0;
		String resultStr = "";
		Map<String,String> map = getClearStringNumber();
		if (input == null) {
			return null;
		}
		for (char c : input.toString().toCharArray()) {
			String s = String.valueOf(c);
			if (map.get(s) != null) {
				resultStr += map.get(s);
			}
		}
		if(resultStr.trim().length()>0) {
			result = Long.valueOf(resultStr);
		}
		return result;
	}	
	
	public static Date limitDate(Date input) {
//		return input;
		Date result = new Date();
		String resultStr = "";
		Map<String,String> map = getClearStringDate();
		if (input == null) {
			return null;
		}
		String inputStr = sdf.format(input);
		for (char c : inputStr.toCharArray()) {
			String s = String.valueOf(c);
			if (map.get(s) != null) {
				resultStr += map.get(s);
			}
		}
		if(resultStr.trim().length()>0) {
			try {
				result = sdf.parse(resultStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static String removeSpecialLimitNumberAndAlphabet (String str) {
//		return str;
		String resultStr = "";
		Map<String,String> map = getClearStringNumberAndAlphabet();
		if (str == null) {
			return null;
		}
		for (char c : str.toCharArray()) {
			String s = String.valueOf(c);
			if (map.get(s) != null) {
				resultStr += map.get(s);
			}
		}
		return resultStr;
	}		
	
	private static Map<String,String> getClearStringCompareMap() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		map.put("d", "d");
		map.put("e", "e");
		map.put("f", "f");
		map.put("g", "g");
		map.put("h", "h");
		map.put("i", "i");
		map.put("j", "j");
		map.put("k", "k");
		map.put("l", "l");
		map.put("m", "m");
		map.put("n", "n");
		map.put("o", "o");
		map.put("p", "p");
		map.put("q", "q");
		map.put("r", "r");
		map.put("s", "s");
		map.put("t", "t");
		map.put("u", "u");
		map.put("v", "v");
		map.put("w", "w");
		map.put("x", "x");
		map.put("y", "y");
		map.put("z", "z");
		map.put("A", "A");
		map.put("B", "B");
		map.put("C", "C");
		map.put("D", "D");
		map.put("E", "E");
		map.put("F", "F");
		map.put("G", "G");
		map.put("H", "H");
		map.put("I", "I");
		map.put("J", "J");
		map.put("K", "K");
		map.put("L", "L");
		map.put("M", "M");
		map.put("N", "N");
		map.put("O", "O");
		map.put("P", "P");
		map.put("Q", "Q");
		map.put("R", "R");
		map.put("S", "S");
		map.put("T", "T");
		map.put("U", "U");
		map.put("V", "V");
		map.put("W", "W");
		map.put("X", "X");
		map.put("Y", "Y");
		map.put("Z", "Z");
		map.put("<", "<");
		map.put(">", ">");
		map.put("(", "(");
		map.put(")", ")");
		map.put("{", "{");
		map.put("}", "}");
		map.put("\"", "\"");
		map.put(".", ".");
		map.put(",", ",");
		map.put("+", "+");
		map.put("-", "-");
		map.put("=", "=");
		map.put("_", "_");
		map.put("!", "!");
		map.put("@", "@");
		map.put("#", "#");
		map.put("%", "%");
		map.put("$", "$");
		map.put(":", ":");
		map.put("&", "&");
		map.put(";", ";");
		map.put("?", "?");
		map.put("[", "[");
		map.put("]", "]");
		map.put("~", "~");
		map.put("*", "*");
		map.put(" ", " ");
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		map.put("6", "6");
		map.put("7", "7");
		map.put("8", "8");
		map.put("9", "9");
		map.put("0", "0");
		map.put("\n", "\n");
		map.put(Config.char1, Config.char1);
		map.put(Config.char2, Config.char2);
		map.put(Config.char3, Config.char3);
		map.put(Config.char4, Config.char4);
		map.put(Config.char5, Config.char5);
		map.put(Config.char6, Config.char6);
		map.put(Config.char7, Config.char7);
		map.put(Config.char8, Config.char8);
		map.put(Config.char9, Config.char9);
		map.put(Config.char10, Config.char10);
		map.put(Config.char11, Config.char11);
		map.put(Config.char12, Config.char12);
		map.put(Config.char13, Config.char13);
		map.put(Config.char14, Config.char14);
		map.put(Config.char15, Config.char15);
		map.put(Config.char16, Config.char16);
		map.put(Config.char17, Config.char17);
		map.put(Config.char18, Config.char18);
		map.put(Config.char19, Config.char19);
		map.put(Config.char20, Config.char20);
		map.put(Config.char21, Config.char21);
		map.put(Config.char22, Config.char22);
		map.put(Config.char23, Config.char23);
		map.put(Config.char24, Config.char24);
		map.put(Config.char25, Config.char25);
		map.put(Config.char26, Config.char26);
		map.put(Config.char27, Config.char27);
		map.put(Config.char28, Config.char28);
		map.put(Config.char29, Config.char29);
		map.put(Config.char30, Config.char30);
		map.put(Config.char31, Config.char31);
		for(int i=19968;i<40870;i++) {
			map.put(unicodeToStringOnlyInteger(i), unicodeToStringOnlyInteger(i));
		}
		return map;
	}	
	
	private static Map<String,String> getClearStringCompareMapForXss() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		map.put("d", "d");
		map.put("e", "e");
		map.put("f", "f");
		map.put("g", "g");
		map.put("h", "h");
		map.put("i", "i");
		map.put("j", "j");
		map.put("k", "k");
		map.put("l", "l");
		map.put("m", "m");
		map.put("n", "n");
		map.put("o", "o");
		map.put("p", "p");
		map.put("q", "q");
		map.put("r", "r");
		map.put("s", "s");
		map.put("t", "t");
		map.put("u", "u");
		map.put("v", "v");
		map.put("w", "w");
		map.put("x", "x");
		map.put("y", "y");
		map.put("z", "z");
		map.put("A", "A");
		map.put("B", "B");
		map.put("C", "C");
		map.put("D", "D");
		map.put("E", "E");
		map.put("F", "F");
		map.put("G", "G");
		map.put("H", "H");
		map.put("I", "I");
		map.put("J", "J");
		map.put("K", "K");
		map.put("L", "L");
		map.put("M", "M");
		map.put("N", "N");
		map.put("O", "O");
		map.put("P", "P");
		map.put("Q", "Q");
		map.put("R", "R");
		map.put("S", "S");
		map.put("T", "T");
		map.put("U", "U");
		map.put("V", "V");
		map.put("W", "W");
		map.put("X", "X");
		map.put("Y", "Y");
		map.put("Z", "Z");
		map.put(".", ".");
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		map.put("6", "6");
		map.put("7", "7");
		map.put("8", "8");
		map.put("9", "9");
		map.put("0", "0");
		for(int i=19968;i<40870;i++) {
			map.put(unicodeToStringOnlyInteger(i), unicodeToStringOnlyInteger(i));
		}
		return map;
	}	
	
	private static Map<String,String> getClearStringNumber() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		map.put("6", "6");
		map.put("7", "7");
		map.put("8", "8");
		map.put("9", "9");
		map.put("0", "0");
		return map;
	}
	
	private static Map<String,String> getClearStringDate() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		map.put("6", "6");
		map.put("7", "7");
		map.put("8", "8");
		map.put("9", "9");
		map.put("0", "0");
		map.put("-", "-");
		map.put(" ", " ");
		map.put(":", ":");
		return map;
	}
	
	private static Map<String,String> getClearStringNumberAndAlphabet() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		map.put("d", "d");
		map.put("e", "e");
		map.put("f", "f");
		map.put("g", "g");
		map.put("h", "h");
		map.put("i", "i");
		map.put("j", "j");
		map.put("k", "k");
		map.put("l", "l");
		map.put("m", "m");
		map.put("n", "n");
		map.put("o", "o");
		map.put("p", "p");
		map.put("q", "q");
		map.put("r", "r");
		map.put("s", "s");
		map.put("t", "t");
		map.put("u", "u");
		map.put("v", "v");
		map.put("w", "w");
		map.put("x", "x");
		map.put("y", "y");
		map.put("z", "z");
		map.put("A", "A");
		map.put("B", "B");
		map.put("C", "C");
		map.put("D", "D");
		map.put("E", "E");
		map.put("F", "F");
		map.put("G", "G");
		map.put("H", "H");
		map.put("I", "I");
		map.put("J", "J");
		map.put("K", "K");
		map.put("L", "L");
		map.put("M", "M");
		map.put("N", "N");
		map.put("O", "O");
		map.put("P", "P");
		map.put("Q", "Q");
		map.put("R", "R");
		map.put("S", "S");
		map.put("T", "T");
		map.put("U", "U");
		map.put("V", "V");
		map.put("W", "W");
		map.put("X", "X");
		map.put("Y", "Y");
		map.put("Z", "Z");
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		map.put("6", "6");
		map.put("7", "7");
		map.put("8", "8");
		map.put("9", "9");
		map.put("0", "0");
		return map;
	}
	
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch+"" );
        }
        return str;
    }
    
    public static String unicodeToStringOnlyInteger(int wi) {
    	String str = Config.char2+"u"+Integer.toHexString(wi);
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch+"" );
        }
        return str;
    }   
    
    //檢查是否有中文字
    public static boolean containsHanScript(String s) {
        return s.codePoints().anyMatch(codepoint -> Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
    }
	
	public static void main(String[] args) {
		String title = "123-456sg////*@你好、【";
		System.out.println(removeSpecialCharacters(title));
//		System.out.println(containsHanScript(title));
		
	}
}
