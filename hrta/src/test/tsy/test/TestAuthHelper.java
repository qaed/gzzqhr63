package tsy.test;

import java.util.ArrayList;
import java.util.List;

import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.OApiResultException;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.department.DepartmentHelper;
import nc.bs.extsys.plugin.dingtalk.department.SyncDept;
import nc.bs.extsys.plugin.dingtalk.user.UserHelper;

import org.junit.Test;

import com.dingtalk.open.client.api.model.corp.CorpUser;
import com.dingtalk.open.client.api.model.corp.CorpUserDetail;
import com.dingtalk.open.client.api.model.corp.CorpUserDetailList;
import com.dingtalk.open.client.api.model.corp.CorpUserList;
import com.dingtalk.open.client.api.model.corp.Department;

public class TestAuthHelper {

	@Test
	public void test() throws Exception {
		String token = AuthHelper.getAccessToken();
		//		List<Department> depts = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");
		//		System.out.println(depts.toString());
		//		DepartmentHelper.createDepartment(AuthHelper.getAccessToken(), "测试ts", "1", null, false, false, null, null, false, null, null);
		//		depts = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");
		//		System.out.println(depts);

	}

	@Test
	public void test1() throws Exception {

		//		BaseDAO dao = new BaseDAO();
		//		List list = (List) dao.executeQuery("select * from org_dept", new MapListProcessor());
		SyncDept sync = new SyncDept();
		sync.executeTask(null);
	}

	@Test
	public void testGetDepartment() throws OApiResultException, OApiException {
		Department dept = DepartmentHelper.getDepartment(AuthHelper.getAccessToken(), "4");
		System.out.println(dept);
	}

	@Test
	public void testGetDepartmentUser() throws Exception {
		CorpUserList users = UserHelper.getDepartmentUser(AuthHelper.getAccessToken(), 1, null, null, null);
		if (users != null) {
			List<CorpUser> corpusers = users.getUserlist();
			if (corpusers.size() > 0) {
				for (CorpUser corpUser : corpusers) {
					System.out.println(corpUser.getUserid() + "::" + corpUser.getName());
				}//125055382826307370::朱烨怡
			} else {
				System.out.println("部门下没有人员");
			}
		}
	}

	@Test
	public void testGetDetails() throws Exception {
		CorpUserDetailList users = UserHelper.getUserDetails(AuthHelper.getAccessToken(), 1, 0L, 10, null);
		if (users != null) {
			List<CorpUserDetail> corpusers = users.getUserlist();
			if (corpusers.size() > 0) {
				for (CorpUserDetail corpUser : corpusers) {
					System.out.println(corpUser.getUserid() + "::" + corpUser.getName());
				}//125055382826307370::朱烨怡
			} else {
				System.out.println("部门下没有人员");
			}
		}
	}

	@Test
	public void testGetUser() throws OApiException, Exception {
		//		CorpUserDetail user = UserHelper.getUser(AuthHelper.getAccessToken(), "125055382826307370");
		CorpUserDetail user = UserHelper.getUser(AuthHelper.getAccessToken(), "tsy3");
		System.out.println(user);
		System.out.println(user.getMobile());

	}

	@Test
	public void testCreateUser() throws Exception {
		CorpUserDetail userDetail = new CorpUserDetail();
		userDetail.setUserid("tsy3");
		userDetail.setName("测试XX名");
		List<Long> departmentList = new ArrayList<Long>();
		departmentList.add(1L);
		userDetail.setDepartment(departmentList);
		userDetail.setMobile("18819422224");
		UserHelper.createUser(AuthHelper.getAccessToken(), userDetail);

	}

	@Test
	public void testDeleteUser() throws Exception {
		UserHelper.deleteUser(AuthHelper.getAccessToken(), "tsy3");
	}

	@Test
	public void testDeletebatch() throws Exception {
		List<String> useridlist = new ArrayList<String>();
		useridlist.add("tsy1");
		useridlist.add("tsy2");
		UserHelper.batchDeleteUser(AuthHelper.getAccessToken(), useridlist);
	}

}
