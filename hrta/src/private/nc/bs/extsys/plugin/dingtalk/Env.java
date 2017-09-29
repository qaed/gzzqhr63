package nc.bs.extsys.plugin.dingtalk;

import java.io.InputStream;
import java.util.Properties;

import nc.bs.extsys.plugin.dingtalk.workflow.SyncWorkFlow;
import nc.bs.logging.Logger;
import nc.vo.pub.BusinessException;

import com.alibaba.fastjson.JSONObject;

public class Env {
	static {
		try {
			Properties pro = new Properties();
			InputStream in = SyncWorkFlow.class.getClassLoader().getResourceAsStream("dingtalk.properties");
			pro.load(in);
			CORP_ID = pro.getProperty("corp_id");
			CORP_SECRET = pro.getProperty("corp_secret");
			BUSINESS_TRIP_PROCESS_CODE = pro.getProperty("business_trip_process_code");
			STEP_OUT_PROCESS_CODE = pro.getProperty("step_out_process_code");
			in.close();
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	public static final String OAPI_HOST = "https://oapi.dingtalk.com";
	public static final String OA_BACKGROUND_URL = "";
	public static String CORP_ID;//= "dingd3d0f0601fb56c7435c2f4657eb6378f"
	public static String CORP_SECRET;// = "JA2tpOuuq6wbU8Js0wCzny92zhdAWzx-GdNq925lrpbqHKuNRjIoeBVwEepiq1tP"
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
	 * 访问令牌
	 */
	public static JSONObject accessTokenValue = null;
	/**
	 * JS调用令牌
	 */
	public static JSONObject jsticket = null;
	/**
	 * 出差审批process_code
	 */
	public static String BUSINESS_TRIP_PROCESS_CODE;// = "PROC-7693769D-6F18-41D4-8834-B74031D21F5E"
	/**
	 * 外出审批process_code
	 */
	public static String STEP_OUT_PROCESS_CODE;// = "PROC-44208D62-2D57-4110-9601-DD346F95D253"
}
