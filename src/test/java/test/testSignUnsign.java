package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;

import org.dom4j.Attribute;
import org.dom4j.Document;

public class testSignUnsign {

	/**
	 * 使用 /src/test/java/test/testMyDataAPItoFile.java 解密後的檔案內容
	 *
	 * /Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz/META-INFO
	 */
	public final static String ALGORITHM = "RSA";
	public final static String SIGNATURE_ALGORITHM = "MD5withRSA";
	
	private static Element root;
    private static List<String> name = new ArrayList<String>();
    private static Document document;
    
	private static File certFile = new File("/Users/mac/Desktop/mydata-example/API.AbAYGIjoYw20190822110146/META-INFO/certificate.cer");    
    
	public static void main(String[] args) throws Exception {
		/**
		 * XML處理 dom4j
		 */
		String manifestPathStr = "/Users/mac/Desktop/mydata-example/API.AbAYGIjoYw20190822110146/META-INFO/manifest.xml";
		File manifestfile = new File(manifestPathStr); 
		Map<String,String> hashmap = new HashMap<String,String>();
		try { 
			SAXReader reader = new SAXReader();
			Document doc = reader.read(manifestfile); 
			root = doc.getRootElement(); 
			List<Element> childElements = root.elements();
			for (Element child : childElements) {
				hashmap.put(child.elementText("filename"), child.elementText("digest"));
	            System.out.println("filename" + child.elementText("filename"));
	            System.out.println("digest" + child.elementText("digest"));
			}
		}catch(Exception ex) {
			ex.printStackTrace(); 
		}
		/**
		 * SHA-256驗證
		 */
		String pdfPathStr = "/Users/mac/Desktop/mydata-example/API.AbAYGIjoYw20190822110146/lowtype_1080822110733.pdf";
		byte[] b = Files.readAllBytes(Paths.get(pdfPathStr));
		String  sha256file = Hashing.sha256().hashBytes(b).toString();
		System.out.println("SHA256-0 file：" + Hashing.sha256().hashBytes(b).toString());
		String  sha256manifest = hashmap.get("lowtype_1080822110733.pdf");
		if(sha256file.equalsIgnoreCase(sha256manifest)) {
			System.out.println("==SHA-256==:"+true);
		}else {
			System.out.println("==SHA-256==:"+false);
		}
		
		manifestPathStr = "/Users/mac/Desktop/mydata-example/API.AbAYGIjoYw20190822110146/META-INFO/manifest.xml";
		String signaturePathStr = "/Users/mac/Desktop/mydata-example/API.AbAYGIjoYw20190822110146/META-INFO/signature.sha256withrsa";

		b = Files.readAllBytes(Paths.get(manifestPathStr));
		System.out.println("SHA256-0 file：" + Hashing.sha256().hashBytes(b).toString());
		String signedData = readFileAsString(signaturePathStr);
		String signAlg = "SHA256withRSA"; // 簽名類型
		Signature signature = Signature.getInstance(signAlg);
		signature.initVerify(getCAPublicKey("/Users/mac/Desktop/mydata-example/API.AbAYGIjoYw20190822110146/META-INFO/certificate.cer"));
		signature.update(b);		
		boolean isVerify = signature.verify(Base64.getDecoder().decode(signedData));
		System.out.println("CA驗簽：" + isVerify);
	}

	
	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
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
	private static PublicKey getCAPublicKey(String cerpath) throws Exception {
		String keyStr = "";
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(new FileInputStream( cerpath ));
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
