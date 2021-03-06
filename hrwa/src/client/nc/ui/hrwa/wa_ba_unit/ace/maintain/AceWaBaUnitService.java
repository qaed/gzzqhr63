package nc.ui.hrwa.wa_ba_unit.ace.maintain;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.uif2app.model.IQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.uif2.LoginContext;

/**
 * 示例单据的操作代理
 * 
 * @since 6.0
 * @version 2011-7-6 上午08:31:09
 * @author duy
 */
@SuppressWarnings("restriction")
public class AceWaBaUnitService implements IAppModelService, IQueryService {
	@Override
	public Object insert(Object object) throws Exception {
		nc.itf.hrwa.IWaBaUnitMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaUnitMaintain.class);
		// return operator.insert((WaBaUnitHVO) object);
		return operator.insert(object);
	}

	@Override
	public Object update(Object object) throws Exception {
		nc.itf.hrwa.IWaBaUnitMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaUnitMaintain.class);
		// return operator.update((WaBaUnitHVO) object);
		return operator.update(object);
	}

	@Override
	public void delete(Object object) throws Exception {
		nc.itf.hrwa.IWaBaUnitMaintain operator = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaUnitMaintain.class);
		// operator.delete((WaBaUnitHVO) object);
		operator.delete(object);
	}

	@Override
	public Object[] queryByWhereSql(String whereSql) throws Exception {
		nc.itf.hrwa.IWaBaUnitMaintain query = NCLocator.getInstance().lookup(nc.itf.hrwa.IWaBaUnitMaintain.class);
		return query.query(whereSql);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		return null;
	}
}
