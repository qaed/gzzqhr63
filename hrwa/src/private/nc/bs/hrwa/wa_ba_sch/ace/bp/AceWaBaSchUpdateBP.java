package nc.bs.hrwa.wa_ba_sch.ace.bp;

import nc.bs.hrwa.wa_ba_sch.plugin.bpplugin.WaBaSchPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.ICompareRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

/**
 * 修改保存的BP
 * 
 */
public class AceWaBaSchUpdateBP {
	public AggWaBaSchHVO[] update(AggWaBaSchHVO[] bills, AggWaBaSchHVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggWaBaSchHVO> bp = new UpdateBPTemplate<AggWaBaSchHVO>(WaBaSchPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggWaBaSchHVO> processer) {
		IRule<AggWaBaSchHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("BAAL");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem("code");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);
	}

	private void addBeforeRule(CompareAroundProcesser<AggWaBaSchHVO> processer) {
		IRule<AggWaBaSchHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		ICompareRule<AggWaBaSchHVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCbilltype("BAAL");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCodeItem("code");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
		rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
		processer.addBeforeRule(rule);
	}
}
