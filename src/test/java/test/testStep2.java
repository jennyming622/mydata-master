package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.hash.Hashing;

/**
 * 產生打包檔
 * 
 * @author mac
 *
 */
public class testStep2 {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		String data = "KhlrhQfmeux/pLUmDd8Hc7kI8ZRRMffN";
		/**
		 * com.google.guava
		 */
		String sha256hex = Hashing.sha256().hashString(data, StandardCharsets.UTF_8).toString();
		System.out.println("原始     ：" + data);
		System.out.println("SHA256-0：" + sha256hex);
		/**
		 * java.security.MessageDigest
		 */
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

		System.out.println("SHA256-1：" + bytesToHex(encodedhash));
		/**
		 * commons-codec
		 */
		String sha256hex2 = DigestUtils.sha256Hex(data);
		System.out.println("SHA256-2：" + sha256hex2);
		
		byte[] b = Files.readAllBytes(Paths.get("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz/person.json"));
		System.out.println("SHA256-0 file：" + Hashing.sha256().hashBytes(b).toString());
		encodedhash = digest.digest(b);
		System.out.println("SHA256-1 file：" + bytesToHex(encodedhash));
		sha256hex2 = DigestUtils.sha256Hex(b);
		System.out.println("SHA256-2 file：" + sha256hex2);
		System.out.println("--------------------------------");
		byte[] b1 = Files.readAllBytes(Paths.get("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz/person.pdf"));
		System.out.println("SHA256-0 file：" + Hashing.sha256().hashBytes(b1).toString());
		encodedhash = digest.digest(b1);
		System.out.println("SHA256-1 file：" + bytesToHex(encodedhash));
		sha256hex2 = DigestUtils.sha256Hex(b1);
		System.out.println("SHA256-2 file：" + sha256hex2);
		
		
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public byte[] bEad(String filename) {
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(filename, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] b = new byte[0];
		try {
			b = new byte[(int) f.length()];
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			f.readFully(b);
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;

	}
}
