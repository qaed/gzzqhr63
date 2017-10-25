package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.message.vo.MessageVO;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.pubapp.uif2app.components.grand.ListGrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandModel;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.editor.BillForm;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

/**
 * 强制完成分配
 * 
 * @author tsheay
 */
@SuppressWarnings("restriction")
public class WaBaSchForceCompletedAllotAction extends NCAction {
	private static final long serialVersionUID = -8596488248404836181L;
	private MainGrandModel mainGrandModel;
	private BillManageModel model;
	private BillForm billform;
	private ListGrandPanelComposite listView;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (getModel().getSelectedData() != null) {
			AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
			WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggvo.getChildren(WaBaSchBVO.class);
			for (WaBaSchBVO bvo : bvos) {
				if (bvo.getClass3() == null) {
					bvo.setClass3(bvo.getPlan_totalmoney());//当期可分配金额
				}
				bvo.setVdef1(null);
				bvo.setDr(0);//必须重新写，否则会被置为null
			}
			HYPubBO_Client.updateAry(bvos);
			//			this.getModel().directlyUpdate(aggvo);
			//			this.getModel().update(aggvo);
			this.getMainGrandModel().directlyUpdate(aggvo);
			HYPubBO_Client.deleteByWhereClause(MessageVO.class, "detail like '%" + aggvo.getParentVO().getPk_ba_sch_h() + "@" + aggvo.getParentVO().getBilltype() + "%'");

			this.showMsgInfo("「强制分配」完成");
		}
	}

	protected void showMsgInfo(String msg) {
		ShowStatusBarMsgUtil.showStatusBarMsg(msg, getModel().getContext());
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

	public BillForm getBillform() {
		return billform;
	}

	public void setBillform(BillForm billform) {
		this.billform = billform;
	}

	public ListGrandPanelComposite getListView() {
		return listView;
	}

	public void setListView(ListGrandPanelComposite listView) {
		this.listView = listView;
	}

	/* （非 Javadoc）
	 * @see javax.swing.AbstractAction#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		//		AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
		//		if (aggvo.getParentVO().getApprovestatus() == ApproveStatus.COMMIT) {
		return super.isEnabled();
		//		}
		//		return false;

	}

	/* （非 Javadoc）
	 * @see nc.ui.uif2.NCAction#isActionEnable()
	 */
	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
	}

}
