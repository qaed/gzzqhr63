package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandModel;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.components.CommonConfirmDialogUtils;

public class WaBaSchDeleteAction extends DeleteScriptAction {
	private static final long serialVersionUID = 1L;
	private String billCodeName;
	private boolean powercheck;
	private MainGrandModel mainGrandModel;
	private ISingleBillService<Object> singleBillService;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (UIDialog.ID_YES == CommonConfirmDialogUtils.showConfirmDeleteDialog(getModel().getContext().getEntranceUI())) {
			Object value = this.getMainGrandModel().getDeleteAggVO();
			//		Object value = this.getModel().getSelectedData();
			//		this.getModel().directlyDelete(value);
			this.getModel().delete();
			Object object = this.getSingleBillService().operateBill(value);
			this.getMainGrandModel().directlyDelete(null);
			this.showSuccessInfo();
		}

	}

	public String getBillCodeName() {
		return this.billCodeName;
	}

	public void setBillCodeName(String billCodeName) {
		this.billCodeName = billCodeName;
	}

	public boolean isPowercheck() {
		return this.powercheck;
	}

	public void setPowercheck(boolean powercheck) {
		this.powercheck = powercheck;
	}

	public WaBaSchDeleteAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.DELETE);
	}

	protected void showSuccessInfo() {
		ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getDelSuccessInfo(), getModel().getContext());
	}

	public ISingleBillService<Object> getSingleBillService() {
		return singleBillService;
	}

	public void setSingleBillService(ISingleBillService<Object> singleBillService) {
		this.singleBillService = singleBillService;
	}

	public MainGrandModel getMainGrandModel() {
		return mainGrandModel;
	}

	public void setMainGrandModel(MainGrandModel mainGrandModel) {
		this.mainGrandModel = mainGrandModel;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}
}
