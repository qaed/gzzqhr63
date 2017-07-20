package nc.vo.wa.wa_ba.bonus;

import nc.vo.pub.IAttributeMeta;
import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVOMeta;

public class AggWaBaBonusHVOMeta extends AbstractBillMeta {
	public AggWaBaBonusHVOMeta() {
		this.init();
	}

	private void init() {
		this.setParent(nc.vo.wa.wa_ba.bonus.WaBaBonusHVO.class);
		this.addChildren(nc.vo.wa.wa_ba.sch.WaBaSchBVO2.class);
	}

//	@Override
//	public IAttributeMeta[] getChildForeignKeys() {
//
//		return new AggWaBaSchHVOMeta().getChildForeignKeys();
//	}

}