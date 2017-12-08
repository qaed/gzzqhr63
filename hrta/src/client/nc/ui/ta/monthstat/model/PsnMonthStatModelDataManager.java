package nc.ui.ta.monthstat.model;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.funcnode.ui.FuncletInitData;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.itf.ta.PeriodServiceFacade;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.daystat.model.IShowNoDataRecordManager;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.hr.pub.FormatVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.TALoginContext;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("restriction")
public class PsnMonthStatModelDataManager implements IAppModelDataManager, IShowNoDataRecordManager, IQueryInfo, IPaginationModelListener {

	private PsnMonthStatAppModel model;
	private PsnMonthStatPFAppModel psnModel;
	private boolean showNoDataRecord;
	private FromWhereSQL fromWhereSQL;
	private PeriodVO periodVO;
	protected int iQueryDataCount = 0;

	private PaginationModel paginationModel;

	public PsnMonthStatModelDataManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initModel() {

		Object initData = getModel().getContext().getInitData();
		if (initData != null && initData instanceof FuncletInitData) {
			Object data = ((FuncletInitData) initData).getInitData();
			if (data != null && data instanceof PfLinkData) {
				// 审批时初始化数据：context设置为单据的pk_org
				try {
					String pk_monthstat = ((PfLinkData) data).getBillID();
					MonthStatVO[] monthStatVOs =
							NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class).queryCurrentMonthDeptBypk(((PfLinkData) data).getPkOrg(), pk_monthstat);
					if (monthStatVOs == null || monthStatVOs.length == 0 || monthStatVOs[0] == null) {
						return;
					}
					PeriodVO periodVO =
							PeriodServiceFacade.queryByYearMonth(((PfLinkData) data).getPkOrg(), monthStatVOs[0].getTbmyear(), monthStatVOs[0].getTbmmonth());
					getModel().setPeriodVO(periodVO);
					this.setPeriodVO(periodVO);
					getModel().recreateColumn();
					getModel().getContext().setPk_org(((PfLinkData) data).getPkOrg());
					String[] pks = new String[monthStatVOs.length];
					for (int i = 0; i < monthStatVOs.length; i++) {
						pks[i] = monthStatVOs[i].getPk_psndoc() + "," + monthStatVOs[i].getPk_org();
					}
					getPaginationModel().setObjectPks(pks);
					getModel().initData(monthStatVOs);
					List<AggMonthStatVO> aggvos = new ArrayList<AggMonthStatVO>();
					if (data != null) {
						for (MonthStatVO monthStatVO : monthStatVOs) {
							AggMonthStatVO aggvo = new AggMonthStatVO();
							aggvo.setParentVO(monthStatVO);
							aggvos.add(aggvo);
						}
					}
					psnModel.initModel(aggvos.toArray(new AggMonthStatVO[0]));
					return;
					//					getOrgPanel().getRefPane().setEnabled(false);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		//非审批时
		try {
			getPaginationModel().setObjectPks(null);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
		}
		//		model.initModel(null);
		//		model.setData(null);
		model.initData(null);
		model.recreateColumn();
		psnModel.initModel(null);
	}

	@Override
	public boolean isShowNoDataRecord() {
		return showNoDataRecord;
	}

	@Override
	public void refresh() {
		model.setPeriodVO(periodVO);
		if (periodVO == null)
			return;
		MonthStatVO[] data = null;
		try {
			if (fromWhereSQL == null) {//第一次进入界面或者在审批界面
				MonthStatVO[] oldvos = model.getData();
				if (oldvos != null && oldvos.length > 0) {
					String[] pks = getPaginationModel().getObjectPks().toArray(new String[0]);
					data = ((IMonthStatQueryMaintain)NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class)).queryByCondition(getModel().getContext(), pks, getPeriodVO().getTimeyear(), getPeriodVO().getTimemonth(), isShowNoDataRecord());
				}
			} else {
				data =
						NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class).queryByCondition(getModel().getContext(), fromWhereSQL, periodVO.getTimeyear(), periodVO.getTimemonth(), showNoDataRecord);
			}

			//月报可能还有空记录的，它没有主键，使用人员代替,因为管理组织的问题，人员主键也不是唯一的了，改用人员+组织
			//			getPaginationModel().setObjectPks(StringPiecer.getStrArray(data, MonthStatVO.PK_PSNDOC));
			if (ArrayUtils.isEmpty(data)) {
				getPaginationModel().setObjectPks(null);
			} else {
				String[] pks = new String[data.length];
				for (int i = 0; i < data.length; i++) {
					pks[i] = data[i].getPk_psndoc() + "," + data[i].getPk_org();
				}
				getPaginationModel().setObjectPks(pks);
			}
			//			model.setData(data);
			model.initData(data);
			List<AggMonthStatVO> aggvos = new ArrayList<AggMonthStatVO>();
			if (data != null) {
				for (MonthStatVO monthStatVO : data) {
					AggMonthStatVO aggvo = new AggMonthStatVO();
					aggvo.setParentVO(monthStatVO);
					aggvos.add(aggvo);
				}
			}
			psnModel.initModel(aggvos.toArray(new AggMonthStatVO[0]));
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			ShowStatusBarMsgUtil.showErrorMsg(e.getMessage(), null, getModel().getContext());
			return;
		}
		iQueryDataCount = ArrayUtils.getLength(data);
		//		model.initModel(data);

	}

	public PaginationModel getPaginationModel() {
		return paginationModel;
	}

	public void setPaginationModel(PaginationModel paginationModel) {
		this.paginationModel = paginationModel;
		paginationModel.addPaginationModelListener(this);
		paginationModel.setPageSize(((TALoginContext) getModel().getContext()).getPaginationSize());
		paginationModel.setMaxPageSize(ICommonConst.MAX_ROW_PER_PAGE);
		paginationModel.init();
	}

	@Override
	public void setShowNoDataRecord(boolean isShowNoDataRecord) {
		this.showNoDataRecord = isShowNoDataRecord;
	}

	public PsnMonthStatAppModel getModel() {
		return model;
	}

	public void setModel(PsnMonthStatAppModel model) {
		this.model = model;
	}

	public PeriodVO getPeriodVO() {
		return periodVO;
	}

	public void setPeriodVO(PeriodVO periodVO) {
		this.periodVO = periodVO;
	}

	public void initModelByFromWhereSQL(FromWhereSQL fromWhereSQL, PeriodVO periodVO) {
		this.fromWhereSQL = fromWhereSQL;
		this.periodVO = periodVO;
		refresh();
	}

	public FromWhereSQL getFromWhereSQL() {
		return fromWhereSQL;
	}

	public void setFromWhereSQL(FromWhereSQL fromWhereSQL) {
		this.fromWhereSQL = fromWhereSQL;
	}

	@Override
	public int getQueryDataCount() {
		return iQueryDataCount;
	}

	@Override
	public void onDataReady() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStructChanged() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return psnModel
	 */
	public PsnMonthStatPFAppModel getPsnModel() {
		return psnModel;
	}

	/**
	 * @param psnModel 要设置的 psnModel
	 */
	public void setPsnModel(PsnMonthStatPFAppModel psnModel) {
		this.psnModel = psnModel;
	}

}
