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
	/** �Ƿ�ִ���˲�ѯ������û������ѯ������ѯ�Ի���󣬵����ȡ������˱���Ϊfalse ��ʾ ��ȡ���� */
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
							/* @res "��ѯ����..." */, IProgressMonitor.UNKNOWN_REMAIN_TIME);
					progressMonitor.setProcessInfo(ResHelper.getString("6001uif2", "06001uif20043")
					/* @res "���ݲ�ѯ�У����Ժ�....." */);

//					ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6001uif2", "06001uif20043")
//							/* @res "���ݲ�ѯ�У����Ժ�....." */, getContext());
					FromWhereSQLDateScopeQueryDelegator delegator = getPsnDelegator();
					UFLiteralDate beginDate = delegator.getBeginDate();
					UFLiteralDate endDate = delegator.getEndDate();
					
					//tsy �����ձ���Ա��ѯ��Ӳ���Ȩ�޿���
					FromWhereSQL fromWhereSQL = queryScheme.getTableJoinFromWhereSQL();
					if (fromWhereSQL.getFrom() == null) {//�ղ�ѯ�������Լ�����һ��fromwheresql
						fromWhereSQL = new FromWhereSQLImpl();
						((FromWhereSQLImpl) fromWhereSQL).setFrom("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob");
						Map<String, String> aliasMap = new HashMap<String, String>();
						aliasMap.put(".", "tbm_psndoc");
						aliasMap.put("pk_psnjob", "T1");
						((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
					}
					SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
					String tableAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
					// ����Ȩ��
					String permission =
							HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, tableAlias);
					if (StringUtils.isNotBlank(permission)) {
						sqlWhereUtil.and(permission);
					}
					Logger.error("���Ȩ�޺�sql:" + sqlWhereUtil.getSQLWhere());
					fromWhereSQL = new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
					
					getPsnManager().initModelByFromWhereSQL(fromWhereSQL,beginDate,endDate);
					queryExcuted = true;
				} finally {
					progressMonitor.done(); // �����������
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
							/* @res "��ѯ����..." */, IProgressMonitor.UNKNOWN_REMAIN_TIME);
					progressMonitor.setProcessInfo(ResHelper.getString("6001uif2", "06001uif20043")
					/* @res "���ݲ�ѯ�У����Ժ�....." */);

//					ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6001uif2", "06001uif20043")
//							/* @res "���ݲ�ѯ�У����Ժ�....." */, getContext());
					DeptQueryDelegator delegator =getDeptDelegator();
					UFLiteralDate beginDate = delegator.getBeginDate();
					UFLiteralDate endDate = delegator.getEndDate();
					
					//tsy �����ձ���ѯ��Ӳ���Ȩ�޿���
					FromWhereSQL fromWhereSQL = queryScheme.getTableJoinFromWhereSQL();
					if (fromWhereSQL.getFrom() == null) {//�ղ�ѯ�������Լ�����һ��fromwheresql
						fromWhereSQL = new FromWhereSQLImpl();
						((FromWhereSQLImpl) fromWhereSQL).setFrom("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob");
						Map<String, String> aliasMap = new HashMap<String, String>();
						aliasMap.put(".", "tbm_psndoc");
						aliasMap.put("pk_psnjob", "T1");
						((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
					}
					SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
					String tableAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
					// ����Ȩ��
					String permission =
							HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, tableAlias);
					if (StringUtils.isNotBlank(permission)) {
						sqlWhereUtil.and(permission);
					}
					Logger.error("���Ȩ�޺�sql:" + sqlWhereUtil.getSQLWhere());
					fromWhereSQL = new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
					
					getDeptManager().initModelByDeptsAndFromWhereSQL(delegator.getDeptPKs(),fromWhereSQL,beginDate,endDate);
					queryExcuted = true;
				} finally {
					progressMonitor.done(); // �����������
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
     * ����״̬����ʾ��Ϣ<br>
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
			String strMsg = iCount == 0 ? ResHelper.getString("6001uif2","06001uif20045")/* @res "δ�鵽�������������ݣ���ȷ�ϲ�ѯ���������²�ѯ��" */: 
				ResHelper.getString("6001uif2", "06001uif20046", String.valueOf(iCount))/* @res "��ѯ�ɹ����Ѳ鵽{0}�����ݡ�" */;
			putValue(MESSAGE_AFTER_ACTION, strMsg);
		} else {
			putValue(MESSAGE_AFTER_ACTION, null);
		}
	}
}
