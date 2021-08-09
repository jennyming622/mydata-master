package tw.gov.ndc.emsg.mydata.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.helper.SequenceHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TWFidoUtil {

    private static ObjectMapper om = new ObjectMapper();

    public static String exPushAuth(String pid, String syscode, String apiKey) {
        try {
            System.out.println("=====pushAuth=====");
            String url = "https://fido.moi.gov.tw/FidoWeb/service/v1/api/pushAuth";
            String tranSeq = SequenceHelper.createUUID();
            String checksum = getChecksum(syscode, pid, tranSeq, apiKey);
            TFido tFido = new TFido(syscode, tranSeq, checksum);
            tFido.setPid(pid);
            String body = om.writeValueAsString(tFido);
            String result = callApi(url, body);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String authConfirm(String qrid, String syscode, String apiKey) {
        try {
            System.out.println("=====authConfirm=====");
            String url = "https://fido.moi.gov.tw/FidoWeb/service/v1/api/authConfirm";
            String tranSeq = SequenceHelper.createUUID();
            String checksum = getChecksum(syscode, qrid, tranSeq, apiKey);
            TFido tFido = new TFido(syscode, tranSeq, checksum);
            tFido.setQrid(qrid);
            String body = om.writeValueAsString(tFido);
            String result = callApi(url, body);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getChecksum(String syscode, String id, String tranSeq, String key) {
        try {
            byte[] ivData = new byte[8];
            byte[] hexData = TripleDESUtil.string2b(id + syscode + tranSeq);
            byte encrypt[] = TripleDESUtil.encrypt(hexData, key, ivData);
            String result = TripleDESUtil.b2h(encrypt);
            String checksum = StringUtils.substring(result, result.length() - 16);
            return checksum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String callApi(String url, String body) {
        try {
            System.out.println("url :" + url);
            System.out.println("body:" + body);
            CloseableHttpClient httpClient = HttpClients.custom().build();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Cache-Control", "no-cache");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("accept", "application/json");

            httpPost.setEntity(new StringEntity(body));

            CloseableHttpResponse httpResp = httpClient.execute(httpPost);
            String content = EntityUtils.toString(httpResp.getEntity());
            return content;

        } catch (Exception e) {

        }
        return "";
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class TFido {
    private String syscode;
    private String pid;
    private String qrid;
    private String tranSeq;
    private String chksum;

    public TFido(String syscode, String tranSeq, String chksum) {
        this.syscode = syscode;
        this.tranSeq = tranSeq;
        this.chksum = chksum;
    }

    public String getSyscode() {
        return syscode;
    }

    public void setSyscode(String syscode) {
        this.syscode = syscode;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getQrid() {
        return qrid;
    }

    public void setQrid(String qrid) {
        this.qrid = qrid;
    }

    public String getTranSeq() {
        return tranSeq;
    }

    public void setTranSeq(String tranSeq) {
        this.tranSeq = tranSeq;
    }

    public String getChksum() {
        return chksum;
    }

    public void setChksum(String chksum) {
        this.chksum = chksum;
    }
}
