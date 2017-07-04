package nc.ui.hrwa.wa_ba_item.ace.handler;

import nc.ui.pub.beans.UIButton;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;

public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent> {
	private UIButton button;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		// HYPubBO_Client bo = new HYPubBO_Client();
		// 表体默认值
		// 获取计算方式 1-手工输入 2公式 3其他来源
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
			if (datatype == 1) {// 手工输入
				e.getBillCardPanel().getHeadItem("value").setEnabled(true);
			} else if (datatype == 2) {// 公式
				e.getBillCardPanel().getHeadItem("vformula").setComponent(getUIButton());
				e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
			} else {// datetype==3 //其他数据源
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
