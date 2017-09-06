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
	 * 初次执行，把钉钉的人员先同步到考勤档案
	 */
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		PreAlertObject alert = new PreAlertObject();
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		StringBuilder returnmsg = new StringBuilder();
		alert.setMsgTitle("人员同步任务执行情况（钉钉-->HR）");
		StringBuilder sql = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
		List<TBMPsndocVO> insertvos = new ArrayList<TBMPsndocVO>();
		/*
		//分布try包围，方便记录错误信息
		List<Department> departs = null;
		//获取全部部门
		try {
			departs = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException("同步失败，获取部门信息失败，请稍后再试", e);
		}

		for (Department department : departs) {
			CorpUserList corpUserList = null;
			try {//获取部门下的所有人员
				corpUserList = UserHelper.getDepartmentUser(AuthHelper.getAccessToken(), department.getId(), null, null, null);
			} catch (Exception e) {
				Logger.error(e);
				returnmsg.append("获取部门成员失败，请稍后再试__部门ID:" + department.getId() + ",部门名称:" + department.getName() + "\n");
			}
			if (corpUserList == null) {
				continue;
			}
			List<CorpUser> userList = corpUserList.getUserlist();
			for (int i = 0; userList != null && i < userList.size(); i++) {
				CorpUser user = userList.get(i);
				CorpUserDetail userDetail = null;
				try {//获取人员详细信息
					userDetail = UserHelper.getUser(getToken(), user.getUserid());
				} catch (Exception e) {
					Logger.error(e);
					returnmsg.append("获取人员详细信息失败，请稍后再试__部门名称:" + department.getName() + ",人员名称:" + user.getName() + "\n");
				}
				if (userDetail == null) {
					continue;
				}
				String mobile = userDetail.getMobile();
				sql.delete(0, sql.length());
				sql.append("select b.begindate,b.pk_group,b.pk_org,a.pk_psndoc,b.pk_psnjob,b.pk_psnorg from bd_psndoc a left join hi_psnjob b on a.pk_psndoc = b.pk_psndoc  where a.mobile='" + mobile + "' and b.lastflag='Y'");
				Map<String, String> result = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (result == null) {
					returnmsg.append("未能找到相应的手机号码，请在HR系统维护手机号码__钉钉人员:" + userDetail.getName() + ",手机号:" + userDetail.getMobile() + "\n");
					continue;
				}
				//保存待插入数据
				TBMPsndocVO vo = new TBMPsndocVO();
				vo.setBegindate(new UFLiteralDate("2017-08-01"));//考勤开始时间result.get("begindate")
				vo.setCreationtime(new UFDateTime(Calendar.getInstance().getTime()));//创建时间
				vo.setPk_psndoc(result.get("pk_psndoc"));//默认他自己
				vo.setPk_group(result.get("pk_group"));//集团
				vo.setPk_org(result.get("pk_org"));//组织
				vo.setPk_psndoc(result.get("pk_psndoc"));//人员主键
				vo.setPk_psnjob(result.get("pk_psnjob"));//工作主键
				vo.setPk_psnorg(result.get("pk_psnorg"));//组织关系
				vo.setEnddate(new UFLiteralDate("9999-12-01"));//结束日期
				vo.setTbm_prop(2);//考勤方式:1=手工考勤，2=机器考勤
				vo.setTimecardid(userDetail.getUserid());//考勤卡号
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
				returnmsg.append("未能找到相应的手机号码，请在HR系统维护手机号码__钉钉人员:" + userDetail.getName() + ",手机号:" + userDetail.getMobile() + "\n");
				continue;
			}
			//保存待插入数据
			TBMPsndocVO vo = new TBMPsndocVO();
			vo.setBegindate(new UFLiteralDate("2017-08-01"));//考勤开始时间result.get("begindate")
			vo.setCreationtime(new UFDateTime(Calendar.getInstance().getTime()));//创建时间
			vo.setPk_psndoc(result.get("pk_psndoc"));//默认他自己
			vo.setPk_group(result.get("pk_group"));//集团
			vo.setPk_org(result.get("pk_org"));//组织
			vo.setPk_psndoc(result.get("pk_psndoc"));//人员主键
			vo.setPk_psnjob(result.get("pk_psnjob"));//工作主键
			vo.setPk_psnorg(result.get("pk_psnorg"));//组织关系
			vo.setEnddate(new UFLiteralDate("9999-12-01"));//结束日期
			vo.setTbm_prop(2);//考勤方式:1=手工考勤，2=机器考勤
			vo.setTimecardid(userDetail.getUserid());//考勤卡号
			vo.setPk_adminorg(result.get("pk_org"));//管理组织
			insertvos.add(vo);
		}
		//		VOInsert<TBMPsndocVO> voInsert = new VOInsert<TBMPsndocVO>();
		//执行保存
		TBMPsndocDelegator.getTBMPsndocManageMaintain().check(insertvos.toArray(new TBMPsndocVO[0]));
		TBMPsndocDelegator.getTBMPsndocManageMaintain().insert(insertvos.toArray(new TBMPsndocVO[0]), true);
		//		voInsert.insert(insertvos.toArray(new TBMPsndocVO[0]));
		returnmsg.append("同步完成\n");
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
			Logger.error("获取token失败", e);
			throw new BusinessException("获取token失败", e);
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
