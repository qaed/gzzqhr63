package nc.ui.hrwa.wa_ba_unit.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

public class AceAddHandler implements IAppEventHandler<AddEvent> {
	@Override
	public void handleAppEvent(AddEvent e) {
		BillCardPanel cardPanel = e.getBillForm().getBillCardPanel();
		String pk_org = e.getContext().getPk_org();
		AppContext appcontext = AppContext.getInstance();
		String pk_group = appcontext.getPkGroup();
		String pk_user = appcontext.getPkUser();
		UFDateTime creationtime = appcontext.getServerTime();
		cardPanel.setHeadItem("pk_org", pk_org);
		cardPanel.setHeadItem("pk_group", pk_group);
		cardPanel.setHeadItem("ba_unit_type", "�Զ�����䵥Ԫ");
		cardPanel.setTailItem("creator", pk_user);
		cardPanel.setTailItem("creationtime", creationtime);
	}
}
