package nc.bs.extsys.plugin.dingtalk.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import nc.bs.logging.Logger;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import uap.json.JSONObject;

public class HttpHelper1 {
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	private static final int CONNECTION_TIMEOUT = 30000;//连接初始化时间
	private static final int TIMEOUT = 300000;
	private static HttpClient httpClient = null;

	/**
	 * 构造post用于发送json
	 * 
	 * @param url
	 * @param timeout
	 * @param jsondata 发送数据
	 * @return
	 */
	private static HttpMethodBase createPostJsonMethod(String url, Map<String, String> header, int timeout, Map<String, String> jsondata) {

		PostMethod method = null;
		try {
			//使用post方式
			method = new PostMethod(url);
			//构造Json
			JSONObject jsonObject = new JSONObject();
			for (Map.Entry<String, String> entry : jsondata.entrySet()) {
				jsonObject.put(entry.getKey(), entry.getValue());
			}

			String transJson = jsonObject.toString();
			//使用StringRequestEntity构造请求实体,三个参数为:1传输的数据,2传送数据的格式,3字符集编码
			RequestEntity se = new StringRequestEntity(transJson, "application/json", "UTF-8");
			method.setRequestEntity(se);
			//使用系统提供的默认的恢复策略
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			//设置超时的时间
			method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, timeout);
			//设置头部
			method.setRequestHeader("Content-Type", "application/json");
			if (header != null) {
				for (Map.Entry<String, String> next : header.entrySet()) {
					method.setRequestHeader(next.getKey(), next.getValue());
				}
			}
		} catch (IllegalArgumentException e) {
			Logger.error("非法的URL：" + url);
		} catch (UnsupportedEncodingException e) {
			Logger.error(e);
			e.printStackTrace();
		}

		return method;
	}

	/**
	 * 将参数拼装在url中，进行post请求。
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static String httpPost(String url, Map<String, String> header, Map<String, String> jsondata) {
		HttpClient httpClient = new HttpClient();
		//设置连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		HttpMethod httpmethod = createPostJsonMethod(url, header, TIMEOUT, jsondata);
		if (httpmethod == null) {
			return null;
		}
		int statusCode = 0;
		String resultStr = null;
		try {
			statusCode = httpClient.executeMethod(httpmethod);
			//只要服务器返回的不是200代码，则统一认为获取失败
			if (statusCode != HttpStatus.SC_OK) {
				Logger.error("Method failed: " + httpmethod.getStatusLine() + "\tstatusCode: " + statusCode);
				return null;
			}
			resultStr = httpmethod.getResponseBodyAsString();
		} catch (HttpException e) {
			Logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			Logger.error(e);
			e.printStackTrace();
		} finally {
			httpmethod.releaseConnection();
		}
		return resultStr;
	}

	public static String httpGet(String url) {
		HttpClient httpClient = new HttpClient();
		//设置连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		HttpMethod httpmethod = new GetMethod(url);
		int statusCode = 0;
		String resultStr = null;
		try {
			statusCode = httpClient.executeMethod(httpmethod);
			//只要服务器返回的不是200代码，则统一认为获取失败
			if (statusCode != HttpStatus.SC_OK) {
				Logger.error("Method failed: " + httpmethod.getStatusLine() + "\tstatusCode: " + statusCode);
				return null;
			}
			resultStr = httpmethod.getResponseBodyAsString();
		} catch (HttpException e) {
			Logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			Logger.error(e);
			e.printStackTrace();
		} finally {
			httpmethod.releaseConnection();
		}
		return resultStr;
	}


}
