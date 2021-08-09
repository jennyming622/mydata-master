package test;

import java.util.Arrays;
import java.util.List;

public class testContains {

	public static void main(String[] args) {
		List<String> resourceIdSpecList = Arrays.asList("API.vTL7cCtoSl", "API.mBqP4awHJY");
		System.out.println(resourceIdSpecList.contains("API.vTL7cCtoSl"));
		System.out.println(resourceIdSpecList.contains("API.xxxxx"));
		System.out.println(resourceIdSpecList.contains("API.mBqP4awHJY"));
	}

}
