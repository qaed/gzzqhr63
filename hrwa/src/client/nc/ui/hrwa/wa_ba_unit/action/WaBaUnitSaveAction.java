package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.event.ActionEvent;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.actions.SaveAction;
import nc.ui.uif2.UIState;

@SuppressWarnings("restriction")
public class WaBaUnitSaveAction extends SaveAction {

	private static final long serialVersionUID = 1541529364997425080L;

	/* ���� Javadoc��
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		//		if (UIDialog.ID_YES == CommonConfirmDialogUtils.showConfirmSaveDialog(getModel().getContext().getEntranceUI())) {
		//		String QUESTION = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000001")/*�Ƿ�Ҫ�������޸ĵ����ݣ�*/;
		if (getModel().getUiState() != UIState.ADD) {
			String title = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000000")/*ȷ�ϱ���*/;
			String question = "ȷ������༭����?";
			if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), title, question, UIDialog.ID_NO)) {
				return;
			}
		}
		super.doAction(e);

	}

	/* ���� Javadoc��
	 * @see nc.ui.uif2.actions.SaveAction#validate(java.lang.Object)
	 */
	@Override
	protected void validate(Object value) {
		//		AggWaBaUnitHVO aggvo = (AggWaBaUnitHVO) ((BillForm) getEditor()).getModel().getSelectedData();
		//		if (aggvo.getChildrenVO()==null || aggvo.getChildrenVO().length < 1) {
		//			List<ValidationFailure> list = new ArrayList();
		//			list.add(new ValidationFailure("���岻��Ϊ��"));
		//			throw new BusinessExceptionAdapter(new ValidationException(list));
		//		}
		super.validate(value);
	}

}
