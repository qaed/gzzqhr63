package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.AbstractReferenceAction;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;

public class WaBaSchShowAllotAction extends NCAction {
	private BillManageModel model;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		WaBaSchAllotDlg dlg = new WaBaSchAllotDlg(model);
		dlg.showModal();
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

}