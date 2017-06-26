package nc.bs.hrwa.wa_ba_sch.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceWaBaSchUnApproveBP {

  public AggWaBaSchHVO[] unApprove(AggWaBaSchHVO[] clientBills,
      AggWaBaSchHVO[] originBills) {
    BillUpdate<AggWaBaSchHVO> update = new BillUpdate<AggWaBaSchHVO>();
    AggWaBaSchHVO[] returnVos = update.update(clientBills, originBills);
    return returnVos;
  }
  
  private void setHeadVOStatus(AggWaBaSchHVO[] clientBills) {
        for (AggWaBaSchHVO clientBill : clientBills) {
            clientBill.getParentVO().setStatus(VOStatus.UPDATED);
        }
    }
}
