package nc.bs.hrwa.wa_ba_sch.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.pub.VOStatus;

/**
 * ��׼������˵�BP
 */
public class AceWaBaSchApproveBP {

  /**
   * ��˶���
   * 
   * @param vos
   * @param script
   * @return
   */
  

  public AggWaBaSchHVO[] approve(AggWaBaSchHVO[] clientBills,
      AggWaBaSchHVO[] originBills) {
    BillUpdate<AggWaBaSchHVO> update = new BillUpdate<AggWaBaSchHVO>();
    AggWaBaSchHVO[] returnVos = update.update(clientBills, originBills);
    return returnVos;
  }
  
}