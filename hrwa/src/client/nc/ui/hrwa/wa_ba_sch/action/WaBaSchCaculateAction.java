package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IWaBaSchMaintain;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.model.BillManageModel;
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

	public WaBaSchCaculateAction() {
	}

	public WaBaSchCaculateAction(AggWaBaSchHVO aggvo) {
		this.aggvo = aggvo;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (this.aggvo == null) {
			this.aggvo = ((AggWaBaSchHVO) getModel().getSelectedData());
		}
		// NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
		IWaBaSchMaintain maintain = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);
		maintain.doCaculate(new AggWaBaSchHVO[] { aggvo });
		IBillQueryService billQuery = (IBillQueryService) NCLocator.getInstance().lookup(IBillQueryService.class);

		AggWaBaSchHVO newVO = billQuery.querySingleBillByPk(this.aggvo.getClass(), this.aggvo.getParentVO().getPk_ba_sch_h());
		if (getModel()!=null) {
			((BillManageModel) getModel()).directlyUpdate(newVO);
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
	 * @param dataManager ÒªÉèÖÃµÄ dataManager
	 */
	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

}
