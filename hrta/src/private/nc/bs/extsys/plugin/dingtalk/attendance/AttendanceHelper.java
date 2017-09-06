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
	 * ��ȡ��������
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
			throw new BusinessException("���ƶ���Ҫ��ѯ���û���������");
		}
		if (userIds.size() > 50) {
			throw new BusinessException("����Ա��id��಻�ܳ���50��");
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
		 * errcode			������
		 * errmsg			�Է�������ı���������
		 * id				Ψһ��ʾID
		 * groupId			������ID
		 * planId			�Ű�ID
		 * workDate			������
		 * userId			�û�ID
		 * checkType		�������ͣ�OnDuty���ϰ࣬OffDuty���°ࣩ
		 * sourceType		������Դ ��ATM:���ڻ�;BEACON:IBeacon;DING_ATM:�������ڻ�;APP_USER:�û���;APP_BOSS:�ϰ��ǩ;APP_APPROVE:����ϵͳ;SYSTEM:����ϵͳ;APP_AUTO_CHECK:�Զ��򿨣�
		 * timeResult		ʱ������Normal:����;Early:����; Late:�ٵ�;SeriousLate:���سٵ���NotSigned:δ�򿨣�
		 * locationResult	λ�ý����Normal:��Χ�ڣ�Outside:��Χ�⣩
		 * approveId		����������id
		 * baseCheckTime	����ٵ������ˣ���׼ʱ��
		 * userCheckTime	ʵ�ʴ�ʱ��
		 * classId			���ڰ��id��û�еĻ���ʾ�ôδ򿨲����Ű���
		 * isLegal			�Ƿ�Ϸ�
		 * locationMethod	��λ����
		 * deviceId			�豸id
		 * userAddress		�û��򿨵�ַ
		 * userLongitude	�û��򿨾���
		 * userLatitude		�û���γ��
		 * userAccuracy		�û��򿨶�λ����
		 * userSsid			�û���wifi SSID
		 * userMacAddr		�û���wifi Mac��ַ
		 * planCheckTime	�Ű��ʱ��
		 * baseAddress		��׼��ַ
		 * baseLongitude	��׼����
		 * baseLatitude		��׼γ��
		 * baseAccuracy		��׼��λ����
		 * baseSsid			��׼wifi ssid
		 * baseMacAddr		��׼ Mac ��ַ
		 * gmtCreate		����ʱ��
		 * gmtModified		�޸�ʱ��
		 */
	}

	/**
	 * <p>
	 * ���ǩ������
	 * </p>
	 * ����: 1��Ŀǰ����ȡ1000�����ڵ�ǩ�����ݣ������������ID�����Ӳ����µ�user����1000���ᱨ�� </br> 2����ʼʱ��ͽ���ʱ��ļ�����ܴ���45 ��
	 * 
	 * @see https://open-doc.dingtalk.com/docs/doc.htm?treeId=355&articleId=106248&docType=1
	 * @param accessToken ���ýӿ�ƾ֤
	 * @param departmentId ����id��1 ��ʾ�����ţ�
	 * @param startTime ��ʼʱ�䣬��ȷ������
	 * @param endTime ����ʱ�䣬��ȷ�����루Ĭ��Ϊ��ǰʱ�䣩��ʼʱ��ͽ���ʱ��ļ�����ܴ���45 ��
	 * @param offset ֧�ַ�ҳ��ѯ����size ����ͬʱ����ʱ����Ч���˲�������ƫ��������0 ��ʼ
	 * @param size ֧�ַ�ҳ��ѯ����offset ����ͬʱ����ʱ����Ч���˲��������ҳ��С�����100
	 * @param order ����asc Ϊ����desc Ϊ����
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
			url.append(order);//֧�ַ�ҳ��ѯ�����ų�Ա���������Ĭ�ϲ����ǰ��Զ�������entry_asc�����ս��벿�ŵ�ʱ������entry_desc�����ս��벿�ŵ�ʱ�併��modify_asc�����ղ�����Ϣ�޸�ʱ������modify_desc�����ղ�����Ϣ�޸�ʱ�併��custom�����û�����(δ����ʱ����ƴ��)����
		}
		JSONObject response = doGet(url.toString());

		/*
		 * name			��Ա����
		 * userId		Ա��Ψһ��ʶID�������޸ģ�
		 * avatar		ͷ��url
		 * timestamp	ǩ��ʱ��
		 * place		ǩ����ַ
		 * detailPlace	ǩ����ϸ��ַ
		 * remark		ǩ����ע
		 * imageList	ǩ����Ƭurl�б�
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
