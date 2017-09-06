package nc.bs.extsys.plugin.dingtalk.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Timer;

import javax.servlet.http.HttpServletRequest;

import nc.bs.extsys.plugin.dingtalk.Env;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.OApiResultException;
import nc.bs.extsys.plugin.dingtalk.util.FileUtils;
import nc.bs.extsys.plugin.dingtalk.util.HttpHelper;
import nc.bs.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.open.client.ServiceFactory;
import com.dingtalk.open.client.api.model.corp.JsapiTicket;
import com.dingtalk.open.client.api.service.corp.CorpConnectionService;
import com.dingtalk.open.client.api.service.corp.JsapiService;
import com.dingtalk.open.client.common.SdkInitException;
import com.dingtalk.open.client.common.ServiceException;
import com.dingtalk.open.client.common.ServiceNotExistException;

public class AuthHelper {

	// public static String jsapiTicket = null;
	// public static String accessToken = null;
	public static Timer timer = null;
	// ������1Сʱ50����
	public static final long cacheTime = 1000 * 60 * 55 * 2;
	public static long currentTime = 0 + cacheTime + 1;
	public static long lastTime = 0;
	public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * �ڴ˷����У�Ϊ�˱���Ƶ����ȡaccess_token��
	 * �ھ�����һ�λ�ȡaccess_tokenʱ��������Сʱ֮�ڵ������
	 * ��ֱ�Ӵӳ־û��洢�ж�ȡaccess_token
	 * 
	 * ��Ϊaccess_token��jsapi_ticket�Ĺ���ʱ�䶼��7200��
	 * �����ڻ�ȡaccess_token��ͬʱҲȥ��ȡ��jsapi_ticket
	 * ע��jsapi_ticket����ǰ��ҳ��JSAPI��Ȩ����֤���õ�ʱ����Ҫʹ�õ�
	 * ������Ϣ��鿴�������ĵ�--Ȩ����֤����
	 */
	public static String getAccessToken() throws OApiException {
		long curTime = System.currentTimeMillis();
		//		JSONObject accessTokenValue = (JSONObject) FileUtils.getValue("accesstoken", Env.CORP_ID);
		JSONObject accessTokenValue = Env.accessTokenValue != null ? Env.accessTokenValue.getJSONObject(Env.CORP_ID) : null;
		String accToken = "";
		String jsTicket = "";
		JSONObject jsontemp = new JSONObject();
		if (accessTokenValue == null || curTime - accessTokenValue.getLong("begin_time") >= cacheTime) {
			//			ServiceFactory serviceFactory = ServiceFactory.getInstance();
			//	        CorpConnectionService corpConnectionService = serviceFactory.getOpenService(CorpConnectionService.class);
			//	        accToken = corpConnectionService.getCorpToken(Env.CORP_ID, Env.CORP_SECRET);
			String url = Env.OAPI_HOST + "/gettoken?" + "corpid=" + Env.CORP_ID + "&corpsecret=" + Env.CORP_SECRET;
			JSONObject response = HttpHelper.httpGet(url);
			if (response.containsKey("access_token")) {
				accToken = response.getString("access_token");
			} else {
				throw new OApiResultException("access_token");
			}
			// save accessToken
			JSONObject jsonAccess = new JSONObject();
			jsontemp.clear();
			jsontemp.put("access_token", accToken);
			jsontemp.put("begin_time", curTime);
			jsonAccess.put(Env.CORP_ID, jsontemp);
			//			FileUtils.write2File(jsonAccess, "accesstoken");
			//			Env.setAccessTokenValue(jsonAccess);
			Env.accessTokenValue = jsonAccess;

			if (accToken.length() > 0) {

				//				JsapiService jsapiService = serviceFactory.getOpenService(JsapiService.class);
				//				JsapiTicket JsapiTicket = jsapiService.getJsapiTicket(accToken, "jsapi");
				url = Env.OAPI_HOST + "/get_jsapi_ticket?" + "access_token=" + accToken;
				JSONObject result = HttpHelper.httpGet(url);
				JsapiTicket JsapiTicket = JSON.toJavaObject(result, JsapiTicket.class);
				jsTicket = JsapiTicket.getTicket();
				JSONObject jsonTicket = new JSONObject();
				JSONObject jsontemp1 = new JSONObject();
				jsontemp1.clear();
				jsontemp1.put("ticket", jsTicket);
				jsontemp1.put("begin_time", curTime);
				jsonTicket.put(Env.CORP_ID, jsontemp1);
				//				FileUtils.write2File(jsonTicket, "jsticket");
				//				Env.setJsticket(jsonTicket);
				Env.jsticket = jsonTicket;
			}

		} else {
			return accessTokenValue.getString("access_token");
		}

		return accToken;
	}

	// ����������£�jsapi_ticket����Ч��Ϊ7200�룬���Կ�������Ҫ��ĳ���ط����һ����ʱ��������ȥ����jsapi_ticket
	public static String getJsapiTicket(String accessToken) throws OApiException {
		//		JSONObject jsTicketValue = (JSONObject) FileUtils.getValue("jsticket", Env.CORP_ID);
		JSONObject jsTicketValue = Env.jsticket != null ? Env.jsticket.getJSONObject(Env.CORP_ID) : null;
		long curTime = System.currentTimeMillis();
		String jsTicket = "";

		if (jsTicketValue == null || curTime - jsTicketValue.getLong("begin_time") >= cacheTime) {
			ServiceFactory serviceFactory;
			try {
				serviceFactory = ServiceFactory.getInstance();
				//				JsapiService jsapiService = serviceFactory.getOpenService(JsapiService.class);
				//				JsapiTicket JsapiTicket = jsapiService.getJsapiTicket(accessToken, "jsapi");
				String url = Env.OAPI_HOST + "/get_jsapi_ticket?" + "access_token=" + accessToken;
				JSONObject result = HttpHelper.httpGet(url);
				JsapiTicket JsapiTicket = JSON.toJavaObject(result, JsapiTicket.class);
				jsTicket = JsapiTicket.getTicket();

				JSONObject jsonTicket = new JSONObject();
				JSONObject jsontemp = new JSONObject();
				jsontemp.clear();
				jsontemp.put("ticket", jsTicket);
				jsontemp.put("begin_time", curTime);
				jsonTicket.put(Env.CORP_ID, jsontemp);
				//				FileUtils.write2File(jsonTicket, "jsticket");
				//				Env.setJsticket(jsonTicket);
				Env.jsticket = jsonTicket;
			} catch (SdkInitException e) {
				Logger.error(e);
				e.printStackTrace();
			}
			return jsTicket;
		} else {
			return jsTicketValue.getString("ticket");
		}
	}

	public static String sign(String ticket, String nonceStr, long timeStamp, String url) throws OApiException {
		String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp) + "&url=" + url;
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			sha1.reset();
			sha1.update(plain.getBytes("UTF-8"));
			return bytesToHex(sha1.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new OApiResultException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new OApiResultException(e.getMessage());
		}
	}

	private static String bytesToHex(byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String getConfig(HttpServletRequest request) {
		String urlString = request.getRequestURL().toString();
		String queryString = request.getQueryString();

		String queryStringEncode = null;
		String url;
		if (queryString != null) {
			queryStringEncode = URLDecoder.decode(queryString);
			url = urlString + "?" + queryStringEncode;
		} else {
			url = urlString;
		}

		String nonceStr = "abcdefg";
		long timeStamp = System.currentTimeMillis() / 1000;
		String signedUrl = url;
		String accessToken = null;
		String ticket = null;
		String signature = null;
		String agentid = null;

		try {
			accessToken = AuthHelper.getAccessToken();

			ticket = AuthHelper.getJsapiTicket(accessToken);
			signature = AuthHelper.sign(ticket, nonceStr, timeStamp, signedUrl);
			agentid = "";

		} catch (OApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String configValue =
				"{jsticket:'" + ticket + "',signature:'" + signature + "',nonceStr:'" + nonceStr + "',timeStamp:'" + timeStamp + "',corpId:'" + Env.CORP_ID + "',agentid:'" + agentid + "'}";
		System.out.println(configValue);
		return configValue;
	}

	public static String getSsoToken() throws OApiException {
		String url = "https://oapi.dingtalk.com/sso/gettoken?corpid=" + Env.CORP_ID + "&corpsecret=" + Env.SSO_Secret;
		JSONObject response = HttpHelper.httpGet(url);
		String ssoToken;
		if (response.containsKey("access_token")) {
			ssoToken = response.getString("access_token");
		} else {
			throw new OApiResultException("Sso_token");
		}
		return ssoToken;

	}

}
