package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.VOStatus;

/**
 * ��׼������˵�BP
 */
public class AceWaBonusApproveBP {

  /**
   * ��˶���
   * 
   * @param vos
   * @param script
   * @return
   */
  

  public AggWaBaBonusHVO[] approve(AggWaBaBonusHVO[] clientBills,
      AggWaBaBonusHVO[] originBills) {
    BillUpdate<AggWaBaBonusHVO> update = new BillUpdate<AggWaBaBonusHVO>();
    AggWaBaBonusHVO[] returnVos = update.update(clientBills, originBills);
    return returnVos;
  }
  
}
