package nc.impl.ta.daystat;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.lock.PKLock;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hr.devitf.IDevItfQueryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.hrss.IURLGenerator;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.ta.AwayServiceFacade;
import nc.itf.ta.CheckTimeServiceFacade;
import nc.itf.ta.IDayStatManageMaintain;
import nc.itf.ta.IDayStatQueryMaintain;
import nc.itf.ta.IHRHolidayManageService;
import nc.itf.ta.IItemQueryService;
import nc.itf.ta.ILateEarlyQueryService;
import nc.itf.ta.IOvertimeManageService;
import nc.itf.ta.IPeriodManageService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryService;
import nc.itf.ta.LeaveServiceFacade;
import nc.itf.ta.OverTimeServiceFacade;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.ShutdownServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DataFilterUtils;
import nc.itf.ta.algorithm.ICheckTime;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillType;
import nc.itf.ta.algorithm.SolidifyUtils;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.customization.IDayDataCreator;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.pubitf.rbac.IUserPubService;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hr.hrss.SSOInfo;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
import nc.vo.ta.algorithm.SolidifyPara;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.customization.DayCalParam;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.daystat.DayStatVO;
import nc.vo.ta.daystat.DayStatbVO;
import nc.vo.ta.daystat.DaystatImportParam;
import nc.vo.ta.daystat.DeptDayStatVO;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.item.DatePeriodFormulaUtils;
import nc.vo.ta.item.HolidayFormulaUtils;
import nc.vo.ta.item.ItemCopyVO;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.item.OverTimeFormulaUtils;
import nc.vo.ta.item.PreviousDayFormulaUtils;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.PsnInSQLDateScope;
import nc.vo.ta.pub.SQLParamWrapper;
import nc.vo.ta.shutdown.ShutdownRegVO;
import nc.vo.ta.statistic.IVOWithDynamicAttributes;
import nc.vo.ta.statistic.annotation.ItemClass;
import nc.vo.ta.timeitem.AwayTypeCopyVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.ShutDownTypeCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DayStatMaintainImpl implements IDayStatManageMaintain, IDayStatQueryMaintain {
	private Map<String, String[]> hashDeptFather = null;
	private BaseDAO dao;
	{
		this.hashDeptFather = new HashMap<String, String[]>();
	}

	public DayStatMaintainImpl() {
	}

	public void generate_RequiresNew(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		generate(pk_org, fromWhereSQL, beginDate, endDate);
	}

	public void generate(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if (!"#UAP#".equalsIgnoreCase(PubEnv.getPk_user())) {
			fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", "Edit", fromWhereSQL);
		}
		String[] pk_psndocs =
				NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(pk_psndocs)) {
			return;
		}

		generateBatch(pk_org, pk_psndocs, beginDate, endDate);
	}

	public void generate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		generateBatch(pk_org, pk_psndocs, beginDate, endDate);
	}

	private void generateBatch(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if ((ArrayUtils.isEmpty(pk_psndocs)) || (StringUtils.isEmpty(pk_org)) || (beginDate.afterDate(endDate)))
			return;
		int length = pk_psndocs.length;
		if (length < 1000) {
			generate4Once(pk_org, pk_psndocs, beginDate, endDate);
			return;
		}
		List<String> psnList = new ArrayList();
		int count = 0;
		for (int i = 0; i < length; i++) {
			count++;
			psnList.add(pk_psndocs[i]);
			if (count >= 999) {
				generate4Once(pk_org, (String[]) psnList.toArray(new String[0]), beginDate, endDate);
				count = 0;
				psnList.clear();
			}
		}
		generate4Once(pk_org, (String[]) psnList.toArray(new String[0]), beginDate, endDate);
	}

	private void generate4Once(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if ((ArrayUtils.isEmpty(pk_psndocs)) || (StringUtils.isEmpty(pk_org)) || (beginDate.afterDate(endDate))) {
			return;
		}
		FromWhereSQL powerWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", "DayStatGenerate", null);
		String[] powerPks =
				NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org, powerWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(powerPks))
			return;
		List<String> pkList = new ArrayList();
		for (int i = 0; i < powerPks.length; i++) {
			for (int j = 0; j < pk_psndocs.length; j++) {
				if (powerPks[i].equals(pk_psndocs[j]))
					pkList.add(pk_psndocs[j]);
			}
		}
		pk_psndocs = CollectionUtils.isEmpty(pkList) ? null : (String[]) pkList.toArray(new String[0]);

		if (ArrayUtils.isEmpty(pk_psndocs)) {
			return;
		}
		PKLock lock = PKLock.getInstance();
		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
		for (int i = 0; i < pk_psndocs.length; i++) {
			pk_psndocsLockacble[i] = ("daystat" + pk_org + pk_psndocs[i]);
		}
		InSQLCreator isc = new InSQLCreator();
		try {
			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
			if (!acquired) {
				throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0089"));
			}
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			DayStatServiceImpl serviceImpl = new DayStatServiceImpl();

			serviceImpl.createDayStatRecord(pk_org, fromWhereSQL, beginDate, endDate);
			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);

			generate0(pk_org, pk_psndocs, beginDate, endDate);

			PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
			psnInSql.setPk_org(pk_org);
			psnInSql.setPsndocInSQL(isc.getInSQL(pk_psndocs));
			psnInSql.setBeginDate(beginDate);
			psnInSql.setEndDate(endDate);
			EventDispatcher.fireEvent(new BusinessEvent("e53b4c0c-5fa2-42ee-8cd7-d6bd3a2e5b6f", "2001", psnInSql));
		} finally {
			lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
		}
	}

	private void generate0(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		DayCalParam dayCalParam = DayStatCalculationHelper.createPara(pk_org);
		InSQLCreator isc = new InSQLCreator();
		try {
			CalSumParam calSumParam = prepareCalSumParam(pk_org, pk_psndocs, beginDate, endDate, isc, dayCalParam);

			processBills(calSumParam);

			IHRHolidayManageService holidaysService = NCLocator.getInstance().lookup(IHRHolidayManageService.class);
			if (calSumParam.containsHolidayFunc)
				holidaysService.createEnjoyDetail(pk_org, pk_psndocs, beginDate.getDateBefore(1095), endDate.getDateAfter(1095));
			IPeriodManageService periodService = NCLocator.getInstance().lookup(IPeriodManageService.class);
			if (calSumParam.containsDatePeriodVar) {
				periodService.createDatePeriod(pk_org, beginDate, endDate);
			}

			processItems(calSumParam);
			if (calSumParam.containsHolidayFunc)
				holidaysService.clearEnjoyDetail();
			if (calSumParam.containsDatePeriodVar) {
				periodService.clearDatePeriod();
			}

			if (calSumParam.containsOvertimeBeginEndTimeVar) {
				IOvertimeManageService otservice = NCLocator.getInstance().lookup(IOvertimeManageService.class);
				otservice.clearOvertimeBelongData();
			}

			sendMessage(pk_org, pk_psndocs, calSumParam, beginDate, endDate);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	private CalSumParam prepareCalSumParam(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, InSQLCreator isc, DayCalParam dayCalParam)
			throws BusinessException {
		CalSumParam calSumParam = new CalSumParam();
		calSumParam.dayCalParam = dayCalParam;
		calSumParam.pk_org = pk_org;
		calSumParam.pk_psndocs = pk_psndocs;
		calSumParam.psndocInSQL = isc.getInSQL(pk_psndocs);
		calSumParam.containsPreviousDayFunc = PreviousDayFormulaUtils.existsPreviousItem(dayCalParam.itemVOs);
		calSumParam.containsHolidayFunc = HolidayFormulaUtils.existsHolidayItem(dayCalParam.itemVOs);
		calSumParam.containsDatePeriodVar = DatePeriodFormulaUtils.existsDatePeriodItem(dayCalParam.itemVOs);
		calSumParam.containsOvertimeBeginEndTimeVar = OverTimeFormulaUtils.existsBeginOrEndTimeItem(dayCalParam.itemVOs);

		calSumParam.timeRuleVO = dayCalParam.timeruleVO;
		calSumParam.paramValues = dayCalParam.paramValues;
		if (dayCalParam.leaveItemMap != null) {
			calSumParam.leaveCopyVOs = ((LeaveTypeCopyVO[]) dayCalParam.leaveItemMap.values().toArray(new LeaveTypeCopyVO[0]));
		}
		if (dayCalParam.awayItemMap != null) {
			calSumParam.awayCopyVOs = ((AwayTypeCopyVO[]) dayCalParam.awayItemMap.values().toArray(new AwayTypeCopyVO[0]));
		}
		if (dayCalParam.overtimeItemMap != null) {
			calSumParam.overCopyVOs = ((OverTimeTypeCopyVO[]) dayCalParam.overtimeItemMap.values().toArray(new OverTimeTypeCopyVO[0]));
		}
		if (dayCalParam.shutdownItemMap != null) {
			calSumParam.shutCopyVOs = ((ShutDownTypeCopyVO[]) dayCalParam.shutdownItemMap.values().toArray(new ShutDownTypeCopyVO[0]));
		}
		calSumParam.dayItemVOs = dayCalParam.itemVOs;
		UFLiteralDate[] allUFDates = CommonUtils.createDateArray(beginDate, endDate, 2, 2);
		calSumParam.allDates = allUFDates;
		calSumParam.dateBeginIndex = 2;
		calSumParam.dateEndIndex = (allUFDates.length - 3);

		Map<String, String[]> datePeriodMap = new HashMap();
		processPeriodAndNextPeriodOfDate(pk_org, CommonUtils.createDateArray(allUFDates[0].getDateBefore(300), allUFDates[(allUFDates.length - 1)].getDateAfter(300)), datePeriodMap);
		calSumParam.datePeriodMap = datePeriodMap;

		boolean existsJavaItem = false;
		for (ItemCopyVO itemVO : calSumParam.dayItemVOs) {
			if (itemVO.getSrc_flag().intValue() == 2) {
				existsJavaItem = true;
				break;
			}
		}

		long time = System.currentTimeMillis();
		calSumParam.daystatPKMap =
				DayStatCalculationHelper.getDaystatPKMap(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate.toString(), endDate.toString());

		Logger.debug("构造日报主键map耗时：" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();

		calSumParam.calendarMap =
				NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(calSumParam.pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1), calSumParam.psndocInSQL);

		time = System.currentTimeMillis();

		calSumParam.aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		if (!MapUtils.isEmpty(calSumParam.aggShiftMap)) {
			calSumParam.shiftMap = new HashMap();
			for (String key : calSumParam.aggShiftMap.keySet()) {
				calSumParam.shiftMap.put(key, ((AggShiftVO) calSumParam.aggShiftMap.get(key)).getShiftVO());
			}
		}

		calSumParam.tbmPsndocMap =
				NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocMapByPsndocInSQL(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate, true, true);

		calSumParam.allDateOrgMap = TBMPsndocVO.createDateOrgMapByTbmPsndocVOMap(calSumParam.tbmPsndocMap, beginDate, endDate);

		time = System.currentTimeMillis();
		if (existsJavaItem) {

			calSumParam.timedataMap =
					NCLocator.getInstance().lookup(ITimeDataQueryService.class).queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate, endDate, calSumParam.psndocInSQL);

			calSumParam.lateearlyMap =
					NCLocator.getInstance().lookup(ILateEarlyQueryService.class).queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate, endDate, calSumParam.psndocInSQL);

			IDevItfQueryService service = NCLocator.getInstance().lookup(IDevItfQueryService.class);
			calSumParam.dataCreatorMap = new HashMap();
			for (ItemCopyVO vo : calSumParam.dayItemVOs) {
				if (vo.getSrc_flag().intValue() == 2) {
					IDayDataCreator creator = (IDayDataCreator) service.queryByCodeAndObj("TA-009", vo.getPrimaryKey());
					calSumParam.dataCreatorMap.put(vo.getItem_code(), creator);
				}
			}
		}
		Logger.debug("查询timedata、lateearly、考勤档案耗时：" + (System.currentTimeMillis() - time));

		time = System.currentTimeMillis();
		calSumParam.leaveMap =
				LeaveServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate);
		calSumParam.lactationMap =
				LeaveServiceFacade.queryAllLactationVOIncEffictiveByPsndocInSQLDate(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate);
		calSumParam.awayMap =
				AwayServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate);

		calSumParam.shutMap =
				ShutdownServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate);

		calSumParam.overMap =
				OverTimeServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate.getDateBefore(2), endDate.getDateAfter(2));

		calSumParam.checkTimesMap =
				CheckTimeServiceFacade.queryCheckTimeMapByPsndocInSQLAndDateScope(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate);
		Logger.debug("查询单据耗时：" + (System.currentTimeMillis() - time));
		calSumParam.billMutexRule = BillMutexRule.createBillMutexRule(calSumParam.timeRuleVO.getBillmutexrule());

		calSumParam.workDayLength = (calSumParam.timeRuleVO == null ? 8.0D : calSumParam.timeRuleVO.getDaytohour().doubleValue());
		return calSumParam;
	}

	private void processBills(CalSumParam calSumParam) throws BusinessException {
		List<DayStatbVO> statbVOList = new ArrayList<DayStatbVO>();
		int psnCount = calSumParam.pk_psndocs.length;
		Map<String, TimeZone> timeZoneMap = calSumParam.timeRuleVO.getTimeZoneMap();

		Map<String, HRHolidayVO[]> psnEnjoyHolidayScope = new HashMap();
		Map<String, OvertimeRegVO[]> overMap = calSumParam.overMap;
		List<OvertimeRegVO> overList = new ArrayList<OvertimeRegVO>();
		if (MapUtils.isNotEmpty(overMap)) {
			for (String pk_psndoc : overMap.keySet()) {
				OvertimeRegVO[] overtimeRegVOs = (OvertimeRegVO[]) overMap.get(pk_psndoc);
				if (!ArrayUtils.isEmpty(overtimeRegVOs)) {
					for (OvertimeRegVO overvo : overtimeRegVOs)
						overList.add(overvo);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(overList)) {
			psnEnjoyHolidayScope = BillProcessHelperAtServer.getOverTimeHolidayScope(overList.toArray(new OvertimeRegVO[0]));
		}

		for (int psnIndex = 0; psnIndex < psnCount; psnIndex++) {
			String pk_psndoc = calSumParam.pk_psndocs[psnIndex];
			Map<UFLiteralDate, String> dateOrgMap = (Map) calSumParam.allDateOrgMap.get(pk_psndoc);
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);

			List<TBMPsndocVO> psndocList = (List) calSumParam.tbmPsndocMap.get(pk_psndoc);
			if (!CollectionUtils.isEmpty(psndocList)) {

				LeaveRegVO[] leaveBills = (LeaveRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.leaveMap);
				AwayRegVO[] awayBills = (AwayRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.awayMap);
				OvertimeRegVO[] overtimeBills = (OvertimeRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.overMap);
				ShutdownRegVO[] shutdownBills = (ShutdownRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.shutMap);

				ITimeScopeWithBillType[] processedBills =
						BillProcessHelper.crossAllBills(new ITimeScopeWithBillType[][] { leaveBills, awayBills, overtimeBills, shutdownBills });

				if ((processedBills != null) && (processedBills.length != 0)) {

					for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
						UFLiteralDate curDate = calSumParam.allDates[dateIndex];
						if (TBMPsndocVO.isIntersect(psndocList, curDate.toString())) {

							AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, curDate);

							ShiftVO curShift =
									curCalendar.getPsnCalendarVO() == null ? null : curCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO().getPk_shift());
							AggPsnCalendar preCalendar =
									getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, calSumParam.allDates[(dateIndex - 1)]);
							ShiftVO preShift =
									preCalendar.getPsnCalendarVO() == null ? null : preCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
							AggPsnCalendar nextCalendar =
									getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, calSumParam.allDates[(dateIndex + 1)]);
							ShiftVO nextShift =
									nextCalendar.getPsnCalendarVO() == null ? null : nextCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO().getPk_shift());
							TimeZone curTimeZone =
									CommonUtils.ensureTimeZone((TimeZone) calSumParam.timeRuleVO.getTimeZoneMap().get(dateOrgMap.get(curDate)));
							TimeZone preTimeZone =
									CommonUtils.ensureTimeZone((TimeZone) calSumParam.timeRuleVO.getTimeZoneMap().get(dateOrgMap.get(calSumParam.allDates[(dateIndex - 1)])));
							TimeZone nextTimeZone =
									CommonUtils.ensureTimeZone((TimeZone) calSumParam.timeRuleVO.getTimeZoneMap().get(dateOrgMap.get(calSumParam.allDates[(dateIndex + 1)])));

							if ((curCalendar != null) && (curCalendar.getPsnCalendarVO().isFlexibleFinal())) {
								ITimeScope kqScope =
										ShiftVO.toKqScope(curShift, preShift, nextShift, curDate.toString(), curTimeZone, preTimeZone, nextTimeZone);

								SolidifyPara solidifyPara = calSumParam.toSolidifyPara(pk_psndoc, curDate, kqScope);

								curCalendar.setPsnWorkTimeVO(SolidifyUtils.solidify(solidifyPara));
							}
						}
					}
					BillMutexRule billMutexRule = calSumParam.billMutexRule;

					List<ITimeScopeWithBillType> leaveBillList = BillProcessHelper.filterBills(processedBills, 1, billMutexRule);
					List<ITimeScopeWithBillType> awayBillList = BillProcessHelper.filterBills(processedBills, 4, billMutexRule);

					List<ITimeScopeWithBillType> overtimeBillList =
							BillProcessHelper.filterOvertimeBills(processedBills, calSumParam.leaveCopyVOs, calSumParam.calendarMap == null ? null : (Map) calSumParam.calendarMap.get(pk_psndoc), calSumParam.aggShiftMap, dateTimeZoneMap, calSumParam.allDates, calSumParam.dateBeginIndex, calSumParam.dateEndIndex, billMutexRule, psnEnjoyHolidayScope.get(pk_psndoc));

					List<ITimeScopeWithBillType> shutdownBillList = BillProcessHelper.filterBills(processedBills, 8, billMutexRule);

					Map<String, UFLiteralDate> overtimeBelongDateMap = new HashMap();
					BillProcessHelper.findBelongtoDate(overtimeBills, overtimeBelongDateMap, calSumParam.calendarMap == null ? null : (Map) calSumParam.calendarMap.get(pk_psndoc), calSumParam.shiftMap, calSumParam.allDates, dateTimeZoneMap);

					if (calSumParam.containsOvertimeBeginEndTimeVar) {
						IOvertimeManageService otservice = NCLocator.getInstance().lookup(IOvertimeManageService.class);
						otservice.createOvertimeBelongData(overtimeBills, overtimeBelongDateMap);
					}

					for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
						UFLiteralDate curDate = calSumParam.allDates[dateIndex];
						if (TBMPsndocVO.isIntersect(psndocList, curDate.toString())) {

							String pk_daystat =
									calSumParam.daystatPKMap.get(pk_psndoc) == null ? null : (String) ((Map) calSumParam.daystatPKMap.get(pk_psndoc)).get(curDate.toString());

							AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, curDate);

							ShiftVO curShift =
									curCalendar.getPsnCalendarVO() == null ? null : curCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO().getPk_shift());
							AggPsnCalendar preCalendar =
									getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, calSumParam.allDates[(dateIndex - 1)]);
							ShiftVO preShift =
									preCalendar.getPsnCalendarVO() == null ? null : preCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
							AggPsnCalendar nextCalendar =
									getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, calSumParam.allDates[(dateIndex + 1)]);
							ShiftVO nextShift =
									nextCalendar.getPsnCalendarVO() == null ? null : nextCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO().getPk_shift());
							TimeZone curTimeZone =
									CommonUtils.ensureTimeZone((TimeZone) calSumParam.timeRuleVO.getTimeZoneMap().get(dateOrgMap.get(curDate)));
							TimeZone preTimeZone =
									CommonUtils.ensureTimeZone((TimeZone) calSumParam.timeRuleVO.getTimeZoneMap().get(dateOrgMap.get(calSumParam.allDates[(dateIndex - 1)])));
							TimeZone nextTimeZone =
									CommonUtils.ensureTimeZone((TimeZone) calSumParam.timeRuleVO.getTimeZoneMap().get(dateOrgMap.get(calSumParam.allDates[(dateIndex + 1)])));

							BillProcessHelper.processLeaveLength(calSumParam.leaveCopyVOs, leaveBillList, curCalendar, preShift, curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone, statbVOList, calSumParam.datePeriodMap, calSumParam.paramValues, pk_daystat, calSumParam.timeRuleVO);

							BillProcessHelper.processAwayLength(calSumParam.awayCopyVOs, awayBillList, curDate.toString(), curCalendar, preShift, curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone, statbVOList, calSumParam.datePeriodMap, calSumParam.paramValues, pk_daystat, calSumParam.timeRuleVO);

							BillProcessHelper.processOvertiemLength(calSumParam.overCopyVOs, overtimeBillList, curDate.toString(), overtimeBelongDateMap, statbVOList, pk_daystat, calSumParam.timeRuleVO);

							BillProcessHelper.processShutdownLength(calSumParam.shutCopyVOs, shutdownBillList, curCalendar, statbVOList, pk_daystat, calSumParam.timeRuleVO);
						}
					}
				}
			}
		}
		if (statbVOList.size() > 0) {
			String pk_org = calSumParam.pk_org;
			String pk_group = calSumParam.timeRuleVO.getPk_group();
			for (DayStatbVO vo : statbVOList) {
				vo.setPk_org(pk_org);
				vo.setPk_group(pk_group);
			}
			new BaseDAO().insertVOList(statbVOList);
		}
	}

	private void processItems(CalSumParam calSumParam) throws DbException, BusinessException {
		if ((calSumParam.containsPreviousDayFunc) || (calSumParam.containsDatePeriodVar)) {
			processItemsDayAfterDay(calSumParam);
			return;
		}
		processItemsAllDaysOnce(calSumParam);
	}

	private void processItemsDayAfterDay(CalSumParam calSumParam) throws DbException, BusinessException {
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;
		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		ItemCopyVO[] dayItemVOs = calSumParam.dayItemVOs;
		JdbcSession session = null;
		try {
			for (int i = calSumParam.dateBeginIndex; i <= calSumParam.dateEndIndex; i++) {
				UFLiteralDate date = calSumParam.allDates[i];

				for (ItemCopyVO itemVO : dayItemVOs)
					if (itemVO.getSrc_flag().intValue() != 1) {
						if (itemVO.getSrc_flag().intValue() == 0) {
							para.clearParams();
							para.addParam(pk_org);
							para.addParam(date.toString());
							String updateSql =
									itemVO.getParsedFormula() + " where " + "pk_org" + "=?" + " and " + "pk_psndoc" + " in (" + psndocInSQL + ") and " + "calendar" + "=?";

							dao.executeUpdate(updateSql, para);
						} else {
							IDayDataCreator creator = (IDayDataCreator) calSumParam.dataCreatorMap.get(itemVO.getItem_code());

							if (creator != null) {

								String[] pk_psndocs = calSumParam.pk_psndocs;
								String updateSql = MessageFormat.format(getUpdateJavaItemSQL(), new Object[] { itemVO.getItem_db_code() });
								if (session == null)
									session = new JdbcSession();
								for (String pk_psndoc : pk_psndocs)
									processJavaItemForOnePersonOneDay(calSumParam, pk_psndoc, calSumParam.allDates[(i - 1)], date, calSumParam.allDates[(i + 1)], session, para, itemVO, creator, updateSql);
							}
						}
					}
			}
			if (session != null) {
				session.executeBatch();
			}
		} finally {
			if (session != null) {
				session.closeAll();
			}
		}
	}

	private void processItemsAllDaysOnce(CalSumParam calSumParam) throws DbException, BusinessException {
		String psndocInSQL = calSumParam.psndocInSQL;
		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		UFLiteralDate beginDate = calSumParam.allDates[calSumParam.dateBeginIndex];
		UFLiteralDate endDate = calSumParam.allDates[calSumParam.dateEndIndex];
		para.addParam(calSumParam.pk_org);
		para.addParam(beginDate.toString());
		para.addParam(endDate.toString());

		ItemCopyVO[] dayItemVOs = calSumParam.dayItemVOs;
		JdbcSession session = null;
		SQLParameter javaItemUpdatePara = new SQLParameter();
		try {
			for (ItemCopyVO itemVO : dayItemVOs)
				if (itemVO.getSrc_flag().intValue() != 1) {
					if (itemVO.getSrc_flag().intValue() == 0) {
						String updateSql =
								itemVO.getParsedFormula() + " where " + "pk_org" + "=?" + " and " + "pk_psndoc" + " in (" + psndocInSQL + ") and " + "calendar" + " between ? and ?";

						try {
							dao.executeUpdate(updateSql, para);
						} catch (Exception e) {
							Logger.error(e.getMessage());
							throw new BusinessException(itemVO.getMultilangName() + ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0098") + e.getMessage());
						}

					} else {
						IDayDataCreator creator = (IDayDataCreator) calSumParam.dataCreatorMap.get(itemVO.getItem_code());

						if (creator != null) {
							if (session == null) {
								session = new JdbcSession();
							}
							String[] pk_psndocs = calSumParam.pk_psndocs;
							String updateSql = MessageFormat.format(getUpdateJavaItemSQL(), new Object[] { itemVO.getItem_db_code() });
							for (String pk_psndoc : pk_psndocs)
								for (int i = calSumParam.dateBeginIndex; i <= calSumParam.dateEndIndex; i++) {
									UFLiteralDate date = calSumParam.allDates[i];
									processJavaItemForOnePersonOneDay(calSumParam, pk_psndoc, calSumParam.allDates[(i - 1)], date, calSumParam.allDates[(i + 1)], session, javaItemUpdatePara, itemVO, creator, updateSql);
								}
						}
					}
				}
			if (session != null) {
				session.executeBatch();
			}
		} finally {
			if (session != null) {
				session.closeAll();
			}
		}
	}

	private void processJavaItemForOnePersonOneDay(CalSumParam calSumParam, String pk_psndoc, UFLiteralDate preDate, UFLiteralDate date, UFLiteralDate nextDate, JdbcSession session, SQLParameter para, ItemCopyVO itemVO, IDayDataCreator creator, String updateSQL)
			throws DbException, BusinessException {
		processDayCalParamByPsndocDate(calSumParam, pk_psndoc, preDate, date, nextDate);
		DayCalParam param = calSumParam.dayCalParam;
		param.itemCode = itemVO.getItem_code();
		creator.process(param);

		para.clearParams();
		para.addParam(calSumParam.pk_org);
		para.addParam(pk_psndoc);
		para.addParam(date.toString());
		session.addBatch(updateSQL, para);
	}

	private void processDayCalParamByPsndocDate(CalSumParam calSumParam, String pk_psndoc, UFLiteralDate preDate, UFLiteralDate date, UFLiteralDate nextDate)
			throws BusinessException {
		DayCalParam param = calSumParam.dayCalParam;
		param.date = date;

		param.psndocVO = TBMPsndocVO.findIntersectionVO((List) calSumParam.tbmPsndocMap.get(pk_psndoc), date.toString());

		Map<UFLiteralDate, TimeDataVO> timeDataMap = (Map) calSumParam.timedataMap.get(pk_psndoc);
		if (!MapUtils.isEmpty(timeDataMap)) {
			param.timeDataVO = ((TimeDataVO) timeDataMap.get(date));
		}
		Map<UFLiteralDate, LateEarlyVO> lateEarlyMap = (Map) calSumParam.lateearlyMap.get(pk_psndoc);
		if (!MapUtils.isEmpty(lateEarlyMap)) {
			param.lateearlyVO = ((LateEarlyVO) lateEarlyMap.get(date));
		}
		Map<UFLiteralDate, String> dateOrgMap = (Map) calSumParam.allDateOrgMap.get(pk_psndoc);

		AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, date);
		ShiftVO curShift =
				curCalendar.getPsnCalendarVO() == null ? null : curCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO().getPk_shift());
		AggPsnCalendar preCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, preDate);
		ShiftVO preShift =
				preCalendar.getPsnCalendarVO() == null ? null : preCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
		AggPsnCalendar nextCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, nextDate);
		ShiftVO nextShift =
				nextCalendar.getPsnCalendarVO() == null ? null : nextCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO().getPk_shift());
		TimeZone curTimeZone = CommonUtils.ensureTimeZone((TimeZone) param.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(date)));
		TimeZone preTimeZone = CommonUtils.ensureTimeZone((TimeZone) param.timeruleVO.getTimeZoneMap().get(preDate));
		TimeZone nextTimeZone = CommonUtils.ensureTimeZone((TimeZone) param.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(nextDate)));
		param.curCalendarVO = curCalendar;
		param.curShiftVO = curShift;
		param.curTimeZone = curTimeZone;

		param.preCalendarVO = preCalendar;
		param.preShiftVO = preShift;
		param.preTimeZone = preTimeZone;

		param.nextCalendarVO = nextCalendar;
		param.nextShiftVO = nextShift;
		param.nextTimeZone = nextTimeZone;
		ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift, nextShift, date.toString(), curTimeZone, preTimeZone, nextTimeZone);

		param.awayBills = ((AwayRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.awayMap, kqScope));
		param.leaveBills = ((LeaveRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.leaveMap, kqScope));
		param.overtimeBills = ((OvertimeRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.overMap, kqScope));
		param.shutdownBills = ((ShutdownRegVO[]) DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.shutMap, kqScope));
		param.mergeTimeScopes =
				TimeScopeUtils.mergeTimeScopes(TimeScopeUtils.mergeTimeScopes(param.awayBills, param.leaveBills), TimeScopeUtils.mergeTimeScopes(param.overtimeBills, param.shutdownBills));
		param.lactationholidayVO = ((LeaveRegVO) DataFilterUtils.filterDateScopeVO(pk_psndoc, calSumParam.lactationMap, date.toString()));

		if (MapUtils.isNotEmpty(calSumParam.checkTimesMap)) {
			ICheckTime[] checkTimes = (ICheckTime[]) calSumParam.checkTimesMap.get(pk_psndoc);
			param.checkTimes = DataFilterUtils.filterCheckTimes(kqScope, checkTimes);

			ITimeScope natualScope = TimeScopeUtils.toFull3Day(date.toString(), param.curTimeZone);
			param.naturalCheckTimes = DataFilterUtils.filterCheckTimes(natualScope, checkTimes);
		}
	}

	private static String getUpdateJavaItemSQL() {
		return "update tbm_daystat set {0}=? where pk_org=? and pk_psndoc=? and calendar=?";
	}

	private AggPsnCalendar getAggPsnCalendarVO(Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap, String pk_psndoc, UFLiteralDate date) {
		if (MapUtils.isEmpty(calendarMap))
			return null;
		Map<UFLiteralDate, AggPsnCalendar> map = (Map) calendarMap.get(pk_psndoc);
		if (MapUtils.isEmpty(map))
			return null;
		Map<String, AggPsnCalendar> stringDateMap = new HashMap();
		for (UFLiteralDate key : map.keySet()) {
			stringDateMap.put(key.toString(), map.get(key));
		}
		return (AggPsnCalendar) stringDateMap.get(date.toString());
	}

	public DayStatVO[] save(String pk_org, DayStatVO[] vos) throws BusinessException {
		try {
			if (ArrayUtils.isEmpty(vos))
				return null;
			DayStatVO[] oldvos = null;
			InSQLCreator isc = new InSQLCreator();
			String condition = "pk_daystat in (" + isc.getInSQL(vos, "pk_daystat") + ") ";
			Collection<OvertimeRegVO> oldc = new BaseDAO().retrieveByClause(DayStatVO.class, condition);
			if (CollectionUtils.isNotEmpty(oldc))
				oldvos = oldc.toArray(new DayStatVO[0]);
			new DayStatDAO().save(pk_org, vos);
			EventDispatcher.fireEvent(new BusinessEvent("e53b4c0c-5fa2-42ee-8cd7-d6bd3a2e5b6f", "1002", vos));

			TaBusilogUtil.writeDayStatEditBusiLog(vos, oldvos);
			return vos;
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	public DeptDayStatVO[] queryDeptDayStatByCondition(LoginContext context, String[] deptPKs, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean showNoDataRecord)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(deptPKs)) {
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPkArrayQuerySQL(deptPKs, fromWhereSQL);
		}

		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);

		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		DeptDayStatVO[] retArray = null;

		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class).queryItemByOrg(context.getPk_org(), itemClass, true);

		for (UFLiteralDate date : allDates) {
			retArray =
					(DeptDayStatVO[]) ArrayUtils.addAll(retArray, queryDeptStatVOByCondition(context, fromWhereSQL, date, showNoDataRecord, allItemVOs));
		}

		return retArray;
	}

	public DayStatVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean showNoDataRecord)
			throws BusinessException {
		return queryByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate, showNoDataRecord);
	}

	protected DayStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean showNoDataRecord)
			throws BusinessException {
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		DayStatVO[] retArray = null;

		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class).queryItemByOrg(pk_org, itemClass, true);

		for (UFLiteralDate date : allDates) {
			retArray =
					(DayStatVO[]) ArrayUtils.addAll(retArray, queryByCondition(pk_org, fromWhereSQL, date, showNoDataRecord, allItemVOs));
		}

		return retArray;
	}

	protected DayStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate date, boolean showNoDataRecord, boolean isforDeptQuery, ItemVO[] allItemVOs)
			throws BusinessException {
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		TBMPsndocVO[] psndocVOsInOrg = psndocService.queryByCondition(pk_org, fromWhereSQL, date, false, isforDeptQuery);

		TBMPsndocVO[] psndocVOsInAdminOrg = psndocService.queryByCondition2(pk_org, fromWhereSQL, date, false, isforDeptQuery);
		if ((ArrayUtils.isEmpty(psndocVOsInOrg)) && (ArrayUtils.isEmpty(psndocVOsInAdminOrg))) {
			return null;
		}
		InSQLCreator isc = new InSQLCreator();
		try {
			SQLParameter para = new SQLParameter();
			para.addParam(date.toString());
			para.addParam(pk_org);
			DayStatVO[] dbVOsInOrg = null;
			DayStatVO[] dbVOsInAdminOrg = null;
			if (!ArrayUtils.isEmpty(psndocVOsInOrg)) {
				String cond =
						"pk_psndoc in(" + isc.getInSQL(psndocVOsInOrg, "pk_psndoc") + ") and " + "calendar" + "=? and " + "pk_org" + "=? ";

				dbVOsInOrg =
						(DayStatVO[]) new DayStatDAO().query2(pk_org, DayStatVO.class, new String[] { "pk_psndoc", "calendar", "pk_group", "pk_org" }, cond, null, para, allItemVOs);
			}

			if (!ArrayUtils.isEmpty(psndocVOsInAdminOrg)) {
				String cond =
						"pk_psndoc in(" + isc.getInSQL(psndocVOsInAdminOrg, "pk_psndoc") + ") and " + "calendar" + "=? and " + "pk_org" + "<>? ";

				dbVOsInAdminOrg =
						(DayStatVO[]) new DayStatDAO().query2(pk_org, DayStatVO.class, new String[] { "pk_psndoc", "calendar", "pk_group", "pk_org" }, cond, null, para, allItemVOs);
			}

			DayStatVO[] retVOsInOrg = processDBVOs(psndocVOsInOrg, dbVOsInOrg, date, showNoDataRecord, isforDeptQuery);

			DayStatVO[] retVOsInAdminOrg = processDBVOs(psndocVOsInAdminOrg, dbVOsInAdminOrg, date, showNoDataRecord, isforDeptQuery);
			return (DayStatVO[]) ArrayUtils.addAll(retVOsInOrg, retVOsInAdminOrg);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}

	protected DayStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate date, boolean showNoDataRecord, ItemVO[] allItemVOs)
			throws BusinessException {
		return queryByCondition(pk_org, fromWhereSQL, date, showNoDataRecord, false, allItemVOs);
	}

	protected DeptDayStatVO[] queryDeptStatVOByCondition(LoginContext context, FromWhereSQL fromWhereSQL, UFLiteralDate date, boolean showNoDataRecord, ItemVO[] allItemVOs)
			throws BusinessException {
		DayStatVO[] psnStatVOs = queryByCondition(context.getPk_org(), fromWhereSQL, date, showNoDataRecord, true, allItemVOs);
		if (ArrayUtils.isEmpty(psnStatVOs)) {
			return null;
		}
		List<DeptDayStatVO> retList = new ArrayList<DeptDayStatVO>();
		Map<String, DeptDayStatVO> statMap = new HashMap();
		for (DayStatVO psnVO : psnStatVOs) {
			String pk_dept = psnVO.getPk_dept();
			DeptDayStatVO deptVO = statMap.get(pk_dept);
			if (deptVO == null) {
				deptVO = new DeptDayStatVO();
				deptVO.setPk_dept(pk_dept);
				deptVO.setCalendar(date);

				deptVO.setPk_dept_v(psnVO.getPk_dept_v());
				deptVO.setPk_org_v(psnVO.getPk_org_v());

				statMap.put(pk_dept, deptVO);
				retList.add(deptVO);
			}
			deptVO.mergePsnDayStatVO(psnVO);
		}
		return retList.toArray(new DeptDayStatVO[0]);
	}

	protected DayStatVO[] processDBVOs(TBMPsndocVO[] psndocVOs, DayStatVO[] dbVOs, UFLiteralDate date, boolean showNoDataRecord, boolean isforDeptQuery)
			throws BusinessException {
		if ((ArrayUtils.isEmpty(dbVOs)) && (!showNoDataRecord))
			return null;
		if (ArrayUtils.isEmpty(psndocVOs)) {
			return null;
		}
		Map<String, DayStatVO> dbVOMap = CommonUtils.toMap("pk_psndoc", dbVOs);
		if (dbVOMap == null)
			dbVOMap = new HashMap();
		List<DayStatVO> retList = new ArrayList<DayStatVO>();

		if (!showNoDataRecord) {
			for (int i = 0; i < psndocVOs.length; i++) {
				TBMPsndocVO psndocVO = psndocVOs[i];
				String pk_psndoc = psndocVO.getPk_psndoc();
				DayStatVO dbVO = dbVOMap.get(pk_psndoc);
				if (dbVO != null) {
					if (((!dbVO.isNoDataRecord()) && (!isforDeptQuery)) || ((!dbVO.isNoDecimalDataRecord()) && (isforDeptQuery))) {
						retList.add(dbVO);

						dbVO.setPk_psnjob(psndocVO.getPk_psnjob());
						dbVO.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
						if (isforDeptQuery) {
							dbVO.setPk_dept(psndocVO.getPk_dept());
						}

						dbVO.setPk_org_v(psndocVO.getPk_org_v());
						dbVO.setPk_dept_v(psndocVO.getPk_dept_v());
					}
				}
			}
			return retList.size() == 0 ? null : retList.toArray(new DayStatVO[0]);
		}

		DayStatVO[] retArray = new DayStatVO[psndocVOs.length];
		for (int i = 0; i < psndocVOs.length; i++) {
			TBMPsndocVO psndocVO = psndocVOs[i];
			DayStatVO daystatVO = dbVOMap.get(psndocVO.getPk_psndoc());
			if (daystatVO != null) {
				retArray[i] = daystatVO;
				daystatVO.setPk_psnjob(psndocVO.getPk_psnjob());
				daystatVO.setPk_tbm_psndoc(psndocVO.getPrimaryKey());

				daystatVO.setPk_org_v(psndocVO.getPk_org_v());
				daystatVO.setPk_dept_v(psndocVO.getPk_dept_v());

				if (isforDeptQuery) {
					daystatVO.setPk_dept(psndocVO.getPk_dept());
				}
			} else {
				daystatVO = new DayStatVO();
				retArray[i] = daystatVO;
				daystatVO.setCalendar(date);
				daystatVO.setPk_psndoc(psndocVO.getPk_psndoc());
				daystatVO.setPk_psnjob(psndocVO.getPk_psnjob());
				daystatVO.setPk_group(psndocVO.getPk_group());
				daystatVO.setPk_org(psndocVO.getPk_org());

				daystatVO.setPk_org_v(psndocVO.getPk_org_v());
				daystatVO.setPk_dept_v(psndocVO.getPk_dept_v());
				if (isforDeptQuery)
					daystatVO.setPk_dept(psndocVO.getPk_dept());
			}
		}
		return retArray;
	}

	public TBMPsndocVO[] queryUnGenerateByCondition(LoginContext context, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);

		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_org_v" + FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_dept_v" + FromWhereSQLUtils.getAttPathPostFix());
		String[] otherTableSelFields =
				{ orgversionAlias + "." + "pk_vid" + " as " + "pk_org_v", deptversionAlias + "." + "pk_vid" + " as " + "pk_dept_v" };

		SQLParamWrapper wrapper =
				TBMPsndocSqlPiecer.selectUnCompleteDailyDataByPsndocFieldAndDateFieldAndDateArea(context.getPk_org(), new String[] { "pk_psndoc", "pk_psnjob", "pk_org" }, otherTableSelFields, "tbm_daystat daystat", "daystat.pk_org", "daystat.pk_psndoc", "daystat.calendar", beginDate.toString(), endDate.toString(), null, "daystat.dirty_flag='N'", fromWhereSQL);

		String sql = wrapper.getSql();
		SQLParameter para = wrapper.getParam();
		TBMPsndocVO[] retvos =
				(TBMPsndocVO[]) CommonUtils.toArray(TBMPsndocVO.class, (List) new BaseDAO().executeQuery(sql, para, new BeanListProcessor(TBMPsndocVO.class)));

		TaBusilogUtil.writeDayStatUngenBusiLog(retvos, beginDate, endDate);
		return retvos;
	}

	private void processPeriodAndNextPeriodOfDate(String pk_org, UFLiteralDate[] dates, Map<String, String[]> periodMap)
			throws BusinessException {
		PeriodVO[] periods = PeriodServiceFacade.queryPeriodsByDateScope(pk_org, dates[0], dates[(dates.length - 1)]);
		if (ArrayUtils.isEmpty(periods)) {
			return;
		}

		for (UFLiteralDate date : dates) {
			for (int i = 0; i < periods.length; i++) {
				if (((date.before(periods[i].getEnddate())) || (date.equals(periods[i].getEnddate()))) && ((date.after(periods[i].getBegindate())) || (date.equals(periods[i].getBegindate())))) {
					String period = periods[i].getTimeyear() + periods[i].getTimemonth();
					if (i < periods.length - 1) {
						String nextPeriod = periods[(i + 1)].getTimeyear() + periods[(i + 1)].getTimemonth();
						periodMap.put(date.toString(), new String[] { period, nextPeriod });
						break;
					}
					periodMap.put(date.toString(), new String[] { period, null });

					break;
				}
				periodMap.put(date.toString(), null);
			}
		}
	}

	private static class CalSumParam {
		SolidifyPara solidifyPara = new SolidifyPara();

		DayCalParam dayCalParam;

		String pk_org;

		TimeRuleVO timeRuleVO;

		String[] pk_psndocs;

		String psndocInSQL;

		UFLiteralDate[] allDates;

		int dateBeginIndex;

		int dateEndIndex;
		boolean containsPreviousDayFunc;
		boolean containsHolidayFunc;
		boolean containsDatePeriodVar;
		boolean containsOvertimeBeginEndTimeVar;
		LeaveTypeCopyVO[] leaveCopyVOs;
		AwayTypeCopyVO[] awayCopyVOs;
		OverTimeTypeCopyVO[] overCopyVOs;
		ShutDownTypeCopyVO[] shutCopyVOs;
		Map<String, Object> paramValues;
		Map<String, IDayDataCreator> dataCreatorMap;
		ItemCopyVO[] dayItemVOs;
		Map<String, AggShiftVO> aggShiftMap;
		Map<String, ShiftVO> shiftMap;
		Map<String, String[]> datePeriodMap;
		BillMutexRule billMutexRule;
		Map<String, LeaveRegVO[]> leaveMap;
		Map<String, LeaveRegVO[]> lactationMap;
		Map<String, OvertimeRegVO[]> overMap;
		Map<String, AwayRegVO[]> awayMap;
		Map<String, ShutdownRegVO[]> shutMap;
		Map<String, ICheckTime[]> checkTimesMap;
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap;
		Map<String, Map<UFLiteralDate, TimeDataVO>> timedataMap = null;

		Map<String, Map<UFLiteralDate, LateEarlyVO>> lateearlyMap = null;

		Map<String, List<TBMPsndocVO>> tbmPsndocMap = null;
		public Map<String, Map<UFLiteralDate, String>> allDateOrgMap;
		Map<String, Map<String, String>> daystatPKMap;
		double workDayLength;

		private CalSumParam() {
		}

		protected SolidifyPara toSolidifyPara(String pk_psndoc, UFLiteralDate date, ITimeScope kqScope) throws BusinessException {
			this.solidifyPara.timeruleVO = this.timeRuleVO;
			this.solidifyPara.date = date;
			this.solidifyPara.calendarVO =
					(this.calendarMap.get(pk_psndoc) == null ? null : this.calendarMap == null ? null : (AggPsnCalendar) ((Map) this.calendarMap.get(pk_psndoc)).get(date));
			String pk_shift = this.solidifyPara.calendarVO == null ? null : this.solidifyPara.calendarVO.getPsnCalendarVO().getPk_shift();
			this.solidifyPara.shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(this.aggShiftMap, pk_shift);
			Map<UFLiteralDate, String> dateOrgMap = this.allDateOrgMap.get(pk_psndoc);
			this.solidifyPara.timeZone =
					(MapUtils.isEmpty(dateOrgMap) ? ICalendar.BASE_TIMEZONE : (TimeZone) this.timeRuleVO.getTimeZoneMap().get(dateOrgMap.get(date)));
			this.solidifyPara.leaveBills =
					DataFilterUtils.filterRegVOs(kqScope, (ITimeScope[]) DataFilterUtils.filterRegVOs(pk_psndoc, this.leaveMap));
			this.solidifyPara.awayBills =
					DataFilterUtils.filterRegVOs(kqScope, (ITimeScope[]) DataFilterUtils.filterRegVOs(pk_psndoc, this.awayMap));
			this.solidifyPara.shutdownBills =
					DataFilterUtils.filterRegVOs(kqScope, (ITimeScope[]) DataFilterUtils.filterRegVOs(pk_psndoc, this.shutMap));
			this.solidifyPara.mergeLASScopes =
					TimeScopeUtils.mergeTimeScopes(TimeScopeUtils.mergeTimeScopes(this.solidifyPara.awayBills, this.solidifyPara.leaveBills), this.solidifyPara.shutdownBills);
			this.solidifyPara.lactationholidayVO =
					((LeaveCommonVO) DataFilterUtils.filterDateScopeVO(date.toString(), (IDateScope[]) DataFilterUtils.filterRegVOs(pk_psndoc, this.lactationMap)));
			this.solidifyPara.checkTimes =
					DataFilterUtils.filterCheckTimes(kqScope, (ICheckTime[]) DataFilterUtils.filterRegVOs(pk_psndoc, this.checkTimesMap));
			return this.solidifyPara;
		}
	}

	public <T extends IVOWithDynamicAttributes> DaystatImportParam importDatas(List<List<GeneralVO>> vosList, DaystatImportParam paramvo)
			throws BusinessException {
		if (CollectionUtils.isEmpty(vosList)) {
			return null;
		}

		PeriodVO[] periodvos =
				PeriodServiceFacade.queryPeriodsByDateScope(paramvo.getContext().getPk_org(), paramvo.getBegindate(), paramvo.getEnddate());
		if (ArrayUtils.isEmpty(periodvos)) {
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0071"));
		}

		StringBuilder invalidPeroidMes = new StringBuilder();
		for (int i = 0; i < periodvos.length; i++) {
			if (true == periodvos[i].isSeal()) {
				invalidPeroidMes.append(" [" + periodvos[i].getBegindate().toString());
				invalidPeroidMes.append("," + periodvos[i].getEnddate().toString() + "]");
			}
		}
		if (!StringUtils.isBlank(invalidPeroidMes.toString())) {
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0072", new String[] { invalidPeroidMes.toString() }));
		}

		String succMes = "";

		Class<T> clz = (Class<T>) (0 == paramvo.getReport_type() ? DayStatVO.class : MonthStatVO.class);
		T[] datavos;
		try {
			if (0 == paramvo.getFile_type()) {
				datavos = DayStatImportHelper.changeGeneralVOToDayStatVOforExcel(vosList, paramvo, clz);
			} else {
				datavos = DayStatImportHelper.changeGeneralVOToDayStatVOforTxt(vosList, paramvo, clz);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

		if (!paramvo.isRightFormat()) {
			return paramvo;
		}

		try {
			List<String> pk_tbm_psndocs = new ArrayList<String>();
			for (T vo : datavos) {
				if (1 == paramvo.getReport_type()) {
					pk_tbm_psndocs.add(((MonthStatVO) vo).getPk_tbm_psndoc());
					// tsy 添加审批状态、单据类型、一级部门、一级部门管理员
					((MonthStatVO) vo).setApprovestatus(ApproveStatus.FREE);
					((MonthStatVO) vo).setBilltype("6407");
					String pk_dept =
							(String) getDao().executeQuery("select pk_dept from hi_psnjob where pk_psnjob ='" + ((MonthStatVO) vo).getPk_psnjob() + "'", new ColumnProcessor());
					((MonthStatVO) vo).setMngdept(getFatherDeptAndMng(pk_dept)[0]);
					((MonthStatVO) vo).setMngpsndoc(getFatherDeptAndMng(pk_dept)[1]);
				} else {
					pk_tbm_psndocs.add(((DayStatVO) vo).getPk_tbm_psndoc());
				}
			}
			IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
			Map<String, UFBoolean> operMap =
					perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc", (String[]) pk_tbm_psndocs.toArray(new String[0]), "Edit", InvocationInfoProxy.getInstance().getGroupId(), InvocationInfoProxy.getInstance().getUserId());
			List<T> saveList = new ArrayList<T>();
			for (T vo : datavos) {
				String pk_tbm_psndoc = null;
				if (1 == paramvo.getReport_type()) {
					pk_tbm_psndoc = ((MonthStatVO) vo).getPk_tbm_psndoc();
				} else {
					pk_tbm_psndoc = ((DayStatVO) vo).getPk_tbm_psndoc();
				}
				if (operMap.get(pk_tbm_psndoc).booleanValue())
					saveList.add(vo);
			}
			datavos = (T[]) saveList.toArray((IVOWithDynamicAttributes[]) Array.newInstance(clz, 0));

			new DayStatDAO().save4Import(paramvo.getContext().getPk_org(), datavos, paramvo.isIgnoreNullCell());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

		if (0 == paramvo.getReport_type())
			EventDispatcher.fireEvent(new BusinessEvent("e53b4c0c-5fa2-42ee-8cd7-d6bd3a2e5b6f", "1002", datavos));
		succMes = ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0073", new String[] { datavos.length + "" });

		paramvo.setSuccessMsg(succMes + "\n\n请刷新数据!");

		if (0 == paramvo.getReport_type()) {
			TaBusilogUtil.writeDayStatImportBusiLog((DayStatVO[]) datavos, paramvo.getBegindate(), paramvo.getEnddate());
		} else
			TaBusilogUtil.writeMonthStatImportBusiLog((MonthStatVO[]) datavos, paramvo.getTbmyear() + paramvo.getTbmmonth());
		return paramvo;
	}

	public DayStatVO[] queryByDeptAndDate(LoginContext context, String pk_dept, UFLiteralDate date) throws BusinessException {
		if ((StringUtils.isBlank(pk_dept)) || (date == null))
			return null;
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, null);

		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class).queryItemByOrg(context.getPk_org(), itemClass, true);
		return queryByCondition(context.getPk_org(), fromWhereSQL, date, true, allItemVOs);
	}

	public DayStatVO[] generate(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean showNoDataRecord)
			throws BusinessException {
		generate(pk_org, fromWhereSQL, beginDate, endDate);
		return queryByCondition(pk_org, fromWhereSQL, beginDate, endDate, showNoDataRecord);
	}

	public DayStatVO[] queryByCondition(String pk_org, String[] pks) throws BusinessException {
		if ((StringUtils.isBlank(pk_org)) || (ArrayUtils.isEmpty(pks)))
			return null;
		Map<String, List<String>> psnMap = new HashMap<String, List<String>>();

		List<String> days = new ArrayList<String>();
		for (String pk : pks) {
			String[] split = pk.split(",");
			String pk_psndoc = split[0];
			String date = split[1];
			if (psnMap.get(date) == null) {
				List<String> psnList = new ArrayList<String>();
				psnList.add(pk_psndoc);
				psnMap.put(date, psnList);
				days.add(date);
			} else {
				psnMap.get(date).add(pk_psndoc);
			}
		}

		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class).queryItemByOrg(pk_org, itemClass, true);
		DayStatVO[] retArray = null;
		String[] dates = (String[]) days.toArray(new String[0]);

		for (String calendar : dates) {
			List<String> list = psnMap.get(calendar);
			if (!CollectionUtils.isEmpty(list)) {
				String[] pk_psndocs = (String[]) list.toArray(new String[0]);
				FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
				UFLiteralDate date = new UFLiteralDate(calendar);
				retArray = (DayStatVO[]) ArrayUtils.addAll(retArray, queryByCondition(pk_org, fromWhereSQL, date, true, allItemVOs));
			}
		}
		return retArray;
	}

	public String[] queryPksByCondition(LoginContext context, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean showNoDataRecord)
			throws BusinessException {
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		DayStatVO[] dayStatVOs = null;

		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class).queryItemByOrg(context.getPk_org(), itemClass, true);

		for (UFLiteralDate date : allDates) {
			dayStatVOs =
					(DayStatVO[]) ArrayUtils.addAll(dayStatVOs, queryByCondition(context.getPk_org(), fromWhereSQL, date, showNoDataRecord, allItemVOs));
		}

		if (ArrayUtils.isEmpty(dayStatVOs))
			return null;
		String[] pks = new String[dayStatVOs.length];
		for (int i = 0; i < dayStatVOs.length; i++) {
			pks[i] = (dayStatVOs[i].getPk_psndoc() + "," + dayStatVOs[i].getCalendar().toString());
		}
		return pks;
	}

	private void sendMessage(String pk_org, String[] pk_psndocs, CalSumParam calSumParam, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if ((!calSumParam.timeRuleVO.isDayNotice()) || (!isHrssStarted().booleanValue()))
			return;
		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, calSumParam, beginDate, endDate);
		if (noticePsnPkset.size() <= 0) {
			return;
		}
		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(noticePsnPks);
		String cond = "pk_psndoc in (" + insql + " )";

		PsndocVO[] psndocvos =
				(PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsndocVO.class, cond);
		if (ArrayUtils.isEmpty(psndocvos)) {
			return;
		}
		OrgVO org = (OrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, OrgVO.class, pk_org);

		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(noticePsnPks);
		DayStatVO[] dayStatVOs = queryByCondition(pk_org, fromWhereSQL, beginDate, endDate, false);
		Map<String, DayStatVO[]> daystatMap = CommonUtils.group2ArrayByField("pk_psndoc", dayStatVOs);

		HashMap<String, UserVO[]> userMap =
				NCLocator.getInstance().lookup(IUserPubService.class).batchQueryUserVOsByPsnDocID(pk_psndocs, null);
		IURLGenerator IurlDirect = NCLocator.getInstance().lookup(IURLGenerator.class);
		IHRMessageSend messageSendServer = NCLocator.getInstance().lookup(IHRMessageSend.class);

		for (PsndocVO psndocVO : psndocvos) {
			HRBusiMessageVO messageVO = new HRBusiMessageVO();
			DayStatVO[] dayStats = daystatMap.get(psndocVO.getPk_psndoc());
			messageVO.setBillVO(ArrayUtils.isEmpty(dayStats) ? null : dayStats[0]);
			messageVO.setMsgrescode("601717");

			Hashtable<String, Object> busiVarValues = new Hashtable<String, Object>();
			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
			SSOInfo ssinfo = new SSOInfo();
			if (!ArrayUtils.isEmpty(users)) {
				ssinfo.setUserPK(users[0].getCuserid());
			}
			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
			ssinfo.setFuncode("E20200910");

			String urlTitle = IurlDirect.buildURLString(ssinfo);
			busiVarValues.put("url", urlTitle);
			busiVarValues.put("CURRUSERNAME", MultiLangHelper.getName(psndocVO));
			busiVarValues.put("CURRCORPNAME", MultiLangHelper.getName(org));
			messageVO.setBusiVarValues(busiVarValues);
			messageVO.setPkorgs(new String[] { pk_org });
			messageVO.setReceiverPkUsers(new String[] { ArrayUtils.isEmpty(users) ? null : users[0].getPrimaryKey() });

			messageSendServer.sendBuziMessage_RequiresNew(messageVO);
		}
	}

	private Set<String> getExceptionPsns(String pk_org, String[] pk_psndocs, CalSumParam calSumParam, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		Set<String> exceptionPsnPkSet = new HashSet<String>();
		ITBMPsndocQueryService tbmPsnQueryS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		Map<String, List<TBMPsndocVO>> tbmPsnMap = tbmPsnQueryS.queryTBMPsndocMapByPsndocs(pk_org, pk_psndocs, beginDate, endDate, true);
		if (MapUtils.isEmpty(tbmPsnMap)) {
			return null;
		}
		ITimeDataQueryService timDataQuery = NCLocator.getInstance().lookup(ITimeDataQueryService.class);

		Map<String, Map<UFLiteralDate, TimeDataVO>> machTimeDateMap =
				timDataQuery.queryVOMapByPsndocInSQLForMonth(pk_org, beginDate, endDate, calSumParam.psndocInSQL);
		if (MapUtils.isNotEmpty(machTimeDateMap)) {
			for (String pk_psndoc : exceptionPsnPkSet) {

				Map<UFLiteralDate, TimeDataVO> dataMap = machTimeDateMap.get(pk_psndoc);
				for (UFLiteralDate date : dataMap.keySet()) {
					TimeDataVO timeDataVO = (TimeDataVO) dataMap.get(date);
					if (!timeDataVO.isNormal()) {
						if ((calSumParam.timeRuleVO.getDayabsentnotice().booleanValue()) && (isAbsent(timeDataVO))) {
							exceptionPsnPkSet.add(pk_psndoc);
						}
						if ((calSumParam.timeRuleVO.getDaylatenotice().booleanValue()) && (isLate(timeDataVO))) {
							exceptionPsnPkSet.add(pk_psndoc);
						}
						if ((calSumParam.timeRuleVO.getDayearlynotice().booleanValue()) && (isEarly(timeDataVO))) {
							exceptionPsnPkSet.add(pk_psndoc);
						}
						if ((calSumParam.timeRuleVO.getDaymidoutnotice().booleanValue()) && (timeDataVO.getIsMidOut())) {
							exceptionPsnPkSet.add(pk_psndoc);
						}
					}
				}

			}
		}
		ILateEarlyQueryService handTimeDataQuery = NCLocator.getInstance().lookup(ILateEarlyQueryService.class);

		Map<String, Map<UFLiteralDate, LateEarlyVO>> handTimeDateMap =
				handTimeDataQuery.queryVOMapByPsndocInSQLForMonth(pk_org, beginDate, endDate, calSumParam.psndocInSQL);
		if (MapUtils.isNotEmpty(handTimeDateMap)) {
			for (String pk_psndoc : exceptionPsnPkSet) {
				Map<UFLiteralDate, LateEarlyVO> dataMap = handTimeDateMap.get(pk_psndoc);
				for (UFLiteralDate date : dataMap.keySet()) {
					LateEarlyVO handDataVO = (LateEarlyVO) dataMap.get(date);
					if (!handDataVO.isNormal()) {
						if ((calSumParam.timeRuleVO.getDayabsentnotice().booleanValue()) && (isAbsent(handDataVO))) {
							exceptionPsnPkSet.add(pk_psndoc);
						}
						if ((calSumParam.timeRuleVO.getDaylatenotice().booleanValue()) && (isLate(handDataVO))) {
							exceptionPsnPkSet.add(pk_psndoc);
						}
						if ((calSumParam.timeRuleVO.getDayearlynotice().booleanValue()) && (isEarly(handDataVO))) {
							exceptionPsnPkSet.add(pk_psndoc);
						}
					}
				}
			}
		}

		return exceptionPsnPkSet;
	}

	private boolean isLate(Object timeDataVO) {
		if ((timeDataVO instanceof TimeDataVO)) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIslate(i).intValue() == 1)
					return true;
			}
		}
		if ((timeDataVO instanceof LateEarlyVO)) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if ((timedata.getLatecount().intValue() > 0) || (timedata.getLatelength().toDouble().doubleValue() >= 0.0D)) {
				return true;
			}
		}
		return false;
	}

	private boolean isEarly(Object timeDataVO) {
		if ((timeDataVO instanceof TimeDataVO)) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIsearly(i).intValue() == 1)
					return true;
			}
		}
		if ((timeDataVO instanceof LateEarlyVO)) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if ((timedata.getEarlycount().intValue() > 0) || (timedata.getEarlylength().toDouble().doubleValue() >= 0.0D)) {
				return true;
			}
		}
		return false;
	}

	private boolean isAbsent(Object timeDataVO) {
		if ((timeDataVO instanceof TimeDataVO)) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if ((timedata.getIsabsent(i).intValue() == 1) || (timedata.getIsearlyabsent(i).intValue() == 1) || (timedata.getIslateabsent(i).intValue() == 1))
					return true;
			}
		}
		if ((timeDataVO instanceof LateEarlyVO)) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if (timedata.getAbsenthour().toDouble().doubleValue() > 0.0D) {
				return true;
			}
		}
		return false;
	}

	private Boolean isHrssStarted() {
		Boolean isstart = Boolean.valueOf(false);
		isstart = Boolean.valueOf(PubEnv.isModuleStarted(PubEnv.getPk_group(), "E202"));

		return isstart;
	}

	/**
	 * 获取父部门
	 * 
	 * @param pk_dept 
	 * @param hashDeptFather 不可为null
	 * @return
	 * @throws BusinessException
	 */
	public String[] getFatherDeptAndMng(String pk_dept) throws BusinessException {
		if (this.hashDeptFather == null) {
			throw new BusinessException("请检查程序，参数不能为空");
		}
		if (this.hashDeptFather.get(pk_dept) == null) {
			String sql =
					" select org_dept.pk_dept,pk_fatherorg,principal  from org_dept start with pk_dept = '" + pk_dept + "' connect by prior pk_fatherorg = pk_dept";
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = (List<Map<String, String>>) getDao().executeQuery(sql, new MapListProcessor());
			for (Map<String, String> map : list) {
				hashDeptFather.put(map.get("pk_dept"), new String[] { map.get("pk_fatherorg"), map.get("principal") });
			}
		}
		return getSrcDeptAndMng(pk_dept);
	}

	/**
	 * 获取最上层的父部门和负责人
	 * 
	 * @param pk_dept
	 * @param hashDeptFather 不可为null
	 * @return
	 */
	private String[] getSrcDeptAndMng(String pk_dept) {
		if (this.hashDeptFather.get(pk_dept)[0] == null) {
			return new String[] { pk_dept, this.hashDeptFather.get(pk_dept)[1] };
		}
		return getSrcDeptAndMng(hashDeptFather.get(pk_dept)[0]);
	}

	/**
	 * @return dao
	 */
	public BaseDAO getDao() {
		if (this.dao == null) {
			this.dao = new BaseDAO();
		}
		return this.dao;
	}
}