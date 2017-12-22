package nc.ui.ta.psncalendar.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.QueryScopeEnum;
import nc.vo.util.SqlWhereUtil;

@SuppressWarnings("restriction")
public class QueryCalendarAction extends QueryAction {

	private static final long serialVersionUID = 2380662049691680978L;

	PsnCalendarAppModelDataManager psnCalendarAppModelDataManager;

	public QueryCalendarAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		validate(null);
		PsnCalendarQueryDelegator delegator = (PsnCalendarQueryDelegator) getQueryDelegator();
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
								progressMonitor.done(); // 进度任务结束
							} finally {
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

	public PsnCalendarAppModelDataManager getPsnCalendarAppModelDataManager() {
		return psnCalendarAppModelDataManager;
	}

	public void setPsnCalendarAppModelDataManager(PsnCalendarAppModelDataManager psnCalendarAppModelDataManager) {
		this.psnCalendarAppModelDataManager = psnCalendarAppModelDataManager;
	}

	protected void executeQuery(final IQueryScheme queryScheme) {
		PsnCalendarQueryDelegator delegator = (PsnCalendarQueryDelegator) getQueryDelegator();
		UFLiteralDate begindateDate = delegator.getBeginDate();
		UFLiteralDate endDateDate = delegator.getEndDate();
		QueryScopeEnum queryScopeEnum = delegator.getQueryScopeEnum();

		//tsy 员工日历查询添加部门权限控制
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

		psnCalendarAppModelDataManager.initModelByFromWhereSQL(fromWhereSQL, begindateDate, endDateDate, queryScopeEnum);
		queryExcuted = true;

	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
		//		UIState state = getModel().getUiState();
		//		return (state==UIState.INIT||state==UIState.NOT_EDIT)&&!StringUtils.isEmpty(getModel().getContext().getPk_org());
	}
}
