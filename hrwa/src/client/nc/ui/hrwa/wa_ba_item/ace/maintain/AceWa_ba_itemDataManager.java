package nc.ui.hrwa.wa_ba_item.ace.maintain;

import nc.ui.pubapp.uif2app.model.BaseBillModelDataManager;
import nc.vo.pubapp.AppContext;

public class AceWa_ba_itemDataManager extends BaseBillModelDataManager {

	@Override
	public void initModel() {
		String pk_group = AppContext.getInstance().getPkGroup();
		String sqlwhere = " and pk_group = '" + pk_group + "' ";
		super.initModelBySqlWhere(sqlwhere);
	}

}
