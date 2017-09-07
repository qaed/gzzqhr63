package nc.vo.wa.wa_ba.sch;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.wa.wa_ba.sch.WaBaSchHVO")
public class AggWaBaSchHVO extends AbstractBill {

  @Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggWaBaSchHVOMeta.class);
    return billMeta;
  }

  @Override
  public WaBaSchHVO getParentVO() {
    return (WaBaSchHVO) this.getParent();
  }
}