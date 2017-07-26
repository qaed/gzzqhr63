package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.bs.hrwa.wa_bonus.plugin.bpplugin.WaBonusPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;

/**
 * ��׼����ɾ��BP
 */
public class AceWaBonusDeleteBP {

  public void delete(AggWaBaBonusHVO[] bills) {

      DeleteBPTemplate<AggWaBaBonusHVO> bp =
          new DeleteBPTemplate<AggWaBaBonusHVO>(WaBonusPluginPoint.DELETE);
     
	      // ����ִ��ǰ����
      this.addBeforeRule(bp.getAroundProcesser());
      // ����ִ�к�ҵ�����
      this.addAfterRule(bp.getAroundProcesser());
      bp.delete(bills);
  }
	   private void addBeforeRule(AroundProcesser<AggWaBaBonusHVO> processer) {
	   IRule<AggWaBaBonusHVO> rule=null;
					  				   				     rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
				   				   				    				   				   
				   				     processer.addBeforeRule(rule);
				   				   
      }
  /**
   * ɾ����ҵ�����
   * 
   * @param processer
   */
  private void addAfterRule(AroundProcesser<AggWaBaBonusHVO> processer) {
  IRule<AggWaBaBonusHVO> rule=null;
				  				   				     rule = new nc.bs.pubapp.pub.rule.ReturnBillCodeRule();
				   				   				    				     ((nc.bs.pubapp.pub.rule.ReturnBillCodeRule)rule).setCbilltype("JJHZ");
				    				   				    				     ((nc.bs.pubapp.pub.rule.ReturnBillCodeRule)rule).setCodeItem("bill_code");
				    				   				    				     ((nc.bs.pubapp.pub.rule.ReturnBillCodeRule)rule).setGroupItem("pk_group");
				    				   				    				     ((nc.bs.pubapp.pub.rule.ReturnBillCodeRule)rule).setOrgItem("pk_org");
				    				   				   
				   				     processer.addAfterRule(rule);
				   				   
    }
}
