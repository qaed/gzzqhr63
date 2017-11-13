package nc.bs.extsys.plugin.dingtalk.user;

import java.util.List;
import java.util.Map;

import nc.bs.extsys.plugin.dingtalk.Env;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.OApiResultException;
import nc.bs.extsys.plugin.dingtalk.util.FileUtils;
import nc.bs.extsys.plugin.dingtalk.util.HttpHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.open.client.ServiceFactory;
import com.dingtalk.open.client.api.model.corp.CorpUserBaseInfo;
import com.dingtalk.open.client.api.model.corp.CorpUserDetail;
import com.dingtalk.open.client.api.model.corp.CorpUserDetailList;
import com.dingtalk.open.client.api.model.corp.CorpUserList;
import com.dingtalk.open.client.api.service.corp.CorpUserService;

public class UserHelper {

	/**
	 * ������Ա
	 * 
	 * @param accessToken
	 * @param userDetail
	 * @throws Exception
	 */
	public static String createUser(String accessToken, CorpUserDetail userDetail) throws Exception {
		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		JSONObject js = (JSONObject) JSONObject.parse(userDetail.getOrderInDepts());
		Map<Long, Long> orderInDepts = FileUtils.toHashMap(js);

		//		corpUserService.createCorpUser(accessToken, userDetail.getUserid(), userDetail.getName(), orderInDepts, userDetail.getDepartment(), userDetail.getPosition(), userDetail.getMobile(), userDetail.getTel(), userDetail.getWorkPlace(), userDetail.getRemark(), userDetail.getEmail(), userDetail.getJobnumber(), userDetail.getIsHide(), userDetail.getSenior(), userDetail.getExtattr());
		/////////////////////////////////
		String url = Env.OAPI_HOST + "/user/create?" + "access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("userid", userDetail.getUserid());//Ա��Ψһ��ʶID�������޸ģ�����ҵ�ڱ���Ψһ������Ϊ1~64���ַ���������������������Զ�����һ��userid
		args.put("name", userDetail.getName());//��Ա���ơ�����Ϊ1~64���ַ�
		if (orderInDepts != null) {
			args.put("orderInDepts", orderInDepts);//�ڶ�Ӧ�Ĳ����е�����, Map�ṹ��json�ַ���, key�ǲ��ŵ�Id, value����Ա��������ŵ�����ֵ
		}
		args.put("department", userDetail.getDepartment());//�������ͣ���������ֵΪ���ͣ���Ա��������id�б�
		if (userDetail.getPosition() != null) {
			args.put("position", userDetail.getPosition());//ְλ��Ϣ������Ϊ0~64���ַ�
		}
		args.put("mobile", userDetail.getMobile());//�ֻ����롣��ҵ�ڱ���Ψһ
		if (userDetail.getTel() != null) {
			args.put("tel", userDetail.getTel());//�ֻ��ţ�����Ϊ0~50���ַ�
		}
		if (userDetail.getWorkPlace() != null) {
			args.put("workPlace", userDetail.getWorkPlace());//�칫�ص㣬����Ϊ0~50���ַ�
		}
		if (userDetail.getRemark() != null) {
			args.put("remark", userDetail.getRemark());//��ע������Ϊ0~1000���ַ�
		}
		if (userDetail.getEmail() != null) {
			args.put("email", userDetail.getEmail());//���䡣����Ϊ0~64���ַ�����ҵ�ڱ���Ψһ
		}
		if (userDetail.getJobnumber() != null) {
			args.put("jobnumber", userDetail.getJobnumber());//Ա�����š���Ӧ��ʾ��OA��̨�Ϳͻ��˸������ϵĹ�����Ŀ������Ϊ0~64���ַ�
		}
		if (userDetail.getIsHide() != null) {
			args.put("isHide", userDetail.getIsHide());//�Ƿ��������, true��ʾ����, false��ʾ�����ء������ֻ��ź��ֻ����ڸ�������ҳ���أ����Կɶ��䷢DING�����𶤶��������绰��
		}
		if (userDetail.getIsAdmin() != null) {
			args.put("isSenior", userDetail.getIsAdmin());//�Ƿ�߹�ģʽ��true��ʾ�ǣ�false��ʾ���ǡ��������ֻ����������Ա�����ء���ͨԱ���޷����䷢DING�����𶤶��������绰���߹�֮�䲻��Ӱ�졣
		}
		if (userDetail.getExtattr() != null) {
			args.put("extattr", userDetail.getExtattr());//��չ���ԣ��������ö�������(���ֻ������ֻ����ʾ10����չ���ԣ�������ʾ��Щ���ԣ��뵽OA�����̨->����->ͨѶ¼��Ϣ���ú�OA�����̨->����->�ֻ�����ʾ��Ϣ����)
		}

		JSONObject response = HttpHelper.httpPost(url, args);
		if (response != null && response.containsKey("userid")) {
			return response.getString("userid");
		} else {
			throw new OApiResultException("userid");
		}
		/////////////////////////////

		//		String url = Env.OAPI_HOST + "/user/create?" +
		//				"access_token=" + accessToken;
		//		HttpHelper.httpPost(url, user);
	}

	/**
	 * ���³�Ա
	 * 
	 * @param accessToken
	 * @param userDetail
	 * @throws Exception
	 */
	public static void updateUser(String accessToken, CorpUserDetail userDetail) throws Exception {
		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		JSONObject js = (JSONObject) JSONObject.parse(userDetail.getOrderInDepts());
		Map<Long, Long> orderInDepts = FileUtils.toHashMap(js);

		//		corpUserService.createCorpUser(accessToken, userDetail.getUserid(), userDetail.getName(), orderInDepts, userDetail.getDepartment(), userDetail.getPosition(), userDetail.getMobile(), userDetail.getTel(), userDetail.getWorkPlace(), userDetail.getRemark(), userDetail.getEmail(), userDetail.getJobnumber(), userDetail.getIsHide(), userDetail.getSenior(), userDetail.getExtattr());
		/////////////////////////////////
		String url = Env.OAPI_HOST + "/user/update?" + "access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("userid", userDetail.getUserid());//Ա��Ψһ��ʶID�������޸ģ�����ҵ�ڱ���Ψһ������Ϊ1~64���ַ���������������������Զ�����һ��userid
		args.put("name", userDetail.getName());//��Ա���ơ�����Ϊ1~64���ַ�

		if (userDetail.getDepartment() != null) {
			args.put("department", userDetail.getDepartment());//�������ͣ���������ֵΪ���ͣ���Ա��������id�б�
		}
		if (orderInDepts != null) {
			args.put("orderInDepts", orderInDepts);//�ڶ�Ӧ�Ĳ����е�����, Map�ṹ��json�ַ���, key�ǲ��ŵ�Id, value����Ա��������ŵ�����ֵ
		}
		if (userDetail.getPosition() != null) {
			args.put("position", userDetail.getPosition());//ְλ��Ϣ������Ϊ0~64���ַ�
		}
		args.put("mobile", userDetail.getMobile());//�ֻ����롣��ҵ�ڱ���Ψһ
		if (userDetail.getTel() != null) {
			args.put("tel", userDetail.getTel());//�ֻ��ţ�����Ϊ0~50���ַ�
		}
		if (userDetail.getWorkPlace() != null) {
			args.put("workPlace", userDetail.getWorkPlace());//�칫�ص㣬����Ϊ0~50���ַ�
		}
		if (userDetail.getRemark() != null) {
			args.put("remark", userDetail.getRemark());//��ע������Ϊ0~1000���ַ�
		}
		if (userDetail.getEmail() != null) {
			args.put("email", userDetail.getEmail());//���䡣����Ϊ0~64���ַ�����ҵ�ڱ���Ψһ
		}
		if (userDetail.getJobnumber() != null) {
			args.put("jobnumber", userDetail.getJobnumber());//Ա�����š���Ӧ��ʾ��OA��̨�Ϳͻ��˸������ϵĹ�����Ŀ������Ϊ0~64���ַ�
		}
		if (userDetail.getIsHide() != null) {
			args.put("isHide", userDetail.getIsHide());//�Ƿ��������, true��ʾ����, false��ʾ�����ء������ֻ��ź��ֻ����ڸ�������ҳ���أ����Կɶ��䷢DING�����𶤶��������绰��
		}
		if (userDetail.getIsAdmin() != null) {
			args.put("isSenior", userDetail.getIsAdmin());//�Ƿ�߹�ģʽ��true��ʾ�ǣ�false��ʾ���ǡ��������ֻ����������Ա�����ء���ͨԱ���޷����䷢DING�����𶤶��������绰���߹�֮�䲻��Ӱ�졣
		}
		if (userDetail.getExtattr() != null) {
			args.put("extattr", userDetail.getExtattr());//��չ���ԣ��������ö�������(���ֻ������ֻ����ʾ10����չ���ԣ�������ʾ��Щ���ԣ��뵽OA�����̨->����->ͨѶ¼��Ϣ���ú�OA�����̨->����->�ֻ�����ʾ��Ϣ����)
		}

		HttpHelper.httpPost(url, args);
		//		corpUserService.updateCorpUser(accessToken, userDetail.getUserid(), userDetail.getName(), orderInDepts, userDetail.getDepartment(), userDetail.getPosition(), userDetail.getMobile(), userDetail.getTel(), userDetail.getWorkPlace(), userDetail.getRemark(), userDetail.getEmail(), userDetail.getJobnumber(), userDetail.getIsHide(), userDetail.getSenior(), userDetail.getExtattr());
	}

	//ɾ����Ա
	public static void deleteUser(String accessToken, String userid) throws Exception {
		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		corpUserService.deleteCorpUser(accessToken, userid);
		String url = Env.OAPI_HOST + "/user/delete?" + "access_token=" + accessToken + "&userid=" + userid;
		HttpHelper.httpGet(url);
	}

	//����ɾ����Ա
	public static void batchDeleteUser(String accessToken, List<String> useridlist) throws Exception {
		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		corpUserService.batchdeleteCorpUserListByUserids(accessToken, useridlist);

		String url = Env.OAPI_HOST + "/user/batchdelete?" + "access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("useridlist", useridlist);//Ա��UserID�б��б�����1��20֮��
		HttpHelper.httpPost(url, args);
	}

	//��ȡ��Ա����
	public static CorpUserDetail getUser(String accessToken, String userid) throws Exception {

		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		return corpUserService.getCorpUser(accessToken, userid);
		String url = Env.OAPI_HOST + "/user/get?" + "access_token=" + accessToken + "&userid=" + userid;
		return JSON.toJavaObject(HttpHelper.httpGet(url), CorpUserDetail.class);
	}

	//��ȡ���ų�Ա
	public static CorpUserList getDepartmentUser(String accessToken, long departmentId, Long offset, Integer size, String order)
			throws Exception {

		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		return corpUserService.getCorpUserSimpleList(accessToken, departmentId, offset, size, order);
		StringBuilder url = new StringBuilder();
		url.append(Env.OAPI_HOST + "/user/simplelist?" + "access_token=");
		url.append(accessToken);
		url.append("&department_id=");
		url.append(departmentId);//��ȡ�Ĳ���id

		if (offset != null) {
			url.append("&offset=");
			url.append(offset);//֧�ַ�ҳ��ѯ����size����ͬʱ����ʱ����Ч���˲�������ƫ����
		}
		if (size != null) {
			url.append("&size=");
			url.append(size);//֧�ַ�ҳ��ѯ����offset����ͬʱ����ʱ����Ч���˲��������ҳ��С�����100
		}
		if (order != null) {
			url.append("&order=");
			url.append(order);//֧�ַ�ҳ��ѯ�����ų�Ա���������Ĭ�ϲ����ǰ��Զ�������entry_asc�����ս��벿�ŵ�ʱ������entry_desc�����ս��벿�ŵ�ʱ�併��modify_asc�����ղ�����Ϣ�޸�ʱ������modify_desc�����ղ�����Ϣ�޸�ʱ�併��custom�����û�����(δ����ʱ����ƴ��)����
		}
		return JSON.toJavaObject(HttpHelper.httpGet(url.toString()), CorpUserList.class);
	}

	//��ȡ���ų�Ա�����飩
	public static CorpUserDetailList getUserDetails(String accessToken, long departmentId, Long offset, Integer size, String order)
			throws Exception {

		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		return corpUserService.getCorpUserList(accessToken, departmentId, offset, size, order);
		StringBuilder url = new StringBuilder();
		url.append(Env.OAPI_HOST + "/user/list?" + "access_token=");
		url.append(accessToken);
		url.append("&department_id=");
		url.append(departmentId);//��ȡ�Ĳ���id

		if (offset != null) {
			url.append("&offset=");
			url.append(offset);//֧�ַ�ҳ��ѯ����size����ͬʱ����ʱ����Ч���˲�������ƫ����
		}
		if (size != null) {
			url.append("&size=");
			url.append(size);//֧�ַ�ҳ��ѯ����offset����ͬʱ����ʱ����Ч���˲��������ҳ��С�����100
		}
		if (order != null) {
			url.append("&order=");
			url.append(order);//֧�ַ�ҳ��ѯ�����ų�Ա���������Ĭ�ϲ����ǰ��Զ�������entry_asc�����ս��벿�ŵ�ʱ������entry_desc�����ս��벿�ŵ�ʱ�併��modify_asc�����ղ�����Ϣ�޸�ʱ������modify_desc�����ղ�����Ϣ�޸�ʱ�併��custom�����û�����(δ����ʱ����ƴ��)����
		}
		return JSON.toJavaObject(HttpHelper.httpGet(url.toString()), CorpUserDetailList.class);
	}

	/**
	 * δ����
	 * 
	 * @param accessToken
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static CorpUserBaseInfo getUserInfo(String accessToken, String code) throws Exception {

		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		return corpUserService.getUserinfo(accessToken, code);
	}

	/**
	 * δ����
	 * 
	 * @param ssoToken
	 * @param code
	 * @return
	 * @throws OApiException
	 */
	public static JSONObject getAgentUserInfo(String ssoToken, String code) throws OApiException {

		String url = Env.OAPI_HOST + "/sso/getuserinfo?" + "access_token=" + ssoToken + "&code=" + code;
		JSONObject response = HttpHelper.httpGet(url);
		return response;
	}

}
