package nc.impl.wa.paydata;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.common.WaCommonImpl;
import nc.impl.wa.end.MonthEndDAO;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.wa.IAmoSchemeQuery;
import nc.itf.hr.wa.IPayfileManageService;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.type.SQLParamType;
import nc.jdbc.framework.type.SQLTypeFactory;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.format.FormatGenerator;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class PaydataDAO extends AppendBaseDAO implements ICommonAlterName {
	public PaydataDAO() {
	}

	public WaClassItemVO[] getUserClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		WaClassItemVO[] classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), null);

		return classitems;
	}

	public WaClassItemVO[] getUserShowClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		String condition =
				" wa_classitem.pk_wa_classitem not in (select pk_wa_classitem from wa_classitemdsp where pk_wa_class = '" + loginVO.getPk_wa_class() + "' and cyear = '" + loginVO.getWaYear() + "' and cperiod = '" + loginVO.getWaPeriod() + "' and pk_user = '" + PubEnv.getPk_user() + "' and bshow = 'N' )";

		WaClassItemVO[] classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), condition);

		return classitems;
	}

	public WaClassItemVO[] getRepayUserShowClassItemVOs(WaLoginContext waLoginContext) throws BusinessException {
		WaLoginVO waLoginVO = waLoginContext.getWaLoginVO();
		String condition = "";
		if ((!StringUtils.isEmpty(waLoginVO.getReperiod())) && (!waLoginVO.getReperiod().equals("-1"))) {
			condition =
					" wa_classitem.pk_wa_item in (select pk_wa_item from wa_classitem where pk_wa_class = '" + waLoginVO.getPk_prnt_class() + "' and cyear = '" + waLoginVO.getReyear() + "' and cperiod = '" + waLoginVO.getReperiod() + "' ) ";
		}

		WaClassItemVO[] classitems = testgetRoleClassItemVOs(waLoginVO, condition);
		return classitems;
	}

	public WaClassItemVO[] getApprovedClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		String condition = " wa_classitem.inapproveditem = 'Y'";
		WaClassItemVO[] classitems = null;
		if ("60130payslipaly".equals(loginVO.getNodeCode())) {
			classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), condition);
		} else {
			classitems = getRoleClassItemVOsNoPower(loginVO.getWaLoginVO(), condition);
		}
		return classitems;
	}

	public boolean isAnyTimesPayed(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		StringBuilder sbd = new StringBuilder();
		sbd.append("  select top 1 wa_periodstate.pk_wa_period from wa_periodstate,wa_period ");
		sbd.append("  	where wa_periodstate.pk_wa_period = wa_period.pk_wa_period  ");
		sbd.append("  	 and wa_period.cyear = ?  ");
		sbd.append("  	and wa_period.cperiod=? and wa_periodstate.payoffflag='Y' ");
		sbd.append("  and ( wa_periodstate.pk_wa_class  in ( ");
		sbd.append("  select pk_childclass  from wa_inludeclass where pk_parentclass = ? and wa_inludeclass.cyear = ? and wa_inludeclass.cperiod = ?  ");
		sbd.append("   ) or  wa_periodstate.pk_wa_class  = ? ) ");

		SQLParameter para = new SQLParameter();
		para.addParam(cyear);
		para.addParam(cperiod);
		para.addParam(pk_wa_class);
		para.addParam(cyear);
		para.addParam(cperiod);
		para.addParam(pk_wa_class);

		return isValueExist(sbd.toString(), para);
	}

	public boolean onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.checkflag = 'N' ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' and wa_data.caculateflag='Y' ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));

		if (!isRangeAll.booleanValue()) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(whereCondition));
		}

		updateTableByColKey("wa_data", "checkflag", UFBoolean.TRUE, sqlBuffer.toString());

		sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_data.pk_wa_data ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.checkflag = 'N' ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' ");

		sqlBuffer.append("   and wa_data.pk_wa_class ='" + waLoginVO.getPk_wa_class() + "' ");
		sqlBuffer.append("   and wa_data.cyear ='" + waLoginVO.getCyear() + "' ");
		sqlBuffer.append("   and wa_data.cperiod ='" + waLoginVO.getCperiod() + "' ");

		boolean isAllChecked = !isValueExist(sqlBuffer.toString());
		if (isAllChecked) {
			updatePeriodState("checkflag", UFBoolean.TRUE, waLoginVO);
			return true;
		}
		return false;
	}

	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll) throws BusinessException {
		PeriodStateVO period = waLoginVO.getPeriodVO();
		if (period != null) {
			//20171025 ljw 
			//       if (period.getIsapproved().booleanValue()) {
			//         throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0447"));
			//       }
			//end
			Boolean isInApproveing = Boolean.valueOf(new WapubDAO().isInApproveing(waLoginVO));
			if (isInApproveing.booleanValue()) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0448"));
			}
		}

		String caccyear = waLoginVO.getPeriodVO().getCaccyear();
		String caccperiod = waLoginVO.getPeriodVO().getCaccperiod();

		checkTaxRelate(waLoginVO, whereCondition, isRangeAll);

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.stopflag = 'N' ");
		sqlBuffer.append("   and wa_data.checkflag = 'Y' ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		if (!isRangeAll) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(whereCondition));
		}
		dealInPayLeavePsn(waLoginVO, whereCondition, isRangeAll);

		updateTableByColKey("wa_data", "prewadata", SQLTypeFactory.getNullType(1), sqlBuffer.toString());

		updateTableByColKey("wa_data", "checkflag", UFBoolean.FALSE, sqlBuffer.toString());

		updatePeriodState("checkflag", UFBoolean.FALSE, waLoginVO);

		String sqlWhereAccPeriod = " cpreclassid = '" + waLoginVO.getPk_wa_class() + "'";
		sqlWhereAccPeriod =
				sqlWhereAccPeriod + " and pk_wa_period in(select pk_wa_period from wa_period where caccyear='" + caccyear + "' and caccperiod='" + caccperiod + "')";

		updateTableByColKey("wa_periodstate", "caculateflag", UFBoolean.FALSE, WherePartUtil.addWhereKeyWord2Condition(sqlWhereAccPeriod));
		//20171025 ljw 取消审核的时候把审批标志置为N，删掉审批单
		String sqlWhere = " pk_wa_class = '" + waLoginVO.getPk_wa_class() + "'";
		updateTableByColKey("wa_periodstate", "isapproved", UFBoolean.FALSE, WherePartUtil.addWhereKeyWord2Condition(sqlWhere));
		delBillAndWorkflow(waLoginVO.getPk_wa_class(), waLoginVO.getCyear(), waLoginVO.getCperiod());
		//end

	}

	private void delBillAndWorkflow(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		String delSql = "delete from wa_payroll where pk_wa_class=? and cyear=? and cperiod=? and billstate='1'";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_wa_class);
		para.addParam(cyear);
		para.addParam(cperiod);
		getBaseDao().executeUpdate(delSql, para);
	}

	private void checkTaxRelate(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll) throws BusinessException {
		String addSql = getCommonWhereCondtion4Data(waLoginVO);
		if ((!isRangeAll) && (whereCondition != null) && (whereCondition.length() > 0)) {
			addSql = addSql + " and wa_data.pk_wa_data in(select pk_wa_data from wa_data where " + whereCondition + ")";
		}

		String sql =
				"select " + SQLHelper.getMultiLangNameColumn("b1.name") + " as psnname," + SQLHelper.getMultiLangNameColumn("w1.name") + " as classname,w2.batch " + "from  wa_data d2 " + "INNER JOIN wa_waclass w1 ON d2.pk_wa_class = w1.pk_wa_class " + "LEFT OUTER JOIN wa_inludeclass w2 	ON w2.pk_childclass = d2.pk_wa_class AND w2.cyear = d2.cyear AND w2.cperiod = d2.cperiod  " + "INNER JOIN bd_psndoc b1  ON b1.pk_psndoc = d2.pk_psndoc WHERE " + " d2.prewadata in(select pk_wa_data from wa_data where " + addSql + ") AND d2.checkflag = 'Y'  ";

		List result = (List) getBaseDao().executeQuery(sql, new MapListProcessor());
		if ((result == null) || (result.size() == 0)) {
			return;
		}
		Map map = null;
		Integer batch = Integer.valueOf(0);
		StringBuffer msg = new StringBuffer();
		msg.append(ResHelper.getString("60130paydata", "060130paydata0473"));
		for (int i = 0; i < result.size(); i++) {
			map = (Map) result.get(i);
			batch = (Integer) map.get("batch");
			msg.append("[" + map.get("psnname"));
			msg.append(":" + map.get("classname"));
			if ((batch != null) && (batch.intValue() != 0)) {
				if (batch.intValue() > 100) {
					msg.append(ResHelper.getString("60130paydata", "060130paydata0470"));
				} else {
					msg.append(MessageFormat.format(ResHelper.getString("60130paydata", "060130paydata0471"), new Object[] { FormatGenerator.getIndexFormat().format(batch.intValue()) }));
				}
			}
			msg.append("]");
		}
		throw new BusinessException(msg.toString());
	}

	private void dealInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll) throws BusinessException {
		String sql =
				"select * FROM wa_data WHERE wa_data.pk_wa_class IN( SELECT w2.pk_childclass 								   FROM wa_inludeclass w2 								  WHERE w2.pk_parentclass = ? 									AND w2.cyear = ? 									AND w2.cperiod = ? )	AND wa_data.cyear = ? 	AND wa_data.cperiod =? 	AND wa_data.checkflag = 'N' 	AND wa_data.pk_psndoc IN( SELECT pk_psndoc 								FROM wa_data w1 							   WHERE w1.pk_wa_class = ? 								 AND w1.cyear = ? 								 AND w1.cperiod = ? 								 AND w1.checkflag = 'Y')";

		if (!isRangeAll) {
			sql = sql + WherePartUtil.formatAddtionalWhere(whereCondition);
		}
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		List<PayfileVO> delVos = (List) getBaseDao().executeQuery(sql, parameter, new BeanListProcessor(PayfileVO.class));

		((IPayfileManageService) NCLocator.getInstance().lookup(IPayfileManageService.class)).delPsnVOs((PayfileVO[]) delVos.toArray(new PayfileVO[0]));
	}

	public PayfileVO[] getInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll) throws DAOException {
		String sql =
				"select bd_psndoc.code as psncode," + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + " as psnname " + "from wa_data ,bd_psndoc " + "where wa_data.pk_psndoc = bd_psndoc.pk_psndoc " + " and wa_data.pk_wa_class = ? " + "	and wa_data.cyear = ? " + "	and wa_data.cperiod = ? " + " and wa_data.checkflag = 'Y' " + "	and wa_data.pk_psndoc in(select w1.pk_psndoc " + "						from wa_data w1 ,wa_inludeclass w2 " + "						where w1.pk_wa_class = w2.pk_childclass " + "							and w1.cyear = w2.cyear " + "							and w1.cperiod = w2.cperiod " + "							and w2.batch >100 " + "							and w1.checkflag = 'N' " + "							and w2.pk_parentclass = ? " + "							and w1.cyear = ? " + "							and w1.cperiod = ? " + "	)";

		if ((!isRangeAll) && (whereCondition != null) && (whereCondition.length() > 0)) {
			sql = sql + " and wa_data.pk_wa_data in(select pk_wa_data from wa_data where " + whereCondition + ")";
		}
		SQLParameter parameter = getCommonParameter(waLoginVO);
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		PayfileVO[] vos = (PayfileVO[]) executeQueryVOs(sql, parameter, PayfileVO.class);
		return vos;
	}

	public String getRelateClassName(WaLoginVO waLoginVO) throws DAOException {
		String sql =
				"select w1.name,w2.batch from wa_periodstate p1,wa_waclass w1,wa_inludeclass w2 where  p1.pk_wa_class = w1.pk_wa_class and w1.pk_wa_class = w2.pk_childclass and p1.cpreclassid = ? and w2.cyear = ? and w2.cperiod = ? ";

		SQLParameter parameter = getCommonParameter(waLoginVO);
		Map map = (Map) getBaseDao().executeQuery(sql, parameter, new MapProcessor());
		String name = map.get("name").toString();
		int batch = Integer.parseInt(map.get("batch").toString());
		if (batch > 100) {
			return name + ResHelper.getString("60130paydata", "060130paydata0470");
		}

		return name + MessageFormat.format(ResHelper.getString("60130paydata", "060130paydata0471"), new Object[] { FormatGenerator.getIndexFormat().format(batch) });
	}

	public boolean isPayrollSubmit(WaLoginVO waLoginVO) throws DAOException {
		String classid = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select 1 from wa_periodstate ");
		sqlBuffer.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlBuffer.append("  wa_period.pk_wa_period and wa_periodstate.enableflag = 'Y') ");
		sqlBuffer.append("where wa_periodstate.isapproved != 'Y' ");
		sqlBuffer.append("and exists (select 1 from wa_payroll  ");
		sqlBuffer.append("where wa_payroll.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" and wa_payroll.billstate='").append(3);
		sqlBuffer.append("' and wa_payroll.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_payroll.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_payroll.cperiod = '").append(cperiod);
		sqlBuffer.append("') and wa_periodstate.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_period.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_period.cperiod = '").append(cperiod).append("' ");

		return isValueExist(sqlBuffer.toString());
	}

	public boolean isPayrollFree(WaLoginVO waLoginVO) throws DAOException {
		String classid = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select 1 from wa_periodstate ");
		sqlBuffer.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlBuffer.append(" wa_period.pk_wa_period and wa_periodstate.enableflag = 'Y') ");
		sqlBuffer.append("where wa_periodstate.isapproved != 'Y' ");
		sqlBuffer.append("and exists (select 1 from wa_payroll  ");
		sqlBuffer.append("where wa_payroll.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" and wa_payroll.billstate='").append(-1);
		sqlBuffer.append("' and wa_payroll.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_payroll.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_payroll.cperiod = '").append(cperiod);
		sqlBuffer.append("') and wa_periodstate.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_period.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_period.cperiod = '").append(cperiod).append("' ");

		return isValueExist(sqlBuffer.toString());
	}

	private WaClassItemVO[] testgetRoleClassItemVOs(WaLoginVO waLoginVO, String condition) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag,");
		sqlBuffer.append(" 'Y' as showflag,");
		sqlBuffer.append(" wa_classitem.idisplayseq as idisplayseq,  ");
		sqlBuffer.append("itempower.editflag ");
		sqlBuffer.append("from wa_classitem , wa_item,");
		sqlBuffer.append("(SELECT pk_wa_item,MAX(editflag) as editflag");
		sqlBuffer.append("   FROM wa_itempower ");

		sqlBuffer.append("  WHERE pk_wa_class = '" + waLoginVO.getPk_prnt_class() + "'");
		sqlBuffer.append("    AND pk_group ='" + waLoginVO.getPk_group() + "'");
		sqlBuffer.append("    AND pk_org = '" + waLoginVO.getPk_org() + "'");
		sqlBuffer.append("    AND ( pk_subject IN(SELECT pk_role ");
		sqlBuffer.append("				       FROM sm_user_role ");
		sqlBuffer.append("				      WHERE cuserid = '" + PubEnv.getPk_user() + "'");
		sqlBuffer.append("                   ) or pk_subject = '" + PubEnv.getPk_user() + "') ");
		sqlBuffer.append("  GROUP BY pk_wa_item ) as itempower");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_item = itempower.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ? ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));
		sqlBuffer.append(" order by wa_classitem.idisplayseq");

		SQLParameter parameter = getCommonParameter(waLoginVO);
		return (WaClassItemVO[]) executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	private WaClassItemVO[] getRoleClassItemVOsNoPower(WaLoginVO waLoginVO, String condition) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag,");
		sqlBuffer.append("isnull(wa_classitemdsp.bshow,'Y') as showflag,");
		sqlBuffer.append("isnull(wa_classitemdsp.displayseq,wa_classitem.idisplayseq) as idisplayseq ");
		sqlBuffer.append("from wa_classitem LEFT OUTER JOIN wa_classitemdsp  ");
		sqlBuffer.append(" ON wa_classitem.pk_wa_class = wa_classitemdsp.pk_wa_class  ");
		sqlBuffer.append(" AND wa_classitem.cyear = wa_classitemdsp.cyear  ");
		sqlBuffer.append(" AND wa_classitem.cperiod = wa_classitemdsp.cperiod  ");
		sqlBuffer.append(" AND wa_classitem.pk_wa_classitem = wa_classitemdsp.pk_wa_classitem and wa_classitemdsp.pk_user = '" + PubEnv.getPk_user() + "' , wa_item ");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ? ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));
		sqlBuffer.append(" order by wa_classitem.idisplayseq");

		SQLParameter parameter = getCommonParameter(waLoginVO);
		return (WaClassItemVO[]) executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	public DataSVO[] getDataSVOs(WaLoginContext loginContext) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_datas.pk_wa_data, ");
		sqlBuffer.append("       wa_datas.pk_wa_datas, ");
		sqlBuffer.append("       wa_datas.pk_wa_classitem, ");
		sqlBuffer.append("       wa_datas.ts, ");
		sqlBuffer.append("       wa_datas.value, ");
		sqlBuffer.append("       wa_datas.to_next, ");
		sqlBuffer.append("       wa_datas.caculatevalue, ");
		sqlBuffer.append("       wa_datas.notes, ");
		sqlBuffer.append("       wa_item.itemkey itemkey, ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("wa_classitem.name") + "  itemname, ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + "psnname" + ", ");
		sqlBuffer.append("       wa_classitem.iflddecimal, ");
		sqlBuffer.append("       bd_psndoc.code psncode, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("       wa_data.checkflag ");
		sqlBuffer.append("  from wa_datas ");
		sqlBuffer.append(" inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join wa_classitem on wa_datas.pk_wa_classitem = wa_classitem.pk_wa_classitem ");

		sqlBuffer.append(" inner join wa_item on wa_classitem.pk_wa_item = wa_item.pk_wa_item ");

		sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(getCommonWhereCondtion4Data(loginContext.getWaLoginVO())));

		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), "wa_data", "SpecialPsnAction", "hi_psnjob");
		if (!StringUtils.isBlank(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), "6007psnjob", "wadefault", "wa_data");

		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		sqlBuffer.append("  and  wa_item.pk_wa_item in (" + ItemPowerUtil.getItemPower(loginContext) + ")");
		sqlBuffer.append(" order by wa_datas.pk_wa_data, wa_datas.pk_wa_classitem");
		return (DataSVO[]) executeQueryVOs(sqlBuffer.toString(), DataSVO.class);
	}

	public DataVO[] queryByCondition(WaLoginContext context, String condition, String orderCondtion) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + "psnname" + ", ");

		sqlBuffer.append("       bd_psndoc.code psncode,  ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psnidtype.name") + "  as idtype, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, bd_psndoc.id as id, ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + "deptname" + ", ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + "orgname" + ", ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + "plsname" + ", ");

		sqlBuffer.append("       om_job.jobname, ");

		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + "postname" + ", ");
		sqlBuffer.append("       wa_data.* ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");

		sqlBuffer.append("  left outer join bd_psnidtype on bd_psndoc.idtype = bd_psnidtype.pk_identitype ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}

		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), "6007psnjob", "wadefault", "wa_data");

		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}
		return (DataVO[]) executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	public DataVO[] queryByConditionWithItem(WaLoginContext context, String orderCondtion, WaClassItemVO[] classItemVOs)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + "psnname" + ", ");

		sqlBuffer.append("       bd_psndoc.code psncode, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + "deptname" + ", ");

		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + "orgname" + ", ");

		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + "plsname" + ", ");

		sqlBuffer.append("       om_job.jobname, ");
		sqlBuffer.append("       om_post.postname, ");
		sqlBuffer.append("       wa_data.f_1,wa_data.f_3 ");
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				sqlBuffer.append("       ,wa_data." + classItemVO.getItemkey());
			}
		}
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");

		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");

		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");

		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");

		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");

		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");

		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}
		return (DataVO[]) executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	public DataVO[] querySumDataByCondition(WaLoginContext context, String orderCondtion, WaClassItemVO[] classItemVOs)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  ");
		sqlBuffer.append("  org_orgs_v.code orgcode ,");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + "orgname" + ", ");

		sqlBuffer.append("  org_dept_v.code deptcode ,");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + "deptname" + ", ");

		sqlBuffer.append("   count(wa_data.pk_psnjob ) psnnum");
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				if ((classItemVO.getIitemtype() != null) && (classItemVO.getIitemtype().intValue() == TypeEnumVO.FLOATTYPE.value().intValue())) {
					sqlBuffer.append("       ,sum(" + classItemVO.getItemkey() + ") " + classItemVO.getItemkey());
				}
			}
		}

		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");

		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		sqlBuffer.append(" group by org_orgs_v.code," + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + ",org_dept_v.code," + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + " ");

		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code");
		}
		return (DataVO[]) executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	public DataVO[] queryByPKSCondition(String condition, String orderCondtion) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + "psnname" + ", ");
		sqlBuffer.append("       bd_psndoc.code psncode, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + "deptname" + ", ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + "orgname" + ", ");
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + "plsname" + ", ");

		sqlBuffer.append("       om_job.jobname, ");

		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + "postname" + ", ");
		sqlBuffer.append("       wa_data.*,datapower.operateflag ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");

		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");

		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append(" left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");

		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");

		String operateConditon =
				((IDataPermissionPubService) NCLocator.getInstance().lookup(IDataPermissionPubService.class)).getDataPermissionSQLWherePartByMetaDataOperation(PubEnv.getPk_user(), "wa_data", "Edit", PubEnv.getPk_group());

		sqlBuffer.append(" left outer join (select 'Y' as operateflag,pk_wa_data from wa_data  where ");

		sqlBuffer.append(operateConditon);
		sqlBuffer.append(") datapower on wa_data.pk_wa_data = datapower.pk_wa_data ");

		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" where  wa_data.pk_wa_data in (" + condition + ")");
		}

		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}

		return (DataVO[]) executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	public String[] queryPKSByCondition(WaLoginContext context, String condition, String orderCondtion) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ");
		sqlBuffer.append("       wa_data.pk_wa_data ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");

		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");

		sqlBuffer.append(" left join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlBuffer.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");

		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");

		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");

			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}

		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), "6007psnjob", "wadefault", "wa_data");

		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}
		DataVO[] vos = (DataVO[]) executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

		String[] pks = new String[0];
		if (vos != null) {
			pks = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				pks[i] = vos[i].getPk_wa_data();
			}
		}

		return pks;
	}

	public AggPayDataVO queryAggPayDataVOByCondition(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(loginContext.getWaLoginVO());
		aggPayDataVO.setLoginVO(waLoginVO);

		WaClassItemVO[] classItemVOs = getUserClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);

		String[] pks = queryPKSByCondition(loginContext, condition, orderCondtion);
		aggPayDataVO.setDataPKs(pks);
		aggPayDataVO.setSumData(querySumData(loginContext, condition, classItemVOs));
		DataSVO[] dsvos = getDataSVOs(loginContext);

		aggPayDataVO.setDataSmallVO(dsvos);

		return aggPayDataVO;
	}

	public DataVO querySumData(WaLoginContext context, String condition, WaClassItemVO[] classItemVOs) throws DAOException {
		if (ArrayUtils.isEmpty(classItemVOs)) {
			return new DataVO();
		}
		StringBuffer sumSql = new StringBuffer();

		for (int i = 0; i < classItemVOs.length; i++) {
			if (classItemVOs[i].getIitemtype().intValue() == 0) {
				sumSql.append("sum(wa_data." + classItemVOs[i].getItemkey() + ") as " + classItemVOs[i].getItemkey() + ",");
			}
		}

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ");
		sqlBuffer.append(sumSql);
		sqlBuffer.append(" '1' as pk_wa_data from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");

		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");

		sqlBuffer.append(" left join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlBuffer.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");

		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");

			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}

		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), "6007psnjob", "wadefault", "wa_data");

		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		DataVO vo = (DataVO) executeQueryAppendableVO(sqlBuffer.toString(), DataVO.class);
		return vo;
	}

	public AggPayDataVO queryAggPayDataVOs(WaLoginContext loginContext, String condition, String orderCondtion) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(loginContext.getWaLoginVO());
		aggPayDataVO.setLoginVO(waLoginVO);

		WaClassItemVO[] classItemVOs = getUserClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);

		DataVO[] dataVOs = queryByCondition(loginContext, condition, orderCondtion);
		aggPayDataVO.setDataVOs(dataVOs);

		DataSVO[] dsvos = getDataSVOs(loginContext);

		aggPayDataVO.setDataSmallVO(dsvos);
		return aggPayDataVO;
	}

	public AggPayDataVO queryAggPayDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);

		DataVO[] dataVOs = queryByConditionWithItem(loginContext, null, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	public AggPayDataVO querySumDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);

		DataVO[] dataVOs = querySumDataByCondition(loginContext, null, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	public DataVO[] querySumDataByConditionAll(WaLoginContext context, String condition, String orderCondtion, WaClassItemVO[] classItemVOs)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  COUNT(wa_data.pk_psnjob ) psnnum ");
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				if ((classItemVO.getIitemtype() != null) && (classItemVO.getIitemtype().intValue() == TypeEnumVO.FLOATTYPE.value().intValue())) {
					sqlBuffer.append("       ,sum(" + classItemVO.getItemkey() + ") " + classItemVO.getItemkey());
				}
			}
		}

		sqlBuffer.append("  from wa_data where");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");

			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}

		return (DataVO[]) executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	public AggPayDataVO querySumDataVOForrollAll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);

		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), "6007psnjob", "wadefault", "wa_data");

		DataVO[] dataVOs = querySumDataByConditionAll(loginContext, powerSql, null, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	private void updatePeriodState(String[] colKeys, Object[] colValues, WaLoginVO waLoginVO) throws BusinessException {
		updateTableByColKey("wa_periodstate", colKeys, colValues, getCommonWhereCondtion4PeriodState(waLoginVO));
	}

	public void updatePeriodState(String colKey, Object colValue, WaLoginVO waLoginVO) throws BusinessException {
		updatePeriodState(new String[] { colKey }, new Object[] { colValue }, waLoginVO);
	}

	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws BusinessException {
		TaxBindCaculateService caculateService = new TaxBindCaculateService(loginContext, whereCondition);
		caculateService.doCaculate();
	}

	public void onPay(WaLoginContext loginContext) throws BusinessException {
		WaLoginVO waLoginVO = loginContext.getWaLoginVO();

		String[] colKeys = { "payoffflag", "vpaycomment", "cpaydate" };

		String comment = waLoginVO.getPeriodVO().getVpaycomment();
		SQLParamType nullValue = SQLTypeFactory.getNullType(12);
		UFDate paydate = waLoginVO.getPeriodVO().getCpaydate();

		Object[] colValues = { UFBoolean.TRUE, comment == null ? nullValue : comment, paydate == null ? nullValue : paydate };

		updatePeriodState(colKeys, colValues, waLoginVO);

		if (!isChildPayoff(loginContext)) {
			colKeys = new String[] { "payoffflag" };
			colValues = new Object[] { UFBoolean.TRUE };
			String cond = getPeriodstateCond(waLoginVO.getPk_prnt_class(), waLoginVO.getCyear(), waLoginVO.getCperiod());
			updateTableByColKey("wa_periodstate", colKeys, colValues, cond);
		}
		colKeys = new String[] { "vpaycomment", "cpaydate" };
		colValues = new Object[] { comment == null ? nullValue : comment, paydate == null ? nullValue : paydate };
		updateTableByColKey("wa_data", colKeys, colValues, WherePartUtil.getCommonWhereCondtion4ChildData(waLoginVO) + " ");

		String updatePayrollSql =
				"update wa_payroll set billstate = '102' where billstate = '1' and pk_wa_class = '" + waLoginVO.getPk_wa_class() + "'";

		getBaseDao().executeUpdate(updatePayrollSql);
	}

	public boolean isChildPayoff(WaLoginContext loginContext) throws DAOException {
		String sql =
				"SELECT wa_inludeclass.pk_childclass FROM wa_inludeclass,wa_periodstate,wa_period WHERE wa_inludeclass.pk_childclass = wa_periodstate.pk_wa_class 	AND wa_periodstate.pk_wa_period = wa_period.pk_wa_period 	AND wa_inludeclass.cyear = wa_period.cyear 	AND wa_inludeclass.cperiod = wa_period.cperiod 	AND wa_inludeclass.pk_parentclass = ? 	AND wa_inludeclass.cyear = ? 	AND wa_inludeclass.cperiod = ? 	AND wa_periodstate.payoffflag = 'N' ";

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(loginContext.getPk_prnt_class());
		parameter.addParam(loginContext.getWaYear());
		parameter.addParam(loginContext.getWaPeriod());
		return isValueExist(sql, parameter);
	}

	private String getPeriodstateCond(String pk_wa_class, String cyear, String cperiod) {
		String cond =
				" pk_wa_class = '" + pk_wa_class + "' and exists " + "(select wa_period.pk_wa_period  from wa_period  " + "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period" + " and wa_period.cyear =  '" + cyear + "' and wa_period.cperiod =  '" + cperiod + "' and  " + "wa_periodstate.pk_wa_class =  '" + pk_wa_class + "')";

		return cond;
	}

	public void update(Object object, WaLoginVO waLoginVO) throws BusinessException {
		if (!object.getClass().isArray()) {
			DataVO datavo = (DataVO) object;
			singleUpdate(datavo);
		} else {
			Object[] objs = (Object[]) object;
			List<Object> list = Arrays.asList(objs);
			DataVO[] dataVos = (DataVO[]) list.toArray(new DataVO[objs.length]);
			batchUpdate(dataVos);
		}

		updatePeriodState("caculateflag", UFBoolean.FALSE, waLoginVO);
	}

	private void batchUpdate(DataVO... objs) throws BusinessException {
		BDPKLockUtil.lockSuperVO(objs);

		BDVersionValidationUtil.validateSuperVO(objs);
		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			String sql =
					"select wa_data.*,bd_psndoc.name psnname from wa_data inner join bd_psndoc on wa_data.PK_PSNDOC = bd_psndoc.pk_psndoc where pk_wa_data in (" + inSQLCreator.getInSQL(objs, "pk_wa_data") + ")";

			DataVO[] dbdatavos = (DataVO[]) executeQueryVOs(sql, DataVO.class);

			if ((dbdatavos == null) || (dbdatavos.length == 0)) {
				return;
			}

			List<String> needUpdateNamesList = getNeedUpdateNamesList(objs[0]);
			List<DataVO> list_update = new ArrayList();
			for (int i = 0; i < objs.length; i++) {

				DataVO temp_datavo = objs[i];
				temp_datavo.setCaculateflag(UFBoolean.FALSE);

				if (!temp_datavo.getCheckflag().booleanValue()) {
					list_update.add(temp_datavo);
				}
			}
			JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());

			getBaseDao().updateVOArray((SuperVO[]) list_update.toArray(new DataVO[list_update.size()]), (String[]) needUpdateNamesList.toArray(new String[needUpdateNamesList.size()]));

			for (int i = 0; i < list_update.size(); i++) {
				WaBusilogUtil.writeEditLog((DataVO) list_update.get(i), (String[]) needUpdateNamesList.toArray(new String[0]));
			}

		} finally {
			inSQLCreator.clear();
		}
	}

	private List<String> getNeedUpdateNamesList(DataVO datavo) {
		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		needUpdateNamesList.add("caculateflag");
		needUpdateNamesList.add("cpaydate");
		needUpdateNamesList.add("vpaycomment");

		return needUpdateNamesList;
	}

	private DataVO singleUpdate(DataVO datavo) throws BusinessException {
		BDPKLockUtil.lockSuperVO(new SuperVO[] { datavo });

		BDVersionValidationUtil.validateSuperVO(new SuperVO[] { datavo });

		DataVO dbdatavo = (DataVO) retrieveByPK(DataVO.class, datavo.getPk_wa_data());
		if (dbdatavo == null) {
			throw new BusinessException(datavo.getAttributeValue("psnname") + ResHelper.getString("60130paydata", "060130paydata0449"));
		}
		if (dbdatavo.getCheckflag().booleanValue()) {
			throw new BusinessException(datavo.getAttributeValue("psnname") + ResHelper.getString("60130paydata", "060130paydata0450"));
		}

		datavo.setCaculateflag(UFBoolean.FALSE);
		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		needUpdateNamesList.add("caculateflag");
		needUpdateNamesList.add("cpaydate");
		needUpdateNamesList.add("vpaycomment");

		JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());
		getBaseDao().updateVO(datavo, (String[]) needUpdateNamesList.toArray(new String[0]));

		WaBusilogUtil.writeEditLog(datavo, (String[]) needUpdateNamesList.toArray(new String[0]));

		return datavo;
	}

	public void onUnPay(WaLoginVO waLoginVO) throws BusinessException {
		String checkSql =
				"SELECT batch FROM wa_inludeclass WHERE pk_parentclass=( select distinct pk_parentclass from wa_inludeclass where pk_childclass =  '" + waLoginVO.getPk_wa_class() + "') and cyear = '" + waLoginVO.getCyear() + "' and cperiod = '" + waLoginVO.getCperiod() + "' and batch > " + waLoginVO.getBatch() + " and batch<100 and pk_childclass <>'" + waLoginVO.getPk_wa_class() + "'";

		if (isValueExist(checkSql)) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0451"));
		}

		checkSql = "SELECT 1 FROM wa_data WHERE fipendflag = 'Y' and pk_wa_class = ? and cyear = ? and cperiod = ?";

		if (isValueExist(checkSql, getCommonParameter(waLoginVO))) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0534"));
		}

		boolean isbill = ((IAmoSchemeQuery) NCLocator.getInstance().lookup(IAmoSchemeQuery.class)).isApportion(waLoginVO);
		if (isbill == true) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0452"));
		}

		String[] colKeys = { "payoffflag", "vpaycomment", "cpaydate" };
		SQLParamType value = SQLTypeFactory.getNullType(12);

		Object[] colValues = { UFBoolean.FALSE, value, value };
		updatePeriodState(colKeys, colValues, waLoginVO);

		colKeys = new String[] { "payoffflag" };
		colValues = new Object[] { UFBoolean.FALSE };
		String cond = getPeriodstateCond(waLoginVO.getPk_prnt_class(), waLoginVO.getCyear(), waLoginVO.getCperiod());
		updateTableByColKey("wa_periodstate", colKeys, colValues, cond);

		String updatePayrollSql =
				"update wa_payroll set billstate = '1' where billstate = '102' and pk_wa_class = '" + waLoginVO.getPk_wa_class() + "' and cyear = '" + waLoginVO.getCyear() + "' and cperiod = '" + waLoginVO.getCperiod() + "' ";

		getBaseDao().executeUpdate(updatePayrollSql);
	}

	public void onReplace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula) throws BusinessException {
		String sql = "update wa_data set ";

		if (TypeEnumVO.FLOATTYPE.value().equals(replaceItem.getIitemtype())) {
			sql =
					sql + replaceItem.getItemkey() + "= " + WaCommonImpl.getRoundSql(getBaseDao().getDBType(), formula, replaceItem.getIflddecimal().intValue(), replaceItem.getRound_type().intValue());
		} else {
			sql = sql + replaceItem.getItemkey() + "=" + formula + ", ";
		}

		sql = sql + "caculateflag= 'N'  ";
		sql = sql + " where checkflag='N' and pk_wa_class = ?  and  cyear = ?  and cperiod = ? ";
		sql = sql + " and dr=0  and stopflag = 'N' ";

		sql = sql + WherePartUtil.formatAddtionalWhere(whereCondition);

		getBaseDao().executeUpdate(sql, getCommonParameter(waLoginVO));

		WaBusilogUtil.writePaydataReplaceBusiLog(waLoginVO, replaceItem, formula);

		updatePeriodState("caculateflag", UFBoolean.FALSE, waLoginVO);
	}

	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException {
		if (dataSVOs == null) {
			return;
		}
		DataVO[] dataVOs = new DataVO[dataSVOs.length];
		for (int i = 0; i < dataVOs.length; i++) {
			dataVOs[i] = new DataVO();
			dataVOs[i].setPk_wa_data(dataSVOs[i].getPk_wa_data());
			dataVOs[i].setCaculateflag(UFBoolean.FALSE);
		}

		BDPKLockUtil.lockSuperVO(dataVOs);

		List<DataSVO> deleteList = new LinkedList();
		List<DataSVO> updateList = new LinkedList();
		List<DataSVO> insertList = new LinkedList();

		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(dataVOs, "pk_wa_data");
		DataVO[] newdataVOs = (DataVO[]) retrieveByClause(DataVO.class, " pk_wa_data in (" + insql + ")");
		if (newdataVOs == null) {
			return;
		}
		HashMap<String, DataVO> map = new HashMap();
		for (DataVO dvo : newdataVOs) {
			map.put(dvo.getPk_wa_data(), dvo);
		}
		for (DataSVO vo : dataSVOs) {
			if (map.get(vo.getPk_wa_data()) == null) {
				String psnname = "";
				if (map.get(vo.getPk_wa_data()) != null) {
					psnname = vo.getPsnname();
				}
				throw new BusinessException(psnname + ResHelper.getString("60130paydata", "060130paydata0449"));
			}
			if (((DataVO) map.get(vo.getPk_wa_data())).getCheckflag().booleanValue()) {
				String psnname = "";
				if (map.get(vo.getPk_wa_data()) != null) {
					psnname = vo.getPsnname();
				}
				throw new BusinessException(psnname + ResHelper.getString("60130paydata", "060130paydata0450"));
			}
			if (vo.getStatus() == 2) {
				insertList.add(vo);
			} else if (vo.getStatus() == 3) {
				deleteList.add(vo);
			} else if (vo.getStatus() == 1) {
				updateList.add(vo);
			}
		}

		getBaseDao().deleteVOArray((SuperVO[]) deleteList.toArray(new DataSVO[0]));

		StringBuffer sqlB = new StringBuffer();

		DataSVO[] updatesvo = (DataSVO[]) updateList.toArray(new DataSVO[updateList.size()]);
		String[] field = HRWACommonConstants.DATASCOLUMN;
		String tableName = isc.insertValues("wa_temp_datas", field, field, updatesvo);

		if (tableName != null) {
			sqlB.append("select wa_datas.pk_wa_datas, ");
			sqlB.append(SQLHelper.getMultiLangNameColumn("bd_psndoc.name"));
			sqlB.append(" psnname ,");
			sqlB.append(SQLHelper.getMultiLangNameColumn("wa_classitem.name"));
			sqlB.append(" itemname");
			sqlB.append(" from wa_datas inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data");
			sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlB.append(" inner join wa_classitem on wa_classitem.pk_wa_classitem = wa_datas.pk_wa_classitem ");
			sqlB.append(" inner join ");
			sqlB.append(tableName);
			sqlB.append(" on (wa_datas.pk_wa_data = ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_data ");
			sqlB.append(" and wa_datas.pk_wa_classitem = ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_classitem ");
			sqlB.append(" and wa_datas.pk_wa_datas != ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_datas )");

			DataSVO[] resultvos = (DataSVO[]) executeQueryVOs(sqlB.toString(), DataSVO.class);
			if ((resultvos != null) && (resultvos.length > 0)) {
				StringBuffer eMsg = new StringBuffer();
				for (DataSVO resultvo : resultvos) {
					eMsg.append(ResHelper.getString("60130paydata", "060130paydata0454", new String[] { resultvo.getPsnname(), resultvo.getItemname() }));

					eMsg.append("/r/n");
				}
				throw new BusinessException(eMsg.toString());
			}
			getBaseDao().updateVOArray(updatesvo);
		}

		DataSVO[] insertsvo = (DataSVO[]) insertList.toArray(new DataSVO[insertList.size()]);
		String[] field2 = HRWACommonConstants.DATAS2COLUMN;
		String tableName2 = isc.insertValues("wa_temp_datas2", field2, field2, insertsvo);

		if (tableName2 != null) {
			sqlB = new StringBuffer();
			sqlB.append("select ");
			sqlB.append(SQLHelper.getMultiLangNameColumn("bd_psndoc.name"));
			sqlB.append(" psnname ,");
			sqlB.append(SQLHelper.getMultiLangNameColumn("wa_classitem.name"));
			sqlB.append(" itemname");
			sqlB.append(" from wa_datas inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data");
			sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlB.append(" inner join wa_classitem on wa_classitem.pk_wa_classitem = wa_datas.pk_wa_classitem ");
			sqlB.append(" inner join ");
			sqlB.append(tableName2);
			sqlB.append(" on (wa_datas.pk_wa_data = ");
			sqlB.append(tableName2);
			sqlB.append(".pk_wa_data ");
			sqlB.append(" and wa_datas.pk_wa_classitem = ");
			sqlB.append(tableName2);
			sqlB.append(".pk_wa_classitem )");

			DataSVO[] iresultvos = (DataSVO[]) executeQueryVOs(sqlB.toString(), DataSVO.class);
			if ((iresultvos != null) && (iresultvos.length > 0)) {
				StringBuffer eMsg = new StringBuffer();
				for (DataSVO iresultvo : iresultvos) {
					eMsg.append(ResHelper.getString("60130paydata", "060130paydata0454", new String[] { iresultvo.getPsnname(), iresultvo.getItemname() }));
				}

				throw new BusinessException(eMsg.toString());
			}

			getBaseDao().insertVOArray(insertsvo);
		}

		getBaseDao().updateVOArray(dataVOs, new String[] { "caculateflag" });
	}

	public static SQLParameter getCommonParameter(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonParameter(waLoginVO);
	}

	public static SQLParameter getCommonParameterTwice(WaLoginVO waLoginVO) {
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return parameter;
	}

	public static String getCommonWhereCondtion4Data(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonWhereCondtion4Data(waLoginVO);
	}

	public static String getCommonWhereCondtion4PeriodState(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonWhereCondtion4PeriodState(waLoginVO);
	}

	public void checkWaClassStateChange(WaLoginVO waLoginVO, String whereCondition) throws BusinessException {
		WaState oldStates = waLoginVO.getState();
		WaState newStates = WaClassStateHelper.getWaclassVOWithState(waLoginVO).getState();

		if (oldStates != newStates) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0457"));
		}
	}

	public WaLoginVO getNewWaclassVOWithState(WaLoginVO waLoginVO) throws BusinessException {
		return WaClassStateHelper.getWaclassVOWithState(waLoginVO);
	}

	public DataVO[] getContractDataVOs(WaLoginContext context, String whereCondition, String orderCondition) throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		WaLoginVO waLoginVO = context.getWaLoginVO();

		String waYear = waLoginVO.getPeriodVO().getCyear();
		String waPeriod = waLoginVO.getPeriodVO().getCperiod();
		UFBoolean checkFlag = waLoginVO.getPeriodVO().getCheckflag();
		if (!pk_wa_class.equals(waLoginVO.getPk_prnt_class())) {
			pk_wa_class = waLoginVO.getPk_prnt_class();
			if ((checkFlag != null) && (!checkFlag.booleanValue())) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0548"));
			}
		}

		WaClassVO classvo = new WaClassVO();
		classvo.setPk_wa_class(pk_wa_class);
		classvo.setCyear(waYear);
		classvo.setCperiod(waPeriod);
		PeriodStateVO periodStateVO = new MonthEndDAO().getSubclassPrePeriodVO(classvo);

		if (periodStateVO == null) {
			return null;
		}
		String preYear = periodStateVO.getCyear();
		String prePeriod = periodStateVO.getCperiod();

		WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);

		StringBuffer tempsql = new StringBuffer();
		tempsql.append(" select a.pk_psndoc from wa_data a, wa_data b where a.pk_wa_class = b.pk_wa_class and a.pk_psndoc = b.pk_psndoc ");

		if ((classItemVOs != null) && (classItemVOs.length > 0)) {
			tempsql.append(" and ( ");
			for (int i = 0; i < classItemVOs.length - 1; i++) {
				tempsql.append(" a.");
				tempsql.append(classItemVOs[i].getItemkey());
				tempsql.append(" <> b.");
				tempsql.append(classItemVOs[i].getItemkey()).append(" or ");
			}
			tempsql.append(" a.");
			tempsql.append(classItemVOs[(classItemVOs.length - 1)].getItemkey());
			tempsql.append(" <> b.");
			tempsql.append(classItemVOs[(classItemVOs.length - 1)].getItemkey());
			tempsql.append(" ) ");
		}

		tempsql.append(" and a.pk_wa_class = '");
		tempsql.append(pk_wa_class);
		tempsql.append("' and a.cyear = '");
		tempsql.append(waYear);
		tempsql.append("' and a.cperiod = '");
		tempsql.append(waPeriod);
		tempsql.append("' and b.pk_wa_class = '");
		tempsql.append(pk_wa_class);
		tempsql.append("' and b.cyear = '");
		tempsql.append(preYear);
		tempsql.append("' and b.cperiod = '");
		tempsql.append(prePeriod);
		tempsql.append("'  ");

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_data.pk_psndoc ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.pk_psndoc in ");
		sqlBuffer.append("       (select wa_data.pk_psndoc ");
		sqlBuffer.append("          from wa_data ");
		sqlBuffer.append("         where ((wa_data.pk_wa_class = '" + pk_wa_class + "' and wa_data.cyear = '" + waYear + "' and wa_data.cperiod = '" + waPeriod + "') or (wa_data.pk_wa_class = '" + pk_wa_class + "' and wa_data.cyear = '" + preYear + "' and wa_data.cperiod = '" + prePeriod + "')) ");

		sqlBuffer.append("   ) ");
		sqlBuffer.append("   and wa_data.pk_psndoc not in ");
		sqlBuffer.append("       (select a.pk_psndoc ");
		sqlBuffer.append("          from wa_data a, wa_data b ");
		sqlBuffer.append("         where a.pk_wa_class = b.pk_wa_class ");
		sqlBuffer.append("           and a.pk_psndoc = b.pk_psndoc ");
		sqlBuffer.append("           and a.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("           and a.cyear = '" + waYear + "' ");
		sqlBuffer.append("           and a.cperiod = '" + waPeriod + "' ");
		sqlBuffer.append("           and b.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("           and b.cyear = '" + preYear + "' ");
		sqlBuffer.append("           and b.cperiod = '" + prePeriod + "' ");
		sqlBuffer.append(") ");

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + "psnname" + ", ");
		sqlB.append("       bd_psndoc.code psncode, ");
		sqlB.append("       hi_psnjob.clerkcode, ");
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + "deptname" + ", ");
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + "plsname" + ", ");
		sqlB.append("       om_job.jobname, ");

		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + "postname" + ", ");
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  orgname, ");
		sqlB.append("       wa_data.* ");
		sqlB.append("  from wa_data ");
		sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlB.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlB.append(" inner join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlB.append("  inner join org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlB.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlB.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlB.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");

		String condtion = " where wa_data.pk_wa_class = '" + pk_wa_class + "'  ";
		condtion =
				condtion + " and ((wa_data.cyear = '" + waYear + "' and wa_data.cperiod = '" + waPeriod + "') or (wa_data.cyear = '" + preYear + "' and wa_data.cperiod = '" + prePeriod + "')) ";

		condtion = condtion + " and stopflag = 'N' ";

		condtion =
				condtion + " and (wa_data.pk_psndoc in ( " + tempsql.toString() + ")or wa_data.pk_psndoc in (" + sqlBuffer.toString() + ")) ";

		if ((whereCondition != null) && (whereCondition.startsWith("pk_")))
			whereCondition = " hi_psnjob." + whereCondition;
		condtion = condtion + WherePartUtil.formatAddtionalWhere(whereCondition);

		sqlB.append(condtion);

		String powerSql = WaPowerSqlHelper.getWaPowerSql(waLoginVO.getPk_group(), "6007psnjob", "wadefault", "wa_data");

		if (!StringUtils.isBlank(powerSql)) {
			sqlB.append(" and " + powerSql);
		}
		if (StringUtil.isEmpty(orderCondition)) {
			orderCondition = " org_dept_v.code , hi_psnjob.clerkcode";
		}
		sqlB.append(" order by ").append(orderCondition + ",wa_data.cyear,wa_data.cperiod");

		return (DataVO[]) executeQueryAppendableVOs(sqlB.toString(), DataVO.class);
	}

	public WaItemVO[] getUnitDigitItem(WaLoginVO waLoginVO) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty ");
		sqlBuffer.append("  from wa_item ");
		sqlBuffer.append(" where wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class in ");
		sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype = 0 ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return (WaItemVO[]) executeQueryVOs(sqlBuffer.toString(), parameter, WaItemVO.class);
	}

	/**
	 * tsy 20171129
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public WaItemVO[] getUnitAllItem(WaLoginVO waLoginVO) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty,wa_item.iitemtype ");
		sqlBuffer.append("  from wa_item ");
		sqlBuffer.append(" where wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class in ");
		sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype in (0,1,2) ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return (WaItemVO[]) executeQueryVOs(sqlBuffer.toString(), parameter, WaItemVO.class);
	}

	public WaItemVO[] getParentClassDigitItem(WaLoginVO waLoginVO) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty ");
		sqlBuffer.append("  from wa_item ");
		sqlBuffer.append("   where  wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype = 0 ");

		return (WaItemVO[]) executeQueryVOs(sqlBuffer.toString(), getCommonParameter(waLoginVO), WaItemVO.class);
	}

	public void deleteUnitClassPsn(WaLoginVO waLoginVO) throws DAOException {
		SQLParameter parameter = getCommonParameter(waLoginVO);
		String sql = "delete from wa_data where pk_wa_class = ? and cyear= ? and cperiod = ? ";

		getBaseDao().executeUpdate(sql, parameter);
	}

	public void deleteUnitRelation(WaLoginVO waLoginVO) throws DAOException {
		SQLParameter parameter = getCommonParameterTwice(waLoginVO);

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("delete from wa_redata where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");

		sqlBuffer.append("          (select wa_data.pk_psnjob ");
		sqlBuffer.append("             from wa_data ");
		sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		sqlBuffer.append("              and wa_data.cyear = ? ");
		sqlBuffer.append("              and wa_data.cperiod = ? )");
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
	}

	public PayfileVO[] getUnitPsnVOs(WaLoginVO waLoginVO) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select pk_psnjob,pk_psndoc,pk_psnorg,taxtype,taxtableid,isderate,isndebuct,derateptg,pk_group,pk_org,pk_bankaccbas1,pk_bankaccbas2,pk_bankaccbas3,partflag,stopflag, wa_data.* from wa_data ");

		sqlBuffer.append("     where wa_data.pk_wa_class in  ");
		sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		sqlBuffer.append("      and wa_data.cyear = ? ");
		sqlBuffer.append("      and wa_data.cperiod = ? ");
		sqlBuffer.append("      and wa_data.stopflag = 'N' ");
		sqlBuffer.append("      and wa_data.pk_psndoc not in ");
		sqlBuffer.append("       (select pk_psndoc ");
		sqlBuffer.append("       		from wa_data ");
		sqlBuffer.append("          	where wa_data.pk_wa_class = ? ");
		sqlBuffer.append("            	and wa_data.cyear = ? ");
		sqlBuffer.append("              and wa_data.cperiod = ?)");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return (PayfileVO[]) executeQueryVOs(sqlBuffer.toString(), parameter, PayfileVO.class);
	}

	public void updateData(WaLoginVO waLoginVO, WaItemVO[] itemVOs) throws DAOException {
		if (itemVOs == null) {
			return;
		}

		if (getBaseDao().getDBType() == 2) {
			updateDataSQLDbs(itemVOs, waLoginVO);
		} else {
			updateDataOracleDbs(itemVOs, waLoginVO);
		}
	}

	private void updateDataSQLDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {
		String tableName = getDataTableName(waLoginVO);

		List<String> list = new LinkedList();
		for (WaItemVO itemVO : itemVOs) {
			list.add(tableName + "." + itemVO.getItemkey() + " = sum_data." + itemVO.getItemkey() + "");
		}

		String colNames = FormatVO.formatListToString(list, "");

		List<String> sumList = new LinkedList();
		for (WaItemVO itemVO : itemVOs) {
			sumList.add("sum(" + tableName + "." + itemVO.getItemkey() + ") " + itemVO.getItemkey());
		}

		String sumColNames = FormatVO.formatListToString(sumList, "");
		String extraConditon = "";

		extraConditon = " and wa_data.stopflag = 'N' ";

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " ");
		sqlBuffer.append("   set " + colNames + "  ");
		sqlBuffer.append("   from (select " + sumColNames + ", " + tableName + ".pk_psndoc ");
		sqlBuffer.append("     from " + tableName + " ");
		sqlBuffer.append("    where " + tableName + ".pk_wa_class in ");
		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("      and " + tableName + ".cyear = ? ");
		sqlBuffer.append("      and " + tableName + ".cperiod = ? ");
		sqlBuffer.append(extraConditon);

		sqlBuffer.append("    group by " + tableName + ".pk_psndoc) sum_data ");
		sqlBuffer.append(" where " + tableName + ".pk_wa_class = ? ");
		sqlBuffer.append(" and " + tableName + ".cyear = ?  ");
		sqlBuffer.append(" and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(" and " + tableName + ".pk_psndoc = sum_data.pk_psndoc ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
	}

	private void updateDataOracleDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {
		String tableName = getDataTableName(waLoginVO);

		List<String> list = new LinkedList();
		for (WaItemVO itemVO : itemVOs) {
			list.add("unit." + itemVO.getItemkey());
		}

		String colNames = FormatVO.formatListToString(list, "");

		List<String> sumList = new LinkedList();
		List<String> groupList = new LinkedList();
		groupList.add(" " + tableName + "." + "pk_psndoc");
		for (WaItemVO itemVO : itemVOs) {
			//20171129 tsy 还要更新字符型和日期型的
			if (itemVO.getIitemtype() == TypeEnumVO.FLOATTYPE.value()) {//数字类型
				sumList.add(" nvl(sum(" + tableName + "." + itemVO.getItemkey() + "),0)");
			} else {
				sumList.add(" " + tableName + "." + itemVO.getItemkey());
				groupList.add(" " + tableName + "." + itemVO.getItemkey());
			}
		}

		String sumColNames = FormatVO.formatListToString(sumList, "");
		String groupColNames = FormatVO.formatListToString(groupList, "");

		String extraConditon = "";

		extraConditon = " and wa_data.stopflag = 'N' ";

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " unit ");
		sqlBuffer.append("   set (" + colNames + ") = (select  " + sumColNames + " ");
		sqlBuffer.append("                                  from " + tableName + " where " + tableName + ".pk_wa_class in ");

		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("                                       and " + tableName + ".cyear = ?  ");
		sqlBuffer.append("                                       and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(extraConditon);
		sqlBuffer.append("                                       and " + tableName + ".pk_psndoc = unit.pk_psndoc group by " + groupColNames + ") ");

		sqlBuffer.append(" where unit.pk_wa_class = ? ");
		sqlBuffer.append("   and unit.cyear = ? ");
		sqlBuffer.append("   and unit.cperiod = ? ");

		sqlBuffer.append(" and unit.pk_psndoc in  (select wa_data.pk_psndoc    from wa_data  where wa_data.pk_wa_class in (select wa_waclass.pk_wa_class from wa_unitctg, wa_waclass  where wa_waclass.pk_wa_class = wa_unitctg.classedid and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag = 'N'  ) and wa_data.cyear = ? and wa_data.cperiod = ? and wa_data.stopflag = 'N') ");

		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
	}

	public void updateStateforTotal(WaLoginVO waLoginVO) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.stopflag = 'N' ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		updateTableByColKey("wa_data", new String[] { "caculateflag", "checkflag" }, new Object[] { UFBoolean.TRUE, UFBoolean.FALSE }, sqlBuffer.toString());

		updatePeriodState("caculateflag", UFBoolean.TRUE, waLoginVO);
	}

	public String getDataTableName(WaLoginVO waLoginVO) {
		String tableName = "wa_data";

		return tableName;
	}

	public void updatePaydataFlag(String pk_wa_class, String cyear, String cperiod) throws DAOException {
		String sql = "update wa_data set checkflag ='N', caculateflag='N' where pk_wa_class =? and cyear=? and cperiod=?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);
		getBaseDao().executeUpdate(sql, parameter);

		sql =
				"update wa_periodstate  set checkflag= 'N',caculateflag = 'N' where pk_wa_class = ? and exists (select wa_period.pk_wa_period  from wa_period  where wa_period.pk_wa_period = wa_periodstate.pk_wa_period and wa_period.cyear = ? and wa_period.cperiod = ? and  wa_periodstate.pk_wa_class = ?)";

		parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);
		parameter.addParam(pk_wa_class);
		getBaseDao().executeUpdate(sql, parameter);
	}

	public void clearClassItemData(WaClassItemVO vo) throws DAOException {
		String sql = " update wa_data set " + vo.getItemkey() + " = ? where pk_wa_class=? and cyear=? and cperiod=?";

		SQLParameter parameter = new SQLParameter();
		if (vo.getItemkey().startsWith("f_")) {
			parameter.addParam(0);
		} else {
			SQLParamType value = SQLTypeFactory.getNullType(12);
			parameter.addParam(value);
		}
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear());
		parameter.addParam(vo.getCperiod());
		getBaseDao().executeUpdate(sql, parameter);
	}

	public BigDecimal getOrgTmSelected(String cacuItem, String whereStr) throws DAOException {
		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT sum(");
		strSql.append(cacuItem);
		strSql.append(")  FROM wa_data,wa_waclass where ");
		strSql.append(whereStr);

		return new BigDecimal(getBaseDao().executeQuery(strSql.toString(), new ColumnProcessor()).toString());
	}

	public BigDecimal getOrgTm(String cacuItem, String pk_org, String accYear, String accPeriod, String pk_wa_class, int sumtype)
			throws DAOException {
		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT isnull(sum(");
		strSql.append(cacuItem);
		strSql.append("),0) FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE ");
		strSql.append(" wa_data.workorg = '" + pk_org + "' ");
		strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N'  and wa_data.pk_wa_class= wa_periodstate.pk_wa_class and wa_periodstate.pk_wa_period = wa_period.pk_wa_period  and wa_data.cyear= wa_period.cyear and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");

		strSql.append(accYear);
		if (sumtype == 1) {
			strSql.append("' and wa_period.caccperiod='");
			strSql.append(accPeriod);
		} else if (sumtype == 2) {
			strSql.append("' and wa_period.caccperiod<='");
			strSql.append(accPeriod);
		}
		strSql.append("' and wa_data.checkflag = 'Y' ");

		return new BigDecimal(getBaseDao().executeQuery(strSql.toString(), new ColumnProcessor()).toString());
	}

	public AppendableVO[] getOrgRealTm(String[] pk_orgs, AppendableVO[] items, String accYear, String accPeriod, int sumtype)
			throws BusinessException {
		InSQLCreator inSQLCreator = new InSQLCreator();
		HashMap<String, String> itemmap = new HashMap();
		String[] strItems = null;
		for (AppendableVO item : items) {
			String pk_budgetItem = item.getAttributeValue("pk_budget_item").toString();
			String computerule = item.getAttributeValue("computerule").toString();
			itemmap.put(pk_budgetItem, computerule);
			strItems = (String[]) ArrayUtils.add(strItems, pk_budgetItem);
		}
		try {
			StringBuffer strSql = new StringBuffer();
			strSql.append("SELECT wa_data.workorg pk_org");
			for (String strItem : strItems) {
				strSql.append(",isnull(sum(");
				strSql.append((String) itemmap.get(strItem));
				strSql.append("),0) realvalue");
				strSql.append(strItem);
			}
			strSql.append(" FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
			strSql.append(" wa_data.workorg in (" + inSQLCreator.getInSQL(pk_orgs) + ") ");

			strSql.append("  and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N'  and wa_data.pk_wa_class= wa_periodstate.pk_wa_class and wa_periodstate.pk_wa_period = wa_period.pk_wa_period  and wa_data.cyear= wa_period.cyear and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");

			strSql.append(accYear);
			if (sumtype == 1) {
				strSql.append("' and wa_period.caccperiod='");
				strSql.append(accPeriod);
			} else if (sumtype == 2) {
				strSql.append("' and wa_period.caccperiod<='");
				strSql.append(accPeriod);
			}
			strSql.append("' and wa_data.checkflag = 'Y' ");

			strSql.append(" group by wa_data.workorg ");

			AppendableVO[] vos = (AppendableVO[]) executeQueryAppendableVOs(strSql.toString(), AppendableVO.class);

			AppendableVO[] itemvos = null;
			if (ArrayUtils.isEmpty(vos)) {
				return null;
			}
			for (AppendableVO vo : vos) {
				String pk_org = vo.getAttributeValue("pk_org").toString();
				for (String strItem : strItems) {
					AppendableVO tvo = new AppendableVO();
					tvo.setAttributeValue("pk_org", pk_org);
					tvo.setAttributeValue("realvalue", vo.getAttributeValue("realvalue" + strItem));

					tvo.setAttributeValue("pk_budgetItem", strItem);
					itemvos = (AppendableVO[]) ArrayUtils.add(itemvos, tvo);
				}
			}
			return itemvos;
		} finally {
		}
	}

	public Map<String, BigDecimal> getDeptTm(String cacuItem, String pk_org, String accYear, String accPeriod, String pk_wa_class, int sumtype)
			throws DAOException {
		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT wa_data.workdept pk_dept,sum(");
		strSql.append(cacuItem);
		strSql.append(") FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
		strSql.append(" wa_data.workorg = '" + pk_org + "' ");
		strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N' and wa_data.pk_wa_class= wa_periodstate.pk_wa_class and wa_periodstate.pk_wa_period = wa_period.pk_wa_period  and wa_data.cyear= wa_period.cyear and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");

		strSql.append(accYear);
		if (sumtype == 1) {
			strSql.append("' and wa_period.caccperiod='");
			strSql.append(accPeriod);
		} else if (sumtype == 2) {
			strSql.append("' and wa_period.caccperiod<='");
			strSql.append(accPeriod);
		}
		strSql.append("' and wa_data.checkflag = 'Y' ");

		strSql.append(" group by wa_data.workdept");

		List<Object[]> list = (List) getBaseDao().executeQuery(strSql.toString(), new ArrayListProcessor());

		Map<String, BigDecimal> resultMap = new HashMap();
		for (Object[] obj : list) {
			BigDecimal bigdecimal = new BigDecimal(0);
			if (obj[1] != null) {
				bigdecimal = new BigDecimal(obj[1].toString());
			}
			resultMap.put((String) obj[0], bigdecimal);
		}
		return resultMap;
	}

	public AppendableVO[] getRealDeptTm(String pk_org, String[] pk_depts, AppendableVO[] items, String accYear, String accPeriod, int sumtype)
			throws BusinessException {
		InSQLCreator inSQLCreator = new InSQLCreator();
		HashMap<String, String> itemmap = new HashMap();
		String[] strItems = null;
		for (AppendableVO item : items) {
			String pk_budgetItem = item.getAttributeValue("pk_budget_item").toString();
			String computerule = item.getAttributeValue("computerule").toString();
			itemmap.put(computerule, pk_budgetItem);

			strItems = (String[]) ArrayUtils.add(strItems, computerule);
		}
		try {
			StringBuffer strSql = new StringBuffer();
			strSql.append("SELECT wa_data.workdept pk_dept");
			for (AppendableVO item : items) {
				strSql.append(",isnull(sum(");
				strSql.append((String) item.getAttributeValue("computerule"));
				strSql.append("),0) realvalue");
				strSql.append((String) item.getAttributeValue("pk_budget_item"));
			}
			strSql.append(" FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
			strSql.append(" wa_data.workorg = '" + pk_org + "' ");
			strSql.append(" and wa_data.workdept in (" + inSQLCreator.getInSQL(pk_depts) + ") ");

			strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N'  and wa_data.pk_wa_class= wa_periodstate.pk_wa_class and wa_periodstate.pk_wa_period = wa_period.pk_wa_period  and wa_data.cyear= wa_period.cyear and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");

			strSql.append(accYear);
			if (sumtype == 1) {
				strSql.append("' and wa_period.caccperiod='");
				strSql.append(accPeriod);
			} else if (sumtype == 2) {
				strSql.append("' and wa_period.caccperiod<='");
				strSql.append(accPeriod);
			}
			strSql.append("' and wa_data.checkflag = 'Y' ");

			strSql.append(" group by wa_data.workdept ");

			AppendableVO[] vos = (AppendableVO[]) executeQueryAppendableVOs(strSql.toString(), AppendableVO.class);

			if (vos == null) {
				return null;
			}
			AppendableVO[] itemvos = null;
			for (AppendableVO vo : vos) {
				for (AppendableVO item : items) {
					String pk_budgetItem = (String) item.getAttributeValue("pk_budget_item");
					AppendableVO tvo = new AppendableVO();
					tvo.setAttributeValue("pk_dept", vo.getAttributeValue("pk_dept").toString());
					tvo.setAttributeValue("realvalue", vo.getAttributeValue("realvalue" + pk_budgetItem));
					tvo.setAttributeValue("pk_budgetItem", item.getAttributeValue("pk_budget_item"));
					itemvos = (AppendableVO[]) ArrayUtils.add(itemvos, tvo);
				}
			}
			return itemvos;
		} finally {
		}
	}

	public Map<String, BigDecimal> getDeptTmSelected(String cacuItem, String whereStr) throws DAOException {
		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT sum(");
		strSql.append(cacuItem);
		strSql.append("),wa_data.workdept  FROM wa_data,wa_waclass where ");
		strSql.append(whereStr);
		strSql.append(" group by wa_data.workdept");
		List<Object[]> list = (List) getBaseDao().executeQuery(strSql.toString(), new ArrayListProcessor());

		Map<String, BigDecimal> resultMap = new HashMap();
		for (Object[] obj : list) {
			BigDecimal bigdecimal = new BigDecimal(0);
			if (obj[0] != null) {
				bigdecimal = new BigDecimal(obj[0].toString());
			}

			resultMap.put((String) obj[1], bigdecimal);
		}
		return resultMap;
	}

	public void updateCalFlag4OnTime(String pk_wa_class, String cyear, String cperiod, String[] pk_psndocs) throws BusinessException {
		String updateWaDataSql =
				"update wa_data set wa_data.caculateflag = 'N' where  wa_data.pk_wa_class = '" + pk_wa_class + "' and " + " wa_data.cyear = '" + cyear + "' and " + " wa_data.cperiod = '" + cperiod + "' and " + " wa_data.pk_psndoc in (" + SQLHelper.joinToInSql(pk_psndocs, -1) + ") and " + " wa_data.checkflag = 'N' ";

		executeSQLs(new String[] { updateWaDataSql });

		String updatePeriodStateSql =
				"update wa_periodstate set wa_periodstate.caculateflag = 'N' where  wa_periodstate.pk_wa_class = '" + pk_wa_class + "' and " + " wa_periodstate.pk_wa_period in " + " ( " + "	select pk_wa_period from wa_period where cyear = '" + cyear + "' and cperiod = '" + cperiod + "' and " + " 		pk_periodscheme in ( select pk_periodscheme from wa_waclass where wa_waclass.pk_wa_class = '" + pk_wa_class + "')" + " ) " + " and " + " wa_periodstate.checkflag = 'N' ";

		executeSQLs(new String[] { updatePeriodStateSql });
	}
}