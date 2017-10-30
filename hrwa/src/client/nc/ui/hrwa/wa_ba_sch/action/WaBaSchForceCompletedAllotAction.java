package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.message.vo.MessageVO;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.pubapp.uif2app.components.grand.ListGrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandModel;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.editor.BillForm;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

/**
 * ǿ����ɷ���
 * 
 * @author tsheay
 */
@SuppressWarnings("restriction")
public class WaBaSchForceCompletedAllotAction extends HrAction {
	private static final long serialVersionUID = -8596488248404836181L;
	private MainGrandModel mainGrandModel;
	private BillForm billform;
	private ListGrandPanelComposite listView;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		String title = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000000")/*ȷ�ϱ���*/;
		String question = "ȷ����ֹ���з��䵥Ԫ�ķ������?";
		if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), title, question, UIDialog.ID_NO)) {
			return;
		}
		if (getModel().getSelectedData() != null) {
			AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
			WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggvo.getChildren(WaBaSchBVO.class);
			for (WaBaSchBVO bvo : bvos) {
				if (bvo.getClass3() == null) {
					bvo.setClass3(bvo.getClass2());//���ڼƻ�������
					bvo.setClass4(bvo.getClass1());//���µĽ���ת�����½���
				}
				bvo.setVdef1(null);
				bvo.setDr(0);//��������д������ᱻ��Ϊnull
			}
			HYPubBO_Client.updateAry(bvos);
			//			this.getModel().directlyUpdate(aggvo);
			//			this.getModel().update(aggvo);
			this.getMainGrandModel().directlyUpdate(aggvo);
			HYPubBO_Client.deleteByWhereClause(MessageVO.class, "detail like '%" + aggvo.getParentVO().getPk_ba_sch_h() + "@" + aggvo.getParentVO().getBilltype() + "%'");

			this.showMsgInfo("��ǿ�Ʒ��䡹���");
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

	/* ���� Javadoc��
	 * @see javax.swing.AbstractAction#isEnabled()
	 */
	@Override
	protected boolean isActionEnable() {
		AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
		if (aggvo == null || aggvo.getParentVO().getApprovestatus() != ApproveStatus.COMMIT) {
			return false;
		}
		return super.isActionEnable();
	}

}
