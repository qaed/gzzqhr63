package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceWaBonusUnSendApproveBP {
  public AggWaBaBonusHVO[] unSend(AggWaBaBonusHVO[] clientBills,
      AggWaBaBonusHVO[] originBills) {
    // 把VO持久化到数据库中
    this.setHeadVOStatus(clientBills);
    BillUpdate<AggWaBaBonusHVO> update = new BillUpdate<AggWaBaBonusHVO>();
    AggWaBaBonusHVO[] returnVos = update.update(clientBills, originBills);
    return returnVos;
  }
  
  private void setHeadVOStatus(AggWaBaBonusHVO[] clientBills) {
        for (AggWaBaBonusHVO clientBill : clientBills) {
        clientBill.getParentVO().setAttributeValue("approvestatus", BillStatusEnum.FREE.value());
            clientBill.getParentVO().setStatus(VOStatus.UPDATED);
        }
    }
}
