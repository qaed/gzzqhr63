package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hrwa.wa_ba_unit.ace.maintain.AceWaBaUnitDataManager;
import nc.ui.hrwa.wa_ba_unit.ace.view.WaBaUnitSearchPsnWizardStep;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.bm.data.BmDataVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * 向导公共类
 * 
 * @author tsheay
 */
public abstract class WaBaUnitWizardAction extends HrAction implements IWizardDialogListener {
	private static final long serialVersionUID = 1L;
	WizardModel wizardModel = null;

	IAppModelDataManager dataManager = null;

	public WaBaUnitWizardAction() {
	}

	public void doAction(ActionEvent e) throws Exception {
		WizardDialog wizardDialog = new WizardDialog(getModel().getContext().getEntranceUI(), getWizardModel(), getSteps(), null);

		wizardDialog.setWizardDialogListener(this);
		wizardDialog.setResizable(true);
		wizardDialog.setSize(new Dimension(800, 560));
		wizardDialog.showModal();
	}

	/**
	 * 步骤
	 * 
	 * @return
	 */
	protected List<WizardStep> getSteps() {
		List<WizardStep> list = new ArrayList();
		list.add(new WaBaUnitSearchPsnWizardStep(getBtnName(), getModel()));
		//		list.add(new WaBaUnitSelectPsnWizardStep(getLoginContext(), getBtnName(), (String) getValue("Code")));
		return list;
	}

	public WizardModel getWizardModel() {
		if (this.wizardModel == null) {
			this.wizardModel = new WizardModel();
			this.wizardModel.setSteps(getSteps());
		}
		return this.wizardModel;
	}

	public void wizardFinish(WizardEvent event) throws WizardActionException {
		BmDataVO[] psnVOs = new BmDataVO[] {};
		//		BmDataVO[] psn
		psnVOs = (BmDataVO[]) getWizardModel().getAttr("selectedPsn");
		/*
		if ((psnVOs != null) && (psnVOs.length > 0)) {
			for (int i = 0; i < psnVOs.length; i++) {
		//				psnVOs[i].setCyear(((LoginContext) getContext()).getCyear());
		//				psnVOs[i].setCperiod(((LoginContext) getContext()).getCperiod());
				psnVOs[i].setPaylocation(null);
			}
		}
		*/
		try {
			doProcess(psnVOs);
			((AceWaBaUnitDataManager) getDataManager()).refresh();
		} catch (BusinessException e) {
			WizardActionException ex = new WizardActionException(e);
			ex.addMsg(ResHelper.getString("60150bmfile", "060150bmfile0004"), e.getMessage());

			throw ex;
		}
	}

	public abstract void doProcess(BmDataVO[] paramArrayOfBmDataVO) throws BusinessException;

	public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {
	}

	public void wizardCancel(WizardEvent event) throws WizardActionException {
	}

	protected boolean isActionEnable() {
		return (super.isActionEnable()) && (StringUtils.isNotBlank(getLoginContext().getPk_org()));
		//		return super.isActionEnable();
	}

	public LoginContext getLoginContext() {
		return (LoginContext) getModel().getContext();
	}

	public IAppModelDataManager getDataManager() {
		return this.dataManager;
	}

	public void setDataManager(IAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}
}
