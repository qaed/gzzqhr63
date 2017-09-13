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
	 * 创建部门
	 * </p>
	 * 
	 * @param accessToken 调用接口凭证
	 * @param name 部门名称。长度限制为1~64个字符
	 * @param parentId 父部门id。根部门id为1
	 * @param order 在父部门中的次序值。order值小的排序靠前
	 * @param createDeptGroup 是否创建一个关联此部门的企业群，默认为false
	 * @param deptHiding 是否隐藏部门, true表示隐藏, false表示显示
	 * @param deptPerimits 可以查看指定隐藏部门的其他部门列表，如果部门隐藏，则此值生效，取值为其他的部门id组成的的字符串，使用 | 符号进行分割
	 * @param userPerimits 可以查看指定隐藏部门的其他人员列表，如果部门隐藏，则此值生效，取值为其他的人员userid组成的的字符串，使用| 符号进行分割
	 * @param outerDept 是否本部门的员工仅可见员工自己, 为true时，本部门员工默认只能看到员工自己
	 * @param outerPermitDepts 本部门的员工仅可见员工自己为true时，可以配置额外可见部门，值为部门id组成的的字符串，使用|符号进行分割
	 * @param outerPermitUsers 本部门的员工仅可见员工自己为true时，可以配置额外可见人员，值为userid组成的的字符串，使用|符号进行分割
	 * @return id 返回ID
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
	 * 获取单个部门
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
			JSONObject response = HttpHelper.httpGet(url);//如果不存在会报错："errcode":60003,"errmsg":"部门不存在"
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
	 * @param accessToken 调用接口凭证
	 * @param id 部门id。（注：不能删除根部门；不能删除含有子部门 、成员的部门）
	 * @throws Exception
	 */
	public static void deleteDepartment(String accessToken, Long id) throws Exception {
		//		CorpDepartmentService corpDepartmentService = ServiceFactory.getInstance().getOpenService(CorpDepartmentService.class);
		//		corpDepartmentService.deptDelete(accessToken, id);
		String url = Env.OAPI_HOST + "/department/delete?" + "access_token=" + accessToken + "&id=" + id;
		HttpHelper.httpGet(url);
	}

	/**
	 * @param accessToken 调用接口凭证
	 * @param id 更新部门id
	 * @param name 部门名称。长度限制为1~64个字符
	 * @param parentId 父部门id。根部门id为1
	 * @param order 在父部门中的次序值。order值小的排序靠前
	 * @param createDeptGroup 是否创建一个关联此部门的企业群
	 * @param autoAddUser 如果有新人加入部门是否会自动加入部门群
	 * @param deptManagerUseridList 部门的主管列表, 取值为由主管的userid组成的字符串 ，不同的userid使用’| 符号进行分割
	 * @param deptHiding 是否隐藏部门, true表示隐藏, false表示显示
	 * @param deptPerimits 可以查看指定隐藏部门的其他部门列表，如果部门隐藏， 则此值生效，取值为其他的部门id组成的的字符串，使用 | 符号进行分割
	 * @param userPerimits 可以查看指定隐藏部门的其他人员列表，如果部门隐藏， 则此值生效 ，取值为其他的人员userid组成的的字符串，使用| 符号进行分割
	 * @param outerDept 是否本部门的员工仅可见员工自己, 为true时，本部门员工默认只能看到员工自己
	 * @param outerPermitDepts 本部门的员工仅可见员工自己为true时， 可以配置额外可见部门 ，值为部门id组成的的字符串，使用|符号进行分割
	 * @param outerPermitUsers 本部门的员工仅可见员工自己为true时， 可以配置额外可见人员 ，值为userid组成的的字符串，使用|符号进行分割
	 * @param orgDeptOwner 企业群群主
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
