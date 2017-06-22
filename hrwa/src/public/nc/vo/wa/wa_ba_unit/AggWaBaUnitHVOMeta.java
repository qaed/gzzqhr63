package nc.vo.wa.wa_ba_unit;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggWaBaUnitHVOMeta extends AbstractBillMeta {
  public AggWaBaUnitHVOMeta() {
    this.init();
  }
  private void init() {
    this.setParent(nc.vo.wa.wa_ba_unit.WaBaUnitHVO.class);
    this.addChildren(nc.vo.wa.wa_ba_unit.WaBaUnitBVO.class);
  }
}