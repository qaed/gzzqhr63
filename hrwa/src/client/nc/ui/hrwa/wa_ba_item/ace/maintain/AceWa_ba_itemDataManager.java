package nc.ui.hrwa.wa_ba_item.ace.maintain;

import nc.ui.pubapp.uif2app.model.BaseBillModelDataManager;
import nc.vo.pubapp.AppContext;

@SuppressWarnings("restriction")
public class AceWa_ba_itemDataManager extends BaseBillModelDataManager {

	@Override
	public void initModel() {

		//		String pk_org = getModel().getContext().getPk_org();
		//		String sqlwhere = " and pk_org = '" + pk_org + "' ";
		// 还原为初始形式
		String pk_group = AppContext.getInstance().getPkGroup();
		String sqlwhere = " and pk_group = '" + pk_group + "' ";
		super.initModelBySqlWhere(sqlwhere);
	}

}
