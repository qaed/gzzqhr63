package nc.impl.ta.monthstat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.hr.devitf.IDevItfQueryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.hrss.IURLGenerator;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.ILateEarlyQueryService;
import nc.itf.ta.IMonthStatManageMaintain;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.IViewOrderQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.itf.ta.customization.IMonthDataCreator;
import nc.itf.ta.monthlydata.IMonthlyRecordCreator;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.pub.tools.HiSQLHelper;
import nc.pubitf.rbac.IUserPubService;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pubapp.uif2app.actions.pflow.ApproveStatus;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.hrss.SSOInfo;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.iufo.approve.ApproveStateEnum;
import nc.vo.ml.MultiLangUtil;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
import nc.vo.ta.bill.TaMessageConst;
import nc.vo.ta.customization.MonthCalParam;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.item.ItemCopyVO;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.ta.monthstat.DeptMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.monthstat.MonthStatbVO;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.period.YearMonthComparator;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.AwayTypeCopyVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.ShutDownTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.ta.vieworder.ViewOrderVO;
import nc.vo.ta.wf.pub.TaWorkFlowManager;
import nc.vo.uif2.LoginContext;
import nc.vo.util.SqlWhereUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class MonthStatMaintainImpl extends TaWorkFlowManager<MonthStatVO, MonthStatbVO> implements IMonthStatManageMaintain,
		IMonthStatQueryMaintain {
	private IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);
	private Map<String, String[]> hashDeptFather = null;
	private BaseDAO dao;
	{
		this.hashDeptFather = new HashMap<String, String[]>();
	}

	@Override
	@Deprecated
	// tsy
	public MonthStatVO[] approve(String pk_org, String year, String month, MonthStatVO[] vos) throws BusinessException {
		//		super.
		if (ArrayUtils.isEmpty(vos))
			return null;
		if (pk_org == null) {
			pk_org = vos[0].getPk_org();
		}
		if (year == null) {
			year = vos[0].getTbmyear();
		}
		if (month == null) {
			month = vos[0].getTbmmonth();
		}
		PeriodServiceFacade.checkCurPeriod(pk_org, year, month);

		MonthStatVO[] retvos = vos;
		List<String> pks = new ArrayList<String>();
		for (int i = 0; i < retvos.length; i++) {
			pks.add(retvos[i].getPk_monthstat());
			retvos[i].setIsapprove(UFBoolean.TRUE);
		}
		InSQLCreator isc = new InSQLCreator();
		try {
			String sql =
					"update tbm_monthstat set isapprove = 'Y',iseffective='Y',isuseful='Y' where pk_monthstat in(" + isc.getInSQL(pks.toArray(new String[0])) + ")";
			new BaseDAO().executeUpdate(sql);
		} finally {
			isc.clear();
		}

		return retvos;
	}

	@Override
	public void generate(String pk_org, FromWhereSQL fromWhereSQL, String year, String month) throws BusinessException {
		PeriodVO curPeriod = PeriodServiceFacade.checkCurPeriod(pk_org, year, month);
		//容错处理：有时候难免考勤档案有数据但月报无数据，此时要做一下容错，将这些人的月报数据插入
		IMonthlyRecordCreator creator = new MonthStatRecordCreator();
		creator.createMonthlyRecord(curPeriod);
		//权限
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", "MonthStatGenerate", fromWhereSQL);
		//tsy 添加权限
		fromWhereSQL = addPsnPower(fromWhereSQL);
		//除去已审批的
		fromWhereSQL = ensureApprove(pk_org, year, month, fromWhereSQL);
		String[] pk_psndocs =
				NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org, fromWhereSQL, curPeriod.getBegindate(), curPeriod.getEnddate());
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		//计算月报
		generate0(pk_org, pk_psndocs, curPeriod);
		MonthStatVO[] vos = queryByCondition(pk_org, fromWhereSQL, year, month, false);
		//更新父部门和管理者
		updateSrcDeptAndMng(vos);

	}

	/**
	 * 处理月报计算时的查询模板的条件，在里面增加条件，以便把审核已经通过的人员排除在外，因为审核通过的人员是不能再次计算的
	 * 
	 * @param pk_org
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	private FromWhereSQL ensureApprove(String pk_org, String year, String month, FromWhereSQL fromWhereSQL) throws BusinessException {
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		boolean needApprove = timeRuleVO != null && timeRuleVO.isMonthStatNeedApprove();
		if (!needApprove)//如果不需要审核，则不用排除任何数据
			return fromWhereSQL;
		//否则需要在条件中排除
		//首先保证tbm_psndoc被拼到sql中
		fromWhereSQL = TBMPsndocSqlPiecer.ensureTBMPsndocTable(fromWhereSQL);
		String where = fromWhereSQL.getWhere();
		String tbmpsndocAlias = fromWhereSQL.getTableAliasByAttrpath(".");
		where =
				SQLHelper.appendExtraCond(where, tbmpsndocAlias + "." + TBMPsndocVO.PK_PSNDOC + " not in (select monthstat.pk_psndoc from tbm_monthstat monthstat where monthstat.pk_org=" + tbmpsndocAlias + "." + TBMPsndocVO.PK_ORG + " and monthstat.pk_psndoc=" + TBMPsndocVO.PK_PSNDOC + " and monthstat." + MonthStatVO.TBMYEAR + "='" + year + "' and monthstat." + MonthStatVO.TBMMONTH + "='" + month + "' and isapprove in('y','Y'))");
		return new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL.getFrom(), where, fromWhereSQL, null);
	}

	@Override
	public void generate(String pk_org, String[] pk_psndocs, String year, String month) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		FromWhereSQL fromWhereSQL = null;
		try {
			fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			generate(pk_org, fromWhereSQL, year, month);
		} finally {
			if (fromWhereSQL != null)
				TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
		}

	}

	protected void generate0(String pk_org, String[] pk_psndocs, PeriodVO curPeriod) throws BusinessException {
		//Java类计算类型项目的计算参数
		MonthCalParam monthCalParam = MonthStatCalculationHelper.createPara(pk_org, curPeriod);
		InSQLCreator isc = new InSQLCreator();
		try {
			//准备计算参数
			CalSumParam calSumParam = prepareCalSumParam(pk_org, pk_psndocs, curPeriod, isc, monthCalParam);
			//计算单据时长
			processBills(calSumParam);
			//计算出勤时长
			processWorkDaysHours(calSumParam);
			//			//计算月报项目
			try {
				processItems(calSumParam);
			} catch (DbException e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			}

			//月报生成结束时对存在考勤异常的人员发送提醒消息
			if (monthCalParam.timeruleVO.hasExceptionnoticeset() && isHrssStarted()) {
				this.sendMessage(pk_org, pk_psndocs, curPeriod, calSumParam);
			}
			TaBusilogUtil.writeMonthStatGenerate(pk_org, pk_psndocs, curPeriod.getBegindate(), curPeriod.getEnddate());
		} finally {
			isc.clear();
		}

	}

	private Boolean isHrssStarted() {
		Boolean isstart = false;
		isstart = PubEnv.isModuleStarted(PubEnv.getPk_group(), ICommonConst.MODULECODE_HRSS);
		//修改说明： 应该使用上面的代码，但是修改了 ICommonConst.MODULECODE_HRSS的补丁不起作用，只好暂时使用下面的代码
		//注意v63要换回来使用上面的代码
		//判断自助模块是否启用
		//			isstart= PubEnv.isModuleStarted(PubEnv.getPk_group(),"E202");
		return isstart;
	}

	@SuppressWarnings("unchecked")
	private void processBills(CalSumParam calSumParam) throws DAOException {
		PeriodVO periodVO = calSumParam.periodVO;
		String year = periodVO.getTimeyear();
		String month = periodVO.getTimemonth();
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;
		String sql =
				"select sum(hournum) as hournum,sum(toresthour) as toresthour,pk_timeitem,tbm_daystat.pk_psndoc ,type, " + "(select top 1 pk_monthstat from tbm_monthstat where tbm_monthstat.pk_psndoc=tbm_daystat.pk_psndoc " + "and tbm_monthstat.pk_org=tbm_daystat.pk_org and tbmyear='" + year + "' and tbmmonth='" + month + "')" + " as pk_monthstat,0 as dr from tbm_daystatb " + "inner join tbm_daystat on tbm_daystat.pk_daystat=tbm_daystatb.pk_daystat " + "where tbm_daystat.pk_org='" + pk_org + "' and tbm_daystat.pk_psndoc in(" + psndocInSQL + ") and  ((tbm_daystat.calendar between '" + periodVO.getBegindate() + "' and '" + periodVO.getEnddate() + "' " + "and isnull(tbm_daystatb.calcperiod,'1')='1') or tbm_daystatb.calcperiod='" + year + "'||'" + month + "')" + "group by tbm_daystat.pk_org,tbm_daystat.pk_psndoc,pk_timeitem,type ";

		String delSql =
				"delete from tbm_monthstatb where pk_monthstat in (select pk_monthstat from tbm_monthstat " + "where pk_org='" + pk_org + "' and pk_psndoc in(" + psndocInSQL + ") and tbmyear='" + year + "' and tbmmonth='" + month + "')";
		BaseDAO dao = new BaseDAO();
		//先删除子表记录
		dao.executeUpdate(delSql);
		MonthStatbVO[] vos =
				CommonUtils.toArray(MonthStatbVO.class, (Collection<MonthStatbVO>) dao.executeQuery(sql, new BeanListProcessor(MonthStatbVO.class)));
		if (ArrayUtils.isEmpty(vos))
			return;
		List<MonthStatbVO> insertList = new ArrayList<MonthStatbVO>();
		String pk_group = periodVO.getPk_group();
		Map<String, TimeItemCopyVO> allTypeMap = new HashMap<String, TimeItemCopyVO>();
		if (!ArrayUtils.isEmpty(calSumParam.leaveCopyVOs)) {
			Map<String, LeaveTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.leaveCopyVOs);
			allTypeMap.putAll(map);
		}
		if (!ArrayUtils.isEmpty(calSumParam.awayCopyVOs)) {
			Map<String, AwayTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.awayCopyVOs);
			allTypeMap.putAll(map);
		}
		if (!ArrayUtils.isEmpty(calSumParam.overCopyVOs)) {
			Map<String, OverTimeTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.overCopyVOs);
			allTypeMap.putAll(map);
		}
		if (!ArrayUtils.isEmpty(calSumParam.shutCopyVOs)) {
			Map<String, ShutDownTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.shutCopyVOs);
			allTypeMap.putAll(map);
		}
		for (MonthStatbVO vo : vos) {
			if (StringUtils.isNotBlank(vo.getPk_monthstat()) && ((vo.getHournum() != null && vo.getHournum().doubleValue() > 0) || (vo.getToresthour() != null && vo.getToresthour().doubleValue() > 0))) {
				insertList.add(vo);
				vo.setPk_group(pk_group);
				vo.setPk_org(pk_org);
				TimeItemCopyVO typeVO = allTypeMap.get(vo.getPk_timeitem());
				vo.setTimeitemunit(typeVO != null ? typeVO.getTimeitemunit() : TimeItemCopyVO.TIMEITEMUNIT_DAY);
			}
		}
		//再插入子表记录
		if (insertList.size() > 0)
			dao.insertVOList(insertList);
	}

	private void processWorkDaysHours(CalSumParam calSumParam) throws DAOException {
		PeriodVO periodVO = calSumParam.periodVO;
		String year = periodVO.getTimeyear();
		String month = periodVO.getTimemonth();
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;

		BaseDAO dao = new BaseDAO();
		String delSql =
				"delete from tbm_monthwork where pk_monthstat in (select pk_monthstat from tbm_monthstat " + "where pk_org='" + pk_org + "' and pk_psndoc in(" + psndocInSQL + ") and tbmyear='" + year + "' and tbmmonth='" + month + "')";
		//先删除子表记录
		dao.executeUpdate(delSql);
		//然后插入
		GeneralVO[] genVOs = new MonthStatDAO().queryWorkDaysHours(pk_org, psndocInSQL, periodVO, calSumParam.timeRuleVO);
		if (ArrayUtils.isEmpty(genVOs))
			return;
		String pk_group = periodVO.getPk_group();
		List<MonthWorkVO> result = new ArrayList<MonthWorkVO>();
		for (GeneralVO genVO : genVOs) {
			MonthWorkVO vo = new MonthWorkVO();
			vo.setPk_group(pk_group);
			vo.setPk_org(pk_org);
			UFDouble actualWorkDays = objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.ACTUALWORKDAYS));
			UFDouble actualWorkHours = objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.ACTUALWORKHOURS));
			vo.setActualworkdays(actualWorkDays.doubleValue() < 0 ? UFDouble.ZERO_DBL : actualWorkDays);
			vo.setActualworkhours(actualWorkHours.doubleValue() < 0 ? UFDouble.ZERO_DBL : actualWorkHours);
			vo.setWorkdays(objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.WORKDAYS)));
			vo.setWorkhours(objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.WORKHOURS)));
			vo.setPk_shift((String) genVO.getAttributeValue(MonthWorkVO.PK_SHIFT));
			vo.setPk_monthstat((String) genVO.getAttributeValue(MonthStatVO.PK_MONTHSTAT));
			vo.setDr(0);
			result.add(vo);
		}
		dao.insertVOArray(result.toArray(new MonthWorkVO[0]));
	}

	private UFDouble objectToUFDouble(Object obj) {
		if (obj == null)
			return UFDouble.ZERO_DBL;
		return new UFDouble(String.valueOf(obj));
	}

	/**
	 * 准备参数，例如所有人的考勤项目等等
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	private CalSumParam prepareCalSumParam(String pk_org, String[] pk_psndocs, PeriodVO curPeriod, InSQLCreator isc, MonthCalParam monthCalParam)
			throws BusinessException {
		CalSumParam calSumParam = new CalSumParam();
		calSumParam.monthCalParam = monthCalParam;
		calSumParam.pk_org = pk_org;
		calSumParam.pk_psndocs = pk_psndocs;
		calSumParam.psndocInSQL = isc.getInSQL(pk_psndocs);
		calSumParam.periodVO = curPeriod;

		calSumParam.timeRuleVO = monthCalParam.timeruleVO;
		calSumParam.paramValues = monthCalParam.paramValues;
		if (monthCalParam.leaveItemMap != null) {
			calSumParam.leaveCopyVOs = monthCalParam.leaveItemMap.values().toArray(new LeaveTypeCopyVO[0]);
		}
		if (monthCalParam.awayItemMap != null) {
			calSumParam.awayCopyVOs = monthCalParam.awayItemMap.values().toArray(new AwayTypeCopyVO[0]);
		}
		if (monthCalParam.overtimeItemMap != null) {
			calSumParam.overCopyVOs = monthCalParam.overtimeItemMap.values().toArray(new OverTimeTypeCopyVO[0]);
		}
		if (monthCalParam.shutdownItemMap != null) {
			calSumParam.shutCopyVOs = monthCalParam.shutdownItemMap.values().toArray(new ShutDownTypeCopyVO[0]);
		}

		calSumParam.monthItemVOs = monthCalParam.itemVOs;
		// 考勤项目中是否存在java类的项目。如果不存在java类项目，则后面的一些操作可以不做，以提高计算效率
		boolean existsJavaItem = false;
		for (ItemCopyVO itemVO : calSumParam.monthItemVOs) {
			if (itemVO.getSrc_flag().intValue() == ItemCopyVO.SRC_FLAG_JAVA) {
				existsJavaItem = true;
				break;
			}
		}
		// 所有人员的日报的主表主键,第一个string是人员主键pk_psndoc,第二个string是日期，第三个string是pk_daystat。这个map的主要作用是为后面的daystatb子表生成做准备
		// Logger.error("查询日报主键map开始："+System.currentTimeMillis());
		long time = System.currentTimeMillis();
		if (existsJavaItem) {
			//获取java类项目的计算类实例
			IDevItfQueryService service = NCLocator.getInstance().lookup(IDevItfQueryService.class);
			calSumParam.dataCreatorMap = new HashMap<String, IMonthDataCreator>();
			for (ItemCopyVO vo : calSumParam.monthItemVOs) {
				if (vo.getSrc_flag() == ItemVO.SRC_FLAG_JAVA) {
					IMonthDataCreator creator =
							(IMonthDataCreator) service.queryByCodeAndObj(ICommonConst.ITF_CODE_MONTH, vo.getPrimaryKey());
					calSumParam.dataCreatorMap.put(vo.getItem_code(), creator);
				}
			}
		}
		Logger.debug("查询timedata、lateearly、考勤档案耗时：" + (System.currentTimeMillis() - time));
		// 取得所有人员、所有日期（startDate的前两天至endDate的后两天）的单据数据
		// key是人员主键，value是人员的休假单子表数组，下面的加班单出差单停工单都一样
		time = System.currentTimeMillis();
		// 一个工作日的时长,在考勤规则中定义
		calSumParam.workDayLength = calSumParam.timeRuleVO == null ? 8 : calSumParam.timeRuleVO.getDaytohour().doubleValue();
		return calSumParam;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MonthStatVO[] save(String pk_org, MonthStatVO[] vos) throws BusinessException {
		try {
			if (ArrayUtils.isEmpty(vos))
				return null;
			MonthStatVO[] oldvos = null;
			InSQLCreator isc = new InSQLCreator();
			String condition = MonthStatVO.PK_MONTHSTAT + " in (" + isc.getInSQL(vos, MonthStatVO.PK_MONTHSTAT) + ") ";
			Collection oldc = new BaseDAO().retrieveByClause(MonthStatVO.class, condition);
			if (CollectionUtils.isNotEmpty(oldc))
				oldvos = (MonthStatVO[]) oldc.toArray(new MonthStatVO[0]);
			new MonthStatDAO().save(pk_org, vos);
			//业务日志
			TaBusilogUtil.writeMonthStatEditBusiLog(vos, oldvos);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		return vos;
	}

	@Override
	@Deprecated
	public MonthStatVO[] unApprove(String pk_org, String year, String month, MonthStatVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		PeriodServiceFacade.checkCurPeriod(pk_org, year, month);
		MonthStatVO[] retvos = vos;
		List<String> pks = new ArrayList<String>();
		for (int i = 0; i < retvos.length; i++) {
			pks.add(retvos[i].getPk_monthstat());
			retvos[i].setIsapprove(UFBoolean.FALSE);
		}
		InSQLCreator isc = new InSQLCreator();
		try {
			String sql =
					"update tbm_monthstat set isapprove = 'N',isuseful='N' where pk_monthstat in(" + isc.getInSQL(pks.toArray(new String[0])) + ")";
			new BaseDAO().executeUpdate(sql);
		} finally {
			isc.clear();
		}

		return retvos;
	}

	/**
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @param showNoDataRecord
	 * @param limitPsnByHROrg，是否根据hr组织过滤人员。对于业务节点的查询，要求根据HR组织来过滤，但是对于经理自助，目前要求不按HR组织过滤
	 * @return
	 * @throws BusinessException
	 */
	protected MonthStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		//首先查询期间的起止日期
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if (periodVO == null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0090"
			/*@res "期间{0}不存在!"*/, year + "-" + month));
		//		UFLiteralDate beginDate = periodVO.getBegindate();
		//		UFLiteralDate endDate = periodVO.getEnddate();
		//查询权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		//tsy 添加权限
		fromWhereSQL = addPsnPower(fromWhereSQL);

		//本组织的月报记录
		MonthStatVO[] vos1 = queryByConditionAndOrg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		//管理组织在本组织的月报记录
		MonthStatVO[] vos2 = queryByConditionAndAndminorg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		return (MonthStatVO[]) ArrayUtils.addAll(vos1, vos2);
	}

	/**
	 * 查询本组织的考勤月报
	 * 
	 * @param pk_org
	 * @param periodVO
	 * @param fromWhereSQL
	 * @param showNoDataRecord
	 * @return
	 * @throws BusinessException
	 */
	private MonthStatVO[] queryByConditionAndOrg(String pk_org, PeriodVO periodVO, FromWhereSQL fromWhereSQL, boolean showNoDataRecord)
			throws BusinessException {
		UFLiteralDate beginDate = periodVO.getBegindate();
		UFLiteralDate endDate = periodVO.getEnddate();
		TBMPsndocVO[] psndocVOsInOrg =
				NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(psndocVOsInOrg))
			return null;
		//查询这些人员已有的月报记录
		InSQLCreator isc = new InSQLCreator();
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(periodVO.getTimeyear());
		para.addParam(periodVO.getTimemonth());
		MonthStatVO[] dbVOsInOrg = null;
		String cond =
				MonthStatVO.PK_ORG + "=? and " + MonthStatVO.PK_PSNDOC + " in(" + isc.getInSQL(psndocVOsInOrg, MonthStatVO.PK_PSNDOC) + ") and " + MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=? ";
		try {
			dbVOsInOrg =
					new MonthStatDAO().query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk", "srcid", "mngdept", "mngpsndoc" }, cond, null, para);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		return processDBVOs(psndocVOsInOrg, dbVOsInOrg, periodVO.getTimeyear(), periodVO.getTimemonth(), showNoDataRecord, endDate);
	}

	/**
	 * 查询管理组织是本组织的考勤月报
	 * 
	 * @param pk_org
	 * @param periodVO
	 * @param fromWhereSQL
	 * @param showNoDataRecord
	 * @return
	 * @throws BusinessException
	 */
	private MonthStatVO[] queryByConditionAndAndminorg(String pk_org, PeriodVO periodVO, FromWhereSQL fromWhereSQL, boolean showNoDataRecord)
			throws BusinessException {
		UFLiteralDate beginDate = periodVO.getBegindate();
		UFLiteralDate endDate = periodVO.getEnddate();
		TBMPsndocVO[] psndocVOsInAdminOrg =
				NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestByCondition2(pk_org, fromWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(psndocVOsInAdminOrg))
			return null;
		//查询这些人员已有的月报记录
		InSQLCreator isc = new InSQLCreator();
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(periodVO.getTimeyear());
		para.addParam(periodVO.getTimemonth());
		String cond =
				MonthStatVO.PK_ORG + "<>? and " + MonthStatVO.PK_PSNDOC + " in(" + isc.getInSQL(psndocVOsInAdminOrg, MonthStatVO.PK_PSNDOC) + ") and " + MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=? ";
		MonthStatVO[] dbVOsInAdminOrg = null;
		try {
			dbVOsInAdminOrg =
					new MonthStatDAO().query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk", "srcid", "mngdept", "mngpsndoc" }, cond, null, para);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		//管理组织在本组织的月报记录
		return processDBVOs(psndocVOsInAdminOrg, dbVOsInAdminOrg, periodVO.getTimeyear(), periodVO.getTimemonth(), showNoDataRecord, endDate);
	}

	@Override
	public MonthStatVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		return queryByCondition(context.getPk_org(), fromWhereSQL, year, month, showNoDataRecord);
	}

	/**
	 * 处理月报项目的计算，包括公式、java自定义
	 * 
	 * @param calSumParam
	 * @throws DAOException
	 * @throws DbException
	 */
	private void processItems(CalSumParam calSumParam) throws DAOException, DbException {
		// 存储日报项目值的map，因为java类计算的项目如果依赖于其他项目的值的话，需要从内存中取。月报项目可能依赖于日报项目
		// 第一个string是itemcode，第二个string是pk_psndoc,第三个string是date，Object是项目的值
		//		Map<String, Map<String, Map<String, Object>>> dayItemValueMap = new HashMap<String, Map<String, Map<String, Object>>>();
		// 存放月报项目值的map，，因为java类计算的项目如果依赖于其他项目的值的话，需要从内存中取。
		// 第一个String是itemcode，第二个string是pk_psndoc,Object是项目的值
		//		Map<String, Map<String, Object>> monthItemValueMap = new HashMap<String, Map<String, Object>>();
		ItemCopyVO[] itemVOs = calSumParam.monthItemVOs;
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;
		PeriodVO periodVO = calSumParam.periodVO;
		BaseDAO dao = new BaseDAO();
		//		String[] pk_psndocs = calSumParam.pk_psndocs;
		String updateEffective = "update " + MonthStatVO.getDefaultTableName() + " set " + MonthStatVO.ISEFFECTIVE + "='Y' ";
		String where =
				" where pk_org='" + pk_org + "' and pk_psndoc in (" + psndocInSQL + ") " + "and tbmyear='" + periodVO.getTimeyear() + "' and tbmmonth='" + periodVO.getTimemonth() + "'";
		updateEffective += where;
		dao.executeUpdate(updateEffective);
		//		int psnCount = pk_psndocs.length;
		//		MonthCalParam calPara = calSumParam.monthCalParam;
		// 按项目循环计算
		for (int itemIndex = 0; itemIndex < itemVOs.length; itemIndex++) {
			ItemCopyVO itemVO = itemVOs[itemIndex];
			// 如果是手工录入类型，则不用处理，直接continue
			if (itemVO.getSrc_flag().intValue() == ItemVO.SRC_FLAG_MANUAL) {
				continue;
			}
			// 如果是公式类型，则需要执行公式
			if (itemVO.getSrc_flag().intValue() == ItemVO.SRC_FLAG_FORMULA) {
				String updateSql = itemVO.getParsedFormula() + where;
				dao.executeUpdate(updateSql);
				continue;
			}
		}
	}

	/**
	 * 将dbVOs数组处理后返回。dbVOs数组是数据库中已有的数据 如果showNoDataRecord为true，则保证一人一条记录，不管数据库中是否有（没有就new一个daystatvo） 如果showNoDataRecord为false，则要将dbVOs中所有字段为空的记录去除掉
	 * 
	 * @param psndocVOs
	 * @param aggVOs
	 * @param date
	 * @param showNoDataRecord
	 * @param endDate
	 * @return
	 */
	private MonthStatVO[] processDBVOs(TBMPsndocVO[] psndocVOs, MonthStatVO[] dbVOs, String year, String month, boolean showNoDataRecord, UFLiteralDate endDate)
			throws BusinessException {
		if (ArrayUtils.isEmpty(dbVOs) && !showNoDataRecord)
			return null;
		if (ArrayUtils.isEmpty(psndocVOs))
			return null;
		//dbVO的map，key是pk_psndoc
		Map<String, MonthStatVO> dbVOMap = CommonUtils.toMap(MonthStatVO.PK_PSNDOC, dbVOs);
		if (dbVOMap == null)
			dbVOMap = new HashMap<String, MonthStatVO>();
		List<MonthStatVO> retList = new ArrayList<MonthStatVO>();
		//如果不含空记录
		if (!showNoDataRecord) {
			for (int i = 0; i < psndocVOs.length; i++) {
				TBMPsndocVO psndocVO = psndocVOs[i];
				String pk_psndoc = psndocVO.getPk_psndoc();
				MonthStatVO dbVO = dbVOMap.get(pk_psndoc);
				if (dbVO == null || dbVO.isNoDataRecord())
					continue;
				retList.add(dbVO);
				//设置任职主键(日报表中并没有存储，元数据上有)
				dbVO.setPk_psnjob(psndocVO.getPk_psnjob());
				dbVO.setPk_org_v(psndocVO.getPk_org_v());
				dbVO.setPk_dept_v(psndocVO.getPk_dept_v());
				dbVO.setPk_dept(psndocVO.getPk_dept());
			}
			return retList.size() == 0 ? null : retList.toArray(new MonthStatVO[0]);
		}
		//如果含空记录，则要求每人都要有vo，没有的话，需要new一个

		MonthStatVO[] retArray = new MonthStatVO[psndocVOs.length];
		for (int i = 0; i < psndocVOs.length; i++) {
			TBMPsndocVO psndocVO = psndocVOs[i];
			MonthStatVO monthstatVO = dbVOMap.get(psndocVO.getPk_psndoc());
			if (monthstatVO != null) {
				retArray[i] = monthstatVO;
				monthstatVO.setPk_psnjob(psndocVO.getPk_psnjob());
				monthstatVO.setPk_org_v(psndocVO.getPk_org_v());
				monthstatVO.setPk_dept_v(psndocVO.getPk_dept_v());
				monthstatVO.setPk_dept(psndocVO.getPk_dept());
				continue;
			}
			//如果没有则new一个
			monthstatVO = new MonthStatVO();
			retArray[i] = monthstatVO;
			monthstatVO.setTbmyear(year);
			monthstatVO.setTbmmonth(month);
			monthstatVO.setPk_psndoc(psndocVO.getPk_psndoc());
			monthstatVO.setPk_psnjob(psndocVO.getPk_psnjob());
			monthstatVO.setPk_group(psndocVO.getPk_group());
			monthstatVO.setPk_org(psndocVO.getPk_org());
			monthstatVO.setPk_org_v(psndocVO.getPk_org_v());
			monthstatVO.setPk_dept_v(psndocVO.getPk_dept_v());
			monthstatVO.setPk_dept(psndocVO.getPk_dept());
			//			OrgVersionVO orgv = orgvs.get(monthstatVO.getPk_psndoc());
			//			if(orgv!= null){
			//				monthstatVO.setPk_org_v(orgv.getPk_vid());
			//			}
			//			DeptVersionVO deptv = deptvs.get(monthstatVO.getPk_psndoc());
			//			if(orgv!= null){
			//				monthstatVO.setPk_dept_v(deptv.getPk_vid());
			//			}
		}
		return retArray;
	}

	@Override
	public TBMPsndocVO[] queryUnGenerateByCondition(LoginContext context, FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException {
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(context.getPk_org(), year, month);
		UFLiteralDate periodBeginDate = periodVO.getBegindate();
		UFLiteralDate periodEndDate = periodVO.getEnddate();
		fromWhereSQL = TBMPsndocSqlPiecer.ensureTBMPsndocTable(fromWhereSQL);
		//增加权限处理
		//		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		//使用维护权限
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		String alias = fromWhereSQL.getTableAliasByAttrpath(".");
		String extraCond =
				alias + "." + TBMPsndocVO.PK_PSNDOC + " not in(" + "select " + MonthStatVO.PK_PSNDOC + " from " + MonthStatVO.getDefaultTableName() + " where " + MonthStatVO.PK_ORG + "='" + context.getPk_org() + "' and " + MonthStatVO.TBMYEAR + "='" + year + "' and " + MonthStatVO.TBMMONTH + "='" + month + "' and " + MonthStatVO.ISEFFECTIVE + "='Y')";
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(extraCond, fromWhereSQL);
		//tsy 添加权限
		fromWhereSQL = addPsnPower(fromWhereSQL);
		ITBMPsndocQueryService queryService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		TBMPsndocVO[] vos = queryService.queryLatestByCondition(context.getPk_org(), fromWhereSQL, periodBeginDate, periodEndDate);
		TaBusilogUtil.writeMonthStatUngenBusiLog(vos, periodVO.getBegindate(), periodVO.getEnddate());
		return vos;
	}

	/**
	 * 记录所有汇总参数的类，即所有的类别、项目等
	 * 
	 * @author zengcheng
	 */
	private static class CalSumParam {

		MonthCalParam monthCalParam;
		String pk_org;
		TimeRuleVO timeRuleVO;
		String[] pk_psndocs;
		String psndocInSQL;//人员主键的in sql。注意，psndocInSQL和pk_psndocs实际上是等同的，只不过，如果只传pk_psndocs，不存psndocInSQL的话，很多地方都要根据pk_psndocs再造临时表，比较浪费
		PeriodVO periodVO;

		Map<String, Object> paramValues;//各个参数的值，key是参数的编码

		Map<String, IMonthDataCreator> dataCreatorMap;//java实现类的map，key是日报项目的code（只有java类的项目put进去），value是实现类

		ItemCopyVO[] monthItemVOs;//月报项目，要求按计算顺序排列
		Map<String, AggShiftVO> aggShiftMap;//所有班次的aggvo的map
		Map<String, ShiftVO> shiftMap;//所有班次的map

		LeaveTypeCopyVO[] leaveCopyVOs;
		AwayTypeCopyVO[] awayCopyVOs;
		OverTimeTypeCopyVO[] overCopyVOs;
		ShutDownTypeCopyVO[] shutCopyVOs;

		//		// 所有人员的排班
		//		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap;
		//		// 所有人员的timedata数据,第一个string是人员主键，第二个UFLiteralDate是日期，value是timedatavo
		//		Map<String, Map<UFLiteralDate, TimeDataVO>> timedataMap=null;
		//		// 所有人员的lateearly数据
		//		Map<String, Map<UFLiteralDate, LateEarlyVO>> lateearlyMap=null;
		//		// 所有人员的考勤档案数据,key是人员主键，value是在这段时间内的考勤档案vo(一般情况下只有一条。如果有多条，那么已经按时间先后排好序)
		//		Map<String, List<TBMPsndocVO>> tbmPsndocMap = null;
		//		// 所有人员的日报的主表主键,第一个string是人员主键pk_psndoc,第二个string是日期，第三个string是pk_daystat。这个map的主要作用是为后面的daystatb子表生成做准备
		//		Map<String, Map<String, String>> daystatPKMap;
		double workDayLength;
	}

	@Override
	public MonthStatVO[] generate(String pk_org, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		generate(pk_org, fromWhereSQL, year, month);

		MonthStatVO[] vos = queryByCondition(pk_org, fromWhereSQL, year, month, showNoDataRecord);
		return vos;

	}

	/**
	 * 更新父部门和管理者
	 * @param vos
	 * @throws BusinessException 
	 */
	private void updateSrcDeptAndMng(MonthStatVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		for (MonthStatVO monthStatVO : vos) {
			String[] deptAndMng = getFatherDeptAndMng(monthStatVO.getPk_dept());
			monthStatVO.setMngdept(deptAndMng[0]);
			monthStatVO.setMngpsndoc(deptAndMng[1]);
			if (monthStatVO.getApprovestatus() == null) {
				monthStatVO.setApprovestatus(ApproveStateEnum.NOSTATE);//自由态
			}
			monthStatVO.setBilltype("6407");
		}
		getDao().updateVOArray(vos, new String[] { "mngdept", "mngpsndoc", "approvestatus", "billtype" });

	}

	@Override
	public MonthStatVO[] queryByConditionAndDept(String pk_dept, boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		DeptVO deptVO = (DeptVO) dao.retrieveByPK(DeptVO.class, pk_dept);
		OrgVO orgVO = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(deptVO.getPk_org());
		return queryByCondition(orgVO.getPk_org(), TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL), year, month, showNoDataRecord);
	}

	@Override
	public TBMPsndocVO[] queryUnGenerateByConditionAndDept(String pk_dept, boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		DeptVO deptVO = (DeptVO) dao.retrieveByPK(DeptVO.class, pk_dept);
		OrgVO orgVO = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(deptVO.getPk_org());
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(orgVO.getPk_org(), year, month);
		UFLiteralDate periodBeginDate = periodVO.getBegindate();
		UFLiteralDate periodEndDate = periodVO.getEnddate();
		fromWhereSQL = TBMPsndocSqlPiecer.ensureTBMPsndocTable(fromWhereSQL);
		String alias = fromWhereSQL.getTableAliasByAttrpath(".");
		String extraCond =
				alias + "." + TBMPsndocVO.PK_PSNDOC + " not in(" + "select " + MonthStatVO.PK_PSNDOC + " from " + MonthStatVO.getDefaultTableName() + " where " + MonthStatVO.TBMYEAR + "='" + year + "' and " + MonthStatVO.TBMMONTH + "='" + month + "' and " + MonthStatVO.ISEFFECTIVE + "='Y')";
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(extraCond, fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		//tsy 添加权限
		fromWhereSQL = addPsnPower(fromWhereSQL);
		ITBMPsndocQueryService queryService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		return queryService.queryLatestByCondition(null, fromWhereSQL, periodBeginDate, periodEndDate);
	}

	@Override
	public MonthStatVO queryByPsn(String pk_psndoc, String year, String month) throws BusinessException {
		return queryByPsn(pk_psndoc, null, year, month);
	}

	@Override
	public MonthStatVO[] queryByPsn(String pk_psndoc, String year) throws BusinessException {
		String cond = MonthStatVO.PK_PSNDOC + "=? and " + MonthStatVO.TBMYEAR + "=? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(year);
		String orderby = MonthStatVO.TBMYEAR + "," + MonthStatVO.TBMMONTH;
		return queryByPsn(pk_psndoc, cond, orderby, para);
	}

	protected MonthStatVO[] queryByPsn(String pk_psndoc, String cond, String orderby, SQLParameter para) throws BusinessException {

		//最新的一条考勤档案记录
		TBMPsndocVO latestPsndocVO =
				NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class).queryByPsndocAndDateTime(pk_psndoc, new UFDateTime());
		String pk_org = latestPsndocVO.getPk_org();
		MonthStatDAO dao = new MonthStatDAO();
		MonthStatVO[] vos = null;

		try {
			vos =
					dao.query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk", "srcid", "mngdept", "mngpsndoc" }, cond, orderby, para);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		if (ArrayUtils.isEmpty(vos))
			return null;
		processPsnjob4Psn(pk_psndoc, vos);
		return vos;
	}

	/**
	 * 为员工自助的月报记录处理pk_psnjob 因为员工自助的月报都是直接从月报表中查询的，没有关联考勤档案，因此pk_psnjob字段无值，导致自助 的界面上无法显示任职的相关信息
	 * 
	 * @param pk_psndoc
	 * @param monthstatVOs
	 * @throws BusinessException
	 */
	private void processPsnjob4Psn(String pk_psndoc, MonthStatVO[] monthstatVOs) throws BusinessException {
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		for (MonthStatVO monthstatVO : monthstatVOs) {
			String pk_org = monthstatVO.getPk_org();
			String year = monthstatVO.getTbmyear();
			String month = monthstatVO.getTbmmonth();
			PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
			if (periodVO == null)
				continue;
			TBMPsndocVO psndocVO = psndocService.queryLatestByPsndocDate(pk_org, pk_psndoc, periodVO.getBegindate(), periodVO.getEnddate());
			if (psndocVO == null)
				continue;
			monthstatVO.setPk_psnjob(psndocVO.getPk_psnjob());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public MonthWorkVO[] queryMonthWorkVOsByPsn(String pk_org, String pk_psndoc, String year, String month) throws BusinessException {
		String cond =
				MonthWorkVO.PK_MONTHSTAT + " in (select " + MonthStatVO.PK_MONTHSTAT + " from " + MonthStatVO.getDefaultTableName() + " where " + MonthStatVO.PK_ORG + "=? and " + MonthStatVO.PK_PSNDOC + "=? and " + MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=?)";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(pk_psndoc);
		para.addParam(year);
		para.addParam(month);
		return (MonthWorkVO[]) CommonUtils.toArray(MonthWorkVO.class, new BaseDAO().retrieveByClause(MonthWorkVO.class, cond, para));
	}

	/**
	 * 月报生成结束时对存在考勤异常的人员发送提醒消息
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param curPeriod
	 * @throws BusinessException
	 */
	//V63以后使用平台的消息通知发送方式
	private void sendMessage(String pk_org, String[] pk_psndocs, PeriodVO curPeriod, CalSumParam calSumParam) throws BusinessException {
		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, curPeriod, calSumParam);//异常人员主键集合
		if (noticePsnPkset.size() <= 0) {
			return;
		}
		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(noticePsnPks);
		String cond = "pk_psndoc in (" + insql + " )";
		//查询出所有的psndocvo
		PsndocVO[] psndocvos =
				(PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsndocVO.class, cond);
		if (ArrayUtils.isEmpty(psndocvos))
			return;
		//查询组织
		OrgVO org = (OrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, OrgVO.class, pk_org);
		//查询月报
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(noticePsnPks);
		MonthStatVO[] monthStatVOs = queryByCondition(pk_org, fromWhereSQL, curPeriod.getTimeyear(), curPeriod.getTimemonth(), true);
		Map<String, MonthStatVO> monthMap = CommonUtils.toMap(MonthStatVO.PK_PSNDOC, monthStatVOs);

		HashMap<String, UserVO[]> userMap =
				NCLocator.getInstance().lookup(IUserPubService.class).batchQueryUserVOsByPsnDocID(pk_psndocs, null);
		IURLGenerator IurlDirect = NCLocator.getInstance().lookup(IURLGenerator.class);
		IHRMessageSend messageSendServer = NCLocator.getInstance().lookup(IHRMessageSend.class);
		//按人员循环发送消息
		for (PsndocVO psndocVO : psndocvos) {
			HRBusiMessageVO messageVO = new HRBusiMessageVO();
			messageVO.setBillVO(monthMap.get(psndocVO.getPk_psndoc()));//元数据属性解析
			messageVO.setMsgrescode(TaMessageConst.MONTHSTATEXCPMSG);
			//业务变量
			Hashtable<String, Object> busiVarValues = new Hashtable<String, Object>();
			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
			SSOInfo ssinfo = new SSOInfo();
			if (!ArrayUtils.isEmpty(users)) {
				// ssinfo.setUserPassword(users[0].getUser_password());
				ssinfo.setUserPK(users[0].getCuserid());
			}
			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
			ssinfo.setFuncode("E20200910");//E20200910 考勤月报的补考勤的功能节点号

			String urlTitle = IurlDirect.buildURLString(ssinfo);
			//				IurlDirect.buildHTML(ssinfo, ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0088")/*@res "员工自助补考勤!"*/);
			busiVarValues.put("url", urlTitle);
			busiVarValues.put("CURRUSERNAME", MultiLangHelper.getName(psndocVO));
			busiVarValues.put("CURRCORPNAME", MultiLangHelper.getName(org));
			messageVO.setBusiVarValues(busiVarValues);
			messageVO.setPkorgs(new String[] { pk_org });
			messageVO.setReceiverPkUsers(ArrayUtils.isEmpty(users) ? null : new String[] { users[0].getPrimaryKey() });

			messageSendServer.sendBuziMessage_RequiresNew(messageVO);
		}
	}

	//得到月报异常人员
	private Set<String> getExceptionPsns(String pk_org, String[] pk_psndocs, PeriodVO curPeriod, CalSumParam calSumParam)
			throws BusinessException {

		Set<String> exceptionPsnPkSet = new HashSet<String>();
		ITBMPsndocQueryService tbmPsnQueryS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		//查询考勤档案  找到对应的考勤方式（机器考勤or手工考勤）
		Map<String, List<TBMPsndocVO>> tbmPsnMap =
				tbmPsnQueryS.queryTBMPsndocMapByPsndocs(pk_org, pk_psndocs, curPeriod.getBegindate(), curPeriod.getEnddate(), true);
		if (MapUtils.isEmpty(tbmPsnMap)) {
			return null;
		}
		ITimeDataQueryService timDataQuery = NCLocator.getInstance().lookup(ITimeDataQueryService.class);
		//机器考勤数据
		Map<String, Map<UFLiteralDate, TimeDataVO>> machTimeDateMap =
				timDataQuery.queryVOMapByPsndocInSQLForMonth(pk_org, curPeriod.getBegindate(), curPeriod.getEnddate(), calSumParam.psndocInSQL);
		//循环查找机器考勤数据 的异常情况，若含有和月报异常发送邮件设置相匹配的 则把 该人员的pk加入的发送邮件的集合内
		if (MapUtils.isNotEmpty(machTimeDateMap)) {
			for (String pk_psndoc : machTimeDateMap.keySet()) {
				Map<UFLiteralDate, TimeDataVO> dataMap = machTimeDateMap.get(pk_psndoc);
				for (UFLiteralDate date : dataMap.keySet()) {
					TimeDataVO timeDataVO = dataMap.get(date);
					if (!timeDataVO.isNormal()) {
						if (calSumParam.timeRuleVO.isAbsentNotice()) {
							if (this.isAbsent(timeDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.isLateNotice()) {
							if (this.isLate(timeDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.isEarlyNotice()) {
							if (this.isEarly(timeDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.isMidOutNotice()) {
							if (timeDataVO.getIsMidOut()) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
					}
				}
			}
		}

		ILateEarlyQueryService handTimeDataQuery = NCLocator.getInstance().lookup(ILateEarlyQueryService.class);
		//手工考勤数据
		Map<String, Map<UFLiteralDate, LateEarlyVO>> handTimeDateMap =
				handTimeDataQuery.queryVOMapByPsndocInSQLForMonth(pk_org, curPeriod.getBegindate(), curPeriod.getEnddate(), calSumParam.psndocInSQL);
		if (MapUtils.isNotEmpty(handTimeDateMap)) {
			for (String pk_psndoc : handTimeDateMap.keySet()) {
				Map<UFLiteralDate, LateEarlyVO> dataMap = handTimeDateMap.get(pk_psndoc);
				for (UFLiteralDate date : dataMap.keySet()) {
					LateEarlyVO handDataVO = dataMap.get(date);
					if (!handDataVO.isNormal()) {
						if (calSumParam.timeRuleVO.isAbsentNotice()) {
							if (this.isAbsent(handDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.isLateNotice()) {
							if (this.isLate(handDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.isEarlyNotice()) {
							if (this.isEarly(handDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						//手工考勤没有中途外出这一项,所以这一段不再需要
						//						if(calSumParam.timeRuleVO.isMidOutNotice()){
						//							if(handDataVO.getAbsenthour().toDouble()>0){
						//								exceptionPsnPkSet.add(pk_psndoc);
						//							}
						//						}

					}
				}
			}

		}
		return exceptionPsnPkSet;
	}

	/**
	 * //判断一条考勤是否 有迟到现象 true 为有迟到现象
	 */
	private boolean isLate(Object timeDataVO) {
		if (timeDataVO instanceof TimeDataVO) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIslate(i) == 1)
					return true;
			}
		}
		if (timeDataVO instanceof LateEarlyVO) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if (timedata.getLatecount() > 0 && timedata.getLatelength().toDouble() >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * //判断一条考勤是否 有早退现象 true 为有早退现象
	 */
	private boolean isEarly(Object timeDataVO) {
		if (timeDataVO instanceof TimeDataVO) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIsearly(i) == 1)
					return true;
			}
		}
		if (timeDataVO instanceof LateEarlyVO) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if (timedata.getEarlycount() > 0 && timedata.getEarlylength().toDouble() >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * //判断一条考勤是否 有矿工现象 true 为包含矿工
	 */
	private boolean isAbsent(Object timeDataVO) {
		if (timeDataVO instanceof TimeDataVO) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIsabsent(i) == 1 || timedata.getIsearlyabsent(i) == 1 || timedata.getIslateabsent(i) == 1)
					return true;
			}
		}
		if (timeDataVO instanceof LateEarlyVO) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if (timedata.getAbsenthour().toDouble() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String beginYear, String beginMonth, String endYear, String endMonth)
			throws BusinessException {
		beginMonth = ensureMonth(beginMonth);
		endMonth = ensureMonth(endMonth);
		//如果开始=结束，则直接按年月查
		if (beginYear.equals(endYear) && beginMonth.equals(endMonth)) {
			MonthStatVO[] vos = queryByPsnAndNatualYearMonth(pk_psndoc, beginYear, beginMonth);
			processExtraInfoForSelf(vos);
			return vos;
		}
		//如果begin>end，则交换一下,增强系统的鲁棒性
		String beginYearMonth = beginYear + beginMonth;
		String endYearMonth = endYear + endMonth;
		if (beginYearMonth.compareTo(endYearMonth) > 0) {
			beginYear = endYearMonth.substring(0, 4);
			beginMonth = endYearMonth.substring(4, 6);
			endYear = beginYearMonth.substring(0, 4);
			endMonth = beginYearMonth.substring(4, 6);
		}
		//先简单地按照每个月都查询一次
		MonthStatVO[] vos = simplyQueryPsnMonthstat(pk_psndoc, beginYear, beginMonth, endYear, endMonth);
		if (ArrayUtils.isEmpty(vos))
			return null;
		//然后进行处理，包括：去掉重复的（有可能同一个组织内，两个自然月查出的月报都是相同的），中间有空档，查出空档
		vos = postProcessPsnMonthStat(vos);
		processExtraInfoForSelf(vos);
		return vos;
	}

	private MonthStatVO[] simplyQueryPsnMonthstat(String pk_psndoc, String beginYear, String beginMonth, String endYear, String endMonth)
			throws NumberFormatException, BusinessException {
		//依次查询从begin到end之间的每一个月
		//如果不垮年，则从开始月查询到结束月
		List<MonthStatVO> retList = new ArrayList<MonthStatVO>();
		int beginMonthInt = getMonth(beginMonth);
		int endMonthInt = getMonth(endMonth);
		if (beginYear.equals(endYear)) {//不垮年
			queryMonths(pk_psndoc, Integer.parseInt(beginYear), beginMonthInt, endMonthInt, retList);
		}
		//如果跨年，则按三种情况处理：第一年，中间年，最后一年，
		else {
			int beginYearInt = Integer.parseInt(beginYear);
			int endYearInt = Integer.parseInt(endYear);
			for (int year = beginYearInt; year <= endYearInt; year++) {
				int month1 = year == beginYearInt ? beginMonthInt : 1;
				int month2 = year == endYearInt ? endMonthInt : 12;
				queryMonths(pk_psndoc, year, month1, month2, retList);
			}
		}
		return retList.toArray(new MonthStatVO[0]);
	}

	protected MonthStatVO queryByPsn(String pk_psndoc, String pk_org, String year, String month) throws BusinessException {
		String cond = MonthStatVO.PK_PSNDOC + "=? and " + MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=?";
		if (StringUtils.isNotEmpty(pk_org))
			cond += " and " + MonthStatVO.PK_ORG + "=?";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(year);
		para.addParam(month);
		if (StringUtils.isNotEmpty(pk_org))
			para.addParam(pk_org);
		MonthStatVO[] vos = queryByPsn(pk_psndoc, cond, null, para);
		if (ArrayUtils.isEmpty(vos))
			return null;
		processPsnjob4Psn(pk_psndoc, vos);
		return vos[0];
	}

	private MonthStatVO[] postProcessPsnMonthStat(MonthStatVO[] vos) throws BusinessException {
		if (vos.length == 1)
			return vos;
		String pk_psndoc = vos[0].getPk_psndoc();
		//按hr组织分组
		Map<String, MonthStatVO[]> orgMap = CommonUtils.group2ArrayByField(MonthStatVO.PK_ORG, vos);
		YearMonthComparator comparator = new YearMonthComparator();
		for (String pk_org : orgMap.keySet()) {
			MonthStatVO[] orgMonthVOs = orgMap.get(pk_org);
			if (vos.length == 1)
				continue;
			//首先按年度期间进行排序
			Arrays.sort(orgMonthVOs, comparator);
			//除去年度相同的记录（有可能会发生按不同的自然月查，返回相同月报记录的情况，例如，一个期间很长，从3.20到4.30，那么查自然月3月和4月，都会查出这条月报记录，需要干掉其中一条，不然用户会看到两条一模一样的月报）
			List<MonthStatVO> list = new ArrayList<MonthStatVO>(orgMonthVOs.length);
			Set<String> yearMonthSet = new HashSet<String>();
			for (MonthStatVO vo : orgMonthVOs) {
				String yearMonth = vo.getTbmyear() + vo.getTbmmonth();
				if (yearMonthSet.contains(yearMonth))
					continue;
				yearMonthSet.add(yearMonth);
				list.add(vo);
			}
			//走到这里，已经没有重复的月报记录了,需要查询第一个月报到最后一个月报之间空着的记录
			//如果不垮考勤年，则很好判断是否有空着的记录
			int firstYearInt = Integer.parseInt(orgMonthVOs[0].getTbmyear());
			int lastYearInt = Integer.parseInt(orgMonthVOs[orgMonthVOs.length - 1].getTbmyear());
			int firstMonthInt = getMonth(orgMonthVOs[0].getTbmmonth());
			int lastMonthInt = getMonth(orgMonthVOs[orgMonthVOs.length - 1].getTbmmonth());
			if (firstYearInt == lastYearInt) {
				//如果两个月是同一月，或者紧挨着，没必要处理(不跳出循环，需要加到orgMap中)
				//				if(lastMonthInt-firstMonthInt<=1)
				//					continue;
				if (lastMonthInt - firstMonthInt > 1) {
					for (int month = firstMonthInt + 1; month < lastMonthInt; month++) {
						String strMonth = getMonth(month);
						if (yearMonthSet.contains(firstYearInt + strMonth))
							continue;
						//还没有这个月的月报，则需要查询
						MonthStatVO statvo = queryByPsn(pk_psndoc, pk_org, Integer.toString(firstYearInt), strMonth);
						if (statvo != null)
							list.add(statvo);
					}
				}
				Collections.sort(list, comparator);
			} else {//如果跨年，则比较麻烦一点，因为考勤期间不一定只有12个，可能有13，14个，因此需要查询考勤期间表
					//查询这些考勤年的所有期间
				Map<String, PeriodVO[]> periodMap =
						PeriodServiceFacade.queryByYear(pk_org, Integer.toString(firstYearInt), Integer.toString(lastYearInt));
				for (int year = firstYearInt; year <= lastYearInt; year++) {
					PeriodVO[] periodVOs = periodMap.get(Integer.toString(year));
					if (ArrayUtils.isEmpty(periodVOs))
						continue;
					Arrays.sort(periodVOs, comparator);
					int beginMonthInt = year == firstYearInt ? firstMonthInt : 1;
					int endMonthInt = year == lastYearInt ? getMonth(periodVOs[periodVOs.length - 1].getTimemonth()) : lastMonthInt;
					for (int month = beginMonthInt; month <= endMonthInt; month++) {
						String strMonth = getMonth(month);
						if (yearMonthSet.contains(firstYearInt + strMonth))
							continue;
						//还没有这个月的月报，则需要查询
						MonthStatVO statvo = queryByPsn(pk_psndoc, pk_org, Integer.toString(firstYearInt), strMonth);
						if (statvo != null)
							list.add(statvo);
					}
				}
			}
			orgMap.put(pk_org, list.toArray(new MonthStatVO[0]));
		}
		Collection<MonthStatVO[]> c = orgMap.values();
		List<MonthStatVO> list = new ArrayList<MonthStatVO>();
		for (MonthStatVO[] array : c) {
			list.addAll(Arrays.asList(array));
		}
		Collections.sort(list, comparator);
		return list.toArray(new MonthStatVO[0]);
	}

	private void queryMonths(String pk_psndoc, int year, int beginMonth, int endMonth, List<MonthStatVO> retList) throws BusinessException {
		for (int month = beginMonth; month <= endMonth; month++) {
			MonthStatVO[] vos = queryByPsnAndNatualYearMonth(pk_psndoc, Integer.toString(year), getMonth(month));
			if (ArrayUtils.isEmpty(vos))
				continue;
			retList.addAll(Arrays.asList(vos));
		}
	}

	private String ensureMonth(String month) {
		if (month.length() == 1)
			month = "0" + month;
		if (month.compareTo("12") > 0)
			month = "12";
		return month;
	}

	private int getMonth(String month) {
		if (month.length() == 0)
			return Integer.parseInt(month);
		if (month.startsWith("0"))
			return Integer.parseInt(month.substring(1));
		return Integer.parseInt(month);
	}

	private String getMonth(int month) {
		if (month >= 10)
			return Integer.toString(month);
		return "0" + month;
	}

	protected MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String year, String month) throws BusinessException {
		//首先确定自然年月的日期范围
		IDateScope monthDateScope = getNatualMonthBeginEndDate(year, month);
		UFLiteralDate beginDate = monthDateScope.getBegindate();
		UFLiteralDate endDate = monthDateScope.getEnddate();
		//查询此人在此日期范围内所有的考勤档案
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		//增强鲁棒性，扩大查询的日期范围，防止有些月报因为自然月与考勤档案无交集而永远查不出来
		TBMPsndocVO[] psndocVOs =
				psndocService.queryTBMPsndocVOsByPsndocDate(pk_psndoc, beginDate.getDateBefore(30), endDate.getDateAfter(30));
		if (ArrayUtils.isEmpty(psndocVOs))
			return null;
		//按HR组织分组
		Map<String, TBMPsndocVO[]> orgMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_ORG, psndocVOs);
		//		String cond = MonthStatVO.PK_PSNDOC+"=? and "+MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=?";
		String cond =
				MonthStatVO.PK_ORG + "=? and " + MonthStatVO.PK_PSNDOC + "=? and " + MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=?";
		SQLParameter para = new SQLParameter();
		MonthStatDAO dao = new MonthStatDAO();
		List<MonthStatVO> monthVOList = new ArrayList<MonthStatVO>();
		//按组织循环处理
		for (String pk_org : orgMap.keySet()) {
			PeriodVO[] periodVOs = PeriodServiceFacade.queryPeriodsByDateScope(pk_org, beginDate, endDate);
			if (ArrayUtils.isEmpty(periodVOs))
				continue;
			// 可递归找存在的记录
			for (int i = ArrayUtils.getLength(periodVOs) - 1; i >= 0; i--) {
				PeriodVO latestPeriodVO = periodVOs[i];
				TBMPsndocVO[] psnVOs = orgMap.get(pk_org);
				if (!DateScopeUtils.isCross(psnVOs, latestPeriodVO))
					continue;
				//查询此人此月的月报
				para.clearParams();
				para.addParam(pk_org);
				para.addParam(pk_psndoc);
				para.addParam(latestPeriodVO.getTimeyear());
				para.addParam(latestPeriodVO.getTimemonth());
				MonthStatVO[] vos = null;
				try {

					vos =
							dao.query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE }, cond, null, para);
				} catch (DbException e) {
					Logger.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				} catch (ClassNotFoundException e) {
					Logger.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				}
				if (ArrayUtils.isEmpty(vos))
					continue;
				DateScopeUtils.sort(psnVOs);
				//因为查询考勤档案时，扩大了查询的日期范围，因此psnVOs的最后一条不一定是period范围内的，这里要特殊处理一下
				for (int j = psnVOs.length - 1; j >= 0; j--) {
					if (DateScopeUtils.isCross(psnVOs[j], latestPeriodVO))
						vos[0].setPk_psnjob(psnVOs[j].getPk_psnjob());
				}
				monthVOList.add(vos[0]);
				break;
			}
		}

		return monthVOList.toArray(new MonthStatVO[0]);
	}

	/**
	 * 自助需要处理也写额外的信息项，例如姓名，公司名，部门名，以及考勤项目+休假出差加班类别
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void processExtraInfoForSelf(MonthStatVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		//为了减少查询次数，建立四个map
		//<pk_psnjob,PsnJobVO>
		Map<String, PsnJobVO> psnjobMap = null;
		//<pk_org,OrgVO>
		Map<String, OrgVO> orgMap = null;
		//<pk_dept,HRDeptVO>
		Map<String, HRDeptVO> deptMap = null;
		//<pk_hrorg,ViewOrderVO[]>
		Map<String, ViewOrderVO[]> viewOrderMap = new HashMap<String, ViewOrderVO[]>();
		BaseDAO dao = new BaseDAO();
		IViewOrderQueryService viewOrderService = NCLocator.getInstance().lookup(IViewOrderQueryService.class);
		nc.vo.hi.psndoc.PsndocVO psndocVO =
				(nc.vo.hi.psndoc.PsndocVO) dao.retrieveByPK(nc.vo.hi.psndoc.PsndocVO.class, vos[0].getPk_psndoc());
		String psnName = psndocVO.getMultiLangName();
		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			//查询psnjob
			String psnjobInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(vos, MonthStatVO.PK_PSNJOB));
			String cond = PsnJobVO.PK_PSNJOB + " in(" + psnjobInSQL + ")";
			PsnJobVO[] psnjobVOs = CommonUtils.retrieveByClause(PsnJobVO.class, dao, cond);
			psnjobMap = CommonUtils.toMap(PsnJobVO.PK_PSNJOB, psnjobVOs);
			//查询orgvo
			String orgInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(psnjobVOs, PsnJobVO.PK_ORG));
			cond = OrgVO.PK_ORG + " in(" + orgInSQL + ")";
			OrgVO[] orgVOs = CommonUtils.retrieveByClause(OrgVO.class, dao, cond);
			orgMap = CommonUtils.toMap(OrgVO.PK_ORG, orgVOs);
			//查询deptvo
			String deptInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(psnjobVOs, PsnJobVO.PK_DEPT));
			cond = HRDeptVO.PK_DEPT + " in(" + deptInSQL + ")";
			HRDeptVO[] deptVOs = CommonUtils.retrieveByClause(HRDeptVO.class, dao, cond);
			deptMap = CommonUtils.toMap(HRDeptVO.PK_DEPT, deptVOs);

		} finally {
			if (isc != null)
				isc.clear();
		}
		for (MonthStatVO vo : vos) {
			vo.setPsnName(psnName);

			String pk_psnjob = vo.getPk_psnjob();
			PsnJobVO psnjobVO = psnjobMap.get(pk_psnjob);

			String pk_org = psnjobVO.getPk_org();
			OrgVO orgVO = orgMap.get(pk_org);
			vo.setOrgName(MultiLangUtil.getSuperVONameOfCurrentLang(orgVO, OrgVO.NAME, null));

			String pk_dept = psnjobVO.getPk_dept();
			HRDeptVO deptVO = deptMap.get(pk_dept);
			vo.setDeptName(deptVO.getMultilangName());

			String pk_hrorg = vo.getPk_org();
			ViewOrderVO[] viewOrderVOs = null;
			if (viewOrderMap.containsKey(pk_hrorg)) {
				viewOrderVOs = viewOrderMap.get(pk_hrorg);
			} else {
				viewOrderVOs = viewOrderService.queryViewOrder(pk_org, ViewOrderVO.FUN_TYPE_MONTH, ViewOrderVO.REPORT_TYPE_PSN, false);
				viewOrderMap.put(pk_hrorg, viewOrderVOs);
			}
			vo.setViewOrderVOs(viewOrderVOs);
		}
	}

	/**
	 * 获取一个自然月的第一天和最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private IDateScope getNatualMonthBeginEndDate(String year, String month) {

		String strMonth = month.length() == 1 ? ("0" + month) : month;
		UFLiteralDate beginDate = UFLiteralDate.getDate(year + "-" + strMonth + "-01");
		int[] dayCount = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int intMonth = getMonth(month) - 1;
		int daysCount = dayCount[intMonth];
		if (intMonth == 1) {
			int yearInt = Integer.parseInt(year);
			if (yearInt % 400 == 0 || (yearInt % 4 == 0 && yearInt % 100 != 0))
				daysCount = 29;
		}
		UFLiteralDate endDate = UFLiteralDate.getDate(year + "-" + strMonth + "-" + daysCount);
		return new DefaultDateScope(beginDate, endDate);
	}

	@Override
	public DeptMonthStatVO[] queryDMSVOByCondition(LoginContext context, String[] pk_depts, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(pk_depts)) {
			//将部门条件加到客户端的条件中去
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPkArrayQuerySQL(pk_depts, fromWhereSQL);
		}
		MonthStatVO[] monthStatVOs = queryByCondition(context, fromWhereSQL, year, month, showNoDataRecord);
		if (ArrayUtils.isEmpty(monthStatVOs))
			return null;
		//将人员数据按部门分组，并且顺序不能改变。即，返回的DeptMonthStatVO[]中vo的顺序，要与MonthStatVO中部门的顺序一致
		List<DeptMonthStatVO> retList = new ArrayList<DeptMonthStatVO>();//list的作用是保证部门vo的顺序
		Map<String, DeptMonthStatVO> statMap = new HashMap<String, DeptMonthStatVO>();//map的作用是取vo方便
		for (MonthStatVO monthStatVO : monthStatVOs) {
			String pk_dept = monthStatVO.getPk_dept();
			DeptMonthStatVO deptVO = statMap.get(pk_dept);
			if (deptVO == null) {
				deptVO = new DeptMonthStatVO();
				deptVO.setPk_dept(pk_dept);

				//设置版本信息
				deptVO.setPk_dept_v(monthStatVO.getPk_dept_v());
				deptVO.setPk_org_v(monthStatVO.getPk_org_v());

				deptVO.setTbmyear(monthStatVO.getTbmyear());
				deptVO.setTbmmonth(monthStatVO.getTbmmonth());

				//				deptVO.setMonthworkVOs(monthStatVO.getMonthworkVOs());

				statMap.put(pk_dept, deptVO);
				retList.add(deptVO);
			}
			//把个人月报数据合并为部门月报数据
			deptVO.mergePsnMonthStatVO(monthStatVO);
		}
		return retList.toArray(new DeptMonthStatVO[0]);
	}

	@Override
	public MonthStatVO[] queryByDeptPeriod(LoginContext context, String pk_dept, String year, String month) throws BusinessException {
		if (context == null)
			return null;
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, null);
		//		String pk_org = context.getPk_org();
		//		//首先查询期间的起止日期
		//		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		//		if(periodVO==null)
		//			throw new BusinessException(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0090") 
		//					 /*@res "期间{0}不存在!"*/, year+"-"+month);
		//		UFLiteralDate beginDate = periodVO.getBegindate();
		//		UFLiteralDate endDate = periodVO.getEnddate();
		//		//查询期间内在部门工作过的考勤档案
		//		String tbmpsnSql = " select tbm_psndoc.pk_tbm_psndoc from tbm_psndoc inner join bd_psnjob on tbm_psndoc.pk_psnjob = bd_psnjob.pk_psnjob " +
		//				"where bd_psnjob.pk_dept = '" + pk_dept + "' and tbm_psndoc.pk_org = '" + pk_org + 
		//				"' and (tbm_psndoc.begindate <= '" + endDate + "' and tbm_psndoc.enddate >='" + beginDate + "') ";
		return queryByCondition(context, fromWhereSQL, year, month, true);
	}

	/**
	 */
	@Override
	public MonthStatVO[] queryByCondition(LoginContext context, String[] pks, String year, String month, boolean showNoDataRecord)
			throws BusinessException {

		//pks = pk_psndoc+pk_psnorg
		if (ArrayUtils.isEmpty(pks))
			return null;
		String pk_org = context.getPk_org();
		//首先查询期间的起止日期
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if (periodVO == null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0090")
			/*@res "期间{0}不存在!"*/, year + "-" + month);
		List<String> psnInOrg = new ArrayList<String>();//本组织中的人员
		List<String> psnInAdminorg = new ArrayList<String>();//管理组织在本组织的人员
		for (String pk : pks) {
			String[] split = pk.split(",");
			if (context.getPk_org().equals(split[1])) {
				psnInOrg.add(split[0]);
			} else {
				psnInAdminorg.add(split[0]);
			}
		}
		//本组织的月报记录
		MonthStatVO[] vos1 = null;
		if (CollectionUtils.isNotEmpty(psnInOrg)) {
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(psnInOrg.toArray(new String[0]));
			//tsy 添加权限
			//			fromWhereSQL = addPsnPower(fromWhereSQL);
			vos1 = queryByConditionAndOrg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		}
		//管理组织在本组织的月报记录
		MonthStatVO[] vos2 = null;
		if (CollectionUtils.isNotEmpty(psnInAdminorg)) {
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(psnInAdminorg.toArray(new String[0]));
			//tsy 添加权限
			fromWhereSQL = addPsnPower(fromWhereSQL);
			vos2 = queryByConditionAndAndminorg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		}
		return (MonthStatVO[]) ArrayUtils.addAll(vos1, vos2);
	}

	@Override
	protected String getBillType() {
		return "6407";
	}

	@Override
	protected Class<?> getHeadVOClass() {
		return MonthStatVO.class;
	}

	@Override
	protected String getBillCodeFieldName() {
		return "billno";
	}

	@Override
	protected nc.vo.ta.wf.pub.TaWorkFlowManager.UserValueConfig getUserValueConfig() {
		TaWorkFlowManager<OvertimehVO, OvertimebVO>.UserValueConfig config = new TaWorkFlowManager.UserValueConfig();
		config.setBillCodeFieldName("billno");
		config.setApproveStateFieldName("approvestatus");
		config.setFieldCodes(new String[] { "pk_org", "pk_group", "pk_psndoc", "tbmyear", "tbmmonth", "iseffective", "isapprove", "isuseful", "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "ime approvedate", "transtype", "billtype", "transtypepk", "srcid", "mngdept", "mngpsndoc" });
		return config;
	}

	@Override
	protected void syncHeadInfoToBody(AggregatedValueObject[] paramArrayOfAggregatedValueObject) {

	}

	@Override
	protected MonthStatVO createMainVO(MonthStatbVO[] bvo) {
		return null;
	}

	/**
	 * 提交
	 * 同部门下的
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public AggMonthStatVO[] doCommit(AggMonthStatVO[] vos) throws BusinessException {
		//		List<MonthStatVO> hvos = new ArrayList<MonthStatVO>();
		for (AggMonthStatVO vo : vos) {
			MonthStatVO billvo = (MonthStatVO) vo.getParentVO();
			MonthStatVO[] hvos =
					(MonthStatVO[]) getDao().retrieveByClause(MonthStatVO.class, "tbmyear='" + billvo.getTbmyear() + "' and tbmmonth='" + billvo.getTbmmonth() + "' and pk_org='" + billvo.getPk_org() + "' and srcid='" + billvo.getPk_monthstat() + "' ").toArray(new MonthStatVO[0]);
			for (int i = 0; i < hvos.length; i++) {
				MonthStatVO monthStatVO = hvos[i];
				monthStatVO.setApprovestatus(ApproveStatus.COMMIT);
			}
			getDao().updateVOArray(hvos, new String[] { "approvestatus" });
		}
		return vos;
	}

	/**
	 * 收回
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggMonthStatVO[] unCommit(AggMonthStatVO[] vos) throws BusinessException {
		for (AggMonthStatVO vo : vos) {
			MonthStatVO billvo = (MonthStatVO) vo.getParentVO();
			MonthStatVO[] hvos =
					(MonthStatVO[]) getDao().retrieveByClause(MonthStatVO.class, "tbmyear='" + billvo.getTbmyear() + "' and tbmmonth='" + billvo.getTbmmonth() + "' and pk_org='" + billvo.getPk_org() + "' and srcid='" + billvo.getPk_monthstat() + "' ").toArray(new MonthStatVO[0]);
			for (int i = 0; i < hvos.length; i++) {
				MonthStatVO monthStatVO = hvos[i];
				monthStatVO.setApprovestatus(ApproveStatus.FREE);
				monthStatVO.setSrcid(null);
			}
			getDao().updateVOArray(hvos, new String[] { "approvestatus", "srcid", "mngdept", "mngpsndoc" });
		}
		return vos;
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

	@Override
	public MonthStatVO[] queryCurrentMonthDeptBypk(String pk_org, String pk_monthstat) throws BusinessException {
		MonthStatVO vo = (MonthStatVO) getDao().retrieveByPK(MonthStatVO.class, pk_monthstat);
		String year = vo.getTbmyear();
		String month = vo.getTbmmonth();
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(vo.getPk_org(), year, month);
		if (periodVO == null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0090"/*@res "期间{0}不存在!"*/, year + "-" + month));
		SQLParameter para = new SQLParameter();
		para.addParam(periodVO.getTimeyear());
		para.addParam(periodVO.getTimemonth());
		para.addParam(pk_monthstat);
		MonthStatVO[] dbVOsInOrg = null;
		String cond =  MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=? and srcid=?";
		try {
			dbVOsInOrg =
					new MonthStatDAO().query(vo.getPk_org(), MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk", "srcid", "mngdept", "mngpsndoc" }, cond, null, para);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		return dbVOsInOrg;
	}

	/**
	 * N_6407_APPROVE专用
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggMonthStatVO[] doApprove(AggMonthStatVO[] vos) throws BusinessException {
		//		super.
		if (ArrayUtils.isEmpty(vos))
			return null;
		String pk_monthstat = ((MonthStatVO) vos[0].getParentVO()).getPk_monthstat();
		String pk_org = ((MonthStatVO) vos[0].getParentVO()).getPk_org();
		String year = ((MonthStatVO) vos[0].getParentVO()).getTbmyear();
		String month = ((MonthStatVO) vos[0].getParentVO()).getTbmmonth();
		int approvestatus = ((MonthStatVO) vos[0].getParentVO()).getApprovestatus();
		PeriodServiceFacade.checkCurPeriod(pk_org, year, month);

		StringBuilder sql = new StringBuilder();
		sql.append("update tbm_monthstat set approvestatus =" + approvestatus);
		if (approvestatus == ApproveStatus.APPROVED) {//已完成全部审批
			sql.append(" ,isapprove = 'Y',iseffective='Y',isuseful='Y' ");
		}
		sql.append(" where pk_org='" + pk_org + "' and tbmyear='" + year + "' and tbmmonth='" + month + "' and srcid='" + pk_monthstat + "' ");
		getDao().executeUpdate(sql.toString());

		sql.delete(0, sql.length());
		sql.append(" PK_MONTHSTAT= '" + pk_monthstat + "'");
		AggMonthStatVO[] retvos =
				(AggMonthStatVO[]) query.queryBillOfVOByCond(AggMonthStatVO.class, sql.toString(), false).toArray(new AggMonthStatVO[0]);
		return retvos;
	}

	/**
	 * N_6407_UNAPPROVE专用
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggMonthStatVO[] doUnApprove(AggMonthStatVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		String pk_monthstat = ((MonthStatVO) vos[0].getParentVO()).getPk_monthstat();
		String pk_org = ((MonthStatVO) vos[0].getParentVO()).getPk_org();
		String year = ((MonthStatVO) vos[0].getParentVO()).getTbmyear();
		String month = ((MonthStatVO) vos[0].getParentVO()).getTbmmonth();
		int approvestatus = ((MonthStatVO) vos[0].getParentVO()).getApprovestatus();
		PeriodServiceFacade.checkCurPeriod(pk_org, year, month);

		StringBuilder sql = new StringBuilder();
		sql.append("update tbm_monthstat set approvestatus =" + approvestatus);
		sql.append(",isapprove = 'N',isuseful='N'");
		sql.append(" where pk_org='" + pk_org + "' and tbmyear='" + year + "' and tbmmonth='" + month + "' and srcid='" + pk_monthstat + "' ");
		getDao().executeUpdate(sql.toString());

		sql.delete(0, sql.length());
		sql.append(" PK_MONTHSTAT= '" + pk_monthstat + "'");
		AggMonthStatVO[] retvos =
				(AggMonthStatVO[]) query.queryBillOfVOByCond(AggMonthStatVO.class, sql.toString(), false).toArray(new AggMonthStatVO[0]);
		return retvos;
	}

	/**
	 * 添加权限
	 * @param fromWhereSQL
	 * @return
	 */
	private FromWhereSQL addPsnPower(FromWhereSQL fromWhereSQL) {
		if (fromWhereSQL == null)
			return fromWhereSQL;
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String alias = "T1";
		// 组织权限
		alias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob" + FromWhereSQLUtils.getAttPathPostFix());
		String orgSql =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, alias);
		if (StringUtils.isNotBlank(orgSql)) {
			Logger.debug("添加组织权限:" + orgSql);
			sqlWhereUtil.and(orgSql);
		} else {
			Logger.debug("无组织权限:" + "	HiSQLHelper.getPsnPowerSql(" + PubEnv.getPk_group() + ", " + HICommonValue.RESOUCECODE_ORG + "," + IRefConst.DATAPOWEROPERATION_CODE + ", " + alias + ");");
		}
		// 部门权限
		alias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob" + FromWhereSQLUtils.getAttPathPostFix());
		String deptSql =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, alias);
		if (StringUtils.isNotBlank(deptSql)) {
			Logger.error("部门权限:" + deptSql);
			sqlWhereUtil.and(deptSql);
		} else {
			Logger.debug("无组织权限:" + "	HiSQLHelper.getPsnPowerSql(" + PubEnv.getPk_group() + ", " + HICommonValue.RESOUCECODE_DEPT + "," + IRefConst.DATAPOWEROPERATION_CODE + ", " + alias + ");");
		}
		Logger.error("添加权限后sql:" + sqlWhereUtil.getSQLWhere());
		return new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
	}

	/**
	 * 添加权限
	 * @param fromWhereSQL
	 * @return
	 */
	private String addPsnPower(String where) {
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(where);
		//		String alias = PsnJobVO.getDefaultTableName();
		//		// 组织权限
		//		String orgSql =
		//				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, alias);
		//		if (StringUtils.isNotBlank(orgSql)) {
		//			sqlWhereUtil.and(orgSql);
		//		}
		//		// 部门权限
		//		String deptSql =
		//				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, alias);
		//		if (StringUtils.isNotBlank(deptSql)) {
		//			sqlWhereUtil.and(deptSql);
		//		}
		return sqlWhereUtil.getSQLWhere();
	}

	@Override
	public void update(MonthStatVO[] vos, String[] selFields) throws BusinessException {
		Logger.error("进入MonthStatMaintainImpl.update(MonthStatVO[] vos, String[] selFields)");
		if ((vos == null) || (vos.length <= 0)) {
			return;
		}
		Logger.error("MonthStatMaintainImpl.update(),参数vos.length为"+vos.length+",selFields为"+Arrays.toString(selFields));

		try {
			if ((selFields != null) && (selFields.length > 0)) {
				getDao().updateVOArray(vos, selFields);
			} else {
				getDao().updateVOArray(vos);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);

			throw new BusinessException(e.getMessage());
		}
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

	public MonthStatVO[] queryBySrc(String srcid) throws BusinessException {
		MonthStatVO vo = (MonthStatVO) getDao().retrieveByPK(MonthStatVO.class, srcid);
		String pk_org = vo.getPk_org();
		Map<String, String> aliasMap = new HashMap<String, String>();
		aliasMap.put(".", "tbm_psndoc");//{.=tbm_psndoc, pk_psnjob=T1}
		aliasMap.put("pk_psnjob", "T1");//{.=tbm_psndoc, pk_psnjob=T1}
		FromWhereSQL fromWhereSQL =
				new nc.ui.hr.pub.FromWhereSQL("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob", "T1.pk_org = '" + pk_org + "' ",null,aliasMap);
		String year = vo.getTbmyear();
		String month = vo.getTbmmonth();
		boolean showNoDataRecord = false;
		MonthStatVO[] vos = queryByCondition(pk_org, fromWhereSQL, year, month, showNoDataRecord);
		Map<String, MonthStatVO[]> monthStatVOMap = CommonUtils.group2ArrayByField("srcid", vos);

		return monthStatVOMap.get(srcid);

	}

}
