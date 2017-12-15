package nc.ui.ta.monthstat.action;

import java.awt.event.ActionEvent;

import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.model.PsnMonthStatModelDataManager;
import nc.ui.ta.monthstat.view.GenerateDialog;
import nc.ui.ta.pub.action.ActionInitializer;
import nc.ui.uif2.IShowMsgConstant;
import nc.vo.ta.monthstat.MonthStatVO;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class GenerateMonthStatAction extends HrAction {
	private static final long serialVersionUID = -7108928885893381921L;
	private GenerateDialog dlg = null;
	PsnMonthStatModelDataManager dataManager;

	public GenerateMonthStatAction() {
		ActionInitializer.initializeAction(this, "Generate");
	}

	public void doAction(ActionEvent e) throws Exception {
		int result = getDlg().showModal();
		if (2 == result) {
			putValue("message_after_action", IShowMsgConstant.getCancelInfo());
		}
	}

	private GenerateDialog getDlg() {
		if (this.dlg == null) {
			this.dlg = new GenerateDialog(getContext().getEntranceUI());
			this.dlg.setModel((PsnMonthStatAppModel) getModel());
			this.dlg.setDataManager(getDataManager());
			this.dlg.initUI();
		}
		return this.dlg;
	}

	protected boolean isActionEnable() {
		PsnMonthStatAppModel model = (PsnMonthStatAppModel) getModel();
		MonthStatVO[] vos = model.getData();
		if (vos == null || vos.length == 0) {
			return true;
		}
		for (MonthStatVO monthStatVO : vos) {
			if (monthStatVO.getApprovestatus() != ApproveStatus.FREE) {
				return false;
			}
		}
		return !StringUtils.isBlank(getContext().getPk_org());
	}

	public PsnMonthStatModelDataManager getDataManager() {
		return this.dataManager;
	}

	public void setDataManager(PsnMonthStatModelDataManager dataManager) {
		this.dataManager = dataManager;
	}
}