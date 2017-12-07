package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

@SuppressWarnings("restriction")
public class WaBaSchUnCommitAction extends UnCommitScriptAction {
	private static final long serialVersionUID = 6881970856678985173L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object value = getModel().getSelectedData();

		super.doAction(e);
		AggregatedValueObject vo =
				HYPubBO_Client.queryBillVOByPrimaryKey(new String[] { AggWaBaSchHVO.class.getName(), WaBaSchHVO.class.getName(), WaBaSchBVO.class.getName() }, ((AggWaBaSchHVO) value).getParentVO().getPk_ba_sch_h());
		this.getModel().directlyUpdate(vo);

	}

}
