package nc.bs.hrwa.wa_ba_sch.ace.bp;

import nc.bs.hrwa.wa_ba_sch.plugin.bpplugin.WaBaSchPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
/**
 * 标准单据新增BP
 */
public class AceWaBaSchInsertBP {

  public AggWaBaSchHVO[] insert(AggWaBaSchHVO[] bills) {

    InsertBPTemplate<AggWaBaSchHVO> bp =
        new InsertBPTemplate<AggWaBaSchHVO>(WaBaSchPluginPoint.INSERT);
    this.addBeforeRule(bp.getAroundProcesser());
    this.addAfterRule(bp.getAroundProcesser());
    return bp.insert(bills);
    
  }
  /**
   * 新增后规则
   * 
   * @param processor
   */
  private void addAfterRule(AroundProcesser<AggWaBaSchHVO> processor) {
  IRule<AggWaBaSchHVO> rule=null;
				  				   				     rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
				   				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setCbilltype("BAAL");
				    				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setCodeItem("code");
				    				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setGroupItem("pk_group");
				    				   				    				     ((nc.bs.pubapp.pub.rule.BillCodeCheckRule)rule).setOrgItem("pk_org");
				    				   				   
				   				     processor.addAfterRule(rule);
				   				   
    }
  /**
   * 新增前规则
   * 
   * @param processor
   */
  private void addBeforeRule(AroundProcesser<AggWaBaSchHVO> processer) {
  IRule<AggWaBaSchHVO> rule=null;
				  				   				     rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
				   				   				    				   				   processer.addBeforeRule(rule);
  				   				     rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
				   				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setCbilltype("BAAL");
				    				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setCodeItem("code");
				    				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setGroupItem("pk_group");
				    				   				    				     ((nc.bs.pubapp.pub.rule.CreateBillCodeRule)rule).setOrgItem("pk_org");
				    				   				   processer.addBeforeRule(rule);
  				   				     rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
				   				   				    				   				   processer.addBeforeRule(rule);
  				   				     rule = new nc.bs.pubapp.pub.rule.CheckNotNullRule();
				   				   				    				   				   processer.addBeforeRule(rule);
    }
}
