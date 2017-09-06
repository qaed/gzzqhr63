package nc.bs.extsys.plugin.dingtalk.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.department.DepartmentHelper;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.itf.ta.TBMPsndocDelegator;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapProcessor;
import nc.ui.ta.psndoc.model.TbmPsndocAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;

import com.dingtalk.open.client.api.model.corp.CorpUser;
import com.dingtalk.open.client.api.model.corp.CorpUserDetail;
import com.dingtalk.open.client.api.model.corp.CorpUserList;
import com.dingtalk.open.client.api.model.corp.Department;

public class SyncUserToHR implements IBackgroundWorkPlugin {
	public SyncUserToHR() {

	}

	public SyncUserToHR(BaseDAO dao) {
		this.dao = dao;
	}

	private BaseDAO dao;
	private int getUserDetailErrorTimes = 0;
	private int getUserDetailErrorTimesMax = 20;
	private List<CorpUserDetail> userDetails = new ArrayList<CorpUserDetail>();

	@Override
	/**
	 * ����ִ�У��Ѷ�������Ա��ͬ�������ڵ���
	 */
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		PreAlertObject alert = new PreAlertObject();
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		StringBuilder returnmsg = new StringBuilder();
		alert.setMsgTitle("��Աͬ������ִ�����������-->HR��");
		StringBuilder sql = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
		List<TBMPsndocVO> insertvos = new ArrayList<TBMPsndocVO>();
		/*
		//�ֲ�try��Χ�������¼������Ϣ
		List<Department> departs = null;
		//��ȡȫ������
		try {
			departs = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException("ͬ��ʧ�ܣ���ȡ������Ϣʧ�ܣ����Ժ�����", e);
		}

		for (Department department : departs) {
			CorpUserList corpUserList = null;
			try {//��ȡ�����µ�������Ա
				corpUserList = UserHelper.getDepartmentUser(AuthHelper.getAccessToken(), department.getId(), null, null, null);
			} catch (Exception e) {
				Logger.error(e);
				returnmsg.append("��ȡ���ų�Աʧ�ܣ����Ժ�����__����ID:" + department.getId() + ",��������:" + department.getName() + "\n");
			}
			if (corpUserList == null) {
				continue;
			}
			List<CorpUser> userList = corpUserList.getUserlist();
			for (int i = 0; userList != null && i < userList.size(); i++) {
				CorpUser user = userList.get(i);
				CorpUserDetail userDetail = null;
				try {//��ȡ��Ա��ϸ��Ϣ
					userDetail = UserHelper.getUser(getToken(), user.getUserid());
				} catch (Exception e) {
					Logger.error(e);
					returnmsg.append("��ȡ��Ա��ϸ��Ϣʧ�ܣ����Ժ�����__��������:" + department.getName() + ",��Ա����:" + user.getName() + "\n");
				}
				if (userDetail == null) {
					continue;
				}
				String mobile = userDetail.getMobile();
				sql.delete(0, sql.length());
				sql.append("select b.begindate,b.pk_group,b.pk_org,a.pk_psndoc,b.pk_psnjob,b.pk_psnorg from bd_psndoc a left join hi_psnjob b on a.pk_psndoc = b.pk_psndoc  where a.mobile='" + mobile + "' and b.lastflag='Y'");
				Map<String, String> result = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (result == null) {
					returnmsg.append("δ���ҵ���Ӧ���ֻ����룬����HRϵͳά���ֻ�����__������Ա:" + userDetail.getName() + ",�ֻ���:" + userDetail.getMobile() + "\n");
					continue;
				}
				//�������������
				TBMPsndocVO vo = new TBMPsndocVO();
				vo.setBegindate(new UFLiteralDate("2017-08-01"));//���ڿ�ʼʱ��result.get("begindate")
				vo.setCreationtime(new UFDateTime(Calendar.getInstance().getTime()));//����ʱ��
				vo.setPk_psndoc(result.get("pk_psndoc"));//Ĭ�����Լ�
				vo.setPk_group(result.get("pk_group"));//����
				vo.setPk_org(result.get("pk_org"));//��֯
				vo.setPk_psndoc(result.get("pk_psndoc"));//��Ա����
				vo.setPk_psnjob(result.get("pk_psnjob"));//��������
				vo.setPk_psnorg(result.get("pk_psnorg"));//��֯��ϵ
				vo.setEnddate(new UFLiteralDate("9999-12-01"));//��������
				vo.setTbm_prop(2);//���ڷ�ʽ:1=�ֹ����ڣ�2=��������
				vo.setTimecardid(userDetail.getUserid());//���ڿ���
				insertvos.add(vo);
			}
		}
		*/

		List<CorpUserDetail> userList = getAllUser();
		for (CorpUserDetail userDetail : userList) {
			String mobile = userDetail.getMobile();
			sql.delete(0, sql.length());
			sql.append("select b.begindate,b.pk_group,b.pk_org,a.pk_psndoc,b.pk_psnjob,b.pk_psnorg from bd_psndoc a left join hi_psnjob b on a.pk_psndoc = b.pk_psndoc  where a.mobile='" + mobile + "' and b.lastflag='Y'");
			Map<String, String> result = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
			if (result == null) {
				returnmsg.append("δ���ҵ���Ӧ���ֻ����룬����HRϵͳά���ֻ�����__������Ա:" + userDetail.getName() + ",�ֻ���:" + userDetail.getMobile() + "\n");
				continue;
			}
			//�������������
			TBMPsndocVO vo = new TBMPsndocVO();
			vo.setBegindate(new UFLiteralDate("2017-08-01"));//���ڿ�ʼʱ��result.get("begindate")
			vo.setCreationtime(new UFDateTime(Calendar.getInstance().getTime()));//����ʱ��
			vo.setPk_psndoc(result.get("pk_psndoc"));//Ĭ�����Լ�
			vo.setPk_group(result.get("pk_group"));//����
			vo.setPk_org(result.get("pk_org"));//��֯
			vo.setPk_psndoc(result.get("pk_psndoc"));//��Ա����
			vo.setPk_psnjob(result.get("pk_psnjob"));//��������
			vo.setPk_psnorg(result.get("pk_psnorg"));//��֯��ϵ
			vo.setEnddate(new UFLiteralDate("9999-12-01"));//��������
			vo.setTbm_prop(2);//���ڷ�ʽ:1=�ֹ����ڣ�2=��������
			vo.setTimecardid(userDetail.getUserid());//���ڿ���
			vo.setPk_adminorg(result.get("pk_org"));//������֯
			insertvos.add(vo);
		}
		//		VOInsert<TBMPsndocVO> voInsert = new VOInsert<TBMPsndocVO>();
		//ִ�б���
		TBMPsndocDelegator.getTBMPsndocManageMaintain().check(insertvos.toArray(new TBMPsndocVO[0]));
		TBMPsndocDelegator.getTBMPsndocManageMaintain().insert(insertvos.toArray(new TBMPsndocVO[0]), true);
		//		voInsert.insert(insertvos.toArray(new TBMPsndocVO[0]));
		returnmsg.append("ͬ�����\n");
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
			Logger.error("��ȡtokenʧ��", e);
			throw new BusinessException("��ȡtokenʧ��", e);
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
