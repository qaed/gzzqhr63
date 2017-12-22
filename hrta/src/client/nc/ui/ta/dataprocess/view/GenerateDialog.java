package nc.ui.ta.dataprocess.view;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITimeDataManageMaintain;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.ui.ta.dailydata.model.PsnAppModel;
import nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4DateView;
import nc.ui.ta.dailydata.model.datamanager.DateAppModelDataManager;
import nc.ui.ta.dailydata.model.datamanager.PsnAppModelDataManager;
import nc.ui.ta.pub.selpsn.ConditionSelPsnDateScopePanel;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.logging.Debug;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.util.SqlWhereUtil;

@SuppressWarnings("restriction")
public class GenerateDialog extends HrDialog {
	private static final long serialVersionUID = -8383642597559315912L;
	PsnAppModel model = null;
	PsnAppModelDataManager psnDataManager;
	DateAppModelDataManager dateManager;
	DailyDataAppModelDataManager4DateView dateViewDailyDataManager;

	public GenerateDialog(Container parent) {
		this(parent, ResHelper.getString("6017dataprocess", "06017dataprocess0025"));
	}

	public GenerateDialog(Container parent, String title) {
		super(parent, title, false);
	}

	protected JComponent createCenterPanel() {
		ConditionSelPsnDateScopePanel conditionPanel = new ConditionSelPsnDateScopePanel();
		conditionPanel.setModel(this.model);
		conditionPanel.init();
		conditionPanel.getDateScopePanel().getRefEndDate().setValueObj(new UFLiteralDate());
		return conditionPanel;
	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if ((e.getID() == 205) || (e.getID() == 200)) {
			ConditionSelPsnDateScopePanel component = (ConditionSelPsnDateScopePanel) getCenterPanel();
			List<IFilterEditor> simpleEditorFilterEditors = component.getQueryEditor().getSimpleEditorFilterEditors();
			IFilterEditor iFilterEditor = (IFilterEditor) simpleEditorFilterEditors.get(0);
			iFilterEditor.getFilterEditorComponent().requestFocus();
		}
	}

	public ConditionSelPsnDateScopePanel getConditionSelPsnDateScopePanel() {
		return (ConditionSelPsnDateScopePanel) getCenterPanel();
	}

	protected JComponent createNorthPanel() {
		return null;
	}

	public PsnAppModel getModel() {
		return this.model;
	}

	public void setModel(PsnAppModel model) {
		this.model = model;
	}

	public void closeOK() {
		try {
			getConditionSelPsnDateScopePanel().validateData();
		} catch (ValidationException e) {
			Debug.error(e.getMessage());
			MessageDialog.showErrorDlg(this, null, e.getMessage());
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				UFLiteralDate beginDate = GenerateDialog.this.getConditionSelPsnDateScopePanel().getBeginDate();
				UFLiteralDate endDate = GenerateDialog.this.getConditionSelPsnDateScopePanel().getEndDate();
				IProgressMonitor progressMonitor = null;
				try {
					progressMonitor = NCProgresses.createDialogProgressMonitor(GenerateDialog.this.getModel().getContext().getEntranceUI());
					progressMonitor.beginTask(PublicLangRes.GENERATE(), -1);
					progressMonitor.setProcessInfo(PublicLangRes.GENERATING());
					FromWhereSQL fromWhereSQL = GenerateDialog.this.getConditionSelPsnDateScopePanel().getQuerySQL();
					
					//tsy 考勤数据生成添加部门权限控制
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
					
					NCLocator.getInstance().lookup(ITimeDataManageMaintain.class).generate(GenerateDialog.this.model.getContext().getPk_org(), fromWhereSQL, beginDate, endDate);

					psnDataManager.initModelByFromWhereSQLAndDateScope(fromWhereSQL, beginDate, endDate);
					dateViewDailyDataManager.setFromWhereSQL(fromWhereSQL);
					dateManager.initModelByDateScope(beginDate, endDate);
					GenerateDialog.super.closeOK();
				} catch (Exception e) {
					Debug.error(e.getMessage(), e);
					MessageDialog.showErrorDlg(GenerateDialog.this.getParent(), null, e.getMessage());
				} finally {
					if (progressMonitor != null)
						progressMonitor.done();// 进度任务结束
				}
			}
		}).start();
	}

	public PsnAppModelDataManager getPsnDataManager() {
		return this.psnDataManager;
	}

	public void setPsnDataManager(PsnAppModelDataManager psnDataManager) {
		this.psnDataManager = psnDataManager;	
	}

	public DateAppModelDataManager getDateManager() {
		return this.dateManager;
	}

	public void setDateManager(DateAppModelDataManager dateManager) {
		this.dateManager = dateManager;
	}

	public DailyDataAppModelDataManager4DateView getDateViewDailyDataManager() {
		return this.dateViewDailyDataManager;
	}

	public void setDateViewDailyDataManager(DailyDataAppModelDataManager4DateView dateViewDailyDataManager) {
		this.dateViewDailyDataManager = dateViewDailyDataManager;
	}
}