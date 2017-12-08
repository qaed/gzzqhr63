package nc.ui.ta.monthstat.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.IPersistenceDAO;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.ui.hr.pf.PFConfirmDialogUtils;
import nc.ui.hr.pf.action.PFSubmitAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.monthstat.view.PsnMonthStatPanel;
import nc.ui.ta.statistic.pub.model.ChangableColumnPaginationModel;
import nc.ui.trade.businessaction.IPFACTION;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * �±��ύ
 */
@SuppressWarnings("restriction")
public class CommitMonthStatAction extends PFSubmitAction {
	private PsnMonthStatAppModel psnModel;
	private static final long serialVersionUID = -7457346136211531153L;
	private PsnMonthStatPanel dataPanel;
	private ChangableColumnPaginationModel paginationModel;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		IPersistenceDAO persist = NCLocator.getInstance().lookup(IPersistenceDAO.class);

		//		List<AggMonthStatVO> aggvos = new ArrayList<AggMonthStatVO>();
		//		for (MonthStatVO hvo : getPsnModel().getData()) {
		//			//			if (hvo.getApprovestatus()) {
		//			//				
		//			//			}
		//			AggMonthStatVO aggvo = new AggMonthStatVO();
		//			aggvo.setParentVO(hvo);
		//			aggvos.add(aggvo);
		//		}
		//		//		PfProcessBatchRetObject commitRetObj =
		//		//				PfUtilClient.runBatchNew(getEntranceUI(), "SAVE", "6407", aggvos.toArray(new AggMonthStatVO[0]), null, getBeforeUIClass(), this.hashPara);
		//		super.doAction(e);
		//����ģ�Ͳ����ǿ�аѸ���Ĳ���Ū����

		initDataPermission();
		// У�����ջز������ŵ���̨����
		putValue(MESSAGE_AFTER_ACTION, null);

		if (UIDialog.ID_YES != showConfirmSubmitDialog(getEntranceUI())) {
			setCancelMsg();
			return;
		}

		MonthStatVO minVO = null;
		List<MonthStatVO> selectedvos = new ArrayList<MonthStatVO>();
		List<AggMonthStatVO> selectedaggvos = new ArrayList<AggMonthStatVO>();
		MonthStatVO[] monthStatVOs = getPsnModel().getData();//��ǰ��������//getModel().getData()
		//�ѵ��ݰ����ŷ���
		Map<String, MonthStatVO[]> monthStatVOMap = CommonUtils.group2ArrayByField("pk_dept", monthStatVOs);
		for (Map.Entry<String, MonthStatVO[]> entry : monthStatVOMap.entrySet()) {
			minVO = null;
			MonthStatVO[] MonthStatVOs = entry.getValue();
			for (MonthStatVO monthStatVO : MonthStatVOs) {
				if (monthStatVO.getApprovestatus() != ApproveStatus.FREE) {//ֻ������̬�ĵ���
					minVO = null;
					break;
				}
				if (minVO == null || minVO.getPk_monthstat().compareTo(monthStatVO.getPk_monthstat()) < 0) {
					minVO = monthStatVO;
				}
			}
			if (minVO != null) {
				selectedvos.add(minVO);//ѡ��pk��С��vo��Ϊselectedvo
				//����������vo�Ը�pk��Сvo��Ϊsrc
				for (MonthStatVO monthStatVO : MonthStatVOs) {
					monthStatVO.setSrcid(minVO.getPk_monthstat());
					monthStatVO.setBillmaker(getContext().getPk_loginUser());
				}
				persist.update(MonthStatVOs, new String[] { "srcid","billmaker" });
			}
		}
		for (MonthStatVO monthStatVO : selectedvos) {
			AggMonthStatVO minAggvo = new AggMonthStatVO();
//			monthStatVO.setBillmaker(getContext().getPk_loginUser());
			minAggvo.setParentVO(monthStatVO);
			selectedaggvos.add(minAggvo);
		}
		selectData = selectedaggvos.toArray(new AggMonthStatVO[0]);

		if (selectData == null || selectData.length == 0) {
			throw new ValidationException("�����쳣����������Ҫ�ύ�ĵ��ݣ�");//ResHelper.getString("6001pf", "06001pf0039")/* @res "���ȹ�ѡ������������!" */
		}

		Integer iApproveType = getApproveType();

		// Ĭ����ϵͳ�ж�
		iApproveType = iApproveType == null ? HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW : iApproveType;

		switch (iApproveType) {
			case HRConstEnum.APPROVE_TYPE_FORCE_DIRECT:// ǿ����ֱ��
				hashPara.put(PfUtilBaseTools.PARAM_NOFLOW, PfUtilBaseTools.PARAM_NOFLOW);
				break;
			default:// 0 ϵͳ�ж�
				break;
		}

		PfProcessBatchRetObject validateRetObj = submitValidate(iApproveType);

		if (validateRetObj == null) {
			hashPara.clear();// ��ղ����б�
			setCancelMsg();
			return;
		}

		if (validateRetObj.getRetObj() == null || validateRetObj.getRetObj().length == 0) {
			// ״̬У�鶼û��ͨ��
			hashPara.clear();// ��ղ����б�
			String errStr = validateRetObj.getExceptionMsg();
			if (StringUtils.isEmpty(errStr)) {
				setCancelMsg();
				return;
			}
			throw new ValidationException(errStr);
		}

		if (validateRetObj.getRetObj() != null && validateRetObj.getRetObj().length > 0) {
			selectData = (AggregatedValueObject[]) validateRetObj.getRetObj();
		}

		PfProcessBatchRetObject commitRetObj = null;

		if (iApproveType == HRConstEnum.APPROVE_TYPE_COMMIT_APPROVE) // �ύ������
		{
			commitRetObj = doCommitWithoutAppr(selectData);
		} else {
			try {
				// ͨ�����������ύ������������ֱ��
				commitRetObj =
						PfUtilClient.runBatchNew(getEntranceUI(), IPFACTION.COMMIT, getModel().getBillType(), selectData, null, getBeforeUIClass(), hashPara);
			} catch (Exception e1) {
				Logger.error(e1.getMessage(), e1);
				if (selectData.length > 1) {
					throw new ValidationException(e1.getMessage());
				}
				// ����һ��������������쳣Ҫ��������
				PFBatchExceptionInfo errinfo = new PFBatchExceptionInfo();
				errinfo.putErrorMessage(1, selectData[0], e1.getMessage());
				commitRetObj = new PfProcessBatchRetObject(new Object[0], errinfo);
			}
		}

		// ������Ϣ������,����������ִ�д���
		String errMsg = "";
		String msg1 = validateRetObj.getExceptionMsg();
		String msg2 = commitRetObj == null ? "" : commitRetObj.getExceptionMsg();

		if (StringUtils.isNotBlank(msg1) || StringUtils.isNotBlank(msg2)) {
			errMsg = (StringUtils.isBlank(msg1) ? "" : msg1) + '\n' + (StringUtils.isBlank(msg2) ? "" : msg2);
		}

		if (commitRetObj != null && commitRetObj.getRetObj() != null && commitRetObj.getRetObj().length > 0) {
			// ������浥��
			getModel().directlyUpdate(commitRetObj.getRetObj());

			try {
				getModel().saveAfterBatchApprove(commitRetObj.getRetObj());
			} catch (BusinessException ex) {
				errMsg += '\n' + ex.getMessage();
			}

		}

		hashPara.clear();// ��ղ����б�

		if (StringUtils.isNotBlank(errMsg)) {
			throw new ValidationException(errMsg);
		}
		//ˢ�½���
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
		return super.isActionEnable();
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
	 * @param psnModel Ҫ���õ� psnModel
	 */
	public void setPsnModel(PsnMonthStatAppModel psnModel) {
		this.psnModel = psnModel;
	}

	/* ���� Javadoc��
	 * @see nc.ui.hr.uif2.action.HrAction#getContext()
	 */
	@Override
	protected LoginContext getContext() {
		return getPsnModel().getContext();
	}

	/**
	 * @see PFConfirmDialogUtils#showConfirmSubmitDialog(Container)
	 * @param parent
	 * @return
	 */
	private int showConfirmSubmitDialog(Container parent) {
		String strTitle = ResHelper.getString("6001uif2", "06001uif20073", new String[] { ResHelper.getString("common", "UC001-0000029") });

		String strQuestion = "�������ύ��������̬����\n\n" + ResHelper.getString("6001pf", "06001pf0052");

		return MessageDialog.showYesNoDlg(parent, strTitle, strQuestion, 8);
	}

}