package nc.ui.hrwa.wa_ba_sch.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.AppContext;

public class AceAddHandler implements IAppEventHandler<AddEvent> {

  @Override
  public void handleAppEvent(AddEvent e) {
    String pk_group = e.getContext().getPk_group();
    String pk_org = e.getContext().getPk_org();
    BillCardPanel panel = e.getBillForm().getBillCardPanel();
	    //设置主组织默认值
    panel.setHeadItem("pk_group", pk_group); 
    panel.setHeadItem("pk_org", pk_org); 
    //设置单据状态、日期默认值
    panel.setHeadItem("approvestatus", BillStatusEnum.FREE.value());
    panel.setHeadItem("billdate", AppContext.getInstance().getBusiDate()); 
  }

}
