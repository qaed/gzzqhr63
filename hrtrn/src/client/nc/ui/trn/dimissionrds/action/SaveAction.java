package nc.ui.trn.dimissionrds.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPersonRecordService;
import nc.itf.trn.rds.IRdsManageService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.trn.rds.action.RdsBaseAction;
import nc.ui.trn.rds.model.RdsPsninfoModel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.pub.TRNConst;

public class SaveAction extends RdsBaseAction {

	private boolean isdisablepsn = Boolean.FALSE;

	public SaveAction() {

		super();
		ActionInitializer.initializeAction(this, IActionCode.SAVE);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("6009tran", "06009tran0042")/*
																							 * @res
																							 * "�Խ������ݽ��б���(Ctrl+S)"
																							 */);
	}

	/**
	 * У�鿪ʼ���ںϷ���
	 * 
	 * @param saveData
	 * @param curRow
	 * @return boolean
	 */
	public boolean checkBegindate(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ��һ����¼�Ŀ�ʼ����
		UFLiteralDate nextRowBegindate = null;
		// ������
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/* @res "��ʼ���ڲ������ڵ�����һ��¼�Ŀ�ʼ���ڣ�" */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/* @res "ǰһ����¼�Ŀ�ʼ���ڲ���Ϊ�գ�" */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/* @res "��ʼ���ڲ������ڵ�����һ��¼�Ľ������ڣ�" */);
		}
		if (endDate != null && nextRowBegindate != null && (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/* @res "�������ڲ������ڻ������һ��¼�Ŀ�ʼ���ڣ�" */);
		}
		// �ù�˾��һ����ְ��¼ ��ְ���ںʹ�Ա����������˾����ְ�������Ƚϣ�У���Ƿ��н���
		if (currow == 0 && TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			NCLocator.getInstance().lookup(IPersonRecordService.class).checkBeginDate(selData.getParentVO().getPsnJobVO(), (UFLiteralDate) saveData.getAttributeValue(PsnJobVO.BEGINDATE), null);
		}
		return true;
	}

	/**
	 * �����¼��Ŀ�ʼ��������
	 * 
	 * @param saveType
	 * @param strTabCode
	 * @throws BusinessException
	 */
	public void checkDataForTableType(int saveType, String strTabCode, SuperVO saveData) throws BusinessException {

		UFLiteralDate begindate = null;
		UFLiteralDate enddate = null;
		String tabCode = getListView().getCurrentTabCode();
		begindate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		enddate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		if (begindate == null) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0047")/* @res "�������ڲ���Ϊ�գ�" */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0048")/* @res "��ʼ���ڲ���Ϊ�գ�" */);
		}
		if (saveData.getAttributeValue("recordnum") != null && ((Integer) saveData.getAttributeValue("recordnum")).intValue() != 0 && enddate == null) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/* @res "�뿪���ڲ���Ϊ�գ�" */);
			}
			if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
				// ���ò��жϽ�������
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/* @res "�������ڲ���Ϊ�գ�" */);
			}
		}
		if (saveData.getAttributeValue("endflag") != null) {
			UFBoolean endflag = (UFBoolean) saveData.getAttributeValue("endflag");
			if (endflag.booleanValue() && enddate == null) {
				if (!TRNConst.Table_NAME_DIMISSION.equals(tabCode)) {
					if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/* @res "�뿪���ڲ���Ϊ�գ�" */);
					}
					if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
						// ���ò��жϽ�������
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/* @res "�������ڲ���Ϊ�գ�" */);
					}
				}
			}
		}
		if (enddate != null && begindate.afterDate(enddate)) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0051")/* @res "�������ڲ��������뿪���ڣ�" */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0052")/* @res "��ʼ���ڲ������ڽ������ڣ�" */);
		}
		if (TRNConst.Table_NAME_TRIAL.equals(tabCode)) {
			// ����
			UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue("regulardate");
			if (regulardate != null && begindate.afterDate(regulardate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/* @res "��ʼ���ڲ�������ת�����ڣ�" */);
			}
		}
		BillModel curBillModel = getListView().getBillListPanel().getBodyBillModel(getListView().getCurrentTabCode());
		int editRow = curBillModel.getEditRow();
		if (TRNConst.Table_NAME_PART.equals(strTabCode)) {
			// ��ְ��У��Ǽ�¼���ʱ���ϵ,ֻ�ڼ�ְ���ʱУ��������¼���¼���ϵ
			if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
				checkPartchg(saveData, editRow);
			} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
				// �޸ļ�ְ��¼,���⴦��
				checkPartEdit(saveData, editRow);
			}
			return;
		}
		if (TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
			// ���õ��ֶ�����һ������У��
			checkTrial(saveData, editRow);
			return;
		}
		if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
			// ����������ֶ�����һ������У��
			checkPsnchg(saveData, editRow);
			return;
		}
		checkBegindate(saveData, editRow);
	}

	private void checkPartchg(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ������
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0054")/* @res "��ʼ���ڲ������ڻ������һ��¼�Ŀ�ʼ���ڣ�" */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/* @res "ǰһ����¼�Ŀ�ʼ���ڲ���Ϊ�գ�" */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0055")/* @res "��ʼ���ڲ������ڻ������һ��¼�Ľ������ڣ�" */);
		}
	}

	private void checkPartEdit(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ��һ����¼�Ŀ�ʼ����
		UFLiteralDate nextRowBegindate = null;
		// ������
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount && saveData.getAttributeValue("lastflag") != null && !((UFBoolean) saveData.getAttributeValue("lastflag")).booleanValue()) {
			// �޸ļ�ְ�������ʷ��¼ʱ�޸�У����һ���Ŀ�ʼʱ��
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
			if (endDate != null && nextRowBegindate != null && (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/* @res "�������ڲ������ڻ������һ��¼�Ŀ�ʼ���ڣ�" */);
			}
		}
		if (currow != 0) {
			// �õ���һ������,�����һ����lastflag='Y'������һ����ʱ��Ƚ�
			CircularlyAccessibleValueObject preVO = getCurBillModel().getBodyValueRowVO(currow - 1, getListView().getCurClassName());
			if (preVO != null && preVO.getAttributeValue("lastflag") != null && !((UFBoolean) saveData.getAttributeValue("lastflag")).booleanValue()) {
				if (preVO.getAttributeValue("enddate") != null) {
					preRowEnddate = (UFLiteralDate) preVO.getAttributeValue("enddate");
				} else {
					preRowBegindate = (UFLiteralDate) preVO.getAttributeValue("begindate");
					preRowEnddate = beginDate.getDateBefore(1);
					if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/*
																									 * @res
																									 * "��ʼ���ڲ������ڵ�����һ��¼�Ŀ�ʼ���ڣ�"
																									 */);
					} else if (preRowBegindate == null) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																									 * @res
																									 * "ǰһ����¼�Ŀ�ʼ���ڲ���Ϊ�գ�"
																									 */);
					}
				}
				if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/* @res "��ʼ���ڲ������ڵ�����һ��¼�Ľ������ڣ�" */);
				}
			}
		}
	}

	private void checkPsnchg(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		;
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ��һ����¼�Ŀ�ʼ����
		UFLiteralDate nextRowBegindate = null;
		// ������
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0056")/* @res "�������ڲ������ڵ�����һ��¼�Ľ������ڣ�" */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0057")/* @res "ǰһ����¼�Ľ������ڲ���Ϊ�գ�" */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0058")/* @res "�������ڲ������ڵ�����һ��¼���뿪���ڣ�" */);
		}
		if (endDate != null && nextRowBegindate != null && (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0059")/* @res "�뿪���ڲ������ڻ������һ��¼�Ľ������ڣ�" */);
		}
	}

	private void checkTrial(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.BEGINDATE);
		UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.REGULARDATE);
		TrialVO trial = (TrialVO) saveData;
		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && regulardate == null) {
			if (trial.getTrialresult() != null && trial.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0060")/* @res "ת�����ڲ���Ϊ�գ�" */);
			}
		}
		if (regulardate != null && beginDate.afterDate(regulardate)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/* @res "��ʼ���ڲ�������ת�����ڣ�" */);
		}
		if (getModel().getEditType() == RdsPsninfoModel.INSERT && trial.getTrialresult() == null) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0061")/* @res "���ý������Ϊ�գ�" */);
		}
		// 20171206 tsy ȥ������ʷת�����ڲ����������¹�����¼�Ŀ�ʼ���ڡ�,����ʼ���ڲ����������¹�����¼�Ŀ�ʼ���ڡ���У��
		//// ���¹�����¼�Ŀ�ʼ����
		//		UFLiteralDate jobBeginDate = selData.getParentVO().getPsnJobVO().getBegindate();
		//		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && jobBeginDate != null && jobBeginDate.afterDate(beginDate)) {
		//			if (jobBeginDate != null && jobBeginDate.beforeDate(regulardate)) {
		//				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0208")/* @res "��ʷת�����ڲ����������¹�����¼�Ŀ�ʼ����" */);
		//			}
		//		} else if (jobBeginDate != null && jobBeginDate.afterDate(beginDate)) {
		//			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0062")/* @res "��ʼ���ڲ����������¹�����¼�Ŀ�ʼ����" */);
		//		}
		// 20171206 end
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		super.doAction(e);
		getListView().tableStopCellEditing(curTabCode);

		// �ǿ�У��
		getListView().dataNotNullValidate();
		// ��ʼ����ʱ���У��
		int editRow = getCurBillModel().getEditRow();
		SuperVO vo =
				(SuperVO) getListView().getBillListPanel().getBodyBillModel(curTabCode).getBodyValueRowVO(editRow, getListView().getCurClassName());
		checkDataForTableType(getModel().getEditType(), curTabCode, vo);
		// end
		// ��ͬ�Ӽ�������У��
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() != RdsPsninfoModel.INSERT) {
			// ������������б���У��,����������ݵ�lastflag����YҲ�����б���У��
			// ����У��
			if (((PsnJobVO) vo).getLastflag().booleanValue()) {
				if (!validateBudget(getContext(), new PsnJobVO[] { selData.getParentVO().getPsnJobVO() }, new PsnJobVO[] { (PsnJobVO) vo })) {
					return;
				}
			}
		}
		// end
		selData.setTableVO(curTabCode, new SuperVO[] { vo });
		if (getModel().getEditType() == RdsPsninfoModel.ADD) {
			// fengwei 2012-12-24 �������ְ��¼�ڵ㣬
			// ���ǡ���Ա��ְ��¼��ҳǩ������ʾ���Ƿ�ͬʱͣ�õ�ǰ��Ա����ֻ����������ʾ
			if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
				int result =
						MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0206")/*
																													* @res
																													* "ȷ��ͣ��"
																													*/, ResHelper.getString("6009tran", "06009tran0185")/*
																																										 * @res
																																										 * "�Ƿ�ͬʱͣ�õ�ǰ��Ա��"
																																										 */);
				if (result == MessageDialog.ID_YES) {
					isdisablepsn = Boolean.TRUE;
				} else {
					isdisablepsn = Boolean.FALSE;
				}
			}

			// �ӱ����Ӻ󱣴�
			if (!saveAddData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
			// �ӱ��޸ĺ󱣴�
			if (!saveUpdateData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.INSERT) {
			// �ӱ����󱣴�
			if (!saveInsertData(selData)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
			// ��ְ�������
			if (!savePartchg(selData)) {
				return;
			}
		}
		getListView().setMainPanelEnabled(false);
		getListView().getBillListPanel().setEnabled(false);
		getModel().setEditType(RdsPsninfoModel.UNCHANGE);
		// ���ܵ�repaint()
		getListView().repaint();
		getModel().setUiState(UIState.NOT_EDIT);
	}

	private BillModel getCurBillModel() {
		return getListView().getBillListPanel().getBodyBillModel(curTabCode);
	}

	private IRdsManageService getIRdsService() {
		return NCLocator.getInstance().lookup(IRdsManageService.class);
	}

	@Override
	protected boolean isActionEnable() {

		if (getContext().getPk_org() == null) {
			return false;
		}
		return getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT;
	}

	private boolean saveAddData(PsndocAggVO aggVO, ActionEvent event) throws Exception {

		boolean isSynWork =
				(TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_PART.equals(curTabCode)) && UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																																																									* @res
																																																									* "ȷ��ͬ��"
																																																									*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																																																						 * @res
																																																																						 * "�Ƿ�ͬ������?"
																																																																						 */);

		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null && trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork =
						UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																																		* @res
																																		* "ȷ��ͬ��"
																																		*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																															 * @res
																																															 * "�Ƿ�ͬ������?"
																																															 */);
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// ��ְ��ְ����ʱҪ����ί�й�ϵ
			validateManageScope((PsnJobVO) aggVO.getTableVO(curTabCode)[0]);
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			// ����������¼
			// �Ƿ�ͬ������
			PsndocAggVO retVO = getIRdsService().addPsnjob(aggVO, curTabCode, isSynWork, getContext().getPk_org());
			Object obj = getModel().getTreeObject();
			String pkTreeObj = "";
			String pkPsn = "";
			if (obj != null && obj instanceof OrgVO) {
				pkTreeObj = ((OrgVO) obj).getPk_org();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_org();
			} else if (obj != null && obj instanceof DeptVO) {
				pkTreeObj = ((DeptVO) obj).getPk_dept();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_dept();
			}
			if (obj == null || pkPsn.equals(pkTreeObj)) {
				// ���ڵ������ͬ�����ڵ�
				setRetData(retVO, curTabCode);
			} else {
				// ���ӹ�����¼����֯���Ż���
				getModel().directlyDelete(aggVO);
			}
			// ���ӹ�����¼��Ҫˢ�½���
		} else if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			getIRdsService().addPsnjobDimission(aggVO, curTabCode, getContext().getPk_org(), isdisablepsn);
			// ������ְ��¼��Ҫˢ�½���
			getModel().directlyDelete(aggVO);
		} else {
			PsndocAggVO retVO = getIRdsService().addSubRecord(aggVO, curTabCode, isSynWork, getContext().getPk_org());
			setRetData(retVO, curTabCode);
		}
		return true;
	}

	private boolean saveInsertData(PsndocAggVO aggVO) throws Exception {

		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		PsndocAggVO retVO = getIRdsService().insertSubRecord(aggVO, curTabCode);
		setRetData(retVO, curTabCode);
		return true;
	}

	private boolean savePartchg(PsndocAggVO aggVO) throws BusinessException {

		boolean isSynWork =
				UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																																* @res
																																* "ȷ��ͬ��"
																																*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																													 * @res
																																													 * "�Ƿ�ͬ������?"
																																													 */);
		aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		PsndocAggVO retVO = getIRdsService().savePartchg(aggVO, curTabCode, isSynWork, getContext().getPk_org());
		setRetData(retVO, curTabCode);
		return true;
	}

	private boolean saveUpdateData(PsndocAggVO aggVO, ActionEvent event) throws Exception {

		boolean isSynWork =
				(TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_PART.equals(curTabCode)) && UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																																																									* @res
																																																									* "ȷ��ͬ��"
																																																									*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																																																						 * @res
																																																																						 * "�Ƿ�ͬ������?"
																																																																						 */);
		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null && trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork =
						UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																																		* @res
																																		* "ȷ��ͬ��"
																																		*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																															 * @res
																																															 * "�Ƿ�ͬ������?"
																																															 */);
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// ��ְ��ְ�޸�ʱҪ����ί�й�ϵ,ֻ���Ǳ�������һ����ʱ��
			PsnJobVO vo = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
			if (vo.getLastflag().booleanValue()) {
				validateManageScope(vo);
			}
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		PsndocAggVO retVO = getIRdsService().updateSubRecord(aggVO, curTabCode, isSynWork, getContext().getPk_org());
		if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode) || TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			Object obj = getModel().getTreeObject();
			String pkTreeObj = "";
			String pkPsn = "";
			if (obj != null && obj instanceof OrgVO) {
				pkTreeObj = ((OrgVO) obj).getPk_org();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_org();
			} else if (obj != null && obj instanceof DeptVO) {
				pkTreeObj = ((DeptVO) obj).getPk_dept();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_dept();
			}
			if (obj == null || pkPsn.equals(pkTreeObj)) {
				// ���ڵ������ͬ�����ڵ�
				setRetData(retVO, curTabCode);
			} else {
				// ���ӹ�����¼����֯���Ż���
				getModel().directlyDelete(aggVO);
			}
		} else {
			setRetData(retVO, curTabCode);
		}
		return true;
	}

}
