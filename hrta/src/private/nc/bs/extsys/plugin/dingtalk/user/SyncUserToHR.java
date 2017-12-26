package nc.bs.extsys.plugin.dingtalk.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import nc.bs.uif2.validation.ValidationException;
import nc.impl.ta.psndoc.TBMPsndocDAO;
import nc.itf.ta.IPeriodQueryMaintain;
import nc.itf.ta.TBMPsndocDelegator;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;

import org.apache.commons.lang.StringUtils;

import com.dingtalk.open.client.api.model.corp.CorpUser;
import com.dingtalk.open.client.api.model.corp.CorpUserDetail;
import com.dingtalk.open.client.api.model.corp.CorpUserList;
import com.dingtalk.open.client.api.model.corp.Department;

@SuppressWarnings("restriction")
public class SyncUserToHR implements IBackgroundWorkPlugin {
	IPeriodQueryMaintain periodmaintain = NCLocator.getInstance().lookup(IPeriodQueryMaintain.class);
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
		alert.setMsgTitle("同步人员任务执行情况22223");
		StringBuilder returnmsg = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
		//即将保存的考勤档案
		List<TBMPsndocVO> insertvos = new ArrayList<TBMPsndocVO>();
		//即将更新考勤卡号的考勤档案
		List<TBMPsndocVO> updatevos = new ArrayList<TBMPsndocVO>();
		//已插入的pk_psndoc
		//		List<String> insertpsndocs = new ArrayList<String>();
		Set<String> insertpk_psndoc = new HashSet<String>();
		TBMPsndocDAO tbmdao = new TBMPsndocDAO();

		List<CorpUserDetail> userList = getAllUser();
		returnmsg.append("以下未能找到相应的手机号码，请在HR系统维护手机号码:" );
		for (CorpUserDetail userDetail : userList) {
			String mobile = userDetail.getMobile();
			sql.delete(0, sql.length());
			sql.append("select b.begindate,b.pk_group,b.pk_org,a.pk_psndoc,b.pk_psnjob,b.pk_psnorg from bd_psndoc a left join hi_psnjob b on a.pk_psndoc = b.pk_psndoc  where a.mobile='" + mobile + "' and b.lastflag='Y'");
			Map<String, String> result = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
			if (result == null) {
				returnmsg.append(userDetail.getName() + ",手机号:" + userDetail.getMobile() + ";");
				continue;
			}
			//过滤重复人员
			if (insertpk_psndoc.contains(result.get("pk_psndoc"))) {
				continue;
			}
			//保存待插入数据
			TBMPsndocVO vo = new TBMPsndocVO();
			String begindate = (String) getDao().executeQuery("select min(timeyear||'-'||timemonth||'-01') from tbm_period where sealflag='N'", new ColumnProcessor());
			vo.setBegindate(new UFLiteralDate(begindate));//考勤开始时间result.get("begindate")
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
			//			insertpsndocs.add(result.get("pk_psndoc"));

			insertpk_psndoc.add(result.get("pk_psndoc"));//保存到一个set，方便去重
			insertvos.add(vo);
			try {
				TBMPsndocDelegator.getTBMPsndocManageMaintain().check(vo);
			} catch (BusinessException e) {
				if (e instanceof ValidationException && e.getMessage() != null && e.getMessage().contains("未结束的考勤档案")) {
					//报错为：XXX已经有未结束的考勤档案！
					TBMPsndocVO tbmvo =
							tbmdao.queryLatestByPsndocDate(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), new UFLiteralDate());
					//更新tbmvo的考勤卡号
					if (!StringUtils.equals(tbmvo.getTimecardid(), userDetail.getUserid())) {
						tbmvo.setTimecardid(userDetail.getUserid());
						updatevos.add(tbmvo);
					}
				} else {
					returnmsg.append(e.getMessage()+"\n");
				}
				insertpk_psndoc.remove(result.get("pk_psndoc"));
				insertvos.remove(vo);

			}
		}
		//		VOInsert<TBMPsndocVO> voInsert = new VOInsert<TBMPsndocVO>();
		//执行保存
//		TBMPsndocDelegator.getTBMPsndocManageMaintain().check(insertvos.toArray(new TBMPsndocVO[0]));
		TBMPsndocDelegator.getTBMPsndocManageMaintain().insert(insertvos.toArray(new TBMPsndocVO[0]), true);
		getDao().updateVOArray(updatevos.toArray(new TBMPsndocVO[0]), new String[] { TBMPsndocVO.TIMECARDID });
		//		voInsert.insert(insertvos.toArray(new TBMPsndocVO[0]));
		returnmsg.append("同步完成\n");
		alert.setReturnObj(returnmsg.toString());
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);


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
