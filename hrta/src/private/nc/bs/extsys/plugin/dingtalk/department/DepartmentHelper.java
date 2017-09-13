package nc.bs.extsys.plugin.dingtalk.department;

import java.util.ArrayList;
import java.util.List;

import nc.bs.extsys.plugin.dingtalk.Env;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.OApiResultException;
import nc.bs.extsys.plugin.dingtalk.util.HttpHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.open.client.ServiceFactory;
import com.dingtalk.open.client.api.model.corp.Department;
import com.dingtalk.open.client.api.service.corp.CorpDepartmentService;
import com.dingtalk.open.client.common.SdkInitException;
import com.dingtalk.open.client.common.ServiceException;
import com.dingtalk.open.client.common.ServiceNotExistException;

public class DepartmentHelper {
	/**
	 * <p>
	 * ��������
	 * </p>
	 * 
	 * @param accessToken ���ýӿ�ƾ֤
	 * @param name �������ơ���������Ϊ1~64���ַ�
	 * @param parentId ������id��������idΪ1
	 * @param order �ڸ������еĴ���ֵ��orderֵС������ǰ
	 * @param createDeptGroup �Ƿ񴴽�һ�������˲��ŵ���ҵȺ��Ĭ��Ϊfalse
	 * @param deptHiding �Ƿ����ز���, true��ʾ����, false��ʾ��ʾ
	 * @param deptPerimits ���Բ鿴ָ�����ز��ŵ����������б�����������أ����ֵ��Ч��ȡֵΪ�����Ĳ���id��ɵĵ��ַ�����ʹ�� | ���Ž��зָ�
	 * @param userPerimits ���Բ鿴ָ�����ز��ŵ�������Ա�б�����������أ����ֵ��Ч��ȡֵΪ��������Աuserid��ɵĵ��ַ�����ʹ��| ���Ž��зָ�
	 * @param outerDept �Ƿ񱾲��ŵ�Ա�����ɼ�Ա���Լ�, Ϊtrueʱ��������Ա��Ĭ��ֻ�ܿ���Ա���Լ�
	 * @param outerPermitDepts �����ŵ�Ա�����ɼ�Ա���Լ�Ϊtrueʱ���������ö���ɼ����ţ�ֵΪ����id��ɵĵ��ַ�����ʹ��|���Ž��зָ�
	 * @param outerPermitUsers �����ŵ�Ա�����ɼ�Ա���Լ�Ϊtrueʱ���������ö���ɼ���Ա��ֵΪuserid��ɵĵ��ַ�����ʹ��|���Ž��зָ�
	 * @return id ����ID
	 * @throws Exception
	 */
	public static String createDepartment(String accessToken, String name, String parentId, String order, Boolean createDeptGroup, Boolean deptHiding, String deptPerimits, String userPerimits, Boolean outerDept, String outerPermitDepts, String outerPermitUsers)
			throws Exception {

		//		CorpDepartmentService corpDepartmentService = ServiceFactory.getInstance().getOpenService(CorpDepartmentService.class);
		//		return corpDepartmentService.deptCreate(accessToken, name, parentId, order, createDeptGroup);

		String url = Env.OAPI_HOST + "/department/create?" + "access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("name", name);
		args.put("parentid", parentId == null || !"".equals(parentId.trim()) ? 1 : Long.parseLong(parentId));
		if (order != null) {
			args.put("order", order);
		}
		if (createDeptGroup != null) {
			args.put("createDeptGroup", createDeptGroup);
		}
		if (deptHiding != null) {
			args.put("deptHiding", deptHiding);
		}
		if (deptPerimits != null) {
			args.put("deptPerimits", deptPerimits);
		}
		if (userPerimits != null) {
			args.put("userPerimits", userPerimits);
		}
		if (outerDept != null) {
			args.put("outerDept", outerDept);
		}
		if (outerPermitDepts != null) {
			args.put("outerPermitDepts", outerPermitDepts);
		}
		if (outerPermitUsers != null) {
			args.put("outerPermitUsers", outerPermitUsers);
		}

		JSONObject response = HttpHelper.httpPost(url, args);
		if (response != null && response.containsKey("id")) {
			return response.getString("id");
		} else {
			throw new OApiResultException("id");
		}
	}

	/**
	 * <p>
	 * ��ȡ��������
	 * </p>
	 * 
	 * @param accessToken
	 * @param id
	 * @return
	 * @throws ServiceNotExistException
	 * @throws SdkInitException
	 * @throws ServiceException
	 * @throws OApiException
	 */
	public static Department getDepartment(String accessToken, String id) throws OApiResultException {
		//		CorpDepartmentService corpDepartmentService = ServiceFactory.getInstance().getOpenService(CorpDepartmentService.class);
		//		List<Department> deptList = corpDepartmentService.getDeptList(accessToken, parentDeptId);
		String url = Env.OAPI_HOST + "/department/get?" + "access_token=" + accessToken + "&id=" + id;
		try {
			JSONObject response = HttpHelper.httpGet(url);//��������ڻᱨ��"errcode":60003,"errmsg":"���Ų�����"
			Department dept = JSON.toJavaObject(response, Department.class);
			return dept;
		} catch (Exception e) {
			throw new OApiResultException("department");
		}
	}

	public static List<Department> listDepartments(String accessToken, String parentDeptId) throws ServiceNotExistException,
			SdkInitException, ServiceException, OApiException {
		//		CorpDepartmentService corpDepartmentService = ServiceFactory.getInstance().getOpenService(CorpDepartmentService.class);
		//		List<Department> deptList = corpDepartmentService.getDeptList(accessToken, parentDeptId);
		String url = Env.OAPI_HOST + "/department/list?" + "access_token=" + accessToken;
		if (parentDeptId != null) {
			url += "&id=" + parentDeptId;
		}
		JSONObject response = HttpHelper.httpGet(url);
		if (response != null && response.containsKey("department")) {
			JSONArray arr = response.getJSONArray("department");
			List<Department> list = new ArrayList<Department>();
			for (int i = 0; i < arr.size(); i++) {
				list.add(arr.getObject(i, Department.class));
			}
			return list;
		} else {
			throw new OApiResultException("department");
		}
	}

	/**
	 * @param accessToken ���ýӿ�ƾ֤
	 * @param id ����id����ע������ɾ�������ţ�����ɾ�������Ӳ��� ����Ա�Ĳ��ţ�
	 * @throws Exception
	 */
	public static void deleteDepartment(String accessToken, Long id) throws Exception {
		//		CorpDepartmentService corpDepartmentService = ServiceFactory.getInstance().getOpenService(CorpDepartmentService.class);
		//		corpDepartmentService.deptDelete(accessToken, id);
		String url = Env.OAPI_HOST + "/department/delete?" + "access_token=" + accessToken + "&id=" + id;
		HttpHelper.httpGet(url);
	}

	/**
	 * @param accessToken ���ýӿ�ƾ֤
	 * @param id ���²���id
	 * @param name �������ơ���������Ϊ1~64���ַ�
	 * @param parentId ������id��������idΪ1
	 * @param order �ڸ������еĴ���ֵ��orderֵС������ǰ
	 * @param createDeptGroup �Ƿ񴴽�һ�������˲��ŵ���ҵȺ
	 * @param autoAddUser ��������˼��벿���Ƿ���Զ����벿��Ⱥ
	 * @param deptManagerUseridList ���ŵ������б�, ȡֵΪ�����ܵ�userid��ɵ��ַ��� ����ͬ��useridʹ�á�| ���Ž��зָ�
	 * @param deptHiding �Ƿ����ز���, true��ʾ����, false��ʾ��ʾ
	 * @param deptPerimits ���Բ鿴ָ�����ز��ŵ����������б�����������أ� ���ֵ��Ч��ȡֵΪ�����Ĳ���id��ɵĵ��ַ�����ʹ�� | ���Ž��зָ�
	 * @param userPerimits ���Բ鿴ָ�����ز��ŵ�������Ա�б�����������أ� ���ֵ��Ч ��ȡֵΪ��������Աuserid��ɵĵ��ַ�����ʹ��| ���Ž��зָ�
	 * @param outerDept �Ƿ񱾲��ŵ�Ա�����ɼ�Ա���Լ�, Ϊtrueʱ��������Ա��Ĭ��ֻ�ܿ���Ա���Լ�
	 * @param outerPermitDepts �����ŵ�Ա�����ɼ�Ա���Լ�Ϊtrueʱ�� �������ö���ɼ����� ��ֵΪ����id��ɵĵ��ַ�����ʹ��|���Ž��зָ�
	 * @param outerPermitUsers �����ŵ�Ա�����ɼ�Ա���Լ�Ϊtrueʱ�� �������ö���ɼ���Ա ��ֵΪuserid��ɵĵ��ַ�����ʹ��|���Ž��зָ�
	 * @param orgDeptOwner ��ҵȺȺ��
	 * @throws Exception
	 */
	public static void updateDepartment(String accessToken, long id, String name, String parentId, String order, Boolean createDeptGroup, Boolean autoAddUser, String deptManagerUseridList, Boolean deptHiding, String deptPerimits, String userPerimits, Boolean outerDept, String outerPermitDepts, String outerPermitUsers, String orgDeptOwner)
			throws Exception {
		//		CorpDepartmentService corpDepartmentService = ServiceFactory.getInstance().getOpenService(CorpDepartmentService.class);
		//		corpDepartmentService.deptUpdate(accessToken, id, name, parentId, order, createDeptGroup, autoAddUser, deptManagerUseridList, deptHiding, deptPerimits, userPerimits, outerDept, outerPermitDepts, outerPermitUsers, orgDeptOwner);

		String url = Env.OAPI_HOST + "/department/update?" + "access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("id", id);
		args.put("name", name);
		args.put("parentid", parentId == null ? 1 : Long.parseLong(parentId));
		if (order != null) {
			args.put("order", order);
		}
		if (createDeptGroup != null) {
			args.put("createDeptGroup", createDeptGroup);
		}
		if (autoAddUser != null) {
			args.put("autoAddUser", autoAddUser);
		}
		if (deptManagerUseridList != null && !"".equals(deptManagerUseridList.trim())) {
			args.put("deptManagerUseridList", deptManagerUseridList);
		}
		if (deptHiding != null) {
			args.put("deptHiding", deptHiding);
		}
		if (deptPerimits != null) {
			args.put("deptPerimits", deptPerimits);
		}
		if (userPerimits != null) {
			args.put("userPerimits", userPerimits);
		}
		if (outerDept != null) {
			args.put("outerDept", outerDept);
		}
		if (outerPermitDepts != null) {
			args.put("outerPermitDepts", outerPermitDepts);
		}
		if (outerPermitUsers != null) {
			args.put("outerPermitUsers", outerPermitUsers);
		}
		if (orgDeptOwner != null) {
			args.put("orgDeptOwner", orgDeptOwner);
		}

		HttpHelper.httpPost(url, args);
	}

}
