package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class emsgF03Test {

	public static void main(String[] args) {
		File f1 = new File("/Users/mac/Desktop/emsg/data_final_6_data2.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(f1))) {
			 String line;
			 String keyLineTmp;
			 String keyLine1Tmp;
			 int total = 0;
			 while ((line = br.readLine()) != null) {
				total = total+1;
			    	System.out.println(line);
			    	String[] s1 = line.split("[\\s]+");
			    	System.out.println(s1[0]+"-"+s1[1]+"-"+s1[2]+","+s1[3]+"-"+s1[4]);
				String patternStr = "101:\\[[\\s\\S]*\\]";
				Pattern pattern = Pattern.compile(patternStr);
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					keyLineTmp = matcher.group();
					System.out.println(keyLineTmp);
					System.out.println("------------------------------");
					String[] s2 = keyLineTmp.split("\\sK");
					for(String s2tmp:s2) {
						//System.out.println(s2tmp);
						String[] s3 = s2tmp.split("[:]");
						System.out.println(s3[1]);
						String[] s4 = s3[1].split("[,]");
						for(String s3tmp:s4) {
							/**
							 * 中文:[\u4e00-\u9fa5] 
							 * 英文字母:[a-zA-Z] 
							 * 数字:[0-9] 
							 * 匹配中文，英文字母和数字及_:^[\u4e00-\u9fa5_a-zA-Z0-9]+$
							 */
							String patternStr1 = "\\'[\\s\\S^\\,]+\\'";
							Pattern pattern1 = Pattern.compile(patternStr1);
							Matcher matcher1 = pattern1.matcher(s3tmp);
							System.out.println("sssssssssssssss");
							while (matcher1.find()) {
								keyLine1Tmp = matcher1.group();
								System.out.println(keyLine1Tmp.replaceAll("'", ""));
							}
						System.out.println("eeeeeeeeeeeeee");
						}
					}
				}
				System.out.println("==================================");
			 }
			 System.out.println(total);
		}catch(Exception ex) {
			System.out.println(ex);
		}

	}

}
