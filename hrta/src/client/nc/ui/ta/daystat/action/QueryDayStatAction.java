package nc.ui.ta.daystat.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.util.SqlWhereUtil;

public class QueryDayStatAction extends Abstract2TabsAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2849899281815449461L;
	
	private FromWhereSQLDateScopeQueryDelegator psnDelegator;
	private DeptQueryDelegator deptDelegator;
	/** 是否执行了查询，如何用户点击查询弹出查询对话框后，点击了取消，则此变量为false 提示 已取消。 */
    protected boolean queryExcuted = false;

	public QueryDayStatAction() {
		ActionInitializer.initializeAction(this, IActionCode.QUERY);
	}

	@Override
	protected void doDeptAction(ActionEvent e) throws Exception {
		validate(null);
		DeptQueryDelegator delegator =getDeptDelegator();
		try{
			delegator.doQuery(new IQueryExecutor(){
				public void doQuery(IQueryScheme queryScheme) {
					executeDeptQuery(queryScheme);
				}
			});
		} finally {
			setStatusBarMsg();
			queryExcuted = false;
		}
		
	}

	@Override
	protected void doPsnAction(ActionEvent e) throws Exception {
		validate(null);
		FromWhereSQLDateScopeQueryDelegator delegator =getPsnDelegator();
		try{
			delegator.doQuery(new IQueryExecutor(){
				
				public void doQuery(IQueryScheme queryScheme) {
					executePsnQuery(queryScheme);
				}
			});
		} finally {
			setStatusBarMsg();
			queryExcuted = false;
		}
	}
	
	protected void executePsnQuery(final IQueryScheme queryScheme){
		new Thread(){
			@Override
			public void run(){
				IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getEntranceUI());

				try{
					progressMonitor.beginTask(ResHelper.getString("6001uif2", "06001uif20042")
							/* @res "查询数据..." */, IProgressMonitor.UNKNOWN_REMAIN_TIME);
					progressMonitor.setProcessInfo(ResHelper.getString("6001uif2", "06001uif20043")
					/* @res "数据查询中，请稍候....." */);

//					ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6001uif2", "06001uif20043")
//							/* @res "数据查询中，请稍候....." */, getContext());
					FromWhereSQLDateScopeQueryDelegator delegator = getPsnDelegator();
					UFLiteralDate beginDate = delegator.getBeginDate();
					UFLiteralDate endDate = delegator.getEndDate();
					
					//tsy 考勤日报人员查询添加部门权限控制
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
					
					getPsnManager().initModelByFromWhereSQL(fromWhereSQL,beginDate,endDate);
					queryExcuted = true;
				} finally {
					progressMonitor.done(); // 进度任务结束
					setStatusBarMsg();
					ShowStatusBarMsgUtil.showStatusBarMsg((String) getValue(MESSAGE_AFTER_ACTION), getContext());
					queryExcuted = false;
				}
			}
		}.start();
	}
	
	protected void executeDeptQuery(final IQueryScheme queryScheme){
		new Thread() {
			@Override
			public void run() {
				IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getEntranceUI());

				try {
					progressMonitor.beginTask(ResHelper.getString("6001uif2", "06001uif20042")
							/* @res "查询数据..." */, IProgressMonitor.UNKNOWN_REMAIN_TIME);
					progressMonitor.setProcessInfo(ResHelper.getString("6001uif2", "06001uif20043")
					/* @res "数据查询中，请稍候....." */);

//					ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6001uif2", "06001uif20043")
//							/* @res "数据查询中，请稍候....." */, getContext());
					DeptQueryDelegator delegator =getDeptDelegator();
					UFLiteralDate beginDate = delegator.getBeginDate();
					UFLiteralDate endDate = delegator.getEndDate();
					
					//tsy 考勤日报查询添加部门权限控制
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
					
					getDeptManager().initModelByDeptsAndFromWhereSQL(delegator.getDeptPKs(),fromWhereSQL,beginDate,endDate);
					queryExcuted = true;
				} finally {
					progressMonitor.done(); // 进度任务结束
					setStatusBarMsg();
					ShowStatusBarMsgUtil.showStatusBarMsg((String) getValue(MESSAGE_AFTER_ACTION), getContext());
					queryExcuted = false;
				}
			}
		}.start();
	}
	public FromWhereSQLDateScopeQueryDelegator getPsnDelegator() {
		return psnDelegator;
	}

	public void setPsnDelegator(FromWhereSQLDateScopeQueryDelegator psnDelegator) {
		this.psnDelegator = psnDelegator;
	}

	public DeptQueryDelegator getDeptDelegator() {
		return deptDelegator;
	}

	public void setDeptDelegator(DeptQueryDelegator deptDelegator) {
		this.deptDelegator = deptDelegator;
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
		IAppModelDataManager dataManager = getCurShowingJudge().isCurPsnView()?getPsnManager():getDeptManager();
		if (dataManager instanceof IQueryInfo) {
			int iCount = ((IQueryInfo) dataManager).getQueryDataCount();
			String strMsg = iCount == 0 ? ResHelper.getString("6001uif2","06001uif20045")/* @res "未查到符合条件的数据，请确认查询条件后重新查询。" */: 
				ResHelper.getString("6001uif2", "06001uif20046", String.valueOf(iCount))/* @res "查询成功，已查到{0}条数据。" */;
			putValue(MESSAGE_AFTER_ACTION, strMsg);
		} else {
			putValue(MESSAGE_AFTER_ACTION, null);
		}
	}
}
