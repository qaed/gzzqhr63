package nc.bs.pub.action;

import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.hrwa.wa_ba_sch.ace.bp.AceWaBaSchUnSendApproveBP;
import nc.bs.hrwa.wa_ba_sch.plugin.bpplugin.WaBaSchPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.NCLocator;

public class N_BAAL_UNSAVEBILL extends AbstractPfAction<AggWaBaSchHVO> {

  public N_BAAL_UNSAVEBILL() {
    super();
  }

  @Override
  protected CompareAroundProcesser<AggWaBaSchHVO> getCompareAroundProcesserWithRules(
      Object userObj) {
    CompareAroundProcesser<AggWaBaSchHVO> processor =
        new CompareAroundProcesser<AggWaBaSchHVO>(
            WaBaSchPluginPoint.UNSEND_APPROVE);
   // TODO 在此处添加前后规则
		   IRule<AggWaBaSchHVO> rule=null;
					  				   				     rule = new nc.bs.pubapp.pub.rule.UncommitStatusCheckRule();
				   				   				    				   				   
				   				     processor.addBeforeRule(rule);
				   				   
  
				      return processor;
  }

  @Override
  protected AggWaBaSchHVO[] processBP(Object userObj,
      AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) {
    nc.itf.hrwa.IWaBaSchMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaSchMaintain.class);
    AggWaBaSchHVO[] bills = null;
      try {
          bills=operator.unsave(clientFullVOs, originBills);
      } catch (BusinessException e) {
				          ExceptionUtils.wrappBusinessException(e.getMessage());
			      }
    return bills;
  }

}
