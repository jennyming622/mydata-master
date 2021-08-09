package test;

import java.lang.reflect.Array;

public class testArrayCheck {

	public static void main(String[] args) {
		boolean[] checkArray = new boolean[3]; 
		checkArray[0] = true;
		checkArray[1] = false;
		checkArray[2] = true;
		
		for(int i=0;i<checkArray.length;i++) {
			System.out.println(checkArray[i]);
		}
	}

}
