package nc.bs.hrwa.wa_bonus.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pub.VOStatus;

/**
 * 标准单据送审的BP
 */
public class AceWaBonusSendApproveBP {
  /**
   * 送审动作
   * 
   * @param vos 单据VO数组
   * @param script 单据动作脚本对象
   * @return 送审后的单据VO数组
   */
  
  public AggWaBaBonusHVO[] sendApprove(AggWaBaBonusHVO[] clientBills,
      AggWaBaBonusHVO[] originBills) {
      for(AggWaBaBonusHVO clientFullVO:clientBills){
		          clientFullVO.getParentVO().setAttributeValue("approvestatus", BillStatusEnum.COMMIT.value());
		          clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
	      }
    // 数据持久化
    AggWaBaBonusHVO[] returnVos =
        new BillUpdate<AggWaBaBonusHVO>().update(clientBills, originBills);
    return returnVos;
  }
}
