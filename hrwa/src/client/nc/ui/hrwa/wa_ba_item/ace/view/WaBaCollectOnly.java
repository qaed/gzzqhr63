package nc.ui.hrwa.wa_ba_item.ace.view;

import nc.ui.hr.itemsource.view.UsableController;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.wa.wa_ba.item.WaBaLoginContext;

/**
 * @author xuanlt
 */
@SuppressWarnings("restriction")
public class WaBaCollectOnly extends UsableController {
	private WaBaLoginContext context = null;

	public WaBaCollectOnly(WaBaLoginContext context) {
		this.context = context;
	}

	/**
	 * @author wh on 2009-12-14
	 * @see nc.ui.hr.itemsource.view.UsableController#isUsable(nc.vo.hr.itemsource.TypeEnumVO)
	 */
	@Override
	public boolean isUsable(TypeEnumVO type) {
		if (context.getWaLoginVO() != null && context.getWaLoginVO().getPeriodVO() != null && context.getWaLoginVO().getPeriodVO().getCperiod() != null && context.isMutiParentWaclss()) {
			return true;
		} else {
			return false;
		}
	}

}
