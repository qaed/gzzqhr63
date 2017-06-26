package nc.bs.hrwa.wa_ba_sch.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pub.VOStatus;

/**
 * 标准单据送审的BP
 */
public class AceWaBaSchSendApproveBP {
  /**
   * 送审动作
   * 
   * @param vos 单据VO数组
   * @param script 单据动作脚本对象
   * @return 送审后的单据VO数组
   */
  
  public AggWaBaSchHVO[] sendApprove(AggWaBaSchHVO[] clientBills,
      AggWaBaSchHVO[] originBills) {
      for(AggWaBaSchHVO clientFullVO:clientBills){
		          clientFullVO.getParentVO().setAttributeValue("approvestatus", BillStatusEnum.COMMIT.value());
		          clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
	      }
    // 数据持久化
    AggWaBaSchHVO[] returnVos =
        new BillUpdate<AggWaBaSchHVO>().update(clientBills, originBills);
    return returnVos;
  }
}
