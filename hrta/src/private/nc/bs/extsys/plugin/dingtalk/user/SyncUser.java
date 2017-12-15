package nc.bs.extsys.plugin.dingtalk.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.department.DepartmentHelper;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;

import com.dingtalk.open.client.api.model.corp.CorpUser;
import com.dingtalk.open.client.api.model.corp.CorpUserDetail;
import com.dingtalk.open.client.api.model.corp.CorpUserList;
import com.dingtalk.open.client.api.model.corp.Department;

public class SyncUser implements IBackgroundWorkPlugin {
	public SyncUser() {

	}

	private int getUserDetailErrorTimes = 0;
	private int getUserDetailErrorTimesMax = 20;
	private List<CorpUserDetail> userDetails = new ArrayList<CorpUserDetail>();
	private List<CorpUserDetail> newUserDetail = new ArrayList<CorpUserDetail>();

	public SyncUser(BaseDAO dao) {
		this.dao = dao;
	}

	private BaseDAO dao;

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		PreAlertObject alert = new PreAlertObject();
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		alert.setMsgTitle("人员同步任务执行情况（HR-->钉钉）");
		StringBuilder returnmsg = new StringBuilder();

		Map<String, Object> map = arg0.getKeyMap();
		String syncAll = (String) map.get("syncAll");//是否全员同步
		returnmsg.append("===============开始同步人员===============\n");
		if (syncAll == null || "".equals(syncAll.trim())) {
			returnmsg.append("未检测到自定义人员同步参数syncAll,默认为N，将同步最近2天变动人员\n");
			syncAll = "N";
		} else {
			returnmsg.append("检测到自定义人员同步参数syncAll = " + syncAll + "\n");
		}

		StringBuilder sql = new StringBuilder();
		//---------------------------------开始同步删除---------------------------
		//查询离职人员
		sql.delete(0, sql.length());
		sql.append("select distinct tbm_psndoc.timecardid timecardid,bd_psndoc.code code,bd_psndoc.name name ");
		sql.append(" from tbm_psndoc ");
		sql.append(" left join bd_psndoc on bd_psndoc.pk_psndoc = tbm_psndoc.pk_psndoc ");
		sql.append(" left join hi_psnorg on hi_psnorg.pk_psndoc = tbm_psndoc.pk_psndoc ");
		sql.append(" where isnull(tbm_psndoc.dr,0)=0 ");
		sql.append(" and hi_psnorg.lastflag='Y' and hi_psnorg.endflag='Y' ");
		List<Map<String, String>> deleteUsers = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		if (deleteUsers != null && deleteUsers.size() > 0) {
			for (Map<String, String> user : deleteUsers) {
				try {
					UserHelper.deleteUser(getToken(), user.get("timecardid"));//不使用批量删除，放止部分删除失败
				} catch (Exception e) {
					if (e.getMessage().contains("60121")) {//找不到该用户，已经被删除
						//不用动
					} else {
						Logger.error("删除钉钉用户失败,HR用户为：" + user.get("name") + ",用户编码：" + user.get("code") + "\n", e);
						//returnmsg.append("删除钉钉用户失败:HR用户为：" + user.get("name") + ",用户编码：" + user.get("code") + " " + e.getMessage() + "\n");
						throw new BusinessException(e);
					}
				}
			}
		}

		//---------------------------------开始同步新增、更新---------------------------
		sql.delete(0, sql.length());
		sql.append("select tbm_psndoc.timecardid timecardid,bd_psndoc.code code,bd_psndoc.name name ,org_dept.def1 departmentid,om_post.postname position,bd_psndoc.mobile mobile ");
		sql.append(" from tbm_psndoc ");
		sql.append(" left join bd_psndoc on bd_psndoc.pk_psndoc=tbm_psndoc.pk_psndoc ");
		sql.append(" left join hi_psnjob on hi_psnjob.pk_psnjob=tbm_psndoc.pk_psnjob ");
		sql.append(" left join om_post on om_post.pk_post=hi_psnjob.pk_post ");
		sql.append(" left join org_dept on org_dept.pk_dept=hi_psnjob.pk_dept ");
		sql.append(" where tbm_psndoc.enddate > to_char(sysdate,'yyyy-mm-dd') and isnull(tbm_psndoc.dr,0)=0");
		if ("N".equals(syncAll)) {//不同步全员，仅同步2天内变动人员
			sql.append(" and (tbm_psndoc.ts > to_char(sysdate-1,'yyyy-mm-dd') or hi_psnjob.ts > to_char(sysdate-1,'yyyy-mm-dd')) ");
		}
		List<Map<String, String>> users = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		returnmsg.append("本次同步人员: " + users.size() + "人 \n");
		if (users.size() > 0) {
			Map<String, CorpUserDetail> userMap = getAllUserMap();
			for (Map<String, String> user : users) {
				CorpUserDetail userDetail = new CorpUserDetail();
				userDetail.setName(user.get("name"));
				if (userMap.get(user.get("mobile"))!=null && userMap.get(user.get("mobile")).getDepartment().size() == 1) {
					//只同步单部门人员的部门情况；如果钉钉中的人员存在多部门，则不同步该人员的部分情况
					List<Long> departments = new ArrayList<Long>();
					departments.add(Long.parseLong(user.get("departmentid")));
					userDetail.setDepartment(departments);//部门
				}
				userDetail.setPosition(user.get("position"));//职位
				userDetail.setMobile(user.get("mobile"));//手机号
				//			userDetail.setTel(user.get("tel"));
				//			userDetail.setWorkPlace(user.get("workPlace"));
				//			userDetail.setRemark(user.get("remark"));
				//			userDetail.setEmail(user.get("email"));
				userDetail.setJobnumber(user.get("code"));
				if (user.get("timecardid") != null && !"".equals(user.get("timecardid").trim())) {
					//有考勤卡号
					userDetail.setUserid(user.get("timecardid"));//后面的人用code来作为userid

					try {
						//先尝试更新
						UserHelper.updateUser(getToken(), userDetail);
					} catch (Exception e) {
						//更新失败
						if (e.getMessage().contains("60121")) {//errormsg：找不到该用户，可能用户在钉钉上已删除。重新创建用户，更新新的考勤卡号
							try {
								String userid = UserHelper.createUser(getToken(), userDetail);
								returnmsg.append("新增钉钉用户：" + userDetail.getName() + "\n");
								userDetail.setUserid(userid);
								newUserDetail.add(userDetail);
								//更新考勤卡号
								sql.delete(0, sql.length());
								sql.append("update tbm_psndoc set timecardid='" + userid + "' where pk_psndoc=(select pk_psndoc from bd_psndoc where mobile='" + userDetail.getMobile() + "')");
								getDao().executeUpdate(sql.toString());
							} catch (Exception e1) {
								Logger.error("新增钉钉用户失败,HR用户为：" + userDetail.getName() + ",用户编码：" + user.get("code") + "\n", e1);
								throw new BusinessException(e1);
							}
						} else if (e.getMessage().contains("60104")) {//手机号码在公司中已存在
							syncTimecardid(userDetail.getMobile());
							returnmsg.append("钉钉用户：" + userDetail.getName() + "考勤号错误,重新同步钉钉\n");
						} else {
							Logger.error("同步用户失败,HR用户为：" + userDetail.getName() + ",用户编码：" + user.get("code") + "\n", e);
							throw new BusinessException(e);
						}
					}
				} else {
					//没有考勤卡号
					try {
						//先尝试新增
						String userid = UserHelper.createUser(getToken(), userDetail);
						userDetail.setUserid(userid);
						newUserDetail.add(userDetail);
						//更新考勤卡号
						sql.delete(0, sql.length());
						sql.append("update tbm_psndoc set timecardid='" + userid + "' where pk_psndoc=(select pk_psndoc from bd_psndoc where mobile='" + userDetail.getMobile() + "')");
						getDao().executeUpdate(sql.toString());
					} catch (Exception e1) {
						if (e1.getMessage().contains("60104")) {//	手机号码在公司中已存在
							returnmsg.append("该用户没有考勤卡号，首次同步钉钉：" + user.get("code") + "  " + userDetail.getName() + "\n");
							syncTimecardid(userDetail.getMobile());
						} else {
							Logger.error(e1);
							throw new BusinessException(e1);
						}
					}
				}
			}
		}
		returnmsg.append("===============同步人员完成===============\n");
		alert.setReturnObj(returnmsg.toString());
		return alert;
	}

	/**
	 * 获取token
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private String getToken() throws BusinessException {
		try {
			return AuthHelper.getAccessToken();
		} catch (OApiException e) {
			Logger.error(e);
			throw new BusinessException(e);
		}
	}

	public BaseDAO getDao() {
		if (dao == null) {
			this.dao = new BaseDAO();
		}
		return dao;
	}

	public void setDao(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据HR中手机号码，查找对应钉钉用户,使用钉钉用户的id作为考勤号,保存到考勤档案
	 * 
	 * @param mobile
	 * @throws BusinessException
	 */
	private void syncTimecardid(String mobile) throws BusinessException {
		List<CorpUserDetail> userlist = getAllUser();
		for (CorpUserDetail corpUserDetail : userlist) {
			if (mobile.equals(corpUserDetail.getMobile().trim())) {
				//更新考勤卡号
				StringBuilder sql = new StringBuilder();
				sql.append("update tbm_psndoc set timecardid='" + corpUserDetail.getUserid() + "' where pk_psndoc=(select pk_psndoc from bd_psndoc where mobile='" + mobile + "')");
				getDao().executeUpdate(sql.toString());
				break;
			}
		}
	}

	/**
	 * 获取钉钉的全部人员
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private List<CorpUserDetail> getAllUser() throws BusinessException {
		if (this.userDetails != null && this.userDetails.size() > 0) {
			if (this.newUserDetail != null && this.newUserDetail.size() > 0) {
				this.userDetails.addAll(newUserDetail);
				newUserDetail.clear();
			}
			return this.userDetails;
		}
		try {
			List<Department> departments = DepartmentHelper.listDepartments(getToken(), "1");
			for (int i = 0; i < departments.size(); i++) {
				CorpUserList corpuserlist = UserHelper.getDepartmentUser(getToken(), departments.get(i).getId(), null, null, null);
				List<CorpUser> userlist = corpuserlist.getUserlist();
				for (int j = 0; j < userlist.size(); j++) {
					this.userDetails.add(UserHelper.getUser(getToken(), userlist.get(j).getUserid()));
				}
			}
			//还有根部门的
			CorpUserList rootcorpuserlist = UserHelper.getDepartmentUser(getToken(), 1L, null, null, null);
			List<CorpUser> rootuserlist = rootcorpuserlist.getUserlist();
			for (int j = 0; j < rootuserlist.size(); j++) {
				this.userDetails.add(UserHelper.getUser(getToken(), rootuserlist.get(j).getUserid()));
			}
		} catch (Exception e) {
			this.getUserDetailErrorTimes++;
			if (this.getUserDetailErrorTimes < this.getUserDetailErrorTimesMax) {
				this.userDetails.clear();
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e1) {
					Logger.error(e1);
				}
				return getAllUser();
			} else {
				throw new BusinessException(e);
			}
		}
		if (this.newUserDetail != null && this.newUserDetail.size() > 0) {
			this.userDetails.addAll(newUserDetail);
			newUserDetail.clear();
		}
		return this.userDetails;
	}

	/**
	 * 获取全部用户Map类型，key 为 mobile
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, CorpUserDetail> getAllUserMap() throws BusinessException {
		List<CorpUserDetail> userList = getAllUser();
		Map<String, CorpUserDetail> userMap = new HashMap<String, CorpUserDetail>();
		for (CorpUserDetail user : userList) {
			userMap.put(user.getMobile(), user);
		}
		return userMap;
	}
}
