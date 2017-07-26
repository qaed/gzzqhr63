package nc.bs.pub.action;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusUnApproveBP;
import nc.bs.hrwa.wa_bonus.plugin.bpplugin.WaBonusPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.VOStatus;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.NCLocator;

public class N_JJHZ_UNAPPROVE extends AbstractPfAction<AggWaBaBonusHVO> {

  public N_JJHZ_UNAPPROVE() {
    super();
  }

  @Override
  protected CompareAroundProcesser<AggWaBaBonusHVO> getCompareAroundProcesserWithRules(
      Object userObj) {
    CompareAroundProcesser<AggWaBaBonusHVO> processor =
        new CompareAroundProcesser<AggWaBaBonusHVO>(
            WaBonusPluginPoint.UNAPPROVE);
   // TODO 在此处添加前后规则
		   IRule<AggWaBaBonusHVO> rule=null;
					  				   				     rule = new nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule();
				   				   				    				   				   
				   				     processor.addBeforeRule(rule);
				   				   
  
				      return processor;
  }

  @Override
  protected AggWaBaBonusHVO[] processBP(Object userObj,
      AggWaBaBonusHVO[] clientFullVOs, AggWaBaBonusHVO[] originBills) {
    for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			        clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		    }
		    AggWaBaBonusHVO[] bills = null;
      try {
          nc.itf.hrwa.IWaBonusMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBonusMaintain.class);
          bills=operator.unapprove(clientFullVOs,originBills);
      } catch (BusinessException e) {
				          ExceptionUtils.wrappBusinessException(e.getMessage());
			      }
    return bills;
  }

}
