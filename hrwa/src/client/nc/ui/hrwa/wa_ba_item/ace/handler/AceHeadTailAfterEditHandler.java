package nc.ui.hrwa.wa_ba_item.ace.handler;

import nc.ui.pub.beans.UIButton;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.vo.wa.item.FromEnumVO;

@SuppressWarnings("restriction")
public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent> {
	private UIButton button;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		// �޸ļ��㷽ʽ
		if ("datatype".equals(e.getKey())) {
			/* ��ȡ���㷽ʽ
			 * keyֵ	|	����
			 * 0	|	��ʽ
			 * 6	|	н��ϵͳ
			 * 2	|	�ֹ�����  
			 * 3	|	�̶�ֵ
			 * 5	|	������Դ 
			 */
			Integer datatype = (Integer) e.getBillCardPanel().getHeadItem("datatype").getValueObject();
			if (datatype != null) {
				e.getBillCardPanel().getHeadItem("value").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformula").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformulastr").setEnabled(false);
				if (datatype == FromEnumVO.FORMULA.value()) {
					// �ɹ�ʽ����
					e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
				} else if (datatype == FromEnumVO.USER_INPUT.value()) {
					// �ֹ�����
				} else if (datatype == FromEnumVO.FIX_VALUE.value()) {
					// �̶�ֵ
					e.getBillCardPanel().getHeadItem("value").setEnabled(true);
				} else if (datatype == FromEnumVO.WAORTHER.value() || datatype == FromEnumVO.OTHER_SYSTEM.value() || datatype == FromEnumVO.WA_WAGEFORM.value()) {
					// ��������Դ��н�� �� ��������Դ �� н��ϵͳ
					e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private UIButton getUIButton() {
		if (button == null) {
			button = new UIButton();
			button.setName("button");
			button.setText("\u516C\u5F0F");
		}
		return button;
	}
}
