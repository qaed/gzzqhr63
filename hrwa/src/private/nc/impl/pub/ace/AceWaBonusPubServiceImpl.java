package nc.impl.pub.ace;

import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusApproveBP;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusDeleteBP;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusInsertBP;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusSendApproveBP;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusUnApproveBP;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusUnSendApproveBP;
import nc.bs.hrwa.wa_bonus.ace.bp.AceWaBonusUpdateBP;
import nc.impl.pubapp.pattern.data.bill.SchemeBillQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;

public abstract class AceWaBonusPubServiceImpl {
		    //新增
    public AggWaBaBonusHVO[] pubinsertBills(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException {
        try {
          // 数据库中数据和前台传递过来的差异VO合并后的结果
          BillTransferTool<AggWaBaBonusHVO> transferTool =new BillTransferTool<AggWaBaBonusHVO>(clientFullVOs);
          // 调用BP
          AceWaBonusInsertBP action = new AceWaBonusInsertBP();
          AggWaBaBonusHVO[] retvos = action.insert(clientFullVOs);
          // 构造返回数据
          return transferTool.getBillForToClient(retvos);
        }catch (Exception e) {
          ExceptionUtils.marsh(e);
        }
        return null;
    }
    //删除
				    public void pubdeleteBills(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException {
				        try {
				          // 调用BP
				          new AceWaBonusDeleteBP().delete(clientFullVOs);
				        } catch (Exception e) {
				          ExceptionUtils.marsh(e);
				        }
				    }
				    //修改
				    public AggWaBaBonusHVO				[] pubupdateBills(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException {
				        try {
				          //加锁 + 检查ts
				          BillTransferTool<AggWaBaBonusHVO				> transferTool =new BillTransferTool<AggWaBaBonusHVO>(clientFullVOs);
				          AceWaBonusUpdateBP bp = new AceWaBonusUpdateBP();
				          AggWaBaBonusHVO[] retvos = bp.update(clientFullVOs, originBills);
				          // 构造返回数据
				          return transferTool.getBillForToClient(retvos);
				        }catch (Exception e) {
				          ExceptionUtils.marsh(e);
        }
				          return null;
  				  }


  public AggWaBaBonusHVO[] pubquerybills(IQueryScheme queryScheme)
      throws BusinessException {
    AggWaBaBonusHVO[] bills = null;
    try {
      this.preQuery(queryScheme);
//      BillLazyQuery<AggWaBaBonusHVO> query =
//          new BillLazyQuery<AggWaBaBonusHVO>(AggWaBaBonusHVO.class);
      SchemeBillQuery<AggWaBaBonusHVO> query = new SchemeBillQuery<AggWaBaBonusHVO>(AggWaBaBonusHVO.class);
      bills = query.query(queryScheme, null);
    }
    catch (Exception e) {
      ExceptionUtils.marsh(e);
    }
    return bills;
  }

	  /**
   * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
   * 
   * @param queryScheme
   */
  protected void preQuery(IQueryScheme queryScheme) {
    // 查询之前对queryScheme进行加工，加入自己的逻辑
  }


		  //提交
				  public AggWaBaBonusHVO[] pubsendapprovebills(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) 
				      throws BusinessException {
				    AceWaBonusSendApproveBP bp = new AceWaBonusSendApproveBP();
				    AggWaBaBonusHVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
				    return retvos;
				  }
				  
				  //收回
				  public AggWaBaBonusHVO[] pubunsendapprovebills(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) 
				      throws BusinessException {
				    AceWaBonusUnSendApproveBP bp = new AceWaBonusUnSendApproveBP();
				    AggWaBaBonusHVO[] retvos = bp.unSend(clientFullVOs, originBills);
				    return retvos;
				  };

  //审批
				  public AggWaBaBonusHVO[] pubapprovebills(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) 
				      throws BusinessException {
				    for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
				      clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
				    }
				    AceWaBonusApproveBP bp = new AceWaBonusApproveBP();
				    AggWaBaBonusHVO[] retvos = bp.approve(clientFullVOs, originBills);
				    return retvos;
				  }
				  
				  //弃审
				
				  public AggWaBaBonusHVO[] pubunapprovebills(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
				      throws BusinessException {
    				for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
				      clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
				    }
				    AceWaBonusUnApproveBP bp = new AceWaBonusUnApproveBP();
				    AggWaBaBonusHVO[] retvos = bp.unApprove(clientFullVOs, originBills);
				    return retvos;
				  }
				  
				
}