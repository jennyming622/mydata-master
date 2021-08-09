package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class testOCSP2 {

	private static CertificateFactory cf = null;
	private static KeyStore keyStore = null;
	
	public static void main(String[] args) throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, CertPathValidatorException {
	    cf = CertificateFactory.getInstance("X.509");
	    keyStore = KeyStore.getInstance("JKS");
	    keyStore.load(new FileInputStream("/Users/mac/mydata.jks"), "mydata1234".toCharArray());
	    ///Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/security/cacerts
	    //keyStore.load(new FileInputStream("/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/security/cacerts"), "changeit".toCharArray());
	    
	    FileInputStream is = new FileInputStream("/Users/mac/mydatanatgovtw.crt");
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
	    List<X509Certificate> certificates = new ArrayList<X509Certificate>();
	    certificates.add(cert);
	    System.out.println(validateChain(certificates));
	}
	
	/**
	 * 根據憑證庫，檢查 X.509 PEM憑證是否正確 
	 * @param certificates
	 * @return
	 * @throws KeyStoreException
	 * @throws InvalidAlgorithmParameterException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws CertPathValidatorException
	 */
	private static Boolean validateChain(List<X509Certificate> certificates) throws KeyStoreException, InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, CertPathValidatorException {
	    PKIXParameters params;
	    CertPath certPath;
	    CertPathValidator certPathValidator;
	    Boolean valid = Boolean.FALSE;

	    params = new PKIXParameters(keyStore);
	    /**
	     * OCSP check
	     */
	    params.setRevocationEnabled(true);
	    Security.setProperty("ocsp.enable", "true");
	    /**
	     * CRL check
	     */
	    System.setProperty("com.sun.security.enableCRLDP", "true");
	    
	    certPath = cf.generateCertPath(certificates);
	    certPathValidator = CertPathValidator.getInstance("PKIX");

	    PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) certPathValidator.validate(certPath, params);
	    System.out.println(result.toString());
	    if(null != result) {
	    		valid = Boolean.TRUE;
	    }
	    System.out.println("----------validateChain-----------------");
	    return valid;
	 }
}
