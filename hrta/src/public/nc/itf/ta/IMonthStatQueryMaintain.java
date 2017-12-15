package nc.itf.ta;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.ta.monthstat.DeptMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.uif2.LoginContext;

public interface IMonthStatQueryMaintain {

	/**
	 * 根据条件、期间查询考勤月报
	 * @param context
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * 根据条件、期间查询考勤月报
	 * @param context
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByCondition(LoginContext context, String[] pk_psndocs, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * 根据条件、期间查询考勤月报
	 * @param context
	 * @param pk_depts 
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	DeptMonthStatVO[] queryDMSVOByCondition(LoginContext context, String[] pk_depts, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * 根据部门、期间查询考勤月报
	 * 部门月报明细查询，查询在本部门本期间内的所有人员
	 * @param context
	 * @param pk_dept
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByDeptPeriod(LoginContext context, String pk_dept, String year, String month) throws BusinessException;

	/**
	 * 根据条件、期间查询考勤月报
	 * @param context
	 * @param condition
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByConditionAndDept(String pk_dept, boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * 根据条件、期间查询月报未生成人员
	 * 逻辑与日报稍微不一样
	 * 日报统计未生成的逻辑是无数据
	 * 因为每个人默认都有一条月报记录（封存期间的时候给每个人都生成下一期的月报记录），因此不能用无数据来判断是否未生成
	 * 要根据iseffective字段来判断：当封存期间的时候，月报数据被insert后，iseffective未N,表示数据虽然进库了，但是仍然属于
	 * 未生成，只有在计算或者导入后，才会变成Y
	 * @param context
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryUnGenerateByCondition(LoginContext context, FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException;

	/**
	 * 经理自助查询部门（是否包含下级由参数控制）内未生成考勤月报人员
	 * @param pk_dept
	 * @param containsSubDept
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryUnGenerateByConditionAndDept(String pk_dept, boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException;

	/**
	 * 查询某员工某期间的月报
	 * @param pk_psndoc
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO queryByPsn(String pk_psndoc, String year, String month) throws BusinessException;

	/**
	 * 查询某员工某年的考勤月报
	 * @param pk_psndoc
	 * @param year
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByPsn(String pk_psndoc, String year) throws BusinessException;

	/**
	 * 查询指定人员从开始自然年月到结束自然年月之间的所有考勤月报
	 * 若开始=结束，则显示在此自然月内，最新的月报记录（即月报对应的考勤期间与此自然月有交集，且是此月内最新的考勤月报），
	 * 每个HR组织单独处理，即，若此自然月内，在多个HR组织都有月报记录，则返回多个HR组织内的月报记录
	 * 若开始<结束，则从开始月开始，一直到结束月，每个月都按上面的逻辑查询一次，然后按HR组织分组，分完组之后，组内
	 * 按月报的year+month排序，如果中间有空的期间，则要补齐（防止有的考勤期间在自然月的中间，按自然月最新去查，永远都查不到）
	 * @param pk_psndoc
	 * @param beginYear
	 * @param beginMonth
	 * @param endYear
	 * @param endMonth
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String beginYear, String beginMonth, String endYear, String endMonth)
			throws BusinessException;

	/**
	 * 查询某员工的出勤情况，用于自助
	 * @param pk_org
	 * @param pk_psndoc
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	MonthWorkVO[] queryMonthWorkVOsByPsn(String pk_org, String pk_psndoc, String year, String month) throws BusinessException;

	/**
	 * 根据pk值查询当月本部门的所有记录，用于点击代办，自动跳出所有该月份单据
	 * @param pk_org
	 * @param pk_monthstat
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryCurrentMonthDeptBypk(String pk_org, String pk_monthstat) throws BusinessException;

	/**
	 * 通过srcid查询月报，portal审批节点
	 * @param srcid
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] queryBySrc(String srcid) throws BusinessException;
}
