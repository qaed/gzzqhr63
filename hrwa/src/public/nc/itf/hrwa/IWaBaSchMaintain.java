package nc.itf.hrwa;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;

public interface IWaBaSchMaintain {

	public void delete(IBill[] vos) throws BusinessException;

	public IBill[] insert(IBill[] vos) throws BusinessException;

	public IBill[] update(IBill[] vos) throws BusinessException;

	public AggWaBaSchHVO[] query(IQueryScheme queryScheme) throws BusinessException;

	public AggWaBaSchHVO[] save(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException;

	public AggWaBaSchHVO[] unsave(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException;

	public AggWaBaSchHVO[] approve(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException;

	public AggWaBaSchHVO[] unapprove(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException;

	public AggWaBaSchHVO[] doCaculate(IBill[] vos) throws BusinessException;

	public AggWaBaSchHVO forceComplete(AggWaBaSchHVO aggvo) throws BusinessException;
	
	public void deleteWorkitem(AggWaBaSchHVO aggvo) throws BusinessException;
}
