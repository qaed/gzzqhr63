package nc.ui.hrwa.wa_ba_item.ace.aciton;

import java.awt.event.ActionEvent;

import nc.ui.hrwa.wa_ba_item.ace.view.WaBaItemBillFormEditor;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.editor.IEditor;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.wa.wa_ba.item.WaBaItemDataType;

@SuppressWarnings("restriction")
public class WaBaItemEditAction extends nc.ui.pubapp.uif2app.actions.EditAction {

	private IEditor editor;
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
		if (datatype == FromEnumVO.FORMULA.value()) {
			// 由公式计算
			panel.getHeadItem("vformula").setEnabled(true);
		} else if (datatype == FromEnumVO.USER_INPUT.value()) {
			// 手工输入
		} else if (datatype == FromEnumVO.FIX_VALUE.value()) {
			// 固定值
			panel.getHeadItem("value").setEnabled(true);
		} else if (datatype == FromEnumVO.WAORTHER.value() || datatype == FromEnumVO.OTHER_SYSTEM.value() || datatype == FromEnumVO.WA_WAGEFORM.value()) {
			// 其他数据源的薪资 或 其他数据源 或 薪资规则表
			panel.getHeadItem("vformula").setEnabled(true);
		}
	}

	@Override
	protected void beforeDoAction() {
		super.beforeDoAction();
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

}
