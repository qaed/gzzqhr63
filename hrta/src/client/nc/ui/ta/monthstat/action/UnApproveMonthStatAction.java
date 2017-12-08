package nc.ui.ta.monthstat.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.ui.hr.pf.action.PFUnApproveAction;
import nc.ui.pf.workitem.beside.BesideApproveContext;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.view.PsnMonthStatPanel;
import nc.ui.ta.statistic.pub.model.ChangableColumnPaginationModel;
import nc.ui.trade.businessaction.IPFACTION;
import nc.ui.uif2.UIState;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.ValidationException;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;

import org.apache.commons.lang.StringUtils;

/**
 * 取消月报审核
 */
@SuppressWarnings("restriction")
public class UnApproveMonthStatAction extends PFUnApproveAction {
	private static final long serialVersionUID = -8482436621737053175L;
	private PsnMonthStatAppModel psnModel;
	private PsnMonthStatPanel dataPanel;
	private ChangableColumnPaginationModel paginationModel;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		MonthStatVO minVO = null;
		List<MonthStatVO> selectedvos = new ArrayList<MonthStatVO>();
		List<AggMonthStatVO> selectedaggvos = new ArrayList<AggMonthStatVO>();
		MonthStatVO[] monthStatVOs = getPsnModel().getData();//当前考勤数据
		//把单据按跟随的单据分类
		Map<String, MonthStatVO[]> monthStatVOMap = CommonUtils.group2ArrayByField("srcid", monthStatVOs);
		for (Map.Entry<String, MonthStatVO[]> entry : monthStatVOMap.entrySet()) {
			if (entry.getKey()==null) {
				continue;
			}
			minVO = null;
			MonthStatVO[] MonthStatVOs = entry.getValue();
			for (MonthStatVO monthStatVO : MonthStatVOs) {
				if (monthStatVO.getApprovestatus()!=ApproveStatus.COMMIT && monthStatVO.getApprovestatus()!=ApproveStatus.APPROVING) {
					minVO = null;
					break;
				}
				if (minVO == null || minVO.getPk_monthstat().compareTo(monthStatVO.getPk_monthstat()) < 0) {
					minVO = monthStatVO;
				}
			}
			selectedvos.add(minVO);//选择pk最小的vo作为selectedvo
		}
		for (MonthStatVO monthStatVO : selectedvos) {
			AggMonthStatVO minAggvo = new AggMonthStatVO();
			monthStatVO.setBillmaker(getContext().getPk_loginUser());
			minAggvo.setParentVO(monthStatVO);
			selectedaggvos.add(minAggvo);
		}
		selectData = selectedaggvos.toArray(new AggMonthStatVO[0]);

		if (selectData == null || selectData.length == 0) {
			throw new ValidationException("数据异常，不存在需要取消审批的单据！");//ResHelper.getString("6001pf", "06001pf0039")/* @res "请先勾选待操作的数据!" */
		}

		initDataPermission();
		// 校验与收回操作均放到后台处理
		putValue(MESSAGE_AFTER_ACTION, null);

		PfProcessBatchRetObject validateRetObj = unApproveValidate();

		if (validateRetObj == null) {
			return;
		}

		if (validateRetObj.getRetObj() == null || validateRetObj.getRetObj().length == 0) {
			if (!StringUtils.isBlank(validateRetObj.getExceptionMsg())) {
				throw new ValidationException(validateRetObj.getExceptionMsg());
			}

			return;
		}

		// 调用远程服务将选中的单据分为直批单据与审批流单据
		HashMap<String, ArrayList<AggregatedValueObject>> billmap =
				getIHrPf().separateBill((AggregatedValueObject[]) validateRetObj.getRetObj());
		// 处理直批单据
		PfProcessBatchRetObject directRetObj = null;
		ArrayList<AggregatedValueObject> directBill = billmap.get(HRConstEnum.DIRECTAPPROVE);
		if (directBill != null && directBill.size() > 0) {
			directRetObj = doDirectUnapprove(directBill.toArray(new AggregatedValueObject[0]));
		}

		// 处理审批流单据
		PfProcessBatchRetObject workflowRetObj = null;
		ArrayList<AggregatedValueObject> workflowBill = billmap.get(HRConstEnum.WORKFLOWAPPROVE);
		if (workflowBill != null && workflowBill.size() > 0) {
			try {
				HashMap<String, BesideApproveContext> pfParam = null;
				if (getBesideApproveContext() != null) {
					pfParam = new HashMap<String, BesideApproveContext>();
					pfParam.put(PfUtilBaseTools.PARAM_BESIDEAPPROVE, getBesideApproveContext());
				}
				workflowRetObj =
						PfUtilClient.runBatchNew(getEntranceUI(), IPFACTION.UNAPPROVE + getContext().getPk_loginUser(), getModel().getBillType(), workflowBill.toArray(new AggregatedValueObject[0]), null, null, pfParam);
			} catch (Exception e1) {
				Logger.error(e1.getMessage(), e1);
				if (workflowBill.size() > 1) {
					throw new ValidationException(e1.getMessage());
				}
				// 对于一条单据如果发生异常要单独处理
				PFBatchExceptionInfo errinfo = new PFBatchExceptionInfo();
				errinfo.putErrorMessage(1, workflowBill.get(0), e1.getMessage());
				workflowRetObj = new PfProcessBatchRetObject(new Object[0], errinfo);
			}
			if (workflowRetObj != null && workflowRetObj.getRetObj() != null && workflowRetObj.getRetObj().length > 0) {

				getModel().directlyUpdate(workflowRetObj.getRetObj());
			}
		}

		String msg1 = StringUtils.isBlank(validateRetObj.getExceptionMsg()) ? "" : validateRetObj.getExceptionMsg();
		String msg2 = directRetObj == null || StringUtils.isBlank(directRetObj.getExceptionMsg()) ? "" : directRetObj.getExceptionMsg();
		String msg3 =
				workflowRetObj == null || StringUtils.isBlank(workflowRetObj.getExceptionMsg()) ? "" : workflowRetObj.getExceptionMsg();

		if (!StringUtils.isBlank(msg1) || !StringUtils.isBlank(msg2) || !StringUtils.isBlank(msg3)) {
			throw new ValidationException((StringUtils.isBlank(msg1) ? "" : (msg1 + '\n')) + (StringUtils.isBlank(msg2) ? "" : (msg2 + '\n')) + (StringUtils.isBlank(msg3) ? "" : msg3));
		}

		//刷新界面
		getPaginationModel().refresh();
	}

	public PsnMonthStatPanel getDataPanel() {
		return dataPanel;
	}

	public void setDataPanel(PsnMonthStatPanel dataPanel) {
		this.dataPanel = dataPanel;
	}

	@Override
	protected boolean isActionEnable() {
		TALoginContext context = (TALoginContext) this.getModel().getContext();
		if (StringUtils.isEmpty(context.getPk_org())) {
			return false;
		}
		if (context.getAllParams() == null)
			return false;
		if (context.getAllParams().getTimeRuleVO() == null)
			return false;
		//当前组织月报是否需要审核判断
		if (context.getAllParams().getTimeRuleVO().isMonthStatNeedApprove() != true)
			return false;
		MonthStatVO[] objs = getPsnModel().getData();
		if (objs == null || objs.length < 1) {
			return false;
		}

		return (getModel().getUiState() == UIState.NOT_EDIT || getModel().getUiState() == UIState.INIT) && getModel().getSelectedData() != null;
	}

	public ChangableColumnPaginationModel getPaginationModel() {
		return this.paginationModel;
	}

	public void setPaginationModel(ChangableColumnPaginationModel paginationModel) {
		this.paginationModel = paginationModel;
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

}