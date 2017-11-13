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
	 * 创建成员
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
		args.put("userid", userDetail.getUserid());//员工唯一标识ID（不可修改），企业内必须唯一。长度为1~64个字符，如果不传，服务器将自动生成一个userid
		args.put("name", userDetail.getName());//成员名称。长度为1~64个字符
		if (orderInDepts != null) {
			args.put("orderInDepts", orderInDepts);//在对应的部门中的排序, Map结构的json字符串, key是部门的Id, value是人员在这个部门的排序值
		}
		args.put("department", userDetail.getDepartment());//数组类型，数组里面值为整型，成员所属部门id列表
		if (userDetail.getPosition() != null) {
			args.put("position", userDetail.getPosition());//职位信息。长度为0~64个字符
		}
		args.put("mobile", userDetail.getMobile());//手机号码。企业内必须唯一
		if (userDetail.getTel() != null) {
			args.put("tel", userDetail.getTel());//分机号，长度为0~50个字符
		}
		if (userDetail.getWorkPlace() != null) {
			args.put("workPlace", userDetail.getWorkPlace());//办公地点，长度为0~50个字符
		}
		if (userDetail.getRemark() != null) {
			args.put("remark", userDetail.getRemark());//备注，长度为0~1000个字符
		}
		if (userDetail.getEmail() != null) {
			args.put("email", userDetail.getEmail());//邮箱。长度为0~64个字符。企业内必须唯一
		}
		if (userDetail.getJobnumber() != null) {
			args.put("jobnumber", userDetail.getJobnumber());//员工工号。对应显示到OA后台和客户端个人资料的工号栏目。长度为0~64个字符
		}
		if (userDetail.getIsHide() != null) {
			args.put("isHide", userDetail.getIsHide());//是否号码隐藏, true表示隐藏, false表示不隐藏。隐藏手机号后，手机号在个人资料页隐藏，但仍可对其发DING、发起钉钉免费商务电话。
		}
		if (userDetail.getIsAdmin() != null) {
			args.put("isSenior", userDetail.getIsAdmin());//是否高管模式，true表示是，false表示不是。开启后，手机号码对所有员工隐藏。普通员工无法对其发DING、发起钉钉免费商务电话。高管之间不受影响。
		}
		if (userDetail.getExtattr() != null) {
			args.put("extattr", userDetail.getExtattr());//扩展属性，可以设置多种属性(但手机上最多只能显示10个扩展属性，具体显示哪些属性，请到OA管理后台->设置->通讯录信息设置和OA管理后台->设置->手机端显示信息设置)
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
	 * 更新成员
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
		args.put("userid", userDetail.getUserid());//员工唯一标识ID（不可修改），企业内必须唯一。长度为1~64个字符，如果不传，服务器将自动生成一个userid
		args.put("name", userDetail.getName());//成员名称。长度为1~64个字符

		if (userDetail.getDepartment() != null) {
			args.put("department", userDetail.getDepartment());//数组类型，数组里面值为整型，成员所属部门id列表
		}
		if (orderInDepts != null) {
			args.put("orderInDepts", orderInDepts);//在对应的部门中的排序, Map结构的json字符串, key是部门的Id, value是人员在这个部门的排序值
		}
		if (userDetail.getPosition() != null) {
			args.put("position", userDetail.getPosition());//职位信息。长度为0~64个字符
		}
		args.put("mobile", userDetail.getMobile());//手机号码。企业内必须唯一
		if (userDetail.getTel() != null) {
			args.put("tel", userDetail.getTel());//分机号，长度为0~50个字符
		}
		if (userDetail.getWorkPlace() != null) {
			args.put("workPlace", userDetail.getWorkPlace());//办公地点，长度为0~50个字符
		}
		if (userDetail.getRemark() != null) {
			args.put("remark", userDetail.getRemark());//备注，长度为0~1000个字符
		}
		if (userDetail.getEmail() != null) {
			args.put("email", userDetail.getEmail());//邮箱。长度为0~64个字符。企业内必须唯一
		}
		if (userDetail.getJobnumber() != null) {
			args.put("jobnumber", userDetail.getJobnumber());//员工工号。对应显示到OA后台和客户端个人资料的工号栏目。长度为0~64个字符
		}
		if (userDetail.getIsHide() != null) {
			args.put("isHide", userDetail.getIsHide());//是否号码隐藏, true表示隐藏, false表示不隐藏。隐藏手机号后，手机号在个人资料页隐藏，但仍可对其发DING、发起钉钉免费商务电话。
		}
		if (userDetail.getIsAdmin() != null) {
			args.put("isSenior", userDetail.getIsAdmin());//是否高管模式，true表示是，false表示不是。开启后，手机号码对所有员工隐藏。普通员工无法对其发DING、发起钉钉免费商务电话。高管之间不受影响。
		}
		if (userDetail.getExtattr() != null) {
			args.put("extattr", userDetail.getExtattr());//扩展属性，可以设置多种属性(但手机上最多只能显示10个扩展属性，具体显示哪些属性，请到OA管理后台->设置->通讯录信息设置和OA管理后台->设置->手机端显示信息设置)
		}

		HttpHelper.httpPost(url, args);
		//		corpUserService.updateCorpUser(accessToken, userDetail.getUserid(), userDetail.getName(), orderInDepts, userDetail.getDepartment(), userDetail.getPosition(), userDetail.getMobile(), userDetail.getTel(), userDetail.getWorkPlace(), userDetail.getRemark(), userDetail.getEmail(), userDetail.getJobnumber(), userDetail.getIsHide(), userDetail.getSenior(), userDetail.getExtattr());
	}

	//删除成员
	public static void deleteUser(String accessToken, String userid) throws Exception {
		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		corpUserService.deleteCorpUser(accessToken, userid);
		String url = Env.OAPI_HOST + "/user/delete?" + "access_token=" + accessToken + "&userid=" + userid;
		HttpHelper.httpGet(url);
	}

	//批量删除成员
	public static void batchDeleteUser(String accessToken, List<String> useridlist) throws Exception {
		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		corpUserService.batchdeleteCorpUserListByUserids(accessToken, useridlist);

		String url = Env.OAPI_HOST + "/user/batchdelete?" + "access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("useridlist", useridlist);//员工UserID列表。列表长度在1到20之间
		HttpHelper.httpPost(url, args);
	}

	//获取成员详情
	public static CorpUserDetail getUser(String accessToken, String userid) throws Exception {

		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		return corpUserService.getCorpUser(accessToken, userid);
		String url = Env.OAPI_HOST + "/user/get?" + "access_token=" + accessToken + "&userid=" + userid;
		return JSON.toJavaObject(HttpHelper.httpGet(url), CorpUserDetail.class);
	}

	//获取部门成员
	public static CorpUserList getDepartmentUser(String accessToken, long departmentId, Long offset, Integer size, String order)
			throws Exception {

		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		return corpUserService.getCorpUserSimpleList(accessToken, departmentId, offset, size, order);
		StringBuilder url = new StringBuilder();
		url.append(Env.OAPI_HOST + "/user/simplelist?" + "access_token=");
		url.append(accessToken);
		url.append("&department_id=");
		url.append(departmentId);//获取的部门id

		if (offset != null) {
			url.append("&offset=");
			url.append(offset);//支持分页查询，与size参数同时设置时才生效，此参数代表偏移量
		}
		if (size != null) {
			url.append("&size=");
			url.append(size);//支持分页查询，与offset参数同时设置时才生效，此参数代表分页大小，最大100
		}
		if (order != null) {
			url.append("&order=");
			url.append(order);//支持分页查询，部门成员的排序规则，默认不传是按自定义排序；entry_asc代表按照进入部门的时间升序，entry_desc代表按照进入部门的时间降序，modify_asc代表按照部门信息修改时间升序，modify_desc代表按照部门信息修改时间降序，custom代表用户定义(未定义时按照拼音)排序
		}
		return JSON.toJavaObject(HttpHelper.httpGet(url.toString()), CorpUserList.class);
	}

	//获取部门成员（详情）
	public static CorpUserDetailList getUserDetails(String accessToken, long departmentId, Long offset, Integer size, String order)
			throws Exception {

		//		CorpUserService corpUserService = ServiceFactory.getInstance().getOpenService(CorpUserService.class);
		//		return corpUserService.getCorpUserList(accessToken, departmentId, offset, size, order);
		StringBuilder url = new StringBuilder();
		url.append(Env.OAPI_HOST + "/user/list?" + "access_token=");
		url.append(accessToken);
		url.append("&department_id=");
		url.append(departmentId);//获取的部门id

		if (offset != null) {
			url.append("&offset=");
			url.append(offset);//支持分页查询，与size参数同时设置时才生效，此参数代表偏移量
		}
		if (size != null) {
			url.append("&size=");
			url.append(size);//支持分页查询，与offset参数同时设置时才生效，此参数代表分页大小，最大100
		}
		if (order != null) {
			url.append("&order=");
			url.append(order);//支持分页查询，部门成员的排序规则，默认不传是按自定义排序；entry_asc代表按照进入部门的时间升序，entry_desc代表按照进入部门的时间降序，modify_asc代表按照部门信息修改时间升序，modify_desc代表按照部门信息修改时间降序，custom代表用户定义(未定义时按照拼音)排序
		}
		return JSON.toJavaObject(HttpHelper.httpGet(url.toString()), CorpUserDetailList.class);
	}

	/**
	 * 未完善
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
	 * 未完善
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
