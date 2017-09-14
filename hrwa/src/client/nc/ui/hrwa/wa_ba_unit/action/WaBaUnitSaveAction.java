package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.event.ActionEvent;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.actions.SaveAction;
import nc.ui.uif2.components.CommonConfirmDialogUtils;

public class WaBaUnitSaveAction extends SaveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1541529364997425080L;

	/* ���� Javadoc��
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		//		if (UIDialog.ID_YES == CommonConfirmDialogUtils.showConfirmSaveDialog(getModel().getContext().getEntranceUI())) {
		String title = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000000")/*ȷ�ϱ���*/;
		//		String QUESTION = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000001")/*�Ƿ�Ҫ�������޸ĵ����ݣ�*/;
		String question = "ȷ������༭����?";
		if (UIDialog.ID_YES == MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), title, question, UIDialog.ID_NO)) {
			super.doAction(e);
		}
	}

}
