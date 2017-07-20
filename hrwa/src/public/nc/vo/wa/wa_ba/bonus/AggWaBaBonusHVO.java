package nc.vo.wa.wa_ba.bonus;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.wa.wa_ba.bonus.WaBaBonusHVO")
public class AggWaBaBonusHVO extends AbstractBill {

  @Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggWaBaBonusHVOMeta.class);
    return billMeta;
  }

  @Override
  public WaBaBonusHVO getParentVO() {
    return (WaBaBonusHVO) this.getParent();
  }
}