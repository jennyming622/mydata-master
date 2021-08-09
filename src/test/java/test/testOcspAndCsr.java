package test;

import java.io.FileInputStream;
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
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.util.ArrayList;

import java.security.cert.X509Certificate;

public class testOcspAndCsr {

	public static void main(String[] args) {
	    //CertVal certVal = new CertVal(new File("/home/varun/Documents/SampleCerts/google.pem"), "X.509");
	    //X509Certificate cert = (X509Certificate) certVal.getCert();
	    
	}

	public static boolean isCertChainValid(ArrayList<X509Certificate> certificateList) {
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			CertPath certPath = certificateFactory.generateCertPath(certificateList);
			CertPathValidator validator = CertPathValidator.getInstance("PKIX");
			KeyStore keystore = KeyStore.getInstance("JKS");
			InputStream is = new FileInputStream(System.getProperty("java.home") + "/lib/security/" + "cacerts");
			keystore.load(is, "changeit".toCharArray());
			PKIXParameters params = new PKIXParameters(keystore);
			params.setRevocationEnabled(true);
			Security.setProperty("ocsp.enable", "true");
			System.setProperty("com.sun.net.ssl.checkRevocation", "true");
			System.setProperty("com.sun.security.enableCRLDP", "true");

			PKIXCertPathValidatorResult r = (PKIXCertPathValidatorResult) validator.validate(certPath, params);
			return true;
		} catch (CertificateException e) {
			throw new RuntimeException(e);

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);

		} catch (KeyStoreException e) {
			throw new RuntimeException(e);

		} catch (IOException e) {
			throw new RuntimeException(e);

		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);

		} catch (CertPathValidatorException e) {
			throw new RuntimeException(e);
		}
	}
}
