package nc.ui.hrwa.wa_ba_unit.ace.view;

import nc.ui.hr.comp.trn.PsnChangeDlg;
import nc.vo.hi.pub.CommonValue;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.wa_ba.unit.WaUnitLoginContext;

/**
 * 变动人员对话框
 */
@SuppressWarnings("restriction")
public class WaUnitPsnChangeDlg extends PsnChangeDlg {

	private static final long serialVersionUID = 7531876897531230834L;

	public WaUnitPsnChangeDlg(WaUnitLoginContext context, UFLiteralDate beginDate, UFLiteralDate endDate) {
		super(context, beginDate, endDate);
	}

	@Override
	public String getAddWhere(int trnType) {
		return ShowChangePsn.getAddWhere((WaUnitLoginContext) getContext(), trnType);
	}

	@Override
	public Integer[] getInitTabs() {
		return new Integer[] { CommonValue.TRN_ADD, CommonValue.TRN_SUB, /*CommonValue.TRN_PART_ADD, CommonValue.TRN_PART_SUB,*/CommonValue.TRN_POST_MOD };
	}

}
