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
import nc.vo.ml.MultiLangUtil;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
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

	private BaseDAO dao;

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
		//�ݴ�����ʱ�����⿼�ڵ��������ݵ��±������ݣ���ʱҪ��һ���ݴ�����Щ�˵��±����ݲ���
		IMonthlyRecordCreator creator = new MonthStatRecordCreator();
		creator.createMonthlyRecord(curPeriod);
		//Ȩ��
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", "MonthStatGenerate", fromWhereSQL);
		//tsy ���Ȩ��
		fromWhereSQL = addPsnPower(fromWhereSQL);
		//��ȥ��������
		fromWhereSQL = ensureApprove(pk_org, year, month, fromWhereSQL);
		String[] pk_psndocs =
				NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org, fromWhereSQL, curPeriod.getBegindate(), curPeriod.getEnddate());
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		//�����±�
		generate0(pk_org, pk_psndocs, curPeriod);

	}

	/**
	 * �����±�����ʱ�Ĳ�ѯģ��������������������������Ա������Ѿ�ͨ������Ա�ų����⣬��Ϊ���ͨ������Ա�ǲ����ٴμ����
	 * 
	 * @param pk_org
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	private FromWhereSQL ensureApprove(String pk_org, String year, String month, FromWhereSQL fromWhereSQL) throws BusinessException {
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		boolean needApprove = timeRuleVO != null && timeRuleVO.isMonthStatNeedApprove();
		if (!needApprove)//�������Ҫ��ˣ������ų��κ�����
			return fromWhereSQL;
		//������Ҫ���������ų�
		//���ȱ�֤tbm_psndoc��ƴ��sql��
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
		//Java�����������Ŀ�ļ������
		MonthCalParam monthCalParam = MonthStatCalculationHelper.createPara(pk_org, curPeriod);
		InSQLCreator isc = new InSQLCreator();
		try {
			//׼���������
			CalSumParam calSumParam = prepareCalSumParam(pk_org, pk_psndocs, curPeriod, isc, monthCalParam);
			//���㵥��ʱ��
			processBills(calSumParam);
			//�������ʱ��
			processWorkDaysHours(calSumParam);
			//			//�����±���Ŀ
			try {
				processItems(calSumParam);
			} catch (DbException e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			}

			//�±����ɽ���ʱ�Դ��ڿ����쳣����Ա����������Ϣ
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
		//�޸�˵���� Ӧ��ʹ������Ĵ��룬�����޸��� ICommonConst.MODULECODE_HRSS�Ĳ����������ã�ֻ����ʱʹ������Ĵ���
		//ע��v63Ҫ������ʹ������Ĵ���
		//�ж�����ģ���Ƿ�����
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
		//��ɾ���ӱ��¼
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
		//�ٲ����ӱ��¼
		if (insertList.size() > 0)
			dao.insertVOList(insertList);
	}

	//	@SuppressWarnings("unchecked")
	//	private void processWorkDaysHours(CalSumParam calSumParam) throws DAOException{
	//		PeriodVO periodVO = calSumParam.periodVO;
	//		String year = periodVO.getTimeyear();
	//		String month = periodVO.getTimemonth();
	//		String pk_org = calSumParam.pk_org;
	//		String psndocInSQL = calSumParam.psndocInSQL;
	//		int decimalDigits = calSumParam.timeRuleVO.getMreportdecimal().intValue();
	//		
	//		String delSql = "delete from tbm_monthwork where pk_monthstat in (select pk_monthstat from tbm_monthstat " +
	//		"where pk_org='"+pk_org+"' and pk_psndoc in("+psndocInSQL+") and tbmyear='"+year+"' and tbmmonth='"+month+"')";
	//		
	//		//�ȹ���һ����ʱ����ʱ������һ��һ��һ����¼���ֶηֱ��¼�˴��˴���İ�����������ʱ������������ʱ����ʵ�ʳ���ʱ������Ϣ��Ȼ���ڴ���ʱ��Ļ����ϣ��ٽ�һ�����ܵõ�Ӧ���ڰ�������Ϣ
	//		//����ʱ��ֻ�л������ڵ���(�����������ֹ����ڷֿ�ͳ�Ƶ�ԭ���ǣ����������ʵ�ʳ���ʱ�����߼���һ�������ڻ������ڣ�timedata�����Ѿ�����ˣ�
	//		//���ֹ����ڻ���Ҫ����������)
	//		String tempTableSQLMachine = 
	//			"select "+
	////			" psncalendar.pk_org,"+//��֯����
	//			" '"+pk_org+"' as pk_org, "+//��֯����
	//			" psncalendar.pk_shift as pk_shift,"+//�������
	//			" psncalendar.pk_psndoc,"+//��Ա����
	//			" shift.gzsj as shiftgzsj,"+//���ʱ��
	//			" psncalendar.gzsj as calendargzsj,"+//��������ʱ��
	//			" timedata.worklength,"+//ʵ�ʳ���ʱ��
	//			" psncalendar.calendar as calendar"+//����
	//			" from tbm_psncalendar psncalendar"+
	//			" inner join bd_shift shift on shift.pk_shift=psncalendar.pk_shift "+
	//			" left join tbm_timedata timedata on " +
	//			//" timedata.pk_org=psncalendar.pk_org and " +��һ����V6.1��ȥ������Ϊ61�й��������Ѳ���HR��֯����������Ϊҵ��Ԫ������
	//			" timedata.pk_psndoc=psncalendar.pk_psndoc  and timedata.calendar=psncalendar.calendar "+
	//			" where " +
	////			" psncalendar.pk_org='"+pk_org+"' and " +��һ����V6.1��ȥ������Ϊ61�й��������Ѳ���HR��֯����������Ϊҵ��Ԫ������
	//			" psncalendar.pk_psndoc in("+calSumParam.psndocInSQL+") "+
	//			" and psncalendar.pk_shift<>'"+ShiftVO.PK_GX+"' and psncalendar.calendar between '"+
	//			periodVO.getBegindate()+"' and '"+periodVO.getEnddate()+"' "+
	//			//�������exist�������ǽ�psncalendar�п��ܴ��ڵ����������ų�������psncalendar���У����ǿ��ڵ�����Ч��������ݣ�������Ӱ��ͳ�ƽ������ȷ��
	//			" and exists (select 1 from tbm_psndoc psndoc where " +
	////			" psndoc.pk_org=psncalendar.pk_org and " +��һ����V6.1��ȥ������Ϊ61�й��������Ѳ���HR��֯����������Ϊҵ��Ԫ������
	//			" psndoc.pk_psndoc=psncalendar.pk_psndoc and psndoc.pk_org='"+pk_org+"' and psndoc.tbm_prop="+TBMPsndocVO.TBM_PROP_MACHINE+" and "+
	//			" psncalendar.calendar between psndoc.begindate and psndoc.enddate)";
	//		//�ٹ�������һ����ʱ��Ҳ��һ��һ��һ����¼���ṹ���������ʱ����ȫ��ͬ��ֻ����ֻͳ���ֹ����ڵ���
	//		String manualMinus = "";//�ֹ����ڵ�ʵ�ʹ���ʱ���ļ���
	//		Map<String, String> workLenMinusItemMap = TimeDataCalRuleConfig.parseMinusItem(calSumParam.timeRuleVO.getWorklenminusitems());
	//		if(!MapUtils.isEmpty(workLenMinusItemMap)){
	//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_LATE)){
	//				manualMinus+="-isnull(lateearly.latelength,0)";
	//			}
	//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_EARLY)){
	//				manualMinus+="-isnull(lateearly.earlylength,0)";
	//			}
	//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_ABSENT)){
	//				manualMinus+="-isnull(lateearly.absenthour*60,0)";
	//			}
	//			String orihounumSQL = "-isnull((select sum(orihournumusehour)*60 from tbm_daystatb daystatb where daystatb.pk_daystat=(select pk_daystat from tbm_daystat daystat where daystat.pk_psndoc=psncalendar.pk_psndoc and daystat.calendar=psncalendar.calendar) and type={0}),0)";
	//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_LEAVE)){ 
	//				manualMinus+=MessageFormat.format(orihounumSQL, 0);
	//			}
	//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_AWAY)){
	//				manualMinus+=MessageFormat.format(orihounumSQL, 2);
	//			}
	//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_SHUTDOWN)){
	//				manualMinus+=MessageFormat.format(orihounumSQL, 3);
	//			}
	//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_LACTATION)){
	//				manualMinus+="-isnull((select top 1 lactationhour*60 from tbm_leavereg leavereg where leavereg.pk_psndoc=psncalendar.pk_psndoc and psncalendar.calendar between leavereg.leavebegindate and leavereg.leaveenddate and islactation='Y'),0)";
	//			}
	//		}
	//		String tempTableSQLManual = 
	//			"select "+
	//			" '"+pk_org+"' as pk_org, "+//��֯����
	//			" psncalendar.pk_shift as pk_shift,"+//�������
	//			" psncalendar.pk_psndoc,"+//��Ա����
	//			" shift.gzsj as shiftgzsj,"+//���ʱ��
	//			" psncalendar.gzsj as calendargzsj,"+//��������ʱ��
	//			" (psncalendar.gzsj*60"+manualMinus+")*60.0 as worklength,"+//ʵ�ʳ���ʱ��
	//			" psncalendar.calendar as calendar"+//����
	//			" from tbm_psncalendar psncalendar"+
	//			" inner join bd_shift shift on shift.pk_shift=psncalendar.pk_shift "+
	//			" left join tbm_lateearly lateearly on " +
	//			" lateearly.pk_psndoc=psncalendar.pk_psndoc  and lateearly.calendar=psncalendar.calendar "+
	//			" where " +
	//			" psncalendar.pk_psndoc in("+calSumParam.psndocInSQL+") "+
	//			" and psncalendar.pk_shift<>'"+ShiftVO.PK_GX+"' and psncalendar.calendar between '"+
	//			periodVO.getBegindate()+"' and '"+periodVO.getEnddate()+"' "+
	//			//�������exist�������ǽ�psncalendar�п��ܴ��ڵ����������ų�������psncalendar���У����ǿ��ڵ�����Ч��������ݣ�������Ӱ��ͳ�ƽ������ȷ��
	//			" and exists (select 1 from tbm_psndoc psndoc where " +
	//			" psndoc.pk_psndoc=psncalendar.pk_psndoc and psndoc.pk_org='"+pk_org+"' and psndoc.tbm_prop="+TBMPsndocVO.TBM_PROP_MANUAL+" and "+
	//			" psncalendar.calendar between psndoc.begindate and psndoc.enddate)";
	//		//�������ʱ��ϸ���в�ѯ�����µĳ���״����ͳ����Ϣ
	//		String sql = 
	//			"select round((sum(calendargzsj)*count(1)/sum(shiftgzsj)),"+decimalDigits+") as workdays,"+//Ӧ���ڰ���workdays
	//			"round(sum(calendargzsj),"+decimalDigits+") as workhours," +                              //Ӧ���ڹ�ʱworkhours
	//			"round(sum(case when worklength<0 then 0 else worklength end)/3600.0,"+decimalDigits+") as actualworkhours,"+                        //���ڹ�ʱactualworkhours
	//			//"round((sum(worklength)*count(1)/sum(shiftgzsj))/86400.0,"+decimalDigits+") as actualworkdays,"+          //���ڰ���actualworkdays
	//			"round((sum(worklength)*count(1)/sum(shiftgzsj))/3600.0,"+decimalDigits+") as actualworkdays,"+          //���ڰ���actualworkdays
	//
	//			"pk_shift,(select top 1 pk_monthstat from tbm_monthstat " +
	//			"where tbm_monthstat.pk_org=t.pk_org and tbm_monthstat.pk_psndoc = t.pk_psndoc " +
	//			"and tbmyear='"+year+"' and tbmmonth='"+month+"') as pk_monthstat,0 as dr,pk_org from("
	//			+tempTableSQLMachine+" union "+tempTableSQLManual+")as t group by pk_org,pk_psndoc,pk_shift";
	//
	//		BaseDAO dao = new BaseDAO();
	//		//��ɾ���ӱ��¼
	//		dao.executeUpdate(delSql);
	//		//Ȼ�����
	//		MonthWorkVO[] vos = CommonUtils.toArray(MonthWorkVO.class, (Collection<MonthWorkVO>)dao.executeQuery(sql, new BeanListProcessor(MonthWorkVO.class)));
	//		if(ArrayUtils.isEmpty(vos))
	//			return;
	//		String pk_group= periodVO.getPk_group();
	//		for(MonthWorkVO vo:vos){
	//			vo.setPk_group(pk_group);
	//			vo.setPk_org(pk_org);
	//			if(vo.getActualworkdays()==null||vo.getActualworkdays().doubleValue()<0)
	//				vo.setActualworkdays(UFDouble.ZERO_DBL);
	//			if(vo.getActualworkhours()==null||vo.getActualworkhours().doubleValue()<0)
	//				vo.setActualworkhours(UFDouble.ZERO_DBL);
	//		}
	//		dao.insertVOArray(vos);
	//	}

	private void processWorkDaysHours(CalSumParam calSumParam) throws DAOException {
		PeriodVO periodVO = calSumParam.periodVO;
		String year = periodVO.getTimeyear();
		String month = periodVO.getTimemonth();
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;

		BaseDAO dao = new BaseDAO();
		String delSql =
				"delete from tbm_monthwork where pk_monthstat in (select pk_monthstat from tbm_monthstat " + "where pk_org='" + pk_org + "' and pk_psndoc in(" + psndocInSQL + ") and tbmyear='" + year + "' and tbmmonth='" + month + "')";
		//��ɾ���ӱ��¼
		dao.executeUpdate(delSql);
		//Ȼ�����
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
	 * ׼�����������������˵Ŀ�����Ŀ�ȵ�
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
		// ������Ŀ���Ƿ����java�����Ŀ�����������java����Ŀ��������һЩ�������Բ���������߼���Ч��
		boolean existsJavaItem = false;
		for (ItemCopyVO itemVO : calSumParam.monthItemVOs) {
			if (itemVO.getSrc_flag().intValue() == ItemCopyVO.SRC_FLAG_JAVA) {
				existsJavaItem = true;
				break;
			}
		}
		// ������Ա���ձ�����������,��һ��string����Ա����pk_psndoc,�ڶ���string�����ڣ�������string��pk_daystat�����map����Ҫ������Ϊ�����daystatb�ӱ�������׼��
		// Logger.error("��ѯ�ձ�����map��ʼ��"+System.currentTimeMillis());
		long time = System.currentTimeMillis();
		//		calSumParam.daystatPKMap = DayStatCalculationHelper.getDaystatPKMap(calSumParam.pk_org, calSumParam.psndocInSQL, 
		//				beginDate.toString(), endDate.toString());
		//		Logger.debug("�����ձ�����map��ʱ��" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		//		// ������Ա�Ĺ�������map,Ҫ����ǰ������2��(���û����ѡ�����10�ŵ�11�ŵ��ձ�����ô�˴�Ӧ�ò�ѯ8�ŵ�13�ŵĹ�������),��������dateArray�Ѿ��Ǳ��ձ����ɷ�ΧҪ��ǰ���������ˣ�
		//		// ֮������ǰ������ô�࣬����Ϊ����Ӱ൥�ĳ��ȣ��ڵ��Ӻܳ���ʱ�򣬿��ܻ��õ��ܶ���Ĺ������������п��ܴ�󳬹��û���������ڷ�Χ
		//		calSumParam.calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
		//			queryCalendarVOByPsnInSQL(calSumParam.pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1), calSumParam.psndocInSQL);
		time = System.currentTimeMillis();
		// Logger.error("��ѯ��������������"+System.currentTimeMillis());
		//		calSumParam.aggBclbMap=NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
		//		if(!MapUtils.isEmpty(calSumParam.aggBclbMap)){
		//			calSumParam.bclbMap=new HashMap<String, BclbVO>();
		//			Iterator<String> keyItrt = calSumParam.aggBclbMap.keySet().iterator();
		//			while(keyItrt.hasNext()){
		//				String key = keyItrt.next();
		//				calSumParam.bclbMap.put(key, calSumParam.aggBclbMap.get(key).getBclbVO());
		//			}
		//		}
		time = System.currentTimeMillis();
		if (existsJavaItem) {
			//			// �������Щmapֻ��java��Ŀ����ʱ���ã����ֻ�ڴ���java��Ŀ��ʱ���ʼ��
			//			// ������Ա��timedata����,��һ��string����Ա�������ڶ���UFLiteralDate�����ڣ�value��timedatavo
			//			calSumParam.timedataMap = NCLocator.getInstance().lookup(ITimeDataQueryService.class).
			//				queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate,endDate,calSumParam.psndocInSQL);
			//			// ������Ա��lateearly����
			//			calSumParam.lateearlyMap = NCLocator.getInstance().lookup(ILateEarlyQueryService.class).
			//				queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate,endDate,calSumParam.psndocInSQL);
			//			// ������Ա�Ŀ��ڵ�������,key����Ա������value�������ʱ���ڵĿ��ڵ���vo(һ�������ֻ��һ��������ж�������ô�Ѿ���ʱ���Ⱥ��ź���)
			//			calSumParam.tbmPsndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
			//				queryTBMPsndocMapByPsndocInSQL(calSumParam.pk_org,calSumParam.psndocInSQL,beginDate,endDate, true);
			//��ȡjava����Ŀ�ļ�����ʵ��
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
		Logger.debug("��ѯtimedata��lateearly�����ڵ�����ʱ��" + (System.currentTimeMillis() - time));
		// ȡ��������Ա���������ڣ�startDate��ǰ������endDate�ĺ����죩�ĵ�������
		// key����Ա������value����Ա���ݼٵ��ӱ����飬����ļӰ൥���ͣ������һ��
		time = System.currentTimeMillis();
		// һ�������յ�ʱ��,�ڿ��ڹ����ж���
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
			//ҵ����־
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
	 * @param limitPsnByHROrg���Ƿ����hr��֯������Ա������ҵ��ڵ�Ĳ�ѯ��Ҫ�����HR��֯�����ˣ����Ƕ��ھ���������ĿǰҪ�󲻰�HR��֯����
	 * @return
	 * @throws BusinessException
	 */
	protected MonthStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		//���Ȳ�ѯ�ڼ����ֹ����
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if (periodVO == null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0090"
			/*@res "�ڼ�{0}������!"*/, year + "-" + month));
		//		UFLiteralDate beginDate = periodVO.getBegindate();
		//		UFLiteralDate endDate = periodVO.getEnddate();
		//��ѯȨ��
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		//tsy ���Ȩ��
		fromWhereSQL = addPsnPower(fromWhereSQL);
		//		//���ȣ���ѯ���ڵ���pk_org���ǵ�ǰpk_org����Ա
		//		TBMPsndocVO[] psndocVOsInOrg = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
		//			queryLatestByCondition(pk_org, fromWhereSQL, beginDate,endDate);
		//Ȼ�󣬲�ѯ���ڵ���pk_adminorg���ǵ�ǰpk_org����Ա�������ڵ������ڱ���֯����������֯�ڱ���֯����Ա
		//		TBMPsndocVO[] psndocVOsInAdminOrg = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
		//			queryLatestByCondition2(pk_org, fromWhereSQL, beginDate,endDate);
		//		if(ArrayUtils.isEmpty(psndocVOsInOrg)&&ArrayUtils.isEmpty(psndocVOsInAdminOrg))
		//			return null;
		//��ѯ��Щ��Ա���е��±���¼
		//		InSQLCreator isc = new InSQLCreator();
		//		try{
		//			SQLParameter para = new SQLParameter();
		//			para.addParam(pk_org);
		//			para.addParam(year);
		//			para.addParam(month);
		////			MonthStatVO[] dbVOsInOrg = null;
		//			MonthStatVO[] dbVOsInAdminOrg = null;

		//			if(!ArrayUtils.isEmpty(psndocVOsInOrg)){
		//				String cond = MonthStatVO.PK_ORG+"=? and "+MonthStatVO.PK_PSNDOC+" in("+isc.getInSQL(psndocVOsInOrg, MonthStatVO.PK_PSNDOC)+") and "+
		//					MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=? ";
		//				dbVOsInOrg = new MonthStatDAO().query(pk_org, MonthStatVO.class, 
		//						new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
		//					,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
		//						cond, null,para);
		//			}

		//			if(!ArrayUtils.isEmpty(psndocVOsInAdminOrg)){
		//				String cond = MonthStatVO.PK_ORG+"<>? and "+MonthStatVO.PK_PSNDOC+" in("+isc.getInSQL(psndocVOsInAdminOrg, MonthStatVO.PK_PSNDOC)+") and "+
		//				MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=? ";
		//				dbVOsInAdminOrg = new MonthStatDAO().query(pk_org, MonthStatVO.class, 
		//						new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
		//					,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
		//						cond, null,para);
		//			}
		//			//����֯���±���¼
		//			MonthStatVO[] vos1 =  processDBVOs(psndocVOsInOrg, dbVOsInOrg, year,month, showNoDataRecord,endDate);
		//			//������֯�ڱ���֯���±���¼
		//			MonthStatVO[] vos2 =  processDBVOs(psndocVOsInAdminOrg, dbVOsInAdminOrg, year,month, showNoDataRecord,endDate);
		//			return (MonthStatVO[])ArrayUtils.addAll(vos1, vos2);
		//		} catch (DbException e) {
		//			Logger.error(e.getMessage(), e);
		//			throw new BusinessRuntimeException(e.getMessage(), e);
		//		} catch (ClassNotFoundException e) {
		//			Logger.error(e.getMessage(), e);
		//			throw new BusinessRuntimeException(e.getMessage(), e);
		//		}
		//����֯���±���¼
		MonthStatVO[] vos1 = queryByConditionAndOrg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		//������֯�ڱ���֯���±���¼
		MonthStatVO[] vos2 = queryByConditionAndAndminorg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		return (MonthStatVO[]) ArrayUtils.addAll(vos1, vos2);
	}

	/**
	 * ��ѯ����֯�Ŀ����±�
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
		//��ѯ��Щ��Ա���е��±���¼
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
					new MonthStatDAO().query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk","srcid" }, cond, null, para);
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
	 * ��ѯ������֯�Ǳ���֯�Ŀ����±�
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
		//��ѯ��Щ��Ա���е��±���¼
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
					new MonthStatDAO().query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk","srcid" }, cond, null, para);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		//������֯�ڱ���֯���±���¼
		return processDBVOs(psndocVOsInAdminOrg, dbVOsInAdminOrg, periodVO.getTimeyear(), periodVO.getTimemonth(), showNoDataRecord, endDate);
	}

	@Override
	public MonthStatVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		return queryByCondition(context.getPk_org(), fromWhereSQL, year, month, showNoDataRecord);
	}

	/**
	 * �����±���Ŀ�ļ��㣬������ʽ��java�Զ���
	 * 
	 * @param calSumParam
	 * @throws DAOException
	 * @throws DbException
	 */
	private void processItems(CalSumParam calSumParam) throws DAOException, DbException {
		// �洢�ձ���Ŀֵ��map����Ϊjava��������Ŀ���������������Ŀ��ֵ�Ļ�����Ҫ���ڴ���ȡ���±���Ŀ�����������ձ���Ŀ
		// ��һ��string��itemcode���ڶ���string��pk_psndoc,������string��date��Object����Ŀ��ֵ
		//		Map<String, Map<String, Map<String, Object>>> dayItemValueMap = new HashMap<String, Map<String, Map<String, Object>>>();
		// ����±���Ŀֵ��map������Ϊjava��������Ŀ���������������Ŀ��ֵ�Ļ�����Ҫ���ڴ���ȡ��
		// ��һ��String��itemcode���ڶ���string��pk_psndoc,Object����Ŀ��ֵ
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
		// ����Ŀѭ������
		for (int itemIndex = 0; itemIndex < itemVOs.length; itemIndex++) {
			ItemCopyVO itemVO = itemVOs[itemIndex];
			// ������ֹ�¼�����ͣ����ô���ֱ��continue
			if (itemVO.getSrc_flag().intValue() == ItemVO.SRC_FLAG_MANUAL) {
				continue;
			}
			// ����ǹ�ʽ���ͣ�����Ҫִ�й�ʽ
			if (itemVO.getSrc_flag().intValue() == ItemVO.SRC_FLAG_FORMULA) {
				String updateSql = itemVO.getParsedFormula() + where;
				dao.executeUpdate(updateSql);
				continue;
			}
			// �����java����㣬��Ҫִ��java��ļ���
			// �����ݿ��в�ѯ�����±���Ŀ���������±���Ŀ���ձ���Ŀ��ֵ���ŵ�dayItemValueMap��monthItemValueMap��
			//			try {
			//				calHelper.queryItemValuesForMonthJavaItem(pkCorp, psnInsql, year, month, beginDate, endDate, itemVO, dayItemValueMap, monthItemValueMap, calPara.code2DayItemVOMap, calPara.code2ItemVOMap);
			//			} catch (SQLException e) {
			//				Logger.error(e.getMessage(), e);
			//				throw new BusinessException(e.getMessage(), e);
			//			}
			//			IMonthDataCreator creator = (IMonthDataCreator) itemVO.getImplObj();
			//			ItemVO[] referencedDayItems = creator.getDependentDayItems(pkCorp, itemVO);
			//			ItemVO[] referencedMonthItems = creator.getDependentItems(pkCorp, itemVO);
			//			// ��itemvalue��map�и��Լ���һ��λ�ã��Լ������Ϳ������ϷŻ�ȥ
			//			// key���˵�������object�ǲ���ֵ
			//			Map<String, Object> valueMap = null;
			//			if (!monthItemValueMap.containsKey(itemVO.getItem_code())) {
			//				valueMap = new HashMap<String, Object>();
			//				monthItemValueMap.put(itemVO.getItem_code(), valueMap);
			//			}
			//			calPara.itemCode = itemVO.getItem_code();
			//			// ���˽���ѭ������
			//			for (int psnIndex = 0; psnIndex < psnCount; psnIndex++) {
			//				// ��Ա����
			//				String pk_psndoc = pk_psndocs[psnIndex];
			//				calPara.pk_psndoc = pk_psndoc;
			//				// ���Ƿ���Ҫ�����±���Ŀ��ֵ�������Ҫ�Ļ�����Ҫ��monthItemValueMap����ȥȡ
			//				if (referencedMonthItems != null && referencedMonthItems.length > 0) {
			//					for (ItemVO referencedItemVO : referencedMonthItems) {
			//						calPara.itemValues.put(referencedItemVO.getItem_code(), monthItemValueMap.get(referencedItemVO.getItem_code()).get(pk_psndoc));
			//					}
			//				}
			//				// ���Ƿ���Ҫ�ձ���Ŀ��ֵ�������Ҫ�Ļ�����Ҫ������ȥȡ
			//				if (referencedDayItems != null && referencedDayItems.length > 0) {
			//					for (ItemVO referencedItemVO : referencedDayItems) {
			//						calPara.dayItemValueMap.put(referencedItemVO.getItem_code(), dayItemValueMap.get(referencedItemVO.getItem_code()).get(pk_psndoc));
			//					}
			//				}
			//				// �����ļ��㣬��IMonthDataCreator�ӿڵ�ʵ�����
			//				Object result = creator.process(calPara);
			//				// ��ˮ�����ھ��ˣ��ղŴ�map��ȡ��������Ŀ��ֵ�������Լ���ֵ������ˣ���Ҫ�ر�map�����Լ���ֵ�Ż�map��
			//				valueMap.put(pk_psndoc, TBMItemUtils.processNullValue(itemVO.getData_type().intValue(), result));
			//			}
			//			// ����ѭ����������±���Ŀ��Ҫ���µ����ݿ�
			//			try {
			//				calHelper.persistMonthItemValueTODB(pkCorp, itemIndex, itemVOs, monthItemValueMap, psnpks, year, month);
			//			} catch (SQLException e) {
			//				Logger.error(e.getMessage(), e);
			//				throw new BusinessException(e.getMessage(), e);
			//			}
		}
	}

	/**
	 * ��dbVOs���鴦��󷵻ء�dbVOs���������ݿ������е����� ���showNoDataRecordΪtrue����֤һ��һ����¼���������ݿ����Ƿ��У�û�о�newһ��daystatvo�� ���showNoDataRecordΪfalse����Ҫ��dbVOs�������ֶ�Ϊ�յļ�¼ȥ����
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
		//dbVO��map��key��pk_psndoc
		Map<String, MonthStatVO> dbVOMap = CommonUtils.toMap(MonthStatVO.PK_PSNDOC, dbVOs);
		if (dbVOMap == null)
			dbVOMap = new HashMap<String, MonthStatVO>();
		List<MonthStatVO> retList = new ArrayList<MonthStatVO>();
		//��������ռ�¼
		if (!showNoDataRecord) {
			for (int i = 0; i < psndocVOs.length; i++) {
				TBMPsndocVO psndocVO = psndocVOs[i];
				String pk_psndoc = psndocVO.getPk_psndoc();
				MonthStatVO dbVO = dbVOMap.get(pk_psndoc);
				if (dbVO == null || dbVO.isNoDataRecord())
					continue;
				retList.add(dbVO);
				//������ְ����(�ձ����в�û�д洢��Ԫ��������)
				dbVO.setPk_psnjob(psndocVO.getPk_psnjob());
				dbVO.setPk_org_v(psndocVO.getPk_org_v());
				dbVO.setPk_dept_v(psndocVO.getPk_dept_v());
				dbVO.setPk_dept(psndocVO.getPk_dept());
			}
			return retList.size() == 0 ? null : retList.toArray(new MonthStatVO[0]);
		}
		//������ռ�¼����Ҫ��ÿ�˶�Ҫ��vo��û�еĻ�����Ҫnewһ��

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
			//���û����newһ��
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
		//����Ȩ�޴���
		//		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		//ʹ��ά��Ȩ��
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		String alias = fromWhereSQL.getTableAliasByAttrpath(".");
		String extraCond =
				alias + "." + TBMPsndocVO.PK_PSNDOC + " not in(" + "select " + MonthStatVO.PK_PSNDOC + " from " + MonthStatVO.getDefaultTableName() + " where " + MonthStatVO.PK_ORG + "='" + context.getPk_org() + "' and " + MonthStatVO.TBMYEAR + "='" + year + "' and " + MonthStatVO.TBMMONTH + "='" + month + "' and " + MonthStatVO.ISEFFECTIVE + "='Y')";
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(extraCond, fromWhereSQL);
		//tsy ���Ȩ��
		fromWhereSQL = addPsnPower(fromWhereSQL);
		ITBMPsndocQueryService queryService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		TBMPsndocVO[] vos = queryService.queryLatestByCondition(context.getPk_org(), fromWhereSQL, periodBeginDate, periodEndDate);
		TaBusilogUtil.writeMonthStatUngenBusiLog(vos, periodVO.getBegindate(), periodVO.getEnddate());
		return vos;
	}

	/**
	 * ��¼���л��ܲ������࣬�����е������Ŀ��
	 * 
	 * @author zengcheng
	 */
	private static class CalSumParam {

		MonthCalParam monthCalParam;
		String pk_org;
		TimeRuleVO timeRuleVO;
		String[] pk_psndocs;
		String psndocInSQL;//��Ա������in sql��ע�⣬psndocInSQL��pk_psndocsʵ�����ǵ�ͬ�ģ�ֻ���������ֻ��pk_psndocs������psndocInSQL�Ļ����ܶ�ط���Ҫ����pk_psndocs������ʱ���Ƚ��˷�
		PeriodVO periodVO;

		Map<String, Object> paramValues;//����������ֵ��key�ǲ����ı���

		Map<String, IMonthDataCreator> dataCreatorMap;//javaʵ�����map��key���ձ���Ŀ��code��ֻ��java�����Ŀput��ȥ����value��ʵ����

		ItemCopyVO[] monthItemVOs;//�±���Ŀ��Ҫ�󰴼���˳������
		Map<String, AggShiftVO> aggShiftMap;//���а�ε�aggvo��map
		Map<String, ShiftVO> shiftMap;//���а�ε�map

		LeaveTypeCopyVO[] leaveCopyVOs;
		AwayTypeCopyVO[] awayCopyVOs;
		OverTimeTypeCopyVO[] overCopyVOs;
		ShutDownTypeCopyVO[] shutCopyVOs;

		//		// ������Ա���Ű�
		//		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap;
		//		// ������Ա��timedata����,��һ��string����Ա�������ڶ���UFLiteralDate�����ڣ�value��timedatavo
		//		Map<String, Map<UFLiteralDate, TimeDataVO>> timedataMap=null;
		//		// ������Ա��lateearly����
		//		Map<String, Map<UFLiteralDate, LateEarlyVO>> lateearlyMap=null;
		//		// ������Ա�Ŀ��ڵ�������,key����Ա������value�������ʱ���ڵĿ��ڵ���vo(һ�������ֻ��һ��������ж�������ô�Ѿ���ʱ���Ⱥ��ź���)
		//		Map<String, List<TBMPsndocVO>> tbmPsndocMap = null;
		//		// ������Ա���ձ�����������,��һ��string����Ա����pk_psndoc,�ڶ���string�����ڣ�������string��pk_daystat�����map����Ҫ������Ϊ�����daystatb�ӱ�������׼��
		//		Map<String, Map<String, String>> daystatPKMap;
		double workDayLength;
	}

	@Override
	public MonthStatVO[] generate(String pk_org, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException {
		generate(pk_org, fromWhereSQL, year, month);
		return queryByCondition(pk_org, fromWhereSQL, year, month, showNoDataRecord);

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
		//tsy ���Ȩ��
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

		//���µ�һ�����ڵ�����¼
		TBMPsndocVO latestPsndocVO =
				NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class).queryByPsndocAndDateTime(pk_psndoc, new UFDateTime());
		String pk_org = latestPsndocVO.getPk_org();
		MonthStatDAO dao = new MonthStatDAO();
		MonthStatVO[] vos = null;

		try {
			vos =
					dao.query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk","srcid" }, cond, orderby, para);
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
	 * ΪԱ���������±���¼����pk_psnjob ��ΪԱ���������±�����ֱ�Ӵ��±����в�ѯ�ģ�û�й������ڵ��������pk_psnjob�ֶ���ֵ���������� �Ľ������޷���ʾ��ְ�������Ϣ
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
	 * �±����ɽ���ʱ�Դ��ڿ����쳣����Ա����������Ϣ
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param curPeriod
	 * @throws BusinessException
	 */

	//	private  Hashtable<String, String[]> createTransferValues(PsndocVO vo,String url) throws BusinessException  {
	//		Hashtable<String, String[]> hm = new Hashtable<String, String[]>();
	//		String[] fieldCode = TimeitemConst.FIELDCODE;
	//		for (int i = 0; i < fieldCode.length; i++) {
	//			String value = "";
	//			if ("name".equals(fieldCode[i])) {
	//				// ����
	//				value = vo.getName();
	//			}
	//			else if ("url".equals(fieldCode[i])) {
	//				// ���ӵ�ַ
	//				value = "testtest";
	//			}
	//			else {
	//				value = "";
	//			}
	//			hm.put(fieldCode[i], new String[]{value});
	//		}
	//		return hm;
	//	}
	//	private void sendMessage(String pk_org, String[] pk_psndocs, PeriodVO curPeriod,CalSumParam calSumParam )throws BusinessException {
	//
	//		Set<String> noticePsnPkset = this.getExceptionPsns(pk_org, pk_psndocs, curPeriod, calSumParam);//�쳣��Ա��������
	//		if(noticePsnPkset.size()>0){
	//		INotice secvice = NCLocator.getInstance().lookup(INotice.class);
	//		NoticeTempletVO[] nt = secvice.queryDistributedTemplates(TimeitemConst.PK_NOTICE_SORT, PubEnv.getPk_group(), pk_org, true);
	//		Iterator<String> it = noticePsnPkset.iterator();
	//		String old="";
	//		while(it.hasNext()){
	//			String psnPK=it.next();//�쳣��Ա����
	//			IPersistenceRetrieve perstRetrieve = NCLocator.getInstance().lookup( IPersistenceRetrieve.class );
	//			PsndocVO psndocVO = (PsndocVO) perstRetrieve.retrieveByPk(null, PsndocVO.class, psnPK);//ͨ�������õ��쳣��Ա��VO
	//			if(psndocVO==null){
	//				continue;
	//			}
	//			String[] mail=new String[]{psndocVO.getEmail()};//�õ������˻� 
	//			if(null==mail[0]||mail.length<=0){
	//				continue;
	//			}
	//			// �滻֪ͨģ��ı���
	//			String noticeContent = nt[0].getContent();// ֪ͨģ�������
	//			if(noticeContent.indexOf("<#name#>")<0 && !StringUtils.isEmpty(old)){
	//				noticeContent=noticeContent.replace(old, "<#name#>");//���滻 name��һֱ�ǵ�һ�������˵�name�� urlҲ���滻
	//			}
	//			StringOperator strSys = new StringOperator(noticeContent);
	//			new StringOperator(strSys);
	//			String psnName=psndocVO.getName();//Ա������
	//			old=psnName;//��������˷����ʼ�ʱ�� psnname���ǵ�һ���˵� Ϊ�˷�ֹ��������Ͱ���һ�ε���Աȡ����Ȼ�����滻��
	//			strSys.replaceAllString("<#name#>",psnName);// Ա������
	//			strSys.replaceAllString("<#url#>", "");//���ӵ�ַ
	//			if (nt != null && nt.length > 0) {
	//				// ������ֵ�ŵ�֪ͨģ����
	//				nt[0].setReceiverEmails(mail);//�������ʼ���ַ
	//				nt[0].setContent(strSys.toString());//��������
	//				if (StringUtils.isBlank( nt[0].getCurrentUserPk() ) || nt[0].getCurrentUserPk().length() != 20) {
	//				// ���ģ��ĵ�ǰ�û�Ϊ�գ����ϵ�ǰ�û�����NCϵͳ�û�
	//				nt[0].setCurrentUserPk( PubEnv.getPk_user() != null && PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
	//						: INCSystemUserConst.NC_USER_PK );
	//				}
	//			//����֪ͨ
	//			secvice.sendNotice_RequiresNew(nt[0], pk_org, false);
	//			
	//			}
	//		}
	//		}
	//	}

	//	private void sendMessage(String pk_org, String[] pk_psndocs, PeriodVO curPeriod,CalSumParam calSumParam )throws BusinessException {
	//		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, curPeriod, calSumParam);//�쳣��Ա��������
	//		if(noticePsnPkset.size()<=0){
	//			return;
	//		}
	//		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
	//		String insql=StringPiecer.getDefaultPiecesTogether(noticePsnPks);
	//		String cond="pk_psndoc in (" + insql +" )";
	//		//��ѯ�����е�psndocvo
	//		PsndocVO[] psndocvos = (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsndocVO.class, cond);
	//		if(ArrayUtils.isEmpty(psndocvos))
	//			return;
	//		INotice service = NCLocator.getInstance().lookup(INotice.class);
	//		IURLGenerator IurlDirect =  NCLocator.getInstance().lookup(IURLGenerator.class);
	//		NoticeTempletVO[] nt = service.queryDistributedTemplates(TimeitemConst.PK_NOTICE_SORT, PubEnv.getPk_group(), pk_org, true);
	//		HashMap<String, UserVO[]> userMap = NCLocator.getInstance().lookup(IUserPubService.class).batchQueryUserVOsByPsnDocID(pk_psndocs, null);
	////		String old="";
	//		for(PsndocVO psndocVO: psndocvos){
	//			//ÿ��ʹ��ǰ����ԭʼ�������¸���һ�ݣ��������״����ϴη�����Ϣ������
	//			NoticeTempletVO noticeTempletVO = (NoticeTempletVO) nt[0].clone();
	//			
	//			// �滻֪ͨģ��ı���
	//			String noticeContent = noticeTempletVO.getContent();// ֪ͨģ�������
	////			if(noticeContent.indexOf("<#name#>")<0 && !StringUtils.isEmpty(old)){
	////				noticeContent=noticeContent.replace(old, "<#name#>");//���滻 name��һֱ�ǵ�һ�������˵�name�� urlҲ���滻
	////			}
	//			StringOperator strSys = new StringOperator(noticeContent);
	//			new StringOperator(strSys);
	//			String psnName=psndocVO.getName();//Ա������
	////			old=psnName;//��������˷����ʼ�ʱ�� psnname���ǵ�һ���˵� Ϊ�˷�ֹ��������Ͱ���һ�ε���Աȡ����Ȼ�����滻��
	//			strSys.replaceAllString("<#name#>",psnName);// Ա������
	//			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
	//			noticeTempletVO.setReceiverPkUsers(StringPiecer.getStrArrayDistinct(users, UserVO.CUSERID));//���ý�����
	//			SSOInfo ssinfo = new SSOInfo();
	//			if(!ArrayUtils.isEmpty(users)){
	//			   // ssinfo.setUserPassword(users[0].getUser_password());
	//				ssinfo.setUserPK(users[0].getCuserid());
	//			}
	//			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
	//			ssinfo.setFuncode("E20200910");//E20200910 �����±��Ĳ����ڵĹ��ܽڵ��
	//			String urlTitle=IurlDirect.buildHTML(ssinfo, ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0088")/*@res "Ա������������!"*/);
	////			String urlTitle=IurlDirect.buildURLString(ssinfo);
	//			strSys.replaceAllString("<#url#>",urlTitle);
	//			if (nt != null && nt.length > 0) {
	//				// ������ֵ�ŵ�֪ͨģ����
	////				nt[0].setReceiverEmails(mail);//�������ʼ���ַ�������˽����˾Ͳ���������email�ˣ�
	//				noticeTempletVO.setContent(strSys.toString());//��������
	//				if (StringUtils.isBlank( noticeTempletVO.getCurrentUserPk() ) || noticeTempletVO.getCurrentUserPk().length() != 20) {
	//				// ���ģ��ĵ�ǰ�û�Ϊ�գ����ϵ�ǰ�û�����NCϵͳ�û�
	//				noticeTempletVO.setCurrentUserPk( PubEnv.getPk_user() != null && PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
	//						: INCSystemUserConst.NC_USER_PK );
	//				}
	//				//����֪ͨ
	//				service.sendNotice_RequiresNew(noticeTempletVO, pk_org, false);
	//			}
	//		}
	//	}
	//V63�Ժ�ʹ��ƽ̨����Ϣ֪ͨ���ͷ�ʽ
	private void sendMessage(String pk_org, String[] pk_psndocs, PeriodVO curPeriod, CalSumParam calSumParam) throws BusinessException {
		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, curPeriod, calSumParam);//�쳣��Ա��������
		if (noticePsnPkset.size() <= 0) {
			return;
		}
		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(noticePsnPks);
		String cond = "pk_psndoc in (" + insql + " )";
		//��ѯ�����е�psndocvo
		PsndocVO[] psndocvos =
				(PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsndocVO.class, cond);
		if (ArrayUtils.isEmpty(psndocvos))
			return;
		//��ѯ��֯
		OrgVO org = (OrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, OrgVO.class, pk_org);
		//��ѯ�±�
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(noticePsnPks);
		MonthStatVO[] monthStatVOs = queryByCondition(pk_org, fromWhereSQL, curPeriod.getTimeyear(), curPeriod.getTimemonth(), true);
		Map<String, MonthStatVO> monthMap = CommonUtils.toMap(MonthStatVO.PK_PSNDOC, monthStatVOs);

		HashMap<String, UserVO[]> userMap =
				NCLocator.getInstance().lookup(IUserPubService.class).batchQueryUserVOsByPsnDocID(pk_psndocs, null);
		IURLGenerator IurlDirect = NCLocator.getInstance().lookup(IURLGenerator.class);
		IHRMessageSend messageSendServer = NCLocator.getInstance().lookup(IHRMessageSend.class);
		//����Աѭ��������Ϣ
		for (PsndocVO psndocVO : psndocvos) {
			HRBusiMessageVO messageVO = new HRBusiMessageVO();
			messageVO.setBillVO(monthMap.get(psndocVO.getPk_psndoc()));//Ԫ�������Խ���
			messageVO.setMsgrescode(TaMessageConst.MONTHSTATEXCPMSG);
			//ҵ�����
			Hashtable<String, Object> busiVarValues = new Hashtable<String, Object>();
			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
			SSOInfo ssinfo = new SSOInfo();
			if (!ArrayUtils.isEmpty(users)) {
				// ssinfo.setUserPassword(users[0].getUser_password());
				ssinfo.setUserPK(users[0].getCuserid());
			}
			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
			ssinfo.setFuncode("E20200910");//E20200910 �����±��Ĳ����ڵĹ��ܽڵ��

			String urlTitle = IurlDirect.buildURLString(ssinfo);
			//				IurlDirect.buildHTML(ssinfo, ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0088")/*@res "Ա������������!"*/);
			busiVarValues.put("url", urlTitle);
			busiVarValues.put("CURRUSERNAME", MultiLangHelper.getName(psndocVO));
			busiVarValues.put("CURRCORPNAME", MultiLangHelper.getName(org));
			messageVO.setBusiVarValues(busiVarValues);
			messageVO.setPkorgs(new String[] { pk_org });
			messageVO.setReceiverPkUsers(ArrayUtils.isEmpty(users) ? null : new String[] { users[0].getPrimaryKey() });

			messageSendServer.sendBuziMessage_RequiresNew(messageVO);
		}
	}

	//�õ��±��쳣��Ա
	private Set<String> getExceptionPsns(String pk_org, String[] pk_psndocs, PeriodVO curPeriod, CalSumParam calSumParam)
			throws BusinessException {

		Set<String> exceptionPsnPkSet = new HashSet<String>();
		ITBMPsndocQueryService tbmPsnQueryS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		//��ѯ���ڵ���  �ҵ���Ӧ�Ŀ��ڷ�ʽ����������or�ֹ����ڣ�
		Map<String, List<TBMPsndocVO>> tbmPsnMap =
				tbmPsnQueryS.queryTBMPsndocMapByPsndocs(pk_org, pk_psndocs, curPeriod.getBegindate(), curPeriod.getEnddate(), true);
		if (MapUtils.isEmpty(tbmPsnMap)) {
			return null;
		}
		ITimeDataQueryService timDataQuery = NCLocator.getInstance().lookup(ITimeDataQueryService.class);
		//������������
		Map<String, Map<UFLiteralDate, TimeDataVO>> machTimeDateMap =
				timDataQuery.queryVOMapByPsndocInSQLForMonth(pk_org, curPeriod.getBegindate(), curPeriod.getEnddate(), calSumParam.psndocInSQL);
		//ѭ�����һ����������� ���쳣����������к��±��쳣�����ʼ�������ƥ��� ��� ����Ա��pk����ķ����ʼ��ļ�����
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
		//�ֹ���������
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
						//�ֹ�����û����;�����һ��,������һ�β�����Ҫ
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
	 * //�ж�һ�������Ƿ� �гٵ����� true Ϊ�гٵ�����
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
	 * //�ж�һ�������Ƿ� ���������� true Ϊ����������
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
	 * //�ж�һ�������Ƿ� �п����� true Ϊ������
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
		//�����ʼ=��������ֱ�Ӱ����²�
		if (beginYear.equals(endYear) && beginMonth.equals(endMonth)) {
			MonthStatVO[] vos = queryByPsnAndNatualYearMonth(pk_psndoc, beginYear, beginMonth);
			processExtraInfoForSelf(vos);
			return vos;
		}
		//���begin>end���򽻻�һ��,��ǿϵͳ��³����
		String beginYearMonth = beginYear + beginMonth;
		String endYearMonth = endYear + endMonth;
		if (beginYearMonth.compareTo(endYearMonth) > 0) {
			beginYear = endYearMonth.substring(0, 4);
			beginMonth = endYearMonth.substring(4, 6);
			endYear = beginYearMonth.substring(0, 4);
			endMonth = beginYearMonth.substring(4, 6);
		}
		//�ȼ򵥵ذ���ÿ���¶���ѯһ��
		MonthStatVO[] vos = simplyQueryPsnMonthstat(pk_psndoc, beginYear, beginMonth, endYear, endMonth);
		if (ArrayUtils.isEmpty(vos))
			return null;
		//Ȼ����д���������ȥ���ظ��ģ��п���ͬһ����֯�ڣ�������Ȼ�²�����±�������ͬ�ģ����м��пյ�������յ�
		vos = postProcessPsnMonthStat(vos);
		processExtraInfoForSelf(vos);
		return vos;
	}

	private MonthStatVO[] simplyQueryPsnMonthstat(String pk_psndoc, String beginYear, String beginMonth, String endYear, String endMonth)
			throws NumberFormatException, BusinessException {
		//���β�ѯ��begin��end֮���ÿһ����
		//��������꣬��ӿ�ʼ�²�ѯ��������
		List<MonthStatVO> retList = new ArrayList<MonthStatVO>();
		int beginMonthInt = getMonth(beginMonth);
		int endMonthInt = getMonth(endMonth);
		if (beginYear.equals(endYear)) {//������
			queryMonths(pk_psndoc, Integer.parseInt(beginYear), beginMonthInt, endMonthInt, retList);
		}
		//������꣬���������������һ�꣬�м��꣬���һ�꣬
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
		//��hr��֯����
		Map<String, MonthStatVO[]> orgMap = CommonUtils.group2ArrayByField(MonthStatVO.PK_ORG, vos);
		YearMonthComparator comparator = new YearMonthComparator();
		for (String pk_org : orgMap.keySet()) {
			MonthStatVO[] orgMonthVOs = orgMap.get(pk_org);
			if (vos.length == 1)
				continue;
			//���Ȱ�����ڼ��������
			Arrays.sort(orgMonthVOs, comparator);
			//��ȥ�����ͬ�ļ�¼���п��ܻᷢ������ͬ����Ȼ�²飬������ͬ�±���¼����������磬һ���ڼ�ܳ�����3.20��4.30����ô����Ȼ��3�º�4�£������������±���¼����Ҫ�ɵ�����һ������Ȼ�û��ῴ������һģһ�����±���
			List<MonthStatVO> list = new ArrayList<MonthStatVO>(orgMonthVOs.length);
			Set<String> yearMonthSet = new HashSet<String>();
			for (MonthStatVO vo : orgMonthVOs) {
				String yearMonth = vo.getTbmyear() + vo.getTbmmonth();
				if (yearMonthSet.contains(yearMonth))
					continue;
				yearMonthSet.add(yearMonth);
				list.add(vo);
			}
			//�ߵ�����Ѿ�û���ظ����±���¼��,��Ҫ��ѯ��һ���±������һ���±�֮����ŵļ�¼
			//������忼���꣬��ܺ��ж��Ƿ��п��ŵļ�¼
			int firstYearInt = Integer.parseInt(orgMonthVOs[0].getTbmyear());
			int lastYearInt = Integer.parseInt(orgMonthVOs[orgMonthVOs.length - 1].getTbmyear());
			int firstMonthInt = getMonth(orgMonthVOs[0].getTbmmonth());
			int lastMonthInt = getMonth(orgMonthVOs[orgMonthVOs.length - 1].getTbmmonth());
			if (firstYearInt == lastYearInt) {
				//�����������ͬһ�£����߽����ţ�û��Ҫ����(������ѭ������Ҫ�ӵ�orgMap��)
				//				if(lastMonthInt-firstMonthInt<=1)
				//					continue;
				if (lastMonthInt - firstMonthInt > 1) {
					for (int month = firstMonthInt + 1; month < lastMonthInt; month++) {
						String strMonth = getMonth(month);
						if (yearMonthSet.contains(firstYearInt + strMonth))
							continue;
						//��û������µ��±�������Ҫ��ѯ
						MonthStatVO statvo = queryByPsn(pk_psndoc, pk_org, Integer.toString(firstYearInt), strMonth);
						if (statvo != null)
							list.add(statvo);
					}
				}
				Collections.sort(list, comparator);
			} else {//������꣬��Ƚ��鷳һ�㣬��Ϊ�����ڼ䲻һ��ֻ��12����������13��14���������Ҫ��ѯ�����ڼ��
					//��ѯ��Щ������������ڼ�
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
						//��û������µ��±�������Ҫ��ѯ
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
		//����ȷ����Ȼ���µ����ڷ�Χ
		IDateScope monthDateScope = getNatualMonthBeginEndDate(year, month);
		UFLiteralDate beginDate = monthDateScope.getBegindate();
		UFLiteralDate endDate = monthDateScope.getEnddate();
		//��ѯ�����ڴ����ڷ�Χ�����еĿ��ڵ���
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		//��ǿ³���ԣ������ѯ�����ڷ�Χ����ֹ��Щ�±���Ϊ��Ȼ���뿼�ڵ����޽�������Զ�鲻����
		TBMPsndocVO[] psndocVOs =
				psndocService.queryTBMPsndocVOsByPsndocDate(pk_psndoc, beginDate.getDateBefore(30), endDate.getDateAfter(30));
		if (ArrayUtils.isEmpty(psndocVOs))
			return null;
		//��HR��֯����
		Map<String, TBMPsndocVO[]> orgMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_ORG, psndocVOs);
		//		String cond = MonthStatVO.PK_PSNDOC+"=? and "+MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=?";
		String cond =
				MonthStatVO.PK_ORG + "=? and " + MonthStatVO.PK_PSNDOC + "=? and " + MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=?";
		SQLParameter para = new SQLParameter();
		MonthStatDAO dao = new MonthStatDAO();
		List<MonthStatVO> monthVOList = new ArrayList<MonthStatVO>();
		//����֯ѭ������
		for (String pk_org : orgMap.keySet()) {
			PeriodVO[] periodVOs = PeriodServiceFacade.queryPeriodsByDateScope(pk_org, beginDate, endDate);
			if (ArrayUtils.isEmpty(periodVOs))
				continue;
			// �ɵݹ��Ҵ��ڵļ�¼
			for (int i = ArrayUtils.getLength(periodVOs) - 1; i >= 0; i--) {
				PeriodVO latestPeriodVO = periodVOs[i];
				TBMPsndocVO[] psnVOs = orgMap.get(pk_org);
				if (!DateScopeUtils.isCross(psnVOs, latestPeriodVO))
					continue;
				//��ѯ���˴��µ��±�
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
				//��Ϊ��ѯ���ڵ���ʱ�������˲�ѯ�����ڷ�Χ�����psnVOs�����һ����һ����period��Χ�ڵģ�����Ҫ���⴦��һ��
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
	 * ������Ҫ����Ҳд�������Ϣ�������������˾�������������Լ�������Ŀ+�ݼٳ���Ӱ����
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	private void processExtraInfoForSelf(MonthStatVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		//Ϊ�˼��ٲ�ѯ�����������ĸ�map
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
			//��ѯpsnjob
			String psnjobInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(vos, MonthStatVO.PK_PSNJOB));
			String cond = PsnJobVO.PK_PSNJOB + " in(" + psnjobInSQL + ")";
			PsnJobVO[] psnjobVOs = CommonUtils.retrieveByClause(PsnJobVO.class, dao, cond);
			psnjobMap = CommonUtils.toMap(PsnJobVO.PK_PSNJOB, psnjobVOs);
			//��ѯorgvo
			String orgInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(psnjobVOs, PsnJobVO.PK_ORG));
			cond = OrgVO.PK_ORG + " in(" + orgInSQL + ")";
			OrgVO[] orgVOs = CommonUtils.retrieveByClause(OrgVO.class, dao, cond);
			orgMap = CommonUtils.toMap(OrgVO.PK_ORG, orgVOs);
			//��ѯdeptvo
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
	 * ��ȡһ����Ȼ�µĵ�һ������һ��
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
			//�����������ӵ��ͻ��˵�������ȥ
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPkArrayQuerySQL(pk_depts, fromWhereSQL);
		}
		MonthStatVO[] monthStatVOs = queryByCondition(context, fromWhereSQL, year, month, showNoDataRecord);
		if (ArrayUtils.isEmpty(monthStatVOs))
			return null;
		//����Ա���ݰ����ŷ��飬����˳���ܸı䡣�������ص�DeptMonthStatVO[]��vo��˳��Ҫ��MonthStatVO�в��ŵ�˳��һ��
		List<DeptMonthStatVO> retList = new ArrayList<DeptMonthStatVO>();//list�������Ǳ�֤����vo��˳��
		Map<String, DeptMonthStatVO> statMap = new HashMap<String, DeptMonthStatVO>();//map��������ȡvo����
		for (MonthStatVO monthStatVO : monthStatVOs) {
			String pk_dept = monthStatVO.getPk_dept();
			DeptMonthStatVO deptVO = statMap.get(pk_dept);
			if (deptVO == null) {
				deptVO = new DeptMonthStatVO();
				deptVO.setPk_dept(pk_dept);

				//���ð汾��Ϣ
				deptVO.setPk_dept_v(monthStatVO.getPk_dept_v());
				deptVO.setPk_org_v(monthStatVO.getPk_org_v());

				deptVO.setTbmyear(monthStatVO.getTbmyear());
				deptVO.setTbmmonth(monthStatVO.getTbmmonth());

				//				deptVO.setMonthworkVOs(monthStatVO.getMonthworkVOs());

				statMap.put(pk_dept, deptVO);
				retList.add(deptVO);
			}
			//�Ѹ����±����ݺϲ�Ϊ�����±�����
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
		//		//���Ȳ�ѯ�ڼ����ֹ����
		//		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		//		if(periodVO==null)
		//			throw new BusinessException(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0090") 
		//					 /*@res "�ڼ�{0}������!"*/, year+"-"+month);
		//		UFLiteralDate beginDate = periodVO.getBegindate();
		//		UFLiteralDate endDate = periodVO.getEnddate();
		//		//��ѯ�ڼ����ڲ��Ź������Ŀ��ڵ���
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
		//���Ȳ�ѯ�ڼ����ֹ����
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if (periodVO == null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0090")
			/*@res "�ڼ�{0}������!"*/, year + "-" + month);
		List<String> psnInOrg = new ArrayList<String>();//����֯�е���Ա
		List<String> psnInAdminorg = new ArrayList<String>();//������֯�ڱ���֯����Ա
		for (String pk : pks) {
			String[] split = pk.split(",");
			if (context.getPk_org().equals(split[1])) {
				psnInOrg.add(split[0]);
			} else {
				psnInAdminorg.add(split[0]);
			}
		}
		//����֯���±���¼
		MonthStatVO[] vos1 = null;
		if (CollectionUtils.isNotEmpty(psnInOrg)) {
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(psnInOrg.toArray(new String[0]));
			//tsy ���Ȩ��
			//			fromWhereSQL = addPsnPower(fromWhereSQL);
			vos1 = queryByConditionAndOrg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		}
		//������֯�ڱ���֯���±���¼
		MonthStatVO[] vos2 = null;
		if (CollectionUtils.isNotEmpty(psnInAdminorg)) {
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(psnInAdminorg.toArray(new String[0]));
			//tsy ���Ȩ��
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
		config.setFieldCodes(new String[] { "pk_org", "pk_group", "pk_psndoc", "tbmyear", "tbmmonth", "iseffective", "isapprove", "isuseful", "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "ime approvedate", "transtype", "billtype", "transtypepk","srcid" });
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
	 * �ύ
	 * ͬ�����µ�
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
	 * �ջ�
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
			getDao().updateVOArray(hvos, new String[] { "approvestatus", "srcid" });
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
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if (periodVO == null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0090"/*@res "�ڼ�{0}������!"*/, year + "-" + month));
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(periodVO.getTimeyear());
		para.addParam(periodVO.getTimemonth());
		para.addParam(pk_monthstat);
		MonthStatVO[] dbVOsInOrg = null;
		String cond = MonthStatVO.PK_ORG + "=?  and " + MonthStatVO.TBMYEAR + "=? and " + MonthStatVO.TBMMONTH + "=? and srcid=?";
		try {
			dbVOsInOrg =
					new MonthStatDAO().query(pk_org, MonthStatVO.class, new String[] { MonthStatVO.PK_PSNDOC, MonthStatVO.TBMYEAR, MonthStatVO.TBMMONTH, MonthStatVO.PK_GROUP, MonthStatVO.PK_ORG, MonthStatVO.ISAPPROVE, "billno", "busitype", "billmaker", "approver", "approvestatus", "approvenote", "approvedate", "transtype", "billtype", "transtypepk","srcid" }, cond, null, para);
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
	 * N_6407_APPROVEר��
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
		if (approvestatus == ApproveStatus.APPROVED) {//�����ȫ������
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
	 * N_6407_UNAPPROVEר��
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
	 * ���Ȩ��
	 * @param fromWhereSQL
	 * @return
	 */
	private FromWhereSQL addPsnPower(FromWhereSQL fromWhereSQL) {
		if (fromWhereSQL == null)
			return fromWhereSQL;
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String alias = "T1";
		// ��֯Ȩ��
		alias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob" + FromWhereSQLUtils.getAttPathPostFix());
		String orgSql =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, alias);
		if (StringUtils.isNotBlank(orgSql)) {
			sqlWhereUtil.and(orgSql);
		}
		// ����Ȩ��
		alias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob" + FromWhereSQLUtils.getAttPathPostFix());
		String deptSql =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, alias);
		if (StringUtils.isNotBlank(deptSql)) {
			sqlWhereUtil.and(deptSql);
		}
		return new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
	}

	/**
	 * ���Ȩ��
	 * @param fromWhereSQL
	 * @return
	 */
	private String addPsnPower(String where) {
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(where);
		//		String alias = PsnJobVO.getDefaultTableName();
		//		// ��֯Ȩ��
		//		String orgSql =
		//				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, alias);
		//		if (StringUtils.isNotBlank(orgSql)) {
		//			sqlWhereUtil.and(orgSql);
		//		}
		//		// ����Ȩ��
		//		String deptSql =
		//				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, alias);
		//		if (StringUtils.isNotBlank(deptSql)) {
		//			sqlWhereUtil.and(deptSql);
		//		}
		return sqlWhereUtil.getSQLWhere();
	}
}
