package test;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;

import tw.gov.ndc.emsg.mydata.web.SessionMember;
import tw.gov.ndc.emsg.mydata.web.UserInfoRestController;

public class testEncryptByPublicKey {
    public final static String ALGORITHM = "RSA";
    //public final static String ALGORITHM = "RSA/ECB/OAEPWithMD5AndMGF1Padding";
    public final static String SIGNATURE_ALGORITHM = "MD5withRSA";
	public static void main(String[] args) throws Exception {		
		//UserInfoRestController.encryptByPublicKey
        //String uuid = UUID.randomUUID().toString();
        String publicKeyStr = getPublicKeyStr();
        System.out.println("公鑰：" + publicKeyStr);
        String content = "F128205045" + ";" + "1ee65a4b-a503-4406-9159-461781545269";
        String encryptedData = encryptByPublicKey(content);
        System.out.println("加密後：" + encryptedData);
        System.out.println("加密後URLEncode：" + URLEncoder.encode(encryptedData, "UTF-8"));
        String nhicardverifyurltemp = "https://www.cp.gov.tw/portal/NHICardVerify.aspx?successUrl=https://mydata.nat.gov.tw/&toVerify=" + URLEncoder.encode(encryptedData, "UTF-8");
        System.out.println("== nhicardverifyurltemp ==:" + nhicardverifyurltemp);
	}
	
    public static String encryptByPublicKey(String data) throws Exception {

        Key k = getPublicKey();

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithMD5AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, k);

        byte[] bytes = cipher.doFinal(data.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(bytes);
    }	
	
    private static Key getPublicKey() throws Exception {
        String keyStr = "";
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(new FileInputStream("/Users/mac/keystore/NDC.cer"));
            keyStr = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
            //System.out.println(Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key k = keyFactory.generatePublic(keySpec);
        return k;
    } 
    
    public static String getPublicKeyStr() throws Exception {
        String keyStr = "";
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(new FileInputStream("/Users/mac/keystore/NDC.cer"));
            keyStr = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
            //System.out.println(Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return keyStr;
    }
}
