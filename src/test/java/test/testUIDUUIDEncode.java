package test;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class testUIDUUIDEncode {
	private static Base64.Encoder encoder = Base64.getEncoder();
	private static Base64.Decoder decoder = Base64.getDecoder();
	private static String keystorePublicCrypto = "/Users/mac/Desktop/Eclipse_Work/workspace_oxygen/mydata/src/main/java/NDC.cer";
    public final static String ALGORITHM = "RSA";
    public final static String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String ENCODE_ALGORITHM = "SHA-256";
	public static void main(String[] args) throws Exception {
		String publicKeyStr = getPublicKeyStr();
		System.out.println("公鑰：" + publicKeyStr);
		String datastr ="H120983053;f37d41c1-f4de-4b35-9e37-3885057a8091";
		System.out.println("toVerify=" + encryptByPublicKey(datastr));	
	}
	
	private static PublicKey getPublicKey() throws Exception {
		String keyStr = "";
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(new FileInputStream(keystorePublicCrypto));
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
	
    public static String getPublicKeyStr() throws Exception{
		String keyStr = "";
		try{
		    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		    Certificate cert = cf.generateCertificate(new FileInputStream(keystorePublicCrypto));		    
		    keyStr = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
		    //System.out.println(Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded()));
		}catch(Exception ex){
		    ex.printStackTrace();
		}
        return keyStr;
    }	
	
    public static String encryptByPublicKey(String data)throws Exception{
        
        Key k = getPublicKey();
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        
        byte[] bytes = cipher.doFinal(data.getBytes("UTF-8"));
        
        return Base64.getEncoder().encodeToString(bytes);
    }
}
