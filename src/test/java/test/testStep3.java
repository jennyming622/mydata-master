package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import com.google.common.hash.Hashing;

public class testStep3 {
	public final static String ALGORITHM = "RSA";
	public final static String SIGNATURE_ALGORITHM = "MD5withRSA";
	
	public static void main(String[] args) throws Exception {
		String jksPath = "/Users/mac/mydata.jks"; // jks file path
		String jksPassword = "mydata1234"; // jks keyStore password
		String certAlias = "mydata.nat.gov.tw"; // cert alias
		String certPassword = "mydata1234"; // cert password
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(jksPath), jksPassword.toCharArray());
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(certAlias, certPassword.toCharArray());
		PublicKey publicKey = keyStore.getCertificate(certAlias).getPublicKey();		
		byte[] b = Files.readAllBytes(Paths.get("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz/META-INFO/manifest.xml"));
		System.out.println("SHA256-0 file：" + Hashing.sha256().hashBytes(b).toString());
		
		String signAlg = "SHA256withRSA"; // 簽名類型
		Signature signature = Signature.getInstance(signAlg);
		signature.initSign(privateKey);
		signature.update(b);
		String signedData = Base64.getEncoder().encodeToString(signature.sign()).replace("\r\n", "").replace("\n", "");
		System.out.println("簽名     ：" + signedData);
		//signature.sha256withrsa
		String signatureContent = readFile("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz/META-INFO/signature.sha256withrsa");
		System.out.println("簽名檔內容：" + signatureContent);
		
		//Signature signature = Signature.getInstance(signAlg);
		signature.initVerify(getCAPublicKey());
		signature.update(b);
		boolean isVerify = signature.verify(Base64.getDecoder().decode(signedData));
		System.out.println("CA驗簽：" + isVerify);
	}
	/**
	 * 使用公鑰進行加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data, PublicKey k) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		byte[] bytes = cipher.doFinal(data.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * 使用私鑰進行加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(String data, PrivateKey k) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		byte[] bytes = cipher.doFinal(data.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * 使用密鑰進行解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data, PrivateKey k) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);

		byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(data));

		return new String(bytes, "UTF-8");
	}

	/**
	 * 使用公鑰進行解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPublicKey(String data, PublicKey k) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(data));
		return new String(bytes, "UTF-8");
	}

	/**
	 * getCAPublickKey
	 * 
	 * @return
	 * @throws Exception
	 */
	private static PublicKey getCAPublicKey() throws Exception {
		String keyStr = "";
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(new FileInputStream("/Users/mac/mydatanatgovtw.crt"));
			keyStr = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
			// System.out.println(Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyStr));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		PublicKey k = keyFactory.generatePublic(keySpec);
		return k;
	}
	
	
	public static String readFile(String file) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");
	    try {
	        while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }
	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
}
