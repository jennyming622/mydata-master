package test;

import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.X509TrustManager;

public class testOCSP3 {

	X509TrustManager origTm;
	boolean verifyServerCert;
	PKIXParameters validatorParams;
	CertPathValidator validator;
	CertificateFactory certFactory;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void X509TrustManagerWrapper(X509TrustManager tm, boolean verifyServerCertificate)
			throws CertificateException {
		this.origTm = tm;
		this.verifyServerCert = verifyServerCertificate;

		if (verifyServerCertificate) {
			try {
				Set<TrustAnchor> anch = new HashSet<TrustAnchor>();
				for (X509Certificate cert : tm.getAcceptedIssuers()) {
					anch.add(new TrustAnchor(cert, null));
				}
				this.validatorParams = new PKIXParameters(anch);
				this.validatorParams.setRevocationEnabled(false);
				this.validator = CertPathValidator.getInstance("PKIX");
				this.certFactory = CertificateFactory.getInstance("X.509");
			} catch (Exception e) {
				throw new CertificateException(e);
			}
		}
	}

	/*private void validateNoCache(List<? extends X509Certificate> certs) throws SignatureException {
		try {
			CertPathValidator validator = CertPathValidator.getInstance(VALIDATOR_TYPE);
			PKIXParameters params = new PKIXParameters(trustRoots);
			params.addCertPathChecker(WAVE_OID_CHECKER);
			params.setDate(timeSource.now());

			// turn off default revocation-checking mechanism
			params.setRevocationEnabled(false);

			// TODO: add a way for clients to add certificate revocation checks,
			// perhaps by letting them pass in PKIXCertPathCheckers. This can also be
			// useful to check for Wave-specific certificate extensions.

			CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
			CertPath certPath = certFactory.generateCertPath(certs);
			validator.validate(certPath, params);
		} catch (GeneralSecurityException e) {
			throw new SignatureException("Certificate validation failure", e);
		}
	}*/
}
