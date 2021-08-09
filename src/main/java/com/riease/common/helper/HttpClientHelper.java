package com.riease.common.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.http.Cookie;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;


/**
 * HttpClient Helper
 * @author wesleyzhuang
 *
 */
public class HttpClientHelper {
	
	private static Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);
	
	/**
	 * 以POST呼叫網址。
	 * 預設 ContentType 為 application/x-www-form-urlencoded
	 * @param url		post的網址
	 * @param param		post上傳的參數及值
	 * @return			無值則回傳null
	 */
	public String post(String url, Map<String,String> param) {
		return post(url,param,"application/x-www-form-urlencoded",null,20);
	}
	/**
	 * 以POST呼叫網址。
	 * 預設 ContentType 為 application/x-www-form-urlencoded
	 * @param url		post的網址
	 * @param param		post上傳的參數及值
	 * @param timeout	timeout時間，秒。
	 * @return			無值則回傳null
	 */
	public String post(String url, Map<String,String> param, int timeout) {
		return post(url,param,"application/x-www-form-urlencoded",null,timeout);
	}
	/**
	 * 以POST呼叫網址。
	 * @param url
	 * @param param
	 * @param contentType
	 * @param timeout	timeout時間，秒。
	 * @return
	 */
	public String post(String url, Map<String,String> param,  String contentType, int timeout) {
		return post(url,param,contentType,null,timeout);
	}
	
	/*
	 * SSL Ignore
	 */
	public String postIgnoreSSL(String url, String body,Cookie[] cookies) {
		org.apache.http.client.HttpClient httpClient = null;
		List<Header> headers = new ArrayList<Header>();
		String respStr = null;
		
		if(cookies != null) {
			BasicCookieStore cookieStore = new BasicCookieStore();
			for(Cookie cookie : cookies) {
				BasicClientCookie ck = new BasicClientCookie(cookie.getName(), cookie.getValue());
				ck.setDomain(cookie.getDomain()==null?"":cookie.getDomain());
				ck.setPath(cookie.getPath()==null?"":cookie.getPath());
				ck.setAttribute(ClientCookie.MAX_AGE_ATTR,Integer.toString(cookie.getMaxAge()));
				ck.setSecure(cookie.getSecure());
				cookieStore.addCookie(ck);
			}
			httpClient = httpClient(url,headers,cookieStore,60);
		}else {
			httpClient = httpClient(url,headers,null,60);
		}
		try {
			StringEntity entity = new StringEntity(body);
			HttpPost post = new HttpPost(url);
			post.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(post);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == statusCode) {
				HttpEntity resp = httpResponse.getEntity();
				InputStream in = resp.getContent();
				StringWriter sw = new StringWriter();
				IOUtils.copy(in, sw, "UTF-8");
				sw.close();
				in.close();
				respStr = sw.toString();
			}else {
				logger.warn("HttpStatus : "+statusCode);
			}
		} catch (IOException e) {
			System.out.println("== postIgnoreSSL IOException ==:"+e);
			e.printStackTrace();
		}
		return respStr;
	}
	
	/**
	 * 以POST呼叫網址。
	 * @param url
	 * @param param
	 * @param contentType
	 * @param cookies
	 * @param timeout	timeout時間，秒。
	 * @return
	 */
	public String post(String url, Map<String,String> param, String contentType, Cookie[] cookies, int timeout) {
		String respStr = null;
		
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		if(param != null && param.size()>0) {
			Iterator<String> pkey = param.keySet().iterator();
			while(pkey.hasNext()) {
				String key = pkey.next();
				String value = param.get(key);
				NameValuePair p = new BasicNameValuePair(key,value);
				ps.add(p);
			}
		}
		
		HttpClient httpClient = null;
		List<Header> headers = new ArrayList<Header>();
		if(contentType != null && !contentType.isEmpty()) {
			Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(header);
		}

		if(cookies != null) {
			BasicCookieStore cookieStore = new BasicCookieStore();
			for(Cookie cookie : cookies) {
				BasicClientCookie ck = new BasicClientCookie(cookie.getName(), cookie.getValue());
				ck.setDomain(cookie.getDomain()==null?"":cookie.getDomain());
				ck.setPath(cookie.getPath()==null?"":cookie.getPath());
				ck.setAttribute(ClientCookie.MAX_AGE_ATTR,Integer.toString(cookie.getMaxAge()));
				ck.setSecure(cookie.getSecure());
				cookieStore.addCookie(ck);
			}
			httpClient = httpClient(url,headers,cookieStore,timeout);
		}else {
			httpClient = httpClient(url,headers,timeout);
		}
		
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
			HttpPost post = new HttpPost(url);
			post.setEntity(entity);
			
			HttpResponse httpResponse = httpClient.execute(post);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == statusCode) {
				HttpEntity resp = httpResponse.getEntity();
				InputStream in = resp.getContent();
				StringWriter sw = new StringWriter();
				IOUtils.copy(in, sw, "UTF-8");
				sw.close();
				in.close();
				respStr = sw.toString();
			}else {
				logger.warn("HttpStatus : "+statusCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	/**
	 * 以POST呼叫網址。
	 * 預設ContentType為application/json。
	 * @param url
	 * @param body
	 * @return
	 */
	public String post(String url, String body) {
		return post(url,body,"application/json",null,30);
	}
	/**
	 * 以POST呼叫網址。
	 * 預設ContentType為application/json。
	 * @param url	
	 * @param body
	 * @param cookies
	 * @return
	 */
	public String post(String url, String body, Cookie[] cookies) {
		return post(url,body,"application/json",cookies,20);
	}
	/**
	 * 以POST呼叫網址。
	 * @param url
	 * @param body
	 * @param contentType
	 * @return
	 */
	public String post(String url, String body, String contentType) {
		return post(url,body,contentType,null,20);
	}
	/**
	 * 以POST呼叫網址。
	 * @param url
	 * @param body
	 * @param contentType
	 * @param cookies
	 * @param timeout	timeout時間，秒。
	 * @return
	 */
	public String post(String url, String body, String contentType, Cookie[] cookies, int timeout) {
		String respStr = null;
		
		HttpClient httpClient = null;
		List<Header> headers = new ArrayList<Header>();
		if(contentType != null && !contentType.isEmpty()) {
			Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(header);
		}

		if(cookies != null) {
			BasicCookieStore cookieStore = new BasicCookieStore();
			for(Cookie cookie : cookies) {
				BasicClientCookie ck = new BasicClientCookie(cookie.getName(), cookie.getValue());
				ck.setDomain(cookie.getDomain()==null?"":cookie.getDomain());
				ck.setPath(cookie.getPath()==null?"":cookie.getPath());
				ck.setAttribute(ClientCookie.MAX_AGE_ATTR,Integer.toString(cookie.getMaxAge()));
				ck.setSecure(cookie.getSecure());
				cookieStore.addCookie(ck);
			}
			httpClient = httpClient(url,headers,cookieStore,timeout);
		}else {
			httpClient = httpClient(url,headers,timeout);
		}
		
		try {
			StringEntity entity = new StringEntity(body);
			HttpPost post = new HttpPost(url);
			post.setEntity(entity);
			
			HttpResponse httpResponse = httpClient.execute(post);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == statusCode) {
				HttpEntity resp = httpResponse.getEntity();
				InputStream in = resp.getContent();
				StringWriter sw = new StringWriter();
				IOUtils.copy(in, sw, "UTF-8");
				sw.close();
				in.close();
				respStr = sw.toString();
			}else {
				logger.warn("HttpStatus : "+statusCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	/**
	 * 以POST呼叫網址。
	 * @param url
	 * @param body
	 * @param contentType
	 * @param cookies
	 * @param timeout	timeout時間，秒。
	 * @return
	 */
	public String postWithHeaderEncode(String url, String body, String encodedValue, Cookie[] cookies, int timeout) {
		String respStr = null;
		
		HttpClient httpClient = null;
		List<Header> headers = new ArrayList<Header>();
		if(encodedValue != null && !encodedValue.isEmpty()) {
			Header header = new BasicHeader("Authorization", "Basic "+encodedValue);
			Header header1 = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
			headers.add(header);
			headers.add(header1);
		}

		if(cookies != null) {
			BasicCookieStore cookieStore = new BasicCookieStore();
			for(Cookie cookie : cookies) {
				BasicClientCookie ck = new BasicClientCookie(cookie.getName(), cookie.getValue());
				ck.setDomain(cookie.getDomain()==null?"":cookie.getDomain());
				ck.setPath(cookie.getPath()==null?"":cookie.getPath());
				ck.setAttribute(ClientCookie.MAX_AGE_ATTR,Integer.toString(cookie.getMaxAge()));
				ck.setSecure(cookie.getSecure());
				cookieStore.addCookie(ck);
			}
			httpClient = httpClient(url,headers,cookieStore,timeout);
		}else {
			httpClient = httpClient(url,headers,timeout);
		}
		
		try {
			StringEntity entity = new StringEntity(body);
			HttpPost post = new HttpPost(url);
			post.setEntity(entity);
			
			HttpResponse httpResponse = httpClient.execute(post);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == statusCode) {
				HttpEntity resp = httpResponse.getEntity();
				InputStream in = resp.getContent();
				StringWriter sw = new StringWriter();
				IOUtils.copy(in, sw, "UTF-8");
				sw.close();
				in.close();
				respStr = sw.toString();
			}else {
				logger.warn("HttpStatus : "+statusCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}	
	
			
	/**
	 * 以GET呼叫網址。
	 * @param url
	 * @return
	 */
	public String get(String url) {
		return get(url,null,null,20,null);
	}
	
	public String get(String url, String encoding) {
		return get(url,null,null,20,encoding);
	}
	
	/**
	 * 以GET呼叫網址。
	 * @param url
	 * @param timeout
	 * @return
	 */
	public String get(String url, int timeout) {
		return get(url,null,null,timeout,null);
	}
	/**
	 * 以GET呼叫網址。
	 * @param url
	 * @param cookies
	 * @return
	 */
	public String get(String url, Cookie[] cookies) {
		return get(url,null,cookies,20,null);
	}
	/**
	 * 以GET呼叫網址。
	 * @param url
	 * @param cookie
	 * @return
	 */
	public String get(String url, Cookie cookie) {
		Cookie[] cookies = new Cookie[1];
		cookies[0] = cookie;
		return get(url,null,cookies,20,null);
	}
	/**
	 * 以GET呼叫網址。
	 * @param url
	 * @param contentType
	 * @param cookies
	 * @param timeout	timeout時間，秒。
	 * @return
	 */
	public String get(String url, String contentType, Cookie[] cookies, int timeout, String responseEncoding) {
		String respStr = null;
		
		List<Header> headers = null;
		if(contentType != null && !contentType.isEmpty()) {
			headers = new ArrayList<Header>();
			Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(header);
		}
		
		BasicCookieStore cookieStore = null;
		if(cookies != null) {
			cookieStore = new BasicCookieStore();
			for(Cookie cookie : cookies) {
				BasicClientCookie ck = new BasicClientCookie(cookie.getName(), cookie.getValue());
				ck.setDomain(cookie.getDomain()==null?"":cookie.getDomain());
				ck.setPath(cookie.getPath()==null?"":cookie.getPath());
				ck.setAttribute(ClientCookie.MAX_AGE_ATTR,Integer.toString(cookie.getMaxAge()));
				ck.setSecure(cookie.getSecure());
				cookieStore.addCookie(ck);
			}
		}
		
		HttpClient httpClient = httpClient(url,headers,cookieStore,timeout);
		try {
			HttpGet get = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(get);
			if(responseEncoding == null) {
				respStr = processResponse(httpResponse);
			}else {
				respStr = processResponse(httpResponse, responseEncoding);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	
	public File getToFile(String url) {
		org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		
		File tmpFolder = Files.createTempDir();
		File file = new File(tmpFolder, SequenceHelper.produceSN());
		
		logger.trace("file from url=[{}] store to {}",
				url,
				file.getAbsolutePath());
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			HttpResponse resp = client.execute(get);
			int result = resp.getStatusLine().getStatusCode(); 
			if(result == HttpStatus.SC_OK) {
				HttpEntity entity = resp.getEntity();
				is = entity.getContent();
				fos = new FileOutputStream(file);
				int inByte;
				if(is!=null&&fos!=null) {
					while((inByte = is.read()) != -1) fos.write(inByte);
				}
				if(fos!=null) {
					fos.flush();
					fos.close();
				}
				if(is!=null) {
					is.close();
				}
			}else {
				logger.warn("HttpStatus : "+result);
			}
		} catch (Exception e) {
			e.printStackTrace();
    	}finally {
			if(fos!=null) {
				HttpClientHelper.safeClose(fos);
			}
			if(is!=null) {
				HttpClientHelper.safeClose(is);
			}
		}
		return file;
	}
	
	
	/**
	 * 以DELETE呼叫網址。
	 * @param url
	 * @return
	 */
	public String delete(String url) {
		return delete(url,null,null,20);
	}
	/**
	 * 以DELETE呼叫網址。
	 * @param url
	 * @param contentType
	 * @param cookies
	 * @param timeout	timeout時間，秒。
	 * @return
	 */
	public String delete(String url, String contentType, Cookie[] cookies, int timeout) {
		String respStr = null;
		
		List<Header> headers = null;
		if(contentType != null && !contentType.isEmpty()) {
			headers = new ArrayList<Header>();
			Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(header);
		}
		
		BasicCookieStore cookieStore = null;
		if(cookies != null) {
			cookieStore = new BasicCookieStore();
			for(Cookie cookie : cookies) {
				BasicClientCookie ck = new BasicClientCookie(cookie.getName(), cookie.getValue());
				ck.setDomain(cookie.getDomain()==null?"":cookie.getDomain());
				ck.setPath(cookie.getPath()==null?"":cookie.getPath());
				ck.setAttribute(ClientCookie.MAX_AGE_ATTR,Integer.toString(cookie.getMaxAge()));
				ck.setSecure(cookie.getSecure());
				cookieStore.addCookie(ck);
			}
		}
		
		HttpClient httpClient = httpClient(url,headers,cookieStore,timeout);
		try {
			HttpDelete delete = new HttpDelete(url);
			HttpResponse httpResponse = httpClient.execute(delete);
			respStr = processResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	/**
	 * 以PUT呼叫網址。
	 * @param url
	 * @return
	 */
	public String put(String url) {
		return put(url,null,null,20);
	}
	/**
	 * 以PUT呼叫網址。
	 * @param url
	 * @param contentType
	 * @param cookies
	 * @param timeout	timeout時間，秒。
	 * @return
	 */
	public String put(String url, String contentType, Cookie[] cookies, int timeout) {
		String respStr = null;
		
		List<Header> headers = null;
		if(contentType != null && !contentType.isEmpty()) {
			headers = new ArrayList<Header>();
			Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(header);
		}
		
		BasicCookieStore cookieStore = null;
		if(cookies != null) {
			cookieStore = new BasicCookieStore();
			for(Cookie cookie : cookies) {
				BasicClientCookie ck = new BasicClientCookie(cookie.getName(), cookie.getValue());
				ck.setDomain(cookie.getDomain()==null?"":cookie.getDomain());
				ck.setPath(cookie.getPath()==null?"":cookie.getPath());
				ck.setAttribute(ClientCookie.MAX_AGE_ATTR,Integer.toString(cookie.getMaxAge()));
				ck.setSecure(cookie.getSecure());
				cookieStore.addCookie(ck);
			}
		}
		
		HttpClient httpClient = httpClient(url,headers,cookieStore,timeout);
		try {
			HttpPut put = new HttpPut(url);
			HttpResponse httpResponse = httpClient.execute(put);
			respStr = processResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	
	
	private String processResponse(HttpResponse httpResponse) {
		String respStr = null;
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == statusCode) {
				HttpEntity resp = httpResponse.getEntity();
				InputStream in = resp.getContent();
				StringWriter sw = new StringWriter();
				IOUtils.copy(in, sw, "UTF-8");
				sw.close();
				in.close();
				respStr = sw.toString();
			}else {
				logger.warn("HttpStatus : "+statusCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	private String processResponse(HttpResponse httpResponse, String encoding) {
		String respStr = null;
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == statusCode) {
				HttpEntity resp = httpResponse.getEntity();
				InputStream in = resp.getContent();
				StringWriter sw = new StringWriter();
				IOUtils.copy(in, sw, encoding);
				sw.close();
				in.close();
				respStr = sw.toString();
			}else {
				logger.warn("HttpStatus : "+statusCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return respStr;
	}
	
	private HttpClient httpClient(String url) {
		return httpClient(url,null,null,20);
	}
	
	private HttpClient httpClient(String url, List<Header> headers) {
		return httpClient(url,headers,null,20);
	}
	
	private HttpClient httpClient(String url, List<Header> headers, int timeout) {
		return httpClient(url,headers,null,timeout);
	}
	
	private HttpClient httpClient(String url, List<Header> headers, CookieStore cookieStore) {
		return httpClient(url,headers,cookieStore,20);
	}
	
	private HttpClient httpClient(String url, List<Header> headers, CookieStore cookieStore, int timeout) {
		RequestConfig config = RequestConfig.custom()
				  .setConnectTimeout(timeout * 1000)
				  .setConnectionRequestTimeout(timeout * 1000)
				  .setSocketTimeout(timeout * 1000).build();
		
		HttpClient httpClient = null;
		if(url.startsWith("https://")) {
			SSLContextBuilder sslContextBuilder = SSLContexts.custom();
			try {
				sslContextBuilder.loadTrustMaterial(new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						return true;
					}
				});
				
				SSLContext sslContext = sslContextBuilder.build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				        sslContext, new HostnameVerifier() {

							@Override
							public boolean verify(String host, SSLSession ssl) {
								return true;
							}
				        	
				        });
				
				Registry<ConnectionSocketFactory> socketFactoryRegistry = 
						RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf).build();
				
				PoolingHttpClientConnectionManager cm = 
						new PoolingHttpClientConnectionManager(socketFactoryRegistry);
				
				HttpClientBuilder httpClientBuilder = HttpClients.custom();
				httpClientBuilder.setConnectionManager(cm);
				if(headers != null) {
					httpClientBuilder.setDefaultHeaders(headers);
				}
				if(cookieStore != null) {
					httpClientBuilder.setDefaultCookieStore(cookieStore);
				}
				if(config != null) {
					httpClientBuilder.setDefaultRequestConfig(config);
				}
				
				httpClient = httpClientBuilder.build();
				
			} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e1) {
				e1.printStackTrace();
			}
		}else {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			if(headers != null) {
				httpClientBuilder.setDefaultHeaders(headers);
			}
			if(cookieStore != null) {
				httpClientBuilder.setDefaultCookieStore(cookieStore);
			}
			if(config != null) {
				httpClientBuilder.setDefaultRequestConfig(config);
			}
			
			httpClient = httpClientBuilder.build();
		}
		return httpClient;
	}
	
	public static void safeClose(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				logger.info("InputStream is not close!");
			}
		}
	}
	
	public static void safeClose(FileInputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				logger.info("FileInputStream is not close!");
			}
		}
	}	
	
	public static void safeClose(BufferedReader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				logger.info("BufferedReader is not close!");
			}
		}
	}
	
	public static void safeClose(InputStreamReader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				logger.info("InputStreamReader is not close!");
			}
		}
	}
	
	public static void safeClose(ZipFile in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				logger.info("ZipFile is not close!");
			}
		}
	}
	
	public static void safeClose(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				logger.info("OutputStream is not close!");
			}
		}
	}
	
	public static void safeClose(OutputStreamWriter out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				logger.info("OutputStreamWriter is not close!");
			}
		}
	}
	
	public static void safeClose(FileOutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				logger.info("FileOutputStream is not close!");
			}
		}
	}	
	
	public static void safeClose(BufferedWriter out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				logger.info("BufferedWriter is not close!");
			}
		}
	}
	
	public static void main(String[] args) {
		HttpClientHelper h = new HttpClientHelper();
		//String resp = h.get("http://www.betternewlife.com/bnlife/");
		//String resp = h.get("http://127.0.0.1:8080/emsgService/v1/oauth/login?app_id=emsgweb-2014");
		//String resp = h.get("https://msg.nat.gov.tw");
		
		Cookie[] cookie = null;
		String resp = h.get("http://www.betternewlife.com/bnlife/",cookie);
		
		System.out.println(resp);
	}
	
	
	

}
