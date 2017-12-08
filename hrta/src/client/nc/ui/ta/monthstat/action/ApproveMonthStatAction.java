package nc.ui.ta.monthstat.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.ui.hr.pf.action.PFApproveAction;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.view.PsnMonthStatPanel;
import nc.ui.ta.statistic.pub.model.ChangableColumnPaginationModel;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;

import org.apache.commons.lang.StringUtils;

/**
 * �±����
 *
 */
@SuppressWarnings("restriction")
public class ApproveMonthStatAction extends PFApproveAction {
	private static final long serialVersionUID = 7537079966609589635L;
	private PsnMonthStatAppModel psnModel;
	private PsnMonthStatPanel dataPanel;
	private ChangableColumnPaginationModel paginationModel;

	//	public ApproveMonthStatAction() {
	//		super();
	//		setCode(IActionCode.APPROVE);
	//		setBtnName(ResHelper.getString("common", "UC001-0000027")
	//		/*@res "���"*/);
	//	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		
		MonthStatVO minVO = null;
		List<MonthStatVO> selectedvos = new ArrayList<MonthStatVO>();
		List<AggMonthStatVO> selectedaggvos = new ArrayList<AggMonthStatVO>();
		MonthStatVO[] monthStatVOs = getPsnModel().getData();//��ǰ��������
		//�ѵ��ݰ�����ĵ��ݷ���
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
			selectedvos.add(minVO);//ѡ��pk��С��vo��Ϊselectedvo
		}
		for (MonthStatVO monthStatVO : selectedvos) {
			AggMonthStatVO minAggvo = new AggMonthStatVO();
			monthStatVO.setBillmaker(getContext().getPk_loginUser());
			minAggvo.setParentVO(monthStatVO);
			selectedaggvos.add(minAggvo);
		}
		selectData = selectedaggvos.toArray(new AggMonthStatVO[0]);

		if (selectData == null || selectData.length == 0) {
			throw new ValidationException("�����쳣����������Ҫ�����ĵ��ݣ�");//ResHelper.getString("6001pf", "06001pf0039")/* @res "���ȹ�ѡ������������!" */
		}
	
		initDataPermission();
		// У�����ջز������ŵ���̨����
		putValue(MESSAGE_AFTER_ACTION, null);

		PfProcessBatchRetObject validateRetObj = approveValidate();

		if (validateRetObj == null) {
			return;
		}

		if (validateRetObj.getRetObj() == null || validateRetObj.getRetObj().length == 0) {
			throw new ValidationException(validateRetObj.getExceptionMsg());
		}

		PfProcessBatchRetObject apprRetObj = null;

		if (isDirectApprove(validateRetObj.getRetObj())) {
			apprRetObj = directApprove(validateRetObj);// ��ֱ��
		} else {
			try {
				apprRetObj = pfApprove(validateRetObj);// ������
			} catch (Exception e1) {
				Logger.error(e1.getMessage(), e1);

				if (validateRetObj.getRetObj().length > 1) {
					throw new ValidationException(e1.getMessage());
				}

				// ����һ��������������쳣Ҫ��������
				PFBatchExceptionInfo errinfo = new PFBatchExceptionInfo();
				errinfo.putErrorMessage(1, validateRetObj.getRetObj()[0], e1.getMessage());
				apprRetObj = new PfProcessBatchRetObject(new Object[0], errinfo);
			}
		}

		// ������Ϣ������,����������ִ�д���
		String errMsg = "";
		String msg1 = validateRetObj.getExceptionMsg();
		String msg2 = apprRetObj == null ? "" : apprRetObj.getExceptionMsg();

		if (StringUtils.isNotBlank(msg1) || StringUtils.isNotBlank(msg2)) {
			errMsg = StringUtils.isBlank(msg1) ? msg2 : (StringUtils.isBlank(msg2) ? msg1 : (msg1 + '\n' + msg2));
			errMsg = StringUtils.removeEnd(errMsg, "\n");
			//            errMsg = (StringUtils.isBlank(msg1) ? "" : msg1) + '\n' + (StringUtils.isBlank(msg2) ? "" : msg2);
		}

		if (apprRetObj != null && apprRetObj.getRetObj() != null && apprRetObj.getRetObj().length > 0) {
			// ������浥��
			ArrayList<Object> al = new ArrayList<Object>();
			Object[] objs = apprRetObj.getRetObj();
			for (int i = 0; i < objs.length; i++) {
				if (objs[i] != null) {
					al.add(objs[i]);
				}
			}

			if (al.size() > 0) {
				getModel().directlyUpdate(al.toArray(new Object[0]));
				try {
					getModel().saveAfterBatchApprove(al.toArray(new Object[0]));
				} catch (BusinessException ex) {
					errMsg += '\n' + ex.getMessage();
				}
			}

			//            ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getApproveSuccessInfo(), getModel().getContext());

		}
		if (apprRetObj != null && StringUtils.isNotBlank(errMsg)) {
			throw new ValidationException(errMsg);
		}
		//ˢ�½���
		getPaginationModel().refresh();

	}

	@Override
	protected boolean isActionEnable() {
		TALoginContext context = (TALoginContext) this.getModel().getContext();
		if (StringUtils.isEmpty(context.getPk_org())) {
			return false;
		}
		//��ǰ��֯�±��Ƿ���Ҫ����ж�
		if (context.getAllParams().getTimeRuleVO() == null || context.getAllParams().getTimeRuleVO().isMonthStatNeedApprove() != true)
			return false;

		MonthStatVO[] objs = getPsnModel().getData();
		if (objs == null || objs.length < 1) {
			return false;
		}

		return (getModel().getUiState() == UIState.NOT_EDIT || getModel().getUiState() == UIState.INIT) && getModel().getSelectedData() != null;
	}

	/**
	 * @return psnModel
	 */
	public PsnMonthStatAppModel getPsnModel() {
		return psnModel;
	}

	/**
	 * @param psnModel Ҫ���õ� psnModel
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
	 * @param dataPanel Ҫ���õ� dataPanel
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
	 * @param paginationModel Ҫ���õ� paginationModel
	 */
	public void setPaginationModel(ChangableColumnPaginationModel paginationModel) {
		this.paginationModel = paginationModel;
	}

}