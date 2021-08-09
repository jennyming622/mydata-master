package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class testSort1 {
	public static Map<String,String> map = new HashMap<String,String>();
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f = new File("/Users/mac/Desktop/tmp/13000.1.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	String[] s1 = line.split("[,]+");
		    	map.put(s1[1], s1[2]);
		    }
		}
		File f1 = new File("/Users/mac/Desktop/tmp/w1.csv");
		List<String> docList = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	docList.add(line);
		    }
		}
		
	}
	
	public static class tcomparator implements Comparator<String> {
		public int compare(String obj1, String obj2) {
			System.out.println(obj1.substring(0, 1)+" - "+ map.get(obj1.substring(0, 1)));
			System.out.println(obj2.substring(0, 1)+" - "+ map.get(obj2.substring(0, 1)));
			int value1 = Integer.valueOf(map.get(obj1.substring(0, 1))).compareTo((Integer.valueOf(map.get(obj2.substring(0, 1)))));
			return value1;
		}
	}	
}
