package nc.ui.hrwa.wa_ba_sch.ace.handler;

import nc.ui.hrwa.wa_ba_sch.action.WaBaSchCencelAllotAction;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.view.BillForm;

/**
 * ���ݱ����ֶα༭���¼�
 * 
 * @since 6.0
 * @version 2011-7-12 ����08:17:33
 * @author duy
 */
@SuppressWarnings("restriction")
public class AceBodyRowChangedHandler implements IAppEventHandler<CardBodyRowChangedEvent> {
	private BillForm billForm;

	@Override
	public void handleAppEvent(CardBodyRowChangedEvent e) {
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
