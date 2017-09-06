package nc.ui.hrwa.wa_ba_item.ace.aciton;

import java.awt.event.ActionEvent;

import nc.ui.hrwa.wa_ba_item.ace.view.WaBaItemBillFormEditor;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.vo.wa.wa_ba.item.ItemsVO;

public class WaBaItemEditAction extends nc.ui.pubapp.uif2app.actions.EditAction {

	private IEditor editor;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8609030080700718241L;

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		ItemsVO vo = (ItemsVO) this.getModel().getSelectedData();
		BillCardPanel panel = ((WaBaItemBillFormEditor) editor).getBillCardPanel();
		Integer datatype = vo.getDatatype();
		panel.getHeadItem("datatype").setEdit(true);
		panel.getHeadItem("value").setEnabled(false);
		panel.getHeadItem("vformula").setEnabled(false);
		panel.getHeadItem("vformulastr").setEnabled(false);
		if (datatype == 0) {// �ɹ�ʽ����
			panel.getHeadItem("vformula").setEnabled(true);
		} else if (datatype == 2) {// �ֹ�����
		} else if (datatype == 3) {// �̶�ֵ
			panel.getHeadItem("value").setEnabled(true);
		} else if (datatype == 5) {// ��������Դ
			panel.getHeadItem("vformula").setEnabled(true);
		}
	}

	@Override
	protected void beforeDoAction() {
		// TODO tsy ��֯״̬�²���ɾ�����ŵ�
		super.beforeDoAction();
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

}
