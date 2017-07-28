package nc.ui.hrwa.wa_ba_item.ace.maintain;

import nc.ui.pubapp.uif2app.model.BaseBillModelDataManager;
import nc.vo.pubapp.AppContext;

public class AceWa_ba_itemDataManager extends BaseBillModelDataManager {

	@Override
	public void initModel() {
//		this.getModel().
		String pk_org = getModel().getContext().getPk_org();
//		String pk_group = AppContext.getInstance().getPkGroup();//0001A110000000000HED
		String sqlwhere = " and pk_org = '" + pk_org + "' ";
		super.initModelBySqlWhere(sqlwhere);
	}

}
