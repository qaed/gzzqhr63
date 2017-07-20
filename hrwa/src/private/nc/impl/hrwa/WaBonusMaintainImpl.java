package nc.impl.hrwa;

import nc.impl.pub.ace.AceWaBonusPubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.bill.pagination.util.PaginationUtils;
import nc.impl.pubapp.pattern.data.bill.BillQuery;

public class WaBonusMaintainImpl extends AceWaBonusPubServiceImpl implements nc.itf.hrwa.IWaBonusMaintain {

      @Override
    public void delete(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException {
        super.pubdeleteBills(clientFullVOs,originBills);
    }
  
      @Override
    public AggWaBaBonusHVO[] insert(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException {
        return super.pubinsertBills(clientFullVOs,originBills);
    }
    
      @Override
    public AggWaBaBonusHVO[] update(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException {
        return super.pubupdateBills(clientFullVOs,originBills);    
    }
  

  @Override
  public AggWaBaBonusHVO[] query(IQueryScheme queryScheme)
      throws BusinessException {
      return super.pubquerybills(queryScheme);
  }



  @Override
  public AggWaBaBonusHVO[] save(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException {
      return super.pubsendapprovebills(clientFullVOs,originBills);
  }

  @Override
  public AggWaBaBonusHVO[] unsave(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException {
      return super.pubunsendapprovebills(clientFullVOs,originBills);
  }

  @Override
  public AggWaBaBonusHVO[] approve(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException {
      return super.pubapprovebills(clientFullVOs,originBills);
  }

  @Override
  public AggWaBaBonusHVO[] unapprove(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException {
      return super.pubunapprovebills(clientFullVOs,originBills);
  }

}
