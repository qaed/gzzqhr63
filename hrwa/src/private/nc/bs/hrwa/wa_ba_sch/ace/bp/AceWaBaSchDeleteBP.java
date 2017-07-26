package nc.bs.hrwa.wa_ba_sch.ace.bp;

import nc.bs.hrwa.wa_ba_sch.plugin.bpplugin.WaBaSchPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;

/**
 * ��׼����ɾ��BP
 */
public class AceWaBaSchDeleteBP {
	public void delete(AggWaBaSchHVO[] bills) {
		DeleteBPTemplate<AggWaBaSchHVO> bp = new DeleteBPTemplate<AggWaBaSchHVO>(WaBaSchPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggWaBaSchHVO> processer) {
		IRule<AggWaBaSchHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggWaBaSchHVO> processer) {
		IRule<AggWaBaSchHVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.ReturnBillCodeRule();
		((nc.bs.pubapp.pub.rule.ReturnBillCodeRule) rule).setCbilltype("BAAL");
		((nc.bs.pubapp.pub.rule.ReturnBillCodeRule) rule).setCodeItem("code");
		((nc.bs.pubapp.pub.rule.ReturnBillCodeRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.ReturnBillCodeRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);
	}
}
