package nc.ui.hrwa.wa_ba_item.ace.view;

import java.util.List;

import nc.ui.hr.formula.HRFormulaEditorDialog;
import nc.ui.hr.formula.HRFormulaRefPane;
import nc.ui.hr.formula.itf.IVariableFactory;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.formula.dialog.FormulaFunctionPanel;
import nc.ui.pub.formula.dialog.FormulaRealEditorPanel;
import nc.ui.pub.formula.dialog.IFormulaTabBuilder;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.wa.formular.WaBznessFuncBuilder;
import nc.ui.wa.item.view.ItemFormularFactory;
import nc.vo.hr.func.HrFormula;

/**
 * @author: xuanlt
 * @date: 2010-4-1 上午10:19:59
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaBaItemFormulaRefPane extends HRFormulaRefPane {

	/**
	 * @author xuanlt on 2010-4-1
	 * @param model
	 * @param parentEditor
	 */
	public WaBaItemFormulaRefPane(AbstractUIAppModel model, HrBillFormEditor parentEditor) {
		super(parentEditor, model);

	}

	@Override
	protected String getConfigFileName() {
		return "/nc/ui/hrwa/wa_ba_item/ace/view/waitem_formulaedit.xml";

	}

	@Override
	protected IVariableFactory getWaVaribleFactory() {
		//必须实现该
		//20170915 tsy 改为新增的Factory
		//		ItemFormularFactory f =new  ItemFormularFactory();
		WabaItemFormularFactory f = new WabaItemFormularFactory();
		// 20170915 end 
		f.setModel(getModel());
		return f;

	}

	@Override
	protected void setParas(HRFormulaEditorDialog dlg) {

		super.setParas(dlg);
		FormulaFunctionPanel functionPanel = (FormulaFunctionPanel) dlg.getFormulaFunctionPanel();

		// 找到WaBznessFuncBuilder 设定 model
		List<IFormulaTabBuilder> builder = functionPanel.getCustomerTabBuilders();

		for (int index = 0; index < builder.size(); index++) {
			// xxx
			if (builder.get(index) instanceof WaBznessFuncBuilder) {
				((WaBznessFuncBuilder) builder.get(index)).setModel(getModel());
			}
		}

	}

	// 多语言问题、公式内容清空、公式中变量重新加载
	@Override
	public void onButtonClicked() {
		if (formula == null) {
			formula = new HrFormula();
			// 默认数据类型

			// return;
		}
		formula.setReturnType(((WaBaItemBillFormEditor) this.getParentEditor()).getTypeEnumValue().value());
		// 改变为设置 formular
		getFormulaDialog().setFormula(formula);

		if (getFormulaDialog().showModal() != UIDialog.ID_OK) {
			return;
		}

		// 得到 业务描述

		// HrFormulaRealEditorPanel ep = (HrFormulaRealEditorPanel)
		// getDlg().getFormulaRealEditorPanel() ;

		String businessDes = ((FormulaRealEditorPanel) getFormulaDialog().getFormulaRealEditorPanel()).getText();
		String scriptLang = ((FormulaRealEditorPanel) getFormulaDialog().getFormulaRealEditorPanel()).getConvertedText();// ((FormulaRealEditorPanel)getDlg().getFormulaRealEditorPanel()).getConvertedText();

		// 得到是否默认公式

		setText(businessDes);

		if (this.formula == null) {
			this.formula = new HrFormula();
		}

		// HrFormula f = new HrFormula();
		this.formula.setBusinessLang(businessDes);
		this.formula.setScirptLang(scriptLang);

		// this.formula = f;

		// 将业务描述翻译成 脚本语言

		// this.formularCode = getDlg().getFormulaDesc();
	}
}
