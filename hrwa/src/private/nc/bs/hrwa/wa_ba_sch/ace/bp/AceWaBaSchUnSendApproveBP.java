package nc.bs.hrwa.wa_ba_sch.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceWaBaSchUnSendApproveBP {
  public AggWaBaSchHVO[] unSend(AggWaBaSchHVO[] clientBills,
      AggWaBaSchHVO[] originBills) {
    // 把VO持久化到数据库中
    this.setHeadVOStatus(clientBills);
    BillUpdate<AggWaBaSchHVO> update = new BillUpdate<AggWaBaSchHVO>();
    AggWaBaSchHVO[] returnVos = update.update(clientBills, originBills);
    return returnVos;
  }
  
  private void setHeadVOStatus(AggWaBaSchHVO[] clientBills) {
        for (AggWaBaSchHVO clientBill : clientBills) {
        clientBill.getParentVO().setAttributeValue("approvestatus", BillStatusEnum.FREE.value());
            clientBill.getParentVO().setStatus(VOStatus.UPDATED);
        }
    }
}
