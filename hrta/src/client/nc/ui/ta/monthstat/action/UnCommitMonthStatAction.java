package nc.ui.ta.monthstat.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.ui.hr.pf.PFConfirmDialogUtils;
import nc.ui.hr.pf.action.PFCallBackAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.view.PsnMonthStatPanel;
import nc.ui.ta.statistic.pub.model.ChangableColumnPaginationModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.ValidationException;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 月报提交
 */
@SuppressWarnings("restriction")
public class UnCommitMonthStatAction extends PFCallBackAction {
	private static final long serialVersionUID = 8925520798701010802L;
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
			if (entry.getKey() == null) {
				continue;
			}
			minVO = null;
			MonthStatVO[] MonthStatVOs = entry.getValue();
			for (MonthStatVO monthStatVO : MonthStatVOs) {
				if (monthStatVO.getApprovestatus() != ApproveStatus.COMMIT) {
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
//			monthStatVO.setBillmaker(getContext().getPk_loginUser());
			minAggvo.setParentVO(monthStatVO);
			selectedaggvos.add(minAggvo);
		}
		selectData = selectedaggvos.toArray(new AggMonthStatVO[0]);

		if (selectData == null || selectData.length == 0) {
			throw new ValidationException("数据异常，不存在需要收回的单据！");//ResHelper.getString("6001pf", "06001pf0039")/* @res "请先勾选待操作的数据!" */
		}

		initDataPermission();
		// 校验与收回操作均放到后台处理
		putValue(MESSAGE_AFTER_ACTION, null);
		// 确定要收回单据吗？
		int iResult = PFConfirmDialogUtils.showConfirmCallBackDialog(getEntranceUI());

		if (iResult != UIDialog.ID_YES) {
			setCancelMsg();

			return;
		}

		// 调用批量收回服务
		PfProcessBatchRetObject validateRetObj = callbackValidate();

		if (validateRetObj == null) {
			return;
		}

		if (ArrayUtils.isEmpty(validateRetObj.getRetObj()) && StringUtils.isNotBlank(validateRetObj.getExceptionMsg())) {
			// 没有通过校验的则直接抛异常
			throw new ValidationException(validateRetObj.getExceptionMsg());
		}

		if (validateRetObj.getRetObj() == null || validateRetObj.getRetObj().length == 0) {
			return;
		}

		PfProcessBatchRetObject callbackRetObj = null;
		try {
			callbackRetObj =
					PfUtilClient.runBatchNew(getEntranceUI(), IPFActionName.RECALL, getModel().getBillType(), (AggregatedValueObject[]) validateRetObj.getRetObj(), null, null, null);
		} catch (Exception e1) {
			Logger.error(e1.getMessage(), e1);
			if (validateRetObj.getRetObj().length > 1) {
				throw new ValidationException(e1.getMessage());
			}

			// 对于一条单据如果发生异常要单独处理
			PFBatchExceptionInfo errinfo = new PFBatchExceptionInfo();
			errinfo.putErrorMessage(1, validateRetObj.getRetObj()[0], e1.getMessage());
			callbackRetObj = new PfProcessBatchRetObject(new Object[0], errinfo);
		}

		if (callbackRetObj != null && callbackRetObj.getRetObj() != null && callbackRetObj.getRetObj().length > 0) {
			getModel().directlyUpdate(callbackRetObj.getRetObj());
			ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6001pf", "06001pf0041")
			/* @res "成功收回单据！" */, getContext());// 成功收回单据！
		}

		String errMsg = "";

		if (StringUtils.isNotBlank(validateRetObj.getExceptionMsg())) {
			errMsg += validateRetObj.getExceptionMsg() + '\n';
		}

		if (callbackRetObj != null && StringUtils.isNotBlank(callbackRetObj.getExceptionMsg())) {
			errMsg += callbackRetObj.getExceptionMsg();
		}

		if (StringUtils.isNotBlank(errMsg)) {
			throw new ValidationException(errMsg);
		}

		//刷新界面
		getPaginationModel().refresh();

		//		putValue("message_after_action", ResHelper.getString("60130payslipaly", "060130payslipaly0551"));
		//		putValue(MESSAGE_AFTER_ACTION, "提交操作完成！");
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
		//当前组织月报是否需要审核判断
		if (context.getAllParams().getTimeRuleVO() == null || context.getAllParams().getTimeRuleVO().isMonthStatNeedApprove() != true)
			return false;

		MonthStatVO[] objs = getPsnModel().getData();
		if (objs == null || objs.length < 1) {//||objs[0].getApprovestatus()
			return false;
		}

		return (getModel().getUiState() == UIState.NOT_EDIT || getModel().getUiState() == UIState.INIT);

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

	@Override
	protected LoginContext getContext() {
		return getPsnModel().getContext();
	}

}