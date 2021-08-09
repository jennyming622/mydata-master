package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class testSort4 {
	private static Map<String, Integer> chineseCharacterMap = new HashMap<String, Integer>();
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f = new File("/Users/mac/Desktop/tmp/13000.1.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	String[] s1 = line.split("[,]+");
		    	chineseCharacterMap.put(s1[1], Integer.valueOf(s1[2]));
		    }
		}
		List<String> list = new ArrayList<String>();
//		list.add("信用卡申請補件服務");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請(註：一站式認證模式)");
//		list.add("信用卡線上申辦補件服務");
//		list.add("信用卡額度調整申請補件服務");
//		list.add("信貸線上申請");
//		list.add("信貸線上申請");
//		list.add("信貸線上申請");
//		list.add("信貸線上申請加值服務");
//		list.add("個人信貸線上申請");
//		list.add("卡友貸申請補件服務");
//		list.add("客戶資料維護");
//		list.add("就學貸款申請(上學期8月1日至9月底；下學期1月15至2月底)");
//		list.add("房貸線上申請");
//		list.add("房貸線上申請");
//		list.add("數位存款帳戶開戶");
//		list.add("既有房貸增貸(本行網銀客戶)線上申請");
//		list.add("未成年人線上開立數位帳戶服務");
//		list.add("未成年人線上開立數位帳戶補傳資料服務");
//		list.add("永久額度調整");
//		list.add("測試");
//		list.add("發大財申請");
//		list.add("線上申請補件服務");
//		list.add("貸款業務申請");
//		list.add("車貸線上申請");
		
		list.add("永久額度調整");
		list.add("卡友貸申請補件服務");
		
		for(String s:list) {
			System.out.println(s);
		}
		System.out.println("------------------------");
		Collections.sort(list,serviceNameStrokeComparator);
		for(String s:list) {
			System.out.println(s);
			for(int i =0;i< s.length();i++) {
				System.out.print(chineseCharacterMap.get(s.substring(i, i+1))+" ");
			}
			System.out.println();
		}		
	}
	
    /**
     * 根據服務名稱中文筆畫
     */
    private static Comparator<String> serviceNameStrokeComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
        	int o1namelen = o1.length();
        	int o2namelen = o2.length();
        	String o1name = o1;
        	String o2name = o2;
        	boolean check = true;
        	int i = 0;
        	int returnFlag = 0;
        	while(check) {
        		if(i > (o1namelen-1) || i > (o2namelen-1)) {
        			//break
        			check = false;
        			if(o1namelen==o2namelen) {
        				returnFlag = 0;
        			}else {
        				if(o1namelen < o2namelen) {
        					returnFlag = -1;
        				}else {
        					returnFlag = 1;
        				}
        			}
        		}
        		if(check) {
        			if(chineseCharacterMap.get(o1name.substring(i, i+1))==null||chineseCharacterMap.get(o2name.substring(i, i+1))==null) {
        				check = false;
        				if(chineseCharacterMap.get(o1name.substring(i, i+1))==null&&chineseCharacterMap.get(o2name.substring(i, i+1))==null) {
        					returnFlag = 0;
        				}else if(chineseCharacterMap.get(o1name.substring(i, i+1))==null) {
        					returnFlag = -1;
        				}else {
        					returnFlag = 1;
        				}
        			}else {
                		if(chineseCharacterMap.get(o1name.substring(i, i+1)).compareTo(chineseCharacterMap.get(o2name.substring(i, i+1)))==0) {
                			//UNDO
                		} else {
                			//break
                			check = false;
                			if(chineseCharacterMap.get(o1name.substring(i, i+1)).compareTo(chineseCharacterMap.get(o2name.substring(i, i+1))) < 0) {
                				returnFlag = -1;
                			}else {
                				returnFlag = 1;
                			}
                		} 
        			}       			
        		}
        		i = i + 1;
        	}
        	return returnFlag;
        }
    };
    
}
