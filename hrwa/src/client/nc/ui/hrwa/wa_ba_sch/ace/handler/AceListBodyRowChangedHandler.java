package nc.ui.hrwa.wa_ba_sch.ace.handler;

import nc.ui.hrwa.wa_ba_sch.action.WaBaSchCencelAllotAction;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.list.ListBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.view.BillForm;

/**
 * ���ݱ����ֶα༭���¼�
 * 
 * @since 6.0
 * @version 2011-7-12 ����08:17:33
 * @author duy
 */
@SuppressWarnings("restriction")
public class AceListBodyRowChangedHandler implements IAppEventHandler<ListBodyRowChangedEvent> {
	private BillForm billForm;

	@Override
	public void handleAppEvent(ListBodyRowChangedEvent e) {
		//����ѡ����
		WaBaSchCencelAllotAction.setRow(e.getRow());
	}

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

}
