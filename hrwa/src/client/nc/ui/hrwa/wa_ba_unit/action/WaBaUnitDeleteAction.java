package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.event.ActionEvent;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.actions.DeleteAction;

@SuppressWarnings("restriction")
public class WaBaUnitDeleteAction extends DeleteAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1426720024806478079L;

	/* ���� Javadoc��
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		String title = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000000")/*ȷ�ϱ���*/;
		String question = "ȷ��ɾ����ѡ�Ľ�����䵥Ԫ?";
		if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), title, question, UIDialog.ID_NO)) {
			return;
		}
		super.doAction(e);

	}

	/* ���� Javadoc��
	 * @see nc.ui.pubapp.uif2app.actions.DeleteAction#beforeStartDoAction(java.awt.event.ActionEvent)
	 */
	@Override
	public boolean beforeStartDoAction(ActionEvent actionEvent) throws Exception {

		return true;
	}

}
