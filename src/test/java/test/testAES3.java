package test;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class testAES3 {
	/**
	 * 密鑰算法
	 */
	public static final String KEY_ALGORITHM = "AES";
	/**
	 * 加密/解密算法/工作模式/填充方式
	 */
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";
	
	public static void main(String[] args) throws UnsupportedEncodingException, Exception {
		String secretKey = "KvloHv1DS3lMe53nKvloHv1DS3lMe53n";
		String uid = "H120978258";
		System.out.println(uid);
		byte[] encryptUid = encrypt(uid.getBytes("UTF-8"),secretKey);
		String encryptUidStr = Base64.getUrlEncoder().encodeToString(encryptUid);
		System.out.println(encryptUidStr);
		byte[] decryptUid = decrypt(Base64.getUrlDecoder().decode(encryptUidStr.getBytes("UTF-8")),secretKey);
		System.out.println(new String(decryptUid,"UTF-8"));
	}
	/**
	* 轉換密鑰
	* @param key 二進制密鑰
	* @return Key 密鑰
	*/
	public static Key toKey(String key) throws Exception {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getUrlDecoder().decode(key);
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
	
}
