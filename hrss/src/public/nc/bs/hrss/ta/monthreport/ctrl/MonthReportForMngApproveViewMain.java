package nc.bs.hrss.ta.monthreport.ctrl;

import java.awt.MenuItem;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.cmd.PFUnApproveCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.pf.ctrl.WebBillApproveView;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.monthreport.MonthReportUtils;
import nc.bs.hrss.ta.monthreport.cmd.MonthstatPFApproveInfoCmd;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.admin.IConfigurationService;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.OpenProperties;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.wfm.constant.WfmConstants;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.DeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class MonthReportForMngApproveViewMain extends WebBillApproveView {
	private static final long serialVersionUID = 1L;
	public static final String DATASET_ID = "dsMonthReport";
	public static final String PAGE_MTH_RPT_DTL_WIDGET = "MonthReportDetail";
	public static final String PAGE_MTH_RPT_WIDGET = "MonthReport";
	public static final String PAGE_UNGENERATE_PSN = "unGeneratePsn";
	public static final String PARAM_ID_NODATA = "showNoDataRecord";
	public static final String PARAM_ID_YEAR = "tbmyear";
	public static final String PARAM_ID_MONTH = "tbmmonth";
	public static final String PARAM_ID_PSNDOC = "psndoc";
	public static final String PARAM_ID_FWSQL = "fromWhereSql";
	public static final String PARAM_ID_TBMYEAR = "tbmyear";
	public static final String PARAM_ID_TBMMONTH = "tbmmonth";
	public static final String PARAM_ID_PK_PSNDOC = "pk_psndoc";
	public static final String PARAM_ID_DEPT = "pk_dept";
	public static final String PARAM_ID_DEPTNAME = "deptName";
	public static final String PLUGIN_PARAM_ID = "qryout";
	public static final String PAGE_QUERY_WIDGET = "pubview_simplequery";
	public static final String SESSION_DATE_CHANGE = "isDateChange";
	private static final String WFMTASKQRY = "wfmtaskqry";

	public MonthReportForMngApproveViewMain() {
	}

	public void onMonthReportDataLoad(DataLoadEvent dataLoadEvent) {
		ApplicationContext app = getLifeCycleContext().getApplicationContext();
		Dataset ds = (Dataset) dataLoadEvent.getSource();
		if (isPagination(ds)) {
			boolean containsSubDepts = SessionUtil.isIncludeSubDept();
			String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute("pk_dept");
			String tbmyear = (String) getLifeCycleContext().getApplicationContext().getAppAttribute("tbmyear");
			String tbmmonth = (String) getLifeCycleContext().getApplicationContext().getAppAttribute("tbmmonth");
			UFBoolean showNoDataRecord = (UFBoolean) getLifeCycleContext().getApplicationContext().getAppAttribute("showNoDataRecord");
			boolean noDataRecord = showNoDataRecord.booleanValue();
			nc.ui.querytemplate.querytree.FromWhereSQLImpl fromWhereSql =
					(nc.ui.querytemplate.querytree.FromWhereSQLImpl) getLifeCycleContext().getApplicationContext().getAppAttribute("fromWhereSql");
			MonthReportUtils.resetData(ds, pk_dept, fromWhereSql, tbmyear, tbmmonth, containsSubDepts, noDataRecord);
			ButtonStateManager.updateButtons();
		} else {
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}

	public void pluginDeptChange(Map<String, Object> keys) {
		ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
		appCxt.addAppAttribute("isDateChange", UFBoolean.TRUE);

		TaAppContextUtil.addTaAppContext();
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext("pubview_simplequery").getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");

		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			String old_year = null;
			String accyear = null;
			if (dsSearch.nameToIndex("tbmyear") > -1) {
				old_year = selRow.getString(dsSearch.nameToIndex("tbmyear"));
				if (latestPeriodVO != null) {
					selRow.setValue(dsSearch.nameToIndex("tbmyear"), latestPeriodVO.getTimeyear());
				} else if ((periodMap != null) && (periodMap.size() > 0)) {
					String[] years = (String[]) periodMap.keySet().toArray(new String[0]);
					if (!ArrayUtils.isEmpty(years)) {
						accyear = years[0];
						selRow.setValue(dsSearch.nameToIndex("tbmyear"), accyear);
					}
				} else {
					selRow.setValue(dsSearch.nameToIndex("tbmyear"), null);
				}

				accyear = selRow.getString(dsSearch.nameToIndex("tbmyear"));
			}

			if ((accyear != null) && (accyear.equals(old_year))) {
				ComboData monthData = simpQryView.getViewModels().getComboData("comb_tbmmonth_value");
				String[] months = null;
				if ((periodMap != null) && (periodMap.size() > 0)) {
					months = (String[]) periodMap.get(accyear);
				}
				ComboDataUtil.addCombItemsAfterClean(monthData, months);
				if (ArrayUtils.isEmpty(months)) {
					selRow.setValue(dsSearch.nameToIndex("tbmmonth"), null);
					return;
				}
				if ((latestPeriodVO != null) && (!StringUtils.isEmpty(latestPeriodVO.getTimeyear())) && (latestPeriodVO.getAccyear().equals(accyear))) {
					String accmonth = latestPeriodVO.getTimemonth();
					for (String month : months) {
						if (month.equals(accmonth)) {
							selRow.setValue(dsSearch.nameToIndex("tbmmonth"), accmonth);
							break;
						}
					}
				} else {
					selRow.setValue(dsSearch.nameToIndex("tbmmonth"), months[0]);
				}
			} else if ((accyear == null) && (old_year == null)) {
				String[] years = null;
				ComboData yearData = simpQryView.getViewModels().getComboData("comb_tbmyear_value");
				if ((periodMap != null) && (periodMap.size() > 0)) {
					years = (String[]) periodMap.keySet().toArray(new String[0]);
					if ((years != null) && (years.length > 1)) {
						Arrays.sort(years);
						Collections.reverse(Arrays.asList(years));
					}
				}
				ComboDataUtil.addCombItemsAfterClean(yearData, years);
			}
		}

		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	public void pluginSearch(Map<String, Object> keys) {
		TBMPsndocUtil.checkTimeRuleVO();

		LfwView viewMain = getCurrentActiveView();
		if (viewMain == null) {
			return;
		}
		Dataset ds = viewMain.getViewModels().getDataset("dsMonthReport");
		if (ds == null) {
			return;
		}

		DatasetUtil.clearData(ds);
		ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		if ((keys == null) || (keys.size() == 0)) {
			return;
		}

		String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute("pk_dept");

		boolean containsSubDepts = SessionUtil.isIncludeSubDept();

		nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get("whereSqlTable");

		nc.ui.querytemplate.querytree.FromWhereSQLImpl fromWhereSQL =
				(nc.ui.querytemplate.querytree.FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);

		try {
			String psnScopeSqlPart = getDeptPsnCondition();
			if ((fromWhereSQL != null) && (!StringUtils.isEmpty(psnScopeSqlPart))) {
				fromWhereSQL.setWhere(fromWhereSQL.getWhere() + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
			}
		} catch (BusinessException e) {
		}

		String year = null;

		String month = null;

		UFBoolean showNoDataRecord = null;
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext("pubview_simplequery").getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row row = dsSearch.getSelectedRow();
			year = row.getString(dsSearch.nameToIndex("tbmyear"));
			month = row.getString(dsSearch.nameToIndex("tbmmonth"));
			showNoDataRecord = (UFBoolean) row.getValue(dsSearch.nameToIndex("showNoDataRecord"));
			if (showNoDataRecord == null) {
				showNoDataRecord = UFBoolean.FALSE;
			}
		}

		if (StringUtil.isEmptyWithTrim(year)) {
			MonthReportUtils.removeCol(viewMain);
			CommonUtil.showErrorDialog(NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0168"), NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0143"));

			return;
		}
		if (StringUtil.isEmptyWithTrim(month)) {
			MonthReportUtils.removeCol(viewMain);
			CommonUtil.showErrorDialog(NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0168"), NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0144"));

			return;
		}

		getLifeCycleContext().getApplicationContext().addAppAttribute("pk_dept", pk_dept);
		getLifeCycleContext().getApplicationContext().addAppAttribute("fromWhereSql", fromWhereSQL);
		getLifeCycleContext().getApplicationContext().addAppAttribute("tbmyear", year);
		getLifeCycleContext().getApplicationContext().addAppAttribute("tbmmonth", month);
		getLifeCycleContext().getApplicationContext().addAppAttribute("showNoDataRecord", showNoDataRecord);

		String pk_hrorg = SessionUtil.getHROrg();
		MonthReportUtils.buildDsAndGrid(viewMain, pk_hrorg);

		ButtonStateManager.updateButtons();

		MonthReportUtils.resetData(ds, pk_dept, fromWhereSQL, year, month, containsSubDepts, showNoDataRecord.booleanValue());
	}

	public void showMthReportDetail(ScriptEvent scriptEvent) {
		String rowId = getLifeCycleContext().getParameter("dsMain_rowId");
		String dsId = getLifeCycleContext().getParameter("dsMain_id");
		Dataset ds = getLifeCycleContext().getViewContext().getView().getViewModels().getDataset(dsId);
		Row selRow = ds.getRowById(rowId);
		if (selRow == null) {
			return;
		}
		String pk_psndoc = (String) selRow.getValue(ds.nameToIndex("pk_psndoc"));
		String tbmyear = (String) selRow.getValue(ds.nameToIndex("tbmyear"));
		String tbmmonth = (String) selRow.getValue(ds.nameToIndex("tbmmonth"));
		getLifeCycleContext().getApplicationContext().addAppAttribute("pk_psndoc", pk_psndoc);
		getLifeCycleContext().getApplicationContext().addAppAttribute("tbmyear", tbmyear);
		getLifeCycleContext().getApplicationContext().addAppAttribute("tbmmonth", tbmmonth);
		CommonUtil.showViewDialog("MonthReportDetail", NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0093"), DialogSize.TINY);
	}

	public void showUnGenerate(MouseEvent<MenuItem> mouseEvent) {
		CommonUtil.showViewDialog("unGeneratePsn", NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0094"), DialogSize.LARGE.getWidth(), 610);
	}

	/**
	 * 审批
	 * 
	 * @param mouseEvent
	 */
	public void onApprove(MouseEvent mouseEvent) {
		String state = AppLifeCycleContext.current().getParameter("state");
		WindowContext contex = AppLifeCycleContext.current().getApplicationContext().getCurrentWindowContext();
		HashMap<String, String> map = new HashMap();
		map.put("state", "State_Run");

		OpenProperties openProperties = new OpenProperties();
		openProperties.setOpenId("taskoperate");//taskoperate  AppLifeCycleContext.current().getViewContext().getId()
		openProperties.setWidth("502");
		openProperties.setHeight("234");
		openProperties.setTitle(NCLangRes4VoTransl.getNCLangRes().getStrByID("pint", "MainViewController-000008"));
		openProperties.setParamMap(map);
		contex.popView(openProperties);

	}

	/**
	 * 退回审批
	 * 
	 * @param mouseEvent
	 */
	public void onUnApprove(MouseEvent mouseEvent) {
		Dataset ds = this.getLifeCycleContext().getViewContext().getView().getViewModels().getDataset(DATASET_ID);

		Row row = ds.getAllRow()[0];
		String billType = (String) AppUtil.getAppAttr("billTypeCode");
		String billPk = row.getString(ds.nameToIndex("srcid"));
		AggregatedValueObject aggVO = getBillVOByPk(billType, billPk);
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_FormInFoCtx, aggVO);
		LfwRuntimeEnvironment.getWebContext().getPageMeta().setHasChanged(false);

		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_ExeAction, "agree");
		CmdInvoker.invoke(new PFUnApproveCmd());
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		AppInteractionUtil.showMessageDialog("任务完成", false);
	}

	/**
	 * 审批情况
	 * 
	 * @param mouseEvent
	 */
	public void onApproveInfo(MouseEvent mouseEvent) {

		Dataset ds = this.getLifeCycleContext().getViewContext().getView().getViewModels().getDataset(DATASET_ID);

		Row row = ds.getAllRow()[0];
		String billType = (String) AppUtil.getAppAttr("billTypeCode");
		String billPk = row.getString(ds.nameToIndex("srcid"));
		AggregatedValueObject aggVO = getBillVOByPk(billType, billPk);
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_FormInFoCtx, aggVO);
		LfwRuntimeEnvironment.getWebContext().getPageMeta().setHasChanged(false);
		CmdInvoker.invoke(new MonthstatPFApproveInfoCmd(DATASET_ID, AggMonthStatVO.class));
	}

	public AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	private LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}

	/** 
	* 获得当前系统编码
	* @return
	*/
	public String getSystemCode() {
		Dataset ds = getCurrentView().getViewModels().getDataset("ds_task");
		Row row = ds.getSelectedRow();
		if (row != null) {
			String sys = row.getString(ds.nameToIndex("sysext3"));
			if (sys != null)
				return sys;
		}

		return WFMTASKQRY;
	}

	public String getDeptPsnCondition() throws BusinessException {
		String curDeptPk = null;
		String curDeptMgrPsndoc = null;
		boolean isIncludeChief = UFBoolean.FALSE.booleanValue();
		boolean isIncludeOtherMgr = UFBoolean.FALSE.booleanValue();
		try {
			IConfigurationService configurationService =
					(IConfigurationService) nc.bs.hrss.pub.ServiceLocator.lookup(IConfigurationService.class);
			curDeptPk = (String) getLifeCycleContext().getApplicationContext().getAppAttribute("pk_dept");

			curDeptMgrPsndoc = SessionUtil.getPk_psndoc();
			isIncludeChief = configurationService.canViewMaster() == null ? false : configurationService.canViewMaster().booleanValue();
			isIncludeOtherMgr =
					configurationService.canViewEachOther() == null ? false : configurationService.canViewEachOther().booleanValue();
		} catch (HrssException e) {
			e.alert();
		}

		DeptVO dept =
				(DeptVO) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByPk(null, DeptVO.class, curDeptPk);

		String deptSql = " select pk_dept from org_dept where innercode like '" + dept.getInnercode() + "%' ";

		String sql =
				"select distinct bd_psndoc.pk_psndoc from bd_psndoc bd_psndoc  inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc  inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg  where hi_psnjob.pk_dept in ( " + deptSql + " )";

		int i =
				((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).getCountByCondition("org_dept", " pk_dept = '" + curDeptPk + "' and principal = '" + curDeptMgrPsndoc + "' ");

		if (i > 0) {
			return sql;
		}

		if (!isIncludeChief) {
			String sql1 = " select principal from org_dept where pk_dept in (" + deptSql + ") ";
			sql = sql + " and bd_psndoc.pk_psndoc not in ( " + sql1 + " ) ";
		}

		if (!isIncludeOtherMgr) {
			String sql1 = " select principal from org_dept where pk_dept in (" + deptSql + ") ";
			String sql2 =
					" select pk_psndoc from org_orgmanager where pk_dept in ( " + deptSql + " ) and pk_psndoc <> '" + curDeptMgrPsndoc + "' and pk_psndoc not in (" + sql1 + " ) ";

			sql = sql + " and bd_psndoc.pk_psndoc not in ( " + sql2 + " ) ";
		}

		return sql;
	}
}