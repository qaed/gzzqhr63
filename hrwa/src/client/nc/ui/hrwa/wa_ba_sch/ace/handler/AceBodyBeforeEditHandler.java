package nc.ui.hrwa.wa_ba_sch.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
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
			//���˵�ǰѡ���ڼ��ѽ��з���ĵ�Ԫ
			String cperiod = e.getBillCardPanel().getHeadItem("cperiod").getValueObject().toString();
			String cyear = e.getBillCardPanel().getHeadItem("cyear").getValueObject().toString();
			uiref.getRefModel().setWherePart("isnull(dr,0)=0 and pk_org='" + e.getContext().getPk_org() + "' and pk_wa_ba_unit not in (select distinct unit.ba_unit_code from wa_ba_sch_unit unit left join wa_ba_sch_h h on unit.pk_ba_sch_h =h.pk_ba_sch_h where h.cperiod = '" + cperiod + "' and h.cyear = '" + cyear + "' and isnull(dr,0)=0 )");
			uiref.getRefModel().reloadData();
		}
	}

}
