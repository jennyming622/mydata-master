package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class testOCSP1 {

	public static void main(String[] args) throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException, URISyntaxException, InvalidAlgorithmParameterException, CertPathValidatorException {
	      //String certFile = args[0];
	      //String cacertsFile = args[1];
	      //String cacertsPassword = args[2];
	      //String responderUrl = args[3];
	      /* old style removed
	      Security.setProperty("ocsp.enable", "true");
	      System.setProperty("com.sun.security.enableCRLDP", "false");
	      Security.setProperty("ocsp.responderURL", responderUrl);
	      if (args.length == 5) {
	        Security.setProperty("ocsp.responderCertSubjectName", args[4]);
	      }*/
	      // read the certificate from the file
	      System.out.println("Loading certificate...");
	      FileInputStream is = new FileInputStream("/Users/mac/mydatanatgovtw.crt");
	      CertificateFactory cf = CertificateFactory.getInstance("X.509");
	      X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
	      // read the cacerts keystore to check signature
	      System.out.println("Loading cacerts...");
	      
	      KeyStore cacerts = KeyStore.getInstance(KeyStore.getDefaultType());
	      cacerts.load(new FileInputStream("/Users/mac/mydata.jks"), "mydata1234".toCharArray());
	      // set security options to ocsp validation (only ocsp)
	      CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");
	      PKIXRevocationChecker rc = (PKIXRevocationChecker) cpb.getRevocationChecker();
	      rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.NO_FALLBACK));
	      rc.setOcspResponder(new URI("https://mydata.nat.gov.tw"));
	      rc.setOcspResponderCert((X509Certificate) cacerts.getCertificate("ocsp-trusted"));
	      // check the certpath with PKIX
	      List certs = new ArrayList();
	      certs.add(cert);
	      CertPath certPath = cf.generateCertPath(certs);
	      CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
	      PKIXParameters params = new PKIXParameters(cacerts);
	      params.addCertPathChecker(rc);
	      //params.setRevocationEnabled(false);
	      System.out.println("Performing PKIX validation...");
	      PKIXCertPathValidatorResult cpvResult = (PKIXCertPathValidatorResult) cpv.validate(certPath, params);
	      System.out.println("Result: OK");
	}

}
