package nc.itf.hrwa;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.wa.wa_ba.bonus.AggWaBaBonusHVO;
import nc.vo.pub.BusinessException;

public interface IWaBonusMaintain {

    public void delete(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException;

    public AggWaBaBonusHVO[] insert(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException;
  
    public AggWaBaBonusHVO[] update(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills) throws BusinessException;


    public AggWaBaBonusHVO[] query(IQueryScheme queryScheme)
      throws BusinessException;

  public AggWaBaBonusHVO[] save(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException ;

  public AggWaBaBonusHVO[] unsave(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException ;

  public AggWaBaBonusHVO[] approve(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException ;

  public AggWaBaBonusHVO[] unapprove(AggWaBaBonusHVO[] clientFullVOs,AggWaBaBonusHVO[] originBills)
      throws BusinessException ;
}
