package nc.ui.ta.monthstat.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.ta.monthstat.model.DeptMonthStatModelDataManager;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.ta.period.PeriodVO;
import nc.vo.util.SqlWhereUtil;

import org.apache.commons.lang.StringUtils;

public class DeptMonthStatQueryAction extends QueryAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2182535881351308208L;
	DeptMonthStatModelDataManager deptManager;

	public DeptMonthStatQueryAction() {

	}

	public DeptMonthStatModelDataManager getDeptManager() {
		return deptManager;
	}

	public void setDeptManager(DeptMonthStatModelDataManager deptMonthStatModelDataManager) {
		this.deptManager = deptMonthStatModelDataManager;
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		validate(null);
		DeptQueryDelegator delegator = (DeptQueryDelegator) getQueryDelegator();
		try {
			delegator.doQuery(new IQueryExecutor() {

				public void doQuery(final IQueryScheme queryScheme) {
					new Thread() {
						@Override
						public void run() {

							IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getEntranceUI());

							try {
								progressMonitor.beginTask(ResHelper.getString("6001uif2", "06001uif20042")
								/* @res "查询数据..." */, IProgressMonitor.UNKNOWN_REMAIN_TIME);
								progressMonitor.setProcessInfo(ResHelper.getString("6001uif2", "06001uif20043")
								/* @res "数据查询中，请稍候....." */);
								executeQuery(queryScheme);
							} finally {
								progressMonitor.done(); // 进度任务结束
								setStatusBarMsg();
								ShowStatusBarMsgUtil.showStatusBarMsg((String) getValue(MESSAGE_AFTER_ACTION), getContext());
								queryExcuted = false;
							}
						}
					}.start();
				}
			});
		} finally {
			setStatusBarMsg();
			queryExcuted = false;
		}
	}

	@Override
	protected void executeQuery(final IQueryScheme queryScheme) {
		DeptQueryDelegator delegator = (DeptQueryDelegator) getQueryDelegator();
		PeriodVO periodVO = delegator.getPeriod();
		String[] pk_depts = delegator.getDeptPKs();

		//tsy 考勤月报部门查询添加部门权限控制
		FromWhereSQL fromWhereSQL = queryScheme.getTableJoinFromWhereSQL();
		if (fromWhereSQL.getFrom() == null) {//空查询条件，自己构造一个fromwheresql
			fromWhereSQL = new FromWhereSQLImpl();
			((FromWhereSQLImpl) fromWhereSQL).setFrom("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob");
			Map<String, String> aliasMap = new HashMap<String, String>();
			aliasMap.put(".", "tbm_psndoc");
			aliasMap.put("pk_psnjob", "T1");
			((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
		}
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String tableAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
		// 部门权限
		String permission =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, tableAlias);
		if (StringUtils.isNotBlank(permission)) {
			sqlWhereUtil.and(permission);
		}
		Logger.error("添加权限后sql:" + sqlWhereUtil.getSQLWhere());
		fromWhereSQL = new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());

		getDeptManager().initModelByFromWhereSQL(fromWhereSQL, pk_depts, periodVO);
		queryExcuted = true;
	}

	@Override
	protected boolean isActionEnable() {
		if (StringUtils.isEmpty(this.getModel().getContext().getPk_org())) {
			return false;
		}
		return true;
	}

	/***************************************************************************
	 * 设置状态栏显示信息<br>
	 * Created on 2011-8-12 9:49:26<br>
	 * @author Rocex Wang
	 ***************************************************************************/
	protected void setStatusBarMsg() {
		if (!queryExcuted) {
			setCancelMsg();
			return;
		}
		if (getDeptManager() instanceof IQueryInfo) {
			int iCount = ((IQueryInfo) getDeptManager()).getQueryDataCount();
			String strMsg =
					iCount == 0 ? ResHelper.getString("6001uif2", "06001uif20045")/* @res "未查到符合条件的数据，请确认查询条件后重新查询。" */: ResHelper.getString("6001uif2", "06001uif20046", String.valueOf(iCount))/* @res "查询成功，已查到{0}条数据。" */;
			putValue(MESSAGE_AFTER_ACTION, strMsg);
		} else {
			putValue(MESSAGE_AFTER_ACTION, null);
		}
	}
}
