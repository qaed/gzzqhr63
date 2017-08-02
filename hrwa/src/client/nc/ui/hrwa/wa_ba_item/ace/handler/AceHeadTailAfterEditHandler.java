package nc.ui.hrwa.wa_ba_item.ace.handler;

import nc.ui.pub.beans.UIButton;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;

public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent> {
	private UIButton button;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		// �޸ļ��㷽ʽ
		if ("datatype".equals(e.getKey())) {
			/* ��ȡ���㷽ʽ
			 * keyֵ	|	����
			 * 0	|	��ʽ
			 * 2	|	�ֹ�����  
			 * 3	|	�̶�ֵ
			 * 5	|	������Դ 
			 */
			Integer datatype = (Integer) e.getBillCardPanel().getHeadItem("datatype").getValueObject();
			if (datatype != null) {
				e.getBillCardPanel().getHeadItem("value").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformula").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformulastr").setEnabled(false);
				if (datatype == 0) {// �ɹ�ʽ����
					e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
				} else if (datatype == 2) {// �ֹ�����
				} else if (datatype == 3) {// �̶�ֵ
					e.getBillCardPanel().getHeadItem("value").setEnabled(true);
				} else if (datatype == 5) {// ��������Դ
					e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
				}
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
