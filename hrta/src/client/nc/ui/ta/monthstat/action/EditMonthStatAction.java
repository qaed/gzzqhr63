package nc.ui.ta.monthstat.action;

import nc.ui.hr.uif2.action.EditAction;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.vo.ta.monthstat.MonthStatVO;

@SuppressWarnings("restriction")
public class EditMonthStatAction extends EditAction {
	private static final long serialVersionUID = -436068001153977305L;

	public EditMonthStatAction() {
	}

	@Override
	protected boolean isActionEnable() {
		PsnMonthStatAppModel model = (PsnMonthStatAppModel) getModel();
		MonthStatVO[] vos = model.getData();
		if (vos == null || vos.length == 0) {
			return false;
		}
		for (MonthStatVO monthStatVO : vos) {
			if (monthStatVO.getApprovestatus() != ApproveStatus.FREE) {
				return false;
			}
		}
		return super.isActionEnable();
	}

}
