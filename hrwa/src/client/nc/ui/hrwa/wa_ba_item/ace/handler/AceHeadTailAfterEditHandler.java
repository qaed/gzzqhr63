package nc.ui.hrwa.wa_ba_item.ace.handler;

import nc.ui.pub.beans.UIButton;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;

public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent> {
	private UIButton button;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		// HYPubBO_Client bo = new HYPubBO_Client();
		// ����Ĭ��ֵ
		// ��ȡ���㷽ʽ 1-�ֹ����� 2��ʽ 3������Դ
		Integer datatype = (Integer) e.getBillCardPanel().getHeadItem("datatype").getValueObject();
		// for (int i = 0; i <
		// e.getBillCardPanel().getBillModel().getRowCount(); i++) {
		// e.getBillCardPanel().setBodyValueAt(pk_rewardtype, i,
		// "pk_rewardtype");
		// }
		if (datatype != null) {
			e.getBillCardPanel().getHeadItem("value").setEnabled(false);
			e.getBillCardPanel().getHeadItem("vformula").setEnabled(false);
			e.getBillCardPanel().getHeadItem("vformulastr").setEnabled(false);
			if (datatype == 1) {// �ֹ�����
				e.getBillCardPanel().getHeadItem("value").setEnabled(true);
			} else if (datatype == 2) {// ��ʽ
				e.getBillCardPanel().getHeadItem("vformula").setComponent(getUIButton());
				e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
			} else {// datetype==3 //��������Դ
				e.getBillCardPanel().getHeadItem("vformulastr").setEnabled(true);
			}
		}
	}

	private UIButton getUIButton() {
		if (button == null) {
			button = new UIButton();
			button.setName("button");
			button.setText("\u516C\u5F0F");
		}
		return button;
	}
}
