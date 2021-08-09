package tw.gov.ndc.emsg.mydata.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.riease.common.helper.HttpClientHelper;

import tw.gov.ndc.emsg.mydata.entity.ext.NhiResultEntity;
import tw.gov.ndc.emsg.mydata.model.NhiVerifyResponse;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Component
public class NhiUtils {

    static Logger logger = LoggerFactory.getLogger(NhiUtils.class);

    public static void main(String[] args) {
    	
    }


    public String call() throws NoSuchAlgorithmException, KeyManagementException {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                		X509Certificate[] certs = null;
                    return certs;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } };
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL("https://www.cp.gov.tw/gsp2ws/NHICard.asmx");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpsURLConnection) urlConnection;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8;");
            } else {
                System.out.println("请输入 URL 地址");
                return "";
            }
            String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    "  <soap12:Header>\n" +
                    "    <ServiceHeader xmlns=\"http://cp.gov.tw/gsp2\">\n" +
                    "      <ServiceID>APP0002001</ServiceID>\n" +
                    "    </ServiceHeader>\n" +
                    "  </soap12:Header>\n" +
                    "  <soap12:Body>\n" +
                    "    <NHIDataVerify xmlns=\"http://cp.gov.tw/gsp2\">\n" +
                    "      <ck_uid>H120983053</ck_uid>\n" +
                    "      <ck_nhiID>000001883667</ck_nhiID>\n" +
                    "      <clientHostName>mydata.nat.gov.tw</clientHostName>\n" +
                    "    </NHIDataVerify>\n" +
                    "  </soap12:Body>\n" +
                    "</soap12:Envelope>\n";
            String dataStr = "";
            if(connection!=null) {
            	OutputStreamWriter writer = null;
            	try {
                	writer = new OutputStreamWriter(connection.getOutputStream());
                	if(writer!=null) {
                        writer.write(data);
                        writer.flush();
                	}
    			}finally {
    				if(writer!=null) {
    					HttpClientHelper.safeClose(writer);
    				}
    			}
            	BufferedReader in = null;
            	InputStreamReader inr = null;
            	try {
            		inr = new InputStreamReader(connection.getInputStream());
                    in = new BufferedReader(inr);
                    String current;
                    if(in!=null) {
                        while ((current = in.readLine()) != null) {
                            dataStr += current;
                        }	
                    }
    			}finally {
    				if(inr!=null) {
    					HttpClientHelper.safeClose(inr);
    				}
    				if(in!=null) {
    					HttpClientHelper.safeClose(in);
    				}
    			}
                System.out.println(dataStr);
            }

            return dataStr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public NhiResultEntity isValid(String uid , String nhiId) throws NoSuchAlgorithmException, KeyManagementException{
        NhiResultEntity entity = new NhiResultEntity();
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                		X509Certificate[] certs = null;
                    return certs;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } };
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL("https://www.cp.gov.tw/gsp2ws/NHICard.asmx");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpsURLConnection) urlConnection;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8;");
            } else {
                System.out.println("请输入 URL 地址");
                entity.setValid(false);
            }

            String ckUid = String.format("<ck_uid>%s</ck_uid>\n",uid);
            String ckNhiId = String.format("<ck_nhiID>%s</ck_nhiID>\n",nhiId);

            String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    "  <soap12:Header>\n" +
                    "    <ServiceHeader xmlns=\"http://cp.gov.tw/gsp2\">\n" +
                    "      <ServiceID>APP0002001</ServiceID>\n" +
                    "    </ServiceHeader>\n" +
                    "  </soap12:Header>\n" +
                    "  <soap12:Body>\n" +
                    "    <NHIDataVerify xmlns=\"http://cp.gov.tw/gsp2\">\n" +
                    ckUid +
                    ckNhiId +
                    "      <clientHostName>mydata.nat.gov.tw</clientHostName>\n" +
                    "    </NHIDataVerify>\n" +
                    "  </soap12:Body>\n" +
                    "</soap12:Envelope>\n";
            System.out.println(data);
            
            if(connection!=null) {
            	OutputStreamWriter writer = null;
            	InputStream inputer = null;
            	try {
                    writer = new OutputStreamWriter(connection.getOutputStream());
                    if(writer!=null) {
                        writer.write(data);
                        writer.flush();
                    }
                	inputer = connection.getInputStream();
                    entity = verify(inputer, entity);
                    if(inputer!=null) {
                    	inputer.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
    				if(writer!=null) {
    					HttpClientHelper.safeClose(writer);
    				}
    				if(inputer!=null) {
    					HttpClientHelper.safeClose(inputer);
    				}
    			}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entity;
    }


    /**
     * 驗證xml response
     * @param inputStream
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private NhiResultEntity verify(InputStream inputStream,NhiResultEntity entity) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String feature = null;
        feature = "http://apache.org/xml/features/disallow-doctype-decl";
        dbf.setFeature(feature, true);
        feature = "http://xml.org/sax/features/external-general-entities";
        dbf.setFeature(feature, false);
        feature = "http://xml.org/sax/features/external-parameter-entities";
        dbf.setFeature(feature, false);
        feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        dbf.setFeature(feature, false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(inputStream,"UTF8");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("NHIDataVerifyResult");
        logger.debug("node list size -> {}" , nList.getLength());
        for(int i =0 ; i < nList.getLength() ; i++){
            Node nNode = nList.item(i);
            if(nNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                String code = elementTextByTagName(eElement,"Code");
                if(!StringUtils.equals(code,"0")){
                    entity.setValid(false);
                    entity.setMessage("系統錯誤");
                }else {
                    // 若是0則進一步檢查message
                    String msg = elementTextByTagName(eElement,"Message");
                    logger.debug("element code -> {}" , elementTextByTagName(eElement,"Code"));
                    logger.debug("element message -> {}" , elementTextByTagName(eElement,"Message"));
                    // ex : 回傳格式為 「A0000:檢核正確」
                    msg = msg.split(":")[0];
                    if(StringUtils.equals(msg, NhiVerifyResponse.Success.getValue())){
                        entity.setValid(true);
                        entity.setMessage("");
                    }else if(StringUtils.equals(msg, NhiVerifyResponse.Fail_Not_Exist.getValue())){
                        entity.setValid(false);
                        entity.setMessage("輸入不符合規則身分證號或健保卡卡號");
                    }else if(StringUtils.equals(msg, NhiVerifyResponse.Fail_Incompatible.getValue())){
                        entity.setValid(false);
                        entity.setMessage("健保卡卡號或身分證字號錯誤");
                    }
                }
            }
        }

        return entity;
    }


    /**
     * 取得xml節點 value
     * @param element
     * @param tag
     * @return
     */
    private static String elementTextByTagName(Element element,String tag){
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }

}
