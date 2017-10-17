package nc.ui.hrwa.wa_ba_unit.action;

import nc.ui.pubapp.uif2app.actions.DeleteAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.actions.SaveAction;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;

public class WaBaUnitDeleteAction extends DeleteAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1426720024806478079L;

	/* （非 Javadoc）
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		String title = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000000")/*确认保存*/;
		String question = "确定删除所选的奖金分配单元?";
		if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), title, question, UIDialog.ID_NO)) {
			return;
		}
		super.doAction(e);

	}

	/* （非 Javadoc）
	 * @see nc.ui.pubapp.uif2app.actions.DeleteAction#beforeStartDoAction(java.awt.event.ActionEvent)
	 */
	@Override
	public boolean beforeStartDoAction(ActionEvent actionEvent) throws Exception {

		return true;
	}

}
