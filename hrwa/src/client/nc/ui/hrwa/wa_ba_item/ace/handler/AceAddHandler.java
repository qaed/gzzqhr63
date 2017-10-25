package nc.ui.hrwa.wa_ba_item.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.AppContext;

@SuppressWarnings("restriction")
public class AceAddHandler implements IAppEventHandler<AddEvent> {

	@Override
	public void handleAppEvent(AddEvent e) {
		BillCardPanel cardPanel = e.getBillForm().getBillCardPanel();
		AppContext appcontext = AppContext.getInstance();
		//		WorkbenchEnvironment  workbenchEnvironment = WorkbenchEnvironment.getInstance();
		String pk_org = e.getContext().getPk_org();
		String pk_group = appcontext.getPkGroup();
		String pk_user = appcontext.getPkUser();
		UFDateTime creationtime = appcontext.getServerTime();
		cardPanel.setHeadItem("pk_org", pk_org);
		cardPanel.setHeadItem("pk_group", pk_group);
		cardPanel.setTailItem("creator", pk_user);
		cardPanel.setTailItem("creationtime", creationtime);

		//initParentCode(e);
	}
	/*
	private void initParentCode(AddEvent e) {
		BillItem parentCode = e.getBillForm().getBillCardPanel().getHeadItem("");// ItemsVO.${parentCode}
		if (e.getBillForm().getModel().getSelectedData() != null) {
			ItemsVO vo = (ItemsVO) e.getBillForm().getModel().getSelectedData();
			((UIRefPane) parentCode.getComponent()).setPK(vo.getPrimaryKey());
			parentCode.setEdit(false);
		} else {
			parentCode.setEdit(true);
		}
	}
	*/
}
