package nc.ui.hrwa.wa_ba_sch.ace.handler;

import java.util.Calendar;

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
		BillCardPanel panel = e.getBillForm().getBillCardPanel();
		// ��������֯Ĭ��ֵ
		panel.setHeadItem("pk_group", e.getContext().getPk_group());
		panel.setHeadItem("pk_org", e.getContext().getPk_org());
		// ���õ���״̬������Ĭ��ֵ
		panel.setHeadItem("approvestatus", BillStatusEnum.FREE.value());
		panel.setHeadItem("billdate", AppContext.getInstance().getBusiDate());
		// ��ȡ������ڼ�
		Calendar cal = Calendar.getInstance();
		panel.setHeadItem("cyear", cal.get(Calendar.YEAR));
		panel.setHeadItem("cperiod", cal.get(Calendar.MONTH));
		// ��������
		panel.setHeadItem("billtype", "BAAL");
	}

}
