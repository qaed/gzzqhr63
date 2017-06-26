package nc.vo.wa.wa_ba.sch;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggWaBaSchHVOMeta extends AbstractBillMeta {
  public AggWaBaSchHVOMeta() {
    this.init();
  }
  private void init() {
    this.setParent(nc.vo.wa.wa_ba.sch.WaBaSchHVO.class);
    this.addChildren(nc.vo.wa.wa_ba.sch.WaBaSchBVO.class);
  }
}