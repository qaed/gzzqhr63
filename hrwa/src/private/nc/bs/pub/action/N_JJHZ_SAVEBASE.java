package nc.bs.pub.action;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusInsertBP;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusUpdateBP;
import nc.bs.hrwa.wa_bonus.plugin.bpplugin.WaBonusPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.vo.jcom.lang.StringUtil;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.NCLocator;

public class N_JJHZ_SAVEBASE extends AbstractPfAction<AggWaBaBonusHVO> {

  @Override
  protected CompareAroundProcesser<AggWaBaBonusHVO> getCompareAroundProcesserWithRules(
      Object userObj) {
      CompareAroundProcesser<AggWaBaBonusHVO> processor = null;
		AggWaBaBonusHVO[] clientFullVOs = (AggWaBaBonusHVO[]) this.getVos();
		/*BillTransferTool<AggWaBaBonusHVO> tool = new BillTransferTool<AggWaBaBonusHVO>(
				clientFullVOs);
		clientFullVOs = tool.getClientFullInfoBill();*/
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggWaBaBonusHVO>(
					WaBonusPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggWaBaBonusHVO>(
					WaBonusPluginPoint.SCRIPT_INSERT);
		}
		   // TODO 在此处添加前后规则
		   IRule<AggWaBaBonusHVO> rule=null;
					  
				  		
    return processor;
  }

  @Override
  protected AggWaBaBonusHVO[] processBP(Object userObj,
      AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) {
      
      AggWaBaBonusHVO[] bills = null;
      try {
            nc.itf.hrwa.IWaBonusMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBonusMaintain.class);
            if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO().getPrimaryKey())) {
              bills = operator.update(clientFullVOs,originBills);
		            } else {
              bills = operator.insert(clientFullVOs,originBills);
		            }
      } catch (BusinessException e) {
				          ExceptionUtils.wrappBusinessException(e.getMessage());
			      }
		      return bills;
  }
}
