package nc.bs.extsys.plugin.dingtalk;

import com.alibaba.fastjson.JSONObject;

public class Env {
	public static final String OAPI_HOST = "https://oapi.dingtalk.com";
	public static final String OA_BACKGROUND_URL = "";
	public static final String CORP_ID = "dingd3d0f0601fb56c7435c2f4657eb6378f";
	//���ԣ��в���Ȩ��JA2tpOuuq6wbU8Js0wCzny92zhdAWzx-GdNq925lrpbqHKuNRjIoeBVwEepiq1tP
	//���ԣ��޲���Ȩ��X9RlLmXO7EzGQIy5peHEBrOAaErpp9jSLKL-fuRCm4YcK-cXQr_UF1ZGY8pYo1Hq
	public static final String CORP_SECRET = "JA2tpOuuq6wbU8Js0wCzny92zhdAWzx-GdNq925lrpbqHKuNRjIoeBVwEepiq1tP";
	public static final String SSO_Secret = "";

	public static String suiteTicket;
	public static String authCode;
	public static String suiteToken;

	public static final String CREATE_SUITE_KEY = "suite4xxxxxxxxxxxxxxx";
	public static final String SUITE_KEY = "";
	public static final String SUITE_SECRET = "";
	public static final String TOKEN = "";
	public static final String ENCODING_AES_KEY = "";
	/**
	 * ��������
	 */
	public static JSONObject accessTokenValue = null;
	/**
	 * JS��������
	 */
	public static JSONObject jsticket = null;
	/**
	 * ��������process_code
	 */
	public static String BUSINESS_TRIP_PROCESS_CODE = "PROC-7693769D-6F18-41D4-8834-B74031D21F5E";
	/**
	 * �������process_code
	 */
	public static String STEP_OUT_PROCESS_CODE = "PROC-44208D62-2D57-4110-9601-DD346F95D253";
}