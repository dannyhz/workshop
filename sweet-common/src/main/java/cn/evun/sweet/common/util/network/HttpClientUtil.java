package cn.evun.sweet.common.util.network;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.evun.sweet.common.util.StringUtils;

/**
 * http服务工具类
 *
 * @author qinjun
 * @since 1.0.0
 */
public class HttpClientUtil {
	
	/**
	 * http GET请求
	 * @param url 请求url
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpGet(String url) throws IOException{
		return get(url, null, createHttpClient());
	}
	
	/**
	 * http GET请求（附带请求头）
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpGet(String url,Map<String, String> header) throws IOException{
		return get(url, header, createHttpClient());
	}
	
	/**
	 * http GET请求（附带请求头）
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @param timeout 超时时间(毫秒，同时设置connectionRequestTimeout,connectTimeout,socketTimeout的值)
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpGet(String url,Map<String, String> header,int timeout) throws IOException{
		return get(url, header, createHttpClient(timeout));
	}
	
	/**
	 * https GET请求
	 * @param url 请求url
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpsGet(String url) throws IOException{
		return get(url, null, createHttpsClient());
	}
	
	/**
	 * https GET请求
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpsGet(String url,Map<String, String> header) throws IOException{
		return get(url, header, createHttpsClient());
	}
	
	/**
	 * https GET请求
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @param timeout 超时时间(毫秒，同时设置connectionRequestTimeout,connectTimeout,socketTimeout的值)
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpsGet(String url,Map<String, String> header,int timeout) throws IOException{
		return get(url, header, createHttpsClient(timeout));
	}
	/**
	 * http POST请求
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpPost(String url, Object params) throws IOException{
		return post(url, params, null, createHttpClient());
	}
	
	/**
	 * http POST请求（附带请求头）
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @param header 请求头
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpPost(String url,Object params,Map<String, String> header) throws IOException{
		return post(url, params, header, createHttpClient());
	}
	
	/**
	 * http POST请求（附带请求头）
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @param header 请求头
	 * @param timeout 超时时间(毫秒，同时设置connectionRequestTimeout,connectTimeout,socketTimeout的值)
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpPost(String url,Object params,Map<String, String> header,int timeout) throws IOException{
		return post(url, params, header, createHttpClient(timeout));
	}
	
	/**
	 * http POST请求
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpsPost(String url,Object params) throws IOException{
		return post(url, params, null, createHttpsClient());
	}
	
	/**
	 * https POST请求（可以添加请求头）
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @param header 请求头
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpsPost(String url,Object params,Map<String, String> header) throws IOException{
		return post(url, params, header, createHttpsClient());
	}
	
	/**
	 * https POST请求（可以添加请求头）
	 * @param url 请求url
	 * @param params 请求参数内容
	 * @param header 请求头
	 * @param timeout 超时时间(毫秒，同时设置connectionRequestTimeout,connectTimeout,socketTimeout的值)
	 * @return response content
	 * @throws IOException 
	 */
	public static String httpsPost(String url,Object params,Map<String, String> header,int timeout) throws IOException{
		return post(url, params, header, createHttpsClient(timeout));
	}
	
	private static String get(String url,Map<String, String> header,CloseableHttpClient client) throws IOException {
		String content = null;
		HttpGet request = new HttpGet(url);
		if (header != null && !header.isEmpty()) {
            for (String headerKey : header.keySet()) {
                request.setHeader(headerKey, header.get(headerKey));
            }
        }
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            content = EntityUtils.toString(entity, "utf-8");
		} finally {
			try {
				client.close();
			} catch (Exception e) {
				return null;
			}
		}
		return content;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String post(String url,Object params,Map<String, String> header,CloseableHttpClient client)
			throws IOException {
		String content = null;
		HttpPost request = new HttpPost(url);
		
		if(params !=null ){
			if (params instanceof String) {
				if(StringUtils.hasText(params.toString())){
					StringEntity reqEntity = new StringEntity(params.toString(), "UTF-8");
					reqEntity.setContentType("application/x-www-form-urlencoded");
					request.setEntity(reqEntity);
				}
			} else{
				Map dataMap = null;
				if (params instanceof Map) {
					dataMap = (HashMap) params;
				} else {
					try {
						dataMap = BeanUtils.describe(params);
					} catch (IllegalAccessException |InvocationTargetException |NoSuchMethodException e) {
					}
				}
				if (dataMap != null) {
					List<NameValuePair> qparams = new ArrayList<NameValuePair>();
					Iterator<String> ite = dataMap.keySet().iterator();
					while (ite.hasNext()) {
						String k = ite.next();
						Object v = dataMap.get(k);
						if (v == null) {
							continue;
						}
						qparams.add(new BasicNameValuePair(k,String.valueOf(v)));
					}
					request.setEntity(new UrlEncodedFormEntity(qparams,"utf-8"));
				}
			}
		}
		
		
		if (header != null && !header.isEmpty()) {
            for (String headerKey : header.keySet()) {
                request.setHeader(headerKey, header.get(headerKey));
            }
        }
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            content = EntityUtils.toString(entity, "utf-8");
		} finally {
			try {
				client.close();
			} catch (Exception e) {
				return null;
			}
		}
		return content;
	}
	
	private static CloseableHttpClient createHttpsClient() {
		return createHttpsClient(-1);
	}
	private static CloseableHttpClient createHttpClient() {
		return createHttpClient(-1);
	}
	
	private static CloseableHttpClient createHttpsClient(int timeout) {
        X509TrustManager x509TrustMgr = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustMgr}, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
        	return null;
        }
        HttpClientBuilder builder =  createHttpClientBuilder(timeout).setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext));
        return builder.build();
    }
	
	public static CloseableHttpClient createHttpClient(int timeout) {
        return createHttpClientBuilder(timeout).build();
    }
	
	private static HttpClientBuilder createHttpClientBuilder(int timeout){
		HttpClientBuilder builder =  HttpClientBuilder.create();
		RequestConfig defaultRequestConfig = null;
		if(timeout > 0){
			defaultRequestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					  .setConnectionRequestTimeout(timeout).build();
		}
		if(defaultRequestConfig != null){
			builder.setDefaultRequestConfig(defaultRequestConfig);
		}
		return builder;
	}
	
}
