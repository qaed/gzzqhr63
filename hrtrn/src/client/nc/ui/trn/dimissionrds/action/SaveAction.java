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
																							 * "对界面数据进行保存(Ctrl+S)"
																							 */);
	}

	/**
	 * 校验开始日期合法型
	 * 
	 * @param saveData
	 * @param curRow
	 * @return boolean
	 */
	public boolean checkBegindate(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 后一条记录的开始日期
		UFLiteralDate nextRowBegindate = null;
		// 总行数
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
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/* @res "开始日期不能早于等于上一记录的开始日期！" */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/* @res "前一条记录的开始日期不能为空！" */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/* @res "开始日期不能早于等于上一记录的结束日期！" */);
		}
		if (endDate != null && nextRowBegindate != null && (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/* @res "结束日期不能晚于或等于下一记录的开始日期！" */);
		}
		// 该公司第一条任职记录 任职日期和此员工在其他公司的任职日期作比较，校验是否有交叠
		if (currow == 0 && TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			NCLocator.getInstance().lookup(IPersonRecordService.class).checkBeginDate(selData.getParentVO().getPsnJobVO(), (UFLiteralDate) saveData.getAttributeValue(PsnJobVO.BEGINDATE), null);
		}
		return true;
	}

	/**
	 * 检验记录间的开始结束日期
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
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0047")/* @res "进入日期不能为空！" */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0048")/* @res "开始日期不能为空！" */);
		}
		if (saveData.getAttributeValue("recordnum") != null && ((Integer) saveData.getAttributeValue("recordnum")).intValue() != 0 && enddate == null) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/* @res "离开日期不能为空！" */);
			}
			if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
				// 试用不判断结束日期
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/* @res "结束日期不能为空！" */);
			}
		}
		if (saveData.getAttributeValue("endflag") != null) {
			UFBoolean endflag = (UFBoolean) saveData.getAttributeValue("endflag");
			if (endflag.booleanValue() && enddate == null) {
				if (!TRNConst.Table_NAME_DIMISSION.equals(tabCode)) {
					if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/* @res "离开日期不能为空！" */);
					}
					if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
						// 试用不判断结束日期
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/* @res "结束日期不能为空！" */);
					}
				}
			}
		}
		if (enddate != null && begindate.afterDate(enddate)) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0051")/* @res "进入日期不能晚于离开日期！" */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0052")/* @res "开始日期不能晚于结束日期！" */);
		}
		if (TRNConst.Table_NAME_TRIAL.equals(tabCode)) {
			// 试用
			UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue("regulardate");
			if (regulardate != null && begindate.afterDate(regulardate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/* @res "开始日期不能晚于转正日期！" */);
			}
		}
		BillModel curBillModel = getListView().getBillListPanel().getBodyBillModel(getListView().getCurrentTabCode());
		int editRow = curBillModel.getEditRow();
		if (TRNConst.Table_NAME_PART.equals(strTabCode)) {
			// 兼职不校验记记录间的时间关系,只在兼职变更时校验两条记录的事件关系
			if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
				checkPartchg(saveData, editRow);
			} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
				// 修改兼职记录,特殊处理
				checkPartEdit(saveData, editRow);
			}
			return;
		}
		if (TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
			// 试用的字段名不一样单独校验
			checkTrial(saveData, editRow);
			return;
		}
		if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
			// 流动情况的字段名不一样单独校验
			checkPsnchg(saveData, editRow);
			return;
		}
		checkBegindate(saveData, editRow);
	}

	private void checkPartchg(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 总行数
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0054")/* @res "开始日期不能早于或等于上一记录的开始日期！" */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/* @res "前一条记录的开始日期不能为空！" */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0055")/* @res "开始日期不能早于或等于上一记录的结束日期！" */);
		}
	}

	private void checkPartEdit(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 后一条记录的开始日期
		UFLiteralDate nextRowBegindate = null;
		// 总行数
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount && saveData.getAttributeValue("lastflag") != null && !((UFBoolean) saveData.getAttributeValue("lastflag")).booleanValue()) {
			// 修改兼职变更的历史记录时修改校验下一条的开始时间
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
			if (endDate != null && nextRowBegindate != null && (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/* @res "结束日期不能晚于或等于下一记录的开始日期！" */);
			}
		}
		if (currow != 0) {
			// 得到上一条数据,如果上一条的lastflag='Y'则不与上一条的时间比较
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
																									 * "开始日期不能早于等于上一记录的开始日期！"
																									 */);
					} else if (preRowBegindate == null) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																									 * @res
																									 * "前一条记录的开始日期不能为空！"
																									 */);
					}
				}
				if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/* @res "开始日期不能早于等于上一记录的结束日期！" */);
				}
			}
		}
	}

	private void checkPsnchg(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		;
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 后一条记录的开始日期
		UFLiteralDate nextRowBegindate = null;
		// 总行数
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
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0056")/* @res "进入日期不能早于等于上一记录的进入日期！" */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0057")/* @res "前一条记录的进入日期不能为空！" */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0058")/* @res "进入日期不能早于等于上一记录的离开日期！" */);
		}
		if (endDate != null && nextRowBegindate != null && (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0059")/* @res "离开日期不能晚于或等于下一记录的进入日期！" */);
		}
	}

	private void checkTrial(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.BEGINDATE);
		UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.REGULARDATE);
		TrialVO trial = (TrialVO) saveData;
		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && regulardate == null) {
			if (trial.getTrialresult() != null && trial.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0060")/* @res "转正日期不能为空！" */);
			}
		}
		if (regulardate != null && beginDate.afterDate(regulardate)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/* @res "开始日期不能晚于转正日期！" */);
		}
		if (getModel().getEditType() == RdsPsninfoModel.INSERT && trial.getTrialresult() == null) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0061")/* @res "试用结果不能为空！" */);
		}
		// 20171206 tsy 去除“历史转正日期不能晚于最新工作记录的开始日期”,“开始日期不能早于最新工作记录的开始日期”的校验
		//// 最新工作记录的开始日期
		//		UFLiteralDate jobBeginDate = selData.getParentVO().getPsnJobVO().getBegindate();
		//		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && jobBeginDate != null && jobBeginDate.afterDate(beginDate)) {
		//			if (jobBeginDate != null && jobBeginDate.beforeDate(regulardate)) {
		//				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0208")/* @res "历史转正日期不能晚于最新工作记录的开始日期" */);
		//			}
		//		} else if (jobBeginDate != null && jobBeginDate.afterDate(beginDate)) {
		//			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0062")/* @res "开始日期不能早于最新工作记录的开始日期" */);
		//		}
		// 20171206 end
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		super.doAction(e);
		getListView().tableStopCellEditing(curTabCode);

		// 非空校验
		getListView().dataNotNullValidate();
		// 开始结束时间的校验
		int editRow = getCurBillModel().getEditRow();
		SuperVO vo =
				(SuperVO) getListView().getBillListPanel().getBodyBillModel(curTabCode).getBodyValueRowVO(editRow, getListView().getCurClassName());
		checkDataForTableType(getModel().getEditType(), curTabCode, vo);
		// end
		// 不同子集的特殊校验
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() != RdsPsninfoModel.INSERT) {
			// 插入操作不进行编制校验,被保存的数据的lastflag不是Y也不进行编制校验
			// 编制校验
			if (((PsnJobVO) vo).getLastflag().booleanValue()) {
				if (!validateBudget(getContext(), new PsnJobVO[] { selData.getParentVO().getPsnJobVO() }, new PsnJobVO[] { (PsnJobVO) vo })) {
					return;
				}
			}
		}
		// end
		selData.setTableVO(curTabCode, new SuperVO[] { vo });
		if (getModel().getEditType() == RdsPsninfoModel.ADD) {
			// fengwei 2012-12-24 如果是离职记录节点，
			// 且是“人员任职记录”页签，则提示“是否同时停用当前人员”，只有新增是提示
			if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
				int result =
						MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0206")/*
																													* @res
																													* "确认停用"
																													*/, ResHelper.getString("6009tran", "06009tran0185")/*
																																										 * @res
																																										 * "是否同时停用当前人员？"
																																										 */);
				if (result == MessageDialog.ID_YES) {
					isdisablepsn = Boolean.TRUE;
				} else {
					isdisablepsn = Boolean.FALSE;
				}
			}

			// 子表增加后保存
			if (!saveAddData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
			// 子表修改后保存
			if (!saveUpdateData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.INSERT) {
			// 子表插入后保存
			if (!saveInsertData(selData)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
			// 兼职变更保存
			if (!savePartchg(selData)) {
				return;
			}
		}
		getListView().setMainPanelEnabled(false);
		getListView().getBillListPanel().setEnabled(false);
		getModel().setEditType(RdsPsninfoModel.UNCHANGE);
		// 万能的repaint()
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
																																																									* "确认同步"
																																																									*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																																																						 * @res
																																																																						 * "是否同步履历?"
																																																																						 */);

		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null && trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork =
						UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																																		* @res
																																		* "确认同步"
																																		*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																															 * @res
																																															 * "是否同步履历?"
																																															 */);
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// 任职离职新增时要交验委托关系
			validateManageScope((PsnJobVO) aggVO.getTableVO(curTabCode)[0]);
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			// 新增工作记录
			// 是否同步履历
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
				// 根节点或是相同的树节点
				setRetData(retVO, curTabCode);
			} else {
				// 增加工作记录后组织或部门换了
				getModel().directlyDelete(aggVO);
			}
			// 增加工作记录后要刷新界面
		} else if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			getIRdsService().addPsnjobDimission(aggVO, curTabCode, getContext().getPk_org(), isdisablepsn);
			// 增加离职记录后要刷新界面
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
																																* "确认同步"
																																*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																													 * @res
																																													 * "是否同步履历?"
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
																																																									* "确认同步"
																																																									*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																																																						 * @res
																																																																						 * "是否同步履历?"
																																																																						 */);
		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null && trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork =
						UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																																		* @res
																																		* "确认同步"
																																		*/, ResHelper.getString("6009tran", "06009tran0063")/*
																																															 * @res
																																															 * "是否同步履历?"
																																															 */);
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// 任职离职修改时要交验委托关系,只有是保存最新一条的时候
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
				// 根节点或是相同的树节点
				setRetData(retVO, curTabCode);
			} else {
				// 增加工作记录后组织或部门换了
				getModel().directlyDelete(aggVO);
			}
		} else {
			setRetData(retVO, curTabCode);
		}
		return true;
	}

}
