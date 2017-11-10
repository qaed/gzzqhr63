package nc.impl.hrwa;

import nc.impl.pub.ace.AceWaBaSchPubServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;

public class WaBaSchMaintainImpl extends AceWaBaSchPubServiceImpl implements nc.itf.hrwa.IWaBaSchMaintain {
	@Override
	public void delete(IBill[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggWaBaSchHVO[] insert(IBill[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggWaBaSchHVO[] update(IBill[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggWaBaSchHVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggWaBaSchHVO[] save(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWaBaSchHVO[] unsave(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWaBaSchHVO[] approve(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWaBaSchHVO[] unapprove(AggWaBaSchHVO[] clientFullVOs, AggWaBaSchHVO[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWaBaSchHVO[] doCaculate(IBill[] vos) throws BusinessException {
		return super.caculate(vos);
	}

	@Override
	public AggWaBaSchHVO forceComplete(AggWaBaSchHVO aggvo) throws BusinessException {
		return super.forceComplete(aggvo);
	}

	@Override
	public void deleteWorkitem(AggWaBaSchHVO aggvo) throws BusinessException {
		super.deleteWorkitem(aggvo);
	}
}
