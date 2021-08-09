package test;

import java.util.UUID;

public class testRandomUUID {

	public static void main(String[] args) {
		String uuidStr = UUID.randomUUID().toString();
		System.out.println(uuidStr);
		System.out.println(uuidStr.replaceAll("-", ""));
	}

}
