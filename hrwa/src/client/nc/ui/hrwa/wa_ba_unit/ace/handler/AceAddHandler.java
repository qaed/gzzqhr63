package nc.ui.hrwa.wa_ba_unit.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.pubapp.AppContext;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

public class AceAddHandler implements IAppEventHandler<AddEvent> {
	@Override
	public void handleAppEvent(AddEvent e) {
		BillCardPanel cardPanel = e.getBillForm().getBillCardPanel();
		String pk_group = AppContext.getInstance().getPkGroup();
		cardPanel.setHeadItem("pk_group", pk_group);
	}
}
