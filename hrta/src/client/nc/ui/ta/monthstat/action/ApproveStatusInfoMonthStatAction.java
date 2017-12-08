package nc.ui.ta.monthstat.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.hr.utils.CommonUtils;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.workflownote.FlowStateDlg;
import nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.view.PsnMonthStatPanel;
import nc.ui.ta.statistic.pub.model.ChangableColumnPaginationModel;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

@SuppressWarnings("restriction")
public class ApproveStatusInfoMonthStatAction extends PFApproveStatusInfoAction {
	private static final long serialVersionUID = 1007154722784014647L;
	private PsnMonthStatAppModel psnModel;
	private PsnMonthStatPanel dataPanel;
	private ChangableColumnPaginationModel paginationModel;

	@SuppressWarnings("unchecked")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		MonthStatVO[] objs = (MonthStatVO[]) getPaginationModel().getRelaSortObject().toArray(new MonthStatVO[0]);
		List<MonthStatVO> checkedVos = new ArrayList<MonthStatVO>();
		for (int i = 0; i < objs.length; i++) {
			if (4 == getDataPanel().getStatPanel().getHeadBillModel().getRowState(i)) {
				checkedVos.add(objs[i]);
			}
		}

		MonthStatVO minVO = null;
		//把单据按部门分类
		Map<String, MonthStatVO[]> monthStatVOMap = CommonUtils.group2ArrayByField("srcid", checkedVos.toArray(new MonthStatVO[0]));
		if (monthStatVOMap == null || monthStatVOMap.size() != 1) {
			String title = "请先选择数据";
			String errormsg = "只能选择某一部门的数据";
			if (UIDialog.ID_YES != MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), title, errormsg)) {
				return;
			}
		}

		for (Map.Entry<String, MonthStatVO[]> entry : monthStatVOMap.entrySet()) {
			MonthStatVO[] MonthStatVOs = entry.getValue();
			for (MonthStatVO monthStatVO : MonthStatVOs) {
				if (minVO == null || minVO.getPk_monthstat() == null || minVO.getPk_monthstat().compareTo(monthStatVO.getPk_monthstat()) < 0) {
					minVO = monthStatVO;
				}
			}
		}
		minVO.setPk_monthstat(minVO.getSrcid());
		AggMonthStatVO minAggvo = new AggMonthStatVO();
		minAggvo.setParentVO(minVO);
		//		Object obj = new AggMonthStatVO[] { minAggvo };
		Object obj = minAggvo;
		NCObject ncObj = NCObject.newInstance(obj);
		IFlowBizItf itf = ncObj.getBizInterface(IFlowBizItf.class);
		String versionPK = itf.getBillVersionPK();

		// 改为按交易类型查看审批流 
		String sBillType = itf.getTranstype();
		if (sBillType == null) {
			sBillType = this.getBillType();
		}
		FlowStateDlg dlg =
				new FlowStateDlg(getModel().getContext().getEntranceUI(), sBillType, versionPK, WorkflowTypeEnum.Approveflow.getIntValue());
		dlg.setVisible(true);
	}

	/**
	 * @return psnModel
	 */
	public PsnMonthStatAppModel getPsnModel() {
		return psnModel;
	}

	/**
	 * @param psnModel 要设置的 psnModel
	 */
	public void setPsnModel(PsnMonthStatAppModel psnModel) {
		this.psnModel = psnModel;
	}

	/**
	 * @return dataPanel
	 */
	public PsnMonthStatPanel getDataPanel() {
		return dataPanel;
	}

	/**
	 * @param dataPanel 要设置的 dataPanel
	 */
	public void setDataPanel(PsnMonthStatPanel dataPanel) {
		this.dataPanel = dataPanel;
	}

	/**
	 * @return paginationModel
	 */
	public ChangableColumnPaginationModel getPaginationModel() {
		return paginationModel;
	}

	/**
	 * @param paginationModel 要设置的 paginationModel
	 */
	public void setPaginationModel(ChangableColumnPaginationModel paginationModel) {
		this.paginationModel = paginationModel;
	}

}