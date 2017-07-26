package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceWaBonusUnApproveBP {

  public AggWaBaBonusHVO[] unApprove(AggWaBaBonusHVO[] clientBills,
      AggWaBaBonusHVO[] originBills) {
    BillUpdate<AggWaBaBonusHVO> update = new BillUpdate<AggWaBaBonusHVO>();
    AggWaBaBonusHVO[] returnVos = update.update(clientBills, originBills);
    return returnVos;
  }
  
  private void setHeadVOStatus(AggWaBaBonusHVO[] clientBills) {
        for (AggWaBaBonusHVO clientBill : clientBills) {
            clientBill.getParentVO().setStatus(VOStatus.UPDATED);
        }
    }
}
