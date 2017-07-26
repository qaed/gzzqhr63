package nc.bs.pub.action;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusApproveBP;
import nc.bs.hrwa.wa_bonus.plugin.bpplugin.WaBonusPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.VOStatus;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.NCLocator;

public class N_JJHZ_APPROVE extends AbstractPfAction<AggWaBaBonusHVO> {

  public N_JJHZ_APPROVE() {
    super();
  }

  @Override
  protected CompareAroundProcesser<AggWaBaBonusHVO> getCompareAroundProcesserWithRules(
      Object userObj) {
    CompareAroundProcesser<AggWaBaBonusHVO> processor =
        new CompareAroundProcesser<AggWaBaBonusHVO>(
            WaBonusPluginPoint.APPROVE);
   IRule<AggWaBaBonusHVO> rule=null;
				  				   				     rule = new nc.bs.pubapp.pub.rule.ApproveStatusCheckRule();
				   				   				    				   				   
	   				     processor.addBeforeRule(rule);
				   				   
  
				      return processor;
  }
  
  @Override
  protected AggWaBaBonusHVO[] processBP(Object userObj,
      AggWaBaBonusHVO[] clientFullVOs, AggWaBaBonusHVO[] originBills) {
		      AggWaBaBonusHVO[] bills = null;
		      nc.itf.hrwa.IWaBonusMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBonusMaintain.class);
      try {
          bills=operator.approve(clientFullVOs,originBills);
      } catch (BusinessException e) {
				          ExceptionUtils.wrappBusinessException(e.getMessage());
			      }
      return bills;
  }

}
