package nc.ui.hrwa.wa_ba_sch.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IWaBaSchMaintain;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;

/**
 * 示例单据的操作代理
 * 
 * @since 6.0
 * @version 2011-7-6 上午08:31:09
 * @author duy
 */
public class AceWaBaSchMaintainProxy implements IDataOperationService, IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
		nc.itf.hrwa.IWaBaSchMaintain query = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaSchMaintain.class);
		return query.query(queryScheme);
	}

	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		IWaBaSchMaintain operator = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);
		AggWaBaSchHVO[] vos = (AggWaBaSchHVO[]) operator.insert((AggWaBaSchHVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		IWaBaSchMaintain operator = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);
		AggWaBaSchHVO[] vos = (AggWaBaSchHVO[]) operator.update((AggWaBaSchHVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		IWaBaSchMaintain operator = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);
		operator.delete((AggWaBaSchHVO[]) value);
		return value;
	}
}
