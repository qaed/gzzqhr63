package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pub.VOStatus;

/**
 * ��׼���������BP
 */
public class AceWaBonusSendApproveBP {
  /**
   * ������
   * 
   * @param vos ����VO����
   * @param script ���ݶ����ű�����
   * @return �����ĵ���VO����
   */
  
  public AggWaBaBonusHVO[] sendApprove(AggWaBaBonusHVO[] clientBills,
      AggWaBaBonusHVO[] originBills) {
      for(AggWaBaBonusHVO clientFullVO:clientBills){
		          clientFullVO.getParentVO().setAttributeValue("approvestatus", BillStatusEnum.COMMIT.value());
		          clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
	      }
    // ���ݳ־û�
    AggWaBaBonusHVO[] returnVos =
        new BillUpdate<AggWaBaBonusHVO>().update(clientBills, originBills);
    return returnVos;
  }
}
