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
 * @date: 2010-4-1 ����10:19:59
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
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
		//����ʵ�ָ�
		//20170915 tsy ��Ϊ������Factory
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

		// �ҵ�WaBznessFuncBuilder �趨 model
		List<IFormulaTabBuilder> builder = functionPanel.getCustomerTabBuilders();

		for (int index = 0; index < builder.size(); index++) {
			// xxx
			if (builder.get(index) instanceof WaBznessFuncBuilder) {
				((WaBznessFuncBuilder) builder.get(index)).setModel(getModel());
			}
		}

	}

	// ���������⡢��ʽ������ա���ʽ�б������¼���
	@Override
	public void onButtonClicked() {
		if (formula == null) {
			formula = new HrFormula();
			// Ĭ����������

			// return;
		}
		formula.setReturnType(((WaBaItemBillFormEditor) this.getParentEditor()).getTypeEnumValue().value());
		// �ı�Ϊ���� formular
		getFormulaDialog().setFormula(formula);

		if (getFormulaDialog().showModal() != UIDialog.ID_OK) {
			return;
		}

		// �õ� ҵ������

		// HrFormulaRealEditorPanel ep = (HrFormulaRealEditorPanel)
		// getDlg().getFormulaRealEditorPanel() ;

		String businessDes = ((FormulaRealEditorPanel) getFormulaDialog().getFormulaRealEditorPanel()).getText();
		String scriptLang = ((FormulaRealEditorPanel) getFormulaDialog().getFormulaRealEditorPanel()).getConvertedText();// ((FormulaRealEditorPanel)getDlg().getFormulaRealEditorPanel()).getConvertedText();

		// �õ��Ƿ�Ĭ�Ϲ�ʽ

		setText(businessDes);

		if (this.formula == null) {
			this.formula = new HrFormula();
		}

		// HrFormula f = new HrFormula();
		this.formula.setBusinessLang(businessDes);
		this.formula.setScirptLang(scriptLang);

		// this.formula = f;

		// ��ҵ����������� �ű�����

		// this.formularCode = getDlg().getFormulaDesc();
	}
}
