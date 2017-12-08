package nc.bs.hrss.ta.monthreport.ctrl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.HrssConsts;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.AppSession;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.comp.RadioGroupComp;
import nc.uap.lfw.core.comp.text.TextComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.TextEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.portal.log.PortalLogger;
import nc.uap.portal.task.itf.ITaskQryTmp;
import nc.uap.portal.task.ui.TaskHelper;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.model.TaskProcessResult;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.workflownote.WorkflownoteVO;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * @author chouhl
 */
public class MonthReportForMngOperateController implements IController {
	private static final long serialVersionUID = 1L;
	private final String WFMTASKQRY = "wfmtaskqry";

	public MonthReportForMngOperateController() {
	}

	public void onclick_btn_ok(MouseEvent mouseEvent) {
		RadioGroupComp operate = (RadioGroupComp) getNowView("taskoperate").getViewComponents().getComponent("operate");
		String val = operate.getValue();
		if (StringUtils.isBlank(val)) {
			AppInteractionUtil.showMessageDialog(NCLangRes4VoTransl.getNCLangRes().getStrByID("pint", "TaskOperateViewController-000000"));
			return;
		}

		LfwView taskoperate = AppUtil.getCntWindowCtx().getViewContext("taskoperate").getView();
		TextComp com2 = (TextComp) taskoperate.getViewComponents().getComponent("content");
		String content = com2.getValue();

		Map<String, Set<String>> sel = getTaskPks();
		int suc = 0;
		int fail = 0;
		StringBuffer msg = new StringBuffer();
		for (Map.Entry<String, Set<String>> entry : sel.entrySet()) {
			String pluginid = (String) entry.getKey();
			String[] pks = (String[]) ((Set) entry.getValue()).toArray(new String[0]);
			ITaskQryTmp taskQry = TaskHelper.getTaskQry(pluginid);
			try {
				TaskProcessResult[] tpr = taskQry.doMutiTaskProcess(pks, val, content);
				if ((tpr == null) || (tpr.length == 0)) {
					fail += pks.length;
				} else {
					for (TaskProcessResult tp : tpr)
						if (tp.getIsPass().booleanValue()) {
							suc++;
						} else {
							fail++;
							msg.append(tp.getBillNo()).append(",");
						}
				}
			} catch (Throwable e) {
				fail += pks.length;
				PortalLogger.error(taskQry.getClass().getName() + ": mutiltask process fail " + e.getMessage(), e);
			}
		}
		StringBuffer message = new StringBuffer(NCLangRes4VoTransl.getNCLangRes().getStrByID("pint", "TaskOperateViewController-000001"));
		message.append(suc).append(NCLangRes4VoTransl.getNCLangRes().getStrByID("pint", "TaskOperateViewController-000002")).append(fail).append(NCLangRes4VoTransl.getNCLangRes().getStrByID("pint", "TaskOperateViewController-000003"));
		if (StringUtils.isNotBlank(msg.toString()))
			message.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("pint", "TaskOperateViewController-000004")).append(msg.toString().substring(0, msg.toString().length() - 1));
		closeView("taskoperate");
		AppInteractionUtil.showMessageDialog(message.toString(), false);
		AppUtil.getCntAppCtx().addExecScript(";try{getTrueParent().reloadData();}catch(e){}");
	}

	private Map<String, Set<String>> getTaskPks() {
		Dataset ds = getDataset("main", "dsMonthReport");
		Row[] selRows = ds.getAllRow();
		if ((selRows == null) || (selRows.length == 0)) {
			return new HashMap(0);
		}
		AppSession appsession = LfwRuntimeEnvironment.getWebContext().getAppSession();
		String sys = "hrsswfmqry";
		WorkflownoteVO workflownoteVO = null;
		
		String billtype = (String) AppUtil.getAppAttr(HrssConsts.BILL_TYPE_CODE);
		String billid = (String) AppUtil.getAppAttr(WfmConstants.WfmAppAttr_BillID);
		try {
			workflownoteVO =
					NCLocator.getInstance().lookup(IWorkflowMachine.class).checkWorkflowActions(billtype, billid);
		} catch (BusinessException e) {
			throw new LfwRuntimeException(e);
		}
		String id = workflownoteVO.getPk_checkflow();
		Map<String, Set<String>> sel = new HashMap();

			Set<String> ids = new HashSet();
			ids.add(id);
			sel.put(sys, ids);
		return sel;
	}

	private Dataset getDataset(String viewId, String datasetid) {
		LfwView lfwWidget = getNowView(viewId);
		return lfwWidget != null ? lfwWidget.getViewModels().getDataset(datasetid) : null;
	}

	private LfwView getNowView(String viewId) {
		ViewContext contex = AppLifeCycleContext.current().getWindowContext().getViewContext(viewId);
		return contex != null ? contex.getView() : null;
	}

	public void operbeforeShow(DialogEvent dialogEvent) {
		ComboData comboData = getNowView("taskoperate").getViewModels().getComboData("opertateitem");
		RadioGroupComp radioGroupComp = (RadioGroupComp) getNowView("taskoperate").getViewComponents().getComponent("operate");
		String state = (String) AppUtil.getAppAttr("$$curstate");
		if ("State_UnRead".equals(state)) {
			comboData.removeComboItem("agree");
			radioGroupComp.setValue("readend");
		} else {
			comboData.removeComboItem("readend");
			radioGroupComp.setValue("agree");
		}
	}

	public void onclick_btn_cancel(MouseEvent mouseEvent) {
		closeView("taskoperate");
	}

	private void closeView(String viewid) {
		AppLifeCycleContext.current().getViewContext().getView().getWindow().setHasChanged(Boolean.valueOf(false));
		AppLifeCycleContext.current().getApplicationContext().getCurrentWindowContext().closeView(viewid);
	}

	public void valueChanged_operate(TextEvent textEvent) {
	}

}
