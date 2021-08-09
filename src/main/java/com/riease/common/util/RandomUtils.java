/**
 * 
 */
package com.riease.common.util;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

import org.apache.commons.text.RandomStringGenerator;

/**
 * 產生隨機字串
 * @author wesleyzhuang
 *
 */
public class RandomUtils {
	//產生隨機英數字串
	private static RandomStringGenerator AlphanumericGenerator = new RandomStringGenerator.Builder()
	        .withinRange('0', 'z')
	        .filteredBy(LETTERS, DIGITS)
	        .build();
	
	private static RandomStringGenerator numericGenerator = new RandomStringGenerator.Builder()
	        .withinRange('0', '9')
	        .filteredBy(LETTERS, DIGITS)
	        .build();
	
	/**
	 * 隨機產生英數字串
	 * @param length		指定字串長度
	 * @return
	 */
	public static String alphaNumericString(int length) {
		return AlphanumericGenerator.generate(length);
	}
	
	public static String numericString(int length) {
		return numericGenerator.generate(length);
	}
	
	public static void main(String[] args) {
		RandomUtils ru = new RandomUtils();
		System.out.println(ru.alphaNumericString(10));
	}
}
