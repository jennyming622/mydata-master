package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLReason;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.ocsp.OCSPResponse;

import sun.security.provider.certpath.OCSPResponse.*;
import sun.security.x509.X509CertImpl;

public class testOCSP {
    public final static String ALGORITHM = "RSA";
    public final static String SIGNATURE_ALGORITHM = "MD5withRSA";
    
	public static void main(String[] args) throws Exception {
		PublicKey publicKey = getCAPublicKey();
		if (publicKey instanceof X509Certificate) {
		    X509Certificate cert = (X509Certificate)publicKey;
		    cert.checkValidity();
		    cert.verify(publicKey);
		}
	}

    /**
     * getCAPublickKey
     * @return
     * @throws Exception
     */
    private static PublicKey getCAPublicKey() throws Exception{
		String keyStr = "";
		try{
		    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		    Certificate cert = cf.generateCertificate(new FileInputStream("/Users/mac/mydatanatgovtw.crt"));		    
		    keyStr = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
		    //System.out.println(Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded()));
		}catch(Exception ex){
		    ex.printStackTrace();
		}
	    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyStr));
	    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
	    PublicKey k = keyFactory.generatePublic(keySpec);
	    return k;
    }

	public static interface RevocationStatus {
		public enum CertStatus { GOOD, REVOKED, UNKNOWN };
		CertStatus getCertStatus();
		Date getRevocationTime();
		CRLReason getRevocationReason();
		Map<String, Extension> getSingleExtensions();
	}	
}
