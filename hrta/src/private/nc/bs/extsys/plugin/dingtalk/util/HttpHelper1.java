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
	private static final int CONNECTION_TIMEOUT = 30000;//���ӳ�ʼ��ʱ��
	private static final int TIMEOUT = 300000;
	private static HttpClient httpClient = null;

	/**
	 * ����post���ڷ���json
	 * 
	 * @param url
	 * @param timeout
	 * @param jsondata ��������
	 * @return
	 */
	private static HttpMethodBase createPostJsonMethod(String url, Map<String, String> header, int timeout, Map<String, String> jsondata) {

		PostMethod method = null;
		try {
			//ʹ��post��ʽ
			method = new PostMethod(url);
			//����Json
			JSONObject jsonObject = new JSONObject();
			for (Map.Entry<String, String> entry : jsondata.entrySet()) {
				jsonObject.put(entry.getKey(), entry.getValue());
			}

			String transJson = jsonObject.toString();
			//ʹ��StringRequestEntity��������ʵ��,��������Ϊ:1���������,2�������ݵĸ�ʽ,3�ַ�������
			RequestEntity se = new StringRequestEntity(transJson, "application/json", "UTF-8");
			method.setRequestEntity(se);
			//ʹ��ϵͳ�ṩ��Ĭ�ϵĻָ�����
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			//���ó�ʱ��ʱ��
			method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, timeout);
			//����ͷ��
			method.setRequestHeader("Content-Type", "application/json");
			if (header != null) {
				for (Map.Entry<String, String> next : header.entrySet()) {
					method.setRequestHeader(next.getKey(), next.getValue());
				}
			}
		} catch (IllegalArgumentException e) {
			Logger.error("�Ƿ���URL��" + url);
		} catch (UnsupportedEncodingException e) {
			Logger.error(e);
			e.printStackTrace();
		}

		return method;
	}

	/**
	 * ������ƴװ��url�У�����post����
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static String httpPost(String url, Map<String, String> header, Map<String, String> jsondata) {
		HttpClient httpClient = new HttpClient();
		//�������ӳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		HttpMethod httpmethod = createPostJsonMethod(url, header, TIMEOUT, jsondata);
		if (httpmethod == null) {
			return null;
		}
		int statusCode = 0;
		String resultStr = null;
		try {
			statusCode = httpClient.executeMethod(httpmethod);
			//ֻҪ���������صĲ���200���룬��ͳһ��Ϊ��ȡʧ��
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
		//�������ӳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		HttpMethod httpmethod = new GetMethod(url);
		int statusCode = 0;
		String resultStr = null;
		try {
			statusCode = httpClient.executeMethod(httpmethod);
			//ֻҪ���������صĲ���200���룬��ͳһ��Ϊ��ȡʧ��
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
