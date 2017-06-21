package nc.ui.hrwa.wa_ba_item.ace.maintain;

import nc.bs.framework.common.NCLocator;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.pubapp.uif2app.model.IQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.uif2.LoginContext;

/**
 * 示例单据的操作代理
 * 
 * @since 6.0
 * @version 2011-7-6 上午08:31:09
 * @author duy
 */
public class AceWa_ba_itemService implements IAppModelService, IQueryService {
	@Override
	public Object insert(Object object) throws Exception {
		nc.itf.hrwa.IWa_ba_itemMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWa_ba_itemMaintain.class);
		return operator.insert((ItemsVO) object);
	}

	@Override
	public Object update(Object object) throws Exception {
		nc.itf.hrwa.IWa_ba_itemMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWa_ba_itemMaintain.class);
		return operator.update((ItemsVO) object);
	}

	@Override
	public void delete(Object object) throws Exception {
		nc.itf.hrwa.IWa_ba_itemMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWa_ba_itemMaintain.class);
		operator.delete((ItemsVO) object);
	}

	@Override
	public Object[] queryByWhereSql(String whereSql) throws Exception {
		nc.itf.hrwa.IWa_ba_itemMaintain query = NCLocator.getInstance().lookup(nc.itf.hrwa.IWa_ba_itemMaintain.class);
		return query.query(whereSql);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		return null;
	}
}
