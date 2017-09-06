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
import com.dingtalk.open.client.common.SdkInitException;
import com.dingtalk.open.client.common.ServiceException;
import com.dingtalk.open.client.common.ServiceNotExistException;

public class TestUserHelper {

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
		CorpUserDetail user = UserHelper.getUser(AuthHelper.getAccessToken(), "tsy1");
		System.out.println(user);
		System.out.print(user.getName());
		System.out.println(user.getMobile());

	}

	@Test
	public void testCreateUser() throws Exception {
		CorpUserDetail userDetail = new CorpUserDetail();
		userDetail.setUserid("tsy2");
		userDetail.setName("测试XX名");
		List<Long> departmentList = new ArrayList<Long>();
		departmentList.add(1L);
		userDetail.setDepartment(departmentList);
		userDetail.setMobile("18819422222");
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

	@Test
	public void testUpdate() throws Exception {
		CorpUserDetail userDetail = new CorpUserDetail();
		userDetail.setUserid("tsy1");
		userDetail.setName("测试YYY名");
		UserHelper.updateUser(AuthHelper.getAccessToken(), userDetail);
	}

	@Test
	public void listAllUser() throws Exception {
		List<Department> departments = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");
		for (Department department : departments) {
			CorpUserList corpUserlist = UserHelper.getDepartmentUser(AuthHelper.getAccessToken(), department.getId(), null, null, null);
			List<CorpUser> userlist = corpUserlist.getUserlist();
			for (CorpUser corpUser : userlist) {
				System.out.println(corpUser.getName() + "::" + department.getName() + "::" + corpUser.getUserid());
			}
		}
	}

}
