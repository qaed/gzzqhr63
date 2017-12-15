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
		alert.setMsgTitle("��Աͬ������ִ�������HR-->������");
		StringBuilder returnmsg = new StringBuilder();

		Map<String, Object> map = arg0.getKeyMap();
		String syncAll = (String) map.get("syncAll");//�Ƿ�ȫԱͬ��
		returnmsg.append("===============��ʼͬ����Ա===============\n");
		if (syncAll == null || "".equals(syncAll.trim())) {
			returnmsg.append("δ��⵽�Զ�����Աͬ������syncAll,Ĭ��ΪN����ͬ�����2��䶯��Ա\n");
			syncAll = "N";
		} else {
			returnmsg.append("��⵽�Զ�����Աͬ������syncAll = " + syncAll + "\n");
		}

		StringBuilder sql = new StringBuilder();
		//---------------------------------��ʼͬ��ɾ��---------------------------
		//��ѯ��ְ��Ա
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
					UserHelper.deleteUser(getToken(), user.get("timecardid"));//��ʹ������ɾ������ֹ����ɾ��ʧ��
				} catch (Exception e) {
					if (e.getMessage().contains("60121")) {//�Ҳ������û����Ѿ���ɾ��
						//���ö�
					} else {
						Logger.error("ɾ�������û�ʧ��,HR�û�Ϊ��" + user.get("name") + ",�û����룺" + user.get("code") + "\n", e);
						//returnmsg.append("ɾ�������û�ʧ��:HR�û�Ϊ��" + user.get("name") + ",�û����룺" + user.get("code") + " " + e.getMessage() + "\n");
						throw new BusinessException(e);
					}
				}
			}
		}

		//---------------------------------��ʼͬ������������---------------------------
		sql.delete(0, sql.length());
		sql.append("select tbm_psndoc.timecardid timecardid,bd_psndoc.code code,bd_psndoc.name name ,org_dept.def1 departmentid,om_post.postname position,bd_psndoc.mobile mobile ");
		sql.append(" from tbm_psndoc ");
		sql.append(" left join bd_psndoc on bd_psndoc.pk_psndoc=tbm_psndoc.pk_psndoc ");
		sql.append(" left join hi_psnjob on hi_psnjob.pk_psnjob=tbm_psndoc.pk_psnjob ");
		sql.append(" left join om_post on om_post.pk_post=hi_psnjob.pk_post ");
		sql.append(" left join org_dept on org_dept.pk_dept=hi_psnjob.pk_dept ");
		sql.append(" where tbm_psndoc.enddate > to_char(sysdate,'yyyy-mm-dd') and isnull(tbm_psndoc.dr,0)=0");
		if ("N".equals(syncAll)) {//��ͬ��ȫԱ����ͬ��2���ڱ䶯��Ա
			sql.append(" and (tbm_psndoc.ts > to_char(sysdate-1,'yyyy-mm-dd') or hi_psnjob.ts > to_char(sysdate-1,'yyyy-mm-dd')) ");
		}
		List<Map<String, String>> users = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		returnmsg.append("����ͬ����Ա: " + users.size() + "�� \n");
		if (users.size() > 0) {
			Map<String, CorpUserDetail> userMap = getAllUserMap();
			for (Map<String, String> user : users) {
				CorpUserDetail userDetail = new CorpUserDetail();
				userDetail.setName(user.get("name"));
				if (userMap.get(user.get("mobile"))!=null && userMap.get(user.get("mobile")).getDepartment().size() == 1) {
					//ֻͬ����������Ա�Ĳ����������������е���Ա���ڶಿ�ţ���ͬ������Ա�Ĳ������
					List<Long> departments = new ArrayList<Long>();
					departments.add(Long.parseLong(user.get("departmentid")));
					userDetail.setDepartment(departments);//����
				}
				userDetail.setPosition(user.get("position"));//ְλ
				userDetail.setMobile(user.get("mobile"));//�ֻ���
				//			userDetail.setTel(user.get("tel"));
				//			userDetail.setWorkPlace(user.get("workPlace"));
				//			userDetail.setRemark(user.get("remark"));
				//			userDetail.setEmail(user.get("email"));
				userDetail.setJobnumber(user.get("code"));
				if (user.get("timecardid") != null && !"".equals(user.get("timecardid").trim())) {
					//�п��ڿ���
					userDetail.setUserid(user.get("timecardid"));//���������code����Ϊuserid

					try {
						//�ȳ��Ը���
						UserHelper.updateUser(getToken(), userDetail);
					} catch (Exception e) {
						//����ʧ��
						if (e.getMessage().contains("60121")) {//errormsg���Ҳ������û��������û��ڶ�������ɾ�������´����û��������µĿ��ڿ���
							try {
								String userid = UserHelper.createUser(getToken(), userDetail);
								returnmsg.append("���������û���" + userDetail.getName() + "\n");
								userDetail.setUserid(userid);
								newUserDetail.add(userDetail);
								//���¿��ڿ���
								sql.delete(0, sql.length());
								sql.append("update tbm_psndoc set timecardid='" + userid + "' where pk_psndoc=(select pk_psndoc from bd_psndoc where mobile='" + userDetail.getMobile() + "')");
								getDao().executeUpdate(sql.toString());
							} catch (Exception e1) {
								Logger.error("���������û�ʧ��,HR�û�Ϊ��" + userDetail.getName() + ",�û����룺" + user.get("code") + "\n", e1);
								throw new BusinessException(e1);
							}
						} else if (e.getMessage().contains("60104")) {//�ֻ������ڹ�˾���Ѵ���
							syncTimecardid(userDetail.getMobile());
							returnmsg.append("�����û���" + userDetail.getName() + "���ںŴ���,����ͬ������\n");
						} else {
							Logger.error("ͬ���û�ʧ��,HR�û�Ϊ��" + userDetail.getName() + ",�û����룺" + user.get("code") + "\n", e);
							throw new BusinessException(e);
						}
					}
				} else {
					//û�п��ڿ���
					try {
						//�ȳ�������
						String userid = UserHelper.createUser(getToken(), userDetail);
						userDetail.setUserid(userid);
						newUserDetail.add(userDetail);
						//���¿��ڿ���
						sql.delete(0, sql.length());
						sql.append("update tbm_psndoc set timecardid='" + userid + "' where pk_psndoc=(select pk_psndoc from bd_psndoc where mobile='" + userDetail.getMobile() + "')");
						getDao().executeUpdate(sql.toString());
					} catch (Exception e1) {
						if (e1.getMessage().contains("60104")) {//	�ֻ������ڹ�˾���Ѵ���
							returnmsg.append("���û�û�п��ڿ��ţ��״�ͬ��������" + user.get("code") + "  " + userDetail.getName() + "\n");
							syncTimecardid(userDetail.getMobile());
						} else {
							Logger.error(e1);
							throw new BusinessException(e1);
						}
					}
				}
			}
		}
		returnmsg.append("===============ͬ����Ա���===============\n");
		alert.setReturnObj(returnmsg.toString());
		return alert;
	}

	/**
	 * ��ȡtoken
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
	 * ����HR���ֻ����룬���Ҷ�Ӧ�����û�,ʹ�ö����û���id��Ϊ���ں�,���浽���ڵ���
	 * 
	 * @param mobile
	 * @throws BusinessException
	 */
	private void syncTimecardid(String mobile) throws BusinessException {
		List<CorpUserDetail> userlist = getAllUser();
		for (CorpUserDetail corpUserDetail : userlist) {
			if (mobile.equals(corpUserDetail.getMobile().trim())) {
				//���¿��ڿ���
				StringBuilder sql = new StringBuilder();
				sql.append("update tbm_psndoc set timecardid='" + corpUserDetail.getUserid() + "' where pk_psndoc=(select pk_psndoc from bd_psndoc where mobile='" + mobile + "')");
				getDao().executeUpdate(sql.toString());
				break;
			}
		}
	}

	/**
	 * ��ȡ������ȫ����Ա
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
			//���и����ŵ�
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
	 * ��ȡȫ���û�Map���ͣ�key Ϊ mobile
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
