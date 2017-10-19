package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.event.ActionEvent;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hrwa.wa_ba_unit.ace.maintain.AceWaBaUnitDataManager;
import nc.ui.hrwa.wa_ba_unit.ace.view.FromDeptGenDialog;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class WaBaUnitGenerateByDeptAction extends HrAction {

	private static final long serialVersionUID = -3230767655987180128L;
	private BillForm billform;
	private IAppModelDataManager dataManager = null;
	private FromDeptGenDialog dialog;

	/**
	 * @return billform
	 */
	public BillForm getBillform() {
		return billform;
	}

	/**
	 * @param billform ÒªÉèÖÃµÄ billform
	 */
	public void setBillform(BillForm billform) {
		this.billform = billform;
	}

	protected boolean isActionEnable() {
		return (super.isActionEnable()) && (StringUtils.isNotBlank(getModel().getContext().getPk_org()));

		//		return super.isActionEnable();
	}

	public void doAction(ActionEvent e) throws Exception {
		//		LoginContext context = (LoginContext) getModel().getContext();
		//		String pk_sch_eva = context.getSchEvaVO().getPk_sch_eva();
		//		SchEvaVO schEvaVO = ((PELoginContext) getContext()).getSchEvaVO();
		//		if (schEvaVO.getScheva_status().intValue() >= SchEvaStatusEnum.EVAEND.toIntValue()) {
		//			String errMsg =
		//					MessageFormat.format(ResHelper.getString("60290106", "0602901060191"), new Object[] { SchEvaStatusEnum.EVAEND.getName() });
		//
		//			throw new BusinessException(errMsg);
		//		}
		if (1 != getDialog().showModal()) {
			putValue("message_after_action", ResHelper.getString("60290106", "0602901060082"));

			return;
		}

		putValue("message_after_action", null);
		((AceWaBaUnitDataManager) getDataManager()).refresh();
	}

	public FromDeptGenDialog getDialog() {
		LoginContext context = (LoginContext) getModel().getContext();
		this.dialog = new FromDeptGenDialog(getEntranceUI(), context);
		this.dialog.setTitle(ResHelper.getString("60290106", "0602901060136"));

		this.dialog.initUI();

		return this.dialog;
	}

	public void setDataManager(IAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public IAppModelDataManager getDataManager() {
		return this.dataManager;
	}
}
