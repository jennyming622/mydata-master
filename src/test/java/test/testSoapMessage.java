package test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class testSoapMessage {

	public static void main(String[] args) throws Exception {
		String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><SendOTPCodeResponse xmlns=\"http://cp.gov.tw/gsp2\"><SendOTPCodeResult><Code>0</Code><Message>816276</Message></SendOTPCodeResult></SendOTPCodeResponse></soap:Body></soap:Envelope>";
		String sub = s.substring(s.indexOf("<Message>")+9, s.indexOf("</Message>"));
		System.out.println(sub);
	}
}
