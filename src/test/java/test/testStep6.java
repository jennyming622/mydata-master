package test;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.google.common.hash.Hashing;

public class testStep6 {
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
		
		/**
		 * 
		 */
		String signedData = readFileAsString("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz/META-INFO/signature.sha256withrsa");
		String signAlg = "SHA256withRSA"; // 簽名類型
		Signature signature = Signature.getInstance(signAlg);
		//Signature signature = Signature.getInstance(signAlg);
		signature.initVerify(getCAPublicKey());
		signature.update(b);		
		boolean isVerify = signature.verify(Base64.getDecoder().decode(signedData));
		System.out.println("CA驗簽：" + isVerify);
	}
	
	public static String readFileAsString(String fileName) throws Exception {
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
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
