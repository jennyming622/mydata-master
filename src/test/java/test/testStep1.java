package test;

import java.io.FileInputStream;
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

/**
 * 驗證加密解密驗簽
 * @author mac
 *
 */
public class testStep1 {

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

		System.out.println("私鑰：" + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
		System.out.println("公鑰：" + Base64.getEncoder().encodeToString(publicKey.getEncoded()));

		String signData = "KhlrhQfmeux/pLUmDd8Hc7kI8ZRRMffN"; // 需要签名的内容
		String signAlg = "SHA256withRSA"; // 簽名類型
		Signature signature = Signature.getInstance(signAlg);
		signature.initSign(privateKey);
		signature.update(signData.getBytes());
		String signedData = Base64.getEncoder().encodeToString(signature.sign()).replace("\r\n", "").replace("\n", "");
		System.out.println("簽名：" + Base64.getEncoder().encodeToString(privateKey.getEncoded()));

		signature.initVerify(publicKey);
		signature.update(signData.getBytes());
		boolean isVerify = signature.verify(Base64.getDecoder().decode(signedData));
		System.out.println("驗簽：" + isVerify);

		signature.initVerify(getCAPublicKey());
		signature.update(signData.getBytes());
		isVerify = signature.verify(Base64.getDecoder().decode(signedData));
		System.out.println("CA驗簽：" + isVerify);

		String data = "KhlrhQfmeux/pLUmDd8Hc7kI8ZRRMffN";
		System.out.println("原 始：" + data);
		System.out.println("---------------公鑰加密，私鑰解密-----------------");
		String encryptedData = encryptByPublicKey(data, publicKey);
		System.out.println("加密後：" + encryptedData);
		String decryptedData = decryptByPrivateKey(encryptedData, privateKey);
		System.out.println("解密後：" + decryptedData);
		System.out.println("---------------私鑰加密，公鑰解密-----------------");
		encryptedData = encryptByPrivateKey(data, privateKey);
		System.out.println("加密後：" + encryptedData);
		decryptedData = decryptByPublicKey(encryptedData, publicKey);
		System.out.println("解密後：" + decryptedData);

		System.out.println("---------------私鑰加密，CA公鑰解密-----------------");
		encryptedData = encryptByPrivateKey(data, privateKey);
		System.out.println("加密後：" + encryptedData);
		decryptedData = decryptByPublicKey(encryptedData, getCAPublicKey());
		System.out.println("解密後：" + decryptedData);

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
}
