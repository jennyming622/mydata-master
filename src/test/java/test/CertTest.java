package test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import sun.security.pkcs.PKCS7;
import sun.security.pkcs.ParsingException;
import sun.security.util.DerInputStream;


public class CertTest {
	
	public static void main(String args[]) throws CertificateException, ParsingException, IOException {
		Base64.Decoder base64Decoder = Base64.getDecoder();
		
		String certb64 = "";
		byte encodedCert[] = Base64.getDecoder().decode(certb64);
		ByteArrayInputStream inputStream  =  new ByteArrayInputStream(encodedCert);
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		X509Certificate cert1 = (X509Certificate)certFactory.generateCertificate(inputStream);
		
		
		String pkcs7String = "";
		PKCS7 pkcs7 = new PKCS7(new DerInputStream(base64Decoder.decode(pkcs7String)));
        X509Certificate[] certs = pkcs7.getCertificates();
        X509Certificate cert2 = certs[0];
	
	}

}
