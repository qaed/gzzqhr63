package nc.bs.pub.action;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchApproveBP;
import nc.bs.hrwa.wa_ba_sch.ace.rule.GenWaBonusRule;
import nc.bs.hrwa.wa_ba_sch.plugin.bpplugin.WaBaSchPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.VOStatus;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.NCLocator;

public class N_BAAL_APPROVE extends AbstractPfAction<AggWaBaSchHVO> {
	public N_BAAL_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggWaBaSchHVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggWaBaSchHVO> processor = new CompareAroundProcesser<AggWaBaSchHVO>(WaBaSchPluginPoint.APPROVE);
		IRule<AggWaBaSchHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.ApproveStatusCheckRule();
		processor.addBeforeRule(rule);
		//审批后生成奖金汇总表
		processor.addAfterRule(new GenWaBonusRule());
		return processor;
	}

	@Override
	protected AggWaBaSchHVO[] processBP(Object userObj, AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) {
		//只有分配完成的单据才可以进行审批
		for (AggWaBaSchHVO aggWaBaSchHVO : originBills) {
			WaBaSchBVO[] bvos = (WaBaSchBVO[]) aggWaBaSchHVO.getChildren(WaBaSchBVO.class);
			for (WaBaSchBVO bvo : bvos) {
				if (bvo.getVdef1() != null && !"".equals(bvo.getVdef1())) {
					ExceptionUtils.wrappBusinessException("该单据尚未分配完成，请稍后重试");
				}

			}
		}
		AggWaBaSchHVO[] bills = null;
		nc.itf.hrwa.IWaBaSchMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaSchMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
