package test;

import java.util.regex.Pattern;

public class TestRegex {

	
	public static void main(String[] args) {
		String regex = "[ 0-9A-Za-z#$%=@!{},`~&*()'<>?.:;_|^/+\\t\\r\\n\\[\\]\"-]*";
		boolean b = Pattern.matches(regex, " !@#123$%^adv{}|");
		System.out.println("............. "+b);
		
	}

}
