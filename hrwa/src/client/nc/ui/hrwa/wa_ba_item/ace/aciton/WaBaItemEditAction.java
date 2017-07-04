package nc.ui.hrwa.wa_ba_item.ace.aciton;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillCardPanel;

public class WaBaItemEditAction extends nc.ui.pubapp.uif2app.actions.EditAction {

	@Override
	public void actionPerformed(ActionEvent e) {
//		BillCardPanel panel = e.getBillForm().getBillCardPanel();
		super.actionPerformed(e);
	}

	@Override
	protected void beforeDoAction() {
		//TODO tsy 组织状态下不能删除集团的
		super.beforeDoAction();
	}
	
}
