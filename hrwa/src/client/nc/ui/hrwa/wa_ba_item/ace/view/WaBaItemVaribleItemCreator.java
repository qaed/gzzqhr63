package nc.ui.hrwa.wa_ba_item.ace.view;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.wa.IItemQueryService;
import nc.itf.hrwa.IWa_ba_itemMaintain;
import nc.ui.hr.formula.HRFormulaItem;
import nc.ui.hr.formula.itf.IVariableFactory;
import nc.ui.hr.formula.variable.AbstractVaribleCreator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.formulaedit.FormulaItem;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.wa_ba.item.ItemsVO;

public class WaBaItemVaribleItemCreator extends AbstractVaribleCreator {
	public WaBaItemVaribleItemCreator() {
	}

	public List<FormulaItem> createFormulaItems(Object... params) {
		IWa_ba_itemMaintain service = (IWa_ba_itemMaintain) NCLocator.getInstance().lookup(IWa_ba_itemMaintain.class);

		List<FormulaItem> fieldItems = new ArrayList();

		ItemsVO[] items = new ItemsVO[0];
		LoginContext context = (LoginContext) params[0];
		try {
			items = service.query(null);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		if (items != null) {
			for (ItemsVO itemVO : items) {
				fieldItems.add(new HRFormulaItem("wa_ba_sch_psns." + itemVO.getCode(), itemVO.getName(), getStdDes("奖金分配项目", itemVO.getName()), getStdDes("奖金分配项目", itemVO.getName())));
			}
		}
		return fieldItems;
	}
}
