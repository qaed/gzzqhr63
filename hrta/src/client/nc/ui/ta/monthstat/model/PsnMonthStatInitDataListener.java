package nc.ui.ta.monthstat.model;

import nc.funcnode.ui.FuncletInitData;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.uif2.IFuncNodeInitDataListener;
import nc.ui.uif2.model.IAppModelDataManager;

import org.apache.commons.lang.StringUtils;

/**
 * 初始化监听
 */

@SuppressWarnings("restriction")
public class PsnMonthStatInitDataListener implements IFuncNodeInitDataListener {
	private PsnMonthStatPFAppModel model;
	private PsnMonthStatAppModel psnModel;
	private IAppModelDataManager dataManager;
	private PrimaryOrgPanel orgPanel;

	public PrimaryOrgPanel getOrgPanel() {
		return orgPanel;
	}

	public void setOrgPanel(PrimaryOrgPanel orgPanel) {
		this.orgPanel = orgPanel;
	}

	@Override
	public void initData(FuncletInitData data) {

		Object initData = getModel().getContext().getInitData();
		if (initData != null && initData instanceof FuncletInitData) {
			Object linkData = ((FuncletInitData) initData).getInitData();
			if (linkData instanceof PfLinkData) {
				PfLinkData pfdata = (PfLinkData) linkData;
				String pk_org = pfdata.getPkOrg();
				if (StringUtils.isNotBlank(pk_org)) {
					getOrgPanel().getRefPane().setEnabled(false);
					getOrgPanel().getRefPane().setPK(pk_org);
					getOrgPanel().getRefPane().setValueObjFireValueChangeEvent(pk_org);
					return;
				}
			}
		}
		getDataManager().initModel();
	}

	/**
	 * @return model
	 */
	public PsnMonthStatPFAppModel getModel() {
		return model;
	}

	/**
	 * @param model 要设置的 model
	 */
	public void setModel(PsnMonthStatPFAppModel model) {
		this.model = model;
	}

	/**
	 * @return psnModel
	 */
	public PsnMonthStatAppModel getPsnModel() {
		return psnModel;
	}

	/**
	 * @param psnModel 要设置的 psnModel
	 */
	public void setPsnModel(PsnMonthStatAppModel psnModel) {
		this.psnModel = psnModel;
	}

	/**
	 * @return dataManager
	 */
	public IAppModelDataManager getDataManager() {
		return dataManager;
	}

	/**
	 * @param dataManager 要设置的 dataManager
	 */
	public void setDataManager(IAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

}
