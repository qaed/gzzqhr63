package nc.ui.hrwa.wa_ba_unit.ace.maintain;

import nc.ui.pubapp.uif2app.model.BaseBillModelDataManager;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.AppContext;

@SuppressWarnings("restriction")
public class AceWaBaUnitDataManager extends BaseBillModelDataManager {
	@Override
	public void initModel() {
		String pk_group = AppContext.getInstance().getPkGroup();
		String pk_org = getModel().getContext().getPk_org();
		String sqlwhere = " pk_group = '" + pk_group + "' and pk_org = '" + pk_org + "' order by code asc";
		super.initModelBySqlWhere(sqlwhere);
	}

	public void initModelByQueryScheme(IQueryScheme queryScheme) {

		String where = queryScheme.getWhereSQLOnly();
		String pk_group = AppContext.getInstance().getPkGroup();
		String pk_org = getModel().getContext().getPk_org();
		if (where == null) {
			where = " pk_group = '" + pk_group + "' and pk_org = '" + pk_org + "' order by code asc";
		} else {
			where += " and  pk_group = '" + pk_group + "' and pk_org = '" + pk_org + "' order by code asc";
		}

		super.initModelBySqlWhere(where);

	}
}
