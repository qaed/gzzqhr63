package nc.ui.ta.daystat.view;

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
import nc.itf.ta.IDayStatManageMaintain;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.ta.daystat.model.PsnDayStatAppModel;
import nc.ui.ta.daystat.model.PsnDayStatModelDataManager;
import nc.ui.ta.pub.selpsn.ConditionSelPsnDateScopePanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.daystat.DayStatVO;
import nc.vo.util.SqlWhereUtil;

public class GenerateDialog extends HrDialog implements AppEventListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -6400925133318159995L;

	PsnDayStatAppModel model;
	PsnDayStatModelDataManager dataManager;

	public GenerateDialog(Container parent) {
		this(parent, ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0003")
		/*@res "日报生成"*/);
	}

	public GenerateDialog(Container parent, String title) {
		super(parent, title, false);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected JComponent createCenterPanel() {
		ConditionSelPsnDateScopePanel conditionPanel = new ConditionSelPsnDateScopePanel();
		conditionPanel.setModel(model);
		conditionPanel.init();
		return conditionPanel;
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_ACTIVATED || e.getID() == WindowEvent.WINDOW_OPENED) {//获取焦点
			ConditionSelPsnDateScopePanel component = (ConditionSelPsnDateScopePanel) getCenterPanel();
			List<IFilterEditor> simpleEditorFilterEditors = component.getQueryEditor().getSimpleEditorFilterEditors();
			IFilterEditor iFilterEditor = simpleEditorFilterEditors.get(0);
			iFilterEditor.getFilterEditorComponent().requestFocus();
		}
	}

	@Override
	protected JComponent createNorthPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConditionSelPsnDateScopePanel getConditionSelPsnDateScopePanel() {
		return (ConditionSelPsnDateScopePanel) getCenterPanel();
	}

	public PsnDayStatAppModel getModel() {
		return model;
	}

	public void setModel(PsnDayStatAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void handleEvent(AppEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeOK() {
		try {
			getConditionSelPsnDateScopePanel().validateData();
		} catch (ValidationException e) {
			Debug.error(e.getMessage());
			MessageDialog.showErrorDlg(this, null, e.getMessage());
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				UFLiteralDate beginDate = getConditionSelPsnDateScopePanel().getBeginDate();
				UFLiteralDate endDate = getConditionSelPsnDateScopePanel().getEndDate();
				IProgressMonitor progressMonitor = null;
				try {
					progressMonitor = NCProgresses.createDialogProgressMonitor(getModel().getContext().getEntranceUI());
					progressMonitor.beginTask(PublicLangRes.GENERATE(), IProgressMonitor.UNKNOWN_REMAIN_TIME);
					progressMonitor.setProcessInfo(PublicLangRes.GENERATING());
					model.setBeginDate(beginDate);
					model.setEndDate(endDate);
					FromWhereSQL fromWhereSQL = getConditionSelPsnDateScopePanel().getQuerySQL();
					
					//tsy 考勤日报生成添加部门权限控制
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
					
					DayStatVO[] result =
							NCLocator.getInstance().lookup(IDayStatManageMaintain.class).generate(model.getContext().getPk_org(), fromWhereSQL, beginDate, endDate, dataManager.isShowNoDataRecord());
					//					model.initModel(result);分页无法显示
					dataManager.setBeginDate(beginDate);
					dataManager.setEndDate(endDate);
					dataManager.setFromWhereSQL(fromWhereSQL);
					dataManager.refresh();
					GenerateDialog.super.closeOK();
				} catch (BusinessException e) {
					Debug.error(e.getMessage(), e);
					MessageDialog.showErrorDlg(getParent(), null, e.getMessage());
					return;
				} finally {
					if (progressMonitor != null)
						progressMonitor.done(); // 进度任务结束
				}
			}
		}).start();
	}

	public PsnDayStatModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(PsnDayStatModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

}