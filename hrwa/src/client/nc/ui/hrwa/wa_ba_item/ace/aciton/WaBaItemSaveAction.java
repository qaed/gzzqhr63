package nc.ui.hrwa.wa_ba_item.ace.aciton;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.SaveAction;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.wa.wa_ba.item.WaBaItemDataType;

public class WaBaItemSaveAction extends SaveAction {

	/* （非 Javadoc）
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		ItemsVO itemsvo = (ItemsVO) getEditor().getValue();
		if (itemsvo.getIitemtype() == WaBaItemDataType.FORMULA.getValue() && (itemsvo.getVformula() == null || "".equals(itemsvo.getVformula().trim()))) {
			throw new BusinessException("公式校验失败！");
		} 
		super.doAction(e);
	}

}
