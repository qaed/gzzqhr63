package nc.ui.ta.dailydata.action;

import java.util.HashMap;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4DateView;
import nc.ui.ta.dailydata.model.datamanager.DateAppModelDataManager;
import nc.ui.ta.dailydata.model.datamanager.PsnAppModelDataManager;
import nc.ui.ta.pub.action.FromWhereSQLDateScopeQueryAction;
import nc.ui.ta.pub.model.IFromWhereSQLDateScopeManager;
import nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.util.SqlWhereUtil;

import org.apache.commons.lang.StringUtils;

public class QueryDailyDataAction extends FromWhereSQLDateScopeQueryAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5292660561719212885L;

	DateAppModelDataManager dateManager;
	DailyDataAppModelDataManager4DateView dateViewDailyDataManager;

	public QueryDailyDataAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void executeQuery(IQueryScheme queryScheme) {
		//tsy 考勤数据查询添加部门权限控制
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
		((QueryScheme)queryScheme).putTableJoinFromWhereSQL(fromWhereSQL);

		super.executeQuery(queryScheme);
		FromWhereSQLDateScopeQueryDelegator delegator = (FromWhereSQLDateScopeQueryDelegator)getQueryDelegator();
		UFLiteralDate begindateDate = delegator.getBeginDate();
		UFLiteralDate endDateDate = delegator.getEndDate();
		dateViewDailyDataManager.setFromWhereSQL(queryScheme.getTableJoinFromWhereSQL());
		dateManager.initModelByDateScope(begindateDate, endDateDate);
	}

	public DateAppModelDataManager getDateManager() {
		return dateManager;
	}

	public void setDateManager(DateAppModelDataManager dateManager) {
		this.dateManager = dateManager;
	}

	public DailyDataAppModelDataManager4DateView getDateViewDailyDataManager() {
		return dateViewDailyDataManager;
	}

	public void setDateViewDailyDataManager(DailyDataAppModelDataManager4DateView dateViewDailyDataManager) {
		this.dateViewDailyDataManager = dateViewDailyDataManager;
	}

	protected void setStatusBarMsg() {
		if (!queryExcuted) {
			putValue(MESSAGE_AFTER_ACTION, ResHelper.getString("6001uif2", "06001uif20002")
			/* @res "已取消！" */);

			return;
		}

		if (getFromWhereSQLDataManager() instanceof IFromWhereSQLDateScopeManager) {
			int iCount = ((PsnAppModelDataManager) getFromWhereSQLDataManager()).getQueryDataCount();

			String strMsg = iCount == 0 ? ResHelper.getString("6001uif2", "06001uif20045")
			/* @res "未查到符合条件的数据，请确认查询条件后重新查询。" */: ResHelper.getString("6001uif2", "06001uif20046", String.valueOf(iCount))
			/* @res "查询成功，已查到{0}条数据。" */;

			putValue(MESSAGE_AFTER_ACTION, strMsg);
		} else {
			putValue(MESSAGE_AFTER_ACTION, null);
		}
	}
}
