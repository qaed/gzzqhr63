package nc.ui.hrwa.wa_bonus.ace.maintain;

import nc.ui.pubapp.uif2app.model.BaseBillModelDataManager;
import nc.vo.pubapp.AppContext;

public class AceWaBonusDataManager extends BaseBillModelDataManager {
	@Override
	public void initModel() {
		String pk_group = AppContext.getInstance().getPkGroup();
		String pk_org = getModel().getContext().getPk_org();
		String sqlwhere = " pk_group = '" + pk_group + "' and pk_org = '" + pk_org + "' and isnull(dr,0)=0 ";
		super.initModelBySqlWhere(sqlwhere);
	}
}
