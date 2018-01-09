package nc.ui.rm.hire.action;

import java.awt.event.ActionEvent;

import nc.ui.hr.uif2.action.AddLineAction;
import nc.ui.rm.pub.view.HireBodyEditorDialog;
import nc.ui.rm.pub.view.HireItemCardForm;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.rm.hire.HireItemVO;

@SuppressWarnings("restriction")
public class HireAddLineAction extends AddLineAction {
	private HireItemCardForm billForm;

	public HireAddLineAction() {
		putValue("SmallIcon", ThemeResourceCenter.getInstance().getImage("themeres/ui/toolbaricons/card-edit.png"));
	}

	protected boolean isActionEnable() {
		return (getModel().getUiState() == nc.ui.uif2.UIState.ADD) || (super.isActionEnable());
	}

	public void doAction(ActionEvent evt) throws Exception {
		HireBodyEditorDialog dlg = getDialog();
		dlg.getBillForm().getBillCardPanel().getBillData().addNew();
		HireItemVO newvo = (HireItemVO) dlg.getBillForm().getValue();
		newvo.setIshire(UFBoolean.TRUE);
		newvo.setHire_date(nc.hr.utils.PubEnv.getServerLiteralDate());
		newvo.setPk_hire_org(getModel().getContext().getPk_org());
		// 20171228 tsy 不自动勾选「是否试用」
		//		newvo.setIsprobationary(UFBoolean.TRUE);
		newvo.setPk_plan_reg_org(getModel().getContext().getPk_org());
		newvo.setSourcetype(Integer.valueOf(0));

		newvo.setProbationary_period_unit(Integer.valueOf(2));
		newvo.setPk_group(getModel().getContext().getPk_group());
		newvo.setPk_org(getModel().getContext().getPk_org());
		dlg.setValue(newvo);
		dlg.getBillForm().setEditable(true);
		if (1 != dlg.showModal()) {
			putValue("message_after_action", nc.ui.uif2.IShowMsgConstant.getCancelInfo());
			dlg.dispose();
			dlg.initUI();
			return;
		}
		HireItemVO itemvo = (HireItemVO) dlg.getValue();
		if (itemvo == null)
			return;
		getCardPanel().getBillCardPanel().addLine();
		int lastIndex = getCardPanel().getBillCardPanel().getBillModel().getRowCount() - 1;
		getCardPanel().getBillCardPanel().getBillModel().setBodyRowObjectByMetaData(itemvo, lastIndex);
		dlg.dispose();
		dlg.initUI();
	}

	public HireBodyEditorDialog getDialog() {
		HireBodyEditorDialog itemDialog = new HireBodyEditorDialog(getEntranceUI());
		itemDialog.setBillForm(getBillForm());
		itemDialog.setValidator((nc.bs.uif2.validation.Validator) getValidator().get(0));
		itemDialog.initUI();
		return itemDialog;
	}

	public HireItemCardForm getBillForm() {
		return this.billForm;
	}

	public void setBillForm(HireItemCardForm billForm) {
		this.billForm = billForm;
	}
}