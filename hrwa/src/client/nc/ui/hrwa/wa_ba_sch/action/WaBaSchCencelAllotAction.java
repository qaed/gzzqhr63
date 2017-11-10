package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IWaBaSchMaintain;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.pubapp.uif2app.components.grand.ListGrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandModel;
import nc.ui.pubapp.uif2app.event.card.BodyRowEditType;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.editor.BillForm;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchTVO;

/**
 * 取消分配
 * 
 * @author tsheay
 */
@SuppressWarnings("restriction")
public class WaBaSchCencelAllotAction extends HrAction {
	private static final long serialVersionUID = 1L;
	private MainGrandModel mainGrandModel;
	private BillForm billform;
	private ListGrandPanelComposite listView;
	/**
	 * <p>
	 * 当前选中行
	 * </p>
	 * 从第0行开始 -1：为未选中
	 */
	private static int row = -1;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		String title = NCLangRes.getInstance().getStrByID("uif2", "CommonConfirmDialogUtils-000000")/*确认保存*/;
		String question = "确定删除所选的奖金分配单元?";
		if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), title, question, UIDialog.ID_NO)) {
			return;
		}
		if (getRow() > -1) {//有选择行
			//			CircularlyAccessibleValueObject[] vos = ((AggWaBaSchHVO) this.getModel().getSelectedData()).getChildrenVO();
			WaBaSchBVO bvo =
					(WaBaSchBVO) this.getBillform().getBillCardPanel().getBillModel().getBodyValueRowVO(getRow(), WaBaSchBVO.class.getName());
			//数据库删除
			//			bvo.setDr(1);
			//			bvo.setVdef1(AppContext.getInstance().getPkUser());
			HYPubBO_Client.delete(bvo);//直接删除，不保留数据
			HYPubBO_Client.deleteByWhereClause(WaBaSchTVO.class, "pk_ba_sch_unit='" + bvo.getPk_ba_sch_unit() + "' and isnull(dr,0)=0");
			//界面删除
			this.getBillform().getBillCardPanel().doLineAction(this.getBillform().getBillCardPanel().getCurrentBodyTableCode(), BodyRowEditType.DELLINE.getType());
			//				this.getModel().directlyUpdate(this.getModel().getSelectedData());
			//				this.getModel().directlyDelete(bvo.getPk_s());
			//				this.getMainGrandModel().directlyDelete(bvo);
			//				this.getMainGrandModel().directlyDelete(bvo.getPk_s());
			//删除代办
			IWaBaSchMaintain maintain = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);
			AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getMainGrandModel().getSelectedData();
			aggvo.setChildrenVO(new WaBaSchBVO[] { bvo });
			maintain.deleteWorkitem(aggvo);
			this.showMsgInfo("「执行取消分配」成功");
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

	public static int getRow() {
		return row;
	}

	public static void setRow(int row) {
		WaBaSchCencelAllotAction.row = row;
	}

	@Override
	protected boolean isActionEnable() {
		AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
		if (aggvo == null || aggvo.getParentVO().getApprovestatus() != ApproveStatus.COMMIT) {
			return false;
		}
		if (getRow() < 0) {
			return false;
		}
		return super.isActionEnable();
	}
}
