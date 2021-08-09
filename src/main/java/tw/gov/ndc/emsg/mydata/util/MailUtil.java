package tw.gov.ndc.emsg.mydata.util;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riease.common.helper.HttpClientHelper;
import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.Config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailUtil {
	private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

	/**
	 * 寄信
	 * 目前僅特定IP(內部網路並需設定才允許連線10.20.13.1)
	 * @param reveicers
	 * @param from
	 * @param title
	 * @param content
	 */
//	public static void sendMail(List<String> reveicers, String from, String title, String content,String mailEnable) {
//		if(mailEnable!=null&&mailEnable.equalsIgnoreCase("true")) {
//			logger.info("== sendMail mailEnable true==");
//			if (reveicers != null && reveicers.size() > 0) {
//				for(String to:reveicers) {
//					logger.info("[{}]-from: {}--title-- {}",to ,from,title);
//					logger.info("[{}]-from: {}--content-- {}",to ,from ,content);
//					try {
//						String host = Config.mailServerHost;
//						Properties properties = System.getProperties();
//						properties.setProperty("mail.smtp.host", host);
//                        properties.setProperty("mail.smtp.connectiontimeout", "25000");
//                        properties.setProperty("mail.smtp.timeout", "25000");
//						/**
//						 * mail ssl/tls auth參數設定，為了配合設定暫時MarkUp
//						 */
//						/*properties.put("mail.smtp.auth", "true");
//						properties.put("mail.smtp.socketFactory.port", 465);
//						properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//						properties.put("mail.smtp.socketFactory.fallback", "false");*/
//
//						Session session = Session.getDefaultInstance(properties);
//						MimeMessage message = new MimeMessage(session);
//						message.setFrom(new InternetAddress(from));
//						/**
//						 *  密件副本
//						 */
//						message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(to));
//						message.setSubject(title);
//						message.setText(content);
//						// Send message
//						Transport.send(message);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}else {
//			logger.info("== sendMail mailEnable false==");
//			if (reveicers != null && reveicers.size() > 0) {
//				for(String to:reveicers) {
//					logger.info("[{}]--title-- {}",to ,title);
//					logger.info("[{}]--content-- {}",to ,content);
//				}
//			}
//		}
//	}
	
//	public static void sendHtmlMail(List<String> reveicers, String from, String title, String content,String mailEnable) {
//		if(mailEnable!=null&&mailEnable.equalsIgnoreCase("true")) {
//			logger.info("== sendMail mailEnable true==");
//			if (reveicers != null && reveicers.size() > 0) {
//				for(String to:reveicers) {
//					logger.info("[{}]-from: {}--title-- {}",to ,from,title);
//					logger.info("[{}]-from: {}--content-- {}",to ,from ,content);
//					try {
//						String host = Config.mailServerHost;
//						Properties properties = System.getProperties();
//						properties.setProperty("mail.smtp.host", host);
//                        properties.setProperty("mail.smtp.connectiontimeout", "25000");
//                        properties.setProperty("mail.smtp.timeout", "25000");
//						/**
//						 * mail ssl/tls auth參數設定，為了配合設定暫時MarkUp
//						 */
//						/*properties.put("mail.smtp.auth", "true");
//						properties.put("mail.smtp.socketFactory.port", 465);
//						properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//						properties.put("mail.smtp.socketFactory.fallback", "false");*/
//
//						Session session = Session.getDefaultInstance(properties);
//						MimeMessage message = new MimeMessage(session);
//						message.setFrom(new InternetAddress(from));
//						/**
//						 *  密件副本
//						 */
//						message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(to));
//						message.setSubject(title,"UTF-8");
//						message.setContent(content, "text/html;charset=UTF-8");
//						// Send message
//						Transport.send(message);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}else {
//			logger.info("== sendMail mailEnable false==");
//			if (reveicers != null && reveicers.size() > 0) {
//				for(String to:reveicers) {
//					logger.info("[{}]--title-- {}",to ,title);
//					logger.info("[{}]--content-- {}",to ,content);
//				}
//			}
//		}
//	}

	public static void sendMail(List<String> reveicers, String from, String title, String content,String mailEnable) {
		from = ValidatorHelper.removeSpecialCharacters(from);
		title = ValidatorHelper.removeSpecialCharacters(title);
		content = ValidatorHelper.removeSpecialCharacters(content);
		if(mailEnable!=null&&mailEnable.equalsIgnoreCase("true")) {
			logger.info("== sendMail mailEnable true==");
			if (reveicers != null && reveicers.size() > 0) {
				for(String to:reveicers) {
					to = ValidatorHelper.removeSpecialCharacters(to);
					logger.info("[{}]-from: {}--title-- {}",to ,from,title);
					logger.info("[{}]-from: {}--content-- {}",to ,from ,content);
					FileInputStream fis = null;
					try {
						String host = Config.mailServerHost;
						Properties properties = System.getProperties();
						properties.setProperty("mail.smtp.host", host);
						properties.setProperty("mail.smtp.connectiontimeout", "25000");
						properties.setProperty("mail.smtp.timeout", "25000");
	                    /**
	                     * mail ssl/tls auth參數設定，為了配合設定暫時MarkUp
	                     */
//						properties.put("mail.smtp.auth", "true");
//						properties.put("mail.smtp.socketFactory.port", Integer.valueOf(Config.mailServerPort));
//						properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//						properties.put("mail.smtp.socketFactory.fallback", "false");
						
                        Session session = Session.getDefaultInstance(properties);
                        
						MimeBodyPart msg = new MimeBodyPart();
						msg.setText(content);

						KeyStore keyStore = KeyStore.getInstance("PKCS12");
						fis = new FileInputStream(Config.emailPfxPath);
						char[] key = "23165300".toCharArray();
						keyStore.load( (InputStream) fis, key);

						PrivateKey pk = (PrivateKey) keyStore.getKey("mydata_system@ndc.gov.tw", key);
						X509Certificate pkcs12Cert = (X509Certificate) keyStore.getCertificate("mydata_system@ndc.gov.tw");
						List certList = new ArrayList();
						certList.add(pkcs12Cert);
						org.bouncycastle.util.Store certs = new JcaCertStore(certList);
						SMIMESignedGenerator gen = new SMIMESignedGenerator();

						ASN1EncodableVector signedAttrs = new ASN1EncodableVector();
						SMIMECapabilityVector caps = new SMIMECapabilityVector();

						caps.addCapability(SMIMECapability.dES_EDE3_CBC);
						caps.addCapability(SMIMECapability.rC2_CBC, 128);
						caps.addCapability(SMIMECapability.dES_CBC);
						signedAttrs.add(new SMIMECapabilitiesAttribute(caps));

						gen.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder()
								.setProvider(new BouncyCastleProvider())
								.setSignedAttributeGenerator(new AttributeTable(signedAttrs))
								.build("SHA256withRSA", pk, pkcs12Cert));

						gen.addCertificates(certs);

						MimeMultipart minePart = gen.generate(msg);

						MimeMessage message = new MimeMessage(session);
						message.setFrom(new InternetAddress(from, "MyData平臺系統通知"));
						/**
						 *  密件副本
						 */
						message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(to));
						message.setSubject(ValidatorHelper.removeSpecialCharacters(title));
						message.setContent(minePart, minePart.getContentType());
						// Send message
						Transport.send(message);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(fis!=null) {
							HttpClientHelper.safeClose(fis);
						}
						logger.info("[{}]-send finish", to);
					}
				}
			}
		}else {
			logger.info("== sendMail mailEnable false==");
			if (reveicers != null && reveicers.size() > 0) {
				for(String to:reveicers) {
					to = ValidatorHelper.removeSpecialCharacters(to);
					logger.info("[{}]--title-- {}",to ,title);
					logger.info("[{}]--content-- {}",to ,content);
				}
			}
		}
	}

	public static void sendHtmlMail(List<String> reveicers, String from, String title, String content,String mailEnable) {
		from = ValidatorHelper.removeSpecialCharacters(from);
		title = ValidatorHelper.removeSpecialCharacters(title);
		content = ValidatorHelper.removeSpecialCharacters(content);
		if(mailEnable!=null&&mailEnable.equalsIgnoreCase("true")) {
			logger.info("== sendMail mailEnable true==");
			if (reveicers != null && reveicers.size() > 0) {
				for(String to:reveicers) {
					to = ValidatorHelper.removeSpecialCharacters(to);
					logger.info("[{}]-from: {}--title-- {}",to ,from,title);
					logger.info("[{}]-from: {}--content-- {}",to ,from ,content);
					FileInputStream fis = null;
					try {
						String host = Config.mailServerHost;
						Properties properties = System.getProperties();
						properties.setProperty("mail.smtp.host", host);
						properties.setProperty("mail.smtp.connectiontimeout", "25000");
						properties.setProperty("mail.smtp.timeout", "25000");
	                    /**
	                     * mail ssl/tls auth參數設定，為了配合設定暫時MarkUp
	                     */
//						properties.put("mail.smtp.auth", "true");
//						properties.put("mail.smtp.socketFactory.port", Integer.valueOf(Config.mailServerPort));
//						properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//						properties.put("mail.smtp.socketFactory.fallback", "false");
						
                        Session session = Session.getDefaultInstance(properties);
						
						MimeBodyPart msg = new MimeBodyPart();
						msg.setContent(content, "text/html;charset=UTF-8");

						KeyStore keyStore = KeyStore.getInstance("PKCS12");
						fis = new FileInputStream(Config.emailPfxPath);
						char[] key = "23165300".toCharArray();
						keyStore.load( (InputStream) fis, key);

						PrivateKey pk = (PrivateKey) keyStore.getKey("mydata_system@ndc.gov.tw", key);
						X509Certificate pkcs12Cert = (X509Certificate) keyStore.getCertificate("mydata_system@ndc.gov.tw");
						List certList = new ArrayList();
						certList.add(pkcs12Cert);
						org.bouncycastle.util.Store certs = new JcaCertStore(certList);
						SMIMESignedGenerator gen = new SMIMESignedGenerator();

						ASN1EncodableVector signedAttrs = new ASN1EncodableVector();
						SMIMECapabilityVector caps = new SMIMECapabilityVector();

						caps.addCapability(SMIMECapability.dES_EDE3_CBC);
						caps.addCapability(SMIMECapability.rC2_CBC, 128);
						caps.addCapability(SMIMECapability.dES_CBC);
						signedAttrs.add(new SMIMECapabilitiesAttribute(caps));

						gen.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder()
								.setProvider(new BouncyCastleProvider())
								.setSignedAttributeGenerator(new AttributeTable(signedAttrs))
								.build("SHA256withRSA", pk, pkcs12Cert));

						gen.addCertificates(certs);

						MimeMultipart minePart = gen.generate(msg);

						MimeMessage message = new MimeMessage(session);
						message.setFrom(new InternetAddress(from, "MyData平臺系統通知"));
						/**
						 *  密件副本
						 */
						message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(to));
						message.setSubject(ValidatorHelper.removeSpecialCharacters(title));
						message.setContent(minePart, minePart.getContentType());
						// Send message
						Transport.send(message);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(fis!=null) {
							HttpClientHelper.safeClose(fis);
						}
						logger.info("[{}]-send finish", to);
					}
				}
			}
		}else {
			logger.info("== sendMail mailEnable false==");
			if (reveicers != null && reveicers.size() > 0) {
				for(String to:reveicers) {
					to = ValidatorHelper.removeSpecialCharacters(to);
					logger.info("[{}]--title-- {}",to ,title);
					logger.info("[{}]--content-- {}",to ,content);
				}
			}
		}
	}
}
