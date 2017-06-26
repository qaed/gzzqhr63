package nc.bs.pub.action;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchInsertBP;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchUpdateBP;
import nc.bs.hrwa.wa_ba_sch.plugin.bpplugin.WaBaSchPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.vo.jcom.lang.StringUtil;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.NCLocator;

public class N_BAAL_SAVEBASE extends AbstractPfAction<AggWaBaSchHVO> {
	@Override
	protected CompareAroundProcesser<AggWaBaSchHVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggWaBaSchHVO> processor = null;
		AggWaBaSchHVO[] clientFullVOs = (AggWaBaSchHVO[]) this.getVos();
		/*
		 * BillTransferTool<AggWaBaSchHVO> tool = new
		 * BillTransferTool<AggWaBaSchHVO>( clientFullVOs); clientFullVOs =
		 * tool.getClientFullInfoBill();
		 */
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO().getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggWaBaSchHVO>(WaBaSchPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggWaBaSchHVO>(WaBaSchPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggWaBaSchHVO> rule = null;
		return processor;
	}

	@Override
	protected AggWaBaSchHVO[] processBP(Object userObj, AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) {
		AggWaBaSchHVO[] bills = null;
//		try {
//			nc.itf.hrwa.IWaBaSchMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaSchMaintain.class);
//			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO().getPrimaryKey())) {
//				//TODO
//				bills = operator.update(clientFullVOs, originBills);
//			} else {
//				bills = operator.insert(clientFullVOs, originBills);
//			}
//		} catch (BusinessException e) {
//			ExceptionUtils.wrappBusinessException(e.getMessage());
//		}
//		return bills;
		return null;
	}
}
