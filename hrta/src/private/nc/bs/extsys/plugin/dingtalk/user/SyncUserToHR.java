package nc.bs.extsys.plugin.dingtalk.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.extsys.plugin.dingtalk.AlarmFormateMsg;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.department.DepartmentHelper;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.impl.ta.psndoc.TBMCardIDValidator;
import nc.impl.ta.psndoc.TBMPsndocDAO;
import nc.impl.ta.psndoc.TBMPsndocDateValidator;
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

	private int getUserDetailErrorTimes = 0;
	private int getUserDetailErrorTimesMax = 20;
	private List<CorpUserDetail> userDetails = new ArrayList<CorpUserDetail>();

	@Override
	/**
	 * 初次执行，把钉钉的人员先同步到考勤档案
	 */
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		PreAlertObject alert = new PreAlertObject();
		alert.setMsgTitle("同步人员任务执行情况");
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
		Set<String> msgSet = new HashSet<String>();
		List<CorpUserDetail> userList = getAllUser();
		for (CorpUserDetail userDetail : userList) {
			String mobile = userDetail.getMobile();
			sql.delete(0, sql.length());
			sql.append("select b.begindate,b.pk_group,b.pk_org,a.pk_psndoc,b.pk_psnjob,b.pk_psnorg from bd_psndoc a left join hi_psnjob b on a.pk_psndoc = b.pk_psndoc  where a.mobile='" + mobile + "' and b.lastflag='Y' and b.ismainjob='Y' and b.endflag='N' ");
			Map<String, String> result = (Map<String, String>) dao.executeQuery(sql.toString(), new MapProcessor());
			if (result == null) {
				msgSet.add("未在HR系统中找到对应的手机号：" + userDetail.getName() + ":" + userDetail.getMobile());
				continue;
			}
			//过滤重复人员
			if (insertpk_psndoc.contains(result.get("pk_psndoc"))) {
				continue;
			}
			//保存待插入数据
			TBMPsndocVO vo = new TBMPsndocVO();
			String begindate =
					(String) dao.executeQuery("select min(timeyear||'-'||timemonth||'-01') from tbm_period where sealflag='N' and pk_org='" + result.get("pk_org") + "'", new ColumnProcessor());
			if (StringUtils.isBlank(begindate)) {
				msgSet.add("无未封存的考勤期间，pk_psndoc:" + result.get("pk_psndoc") + "，pk_org:" + result.get("pk_org"));
				continue;
			}
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
				//				TBMPsndocDelegator.getTBMPsndocManageMaintain().check(vo);
				DefaultValidationService vService = new DefaultValidationService();
				TBMCardIDValidator idValidator = new TBMCardIDValidator();
				vService.addValidator(idValidator);
				TBMPsndocDateValidator dateValidator = new TBMPsndocDateValidator();
				vService.addValidator(dateValidator);
				vService.validate(vo);

			} catch (BusinessException e) {
				if (e instanceof ValidationException && e.getMessage() != null && e.getMessage().contains("未结束的考勤档案")) {
					//报错为：XXX已经有未结束的考勤档案！
					TBMPsndocVO tbmvo =
							tbmdao.queryLatestByPsndocDate(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), new UFLiteralDate());
					//							(TBMPsndocVO[]) CommonUtils.toArray(TBMPsndocVO.class, (Collection) dao.executeQuery("select top 1 * from tbm_psndoc where pk_org=" + vo.getPk_org() + " and begindate<=" + vo.getBegindate() + " and enddate>=" + new UFLiteralDate() + " order by begindate desc", new BeanListProcessor(TBMPsndocVO.class)));
					//					TBMPsndocVO tbmvo = tbmvos == null ? null : tbmvos[0];
					//更新tbmvo的考勤卡号
					if (!StringUtils.equals(tbmvo.getTimecardid(), userDetail.getUserid())) {
						tbmvo.setTimecardid(userDetail.getUserid());
						updatevos.add(tbmvo);
					}
				} else {
					msgSet.add(e.getMessage());
				}
				insertpk_psndoc.remove(result.get("pk_psndoc"));
				insertvos.remove(vo);

			}
		}
		//		VOInsert<TBMPsndocVO> voInsert = new VOInsert<TBMPsndocVO>();
		//执行保存
		//		TBMPsndocDelegator.getTBMPsndocManageMaintain().check(insertvos.toArray(new TBMPsndocVO[0]));
		dao.updateVOArray(updatevos.toArray(new TBMPsndocVO[0]), new String[] { TBMPsndocVO.TIMECARDID });
		TBMPsndocDelegator.getTBMPsndocManageMaintain().insert(insertvos.toArray(new TBMPsndocVO[0]), true);
		//		NCLocator.getInstance().lookup(ITBMPsndocManageMaintain.class).insert(insertvos.toArray(new TBMPsndocVO[0]), true);
		//		voInsert.insert(insertvos.toArray(new TBMPsndocVO[0]));
		AlarmFormateMsg msg = new AlarmFormateMsg();
		msg.setTitle("详细信息");
		msg.setBodyFields(new String[] { "内容" });
		String[][] bodyValues = new String[msgSet.size()][1];
		int index = 0;
		for (String str : msgSet) {
			String[] value = bodyValues[index];
			value[0] = str;
			index++;
		}
		msg.setBodyValue(bodyValues);
		alert.setReturnObj(msg);
		alert.setReturnType(PreAlertReturnType.RETURNFORMATMSG);

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
