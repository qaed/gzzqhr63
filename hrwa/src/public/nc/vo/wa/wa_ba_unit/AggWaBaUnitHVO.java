package nc.vo.wa.wa_ba_unit;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.wa.wa_ba_unit.WaBaUnitHVO")
public class AggWaBaUnitHVO extends AbstractBill {

  @Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggWaBaUnitHVOMeta.class);
    return billMeta;
  }

  @Override
  public WaBaUnitHVO getParentVO() {
    return (WaBaUnitHVO) this.getParent();
  }
}