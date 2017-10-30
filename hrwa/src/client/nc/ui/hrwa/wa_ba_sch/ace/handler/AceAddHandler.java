package nc.ui.hrwa.wa_ba_sch.ace.handler;

import java.util.Calendar;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.org.IOrgVersionQryService;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.pub.BusinessException;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.vorg.OrgVersionVO;

@SuppressWarnings("restriction")
public class AceAddHandler implements IAppEventHandler<AddEvent> {

	@Override
	public void handleAppEvent(AddEvent e) {
		BillCardPanel panel = e.getBillForm().getBillCardPanel();
		// 设置主组织默认值
		panel.setHeadItem("pk_group", e.getContext().getPk_group());
		panel.setHeadItem("pk_org", e.getContext().getPk_org());
		IOrgVersionQryService orgService = NCLocator.getInstance().lookup(IOrgVersionQryService.class);

		try {
			OrgVersionVO orgVersion = orgService.getOrgUnitLastVersionByOrgID(e.getContext().getPk_org());
			panel.setHeadItem("pk_org_v", orgVersion.getPk_vid());
			//自动生成编码
			panel.setHeadItem("sch_code", ((IHrBillCode) NCLocator.getInstance().lookup(IHrBillCode.class)).getBillCode("BAAL", e.getContext().getPk_group(), e.getContext().getPk_org()));

		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		// 设置单据状态、日期默认值
		panel.setHeadItem("approvestatus", BillStatusEnum.FREE.value());
		panel.setHeadItem("billdate", AppContext.getInstance().getBusiDate());
		// 年度、带出期间
		Calendar cal = Calendar.getInstance();
		panel.setHeadItem("cyear", cal.get(Calendar.YEAR));
		String month = cal.get(Calendar.MONTH) + 1 + "";
		panel.setHeadItem("cperiod", month.length() == 1 ? "0" + month : month);
		// 单据类型
		panel.setHeadItem("billtype", "BAAL");
		// 创建人、创建时间
		panel.setHeadItem("creator", e.getContext().getPk_loginUser());
		panel.setHeadItem("creationtime", AppContext.getInstance().getServerTime());
	}

}
