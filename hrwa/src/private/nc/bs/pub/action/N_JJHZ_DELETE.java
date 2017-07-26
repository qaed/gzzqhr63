package nc.bs.pub.action;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusDeleteBP;
import nc.bs.hrwa.wa_bonus.plugin.bpplugin.WaBonusPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.NCLocator;

public class N_JJHZ_DELETE extends AbstractPfAction<AggWaBaBonusHVO> {
	@Override
	protected CompareAroundProcesser<AggWaBaBonusHVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggWaBaBonusHVO> processor = new CompareAroundProcesser<AggWaBaBonusHVO>(WaBonusPluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		IRule<AggWaBaBonusHVO> rule = null;
		return processor;
	}

	@Override
	protected AggWaBaBonusHVO[] processBP(Object userObj, AggWaBaBonusHVO[] clientFullVOs, AggWaBaBonusHVO[] originBills) {
		nc.itf.hrwa.IWaBonusMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBonusMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}
}
