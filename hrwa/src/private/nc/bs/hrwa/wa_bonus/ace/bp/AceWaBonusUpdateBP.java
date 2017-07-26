package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.bs.hrwa.wa_bonus.plugin.bpplugin.WaBonusPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.ICompareRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

/**
 * 修改保存的BP
 * 
 */
public class AceWaBonusUpdateBP {

  public AggWaBaBonusHVO[] update(AggWaBaBonusHVO[] bills, AggWaBaBonusHVO[] originBills) {

    		    // 调用修改模板
        UpdateBPTemplate<AggWaBaBonusHVO> bp = new UpdateBPTemplate<AggWaBaBonusHVO>(WaBonusPluginPoint.UPDATE);

        // 执行前规则
        this.addBeforeRule(bp.getAroundProcesser());
        // 执行后规则
        this.addAfterRule(bp.getAroundProcesser());
        return bp.update(bills, originBills);
  }
  private void addAfterRule(CompareAroundProcesser<AggWaBaBonusHVO> processer) {
  IRule<AggWaBaBonusHVO> rule=null;
				  				   				    				     rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
				    				   				   				    				     				      ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setCbilltype("JJHZ");
				     				    				   				    				     				      ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setCodeItem("bill_code");
				     				    				   				    				     				      ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setGroupItem("pk_group");
				     				    				   				    				     				      ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setOrgItem("pk_org");
				     				    				   				   
				   				     processer.addAfterRule(rule);
				   				     
    }
  private void addBeforeRule(CompareAroundProcesser<AggWaBaBonusHVO> processer) {
   IRule<AggWaBaBonusHVO> rule=null;
					  				   				   				     rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
				    				   				   				    				     				    				   				    				      				       processer.addBeforeRule(rule);
				      				      				   				   				     ICompareRule<AggWaBaBonusHVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
				    				   				   				    				     ((nc.bs.pubapp.pub.rule.UpdateBillCodeRule)ruleCom).setCbilltype("JJHZ");
				    				   				    				     ((nc.bs.pubapp.pub.rule.UpdateBillCodeRule)ruleCom).setCodeItem("bill_code");
				    				   				    				     ((nc.bs.pubapp.pub.rule.UpdateBillCodeRule)ruleCom).setGroupItem("pk_group");
				    				   				    				     ((nc.bs.pubapp.pub.rule.UpdateBillCodeRule)ruleCom).setOrgItem("pk_org");
				    				   				    				      				       processer.addBeforeRule(ruleCom);
				      				      				   				   				     rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
				    				   				   				    				     				    				   				    				      				       processer.addBeforeRule(rule);
				      				        }

}
