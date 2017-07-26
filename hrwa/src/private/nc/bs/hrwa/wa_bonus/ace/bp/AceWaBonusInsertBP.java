package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.bs.hrwa.wa_bonus.plugin.bpplugin.WaBonusPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
/**
 * 标准单据新增BP
 */
public class AceWaBonusInsertBP {

  public AggWaBaBonusHVO[] insert(AggWaBaBonusHVO[] bills) {

    InsertBPTemplate<AggWaBaBonusHVO> bp =
        new InsertBPTemplate<AggWaBaBonusHVO>(WaBonusPluginPoint.INSERT);
    this.addBeforeRule(bp.getAroundProcesser());
    this.addAfterRule(bp.getAroundProcesser());
    return bp.insert(bills);
    
  }
  /**
   * 新增后规则
   * 
   * @param processor
   */
  private void addAfterRule(AroundProcesser<AggWaBaBonusHVO> processor) {
  IRule<AggWaBaBonusHVO> rule=null;
				  				   				     rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
				   				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setCbilltype("JJHZ");
				    				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setCodeItem("bill_code");
				    				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setGroupItem("pk_group");
				    				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setOrgItem("pk_org");
				    				   				   
				   				     processor.addAfterRule(rule);
				   				   
    }
  /**
   * 新增前规则
   * 
   * @param processor
   */
  private void addBeforeRule(AroundProcesser<AggWaBaBonusHVO> processer) {
  IRule<AggWaBaBonusHVO> rule=null;
				  				   				     rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
				   				   				    				   				   processer.addBeforeRule(rule);
  				   				     rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
				   				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setCbilltype("JJHZ");
				    				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setCodeItem("bill_code");
				    				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setGroupItem("pk_group");
				    				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setOrgItem("pk_org");
				    				   				   processer.addBeforeRule(rule);
  				   				     rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
				   				   				    				   				   processer.addBeforeRule(rule);
  				   				     rule = new nc.bs.pubapp.pub.rule.CheckNotNullRule();
				   				   				    				   				   processer.addBeforeRule(rule);
    }
}
