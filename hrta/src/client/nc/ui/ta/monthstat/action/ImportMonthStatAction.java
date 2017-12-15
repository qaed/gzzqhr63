package nc.ui.ta.monthstat.action;

import java.awt.event.ActionEvent;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.view.MonthStatImportDialog;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.ta.monthstat.MonthStatVO;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class ImportMonthStatAction extends HrAction {
	private static final long serialVersionUID = 6606620748654417436L;
	private MonthStatImportDialog dialog;

	public MonthStatImportDialog getDialog() {
		if (this.dialog == null) {
			this.dialog = new MonthStatImportDialog(getEntranceUI());
			this.dialog.setModel(getModel());
			this.dialog.initUI();
		}

		return this.dialog;
	}

	public ImportMonthStatAction() {
		ActionInitializer.initializeAction(this, "ImportData");
	}

	public void doAction(ActionEvent e) throws Exception {
		if (1 != getDialog().showModal()) {
			putValue("message_after_action", IShowMsgConstant.getCancelInfo());
			return;
		}
		putValue("message_after_action", ResHelper.getString("6001uif2", "06001uif20010", new String[] { getBtnName() }));
	}

	protected boolean isActionEnable() {
		PsnMonthStatAppModel model = (PsnMonthStatAppModel) getModel();
		MonthStatVO[] vos = model.getData();
		if (vos == null || vos.length == 0) {
			return true;
		}
		for (MonthStatVO monthStatVO : vos) {
			if (monthStatVO.getApprovestatus() != ApproveStatus.FREE) {
				return false;
			}
		}
		return !StringUtils.isEmpty(getModel().getContext().getPk_org());
	}
}
