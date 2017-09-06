package nc.bs.extsys.plugin.dingtalk.attendance;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.open.client.api.model.corp.CorpUserDetailList;

import nc.bs.extsys.plugin.dingtalk.Env;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.OApiResultException;
import nc.bs.extsys.plugin.dingtalk.util.HttpHelper;
import nc.vo.pub.BusinessException;

public class AttendanceHelper {

	private static int httpErrortimes = 0;

	/**
	 * 获取考勤数据
	 * 
	 * @see https://open-doc.dingtalk.com/docs/doc.htm?treeId=355&articleId=106662&docType=1
	 * @param accessToken
	 * @param userIds
	 * @param checkDateFrom
	 * @param checkDateTo
	 * @return
	 * @throws Exception
	 */
	public static JSONArray listRecord(String accessToken, List<String> userIds, String checkDateFrom, String checkDateTo) throws Exception {
		if (userIds == null) {
			throw new BusinessException("请制定需要查询的用户考勤数据");
		}
		if (userIds.size() > 50) {
			throw new BusinessException("单次员工id最多不能超过50个");
		}
		String url = Env.OAPI_HOST + "/attendance/listRecord?" + "access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("userIds", userIds);
		if (checkDateFrom != null) {
			args.put("checkDateFrom", checkDateFrom);
		}
		if (checkDateTo != null) {
			args.put("checkDateTo", checkDateTo);
		}
		JSONObject response = doPost(url, args);

		if (response != null && response.containsKey("recordresult")) {
			return response.getJSONArray("recordresult");
		} else {
			throw new OApiResultException("attendance");
		}
		/*
		 * errcode			返回码
		 * errmsg			对返回码的文本描述内容
		 * id				唯一标示ID
		 * groupId			考勤组ID
		 * planId			排班ID
		 * workDate			工作日
		 * userId			用户ID
		 * checkType		考勤类型（OnDuty：上班，OffDuty：下班）
		 * sourceType		数据来源 （ATM:考勤机;BEACON:IBeacon;DING_ATM:钉钉考勤机;APP_USER:用户打卡;APP_BOSS:老板改签;APP_APPROVE:审批系统;SYSTEM:考勤系统;APP_AUTO_CHECK:自动打卡）
		 * timeResult		时间结果（Normal:正常;Early:早退; Late:迟到;SeriousLate:严重迟到；NotSigned:未打卡）
		 * locationResult	位置结果（Normal:范围内；Outside:范围外）
		 * approveId		关联的审批id
		 * baseCheckTime	计算迟到和早退，基准时间
		 * userCheckTime	实际打卡时间
		 * classId			考勤班次id，没有的话表示该次打卡不在排班内
		 * isLegal			是否合法
		 * locationMethod	定位方法
		 * deviceId			设备id
		 * userAddress		用户打卡地址
		 * userLongitude	用户打卡经度
		 * userLatitude		用户打卡纬度
		 * userAccuracy		用户打卡定位精度
		 * userSsid			用户打卡wifi SSID
		 * userMacAddr		用户打卡wifi Mac地址
		 * planCheckTime	排班打卡时间
		 * baseAddress		基准地址
		 * baseLongitude	基准经度
		 * baseLatitude		基准纬度
		 * baseAccuracy		基准定位精度
		 * baseSsid			基准wifi ssid
		 * baseMacAddr		基准 Mac 地址
		 * gmtCreate		创建时间
		 * gmtModified		修改时间
		 */
	}

	/**
	 * <p>
	 * 获得签到数据
	 * </p>
	 * 限制: 1、目前最多获取1000人以内的签到数据，如果所传部门ID及其子部门下的user超过1000，会报错 </br> 2、开始时间和结束时间的间隔不能大于45 天
	 * 
	 * @see https://open-doc.dingtalk.com/docs/doc.htm?treeId=355&articleId=106248&docType=1
	 * @param accessToken 调用接口凭证
	 * @param departmentId 部门id（1 表示根部门）
	 * @param startTime 开始时间，精确到毫秒
	 * @param endTime 结束时间，精确到毫秒（默认为当前时间）开始时间和结束时间的间隔不能大于45 天
	 * @param offset 支持分页查询，与size 参数同时设置时才生效，此参数代表偏移量，从0 开始
	 * @param size 支持分页查询，与offset 参数同时设置时才生效，此参数代表分页大小，最大100
	 * @param order 排序，asc 为正序，desc 为倒序
	 * @return
	 * @throws OApiException
	 */
	public static JSONArray listCheckinRecord(String accessToken, String departmentId, Long startTime, Long endTime, Long offset, Integer size, String order)
			throws OApiException {
		StringBuilder url = new StringBuilder();
		url.append(Env.OAPI_HOST + "/checkin/record?" + "access_token=");
		url.append(accessToken);
		url.append("&department_id=");
		url.append(departmentId);
		url.append("&start_time=");
		url.append(startTime);
		if (endTime != null) {
			url.append("&end_time=");
			url.append(endTime);
		}
		if (offset != null) {
			url.append("&offset=");
			url.append(offset);
		}
		if (size != null) {
			url.append("&size=");
			url.append(size);
		}
		if (order != null) {
			url.append("&order=");
			url.append(order);//支持分页查询，部门成员的排序规则，默认不传是按自定义排序；entry_asc代表按照进入部门的时间升序，entry_desc代表按照进入部门的时间降序，modify_asc代表按照部门信息修改时间升序，modify_desc代表按照部门信息修改时间降序，custom代表用户定义(未定义时按照拼音)排序
		}
		JSONObject response = doGet(url.toString());

		/*
		 * name			成员名称
		 * userId		员工唯一标识ID（不可修改）
		 * avatar		头像url
		 * timestamp	签到时间
		 * place		签到地址
		 * detailPlace	签到详细地址
		 * remark		签到备注
		 * imageList	签到照片url列表
		 */
		if (response != null && response.containsKey("data")) {
			return response.getJSONArray("data");
		} else {
			throw new OApiResultException("checkinData");
		}

	}

	private static JSONObject doPost(String url, JSONObject args) throws OApiException {
		JSONObject response = null;
		try {
			response = HttpHelper.httpPost(url, args);

		} catch (OApiException e) {
			if ((e.getMessage().contains("-1") || e.getMessage().contains("-2")) && httpErrortimes < 10) {
				httpErrortimes++;
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				response = doPost(url, args);
			} else {
				httpErrortimes = 0;
				throw e;
			}
		}
		return response;
	}

	private static JSONObject doGet(String url) throws OApiException {
		JSONObject response = null;
		try {
			response = HttpHelper.httpGet(url);

		} catch (OApiException e) {
			if ((e.getMessage().contains("-1") || e.getMessage().contains("-2")) && httpErrortimes < 10) {
				httpErrortimes++;
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				response = doGet(url);
			} else {
				httpErrortimes = 0;
				throw e;
			}
		}
		return response;
	}
}
