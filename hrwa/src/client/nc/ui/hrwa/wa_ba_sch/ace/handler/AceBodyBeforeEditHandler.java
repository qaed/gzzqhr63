package nc.ui.hrwa.wa_ba_sch.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
/**
 * �����ֶα༭ǰ�¼�������
 * 
 * @since 6.0
 * @version 2011-7-7 ����02:52:57
 * @author duy
 */
public class AceBodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {
	private UIRefPane uiref;
    @Override
    public void handleAppEvent(CardBodyBeforeEditEvent e) {
    	if (e.getKey().equals("ba_unit_code")) {
			uiref = (UIRefPane) e.getBillCardPanel().getBodyItem("ba_unit_code").getComponent();
			uiref.setMultiSelectedEnabled(true);// ��ѡ
		}
    }

}
