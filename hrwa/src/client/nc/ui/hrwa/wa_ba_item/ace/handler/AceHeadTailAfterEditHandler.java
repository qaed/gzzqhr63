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
		// 修改计算方式
		if ("datatype".equals(e.getKey())) {
			/* 获取计算方式
			 * key值	|	涵义
			 * 0	|	公式
			 * 6	|	薪资系统
			 * 2	|	手工输入  
			 * 3	|	固定值
			 * 5	|	其他来源 
			 */
			Integer datatype = (Integer) e.getBillCardPanel().getHeadItem("datatype").getValueObject();
			if (datatype != null) {
				e.getBillCardPanel().getHeadItem("value").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformula").setEnabled(false);
				e.getBillCardPanel().getHeadItem("vformulastr").setEnabled(false);
				if (datatype == FromEnumVO.FORMULA.value()) {
					// 由公式计算
					e.getBillCardPanel().getHeadItem("vformula").setEnabled(true);
				} else if (datatype == FromEnumVO.USER_INPUT.value()) {
					// 手工输入
				} else if (datatype == FromEnumVO.FIX_VALUE.value()) {
					// 固定值
					e.getBillCardPanel().getHeadItem("value").setEnabled(true);
				} else if (datatype == FromEnumVO.WAORTHER.value() || datatype == FromEnumVO.OTHER_SYSTEM.value() || datatype == FromEnumVO.WA_WAGEFORM.value()) {
					// 其他数据源的薪资 或 其他数据源 或 薪资系统
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
