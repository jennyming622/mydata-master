package test;

import java.util.ArrayList;
import java.util.List;

public class testContainInteger {

	public static void main(String[] args) {
		List<Integer> forFinalPrIdCheck = new ArrayList<Integer>();
		forFinalPrIdCheck.add(Integer.valueOf(2));
		for(int i=0;i<5;i++) {
			if(!forFinalPrIdCheck.contains(i)) {
				forFinalPrIdCheck.add(i);
				System.out.println("add "+i);
			}
		}
		//
		for(Integer i:forFinalPrIdCheck) {
			System.out.println(i);
		}
		
	}

}
