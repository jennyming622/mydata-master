package test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.hash.Hashing;

public class testDigest {

	public static void main(String[] args) {
		String pdfPathStr = "/Users/mac/Desktop/mydata資料/tmp/en_20200115114435Rl204L0s0pK5G07gcF00.pdf";
		/**
		 * Google Hash sha256
		 */
        try {
            byte[] b = Files.readAllBytes(Paths.get(pdfPathStr));
            String sha256file = Hashing.sha256().hashBytes(b).toString();
            System.out.println("1. Google Hash sha256：\n" + Hashing.sha256().hashBytes(b).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		/**
		 * java.security.MessageDigest
		 */
        try {
            byte[] b = Files.readAllBytes(Paths.get(pdfPathStr));
    			MessageDigest digest = MessageDigest.getInstance("SHA-256");
    			byte[] encodedhash = digest.digest(b);
            System.out.println("2. java.security.MessageDigest：\n" + bytesToHex(encodedhash));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        
		/**
		 * commons-codec
		 */
        try {
            byte[] b = Files.readAllBytes(Paths.get(pdfPathStr));
	        String sha256hex2 = DigestUtils.sha256Hex(b);
	        System.out.println("3. commons-codec：\n" + sha256hex2);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
