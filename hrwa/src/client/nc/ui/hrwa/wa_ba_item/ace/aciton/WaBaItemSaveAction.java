package nc.ui.hrwa.wa_ba_item.ace.aciton;

import java.awt.event.ActionEvent;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.ui.pubapp.uif2app.actions.SaveAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.wa.wa_ba.item.WaBaItemDataType;

@SuppressWarnings("restriction")
public class WaBaItemSaveAction extends SaveAction {
	private static final long serialVersionUID = 8867373424228905577L;

	/* ���� Javadoc��
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		ItemsVO itemsvo = (ItemsVO) getEditor().getValue();
		if (itemsvo.getIitemtype() == WaBaItemDataType.FORMULA.getValue() && (itemsvo.getVformula() == null || "".equals(itemsvo.getVformula().trim()))) {
			throw new BusinessException("��ʽУ��ʧ�ܣ�");
		}

		super.doAction(e);
	}

	/* ���� Javadoc��
	 * @see nc.ui.uif2.actions.SaveAction#validate(java.lang.Object)
	 */
	@Override
	protected void validate(Object value) {
		try {
			// ���зǿ�У��
			((BillForm) this.getEditor()).getBillCardPanel().dataNotNullValidate();
		} catch (ValidationException e) {
			throw new BusinessExceptionAdapter(e);
		}
		//		super.validate(value);
	}

}
