package nc.itf.ta;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.ta.monthstat.MonthStatVO;

public interface IMonthStatManageMaintain {

	/**
	 * 按条件生成某组织某考勤期间的考勤月报
	 * @param pk_org
	 * @param cond
	 * @param year
	 * @param month
	 * @throws BusinessException
	 */
	void generate(String pk_org, FromWhereSQL fromWhereSQL, String year, String month) throws BusinessException;

	/**
	 * 按条件生成某组织某考勤期间的考勤月报
	 * 生成的结果直接返回到客户端
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @param showNoDataRecord
	 * @throws BusinessException
	 */
	MonthStatVO[] generate(String pk_org, FromWhereSQL fromWhereSQL, String year, String month, boolean showNoDataRecord)
			throws BusinessException;

	/**
	 * 生成一批人员的考勤月报
	 * @param pk_org
	 * @param pk_psndocs
	 * @param year
	 * @param month
	 * @throws BusinessException
	 */
	void generate(String pk_org, String[] pk_psndocs, String year, String month) throws BusinessException;

	/**
	 * 修改月报后保存
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	MonthStatVO[] save(String pk_org, MonthStatVO[] vos) throws BusinessException;

	MonthStatVO[] approve(String pk_org, String year, String month, MonthStatVO[] vos) throws BusinessException;

	MonthStatVO[] unApprove(String pk_org, String year, String month, MonthStatVO[] vos) throws BusinessException;

	/**
	 * 通用更新方法
	 * @param vos
	 * @param fields
	 * @throws BusinessException
	 */
	void update(MonthStatVO[] vos, String[] fields) throws BusinessException;

}
