package nc.bs.hrss.ta.monthreport.ctrl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hi.IPsndocQryService;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.WebContext;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.ViewModels;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author chouhl
 */
public class MonthReportForMngViewLeft extends IQueryController {
	private static final long serialVersionUID = 1L;
	// 字段
	public static final String FS_TBMYEAR = "tbmyear";
	public static final String FS_TBMMONTH = "tbmmonth";

	/**
	 * 设置默认值
	 */
	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		}
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
		WebContext context = LfwRuntimeEnvironment.getWebContext();

		Map<String, String[]> periodMap = TaAppContextUtil.getTBMPeriodVOMap();
		if (latestPeriodVO == null) {
			if (periodMap != null && periodMap.size() > 0) {
				String[] years = periodMap.keySet().toArray(new String[0]);
				if (!ArrayUtils.isEmpty(years)) {
					selRow.setValue(ds.nameToIndex(FS_TBMYEAR), years[0]);
				}
			} else {
				selRow.setValue(ds.nameToIndex(FS_TBMYEAR), null);
			}
		} else {
			selRow.setValue(ds.nameToIndex(FS_TBMYEAR), latestPeriodVO.getTimeyear());
		}

		try {
			String pk_monthstat = context.getWebSession().getOriginalParamMap().get("openBillId");//单据id
			MonthStatVO[] monthStatVOs =
					NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class).queryCurrentMonthDeptBypk(TaAppContextUtil.getHROrg(), pk_monthstat);
			if (monthStatVOs == null || monthStatVOs.length == 0 || monthStatVOs[0] == null) {
				return;
			}
			selRow.setValue(ds.nameToIndex(FS_TBMYEAR), monthStatVOs[0].getTbmyear());
			selRow.setValue(ds.nameToIndex(FS_TBMMONTH), monthStatVOs[0].getTbmmonth());

			PeriodVO periodVO =
					PeriodServiceFacade.queryByYearMonth(TaAppContextUtil.getHROrg(), monthStatVOs[0].getTbmyear(), monthStatVOs[0].getTbmmonth());
			TBMPsndocVO psndocVO =
					NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestByPsndocDate(TaAppContextUtil.getHROrg(), monthStatVOs[0].getPk_psndoc(), periodVO.getBegindate(), periodVO.getEnddate());
			PsnJobVO[] psnjobvos =
					NCLocator.getInstance().lookup(IPsndocQryService.class).queryPsnjobByPKs(new String[] { psndocVO.getPk_psnjob() });
			SessionUtil.getSessionBean().setIncludeSubDept(true);
			if (psnjobvos != null && psnjobvos[0] != null) {

				AppLifeCycleContext.current().getApplicationContext().addAppAttribute("pk_dept", psnjobvos[0].getPk_dept());
				//				if (!ArrayUtils.isEmpty(depts)) {
				//					for (HRDeptVO dept : depts) {
				//						if (dept.getPk_dept().equals(psnjobvos[0].getPk_dept())) {

				//							SessionUtil.setDefaultDeptToSession(psnjobvos[0].getPk_dept());
				//				SessionUtil.setCurrentDept(depts[0].getPk_dept(), true);
				//				SessionUtil.setDefaultDeptToSession(psnjobvos[0].getPk_dept());
				//							break;
				//						}

				//					}
				//				}
			}
			//			PeriodVO periodVO =
			//					PeriodServiceFacade.queryByYearMonth(TaAppContextUtil.getHROrg(), monthStatVOs[0].getTbmyear(), monthStatVOs[0].getTbmmonth());
			//		getModel().setPeriodVO(periodVO);

		} catch (Exception e) {
			int i = 0;
		}

	}

	@Override
	public void simpleValueChanged(DatasetCellEvent datasetCellEvent) {

		Dataset ds = datasetCellEvent.getSource();

		String tbmyear = null;
		String tbmmonth = null;
		try {
			WebContext context = LfwRuntimeEnvironment.getWebContext();
			String pk_monthstat = context.getWebSession().getOriginalParamMap().get("openBillId");//单据id
			MonthStatVO[] monthStatVOs =
					NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class).queryCurrentMonthDeptBypk(TaAppContextUtil.getHROrg(), pk_monthstat);
			if (monthStatVOs == null || monthStatVOs.length == 0 || monthStatVOs[0] == null) {
				return;
			}
			tbmyear = monthStatVOs[0].getTbmyear();
			tbmmonth = monthStatVOs[0].getTbmmonth();

			//			PeriodVO periodVO =
			//					PeriodServiceFacade.queryByYearMonth(TaAppContextUtil.getHROrg(), monthStatVOs[0].getTbmyear(), monthStatVOs[0].getTbmmonth());
			//		getModel().setPeriodVO(periodVO);

		} catch (Exception e) {
		}

		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(FS_TBMYEAR)) {// 年度发生变更
			return;
		}
		// 设置月份的默认选中值
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Row selRow = ds.getSelectedRow();
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();

		Map<String, String[]> periodMap = TaAppContextUtil.getTBMPeriodVOMap();
		String accyear = (String) datasetCellEvent.getNewValue();
		if (latestPeriodVO == null) {
			if (periodMap != null && periodMap.size() > 0) {
				String[] years = periodMap.keySet().toArray(new String[0]);
				if (!ArrayUtils.isEmpty(years)) {
					accyear = years[0];
					selRow.setValue(ds.nameToIndex(FS_TBMYEAR), accyear);
				}
			} else {
				selRow.setValue(ds.nameToIndex(FS_TBMYEAR), null);
			}
			// selRow.setValue(ds.nameToIndex(FS_TBMMONTH), null);
		} else {
			selRow.setValue(ds.nameToIndex(FS_TBMYEAR), latestPeriodVO.getTimeyear());
		}

		ComboData yearData = widget.getViewModels().getComboData("comb_tbmyear_value");
		String[] years = null;
		if (periodMap != null && periodMap.size() > 0) {
			years = periodMap.keySet().toArray(new String[0]);
			if (years != null && years.length > 1) {
				Arrays.sort(years);
				Collections.reverse(Arrays.asList(years));
			}
		}
		ComboDataUtil.addCombItemsAfterClean(yearData, years);
		ComboData monthData = widget.getViewModels().getComboData("comb_tbmmonth_value");
		String[] months = null;
		if (periodMap != null && periodMap.size() > 0) {
			months = periodMap.get(accyear);
		}
		ComboDataUtil.addCombItemsAfterClean(monthData, months);

		// 当通过审批进来时，直接使用处理单据进行period赋值
		if (StringUtils.isNotBlank(tbmmonth) && StringUtils.isNotBlank(tbmyear)) {
			selRow.setValue(ds.nameToIndex(FS_TBMYEAR), tbmyear);
			selRow.setValue(ds.nameToIndex(FS_TBMMONTH), tbmmonth);
			return;
		}

		if (ArrayUtils.isEmpty(months)) {
			selRow.setValue(ds.nameToIndex(FS_TBMMONTH), null);
			return;
		}
		if (latestPeriodVO != null && !StringUtils.isEmpty(latestPeriodVO.getTimeyear()) && latestPeriodVO.getTimeyear().equals(accyear)) {
			String accmonth = TaAppContextUtil.getLatestPeriodVO().getTimemonth();
			for (String month : months) {
				if (month.equals(accmonth)) {
					selRow.setValue(ds.nameToIndex(FS_TBMMONTH), accmonth);
					break;
				}
			}
		} else {
			selRow.setValue(ds.nameToIndex(FS_TBMMONTH), months[0]);
		}
	}

	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
	}

}
