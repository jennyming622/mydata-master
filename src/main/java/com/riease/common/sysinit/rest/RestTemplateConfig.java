/**
 * 
 */
package com.riease.common.sysinit.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 設定使用 RestTemplate
 * @author wesleyzhuang
 * @see org.springframework.web.client.RestTemplate
 */
@Configuration
public class RestTemplateConfig {
	
	private int connectTime = 10*1000;
	private int readTimeout = 20*1000;
	private int connectionRequestTimeout = 10*1000;

	
	@Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.getMessageConverters()
        			.add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
    	HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    	factory.setConnectTimeout(connectTime);
    	factory.setReadTimeout(readTimeout);
    	factory.setConnectionRequestTimeout(connectionRequestTimeout);
    	factory.setBufferRequestBody(false);
        return factory;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }

	/**
	 * @return the connectTime
	 */
	public int getConnectTime() {
		return connectTime;
	}

	/**
	 * @param connectTime the connectTime to set
	 */
	public void setConnectTime(int connectTime) {
		this.connectTime = connectTime;
	}

	/**
	 * @return the readTimeout
	 */
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * @param readTimeout the readTimeout to set
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * @return the connectionRequestTimeout
	 */
	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	/**
	 * @param connectionRequestTimeout the connectionRequestTimeout to set
	 */
	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}
    
}
