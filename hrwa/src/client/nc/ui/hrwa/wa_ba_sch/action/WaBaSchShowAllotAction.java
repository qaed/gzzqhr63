package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.NCAction;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;

@SuppressWarnings("restriction")
public class WaBaSchShowAllotAction extends HrAction {
	private static final long serialVersionUID = -9161181519510543166L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		WaBaSchAllotDlg dlg = new WaBaSchAllotDlg((BillManageModel) getModel());
		dlg.showModal();
	}

	@Override
	protected boolean isActionEnable() {
		AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
		if (aggvo == null || aggvo.getParentVO().getApprovestatus() == ApproveStatus.FREE) {
			return false;
		}
		return super.isActionEnable();
	}
}
