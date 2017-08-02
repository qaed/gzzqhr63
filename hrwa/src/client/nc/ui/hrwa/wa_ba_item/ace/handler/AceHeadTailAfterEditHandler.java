package nc.ui.hrwa.wa_ba_item.ace.handler;

import nc.ui.pub.beans.UIButton;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;

public class AceHeadTailAfterEditHandler implements IAppEventHandler<CardHeadTailAfterEditEvent> {
	private UIButton button;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		// 修改计算方式
		if ("datatype".equals(e.getKey())) {
			/* 获取计算方式
			 * key值	|	涵义
			 * 0	|	公式
			 * 2	|	手工输入  
			 * 3	|	固定值
			 * 5	|	其他来源 
			 */
			Integer datatype = (Integer) e.getBillCardPanel().getHeadItem("datatype").getValueObject();
			if (datatype != null) {
				e.getBillCardPanel().getHeadItem("value").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformula").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformulastr").setEnabled(false);
				if (datatype == 0) {// 由公式计算
					e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
				} else if (datatype == 2) {// 手工输入
				} else if (datatype == 3) {// 固定值
					e.getBillCardPanel().getHeadItem("value").setEnabled(true);
				} else if (datatype == 5) {// 其他数据源
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
