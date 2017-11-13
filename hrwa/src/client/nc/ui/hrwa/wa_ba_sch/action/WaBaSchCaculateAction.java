package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IWaBaSchMaintain;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandModel;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

@SuppressWarnings("restriction")
public class WaBaSchCaculateAction extends HrAction {
	private AggWaBaSchHVO aggvo = null;
	//	private String year;
	//	private String period;
	//	private Vector<WaBaSchBVO> v = null;
	private IModelDataManager dataManager = null;
	private static final long serialVersionUID = 1292796453304461013L;
	private MainGrandModel mainGrandModel;

	public WaBaSchCaculateAction() {
	}

	public WaBaSchCaculateAction(AggWaBaSchHVO aggvo) {
		this.aggvo = aggvo;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		//有2个操作会调用：
		//1:保存时,传入aggvo，此时getMainGrandModel()为null
		//2:点击计算时，aggvo需要改为当前选择的aggvo
		if (getMainGrandModel() != null && getMainGrandModel().getSelectedData() != null) {

			this.aggvo = (AggWaBaSchHVO) getMainGrandModel().getSelectedData();
		}
		// NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
		IWaBaSchMaintain maintain = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);
		maintain.doCaculate(new AggWaBaSchHVO[] { aggvo });
		IBillQueryService billQuery = (IBillQueryService) NCLocator.getInstance().lookup(IBillQueryService.class);

		AggWaBaSchHVO newVO = billQuery.querySingleBillByPk(this.aggvo.getClass(), this.aggvo.getParentVO().getPk_ba_sch_h());

		if (getModel() != null) {
			getMainGrandModel().directlyUpdate(newVO);
		}

	}

	@Override
	protected boolean isActionEnable() {
		AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
		if (aggvo == null) {
			return false;
		}
		try {
			WaBaSchHVO hvo = (WaBaSchHVO) HYPubBO_Client.queryByPrimaryKey(WaBaSchHVO.class, aggvo.getParentVO().getPk_ba_sch_h());
			if (!hvo.getApprovestatus().equals(BillStatusEnum.FREE.value())) {
				return false;
			}
		} catch (UifException e) {
			return false;
		}
		return super.isActionEnable();
	}

	/**
	 * @return dataManager
	 */
	public IModelDataManager getDataManager() {
		return dataManager;
	}

	/**
	 * @param dataManager 要设置的 dataManager
	 */
	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	/**
	 * @return mainGrandModel
	 */
	public MainGrandModel getMainGrandModel() {
		return mainGrandModel;
	}

	/**
	 * @param mainGrandModel 要设置的 mainGrandModel
	 */
	public void setMainGrandModel(MainGrandModel mainGrandModel) {
		this.mainGrandModel = mainGrandModel;
	}

}
