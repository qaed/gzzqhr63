package nc.bs.extsys.plugin.dingtalk.user;

import java.util.ArrayList;
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
		returnmsg.append("----------------��ʼͬ����Ա------------\n");
		StringBuilder sql = new StringBuilder();
		//---------------------------------��ʼͬ��ɾ��---------------------------
		//---------------------------------��ʼͬ������������---------------------------
		sql.delete(0, sql.length());
		sql.append("select tbm_psndoc.timecardid timecardid,bd_psndoc.code code,bd_psndoc.name name ,org_dept.def1 departmentid,om_post.postname position,bd_psndoc.mobile mobile ");
		sql.append(" from tbm_psndoc ");
		sql.append(" left join bd_psndoc on bd_psndoc.pk_psndoc=tbm_psndoc.pk_psndoc ");
		sql.append(" left join hi_psnjob on hi_psnjob.pk_psnjob=tbm_psndoc.pk_psnjob ");
		sql.append(" left join om_post on om_post.pk_post=hi_psnjob.pk_post ");
		sql.append(" left join org_dept on org_dept.pk_dept=hi_psnjob.pk_dept ");
		sql.append(" where tbm_psndoc.enddate > to_char(sysdate,'yyyy-mm-dd') and isnull(tbm_psndoc.dr,0)=0");
		List<Map<String, String>> users = (List<Map<String, String>>) getDao().executeQuery(sql.toString(), new MapListProcessor());
		returnmsg.append("������Ҫͬ����Ա��: " + users.size() + "�� \n");
		for (Map<String, String> user : users) {
			CorpUserDetail userDetail = new CorpUserDetail();
			userDetail.setName(user.get("name"));
			List<Long> departments = new ArrayList<Long>();
			departments.add(Long.parseLong(user.get("departmentid")));
			userDetail.setDepartment(departments);//����
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
		returnmsg.append("-----------ͬ����Ա���-----------\n");
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
	 * ����HR���ֻ����룬���Ҷ�Ӧ�����û���id��Ϊ���ں�
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

	private List<CorpUserDetail> getAllUser() throws BusinessException {
		if (this.userDetails != null && this.userDetails.size() > 0) {
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
		return this.userDetails;
	}
}