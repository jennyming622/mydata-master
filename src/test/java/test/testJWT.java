package test;

import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.net.URLDecoder;

public class testJWT {
	private static Base64.Encoder encoder = Base64.getEncoder();
	private static Base64.Decoder decoder = Base64.getDecoder();
	private static String keystorePublicCrypto = "/Users/mac/Desktop/Eclipse_Work/workspace_oxygen/mydata/src/main/java/NDC.cer";
    public final static String ALGORITHM = "RSA";
    public final static String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String ENCODE_ALGORITHM = "SHA-256";
    
	public static void main(String[] args) throws SignatureException, ExpiredJwtException, UnsupportedJwtException,
			MalformedJwtException, IllegalArgumentException, Exception {
		String result = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ3d3cuZ3NwLmdvdi50dyIsInV1aWQiOiJmMDNjNWI4MS05Y2I4LTQwZjctODk0NS1jNjNmMTI1ODA4MjEiLCJyZXN1bHQiOiJBMDAwMO-8muaqouaguOato-eiuiEiLCJpYXQiOjE1NTc5NzA5NjV9.SDQL-IiuGCLqJ7mbHf7eupN0mOSdUPU_vSMndCqAq8Wqm7-9sKrcd7667_YK4rxeXl5vdRjX_avDiQB0PmcO1rPUbxCE5MZfe8kpQ23j4o4DfKHuV_ljUbJXqPtm27WTENH9NZru9tdyWRF9exHq4nJ_fIrtT3y6dSrQwjJE77s";
		System.out.println("------------ Decode JWT ------------");
		String[] split_string = result.split("\\.");
		String base64EncodedHeader = split_string[0];
		String base64EncodedBody = split_string[1];
		String base64EncodedSignature = split_string[2];

		System.out.println("~~~~~~~~~ JWT Header ~~~~~~~");
		String header = new String(Base64.getUrlDecoder().decode(base64EncodedHeader));
		System.out.println("JWT Header : " + header);

		System.out.println("~~~~~~~~~ JWT Body ~~~~~~~");
		String body = new String(Base64.getUrlDecoder().decode(base64EncodedBody));
		System.out.println("JWT Body : " + body);

		String publicKeyStr = getPublicKeyStr();
		System.out.println("公鑰：" + publicKeyStr);		
		
		//String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
		//String payload = "{\"iss\":\"www.gsp.gov.tw\",\"uuid\":\"f03c5b81-9cb8-40f7-8945-c63f12580821\",\"result\":\"A0000：檢核正確!\",\"iat\":1557970965}";
		//String unsigntoken = Base64.getUrlEncoder().encode(header.getBytes("UTF-8"))+"."+Base64.getUrlEncoder().encode(body.getBytes("UTF-8"));
		//verifySign(getPublicKey(),unsigntoken,base64EncodedSignature.getBytes("UTF-8"));
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
    
    public static boolean verifySign(PublicKey publicKey, String plain_text, byte[] signed) {
		MessageDigest messageDigest;
		boolean SignedSuccess=false;
		try {
			messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
			messageDigest.update(plain_text.getBytes());
			byte[] outputDigest_verify = messageDigest.digest();
			//System.out.println("SHA-256加密后-----》" +bytesToHexString(outputDigest_verify));
			Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
			verifySign.initVerify(publicKey);
			verifySign.update(outputDigest_verify);
			SignedSuccess = verifySign.verify(signed);
			System.out.println("验证成功？---" + SignedSuccess);
			
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
		} catch (java.security.SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SignedSuccess;
	}
}
