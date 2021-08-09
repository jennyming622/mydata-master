package tw.gov.ndc.emsg.mydata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tw.gov.ndc.emsg.mydata.model.WebServiceJobId;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("immigrationUtil")
public class ImmigrationUtil implements ValidUtil {
    private static Logger log = LoggerFactory.getLogger(ImmigrationUtil.class);

    private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");

    @Value("${immigrationUrl}")
    private String requestUrl;

    @Value("${moi.orgId}")
    private String orgId;

    private ObjectMapper mapper = new ObjectMapper();

    public String call(Map<String, Object> conditionMap, WebServiceJobId jobId) throws Exception {
        log.debug("Use ImmigrationUtil");

        String residentIdNo = MapUtils.getString(conditionMap, "ck_personId");
        String birthDate = MapUtils.getString(conditionMap, "ck_birthDate");
        Date birthDateAd = CDateUtil.ROCDateToAD(birthDate);

        List<NameValuePair> queryParam = new LinkedList<NameValuePair>();
        queryParam.add(new BasicNameValuePair("residentIdNo", residentIdNo));
        queryParam.add(new BasicNameValuePair("birthDate", sdf6.format(birthDateAd)));

        String realRequestUrl = StringUtils.join(this.requestUrl, "?", URLEncodedUtils.format(queryParam, "UTF-8"));

        log.warn("request url {}", realRequestUrl);

        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustStrategy() {
                    // ignore certificate
                    public boolean isTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                        return true;
                    }
                })
                .build();

        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(15 * 1000).build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(connectionFactory)
                .build();
        HttpPost httpPost = new HttpPost(realRequestUrl);
        httpPost.setHeader("Cache-Control", "no-cache");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("govid", orgId);
        httpPost.setConfig(requestConfig);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            CloseableHttpResponse httpResp = httpClient.execute(httpPost);
            String content = EntityUtils.toString(httpResp.getEntity());
            log.warn("content values >>> {}", content);
            return content;
        }catch(Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
        return RETURN_FAIL;
    }

    public Boolean isIdentifyValid(String content) throws Exception{
        if(StringUtils.equals(content, RETURN_FAIL)) {
            return false;
        }

        JsonNode node = mapper.readTree(content);

        System.out.println(node);
//        Boolean hasData = node.has("data");
//        if(hasData == false) {
//            return false;
//        }
//
//        JsonNode dataNode = node.get("data");
//        String result = dataNode.get("result").asText();
//
//        JsonNode resultNode = mapper.readTree(result);
        Boolean hasVaild = node.has("isValid");

        if(hasVaild == false) {
            return false;
        }
        String isValid = node.get("isValid").asText();

        return StringUtils.equals(isValid, "Y");
    }

    public static void main(String[] args) {
        String json = "{\"isValid\":\"Y\",\"rcode\":\"0000\"}";
        //String json = RETURN_FAIL;
        try {
            System.out.println(new ImmigrationUtil().isIdentifyValid(json));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
