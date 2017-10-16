package nc.ui.hrwa.wa_ba_unit.action;

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

public class WaBaUnitSaveAction extends SaveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1541529364997425080L;

	/* （非 Javadoc）
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		//		if (UIDialog.ID_YES == CommonConfirmDialogUtils.showConfirmSaveDialog(getModel().getContext().getEntranceUI())) {
		//		String QUESTION = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000001")/*是否要保存已修改的数据？*/;
		if (getModel().getUiState() != UIState.ADD) {
			String title = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000000")/*确认保存*/;
			String question = "确定保存编辑数据?";
			if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), title, question, UIDialog.ID_NO)) {
				return;
			}
		}
		super.doAction(e);

	}

	/* （非 Javadoc）
	 * @see nc.ui.uif2.actions.SaveAction#validate(java.lang.Object)
	 */
	@Override
	protected void validate(Object value) {
//		AggWaBaUnitHVO aggvo = (AggWaBaUnitHVO) ((BillForm) getEditor()).getModel().getSelectedData();
//		if (aggvo.getChildrenVO()==null || aggvo.getChildrenVO().length < 1) {
//			List<ValidationFailure> list = new ArrayList();
//			list.add(new ValidationFailure("表体不能为空"));
//			throw new BusinessExceptionAdapter(new ValidationException(list));
//		}
		super.validate(value);
	}

}
