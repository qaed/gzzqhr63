package nc.ui.hrwa.wa_ba_unit.ace.view;

import nc.ui.pubapp.uif2app.view.BillForm;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaUnitLoginContext;

@SuppressWarnings("restriction")
public class WaBaUnitBillForm extends BillForm {

	private static final long serialVersionUID = 797342370572536044L;

	/* ���� Javadoc��
	 * @see nc.ui.pubapp.uif2app.view.BillForm#onSelectionChanged()
	 */
	@Override
	protected void onSelectionChanged() {
		initSelectedVO();
		super.onSelectionChanged();

	}

	/**
	 * ��ʼ��LoginContext��selectedVO
	 */
	private void initSelectedVO() {
		WaUnitLoginContext loginContext = (WaUnitLoginContext) this.getModel().getContext();
		loginContext.setSelectedVO((AggWaBaUnitHVO) this.getModel().getSelectedData());
	}

}
