package test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

public class testStep4 {
	/**
	 * 密鑰算法
	 */
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * 加密/解密算法/工作模式/填充方式
	 */
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";
	
	/**
	 * AES，壓密解密
	 * @param args
	 * @throws Exception
	 * secret_key 0471f681fa334dba87c0d47fccd76a47
	 */
	public static void main(String[] args) throws Exception {
		String secret_key = "0471f681fa334dba87c0d47fccd76a47";
		byte[] b = Files.readAllBytes(Paths.get("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ.zip"));
		
		byte[] encryptb = encrypt(b,secret_key);
		FileUtils.writeByteArrayToFile(new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ.encrypt.zip"), encryptb);

		byte[] encryptb1 = Files.readAllBytes(Paths.get("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ.encrypt.zip"));
		
		byte[] decryptb = decrypt(encryptb1,secret_key);
		FileUtils.writeByteArrayToFile(new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ.decrypt.zip"), decryptb);
		//FileUtils.writeByteArrayToFile(new File("pathname"), myByteArray)
		
		String encodedString = encodeFileToBase64Binary("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ.encrypt.zip");
		System.out.println(encodedString);
		System.out.println("--length--:"+encodedString.length());
		decodeFileToBase64Binary(encodedString,"/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ.encrypt.base64.zip");
	}

	public static String encodeFileToBase64Binary(String fileName) throws Exception {
		byte[] bytes = Files.readAllBytes(Paths.get(fileName));
		String encodedString = Base64.getUrlEncoder().encodeToString(bytes);
		return encodedString;
	}
	
	public static String decodeFileToBase64Binary(String encodedString,String fileName) throws Exception {
		byte[] b = Base64.getUrlDecoder().decode(encodedString.getBytes("UTF-8"));
		FileUtils.writeByteArrayToFile(new File(fileName), b);
		return encodedString;
	}
	
	
	
	
	
	/**
	* 轉換密鑰
	* @param key 二進制密鑰
	* @return Key 密鑰
	*/
	public static Key toKey(String key) throws Exception {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(key);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return originalKey;
	}
	
	/**
	* 加密數據
	* @param data 待加密數據
	* @param key 密鑰
	* @return byte[] 加密後的數據
	*/
	public static byte[] encrypt(byte[] data, String key) throws Exception {
		// 還原密鑰
		Key k = toKey(key);
		// 實例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，設置為加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);
		// 執行操作
		return cipher.doFinal(data);
	}	
	
	public static File encrypt(File sourceFile, String key) throws Exception {
		File encryptFiles=null;
		Path path = Paths.get(sourceFile.getAbsolutePath());
		byte[] data = Files.readAllBytes(path);
		// 還原密鑰
		Key k = toKey(key);
		// 實例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，設置為加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);
		// 執行操作
		byte[] encryptByres =cipher.doFinal(data);
		FileUtils.writeByteArrayToFile(new File("pathname"), encryptByres);
		
		return encryptFiles;
	}
	
	/**
	* 解密數據
	* @param data 待解密數據
	* @param key 密鑰
	* @return byte[] 解密後的數據
	*/
	public static byte[] decrypt(byte[] data, String key) throws Exception {
		// 歡迎密鑰
		Key k = toKey(key);
		// 實例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，設置為解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);
		// 執行操作
		return cipher.doFinal(data);
	}
	
	public static File decrypt(File sourceFile, String key) throws Exception {
		File encryptFiles=null;
		Path path = Paths.get(sourceFile.getAbsolutePath());
		byte[] data = Files.readAllBytes(path);
		// 歡迎密鑰
		Key k = toKey(key);
		// 實例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，設置為解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);
		// 執行操作
		byte[] encryptByres =cipher.doFinal(data);
		FileUtils.writeByteArrayToFile(new File("pathname"), encryptByres);
		return encryptFiles;
	}		
	
	
}
