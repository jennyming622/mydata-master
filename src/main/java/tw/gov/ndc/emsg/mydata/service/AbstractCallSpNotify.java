package tw.gov.ndc.emsg.mydata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import tw.gov.ndc.emsg.mydata.entity.SpNotify;

public abstract class AbstractCallSpNotify {

    private ObjectMapper objectMapper = new ObjectMapper();

    protected abstract CloseableHttpClient client() throws Exception;

    public Integer call(String url, String responseData) {
    	System.out.println(url);
        CloseableHttpClient httpClient;
        HttpResponse response=null;
		try {
			httpClient = client();
	        RequestConfig requestConfig = RequestConfig.custom()
	                .setConnectTimeout(15 * 1000).build();

	        //Body
	        StringEntity stringEntity = new StringEntity(responseData);

	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setHeader("Cache-Control", "no-cache");
	        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setEntity(stringEntity);
	        httpPost.setConfig(requestConfig);

	        response = httpClient.execute(httpPost);
		} catch (Exception e) {
			System.out.println("AbstractCallSpNotify="+e);
			e.printStackTrace();
		}

        return response==null? -1:response.getStatusLine().getStatusCode();
    };

    public String responseData(SpNotify spNotify) throws JsonProcessingException {
        return objectMapper.writeValueAsString(spNotify);
    };
}
