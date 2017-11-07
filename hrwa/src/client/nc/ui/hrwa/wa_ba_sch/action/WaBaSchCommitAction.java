package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.pub.BusinessException;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class WaBaSchCommitAction extends CommitScriptAction {
	private static final long serialVersionUID = 6881970856678985173L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggWaBaSchHVO aggvo = (AggWaBaSchHVO) getModel().getSelectedData();
		if (aggvo == null || aggvo.getChildrenVO().length == 0) {
			throw new BusinessException("数据异常，请刷新后重试");
		}
		WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggvo.getChildren(WaBaSchBVO.class);
		for (WaBaSchBVO bvo : bvos) {
			WaBaUnitHVO unithvo = (WaBaUnitHVO) HYPubBO_Client.queryByPrimaryKey(WaBaUnitHVO.class, bvo.getBa_unit_code());
			if (StringUtils.isEmpty(unithvo.getBa_mng_psnpk())) {
				throw new BusinessException("分配人为空，请到「分配单元」维护分配人");
			}
		}
		super.doAction(e);
	}

}
