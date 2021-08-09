package test;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class testPid {
	/**
	 * 密鑰算法
	 */
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * 加密/解密算法/工作模式/填充方式
	 */
	public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
	/**
	 * 測試機sp-example服務，cbc_iv CBC壓密向量值
	 */
	public static final String cbc_iv = "Vz91MSW9Gnja9VPG";
	
	public static void main(String[] args) throws UnsupportedEncodingException, Exception {
		//String secret_key = "LQoJEvgdKug20RX4LQoJEvgdKug20RX4";
		String secret_key = "0oHhV7sbmjFg4HG90oHhV7sbmjFg4HG9";
		String pid="A123456789";
		//c69ac7b1-a075-45fb-ba62-36aefa442df5
		//String pid="c69ac7b1-a075-45fb-ba62-36aefa442df5";
		/**
		 * 壓密 AES
		 */
		byte[] bs = encrypt(pid.getBytes("UTF-8"),secret_key,cbc_iv);
		System.out.println(Base64.getEncoder().encodeToString(bs));
		/**
		 * 去壓密 AES
		 */
		byte[] b = decrypt(bs,secret_key,cbc_iv);
		System.out.println(new String(b,"UTF-8"));
	}
	/**
	 * 讀取文字檔
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String readFileAsString(String fileName) throws Exception {
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
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
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, KEY_ALGORITHM);
		return originalKey;
	}
	
	/**
	* 加密數據
	* @param data 待加密數據
	* @param key 密鑰
	* @return byte[] 加密後的數據
	*/
	public static byte[] encrypt(byte[] data, String key, String ivstr) throws Exception {
		// 密鑰
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
		// 向量鰎值
		IvParameterSpec iv = new IvParameterSpec(ivstr.getBytes("UTF-8"));
		// 實例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，設置為加密模式
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		// 執行操作
		return cipher.doFinal(data);
	}
	
	/**
	* 解密數據
	* @param data 待解密數據
	* @param key 密鑰
	* @return byte[] 解密後的數據
	*/
	public static byte[] decrypt(byte[] data, String key, String ivstr) throws Exception {
		// 密鑰
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
		// 向量鰎值
		IvParameterSpec iv = new IvParameterSpec(ivstr.getBytes("UTF-8"));
		// 實例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，設置為解密模式
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		// 執行操作
		return cipher.doFinal(data);
	}
}
