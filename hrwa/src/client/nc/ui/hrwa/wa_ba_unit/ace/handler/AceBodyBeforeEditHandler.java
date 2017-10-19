package nc.ui.hrwa.wa_ba_unit.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;

/**
 * �����ֶα༭ǰ�¼�������
 * 
 * @since 6.0
 * @version 2011-7-7 ����02:52:57
 * @author duy
 */
@SuppressWarnings("restriction")
public class AceBodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {
	private UIRefPane uiref;

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		e.setReturnValue(true);
		if (e.getKey().equals("pk_psndoc")) {
			uiref = (UIRefPane) e.getBillCardPanel().getBodyItem("pk_psndoc").getComponent();
			uiref.setMultiSelectedEnabled(true);// ��ѡ
		}
	}
}
