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

import test.testSort.tcomparator;

public class testSort2 {
	
	public static Map<String,String> map = new HashMap<String,String>();
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f1 = new File("/Users/mac/Desktop/tmp/13000.1.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(f1))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	String[] s1 = line.split("[,]+");
		    	map.put(s1[1], s1[2]);
		    }
		}
		List<String> list = new ArrayList<String>(); 
		list.add("中");
		list.add("王");
		list.add("玉");
		list.add("台");
		list.add("永");
		list.add("行");
		list.add("兆");
		list.add("花");
		list.add("科");
		list.add("桃");
		list.add("教");
		list.add("國");
		list.add("第");
		list.add("雲");
		list.add("道");
		list.add("渣");
		list.add("新");
		list.add("遠");
		list.add("經");
		list.add("嘉");
		list.add("臺");
		list.add("彰");
		list.add("澎");
		list.add("衛");

		for(String s:list) {
			System.out.println(s+" - "+ map.get(s));
		}
		
	}
}
